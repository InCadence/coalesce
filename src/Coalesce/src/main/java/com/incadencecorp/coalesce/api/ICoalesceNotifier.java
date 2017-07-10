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

package com.incadencecorp.coalesce.api;

import com.incadencecorp.coalesce.enums.EAuditCategory;
import com.incadencecorp.coalesce.enums.EAuditLevels;
import com.incadencecorp.coalesce.enums.ECrudOperations;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.framework.jobs.AbstractCoalesceJob;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.framework.tasks.MetricResults;

/**
 * Interface for sending out notifications.
 * 
 * @author Derek Clemenzi
 */
public interface ICoalesceNotifier {

    /**
     * Send when metrics are collected.
     * 
     * @param task
     * @param results
     */
    void sendMetrics(String task, MetricResults<?> results);

    /**
     * Send when a CRUD operation is performed on a object.
     * 
     * @param task
     * @param data
     */
    void sendCrud(String task, ECrudOperations operation, ObjectMetaData data);

    /**
     * Send when modifying linkages between objects.
     * 
     * @param task
     * @param operation
     * @param entity1
     * @param relationship
     * @param entity2
     */
    void sendLinkage(String task,
                     ECrudOperations operation,
                     ObjectMetaData entity1,
                     ELinkTypes relationship,
                     ObjectMetaData entity2);

    /**
     * Send when an auditable event occurs.
     * 
     * @param task
     * @param category
     * @param level
     * @param message
     */
    void sendAudit(String task, EAuditCategory category, EAuditLevels level, String message);

    /**
     * Send when job completes.
     * 
     * @param job
     */
    void sendJobComplete(AbstractCoalesceJob<?, ?, ?> job);

}
