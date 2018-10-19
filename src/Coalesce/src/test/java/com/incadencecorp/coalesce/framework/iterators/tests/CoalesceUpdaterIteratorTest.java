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
import org.junit.BeforeClass;
import org.junit.Test;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.EnumerationProviderUtil;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEnumerationField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEnumerationListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIntegerField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIntegerListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringListField;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.datamodel.TestRecord;
import com.incadencecorp.coalesce.framework.enumerationprovider.impl.CodeEnumerationProvider;
import com.incadencecorp.coalesce.framework.iterators.CoalesceUpdaterIterator;

public class CoalesceUpdaterIteratorTest {

    private static final String NEW_SECTION = "new";
    private static final String NEW_RECORDSET = "new";
    private static final String NEW_FIELD = "new";

    /**
     * Sets up the enumerations that will be used during these tests.
     */
    @BeforeClass
    public static void initalizeEnumerations()
    {
        TestEntity entity = new TestEntity();
        entity.initialize();

        TestRecord record = entity.addRecord1();

        List<String> values = Arrays.asList("Hello", "World");

        Map<String, List<String>> enumerations = new HashMap<>();
        enumerations.put(EnumerationProviderUtil.lookupEnumeration(record.getIntegerField()), values);
        enumerations.put(EnumerationProviderUtil.lookupEnumeration(record.getIntegerListField()), values);
        enumerations.put(EnumerationProviderUtil.lookupEnumeration(record.getStringField()), values);
        enumerations.put(EnumerationProviderUtil.lookupEnumeration(record.getStringListField()), values);
        enumerations.put(EnumerationProviderUtil.lookupEnumeration(record.getEnumerationField()), values);
        enumerations.put(EnumerationProviderUtil.lookupEnumeration(record.getEnumerationListField()), values);

        EnumerationProviderUtil.addEnumerationProviders(new CodeEnumerationProvider(enumerations));
    }

    @Test
    public void testAddingFields() throws Exception
    {
        TestEntity entity = new TestEntity();
        entity.initialize();
        TestRecord record = entity.addRecord1();

        record.getBooleanField().setValue(false);

        TestEntity template = new TestEntity();
        template.initialize();
        CoalesceSection section = CoalesceSection.create(CoalesceSection.create(template, NEW_SECTION), NEW_SECTION);
        CoalesceRecordset recordset = CoalesceRecordset.create(section, NEW_RECORDSET);
        CoalesceFieldDefinition.create(recordset, NEW_FIELD, ECoalesceFieldDataTypes.STRING_TYPE);

        CoalesceFieldDefinition.create(template.getRecordset1(), NEW_FIELD, ECoalesceFieldDataTypes.STRING_TYPE);

        Assert.assertNull(record.getFieldByName(NEW_FIELD));
        Assert.assertNull(entity.getCoalesceSectionForNamePath(section.getNamePath()));
        Assert.assertNull(entity.getCoalesceRecordsetForNamePath(recordset.getNamePath()));

        CoalesceUpdaterIterator iterator = new CoalesceUpdaterIterator(template.createNewEntityTemplate());
        TestEntity updated = new TestEntity();
        updated.initialize(iterator.iterate(entity));

        Assert.assertNotNull(updated.getRecordset1().getRecords().get(0).getFieldByName(NEW_FIELD));
        Assert.assertNotNull(updated.getCoalesceSectionForNamePath(section.getNamePath()));
        Assert.assertNotNull(updated.getCoalesceRecordsetForNamePath(recordset.getNamePath()));

    }

