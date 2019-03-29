/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.synchronizer.service.operations;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractOperation;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractOperationTask;
import org.geotools.data.Query;
import org.opengis.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This implementation verifies that the keys referenced are present within the target database.
 *
 * @author Derek Clemenzi
 */
public class VerifyOperationImpl extends AbstractOperation<AbstractOperationTask> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CopyOperationImpl.class);

    @Override
    protected AbstractOperationTask createTask()
    {
        return new AbstractOperationTask() {

            private final Set<String> missing = new HashSet<>();

            @Override
            protected Boolean doWork(String[] keys, CachedRowSet rowset) throws CoalescePersistorException
            {
                for (ICoalescePersistor target : targets)
                {
                    missing.addAll(verify(target, keys));
                }

                if (!missing.isEmpty())
                {
                    if (LOGGER.isDebugEnabled())
                    {
                        LOGGER.debug(CoalesceErrors.NOT_FOUND.replaceAll("%s", "{}"), "Entity", missing);
                    }

                    throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_FOUND, "Entity", missing));
                }

                return true;
            }

            @Override
            public String[] getErrorSubset()
            {
                return missing.toArray(new String[missing.size()]);
            }
        };
    }

    /**
     * @param target to verify
     * @param keys   which should be present within the target
     * @return a list of keys that are missing from the target.
     */
    public static Set<String> verify(ICoalescePersistor target, String[] keys) throws CoalescePersistorException
    {
        Set<String> missing;

        if (target instanceof ICoalesceSearchPersistor)
        {
            missing = verifyBySearch((ICoalesceSearchPersistor) target, keys);
        }
        else
        {
            missing = verifyByCRUD(target, keys);
        }

        return missing;
    }

    private static Set<String> verifyByCRUD(ICoalescePersistor persister, String[] keys) throws CoalescePersistorException
    {
        Set<String> results = new HashSet<>();

        // Get from Source
        CoalesceEntity[] entities = persister.getEntity(keys);

        if (keys.length != entities.length)
        {
            Set<String> entityKeys = new HashSet<>();

            for (CoalesceEntity entity : entities)
            {
                entityKeys.add(entity.getKey());
            }

            for (String key : keys)
            {
                if (!entityKeys.contains(key))
                {
                    results.add(key);
                }
            }

        }

        return results;
    }

    private static Set<String> verifyBySearch(ICoalesceSearchPersistor persister, String[] keys)
            throws CoalescePersistorException
    {
        Set<String> results = new HashSet<>();

        List<Filter> filters = new ArrayList<>();

        for (String key : keys)
        {
            filters.add(CoalescePropertyFactory.getEntityKey(key));
        }

        Query query = new Query();
        query.setMaxFeatures(keys.length);
        query.setFilter(CoalescePropertyFactory.getFilterFactory().or(filters));

        // Get from Source
        SearchResults hits = persister.search(query);

        if (hits.isSuccessful())
        {
            if (keys.length != hits.getResults().size())
            {
                Set<String> entityKeys = new HashSet<>();

                try (CachedRowSet rowset = hits.getResults())
                {
                    while (rowset.next())
                    {
                        entityKeys.add(rowset.getString(1));
                    }

                    for (String key : keys)
                    {
                        if (!entityKeys.contains(key))
                        {
                            results.add(key);
                        }
                    }
                }
                catch (SQLException e)
                {
                    throw new CoalescePersistorException(e);
                }
            }
        }
        else
        {
            throw new CoalescePersistorException(hits.getError());
        }

        return results;
    }

}
