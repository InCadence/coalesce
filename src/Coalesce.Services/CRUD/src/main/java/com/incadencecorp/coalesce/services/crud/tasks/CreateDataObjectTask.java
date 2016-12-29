package com.incadencecorp.coalesce.services.crud.tasks;

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
