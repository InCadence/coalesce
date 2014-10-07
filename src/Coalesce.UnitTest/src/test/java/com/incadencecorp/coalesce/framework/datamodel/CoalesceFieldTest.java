package com.incadencecorp.coalesce.framework.datamodel;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.jdom2.JDOMException;
import org.joda.time.DateTime;
import org.junit.After;
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
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
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
    public ExpectedException thrown = ExpectedException.none();

    private static final Marking TOPSECRETCLASSIFICATIONMARKING = new Marking("//JOINT TOP SECRET AND USA//FOUO-LES//SBU/ACCM-BOB");
    private static final String COORDINATE_ERROR_MESSAGE = "Failed to parse coordinate value for: MissionGeoLocation";
    private static final String COORDINATES_ERROR_MESSAGE = "Failed to parse coordinates value for: MissionGeoLocation";
    private static final String POINT_ERROR_MESSAGE = "Failed to parse point value for: MissionGeoLocation";

    @BeforeClass
    public static void setUpBeforeClass()
    {
        initializeSettings();
    }

    /*
     * @AfterClass public static void tearDownAfterClass() throws Exception { }
     * 
     * @Before public void setUp() throws Exception { }
     */

    @After
    public void tearDown()
    {
        initializeSettings();
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
    public void setValueTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = getTestMissionNameField(mission);

        field.setBaseValue("Testingvalue");

        CoalesceField<?> savedField = getSavedTestMissionField(mission);

        assertEquals("Testingvalue", savedField.getBaseValue());

    }

    @Test
    public void getDataType()
    {
        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> stringField = getTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
        assertEquals(ECoalesceFieldDataTypes.StringType, stringField.getDataType());

        CoalesceField<?> dateField = getTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);
        assertEquals(ECoalesceFieldDataTypes.DateTimeType, dateField.getDataType());

        CoalesceField<?> integerField = getTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);
        assertEquals(ECoalesceFieldDataTypes.IntegerType, integerField.getDataType());

    }

    /*
    @Test
    public void setDateType()
    {
        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField<?> MissionNameField = getTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
        XsdField<?> dateField = getTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);
        XsdField<?> integerField = getTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        String serializedMission = mission.toXml();
        XsdEntity savedMission = XsdEntity.create(serializedMission);

        XsdField<?> savedStringField = getTestMissionFieldByName(savedMission, CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
        assertEquals(ECoalesceFieldDataTypes.DateTimeType, savedStringField.getDataType());

        XsdField<?> savedDateField = getTestMissionFieldByName(savedMission, CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);
        assertEquals(ECoalesceFieldDataTypes.IntegerType, savedDateField.getDataType());

        XsdField<?> savedIntegerField = getTestMissionFieldByName(savedMission, CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);
        assertEquals(ECoalesceFieldDataTypes.StringType, savedIntegerField.getDataType());

    }
    */

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
    public void getSuspendHistoryTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = getTestMissionNameField(mission);

        assertFalse(field.getSuspendHistory());

        field.setSuspendHistory(true);

        assertTrue(field.getSuspendHistory());

        field.setSuspendHistory(false);

        assertFalse(field.getSuspendHistory());
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
                                                                    ECoalesceFieldDataTypes.BinaryType);

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
            CoalesceField<?> field = getTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_LOCATION_LIST_PATH);

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
    public void getCoalesceFullFilenameTwoSubDirTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        getCoalesceFullFilename();
    }

    @Test
    public void getCoalesceFullFilenameZeroSubDirTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        CoalesceUnitTestSettings.setSubDirectoryLength(0);

        getCoalesceFullFilename();
    }

    @Test
    public void getCoalesceFullFilenameFiveSubDirTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        CoalesceUnitTestSettings.setSubDirectoryLength(5);

        getCoalesceFullFilename();

    }

    private void getCoalesceFullFilename() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException
    {

        FileTestResult result = getJpgFile();

        assertEquals(CoalesceFieldCommon.CallGetBaseFilenameWithFullDirectoryPathForKey(result.Field.getKey(), false) + ".jpg",
                     result.SavedField.getCoalesceFullFilename());

    }

    @Test
    public void getCoalesceFullThumbnailFilenameNotFileTest()
    {
        CoalesceField<?> field = getTestMissionNameField();

        assertTrue(StringHelper.isNullOrEmpty(field.getCoalesceFullThumbnailFilename()));
    }

    @Test
    public void getCoalesceFullThumbnailFilenameTwoSubDirTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        getCoalesceFullThumbnailFilename();
    }

    @Test
    public void getCoalesceFullThumbnailFilenameZeroSubDirTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        CoalesceUnitTestSettings.setSubDirectoryLength(0);

        getCoalesceFullThumbnailFilename();
    }

    @Test
    public void getCoalesceFullThumbnailFilenameFiveSubDirTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        CoalesceUnitTestSettings.setSubDirectoryLength(5);

        getCoalesceFullThumbnailFilename();

    }

    private void getCoalesceFullThumbnailFilename() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException
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
    public void getCoalesceFilenameWithLastModifiedTagTwoSubDirTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        getCoalesceFilenameWithLastModifiedTag();
    }

    @Test
    public void getCoalesceFilenameWithLastModifiedTagZeroSubDirTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        CoalesceUnitTestSettings.setSubDirectoryLength(0);

        getCoalesceFilenameWithLastModifiedTag();
    }

    @Test
    public void getCoalesceFilenameWithLastModifiedTagFiveSubDirTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        CoalesceUnitTestSettings.setSubDirectoryLength(5);

        getCoalesceFilenameWithLastModifiedTag();

    }

    private void getCoalesceFilenameWithLastModifiedTag() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
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
    public void getCoalesceThumbnailFilenameWithLastModifiedTagTwoSubDirTest() throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        getCoalesceThumbnailFilenameWithLastModifiedTag();
    }

    @Test
    public void getCoalesceThumbnailFilenameWithLastModifiedTagZeroSubDirTest() throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        CoalesceUnitTestSettings.setSubDirectoryLength(0);

        getCoalesceThumbnailFilenameWithLastModifiedTag();
    }

    @Test
    public void getCoalesceThumbnailFilenameWithLastModifiedTagFiveSubDirTest() throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        CoalesceUnitTestSettings.setSubDirectoryLength(5);

        getCoalesceThumbnailFilenameWithLastModifiedTag();

    }

    private void getCoalesceThumbnailFilenameWithLastModifiedTag() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
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
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset, "Uri", ECoalesceFieldDataTypes.UriType);

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
                                                                    ECoalesceFieldDataTypes.DateTimeType);

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
                                                                    ECoalesceFieldDataTypes.BinaryType);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        byte[] bytes = new byte[0];

        assertArrayEquals(bytes, field.getBinaryValue());

    }

    @Test
    public void getDataSetTypedValueBinaryTypeTest() throws UnsupportedEncodingException, CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                    "Binary",
                                                                    ECoalesceFieldDataTypes.BinaryType);

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
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                    "File",
                                                                    ECoalesceFieldDataTypes.FileType);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        byte[] dataBytes = Files.readAllBytes(Paths.get("src\\test\\resources\\TestDocument.docx"));

        DocumentProperties docProps = new DocumentProperties();
        docProps.initialize("src\\test\\resources\\TestDocument.docx");

        field.setTypedValue(dataBytes, docProps);

        Object data = field.getValue();

        assertTrue(data instanceof byte[]);
        assertArrayEquals(dataBytes, (byte[]) data);
        assertArrayEquals(dataBytes, field.getBinaryValue());

        assertEquals("TestDocument.docx", field.getFilename());
        assertEquals("docx", field.getExtension());
        assertEquals(MimeHelper.getMimeTypeForExtension("docx"), field.getMimeType());
        assertEquals(dataBytes.length, docProps.getSize());

    }

    @Test
    public void setTypedValueDocPropsTest() throws IOException, ImageProcessingException, JDOMException,
            CoalesceDataFormatException, CoalesceCryptoException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                    "File",
                                                                    ECoalesceFieldDataTypes.FileType);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        byte[] dataBytes = Files.readAllBytes(Paths.get("src\\test\\resources\\TestDocument.docx"));

        DocumentProperties docProps = new DocumentProperties();
        docProps.initialize("src\\test\\resources\\TestDocument.docx");

        field.setTypedValue(dataBytes, docProps);

        Object data = field.getValue();

        assertTrue(data instanceof byte[]);
        assertArrayEquals(dataBytes, (byte[]) data);
        assertArrayEquals(dataBytes, field.getBinaryValue());

        assertEquals("TestDocument.docx", field.getFilename());
        assertEquals("docx", field.getExtension());
        assertEquals(MimeHelper.getMimeTypeForExtension("docx"), field.getMimeType());
        assertEquals(dataBytes.length, docProps.getSize());

    }

    @Test
    public void getDataSetTypedValueBooleanTypeTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                    "Boolean",
                                                                    ECoalesceFieldDataTypes.BooleanType);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        assertFalse(field.getBooleanValue());
        assertEquals("false", field.getBaseValue().toLowerCase());

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
                                                                    ECoalesceFieldDataTypes.IntegerType);

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
                                                                    ECoalesceFieldDataTypes.IntegerType);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        assertEquals("", field.getBaseValue());

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
                                                                    ECoalesceFieldDataTypes.GuidType);

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
                                                                    ECoalesceFieldDataTypes.GuidType);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        assertEquals("", field.getBaseValue());

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
                                                                    ECoalesceFieldDataTypes.GeocoordinateType);

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
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setTypedValue(new Coordinate(91.00000000000001, 0));

    }

    @Test
    public void coordinateLatitudeToSmallTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setTypedValue(new Coordinate(-90.00000000000001, 0));

    }

    @Test
    public void coordinateLongitudeToLargeTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setTypedValue(new Coordinate(0, 90.00000000000001));

    }

    @Test
    public void coordinateLongitudeToSmallTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setTypedValue(new Coordinate(0, -90.00000000000001));

    }

    @Test
    public void coordinateBothTooLargeTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setTypedValue(new Coordinate(90.00000000000001, 90.00000000000001));

    }

    @Test
    public void coordinateBothTooSmallTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setTypedValue(new Coordinate(-90.00000000000001, -90.00000000000001));

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
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT (0 90.00000000000001)");

        field.getValue();
    }

    @Test
    public void parseCoordinateLatitudeToSmallTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT (0 -90.00000000000001)");

        field.getValue();
    }

    @Test
    public void parseCoordinateLongitudeToLargeTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT (90.00000000000001 0)");

        field.getValue();
    }

    @Test
    public void parseCoordinateLongitudeToSmallTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT (-90.00000000000001 0)");

        field.getValue();
    }

    @Test
    public void parseCoordinateBothTooLargeTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT (90.00000000000001 90.00000000000001)");

        field.getValue();
    }

    @Test
    public void parseCoordinateBothTooSmallTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT (-90.00000000000001 -90.00000000000001)");

        field.getValue();
    }

    @Test
    public void parseCoordinateMissingLeftParenTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT 0 0)");

        field.getValue();
    }

    @Test
    public void parseCoordinateMissingRightParenTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT (0 0");

        field.getValue();
    }

    @Test
    public void parseCoordinateMissingBothParenTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

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
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("(0 0)");

        field.getValue();
    }

    @Test
    public void parseCoordinateLatitudeNotNumberTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT (X 0)");

        field.getValue();
    }

    @Test
    public void parseGeolocatioLongitudeNotNumberTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT (0 Y)");

        field.getValue();
    }

    @Test
    public void parseCoordinateBothNotNumberTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("POINT (X Y)");

        field.getValue();
    }

    @Test
    public void parseCoordinateMissingValueTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

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
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.POINT_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setBaseValue("  ");

        field.getValue();
    }

    @Test
    public void setTypedValueGeolocationTypeTypeMismatchTest() throws UnsupportedEncodingException,
            CoalesceDataFormatException
    {
        thrown.expect(ClassCastException.class);
        thrown.expectMessage("Type mismatch");

        CoalesceField<?> field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(new Coordinate());

    }

    @Test
    public void setTypedValueGeolocationTypeTypeMismatchGeolocationsListTest() throws UnsupportedEncodingException,
            CoalesceDataFormatException
    {
        thrown.expect(ClassCastException.class);
        thrown.expectMessage("Type mismatch");

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
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((0 90.00000000000001), (0 0))");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointLatitudeToSmallTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((0 0), (0 -90.00000000000001))");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointLongitudeToLargeTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((90.00000000000001 0), (0 0))");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointLongitudeToSmallTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((0 0), (-90.00000000000001 0))");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointBothTooLargeTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((90.00000000000001 90.00000000000001), (0 0))");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointBothTooSmallTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATE_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((0 0), (-90.00000000000001 -90.00000000000001))");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointMissingLeftParenTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT (0 0), (90 90))");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointMissingRightParenTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((0 0), (90 90)");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointMissingBothParenTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT (0 0)");

        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointMissingPointParenTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

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
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("((0 0), (90 90))");
        field.getValue();

    }

    @Test
    public void parseCoordinateMultipointLatitudeNotNumberTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((X 0), (90 90))");
        field.getValue();
    }

    @Test
    public void parseGeolocatioMultipointLongitudeNotNumberTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((90 90), (0 Y)");
        field.getValue();
    }

    @Test
    public void parseCoordinateMultipointBothNotNumberTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((X Y), (0 0))");
        field.getValue();
    }

    @Test
    public void parseCoordinateMultipointMissingValueTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(CoalesceFieldTest.COORDINATES_ERROR_MESSAGE);

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocationList");

        field.setBaseValue("MULTIPOINT ((0), (0 0))");
        field.getValue();

    }

    @Test
    public void setTypedValueGeolocationListTypeTypeMismatchTest() throws UnsupportedEncodingException,
            CoalesceDataFormatException
    {
        thrown.expect(ClassCastException.class);
        thrown.expectMessage("Type mismatch");

        CoalesceField<?> field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(new Coordinate[] { new Coordinate(0, 0) });

    }

    @Test
    public void setTypedValueGeolocationListTypeTypeMismatchGeolocationsTest() throws UnsupportedEncodingException,
            CoalesceDataFormatException
    {
        thrown.expect(ClassCastException.class);
        thrown.expectMessage("Type mismatch");

        CoalesceField<?> field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_LOCATION_PATH);

        field.setTypedValue(new Coordinate[] { new Coordinate(0, 0) });

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

    public static CoalesceField<?> getTestMissionNameField()
    {

        return getTestMissionNameField(CoalesceTypeInstances.TEST_MISSION);

    }

    public static CoalesceField<?> getTestMissionFieldByName(String fieldPath)
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        return getTestMissionFieldByName(mission, fieldPath);

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
                                                                    ECoalesceFieldDataTypes.FileType);

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

}
