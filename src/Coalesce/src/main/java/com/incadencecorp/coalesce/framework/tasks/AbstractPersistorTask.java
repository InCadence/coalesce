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
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.persistance.ResultType;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;

/**
 * Abstract base for persister tasks in Coalesce.
 * 
 * @author Derek
 *
 * @param <T>
 */
public abstract class AbstractPersistorTask<T> implements Callable<ResultType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPersistorTask.class);

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private ICoalescePersistor _persistor;
    private T _params;

    /*--------------------------------------------------------------------------
    Getters / Setters
    --------------------------------------------------------------------------*/

    /**
     * Sets the persistor
     * 
     * @param value
     */
    public void setPersistor(ICoalescePersistor value)
    {
        _persistor = value;
    }

    /**
     * Sets the parameters.
     * 
     * @param value
     */
    public void setParams(T value)
    {
        _params = value;
    }

    /**
     * @return the persistor.
     */
    public ICoalescePersistor getPersistor()
    {
        return _persistor;
    }

    /**
     * @return the parameters.
     */
    public T getParams()
    {
        return _params;
    }

    /*--------------------------------------------------------------------------
    Override Methods
    --------------------------------------------------------------------------*/

    @Override
    public ResultType call()
    {
        try
        {

            ResultType result = doWork();

            if (!result.isSuccessful())
            {
                LOGGER.error(String.format(CoalesceErrors.FAILED_TASK,
                                           this.getClass().getName(),
                                           _persistor.getClass().getName(),
                                           result.getMessage()));

                if (LOGGER.isDebugEnabled() && result.getException() != null)
                {
                    LOGGER.debug("Stack Trace", result.getException());
                }

                logParameters();
            }

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
                                       _persistor.getClass().getName(),
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

    abstract protected ResultType doWork();

    abstract protected Map<String, String> getParameters(T params, boolean isTrace);

}