    @Test
    public void testUpdatingStringToEnum() throws Exception
    {
        TestEntity entity = new TestEntity();
        entity.initialize();
        TestRecord record = entity.addRecord1();

        record.getStringField().setValue("Hello");
        record.getStringListField().setValue(new String[] {
                "Hello", "World"
        });

        TestEntity template = new TestEntity();
        template.initialize();
        template.getRecordset1().getFieldDefinition("string").setDataType(ECoalesceFieldDataTypes.ENUMERATION_TYPE);
        template.getRecordset1().getFieldDefinition("stringlist").setDataType(ECoalesceFieldDataTypes.ENUMERATION_LIST_TYPE);

        Assert.assertEquals(ECoalesceFieldDataTypes.STRING_TYPE, record.getStringField().getDataType());
        Assert.assertEquals(ECoalesceFieldDataTypes.STRING_LIST_TYPE, record.getStringListField().getDataType());
        Assert.assertEquals(ECoalesceFieldDataTypes.STRING_TYPE,
                            entity.getRecordset1().getFieldDefinition("string").getDataType());
        Assert.assertEquals(ECoalesceFieldDataTypes.STRING_LIST_TYPE,
                            entity.getRecordset1().getFieldDefinition("stringlist").getDataType());
        Assert.assertEquals("Hello", record.getStringField().getBaseValue());
        Assert.assertEquals("Hello,World", record.getStringListField().getBaseValue());

        Assert.assertTrue(record.getFieldByName("string") instanceof CoalesceStringField);
        Assert.assertTrue(record.getFieldByName("stringlist") instanceof CoalesceStringListField);

        CoalesceUpdaterIterator iterator = new CoalesceUpdaterIterator(template.createNewEntityTemplate());
        TestEntity updated = new TestEntity();
        updated.initialize(iterator.iterate(entity));

        record = new TestRecord(updated.getRecordset1().getRecords().get(0));

        Assert.assertEquals(ECoalesceFieldDataTypes.ENUMERATION_TYPE,
                            updated.getRecordset1().getFieldDefinition("string").getDataType());
        Assert.assertEquals(ECoalesceFieldDataTypes.ENUMERATION_LIST_TYPE,
                            updated.getRecordset1().getFieldDefinition("stringlist").getDataType());
        Assert.assertEquals("0", record.getFieldByName("string").getBaseValue());
        Assert.assertEquals("0,1", record.getFieldByName("stringlist").getBaseValue());

        Assert.assertTrue(record.getFieldByName("string") instanceof CoalesceEnumerationField);
        Assert.assertTrue(record.getFieldByName("stringlist") instanceof CoalesceEnumerationListField);
    }

    @Test
    public void testUpdatingEnumToString() throws Exception
    {
        TestEntity entity = new TestEntity();
        entity.initialize();
        TestRecord record = entity.addRecord1();

        record.getEnumerationField().setValue(0);
        record.getIntegerField().setValue(0);

        TestEntity template = new TestEntity();
        template.initialize();
        template.getRecordset1().getFieldDefinition("enum").setDataType(ECoalesceFieldDataTypes.STRING_TYPE);
        template.getRecordset1().getFieldDefinition("int").setDataType(ECoalesceFieldDataTypes.STRING_TYPE);

        Assert.assertEquals(ECoalesceFieldDataTypes.ENUMERATION_TYPE, record.getEnumerationField().getDataType());
        Assert.assertEquals(ECoalesceFieldDataTypes.INTEGER_TYPE, record.getIntegerField().getDataType());
        Assert.assertEquals(ECoalesceFieldDataTypes.ENUMERATION_TYPE,
                            entity.getRecordset1().getFieldDefinition("enum").getDataType());
        Assert.assertEquals(ECoalesceFieldDataTypes.INTEGER_TYPE,
                            entity.getRecordset1().getFieldDefinition("int").getDataType());
        Assert.assertEquals("0", record.getEnumerationField().getBaseValue());
        Assert.assertEquals("0", record.getIntegerField().getBaseValue());

        Assert.assertTrue(record.getFieldByName("enum") instanceof CoalesceEnumerationField);
        Assert.assertTrue(record.getFieldByName("int") instanceof CoalesceIntegerField);

        CoalesceUpdaterIterator iterator = new CoalesceUpdaterIterator(template.createNewEntityTemplate());
        TestEntity updated = new TestEntity();
        updated.initialize(iterator.iterate(entity));

        record = new TestRecord(updated.getRecordset1().getRecords().get(0));

        Assert.assertEquals(ECoalesceFieldDataTypes.STRING_TYPE,
                            updated.getRecordset1().getFieldDefinition("enum").getDataType());
        Assert.assertEquals(ECoalesceFieldDataTypes.STRING_TYPE,
                            updated.getRecordset1().getFieldDefinition("int").getDataType());
        Assert.assertEquals("Hello", record.getFieldByName("enum").getBaseValue());
        Assert.assertEquals("0", record.getFieldByName("int").getBaseValue());

        Assert.assertTrue(record.getFieldByName("enum") instanceof CoalesceStringField);
        Assert.assertTrue(record.getFieldByName("int") instanceof CoalesceStringField);
    }

