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

import com.incadencecorp.coalesce.api.CoalesceParameters;

/**
 * Defines the parameters used by the synchronization service.
 *
 * @author n78554
 * @see CoalesceParameters
 */
public final class SynchronizerParameters {

    private static final String INC = "com.incadencecorp.";
    private static final String SCANNERS = INC + "scanners.";
    private static final String OPS = INC + "operations.";
    private static final String DRIVER = INC + "driver.";

    private SynchronizerParameters()
    {
        // Do Nothing
    }

    /*--------------------------------------------------------------------------
    Driver Parameters
    --------------------------------------------------------------------------*/

    /**
     * (Integer) Max number of threads to execute operations on.
     */
    public static final String PARAM_DRIVER_MAX_THREADS = DRIVER + "threads";

    /**
     * (Integer) Delay before driver starts processing data after starting it.
     */
    public static final String PARAM_DRIVER_DELAY = DRIVER + "delay";

    /**
     * (Integer) Periodic interval in which the driver should be ran. Used by
     * the Interval driver.
     */
    public static final String PARAM_DRIVER_INTERVAL = DRIVER + "interval";

    /**
     * (TimeUnit as a String, TimeUnit.MINUTES.toString()) Units of the
     * interval.
     */
    public static final String PARAM_DRIVER_INTERVAL_UNITS = DRIVER + "intervalunits";

    /**
     * (Boolean) If <code>true</code> then execute driver.
     */
    public static final String PARAM_DRIVER_EXECUTE = DRIVER + "execute";

    /*--------------------------------------------------------------------------
    Operation Parameters
    --------------------------------------------------------------------------*/

    /**
     * (Integer) Determines the number of keys that can be processed by a
     * Operation's single task.
     */
    public static final String PARAM_OP_WINDOW_SIZE = OPS + "window";

    /**
     * (Boolean) If <code>true</code> then don't save changes to the target;
     * just log them.
     */
    public static final String PARAM_OP_DRYRUN = OPS + "dryrun";

    /*--------------------------------------------------------------------------
    Scanner Parameters
    --------------------------------------------------------------------------*/

    /**
     * (DateTime) Last time the scan successfully completed.
     */
    public static final String PARAM_SCANNER_LAST_SUCCESS = SCANNERS + "lastscan";

    /**
     * (String) CQL used to filter results. Property name syntax is 'recordset.fieldname' and MUST be wrapped in double quotes.
     */
    public static final String PARAM_SCANNER_CQL = SCANNERS + "cql";

    /**
     * (Integer) Number of days since last scanned to confine the scanner to a
     * time period. If not specified it defaults to
     * {@value #DEFAULT_SCANNER_WINDOW}.
     */
    public static final String PARAM_SCANNER_WINDOW = SCANNERS + "window";

    public static final String PARAM_SCANNER_WINDOW_UNITS = SCANNERS + "window.units";

    /**
     * {@link #PARAM_SCANNER_WINDOW} default value.
     */
    public static final int DEFAULT_SCANNER_WINDOW = 0;

    /**
     * (Integer) Number of results returned per scan. If not specified; defaults to {@value DEFAULT_SCANNER_MAX}.
     */
    public static final String PARAM_SCANNER_MAX = SCANNERS + "max";

    /**
     * {@link #PARAM_SCANNER_MAX} default value.
     */
    public static final int DEFAULT_SCANNER_MAX = 200;

    /**
     * (String) Date / Time Pattern.
     */
    public static final String PARAM_SCANNER_DATETIME_PATTERN = SCANNERS + "pattern";

}
