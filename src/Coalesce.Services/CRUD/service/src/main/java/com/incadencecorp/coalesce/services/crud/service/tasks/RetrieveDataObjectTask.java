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
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIteratorGetVersion;
import com.incadencecorp.coalesce.framework.tasks.AbstractFrameworkTask;
import com.incadencecorp.coalesce.framework.tasks.TaskParameters;
import com.incadencecorp.coalesce.framework.util.CoalesceNotifierUtil;
import com.incadencecorp.coalesce.services.api.common.ResultsType;
import com.incadencecorp.coalesce.services.api.crud.DataObjectKeyType;

public class RetrieveDataObjectTask extends AbstractFrameworkTask<DataObjectKeyType[], ResultsType> {

    @Override
    protected ResultsType doWork(TaskParameters<CoalesceFramework, DataObjectKeyType[]> parameters) throws CoalesceException
    {
        ResultsType result = new ResultsType();
        CoalesceIteratorGetVersion it = new CoalesceIteratorGetVersion();
        CoalesceFramework framework = parameters.getTarget();
        DataObjectKeyType[] params = parameters.getParams();

        for (DataObjectKeyType task : params)
        {
            CoalesceEntity entity = framework.getCoalesceEntity(task.getKey());

            if (task.getVer() == -1)
            {
                task.setVer(entity.getObjectVersion());
            }

            if (entity.isValidObjectVersion(task.getVer()))
            {
                it.getVersion(entity, task.getVer());
                result.setStatus(EResultStatus.SUCCESS);
                result.setResult(entity.toXml());
                
                CoalesceNotifierUtil.sendCrud(getName(), ECrudOperations.READ, entity);
            }
            else
            {
                result.setStatus(EResultStatus.FAILED);
                result.setError(String.format(CoalesceErrors.INVALID_OBJECT_VERSION, task.getVer(), task.getKey()));
            }

            result.setStatus(EResultStatus.SUCCESS);
        }

        return result;
    }

    @Override
    protected Map<String, String> getParameters(DataObjectKeyType[] params, boolean isTrace)
    {
        Map<String, String> results = new HashMap<String, String>();

        for (DataObjectKeyType type : params)
        {
            results.put(type.getKey(), Integer.toString(type.getVer()));
        }

        return results;
    }

    @Override
    protected ResultsType createResult()
    {
        return new ResultsType();
    }

}
