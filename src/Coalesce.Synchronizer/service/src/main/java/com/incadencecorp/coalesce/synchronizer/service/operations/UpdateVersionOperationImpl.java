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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.iterators.CoalesceUpdaterIterator;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractOperation;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractOperationTask;
import com.incadencecorp.coalesce.synchronizer.api.common.SynchronizerParameters;

/**
 * This implementation attempts to update entities that are out of date.
 * 
 * @author n78554
 * @see SynchronizerParameters#PARAM_OP_DRYRUN
 */
public class UpdateVersionOperationImpl extends AbstractOperation<AbstractOperationTask> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateVersionOperationImpl.class);
    private static final Map<String, CoalesceUpdaterIterator> ITERATORS = new HashMap<String, CoalesceUpdaterIterator>();
    private static final String CRLF = "\r\n";
    private boolean isDryrun = false;

    @Override
    public void setProperties(Map<String, String> params)
    {
        super.setProperties(params);

        if (params.containsKey(SynchronizerParameters.PARAM_OP_DRYRUN))
        {
            isDryrun = Boolean.valueOf(params.get(SynchronizerParameters.PARAM_OP_DRYRUN));
        }
    }

    @Override
    protected AbstractOperationTask createTask()
    {
        return new AbstractOperationTask() {

            @Override
            protected Boolean doWork(String[] keys, CachedRowSet rowset) throws CoalescePersistorException
            {
                // Get from Source
                CoalesceEntity[] entities = source.getEntity(keys);
                List<CoalesceEntity> updatedEntities = new ArrayList<CoalesceEntity>();

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

                StringBuilder sb = new StringBuilder("");

                for (CoalesceEntity entity : entities)
                {
                    CoalesceUpdaterIterator iterator = getIterator(entity);
                    try
                    {
                        CoalesceEntity updated = iterator.iterate(entity);
                        if (iterator.getUpdates().size() > 0)
                        {
                            if (LOGGER.isInfoEnabled())
                            {
                                sb.append(String.format("Updated (%s), (%s), (%s), (%s)%n",
                                                        entity.getKey(),
                                                        entity.getName(),
                                                        entity.getSource(),
                                                        entity.getVersion()));
                                if (LOGGER.isTraceEnabled())
                                {
                                    for (String update : iterator.getUpdates())
                                    {
                                        sb.append("\t" + update + CRLF);
                                    }
                                }
                            }

                            updatedEntities.add(updated);
                        }
                    }
                    catch (CoalesceException e)
                    {
                        throw new CoalescePersistorException(String.format("(FAILED) Updating Entity (%s), (%s), (%s), (%s)",
                                                                           entity.getKey(),
                                                                           entity.getName(),
                                                                           entity.getSource(),
                                                                           entity.getVersion()));
                    }

                }

                LOGGER.info("Processed ({}) key(s) and updated ({}): " + CRLF + "{}",
                            entities.length,
                            updatedEntities.size(),
                            sb.toString());

                if (updatedEntities.size() > 0)
                {
                    if (!isDryrun)
                    {
                        target.saveEntity(false, updatedEntities.toArray(new CoalesceEntity[updatedEntities.size()]));
                    }
                    else
                    {
                        LOGGER.info("DRY RUN MODE");
                    }
                }

                return true;
            }

            private CoalesceUpdaterIterator getIterator(CoalesceEntity entity) throws CoalescePersistorException
            {
                String key = String.format("%s_%s", entity.getName(), entity.getSource());

                if (!ITERATORS.containsKey(key))
                {
                    for (ObjectMetaData meta : source.getEntityTemplateMetadata())
                    {
                        if (meta.getName().equalsIgnoreCase(entity.getName())
                                && meta.getSource().equalsIgnoreCase(entity.getSource()))
                        {
                            ITERATORS.put(key, new CoalesceUpdaterIterator(source.getEntityTemplate(meta.getKey())));
                            break;
                        }
                    }
                }

                if (!ITERATORS.containsKey(key))
                {
                    throw new CoalescePersistorException(String.format(CoalesceErrors.TEMPLATE_LOAD,
                                                                       entity.getName(),
                                                                       entity.getSource(),
                                                                       entity.getVersion()));
                }

                return ITERATORS.get(key);
            }

        };
    }
}
