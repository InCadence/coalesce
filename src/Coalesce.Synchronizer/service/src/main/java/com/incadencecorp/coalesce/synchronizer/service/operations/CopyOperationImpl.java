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

package com.incadencecorp.coalesce.synchronizer.service.operations;

import javax.sql.rowset.CachedRowSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractOperation;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractOperationTask;

/**
 * This implementation copies entities from the source to the target(s).
 * 
 * @author n78554
 */
public class CopyOperationImpl extends AbstractOperation<AbstractOperationTask> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CopyOperationImpl.class);

    @Override
    protected AbstractOperationTask createTask()
    {
        return new AbstractOperationTask() {

            @Override
            protected Boolean doWork(String[] keys, CachedRowSet rowset) throws CoalescePersistorException
            {
                // Get from Source
                CoalesceEntity[] entities = source.getEntity(keys);

                if (keys.length != entities.length)
                {
                    for (String key : keys)
                    {
                        boolean found = false;

                        for (CoalesceEntity entity : entities)
                        {
                            if (entity.getKey().equalsIgnoreCase(key))
                            {
                                found = true;
                                break;
                            }
                        }

                        if (!found)
                        {
                            throw new CoalescePersistorException("Entity " + key + " was not found", null);
                        }
                    }
                }

                // Copy to Targets
                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("Processing {} key(s)", entities.length);
                    LOGGER.trace("Details:");

                    for (CoalesceEntity entity : entities)
                    {
                        LOGGER.trace("\t{} {} {}", entity.getName(), entity.getSource(), entity.getKey());
                    }

                }

                saveWork(false, entities);

                return true;
            }

        };
    }

}
