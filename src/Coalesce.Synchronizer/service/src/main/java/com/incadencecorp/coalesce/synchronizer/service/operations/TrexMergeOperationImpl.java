
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

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractOperationTask;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractTrexOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.util.ArrayList;

/**
 * The goal of this merge operation is to be able to move from one target to another if the first one is closed or has connection issues.
 *
 * This is for a more round robin style operation.
 */

public class TrexMergeOperationImpl extends AbstractTrexOperation<AbstractOperationTask> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CopyOperationImpl.class);


    @Override
    protected AbstractOperationTask createTask()
    {
        return new AbstractOperationTask() {


            @Override
            protected Boolean doWork(String[] keys, CachedRowSet rowset) throws CoalescePersistorException
            {
                for(ICoalescePersistor target:targets)
                {
                    // Get from Source
                    CoalesceEntity[] entities = source.getEntity(keys);
                    CoalesceEntity[] targetEntities = target.getEntity(keys);
                    CoalesceEntity[] toTarget;
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
                    if (targetEntities.length != 0)
                    {
                        toTarget = getEntitiesToTarget(entities, targetEntities);
                    }
                    else
                    {
                        toTarget = entities;
                    }
                    // Copy to Targets
                    if (LOGGER.isDebugEnabled())
                    {
                        LOGGER.debug("Processing {} key(s) for {}", entities.length, target.getClass().getName());
                        LOGGER.trace("Details:");

                        for (CoalesceEntity entity : toTarget)
                        {
                            LOGGER.trace("\tMerged Entity: {} {} {}", entity.getName(), entity.getSource(), entity.getKey());
                        }
                    }
                    target.saveEntity(false, toTarget);
                    return true;
                }
                return false;
            }
        };
    }

    public CoalesceEntity[] getEntitiesToTarget(CoalesceEntity[] entities, CoalesceEntity[] targetEntities)
    {
        ArrayList<CoalesceEntity> mergedArrayList = new ArrayList<>();
        try
        {
            for (CoalesceEntity entity : targetEntities)
            {
                CoalesceEntity MergedEntity;
                for (CoalesceEntity sourceEntity : entities)
                {
                    if (entity.getKey().equals(sourceEntity.getKey()))
                    {
                        int a = entity.getLastModified().compareTo(sourceEntity.getLastModified());
                        if (a < 0)
                        {
                            MergedEntity = CoalesceEntity.mergeSyncEntity(entity, sourceEntity, "userId", "ip");
                            mergedArrayList.add(MergedEntity);
                        }else if(a > 0)
                        {
                            MergedEntity = CoalesceEntity.mergeSyncEntity(sourceEntity,entity,"userId","ip");
                            mergedArrayList.add(MergedEntity);
                        }
                    }else
                    {
                        MergedEntity = sourceEntity;
                        mergedArrayList.add(MergedEntity);
                    }
                }
            }
        }
        catch (CoalesceException e)
        {
            e.printStackTrace();
        }
        CoalesceEntity[] mergedList = new CoalesceEntity[mergedArrayList.size()];
        for(int i = 0; i<mergedArrayList.size(); i++)
        {
            mergedList[i] = mergedArrayList.get(i);
        }
        return mergedList;
    }



}
