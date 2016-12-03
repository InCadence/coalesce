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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.incadencecorp.coalesce.services.api.common.EJobStatusType;

/**
 * Manages adding, removing, and getting job status for submitted jobs.
 *
 * @author Derek C.
 */
public class JobManager {

    // ----------------------------------------------------------------------//
    // Protected Member Variables
    // ----------------------------------------------------------------------//

    private ConcurrentHashMap<UUID, JobBase<?, ?>> jobsh;

    // ----------------------------------------------------------------------//
    // Constructor and Initialization
    // ----------------------------------------------------------------------//

    /**
     * Constructor for Job Manager
     */
    public JobManager()
    {
        // Initialize Data Structures
        jobsh = new ConcurrentHashMap<UUID, JobBase<?, ?>>();
    }

    // ----------------------------------------------------------------------//
    // Public Methods
    // ----------------------------------------------------------------------//

    /**
     * Adds job to processing queue.
     *
     * @param job Job to be processed
     */
    public void addJob(JobBase<?, ?> job)
    {
        // Add to the Jobs HashMap first
        jobsh.put(job.getJobID(), job);
    }

    /**
     * Returns the status of the requested job.
     *
     * @param jobID ID of job
     * @return {@link EJobStatusType} of job.
     */
    public EJobStatusType getJobStatus(UUID jobID)
    {
        if (jobsh.containsKey(jobID))
        {
            // Return Job
            return jobsh.get(jobID).getJobStatus();
        }
        else
        {
            // Job Not Found
            return EJobStatusType.NOT_FOUND;
        }
    }

    /**
     * Removes the requested job from the processing queue.
     *
     * @param jobID ID of job
     * @return {@link JobBase}
     */
    public JobBase<?, ?> removeJob(UUID jobID)
    {
        if (jobsh.containsKey(jobID))
        {
            // Remove and Return Job
            return jobsh.remove(jobID);
        }
        else
        {
            return null;
        }
    }

    /**
     * Removes jobs that have expired and were never claimed by a client.
     * 
     * @param minutes Max number of minutes a job should wait for retrieval
     *            before removing.
     * @return Returns an array of expired jobs.
     */
    public JobBase<?, ?>[] removeOldJobs(long minutes)
    {

        List<JobBase<?, ?>> expiredList = new ArrayList<JobBase<?, ?>>();

        // Determine Max Queue Time
        long minCreateTime = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(minutes);

        // For Each Job
        for (JobBase<?, ?> job : jobsh.values())
        {

            // Expired?
            if (job.getMetrics().getCreated() < minCreateTime)
            {

                // Yes; Complete?
                if (!job.isDone() && job.getFuture() != null)
                {
                    // No; Terminate
                    job.getFuture().cancel(true);
                }

                // Add to Expired List.
                expiredList.add(job);

            }

        }

        // Remove Expired Jobs from Queue
        for (JobBase<?, ?> job : expiredList)
        {
            jobsh.remove(job.getJobID());
        }

        // Return Expired Jobs
        return expiredList.toArray(new JobBase<?, ?>[expiredList.size()]);

    }

    /**
     * Returns the job requested.
     *
     * @param jobID ID of job
     * @return {@link JobBase}
     */
    public JobBase<?, ?> getJob(UUID jobID)
    {
        if (jobsh.containsKey(jobID))
        {
            // Get Job
            return jobsh.get(jobID);
        }
        else
        {
            return null;
        }
    }

}
