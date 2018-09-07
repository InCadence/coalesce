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

package com.incadencecorp.coalesce.framework.tasks;

import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceResponseType;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;

import java.util.*;

/**
 * This task is used to persist entities.
 *
 * @author Derek
 */
public class CoalesceReadEntityTask extends AbstractPersistorTask<String[], ICoalesceResponseType<List<CoalesceEntity>>> {

    @Override
    protected CoalesceResponseType<List<CoalesceEntity>> doWork(TaskParameters<ICoalescePersistor, String[]> parameters)
            throws CoalesceException
    {
        CoalesceResponseType<List<CoalesceEntity>> result = new CoalesceResponseType<>();

        try
        {
            CoalesceEntity[] entities = parameters.getTarget().getEntity(parameters.getParams());

            result.setStatus(EResultStatus.SUCCESS);
            result.setResult(entities != null ? Arrays.asList(entities) : Collections.emptyList());
        }
        catch (CoalescePersistorException e)
        {
            result.setStatus(EResultStatus.FAILED);
            result.setError(e.getMessage());
        }

        return result;
    }

    @Override
    protected Map<String, String> getParameters(String[] params, boolean isTrace)
    {
        return new HashMap<>();
    }

    @Override
    protected CoalesceResponseType<List<CoalesceEntity>> createResult()
    {
        return new CoalesceResponseType<>();
    }

}
