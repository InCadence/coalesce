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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIteratorMerge;
import com.incadencecorp.coalesce.framework.tasks.AbstractFrameworkTask;
import com.incadencecorp.coalesce.framework.tasks.TaskParameters;
import com.incadencecorp.coalesce.framework.validation.CoalesceValidator;
import com.incadencecorp.coalesce.services.api.common.ResultsType;

public class UpdateDataObjectTask extends AbstractFrameworkTask<String[], ResultsType> {

    @Override
    protected ResultsType doWork(TaskParameters<CoalesceFramework, String[]> parameters)
    {
        ResultsType result = new ResultsType();
        CoalesceFramework framework = parameters.getTarget();
        CoalesceValidator validator = new CoalesceValidator();
        String[] params = parameters.getParams();

        try
        {
            List<CoalesceEntity> entities = new ArrayList<CoalesceEntity>();
            
            for (String xml : params)
            {
                CoalesceEntity updated = new CoalesceEntity();
                if (updated.initialize(xml))
                {
                    CoalesceEntity original = framework.getCoalesceEntity(updated.getKey());

                    if (!original.isReadOnly())
                    {
                        CoalesceIteratorMerge merger = new CoalesceIteratorMerge();

                        updated.pruneCoalesceObject(updated.getLinkageSection());
                        updated = merger.merge(parameters.getPrincipalName(), parameters.getPrincipalIp(), original, updated);

                        // TODO Enable this
                        Map<String, String> results = new HashMap<String, String>();
                        // validator.validate(extra.getPrincipal(), entity,
                        // CoalesceConstraintCache.getCoalesceConstraints(entity));

                        if (results.size() != 0)
                        {
                            result.setStatus(EResultStatus.FAILED);
                            break;
                        }
                        
                        entities.add(updated);
                    }
                }
                else
                {
                    result.setStatus(EResultStatus.FAILED);
                    result.setResult(String.format(CoalesceErrors.NOT_INITIALIZED, "Entity"));
                }
            }

            if (framework.saveCoalesceEntity(entities.toArray(new CoalesceEntity[entities.size()])))
            {
                result.setStatus(EResultStatus.SUCCESS);
            }
            else
            {
                result.setStatus(EResultStatus.FAILED);
            }
        }
        catch (CoalesceException e)
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

        for (String type : params)
        {
            // TODO Not Implemented
        }

        return results;
    }
    
    @Override
    protected ResultsType createResult()
    {
        return new ResultsType();
    }


}
