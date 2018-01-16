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

package com.incadencecorp.coalesce.framework;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This implementation creates threads prefixed with Coalesce.
 *
 * @author Derek
 */
public class CoalesceThreadFactoryImpl implements ThreadFactory {

    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger THREAD_NUMBER = new AtomicInteger(1);
    private final String namePrefix;

    /**
     * Default Constructor.
     */
    public CoalesceThreadFactoryImpl()
    {
        this(System.getSecurityManager()
                     != null ? System.getSecurityManager().getThreadGroup() : Thread.currentThread().getThreadGroup());
    }

    /**
     * Constructor allowing the group to be specified.
     *
     * @param group
     */
    public CoalesceThreadFactoryImpl(ThreadGroup group)
    {
        this.group = group;
        this.namePrefix = "coalesce-thread(" + POOL_NUMBER.getAndIncrement() + ":%s)-%s";
    }

    /**
     * @return the pool identifier
     */
    public int getPoolNumber()
    {
        return POOL_NUMBER.get();
    }

    /**
     * @return the total number of threads that have been created for this pool.
     */
    public int getThreadNumber()
    {
        return THREAD_NUMBER.get();
    }

    @Override
    public Thread newThread(Runnable r)
    {
        Thread thread = new Thread(group, r);
        thread.setName(String.format(namePrefix, THREAD_NUMBER.getAndIncrement(), thread.getId()));
        thread.setDaemon(true);
        thread.setPriority(Thread.NORM_PRIORITY);
        return thread;
    }

}
