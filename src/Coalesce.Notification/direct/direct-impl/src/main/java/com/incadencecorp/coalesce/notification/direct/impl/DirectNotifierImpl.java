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
import com.incadencecorp.coalesce.api.subscriber.events.*;
import com.incadencecorp.coalesce.enums.EAuditCategory;
import com.incadencecorp.coalesce.enums.EAuditLevels;
import com.incadencecorp.coalesce.enums.ECrudOperations;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.framework.jobs.AbstractCoalesceJob;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.framework.tasks.MetricResults;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class DirectNotifierImpl implements ICoalesceNotifier, ICoalesceSubscriber {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectNotifierImpl.class);

    private ICoalesceEventHandler<MetricsEvent> handlerMetrics;
    private ICoalesceEventHandler<CrudEvent> handlerCrud;
    private ICoalesceEventHandler<LinkageEvent> handlerLinkage;
    private ICoalesceEventHandler<AuditEvent> handlerAudit;
    private ICoalesceEventHandler<JobEvent> handlerJob;
    private Map<String, ICoalesceEventHandler> handlerKvp = new HashMap<>();

    @Override
    public void setContext(BundleContext context)
    {
        // DO Nothing
    }

    @Override
    public void sendMetrics(String task, MetricResults<?> results)
    {
        if (handlerMetrics != null)
        {
            MetricsEvent event = new MetricsEvent();
            event.setName(task);
            event.setPending(results.getWatch().getPendingLife());
            event.setWorking(results.getWatch().getWorkLife());
            event.setTotal(results.getWatch().getTotalLife());
            event.setSuccessful(results.isSuccessful());

            handlerMetrics.handle(event);
        }
        else
        {
            LOGGER.warn("Handler Not Found");
        }
    }

    @Override
    public void sendMetrics(String task, Long duration)
    {
        if (handlerMetrics != null)
        {
            MetricsEvent event = new MetricsEvent();
            event.setName(task);
            event.setPending(0);
            event.setWorking(duration);
            event.setTotal(duration);
            event.setSuccessful(true);

            handlerMetrics.handle(event);
        }
        else
        {
            LOGGER.warn("Handler Not Found");
        }
    }

    @Override
    public void sendCrud(String task, ECrudOperations operation, ObjectMetaData data)
    {
        if (handlerCrud != null)
        {
            CrudEvent event = new CrudEvent();
            event.setName(task);
            event.setOperation(operation);
            event.setMeta(new ObjectMetaData(data.getKey(), data.getName(), data.getSource(), data.getVersion()));

            handlerCrud.handle(event);
        }
        else
        {
            LOGGER.warn("Handler Not Found");
        }
    }

    @Override
    public void sendLinkage(String task,
                            ECrudOperations operation,
                            ObjectMetaData entity1,
                            ELinkTypes relationship,
                            ObjectMetaData entity2)
    {
        if (handlerLinkage != null)
        {
            LinkageEvent event = new LinkageEvent();
            event.setName(task);
            event.setRelationship(relationship);
            event.setOperation(operation);

            event.setEntity1(new ObjectMetaData(entity1.getKey(),
                                                entity1.getName(),
                                                entity1.getSource(),
                                                entity1.getVersion()));
            event.setEntity2(new ObjectMetaData(entity2.getKey(),
                                                entity2.getName(),
                                                entity2.getSource(),
                                                entity2.getVersion()));

            handlerLinkage.handle(event);
        }
        else
        {
            LOGGER.warn("Handler Not Found");
        }
    }

    @Override
    public void sendAudit(String task, EAuditCategory category, EAuditLevels level, String message)
    {
        if (handlerAudit != null)
        {
            AuditEvent event = new AuditEvent();
            event.setName(task);
            event.setCategory(category);
            event.setLevel(level);
            event.setMessage(message);

            handlerAudit.handle(event);
        }
        else
        {
            LOGGER.warn("Handler Not Found");
        }
    }

    @Override
    public void sendJobComplete(AbstractCoalesceJob<?, ?, ?> job)
    {
        if (handlerJob != null)
        {
            JobEvent event = new JobEvent();
            event.setName(job.getName());
            event.setId(job.getJobId());
            event.setStatus(job.getJobStatus());

            handlerJob.handle(event);
        }
        else
        {
            LOGGER.warn("Handler Not Found");
        }
    }

    @Override
    public <V> void sendMessage(String topic, String key, V value)
    {
        if (handlerKvp.containsKey(topic))
        {
            KeyValuePairEvent<V> event = new KeyValuePairEvent<>();
            event.setKey(key);
            event.setValue(value);

            handlerKvp.get(topic).handle(event);
        }
        else
        {
            LOGGER.warn("Handler for {} Not Found", topic);
        }
    }

    @Override
    public void subscribeToMetrics(ICoalesceEventHandler<MetricsEvent> handler)
    {
        this.handlerMetrics = handler;
        LOGGER.debug("Subscribed");
    }

    @Override
    public void subscribeToCRUD(ICoalesceEventHandler<CrudEvent> handler)
    {
        this.handlerCrud = handler;
        LOGGER.debug("Subscribed");
    }

    @Override
    public void subscribeLinkages(ICoalesceEventHandler<LinkageEvent> handler)
    {
        this.handlerLinkage = handler;
        LOGGER.debug("Subscribed");
    }

    @Override
    public void subscribeAudit(ICoalesceEventHandler<AuditEvent> handler)
    {
        this.handlerAudit = handler;
        LOGGER.debug("Subscribed");
    }

    @Override
    public void subscribeJobComplete(ICoalesceEventHandler<JobEvent> handler)
    {
        this.handlerJob = handler;
        LOGGER.debug("Subscribed");
    }

    @Override
    public <V> void subscribeTopic(String topic, ICoalesceEventHandler<KeyValuePairEvent<V>> handler, Class<V> clazz)
    {
        this.handlerKvp.put(topic, handler);

        LOGGER.debug("Subscribed to {}", topic);
    }
}
