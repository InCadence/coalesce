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

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.enums.ECrudOperations;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.tasks.AbstractFrameworkTask;
import com.incadencecorp.coalesce.framework.tasks.TaskParameters;
import com.incadencecorp.coalesce.framework.util.CoalesceNotifierUtil;
import com.incadencecorp.coalesce.framework.validation.CoalesceValidator;
import com.incadencecorp.coalesce.services.api.common.ResultsType;

public class CreateDataObjectTask extends AbstractFrameworkTask<String[], ResultsType> {

    @Override
    protected ResultsType doWork(TaskParameters<CoalesceFramework, String[]> parameters) throws CoalesceException
    {
        ResultsType result = new ResultsType();
        CoalesceFramework framework = parameters.getTarget();
        String[] params = parameters.getParams();
        CoalesceValidator validator = new CoalesceValidator();

        CoalesceEntity[] entities = new CoalesceEntity[params.length];

        for (int ii = 0; ii < params.length; ii++)
        {
            CoalesceEntity entity = new CoalesceEntity();
            if (entity.initialize(params[ii]))
            {
                // TODO This breaks the SOAP Persister Impl Tests
                //entity.getLinkageSection().clearLinkages();
                entity.setModifiedBy(parameters.getPrincipalName());
                entity.setModifiedByIP(parameters.getPrincipalIp());

                Map<String, String> results = new HashMap<String, String>();
                // TODO validator.validate(parameters.getPrincipal(), entity,
                // CoalesceConstraintCache.getCoalesceConstraints(entity));

                // TODO Check to see if entity exists first.

                if (results.size() != 0)
                {
                    result.setStatus(EResultStatus.FAILED);
                    // TODO Create a more meaningful error message
                    result.setResult("Validation Failed");
                    break;
                }

                entities[ii] = entity;
            }
            else
            {
                result.setStatus(EResultStatus.FAILED);
                result.setResult(String.format(CoalesceErrors.NOT_INITIALIZED, "Entity"));
                break;
            }
        }

        if (result.getStatus() != EResultStatus.FAILED && framework.saveCoalesceEntity(entities))
        {
            result.setStatus(EResultStatus.SUCCESS);

            CoalesceNotifierUtil.sendCrud(getName(), ECrudOperations.CREATE, entities);
        }
        else
        {
            result.setStatus(EResultStatus.FAILED);
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

    @Override
    protected ResultsType createResult()
    {
        return new ResultsType();
    }

}
