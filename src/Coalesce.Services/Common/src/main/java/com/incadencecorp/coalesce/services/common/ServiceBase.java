package com.incadencecorp.coalesce.services.common;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.ICoalesceComponent;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.jobs.AbstractCoalesceJob;
import com.incadencecorp.coalesce.services.api.ICoalesceExecutorService;

public class ServiceBase implements ICoalesceExecutorService, AutoCloseable {

    /*--------------------------------------------------------------------------
    Static Variables
    --------------------------------------------------------------------------*/
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceBase.class);

    /*--------------------------------------------------------------------------
    Member Variables
    --------------------------------------------------------------------------*/

    private ExecutorService service;
    private CoalesceFramework framework;
    final private ConcurrentHashMap<String, AbstractCoalesceJob<?, ?>> jobs = new ConcurrentHashMap<String, AbstractCoalesceJob<?, ?>>();

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    public ServiceBase()
    {
        this(null, null);
    }

    /**
     * Construct service while specifing the service and framework to use.
     * 
     * @param service
     * @param framework
     */
    public ServiceBase(ExecutorService service, CoalesceFramework framework)
    {
        setExecutorService(service);
        setCoalesceFramework(framework);
    }

    /*--------------------------------------------------------------------------
    Setters / Getters
    --------------------------------------------------------------------------*/

    /**
     * Sets the service to use for submitting tasks.
     * 
     * @param service
     */
    public void setExecutorService(ExecutorService service)
    {
        this.service = service;
    }

    private void getExecutorService(ExecutorService service)
    {
        this.service = service;
    }

    /**
     * Sets the framework to use.
     * 
     * @param framework
     */
    public void setCoalesceFramework(CoalesceFramework framework)
    {
        this.framework = framework;
    }

    /**
     * @return the framework.
     */
    public CoalesceFramework getCoalesceFramework()
    {
        return framework;
    }

    /*--------------------------------------------------------------------------
    AutoCloseable Implementation
    --------------------------------------------------------------------------*/

    /*--------------------------------------------------------------------------
    AutoCloseable Implementation
    --------------------------------------------------------------------------*/

    public void close() throws Exception
    {
        try
        {
            service.shutdown();
            service.awaitTermination(1, TimeUnit.MINUTES);
        }
        catch (InterruptedException e)
        {
            for (Runnable runnable : service.shutdownNow())
            {
                if (runnable instanceof ICoalesceComponent)
                {
                    LOGGER.warn("Runnable Expired ({})", ((ICoalesceComponent) runnable).getName());
                }
            }
        }
    }

    /*--------------------------------------------------------------------------
    ICoalesceExecutorService Implementation
    --------------------------------------------------------------------------*/

    public void execute(Runnable command)
    {
        service.execute(command);
    }

    public boolean isShutdown()
    {
        return service.isShutdown();
    }

    public boolean isTerminated()
    {
        return service.isTerminated();
    }

    public <T, Y> Future<Y> submit(AbstractCoalesceJob<T, Y> job)
    {
        return service.submit(job);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException
    {
        return service.invokeAll(tasks);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException
    {
        return service.invokeAll(tasks, timeout, unit);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException
    {
        return service.invokeAny(tasks);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException
    {
        return service.invokeAny(tasks, timeout, unit);
    }

}
