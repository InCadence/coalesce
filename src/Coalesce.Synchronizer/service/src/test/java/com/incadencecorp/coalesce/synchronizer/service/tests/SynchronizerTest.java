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

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.api.IExceptionHandler;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.persistance.MockPersister;
import com.incadencecorp.coalesce.framework.persistance.derby.DerbyPersistor;
import com.incadencecorp.coalesce.handlers.FileExceptionHandlerImpl;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.synchronizer.api.IPersistorDriver;
import com.incadencecorp.coalesce.synchronizer.api.IPersistorOperation;
import com.incadencecorp.coalesce.synchronizer.api.IPersistorScan;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractOperation;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractOperationTask;
import com.incadencecorp.coalesce.synchronizer.api.common.SynchronizerParameters;
import com.incadencecorp.coalesce.synchronizer.service.SynchronizerService;
import com.incadencecorp.coalesce.synchronizer.service.drivers.IntervalDriverImpl;
import com.incadencecorp.coalesce.synchronizer.service.operations.CopyOperationImpl;
import com.incadencecorp.coalesce.synchronizer.service.operations.ExceptionOperationImpl;
import com.incadencecorp.coalesce.synchronizer.service.scanners.AfterLastModifiedScanImpl;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.rowset.CachedRowSet;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSetMetaData;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * These test ensure proper operation of the synchronizer service.
 *
 * @author n78554
 */
public class SynchronizerTest {

    /**
     * This test ensures that entities are copied from the source to target
     * persistor.
     */
    @Test
    public void testSynchronizer() throws Exception
    {

        Map<String, String> params;

        params = new HashMap<>();
        params.put(SynchronizerParameters.PARAM_DRIVER_INTERVAL_UNITS, TimeUnit.MINUTES.toString());
        params.put(SynchronizerParameters.PARAM_OP_WINDOW_SIZE, "1");
        params.put(SynchronizerParameters.PARAM_SCANNER_LAST_SUCCESS,
                   JodaDateTimeHelper.toXmlDateTimeUTC(JodaDateTimeHelper.nowInUtc().minusDays(2)));

        DerbyPersistor source = new DerbyPersistor();
        MockPersister target = new MockPersister();

        TestEntity entity = new TestEntity();
        entity.initialize();

        TestEntity entity2 = new TestEntity();
        entity2.initialize();

        TestEntity entity3 = new TestEntity();
        entity3.initialize();

        source.saveEntity(false, entity, entity2, entity3);

        IPersistorScan scan = new AfterLastModifiedScanImpl();
        scan.setSource(source);
        scan.setProperties(params);

        IPersistorOperation operation = new CopyOperationImpl();
        operation.setSource(source);
        operation.setTarget(target);

        IPersistorDriver driver = new IntervalDriverImpl();
        driver.setScan(scan);
        driver.setOperations(operation);
        driver.setProperties(params);
        driver.setup();

        Assert.assertEquals(0, target.getEntity(entity.getKey()).length);
        Assert.assertEquals(0, target.getEntity(entity2.getKey()).length);
        Assert.assertEquals(0, target.getEntity(entity3.getKey()).length);

        SynchronizerService service = new SynchronizerService();
        service.setDrivers(driver);
        service.start();

        Assert.assertEquals(1, target.getEntity(entity.getKey()).length);
        Assert.assertEquals(1, target.getEntity(entity2.getKey()).length);
        Assert.assertEquals(1, target.getEntity(entity3.getKey()).length);

        service.stop();
    }

    @Test
    public void testSynchronizerFailure() throws Exception
    {
        Map<String, String> params;

        params = new HashMap<>();
        params.put(SynchronizerParameters.PARAM_DRIVER_INTERVAL_UNITS, TimeUnit.MINUTES.toString());
        params.put(SynchronizerParameters.PARAM_OP_WINDOW_SIZE, "1");
        params.put(CoalesceParameters.PARAM_DIRECTORY, Paths.get("src", "test", "resources").toUri().toString());

        DerbyPersistor scanSource = new DerbyPersistor();
        MockPersister source = new MockPersister();
        MockPersister target = new MockPersister();

        TestEntity entity = new TestEntity();
        entity.initialize();

        TestEntity entity2 = new TestEntity();
        entity2.initialize();

        TestEntity entity3 = new TestEntity();
        entity3.initialize();

        scanSource.saveEntity(false, entity, entity2, entity3);
        source.saveEntity(false, entity, entity2);

        IPersistorScan scan = new AfterLastModifiedScanImpl();
        scan.setSource(scanSource);

        IExceptionHandler handler = new FileExceptionHandlerImpl();
        handler.setProperties(params);

        IPersistorOperation operation = new CopyOperationImpl();
        operation.setProperties(params);
        operation.setSource(source);
        operation.setTarget(target);
        operation.setHandler(handler);

        IPersistorDriver driver = new IntervalDriverImpl();
        driver.setScan(scan);
        driver.setOperations(operation);
        driver.setProperties(params);
        driver.setup();

        Assert.assertEquals(0, target.getEntity(entity.getKey()).length);
        Assert.assertEquals(0, target.getEntity(entity2.getKey()).length);
        Assert.assertEquals(0, target.getEntity(entity3.getKey()).length);

        SynchronizerService service = new SynchronizerService();
        service.setDrivers(driver);
        service.start();

        Assert.assertEquals(1, target.getEntity(entity.getKey()).length);
        Assert.assertEquals(1, target.getEntity(entity2.getKey()).length);
        Assert.assertEquals(0, target.getEntity(entity3.getKey()).length);

        Path path = Paths.get("src", "test", "resources", operation.getName(), entity3.getKey());

        Assert.assertTrue(Files.exists(path));

        service.stop();

        Files.delete(path);

    }

