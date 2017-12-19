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
import com.incadencecorp.coalesce.framework.persistance.accumulo.tasks.AccumuloCreateSchemaTask;
import com.incadencecorp.coalesce.framework.tasks.MetricResults;
import org.geotools.data.DataStore;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This job creates {@link AccumuloCreateSchemaTask} tasks.
 *
 * @author Derek Clemenzi
 */
public class AccumuloCreateSchemaJob extends
        AbstractCoalesceJob<List<SimpleFeatureType>, ICoalesceResponseType<List<CoalesceStringResponseType>>, CoalesceStringResponseType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloFeatureJob.class);
    private DataStore datastore;

    public AccumuloCreateSchemaJob(DataStore datastore, List<SimpleFeatureType> features)
    {
        super(features);

        this.datastore = datastore;
    }

    @Override
    protected ICoalesceResponseType<List<CoalesceStringResponseType>> doWork(ICoalescePrincipal principal,
                                                                             List<SimpleFeatureType> features)
            throws CoalesceException
    {
        List<CoalesceStringResponseType> results = new ArrayList<>();

        try
        {
            List<AccumuloCreateSchemaTask> tasks = new ArrayList<>();

            // Create Tasks
            for (SimpleFeatureType feature : features)
            {
                AccumuloCreateSchemaTask task = new AccumuloCreateSchemaTask();
                task.setParams(feature);
                task.setTarget(datastore);
                task.setPrincipal(principal);

                tasks.add(task);
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
