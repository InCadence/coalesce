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

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.tasks.AbstractPersistorTask;
import com.incadencecorp.coalesce.framework.tasks.CoalesceSaveEntityTask;

/**
 * 
 * @author Derek
 *
 */
public class CoalesceSaveEntityJob extends AbstractCoalescePersistorsJob<CoalesceSaveEntityProperties> {

    /**
     * Constructor
     * 
     * @param params
     */
    public CoalesceSaveEntityJob(CoalesceSaveEntityProperties params)
    {
        super(params);
    }

    @Override
    protected AbstractPersistorTask<CoalesceSaveEntityProperties> createTask()
    {
        return new CoalesceSaveEntityTask();
    }

    @Override
    protected String[] getKeys(AbstractPersistorTask<CoalesceSaveEntityProperties> task)
    {
        List<String> keys = new ArrayList<String>(); 
        
        for (CoalesceEntity entity : task.getParams().getEntities())
        {
            keys.add(entity.getKey());
        }

        return keys.toArray(new String[keys.size()]);
    }

}
