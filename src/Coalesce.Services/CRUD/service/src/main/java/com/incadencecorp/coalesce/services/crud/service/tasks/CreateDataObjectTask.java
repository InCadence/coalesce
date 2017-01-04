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

package com.incadencecorp.coalesce.services.crud.service.tasks;

import java.util.HashMap;
import java.util.Map;

import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.tasks.AbstractFrameworkTask;
import com.incadencecorp.coalesce.services.api.common.ResultsType;

public class CreateDataObjectTask extends AbstractFrameworkTask<String[], ResultsType> {

    @Override
    protected ResultsType doWork(CoalesceFramework framework, String[] params)
    {
        ResultsType result = new ResultsType();

        CoalesceEntity[] entities = new CoalesceEntity[params.length];

        for (int ii = 0; ii < params.length; ii++)
        {
            entities[ii] = CoalesceEntity.create(params[ii]);
        }

        try
        {
            if (framework.saveCoalesceEntity(entities))
            {
                result.setStatus(EResultStatus.SUCCESS);
            }
            else
            {
                result.setStatus(EResultStatus.FAILED);
            }
        }
        catch (CoalescePersistorException e)
        {
            result.setStatus(EResultStatus.FAILED);
            result.setResult(e.getMessage());
        }

        return result;
    }

    @Override
    protected Map<String, String> getParameters(String[] params, boolean isTrace)
    {
        Map<String, String> results = new HashMap<String, String>();

        for (int ii = 0; ii < params.length; ii++)
        {
            CoalesceEntity entity = CoalesceEntity.create(params[ii]);

            if (isTrace)
            {
                results.put(entity.getKey(), params[ii]);
            }
            else
            {
                results.put(entity.getKey(), entity.getName());
            }
        }

        return results;
    }

}
