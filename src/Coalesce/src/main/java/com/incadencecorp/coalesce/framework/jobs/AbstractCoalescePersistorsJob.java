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

import com.incadencecorp.coalesce.api.*;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceResponseType;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.tasks.AbstractPersistorTask;
import com.incadencecorp.coalesce.framework.tasks.MetricResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Abstract base for jobs that perform the same task with the same parameters
 * for each persister configured.
 *
 * @param <INPUT>  input type
 * @param <OUTPUT>
 * @author Derek
 */
public abstract class AbstractCoalescePersistorsJob<INPUT, OUTPUT extends ICoalesceResponseType<?>>
        extends AbstractCoalesceJob<INPUT, ICoalesceResponseType<List<OUTPUT>>, OUTPUT> implements ICoalescePersistorJob {

    private static Logger LOGGER = LoggerFactory.getLogger(AbstractCoalescePersistorsJob.class);

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

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
    public AbstractCoalescePersistorsJob(INPUT params)
    {
        super(params);
    }

    /*--------------------------------------------------------------------------
    Getters / Setters
    --------------------------------------------------------------------------*/

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
    public final ICoalesceResponseType<List<OUTPUT>> doWork(ICoalescePrincipal principal, INPUT params)
            throws CoalesceException
    {
        List<OUTPUT> results = new ArrayList<>();

        try
        {
            List<AbstractPersistorTask<INPUT, OUTPUT>> tasks = new ArrayList<>();

            // Create Tasks
            for (ICoalescePersistor persistor : _persistors)
            {
                AbstractPersistorTask<INPUT, OUTPUT> task = createTask();
                task.setParams(params);
                task.setTarget(persistor);
                task.setPrincipal(principal);

                tasks.add(task);
            }

            int ii = 0;

            // Execute Tasks
            for (Future<MetricResults<OUTPUT>> future : getService().invokeAll(tasks))
            {
                MetricResults<OUTPUT> metrics;

                try
                {
                    metrics = future.get();
                }
                catch (InterruptedException | ExecutionException e)
                {
                    LOGGER.error("Interrupted Task", e);

                    // Create Failed Result
                    OUTPUT task = createResults();
                    task.setError(e.getMessage());
                    task.setStatus(EResultStatus.FAILED);

                    metrics = new MetricResults<>(this.getName());
                    metrics.setResults(task);
                    metrics.getResults().setStatus(EResultStatus.FAILED);
                }

                if (!metrics.isSuccessful() && _handler != null)
                {
                    if (LOGGER.isTraceEnabled())
                    {
                        LOGGER.trace("Handling ({})", _handler.getName());
                    }

                    if (metrics.getResults() instanceof CoalesceResponseType)
                    {
                        _handler.handle(getKeys(tasks.get(ii)),
                                        this,
                                        ((CoalesceResponseType) metrics.getResults()).getException());
                    }
                }

                addResult(metrics);

                results.add(metrics.getResults());

                ii++;
            }
        }
        catch (InterruptedException e)
        {
            throw new CoalesceException("Job Interrupted", e);
        }

        CoalesceResponseType<List<OUTPUT>> result = new CoalesceResponseType<>();
        result.setResult(results);
        result.setStatus(EResultStatus.SUCCESS);

        return result;
    }

    /*--------------------------------------------------------------------------
    Abstract Methods
    --------------------------------------------------------------------------*/

    abstract protected AbstractPersistorTask<INPUT, OUTPUT> createTask();

    abstract protected String[] getKeys(AbstractPersistorTask<INPUT, OUTPUT> task);
}
