package com.incadencecorp.coalesce.framework.datamodel;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

import org.apache.xerces.impl.dv.util.Base64;
import org.jdom2.JDOMException;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.drew.imaging.ImageProcessingException;
import com.incadencecorp.coalesce.common.CoalesceTypeInstances;
import com.incadencecorp.coalesce.common.CoalesceUnitTestSettings;
import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.classification.MarkingValueTest;
import com.incadencecorp.coalesce.common.exceptions.CoalesceCryptoException;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.helpers.DocumentProperties;
import com.incadencecorp.coalesce.common.helpers.GUIDHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.MimeHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;
import com.incadencecorp.coalesce.framework.generatedjaxb.Field;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

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

public class CoalesceFieldTest {

    @Rule
    public ExpectedException _thrown = ExpectedException.none();

    private static final Marking TOPSECRETCLASSIFICATIONMARKING = new Marking("//JOINT TOP SECRET AND USA//FOUO-LES//SBU/ACCM-BOB");
    private static final String COORDINATE_ERROR_MESSAGE = "Failed to parse coordinate value for: MissionGeoLocation";
    private static final String COORDINATES_ERROR_MESSAGE = "Failed to parse coordinates value for: MissionGeoLocation";
    private static final String POINT_ERROR_MESSAGE = "Failed to parse point value for: MissionGeoLocation";

    @BeforeClass
    public static void setUpBeforeClass() throws IOException
    {
        CoalesceUnitTestSettings.initialize();

        initializeSettings();
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

        String serializedMission = mission.toXml();
        CoalesceEntity savedMission = CoalesceEntity.create(serializedMission);

        CoalesceField<?> savedField = (CoalesceField<?>) savedMission.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH.replace(CoalesceTypeInstances.TEST_MISSION_NAME_NAME,
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
    public void getSizeDoesNotExistTest()
    {
        CoalesceField<?> field = getTestMissionNameField();

        assertEquals(0, field.getSize());

    }

    @Test
    public void setSizeTest()
    {
        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = getTestMissionNameField(mission);
        field.setSize(128);

        String serializedMission = mission.toXml();
        CoalesceEntity savedMission = CoalesceEntity.create(serializedMission);

        CoalesceField<?> savedField = getTestMissionNameField(savedMission);
        assertEquals(128, savedField.getSize());

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

        MarkingValueTest.assertMarkingValue(new Marking().getClassification(),
                                            field.getClassificationMarking().getClassification());

    }

    @Test
    public void getClassificationMarkingAfterSetAndSerializedTest()
    {
        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = getTestMissionNameField(mission);

        field.setClassificationMarking(TOPSECRETCLASSIFICATIONMARKING);

        CoalesceField<?> savedField = getSavedTestMissionField(mission);

        MarkingValueTest.assertMarkingValue(TOPSECRETCLASSIFICATIONMARKING.getClassification(),
                                            savedField.getClassificationMarking().getClassification());

    }

    @Test
    public void setClassificationMarkingTopSecretTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) mission.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

        field.setClassificationMarking(TOPSECRETCLASSIFICATIONMARKING);

        MarkingValueTest.assertMarkingValue(TOPSECRETCLASSIFICATIONMARKING.getClassification(),
                                            field.getClassificationMarking().getClassification());

    }

    @Test
    public void setClassificationmarkingNullTest()
    {
        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) mission.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

        field.setClassificationMarking((String) null);

