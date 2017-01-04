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

package com.incadencecorp.coalesce.framework.jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.EJobStatus;
import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.api.ICoalesceFrameworkJob;
import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.api.persistance.ICoalesceExecutorService;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.jobs.metrics.StopWatch;
import com.incadencecorp.coalesce.framework.tasks.AbstractFrameworkTask;
import com.incadencecorp.coalesce.framework.tasks.MetricResults;

/**
 * Abstract base for persister jobs in Coalesce.
 * 
 * @author Derek
 * @param <T> input type
 */
public abstract class AbstractCoalesceFrameworkJob<T, Y extends ICoalesceResponseType<List<X>>, X extends ICoalesceResponseType<?>>
        extends AbstractCoalesceJob<T, Y> implements ICoalesceFrameworkJob {

    private static Logger LOGGER = LoggerFactory.getLogger(AbstractCoalesceFrameworkJob.class);

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private ICoalesceExecutorService _service;
    private CoalesceFramework _framework;
    private List<MetricResults<X>> taskMetrics = null;

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/

    /**
     * Sets the persistors that the task will be performed on.
     * 
     * @param params task parameters.
     */
    public AbstractCoalesceFrameworkJob(T params)
    {
        super(params);
    }

    /*--------------------------------------------------------------------------
    Getters / Setters
    --------------------------------------------------------------------------*/

    @Override
    public final void setExecutor(ICoalesceExecutorService service)
    {
        _service = service;
    }

    @Override
    public final void setTarget(CoalesceFramework framework)
    {
        _framework = framework;
    }

    @Override
    public final MetricResults<X>[] getTaskMetrics()
    {
        return (MetricResults<X>[]) taskMetrics.toArray();
    }
    
    /**
     * @return the response with the job ID and status. Also includes the
     *         results if completed or an error message if an exception was
     *         thrown with the status of JOB_FAILED.
     */
    public final Y getResponse()
    {
        Y response = null;

        if (isDone())
        {
            try
            {
                // Get Response
                response = getFuture().get();
                if (getJobStatus() == EJobStatus.COMPLETE)
                {
                    response.setStatus(EResultStatus.SUCCESS);
                }
                else
                {
                    response.setStatus(EResultStatus.FAILED_PENDING);
                }
            }
            catch (InterruptedException | ExecutionException e)
            {
                LOGGER.error(e.getMessage(), e);

                // Create Response w/ Error Message
                response = createResponse();
                response.setStatus(EResultStatus.FAILED);
            }
        }
        else
        {
            // Create Response w/o Results
            response = createResponse();
            if (getJobStatus() == EJobStatus.COMPLETE)
            {
                response.setStatus(EResultStatus.SUCCESS);
            }
            else
            {
                response.setStatus(EResultStatus.FAILED_PENDING);
            }

            // Check for Race Condition (Job completed after isDone() check and
            // before response creation)
            if (getJobStatus() == EJobStatus.COMPLETE)
            {
                setJobStatus(EJobStatus.IN_PROGRESS);
            }
        }

        response.setId(getJobId());

        return response;
    }

    /*--------------------------------------------------------------------------
    Override Methods
    --------------------------------------------------------------------------*/

    @Override
    public final Y doWork(T params) throws CoalesceException
    {
        Y response = createResponse();

        Collection<AbstractFrameworkTask<?, X>> tasks = getTasks(params);

        for (AbstractFrameworkTask<?, X> task : tasks)
        {
            task.setFramework(_framework);
        }

        // Execute Tasks
        try
        {
            taskMetrics = new ArrayList<MetricResults<X>>();

            for (Future<MetricResults<X>> future : _service.invokeAll(tasks))
            {
                MetricResults<X> result;

                try
                {
                    result = future.get();
                }
                catch (InterruptedException | ExecutionException e)
                {
                    LOGGER.error(e.getMessage(), e);

                    result = new MetricResults<X>();
                    result.setResults(createFailedResults(e));
                    result.getResults().setStatus(EResultStatus.FAILED);
                }

                taskMetrics.add(result);

                // Add Result to Response
                response.getResult().add(result.getResults());
            }
        }
        catch (InterruptedException e)
        {
            throw new CoalesceException("Job Interrupted", e);
        }
        
        return response;
    }

    /*--------------------------------------------------------------------------
    Abstract Methods
    --------------------------------------------------------------------------*/

    protected abstract Collection<AbstractFrameworkTask<?, X>> getTasks(T params);

    protected abstract Y createResponse();

    protected abstract X createFailedResults(Exception e);

}
