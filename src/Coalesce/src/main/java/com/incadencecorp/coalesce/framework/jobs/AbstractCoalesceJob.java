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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.EJobStatus;
import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.api.ICoalesceJob;
import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.CoalesceComponentImpl;
import com.incadencecorp.coalesce.framework.jobs.metrics.StopWatch;
import com.incadencecorp.coalesce.framework.tasks.MetricResults;

/**
 * Abstract base for jobs in Coalesce.
 * 
 * @author Derek
 * @param <T> input type
 * @param <Y> output type
 */
public abstract class AbstractCoalesceJob<T, Y extends ICoalesceResponseType<?>> extends CoalesceComponentImpl
        implements ICoalesceJob, Callable<Y> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCoalesceJob.class);
    
    /*--------------------------------------------------------------------------
    Member Variables
    --------------------------------------------------------------------------*/

    private T params;
    private StopWatch watch;
    private String id;
    private EJobStatus status;
    private Y results;
    private Future<Y> future;

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/

    /**
     * @param params parameters to pass to the task.
     */
    public AbstractCoalesceJob(T params)
    {
        this.params = params;
        this.id = UUID.randomUUID().toString();
        this.watch = new StopWatch();
        this.status = EJobStatus.NEW;
    }

    /*--------------------------------------------------------------------------
    Override Methods
    --------------------------------------------------------------------------*/

    @Override
    public final Y call()
    {
        // Set Start Time
        watch.start();

        // Set Status to In Progress
        status = EJobStatus.IN_PROGRESS;

        try
        {
            results = this.doWork(params);
        }
        catch (CoalesceException e)
        {
            LOGGER.error("(FAILED) Job Execution", e);

            status = EJobStatus.FAILED;
        }

        // Set Complete Time
        watch.finish();

        if (results.getStatus() != EResultStatus.SUCCESS)
        {
            // All other states are treated as failures because this is a
            // synchronous call.
            status = EJobStatus.FAILED;
        }

        return results;
    }

    /*--------------------------------------------------------------------------
    Public Methods
    --------------------------------------------------------------------------*/

    /**
     * @return the job's status.
     */
    public final EJobStatus getJobStatus()
    {
        return status;
    }

    /**
     * @return the job's ID
     */
    public final String getJobId()
    {
        return id;
    }

    /**
     * @return the parameters.
     */
    public final T getParams()
    {
        return params;
    }

    /**
     * @return the metrics for running the job.
     */
    public final StopWatch getMetrics()
    {
        return watch;
    }

    /**
     * @return <code>true</code> if the job is complete.
     * @see java.util.concurrent.Future#isDone()
     */
    public final boolean isDone()
    {
        return future != null && future.isDone();
    }

    /**
     * @return <code>true</code> if the job is canceled.
     * @see java.util.concurrent.Future#isCancelled()
     */
    public final boolean isCanceled()
    {
        return future != null && future.isCancelled();
    }

    /**
     * @param future the future to set
     */
    public final void setFuture(Future<?> future)
    {
        this.future = (Future<Y>) future;
    }

    /**
     * @return the {@link Future} for this job
     */
    public final Future<Y> getFuture()
    {
        return this.future;
    }

    /**
     * @return <code>True</code> if the job is to be ran non-blocking, otherwise
     *         <code>False</code>.
     */
    public boolean isAsync()
    {
        return false;
    }

    /**
     * @return the metrics of any task performed by this job.
     */
    public MetricResults<?>[] getTaskMetrics()
    {
        return null;
    }
    
    /*--------------------------------------------------------------------------
    Protected Methods
    --------------------------------------------------------------------------*/

    public final void setJobStatus(EJobStatus value)
    {
        status = value;
    }

    /**
     * @return the job's ID
     */
    public final void setJobId(String value)
    {
        id = value;
    }

    /*--------------------------------------------------------------------------
    Abstract Methods
    --------------------------------------------------------------------------*/

    /**
     * Performs the work of the job.
     */
    protected abstract Y doWork(T params) throws CoalesceException;

}
