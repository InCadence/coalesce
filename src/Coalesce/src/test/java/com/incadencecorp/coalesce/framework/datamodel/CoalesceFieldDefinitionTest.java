package com.incadencecorp.coalesce.framework.datamodel;

import com.incadencecorp.coalesce.common.CoalesceTypeInstances;
import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

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

public class CoalesceFieldDefinitionTest {

    private static Marking UNCLASS_MARKING = new Marking("UNCLASSIFIED");
    private static Marking TS_MARKING = new Marking("(TS)");

    /*
     * @BeforeClass public static void setUpBeforeClass() throws Exception { }
     * 
     * @AfterClass public static void tearDownAfterClass() throws Exception { }
     * 
     * @Before public void setUp() throws Exception { }
     * 
     * @After public void tearDown() throws Exception { }
     */

    @Test
    public void createNameTypeTest()
    {
        CoalesceRecordset recordSet = createTestRecordset();

        CoalesceFieldDefinition newFieldDef = CoalesceFieldDefinition.create(recordSet,
                                                                             "Field Def Name",
                                                                             ECoalesceFieldDataTypes.STRING_TYPE);

        CoalesceObject xdo = recordSet.getCoalesceObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              newFieldDef.getName(),
                              ECoalesceFieldDataTypes.STRING_TYPE,
                              null,
                              CoalesceFieldDefinitionTest.UNCLASS_MARKING,
                              null,
                              CoalesceObject.ATTRIBUTE_NOINDEX_DEFAULT,
                              (CoalesceFieldDefinition) xdo);

    }

    @Test
    public void createBooleanDefaultValueFalseNoIndexTest()
    {
        CoalesceRecordset recordSet = createTestRecordset();

        CoalesceFieldDefinition newFieldDef = CoalesceFieldDefinition.create(recordSet,
                                                                             "Field Def Name",
                                                                             "Boolean Def",
                                                                             "(TS)",
                                                                             false,
                                                                             true);

        CoalesceObject xdo = recordSet.getCoalesceObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              newFieldDef.getName(),
                              ECoalesceFieldDataTypes.BOOLEAN_TYPE,
                              "Boolean Def",
                              CoalesceFieldDefinitionTest.TS_MARKING,
                              Boolean.FALSE.toString(),
                              true,
                              (CoalesceFieldDefinition) xdo);

        CoalesceRecord record = recordSet.addNew();

        CoalesceFieldDefinitionTest.assertNewField(newFieldDef, record.getFieldByName(newFieldDef.getName()));

    }

    @Test
    public void createBooleanDefaultValueFalseWithNullTest()
    {
        CoalesceRecordset recordSet = createTestRecordset();

        CoalesceFieldDefinition newFieldDef = CoalesceFieldDefinition.create(recordSet, "Field Def Name", null, null, false);

        CoalesceObject xdo = recordSet.getCoalesceObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              newFieldDef.getName(),
                              ECoalesceFieldDataTypes.BOOLEAN_TYPE,
                              null,
                              CoalesceFieldDefinitionTest.UNCLASS_MARKING,
                              Boolean.FALSE.toString(),
                              CoalesceObject.ATTRIBUTE_NOINDEX_DEFAULT,
                              (CoalesceFieldDefinition) xdo);

        CoalesceRecord record = recordSet.addNew();

        CoalesceFieldDefinitionTest.assertNewField(newFieldDef, record.getFieldByName(newFieldDef.getName()));

    }

    @Test
    public void createBooleanDefaultValueTrueNoIndexTest()
    {
        CoalesceRecordset recordSet = createTestRecordset();

        CoalesceFieldDefinition newFieldDef = CoalesceFieldDefinition.create(recordSet,
                                                                             "Field Def Name",
                                                                             "Boolean Def",
                                                                             "(U)",
                                                                             true,
                                                                             true);

        CoalesceObject xdo = recordSet.getCoalesceObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              newFieldDef.getName(),
                              ECoalesceFieldDataTypes.BOOLEAN_TYPE,
                              "Boolean Def",
                              CoalesceFieldDefinitionTest.UNCLASS_MARKING,
                              Boolean.TRUE.toString(),
                              true,
                              (CoalesceFieldDefinition) xdo);

        CoalesceRecord record = recordSet.addNew();

        CoalesceFieldDefinitionTest.assertNewField(newFieldDef, record.getFieldByName(newFieldDef.getName()));

    }

    @Test
    public void createBooleanDefaultValueFalseIndexTest()
    {
        CoalesceRecordset recordSet = createTestRecordset();

        CoalesceFieldDefinition newFieldDef = CoalesceFieldDefinition.create(recordSet,
                                                                             "Field Def Name",
                                                                             "Boolean Def",
                                                                             "(TS)",
                                                                             false,
                                                                             false);

        CoalesceObject xdo = recordSet.getCoalesceObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              newFieldDef.getName(),
                              ECoalesceFieldDataTypes.BOOLEAN_TYPE,
                              "Boolean Def",
                              CoalesceFieldDefinitionTest.TS_MARKING,
                              Boolean.FALSE.toString(),
                              false,
                              (CoalesceFieldDefinition) xdo);

        CoalesceRecord record = recordSet.addNew();

        CoalesceFieldDefinitionTest.assertNewField(newFieldDef, record.getFieldByName(newFieldDef.getName()));

    }

    @Test
    public void createBooleanDefaultValueTrueIndexTest()
    {
        CoalesceRecordset recordSet = createTestRecordset();

        CoalesceFieldDefinition newFieldDef = CoalesceFieldDefinition.create(recordSet,
                                                                             "Field Def Name",
                                                                             "Boolean Def",
                                                                             "(U)",
                                                                             true,
                                                                             false);

        CoalesceObject xdo = recordSet.getCoalesceObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              newFieldDef.getName(),
                              ECoalesceFieldDataTypes.BOOLEAN_TYPE,
                              "Boolean Def",
                              CoalesceFieldDefinitionTest.UNCLASS_MARKING,
                              Boolean.TRUE.toString(),
                              false,
                              (CoalesceFieldDefinition) xdo);

        CoalesceRecord record = recordSet.addNew();

        CoalesceFieldDefinitionTest.assertNewField(newFieldDef, record.getFieldByName(newFieldDef.getName()));

    }

    @Test
    public void createBooleanDefaultValueTrueTest()
    {
        CoalesceRecordset recordSet = createTestRecordset();

        CoalesceFieldDefinition newFieldDef = CoalesceFieldDefinition.create(recordSet,
                                                                             "Field Def Name",
                                                                             "Boolean Def",
                                                                             "(U)",
                                                                             true);

        CoalesceObject xdo = recordSet.getCoalesceObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              newFieldDef.getName(),
                              ECoalesceFieldDataTypes.BOOLEAN_TYPE,
                              "Boolean Def",
                              CoalesceFieldDefinitionTest.UNCLASS_MARKING,
                              Boolean.TRUE.toString(),
                              CoalesceObject.ATTRIBUTE_NOINDEX_DEFAULT,
                              (CoalesceFieldDefinition) xdo);

        CoalesceRecord record = recordSet.addNew();

        CoalesceFieldDefinitionTest.assertNewField(newFieldDef, record.getFieldByName(newFieldDef.getName()));

    }

    @Test
    public void createIntegerNoIndexTest()
    {
        CoalesceRecordset recordSet = createTestRecordset();

        CoalesceFieldDefinition newFieldDef = CoalesceFieldDefinition.create(recordSet,
                                                                             "Field Def Name",
                                                                             "Integer Def",
                                                                             "(TS)",
                                                                             5,
                                                                             true);

        CoalesceObject xdo = recordSet.getCoalesceObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              newFieldDef.getName(),
                              ECoalesceFieldDataTypes.INTEGER_TYPE,
                              "Integer Def",
                              CoalesceFieldDefinitionTest.TS_MARKING,
                              Integer.toString(5),
                              true,
                              (CoalesceFieldDefinition) xdo);

        CoalesceRecord record = recordSet.addNew();

        CoalesceFieldDefinitionTest.assertNewField(newFieldDef, record.getFieldByName(newFieldDef.getName()));

    }

    @Test
    public void createIntegerIndexTest()
    {
        CoalesceRecordset recordSet = createTestRecordset();

        CoalesceFieldDefinition newFieldDef = CoalesceFieldDefinition.create(recordSet,
                                                                             "Field Def Name",
                                                                             "Integer Def",
                                                                             "(U)",
                                                                             5,
                                                                             false);

        CoalesceObject xdo = recordSet.getCoalesceObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              newFieldDef.getName(),
                              ECoalesceFieldDataTypes.INTEGER_TYPE,
                              "Integer Def",
                              CoalesceFieldDefinitionTest.UNCLASS_MARKING,
                              Integer.toString(5),
                              false,
                              (CoalesceFieldDefinition) xdo);

        CoalesceRecord record = recordSet.addNew();

        CoalesceFieldDefinitionTest.assertNewField(newFieldDef, record.getFieldByName(newFieldDef.getName()));

    }

    @Test
    public void createIntegerTest()
    {
        CoalesceRecordset recordSet = createTestRecordset();

        CoalesceFieldDefinition newFieldDef = CoalesceFieldDefinition.create(recordSet,
                                                                             "Field Def Name",
                                                                             "Integer Def",
                                                                             "(U)",
                                                                             5);

        CoalesceObject xdo = recordSet.getCoalesceObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              newFieldDef.getName(),
                              ECoalesceFieldDataTypes.INTEGER_TYPE,
                              "Integer Def",
                              CoalesceFieldDefinitionTest.UNCLASS_MARKING,
                              Integer.toString(5),
                              CoalesceObject.ATTRIBUTE_NOINDEX_DEFAULT,
                              (CoalesceFieldDefinition) xdo);

        CoalesceRecord record = recordSet.addNew();

        CoalesceFieldDefinitionTest.assertNewField(newFieldDef, record.getFieldByName(newFieldDef.getName()));

    }

    @Test
    public void createStringNoIndexTest()
    {
        CoalesceRecordset recordSet = createTestRecordset();

        CoalesceFieldDefinition newFieldDef = CoalesceFieldDefinition.create(recordSet,
                                                                             "Field Def Name",
                                                                             ECoalesceFieldDataTypes.STRING_TYPE,
                                                                             "String Def",
                                                                             "(TS)",
                                                                             "Unknown",
                                                                             true);

        CoalesceObject xdo = recordSet.getCoalesceObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              newFieldDef.getName(),
                              ECoalesceFieldDataTypes.STRING_TYPE,
                              "String Def",
                              CoalesceFieldDefinitionTest.TS_MARKING,
                              "Unknown",
                              true,
                              (CoalesceFieldDefinition) xdo);

        CoalesceRecord record = recordSet.addNew();

        CoalesceFieldDefinitionTest.assertNewField(newFieldDef, record.getFieldByName(newFieldDef.getName()));

    }

    @Test
    public void createStringIndexTest()
    {
        CoalesceRecordset recordSet = createTestRecordset();

        CoalesceFieldDefinition newFieldDef = CoalesceFieldDefinition.create(recordSet,
                                                                             "Field Def Name",
                                                                             ECoalesceFieldDataTypes.STRING_TYPE,
                                                                             "String Def",
                                                                             "(TS)",
                                                                             "XXX",
                                                                             false);

        CoalesceObject xdo = recordSet.getCoalesceObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              newFieldDef.getName(),
                              ECoalesceFieldDataTypes.STRING_TYPE,
                              "String Def",
                              CoalesceFieldDefinitionTest.TS_MARKING,
                              "XXX",
                              false,
                              (CoalesceFieldDefinition) xdo);

        CoalesceRecord record = recordSet.addNew();

        CoalesceFieldDefinitionTest.assertNewField(newFieldDef, record.getFieldByName(newFieldDef.getName()));

    }

    @Test
    public void createStringTest()
    {
        CoalesceRecordset recordSet = createTestRecordset();

        CoalesceFieldDefinition newFieldDef = CoalesceFieldDefinition.create(recordSet,
                                                                             "Field Def Name",
                                                                             ECoalesceFieldDataTypes.STRING_TYPE,
                                                                             "String Def",
                                                                             "(TS)",
                                                                             "XXX");

        CoalesceObject xdo = recordSet.getCoalesceObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              newFieldDef.getName(),
                              ECoalesceFieldDataTypes.STRING_TYPE,
                              "String Def",
                              CoalesceFieldDefinitionTest.TS_MARKING,
                              "XXX",
                              CoalesceObject.ATTRIBUTE_NOINDEX_DEFAULT,
                              (CoalesceFieldDefinition) xdo);

        CoalesceRecord record = recordSet.addNew();

        CoalesceFieldDefinitionTest.assertNewField(newFieldDef, record.getFieldByName(newFieldDef.getName()));

    }

    @Test
    public void createStringNullNameTest()
    {
        CoalesceRecordset recordSet = createTestRecordset();

        CoalesceFieldDefinition newFieldDef = CoalesceFieldDefinition.create(recordSet,
                                                                             null,
                                                                             ECoalesceFieldDataTypes.STRING_TYPE,
                                                                             "String Def",
                                                                             "(TS)",
                                                                             "XXX");

        assertEquals(null, newFieldDef);

    }

    @Test
    public void createStringNullParentTest()
    {
        Assert.assertNull(CoalesceFieldDefinition.create(null,
                                                         "Field Def Name",
                                                         ECoalesceFieldDataTypes.STRING_TYPE,
                                                         "String Def",
                                                         "(TS)",
                                                         "XXX"));
    }

    @Test
    public void createDateNoIndexTest()
    {
        CoalesceRecordset recordSet = createTestRecordset();

        DateTime now = JodaDateTimeHelper.nowInUtc();
        CoalesceFieldDefinition newFieldDef = CoalesceFieldDefinition.create(recordSet,
                                                                             "Field Def Name",
                                                                             ECoalesceFieldDataTypes.DATE_TIME_TYPE,
                                                                             "DateTime Def",
                                                                             "(TS)",
                                                                             JodaDateTimeHelper.toXmlDateTimeUTC(now),
                                                                             true);

        CoalesceObject xdo = recordSet.getCoalesceObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              "Field Def Name",
                              ECoalesceFieldDataTypes.DATE_TIME_TYPE,
                              "DateTime Def",
                              CoalesceFieldDefinitionTest.TS_MARKING,
                              JodaDateTimeHelper.toXmlDateTimeUTC(now),
                              true,
                              (CoalesceFieldDefinition) xdo);

        CoalesceRecord record = recordSet.addNew();

        CoalesceFieldDefinitionTest.assertNewField(newFieldDef, record.getFieldByName(newFieldDef.getName()));

    }

    @Test
    public void keyTest()
    {
        CoalesceFieldDefinition fieldDefinition = getFieldDefinitionFromXml(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("93C6A209-AD86-4474-9FFB-D6801B2548AA", fieldDefinition.getKey());

        UUID newGUID = UUID.randomUUID();

        fieldDefinition.setKey(newGUID.toString());

        assertEquals(newGUID.toString(), fieldDefinition.getKey());

    }

    @Test
    public void nameTest()
    {
        CoalesceFieldDefinition fieldDefinition = getFieldDefinitionFromXml(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("ActionNumber", fieldDefinition.getName());

        fieldDefinition.setName("New Action Number");

        assertEquals("New Action Number", fieldDefinition.getName());

    }

    @Test
    public void typeTest()
    {
        CoalesceFieldDefinition fieldDefinition = getFieldDefinitionFromXml(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("fielddefinition", fieldDefinition.getType());

    }

    @Test
    public void labelTest()
    {
        CoalesceFieldDefinition fieldDefinition = getFieldDefinitionFromXml(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("Action Number", fieldDefinition.getLabel());

        fieldDefinition.setLabel("New Action Number Label");

        assertEquals("New Action Number Label", fieldDefinition.getLabel());

    }

    @Test
    public void dataTypeTest()
    {
        CoalesceFieldDefinition fieldDefinition = getFieldDefinitionFromXml(CoalesceTypeInstances.TEST_MISSION);

        assertEquals(ECoalesceFieldDataTypes.STRING_TYPE, fieldDefinition.getDataType());

        fieldDefinition.setDataType(ECoalesceFieldDataTypes.BINARY_TYPE);

        assertEquals(ECoalesceFieldDataTypes.BINARY_TYPE, fieldDefinition.getDataType());

    }

    @Test
    public void defaultClassificationMarkingStringTest()
    {
        CoalesceFieldDefinition fieldDefinition = getFieldDefinitionFromXml(CoalesceTypeInstances.TEST_MISSION);

        assertEquals(CoalesceFieldDefinitionTest.UNCLASS_MARKING.toPortionString(),
                     fieldDefinition.getDefaultClassificationMarking().toPortionString());

        fieldDefinition.setDefaultClassificationMarkingAsString(CoalesceFieldDefinitionTest.TS_MARKING.toString());

        assertEquals(CoalesceFieldDefinitionTest.TS_MARKING.toPortionString(),
                     fieldDefinition.getDefaultClassificationMarking().toPortionString());

    }

    @Test
    public void defaultClassificationMarkingStringPortionTest()
    {
        CoalesceFieldDefinition fieldDefinition = getFieldDefinitionFromXml(CoalesceTypeInstances.TEST_MISSION);

        assertEquals(CoalesceFieldDefinitionTest.UNCLASS_MARKING.toPortionString(),
                     fieldDefinition.getDefaultClassificationMarking().toPortionString());

        fieldDefinition.setDefaultClassificationMarkingAsString(CoalesceFieldDefinitionTest.TS_MARKING.toPortionString());

        assertEquals(CoalesceFieldDefinitionTest.TS_MARKING.toString(),
                     fieldDefinition.getDefaultClassificationMarking().toString());

    }

    @Test
    public void defaultClassificationMarkingMarkingTest()
    {
        CoalesceFieldDefinition fieldDefinition = getFieldDefinitionFromXml(CoalesceTypeInstances.TEST_MISSION);

        assertEquals(CoalesceFieldDefinitionTest.UNCLASS_MARKING.toPortionString(),
                     fieldDefinition.getDefaultClassificationMarking().toPortionString());

        fieldDefinition.setDefaultClassificationMarking(CoalesceFieldDefinitionTest.TS_MARKING);

        assertEquals(CoalesceFieldDefinitionTest.TS_MARKING.toString(),
                     fieldDefinition.getDefaultClassificationMarking().toString());

    }

    @Test
    public void defaultValue()
    {
        CoalesceFieldDefinition fieldDefinition = getFieldDefinitionFromXml(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("0", fieldDefinition.getDefaultValue());

        fieldDefinition.setDefaultValue("5");

        assertEquals("5", fieldDefinition.getDefaultValue());

    }

    @Test
    public void dateCreatedTest()
    {
        CoalesceFieldDefinition fieldDefinition = getFieldDefinitionFromXml(CoalesceTypeInstances.TEST_MISSION);

        assertEquals(JodaDateTimeHelper.fromXmlDateTimeUTC("2014-05-02T14:33:51.8525751Z"),
                     fieldDefinition.getDateCreated());

        DateTime now = JodaDateTimeHelper.nowInUtc();
        fieldDefinition.setDateCreated(now);

        assertEquals(now, fieldDefinition.getDateCreated());

    }

    @Test
    public void lastModifiedTest()
    {
        CoalesceFieldDefinition fieldDefinition = getFieldDefinitionFromXml(CoalesceTypeInstances.TEST_MISSION);

        assertEquals(JodaDateTimeHelper.fromXmlDateTimeUTC("2014-05-02T14:33:51.8525751Z"),
                     fieldDefinition.getLastModified());

        DateTime now = JodaDateTimeHelper.nowInUtc();
        fieldDefinition.setLastModified(now);

        assertEquals(now, fieldDefinition.getLastModified());

    }

    public static void assertNewField(CoalesceFieldDefinition fieldDefinition, CoalesceField<?> field)
    {
        assertEquals(fieldDefinition.getName(), field.getName());
        assertEquals(fieldDefinition.getDataType(), field.getDataType());
        if (fieldDefinition.getLabel() == null)
        {
            assertTrue(StringHelper.isNullOrEmpty(field.getLabel()));
        }
        else
        {
            assertEquals(fieldDefinition.getLabel(), field.getLabel());
        }

        assertEquals(fieldDefinition.getDefaultClassificationMarking().toString(),
                     field.getClassificationMarking().toString());
        assertEquals(fieldDefinition.getDefaultValue(), field.getBaseValue());
        assertEquals(fieldDefinition.isNoIndex(), field.isNoIndex());

    }

    @Test
    public void toXmlTest()
    {
        CoalesceFieldDefinition fd = getFieldDefinitionFromXml(CoalesceTypeInstances.TEST_MISSION);

        String fdXml = fd.toXml();

        Fielddefinition desFd = (Fielddefinition) XmlHelper.deserialize(fdXml, Fielddefinition.class);

        assertEquals(fd.getKey(), desFd.getKey());
        assertEquals(fd.getName(), desFd.getName());
        assertEquals(fd.getDateCreated(), desFd.getDatecreated());
        assertEquals(fd.getLastModified(), desFd.getLastmodified());
        assertEquals(fd.getDataType(), ECoalesceFieldDataTypes.getTypeForCoalesceType(desFd.getDatatype()));
        assertEquals(fd.getDefaultClassificationMarking(), new Marking(desFd.getDefaultclassificationmarking()));
        assertEquals(fd.getLabel(), desFd.getLabel());
        assertEquals(fd.getDefaultValue(), desFd.getDefaultvalue());
        assertEquals(fd.getStatus(), desFd.getStatus());

    }

    @Test
    public void setStatusTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceFieldDefinition fd = getFieldDefinition(entity);

        assertEquals(ECoalesceObjectStatus.ACTIVE, fd.getStatus());

        fd.setStatus(ECoalesceObjectStatus.UNKNOWN);
        String fdXml = fd.toXml();

        Fielddefinition desFd = (Fielddefinition) XmlHelper.deserialize(fdXml, Fielddefinition.class);

        assertEquals(ECoalesceObjectStatus.UNKNOWN, desFd.getStatus());

        CoalesceRecordset recordset = (CoalesceRecordset) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset");

        assertEquals(16, recordset.getFieldDefinitions().size());

        fd.setStatus(ECoalesceObjectStatus.ACTIVE);
        assertEquals(ECoalesceObjectStatus.ACTIVE, fd.getStatus());

        assertEquals(17, recordset.getFieldDefinitions().size());

        fd.setStatus(ECoalesceObjectStatus.ACTIVE);
        assertEquals(ECoalesceObjectStatus.ACTIVE, fd.getStatus());

        assertEquals(17, recordset.getFieldDefinitions().size());

    }

    @Test
    public void attributeTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceFieldDefinition fd = getFieldDefinition(entity);

        int before = fd.getAttributes().size();

        fd.setAttribute("TestAttribute", "TestingValue");

        assertEquals(before + 1, fd.getAttributes().size());

        assertEquals("TestingValue", fd.getAttribute("TestAttribute"));

        assertEquals("ActionNumber", fd.getName());
        assertEquals(CoalesceObject.ATTRIBUTE_NOINDEX_DEFAULT, fd.isNoIndex());

        fd.setAttribute("Name", "TestingName");
        assertEquals("TestingName", fd.getName());
        assertEquals("TestingName", fd.getAttribute("Name"));

        UUID guid = UUID.randomUUID();
        fd.setAttribute("Key", guid.toString());
        assertEquals(guid.toString(), fd.getKey());
        assertEquals(guid.toString(), fd.getAttribute("Key"));

        DateTime now = JodaDateTimeHelper.nowInUtc();
        DateTime future = now.plusDays(2);

        fd.setAttribute("DateCreated", JodaDateTimeHelper.toXmlDateTimeUTC(now));
        assertEquals(now, fd.getDateCreated());

        fd.setAttribute("NoIndex", "True");
        assertEquals(true, fd.isNoIndex());

        fd.setAttribute("DataType", "Integer");
        assertEquals(ECoalesceFieldDataTypes.INTEGER_TYPE, fd.getDataType());

        fd.setAttribute("DefaultClassificationmarking", "(TS)");
        assertEquals(new Marking("(TS)"), fd.getDefaultClassificationMarking());

        fd.setAttribute("Label", "labelTest");
        assertEquals("labelTest", fd.getLabel());

        fd.setAttribute("DefaultValue", "123");
        assertEquals("123", fd.getDefaultValue());

        fd.setAttribute("Status", ECoalesceObjectStatus.UNKNOWN.toString());
        assertEquals(ECoalesceObjectStatus.UNKNOWN, fd.getStatus());

        fd.setAttribute("Status", ECoalesceObjectStatus.ACTIVE.toString());
        assertEquals(ECoalesceObjectStatus.ACTIVE, fd.getStatus());

        fd.setAttribute("LastModified", JodaDateTimeHelper.toXmlDateTimeUTC(future));
        assertEquals(future, fd.getLastModified());

        String entityXml = entity.toXml();
        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceFieldDefinition desFd = (CoalesceFieldDefinition) desEntity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/TestingName");

        assertEquals("TestingValue", desFd.getAttribute("TestAttribute"));
        assertEquals("TestingName", desFd.getName());
        assertEquals(guid.toString(), desFd.getKey());
        assertEquals(now, desFd.getDateCreated());
        assertEquals(future, desFd.getLastModified());
        assertEquals(true, desFd.isNoIndex());
        assertEquals(ECoalesceFieldDataTypes.INTEGER_TYPE, desFd.getDataType());
        assertEquals(new Marking("(TS)"), desFd.getDefaultClassificationMarking());
        assertEquals("labelTest", desFd.getLabel());
        assertEquals("123", desFd.getDefaultValue());
        assertEquals(ECoalesceObjectStatus.ACTIVE, desFd.getStatus());

    }

    @Test
    public void notActiveFieldDefinitionTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset recordset = (CoalesceRecordset) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset");

        assertEquals(17, recordset.getFieldDefinitions().size());

        CoalesceFieldDefinition fd = getFieldDefinition(entity);

        fd.setAttribute("Status", ECoalesceObjectStatus.UNKNOWN.toString());
        assertEquals(ECoalesceObjectStatus.UNKNOWN, fd.getStatus());

        assertEquals(16, recordset.getFieldDefinitions().size());

        String entityXml = entity.toXml();
        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);

        CoalesceRecordset desRecordset = (CoalesceRecordset) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset");

        assertEquals(16, desRecordset.getFieldDefinitions().size());

        CoalesceFieldDefinition desFd = (CoalesceFieldDefinition) desEntity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/TestingName");

        assertNull(desFd);

    }

    // -----------------------------------------------------------------------//
    // Private Methods
    // -----------------------------------------------------------------------//

    private CoalesceFieldDefinition getFieldDefinition(CoalesceEntity entity)
    {
        return (CoalesceFieldDefinition) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/ActionNumber");

    }

    private CoalesceFieldDefinition getFieldDefinitionFromXml(String entityXml)
    {
        CoalesceEntity entity = CoalesceEntity.create(entityXml);

        return getFieldDefinition(entity);

    }

    private CoalesceRecordset createTestRecordset()
    {
        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();

        CoalesceSection informationSection = CoalesceSection.create(entity, "Entity Information Section", true);

        return CoalesceRecordset.create(informationSection, "Information Recordset");
    }

    private void assertFieldDefinition(CoalesceRecordset parent,
                                       String name,
                                       ECoalesceFieldDataTypes dataType,
                                       String label,
                                       Marking marking,
                                       String defaultValue,
                                       boolean noIndex,
                                       CoalesceFieldDefinition fieldDefinition)
    {
        assertEquals(parent, fieldDefinition.getParent());
        assertEquals(name, fieldDefinition.getName());
        assertEquals(dataType, fieldDefinition.getDataType());
        assertEquals(label, fieldDefinition.getLabel());
        assertEquals(marking.toString(), fieldDefinition.getDefaultClassificationMarking().toString());
        assertEquals(defaultValue, fieldDefinition.getDefaultValue());
        assertEquals(noIndex, fieldDefinition.isNoIndex());
    }
}
