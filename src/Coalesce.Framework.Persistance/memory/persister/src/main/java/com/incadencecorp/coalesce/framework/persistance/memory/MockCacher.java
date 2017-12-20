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

package com.incadencecorp.coalesce.framework.persistance.memory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.persistance.ECoalesceCacheStates;
import com.incadencecorp.coalesce.framework.persistance.ICoalesceCacher;

/**
 * This implementation uses a hash map.
 * 
 * @author n78554
 *
 */
public class MockCacher implements ICoalesceCacher {

    private ConcurrentMap<String, CoalesceEntity> cache = new ConcurrentHashMap<String, CoalesceEntity>();

    @Override
    public boolean containsEntity(String key)
    {
        return cache.containsKey(key);
    }

    @Override
    public boolean storeEntity(CoalesceEntity entity)
    {
        cache.put(entity.getKey(), entity);

        return true;
    }

    @Override
    public CoalesceEntity retrieveEntity(String key)
    {
        return cache.get(key);
    }

    @Override
    public boolean removeEntity(String key)
    {
        cache.remove(key);
        return true;
    }

    @Override
    public long entityCount()
    {
        return cache.size();
    }

    @Override
    public boolean clearCache()
    {
        cache.clear();
        return true;
    }

    @Override
    public ECoalesceCacheStates getState()
    {
        return ECoalesceCacheStates.SPACE_AVAILABLE;
    }

    @Override
    public boolean getSupportsDelayedSave()
    {
        return false;
    }

}