    @Test
    public void testUpdatingToFromStringList() throws Exception
    {
        TestEntity entity = new TestEntity();
        entity.initialize();
        TestRecord record = entity.addRecord1();

        record.getStringField().setValue("Hello");
        record.getStringListField().setValue(new String[] {
            "World"
        });

        TestEntity template = new TestEntity();
        template.initialize();
        template.getRecordset1().getFieldDefinition("string").setDataType(ECoalesceFieldDataTypes.STRING_LIST_TYPE);
        template.getRecordset1().getFieldDefinition("stringlist").setDataType(ECoalesceFieldDataTypes.STRING_TYPE);

        Assert.assertEquals(ECoalesceFieldDataTypes.STRING_TYPE, record.getStringField().getDataType());
        Assert.assertEquals(ECoalesceFieldDataTypes.STRING_LIST_TYPE, record.getStringListField().getDataType());
        Assert.assertEquals(ECoalesceFieldDataTypes.STRING_TYPE,
                            entity.getRecordset1().getFieldDefinition("string").getDataType());
        Assert.assertEquals(ECoalesceFieldDataTypes.STRING_LIST_TYPE,
                            entity.getRecordset1().getFieldDefinition("stringlist").getDataType());
        Assert.assertEquals("Hello", record.getStringField().getBaseValue());
        Assert.assertEquals("World", record.getStringListField().getBaseValue());

        Assert.assertTrue(record.getFieldByName("string") instanceof CoalesceStringField);
        Assert.assertTrue(record.getFieldByName("stringlist") instanceof CoalesceStringListField);

        CoalesceUpdaterIterator iterator = new CoalesceUpdaterIterator(template.createNewEntityTemplate());
        TestEntity updated = new TestEntity();
        updated.initialize(iterator.iterate(entity));

        record = new TestRecord(updated.getRecordset1().getRecords().get(0));

        Assert.assertEquals(ECoalesceFieldDataTypes.STRING_LIST_TYPE,
                            updated.getRecordset1().getFieldDefinition("string").getDataType());
        Assert.assertEquals(ECoalesceFieldDataTypes.STRING_TYPE,
                            updated.getRecordset1().getFieldDefinition("stringlist").getDataType());
        Assert.assertEquals("Hello", record.getFieldByName("string").getBaseValue());
        Assert.assertEquals("World", record.getFieldByName("stringlist").getBaseValue());

        Assert.assertTrue(record.getFieldByName("string") instanceof CoalesceStringListField);
        Assert.assertTrue(record.getFieldByName("stringlist") instanceof CoalesceStringField);
    }

