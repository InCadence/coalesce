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

package com.incadencecorp.coalesce.framework.jobs;

import java.util.ArrayList;
import java.util.List;

import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceResponseType;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceStringResponseType;
import com.incadencecorp.coalesce.framework.tasks.AbstractPersistorTask;
import com.incadencecorp.coalesce.framework.tasks.CoalesceRegisterTemplateTask;

/**
 * This job registers templates.
 * 
 * @author n78554
 */
public class CoalesceRegisterTemplateJob extends AbstractCoalescePersistorsJob<CoalesceEntityTemplate[], CoalesceStringResponseType> {

    /**
     * Default Constructor
     */
    public CoalesceRegisterTemplateJob(CoalesceEntityTemplate[] params)
    {
        super(params);
    }

    @Override
    protected AbstractPersistorTask<CoalesceEntityTemplate[], CoalesceStringResponseType> createTask()
    {
        return new CoalesceRegisterTemplateTask();
    }

    @Override
    protected String[] getKeys(AbstractPersistorTask<CoalesceEntityTemplate[], CoalesceStringResponseType> task)
    {
        List<String> keys = new ArrayList<>();
        
        for (CoalesceEntityTemplate template : task.getParams())
        {
            keys.add(template.getName() + "_" + template.getSource() + "_" + template.getVersion());
        }

        return keys.toArray(new String[keys.size()]);
    }

    @Override
    protected ICoalesceResponseType<List<CoalesceStringResponseType>> createResponse()
    {
        return new CoalesceResponseType<>();
    }

    @Override
    protected CoalesceStringResponseType createResults()
    {
        return new CoalesceStringResponseType(); 
    }

}
