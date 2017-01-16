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

package com.incadencecorp.coalesce.framework.tasks;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.api.ICoalescePrincipal;
import com.incadencecorp.coalesce.api.ICoalesceResponseTypeBase;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.jobs.metrics.StopWatch;

/**
 * Abstract base for persister tasks in Coalesce.
 * 
 * @author Derek
 *
 * @param <INPUT>
 * @param <OUTPUT>
 * @param <TARGET>
 */
public abstract class AbstractTask<INPUT, OUTPUT extends ICoalesceResponseTypeBase, TARGET> implements Callable<MetricResults<OUTPUT>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTask.class);

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private StopWatch watch;
    private String id;
    private TaskParameters<TARGET, INPUT> parameters = new TaskParameters<TARGET, INPUT>();

    public AbstractTask()
    {
        id = UUID.randomUUID().toString();
        watch = new StopWatch();
    }

    /*--------------------------------------------------------------------------
    Getters / Setters
    --------------------------------------------------------------------------*/

    /**
     * Sets the framework
     * 
     * @param value
     */
    public final void setTarget(TARGET value)
    {
        parameters.setTarget(value);
    }
    
    /**
     * Sets the principal of the user running this task.
     * @param value
     */
    public final void setPrincipal(ICoalescePrincipal value)
    {
        parameters.setPrincipal(value);
    }

    /**
     * Sets the parameters.
     * 
     * @param value
     */
    public final void setParams(INPUT value)
    {
        parameters.setParams(value);
    }

    /**
     * @return the parameters.
     */
    public final INPUT getParams()
    {
        return parameters.getParams();
    }

    /**
     * @return the task's ID.
     */
    public final String getId()
    {
        return id;
    }

    /**
     * @return the metrics for running the job.
     */
    public final StopWatch getMetrics()
    {
        return watch;
    }

    /*--------------------------------------------------------------------------
    Override Methods
    --------------------------------------------------------------------------*/

    @Override
    public MetricResults<OUTPUT> call() throws CoalesceException
    {
        MetricResults<OUTPUT> result = new MetricResults<OUTPUT>();

        watch.start();

        try
        {
            result.setResults(doWork(parameters));


            if (!result.isSuccessful())
            {
                LOGGER.error(String.format(CoalesceErrors.FAILED_TASK,
                                           this.getClass().getName(),
                                           parameters.getTarget().getClass().getName(),
                                           "Failure"));

                logParameters();
            }
        }
        catch (CoalesceException e)
        {
            LOGGER.error("(FAILED) Task", e);
            
            result.setResults(createResult());
            result.getResults().setStatus(EResultStatus.FAILED);
            result.getResults().setError(e.getMessage());

            // String reason = e.getMessage();
            //
            // if (StringHelper.isNullOrEmpty(reason))
            // {
            // reason = e.getClass().getSimpleName();
            // }
            //
            // LOGGER.error(String.format(CoalesceErrors.FAILED_TASK,
            // this.getClass().getName(),
            // target.getClass().getName(),
            // reason));
            // if (LOGGER.isDebugEnabled())
            // {
            // LOGGER.debug("Stack Trace", e);
            // }
            //
            // logParameters();
            //
            // // Re-throw
            // throw e;
        } finally {
            watch.finish();
        }

        result.setWatch(watch);
        return result;
    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private void logParameters()
    {
        if (LOGGER.isDebugEnabled())
        {
            for (Map.Entry<String, String> entry : getParameters(parameters.getParams(), LOGGER.isTraceEnabled()).entrySet())
            {
                LOGGER.debug(String.format("%s = %s", entry.getKey(), entry.getValue()));
            }
        }
    }

    /*--------------------------------------------------------------------------
    Abstract Methods
    --------------------------------------------------------------------------*/

    abstract protected OUTPUT doWork(TaskParameters<TARGET, INPUT> parameters) throws CoalesceException;

    abstract protected Map<String, String> getParameters(INPUT params, boolean isTrace);

    abstract protected OUTPUT createResult();

}
