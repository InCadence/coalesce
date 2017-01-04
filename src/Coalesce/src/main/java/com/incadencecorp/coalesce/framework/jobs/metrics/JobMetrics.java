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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.incadencecorp.coalesce.framework.jobs.AbstractCoalesceJob;
import com.incadencecorp.coalesce.framework.tasks.MetricResults;

/**
 * Stores the metrics for an individual job run.
 */
public class JobMetrics {

    // ----------------------------------------------------------------------//
    // Private Members
    // ----------------------------------------------------------------------//

    private String name;
    private AbstractCoalesceJob<?, ?> job;

    private MetricResults<?>[] taskMetrics;
    private int totalTasks;
    private int totalTasksFailed;
    private int totalTasksSuccess;

    private RunningAverage taskRunningMetrics;
    private Set<String> entityTypes;

    // ----------------------------------------------------------------------//
    // Constructor
    // ----------------------------------------------------------------------//

    /**
     * Creates a JobMetrics object with the requested name.
     *
     * @param name
     */
    public JobMetrics(final AbstractCoalesceJob<?, ?> pJob, MetricResults<?>... pMetrics)
    {

        job = pJob;
        name = job.getClass().getName();
        taskMetrics = pMetrics;
        taskRunningMetrics = new RunningAverage();
        entityTypes = new HashSet<>();

        calculateStatistics(pMetrics);
    }

    // ----------------------------------------------------------------------//
    // Public Getters
    // ----------------------------------------------------------------------//

    /**
     * @return the jobId
     */
    public AbstractCoalesceJob<?, ?> getJob()
    {
        return job;
    }

    /**
     * @return the totalJobTime
     */
    public long getTotalJobTime()
    {
        return job.getMetrics().getTotalLife();
    }

    /**
     * @return the job start time
     */
    public long getJobStartTime()
    {
        return job.getMetrics().getStarted();
    }

    /**
     * @return the job finish time
     */
    public long getJobFinishTime()
    {
        return job.getMetrics().getCompleted();
    }

    /**
     * @return the totalTasks
     */
    public long getTotalTasks()
    {
        return totalTasks;
    }

    /**
     * @return the totalTasksFailed
     */
    public long getTotalTasksFailed()
    {
        return totalTasksFailed;
    }

    /**
     * @return the totalTasksSuccess
     */
    public long getTotalTasksSuccess()
    {
        return totalTasksSuccess;
    }

    /**
     * @return the taskMetrics
     */
    public RunningAverage getTaskMetricAverages()
    {
        return taskRunningMetrics;
    }

    /**
     * @return the class name of the job type that is being handled by this
     *         class.
     */
    public final String getName()
    {
        return name;
    }

    public MetricResults<?>[] getTaskMetrics()
    {
        return taskMetrics;
    }

    public Set<String> getEntityTypes()
    {
        return entityTypes;
    }

    // ----------------------------------------------------------------------//
    // Protected Functions
    // ----------------------------------------------------------------------//

    private void calculateStatistics(MetricResults<?>... metrics)
    {
        totalTasks = metrics.length;
        totalTasksFailed = 0;
        totalTasksSuccess = 0;

        for (MetricResults<?> metric : metrics)
        {
            if (metric.getResults() != null)
            {
                switch (metric.getResults().getStatus()) {
                case FAILED:
                case FAILED_PENDING:
                    totalTasksFailed++;
                    break;
                case SUCCESS:
                    totalTasksSuccess++;
                }
            }

            taskRunningMetrics.add(metric.getWatch().getWorkLife());

            // TODO Not Implemented
            // entityTypes.addAll(metric.getEntityTypes());
        }
    }

}
