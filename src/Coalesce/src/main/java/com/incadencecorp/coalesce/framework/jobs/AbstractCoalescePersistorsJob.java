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

package com.incadencecorp.coalesce.framework.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.ICoalescePersistorJob;
import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.api.IExceptionHandler;
import com.incadencecorp.coalesce.api.persistance.ICoalesceExecutorService;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceResponseType;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceStringResponseType;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.tasks.AbstractPersistorTask;

/**
 * Abstract base for persister jobs in Coalesce.
 * 
 * @author Derek
 * @param <T> input type
 */
public abstract class AbstractCoalescePersistorsJob<T> extends AbstractCoalesceJob<T, ICoalesceResponseType<List<CoalesceStringResponseType>>, CoalesceStringResponseType>
        implements ICoalescePersistorJob {

    private static Logger LOGGER = LoggerFactory.getLogger(AbstractCoalescePersistorsJob.class);

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private ICoalesceExecutorService _service;
    private IExceptionHandler _handler;
    private ICoalescePersistor _persistors[];

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/

    /**
     * Sets the persistors that the task will be performed on.
     * 
     * @param params task parameters.
     */
    public AbstractCoalescePersistorsJob(T params)
    {
        super(params);
    }

    /*--------------------------------------------------------------------------
    Getters / Setters
    --------------------------------------------------------------------------*/

    @Override
    public void setExecutor(ICoalesceExecutorService service)
    {
        _service = service;
    }

    @Override
    public void setHandler(IExceptionHandler handler)
    {
        _handler = handler;
    }

    @Override
    public void setTarget(ICoalescePersistor... targets)
    {
        _persistors = targets;
    }

    /*--------------------------------------------------------------------------
    Override Methods
    --------------------------------------------------------------------------*/

    @Override
    public final ICoalesceResponseType<List<CoalesceStringResponseType>> doWork(T params) throws CoalesceException
    {
        List<CoalesceStringResponseType> results = new ArrayList<CoalesceStringResponseType>();

        try
        {
            CoalesceStringResponseType result;

            List<AbstractPersistorTask<T>> tasks = new ArrayList<AbstractPersistorTask<T>>();

            // Create Tasks
            for (int ii = 0; ii < _persistors.length; ii++)
            {
                tasks.add(createTask(params, _persistors[ii]));
            }

            int ii = 0;

            // Execute Tasks
            for (Future<CoalesceStringResponseType> future : _service.invokeAll(tasks))
            {
                try
                {
                    // Get Result
                    result = future.get();
                }
                catch (InterruptedException | ExecutionException e)
                {
                    LOGGER.error("Interrupted Task", e);

                    // Create Failed Result
                    result = new CoalesceStringResponseType();
                    result.setException(e);
                }

                if (!result.isSuccessful() && _handler != null)
                {
                    if (LOGGER.isTraceEnabled())
                    {
                        LOGGER.trace("Handling ({})", _handler.getName());
                    }

                    _handler.handle(getKeys(tasks.get(ii)), this, result.getException());
                }

                results.add(result);

                ii++;
            }
        }
        catch (InterruptedException e)
        {
            LOGGER.error("Interrupted Job", e);

            CoalesceStringResponseType result = new CoalesceStringResponseType();
            result.setException(e);

            results = new ArrayList<CoalesceStringResponseType>();
            results.add(result);

        }

        CoalesceResponseType<List<CoalesceStringResponseType>> result = new CoalesceResponseType<List<CoalesceStringResponseType>>();
        result.setResult(results);

        return result;
    }

    /*--------------------------------------------------------------------------
    Abstract Methods
    --------------------------------------------------------------------------*/

    abstract protected AbstractPersistorTask<T> createTask();

    abstract protected String[] getKeys(AbstractPersistorTask<T> task);

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private AbstractPersistorTask<T> createTask(T params, ICoalescePersistor persistor)
    {
        AbstractPersistorTask<T> task = createTask();
        task.setParams(params);
        task.setPersistor(persistor);

        return task;
    }

}
