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

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractOperation;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractOperationTask;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Mock Operation Implementation
 *
 * @author n78554
 */
public class MockOperation extends AbstractOperation<AbstractOperationTask> {

    private boolean throwException;

    /**
     * Sets whether the operation should throw an exception.
     *
     * @param value
     */
    public void setThrowException(boolean value)
    {
        this.throwException = value;
    }

    @Override
    protected AbstractOperationTask createTask()
    {
        return new AbstractOperationTask() {

            @Override
            protected Boolean doWork(String[] keys, CachedRowSet rowset) throws CoalescePersistorException
            {
                if (throwException)
                {
                    throw new CoalescePersistorException("Hello World");
                }

                try
                {
                    // Change Title
                    CoalesceEntity[] entities = source.getEntity(keys);

                    if (rowset.first())
                    {
                        int ii = 0;

                        do
                        {
                            entities[ii++].setTitle(rowset.getString(1));
                        }
                        while (rowset.next());
                    }

                    return saveWork(false, entities);
                }
                catch (SQLException e)
                {
                    throw new CoalescePersistorException(e);
                }
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
