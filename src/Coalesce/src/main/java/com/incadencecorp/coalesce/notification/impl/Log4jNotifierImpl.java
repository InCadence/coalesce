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

package com.incadencecorp.coalesce.notification.impl;

import com.incadencecorp.coalesce.api.ICoalesceNotifier;
import com.incadencecorp.coalesce.enums.EAuditCategory;
import com.incadencecorp.coalesce.enums.EAuditLevels;
import com.incadencecorp.coalesce.enums.ECrudOperations;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.framework.jobs.AbstractCoalesceJob;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.framework.tasks.MetricResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This implementation send notifications to log4j.
 *
 * @author Derek Clemenzi
 */
public class Log4jNotifierImpl implements ICoalesceNotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(Log4jNotifierImpl.class);

    /*--------------------------------------------------------------------------
    Override Methods
    --------------------------------------------------------------------------*/

    @Override
    public void sendMetrics(String task, MetricResults<?> results)
    {
        LOGGER.debug("({}) ({}) Pending ({}) Working ({}) Total ({}) {}",
                     results.isSuccessful() ? "SUCCESS" : "FAILED",
                     results.getName(),
                     results.getWatch().getPendingLife(),
                     results.getWatch().getWorkLife(),
                     results.getWatch().getTotalLife(),
                     results.isSuccessful() ? "" : " Reason: (" + results.getResults().getError() + ")");

    }

    @Override
    public void sendCrud(String task, ECrudOperations operation, ObjectMetaData data)
    {
        LOGGER.info("({}) ({}) ({})", operation.toString(), task, getDetail(data));
    }

    @Override
    public void sendLinkage(String task,
                            ECrudOperations operation,
                            ObjectMetaData entity1,
                            ELinkTypes relationship,
                            ObjectMetaData entity2)
    {
        LOGGER.info("({}) ({}) ({})-[{}]->({})",
                    operation.toString(),
                    task,
                    getDetail(entity1),
                    relationship.toString(),
                    getDetail(entity2));
    }

    @Override
    public void sendAudit(String task, EAuditCategory category, EAuditLevels level, String message)
    {
        String auditMessage = String.format("Audit-(%s) (%s) (%s)", category.toString(), task, message);

        switch (level)
        {
        case ERROR:
            LOGGER.error(auditMessage);
            break;
        case INFO:
            LOGGER.info(auditMessage);
            break;
        case WARN:
            LOGGER.warn(auditMessage);
            break;
        }
    }

    @Override
    public void sendJobComplete(AbstractCoalesceJob<?, ?, ?> job)
    {
        LOGGER.info("({}) Job ({}) Completed [ID = ({})] Pending ({}) Working ({}) Total ({})",
                    job.getJobStatus().toString(),
                    job.getName(),
                    job.getJobId(),
                    job.getMetrics().getPendingLife(),
                    job.getMetrics().getWorkLife(),
                    job.getMetrics().getTotalLife());
    }

    @Override
    public <V> void sendMessage(String topic, String key, V value)
    {
        LOGGER.info("Topic ({}): ({}) = ({})", topic, key, value);
    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private String getDetail(ObjectMetaData data)
    {
        return String.format("%s [%s, %s, %s]", data.getKey(), data.getName(), data.getSource(), data.getVersion());
    }

}
