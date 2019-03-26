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

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractOperation;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractOperationTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.util.ArrayList;

/**
 * This implementation copies entities from the source to the target(s).
 *
 * @author n78554
 */
public class MergeOperationImpl extends AbstractOperation<AbstractOperationTask> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CopyOperationImpl.class);

    @Override
    protected AbstractOperationTask createTask()
    {
        return new AbstractOperationTask() {

            @Override
            protected Boolean doWork(String[] keys, CachedRowSet rowset) throws CoalescePersistorException
            {

                boolean results = true;
                //keys are what we have that is ready to be sent

                // Get from Source
                CoalesceEntity[] entitiesSource = source.getEntity(keys);

                for (ICoalescePersistor target : targets)
                {
                    CoalesceEntity[] entitiesTarget = target.getEntity(keys);
                    ArrayList<CoalesceEntity> toCopyOrMergeEntities = new ArrayList<>();
                    if (keys.length != entitiesSource.length)
                    {
                        for (String key : keys)
                        {
                            boolean found = false;

                            for (CoalesceEntity entity : entitiesSource)
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
                    for (CoalesceEntity entitySource : entitiesSource)
                    {
                        boolean needsTransfer = true;
                        for (CoalesceEntity entityTarget : entitiesTarget)
                        {
                            if (entitySource.getKey().equalsIgnoreCase(entityTarget.getKey()))
                            {
                                //Check to see if it needs to be merged
                                int compare = entitySource.getLastModified().compareTo(entityTarget.getLastModified());
                                //If Source has a more Updated version
                                if (compare > 0)
                                {
                                    //add to merge option
                                    break;
                                }
                                else if (compare == 0 || compare < 0)
                                {
                                    //move on to the next one
                                    needsTransfer = false;
                                    break;
                                }
                            }
                        }
                        if (needsTransfer)
                        {
                            toCopyOrMergeEntities.add(entitySource);
                        }
                    }

                    CoalesceEntity[] toMerge = new CoalesceEntity[toCopyOrMergeEntities.size()];
                    toMerge = toCopyOrMergeEntities.toArray(toMerge);
                    // Copy/Merge to Targets
                    if (LOGGER.isDebugEnabled())
                    {
                        LOGGER.debug("Processing {} key(s)", toMerge.length);
                        LOGGER.trace("Details:");

                        for (CoalesceEntity entity : toMerge)
                        {
                            LOGGER.trace("\t{} {} {}", entity.getName(), entity.getSource(), entity.getKey());
                        }

                    }

                    results = results && target.saveEntity(false, toMerge);
                }

                return results;
            }

        };

    }

}
