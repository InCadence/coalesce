package Coalesce.Framework.DataModel;

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

import Coalesce.Common.Classification.Marking;
import Coalesce.Common.Classification.MarkingValueTest;
import Coalesce.Common.Exceptions.CoalesceCryptoException;
import Coalesce.Common.Exceptions.CoalesceDataFormatException;
import Coalesce.Common.Helpers.DocumentProperties;
import Coalesce.Common.Helpers.GUIDHelper;
import Coalesce.Common.Helpers.JodaDateTimeHelper;
import Coalesce.Common.Helpers.MimeHelper;
import Coalesce.Common.Helpers.StringHelper;
import Coalesce.Common.UnitTest.CoalesceTypeInstances;
import Coalesce.Common.UnitTest.CoalesceUnitTestSettings;

import com.drew.imaging.ImageProcessingException;
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

public class XsdFieldTest {

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

        XsdField field = getTestMissionNameField();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_KEY, field.getKey());

    }

    @Test
    public void setKeyTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = getTestMissionNameField(mission);

        UUID newGuid = UUID.randomUUID();
        field.setKey(newGuid.toString());

        XsdField savedField = getSavedTestMissionField(mission);

        assertEquals(savedField.getKey().toUpperCase(), newGuid.toString().toUpperCase());

    }

    @Test
    public void getNameTest()
    {

        XsdField field = getTestMissionNameField();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_NAME, field.getName());

    }

    @Test
    public void setNameTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = getTestMissionNameField(mission);

        field.setName("Testingname");

        String serializedMission = mission.toXml();
        XsdEntity savedMission = XsdEntity.create(serializedMission);

        XsdField savedField = (XsdField) savedMission.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH.replace(CoalesceTypeInstances.TEST_MISSION_NAME_NAME,
                                                                                                                                    "Testingname"));

        assertEquals("Testingname", savedField.getName());

    }

    @Test
    public void getValueTest()
    {

        XsdField field = getTestMissionNameField();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_VALUE, field.getValue());

    }

    @Test
    public void setValueTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = getTestMissionNameField(mission);

        field.setValue("Testingvalue");

        XsdField savedField = getSavedTestMissionField(mission);

        assertEquals("Testingvalue", savedField.getValue());

    }

    @Test
    public void getDataType()
    {
        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField stringField = getTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
        assertEquals(ECoalesceFieldDataTypes.StringType, stringField.getDataType());

        XsdField dateField = getTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);
        assertEquals(ECoalesceFieldDataTypes.DateTimeType, dateField.getDataType());

        XsdField integerField = getTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);
        assertEquals(ECoalesceFieldDataTypes.IntegerType, integerField.getDataType());

    }

    @Test
    public void setDateType()
    {
        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField stringField = getTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
        stringField.setDataType(ECoalesceFieldDataTypes.DateTimeType);

        XsdField dateField = getTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);
        dateField.setDataType(ECoalesceFieldDataTypes.IntegerType);

        XsdField integerField = getTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);
        integerField.setDataType(ECoalesceFieldDataTypes.StringType);

        String serializedMission = mission.toXml();
        XsdEntity savedMission = XsdEntity.create(serializedMission);

        XsdField savedStringField = getTestMissionFieldByName(savedMission, CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
        assertEquals(ECoalesceFieldDataTypes.DateTimeType, savedStringField.getDataType());

        XsdField savedDateField = getTestMissionFieldByName(savedMission, CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);
        assertEquals(ECoalesceFieldDataTypes.IntegerType, savedDateField.getDataType());

        XsdField savedIntegerField = getTestMissionFieldByName(savedMission, CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);
        assertEquals(ECoalesceFieldDataTypes.StringType, savedIntegerField.getDataType());

    }

    @Test
    public void getLabelTest()
    {

        XsdField field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_ACTION_NUMBER_PATH);

        assertEquals(CoalesceTypeInstances.TEST_MISSION_ACTION_NUMBER_LABEL, field.getLabel());

    }

    @Test
    public void getLabelDoesNotExistTest()
    {

        XsdField field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

        assertEquals("", field.getLabel());

    }

    @Test
    public void setLabelTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = getTestMissionNameField(mission);

        field.setLabel("Testinglabel");

        XsdField savedField = getSavedTestMissionField(mission);

        assertEquals("Testinglabel", savedField.getLabel());

    }

    @Test
    public void setLabelNullTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = getTestMissionNameField(mission);

        field.setLabel(null);

        XsdField savedField = getSavedTestMissionField(mission);

        assertEquals("", savedField.getLabel());

    }

    @Test
    public void getSizeDoesNotExistTest()
    {
        XsdField field = getTestMissionNameField();

        assertEquals(0, field.getSize());

    }

    @Test
    public void setSizeTest()
    {
        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = getTestMissionNameField(mission);
        field.setSize(128);

        String serializedMission = mission.toXml();
        XsdEntity savedMission = XsdEntity.create(serializedMission);

        XsdField savedField = getTestMissionNameField(savedMission);
        assertEquals(128, savedField.getSize());

    }

    @Test
    public void getModifiedByDoesNotExistTest()
    {

        XsdField field = getTestMissionNameField();

        assertEquals("", field.getModifiedBy());

    }

    @Test
    public void setModifiedByTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = getTestMissionNameField(mission);

        field.setModifiedBy("TestingModifiedBy");

        XsdField savedField = getSavedTestMissionField(mission);

        assertEquals("TestingModifiedBy", savedField.getModifiedBy());

    }

    @Test
    public void getModifiedByIpDoesNotExistTest()
    {

        XsdField field = getTestMissionNameField();

        assertEquals("", field.getModifiedByIP());

    }

    @Test
    public void setModifiedByIpTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = getTestMissionNameField(mission);

        field.setModifiedByIP("192.168.2.2");

        XsdField savedField = getSavedTestMissionField(mission);

        assertEquals("192.168.2.2", savedField.getModifiedByIP());

    }

    @Test
    public void getClassificationMarkingDefaultTest()
    {

        XsdField field = getTestMissionNameField();

        MarkingValueTest.assertMarkingValue(new Marking().GetClassification(),
                                            field.getClassificationMarking().GetClassification());

    }

    @Test
    public void getClassificationMarkingAfterSetAndSerializedTest()
    {
        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = getTestMissionNameField(mission);

        field.setClassificationMarking(TOPSECRETCLASSIFICATIONMARKING);

        XsdField savedField = getSavedTestMissionField(mission);

        MarkingValueTest.assertMarkingValue(TOPSECRETCLASSIFICATIONMARKING.GetClassification(),
                                            savedField.getClassificationMarking().GetClassification());

    }

    @Test
    public void setClassificationMarkingTopSecretTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) mission.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

        field.setClassificationMarking(TOPSECRETCLASSIFICATIONMARKING);

        MarkingValueTest.assertMarkingValue(TOPSECRETCLASSIFICATIONMARKING.GetClassification(),
                                            field.getClassificationMarking().GetClassification());

    }

    @Test
    public void getValueWithMarkingTest()
    {

        XsdField field = getTestMissionNameField();

        assertEquals("UNCLASSIFIED NORTHCOM Volunteer Background Checks Changed", field.getValueWithMarking());
    }

    @Test
    public void getPortionMarkingTest()
    {

        XsdField field = getTestMissionNameField();

        assertEquals("(U)", field.getPortionMarking());
    }

    @Test
    public void getPreviousHistoryKeyNoneTest()
    {

        XsdField field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        assertEquals("00000000-0000-0000-0000-000000000000", field.getPreviousHistoryKey());

    }

    @Test
    public void getPreviousHistoryKeyClassificationMarkingChangeTest()
    {

        XsdField field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        assertEquals("00000000-0000-0000-0000-000000000000", field.getPreviousHistoryKey());

        String fieldKey = field.getKey();

        field.setClassificationMarking(TOPSECRETCLASSIFICATIONMARKING);

        assertEquals(fieldKey, field.getKey());
        assertEquals(field.getHistory().get(0).getKey(), field.getPreviousHistoryKey());

    }

    @Test
    public void getFilenameDoesNotExistTest()
    {

        XsdField field = getTestMissionNameField();

        assertEquals("", field.getFilename());

    }

    @Test
    public void setFilenameTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = getTestMissionNameField(mission);

        field.setFilename("c:/Program Files/java/jre7/bin");

        XsdField savedField = getSavedTestMissionField(mission);

        assertEquals("c:/Program Files/java/jre7/bin", savedField.getFilename());

    }

    @Test
    public void getExtensionDoesNotExistTest()
    {

        XsdField field = getTestMissionNameField();

        assertEquals("", field.getExtension());

    }

    @Test
    public void setExtensionTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = getTestMissionNameField(mission);

        field.setExtension(".jpeg");

        XsdField savedField = getSavedTestMissionField(mission);

        assertEquals("jpeg", savedField.getExtension());

    }

    @Test
    public void getMimeTypeDoesNotExistTest()
    {

        XsdField field = getTestMissionNameField();

        assertEquals("", field.getMimeType());

    }

    @Test
    public void setMimeTypeTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = getTestMissionNameField(mission);

        field.setMimeType("application/pdf");

        XsdField savedField = getSavedTestMissionField(mission);

        assertEquals("application/pdf", savedField.getMimeType());

    }

    @Test
    public void getHashDoesNotExistTest()
    {

        XsdField field = getTestMissionNameField();

        assertEquals("", field.getHash());

    }

    @Test
    public void setHashTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = getTestMissionNameField(mission);

        field.setHash("8743b52063cd84097a65d1633f5c74f5");

        XsdField savedField = getSavedTestMissionField(mission);

        assertEquals("8743b52063cd84097a65d1633f5c74f5", savedField.getHash());

    }

    @Test
    public void PreviousHistoryOrderTest() throws CoalesceDataFormatException
    {

        XsdField field = XsdFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        field.setTypedValue(1111);
        field.setTypedValue(2222);

        assertEquals(2222, field.getIntegerValue());
        assertEquals(1111, field.getHistory().get(0).getIntegerValue());
        assertEquals(CoalesceTypeInstances.TEST_MISSION_BASE64_VALUE, field.getHistory().get(1).getIntegerValue());

    }

    @Test
    public void getSuspendHistoryTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = getTestMissionNameField(mission);

        assertFalse(field.getSuspendHistory());

        field.setSuspendHistory(true);

        assertTrue(field.getSuspendHistory());

        field.setSuspendHistory(false);

        assertFalse(field.getSuspendHistory());
    }

    @Test
    public void setSuspendHistoryTrueTest() throws CoalesceDataFormatException
    {

        XsdField field = XsdFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        field.setTypedValue(2222);

        assertEquals(2222, field.getIntegerValue());
        assertEquals(1, field.getHistory().size());
        assertEquals(CoalesceTypeInstances.TEST_MISSION_BASE64_VALUE, field.getHistory().get(0).getIntegerValue());

    }

    @Test
    public void setSuspendHistoryFalseTest() throws CoalesceDataFormatException
    {

        XsdField field = XsdFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        field.setSuspendHistory(true);
        field.setTypedValue(2222);

        assertEquals(2222, field.getIntegerValue());
        assertTrue(field.getHistory().isEmpty());

    }

    @Test
    public void setSuspendHistoryFalseBinaryTest()
    {

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset,
                                                                    "Binary",
                                                                    ECoalesceFieldDataTypes.BinaryType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.create(parentRecord, fileFieldDef);

        assertTrue(field.getHistory().isEmpty());

        field.setValue("Something");

        assertTrue(field.getHistory().isEmpty());

    }

    @Test
    public void setSuspendHistoryFalseFileTest()
    {

        FileTestResult result = getJpgFile();

        XsdField field = result.SavedField;
        assertTrue(field.getHistory().isEmpty());

        field.setValue("Something");

        assertTrue(field.getHash().isEmpty());

    }

    @Test
    public void getDateCreatedExistTest()
    {

        XsdField field = getTestMissionNameField();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_CREATED, field.getDateCreated());

    }

    @Test
    public void setDateCreatedTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = getTestMissionNameField(mission);

        DateTime now = JodaDateTimeHelper.NowInUtc();

        field.setDateCreated(now);

        XsdField savedField = getSavedTestMissionField(mission);

        assertEquals(now, savedField.getDateCreated());

    }

    @Test
    public void getCoordinateTest()
    {

        try
        {
            // Create Mission Entity
            XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

            // Get Mission Location Field
            XsdField field = getTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_LOCATION_PATH);

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
            XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

            // Get Mission Location Field
            XsdField field = getTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_LOCATION_PATH);

            // Change Field Type
            field.setDataType(ECoalesceFieldDataTypes.GeocoordinateListType);

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

        XsdField field = getTestMissionNameField();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_MODIFIED, field.getLastModified());

    }

    @Test
    public void setLastModifiedTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = getTestMissionNameField(mission);

        DateTime now = JodaDateTimeHelper.NowInUtc();

        field.setLastModified(now);

        XsdField savedField = getSavedTestMissionField(mission);

        assertEquals(now, savedField.getLastModified());

    }

    @Test
    public void ToXmlTest()
    {

        XsdField field = getTestMissionNameField();

        String fieldXml = field.toXml();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_XML,
                     fieldXml.replace("\n", "").replace("\r", "").replace("    ", ""));

    }

    @Test
    public void getCoalesceFullFilenameNotFileTest()
    {
        XsdField field = getTestMissionNameField();

        assertTrue(StringHelper.IsNullOrEmpty(field.getCoalesceFullFilename()));
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

        assertEquals(XsdFieldCommon.CallGetBaseFilenameWithFullDirectoryPathForKey(result.Field.getKey(), false) + ".jpg",
                     result.SavedField.getCoalesceFullFilename());

    }

    @Test
    public void getCoalesceFullThumbnailFilenameNotFileTest()
    {
        XsdField field = getTestMissionNameField();

        assertTrue(StringHelper.IsNullOrEmpty(field.getCoalesceFullThumbnailFilename()));
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

        assertEquals(XsdFieldCommon.CallGetBaseFilenameWithFullDirectoryPathForKey(result.Field.getKey(), false)
                + "_thumb.jpg", result.SavedField.getCoalesceFullThumbnailFilename());

    }

    @Test
    public void getCoalesceFilenameWithLastModifiedTagNotFileTest()
    {
        XsdField field = getTestMissionNameField();

        assertTrue(StringHelper.IsNullOrEmpty(field.getCoalesceFilenameWithLastModifiedTag()));
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
        XsdField field = getTestMissionNameField();

        assertTrue(StringHelper.IsNullOrEmpty(field.getCoalesceThumbnailFilenameWithLastModifiedTag()));
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
        XsdField field = getTestMissionNameField();

        assertTrue(StringHelper.IsNullOrEmpty(field.getCoalesceFilename()));
    }

    @Test
    public void getCoalesceFilenameTest()
    {

        FileTestResult result = getJpgFile();

        assertEquals(GUIDHelper.RemoveBrackets(result.Field.getKey()) + ".jpg", result.SavedField.getCoalesceFilename());

    }

    @Test
    public void getCoalesceThumbnailFilenameNotFileTest()
    {
        XsdField field = getTestMissionNameField();

        assertTrue(StringHelper.IsNullOrEmpty(field.getCoalesceThumbnailFilename()));
    }

    @Test
    public void getCoalesceThumbnailFilename()
    {

        FileTestResult result = getJpgFile();

        assertEquals(GUIDHelper.RemoveBrackets(result.Field.getKey()) + "_thumb.jpg",
                     result.SavedField.getCoalesceThumbnailFilename());

    }

    @Test
    public void StringTypeTest() throws CoalesceDataFormatException
    {

        XsdField field = getTestMissionNameField();

        Object data = field.getData();

        assertTrue(data instanceof String);
        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_VALUE, data);

        field.setTypedValue("Changed");

        data = null;
        data = field.getData();

        assertTrue(data instanceof String);
        assertEquals("Changed", data);
        assertEquals("Changed", field.getValue());

    }

    @Test(expected = ClassCastException.class)
    public void setTypedValueStringTypeTypeMismatchTest()
    {

        XsdField field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(UUID.randomUUID());

    }

    @Test
    public void uriTypeTest() throws CoalesceDataFormatException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset, "Uri", ECoalesceFieldDataTypes.UriType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.create(parentRecord, fileFieldDef);
        field.setTypedValue("uri:document/pdf");

        Object data = field.getData();

        assertTrue(data instanceof String);
        assertEquals("uri:document/pdf", data);
        assertEquals("uri:document/pdf", field.getValue());

    }

    @Test
    public void getDataDateTimeTypeNotSetTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset,
                                                                    "DateTime",
                                                                    ECoalesceFieldDataTypes.DateTimeType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.create(parentRecord, fileFieldDef);

        assertNull(field.getDateTimeValue());

    }

    @Test
    public void getDataSetTypedValueDateTimeTypeTest() throws CoalesceDataFormatException
    {
        XsdField field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        Object data = field.getData();

        assertTrue(data instanceof DateTime);
        assertEquals(JodaDateTimeHelper.FromXmlDateTimeUTC(CoalesceTypeInstances.TEST_MISSION_START_TIME_VALUE), data);

        DateTime now = JodaDateTimeHelper.NowInUtc();
        field.setTypedValue(now);

        assertEquals(JodaDateTimeHelper.ToXmlDateTimeUTC(now), field.getValue());

        data = null;
        data = field.getData();

        assertTrue(data instanceof DateTime);
        assertEquals(now, data);
        assertEquals(now, field.getDateTimeValue());

    }

    @Test(expected = ClassCastException.class)
    public void setTypedValueDateTimeTypeTypeMismatchTest()
    {

        XsdField field = getTestMissionNameField();

        field.setTypedValue(JodaDateTimeHelper.NowInUtc());

    }

    @Test
    public void getDataBinaryTypeNotSetTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset,
                                                                    "Binary",
                                                                    ECoalesceFieldDataTypes.BinaryType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.create(parentRecord, fileFieldDef);

        byte[] bytes = new byte[0];

        assertArrayEquals(bytes, field.getBinaryValue());

    }

    @Test
    public void getDataSetTypedValueBinaryTypeTest() throws UnsupportedEncodingException, CoalesceDataFormatException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset,
                                                                    "Binary",
                                                                    ECoalesceFieldDataTypes.BinaryType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.create(parentRecord, fileFieldDef);

        String byteString = "Testing String";
        byte[] dataBytes = byteString.getBytes("US-ASCII");
        field.setTypedValue(dataBytes);

        Object data = field.getData();

        assertTrue(data instanceof byte[]);
        assertArrayEquals(dataBytes, (byte[]) data);
        assertArrayEquals(dataBytes, field.getBinaryValue());
        assertEquals("VGVzdGluZyBTdHJpbmc=", field.getValue());

    }

    @Test(expected = ClassCastException.class)
    public void setTypedValueBinaryTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        XsdField field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        String byteString = "Testing String";
        byte[] dataBytes = byteString.getBytes("US-ASCII");
        field.setTypedValue(dataBytes);

    }

    @Test
    public void setTypedValueFileBytesTest() throws CoalesceDataFormatException, ImageProcessingException, IOException,
            JDOMException, CoalesceCryptoException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset,
                                                                    "File",
                                                                    ECoalesceFieldDataTypes.FileType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.create(parentRecord, fileFieldDef);

        byte[] dataBytes = Files.readAllBytes(Paths.get("src\\resources\\TestDocument.docx"));

        DocumentProperties docProps = new DocumentProperties();
        docProps.initialize("src\\resources\\TestDocument.docx");

        field.setTypedValue(dataBytes, docProps);

        Object data = field.getData();

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
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset,
                                                                    "File",
                                                                    ECoalesceFieldDataTypes.FileType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.create(parentRecord, fileFieldDef);

        byte[] dataBytes = Files.readAllBytes(Paths.get("src\\resources\\TestDocument.docx"));

        DocumentProperties docProps = new DocumentProperties();
        docProps.initialize("src\\resources\\TestDocument.docx");

        field.setTypedValue(dataBytes, docProps);

        Object data = field.getData();

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
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset,
                                                                    "Boolean",
                                                                    ECoalesceFieldDataTypes.BooleanType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.create(parentRecord, fileFieldDef);

        assertFalse(field.getBooleanValue());
        assertEquals("false", field.getValue().toLowerCase());

        field.setTypedValue(true);

        Object data = field.getData();

        assertTrue(data instanceof Boolean);
        assertEquals(true, data);
        assertEquals("true", field.getValue().toLowerCase());
        assertEquals(true, field.getBooleanValue());

    }

    @Test(expected = ClassCastException.class)
    public void setTypedValueBooleanTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        XsdField field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(true);

    }

    @Test(expected = CoalesceDataFormatException.class)
    public void getDataIntegerTypeNotSetTest() throws CoalesceDataFormatException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset,
                                                                    "Integer",
                                                                    ECoalesceFieldDataTypes.IntegerType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.create(parentRecord, fileFieldDef);

        field.getIntegerValue();

    }

    @Test
    public void getDataSetTypedValueIntegerTypeTest() throws CoalesceDataFormatException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset,
                                                                    "Integer",
                                                                    ECoalesceFieldDataTypes.IntegerType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.create(parentRecord, fileFieldDef);

        assertEquals("", field.getValue());

        field.setTypedValue(1111);

        Object data = field.getData();

        assertTrue(data instanceof Integer);
        assertEquals(1111, data);
        assertEquals("1111", field.getValue());
        assertEquals(1111, field.getIntegerValue());

    }

    @Test(expected = ClassCastException.class)
    public void setTypedValueIntgerTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        XsdField field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(1111);

    }

    @Test
    public void getDataGuidTypeNotSetTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset,
                                                                    "GUID",
                                                                    ECoalesceFieldDataTypes.GuidType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.create(parentRecord, fileFieldDef);

        assertNull(field.getGuidValue());

    }

    @Test
    public void getDataSetTypedValueGuidTypeTest() throws CoalesceDataFormatException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset,
                                                                    "GUID",
                                                                    ECoalesceFieldDataTypes.GuidType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.create(parentRecord, fileFieldDef);

        assertEquals("", field.getValue());

        UUID guid = UUID.randomUUID();
        field.setTypedValue(guid);

        Object data = field.getData();

        assertTrue(data instanceof UUID);
        assertEquals(guid, data);
        assertEquals(GUIDHelper.GetGuidString(guid), field.getValue());
        assertEquals(guid, field.getGuidValue());

    }

    @Test(expected = ClassCastException.class)
    public void setTypedValueGUIDTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        XsdField field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(UUID.randomUUID());

    }

    @Test
    public void getDataGeocoordinateTypeNotSetTest() throws CoalesceDataFormatException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset,
                                                                    "Location",
                                                                    ECoalesceFieldDataTypes.GeocoordinateType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.create(parentRecord, fileFieldDef);

        assertNull(field.getCoordinateValue());

    }

    @Test
    public void getDataSetTypedValueGeolocationTypeTest() throws CoalesceDataFormatException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        assertEquals("POINT (-80.9363995 43.6616578)", field.getValue());

        Object data = (Coordinate) field.getData();

        assertTrue(data instanceof Coordinate);

        Coordinate location = (Coordinate) data;

        assertEquals(-80.9363995, location.x, 0.00001);
        assertEquals(43.6616578, location.y, 0.00001);

        Coordinate pentagon = new Coordinate(38.87116000, -77.05613800);
        field.setTypedValue(pentagon);

        String entityXml = entity.toXml();
        XsdEntity desEntity = XsdEntity.create(entityXml);

        XsdField desField = (XsdField) desEntity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        Coordinate desLocation = desField.getCoordinateValue();

        assertEquals(pentagon, desLocation);
        assertEquals("POINT ( 38.87116 -77.056138 )", field.getValue());
    }

    @Test
    public void geolocationPointTests() throws ImageProcessingException, CoalesceCryptoException, IOException,
            JDOMException, CoalesceDataFormatException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        DocumentProperties docProps = new DocumentProperties();
        docProps.initialize("src\\resources\\desert.jpg");

        field.setTypedValue(new Coordinate(docProps.getLongitude(), docProps.getLatitude()));
        assertEquals("POINT ( 8.67243350003624 49.39875240003339 )", field.getValue());

        field.setTypedValue(new Coordinate(0, 0));
        assertEquals("POINT ( 0.0 0.0 )", field.getValue());

        field.setTypedValue(new Coordinate(-90, -90));
        assertEquals("POINT ( -90.0 -90.0 )", field.getValue());

        field.setTypedValue(new Coordinate(90, 90));
        assertEquals("POINT ( 90.0 90.0 )", field.getValue());

        field.setTypedValue(new Coordinate(90, 0));
        assertEquals("POINT ( 90.0 0.0 )", field.getValue());

        field.setTypedValue(new Coordinate(-90, 0));
        assertEquals("POINT ( -90.0 0.0 )", field.getValue());

        field.setTypedValue(new Coordinate(0, 90));
        assertEquals("POINT ( 0.0 90.0 )", field.getValue());

        field.setTypedValue(new Coordinate(0, -90));
        assertEquals("POINT ( 0.0 -90.0 )", field.getValue());

        field.setTypedValue(new Coordinate(-77.05613800, 38.87116000));
        assertEquals("POINT ( -77.056138 38.87116 )", field.getValue());

    }

    @Test
    public void coordinateLatitudeToLargeTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATE_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setTypedValue(new Coordinate(91.00000000000001, 0));

    }

    @Test
    public void coordinateLatitudeToSmallTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATE_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setTypedValue(new Coordinate(-90.00000000000001, 0));

    }

    @Test
    public void coordinateLongitudeToLargeTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATE_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setTypedValue(new Coordinate(0, 90.00000000000001));

    }

    @Test
    public void coordinateLongitudeToSmallTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATE_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setTypedValue(new Coordinate(0, -90.00000000000001));

    }

    @Test
    public void coordinateBothTooLargeTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATE_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setTypedValue(new Coordinate(90.00000000000001, 90.00000000000001));

    }

    @Test
    public void coordinateBothTooSmallTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATE_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setTypedValue(new Coordinate(-90.00000000000001, -90.00000000000001));

    }

    @Test
    public void parseCoordinateTest() throws CoalesceDataFormatException
    {

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setValue("POINT (8.67243350003624 49.39875240003339)");
        assertEquals(new Coordinate(8.67243350003624, 49.39875240003339), field.getData());

        field.setValue("POINT (0 0)");
        assertEquals(new Coordinate(0, 0), field.getData());

        field.setValue("POINT (-90 -90)");
        assertEquals(new Coordinate(-90, -90), field.getData());

        field.setValue("POINT (90 90)");
        assertEquals(new Coordinate(90, 90), field.getData());

        field.setValue("POINT (90 0)");
        assertEquals(new Coordinate(90, 0), field.getData());

        field.setValue("POINT (-90 0)");
        assertEquals(new Coordinate(-90, 0), field.getData());

        field.setValue("POINT (0 90)");
        assertEquals(new Coordinate(0, 90), field.getData());

        field.setValue("POINT (0 -90)");
        assertEquals(new Coordinate(0, -90), field.getData());

        field.setValue("POINT (-77.056138 38.87116)");
        assertEquals(new Coordinate(-77.05613800, 38.87116000), field.getData());

    }

    @Test
    public void parseCoordinateLatitudeToLargeTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATE_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setValue("POINT (0 90.00000000000001)");

        field.getData();
    }

    @Test
    public void parseCoordinateLatitudeToSmallTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATE_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setValue("POINT (0 -90.00000000000001)");

        field.getData();
    }

    @Test
    public void parseCoordinateLongitudeToLargeTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATE_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setValue("POINT (90.00000000000001 0)");

        field.getData();
    }

    @Test
    public void parseCoordinateLongitudeToSmallTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATE_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setValue("POINT (-90.00000000000001 0)");

        field.getData();
    }

    @Test
    public void parseCoordinateBothTooLargeTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATE_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setValue("POINT (90.00000000000001 90.00000000000001)");

        field.getData();
    }

    @Test
    public void parseCoordinateBothTooSmallTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATE_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setValue("POINT (-90.00000000000001 -90.00000000000001)");

        field.getData();
    }

    @Test
    public void parseCoordinateMissingLeftParenTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.POINT_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setValue("POINT 0 0)");

        field.getData();
    }

    @Test
    public void parseCoordinateMissingRightParenTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.POINT_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setValue("POINT (0 0");

        field.getData();
    }

    @Test
    public void parseCoordinateMissingBothParenTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.POINT_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setValue("POINT 0 0");

        field.getData();
    }

    @Test
    public void parseCoordinateMissingSpaceTest() throws CoalesceDataFormatException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setValue("POINT(0 0)");

        Coordinate location = field.getCoordinateValue();

        assertEquals(new Coordinate(0, 0), location);

    }

    @Test
    public void parseCoordinateMissingPOINTTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.POINT_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setValue("(0 0)");

        field.getData();
    }

    @Test
    public void parseCoordinateLatitudeNotNumberTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.POINT_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setValue("POINT (X 0)");

        field.getData();
    }

    @Test
    public void parseGeolocatioLongitudeNotNumberTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.POINT_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setValue("POINT (0 Y)");

        field.getData();
    }

    @Test
    public void parseCoordinateBothNotNumberTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.POINT_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setValue("POINT (X Y)");

        field.getData();
    }

    @Test
    public void parseCoordinateMissingValueTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.POINT_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setValue("POINT (0)");

        field.getData();
    }

    @Test
    public void parseCoordinateNullTest() throws CoalesceDataFormatException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setValue(null);

        assertNull(field.getData());
    }

    @Test
    public void parseCoordinateEmptyTest() throws CoalesceDataFormatException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setValue("");

        assertNull(field.getData());
    }

    @Test
    public void parseCoordinateWhiteSpaceTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.POINT_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");

        field.setValue("  ");

        field.getData();
    }

    @Test
    public void setTypedValueGeolocationTypeTypeMismatchTest() throws UnsupportedEncodingException,
            CoalesceDataFormatException
    {
        thrown.expect(ClassCastException.class);
        thrown.expectMessage("Type mismatch");

        XsdField field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(new Coordinate());

    }

    @Test
    public void setTypedValueGeolocationTypeTypeMismatchGeolocationsListTest() throws UnsupportedEncodingException,
            CoalesceDataFormatException
    {
        thrown.expect(ClassCastException.class);
        thrown.expectMessage("Type mismatch");

        XsdField field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_LOCATION_PATH);
        field.setDataType(ECoalesceFieldDataTypes.GeocoordinateListType);

        field.setTypedValue(new Coordinate());

    }

    @Test
    public void parseCoordinateMultipointTest() throws CoalesceDataFormatException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");
        field.setDataType(ECoalesceFieldDataTypes.GeocoordinateListType);

        field.setValue("MULTIPOINT ((-70.6280916 34.6873833), (-77.056138 38.87116))");

        Coordinate[] locations = field.getCoordinateListValue();

        Coordinate[] expected = new Coordinate[2];
        expected[0] = new Coordinate(-70.6280916, 34.6873833);
        expected[1] = new Coordinate(-77.056138, 38.87116);

        assertArrayEquals(expected, locations);

    }

    @Test
    public void parseCoordinateMultipointSingleTest() throws CoalesceDataFormatException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");
        field.setDataType(ECoalesceFieldDataTypes.GeocoordinateListType);

        field.setValue("MULTIPOINT ((-70.6280916 34.6873833))");

        Coordinate[] locations = field.getCoordinateListValue();

        Coordinate[] expected = new Coordinate[1];
        expected[0] = new Coordinate(-70.6280916, 34.6873833);

        assertArrayEquals(expected, locations);

    }

    @Test
    public void pareseCoordinateMultipointNoneTest() throws CoalesceDataFormatException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");
        field.setDataType(ECoalesceFieldDataTypes.GeocoordinateListType);

        field.setValue("MULTIPOINT EMPTY");

        Coordinate[] locations = field.getCoordinateListValue();

        Coordinate[] expected = new Coordinate[0];

        assertArrayEquals(expected, locations);

    }

    @Test
    public void parseCoordinateMultipointLatitudeToLargeTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATE_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");
        field.setDataType(ECoalesceFieldDataTypes.GeocoordinateListType);

        field.setValue("MULTIPOINT ((0 90.00000000000001), (0 0))");

        field.getData();

    }

    @Test
    public void parseCoordinateMultipointLatitudeToSmallTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATE_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");
        field.setDataType(ECoalesceFieldDataTypes.GeocoordinateListType);

        field.setValue("MULTIPOINT ((0 0), (0 -90.00000000000001))");

        field.getData();

    }

    @Test
    public void parseCoordinateMultipointLongitudeToLargeTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATE_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");
        field.setDataType(ECoalesceFieldDataTypes.GeocoordinateListType);

        field.setValue("MULTIPOINT ((90.00000000000001 0), (0 0))");

        field.getData();

    }

    @Test
    public void parseCoordinateMultipointLongitudeToSmallTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATE_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");
        field.setDataType(ECoalesceFieldDataTypes.GeocoordinateListType);

        field.setValue("MULTIPOINT ((0 0), (-90.00000000000001 0))");

        field.getData();

    }

    @Test
    public void parseCoordinateMultipointBothTooLargeTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATE_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");
        field.setDataType(ECoalesceFieldDataTypes.GeocoordinateListType);

        field.setValue("MULTIPOINT ((90.00000000000001 90.00000000000001), (0 0))");

        field.getData();

    }

    @Test
    public void parseCoordinateMultipointBothTooSmallTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATE_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");
        field.setDataType(ECoalesceFieldDataTypes.GeocoordinateListType);

        field.setValue("MULTIPOINT ((0 0), (-90.00000000000001 -90.00000000000001))");

        field.getData();

    }

    @Test
    public void parseCoordinateMultipointMissingLeftParenTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATES_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");
        field.setDataType(ECoalesceFieldDataTypes.GeocoordinateListType);

        field.setValue("MULTIPOINT (0 0), (90 90))");

        field.getData();

    }

    @Test
    public void parseCoordinateMultipointMissingRightParenTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATES_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");
        field.setDataType(ECoalesceFieldDataTypes.GeocoordinateListType);

        field.setValue("MULTIPOINT ((0 0), (90 90)");

        field.getData();

    }

    @Test
    public void parseCoordinateMultipointMissingBothParenTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATES_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");
        field.setDataType(ECoalesceFieldDataTypes.GeocoordinateListType);

        field.setValue("MULTIPOINT (0 0)");

        field.getData();

    }

    @Test
    public void parseCoordinateMultipointMissingPointParenTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATES_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");
        field.setDataType(ECoalesceFieldDataTypes.GeocoordinateListType);

        field.setValue("MULTIPOINT (0 0, (89 9))");

        field.getData();

    }

    @Test
    public void parseCoordinateMultipointMissingSpaceTest() throws CoalesceDataFormatException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");
        field.setDataType(ECoalesceFieldDataTypes.GeocoordinateListType);

        field.setValue("MULTIPOINT((0 0))");

        Coordinate[] locations = field.getCoordinateListValue();

        Coordinate[] expected = new Coordinate[1];
        expected[0] = new Coordinate(0, 0);

        assertArrayEquals(expected, locations);

    }

    @Test
    public void parseCoordinateMultipointMissingMULTIPOINTTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATES_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");
        field.setDataType(ECoalesceFieldDataTypes.GeocoordinateListType);

        field.setValue("((0 0), (90 90))");

        field.getData();

    }

    @Test
    public void parseCoordinateMultipointLatitudeNotNumberTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATES_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");
        field.setDataType(ECoalesceFieldDataTypes.GeocoordinateListType);

        field.setValue("MULTIPOINT ((X 0), (90 90))");

        field.getData();

    }

    @Test
    public void parseGeolocatioMultipointLongitudeNotNumberTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATES_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");
        field.setDataType(ECoalesceFieldDataTypes.GeocoordinateListType);

        field.setValue("MULTIPOINT ((90 90), (0 Y)");

        field.getData();
    }

    @Test
    public void parseCoordinateMultipointBothNotNumberTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATES_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");
        field.setDataType(ECoalesceFieldDataTypes.GeocoordinateListType);

        field.setValue("MULTIPOINT ((X Y), (0 0))");

        field.getData();

    }

    @Test
    public void parseCoordinateMultipointMissingValueTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(XsdFieldTest.COORDINATES_ERROR_MESSAGE);

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionGeoLocation");
        field.setDataType(ECoalesceFieldDataTypes.GeocoordinateListType);

        field.setValue("MULTIPOINT ((0), (0 0))");

        field.getData();

    }

    @Test
    public void setTypedValueGeolocationListTypeTypeMismatchTest() throws UnsupportedEncodingException,
            CoalesceDataFormatException
    {
        thrown.expect(ClassCastException.class);
        thrown.expectMessage("Type mismatch");

        XsdField field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(new Coordinate[] { new Coordinate(0, 0) });

    }

    @Test
    public void setTypedValueGeolocationListTypeTypeMismatchGeolocationsTest() throws UnsupportedEncodingException,
            CoalesceDataFormatException
    {
        thrown.expect(ClassCastException.class);
        thrown.expectMessage("Type mismatch");

        XsdField field = getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_LOCATION_PATH);

        field.setTypedValue(new Coordinate[] { new Coordinate(0, 0) });

    }

    // -----------------------------------------------------------------------//
    // Private Static Methods
    // -----------------------------------------------------------------------//

    private static void initializeSettings()
    {

        CoalesceUnitTestSettings.setSubDirectoryLength(2);

    }

    private static XsdField getTestMissionNameField(String entityXml)
    {

        XsdEntity entity = XsdEntity.create(entityXml);

        return getTestMissionNameField(entity);

    }

    private static XsdField getTestMissionNameField(XsdEntity entity)
    {

        return getTestMissionFieldByName(entity, CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

    }

    public static XsdField getTestMissionNameField()
    {

        return getTestMissionNameField(CoalesceTypeInstances.TEST_MISSION);

    }

    public static XsdField getTestMissionFieldByName(String fieldPath)
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        return getTestMissionFieldByName(mission, fieldPath);

    }

    private static XsdField getTestMissionFieldByName(XsdEntity entity, String fieldPath)
    {

        XsdDataObject fdo = entity.getDataObjectForNamePath(fieldPath);

        assertTrue(fdo instanceof XsdField);

        return (XsdField) fdo;

    }

    private static XsdField getSavedTestMissionField(XsdEntity entity)
    {

        String serializedMission = entity.toXml();

        return getTestMissionNameField(serializedMission);

    }

    private FileTestResult getJpgFile()
    {

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset,
                                                                    "File",
                                                                    ECoalesceFieldDataTypes.FileType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField fileField = XsdField.create(parentRecord, fileFieldDef);
        fileField.setExtension("jpg");

        String savedEntity = entity.toXml();

        XsdEntity desEntity = XsdEntity.create(savedEntity);

        XsdField savedFileField = (XsdField) desEntity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/File");

        return new FileTestResult(fileField, savedFileField);

    }

    private class FileTestResult {

        public XsdField Field;
        public XsdField SavedField;

        public FileTestResult(XsdField field, XsdField savedField)
        {
            Field = field;
            SavedField = savedField;
        }
    }

}
