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

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.ICoalesceNotifier;
import com.incadencecorp.coalesce.enums.EAuditCategory;
import com.incadencecorp.coalesce.enums.EAuditLevels;
import com.incadencecorp.coalesce.enums.ECrudOperations;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.framework.jobs.AbstractCoalesceJob;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.framework.tasks.MetricResults;

/**
 * This implementation uses AdminEvents to push notifications.
 * 
 * @author Derek Clemenzi
 */
public class AdminEventNotifierImpl implements ICoalesceNotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminEventNotifierImpl.class);

    /*--------------------------------------------------------------------------
    Member Variables
    --------------------------------------------------------------------------*/

    private BundleContext context;

    /*--------------------------------------------------------------------------
    Public Methods
    --------------------------------------------------------------------------*/

    public BundleContext getContext()
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
                LOGGER.warn("Bundle Not Found");
            }
        }

        return context;
    }

    public void setContext(BundleContext context)
    {
        this.context = context;
    }

    /*--------------------------------------------------------------------------
    Override Methods
    --------------------------------------------------------------------------*/

    @Override
    public void sendMetrics(String task, MetricResults<?> results)
    {
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put("name", task);
        properties.put("pending", results.getWatch().getPendingLife());
        properties.put("working", results.getWatch().getWorkLife());
        properties.put("total", results.getWatch().getTotalLife());
        properties.put("successful", results.isSuccessful());

        sendEvent(new Event("com/incadence/metrics", properties));
    }

    @Override
    public void sendCrud(String task, ECrudOperations operation, ObjectMetaData data)
    {
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put("name", task);
        properties.put("operation", operation.toString());
        properties.put("entitykey", data.getKey());
        properties.put("entityname", data.getName());
        properties.put("entitysource", data.getSource());
        properties.put("entityversion", data.getVersion());

        sendEvent(new Event("com/incadence/crud", properties));
    }

    @Override
    public void sendLinkage(String task,
                            ECrudOperations operation,
                            ObjectMetaData entity1,
                            ELinkTypes relationship,
                            ObjectMetaData entity2)
    {
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put("name", task);
        properties.put("operation", operation.toString());
        properties.put("entity1key", entity1.getKey());
        properties.put("entity1name", entity1.getName());
        properties.put("entity1source", entity1.getSource());
        properties.put("entity1version", entity1.getVersion());
        properties.put("relationship", relationship.toString());
        properties.put("entity2key", entity2.getKey());
        properties.put("entity2name", entity2.getName());
        properties.put("entity2source", entity2.getSource());
        properties.put("entity2version", entity2.getVersion());

        sendEvent(new Event("com/incadence/linkage", properties));
    }

    @Override
    public void sendAudit(String task, EAuditCategory category, EAuditLevels level, String message)
    {
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put("name", task);
        properties.put("category", category.toString());
        properties.put("level", level.toString());
        properties.put("message", message);

        sendEvent(new Event("com/incadence/audit", properties));
    }
    
    @Override
    public void sendJobComplete(AbstractCoalesceJob<?, ?, ?> job)
    {
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put("name", job.getName());
        properties.put("id", job.getJobId());
        properties.put("status", job.getJobStatus().toString());

        sendEvent(new Event("com/incadence/job", properties));
    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private void sendEvent(Event event)
    {
        ServiceReference ref = getContext().getServiceReference(EventAdmin.class.getName());
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

}
