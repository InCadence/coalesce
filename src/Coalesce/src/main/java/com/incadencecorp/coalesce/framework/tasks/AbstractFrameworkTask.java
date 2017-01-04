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
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.jobs.metrics.StopWatch;

/**
 * Abstract base for persister tasks in Coalesce.
 * 
 * @author Derek
 *
 * @param <T>
 */
public abstract class AbstractFrameworkTask<T, Y extends ICoalesceResponseTypeBase> implements Callable<MetricResults<Y>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFrameworkTask.class);

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private CoalesceFramework _framework;
    private T _params;
    private StopWatch watch;
    private String id;

    public AbstractFrameworkTask()
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
    public final void setFramework(CoalesceFramework value)
    {
        _framework = value;
    }

    /**
     * Sets the parameters.
     * 
     * @param value
     */
    public final void setParams(T value)
    {
        _params = value;
    }
    
    /**
     * @return the task's ID.
     */
    public final String getId() {
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
    public MetricResults<Y> call()
    {
        try
        {
            MetricResults<Y> result = new MetricResults<Y>();
            
            watch.start();

            result.setResults(doWork(_framework, _params));
            
            watch.finish();

            if (!result.isSuccessful())
            {
                LOGGER.error(String.format(CoalesceErrors.FAILED_TASK,
                                           this.getClass().getName(),
                                           _framework.getClass().getName(),
                                           ""));

                logParameters();
            }

            result.setWatch(watch);
            
            return result;
        }
        catch (Exception e)
        {
            String reason = e.getMessage();

            if (StringHelper.isNullOrEmpty(reason))
            {
                reason = e.getClass().getSimpleName();
            }

            LOGGER.error(String.format(CoalesceErrors.FAILED_TASK,
                                       this.getClass().getName(),
                                       _framework.getClass().getName(),
                                       reason));
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Stack Trace", e);
            }

            logParameters();

            // Re-throw
            throw e;
        }

    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private void logParameters()
    {
        if (LOGGER.isDebugEnabled())
        {
            for (Map.Entry<String, String> entry : getParameters(_params, LOGGER.isTraceEnabled()).entrySet())
            {
                LOGGER.debug(String.format("%s = %s", entry.getKey(), entry.getValue()));
            }
        }
    }

    /*--------------------------------------------------------------------------
    Abstract Methods
    --------------------------------------------------------------------------*/

    abstract protected Y doWork(CoalesceFramework framework, T params);

    abstract protected Map<String, String> getParameters(T params, boolean isTrace);

}
