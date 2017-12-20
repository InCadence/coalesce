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

package com.incadencecorp.coalesce.framework.util;

import com.incadencecorp.coalesce.api.ICoalesceNotifier;
import com.incadencecorp.coalesce.enums.EAuditCategory;
import com.incadencecorp.coalesce.enums.EAuditLevels;
import com.incadencecorp.coalesce.enums.ECrudOperations;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.framework.jobs.AbstractCoalesceJob;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.framework.tasks.MetricResults;
import com.incadencecorp.coalesce.notification.impl.Log4jNotifierImpl;

/**
 * This utility is used for sending notifications.
 * 
 * @author Derek Clemenzi
 */
public final class CoalesceNotifierUtil {

    private static ICoalesceNotifier notifier = new Log4jNotifierImpl();

    /**
     * Sets the notifier to be used by the system to send notifications.
     * 
     * @param value
     */
    public static final void setNotifier(final ICoalesceNotifier value)
    {
        notifier = value;
    }

    /**
     * Sends a job complete as well as a metrics notification.
     * 
     * @see ICoalesceNotifier#sendMetrics(String, MetricResults)
     * @see ICoalesceNotifier#sendJobComplete(AbstractCoalesceJob)
     * @param job
     */
    public static final void sendJobComplete(AbstractCoalesceJob<?, ?, ?> job)
    {

        notifier.sendJobComplete(job);

        // Send Task Metrics
        sendMetrics(job.getName(), job.getTaskMetrics());
    }

    /**
     * @see ICoalesceNotifier#sendMetrics(String, MetricResults)
     * @param task
     * @param results
     */
    public static final void sendMetrics(final String task, final MetricResults<?>... results)
    {
        for (MetricResults<?> result : results)
        {
            if (result != null && result.getWatch() != null)
            {
                notifier.sendMetrics(task, result);
            }
        }
    }

    /**
     * Overloaded method that takes {@link CoalesceEntity} instead of
     * {@link ObjectMetaData}
     * 
     * @param task
     * @param operation
     * @param entities
     * @see ICoalesceNotifier#sendCrud(String, ECrudOperations, ObjectMetaData)
     */
    public static final void sendCrud(final String task, final ECrudOperations operation, final CoalesceEntity... entities)
    {
        for (CoalesceEntity entity : entities)
        {
            sendCrud(task,
                     operation,
                     new ObjectMetaData(entity.getKey(),
                                        entity.getName(),
                                        entity.getSource(),
                                        entity.getVersion(),
                                        entity.getDateCreated(),
                                        entity.getLastModified()));
        }
    }

    /**
     * @see ICoalesceNotifier#sendCrud(String, ECrudOperations, ObjectMetaData)
     * @param task
     * @param operation
     * @param data
     */
    public static final void sendCrud(final String task, final ECrudOperations operation, final ObjectMetaData data)
    {
        notifier.sendCrud(task, operation, data);
    }

    /**
     * Overloaded method that takes {@link CoalesceEntity} instead of
     * {@link ObjectMetaData}
     * 
     * @see ICoalesceNotifier#sendLinkage(String, ECrudOperations,
     *      ObjectMetaData, ELinkTypes, ObjectMetaData)
     * @param task
     * @param operation
     * @param entity1
     * @param relationship
     * @param entity2
     */
    public static final void sendLinkage(final String task,
                                         final ECrudOperations operation,
                                         final CoalesceEntity entity1,
                                         final ELinkTypes relationship,
                                         final CoalesceEntity entity2)
    {
        ObjectMetaData data1 = new ObjectMetaData(entity1.getKey(),
                                                  entity1.getName(),
                                                  entity1.getSource(),
                                                  entity1.getVersion(),
                                                  entity1.getDateCreated(),
                                                  entity1.getLastModified());

        ObjectMetaData data2 = new ObjectMetaData(entity2.getKey(),
                                                  entity2.getName(),
                                                  entity2.getSource(),
                                                  entity2.getVersion(),
                                                  entity2.getDateCreated(),
                                                  entity2.getLastModified());

        sendLinkage(task, operation, data1, relationship, data2);
    }

    /**
     * @see ICoalesceNotifier#sendLinkage(String, ECrudOperations,
     *      ObjectMetaData, ELinkTypes, ObjectMetaData)
     * @param task
     * @param operation
     * @param entity1
     * @param relationship
     * @param entity2
     */
    public static final void sendLinkage(final String task,
                                         final ECrudOperations operation,
                                         final ObjectMetaData entity1,
                                         final ELinkTypes relationship,
                                         final ObjectMetaData entity2)
    {
        notifier.sendLinkage(task, operation, entity1, relationship, entity2);
    }

    /**
     * @see ICoalesceNotifier#sendAudit(String, EAuditCategory, EAuditLevels,
     *      String)
     * @param task
     * @param category
     * @param level
     * @param message
     */
    public static final void sendAudit(final String task,
                                       final EAuditCategory category,
                                       final EAuditLevels level,
                                       final String message)
    {
        notifier.sendAudit(task, category, level, message);
    }

    /**
     * @see ICoalesceNotifier#sendMessage(String, String, Object)
     * @param topic
     * @param key
     * @param value
     * @param <V>
     */
    public static final <V> void sendMessage(String topic, String key, V value)
    {
        notifier.sendMessage(topic, key, value);
    }

}