        MarkingValueTest.assertMarkingValue(new Marking("(U)").getClassification(),
                                            field.getClassificationMarking().getClassification());

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
        assertEquals(field.getHistory().get(0).getKey(), field.getPreviousHistoryKey());

    }

    @Test
    public void getFilenameDoesNotExistTest()
    {

        CoalesceField<?> field = getTestMissionNameField();

        assertEquals("", field.getFilename());

    }

    @Test
    public void setFilenameTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = getTestMissionNameField(mission);

        field.setFilename("c:/Program Files/java/jre7/bin");

        CoalesceField<?> savedField = getSavedTestMissionField(mission);

        assertEquals("c:/Program Files/java/jre7/bin", savedField.getFilename());

        field.setFilename(null);

        assertEquals("", field.getFilename());

    }

    @Test
    public void getExtensionDoesNotExistTest()
    {

        CoalesceField<?> field = getTestMissionNameField();

        assertEquals("", field.getExtension());

    }

    @Test
    public void setExtensionTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = getTestMissionNameField(mission);

        field.setExtension(".jpeg");

        CoalesceField<?> savedField = getSavedTestMissionField(mission);

        assertEquals("jpeg", savedField.getExtension());

        field.setExtension(null);

        assertEquals("", field.getExtension());

    }

    @Test
    public void getMimeTypeDoesNotExistTest()
    {

        CoalesceField<?> field = getTestMissionNameField();

        assertEquals("", field.getMimeType());

    }

    @Test
    public void setMimeTypeTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = getTestMissionNameField(mission);

        field.setMimeType("application/pdf");

        CoalesceField<?> savedField = getSavedTestMissionField(mission);

        assertEquals("application/pdf", savedField.getMimeType());

    }

    @Test
    public void getHashDoesNotExistTest()
    {

        CoalesceField<?> field = getTestMissionNameField();

        assertEquals("", field.getHash());

    }

    @Test
    public void setHashTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = getTestMissionNameField(mission);

        field.setHash("8743b52063cd84097a65d1633f5c74f5");

        CoalesceField<?> savedField = getSavedTestMissionField(mission);

        assertEquals("8743b52063cd84097a65d1633f5c74f5", savedField.getHash());

        field.setHash(null);

        assertEquals("", field.getHash());

    }

    @Test
    public void inputLangExistingEntityTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceStringField field = (CoalesceStringField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionName");

        assertEquals(null, field.getInputLang());

        field.setInputLang(new Locale("en"));

        assertEquals(new Locale("en"), field.getInputLang());

        String entityXml = entity.toXml();

        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceStringField desField = (CoalesceStringField) desEntity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionName");

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
        CoalesceStringField desField = (CoalesceStringField) desEntity.getDataObjectForNamePath("Operation/NewSection/NewRecordset/NewRecordset Record/NewField");

        assertEquals(Locale.UK, desField.getInputLang());

        CoalesceEntity desEntityEmptyLocale = CoalesceEntity.create(entityXml.replace("en-GB", ""));
        CoalesceStringField desFieldEmtpyLocale = (CoalesceStringField) desEntityEmptyLocale.getDataObjectForNamePath("Operation/NewSection/NewRecordset/NewRecordset Record/NewField");

        assertEquals(null, desFieldEmtpyLocale.getInputLang());

        CoalesceEntity desEntityBadLocale = CoalesceEntity.create(entityXml.replace("en-GB", "engb"));
        CoalesceStringField desFieldBadLocale = (CoalesceStringField) desEntityBadLocale.getDataObjectForNamePath("Operation/NewSection/NewRecordset/NewRecordset Record/NewField");

        assertEquals(null, desFieldBadLocale.getInputLang());

    }

    @Test
    public void PreviousHistoryOrderTest() throws CoalesceDataFormatException
    {

        CoalesceField<?> field = CoalesceFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        field.setTypedValue(1111);
        field.setTypedValue(2222);

        assertEquals(2222, field.getIntegerValue().intValue());
        assertEquals(1111, field.getHistory().get(0).getIntegerValue().intValue());
        assertEquals(CoalesceTypeInstances.TEST_MISSION_BASE64_VALUE, field.getHistory().get(1).getIntegerValue().intValue());

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

        assertEquals(1, trackField.getHistory().size());
        assertFalse(trackField.toXml().toLowerCase().contains("disablehistory"));

        noTrackField.setValue(1111);
        noTrackField.setValue(2222);

        assertTrue("Field with tracking disabled has history", noTrackField.getHistory().isEmpty());

        noTrackField.setDisableHistory(false);

        assertFalse(noTrackField.toXml().toLowerCase().contains("disablehistory"));

        noTrackField.setValue(3333);
        noTrackField.setValue(4444);

        assertEquals(2, noTrackField.getHistory().size());

        noTrackField.setDisableHistory(true);

        assertEquals(2, noTrackField.getHistory().size());

        assertTrue(noTrackField.isDisableHistory());
        assertTrue(noTrackField.isSuspendHistory());

        noTrackField.setSuspendHistory(false);

        assertTrue(noTrackField.isSuspendHistory());

        String entityXml = testEntity.toXml();
        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);

        CoalesceIntegerField desNoTrackField = (CoalesceIntegerField) desEntity.getDataObjectForNamePath("Test/TestSection/TestRecordset/TestRecordset Record/NoTrack");
        assertEquals(2, desNoTrackField.getHistory().size());
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
        assertEquals(1, field.getHistory().size());
        assertEquals(CoalesceTypeInstances.TEST_MISSION_BASE64_VALUE, field.getHistory().get(0).getIntegerValue().intValue());

    }

    @Test
    public void setSuspendHistoryFalseTest() throws CoalesceDataFormatException
    {

        CoalesceField<?> field = CoalesceFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        field.setSuspendHistory(true);
        field.setTypedValue(2222);

        assertEquals(2222, field.getIntegerValue().intValue());
        assertTrue(field.getHistory().isEmpty());

    }

    @Test
    public void setSuspendHistoryFalseBinaryTest()
    {

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "Binary",
                                                                              ECoalesceFieldDataTypes.BINARY_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        assertTrue(field.getHistory().isEmpty());

        field.setBaseValue("Something");

        assertTrue(field.getHistory().isEmpty());

    }

    @Test
    public void setSuspendHistoryFalseFileTest()
    {

        FileTestResult result = getJpgFile();

        CoalesceField<?> field = result.SavedField;
        assertTrue(field.getHistory().isEmpty());

        field.setBaseValue("Something");

        assertTrue(field.getHash().isEmpty());

    }

    @Test
    public void getHistoryRecordTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceStringField actionNumber = (CoalesceStringField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/ActionNumber");

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
            assertTrue(((Point) multipoint.getGeometryN(0)).getCoordinate().equals2D(coordinates[0]));
            assertTrue(((Point) multipoint.getGeometryN(1)).getCoordinate().equals2D(coordinates[1]));

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

    @Test
    public void ToXmlTest()
    {

        CoalesceField<?> field = getTestMissionNameField();

        String fieldXml = field.toXml();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_XML,
                     fieldXml.replace("\n", "").replace("\r", "").replace("    ", ""));

    }

    @Test
    public void getCoalesceFullFilenameNotFileTest()
    {
        CoalesceField<?> field = getTestMissionNameField();

        assertTrue(StringHelper.isNullOrEmpty(field.getCoalesceFullFilename()));
    }

    @Test
    public void getCoalesceFullFilenameTwoSubDirTest() throws NoSuchMethodException, IllegalAccessException,
             InvocationTargetException
    {

        getCoalesceFullFilename();
    }

    @Test
    public void getCoalesceFullFilenameZeroSubDirTest() throws NoSuchMethodException, IllegalAccessException,
             InvocationTargetException
    {

        CoalesceUnitTestSettings.setSubDirectoryLength(0);

        getCoalesceFullFilename();
    }

    @Test
    public void getCoalesceFullFilenameFiveSubDirTest() throws NoSuchMethodException, IllegalAccessException,
             InvocationTargetException
    {

        CoalesceUnitTestSettings.setSubDirectoryLength(5);

        getCoalesceFullFilename();

    }

    private void getCoalesceFullFilename() throws NoSuchMethodException, 
            InvocationTargetException, IllegalAccessException
    {

        FileTestResult result = getJpgFile();

        assertEquals(CoalesceFieldCommon.CallGetBaseFilenameWithFullDirectoryPathForKey(result.Field.getKey(), false)
                + ".jpg", result.SavedField.getCoalesceFullFilename());

    }

    @Test
    public void getCoalesceFullThumbnailFilenameNotFileTest()
    {
        CoalesceField<?> field = getTestMissionNameField();

        assertTrue(StringHelper.isNullOrEmpty(field.getCoalesceFullThumbnailFilename()));
    }

    @Test
    public void getCoalesceFullThumbnailFilenameTwoSubDirTest() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException
    {

        getCoalesceFullThumbnailFilename();
    }

    @Test
    public void getCoalesceFullThumbnailFilenameZeroSubDirTest() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException
    {

        CoalesceUnitTestSettings.setSubDirectoryLength(0);

        getCoalesceFullThumbnailFilename();
    }

    @Test
    public void getCoalesceFullThumbnailFilenameFiveSubDirTest() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException
    {

        CoalesceUnitTestSettings.setSubDirectoryLength(5);

        getCoalesceFullThumbnailFilename();

    }

    private void getCoalesceFullThumbnailFilename() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException
    {

        FileTestResult result = getJpgFile();

        assertEquals(CoalesceFieldCommon.CallGetBaseFilenameWithFullDirectoryPathForKey(result.Field.getKey(), false)
                + "_thumb.jpg", result.SavedField.getCoalesceFullThumbnailFilename());

    }

    @Test
    public void getCoalesceFilenameWithLastModifiedTagNotFileTest()
    {
        CoalesceField<?> field = getTestMissionNameField();

        assertTrue(StringHelper.isNullOrEmpty(field.getCoalesceFilenameWithLastModifiedTag()));
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

        // Create file
        File fieldFile = new File(result.Field.getCoalesceFullFilename());
        try
        {
            fieldFile.createNewFile();
        }
        catch (IOException e)
        {
            // Catch
        }

        assertEquals(fieldFile.getName() + "?" + fieldFile.lastModified(),
                     result.SavedField.getCoalesceFilenameWithLastModifiedTag());

        // Delete file
        fieldFile.delete();

    }

    @Test
    public void getCoalesceThumbnailFilenameWithLastModifiedTagNotFileTest()
    {
        CoalesceField<?> field = getTestMissionNameField();

        assertTrue(StringHelper.isNullOrEmpty(field.getCoalesceThumbnailFilenameWithLastModifiedTag()));
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

        // Create file
        File fieldFile = new File(result.Field.getCoalesceFullThumbnailFilename());
        try
        {
            fieldFile.createNewFile();
        }
        catch (IOException e)
        {
            // Catch
        }

        assertEquals(fieldFile.getName() + "?" + fieldFile.lastModified(),
                     result.SavedField.getCoalesceThumbnailFilenameWithLastModifiedTag());

        // Delete file
        fieldFile.delete();

    }

    @Test
    public void getCoalesceFilenameNotFileTest()
    {
        CoalesceField<?> field = getTestMissionNameField();

        assertTrue(StringHelper.isNullOrEmpty(field.getCoalesceFilename()));
    }

    @Test
    public void getCoalesceFilenameTest()
    {

        FileTestResult result = getJpgFile();

        assertEquals(GUIDHelper.removeBrackets(result.Field.getKey()) + ".jpg", result.SavedField.getCoalesceFilename());

    }

    @Test
    public void getCoalesceThumbnailFilenameNotFileTest()
    {
        CoalesceField<?> field = getTestMissionNameField();

        assertTrue(StringHelper.isNullOrEmpty(field.getCoalesceThumbnailFilename()));
    }

    @Test
    public void getCoalesceThumbnailFilename()
    {

        FileTestResult result = getJpgFile();

        assertEquals(GUIDHelper.removeBrackets(result.Field.getKey()) + "_thumb.jpg",
                     result.SavedField.getCoalesceThumbnailFilename());

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
    public void StringTypeTest() throws CoalesceDataFormatException
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

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
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

    @Test
    public void getDataDateTimeTypeNotSetTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "DateTime",
                                                                              ECoalesceFieldDataTypes.DATE_TIME_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        assertNull(field.getDateTimeValue());

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
    public void getDataBinaryTypeNotSetTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "Binary",
                                                                              ECoalesceFieldDataTypes.BINARY_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        assertNull(field.getBinaryValue());
    }

    @Test
    public void getDataSetTypedValueBinaryTypeTest() throws UnsupportedEncodingException, CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "Binary",
                                                                              ECoalesceFieldDataTypes.BINARY_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        String byteString = "Testing String";
        byte[] dataBytes = byteString.getBytes("US-ASCII");
        field.setTypedValue(dataBytes);

        Object data = field.getValue();

        assertTrue(data instanceof byte[]);
        assertArrayEquals(dataBytes, (byte[]) data);
        assertArrayEquals(dataBytes, field.getBinaryValue());
        assertEquals("VGVzdGluZyBTdHJpbmc=", field.getBaseValue());

    }

    @Test(expected = ClassCastException.class)
    public void setTypedValueBinaryTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        CoalesceField<?> field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        String byteString = "Testing String";
        byte[] dataBytes = byteString.getBytes("US-ASCII");
        field.setTypedValue(dataBytes);

    }

    @Test
    public void setTypedValueFileBytesTest() throws CoalesceDataFormatException, ImageProcessingException, IOException,
            JDOMException, CoalesceCryptoException
    {
        CoalesceFileField field = getFileField();

        byte[] dataBytes = Files.readAllBytes(Paths.get("src\\test\\resources\\TestDocument.docx"));

        field.setTypedValue(dataBytes, "TestDocument.docx", "docx");

        assertFileField(field, dataBytes);

    }

    @Test
    public void setTypedValueDocPropsTest() throws IOException, ImageProcessingException, JDOMException,
            CoalesceDataFormatException, CoalesceCryptoException
    {
        CoalesceFileField field = getFileField();

        byte[] dataBytes = Files.readAllBytes(Paths.get("src\\test\\resources\\TestDocument.docx"));

        DocumentProperties docProps = new DocumentProperties();
        docProps.initialize("src\\test\\resources\\TestDocument.docx");

        field.setTypedValue(dataBytes, docProps);

        assertFileField(field, dataBytes);

    }

    @Test
    public void getDataSetTypedValueBooleanTypeTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "Boolean",
                                                                              ECoalesceFieldDataTypes.BOOLEAN_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        assertFalse(field.getBooleanValue());
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

    @Test(expected = CoalesceDataFormatException.class)
    public void getDataIntegerTypeNotSetTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "Integer",
                                                                              ECoalesceFieldDataTypes.INTEGER_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        field.getIntegerValue();

    }

    @Test
    public void getDataSetTypedValueIntegerTypeTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
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

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
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

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
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
        assertEquals(GUIDHelper.getGuidString(guid), field.getBaseValue());
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

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
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

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        assertEquals("POINT (-80.9363995 43.6616578)", field.getBaseValue());

        Object data = (Coordinate) field.getValue();

        assertTrue(data instanceof Coordinate);

        Coordinate location = (Coordinate) data;

        assertEquals(-80.9363995, location.x, 0.00001);
        assertEquals(43.6616578, location.y, 0.00001);

        Coordinate pentagon = new Coordinate(38.87116000, -77.05613800);
        field.setTypedValue(pentagon);

        String entityXml = entity.toXml();
        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);

        CoalesceField<?> desField = (CoalesceField<?>) desEntity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        Coordinate desLocation = desField.getCoordinateValue();

        assertEquals(pentagon, desLocation);
        assertEquals("POINT ( 38.87116 -77.056138 )", field.getBaseValue());
    }

    @Test
    public void geolocationPointTests() throws ImageProcessingException, CoalesceCryptoException, IOException,
            JDOMException, CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        DocumentProperties docProps = new DocumentProperties();
        docProps.initialize("src\\test\\resources\\desert.jpg");

        field.setTypedValue(new Coordinate(docProps.getLongitude(), docProps.getLatitude()));
        assertEquals("POINT ( 8.67243350003624 49.39875240003339 )", field.getBaseValue());

        field.setTypedValue(new Coordinate(0, 0));
        assertEquals("POINT ( 0.0 0.0 )", field.getBaseValue());

        field.setTypedValue(new Coordinate(-90, -90));
        assertEquals("POINT ( -90.0 -90.0 )", field.getBaseValue());

        field.setTypedValue(new Coordinate(90, 90));
        assertEquals("POINT ( 90.0 90.0 )", field.getBaseValue());

        field.setTypedValue(new Coordinate(90, 0));
        assertEquals("POINT ( 90.0 0.0 )", field.getBaseValue());

        field.setTypedValue(new Coordinate(-90, 0));
        assertEquals("POINT ( -90.0 0.0 )", field.getBaseValue());

        field.setTypedValue(new Coordinate(0, 90));
        assertEquals("POINT ( 0.0 90.0 )", field.getBaseValue());

        field.setTypedValue(new Coordinate(0, -90));
        assertEquals("POINT ( 0.0 -90.0 )", field.getBaseValue());

        field.setTypedValue(new Coordinate(-77.05613800, 38.87116000));
        assertEquals("POINT ( -77.056138 38.87116 )", field.getBaseValue());

    }

    @Test
    public void coordinateLatitudeToLargeTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setTypedValue(new Coordinate(180.0000000000001, 0));

    }

    @Test
    public void coordinateLatitudeToSmallTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setTypedValue(new Coordinate(-180.0000000000001, 0));

    }

    @Test
    public void coordinateLongitudeToLargeTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setTypedValue(new Coordinate(0, 180.0000000000001));

    }

    @Test
    public void coordinateLongitudeToSmallTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setTypedValue(new Coordinate(0, -180.0000000000001));

    }

    @Test
    public void coordinateBothTooLargeTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setTypedValue(new Coordinate(180.0000000000001, 180.0000000000001));

    }

    @Test
    public void coordinateBothTooSmallTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setTypedValue(new Coordinate(-180.0000000000001, -180.0000000000001));

    }

    @Test
    public void parseCoordinateTest() throws CoalesceDataFormatException
    {

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT (8.67243350003624 49.39875240003339)");
        assertEquals(new Coordinate(8.67243350003624, 49.39875240003339), field.getValue());

        field.setBaseValue("POINT (0 0)");
        assertEquals(new Coordinate(0, 0), field.getValue());

        field.setBaseValue("POINT (-90 -90)");
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

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT (0 180.0000000000001)");

        field.getValue();
    }

    @Test
    public void parseCoordinateLatitudeToSmallTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT (0 -180.0000000000001)");

        field.getValue();
    }

    @Test
    public void parseCoordinateLongitudeToLargeTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT (180.0000000000001 0)");

        field.getValue();
    }

    @Test
    public void parseCoordinateLongitudeToSmallTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT (-180.0000000000001 0)");

        field.getValue();
    }

    @Test
    public void parseCoordinateBothTooLargeTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT (180.0000000000001 180.0000000000001)");

        field.getValue();
    }

    @Test
    public void parseCoordinateBothTooSmallTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT (-180.0000000000001 -180.0000000000001)");

        field.getValue();
    }

    @Test
    public void parseCoordinateMissingLeftParenTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT 0 0)");

        field.getValue();
    }

    @Test
    public void parseCoordinateMissingRightParenTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT (0 0");

        field.getValue();
    }

    @Test
    public void parseCoordinateMissingBothParenTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT 0 0");

        field.getValue();
    }

    @Test
    public void parseCoordinateMissingSpaceTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

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

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("(0 0)");

        field.getValue();
    }

    @Test
    public void parseCoordinateLatitudeNotNumberTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT (X 0)");

        field.getValue();
    }

    @Test
    public void parseGeolocatioLongitudeNotNumberTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT (0 Y)");

        field.getValue();
    }

    @Test
    public void parseCoordinateBothNotNumberTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT (X Y)");

        field.getValue();
    }

    @Test
    public void parseCoordinateMissingValueTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT (0)");

        field.getValue();
    }

    @Test
    public void parseCoordinateNullTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue(null);

        assertNull(field.getValue());
    }

    @Test
    public void parseCoordinateEmptyTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("");

        assertNull(field.getValue());
    }

    @Test
    public void parseCoordinateWhiteSpaceTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("  ");

        assertNull(field.getValue());
    }

    @Test
    public void setTypedValueGeolocationTypeTypeMismatchTest() throws UnsupportedEncodingException,
            CoalesceDataFormatException
    {
        _thrown.expect(ClassCastException.class);
        _thrown.expectMessage("Type mismatch");

        CoalesceField<?> field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(new Coordinate());

    }

    @Test
    public void setTypedValueGeolocationTypeTypeMismatchGeolocationsListTest() throws UnsupportedEncodingException,
            CoalesceDataFormatException
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

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

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

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

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

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

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

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((0 180.0000000000001), (0 0))");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointLatitudeToSmallTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((0 0), (0 -180.0000000000001))");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointLongitudeToLargeTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((180.0000000000001 0), (0 0))");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointLongitudeToSmallTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((0 0), (-180.0000000000001 0))");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointBothTooLargeTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((180.0000000000001 180.0000000000001), (0 0))");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointBothTooSmallTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        _thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((0 0), (-180.0000000000001 -180.0000000000001))");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointMissingLeftParenTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        //_thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT (0 0), (90 90))");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointMissingRightParenTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        //_thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((0 0), (90 90)");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointMissingBothParenTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        //_thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT (0 0)");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointMissingPointParenTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        //_thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT (0 0, (89 9))");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointMissingSpaceTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

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
        //_thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("((0 0), (90 90))");
        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointLatitudeNotNumberTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        //_thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((X 0), (90 90))");
        field.getValue();
    }

    @Test
    public void parseGeolocatioMultipointLongitudeNotNumberTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        //_thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((90 90), (0 Y)");
        field.getValue();
    }

    @Test
    public void parseCoordinateMultipointBothNotNumberTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        //_thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((X Y), (0 0))");
        field.getValue();
    }

    @Test
    public void parseCoordinateMultipointMissingValueTest() throws CoalesceDataFormatException
    {
        _thrown.expect(CoalesceDataFormatException.class);
        //_thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((0), (0 0))");
        field.getValue();

    }

    @Test
    public void setTypedValueGeolocationListTypeTypeMismatchTest() throws UnsupportedEncodingException,
            CoalesceDataFormatException
    {
        _thrown.expect(ClassCastException.class);
        _thrown.expectMessage("Type mismatch");

        CoalesceField<?> field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(new Coordinate[] { new Coordinate(0, 0) });

    }

    @Test
    public void setTypedValueGeolocationListTypeTypeMismatchGeolocationsTest() throws UnsupportedEncodingException,
            CoalesceDataFormatException
    {
        _thrown.expect(ClassCastException.class);
        _thrown.expectMessage("Type mismatch");

        CoalesceField<?> field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_LOCATION_PATH);

        field.setTypedValue(new Coordinate[] { new Coordinate(0, 0) });

    }

    @Test
    public void toXmlTest()
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
        assertEquals(field.getStatus(), ECoalesceDataObjectStatus.getTypeForLabel(desField.getStatus()));

    }

    @Test
    public void setStatusTest()
    {
        CoalesceStringField field = (CoalesceStringField) getTestMissionNameField();

        assertEquals(ECoalesceDataObjectStatus.ACTIVE, field.getStatus());

        field.setStatus(ECoalesceDataObjectStatus.UNKNOWN);
        String fieldXml = field.toXml();

        Field desField = (Field) XmlHelper.deserialize(fieldXml, Field.class);

        assertEquals(ECoalesceDataObjectStatus.UNKNOWN, ECoalesceDataObjectStatus.getTypeForLabel(desField.getStatus()));

    }

    @Test
    public void attributeTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceStringField field = (CoalesceStringField) getTestMissionNameField(entity);
        field.setAttribute("TestAttribute", "TestingValue");

        assertEquals(10, field.getAttributes().size());

        assertEquals("TestingValue", field.getAttribute("TestAttribute"));

        assertEquals("MissionName", field.getName());
        assertEquals(false, field.getNoIndex());

        field.setAttribute("Name", "TestingName");
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
        assertEquals(true, field.getNoIndex());

        field.setAttribute("DataType", "Integer");
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

        field.setAttribute("Status", ECoalesceDataObjectStatus.UNKNOWN.getLabel());
        assertEquals(ECoalesceDataObjectStatus.UNKNOWN, field.getStatus());

        field.setAttribute("LastModified", JodaDateTimeHelper.toXmlDateTimeUTC(future));
        assertEquals(future, field.getLastModified());

        String entityXml = entity.toXml();
        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceIntegerField desField = (CoalesceIntegerField) desEntity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/TestingName");

        assertEquals("TestingValue", desField.getAttribute("TestAttribute"));
        assertEquals("TestingName", desField.getName());
        assertEquals(guid.toString(), desField.getKey());
        assertEquals(now, desField.getDateCreated());
        assertEquals(future, desField.getLastModified());
        assertEquals(true, desField.getNoIndex());
        assertEquals(ECoalesceFieldDataTypes.INTEGER_TYPE, desField.getDataType());
        assertEquals(new Marking("(TS)"), desField.getClassificationMarking());
        assertEquals("labelTest", desField.getLabel());
        assertEquals(new Integer(123), desField.getValue());
        assertEquals(Locale.UK, field.getInputLang());
        assertEquals(ECoalesceDataObjectStatus.UNKNOWN, desField.getStatus());

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

        CoalesceDataObject fdo = entity.getDataObjectForNamePath(fieldPath);

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

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "File",
                                                                              ECoalesceFieldDataTypes.FILE_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> fileField = CoalesceField.create(parentRecord, fileFieldDef);
        fileField.setExtension("jpg");

        String savedEntity = entity.toXml();

        CoalesceEntity desEntity = CoalesceEntity.create(savedEntity);

        CoalesceField<?> savedFileField = (CoalesceField<?>) desEntity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/File");

        return new FileTestResult(fileField, savedFileField);

    }

    private class FileTestResult {

        public CoalesceField<?> Field;
        public CoalesceField<?> SavedField;

        public FileTestResult(CoalesceField<?> field, CoalesceField<?> savedField)
        {
            Field = field;
            SavedField = savedField;
        }
    }

    private CoalesceFileField getFileField()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
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
        assertArrayEquals(dataBytes, field.getBinaryValue());

        assertEquals("TestDocument.docx", field.getFilename());
        assertEquals("docx", field.getExtension());
        assertEquals(MimeHelper.getMimeTypeForExtension("docx"), field.getMimeType());
        assertEquals(dataBytes.length, field.getSize());

    }

}
