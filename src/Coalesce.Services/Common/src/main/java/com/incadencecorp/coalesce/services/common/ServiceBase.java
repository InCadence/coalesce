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

package com.incadencecorp.coalesce.services.common;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.EJobStatus;
import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.api.ICoalesceComponent;
import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.api.persistance.ICoalesceExecutorService;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.CoalesceThreadFactoryImpl;
import com.incadencecorp.coalesce.framework.jobs.JobManager;
import com.incadencecorp.coalesce.framework.jobs.metrics.JobMetricsCollectionAsync;
import com.incadencecorp.coalesce.services.api.common.BaseRequest;
import com.incadencecorp.coalesce.services.api.common.BaseResponse;
import com.incadencecorp.coalesce.services.api.common.JobRequest;
import com.incadencecorp.coalesce.services.api.common.MultipleResponse;
import com.incadencecorp.coalesce.services.api.common.ResponseResultsType;
import com.incadencecorp.coalesce.services.api.common.ResultsType;
import com.incadencecorp.coalesce.services.api.common.StatusResponse;
import com.incadencecorp.coalesce.services.api.common.StatusType;
import com.incadencecorp.coalesce.services.api.common.StringResponse;
import com.incadencecorp.coalesce.services.common.jobs.AbstractServiceJob;

/**
 * Base class for handling the queuing of jobs asynchronously, returning the
 * results, and the collection of metrics. Must call dispose to free up
 * resources.
 * 
 * @author Derek C.
 */
