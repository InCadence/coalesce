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

package com.incadencecorp.coalesce.framework.jobs.metrics;

import java.util.ArrayList;
import java.util.List;

import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.framework.jobs.AbstractCoalesceJob;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceResponseType;
import com.incadencecorp.coalesce.framework.tasks.MetricResults;

/**
 * Stores the {@link RunningAverage running averages} and totals for a given job
 * type.
 *
 * @author Derek C.
 */
public class JobMetricsType {

    // ----------------------------------------------------------------------//
    // Private Members
    // ----------------------------------------------------------------------//

    private String name;

    private long total;
    private long totalFailed;
    private long totalComplete;
    private long totalCanceled;
    private long totalAsync;

    private RunningAverage responsePayloadMetrics;
    private RunningAverage requestPayloadMetrics;
    private RunningAverage workTimeMetrics;
    private RunningAverage pendingTimeMetrics;
    private RunningAverage totalTimeMetrics;

    private long totalWorkerThreads;
    private long totalWorkerThreadsFailed;
    private long totalWorkerThreadsSuccess;

    private List<JobMetrics> jobMetrics;

    // ----------------------------------------------------------------------//
    // Constructor
    // ----------------------------------------------------------------------//

    /**
     * Creates a JobMetrics object with the requested name.
     *
     * @param name the object name
     */
    public JobMetricsType(String name)
    {

        this.name = name;

        total = 0;
        totalFailed = 0;
        totalComplete = 0;
        totalCanceled = 0;
        totalAsync = 0;

        responsePayloadMetrics = new RunningAverage();
        requestPayloadMetrics = new RunningAverage();
        workTimeMetrics = new RunningAverage();
        pendingTimeMetrics = new RunningAverage();
        totalTimeMetrics = new RunningAverage();

        totalWorkerThreads = 0;
        totalWorkerThreadsFailed = 0;
        totalWorkerThreadsSuccess = 0;

        jobMetrics = new ArrayList<>();

    }

    // ----------------------------------------------------------------------//
    // Public Getters
    // ----------------------------------------------------------------------//

    /**
     * @return the total number of jobs that have been processed for the job
     *         type.
     */
    public final long getTotalCount()
    {
        return total;
    }

    /**
     * @return the total number of jobs that failed.
     */
    public final long getFailedCount()
    {
        return totalFailed;
    }

    /**
     * @return the total number of jobs that completed.
     */
    public final long getCompletedCount()
    {
        return totalComplete;
    }

    /**
     * @return the total number of jobs that were canceled.
     */
    public final long getCanceledCount()
    {
        return totalCanceled;
    }

    /**
     * @return the total number of jobs performed asynchronously.
     */
    public final long getAsyncCount()
    {
        return totalAsync;
    }

    /**
     * @return the class name of the job type that is being handled by this
     *         class.
     */
    public final String getName()
    {
        return name;
    }

    /**
     * @return the response's pay load running averages of the jobs
     */
    public final RunningAverage getAverageResponsePayload()
    {
        return responsePayloadMetrics;
    }

    /**
     * @return the request's pay load running averages of the jobs.
     */
    public final RunningAverage getAverageRequestPayload()
    {
        return requestPayloadMetrics;
    }

    /**
     * @return the work time running averages of the jobs.
     */
    public final RunningAverage getAverageWorkTime()
    {
        return workTimeMetrics;
    }

    /**
     * @return the pending time running averages of the jobs.
     */
    public final RunningAverage getAveragePendingTime()
    {
        return pendingTimeMetrics;
    }

    /**
     * @return the total processing time running averages of the jobs.
     */
    public final RunningAverage getAverageTotalTime()
    {
        return totalTimeMetrics;
    }

    /**
     * @return the total number of worker threads that have been processed for
     *         the job type.
     */
    public final long getTotalWorkerThreadCount()
    {
        return totalWorkerThreads;
    }

    /**
     * @return the total number of worker threads that failed.
     */
    public final long getFailedWorkerThreadCount()
    {
        return totalWorkerThreadsFailed;
    }

    /**
     * @return the total number of worker threads that succeeded.
     */
    public final long getSuccessWorkerThreadCount()
    {
        return totalWorkerThreadsSuccess;
    }

    /**
     * @return the jobMetrics
     */
    public List<JobMetrics> getIndividualJobMetrics()
    {
        return jobMetrics;
    }

    // ----------------------------------------------------------------------//
    // Protected Functions
    // ----------------------------------------------------------------------//

    /**
     * Adds the metrics of a given job to the running totals.
     *
     * @param job
     */
    protected final void addJobMetrics(final AbstractCoalesceJob<?, ?, ?> job)
    {

        // Valid Job Type?
        if (name.compareToIgnoreCase(job.getClass().getName()) != 0)
        {
            // Invalid Job Type
            return;
        }

        // Increment running totals of status
        switch (job.getJobStatus()) {
        case FAILED:
            totalFailed += 1;
            break;
        case COMPLETE:
            totalComplete += 1;
            break;
        case CANCELED:
            totalCanceled += 1;
            break;
        default:
            // Do Nothing
            break;
        }

        JobMetrics indJobMetrics;

        MetricResults<?>[] metrics = job.getTaskMetrics();
        if (metrics != null)
        {
            // Batch job - count tasks
            indJobMetrics = new JobMetrics(job, metrics);

            totalWorkerThreads += metrics.length;

            for (MetricResults<?> results : metrics)
            {
                if (results.isSuccessful())
                {
                    totalWorkerThreadsSuccess++;
                }
                else
                {
                    totalWorkerThreadsFailed++;
                }
            }
        }
        else
        {
            // Not a batch job - single worker thread
            MetricResults<CoalesceResponseType<Boolean>> metric = new MetricResults<CoalesceResponseType<Boolean>>(); 
            
            totalWorkerThreads += 1;

            CoalesceResponseType<Boolean> result = new CoalesceResponseType<Boolean>();

            switch (job.getJobStatus()) {
            case COMPLETE:
                result.setStatus(EResultStatus.SUCCESS);
                totalWorkerThreadsSuccess += 1;
                break;
            case FAILED:
            case CANCELED:
                result.setStatus(EResultStatus.FAILED);
                totalWorkerThreadsFailed += 1;
                break;
            default:
                result.setStatus(EResultStatus.FAILED_PENDING);
                break;
            }

            metric.setWatch(job.getMetrics());
            metric.setResults(result);

            indJobMetrics = new JobMetrics(job, metric);

        }

        jobMetrics.add(indJobMetrics);

        total += 1;

        if (job.isAsync())
        {
            totalAsync += 1;
        }

        // Increment Metrics
        // TODO Not implemented
        // requestPayloadMetrics.add(job.getRequest().getPayloadSize());
        // responsePayloadMetrics.add(response.getPayloadSize());
        pendingTimeMetrics.add(job.getMetrics().getPendingLife());
        workTimeMetrics.add(job.getMetrics().getWorkLife());
        totalTimeMetrics.add(job.getMetrics().getTotalLife());
    }

}
