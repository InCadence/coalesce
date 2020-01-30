package com.incadencecorp.coalesce.framework.datamodel;

import com.incadencecorp.coalesce.common.CoalesceTypeInstances;
import com.incadencecorp.coalesce.common.CoalesceUnitTestSettings;
import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.helpers.DocumentProperties;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.MimeHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;
import com.incadencecorp.coalesce.framework.CoalesceSettings;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKTWriter;
import org.locationtech.jts.util.GeometricShapeFactory;
import org.apache.xerces.impl.dv.util.Base64;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
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

/**
 * Unit tests
 *
 * @author n78554
 */
public class CoalesceFieldTest {

    /**
     * XPath location of the coordinate field within a TREXMission
     */
    private static final String FIELD_LOCATION = "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation";

    @Rule
    public ExpectedException _thrown = ExpectedException.none();

    private static final Marking TOPSECRETCLASSIFICATIONMARKING = new Marking(
            "//JOINT TOP SECRET AND USA//FOUO-LES//SBU/ACCM-BOB");
    private static final String COORDINATE_ERROR_MESSAGE = "Coordinate out of range: MissionGeoLocation";
    // private static final String COORDINATES_ERROR_MESSAGE =
    // "Failed to parse coordinates value for: MissionGeoLocation";
    private static final String POINT_ERROR_MESSAGE = "Failed to parse point value for: MissionGeoLocation";

    private static TestRecord record;

    @BeforeClass
    public static void setUpBeforeClass() throws IOException
    {
        CoalesceUnitTestSettings.initialize();

        initializeSettings();

        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();

        // Create Section
        CoalesceSection section = CoalesceSection.create(entity, "test");

        record = new TestRecord(TestRecord.createCoalesceRecordset(section, "test").addNew());

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
        CoalesceUnitTestSettings.tearDownAfterClass();
    }

    /*
     * @Before public void setUp() throws Exception { }
     */

    @After
    public void tearDown()
    {
        initializeSettings();
    }

    @Test
    public void createTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("TestingName", "Testingsource", "1.1", "EntityID", "entityType");
        CoalesceSection section = entity.createSection("TestSection");
        CoalesceRecordset recordset = section.createRecordset("TestRecordset");
        recordset.createFieldDefinition("Double", ECoalesceFieldDataTypes.DOUBLE_TYPE);
        recordset.createFieldDefinition("Float", ECoalesceFieldDataTypes.FLOAT_TYPE);

        CoalesceRecord record = recordset.addNew();

        CoalesceField<?> doubleField = record.getFieldByName("Double");
        CoalesceField<?> floatField = record.getFieldByName("Float");

        assertEquals("field", doubleField.getType());
        assertEquals("field", floatField.getType());

        assertTrue("Should be a double type field", doubleField instanceof CoalesceDoubleField);
        assertTrue("Should be a float type field", floatField instanceof CoalesceFloatField);

