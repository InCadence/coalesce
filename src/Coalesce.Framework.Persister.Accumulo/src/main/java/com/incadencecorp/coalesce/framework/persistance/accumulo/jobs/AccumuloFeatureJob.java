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

import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.api.ICoalescePrincipal;
import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.jobs.AbstractCoalesceJob;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceResponseType;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceStringResponseType;
import com.incadencecorp.coalesce.framework.persistance.accumulo.AccumuloDataConnector;
import com.incadencecorp.coalesce.framework.persistance.accumulo.AccumuloFeatureIterator;
import com.incadencecorp.coalesce.framework.persistance.accumulo.tasks.AccumuloDeleteMutationTask;
import com.incadencecorp.coalesce.framework.persistance.accumulo.tasks.AccumuloFeatureTask;
import com.incadencecorp.coalesce.framework.persistance.accumulo.tasks.AccumuloWriteMutationTask;
import com.incadencecorp.coalesce.framework.tasks.AbstractTask;
import com.incadencecorp.coalesce.framework.tasks.MetricResults;
import org.apache.accumulo.core.client.BatchWriterConfig;
import org.apache.accumulo.core.data.Mutation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This job creates {@link AccumuloFeatureTask} tasks.
 *
 * @author Derek Clemenzi
 */
public class AccumuloFeatureJob extends
        AbstractCoalesceJob<AccumuloDataConnector, ICoalesceResponseType<List<CoalesceStringResponseType>>, CoalesceStringResponseType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloFeatureJob.class);
    private Map<String, AccumuloFeatureIterator.FeatureCollections> features;
    private BatchWriterConfig config;
    private List<Mutation> entityMutations;
    private List<Mutation> indexMutations;
    private List<String> keysToDelete;

    public AccumuloFeatureJob(AccumuloDataConnector conn)
    {
        super(conn);
    }

    public void setFeatures(Map<String, AccumuloFeatureIterator.FeatureCollections> value)
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
    protected ICoalesceResponseType<List<CoalesceStringResponseType>> doWork(ICoalescePrincipal principal,
                                                                             AccumuloDataConnector params)
            throws CoalesceException
    {
        List<CoalesceStringResponseType> results = new ArrayList<>();

        try
        {
            List<AbstractTask<?, CoalesceStringResponseType, ?>> tasks = new ArrayList<>();

            // Create Tasks
            for (Map.Entry<String, AccumuloFeatureIterator.FeatureCollections> entry : features.entrySet())
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
                    task.setPrincipal(principal);

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

            // Execute Tasks
            for (Future<MetricResults<CoalesceStringResponseType>> future : getService().invokeAll(tasks))
            {
                MetricResults<CoalesceStringResponseType> metrics;

                try
                {
                    metrics = future.get();
                }
                catch (InterruptedException | ExecutionException e)
                {
                    LOGGER.error("Interrupted Task", e);

                    // Create Failed Result
                    CoalesceStringResponseType task = createResults();
                    task.setError(e.getMessage());
                    task.setStatus(EResultStatus.FAILED);

                    metrics = new MetricResults<>(getName());
                    metrics.setResults(task);
                    metrics.getResults().setStatus(EResultStatus.FAILED);
                }

                addResult(metrics);

                results.add(metrics.getResults());
            }
        }
        catch (InterruptedException e)
        {
            throw new CoalesceException("Job Interrupted", e);
        }

        CoalesceResponseType<List<CoalesceStringResponseType>> result = new CoalesceResponseType<>();
        result.setResult(results);
        result.setStatus(EResultStatus.SUCCESS);

        return result;
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
