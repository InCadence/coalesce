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

package com.incadencecorp.coalesce.framework.iterators.tests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.datamodel.TestRecord;
import com.incadencecorp.coalesce.framework.iterators.CoalescePrunerIterator;
import com.incadencecorp.coalesce.framework.iterators.CoalescePrunerIterator.EMode;

/**
 * These unit test ensures proper operation of the pruner iterator.
 * 
 * @author n78554
 */
public class CoalescePrunerIteratorTest {

    /**
     * This test verifies pruning the fields.
     * 
     * @throws Exception
     */
    @Test
    public void testPrune() throws Exception
    {
        CoalescePrunerIterator pruner = new CoalescePrunerIterator();

        pruner.setMode(EMode.PRUNE);

        TestEntity entity1 = new TestEntity();
        entity1.initialize();

        TestRecord record1 = entity1.addRecord1();

        record1.getStringField().setValue("Hello");
        record1.getBooleanField().setValue(false);
        record1.getIntegerField().setValue(10);

        TestEntity entity2 = new TestEntity();
        entity2.initialize();

        TestRecord record2 = entity2.addRecord1();

        record2.getStringField().setValue("Hello");
        record2.getBooleanField().setValue(false);
        record2.getIntegerField().setValue(10);

        Map<String, List<String>> fieldsToPrune = new HashMap<>();

        String[] fields = new String[] {
                record1.getStringField().getName(), record1.getBooleanField().getName()
        };

        fieldsToPrune.put(TestEntity.getTest1RecordsetName(), Arrays.asList(fields));

        pruner.iterate(fieldsToPrune, entity1, entity2);

        Assert.assertFalse(record1.hasField("string"));
        Assert.assertFalse(record1.hasField("boolean"));
        Assert.assertTrue(record1.hasField("int"));
        
        Assert.assertFalse(record2.hasField("string"));
        Assert.assertFalse(record2.hasField("boolean"));
        Assert.assertTrue(record2.hasField("int"));
    }

    /**
     * This test verifies clearing the fields.
     * 
     * @throws Exception
     */
    @Test
    public void testClear() throws Exception
    {
        CoalescePrunerIterator pruner = new CoalescePrunerIterator();

        pruner.setMode(EMode.CLEAR);

        TestEntity entity = new TestEntity();
        entity.initialize();

        TestRecord record = entity.addRecord1();

        record.getStringField().setValue("Hello");
        record.getBooleanField().setValue(false);
        record.getIntegerField().setValue(10);

        Map<String, List<String>> fieldsToPrune = new HashMap<>();

        String[] fields = new String[] {
                record.getStringField().getName(), record.getBooleanField().getName()
        };

        fieldsToPrune.put(TestEntity.getTest1RecordsetName(), Arrays.asList(fields));

        pruner.iterate(fieldsToPrune, entity);

        Assert.assertNotNull(record.getStringField());
        Assert.assertNotNull(record.getBooleanField());
        Assert.assertNotNull(record.getIntegerField());

        Assert.assertEquals("", record.getStringField().getBaseValue());
        Assert.assertEquals("", record.getBooleanField().getBaseValue());
        Assert.assertEquals(10, record.getIntegerField().getValue(), 0);
    }

    /**
     * This test verifies specifying invalid field does not affect the iterator.
     * 
     * @throws Exception
     */
    @Test
    public void testInvalidField() throws Exception
    {
        CoalescePrunerIterator pruner = new CoalescePrunerIterator();

        pruner.setMode(EMode.PRUNE);

        TestEntity entity = new TestEntity();
        entity.initialize();

        TestRecord record = entity.addRecord1();

        Map<String, List<String>> fieldsToPrune = new HashMap<>();

        String[] fields = new String[] {
                record.getStringField().getName(), record.getBooleanField().getName(), "unknown"
        };

        fieldsToPrune.put(TestEntity.getTest1RecordsetName(), Arrays.asList(fields));
        fieldsToPrune.put("unknown", Arrays.asList(new String[0]));

        pruner.iterate(fieldsToPrune, entity);

        Assert.assertFalse(record.hasField("string"));
        Assert.assertFalse(record.hasField("boolean"));
        Assert.assertTrue(record.hasField("int"));
    }

    /**
     * This test verifies that specifying record sets outside of the entity wont
     * cause it to fail.
     * 
     * @throws Exception
     */
    @Test 
    public void testClone() throws Exception
    {
        CoalescePrunerIterator pruner = new CoalescePrunerIterator();

        pruner.setMode(EMode.CLEAR);

        TestEntity entity = new TestEntity();
        entity.initialize();

        TestRecord record = entity.addRecord1();

        record.getStringField().setValue("Hello");
        record.getBooleanField().setValue(false);
        record.getIntegerField().setValue(10);

        Map<String, List<String>> fieldsToPrune = new HashMap<>();

        String[] fields = new String[] {
                record.getStringField().getName(), record.getBooleanField().getName()
        };

        fieldsToPrune.put(TestEntity.getTest1RecordsetName(), Arrays.asList(fields));

        CoalesceEntity[] results = pruner.iterateClone(fieldsToPrune, entity);

        Assert.assertEquals(1, results.length);

        TestEntity updated = new TestEntity();
        updated.initialize(results[0]);

        TestRecord updatedRecord = new TestRecord(updated.getRecordset1().getRecords().get(0));

        Assert.assertNotNull(updatedRecord.getStringField());
        Assert.assertNotNull(updatedRecord.getBooleanField());
        Assert.assertNotNull(updatedRecord.getIntegerField());

        Assert.assertEquals("", updatedRecord.getStringField().getBaseValue());
        Assert.assertEquals("", updatedRecord.getBooleanField().getBaseValue());
        Assert.assertEquals(10, updatedRecord.getIntegerField().getValue(), 0);

        Assert.assertNotNull(record.getStringField());
        Assert.assertNotNull(record.getBooleanField());
        Assert.assertNotNull(record.getIntegerField());

        Assert.assertEquals("Hello", record.getStringField().getValue());
        Assert.assertEquals(false, record.getBooleanField().getValue());
        Assert.assertEquals(10, record.getIntegerField().getValue(), 0);
    }

    /**
     * This test verifies that cloning wont affect the original.
     * 
     * @throws Exception
     */
    @Test
    public void testUnknownEntity() throws Exception
    {
        CoalescePrunerIterator pruner = new CoalescePrunerIterator();

        pruner.setMode(EMode.CLEAR);

        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();

        CoalesceSection section = CoalesceSection.create(entity, "section");
        CoalesceRecordset rs = CoalesceRecordset.create(section, "set");
        CoalesceFieldDefinition fd = CoalesceFieldDefinition.create(rs, "field1", ECoalesceFieldDataTypes.STRING_TYPE);

        CoalesceRecord record = rs.addNew();

        ((CoalesceStringField) record.getFieldByName(fd.getName())).setValue("Hello");

        Map<String, List<String>> fieldsToPrune = new HashMap<>();

        String[] fields = new String[] {
            fd.getName()
        };

        fieldsToPrune.put(TestEntity.getTest1RecordsetName(), Arrays.asList(fields));

        pruner.iterate(fieldsToPrune, entity);

        Assert.assertNotNull(record.getFieldByName(fd.getName()));
        Assert.assertEquals("Hello", record.getFieldByName(fd.getName()).getBaseValue());
    }

}
