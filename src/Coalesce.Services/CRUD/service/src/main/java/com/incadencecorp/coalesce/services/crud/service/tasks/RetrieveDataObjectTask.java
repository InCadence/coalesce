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
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIteratorGetVersion;
import com.incadencecorp.coalesce.framework.tasks.AbstractFrameworkTask;
import com.incadencecorp.coalesce.services.api.common.ResultsType;
import com.incadencecorp.coalesce.services.api.crud.DataObjectKeyType;

public class RetrieveDataObjectTask extends AbstractFrameworkTask<DataObjectKeyType[], ResultsType> {

    @Override
    protected ResultsType doWork(CoalesceFramework framework, DataObjectKeyType[] params)
    {
        ResultsType result = new ResultsType();

        try
        {
            for (DataObjectKeyType task : params)
            {
                if (task.getVer() != -1)
                {
                    CoalesceIteratorGetVersion it = new CoalesceIteratorGetVersion();

                    // Revert to Specified Version
                    CoalesceEntity entity = framework.getCoalesceEntity(task.getKey());
                    it.getVersion(entity, task.getVer());

                    result.setResult(entity.toXml());
                }
                else
                {
                    result.setResult(framework.getEntityXml(task.getKey()));
                }
                result.setStatus(EResultStatus.SUCCESS);
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
    protected Map<String, String> getParameters(DataObjectKeyType[] params, boolean isTrace)
    {
        Map<String, String> results = new HashMap<String, String>();

        for (DataObjectKeyType type : params)
        {
            results.put(type.getKey(), Integer.toString(type.getVer()));
        }

        return results;
    }

}
