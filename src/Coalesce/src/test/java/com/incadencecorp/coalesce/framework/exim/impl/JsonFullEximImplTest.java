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

package com.incadencecorp.coalesce.framework.exim.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import com.incadencecorp.coalesce.api.Views;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.compareables.CoalesceFieldComparator;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.datamodel.TestRecord;

/**
 * This unit test covers the {@link JsonFullEximImpl}
 * 
 * @author n78554
 *
 */
public class JsonFullEximImplTest {

    private static final String ADDITIONAL_RECORDSET = "Additional Recordset";
    private static final String ADDITIONAL_SECTION = "Additional Section";

    /**
     * This is an example of how to use the ObjectMapper to perform the same
     * function as JsonFullEximImpl
     * 
     * @throws Exception
     */
    @Test
    public void eximTest() throws Exception
    {
        JsonFullEximImpl exim = new JsonFullEximImpl();
        exim.setView(Views.Entity.class);

        TestEntity entity = new TestEntity();
        entity.initialize();
        TestRecord record = entity.addRecord1();
        record.getBooleanField().setValue(true);
        record.getStringField().setValue("AA");
        record.getStringListField().setValue(new String[] {
                                                            "AA","BB"
        });

        CoalesceEntity entity1 = new CoalesceEntity();
        entity1.initialize(entity);

        JSONObject result = exim.exportValues(entity, true);

        TestEntity entity2 = new TestEntity();
        entity2.initialize();

        exim.importValues(result, entity2);

        Assert.assertEquals(entity1.getKey(), entity2.getKey());

        TestRecord record2 = new TestRecord(entity2.getRecordset1().getAllRecords().get(0));
        
        Assert.assertEquals(record.getBooleanField().getBaseValue(),
                            record2.getBooleanField().getBaseValue());

        Assert.assertEquals(record.getStringField().getBaseValue(),
                            record2.getStringField().getBaseValue());

        Assert.assertEquals(record.getStringListField().getBaseValue(),
                            record2.getStringListField().getBaseValue());
    }

    /**
     * This test passes in an invalid JSON Object. Arrays are only used for
     * Record Sets, passing them in at any other level will throw a runtime
     * exception.
     * 
     * @throws Exception
     */
    @Test(expected = RuntimeException.class)
    public void invalidJSONTest() throws Exception
    {

        JsonFullEximImpl exim = new JsonFullEximImpl();

        TestEntity entity = new TestEntity();
        entity.initialize();

        // Create JSON Object w/ an Array as a Section
        JSONObject json = new JSONObject();
        json.put(entity.getName(), new JSONArray());

        // Should Throw Exception
        exim.importValues(json, entity);

    }

    /**
     * This test creates an object, exports to JSON, and imports it into another
     * object with an additional record set.
     * 
     * @throws Exception
     */
    @Test
    public void importIntoExpandedEntityTests() throws Exception
    {

        JsonFullEximImpl exim = new JsonFullEximImpl();

        // Create Entity
        TestEntity entity = new TestEntity();
        entity.initialize();

        // Create Record
        TestRecord record = entity.addRecord1();
        record.getBooleanField().setValue(false);
        record.getStringField().setValue("Hello World");

        // Export
        JSONObject json = exim.exportValues(entity, true);

        // Create New Entity
        TestEntity entity2 = new TestEntity();
        entity2.initialize();

        // Add Additional Record
        CoalesceSection section = CoalesceSection.create(entity2, ADDITIONAL_SECTION);
        CoalesceRecordset recordset = CoalesceRecordset.create(section, ADDITIONAL_RECORDSET);
        CoalesceFieldDefinition.create(recordset, "test", ECoalesceFieldDataTypes.BOOLEAN_TYPE);

        // Import Values
        exim.importValues(json, entity2);

        // Verify Import
        assertEquals(1, entity2.getRecordset1().getCount());

        TestRecord record2 = new TestRecord(entity2.getRecordset1().getItem(0));

        System.out.println(record2.getParent().toXml());

        assertEquals(record.getBooleanField().getValue(), record2.getBooleanField().getValue());
        assertEquals(record.getStringField().getValue(), record2.getStringField().getValue());

        assertEquals(0, recordset.getCount());

    }

