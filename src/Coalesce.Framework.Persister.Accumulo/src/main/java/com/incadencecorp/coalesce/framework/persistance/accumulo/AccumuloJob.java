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

package com.incadencecorp.coalesce.framework.persistance.accumulo;

import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.api.ICoalescePrincipal;
import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.jobs.AbstractCoalesceJob;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceResponseType;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceStringResponseType;
import com.incadencecorp.coalesce.framework.tasks.MetricResults;
import org.geotools.data.DataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Derek Clemenzi
 */
public class AccumuloJob extends
        AbstractCoalesceJob<Map<String, AccumuloFeatureIterator.FeatureCollections>, ICoalesceResponseType<List<CoalesceStringResponseType>>, CoalesceStringResponseType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloJob.class);
    private DataStore datastore;

    public AccumuloJob(DataStore datastore, Map<String, AccumuloFeatureIterator.FeatureCollections> params)
    {
        super(params);

        this.datastore = datastore;
    }

    @Override
    protected ICoalesceResponseType<List<CoalesceStringResponseType>> doWork(ICoalescePrincipal principal,
                                                                             Map<String, AccumuloFeatureIterator.FeatureCollections> params)
            throws CoalesceException
    {
        List<CoalesceStringResponseType> results = new ArrayList<CoalesceStringResponseType>();

        try
        {
            List<AccumuloTask> tasks = new ArrayList<>();

            // Create Tasks
            for (Map.Entry<String, AccumuloFeatureIterator.FeatureCollections> entry : params.entrySet())
            {
                AccumuloTask task = new AccumuloTask();
                task.setParams(entry);
                task.setTarget(datastore);
                task.setPrincipal(principal);

                tasks.add(task);
            }

            int ii = 0;

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

                    metrics = new MetricResults<CoalesceStringResponseType>();
                    metrics.setResults(task);
                    metrics.getResults().setStatus(EResultStatus.FAILED);
                }

                addResult(metrics);

                results.add(metrics.getResults());

                ii++;
            }
        }
        catch (InterruptedException e)
        {
            throw new CoalesceException("Job Interrupted", e);
        }

        CoalesceResponseType<List<CoalesceStringResponseType>> result = new CoalesceResponseType<List<CoalesceStringResponseType>>();
        result.setResult(results);
        result.setStatus(EResultStatus.SUCCESS);

        return result;
    }

    @Override
    protected ICoalesceResponseType<List<CoalesceStringResponseType>> createResponse()
    {
        return new CoalesceResponseType<List<CoalesceStringResponseType>>();
    }

    @Override
    protected CoalesceStringResponseType createResults()
    {
        return new CoalesceStringResponseType();
    }
}
