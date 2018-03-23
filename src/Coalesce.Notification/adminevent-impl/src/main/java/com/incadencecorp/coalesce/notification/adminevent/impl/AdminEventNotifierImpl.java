/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

 Notwithstanding any contractor copyright notice, the Government has Unlimited
 Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 of this work other than as specifically authorized by these DFARS Clauses may
 violate Government rights in this work.

 DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 Unlimited Rights. The Government has the right to use, modify, reproduce,
 perform, display, release or disclose this computer software and to have or
 authorize others to do so.

 Distribution Statement D. Distribution authorized to the Department of
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 -----------------------------------------------------------------------------*/

package com.incadencecorp.coalesce.notification.adminevent.impl;

import com.incadencecorp.coalesce.api.ICoalesceNotifier;
import com.incadencecorp.coalesce.api.subscriber.events.*;
import com.incadencecorp.coalesce.common.classification.helpers.StringHelper;
import com.incadencecorp.coalesce.enums.EAuditCategory;
import com.incadencecorp.coalesce.enums.EAuditLevels;
import com.incadencecorp.coalesce.enums.ECrudOperations;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.framework.jobs.AbstractCoalesceJob;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.framework.tasks.MetricResults;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * This implementation uses AdminEvents to push notifications and ONLY works
 * inside of a OSGi environment.
 *
 * @author Derek Clemenzi
 */
public class AdminEventNotifierImpl implements ICoalesceNotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminEventNotifierImpl.class);
    public static final String TOPIC_METRICS = "com/incadence/metrics";
    public static final String TOPIC_CRUD = "com/incadence/crud";
    public static final String TOPIC_LINKAGE = "com/incadence/linkage";
    public static final String TOPIC_AUDIT = "com/incadence/audit";
    public static final String TOPIC_JOB = "com/incadence/job";

    /*--------------------------------------------------------------------------
    Member Variables
    --------------------------------------------------------------------------*/

    private BundleContext context;

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private BundleContext getContext()
    {
        if (context == null)
        {
            Bundle bundle = FrameworkUtil.getBundle(AdminEventNotifierImpl.class);
            if (bundle != null)
            {
                context = bundle.getBundleContext();
            }
            else
            {
                LOGGER.error("Bundle Context Not Found");
            }
        }

        return context;
    }


    /*--------------------------------------------------------------------------
    Override Methods
    --------------------------------------------------------------------------*/

    @Override
    public void setContext(BundleContext context)
    {
        this.context = context;
    }

    @Override
    public void sendMetrics(String task, MetricResults<?> results)
    {
        MetricsEvent event = new MetricsEvent();
        event.setName(task);
        event.setPending(results.getWatch().getPendingLife());
        event.setWorking(results.getWatch().getWorkLife());
        event.setTotal(results.getWatch().getTotalLife());
        event.setSuccessful(results.isSuccessful());
        event.setError(results.getResults().getError());

        Dictionary<String, Object> properties = new Hashtable<>();
        properties.put("event", event);

        sendEvent(new Event(TOPIC_METRICS, properties));
    }

    @Override
    public void sendCrud(String task, ECrudOperations operation, ObjectMetaData data)
    {
        CrudEvent event = new CrudEvent();
        event.setName(task);
        event.setOperation(operation);
        event.setMeta(data);

        Dictionary<String, Object> properties = new Hashtable<>();
        properties.put("event", event);

        sendEvent(new Event(TOPIC_CRUD, properties));
    }

    @Override
    public void sendLinkage(String task,
                            ECrudOperations operation,
                            ObjectMetaData entity1,
                            ELinkTypes relationship,
                            ObjectMetaData entity2)
    {
        LinkageEvent event = new LinkageEvent();
        event.setName(task);
        event.setOperation(operation);
        event.setEntity1(entity1);
        event.setRelationship(relationship);
        event.setEntity2(entity2);

        Dictionary<String, Object> properties = new Hashtable<>();
        properties.put("event", event);

        sendEvent(new Event(TOPIC_LINKAGE, properties));
    }

    @Override
    public void sendAudit(String task, EAuditCategory category, EAuditLevels level, String message)
    {
        AuditEvent event = new AuditEvent();
        event.setName(task);
        event.setCategory(category);
        event.setLevel(level);
        event.setMessage(message);

        Dictionary<String, Object> properties = new Hashtable<>();
        properties.put("event", event);

        sendEvent(new Event(TOPIC_AUDIT, properties));
    }

    @Override
    public void sendJobComplete(AbstractCoalesceJob<?, ?, ?> job)
    {
        JobEvent event = new JobEvent();
        event.setName(job.getName());
        event.setId(job.getJobId());
        event.setStatus(job.getJobStatus());

        Dictionary<String, Object> properties = new Hashtable<>();
        properties.put("event", event);

        sendEvent(new Event(TOPIC_JOB, properties));
    }

    @Override
    public <V> void sendMessage(String topic, String key, V value)
    {
        KeyValuePairEvent<V> event = new KeyValuePairEvent<>();
        event.setKey(key);
        event.setValue(value);

        Dictionary<String, Object> properties = new Hashtable<>();
        properties.put("event", event);

        sendEvent(new Event(topic.replace("\\.", "/"), properties));
    }


    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private void sendEvent(Event event)
    {
        BundleContext context = getContext();

        if (context != null)
        {
            ServiceReference ref = context.getServiceReference(EventAdmin.class.getName());

            if (ref != null)
            {
                EventAdmin eventAdmin = (EventAdmin) context.getService(ref);
                eventAdmin.sendEvent(event);
            }
            else
            {
                LOGGER.error("Service Reference Not Found");
            }
        }
        else
        {
            LOGGER.error("Bundle Context Not Found");
        }
    }

}