    /**
     * This test ensures that you can export and import values.
     * 
     * @throws Exception
     */
    @Test
    public void importExportTest() throws Exception
    {
        TestEntity entity = new TestEntity();
        entity.initialize();

        // Add Additional Record
        CoalesceSection section = entity.getCoalesceSectionForNamePath(TestEntity.NAME, TestEntity.TESTSECTION);
        CoalesceRecordset recordset = CoalesceRecordset.create(section, ADDITIONAL_RECORDSET);
        CoalesceFieldDefinition.create(recordset, "test", ECoalesceFieldDataTypes.BOOLEAN_TYPE);

        TestRecord record;

        // Create Record 1
        record = entity.addRecord1();
        record.getBooleanField().setValue(false);
        record.getStringField().setValue("Hello World");

        // Create Record 2
        record = entity.addRecord1();
        record.getIntegerField().setValue(1);

        // Add Empty Record
        entity.addRecord1();

        JsonFullEximImpl exim = new JsonFullEximImpl();

        JSONObject json = exim.exportValues(entity, true);

        // Verify Metadata
        assertEquals(entity.getVersion(), json.getString("version"));
        assertEquals(entity.getSource(), json.getString("source"));
        assertEquals(entity.getClassName(), json.getString("className"));
        assertEquals(entity.getName(), json.getString("name"));
        assertEquals(entity.getName(), json.getString("namePath"));
        assertEquals(entity.getKey(), json.getString("key"));

        // Verify Section
        assertTrue(json.has("sectionsAsList"));

        JSONArray sections = json.getJSONArray("sectionsAsList");
        assertEquals(1, sections.length());

        JSONObject sectionJSON = sections.getJSONObject(0);
        assertEquals(TestEntity.TESTSECTION, sectionJSON.getString("name"));

        assertTrue(sectionJSON.has("sectionsAsList"));
        assertTrue(sectionJSON.has("recordsetsAsList"));
        assertEquals(section.getKey(), sectionJSON.getString("key"));

        // Verify Record Sets
        JSONArray recordsets = sectionJSON.getJSONArray("recordsetsAsList");

        assertEquals(2, recordsets.length());

        JSONObject recordset1 = recordsets.getJSONObject(0);
        JSONObject recordset2 = recordsets.getJSONObject(1);

        Assert.assertEquals(entity.getRecordset1().getKey(), recordset1.getString("key"));
        Assert.assertEquals(entity.getRecordset1().getName(), recordset1.getString("name"));

        Assert.assertEquals(recordset.getKey(), recordset2.getString("key"));
        Assert.assertEquals(recordset.getName(), recordset2.getString("name"));

        Assert.assertTrue(recordset1.has("allRecords"));
        Assert.assertEquals(3, recordset1.getJSONArray("allRecords").length());

        CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entity);

        entity = new TestEntity();
        entity.initialize(exim.importValues(json, template));

        // Verify Entity
        assertEquals(3, entity.getRecordset1().getCount());

        TestEntity entity4 = new TestEntity();
        entity4.initialize();

        CoalesceRecordset.create(entity4.getCoalesceSectionForNamePath(TestEntity.NAME, TestEntity.TESTSECTION),
                                 ADDITIONAL_RECORDSET);
    }

    /**
     * This test creates a {@link TestEntity}, exports its values, and attempts
     * to import into an invalid entity type.
     * 
     * @throws Exception
     */
    @Test(expected = CoalesceException.class)
    public void invalidObjectTest() throws Exception
    {

        TestEntity entity = new TestEntity();
        entity.initialize();

        TestRecord record;

        // Create Record 1
        record = entity.addRecord1();
        record.getBooleanField().setValue(false);
        record.getStringField().setValue("Hello World");

        // Create Record 2
        record = entity.addRecord1();
        record.getIntegerField().setValue(1);

        JsonFullEximImpl exim = new JsonFullEximImpl();

        JSONObject json = exim.exportValues(entity, false);

        CoalesceEntity entity2 = new CoalesceEntity();
        entity2.initialize();

        exim.importValues(json, entity2);
    }

}
