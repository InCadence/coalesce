/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.framework;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Derek Clemenzi
 */
public class ThreadStatus {

    private long pending;
    private long completed;
    private long queued;
    private long total;

    /**
     * Default Constructor
     */
    public ThreadStatus()
    {

    }

    /**
     * @param executor to derive values from.
     */
    public ThreadStatus(ThreadPoolExecutor executor)
    {
        pending = executor.getActiveCount();
        completed = executor.getCompletedTaskCount();
        queued = executor.getQueue().size();
        total = executor.getTaskCount();
    }

    /**
     * @return the approximate number of threads that are actively
     * executing tasks.
     */
    public long getPending()
    {
        return pending;
    }

    /**
     * @param pending the approximate number of threads that are actively
     *                executing tasks.
     */
    public void setPending(long pending)
    {
        this.pending = pending;
    }

    /**
     * @return the approximate total number of tasks that have
     * completed execution.
     */
    public long getCompleted()
    {
        return completed;
    }

    /**
     * @param completed the approximate total number of tasks that have
     *                  completed execution.
     */
    public void setCompleted(long completed)
    {
        this.completed = completed;
    }

    /**
     * @return the task queued.
     */
    public long getQueued()
    {
        return queued;
    }

    /**
     * @param queued the task queued.
     */
    public void setQueued(long queued)
    {
        this.queued = queued;
    }

    /**
     * @return the approximate total number of tasks that have ever been
     * scheduled for execution.
     */
    public long getTotal()
    {
        return total;
    }

    /**
     * @param total the approximate total number of tasks that have ever been
     *              scheduled for execution.
     */
    public void setTotal(long total)
    {
        this.total = total;
    }
}