        assertEquals("field", doubleField.getType());
        assertEquals("field", floatField.getType());

    }

    @Test
    public void getKeyTest()
    {

        CoalesceField<?> field = getTestMissionNameField();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_KEY, field.getKey());

    }

    /**
     * This test ensures that a field with a default value set will return that value instead of null.
     */
    @Test
    public void testDefaultValue() throws Exception
    {
        TestEntity entity = new TestEntity();
        entity.initialize();

        TestRecord record = entity.addRecord1();

        // Verify Null
        Assert.assertNull(record.getIntegerField().getValue());

        // Verify that getting the value did not affect the field
        Assert.assertNull(record.getIntegerField().getValue());

        record.getIntegerField().getFieldDefinition().setDefaultValue("200");

        Assert.assertEquals(200, record.getIntegerField().getValue(), 0);

    }

    @Test
    public void setKeyTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = getTestMissionNameField(mission);

        UUID newGuid = UUID.randomUUID();
        field.setKey(newGuid.toString());

        CoalesceField<?> savedField = getSavedTestMissionField(mission);

        assertEquals(savedField.getKey().toUpperCase(), newGuid.toString().toUpperCase());

    }

    @Test
    public void getNameTest()
    {

        CoalesceField<?> field = getTestMissionNameField();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_NAME, field.getName());

    }

    @Test
    public void setNameTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = getTestMissionNameField(mission);

        field.setName("Testingname");
        field.getFieldDefinition().setName("Testingname");

        String serializedMission = mission.toXml();
        CoalesceEntity savedMission = CoalesceEntity.create(serializedMission);

        CoalesceField<?> savedField = (CoalesceField<?>) savedMission.getCoalesceObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH.replace(
                CoalesceTypeInstances.TEST_MISSION_NAME_NAME,
                "Testingname"));

        assertEquals("Testingname", savedField.getName());

    }

    @Test
    public void getValueTest()
    {

        CoalesceField<?> field = getTestMissionNameField();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_VALUE, field.getBaseValue());

    }

    @Test
    public void setValueTest() throws CoalesceDataFormatException
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = getTestMissionNameField(mission);

        field.setBaseValue("Testingvalue");

        CoalesceField<?> savedField = getSavedTestMissionField(mission);

        assertEquals("Testingvalue", savedField.getBaseValue());

        field.setBaseValue(null);

        assertEquals("", field.getValue());

    }

    @Test
    public void getDataType()
    {
        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> stringField = getTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
        assertEquals(ECoalesceFieldDataTypes.STRING_TYPE, stringField.getDataType());

        CoalesceField<?> dateField = getTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);
        assertEquals(ECoalesceFieldDataTypes.DATE_TIME_TYPE, dateField.getDataType());

        CoalesceField<?> integerField = getTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);
        assertEquals(ECoalesceFieldDataTypes.INTEGER_TYPE, integerField.getDataType());

    }

    @Test
    public void getLabelTest()
    {

        CoalesceField<?> field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_ACTION_NUMBER_PATH);

        assertEquals(CoalesceTypeInstances.TEST_MISSION_ACTION_NUMBER_LABEL, field.getLabel());

    }

    @Test
    public void getLabelDoesNotExistTest()
    {

        CoalesceField<?> field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

        assertEquals("", field.getLabel());

    }

    @Test
    public void setLabelTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = getTestMissionNameField(mission);

        field.setLabel("Testinglabel");

        CoalesceField<?> savedField = getSavedTestMissionField(mission);

        assertEquals("Testinglabel", savedField.getLabel());

    }

    @Test
    public void setLabelNullTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = getTestMissionNameField(mission);

        field.setLabel(null);

        CoalesceField<?> savedField = getSavedTestMissionField(mission);

        assertEquals("", savedField.getLabel());

    }

    @Test
    public void getModifiedByDoesNotExistTest()
    {

        CoalesceField<?> field = getTestMissionNameField();

        assertEquals("", field.getModifiedBy());

    }

    @Test
    public void setModifiedByTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = getTestMissionNameField(mission);

        field.setModifiedBy("TestingModifiedBy");

        CoalesceField<?> savedField = getSavedTestMissionField(mission);

        assertEquals("TestingModifiedBy", savedField.getModifiedBy());

    }

    @Test
    public void getModifiedByIpDoesNotExistTest()
    {

        CoalesceField<?> field = getTestMissionNameField();

        assertEquals("", field.getModifiedByIP());

    }

    @Test
    public void setModifiedByIpTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = getTestMissionNameField(mission);

        field.setModifiedByIP("192.168.2.2");

        CoalesceField<?> savedField = getSavedTestMissionField(mission);

        assertEquals("192.168.2.2", savedField.getModifiedByIP());

    }

    @Test
    public void getClassificationMarkingDefaultTest()
    {

        CoalesceField<?> field = getTestMissionNameField();

        Assert.assertEquals(new Marking().getClassification().getPortion(),
                            field.getClassificationMarking().getClassification().getPortion());

    }

    @Test
    public void getClassificationMarkingAfterSetAndSerializedTest()
    {
        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = getTestMissionNameField(mission);

        field.setClassificationMarking(TOPSECRETCLASSIFICATIONMARKING);

        CoalesceField<?> savedField = getSavedTestMissionField(mission);

        Assert.assertEquals(TOPSECRETCLASSIFICATIONMARKING.getClassification().getPortion(),
                            savedField.getClassificationMarking().getClassification().getPortion());

    }

    @Test
    public void setClassificationMarkingTopSecretTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) mission.getCoalesceObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

        field.setClassificationMarking(TOPSECRETCLASSIFICATIONMARKING);

        Assert.assertEquals(TOPSECRETCLASSIFICATIONMARKING.getClassification().getPortion(),
                            field.getClassificationMarking().getClassification().getPortion());
    }

    @Test
    public void setClassificationmarkingNullTest()
    {
        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) mission.getCoalesceObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

        field.setClassificationMarkingAsString(null);

        Assert.assertEquals(new Marking("(U)").getClassification().getPortion(),
                            field.getClassificationMarking().getClassification().getPortion());
    }

    @Test
    public void getValueWithMarkingTest()
    {

        CoalesceField<?> field = getTestMissionNameField();

        assertEquals("UNCLASSIFIED NORTHCOM Volunteer Background Checks Changed", field.getValueWithMarking());
    }

    @Test
    public void getPortionMarkingTest()
    {

        CoalesceField<?> field = getTestMissionNameField();

        assertEquals("(U)", field.getPortionMarking());
    }

    @Test
    public void getPreviousHistoryKeyNoneTest()
    {

        CoalesceField<?> field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        assertEquals("00000000-0000-0000-0000-000000000000", field.getPreviousHistoryKey());

    }

    @Test
    public void getPreviousHistoryKeyClassificationMarkingChangeTest()
    {

        CoalesceField<?> field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        assertEquals("00000000-0000-0000-0000-000000000000", field.getPreviousHistoryKey());

        String fieldKey = field.getKey();

        field.setClassificationMarking(TOPSECRETCLASSIFICATIONMARKING);

        assertEquals(fieldKey, field.getKey());
        assertEquals(field.getHistory()[0].getKey(), field.getPreviousHistoryKey());

    }

    @Test
    public void inputLangExistingEntityTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceStringField field = (CoalesceStringField) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionName");

        assertEquals(null, field.getInputLang());

        field.setInputLang(new Locale("en"));

        assertEquals(new Locale("en"), field.getInputLang());

        String entityXml = entity.toXml();

        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceStringField desField = (CoalesceStringField) desEntity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionName");

        assertEquals(new Locale("en"), desField.getInputLang());

    }

    @Test
    public void inputLangNewEntityTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", "1.2.3.4", "ID", "Type");
        CoalesceSection section = entity.createSection("NewSection");
        CoalesceRecordset recordset = section.createRecordset("NewRecordset");
        recordset.createFieldDefinition("NewField", ECoalesceFieldDataTypes.STRING_TYPE);

        CoalesceRecord record = recordset.addNew();

        CoalesceStringField field = (CoalesceStringField) record.getFieldByName("NewField");

        assertEquals(null, field.getInputLang());

        field.setInputLang(Locale.UK);

        String entityXml = entity.toXml();
        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceStringField desField = (CoalesceStringField) desEntity.getCoalesceObjectForNamePath(
                "Operation/NewSection/NewRecordset/NewRecordset Record/NewField");

        assertEquals(Locale.UK, desField.getInputLang());

        CoalesceEntity desEntityEmptyLocale = CoalesceEntity.create(entityXml.replace("en-GB", ""));
        CoalesceStringField desFieldEmtpyLocale = (CoalesceStringField) desEntityEmptyLocale.getCoalesceObjectForNamePath(
                "Operation/NewSection/NewRecordset/NewRecordset Record/NewField");

        assertEquals(null, desFieldEmtpyLocale.getInputLang());

        CoalesceEntity desEntityBadLocale = CoalesceEntity.create(entityXml.replace("en-GB", "engb"));
        CoalesceStringField desFieldBadLocale = (CoalesceStringField) desEntityBadLocale.getCoalesceObjectForNamePath(
                "Operation/NewSection/NewRecordset/NewRecordset Record/NewField");

        assertEquals(null, desFieldBadLocale.getInputLang());

    }

    @Test
    public void previousHistoryOrderTest() throws CoalesceDataFormatException
    {

        CoalesceField<?> field = CoalesceFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        field.setTypedValue(1111);
        field.setTypedValue(2222);

        assertEquals(2222, field.getIntegerValue().intValue());
        assertEquals(1111, field.getHistory()[0].getIntegerValue().intValue());
        assertEquals(CoalesceTypeInstances.TEST_MISSION_BASE64_VALUE, field.getHistory()[1].getIntegerValue().intValue());

    }

    @Test
    public void disableHistoryTest()
    {
        CoalesceEntity testEntity = CoalesceEntity.create("Test", "UnitTest", "1.0", "Testing", "UnitTesting");
        CoalesceSection testSection = testEntity.createSection("TestSection");
        CoalesceRecordset testRecordset = testSection.createRecordset("TestRecordset");
        testRecordset.createFieldDefinition("Track", ECoalesceFieldDataTypes.STRING_TYPE, "", "(U)", null);
        testRecordset.createFieldDefinition("NoTrack", ECoalesceFieldDataTypes.INTEGER_TYPE, "", "(TS)", "", true);

        CoalesceRecord testRecord = testRecordset.addNew();

        CoalesceStringField trackField = (CoalesceStringField) testRecord.getFieldByName("Track");
        CoalesceIntegerField noTrackField = (CoalesceIntegerField) testRecord.getFieldByName("NoTrack");

        trackField.setValue("Test one");
        trackField.setValue("Test two");

        assertEquals(1, trackField.getHistory().length);
        assertFalse(trackField.toXml().toLowerCase().contains("disablehistory"));

        noTrackField.setValue(1111);
        noTrackField.setValue(2222);

        assertTrue("Field with tracking disabled has history", noTrackField.getHistory().length == 0);

        noTrackField.setDisableHistory(false);

        assertFalse(noTrackField.toXml().toLowerCase().contains("disablehistory"));

        noTrackField.setValue(3333);
        noTrackField.setValue(4444);

        assertEquals(2, noTrackField.getHistory().length);

        noTrackField.setDisableHistory(true);

        assertEquals(2, noTrackField.getHistory().length);

        assertTrue(noTrackField.isDisableHistory());
        assertTrue(noTrackField.isSuspendHistory());

        noTrackField.setSuspendHistory(false);

        assertTrue(noTrackField.isSuspendHistory());

        String entityXml = testEntity.toXml();
        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);

        CoalesceIntegerField desNoTrackField = (CoalesceIntegerField) desEntity.getCoalesceObjectForNamePath(
                "Test/TestSection/TestRecordset/TestRecordset Record/NoTrack");
        assertEquals(2, desNoTrackField.getHistory().length);
        assertTrue(desNoTrackField.isDisableHistory());
        assertTrue(desNoTrackField.isSuspendHistory());

    }

    @Test
    public void getSuspendHistoryTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = getTestMissionNameField(mission);

        assertFalse(field.isSuspendHistory());

        field.setSuspendHistory(true);

        assertTrue(field.isSuspendHistory());

        field.setSuspendHistory(false);

        assertFalse(field.isSuspendHistory());
    }

    @Test
    public void setSuspendHistoryTrueTest() throws CoalesceDataFormatException
    {

        CoalesceField<?> field = CoalesceFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        field.setTypedValue(2222);

        assertEquals(2222, field.getIntegerValue().intValue());
        assertEquals(1, field.getHistory().length);
        assertEquals(CoalesceTypeInstances.TEST_MISSION_BASE64_VALUE, field.getHistory()[0].getIntegerValue().intValue());

    }

    @Test
    public void setSuspendHistoryFalseTest() throws CoalesceDataFormatException
    {

        CoalesceField<?> field = CoalesceFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        field.setSuspendHistory(true);
        field.setTypedValue(2222);

        assertEquals(2222, field.getIntegerValue().intValue());
        assertEquals(0, field.getHistory().length);

    }

    @Test
    public void setSuspendHistoryFalseBinaryTest()
    {

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "Binary",
                                                                              ECoalesceFieldDataTypes.BINARY_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        assertEquals(0, field.getHistory().length);

        field.setBaseValue("Something");

        assertEquals(0, field.getHistory().length);

    }

    @Test
    public void setSuspendHistoryFalseFileTest()
    {

        FileTestResult result = getJpgFile();

        CoalesceFileField field = (CoalesceFileField) result._savedField;
        assertEquals(0, field.getHistory().length);

        field.setBaseValue("Something");

        assertTrue(field.getHash().isEmpty());

    }

    @Test
    public void getHistoryRecordTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceStringField actionNumber = (CoalesceStringField) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/ActionNumber");

        CoalesceFieldHistory history = actionNumber.getHistoryRecord("00BB7A9F-4F37-46E9-85EB-9280ED3619CC");
        assertNotNull(history);
        assertEquals("00BB7A9F-4F37-46E9-85EB-9280ED3619CC", history.getKey());

        CoalesceFieldHistory badHistory = actionNumber.getHistoryRecord(UUID.randomUUID().toString());
        assertNull(badHistory);

    }

    @Test
    public void getDateCreatedExistTest()
    {

        CoalesceField<?> field = getTestMissionNameField();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_CREATED, field.getDateCreated());

    }

    @Test
    public void setDateCreatedTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = getTestMissionNameField(mission);

        DateTime now = JodaDateTimeHelper.nowInUtc();

        field.setDateCreated(now);

        CoalesceField<?> savedField = getSavedTestMissionField(mission);

        assertEquals(now, savedField.getDateCreated());

    }

    @Test
    public void getCoordinateTest()
    {

        try
        {
            // Create Mission Entity
            CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

            // Get Mission Location Field
            CoalesceField<?> field = getTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_LOCATION_PATH);

            // Get Field Value
            Coordinate coordinateTest = field.getCoordinateValue();

            // Validate Value
            assertTrue(-80.9363995 == coordinateTest.x);
            assertTrue(43.6616578 == coordinateTest.y);

            // Change Value
            field.setTypedValue(new Coordinate(5, 6));
            field.getPointValue();

            Point pointTest = field.getPointValue();

            assertTrue(pointTest.isValid());
            assertTrue(5 == pointTest.getX());
            assertTrue(6 == pointTest.getY());
        }
        catch (CoalesceDataFormatException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void getCoordinateListTest()
    {

        try
        {
            // Create Mission Entity
            CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

            // Get Mission Location Field
            CoalesceField<?> field = getTestMissionFieldByName(mission,
                                                               CoalesceTypeInstances.TEST_MISSION_LOCATION_LIST_PATH);

            // Create Coordinate List
            Coordinate coordinates[] = new Coordinate[2];
            coordinates[0] = new Coordinate(1, 2);
            coordinates[1] = new Coordinate(3, 4);

            // Set Coordinate Values
            field.setTypedValue(coordinates);

            // Get MultiPoint
            MultiPoint multipoint = field.getMultiPointValue();

            // Validate Number of Coordinates
            assertTrue(multipoint.getNumGeometries() == coordinates.length);

            // Validate Coordinates
            assertTrue(multipoint.getGeometryN(0).getCoordinate().equals2D(coordinates[0]));
            assertTrue(multipoint.getGeometryN(1).getCoordinate().equals2D(coordinates[1]));

        }
        catch (CoalesceDataFormatException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void getLastModifiedExistTest()
    {

        CoalesceField<?> field = getTestMissionNameField();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_MODIFIED, field.getLastModified());

    }

    @Test
    public void setLastModifiedTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = getTestMissionNameField(mission);

        DateTime now = JodaDateTimeHelper.nowInUtc();

        field.setLastModified(now);

        CoalesceField<?> savedField = getSavedTestMissionField(mission);

        assertEquals(now, savedField.getLastModified());

    }

    // @Test
    public void toXmlTest()
    {

        CoalesceField<?> field = getTestMissionNameField();

        String fieldXml = field.toXml().replace("\n", "").replace("\r", "").replace("    ", "");

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_XML, fieldXml);

    }

    @Test
    public void getCoalesceFilenameWithLastModifiedTagTwoSubDirTest() throws NoSuchMethodException, IllegalAccessException
    {

        getCoalesceFilenameWithLastModifiedTag();
    }

    @Test
    public void getCoalesceFilenameWithLastModifiedTagZeroSubDirTest() throws NoSuchMethodException, IllegalAccessException
    {

        CoalesceUnitTestSettings.setSubDirectoryLength(0);

        getCoalesceFilenameWithLastModifiedTag();
    }

    @Test
    public void getCoalesceFilenameWithLastModifiedTagFiveSubDirTest() throws NoSuchMethodException, IllegalAccessException
    {

        CoalesceUnitTestSettings.setSubDirectoryLength(5);

        getCoalesceFilenameWithLastModifiedTag();

    }

    private void getCoalesceFilenameWithLastModifiedTag() throws NoSuchMethodException, IllegalAccessException
    {

        FileTestResult result = getJpgFile();
        CoalesceFileField field = (CoalesceFileField) result._field;

        // Create file
        File fieldFile = new File(field.getCoalesceFullFilename());
        try
        {
            fieldFile.createNewFile();
        }
        catch (IOException e)
        {
            // Catch
        }

        assertEquals(fieldFile.getName() + "?" + fieldFile.lastModified(), field.getCoalesceFilenameWithLastModifiedTag());

        // Delete file
        fieldFile.delete();

    }

    @Test
    public void getCoalesceThumbnailFilenameWithLastModifiedTagTwoSubDirTest() throws NoSuchMethodException
    {
        getCoalesceThumbnailFilenameWithLastModifiedTag();
    }

    @Test
    public void getCoalesceThumbnailFilenameWithLastModifiedTagZeroSubDirTest() throws NoSuchMethodException
    {
        CoalesceUnitTestSettings.setSubDirectoryLength(0);

        getCoalesceThumbnailFilenameWithLastModifiedTag();
    }

    @Test
    public void getCoalesceThumbnailFilenameWithLastModifiedTagFiveSubDirTest() throws NoSuchMethodException
    {

        CoalesceUnitTestSettings.setSubDirectoryLength(5);

        getCoalesceThumbnailFilenameWithLastModifiedTag();

    }

    private void getCoalesceThumbnailFilenameWithLastModifiedTag() throws NoSuchMethodException
    {
        FileTestResult result = getJpgFile();
        CoalesceFileField field = (CoalesceFileField) result._field;

        // Create file
        File fieldFile = new File(field.getCoalesceFullThumbnailFilename());
        try
        {
            fieldFile.createNewFile();
        }
        catch (IOException e)
        {
            // Catch
        }

        assertEquals(fieldFile.getName() + "?" + fieldFile.lastModified(),
                     field.getCoalesceThumbnailFilenameWithLastModifiedTag());

        // Delete file
        fieldFile.delete();

    }

    @Test
    public void changeTest() throws CoalesceDataFormatException
    {
        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceStringField field = (CoalesceStringField) getTestMissionNameField(mission);

        field.change("NewValue", new Marking("(TS)"), "testinguser", "192.168.0.1");

        assertEquals("NewValue", field.getValue());
        assertEquals(new Marking("(TS)"), field.getClassificationMarking());
        assertEquals("testinguser", field.getModifiedBy());
        assertEquals("192.168.0.1", field.getModifiedByIP());

        field.change("NewValue", new Marking("(TS)"), "testinguser2", "192.168.0.2");

        assertEquals("NewValue", field.getValue());
        assertEquals(new Marking("(TS)"), field.getClassificationMarking());
        assertEquals("testinguser", field.getModifiedBy());
        assertEquals("192.168.0.1", field.getModifiedByIP());

        field.change("NewValue2", new Marking("(TS)"), "testinguser2", "192.168.0.2");

        assertEquals("NewValue2", field.getValue());
        assertEquals(new Marking("(TS)"), field.getClassificationMarking());
        assertEquals("testinguser2", field.getModifiedBy());
        assertEquals("192.168.0.2", field.getModifiedByIP());

        field.change("NewValue2", new Marking("(U)"), "testinguser3", "192.168.0.3");

        assertEquals("NewValue2", field.getValue());
        assertEquals(new Marking("(U)"), field.getClassificationMarking());
        assertEquals("testinguser3", field.getModifiedBy());
        assertEquals("192.168.0.3", field.getModifiedByIP());

    }

    @Test
    public void stringTypeTest() throws CoalesceDataFormatException
    {

        CoalesceField<?> field = getTestMissionNameField();

        Object data = field.getValue();

        assertTrue(data instanceof String);
        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_VALUE, data);

        field.setTypedValue("Changed");

        data = null;
        data = field.getValue();

        assertTrue(data instanceof String);
        assertEquals("Changed", data);
        assertEquals("Changed", field.getBaseValue());

    }

    @Test(expected = ClassCastException.class)
    public void setTypedValueStringTypeTypeMismatchTest()
    {

        CoalesceField<?> field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(UUID.randomUUID());

    }

    @Test
    public void uriTypeTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "Uri",
                                                                              ECoalesceFieldDataTypes.URI_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);
        field.setTypedValue("uri:document/pdf");

        Object data = field.getValue();

        assertTrue(data instanceof String);
        assertEquals("uri:document/pdf", data);
        assertEquals("uri:document/pdf", field.getBaseValue());

    }

    /**
     * This test ensures that if a date field has either never been set or
     * nulled out it will return null when attempting to read the value.
     *
     * @throws Exception
     */
    @Test
    public void getDataDateTimeTypeNotSetTest() throws Exception
    {
        TestEntity entity = new TestEntity();
        entity.initialize();

        TestRecord record = entity.addRecord1();

        assertNull(record.getDateField().getValue());

        record.getDateField().setValue(null);

        assertNull(record.getDateField().getValue());
    }

    @Test
    public void getDataSetTypedValueDateTimeTypeTest() throws CoalesceDataFormatException
    {
        CoalesceField<?> field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        Object data = field.getValue();

        assertTrue(data instanceof DateTime);
        assertEquals(JodaDateTimeHelper.fromXmlDateTimeUTC(CoalesceTypeInstances.TEST_MISSION_START_TIME_VALUE), data);

        DateTime now = JodaDateTimeHelper.nowInUtc();
        field.setTypedValue(now);

        assertEquals(JodaDateTimeHelper.toXmlDateTimeUTC(now), field.getBaseValue());

        data = null;
        data = field.getValue();

        assertTrue(data instanceof DateTime);
        assertEquals(now, data);
        assertEquals(now, field.getDateTimeValue());

    }

    @Test(expected = ClassCastException.class)
    public void setTypedValueDateTimeTypeTypeMismatchTest()
    {

        CoalesceField<?> field = getTestMissionNameField();

        field.setTypedValue(JodaDateTimeHelper.nowInUtc());

    }

    @Test
    public void getDataBinaryTypeNotSetTest() throws Exception
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "Binary",
                                                                              ECoalesceFieldDataTypes.BINARY_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceBinaryField field = (CoalesceBinaryField) CoalesceField.create(parentRecord, fileFieldDef);

        assertNull(field.getValue());
    }

    @Test
    public void getDataSetTypedValueBinaryTypeTest() throws UnsupportedEncodingException, CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "Binary",
                                                                              ECoalesceFieldDataTypes.BINARY_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceBinaryField field = (CoalesceBinaryField) CoalesceField.create(parentRecord, fileFieldDef);

        String byteString = "Testing String";
        byte[] dataBytes = byteString.getBytes("US-ASCII");
        field.setValue(dataBytes);

        Object data = field.getValue();

        assertTrue(data instanceof byte[]);
        assertArrayEquals(dataBytes, (byte[]) data);
        assertArrayEquals(dataBytes, field.getValue());
        assertEquals("VGVzdGluZyBTdHJpbmc=", field.getBaseValue());

    }

    @Test
    public void setTypedValueFileBytesTest() throws Exception
    {
        CoalesceFileField field = getFileField();

        String filePath = CoalesceUnitTestSettings.getResourceAbsolutePath("TestDocument.docx");
        byte[] dataBytes = Files.readAllBytes(Paths.get(filePath));

        field.setValue(dataBytes, "TestDocument.docx", "docx");

        assertFileField(field, dataBytes);

    }

    @Test
    public void setTypedValueDocPropsTest() throws Exception
    {
        CoalesceFileField field = getFileField();

        String filePath = CoalesceUnitTestSettings.getResourceAbsolutePath("TestDocument.docx");
        byte[] dataBytes = Files.readAllBytes(Paths.get(filePath));

        DocumentProperties docProps = new DocumentProperties();
        docProps.initialize(filePath);

        field.setValue(dataBytes, docProps);

        assertFileField(field, dataBytes);

    }

    @Test
    public void getDataSetTypedValueBooleanTypeTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "Boolean",
                                                                              ECoalesceFieldDataTypes.BOOLEAN_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        assertNull(field.getBooleanValue());
        assertNull(field.getBaseValue());

        field.setTypedValue(true);

        Object data = field.getValue();

        assertTrue(data instanceof Boolean);
        assertEquals(true, data);
        assertEquals("true", field.getBaseValue().toLowerCase());
        assertEquals(true, field.getBooleanValue());

    }

    @Test(expected = ClassCastException.class)
    public void setTypedValueBooleanTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        CoalesceField<?> field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(true);

    }

    public void getDataIntegerTypeNotSetTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "Integer",
                                                                              ECoalesceFieldDataTypes.INTEGER_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        assertNull(field.getIntegerValue());

    }

    @Test
    public void getDataSetTypedValueIntegerTypeTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "Integer",
                                                                              ECoalesceFieldDataTypes.INTEGER_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        assertNull(field.getBaseValue());

        field.setTypedValue(1111);

        Object data = field.getValue();

        assertTrue(data instanceof Integer);
        assertEquals(1111, data);
        assertEquals("1111", field.getBaseValue());
        assertEquals(1111, field.getIntegerValue().intValue());

    }

    @Test(expected = ClassCastException.class)
    public void setTypedValueIntgerTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        CoalesceField<?> field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(1111);

    }

    @Test
    public void getDataGuidTypeNotSetTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "GUID",
                                                                              ECoalesceFieldDataTypes.GUID_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        assertNull(field.getGuidValue());

    }

    @Test
    public void getDataSetTypedValueGuidTypeTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "GUID",
                                                                              ECoalesceFieldDataTypes.GUID_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        assertNull(field.getBaseValue());

        UUID guid = UUID.randomUUID();
        field.setTypedValue(guid);

        Object data = field.getValue();

        assertTrue(data instanceof UUID);
        assertEquals(guid, data);
        assertEquals(guid.toString(), field.getBaseValue());
        assertEquals(guid, field.getGuidValue());

    }

    @Test(expected = ClassCastException.class)
    public void setTypedValueGUIDTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        CoalesceField<?> field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(UUID.randomUUID());

    }

    @Test
    public void getDataGeocoordinateTypeNotSetTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "Location",
                                                                              ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        assertNull(field.getCoordinateValue());

    }

    @Test
    public void getDataSetTypedValueGeolocationTypeTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        assertEquals("POINT (-80.9363995 43.6616578)", field.getBaseValue());

        Object data = field.getValue();

        assertTrue(data instanceof Coordinate);

        Coordinate location = (Coordinate) data;

        assertEquals(-80.9363995, location.x, 0.00001);
        assertEquals(43.6616578, location.y, 0.00001);

        Coordinate pentagon = new Coordinate(38.87116000, -77.05613800);
        field.setTypedValue(pentagon);

        String entityXml = entity.toXml();
        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);

        CoalesceField<?> desField = (CoalesceField<?>) desEntity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        Coordinate desLocation = desField.getCoordinateValue();

        assertEquals(pentagon, desLocation);
        assertEquals("POINT Z(38.87116 -77.056138 0)", field.getBaseValue());
    }

    @Test
    public void geolocationPointTests() throws Exception
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        DocumentProperties docProps = new DocumentProperties();
        String filePath = CoalesceUnitTestSettings.getResourceAbsolutePath("Desert.jpg");
        docProps.initialize(filePath);

        field.setTypedValue(new Coordinate(docProps.getLongitude(), docProps.getLatitude()));
        assertEquals("POINT Z(8.67243350003624 49.39875240003339 0)", field.getBaseValue());

        field.setTypedValue(new Coordinate(0, 0));
        assertEquals("POINT Z(0 0 0)", field.getBaseValue());

        field.setTypedValue(new Coordinate(-90, -90));
        assertEquals("POINT Z(-90 -90 0)", field.getBaseValue());

        field.setTypedValue(new Coordinate(90, 90));
        assertEquals("POINT Z(90 90 0)", field.getBaseValue());

        field.setTypedValue(new Coordinate(90, 0));
        assertEquals("POINT Z(90 0 0)", field.getBaseValue());

        field.setTypedValue(new Coordinate(-90, 0));
        assertEquals("POINT Z(-90 0 0)", field.getBaseValue());

        field.setTypedValue(new Coordinate(0, 90));
        assertEquals("POINT Z(0 90 0)", field.getBaseValue());

        field.setTypedValue(new Coordinate(0, -90));
        assertEquals("POINT Z(0 -90 0)", field.getBaseValue());

        field.setTypedValue(new Coordinate(-77.05613800, 38.87116000));
        assertEquals("POINT Z(-77.056138 38.87116 0)", field.getBaseValue());

    }

    /**
     * This test ensures that the Z-Axis is being set and read correctly.
     *
     * @throws Exception
     */
    @Test
    public void coordinateZAxisTest() throws Exception
    {

        Point point;

        // Construct Record
        CoalesceRecord record = createRecord("Z-Axis", ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE);

        // Get Field
        CoalesceCoordinateField field = (CoalesceCoordinateField) record.getFieldByName(ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE.toString());

        assertEquals(null, field.getBaseValue());

        // Verify a Coordinate w/ Z-Axis Specified
        field.setValue(new Coordinate(1, 2, 3));
        point = field.getValueAsPoint();

        assertEquals("POINT Z(1 2 3)", field.getBaseValue());
        assertEquals(1, point.getX(), 0);
        assertEquals(2, point.getY(), 0);
        assertEquals(3, point.getCoordinate().z, 0);
        assertEquals(point.getCoordinate(), field.getCoordinateValue());

        // Verify a Coordinate w/o Z-Axis Specified
        field.setValue(new Coordinate(4, 5));
        point = field.getValueAsPoint();

        assertEquals("POINT Z(4 5 0)", field.getBaseValue());
        assertEquals(4, point.getX(), 0);
        assertEquals(5, point.getY(), 0);
        assertEquals(0, point.getCoordinate().z, 0);
        assertEquals(point.getCoordinate(), field.getCoordinateValue());

        // Verify a Coordinate Using Lat / Long
        field.setValue(7, 6);
        point = field.getValueAsPoint();

        assertEquals("POINT Z(6 7 0)", field.getBaseValue());
        assertEquals(6, point.getX(), 0);
        assertEquals(7, point.getY(), 0);
        assertEquals(0, point.getCoordinate().z, 0);
        assertEquals(point.getCoordinate(), field.getCoordinateValue());

        // Verify Setting Field to Null
        field.setValue((Point) null);

        assertEquals("", field.getBaseValue());

    }

    /**
     * This test ensures that when the Z-Axis is retricted that NaN will throw
     * an error
     *
     * @throws Exception
     */
    @Test
    public void coordinateZAxisRestrictedTest() throws Exception
    {

        // Construct Record
        CoalesceRecord record = createRecord("Z-Axis", ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE);

        // Get Field
        CoalesceCoordinateField field = (CoalesceCoordinateField) record.getFieldByName(ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE.toString());

        try
        {

            // Restrict Z Axis
            CoalesceSettings.EAxis.Z.setRestricted(true);
            CoalesceSettings.EAxis.Z.setMin(0);
            CoalesceSettings.EAxis.Z.setMax(10);

            try
            {
                // Z = NaN
                field.setValue(new Coordinate(1, 2));

                fail("Expected CoalesceDataFormatException");
            }
            catch (CoalesceDataFormatException e)
            {
                // Passed
            }

            try
            {
                // Z = NaN
                field.setValue(new Coordinate(1, 2 - 1));

                fail("Expected CoalesceDataFormatException");
            }
            catch (CoalesceDataFormatException e)
            {
                // Passed
            }

            field.setValue(new Coordinate(1, 2, 5));
        }
        finally
        {
            CoalesceSettings.EAxis.Z.setRestricted(false);
        }

    }

    /**
     * This test ensures that setting the x or y axis to NaN throw an exception.
     *
     * @throws Exception
     */
    @Test(expected = CoalesceDataFormatException.class)
    public void coordinateNaNTest() throws Exception
    {
        // Construct Record
        CoalesceRecord record = createRecord("Z-Axis", ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE);

        // Get Field
        CoalesceCoordinateField field = (CoalesceCoordinateField) record.getFieldByName(ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE.toString());

        // Verify a Coordinate Using Lat / Long
        field.setValue(Double.NaN, Double.NaN);
        Point point = field.getValueAsPoint();

        assertEquals("POINT (NaN NaN)", field.getBaseValue());
        assertEquals(Double.NaN, point.getX(), 0);
        assertEquals(Double.NaN, point.getY(), 0);
        assertEquals(Double.NaN, point.getCoordinate().z, 0);

    }

    /**
     * This test ensures that if you provide no coordinates its marked as empty.
     *
     * @throws Exception
     */
    @Test
    public void coordinateEmptyTest() throws Exception
    {

        // Construct Record
        CoalesceRecord record = createRecord("Z-Axis", ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE);

        // Get Field
        CoalesceCoordinateField field = (CoalesceCoordinateField) record.getFieldByName(ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE.toString());

        // Verify a Coordinate Using Lat / Long
        field.setValue(new Point(null, new GeometryFactory()));
        Point point = field.getValueAsPoint();

        assertEquals("POINT EMPTY", field.getBaseValue());
        assertTrue(point.isEmpty());

    }

    /**
     * This test ensures that if you provide no coordinates its marked as empty.
     *
     * @throws Exception
     */
    @Test
    public void coordinateListEmptyTest() throws Exception
    {

        // Construct Record
        CoalesceRecord record = createRecord("Z-Axis", ECoalesceFieldDataTypes.GEOCOORDINATE_LIST_TYPE);

        // Get Field
        CoalesceCoordinateListField field = (CoalesceCoordinateListField) record.getFieldByName(ECoalesceFieldDataTypes.GEOCOORDINATE_LIST_TYPE.toString());

        // Verify a Coordinate Using Lat / Long
        field.setValue(new Coordinate[] { null, null
        });
        MultiPoint point = field.getValueAsMultiPoint();

        assertEquals("MULTIPOINT EMPTY", field.getBaseValue());
        assertTrue(point.isEmpty());

    }

    /**
     * This test ensures that the Z-Axis is being set and read correctly for
     * MultiPoints
     *
     * @throws Exception
     */
    @Test
    public void coordinateListZAxisTest() throws Exception
    {

        Coordinate[] coords;

        // Construct Record
        CoalesceRecord record = createRecord("Z-Axis", ECoalesceFieldDataTypes.GEOCOORDINATE_LIST_TYPE);

        // Get Field
        CoalesceCoordinateListField field = (CoalesceCoordinateListField) record.getFieldByName(ECoalesceFieldDataTypes.GEOCOORDINATE_LIST_TYPE.toString());

        assertEquals(null, field.getBaseValue());

        // Verify
        field.setValue(new Coordinate[] { new Coordinate(1, 2, 3), new Coordinate(4, 5)
        });
        coords = field.getCoordinateListValue();

        assertEquals("MULTIPOINT Z((1 2 3), (4 5 0))", field.getBaseValue());
        assertEquals(1, coords[0].x, 0);
        assertEquals(2, coords[0].y, 0);
        assertEquals(3, coords[0].z, 0);
        assertEquals(4, coords[1].x, 0);
        assertEquals(5, coords[1].y, 0);
        assertEquals(0, coords[1].z, 0);
        assertArrayEquals(coords, field.getValueAsMultiPoint().getCoordinates());

        // Verify Setting Field to Null
        field.setValue((MultiPoint) null);

        assertEquals("", field.getBaseValue());

    }

    @Test
    public void coordinateLatitudeToLargeTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setTypedValue(new Coordinate(180.0000000000001, 0));

    }

    @Test
    public void coordinateLatitudeToSmallTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setTypedValue(new Coordinate(-180.0000000000001, 0));

    }

    @Test
    public void coordinateLongitudeToLargeTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setTypedValue(new Coordinate(0, 180.0000000000001));

    }

    @Test
    public void coordinateLongitudeToSmallTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setTypedValue(new Coordinate(0, -180.0000000000001));

    }

    @Test
    public void coordinateBothTooLargeTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setTypedValue(new Coordinate(180.0000000000001, 180.0000000000001));

    }

    @Test
    public void coordinateBothTooSmallTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setTypedValue(new Coordinate(-180.0000000000001, -180.0000000000001));

    }

    @Test
    public void parseCoordinateTest() throws CoalesceDataFormatException
    {

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setBaseValue("POINT (8.67243350003624 49.39875240003339)");
        assertEquals(new Coordinate(8.67243350003624, 49.39875240003339), field.getValue());

        field.setBaseValue("POINT ( 0 0)");
        assertEquals(new Coordinate(0, 0), field.getValue());

        field.setBaseValue("POINT (-90 -90 )");
        assertEquals(new Coordinate(-90, -90), field.getValue());

        field.setBaseValue("POINT (90 90)");
        assertEquals(new Coordinate(90, 90), field.getValue());

        field.setBaseValue("POINT (90 0)");
        assertEquals(new Coordinate(90, 0), field.getValue());

        field.setBaseValue("POINT (-90 0)");
        assertEquals(new Coordinate(-90, 0), field.getValue());

        field.setBaseValue("POINT (0 90)");
        assertEquals(new Coordinate(0, 90), field.getValue());

        field.setBaseValue("POINT (0 -90)");
        assertEquals(new Coordinate(0, -90), field.getValue());

        field.setBaseValue("POINT (-77.056138 38.87116)");
        assertEquals(new Coordinate(-77.05613800, 38.87116000), field.getValue());

    }

    @Test
    public void parseCoordinateLatitudeToLargeTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setBaseValue("POINT (0 180.0000000000001)");

        field.getValue();
    }

    @Test
    public void parseCoordinateLatitudeToSmallTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setBaseValue("POINT (0 -180.0000000000001)");

        field.getValue();
    }

    @Test
    public void parseCoordinateLongitudeToLargeTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setBaseValue("POINT (180.0000000000001 0)");

        field.getValue();
    }

    @Test
    public void parseCoordinateLongitudeToSmallTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setBaseValue("POINT (-180.0000000000001 0)");

        field.getValue();
    }

    @Test
    public void parseCoordinateBothTooLargeTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setBaseValue("POINT (180.0000000000001 180.0000000000001)");

        field.getValue();
    }

    @Test
    public void parseCoordinateBothTooSmallTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setBaseValue("POINT (-180.0000000000001 -180.0000000000001)");

        field.getValue();
    }

    @Test
    public void parseCoordinateMissingLeftParenTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setBaseValue("POINT 0 0)");

        field.getValue();
    }

    @Test
    public void parseCoordinateMissingRightParenTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setBaseValue("POINT (0 0");

        field.getValue();
    }

    @Test
    public void parseCoordinateMissingBothParenTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setBaseValue("POINT 0 0");

        field.getValue();
    }

    @Test
    public void parseCoordinateMissingSpaceTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setBaseValue("POINT(0 0)");

        Coordinate location = field.getCoordinateValue();

        assertEquals(new Coordinate(0, 0), location);

    }

    @Test
    public void parseCoordinateMissingPOINTTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setBaseValue("(0 0)");

        field.getValue();
    }

    @Test
    public void parseCoordinateLatitudeNotNumberTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setBaseValue("POINT (X 0)");

        field.getValue();
    }

    @Test
    public void parseGeolocatioLongitudeNotNumberTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setBaseValue("POINT (0 Y)");

        field.getValue();
    }

    @Test
    public void parseCoordinateBothNotNumberTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setBaseValue("POINT (X Y)");

        field.getValue();
    }

    @Test
    public void parseCoordinateMissingValueTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setBaseValue("POINT (0)");

        field.getValue();
    }

    @Test
    public void parseCoordinateNullTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setBaseValue(null);

        assertNull(field.getValue());
    }

    @Test
    public void parseCoordinateEmptyTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setBaseValue("");

        assertNull(field.getValue());
    }

    @Test
    public void parseCoordinateWhiteSpaceTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(FIELD_LOCATION);

        field.setBaseValue("  ");

        assertNull(field.getValue());
    }

    @Test
    public void setTypedValueGeolocationTypeTypeMismatchTest()
            throws UnsupportedEncodingException, CoalesceDataFormatException
    {
        _thrown.expect(ClassCastException.class);
        _thrown.expectMessage("Type mismatch");

        CoalesceField<?> field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(new Coordinate());

    }

    @Test
    public void setTypedValueGeolocationTypeTypeMismatchGeolocationsListTest()
            throws UnsupportedEncodingException, CoalesceDataFormatException
    {
        _thrown.expect(ClassCastException.class);
        _thrown.expectMessage("Type mismatch");

        CoalesceField<?> field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_LOCATION_LIST_PATH);

        field.setTypedValue(new Coordinate());

    }

    @Test
    public void parseCoordinateMultipointTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((-70.6280916 34.6873833), (-77.056138 38.87116))");

        Coordinate[] locations = field.getCoordinateListValue();

        Coordinate[] expected = new Coordinate[2];
        expected[0] = new Coordinate(-70.6280916, 34.6873833);
        expected[1] = new Coordinate(-77.056138, 38.87116);

        assertArrayEquals(expected, locations);

    }

    @Test
    public void parseCoordinateMultipointSingleTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((-70.6280916 34.6873833))");

        Coordinate[] locations = field.getCoordinateListValue();

        Coordinate[] expected = new Coordinate[1];
        expected[0] = new Coordinate(-70.6280916, 34.6873833);

        assertArrayEquals(expected, locations);

    }

    @Test
    public void pareseCoordinateMultipointNoneTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT EMPTY");

        Coordinate[] locations = field.getCoordinateListValue();

        Coordinate[] expected = new Coordinate[0];

        assertArrayEquals(expected, locations);

    }

    @Test
    public void parseCoordinateMultipointLatitudeToLargeTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((0 180.0000000000001), (0 0))");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointLatitudeToSmallTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((0 0), (0 -180.0000000000001))");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointLongitudeToLargeTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((180.0000000000001 0), (0 0))");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointLongitudeToSmallTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((0 0), (-180.0000000000001 0))");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointBothTooLargeTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((180.0000000000001 180.0000000000001), (0 0))");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointBothTooSmallTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((0 0), (-180.0000000000001 -180.0000000000001))");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointMissingLeftParenTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        // _thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT (0 0), (90 90))");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointMissingRightParenTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        // _thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((0 0), (90 90)");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointMissingPointParenTest() throws CoalesceDataFormatException
    {
        TestEntity entity = new TestEntity();
        entity.initialize();
        TestRecord record = entity.addRecord1();

        record.getGeoListField().setBaseValue("MULTIPOINT (0 0, (89 9))");

        Assert.assertEquals(2, record.getGeoListField().getValue().length);
        Assert.assertEquals(0, record.getGeoListField().getValue()[0].x, 0);
        Assert.assertEquals(0, record.getGeoListField().getValue()[0].y, 0);
        Assert.assertEquals(89, record.getGeoListField().getValue()[1].x, 0);
        Assert.assertEquals(9, record.getGeoListField().getValue()[1].y, 0);
    }

    @Test
    public void parseCoordinateMultipointMissingSpaceTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT((0 0))");

        Coordinate[] locations = field.getCoordinateListValue();

        Coordinate[] expected = new Coordinate[1];
        expected[0] = new Coordinate(0, 0);

        assertArrayEquals(expected, locations);

    }

    @Test
    public void parseCoordinateMultipointMissingMULTIPOINTTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        // _thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("((0 0), (90 90))");
        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointLatitudeNotNumberTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        // _thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((X 0), (90 90))");
        field.getValue();
    }

    @Test
    public void parseGeolocatioMultipointLongitudeNotNumberTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        // _thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((90 90), (0 Y)");
        field.getValue();
    }

    @Test
    public void parseCoordinateMultipointBothNotNumberTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        // _thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((X Y), (0 0))");
        field.getValue();
    }

    @Test
    public void parseCoordinateMultipointMissingValueTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        // _thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((0), (0 0))");
        field.getValue();

    }

    @Test
    public void setTypedValueGeolocationListTypeTypeMismatchTest()
            throws UnsupportedEncodingException, CoalesceDataFormatException
    {
        _thrown.expect(ClassCastException.class);
        _thrown.expectMessage("Type mismatch");

        CoalesceField<?> field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(new Coordinate[] { new Coordinate(0, 0)
        });

    }

    @Test
    public void setTypedValueGeolocationListTypeTypeMismatchGeolocationsTest()
            throws UnsupportedEncodingException, CoalesceDataFormatException
    {
        _thrown.expect(ClassCastException.class);
        _thrown.expectMessage("Type mismatch");

        CoalesceField<?> field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_LOCATION_PATH);

        field.setTypedValue(new Coordinate[] { new Coordinate(0, 0)
        });

    }

    @Test
    public void toXmlTest2()
    {
        CoalesceStringField field = (CoalesceStringField) getTestMissionNameField();
        String fieldXml = field.toXml();

        Field desField = (Field) XmlHelper.deserialize(fieldXml, Field.class);

        assertEquals(field.getKey(), desField.getKey());
        assertEquals(field.getName(), desField.getName());
        assertEquals(field.getDateCreated(), desField.getDatecreated());
        assertEquals(field.getLastModified(), desField.getLastmodified());
        assertEquals(field.getDataType(), ECoalesceFieldDataTypes.getTypeForCoalesceType(desField.getDatatype()));
        assertEquals(field.getClassificationMarking(), new Marking(desField.getClassificationmarking()));
        assertEquals(field.getLabel(), desField.getLabel());
        assertEquals(field.getValue(), desField.getValue());
        assertEquals(field.getInputLang(), desField.getInputlang());
        assertEquals(field.getStatus(), desField.getStatus());

    }

    @Test
    public void setStatusTest()
    {
        CoalesceStringField field = (CoalesceStringField) getTestMissionNameField();

        assertEquals(ECoalesceObjectStatus.ACTIVE, field.getStatus());

        field.setStatus(ECoalesceObjectStatus.UNKNOWN);
        String fieldXml = field.toXml();

        Field desField = (Field) XmlHelper.deserialize(fieldXml, Field.class);

        assertEquals(ECoalesceObjectStatus.UNKNOWN, desField.getStatus());

    }

    @Test
    public void attributeTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceStringField field = (CoalesceStringField) getTestMissionNameField(entity);

        int before = field.getAttributes().size();

        field.setAttribute("TestAttribute", "TestingValue");

        assertEquals(before + 1, field.getAttributes().size());

        assertEquals("TestingValue", field.getAttribute("TestAttribute"));

        assertEquals("MissionName", field.getName());
        assertEquals(CoalesceObject.ATTRIBUTE_NOINDEX_DEFAULT, field.isNoIndex());

        field.setAttribute("Name", "TestingName");
        field.getFieldDefinition().setName(field.getName());

        assertEquals("TestingName", field.getName());
        assertEquals("TestingName", field.getAttribute("Name"));

        UUID guid = UUID.randomUUID();
        field.setAttribute("Key", guid.toString());
        assertEquals(guid.toString(), field.getKey());
        assertEquals(guid.toString(), field.getAttribute("Key"));

        DateTime now = JodaDateTimeHelper.nowInUtc();
        DateTime future = now.plusDays(2);

        field.setAttribute("DateCreated", JodaDateTimeHelper.toXmlDateTimeUTC(now));
        assertEquals(now, field.getDateCreated());

        field.setAttribute("NoIndex", "True");
        assertEquals(true, field.isNoIndex());

        field.setAttribute("DataType", "Integer");
        field.getFieldDefinition().setDataType(ECoalesceFieldDataTypes.INTEGER_TYPE);

        assertEquals(ECoalesceFieldDataTypes.INTEGER_TYPE, field.getDataType());

        field.setAttribute("Classificationmarking", "(TS)");
        assertEquals(new Marking("(TS)"), field.getClassificationMarking());

        field.setAttribute("Label", "labelTest");
        assertEquals("labelTest", field.getLabel());

        field.setAttribute("Value", "123");
        assertEquals("123", field.getValue());

        field.setAttribute("InputLang", "");
        assertEquals(null, field.getInputLang());

        field.setAttribute("InputLang", "en-GB");
        assertEquals(Locale.UK, field.getInputLang());

        field.setAttribute("Status", ECoalesceObjectStatus.UNKNOWN.toString());
        assertEquals(ECoalesceObjectStatus.UNKNOWN, field.getStatus());

        field.setAttribute("LastModified", JodaDateTimeHelper.toXmlDateTimeUTC(future));
        assertEquals(future, field.getLastModified());

        String entityXml = entity.toXml();
        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceIntegerField desField = (CoalesceIntegerField) desEntity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/TestingName");

        assertEquals("TestingValue", desField.getAttribute("TestAttribute"));
        assertEquals("TestingName", desField.getName());
        assertEquals(guid.toString(), desField.getKey());
        assertEquals(now, desField.getDateCreated());
        assertEquals(future, desField.getLastModified());
        assertEquals(true, desField.isNoIndex());
        assertEquals(ECoalesceFieldDataTypes.INTEGER_TYPE, desField.getDataType());
        assertEquals(new Marking("(TS)"), desField.getClassificationMarking());
        assertEquals("labelTest", desField.getLabel());
        assertEquals(new Integer(123), desField.getValue());
        assertEquals(Locale.UK, field.getInputLang());
        assertEquals(ECoalesceObjectStatus.UNKNOWN, desField.getStatus());

    }

    @Test
    public void setAttributeInputLangInvalidCaseTest() throws CoalesceDataFormatException
    {
        _thrown.expect(IllegalArgumentException.class);
        _thrown.expectMessage("");

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceStringField field = (CoalesceStringField) getTestMissionNameField(entity);

        field.setAttribute("InputLang", "en-gb");

    }

    @Test
    public void setAttributeInputLangMissingDashTest() throws CoalesceDataFormatException
    {
        _thrown.expect(IllegalArgumentException.class);
        _thrown.expectMessage("");

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceStringField field = (CoalesceStringField) getTestMissionNameField(entity);

        field.setAttribute("InputLang", "engb");

    }

    @Test
    public void nullFieldTest() throws CoalesceDataFormatException
    {

        // Verify Initial Values
        assertNull(record.getBinaryField().getValue());
        assertNull(record.getBooleanField().getValue());
        assertNull(record.getDateField().getValue());
        assertNull(record.getFileField().getValue());
        assertNull(record.getFloatField().getValue());
        assertNull(record.getGeoListField().getValue());
        assertNull(record.getGeoField().getValue());
        assertNull(record.getGuidField().getValue());
        assertNull(record.getIntegerField().getValue());
        assertNull(record.getStringField().getValue());
        assertNull(record.getURIField().getValue());
        assertNull(record.getLongField().getValue());

        // Set to Null
        record.getBinaryField().setValue(null);
        record.getBooleanField().setValue(null);
        record.getDateField().setValue(null);
        record.getFileField().setValue(null);
        record.getFloatField().setValue(null);
        record.getGeoListField().setValue((MultiPoint) null);
        record.getGeoListField().setValue((Coordinate[]) null);
        record.getGeoField().setValue((Point) null);
        record.getGeoField().setValue((Coordinate) null);
        record.getGuidField().setValue(null);
        record.getIntegerField().setValue(null);
        record.getStringField().setValue(null);
        record.getURIField().setValue(null);
        record.getLongField().setValue(null);

        // Verify Values (Values once set cannot be cleared)
        assertTrue(record.getBinaryField().getValue().length == 0);
        assertNull(record.getBooleanField().getValue());
        assertNull(record.getDateField().getValue());
        assertNull(record.getFileField().getValue());
        assertNull(record.getFloatField().getValue());
        assertNull(record.getGeoListField().getValue());
        assertNull(record.getGeoField().getValue());
        assertNull(record.getGuidField().getValue());
        assertNull(record.getIntegerField().getValue());
        assertTrue(StringHelper.isNullOrEmpty(record.getStringField().getValue()));
        assertTrue(StringHelper.isNullOrEmpty(record.getURIField().getValue()));
        assertNull(record.getLongField().getValue());

    }

    @Test
    public void polygonTest() throws Exception
    {

        // Create Entity
        TestEntity entity = new TestEntity();
        entity.initialize();

        // Create Record
        CoalescePolygonField field = entity.addRecord1().getPolygonField();

        // Create Polygon
        GeometricShapeFactory factory = new GeometricShapeFactory();
        factory.setSize(10);
        factory.setNumPoints(4);
        factory.setCentre(new Coordinate(0, 0));
        Polygon shape = factory.createCircle();

        field.setValue(shape);

        // Verify
        assertEquals(new WKTWriter(3).write(shape), field.getBaseValue());

    }

    @Test
    public void lineStringTest() throws Exception
    {

        // Create Entity
        TestEntity entity = new TestEntity();
        entity.initialize();

        // Create Record
        CoalesceLineStringField field = entity.addRecord1().getLineField();

        // Create Polygon
        GeometryFactory factory = new GeometryFactory();
        LineString line = factory.createLineString(new Coordinate[] { new Coordinate(0, 0), new Coordinate(1, 1),
                                                                      new Coordinate(2, 2)
        });

        field.setValue(line);

        // Verify
        assertEquals(new WKTWriter(3).write(line), field.getBaseValue());

    }

    @Test
    public void circleTest() throws Exception
    {

        // Create Entity
        TestEntity entity = new TestEntity();
        entity.initialize();

        // Create Record
        CoalesceCircleField field = entity.addRecord1().getCircleField();

        // Create Polygon
        Point center = new GeometryFactory().createPoint(new Coordinate(0, 0));

        field.setValue(center, 5);

        // Verify
        assertEquals(new WKTWriter(3).write(center), field.getBaseValue());
        assertEquals(5, Double.valueOf(field.getAttribute("radius")), 0);

    }

    // -----------------------------------------------------------------------//
    // Public Static Methods
    // -----------------------------------------------------------------------//

    public static CoalesceField<?> getTestMissionNameField()
    {

        return getTestMissionNameField(CoalesceTypeInstances.TEST_MISSION);

    }

    public static CoalesceField<?> getTestMissionFieldByName(String fieldPath)
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        return getTestMissionFieldByName(mission, fieldPath);

    }

    // -----------------------------------------------------------------------//
    // Private Static Methods
    // -----------------------------------------------------------------------//

    private static void initializeSettings()
    {

        CoalesceUnitTestSettings.setSubDirectoryLength(2);

    }

    private static CoalesceField<?> getTestMissionNameField(String entityXml)
    {

        CoalesceEntity entity = CoalesceEntity.create(entityXml);

        return getTestMissionNameField(entity);

    }

    private static CoalesceField<?> getTestMissionNameField(CoalesceEntity entity)
    {

        return getTestMissionFieldByName(entity, CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

    }

    private static CoalesceField<?> getTestMissionFieldByName(CoalesceEntity entity, String fieldPath)
    {

        CoalesceObject fdo = entity.getCoalesceObjectForNamePath(fieldPath);

        assertTrue(fdo instanceof CoalesceField<?>);

        return (CoalesceField<?>) fdo;

    }

    private static CoalesceField<?> getSavedTestMissionField(CoalesceEntity entity)
    {

        String serializedMission = entity.toXml();

        return getTestMissionNameField(serializedMission);

    }

    private FileTestResult getJpgFile()
    {

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "File",
                                                                              ECoalesceFieldDataTypes.FILE_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceFileField fileField = (CoalesceFileField) CoalesceField.create(parentRecord, fileFieldDef);
        fileField.setExtension("jpg");

        String savedEntity = entity.toXml();

        CoalesceEntity desEntity = CoalesceEntity.create(savedEntity);

        CoalesceField<?> savedFileField = (CoalesceField<?>) desEntity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/File");

        return new FileTestResult(fileField, savedFileField);

    }

    private class FileTestResult {

        public CoalesceField<?> _field;
        public CoalesceField<?> _savedField;

        public FileTestResult(CoalesceField<?> fieldParam, CoalesceField<?> savedFieldParam)
        {
            _field = fieldParam;
            _savedField = savedFieldParam;
        }
    }

    private CoalesceFileField getFileField()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "File",
                                                                              ECoalesceFieldDataTypes.FILE_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        assertTrue(field instanceof CoalesceFileField);

        return (CoalesceFileField) field;

    }

    private void assertFileField(CoalesceFileField field, byte[] dataBytes) throws CoalesceDataFormatException
    {
        assertTrue(field.getValue() instanceof DocumentProperties);

        byte[] fieldBytes = Base64.decode(field.getBaseValue());
        assertArrayEquals(dataBytes, fieldBytes);
        // TODO File fields store thier data externally
        // assertArrayEquals(dataBytes, field.getBinaryValue());

        assertEquals("TestDocument.docx", field.getFilename());
        assertEquals("docx", field.getExtension());
        assertEquals(MimeHelper.getMimeTypeForExtension("docx"), field.getMimeType());
        assertEquals(dataBytes.length, field.getSize());

    }

    /**
     * @param name
     * @param types
     * @return a record with fields of the specified types.
     */
    private CoalesceRecord createRecord(String name, ECoalesceFieldDataTypes... types)
    {

        CoalesceEntity entity = CoalesceEntity.create(name, "Unit Test", "1.0", null, null);
        CoalesceRecordset recordset = CoalesceRecordset.create(CoalesceSection.create(entity, name), name);

        for (ECoalesceFieldDataTypes type : types)
        {
            CoalesceFieldDefinition.create(recordset, type.toString(), type);
        }

        return recordset.addNew();

    }
}
