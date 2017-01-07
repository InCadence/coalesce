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
import com.incadencecorp.coalesce.services.api.crud.DataObjectLinkType;

public class UpdateDataObjectLinkagesTask extends AbstractFrameworkTask<DataObjectLinkType[], ResultsType> {

    @Override
    protected ResultsType doWork(CoalesceFramework framework, DataObjectLinkType[] params)
    {
        ResultsType result = new ResultsType(); 
        
        for (DataObjectLinkType task : params)
        {
            try
            {
                CoalesceEntity[] entities = framework.getCoalesceEntities(task.getDataObjectKeySource(),
                                                                          task.getDataObjectKeyTarget());

                // TODO Not Implemented
            }
            catch (CoalescePersistorException e)
            {
                result.setStatus(EResultStatus.FAILED);
                result.setResult(e.getMessage());
            }

        }
        
        return result;
    }

    @Override
    protected Map<String, String> getParameters(DataObjectLinkType[] params, boolean isTrace)
    {
        Map<String, String> results = new HashMap<String, String>();

        for (DataObjectLinkType type : params)
        {
            results.put("source", type.getDataObjectKeySource());
            results.put("target", type.getDataObjectKeyTarget());
            results.put("label", type.getLabel());
            results.put("action", type.getAction().toString());
            results.put("link type", type.getLinkType().toString());
        }

        return results;
    }

}
