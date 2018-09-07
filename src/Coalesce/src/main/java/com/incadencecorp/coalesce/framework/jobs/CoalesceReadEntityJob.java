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

package com.incadencecorp.coalesce.framework.jobs;

import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceResponseType;
import com.incadencecorp.coalesce.framework.tasks.AbstractPersistorTask;
import com.incadencecorp.coalesce.framework.tasks.CoalesceReadEntityTask;

import java.util.List;

/**
 * @author Derek
 */
public class CoalesceReadEntityJob extends AbstractCoalescePersistorsJob<String[], ICoalesceResponseType<List<CoalesceEntity>>> {

    /**
     * Constructor
     */
    public CoalesceReadEntityJob(String[] params)
    {
        super(params);
    }

    @Override
    protected AbstractPersistorTask<String[], ICoalesceResponseType<List<CoalesceEntity>>> createTask()
    {
        return new CoalesceReadEntityTask();
    }

    @Override
    protected String[] getKeys(AbstractPersistorTask<String[], ICoalesceResponseType<List<CoalesceEntity>>> task)
    {
        return task.getParams();
    }

    @Override
    protected ICoalesceResponseType<List<ICoalesceResponseType<List<CoalesceEntity>>>> createResponse()
    {
        return new CoalesceResponseType<>();
    }

    @Override
    protected ICoalesceResponseType<List<CoalesceEntity>> createResults()
    {
        return new CoalesceResponseType<>();
    }

}
