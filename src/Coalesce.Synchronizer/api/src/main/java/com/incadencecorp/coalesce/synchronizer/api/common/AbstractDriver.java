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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.sql.rowset.CachedRowSet;

import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.persistance.ICoalesceExecutorService;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.CoalesceComponentImpl;
import com.incadencecorp.coalesce.synchronizer.api.IPersistorDriver;
import com.incadencecorp.coalesce.synchronizer.api.IPersistorOperation;
import com.incadencecorp.coalesce.synchronizer.api.IPersistorScan;

/**
 * Abstract implementation which is the base of all drivers.
 * 
 * @author n78554
 */
public abstract class AbstractDriver extends CoalesceComponentImpl implements IPersistorDriver, ICoalesceExecutorService,
        Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDriver.class);
    private boolean isInitialized = false;

    private IPersistorScan scanner;
    private IPersistorOperation operations[];

    @Override
    public final void setup()
    {
        if (!isInitialized)
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

            doSetup();
            isInitialized = true;
        }
    }

    @Override
    public void run()
    {
        if (LOGGER.isTraceEnabled())
        {
            LOGGER.trace("Driver {} Started", getName());
        }

        StopWatch watch = new StopWatch();
        watch.start();

        if (LOGGER.isTraceEnabled())
        {
            LOGGER.trace("Running {} Scan", scanner.getName());
        }

        // Perform Scan
        try
        {
            CachedRowSet results = scanner.scan();

            try
            {
                LOGGER.info("{} completed in {} ms with {} results.", scanner.getName(), watch.getTime(), results.size());

                if (results.size() > 0)
                {
                    StopWatch operationWatch = new StopWatch();

                    // Execute Operation(s) on Results
                    for (IPersistorOperation operation : operations)
                    {
                        if (LOGGER.isTraceEnabled())
                        {
                            LOGGER.trace("Executing {} Operation", operation.getName());
                        }

                        operationWatch.start();
                        results = operation.execute(this, results);
                        LOGGER.info("{} completed in {} ms", operation.getName(), operationWatch.getTime());
                        operationWatch.reset();
                    }
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

        watch.stop();

        LOGGER.info("{} completed in {} ms", getName(), watch.getTime());

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
