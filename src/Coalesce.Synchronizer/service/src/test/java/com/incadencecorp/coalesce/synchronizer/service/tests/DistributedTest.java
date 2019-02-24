/*-----------------------------------------------------------------------------'
 Copyright 2019 - InCadence Strategic Solutions Inc., All Rights Reserved

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

import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.framework.CoalesceExecutorServiceImpl;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.framework.persistance.derby.DerbyPersistor;
import com.incadencecorp.coalesce.notification.direct.impl.DirectNotifierImpl;
import com.incadencecorp.coalesce.synchronizer.api.IPersistorOperation;
import com.incadencecorp.coalesce.synchronizer.api.IPersistorScan;
import com.incadencecorp.coalesce.synchronizer.api.common.SynchronizerParameters;
import com.incadencecorp.coalesce.synchronizer.service.drivers.DistibutedDriver;
import com.incadencecorp.coalesce.synchronizer.service.operations.DeleteOperationImpl;
import com.incadencecorp.coalesce.synchronizer.service.operations.DistributedOperation;
import com.incadencecorp.coalesce.synchronizer.service.scanners.AfterLastModifiedScanImpl2;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.rowset.CachedRowSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author derek
 */
public class DistributedTest {

    @Test
    public void testDistributed() throws Exception
    {
        CoalesceEntity entity1 = new CoalesceEntity();
        entity1.initialize();

        CoalesceEntity entity2 = new CoalesceEntity();
        entity2.initialize();

        Map<String, String> params = new HashMap<>();
        params.put(SynchronizerParameters.PARAM_SCANNER_LAST_SUCCESS,
                   JodaDateTimeHelper.toXmlDateTimeUTC(JodaDateTimeHelper.nowInUtc().minusDays(2)));
        params.put(DeleteOperationImpl.PARAM_MARK_AS_DELETED, Boolean.TRUE.toString());

        DerbyPersistor derby = new DerbyPersistor();

        derby.saveEntity(false, entity1, entity2);

        DirectNotifierImpl notifier = new DirectNotifierImpl();

        IPersistorScan scan = new AfterLastModifiedScanImpl2();
        scan.setSource(derby);
        scan.setProperties(params);

        DistributedOperation operation = new DistributedOperation();
        operation.setProperties(Collections.singletonMap(SynchronizerParameters.PARAM_OP_TOPIC, "unittest"));
        operation.setTarget(derby);
        operation.setNotifier(notifier);

        IPersistorOperation delete = new DeleteOperationImpl();
        delete.setSource(derby);
        delete.setTarget(derby);
        delete.setProperties(params);

        DistibutedDriver driver = new DistibutedDriver();
        driver.setProperties(Collections.singletonMap(SynchronizerParameters.PARAM_OP_TOPIC, "unittest"));
        driver.setOperations(delete);
        driver.setSubscriber(notifier);
        driver.start();

        Assert.assertEquals(ECoalesceObjectStatus.ACTIVE, derby.getEntity(entity1.getKey())[0].getStatus());
        Assert.assertEquals(ECoalesceObjectStatus.ACTIVE, derby.getEntity(entity2.getKey())[0].getStatus());

        CoalesceExecutorServiceImpl service = new CoalesceExecutorServiceImpl();

        CachedRowSet rowset = scan.scan();
        operation.execute(service, rowset);

        Assert.assertEquals(ECoalesceObjectStatus.DELETED, derby.getEntity(entity1.getKey())[0].getStatus());
        Assert.assertEquals(ECoalesceObjectStatus.DELETED, derby.getEntity(entity2.getKey())[0].getStatus());

        delete.setProperties(Collections.singletonMap(DeleteOperationImpl.PARAM_MARK_AS_DELETED,
                                                         Boolean.FALSE.toString()));

        operation.execute(service, rowset);

        Assert.assertEquals(0, derby.getEntity(entity1.getKey()).length);
        Assert.assertEquals(0, derby.getEntity(entity2.getKey()).length);
    }
}
