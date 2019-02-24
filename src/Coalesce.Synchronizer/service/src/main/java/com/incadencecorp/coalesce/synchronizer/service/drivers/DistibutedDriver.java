/*-----------------------------------------------------------------------------'
 Copyright 2019 - InCadence Strategic Solutions Inc., All Rights Reserved

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

import com.incadencecorp.coalesce.api.subscriber.ICoalesceSubscriber;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.CoalesceThreadFactoryImpl;
import com.incadencecorp.coalesce.framework.jobs.metrics.PipelineMetrics;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.search.resultset.CoalesceResultSet;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractDriver;
import com.incadencecorp.coalesce.synchronizer.api.common.SynchronizerParameters;
import com.incadencecorp.coalesce.synchronizer.service.operations.DistributedOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This implementation needs to be paired with a {@link com.incadencecorp.coalesce.synchronizer.service.operations.DistributedOperation} using the same topic.
 *
 * @author derek
 */
public class DistibutedDriver extends AbstractDriver {

    private static final Logger LOGGER = LoggerFactory.getLogger(DistibutedDriver.class);

    private ExecutorService executor;
    private int threads = 20;
    private String topic = DistributedOperation.class.getSimpleName();
    private ICoalesceSubscriber subscriber;

    @Override
    public void start()
    {
        LOGGER.info("Starting {} with {} threads", getName(), threads);

        executor = Executors.newScheduledThreadPool(threads, new CoalesceThreadFactoryImpl());

        LOGGER.info("Subscribing to {}", topic);

        subscriber.subscribeTopic(topic, event -> {

            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Topic: {} Keys: {}", topic, event.getValue());
            }

            PipelineMetrics metrics = new PipelineMetrics();

            String[] columnList = new String[1];
            columnList[0] = CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getEntityKey());

            List<Object[]> rows = new ArrayList<>();

            for (String key : event.getValue())
            {
                rows.add(new Object[] { key });
            }

            CoalesceResultSet resultSet = new CoalesceResultSet(rows.iterator(), columnList);

            try
            {
                CachedRowSet rowset = RowSetProvider.newFactory().createCachedRowSet();
                rowset.populate(resultSet);

                executeOperations(metrics, rowset);
            }
            catch (CoalesceException | SQLException e)
            {
                LOGGER.error("Driver Scan Failed", e);
            }

            if (LOGGER.isInfoEnabled())
            {
                LOGGER.info("{} completed: {}", getName(), metrics.getMeterics());
            }

        }, String[].class);
    }

    @Override
    public void stop()
    {
        LOGGER.info("Stopping {}.", getName(), threads);

        try
        {
            // Graceful shutdown (Allow 1 minutes for jobs to finish)
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
        }
        catch (InterruptedException e)
        {
            // Force shutdown
            List<Runnable> jobList = executor.shutdownNow();

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

        // Number of Threads Configured?
        if (parameters.containsKey(SynchronizerParameters.PARAM_DRIVER_MAX_THREADS))
        {
            threads = Integer.parseInt(parameters.get(SynchronizerParameters.PARAM_DRIVER_MAX_THREADS));

            if (threads < 1)
            {
                throw new IllegalArgumentException("Invalid Number of Threads: " + threads);
            }
        }

        topic = params.getOrDefault(SynchronizerParameters.PARAM_OP_TOPIC, DistributedOperation.class.getSimpleName());

    }

    /**
     * Sets the subscriber implementation to use.
     *
     * @param value the subscriber implementation to use.
     */
    public void setSubscriber(ICoalesceSubscriber value)
    {
        subscriber = value;
    }

    @Override
    public void run()
    {
        // Do Nothing
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException
    {
        return executor.invokeAll(tasks);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task)
    {
        return executor.submit(task);
    }

    @Override
    public final void execute(Runnable command)
    {
        executor.execute(command);
    }

    @Override
    public final boolean isShutdown()
    {
        return executor.isShutdown();
    }

    @Override
    public final boolean isTerminated()
    {
        return executor.isTerminated();
    }

    @Override
    public final <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException
    {
        return executor.invokeAll(tasks, timeout, unit);
    }

    @Override
    public final <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException
    {
        return executor.invokeAny(tasks);
    }

    @Override
    public final <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException
    {
        return executor.invokeAny(tasks, timeout, unit);
    }

}
