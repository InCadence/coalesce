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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.api.ICoalesceFrameworkJob;
import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.api.ICoalesceResponseTypeBase;
import com.incadencecorp.coalesce.api.persistance.ICoalesceExecutorService;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.tasks.AbstractFrameworkTask;

/**
 * Abstract base for persister jobs in Coalesce.
 * 
 * @author Derek
 * @param <T> input type
 */
public abstract class AbstractCoalesceFrameworkJob<T, Y extends ICoalesceResponseTypeBase, X extends ICoalesceResponseType<?>>
        extends AbstractCoalesceJob<T, ICoalesceResponseType<List<X>>> implements ICoalesceFrameworkJob {

    private static Logger LOGGER = LoggerFactory.getLogger(AbstractCoalesceFrameworkJob.class);

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private ICoalesceExecutorService _service;
    private CoalesceFramework _framework;

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/

    /**
     * Sets the persistors that the task will be performed on.
     * 
     * @param params task parameters.
     */
    public AbstractCoalesceFrameworkJob(T params)
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
    public void setTarget(CoalesceFramework framework)
    {
        _framework = framework;
    }

    /*--------------------------------------------------------------------------
    Override Methods
    --------------------------------------------------------------------------*/

    @Override
    public final ICoalesceResponseType<List<X>> doWork(T params) throws CoalesceException
    {
        ICoalesceResponseType<List<X>> response = createResponse();

        Collection<AbstractFrameworkTask<?, X>> tasks = getTasks(params);
        
        for (AbstractFrameworkTask<?, X> task : tasks) {
            task.setFramework(_framework);
        }
        
        // Execute Tasks
        try
        {
            for (Future<X> future : _service.invokeAll(tasks))
            {
                X result;

                try
                {
                    result = future.get();
                }
                catch (InterruptedException | ExecutionException e)
                {
                    LOGGER.error(e.getMessage(), e);

                    result = createFailedResults(e);
                    result.setStatus(EResultStatus.FAILED);
                }

                // Add Result to Response
                response.getResult().add(result);
            }
        }
        catch (InterruptedException e)
        {
            throw new CoalesceException("Job Interrupted", e);
        }

        return response;
    }

    /*--------------------------------------------------------------------------
    Abstract Methods
    --------------------------------------------------------------------------*/

    protected abstract Collection<AbstractFrameworkTask<?, X>> getTasks(T params);

    protected abstract ICoalesceResponseType<List<X>> createResponse();

    protected abstract X createFailedResults(Exception e);

}
