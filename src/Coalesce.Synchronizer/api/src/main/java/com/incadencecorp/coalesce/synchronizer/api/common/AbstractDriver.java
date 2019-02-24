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

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.persistance.ICoalesceExecutorService;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.CoalesceComponentImpl;
import com.incadencecorp.coalesce.framework.jobs.metrics.PipelineMetrics;
import com.incadencecorp.coalesce.synchronizer.api.IPersistorDriver;
import com.incadencecorp.coalesce.synchronizer.api.IPersistorOperation;
import com.incadencecorp.coalesce.synchronizer.api.IPersistorScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Abstract implementation which is the base of all drivers.
 *
 * @author n78554
 */
public abstract class AbstractDriver extends CoalesceComponentImpl
        implements IPersistorDriver, ICoalesceExecutorService, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDriver.class);
    private boolean isInitialized = false;

    private IPersistorScan scanner;
    private IPersistorOperation operations[];

    @Override
    public final void setup()
    {
        if (!isInitialized)
        {
            if (operations == null)
            {
                throw new IllegalStateException(String.format(CoalesceErrors.NOT_INITIALIZED, "Operations"));
            }

            if (scanner != null)
            {
                Set<String> columns = new LinkedHashSet<>();

                for (IPersistorOperation operation : operations)
                {
                    columns.addAll(operation.getRequiredColumns());
                }

                if (LOGGER.isTraceEnabled())
                {
                    LOGGER.trace(String.format("%s's Required Columns:", scanner.getName()));

                    for (String column : columns)
                    {
                        LOGGER.trace(String.format("\t%s", column));
                    }
                }

                scanner.setReturnedColumns(columns);
                scanner.setup();
            }

            doSetup();
            isInitialized = true;
        }
    }

    @Override
    public void run()
    {
        PipelineMetrics metrics = new PipelineMetrics();

        if (LOGGER.isTraceEnabled())
        {
            LOGGER.trace("Driver {} Started", getName());
        }

        if (LOGGER.isTraceEnabled())
        {
            LOGGER.trace("Running {} Scan", scanner.getName());
        }

        // Perform Scan
        try (CachedRowSet results = scanner.scan())
        {
            metrics.finish("scan");

            try
            {
                if (LOGGER.isInfoEnabled())
                {
                    LOGGER.info("{} completed with {} results.", scanner.getName(), results.size());
                }

                if (results.size() > 0)
                {
                    // Execute Operation(s) on Results
                    executeOperations(metrics, results);
                }

                scanner.finished(true, results);
            }
            catch (CoalesceException e)
            {
                LOGGER.error("Driver Execution Failed", e);

                scanner.finished(false, results);
            }
        }
        catch (CoalesceException e)
        {
            LOGGER.error("Driver Scan Failed", e);
        }
        catch (Exception e)
        {
            LOGGER.error("Driver Failed", e);
        }

        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("{} completed: {}", getName(), metrics.getMeterics());
        }

    }

    protected void executeOperations(PipelineMetrics metrics, CachedRowSet results) throws CoalesceException
    {
        for (IPersistorOperation operation : operations)
        {
            if (LOGGER.isTraceEnabled())
            {
                LOGGER.trace("Executing {} Operation", operation.getName());
            }

            operation.execute(this, results);
            metrics.finish(operation.getName());
        }
    }

    @Override
    public final void setScan(IPersistorScan scan)
    {
        this.scanner = scan;
    }

    @Override
    public final void setOperations(IPersistorOperation... operations)
    {
        this.operations = operations;
    }

    protected void doSetup()
    {
        // Do Nothing
    }

}