    @Test
    public void testUpdatingIntegerToEnum() throws Exception
    {
        TestEntity entity = new TestEntity();
        entity.initialize();
        TestRecord record = entity.addRecord1();

        record.getIntegerField().setValue(0);
        record.getIntegerListField().setValue(new int[] {
                0, 1
        });

        TestEntity template = new TestEntity();
        template.initialize();
        template.getRecordset1().getFieldDefinition("int").setDataType(ECoalesceFieldDataTypes.ENUMERATION_TYPE);
        template.getRecordset1().getFieldDefinition("intlist").setDataType(ECoalesceFieldDataTypes.ENUMERATION_LIST_TYPE);

        Assert.assertEquals(ECoalesceFieldDataTypes.INTEGER_TYPE, record.getIntegerField().getDataType());
        Assert.assertEquals(ECoalesceFieldDataTypes.INTEGER_LIST_TYPE, record.getIntegerListField().getDataType());
        Assert.assertEquals(ECoalesceFieldDataTypes.INTEGER_TYPE,
                            entity.getRecordset1().getFieldDefinition("int").getDataType());
        Assert.assertEquals(ECoalesceFieldDataTypes.INTEGER_LIST_TYPE,
                            entity.getRecordset1().getFieldDefinition("intlist").getDataType());
        Assert.assertEquals("0", record.getIntegerField().getBaseValue());
        Assert.assertEquals("0,1", record.getIntegerListField().getBaseValue());

        Assert.assertTrue(record.getFieldByName("int") instanceof CoalesceIntegerField);
        Assert.assertTrue(record.getFieldByName("intlist") instanceof CoalesceIntegerListField);

        CoalesceUpdaterIterator iterator = new CoalesceUpdaterIterator(template.createNewEntityTemplate());
        TestEntity updated = new TestEntity();
        updated.initialize(iterator.iterate(entity));

        record = new TestRecord(updated.getRecordset1().getRecords().get(0));

        Assert.assertEquals(ECoalesceFieldDataTypes.ENUMERATION_TYPE,
                            updated.getRecordset1().getFieldDefinition("int").getDataType());
        Assert.assertEquals(ECoalesceFieldDataTypes.ENUMERATION_LIST_TYPE,
                            updated.getRecordset1().getFieldDefinition("intlist").getDataType());
        Assert.assertEquals("0", record.getFieldByName("int").getBaseValue());
        Assert.assertEquals("0,1", record.getFieldByName("intlist").getBaseValue());

        Assert.assertTrue(record.getFieldByName("int") instanceof CoalesceEnumerationField);
        Assert.assertTrue(record.getFieldByName("intlist") instanceof CoalesceEnumerationListField);
    }

    @Test
    public void testUpdatingInvalidTypeFields() throws Exception
    {
        TestEntity entity = new TestEntity();
        entity.initialize();

        TestRecord record = entity.addRecord1();
        record.getIntegerField().setValue(0);

        TestEntity template = new TestEntity();
        template.initialize();
        template.getRecordset1().getFieldDefinition("int").setDataType(ECoalesceFieldDataTypes.BOOLEAN_TYPE);

        Assert.assertEquals(ECoalesceFieldDataTypes.INTEGER_TYPE, record.getIntegerField().getDataType());
        Assert.assertEquals(ECoalesceFieldDataTypes.INTEGER_TYPE,
                            entity.getRecordset1().getFieldDefinition("int").getDataType());

        CoalesceUpdaterIterator iterator = new CoalesceUpdaterIterator(template.createNewEntityTemplate());
        try
        {
            iterator.iterate(entity);
        }
        catch (CoalesceException e)
        {
            Assert.assertEquals(String.format(CoalesceErrors.INVALID_DATA_TYPE,
                                              record.getIntegerField().getName(),
                                              record.getIntegerField().getDataType(),
                                              entity.getKey(),
                                              ECoalesceFieldDataTypes.BOOLEAN_TYPE), e.getMessage());
        }

    }

    @Test
    public void testUpdatingBooleanToEnum() throws Exception
    {
        TestEntity entity = new TestEntity();
        entity.initialize();
        TestRecord record = entity.addRecord1();

        record.getIntegerField().setValue(0);
        record.getIntegerListField().setValue(new int[] {
                0, 1
        });

        TestEntity template = new TestEntity();
        template.initialize();
        template.getRecordset1().getFieldDefinition("boolean").setDataType(ECoalesceFieldDataTypes.ENUMERATION_TYPE);

        CoalesceUpdaterIterator iterator = new CoalesceUpdaterIterator(template.createNewEntityTemplate());

        try
        {
            iterator.iterate(entity);
        }
        catch (CoalesceException e)
        {
            Assert.assertEquals(String.format(CoalesceErrors.INVALID_DATA_TYPE,
                                              record.getBooleanField().getName(),
                                              record.getBooleanField().getDataType(),
                                              entity.getKey(),
                                              ECoalesceFieldDataTypes.ENUMERATION_TYPE), e.getMessage());
        }
    }