    @Test
    public void testExceptionOperationImpl() throws Exception
    {

        Map<String, String> params;

        params = new HashMap<>();
        params.put(SynchronizerParameters.PARAM_DRIVER_INTERVAL_UNITS, TimeUnit.MINUTES.toString());
        params.put(SynchronizerParameters.PARAM_OP_WINDOW_SIZE, "1");
        params.put(CoalesceParameters.PARAM_DIRECTORY, "src/test/resources");

        DerbyPersistor source = new DerbyPersistor();
        MockPersister target = new MockPersister();

        TestEntity entity = new TestEntity();
        entity.initialize();

        source.saveEntity(false, entity);

        IPersistorScan scan = new AfterLastModifiedScanImpl();
        scan.setSource(source);

        IExceptionHandler handler = new FileExceptionHandlerImpl();
        handler.setProperties(params);

        IPersistorOperation operation = new ExceptionOperationImpl();
        operation.setSource(source);
        operation.setTarget(target);
        operation.setHandler(handler);

        IPersistorDriver driver = new IntervalDriverImpl();
        driver.setScan(scan);
        driver.setOperations(operation);
        driver.setProperties(params);
        driver.setup();

        Assert.assertEquals(0, target.getEntity(entity.getKey()).length);

        SynchronizerService service = new SynchronizerService();
        service.setDrivers(driver);
        service.start();

        Assert.assertEquals(0, target.getEntity(entity.getKey()).length);

        Path path = Paths.get("src", "test", "resources", operation.getName(), entity.getKey());

        Assert.assertTrue(Files.exists(path));

        service.stop();

        Files.delete(path);

    }

    /**
     * This test ensures that the drivers compile the required columns
     * correctly.
     */
    @Test
    public void testRequiredColumns() throws Exception
    {
        IPersistorScan scan = new AfterLastModifiedScanImpl();
        scan.setSource(new DerbyPersistor());

        IPersistorOperation op1 = new MockOperation(CoalescePropertyFactory.getName().toString(),
                                                    CoalescePropertyFactory.getSource().toString());

        IPersistorOperation op2 = new MockOperation(CoalescePropertyFactory.getSource().toString(),
                                                    CoalescePropertyFactory.getDateCreated().toString());

        IPersistorDriver driver = new IntervalDriverImpl();
        driver.setScan(scan);
        driver.setOperations(op1, op2);
        driver.setup();

        CachedRowSet results = scan.scan();

        ResultSetMetaData metadata = results.getMetaData();

        Assert.assertEquals(4, metadata.getColumnCount());
        Assert.assertTrue(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getEntityKey()).equalsIgnoreCase(metadata.getColumnLabel(1)));
        Assert.assertTrue(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getName()).equalsIgnoreCase(metadata.getColumnLabel(2)));
        Assert.assertTrue(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getDateCreated()).equalsIgnoreCase(metadata.getColumnLabel(3)));
        Assert.assertTrue(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getSource()).equalsIgnoreCase(metadata.getColumnLabel(4)));
    }

    private class MockOperation extends AbstractOperation<AbstractOperationTask> {

        private Set<String> columns = new HashSet<>();

        private MockOperation(String... columns)
        {
            Collections.addAll(this.columns, columns);
        }

        @Override
        protected AbstractOperationTask createTask()
        {
            return new AbstractOperationTask() {

                @Override
                protected Boolean doWork(String[] keys, CachedRowSet rowset) throws CoalescePersistorException
                {
                    // Do Nothing
                    return true;
                }

            };
        }

        @Override
        public Set<String> getAdditionalRequiredColumns()
        {
            return columns;
        }

    }
}
