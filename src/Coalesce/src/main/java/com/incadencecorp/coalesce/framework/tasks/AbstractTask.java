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
import com.incadencecorp.coalesce.api.ICoalesceResponseTypeBase;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.jobs.metrics.StopWatch;

/**
 * Abstract base for persister tasks in Coalesce.
 * 
 * @author Derek
 *
 * @param <T>
 */
public abstract class AbstractTask<T, Y extends ICoalesceResponseTypeBase, X> implements Callable<MetricResults<Y>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTask.class);

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private X target;
    private T params;
    private StopWatch watch;
    private String id;

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
    public final void setTarget(X value)
    {
        target = value;
    }

    /**
     * Sets the parameters.
     * 
     * @param value
     */
    public final void setParams(T value)
    {
        params = value;
    }

    /**
     * @return the parameters.
     */
    public final T getParams()
    {
        return params;
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
    public MetricResults<Y> call() throws CoalesceException
    {
        MetricResults<Y> result = new MetricResults<Y>();

        watch.start();

        try
        {
            TaskParameters<X, T> extraParams = new TaskParameters<X, T>();
            extraParams.setTarget(target);
            extraParams.setParams(params);

            result.setResults(doWork(extraParams));


            if (!result.isSuccessful())
            {
                LOGGER.error(String.format(CoalesceErrors.FAILED_TASK,
                                           this.getClass().getName(),
                                           target.getClass().getName(),
                                           "Failure"));

                logParameters();
            }
        }
        catch (CoalesceException e)
        {
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
            for (Map.Entry<String, String> entry : getParameters(params, LOGGER.isTraceEnabled()).entrySet())
            {
                LOGGER.debug(String.format("%s = %s", entry.getKey(), entry.getValue()));
            }
        }
    }

    /*--------------------------------------------------------------------------
    Abstract Methods
    --------------------------------------------------------------------------*/

    abstract protected Y doWork(TaskParameters<X, T> parameters) throws CoalesceException;

    abstract protected Map<String, String> getParameters(T params, boolean isTrace);

    abstract protected Y createResult();

}