public class ServiceBase implements ICoalesceExecutorService, AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceBase.class);

    // ----------------------------------------------------------------------//
    // Protected Member Variables
    // ----------------------------------------------------------------------//

    // TODO Make these configurable
    private static final int MIN_THREADS = 2;
    private static final int MAX_THREADS = 5;
    private static final int TIME_TO_LIVE = 10;

    private JobManager<AbstractServiceJob<?, ?, ?>> jobs;
    private ExecutorService service;
    private JobMetricsCollectionAsync metrics;
    private CoalesceFramework framework;

    // ----------------------------------------------------------------------//
    // Constructor and Initialization
    // ----------------------------------------------------------------------//

    /**
     * Default Constructor
     */
    public ServiceBase()
    {
        this(new ThreadPoolExecutor(MIN_THREADS,
                                    MAX_THREADS,
                                    60,
                                    TimeUnit.SECONDS,
                                    new SynchronousQueue<Runnable>(),
                                    new CoalesceThreadFactoryImpl(),
                                    new ThreadPoolExecutor.CallerRunsPolicy()),
             new CoalesceFramework());
    }

    /**
     * Construct service while specifying the service and framework to use.
     * 
     * @param pool
     * @param framework
     */
    public ServiceBase(ExecutorService pool, CoalesceFramework framework)
    {
        this.service = pool;
        this.jobs = new JobManager<AbstractServiceJob<?, ?, ?>>();
        this.framework = framework;
    }

    /**
     * Enables / disables the collection of metrics.
     *
     * @param enabled If <code>true</code> enables the collection of metrics. If
     *            already been enabled nothing happens. If <code>false</code> is
     *            disables the collection of metrics and any metrics collected
     *            up to this point are written to the database.
     * @param interval specifies the frequency (in minutes) in which metrics
     *            should be persisted.
     */
    public final void enableMetricCollection(boolean enabled, int interval)
    {
        if (enabled)
        {
            if (metrics == null)
            {
                metrics = new JobMetricsCollectionAsync(this.getClass().getName());
                metrics.initialize(interval);
            }
        }
        else
        {
            if (metrics != null)
            {
                metrics.close();
                metrics = null;
            }
        }
    }

    // ----------------------------------------------------------------------//
    // Public Methods
    // ----------------------------------------------------------------------//

    /**
     * @param request containing the ID of the job being inquired about.
     * @return the response containing the job's current status.
     */
    public final StatusResponse getJobStatus(JobRequest request)
    {

        StatusResponse response = new StatusResponse();

        for (String jobId : request.getJobIdList())
        {

            StatusType result = new StatusType();

            result.setId(jobId);
            result.setResult(getJobStatus(jobId));

            response.getResult().add(result);

        }

        response.setId(UUID.randomUUID().toString());
        response.setStatus(EResultStatus.SUCCESS);

        return response;
    }

    /**
     * @param id of the job being inquired about.
     * @return the response containing the job's current status.
     */
    private EJobStatus getJobStatus(String id)
    {
        return getJobStatus(id);
    }

    /**
     * @param request the request
     * @return the response of the job indicated by the request if completed.
     *         Otherwise it returns the current status of job not found.
     */
    public final MultipleResponse pickupJobResults(JobRequest request)
    {

        MultipleResponse response = new MultipleResponse();

        for (String key : request.getJobIdList())
        {

            ResponseResultsType result = new ResponseResultsType();
            AbstractServiceJob<?, ?, ?> job;

            // Check the Job Manager for this Job's Status
            EJobStatus jobStatus = getJobStatus(key);

            switch (jobStatus) {

            case CANCELED:
            case COMPLETE:
            case FAILED:
                // Job is complete in some way; return the results
                job = jobs.removeJob(key);

                result.setResult((BaseResponse) job.getResponse());
                result.setStatus(EResultStatus.SUCCESS);

                if (metrics != null)
                {
                    // Add Metrics
                    metrics.addJob(job);
                }

                break;

            case NEW:
            case PENDING:
            case IN_PROGRESS:
                // Job is not complete; return job status response
                job = jobs.getJob(key);

                result.setResult((BaseResponse) job.getResponse());
                result.setStatus(EResultStatus.SUCCESS);

                break;

            case NOT_FOUND:
            default:
                // Unexpected state; return JOBNOTFOUND
                BaseResponse failedResponse = new BaseResponse();
                failedResponse.setId(key);
                failedResponse.setStatus(EResultStatus.FAILED);

                result.setResult(failedResponse);
                result.setStatus(EResultStatus.SUCCESS);

                break;
            }

            response.getResult().add(result);

        }

        response.setId(UUID.randomUUID().toString());
        response.setStatus(EResultStatus.SUCCESS);

        return response;
    }

    /**
     * Cancels a submitted job. Canceling a job will remove it from the queue,
     * therefore status for this job will no longer be provided.
     *
     * @param request the request
     * @return {@link BaseResponse#getJobStatus()} which states if cancel was
     *         successful.
     */
    public final StringResponse cancelJob(JobRequest request)
    {

        StringResponse response = new StringResponse();

        for (String jobId : request.getJobIdList())
        {
            ResultsType result = new ResultsType();

            result.setResult(jobId);

            AbstractServiceJob<?, ?, ?> job = jobs.removeJob(jobId);

            // Job Queued?
            if (job != null)
            {
                // Yes; Cancel the job
                if (!job.getFuture().cancel(true))
                {
                    result.setStatus(EResultStatus.FAILED);
                }
                else
                {
                    result.setStatus(EResultStatus.SUCCESS);
                }
            }
            else
            {
                // No; Return status not found
                result.setStatus(EResultStatus.FAILED);
            }

            response.getResult().add(result);

        }

        response.setId(UUID.randomUUID().toString());
        response.setStatus(EResultStatus.SUCCESS);

        return response;
    }

    /**
     * Runs a job. If {@link AbstractServiceJob#isAsync} is true then its queued
     * within the thread pool and the requester should use
     * {@link #pickupJobResults} to pick up the response.
     *
     * @param job {@link AbstractServiceJob}
     * @return the response with results if {@link AbstractServiceJob#isAsync}
     *         is false; Otherwise a response with no results and a
     *         {@link AbstractServiceJob#getJobID() Job ID} that can be used to
     *         request the results once the job has completed.
     * @throws Exception
     */
    public final <T extends BaseRequest, Y extends ICoalesceResponseType<List<X>>, X extends ICoalesceResponseType<?>> Y performJob(AbstractServiceJob<T, Y, X> job)
    {
        Y response = null;

        job.setExecutor(this);

        // Async?
        if (job.isAsync())
        {
            // Yes; Get Response
            response = job.getResponse();

            // Add to Job Manager
            jobs.addJob(job);

            // Thread Pool Shutdown?
            if (!service.isShutdown())
            {
                // No; Add Job
                job.setFuture(service.submit(job));
            }
            else
            {
                response.setStatus(EResultStatus.FAILED);
            }
        }
        else
        {
            response = job.call();
            response.setId(job.getJobId().toString());
            switch (job.getJobStatus()) {
            case CANCELED:
            case FAILED:
            case NOT_FOUND:
                response.setStatus(EResultStatus.FAILED);
                break;
            case IN_PROGRESS:
            case NEW:
            case PENDING:
                response.setStatus(EResultStatus.FAILED_PENDING);
                break;
            case COMPLETE:
                response.setStatus(EResultStatus.SUCCESS);
                break;
            }

            if (metrics != null)
            {
                // Add Metrics
                metrics.addJob(job);
            }
        }

        // Remove Expired Jobs
        jobs.removeOldJobs(TIME_TO_LIVE);

        // Return Response Clone
        return response;
    }

    /**
     * Removes jobs older then X minutes from the queue. If not complete they
     * are terminated.
     *
     * @param minutes age of jobs to remove
     * @return Returns a list of expired jobs.
     */
    public final List<AbstractServiceJob<?, ?, ?>> removeOldJobs(long minutes)
    {
        return jobs.removeOldJobs(minutes);
    }

    @Override
    public final void close()
    {
        try
        {
            service.shutdown();
            service.awaitTermination(1, TimeUnit.MINUTES);
        }
        catch (InterruptedException e)
        {
            // Force shutdown
            List<Runnable> jobList = service.shutdownNow();

            // Iterate through each failed job
            for (Runnable runnable : jobList)
            {
                if (runnable instanceof AbstractServiceJob<?, ?, ?>)
                {
                    LOGGER.warn("Job ({}) expired", ((AbstractServiceJob<?, ?, ?>) runnable).getJobId());
                }
                else if (runnable instanceof ICoalesceComponent)
                {
                    LOGGER.warn("Runnable Expired ({})", ((ICoalesceComponent) runnable).getName());
                }
            }
        }

        if (metrics != null)
        {
            metrics.close();
        }
    }

    /*--------------------------------------------------------------------------
    ICoalesceExecutorService Implementation
    --------------------------------------------------------------------------*/

    @Override
    public final void execute(Runnable command)
    {
        service.execute(command);
    }

    @Override
    public final boolean isShutdown()
    {
        return service.isShutdown();
    }

    @Override
    public final boolean isTerminated()
    {
        return service.isTerminated();
    }

    // @Override
    public <T extends BaseRequest, Y extends ICoalesceResponseType<List<X>>, X extends ICoalesceResponseType<?>> Future<Y> submit(AbstractServiceJob<T, Y, X> job)
            throws CoalescePersistorException
    {
        return service.submit(job);
    }

    public final <T> Future<T> submit(Callable<T> job)
    {
        return service.submit(job);
    }

    @Override
    public final <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException
    {
        return service.invokeAll(tasks);
    }

    @Override
    public final <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException
    {
        return service.invokeAll(tasks, timeout, unit);
    }

    @Override
    public final <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException
    {
        return service.invokeAny(tasks);
    }

    @Override
    public final <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException
    {
        return service.invokeAny(tasks, timeout, unit);
    }

    /*--------------------------------------------------------------------------
    Protected Methods
    --------------------------------------------------------------------------*/

    /**
     * @return the framework that this service was initialized with.
     */
    protected final CoalesceFramework getFramework()
    {
        return framework;
    }

}
