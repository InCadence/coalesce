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

package com.incadencecorp.coalesce.notification.direct.impl;

import com.incadencecorp.coalesce.api.ICoalesceNotifier;
import com.incadencecorp.coalesce.api.subscriber.ICoalesceEventHandler;
import com.incadencecorp.coalesce.api.subscriber.ICoalesceSubscriber;
import com.incadencecorp.coalesce.api.subscriber.events.KeyValuePairEvent;
import com.incadencecorp.coalesce.notification.impl.NotificationSessions;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Derek Clemenzi
 */
public class DirectSessions<I, R> extends NotificationSessions<I, R> {

    private static final DirectNotifierImpl IMPL = new DirectNotifierImpl();

    public DirectSessions(Map<String, String> params,
                          Class<R> clazz)
    {
        this(params, clazz, null, null);
    }

    public DirectSessions(Map<String, String> params,
                          ScheduledExecutorService service,
                          Class<R> clazz)
    {
        this(params, clazz, service, null);
    }

    public DirectSessions(Map<String, String> params,
                          Class<R> clazz,
                          ScheduledExecutorService service,
                          ICoalesceEventHandler<KeyValuePairEvent<R>> handler)
    {
        super(IMPL, IMPL, params, clazz, service, handler);
    }

    @Override
    public void sendMessage(String session, I value)
    {
        super.sendMessage(session, value);
    }
}