    @Test
    public void testUpdatingInvalidPositionFields() throws Exception
    {
        TestEntity entity = new TestEntity();
        entity.initialize();

        TestRecord record = entity.addRecord1();
        record.getIntegerField().setValue(10);

        TestEntity template = new TestEntity();
        template.initialize();
        template.getRecordset1().getFieldDefinition("int").setDataType(ECoalesceFieldDataTypes.ENUMERATION_TYPE);

        CoalesceUpdaterIterator iterator = new CoalesceUpdaterIterator(template.createNewEntityTemplate());

        try
        {
            iterator.iterate(entity);
        }
        catch (CoalesceException e)
        {
            Assert.assertEquals(String.format(CoalesceErrors.INVALID_ENUMERATION_POSITION,
                                              record.getIntegerField().getBaseValue(),
                                              record.getParent().getName() + "." + record.getIntegerField().getName()),
                                e.getMessage());
        }
    }

    @Test
    public void testUpdatingInvalidListPositionFields() throws Exception
    {
        TestEntity entity = new TestEntity();
        entity.initialize();

        TestRecord record = entity.addRecord1();
        record.getIntegerListField().setValue(new int[] {
            10
        });

        TestEntity template = new TestEntity();
        template.initialize();
        template.getRecordset1().getFieldDefinition(record.getIntegerListField().getName()).setDataType(ECoalesceFieldDataTypes.ENUMERATION_LIST_TYPE);

        CoalesceUpdaterIterator iterator = new CoalesceUpdaterIterator(template.createNewEntityTemplate());

        try
        {
            iterator.iterate(entity);
        }
        catch (CoalesceException e)
        {
            Assert.assertEquals(String.format(CoalesceErrors.INVALID_ENUMERATION_POSITION,
                                              record.getIntegerListField().getBaseValue(),
                                              record.getParent().getName() + "." + record.getIntegerListField().getName()),
                                e.getMessage());
        }
    }

    @Test
    public void testUpdatingUnsupportedFields() throws Exception
    {
        TestEntity entity = new TestEntity();
        entity.initialize();

        TestRecord record = entity.addRecord1();
        record.getIntegerField().setValue(10);

        TestEntity template = new TestEntity();
        template.initialize();
        template.getRecordset1().getFieldDefinition(record.getIntegerField().getName()).setDataType(ECoalesceFieldDataTypes.ENUMERATION_LIST_TYPE);

        CoalesceUpdaterIterator iterator = new CoalesceUpdaterIterator(template.createNewEntityTemplate());

        try
        {
            iterator.iterate(entity);
        }
        catch (CoalesceException e)
        {
            Assert.assertEquals(String.format(CoalesceErrors.INVALID_DATA_TYPE,
                                              record.getIntegerField().getName(),
                                              record.getIntegerField().getDataType(),
                                              entity.getKey(),
                                              ECoalesceFieldDataTypes.ENUMERATION_LIST_TYPE), e.getMessage());
        }
    }

    @Test
    public void testInvalidTemplate() throws Exception
    {
        TestEntity entity = new TestEntity();
        entity.initialize();

        TestRecord record = entity.addRecord1();
        record.getIntegerField().setValue(10);

        TestEntity template = new TestEntity();
        template.initialize();
        template.setName("Test");
        template.getRecordset1().getFieldDefinition(record.getIntegerField().getName()).setDataType(ECoalesceFieldDataTypes.ENUMERATION_LIST_TYPE);

        CoalesceUpdaterIterator iterator = new CoalesceUpdaterIterator(template.createNewEntityTemplate());

        try
        {
            iterator.iterate(entity);
        }
        catch (CoalesceException e)
        {
            Assert.assertEquals(String.format(CoalesceErrors.INVALID_INPUT_REASON,
                                              entity.getName() + ":" + entity.getSource(),
                                              template.getName() + ":" + template.getSource()),
                                e.getMessage());
        }
    }
}
