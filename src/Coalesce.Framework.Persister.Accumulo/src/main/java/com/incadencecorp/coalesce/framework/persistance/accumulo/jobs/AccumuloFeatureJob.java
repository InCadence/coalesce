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

package com.incadencecorp.coalesce.framework.persistance.accumulo.jobs;

import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.jobs.AbstractStringResponseJob;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceResponseType;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceStringResponseType;
import com.incadencecorp.coalesce.framework.persistance.accumulo.AccumuloDataConnector;
import com.incadencecorp.coalesce.framework.persistance.accumulo.AccumuloFeatureIterator;
import com.incadencecorp.coalesce.framework.persistance.accumulo.FeatureCollections;
import com.incadencecorp.coalesce.framework.persistance.accumulo.tasks.AccumuloDeleteMutationTask;
import com.incadencecorp.coalesce.framework.persistance.accumulo.tasks.AccumuloFeatureTask;
import com.incadencecorp.coalesce.framework.persistance.accumulo.tasks.AccumuloWriteMutationTask;
import com.incadencecorp.coalesce.framework.tasks.AbstractTask;
import org.apache.accumulo.core.client.BatchWriterConfig;
import org.apache.accumulo.core.data.Mutation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This job updates CoalesceEntity entries in the data store .
 *
 * @author Derek Clemenzi
 */
public class AccumuloFeatureJob extends AbstractStringResponseJob<AccumuloDataConnector> {

    private Map<String, FeatureCollections> features;
    private BatchWriterConfig config;
    private List<Mutation> entityMutations;
    private List<Mutation> indexMutations;
    private List<String> keysToDelete;

    public AccumuloFeatureJob(AccumuloDataConnector conn)
    {
        super(conn);
    }

    public void setFeatures(Map<String, FeatureCollections> value)
    {
        this.features = value;
    }

    public void setConfig(BatchWriterConfig value)
    {
        this.config = value;
    }

    public void setMutations(List<Mutation> entity, List<Mutation> indexes)
    {
        this.entityMutations = entity;
        this.indexMutations = indexes;
    }

    public void setKeysToDelete(List<String> value)
    {
        this.keysToDelete = value;
    }

    @Override
    protected Collection<AbstractTask<?, CoalesceStringResponseType, ?>> getTasks(AccumuloDataConnector params)
            throws CoalesceException
    {
        List<AbstractTask<?, CoalesceStringResponseType, ?>> tasks = new ArrayList<>();

        // Create Tasks
        for (Map.Entry<String, FeatureCollections> entry : features.entrySet())
        {
            if (entry.getValue().featuresToAdd.size() > 0 || entry.getValue().keysToDelete.size() > 0)
            {
                AccumuloFeatureTask task = new AccumuloFeatureTask();
                task.setName(String.format("%s) Updating (%s [+%s / -%s]",
                                           task.getName(),
                                           entry.getKey(),
                                           entry.getValue().featuresToAdd.size(),
                                           entry.getValue().keysToDelete.size()));
                task.setParams(entry);
                task.setTarget(params.getGeoDataStore());

                tasks.add(task);
            }
        }

        if (entityMutations != null && entityMutations.size() > 0)
        {
            AccumuloWriteMutationTask task = new AccumuloWriteMutationTask();
            task.setName(String.format("%s) Updating (%s [+%s]",
                                       task.getName(),
                                       AccumuloDataConnector.COALESCE_ENTITY_TABLE,
                                       entityMutations.size()));
            task.setConfig(config);
            task.setTablename(AccumuloDataConnector.COALESCE_ENTITY_TABLE);
            task.setParams(entityMutations);
            task.setTarget(params);

            tasks.add(task);
        }

        if (indexMutations != null && indexMutations.size() > 0)
        {
            AccumuloWriteMutationTask task = new AccumuloWriteMutationTask();
            task.setName(String.format("%s) Updating (%s [+%s]",
                                       task.getName(),
                                       AccumuloDataConnector.COALESCE_ENTITY_IDX_TABLE,
                                       indexMutations.size()));
            task.setConfig(config);
            task.setTablename(AccumuloDataConnector.COALESCE_ENTITY_IDX_TABLE);
            task.setParams(indexMutations);
            task.setTarget(params);

            tasks.add(task);
        }

        if (keysToDelete != null && keysToDelete.size() > 0)
        {
            AccumuloDeleteMutationTask task1 = new AccumuloDeleteMutationTask();
            task1.setName(String.format("%s) Updating (%s [-%s]",
                                        task1.getName(),
                                        AccumuloDataConnector.COALESCE_ENTITY_TABLE,
                                        keysToDelete.size()));
            task1.setConfig(config);
            task1.setTablename(AccumuloDataConnector.COALESCE_ENTITY_TABLE);
            task1.setParams(keysToDelete);
            task1.setTarget(params);

            AccumuloDeleteMutationTask task2 = new AccumuloDeleteMutationTask();
            task2.setName(String.format("%s) Updating (%s [-%s]",
                                        task2.getName(),
                                        AccumuloDataConnector.COALESCE_ENTITY_IDX_TABLE,
                                        keysToDelete.size()));
            task2.setConfig(config);
            task2.setTablename(AccumuloDataConnector.COALESCE_ENTITY_IDX_TABLE);
            task2.setParams(keysToDelete);
            task2.setTarget(params);

            tasks.add(task1);
            tasks.add(task2);
        }

        return tasks;
    }

    @Override
    protected ICoalesceResponseType<List<CoalesceStringResponseType>> createResponse()
    {
        return new CoalesceResponseType<>();
    }

    @Override
    protected CoalesceStringResponseType createResults()
    {
        return new CoalesceStringResponseType();
    }
}
