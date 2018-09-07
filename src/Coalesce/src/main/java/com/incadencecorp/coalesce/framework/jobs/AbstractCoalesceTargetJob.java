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

import com.incadencecorp.coalesce.api.EJobStatus;
import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.api.ICoalesceTargetJob;
import com.incadencecorp.coalesce.api.ICoalescePrincipal;
import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.tasks.AbstractTask;
import com.incadencecorp.coalesce.framework.tasks.MetricResults;

/**
 * Abstract base for jobs that spawn multiple task depending on parameters of
 * the input.
 * 
 * @author Derek
 * @param <INPUT> input type
 */
public abstract class AbstractCoalesceTargetJob<INPUT, OUTPUT extends ICoalesceResponseType<List<TASKOUTPUT>>, TASKOUTPUT extends ICoalesceResponseType<?>, TARGET>
        extends AbstractCoalesceJob<INPUT, OUTPUT, TASKOUTPUT> implements ICoalesceTargetJob<TARGET> {

    private static Logger LOGGER = LoggerFactory.getLogger(AbstractCoalesceTargetJob.class);

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private TARGET _target;

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/

    /**
     * Sets the persistors that the task will be performed on.
     * 
     * @param params task parameters.
     */
    public AbstractCoalesceTargetJob(INPUT params)
    {
        super(params);
    }

    /*--------------------------------------------------------------------------
    Getters / Setters
    --------------------------------------------------------------------------*/

    @Override
    public final void setTarget(TARGET value)
    {
        _target = value;
    }

    /**
     * @return the response with the job ID and status. Also includes the
     *         results if completed or an error message if an exception was
     *         thrown with the status of JOB_FAILED.
     */
    public final OUTPUT getResponse()
    {
        OUTPUT response;

        if (isDone())
        {
            try
            {
                // Get Response
                response = getFuture().get();
                if (getJobStatus() == EJobStatus.COMPLETE)
                {
                    response.setStatus(EResultStatus.SUCCESS);
                }
                else
                {
                    response.setStatus(EResultStatus.FAILED_PENDING);
                }
            }
            catch (InterruptedException | ExecutionException e)
            {
                LOGGER.error(e.getMessage(), e);

                // Create Response w/ Error Message
                response = createResponse();
                response.setStatus(EResultStatus.FAILED);
            }
        }
        else
        {
            // Create Response w/o Results
            response = createResponse();
            if (getJobStatus() == EJobStatus.COMPLETE)
            {
                response.setStatus(EResultStatus.SUCCESS);
            }
            else
            {
                response.setStatus(EResultStatus.FAILED_PENDING);
            }

            // Check for Race Condition (Job completed after isDone() check and
            // before response creation)
            if (getJobStatus() == EJobStatus.COMPLETE)
            {
                setJobStatus(EJobStatus.IN_PROGRESS);
            }
        }

        response.setId(getJobId());

        return response;
    }

    /*--------------------------------------------------------------------------
    Override Methods
    --------------------------------------------------------------------------*/

    @Override
    public final OUTPUT doWork(ICoalescePrincipal principal, INPUT params) throws CoalesceException
    {
        OUTPUT response = createResponse();
        
        // Default to Success
        response.setStatus(EResultStatus.SUCCESS);

        Collection<AbstractTask<?, TASKOUTPUT, TARGET>> tasks = getTasks(params);

        for (AbstractTask<?, TASKOUTPUT, TARGET> task : tasks)
        {
            task.setTarget(_target);
            task.setPrincipal(principal);
        }

        // Execute Tasks
        try
        {
            for (Future<MetricResults<TASKOUTPUT>> future : getService().invokeAll(tasks))
            {
                MetricResults<TASKOUTPUT> result;

                try
                {
                    result = future.get();
                }
                catch (InterruptedException | ExecutionException e)
                {
                    LOGGER.error(e.getMessage(), e);

                    TASKOUTPUT task = createResults();
                    task.setError(e.getMessage());
                    task.setStatus(EResultStatus.FAILED);

                    result = new MetricResults<>(getName());
                    result.setResults(task);
                    result.getResults().setStatus(EResultStatus.FAILED);
                }

                addResult(result);

                // Add Result to Response
                response.getResult().add(result.getResults());
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

    protected abstract Collection<AbstractTask<?, TASKOUTPUT, TARGET>> getTasks(INPUT params) throws CoalesceException;

}
