/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

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

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.api.IExceptionHandler;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.persistance.MockPersister;
import com.incadencecorp.coalesce.framework.persistance.postgres.PostGreSQLPersistorExt;
import com.incadencecorp.coalesce.framework.persistance.postgres.PostGreSQLSettings;
import com.incadencecorp.coalesce.handlers.LoggerExceptionHandlerImpl;
import com.incadencecorp.coalesce.synchronizer.api.IPersistorDriver;
import com.incadencecorp.coalesce.synchronizer.api.IPersistorOperation;
import com.incadencecorp.coalesce.synchronizer.api.IPersistorScan;
import com.incadencecorp.coalesce.synchronizer.api.common.SynchronizerParameters;
import com.incadencecorp.coalesce.synchronizer.service.SynchronizerService;
import com.incadencecorp.coalesce.synchronizer.service.drivers.IntervalDriverImpl;
import com.incadencecorp.coalesce.synchronizer.service.operations.UpdateVersionOperationImpl;
import com.incadencecorp.coalesce.synchronizer.service.scanners.AfterLastModifiedScanImpl;

public class UpdateVersionOperationImplIT {

    @Test
    public void test() throws Exception
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SynchronizerParameters.PARAM_DRIVER_INTERVAL_UNITS, TimeUnit.MINUTES.toString());
        params.put(SynchronizerParameters.PARAM_OP_WINDOW_SIZE, "5");
        params.put(SynchronizerParameters.PARAM_OP_DRYRUN, "true");
        params.put(SynchronizerParameters.PARAM_SCANNER_LAST_SUCCESS, "2017-03-22T19:15:00.000Z");
        params.put(CoalesceParameters.PARAM_DIRECTORY, Paths.get("src", "test", "resources").toUri().toString());
        params.put(CoalesceParameters.PARAM_SUBDIR_LEN, "2");

        PostGreSQLSettings.setDatabaseName("DSS_SNAPSHOT");

        PostGreSQLPersistorExt source = new PostGreSQLPersistorExt();
        ICoalescePersistor target = new MockPersister();

        IExceptionHandler handler = new LoggerExceptionHandlerImpl();
        handler.setProperties(params);

        IPersistorScan scan = new AfterLastModifiedScanImpl();
        scan.setSource(source);
        scan.setProperties(params);

        IPersistorOperation operation = new UpdateVersionOperationImpl();
        operation.setSource(source);
        operation.setTarget(target);
        operation.setHandler(handler);
        operation.setProperties(params);

        IPersistorDriver driver = new IntervalDriverImpl();
        driver.setScan(scan);
        driver.setOperations(operation);
        driver.setProperties(params);
        driver.setup();

        SynchronizerService service = new SynchronizerService();
        service.setDrivers(driver);
        service.start();

        Thread.sleep(200);

        service.stop();

    }
}
