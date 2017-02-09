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

package com.incadencecorp.coalesce.synchronizer.api.common;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.sql.rowset.CachedRowSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.IExceptionHandler;
import com.incadencecorp.coalesce.api.persistance.ICoalesceExecutorService;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.CoalesceComponentImpl;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.search.resultset.CoalesceResultSet;
import com.incadencecorp.coalesce.synchronizer.api.IPersistorOperation;

/**
 * Abstract implementation which is the base of all operations.
 * 
 * @author n78554
 * @param <T>
 */
public abstract class AbstractOperation<T extends AbstractOperationTask> extends CoalesceComponentImpl implements
        IPersistorOperation, Callable<Boolean> {

    private static Logger LOGGER = LoggerFactory.getLogger(AbstractOperation.class);

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private ICoalescePersistor source;
    private ICoalescePersistor targets[];
    private CachedRowSet rowset;
    private ICoalesceExecutorService service;
    private IExceptionHandler handler;

    private int window = 100;

    /*--------------------------------------------------------------------------
    Getters / Setters
    --------------------------------------------------------------------------*/

    @Override
    public void setExecutor(ICoalesceExecutorService service)
    {
        this.service = service;
    }

    @Override
    public final void setSource(ICoalescePersistor source)
    {
        this.source = source;
    }

    @Override
    public final void setTarget(ICoalescePersistor... targets)
    {
        this.targets = targets;
    }

    @Override
    public void setHandler(IExceptionHandler handler)
    {
        this.handler = handler;
    }

    @Override
    public void setProperties(Map<String, String> params)
    {
        super.setProperties(params);

        if (params.containsKey(SynchronizerParameters.PARAM_OP_WINDOW_SIZE))
        {
            window = Integer.valueOf(params.get(SynchronizerParameters.PARAM_OP_WINDOW_SIZE));

            if (window < 1)
            {
                throw new IllegalArgumentException("Invalid Window Size: " + window);
            }
        }
    }

    @Override
    public final Set<String> getRequiredColumns()
    {
        Set<String> columns = new HashSet<String>();
        columns.add(CoalescePropertyFactory.getEntityKey().getPropertyName());

        Set<String> additionalColumns = getAdditionalRequiredColumns();

        if (additionalColumns != null)
        {
            columns.addAll(getAdditionalRequiredColumns());
        }

        return columns;
    }

    protected Set<String> getAdditionalRequiredColumns()
    {
        return null;
    }

    /*--------------------------------------------------------------------------
    Override Methods
    --------------------------------------------------------------------------*/

    @Override
    public Boolean call() throws CoalesceException
    {
        boolean results = true;

        try
        {
            List<T> tasks = createTasks();

            int ii = 0;

            // Execute Tasks
            for (Future<Boolean> future : service.invokeAll(tasks))
            {
                try
                {
                    // Get Result
                    results = results && future.get();
                }
                catch (ExecutionException e)
                {
                    if (LOGGER.isDebugEnabled())
                    {
                        LOGGER.debug("Operation Failed");
                    }

                    if (handler == null || !handler.handle(tasks.get(ii).getSubset(), this, e))
                    {
                        throw e;
                    }
                }

                ii++;
            }
        }
        catch (InterruptedException | ExecutionException e)
        {
            throw new CoalesceException("Interrupted", e);
        }

        return results;
    }

    @Override
    public CachedRowSet execute(ICoalesceExecutorService service, CachedRowSet rows) throws CoalesceException
    {
        this.service = service;
        this.rowset = rows;

        try
        {
            this.call();
        }
        catch (Exception e)
        {
            throw new CoalesceException("Executing Operation", e);
        }

        return rows;
    }

    /*--------------------------------------------------------------------------
    Abstract Methods
    --------------------------------------------------------------------------*/

    abstract protected T createTask();

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private List<T> createTasks() throws CoalesceException
    {
        List<T> tasks = new ArrayList<T>();

        try
        {

            if (rowset.first())
            {
                List<String> keys = new ArrayList<String>();

                int keyIdx = CoalesceResultSet.getEntityKeyColumn(rowset);

                if (keyIdx == -1)
                {
                    throw new IllegalArgumentException("Missing Column: "
                            + CoalescePropertyFactory.getEntityKey().getPropertyName());
                }

                // Obtain list of keys
                do
                {
                    keys.add(rowset.getString(keyIdx));
                }
                while (rowset.next());

                if (targets != null)
                {
                    for (int ii = 0; ii < keys.size() / window + 1; ii++)
                    {
                        String[] subset = getSubSet(keys, ii * window, window);

                        if (subset.length > 0)
                        {
                            // TODO Load XML First
                            for (ICoalescePersistor target : targets)
                            {
                                T task = createTask();
                                task.setParameters(parameters);
                                task.setSource(source);
                                task.setRowset(rowset);
                                task.setSubset(subset);
                                task.setTarget(target);
                                tasks.add(task);
                            }
                        }
                    }
                }
                else
                {
                    throw new CoalesceException("No Targets Specified");
                }

                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("Created {} Task(s) to process {} keys", tasks.size(), keys.size());
                }
            }
        }
        catch (SQLException e)
        {
            throw new CoalesceException("Creating Tasks", e);
        }

        return tasks;
    }

    private String[] getSubSet(List<String> keys, int offset, int window)
    {
        String[] values = new String[((keys.size() - offset) < window) ? (keys.size() - offset) : window];

        for (int ii = 0; ii < values.length; ii++)
        {
            values[ii] = keys.get(ii + offset);
        }

        return values;
    }

}