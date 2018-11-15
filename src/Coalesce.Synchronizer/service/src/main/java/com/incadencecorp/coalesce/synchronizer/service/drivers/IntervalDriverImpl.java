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

package com.incadencecorp.coalesce.synchronizer.service.drivers;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.CoalesceThreadFactoryImpl;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractDriver;
import com.incadencecorp.coalesce.synchronizer.api.common.SynchronizerParameters;

/**
 * This implementation executes a scan on a configurable interval and performs
 * operations in sequential order on the results of the scan.
 * 
 * @author n78554
 * @see SynchronizerParameters#PARAM_DRIVER_DELAY
 * @see SynchronizerParameters#PARAM_DRIVER_INTERVAL
 * @see SynchronizerParameters#PARAM_DRIVER_INTERVAL_UNITS
 * @see SynchronizerParameters#PARAM_DRIVER_MAX_THREADS
 */
public class IntervalDriverImpl extends AbstractDriver {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntervalDriverImpl.class);

    private ScheduledFuture<?> future;
    private ScheduledExecutorService scheduler;

    private int delay = 0;
    private int interval = 0;
    private int threads = 20;
    private TimeUnit units = TimeUnit.SECONDS;

    @Override
    public void start()
    {
        LOGGER.info("Starting {} with {} threads.", getName(), threads);

        scheduler = Executors.newScheduledThreadPool(threads, new CoalesceThreadFactoryImpl());

        // Interval Configured?
        if (interval > 0)
        {
            future = scheduler.scheduleAtFixedRate(this, delay, interval, units);
        }
        else
        {
            // No; Run as Single Task
            future = scheduler.schedule(this, delay, units);

            // Debug?
            if (LOGGER.isDebugEnabled())
            {
                // Yes; Run Once and Block Thread
                LOGGER.warn("Driver is in Synchronise mode because logger is set to debug (or lower) and the interval was not set");
                try
                {
                    future.get();
                }
                catch (InterruptedException | ExecutionException e)
                {
                    LOGGER.error("Starting Driver: " + getName(), e);
                }
            }
        }
    }

    @Override
    public void stop()
    {
        LOGGER.info("Stopping {}.", getName(), threads);

        // Cancel Future
        future.cancel(true);

        try
        {
            // Graceful shutdown (Allow 1 minutes for jobs to finish)
            scheduler.shutdown();
            scheduler.awaitTermination(1, TimeUnit.MINUTES);
        }
        catch (InterruptedException e)
        {
            // Force shutdown
            List<Runnable> jobList = scheduler.shutdownNow();

            // Iterate through each failed job
            for (Runnable job : jobList)
            {
                LOGGER.warn("Job expired");
            }

        }

        LOGGER.info("Stopped {}.", getName(), threads);
    }

    @Override
    public void setProperties(Map<String, String> params)
    {
        super.setProperties(params);

        // Delay Configured?
        if (parameters.containsKey(SynchronizerParameters.PARAM_DRIVER_DELAY))
        {
            delay = Integer.parseInt(parameters.get(SynchronizerParameters.PARAM_DRIVER_DELAY));

            if (delay < 1)
            {
                throw new IllegalArgumentException("Invalid Delay: " + delay);
            }
        }

        // Interval Configured?
        if (parameters.containsKey(SynchronizerParameters.PARAM_DRIVER_INTERVAL))
        {
            interval = Integer.parseInt(parameters.get(SynchronizerParameters.PARAM_DRIVER_INTERVAL));

            if (interval < 1)
            {
                throw new IllegalArgumentException("Invalid Interval: " + interval);
            }
        }

        // Number of Threads Configured?
        if (parameters.containsKey(SynchronizerParameters.PARAM_DRIVER_MAX_THREADS))
        {
            threads = Integer.parseInt(parameters.get(SynchronizerParameters.PARAM_DRIVER_MAX_THREADS));

            if (threads < 1)
            {
                throw new IllegalArgumentException("Invalid Number of Threads: " + threads);
            }
        }

        // Interval Units Configured?
        if (parameters.containsKey(SynchronizerParameters.PARAM_DRIVER_INTERVAL_UNITS))
        {
            units = TimeUnit.valueOf(parameters.get(SynchronizerParameters.PARAM_DRIVER_INTERVAL_UNITS));
        }
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException
    {
        return scheduler.invokeAll(tasks);
    }
    
    @Override
    public <T> Future<T> submit(Callable<T> task) throws CoalescePersistorException
    {
        return scheduler.submit(task);
    }
    
    @Override
    public final void execute(Runnable command)
    {
        scheduler.execute(command);
    }

    @Override
    public final boolean isShutdown()
    {
        return scheduler.isShutdown();
    }

    @Override
    public final boolean isTerminated()
    {
        return scheduler.isTerminated();
    }

    @Override
    public final <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException
    {
        return scheduler.invokeAll(tasks, timeout, unit);
    }

    @Override
    public final <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException
    {
        return scheduler.invokeAny(tasks);
    }

    @Override
    public final <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException
    {
        return scheduler.invokeAny(tasks, timeout, unit);
    }

}
