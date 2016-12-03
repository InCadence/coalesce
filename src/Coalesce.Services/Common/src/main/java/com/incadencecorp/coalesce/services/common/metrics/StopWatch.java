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

package com.incadencecorp.coalesce.services.common.metrics;

import java.util.concurrent.TimeUnit;

/**
 * Stores when the task was created, started, and completed.
 *
 * @author Derek C.
 */
public class StopWatch {

    // ----------------------------------------------------------------------//
    // Private Members
    // ----------------------------------------------------------------------//

    private long created;
    private long started;
    private long finished;

    // ----------------------------------------------------------------------//
    // Constructor
    // ----------------------------------------------------------------------//

    /**
     * Default constructor
     */
    public StopWatch() {
        created = System.currentTimeMillis();
    }

    /**
     * Exposed for Unit Test. Sets the created to to X minutes in the past from current system time.
     *
     * @param minutes
     *            minutes into the past
     */
    public StopWatch(long minutes) {
        created = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(minutes);
    }

    // ----------------------------------------------------------------------//
    // Public Getters / Setters
    // ----------------------------------------------------------------------//

    /**
     * @return the created
     */
    public final long getCreated() {
        return created;
    }

    /**
     * @return the started
     */
    public final long getStarted() {
        return started;
    }

    /**
     * @return the completed
     */
    public final long getCompleted() {
        return finished;
    }

    // ----------------------------------------------------------------------//
    // Public Functions
    // ----------------------------------------------------------------------//

    /**
     * Records the start time.
     */
    public final void start() {
        started = System.currentTimeMillis();
    }

    /**
     * Records the finish time.
     */
    public final void finish() {
        finished = System.currentTimeMillis();
    }

    /**
     * @return the total time the job was sitting in the queue before starting.
     */
    public final long getPendingLife() {
        return started - created;
    }

    /**
     * @return the total time the job took to perform its task.
     */
    public final long getWorkLife() {
        return finished - started;
    }

    /**
     * @return the total time from the job creation to it's completion.
     */
    public final long getTotalLife() {
        return finished - created;
    }

}
