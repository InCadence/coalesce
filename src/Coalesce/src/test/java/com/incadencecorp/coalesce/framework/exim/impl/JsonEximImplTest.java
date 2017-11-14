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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.incadencecorp.coalesce.api.Views;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.datamodel.TestRecord;

/**
 * This unit test covers the {@link JsonEximImpl}
 * 
 * @author n78554
 *
 */
public class JsonEximImplTest {

    private static final String ADDITIONAL_RECORDSET = "Additional Recordset";
    private static final String ADDITIONAL_SECTION = "Additional Section";

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

        JsonEximImpl exim = new JsonEximImpl();

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

        JsonEximImpl exim = new JsonEximImpl();

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

        JsonEximImpl exim = new JsonEximImpl();

        JSONObject json = exim.exportValues(entity, true);

        assertEquals(entity.getVersion(), json.getString("_version"));
        assertEquals(entity.getSource(), json.getString("_source"));
        assertEquals(entity.getClassName(), json.getString("_class"));
        assertTrue(json.has(entity.getName()));

        JSONObject entityJSON = json.getJSONObject(entity.getName());

        // Verify Sections
        assertEquals(1, entityJSON.length());
        assertTrue(entityJSON.has(TestEntity.TESTSECTION));

        JSONObject sectionJSON = entityJSON.getJSONObject(TestEntity.TESTSECTION);

        // Verify Record Sets
        assertEquals(1, sectionJSON.length());
        assertFalse(sectionJSON.has(ADDITIONAL_RECORDSET));
        assertTrue(sectionJSON.has(TestEntity.RECORDSET1));

        JSONArray rs1 = sectionJSON.getJSONArray(TestEntity.RECORDSET1);

        // Verify Fields
        assertEquals(2, rs1.length());

        CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entity);

        entity = new TestEntity();
        entity.initialize(exim.importValues(json, template));

        // Verify Entity
        assertEquals(2, entity.getRecordset1().getCount());

        TestEntity entity4 = new TestEntity();
        entity4.initialize();

        record = entity4.addRecord1();
        record.getLongField().setValue(5L);

        exim.importValues(json, entity4);

        // Verify Clearing Field
        assertTrue(StringHelper.isNullOrEmpty(record.getLongField().getBaseValue()));

    }

    /**
     * This test creates a {@link TestEntity}, exports its values, and attempts
     * to import into an invalid entity type.
     * 
     * @throws Exception
     */
    @Test
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

        JsonEximImpl exim = new JsonEximImpl();

        JSONObject json = exim.exportValues(entity, false);

        CoalesceEntity entity2 = new CoalesceEntity();
        entity2.initialize();

        exim.importValues(json, entity2);

        // Should fail to import the values into the entity because its missing
        // the needed sections / record sets.
        assertEquals(0, entity2.getSectionsAsList().size());
    }

}
