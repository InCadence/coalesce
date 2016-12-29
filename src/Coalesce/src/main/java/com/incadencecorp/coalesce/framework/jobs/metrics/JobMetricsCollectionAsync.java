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

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.incadencecorp.coalesce.framework.CoalesceThreadFactoryImpl;
import com.incadencecorp.coalesce.framework.jobs.AbstractCoalesceJob;

/**
 * Handles the processing of metrics collected. Every {@link #initialize X
 * minutes} the metrics are persisted and reset. Dispose must be called to
 * release resources.
 *
 * @author Derek C.
 */
public class JobMetricsCollectionAsync implements AutoCloseable {

    // ----------------------------------------------------------------------//
    // Private Members
    // ----------------------------------------------------------------------//

    private final ConcurrentLinkedQueue<JobNode> jobQueue = new ConcurrentLinkedQueue<JobNode>();
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> future;
    private TimeUnit units = TimeUnit.MINUTES;
    private String name;

    // ----------------------------------------------------------------------//
    // Constructor / Initialization
    // ----------------------------------------------------------------------//

    /**
     * Constructor
     */
    public JobMetricsCollectionAsync(String name)
    {
        this.name = name;
    }

    /**
     * Initializes a thread to take snap shots of the metrics collected at a set
     * interval and persist them within the database.
     *
     * @param interval in minutes.
     * @param userId
     */
    public void initialize(int interval)
    {
        scheduler = Executors.newScheduledThreadPool(1, new CoalesceThreadFactoryImpl());
        future = scheduler.scheduleAtFixedRate(new PollingWorker(), interval, interval, units);
    }

    // ----------------------------------------------------------------------//
    // Public Functions
    // ----------------------------------------------------------------------//

    /**
     * Adds the job to the queue for future processing so not to block the main
     * thread.
     *
     * @param job
     */
    public void addJob(AbstractCoalesceJob<?, ?> job)
    {
        jobQueue.add(new JobNode(job));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    @Override
    public void close()
    {
        future.cancel(false);
        scheduler.shutdownNow();
    }

    // ----------------------------------------------------------------------//
    // Private Classes
    // ----------------------------------------------------------------------//

    private static final class JobNode {

        // ----------------------------------------------------------------------//
        // Private Members
        // ----------------------------------------------------------------------//

        private AbstractCoalesceJob<?, ?> job;

        // ----------------------------------------------------------------------//
        // Constructor
        // ----------------------------------------------------------------------//

        public JobNode(AbstractCoalesceJob<?, ?> job)
        {
            this.job = job;
        }

    }

    private class PollingWorker implements Runnable {

        // ----------------------------------------------------------------------//
        // Private Members
        // ----------------------------------------------------------------------//

        private JobMetricsCollection metrics = new JobMetricsCollection();

        // ----------------------------------------------------------------------//
        // Public Functions
        // ----------------------------------------------------------------------//

        @Override
        public void run()
        {
            // Pop Node
            JobNode node = jobQueue.poll();

            // Node in Queue?
            while (node != null)
            {

                // Yes; Process
                metrics.addJob(node.job);

                // Check for additional nodes.
                node = jobQueue.poll();
            }

            // TODO Log Metrics

            // Reset metrics
            metrics = new JobMetricsCollection();

        }
    }
}
