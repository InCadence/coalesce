/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.synchronizer.service.tests;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.datamodel.TestRecord;
import com.incadencecorp.coalesce.framework.persistance.derby.DerbyPersistor;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.synchronizer.api.IPersistorScan;
import com.incadencecorp.coalesce.synchronizer.api.common.SynchronizerParameters;
import com.incadencecorp.coalesce.synchronizer.service.scanners.CQLScanImpl;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.rowset.CachedRowSet;
import java.util.HashMap;
import java.util.Map;

/**
 * These test ensure proper behavior of {@link CQLScanImpl}.
 *
 * @author Derek Clemenzi
 */
public class CQLScanImplTest {

    /**
     * This test ensures that the CQL implementation of the scanner works as intended using a null filter.
     */
    @Test
    public void testIsNullFilter() throws Exception
    {
        // Create Entity
        TestEntity entity = new TestEntity();
        entity.initialize();

        TestRecord record = entity.addRecord1();

        // Create Persistor & Save
        DerbyPersistor persistor = new DerbyPersistor();
        persistor.registerTemplate(CoalesceEntityTemplate.create(entity));
        persistor.saveEntity(false, entity);

        String cql = "\"" + CoalescePropertyFactory.getFieldProperty(record.getStringField()) + "\" is null and " + "\""
                + CoalescePropertyFactory.getEntityKey().getPropertyName() + "\" = '" + entity.getKey() + "'";

        // Create Parameters
        Map<String, String> params = new HashMap<>();
        params.put(SynchronizerParameters.PARAM_SCANNER_CQL, cql);

        // Create Scanner
        IPersistorScan scan = new CQLScanImpl();
        scan.setSource(persistor);
        scan.setProperties(params);

        // Verify Hit
        CachedRowSet results = scan.scan();
        Assert.assertEquals(1, results.size());

        // Set Value
        record.getStringField().setValue("Hello World");
        persistor.saveEntity(false, entity);

        // Verify No Hits
        results = scan.scan();
        Assert.assertEquals(0, results.size());

        entity.markAsDeleted();
        persistor.saveEntity(true, entity);
    }

}
