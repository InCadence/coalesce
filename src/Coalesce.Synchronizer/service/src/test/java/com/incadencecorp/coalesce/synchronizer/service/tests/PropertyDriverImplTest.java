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

import com.incadencecorp.coalesce.framework.PropertyLoader;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.persistance.derby.DerbyPersistor;
import com.incadencecorp.coalesce.synchronizer.api.IPersistorDriver;
import com.incadencecorp.coalesce.synchronizer.api.common.SynchronizerParameters;
import com.incadencecorp.coalesce.synchronizer.api.common.mocks.MockOperation;
import com.incadencecorp.coalesce.synchronizer.api.common.mocks.MockScanner;
import com.incadencecorp.coalesce.synchronizer.service.drivers.PropertyDriverImpl;
import com.incadencecorp.unity.common.connectors.MemoryConnector;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * These unit test are for the {@link PropertyDriverImpl} implementation.
 *
 * @author n78554
 */
public class PropertyDriverImplTest {

    /**
     * Ensures the scan skips if the property is not set.
     *
     * @throws Exception
     */
    @Test
    public void testDriver() throws Exception
    {
        String newTitle = "Hello World";

        Map<String, String> params = new HashMap<String, String>();
        params.put(SynchronizerParameters.PARAM_DRIVER_INTERVAL_UNITS, TimeUnit.SECONDS.toString());
        params.put(SynchronizerParameters.PARAM_DRIVER_DELAY, "1");
        params.put(SynchronizerParameters.PARAM_DRIVER_MAX_THREADS, "10");

        CoalesceEntity entity = CoalesceEntity.create("UNIT_TEST", "UNIT_TEST", "1", null, null);
        entity.initialize();

        DerbyPersistor source = new DerbyPersistor();
        source.saveEntity(false, entity);

        MockScanner scan = new MockScanner();
        scan.setProperties(params);
        scan.setSource(source);
        scan.setup();

        MockOperation operation = new MockOperation();
        operation.setTitle(newTitle);
        operation.setSource(source);
        operation.setTarget(source);

        PropertyLoader loader = new PropertyLoader(new MemoryConnector(), "unit-test.properties");
        loader.setProperty("test", "test");

        IPersistorDriver driver = new PropertyDriverImpl();
        driver.setScan(scan);
        driver.setPropertyLoader(loader);
        driver.setProperties(params);
        driver.setOperations(operation);
        driver.setup();

        driver.start();

        Assert.assertEquals(entity.getName(), source.getEntity(entity.getKey())[0].getTitle());

        driver.stop();

        loader.setProperty(SynchronizerParameters.PARAM_DRIVER_EXECUTE, Boolean.toString(true));

        driver.start();

        Assert.assertEquals(newTitle, source.getEntity(entity.getKey())[0].getTitle());

        driver.stop();
    }

    /**
     * Ensures setting parameters works correctly.
     *
     * @throws Exception
     */
    @Test
    public void testProperty() throws Exception
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SynchronizerParameters.PARAM_DRIVER_EXECUTE, "A");

        PropertyLoader loader = new PropertyLoader(new MemoryConnector(), "unit-test.properties");
        loader.setProperty("test", "test");

        IPersistorDriver driver = new PropertyDriverImpl();
        driver.setPropertyLoader(loader);
        driver.setProperties(params);

        Assert.assertFalse(Boolean.valueOf(loader.getProperty(SynchronizerParameters.PARAM_DRIVER_EXECUTE)));

        params.put(SynchronizerParameters.PARAM_DRIVER_EXECUTE, Boolean.toString(true));
        driver.setProperties(params);

        Assert.assertTrue(Boolean.valueOf(loader.getProperty(SynchronizerParameters.PARAM_DRIVER_EXECUTE)));
    }

}
