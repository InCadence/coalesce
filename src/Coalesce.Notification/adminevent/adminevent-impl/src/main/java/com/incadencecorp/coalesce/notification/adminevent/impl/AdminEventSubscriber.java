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

package com.incadencecorp.coalesce.notification.adminevent.impl;

import com.incadencecorp.coalesce.api.subscriber.ICoalesceEventHandler;
import com.incadencecorp.coalesce.api.subscriber.ICoalesceSubscriber;
import com.incadencecorp.coalesce.api.subscriber.events.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * This implementation allows client to subscribe to AdminEvents.
 *
 * @author Derek Clemenzi
 */
public class AdminEventSubscriber implements ICoalesceSubscriber {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminEventSubscriber.class);

    private BundleContext context;

    /**
     * Default Constructor
     */
    public AdminEventSubscriber()
    {
        // Register Filter.json Monitor
        Bundle bundle = FrameworkUtil.getBundle(AdminEventSubscriber.class);

        // Running in OSGi?
        if (bundle != null)
        {
            // Default the context to this bundle
            context = bundle.getBundleContext();
        }
    }

    @Override
    public void setContext(BundleContext context)
    {
        this.context = context;
    }

    @Override
    public void subscribeToMetrics(ICoalesceEventHandler<MetricsEvent> handler)
    {
        if (context != null)
        {
            LOGGER.debug("Registering EventHandler for ({})", AdminEventNotifierImpl.TOPIC_METRICS);

            Dictionary<String, String> props = new Hashtable<>();
            props.put(EventConstants.EVENT_TOPIC, AdminEventNotifierImpl.TOPIC_METRICS);

            context.registerService(EventHandler.class.getName(),
                                    (EventHandler) (Event event) -> handler.handle((MetricsEvent) event.getProperty("event")),
                                    props);
        }
    }

    @Override
    public void subscribeToCRUD(ICoalesceEventHandler<CrudEvent> handler)
    {
        if (context != null)
        {
            LOGGER.debug("Registering EventHandler for ({})", AdminEventNotifierImpl.TOPIC_CRUD);

            Dictionary<String, String> props = new Hashtable<>();
            props.put(EventConstants.EVENT_TOPIC, AdminEventNotifierImpl.TOPIC_CRUD);

            context.registerService(EventHandler.class.getName(),
                                    (EventHandler) (Event event) -> handler.handle((CrudEvent) event.getProperty("event")),
                                    props);
        }
    }

    @Override
    public void subscribeLinkages(ICoalesceEventHandler<LinkageEvent> handler)
    {
        if (context != null)
        {
            LOGGER.debug("Registering EventHandler for ({})", AdminEventNotifierImpl.TOPIC_LINKAGE);

            Dictionary<String, String> props = new Hashtable<>();
            props.put(EventConstants.EVENT_TOPIC, AdminEventNotifierImpl.TOPIC_LINKAGE);

            context.registerService(EventHandler.class.getName(),
                                    (EventHandler) (Event event) -> handler.handle((LinkageEvent) event.getProperty("event")),
                                    props);
        }
    }

    @Override
    public void subscribeAudit(ICoalesceEventHandler<AuditEvent> handler)
    {
        if (context != null)
        {
            LOGGER.debug("Registering EventHandler for ({})", AdminEventNotifierImpl.TOPIC_AUDIT);

            Dictionary<String, String> props = new Hashtable<>();
            props.put(EventConstants.EVENT_TOPIC, AdminEventNotifierImpl.TOPIC_AUDIT);

            context.registerService(EventHandler.class.getName(),
                                    (EventHandler) (Event event) -> handler.handle((AuditEvent) event.getProperty("event")),
                                    props);
        }
    }

    @Override
    public void subscribeJobComplete(ICoalesceEventHandler<JobEvent> handler)
    {
        if (context != null)
        {
            LOGGER.debug("Registering EventHandler for ({})", AdminEventNotifierImpl.TOPIC_JOB);

            Dictionary<String, String> props = new Hashtable<>();
            props.put(EventConstants.EVENT_TOPIC, AdminEventNotifierImpl.TOPIC_JOB);

            context.registerService(EventHandler.class.getName(),
                                    (EventHandler) (Event event) -> handler.handle((JobEvent) event.getProperty("event")),
                                    props);
        }
    }

    @Override
    public <V> void subscribeTopic(String topic, ICoalesceEventHandler<KeyValuePairEvent<V>> handler)
    {
        if (context != null)
        {
            LOGGER.debug("Registering EventHandler for ({})", topic);

            Dictionary<String, String> props = new Hashtable<>();
            props.put(EventConstants.EVENT_TOPIC, topic);

            context.registerService(EventHandler.class.getName(),
                                    (EventHandler) (Event event) -> handler.handle((KeyValuePairEvent) event.getProperty(
                                            "event")),
                                    props);

        }
    }

}
