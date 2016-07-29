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

package com.incadencecorp.coalesce.synchronizer.api.common.mocks;

import java.util.HashSet;
import java.util.Set;

import javax.sql.rowset.CachedRowSet;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractOperation;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractOperationTask;

/**
 * Mock Operation Implementation
 * 
 * @author n78554
 */
public class MockOperation extends AbstractOperation<AbstractOperationTask> {

    private String title;

    /**
     * Sets the title which will be set by this operation on each entity.
     * 
     * @param title
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    @Override
    protected AbstractOperationTask createTask()
    {
        return new AbstractOperationTask() {

            @Override
            protected Boolean doWork(String[] keys, CachedRowSet rowset) throws CoalescePersistorException
            {
                if (!StringHelper.isNullOrEmpty(title))
                {
                    for (String key : keys)
                    {
                        // Change Title
                        CoalesceEntity[] entities = source.getEntity(key);

                        for (CoalesceEntity entity : entities)
                        {
                            entity.setTitle(title);
                            target.saveEntity(false, entity);
                        }
                    }
                }
                else
                {
                    throw new CoalescePersistorException("Failed", null);
                }

                return true;
            }

        };
    }

    @Override
    public Set<String> getAdditionalRequiredColumns()
    {
        Set<String> columns = new HashSet<String>();
        columns.add(CoalescePropertyFactory.getEntityKey().getPropertyName());
        columns.add(CoalescePropertyFactory.getEntityTitle().getPropertyName());

        return columns;
    }

}
