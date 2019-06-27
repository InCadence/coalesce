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

package com.incadencecorp.coalesce.services.crud.service.data.controllers.tasks;

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

import java.util.HashMap;
import java.util.Map;

public class CreateDataObjectTask extends AbstractFrameworkTask<CoalesceEntity[], ResultsType> {

    @Override
    protected ResultsType doWork(TaskParameters<CoalesceFramework, CoalesceEntity[]> parameters) throws CoalesceException
    {
        ResultsType result = new ResultsType();
        CoalesceFramework framework = parameters.getTarget();
        CoalesceEntity[] entities = parameters.getParams();
        CoalesceValidator validator = new CoalesceValidator();

        for (CoalesceEntity entity : entities)
        {
            // TODO This breaks the SOAP Persister Impl Tests
            //entity.getLinkageSection().clearLinkages();
            entity.setModifiedBy(parameters.getPrincipal());

            Map<String, String> results = new HashMap<>();
            // TODO validator.validate(parameters.getPrincipal(), entity,
            // CoalesceConstraintCache.getCoalesceConstraints(entity));

            // TODO Check to see if entity exists first.

            if (!results.isEmpty())
            {
                result.setStatus(EResultStatus.FAILED);
                // TODO Create a more meaningful error message
                result.setError("Validation Failed");
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
    protected Map<String, String> getParameters(CoalesceEntity[] entities, boolean isTrace)
    {
        Map<String, String> results = new HashMap<>();

        for (CoalesceEntity entity : entities)
        {
            if (isTrace)
            {
                results.put(entity.getKey(), entity.toXml());
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
