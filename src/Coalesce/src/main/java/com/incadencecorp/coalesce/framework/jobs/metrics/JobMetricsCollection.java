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
import java.util.HashMap;

import com.incadencecorp.coalesce.framework.jobs.AbstractCoalesceJob;

/**
 * Stores the {@link JobMetricsType metrics} for multiple job types and combined their totals into a
 * summary.
 *
 * @author Derek C.
 */
public class JobMetricsCollection {

    // ----------------------------------------------------------------------//
    // Private Members
    // ----------------------------------------------------------------------//

    private HashMap<String, JobMetricsType> jobMetrics = new HashMap<String, JobMetricsType>();
    private long started;

    // ----------------------------------------------------------------------//
    // Constructor
    // ----------------------------------------------------------------------//

    /**
     * Constructor
     */
    public JobMetricsCollection() {
        started = System.currentTimeMillis();
    }

    // ----------------------------------------------------------------------//
    // Public Functions
    // ----------------------------------------------------------------------//

    /**
     * Adds the metrics of a given job and response to the running totals.
     *
     * @param job
     *            the job
     */
    public void addJob(AbstractCoalesceJob<?, ?, ?> job) {

        JobMetricsType metric = getMetrics(job.getClass());
        metric.addJobMetrics(job);

    }

    /**
     * Clears all metrics.
     */
    public void reset() {
        started = System.currentTimeMillis();
        jobMetrics.clear();
    }

    // ----------------------------------------------------------------------//
    // Public Getters
    // ----------------------------------------------------------------------//

    /**
     * @return the total number of jobs that have been processed.
     */
    public long getTotalJobCount() {

        long total = 0;

        for (JobMetricsType metric: jobMetrics.values()) {
            total += metric.getTotalCount();
        }

        return total;
    }

    /**
     * @return the total number of asynchronous jobs that have been processed.
     */
    public long getTotalAsyncJobCount() {

        long total = 0;

        for (JobMetricsType metric: jobMetrics.values()) {
            total += metric.getAsyncCount();
        }

        return total;
    }

    /**
     * @return the {@link RunningAverage running averages} of the time jobs spent sitting in the
     *         queue pending.
     */
    public RunningAverage getAveragePendingTime() {

        RunningAverage total = new RunningAverage();

        for (JobMetricsType metric: jobMetrics.values()) {
            total.add(metric.getAveragePendingTime());
        }

        return total;
    }

    /**
     * @return the {@link RunningAverage running averages} of the time jobs spent doing work.
     */
    public RunningAverage getAverageWorkTime() {

        RunningAverage total = new RunningAverage();

        for (JobMetricsType metric: jobMetrics.values()) {
            total.add(metric.getAverageWorkTime());
        }

        return total;
    }

    /**
     * @return the {@link RunningAverage running averages} of the total time of jobs spent in the
     *         queue.
     */
    public RunningAverage getAverageTotalTime() {

        RunningAverage total = new RunningAverage();

        for (JobMetricsType metric: jobMetrics.values()) {
            total.add(metric.getAverageTotalTime());
        }

        return total;
    }

    /**
     * @return the {@link RunningAverage running averages} of the request pay loads.
     */
    public RunningAverage getAverageRequestPayload() {

        RunningAverage total = new RunningAverage();

        for (JobMetricsType metric: jobMetrics.values()) {
            total.add(metric.getAverageRequestPayload());
        }

        return total;
    }

    /**
     * @return the {@link RunningAverage running averages} of the response pay loads.
     */
    public RunningAverage getAverageResponsePayload() {

        RunningAverage total = new RunningAverage();

        for (JobMetricsType metric: jobMetrics.values()) {
            total.add(metric.getAverageResponsePayload());
        }

        return total;
    }

    /**
     * @return the last time the metrics were reset.
     */
    public long getStartTime() {
        return started;
    }

    /**
     * @param type
     *            the class type you wish to retrieve the metrics for.
     * @return the {@link JobMetricsType metrics} of a given class type.
     */
    public JobMetricsType getMetrics(Class<?> type) {

        JobMetricsType metric;

        // Metrics for the job type exists?
        if (jobMetrics.containsKey(type.getName())) {
            // Yes; Get existing metric.
            metric = jobMetrics.get(type.getName());
        } else {
            // No; Create and add to list.
            metric = new JobMetricsType(type.getName());
            jobMetrics.put(type.getName(), metric);
        }

        return metric;
    }

    /**
     * @return a collections of all the metrics collected.
     */
    public Collection<JobMetricsType> getMetricValues() {
        return jobMetrics.values();
    }
}
