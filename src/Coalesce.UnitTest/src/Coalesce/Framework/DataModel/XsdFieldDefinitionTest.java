package Coalesce.Framework.DataModel;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.joda.time.DateTime;
import org.junit.Test;

import Coalesce.Common.Classification.Marking;
import Coalesce.Common.Helpers.JodaDateTimeHelper;
import Coalesce.Common.UnitTest.CoalesceTypeInstances;

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

public class XsdFieldDefinitionTest {

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
                                                                   ECoalesceFieldDataTypes.StringType);

        CoalesceDataObject xdo = recordSet.getDataObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              "Field Def Name",
                              ECoalesceFieldDataTypes.StringType,
                              "",
                              XsdFieldDefinitionTest.UNCLASS_MARKING,
                              "",
                              false,
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

        CoalesceDataObject xdo = recordSet.getDataObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              "Field Def Name",
                              ECoalesceFieldDataTypes.BooleanType,
                              "Boolean Def",
                              XsdFieldDefinitionTest.TS_MARKING,
                              Boolean.FALSE.toString(),
                              true,
                              (CoalesceFieldDefinition) xdo);

        CoalesceRecord record = recordSet.addNew();

        XsdFieldDefinitionTest.assertNewField(newFieldDef, record.getFieldByName("Field Def Name"));

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

        CoalesceDataObject xdo = recordSet.getDataObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              "Field Def Name",
                              ECoalesceFieldDataTypes.BooleanType,
                              "Boolean Def",
                              XsdFieldDefinitionTest.UNCLASS_MARKING,
                              Boolean.TRUE.toString(),
                              true,
                              (CoalesceFieldDefinition) xdo);

        CoalesceRecord record = recordSet.addNew();

        XsdFieldDefinitionTest.assertNewField(newFieldDef, record.getFieldByName("Field Def Name"));

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

        CoalesceDataObject xdo = recordSet.getDataObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              "Field Def Name",
                              ECoalesceFieldDataTypes.BooleanType,
                              "Boolean Def",
                              XsdFieldDefinitionTest.TS_MARKING,
                              Boolean.FALSE.toString(),
                              false,
                              (CoalesceFieldDefinition) xdo);

        CoalesceRecord record = recordSet.addNew();

        XsdFieldDefinitionTest.assertNewField(newFieldDef, record.getFieldByName("Field Def Name"));

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

        CoalesceDataObject xdo = recordSet.getDataObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              "Field Def Name",
                              ECoalesceFieldDataTypes.BooleanType,
                              "Boolean Def",
                              XsdFieldDefinitionTest.UNCLASS_MARKING,
                              Boolean.TRUE.toString(),
                              false,
                              (CoalesceFieldDefinition) xdo);

        CoalesceRecord record = recordSet.addNew();

        XsdFieldDefinitionTest.assertNewField(newFieldDef, record.getFieldByName("Field Def Name"));

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

        CoalesceDataObject xdo = recordSet.getDataObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              "Field Def Name",
                              ECoalesceFieldDataTypes.IntegerType,
                              "Integer Def",
                              XsdFieldDefinitionTest.TS_MARKING,
                              Integer.toString(5),
                              true,
                              (CoalesceFieldDefinition) xdo);

        CoalesceRecord record = recordSet.addNew();

        XsdFieldDefinitionTest.assertNewField(newFieldDef, record.getFieldByName("Field Def Name"));

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

        CoalesceDataObject xdo = recordSet.getDataObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              "Field Def Name",
                              ECoalesceFieldDataTypes.IntegerType,
                              "Integer Def",
                              XsdFieldDefinitionTest.UNCLASS_MARKING,
                              Integer.toString(5),
                              false,
                              (CoalesceFieldDefinition) xdo);

        CoalesceRecord record = recordSet.addNew();

        XsdFieldDefinitionTest.assertNewField(newFieldDef, record.getFieldByName("Field Def Name"));

    }

    @Test
    public void createStringNoIndexTest()
    {
        CoalesceRecordset recordSet = createTestRecordset();

        CoalesceFieldDefinition newFieldDef = CoalesceFieldDefinition.create(recordSet,
                                                                   "Field Def Name",
                                                                   ECoalesceFieldDataTypes.StringType,
                                                                   "String Def",
                                                                   "(TS)",
                                                                   "Unknown",
                                                                   true);

        CoalesceDataObject xdo = recordSet.getDataObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              "Field Def Name",
                              ECoalesceFieldDataTypes.StringType,
                              "String Def",
                              XsdFieldDefinitionTest.TS_MARKING,
                              "Unknown",
                              true,
                              (CoalesceFieldDefinition) xdo);

        CoalesceRecord record = recordSet.addNew();

        XsdFieldDefinitionTest.assertNewField(newFieldDef, record.getFieldByName("Field Def Name"));

    }

    @Test
    public void createStringIndexTest()
    {
        CoalesceRecordset recordSet = createTestRecordset();

        CoalesceFieldDefinition newFieldDef = CoalesceFieldDefinition.create(recordSet,
                                                                   "Field Def Name",
                                                                   ECoalesceFieldDataTypes.StringType,
                                                                   "String Def",
                                                                   "(TS)",
                                                                   "XXX",
                                                                   false);

        CoalesceDataObject xdo = recordSet.getDataObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              "Field Def Name",
                              ECoalesceFieldDataTypes.StringType,
                              "String Def",
                              XsdFieldDefinitionTest.TS_MARKING,
                              "XXX",
                              false,
                              (CoalesceFieldDefinition) xdo);

        CoalesceRecord record = recordSet.addNew();

        XsdFieldDefinitionTest.assertNewField(newFieldDef, record.getFieldByName("Field Def Name"));

    }

    @Test
    public void createDateNoIndexTest()
    {
        CoalesceRecordset recordSet = createTestRecordset();

        DateTime now = JodaDateTimeHelper.nowInUtc();
        CoalesceFieldDefinition newFieldDef = CoalesceFieldDefinition.create(recordSet,
                                                                   "Field Def Name",
                                                                   ECoalesceFieldDataTypes.DateTimeType,
                                                                   "DateTime Def",
                                                                   "(TS)",
                                                                   JodaDateTimeHelper.toXmlDateTimeUTC(now),
                                                                   true);

        CoalesceDataObject xdo = recordSet.getDataObjectForNamePath("Entity Information Section/Field Def Name");

        assertEquals(newFieldDef, xdo);

        assertFieldDefinition(recordSet,
                              "Field Def Name",
                              ECoalesceFieldDataTypes.DateTimeType,
                              "DateTime Def",
                              XsdFieldDefinitionTest.TS_MARKING,
                              JodaDateTimeHelper.toXmlDateTimeUTC(now),
                              true,
                              (CoalesceFieldDefinition) xdo);

        CoalesceRecord record = recordSet.addNew();

        XsdFieldDefinitionTest.assertNewField(newFieldDef, record.getFieldByName("Field Def Name"));

    }

    @Test
    public void KeyTest()
    {
        CoalesceFieldDefinition fieldDefinition = getFieldDefinitionFromXml(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("93C6A209-AD86-4474-9FFB-D6801B2548AA", fieldDefinition.getKey());

        UUID newGUID = UUID.randomUUID();

        fieldDefinition.setKey(newGUID);

        assertEquals(newGUID.toString(), fieldDefinition.getKey());

    }

    @Test
    public void NameTest()
    {
        CoalesceFieldDefinition fieldDefinition = getFieldDefinitionFromXml(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("ActionNumber", fieldDefinition.getName());

        fieldDefinition.setName("New Action Number");

        assertEquals("New Action Number", fieldDefinition.getName());

    }

    @Test
    public void TypeTest()
    {
        CoalesceFieldDefinition fieldDefinition = getFieldDefinitionFromXml(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("fielddefinition", fieldDefinition.getType());

    }

    @Test
    public void LabelTest()
    {
        CoalesceFieldDefinition fieldDefinition = getFieldDefinitionFromXml(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("Action Number", fieldDefinition.getLabel());

        fieldDefinition.setLabel("New Action Number Label");

        assertEquals("New Action Number Label", fieldDefinition.getLabel());

    }

    @Test
    public void DataTypeTest()
    {
        CoalesceFieldDefinition fieldDefinition = getFieldDefinitionFromXml(CoalesceTypeInstances.TEST_MISSION);

        assertEquals(ECoalesceFieldDataTypes.StringType, fieldDefinition.getDataType());

        fieldDefinition.setDataType(ECoalesceFieldDataTypes.BinaryType);

        assertEquals(ECoalesceFieldDataTypes.BinaryType, fieldDefinition.getDataType());

    }

    @Test
    public void DefaultClassificationMarkingStringTest()
    {
        CoalesceFieldDefinition fieldDefinition = getFieldDefinitionFromXml(CoalesceTypeInstances.TEST_MISSION);

        assertEquals(XsdFieldDefinitionTest.UNCLASS_MARKING.toPortionString(),
                     fieldDefinition.getDefaultClassificationMarking().toPortionString());

        fieldDefinition.setDefaultClassificationMarking(XsdFieldDefinitionTest.TS_MARKING.toString());

        assertEquals(XsdFieldDefinitionTest.TS_MARKING.toPortionString(),
                     fieldDefinition.getDefaultClassificationMarking().toPortionString());

    }

    @Test
    public void DefaultClassificationMarkingStringPortionTest()
    {
        CoalesceFieldDefinition fieldDefinition = getFieldDefinitionFromXml(CoalesceTypeInstances.TEST_MISSION);

        assertEquals(XsdFieldDefinitionTest.UNCLASS_MARKING.toPortionString(),
                     fieldDefinition.getDefaultClassificationMarking().toPortionString());

        fieldDefinition.setDefaultClassificationMarking(XsdFieldDefinitionTest.TS_MARKING.toPortionString());

        assertEquals(XsdFieldDefinitionTest.TS_MARKING.toString(),
                     fieldDefinition.getDefaultClassificationMarking().toString());

    }

    @Test
    public void DefaultClassificationMarkingMarkingTest()
    {
        CoalesceFieldDefinition fieldDefinition = getFieldDefinitionFromXml(CoalesceTypeInstances.TEST_MISSION);

        assertEquals(XsdFieldDefinitionTest.UNCLASS_MARKING.toPortionString(),
                     fieldDefinition.getDefaultClassificationMarking().toPortionString());

        fieldDefinition.setDefaultClassificationMarking(XsdFieldDefinitionTest.TS_MARKING);

        assertEquals(XsdFieldDefinitionTest.TS_MARKING.toString(),
                     fieldDefinition.getDefaultClassificationMarking().toString());

    }

    @Test
    public void DefaultValue()
    {
        CoalesceFieldDefinition fieldDefinition = getFieldDefinitionFromXml(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("0", fieldDefinition.getDefaultValue());

        fieldDefinition.setDefaultValue("5");

        assertEquals("5", fieldDefinition.getDefaultValue());

    }

    @Test
    public void DateCreatedTest()
    {
        CoalesceFieldDefinition fieldDefinition = getFieldDefinitionFromXml(CoalesceTypeInstances.TEST_MISSION);

        assertEquals(JodaDateTimeHelper.fromXmlDateTimeUTC("2014-05-02T14:33:51.8525751Z"), fieldDefinition.getDateCreated());

        DateTime now = JodaDateTimeHelper.nowInUtc();
        fieldDefinition.setDateCreated(now);

        assertEquals(now, fieldDefinition.getDateCreated());

    }

    @Test
    public void LastModifiedTest()
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
        assertEquals(fieldDefinition.getLabel(), field.getLabel());
        assertEquals(fieldDefinition.getDefaultClassificationMarking().toString(),
                     field.getClassificationMarking().toString());
        assertEquals(fieldDefinition.getDefaultValue(), field.getBaseValue());
        assertEquals(fieldDefinition.getNoIndex(), field.getNoIndex());

    }

    // -----------------------------------------------------------------------//
    // Private Methods
    // -----------------------------------------------------------------------//

    private CoalesceFieldDefinition getFieldDefinitionFromXml(String entityXml)
    {
        CoalesceEntity entity = CoalesceEntity.create(entityXml);

        CoalesceFieldDefinition fieldDefinition = (CoalesceFieldDefinition) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/ActionNumber");

        return fieldDefinition;
    }

    private CoalesceRecordset createTestRecordset()
    {
        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();

        CoalesceSection informationSection = CoalesceSection.create(entity, "Entity Information Section", true);
        CoalesceRecordset informationRecordSet = CoalesceRecordset.create(informationSection, "Information Recordset");

        return informationRecordSet;
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
        assertEquals(noIndex, fieldDefinition.getNoIndex());
    }
}
