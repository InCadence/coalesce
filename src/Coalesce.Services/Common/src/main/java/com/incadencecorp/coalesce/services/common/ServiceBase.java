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

import com.incadencecorp.coalesce.api.EJobStatus;
import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.CoalesceExecutorServiceImpl;
import com.incadencecorp.coalesce.framework.jobs.JobManager;
import com.incadencecorp.coalesce.services.api.common.*;
import com.incadencecorp.coalesce.services.common.jobs.AbstractServiceJob;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

/**
 * Base class for handling the queuing of jobs asynchronously, returning the
 * results, and the collection of metrics. Must call dispose to free up
 * resources.
 *
 * @author Derek C.
 */
public abstract class ServiceBase<T> extends CoalesceExecutorServiceImpl {

    // ----------------------------------------------------------------------//
    // Protected Member Variables
    // ----------------------------------------------------------------------//

    // TODO Make these configurable
    private static final int TIME_TO_LIVE = 10;

    private JobManager<AbstractServiceJob<?, ?, ?, T>> jobs;
    private T target;

    // ----------------------------------------------------------------------//
    // Constructor and Initialization
    // ----------------------------------------------------------------------//

    /**
     * Construct service while specifying the service and framework to use.
     *
     * @param target
     * @param service
     */
    public ServiceBase(T target, ExecutorService service)
    {
        super(service);

        this.jobs = new JobManager<>();
        this.target = target;
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
        return jobs.getJobStatus(id);
    }

    /**
     * @param request the request
     * @return the response of the job indicated by the request if completed.
     * Otherwise it returns the current status of job not found.
     */
    public final MultipleResponse pickupJobResults(JobRequest request)
    {

        MultipleResponse response = new MultipleResponse();

        for (String key : request.getJobIdList())
        {

            ResponseResultsType result = new ResponseResultsType();
            AbstractServiceJob<?, ?, ?, T> job;

            // Check the Job Manager for this Job's Status
            EJobStatus jobStatus = getJobStatus(key);

            switch (jobStatus)
            {

            case CANCELED:
            case COMPLETE:
            case FAILED:
                // Job is complete in some way; return the results
                job = jobs.removeJob(key);

                result.setResult((BaseResponse) job.getResponse());
                result.setStatus(EResultStatus.SUCCESS);
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
                StringResponse failedResponse = new StringResponse();
                failedResponse.setId(key);
                failedResponse.setStatus(EResultStatus.FAILED);
                failedResponse.setError("Job Not Found");

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
     * @return {@link BaseResponse#getStatus()} which states if cancel was
     * successful.
     */
    public final StringResponse cancelJob(JobRequest request)
    {

        StringResponse response = new StringResponse();

        for (String jobId : request.getJobIdList())
        {
            ResultsType result = new ResultsType();

            result.setResult(jobId);

            AbstractServiceJob<?, ?, ?, T> job = jobs.removeJob(jobId);

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
     * is false; Otherwise a response with no results and a
     * {@link AbstractServiceJob#getJobId()} that can be used to
     * request the results once the job has completed.
     */
    public final <REQUEST extends BaseRequest, RESPONSE extends ICoalesceResponseType<List<X>>, X extends ICoalesceResponseType<?>> RESPONSE performJob(
            AbstractServiceJob<REQUEST, RESPONSE, X, T> job)
    {
        RESPONSE response;

        job.setExecutor(this);
        job.setTarget(getTarget());
        // TODO job.setPrincipal(principal);

        // Async?
        if (job.isAsync())
        {
            // Yes; Get Response
            response = job.getResponse();

            // Add to Job Manager
            jobs.addJob(job);

            // Thread Pool Shutdown?
            if (!isShutdown())
            {
                // No; Add Job
                try
                {
                    job.setFuture(submit(job));
                }
                catch (CoalescePersistorException e)
                {
                    response.setStatus(EResultStatus.FAILED);
                    response.setError(e.getMessage());
                }
            }
            else
            {
                response.setStatus(EResultStatus.FAILED);
                response.setError("Thread pool has been shutdown");
            }
        }
        else
        {
            response = job.call();
            response.setId(job.getJobId().toString());
            switch (job.getJobStatus())
            {
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
    public final List<AbstractServiceJob<?, ?, ?, T>> removeOldJobs(long minutes)
    {
        return jobs.removeOldJobs(minutes);
    }

    /*--------------------------------------------------------------------------
    Protected Methods
    --------------------------------------------------------------------------*/

    /**
     * @return the framework that this service was initialized with.
     */
    protected final T getTarget()
    {
        return target;
    }

}
