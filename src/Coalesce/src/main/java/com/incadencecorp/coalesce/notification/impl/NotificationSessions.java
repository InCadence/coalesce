/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.notification.impl;

import com.incadencecorp.coalesce.api.ICoalesceNotifier;
import com.incadencecorp.coalesce.api.subscriber.ICoalesceEventHandler;
import com.incadencecorp.coalesce.api.subscriber.ICoalesceSubscriber;
import com.incadencecorp.coalesce.api.subscriber.events.KeyValuePairEvent;
import com.incadencecorp.coalesce.framework.CoalesceSchedulerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Derek Clemenzi
 */
public class NotificationSessions<I, R> extends CoalesceSchedulerServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationSessions.class);

    private final ICoalesceNotifier notifier;
    private final ICoalesceSubscriber subscriber;

    private final Object SYNC_SESSIONS = new Object();

    private final Map<String, NotificationSession<I, R>> sessions = new ConcurrentHashMap<>();
    private final String topic;
    private final int expire;

    private final ICoalesceEventHandler<KeyValuePairEvent<R>> handler;

    private static final String PARAM_BASE = "com.incadencecorp.session.";
    private static final String DEFAULT_TOPIC = "ingest";
    private static final String DEFAULT_SESSION_INTERVAL = "1";
    private static final String DEFAULT_SESSION_EXPIRE = "5";

    /**
     * (String) Topic used when processing request.
     */
    public static final String PARAM_TOPIC = PARAM_BASE + "topic";
    /**
     * (String) Topic used when processing responses.
     */
    public static final String PARAM_RESPONSE_TOPIC = PARAM_BASE + "resultTopic";
    /**
     * (Integer) Interval in minutes for how often the controller should check for expired session. Default: {@value #DEFAULT_SESSION_INTERVAL}.
     */
    public static final String PARAM_SESSION_INTERVAL = PARAM_BASE + "check";
    /**
     * (Integer) Time in minutes in which if a session has not been touched to expire it. This also determines the check interval therefore
     * a session could live for twice the number specified depending on when it was created. Default: {@value #DEFAULT_SESSION_EXPIRE}.
     */
    public static final String PARAM_SESSION_EXPIRE = PARAM_BASE + "expire";

    public NotificationSessions(ICoalesceNotifier notifier,
                                ICoalesceSubscriber subscriber,
                                Map<String, String> params,
                                Class<R> clazz)
    {
        this(notifier, subscriber, params, clazz, null, null);
    }

    public NotificationSessions(ICoalesceNotifier notifier,
                                ICoalesceSubscriber subscriber,
                                Map<String, String> params,
                                ScheduledExecutorService service,
                                Class<R> clazz)
    {
        this(notifier, subscriber, params, clazz, service, null);
    }

    public NotificationSessions(ICoalesceNotifier notifier,
                                ICoalesceSubscriber subscriber,
                                Map<String, String> params,
                                Class<R> clazz,
                                ScheduledExecutorService service,
                                ICoalesceEventHandler<KeyValuePairEvent<R>> handler)
    {
        super(service);

        this.topic = params.getOrDefault(PARAM_TOPIC, DEFAULT_TOPIC);
        this.expire = Integer.parseInt(params.getOrDefault(PARAM_SESSION_EXPIRE, DEFAULT_SESSION_EXPIRE));

        this.handler = handler;
        this.notifier = notifier;
        this.subscriber = subscriber;

        String responseTopic = params.getOrDefault(PARAM_RESPONSE_TOPIC, DEFAULT_TOPIC + "/response");

        subscriber.subscribeTopic(responseTopic, this::handledKafkaResponse, clazz);

        int interval = Integer.parseInt(params.getOrDefault(PARAM_SESSION_INTERVAL, DEFAULT_SESSION_INTERVAL));

        scheduleAtFixedRate(this::expireSessions, interval, interval, TimeUnit.MINUTES);

        LOGGER.debug("Expired Session Check ({} Minutes)", interval);
    }

    @Override
    public void close()
    {
        super.close();

        try
        {
            if (notifier instanceof AutoCloseable)
            {
                ((AutoCloseable) notifier).close();
            }

            if (subscriber instanceof AutoCloseable)
            {
                ((AutoCloseable) subscriber).close();
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String session, I value)
    {
        getSession(session).addRequest(value);
        notifier.sendMessage(topic, session, value);
    }

    public void expireAllSessions()
    {
        synchronized (SYNC_SESSIONS)
        {
            for (NotificationSession session : sessions.values())
            {
                session.expire();
            }

            sessions.clear();
        }
    }

    public void expireSessions()
    {
        synchronized (SYNC_SESSIONS)
        {
            LOGGER.trace("Checking for Expired Sessions");

            List<String> sessionsToExpire = new ArrayList<>();

            for (Map.Entry<String, NotificationSession<I, R>> entry : sessions.entrySet())
            {
                if (entry.getValue().isExpired(expire))
                {
                    sessionsToExpire.add(entry.getKey());
                }
            }

            if (sessionsToExpire.size() > 0)
            {
                LOGGER.info("{} Expired Sessions Found", sessionsToExpire.size());
            }
            else
            {
                LOGGER.trace("{} Expired Sessions Found", sessionsToExpire.size());
            }

            for (String key : sessionsToExpire)
            {
                LOGGER.debug("Expiring {}", key);
                sessions.remove(key);
                LOGGER.debug("Expired {}", key);
            }
        }
    }

    private void handledKafkaResponse(KeyValuePairEvent<R> event)
    {
        if (sessions.containsKey(event.getKey()))
        {
            LOGGER.debug("Handling: {}", event.getKey());
            getSession(event.getKey()).addResponse(event.getValue());

            if (handler != null)
            {
                handler.handle(event);
            }
        }
        else
        {
            LOGGER.debug("Session Not Found: {}", event.getKey());
        }
    }

    public Set<String> getSessions()
    {
        synchronized (SYNC_SESSIONS)
        {
            return sessions.keySet();
        }
    }

    public NotificationSession<I, R> getSession(String key)
    {
        synchronized (SYNC_SESSIONS)
        {
            NotificationSession<I, R> session = sessions.get(key);

            if (session == null)
            {
                session = new NotificationSession<>();

                sessions.put(key, session);
            }

            session.touch();

            return session;
        }
    }

}


