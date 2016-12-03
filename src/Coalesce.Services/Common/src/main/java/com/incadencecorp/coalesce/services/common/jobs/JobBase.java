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

package com.incadencecorp.coalesce.services.common.jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.services.api.common.BaseRequest;
import com.incadencecorp.coalesce.services.api.common.BaseResponse;
import com.incadencecorp.coalesce.services.api.common.EJobStatusType;
import com.incadencecorp.coalesce.services.api.common.MetricsResultsType;
import com.incadencecorp.coalesce.services.common.api.ICoalesceExecutorService;
import com.incadencecorp.coalesce.services.common.metrics.StopWatch;

/**
 * Abstract base class to be extended to create jobs.
 *
 * @author Derek C.
 * @param <T> Request that this job handles; must extend {@link BaseRequest}.
 * @param <Y> Response that this job produces; must extend {@link BaseResponse}.
 */
public abstract class JobBase<T extends BaseRequest, Y extends BaseResponse> implements Callable<Y> {

    // ----------------------------------------------------------------------//
    // Private Members
    // ----------------------------------------------------------------------//

    private static final Logger LOGGER = LoggerFactory.getLogger(JobBase.class);

    private T request;
    private Future<Y> future;
    private StopWatch metrics;

    private UUID jobID;
    private EJobStatusType jobStatus;

    private ICoalesceExecutorService executor;

    private Collection<MetricsResultsType> taskResults;

    // ----------------------------------------------------------------------//
    // Constructor
    // ----------------------------------------------------------------------//

    /**
     * Creates a job based off of the request and initializes the response
     * object.
     *
     * @param request the request
     */
    public JobBase(T request)
    {
        this.request = request;
        this.jobID = UUID.randomUUID();
        this.jobStatus = EJobStatusType.NEW;
        this.metrics = new StopWatch();
        this.taskResults = new ArrayList<>();
    }

    // ----------------------------------------------------------------------//
    // Public Properties
    // ----------------------------------------------------------------------//

    /**
     * @return <code>True</code> if the job is to be ran not blocking, otherwise
     *         <code>False</code>.
     */
    public final boolean isAsync()
    {
        return request.isAsyncCall();
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
     * @return the job's ID.
     */
    public final UUID getJobID()
    {
        return jobID;
    }

    /**
     * @return the job's status.
     */
    public final EJobStatusType getJobStatus()
    {

        if (isAsync())
        {
            if (future == null)
            {
                return EJobStatusType.NEW;
            }
            else if (this.isCanceled())
            {
                return EJobStatusType.CANCELED;
            }
            else
            {
                return jobStatus;
            }
        }
        else
        {
            return jobStatus;
        }
    }

    /**
     * @return the original request that spawned the job
     */
    public final T getRequest()
    {
        return request;
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
                response = future.get();
                response.setJobId(this.jobID.toString());
                response.setJobStatus(this.jobStatus);

            }
            catch (InterruptedException | ExecutionException e)
            {
                LOGGER.error(e.getMessage(), e);

                // Create Response w/ Error Message
                response = initializeResponse();
                response.setJobId(this.getJobID().toString());
                response.setJobStatus(EJobStatusType.FAILED);

            }

        }
        else
        {
            // Create Response w/o Results
            response = initializeResponse();
            response.setJobId(this.jobID.toString());
            response.setJobStatus(this.jobStatus);

            // Check for Race Condition (Job completed after isDone() check and
            // before response
            // creation)
            if (response.getJobStatus() == EJobStatusType.COMPLETE)
            {
                response.setJobStatus(EJobStatusType.IN_PROGRESS);
            }
        }

        return response;
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
     * @return the metrics for running the job.
     */
    public final StopWatch getMetrics()
    {
        return metrics;
    }

    /**
     * Sets the executor service to be used for spawning task threads.
     *
     * @param executor the executor service
     */
    public void setExecutor(ICoalesceExecutorService executor)
    {
        this.executor = executor;
    }

    /**
     * @return the job's task status.
     */
    public final Collection<MetricsResultsType> getTaskResults()
    {
        return taskResults;
    }

    // ----------------------------------------------------------------------//
    // Protected Functions
    // ----------------------------------------------------------------------//

    protected ICoalesceExecutorService getExecutor()
    {
        return this.executor;
    }

    protected void addTaskResults(MetricsResultsType results)
    {
        taskResults.add(results);
    }

    // ----------------------------------------------------------------------//
    // Work Functions
    // ----------------------------------------------------------------------//

    @Override
    public final Y call()
    {

        Y result;

        // Set Start Time
        metrics.start();

        // Set Status to In Progress
        jobStatus = EJobStatusType.IN_PROGRESS;

        result = this.doWork();

        // Set Complete Time
        metrics.finish();

        // Set Status to In Progress
        if (result.getJobStatus() == EJobStatusType.FAILED)
        {
            jobStatus = EJobStatusType.FAILED;
        }
        else
        {
            jobStatus = EJobStatusType.COMPLETE;
        }

        // TODO Notify Listeners
        result.setRequestId(this.getClass().getSimpleName());

        return result;
    }

    // ----------------------------------------------------------------------//
    // Abstract Functions
    // ----------------------------------------------------------------------//

    /**
     * Performs the work of the job.
     */
    protected abstract Y doWork();

    /**
     * Initializes the response with default parameters.
     *
     * @return Initialized response.
     */
    protected abstract Y initializeResponse();

}
