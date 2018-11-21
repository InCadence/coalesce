/*-----------------------------------------------------------------------------'
 Copyright 2018 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.synchronizer.service.operations;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractOperation;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractOperationTask;

import javax.sql.rowset.CachedRowSet;
import java.util.Map;

/**
 * This implementation can either mark entities returned by the scanner as deleted or physically delete them from the database.
 *
 * @author Derek Clemenzi
 * @see #PARAM_MARK_AS_DELETED
 */
public class DeleteOperationImpl extends AbstractOperation<AbstractOperationTask> {

    /**
     * (Boolean) If true then the entities are only marked as deleted; otherwise they are physically deleted.
     */
    public static final String PARAM_MARK_AS_DELETED = DeleteOperationImpl.class.getName() + ".markOnly";

    private boolean allowRemoval = false;

    @Override
    public void setProperties(Map<String, String> params)
    {
        super.setProperties(params);

        allowRemoval = Boolean.parseBoolean(parameters.getOrDefault(PARAM_MARK_AS_DELETED, "false"));
    }

    @Override
    protected AbstractOperationTask createTask()
    {
        return new AbstractOperationTask() {

            @Override
            protected Boolean doWork(String[] keys, CachedRowSet rowset) throws CoalescePersistorException
            {
                CoalesceEntity[] entities = target.getEntity(keys);

                for (CoalesceEntity entity : entities)
                {
                    entity.markAsDeleted();
                }

                return target.saveEntity(allowRemoval, entities);
            }
        };
    }
}
