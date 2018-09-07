/*-----------------------------------------------------------------------------'
 Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.framework.tasks;

import java.util.HashMap;
import java.util.Map;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceStringResponseType;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;

/**
 * This task saves templates.
 * 
 * @author Derek
 */
public class CoalesceSaveTemplateTask extends AbstractPersistorTask<CoalesceEntityTemplate[], CoalesceStringResponseType> {

    @Override
    protected CoalesceStringResponseType doWork(TaskParameters<ICoalescePersistor, CoalesceEntityTemplate[]> parameters)
            throws CoalesceException
    {
        CoalesceStringResponseType result = new CoalesceStringResponseType();

        try
        {
            parameters.getTarget().saveTemplate(parameters.getParams());
        }
        catch (CoalescePersistorException e)
        {
            result.setException(e);
        }

        return result;
    }

    @Override
    protected Map<String, String> getParameters(CoalesceEntityTemplate[] params, boolean isTrace)
    {
        Map<String, String> results = new HashMap<>();

        results.put("entity total", String.valueOf(params.length));

        for (int ii = 0; ii < params.length; ii++)
        {
            if (isTrace)
            {
                results.put("entity[" + ii + "]", params[ii].toXml());
            }
            else
            {
                results.put("entity[" + ii + "]",
                            params[ii].getName() + "/" + params[ii].getSource() + "/" + params[ii].getVersion());
            }
        }

        return results;
    }
    
    @Override
    protected CoalesceStringResponseType createResult()
    {
        return new CoalesceStringResponseType();
    }


}
