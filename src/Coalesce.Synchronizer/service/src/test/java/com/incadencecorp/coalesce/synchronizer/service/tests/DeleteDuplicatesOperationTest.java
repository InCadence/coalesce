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

import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.datamodel.TestRecord;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.persistance.derby.DerbyPersistor;
import com.incadencecorp.coalesce.search.CoalesceSearchFramework;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.search.resultset.CoalesceResultSet;
import com.incadencecorp.coalesce.synchronizer.service.operations.DeleteDuplicatesOperation;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * These test ensure the {@link DeleteDuplicatesOperation} detects and removes duplicate entities.
 *
 * @author Derek Clemenzi
 */
public class DeleteDuplicatesOperationTest {

    /**
     * Initialize components used within these tests.
     */
    @BeforeClass
    public static void initialize() throws Exception
    {
        ICoalescePersistor persistor = new DerbyPersistor();
        persistor.registerTemplate(CoalesceEntityTemplate.create(new TestEntity()));
    }

    /**
     * This test verifies correct behaviour of the operation.
     */
    @Test
    public void testOperation() throws Exception
    {
        ICoalescePersistor persistor = new DerbyPersistor();

        CoalesceSearchFramework framework = new CoalesceSearchFramework();
        framework.setAuthoritativePersistor(persistor);

        // Create Entities
        String key = UUID.randomUUID().toString();

        TestEntity entity1 = new TestEntity();
        entity1.initialize();
        TestRecord record1 = entity1.addRecord1();
        record1.getStringField().setValue("HelloWorld");
        record1.getIntegerField().setValue(5);
        entity1.setLastModified(JodaDateTimeHelper.nowInUtc());

        TestEntity entity2 = new TestEntity();
        entity2.initialize();
        TestRecord record2 = entity2.addRecord1();
        record2.getStringField().setValue(record1.getStringField().getValue());
        record2.getIntegerField().setValue(record1.getIntegerField().getValue());
        entity2.setLastModified(entity1.getLastModified().plusMinutes(10));

        TestEntity entity3 = new TestEntity();
        entity3.initialize();
        TestRecord record3 = entity3.addRecord1();
        record3.getStringField().setValue(record1.getStringField().getValue());
        record3.getIntegerField().setValue(10);
        entity3.setLastModified(entity1.getLastModified().plusMinutes(5));

        // Save Entities
        framework.saveCoalesceEntity(entity1, entity2, entity3);

        List<String> fields = new ArrayList<>();
        fields.add(CoalescePropertyFactory.getFieldProperty(record1.getStringField()).toString());
        fields.add(CoalescePropertyFactory.getFieldProperty(record1.getIntegerField()).toString());

        // Create Operation
        Map<String, String> params = new HashMap<>();
        params.put(DeleteDuplicatesOperation.PARAM_MARK_AS_DELETED, "true");
        params.put(DeleteDuplicatesOperation.PARAM_UNIQUE_KEY, String.join(",", fields.toArray(new String[fields.size()])));

        DeleteDuplicatesOperation operation = new DeleteDuplicatesOperation();
        operation.setSource(persistor);
        operation.setTarget(persistor);
        operation.setProperties(params);

        // Execute Operation
        List<Object[]> rows = new ArrayList<>();
        rows.add(new Object[] { key, record1.getIntegerField().getValue(), record1.getStringField().getValue() });

        List<String> columns = new ArrayList<>();
        columns.add(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getEntityKey()));

        for (String field : operation.getRequiredColumns())
        {
            columns.add(CoalescePropertyFactory.getColumnName(field));
        }

        CachedRowSet rowset = RowSetProvider.newFactory().createCachedRowSet();
        rowset.populate(new CoalesceResultSet(rows.iterator(), columns.toArray(new String[columns.size()])));

        operation.execute(framework, rowset);

        // Verify
        CoalesceEntity[] entities = framework.getCoalesceEntities(entity1.getKey(), entity2.getKey(), entity3.getKey());

        Assert.assertEquals(ECoalesceObjectStatus.DELETED, entities[0].getStatus());
        Assert.assertEquals(ECoalesceObjectStatus.ACTIVE, entities[1].getStatus());
        Assert.assertEquals(ECoalesceObjectStatus.ACTIVE, entities[2].getStatus());

        // Cleanup
        entity1.markAsDeleted();
        entity2.markAsDeleted();
        entity3.markAsDeleted();

        framework.saveCoalesceEntity(true, entity1, entity2, entity3);
    }

}
