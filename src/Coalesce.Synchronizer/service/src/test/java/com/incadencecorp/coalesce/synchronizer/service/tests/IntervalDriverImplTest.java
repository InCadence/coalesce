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

package com.incadencecorp.coalesce.synchronizer.service.tests;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.incadencecorp.coalesce.synchronizer.api.common.SynchronizerParameters;
import com.incadencecorp.coalesce.synchronizer.service.drivers.IntervalDriverImpl;

/**
 * These test ensure providing invalid parameters will throw an exception.
 * 
 * @author n78554
 */
public class IntervalDriverImplTest {

    /**
     * Ensures that providing an invalid value throws an
     * {@link IllegalArgumentException}.
     * 
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRangeDelay() throws Exception
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SynchronizerParameters.PARAM_DRIVER_DELAY, "-1");

        IntervalDriverImpl driver = new IntervalDriverImpl();
        driver.setProperties(params);
    }

    /**
     * Ensures that providing an invalid value throws an
     * {@link IllegalArgumentException}.
     * 
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidNumberDelay() throws Exception
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SynchronizerParameters.PARAM_DRIVER_DELAY, "A");

        IntervalDriverImpl driver = new IntervalDriverImpl();
        driver.setProperties(params);
    }

    /**
     * Ensures that providing an invalid value throws an
     * {@link IllegalArgumentException}.
     * 
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRangeInterval() throws Exception
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SynchronizerParameters.PARAM_DRIVER_INTERVAL, "-1");

        IntervalDriverImpl driver = new IntervalDriverImpl();
        driver.setProperties(params);
    }

    /**
     * Ensures that providing an invalid value throws an
     * {@link IllegalArgumentException}.
     * 
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidNumberInterval() throws Exception
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SynchronizerParameters.PARAM_DRIVER_INTERVAL, "A");

        IntervalDriverImpl driver = new IntervalDriverImpl();
        driver.setProperties(params);
    }

    /**
     * Ensures that providing an invalid value throws an
     * {@link IllegalArgumentException}.
     * 
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRangeThreads() throws Exception
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SynchronizerParameters.PARAM_DRIVER_MAX_THREADS, "-1");

        IntervalDriverImpl driver = new IntervalDriverImpl();
        driver.setProperties(params);
    }

    /**
     * Ensures that providing an invalid value throws an
     * {@link IllegalArgumentException}.
     * 
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidNumberThreads() throws Exception
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SynchronizerParameters.PARAM_DRIVER_MAX_THREADS, "A");

        IntervalDriverImpl driver = new IntervalDriverImpl();
        driver.setProperties(params);
    }

    /**
     * Ensures that providing an invalid value throws an
     * {@link IllegalArgumentException}.
     * 
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTimeUnit() throws Exception
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SynchronizerParameters.PARAM_DRIVER_INTERVAL_UNITS, "HelloWorld");

        IntervalDriverImpl driver = new IntervalDriverImpl();
        driver.setProperties(params);
    }

    /**
     * Ensures proper parsing of time unites.
     * 
     * @throws Exception
     */
    @Test
    public void testTimeUnit() throws Exception
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SynchronizerParameters.PARAM_DRIVER_INTERVAL, "20");
        params.put(SynchronizerParameters.PARAM_DRIVER_DELAY, "10");
        params.put(SynchronizerParameters.PARAM_DRIVER_MAX_THREADS, "10");

        IntervalDriverImpl driver = new IntervalDriverImpl();

        for (TimeUnit unit : TimeUnit.values())
        {
            params.put(SynchronizerParameters.PARAM_DRIVER_INTERVAL_UNITS, unit.toString());
            driver.setProperties(params);
        }

    }

}
