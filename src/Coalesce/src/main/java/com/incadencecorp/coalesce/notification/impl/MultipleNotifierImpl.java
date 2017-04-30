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

import java.util.ArrayList;
import java.util.List;

import com.incadencecorp.coalesce.api.ICoalesceNotifier;
import com.incadencecorp.coalesce.enums.EAuditCategory;
import com.incadencecorp.coalesce.enums.EAuditLevels;
import com.incadencecorp.coalesce.enums.ECrudOperations;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.framework.jobs.AbstractCoalesceJob;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.framework.tasks.MetricResults;

/**
 * This implementation allows for multiple notifiers to be initialized and used.
 * 
 * @author Derek Clemenzi
 */
public class MultipleNotifierImpl implements ICoalesceNotifier {

    /*--------------------------------------------------------------------------
    Member Variables
    --------------------------------------------------------------------------*/

    private final List<ICoalesceNotifier> notifiers = new ArrayList<ICoalesceNotifier>();

    /*--------------------------------------------------------------------------
    Public Methods
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     * 
     * @param values
     */
    public MultipleNotifierImpl(ICoalesceNotifier... values)
    {
        for (ICoalesceNotifier value : values)
        {
            if (!this.getClass().isInstance(value))
            {
                notifiers.add(value);
            }
        }
    }

    /*--------------------------------------------------------------------------
    Override Methods
    --------------------------------------------------------------------------*/

    @Override
    public void sendMetrics(String task, MetricResults<?> results)
    {
        for (ICoalesceNotifier notifier : notifiers)
        {
            notifier.sendMetrics(task, results);
        }
    }

    @Override
    public void sendCrud(String task, ECrudOperations operation, ObjectMetaData data)
    {
        for (ICoalesceNotifier notifier : notifiers)
        {
            notifier.sendCrud(task, operation, data);
        }
    }

    @Override
    public void sendLinkage(String task,
                            ECrudOperations operation,
                            ObjectMetaData entity1,
                            ELinkTypes relationship,
                            ObjectMetaData entity2)
    {
        for (ICoalesceNotifier notifier : notifiers)
        {
            notifier.sendLinkage(task, operation, entity1, relationship, entity2);
        }
    }

    @Override
    public void sendAudit(String task, EAuditCategory category, EAuditLevels level, String message)
    {
        for (ICoalesceNotifier notifier : notifiers)
        {
            notifier.sendAudit(task, category, level, message);
        }
    }

    @Override
    public void sendJobComplete(AbstractCoalesceJob<?, ?, ?> job)
    {
        for (ICoalesceNotifier notifier : notifiers)
        {
            notifier.sendJobComplete(job);
        }
    }

}
