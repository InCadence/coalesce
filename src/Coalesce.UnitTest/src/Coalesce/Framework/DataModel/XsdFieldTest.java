package Coalesce.Framework.DataModel;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import Coalesce.Common.Classification.Marking;
import Coalesce.Common.Classification.MarkingValueTest;
import Coalesce.Common.Exceptions.DataFormatException;
import Coalesce.Common.Helpers.GUIDHelper;
import Coalesce.Common.Helpers.JodaDateTimeHelper;
import Coalesce.Common.Helpers.StringHelper;
import Coalesce.Common.UnitTest.CoalesceSettingsTestHelper;
import Coalesce.Common.UnitTest.CoalesceTypeInstances;
import Coalesce.Common.UnitTest.CoalesceUnitTestSettings;

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

    private static final Marking TOPSECRETCLASSIFICATIONMARKING = new Marking("//JOINT TOP SECRET AND USA//FOUO-LES//SBU/ACCM-BOB");

    @BeforeClass
    public static void setUpBeforeClass()
    {
        CoalesceSettingsTestHelper.setUpdBeforeClass();

        InitializeSettings();

    }

    /*
     * @AfterClass public static void tearDownAfterClass() throws Exception { }
     * 
     * @Before public void setUp() throws Exception { }
     */

    @After
    public void tearDown()
    {
        InitializeSettings();
    }

    @Test
    public void GetKeyTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_KEY, field.getKey());

    }

    @Test
    public void SetKeyTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = GetTestMissionNameField(mission);

        UUID newGuid = UUID.randomUUID();
        field.setKey(newGuid.toString());

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals(savedField.getKey().toUpperCase(), newGuid.toString().toUpperCase());

    }

    @Test
    public void GetNameTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_NAME, field.getName());

    }

    @Test
    public void SetNameTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.setName("Testingname");

        String serializedMission = mission.toXml();
        XsdEntity savedMission = XsdEntity.create(serializedMission);

        XsdField savedField = (XsdField) savedMission.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH.replace(CoalesceTypeInstances.TEST_MISSION_NAME_NAME,
                                                                                                                                    "Testingname"));

        assertEquals("Testingname", savedField.getName());

    }

    @Test
    public void GetValueTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_VALUE, field.getValue());

    }

    @Test
    public void SetValueTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.setValue("Testingvalue");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("Testingvalue", savedField.getValue());

    }

    @Test
    public void GetDataType()
    {
        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField stringField = GetTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
        assertEquals(ECoalesceFieldDataTypes.StringType, stringField.getDataType());

        XsdField dateField = GetTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);
        assertEquals(ECoalesceFieldDataTypes.DateTimeType, dateField.getDataType());

        XsdField integerField = GetTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);
        assertEquals(ECoalesceFieldDataTypes.IntegerType, integerField.getDataType());

    }

    @Test
    public void SetDateType()
    {
        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField stringField = GetTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
        stringField.setDataType(ECoalesceFieldDataTypes.DateTimeType);

        XsdField dateField = GetTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);
        dateField.setDataType(ECoalesceFieldDataTypes.IntegerType);

        XsdField integerField = GetTestMissionFieldByName(mission, CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);
        integerField.setDataType(ECoalesceFieldDataTypes.StringType);

        String serializedMission = mission.toXml();
        XsdEntity savedMission = XsdEntity.create(serializedMission);

        XsdField savedStringField = GetTestMissionFieldByName(savedMission, CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
        assertEquals(ECoalesceFieldDataTypes.DateTimeType, savedStringField.getDataType());

        XsdField savedDateField = GetTestMissionFieldByName(savedMission, CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);
        assertEquals(ECoalesceFieldDataTypes.IntegerType, savedDateField.getDataType());

        XsdField savedIntegerField = GetTestMissionFieldByName(savedMission, CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);
        assertEquals(ECoalesceFieldDataTypes.StringType, savedIntegerField.getDataType());

    }

    @Test
    public void GetLabelTest()
    {

        XsdField field = GetTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_ACTION_NUMBER_PATH);

        assertEquals(CoalesceTypeInstances.TEST_MISSION_ACTION_NUMBER_LABEL, field.getLabel());

    }

    @Test
    public void GetLabelDoesNotExistTest()
    {

        XsdField field = GetTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

        assertEquals("", field.getLabel());

    }

    @Test
    public void SetLabelTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.setLabel("Testinglabel");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("Testinglabel", savedField.getLabel());

    }

    @Test
    public void SetLabelNullTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.setLabel(null);

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("", savedField.getLabel());

    }

    @Test
    public void GetSizeDoesNotExistTest()
    {
        XsdField field = GetTestMissionNameField();

        assertEquals(0, field.getSize());

    }

    @Test
    public void SetSizeTest()
    {
        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = GetTestMissionNameField(mission);
        field.setSize(128);

        String serializedMission = mission.toXml();
        XsdEntity savedMission = XsdEntity.create(serializedMission);

        XsdField savedField = GetTestMissionNameField(savedMission);
        assertEquals(128, savedField.getSize());

    }

    @Test
    public void GetModifiedByDoesNotExistTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals("", field.getModifiedBy());

    }

    @Test
    public void SetModifiedByTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.setModifiedBy("TestingModifiedBy");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("TestingModifiedBy", savedField.getModifiedBy());

    }

    @Test
    public void GetModifiedByIpDoesNotExistTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals("", field.getModifiedByIP());

    }

    @Test
    public void SetModifiedByIpTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.setModifiedByIP("192.168.2.2");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("192.168.2.2", savedField.getModifiedByIP());

    }

    @Test
    public void GetClassificationMarkingDefaultTest()
    {

        XsdField field = GetTestMissionNameField();

        MarkingValueTest.assertMarkingValue(new Marking().GetClassification(),
                                            new Marking(field.getClassificationMarking()).GetClassification());

    }

    @Test
    public void GetClassificationMarkingAfterSetAndSerializedTest()
    {
        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.setClassificationMarking(TOPSECRETCLASSIFICATIONMARKING);

        XsdField savedField = GetSavedTestMissionField(mission);

        MarkingValueTest.assertMarkingValue(TOPSECRETCLASSIFICATIONMARKING.GetClassification(),
                                            new Marking(savedField.getClassificationMarking()).GetClassification());

    }

    @Test
    public void SetClassificationMarkingTopSecretTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) mission.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

        field.setClassificationMarking(TOPSECRETCLASSIFICATIONMARKING);

        MarkingValueTest.assertMarkingValue(TOPSECRETCLASSIFICATIONMARKING.GetClassification(),
                                            new Marking(field.getClassificationMarking()).GetClassification());

    }

    @Test
    public void GetValueWithMarkingTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals("UNCLASSIFIED NORTHCOM Volunteer Background Checks Changed", field.getValueWithMarking());
    }

    @Test
    public void GetPortionMarkingTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals("(U)", field.getPortionMarking());
    }

    @Test
    public void GetPreviousHistoryKeyNoneTest()
    {

        XsdField field = GetTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        assertEquals("00000000-0000-0000-0000-000000000000", field.getPreviousHistoryKey());

    }

    @Test
    public void GetPreviousHistoryKeyClassificationMarkingChangeTest()
    {

        XsdField field = GetTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        assertEquals("00000000-0000-0000-0000-000000000000", field.getPreviousHistoryKey());

        String fieldKey = field.getKey();

        field.setClassificationMarking(TOPSECRETCLASSIFICATIONMARKING);

        assertEquals(fieldKey, field.getKey());
        assertEquals(field.GetHistory().get(0).getKey(), field.getPreviousHistoryKey());

    }

    @Test
    public void GetFilenameDoesNotExistTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals("", field.getFilename());

    }

    @Test
    public void SetFilenameTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.setFilename("c:/Program Files/java/jre7/bin");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("c:/Program Files/java/jre7/bin", savedField.getFilename());

    }

    @Test
    public void GetExtensionDoesNotExistTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals("", field.getExtension());

    }

    @Test
    public void SetExtensionTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.setExtension(".jpeg");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("jpeg", savedField.getExtension());

    }

    @Test
    public void GetMimeTypeDoesNotExistTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals("", field.getMimeType());

    }

    @Test
    public void SetMimeTypeTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.setMimeType("application/pdf");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("application/pdf", savedField.getMimeType());

    }

    @Test
    public void GetHashDoesNotExistTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals("", field.getHash());

    }

    @Test
    public void SetHashTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.setHash("8743b52063cd84097a65d1633f5c74f5");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("8743b52063cd84097a65d1633f5c74f5", savedField.getHash());

    }

    @Test
    public void PreviousHistoryOrderTest() throws DataFormatException
    {

        XsdField field = XsdFieldTest.GetTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        field.setTypedValue(1111);
        field.setTypedValue(2222);

        assertEquals(2222, field.getIntegerValue());
        assertEquals(1111, field.GetHistory().get(0).getIntegerValue());
        assertEquals(CoalesceTypeInstances.TEST_MISSION_BASE64_VALUE, field.GetHistory().get(1).getIntegerValue());

    }

    @Test
    public void GetSuspendHistoryTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = GetTestMissionNameField(mission);

        assertFalse(field.GetSuspendHistory());

        field.SetSuspendHistory(true);

        assertTrue(field.GetSuspendHistory());

        field.SetSuspendHistory(false);

        assertFalse(field.GetSuspendHistory());
    }

    @Test
    public void SetSuspendHistoryTrueTest() throws DataFormatException
    {

        XsdField field = XsdFieldTest.GetTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        field.setTypedValue(2222);

        assertEquals(2222, field.getIntegerValue());
        assertEquals(1, field.GetHistory().size());
        assertEquals(CoalesceTypeInstances.TEST_MISSION_BASE64_VALUE, field.GetHistory().get(0).getIntegerValue());

    }

    @Test
    public void SetSuspendHistoryFalseTest() throws DataFormatException
    {

        XsdField field = XsdFieldTest.GetTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        field.SetSuspendHistory(true);
        field.setTypedValue(2222);

        assertEquals(2222, field.getIntegerValue());
        assertTrue(field.GetHistory().isEmpty());

    }

    @Test
    public void SetSuspendHistoryFalseBinaryTest()
    {

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset,
                                                                    "Binary",
                                                                    ECoalesceFieldDataTypes.BinaryType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.Create(parentRecord, fileFieldDef);

        assertTrue(field.GetHistory().isEmpty());

        field.setValue("Something");

        assertTrue(field.GetHistory().isEmpty());

    }

    @Test
    public void SetSuspendHistoryFalseFileTest()
    {

        FileTestResult result = GetJpgFile();

        XsdField field = result.SavedField;
        assertTrue(field.GetHistory().isEmpty());

        field.setValue("Something");

        assertTrue(field.getHash().isEmpty());

    }

    @Test
    public void GetDateCreatedExistTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_CREATED, field.getDateCreated());

    }

    @Test
    public void SetDateCreatedTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = GetTestMissionNameField(mission);

        DateTime now = JodaDateTimeHelper.NowInUtc();

        field.setDateCreated(now);

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals(now, savedField.getDateCreated());

    }

    @Test
    public void GetLastModifiedExistTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_MODIFIED, field.getLastModified());

    }

    @Test
    public void SetLastModifiedTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = GetTestMissionNameField(mission);

        DateTime now = JodaDateTimeHelper.NowInUtc();

        field.setLastModified(now);

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals(now, savedField.getLastModified());

    }

    @Test
    public void ToXmlTest()
    {

        XsdField field = GetTestMissionNameField();

        String fieldXml = field.toXml();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_XML,
                     fieldXml.replace("\n", "").replace("\r", "").replace("    ", ""));

    }

    @Test
    public void GetCoalesceFullFilenameNotFileTest()
    {
        XsdField field = GetTestMissionNameField();

        assertTrue(StringHelper.IsNullOrEmpty(field.GetCoalesceFullFilename()));
    }

    @Test
    public void GetCoalesceFullFilenameTwoSubDirTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        GetCoalesceFullFilename();
    }

    @Test
    public void GetCoalesceFullFilenameZeroSubDirTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        CoalesceUnitTestSettings.SetSubDirectoryLength(0);

        GetCoalesceFullFilename();
    }

    @Test
    public void GetCoalesceFullFilenameFiveSubDirTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        CoalesceUnitTestSettings.SetSubDirectoryLength(5);

        GetCoalesceFullFilename();

    }

    private void GetCoalesceFullFilename() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException
    {

        FileTestResult result = GetJpgFile();

        assertEquals(XsdFieldCommon.CallGetBaseFilenameWithFullDirectoryPathForKey(result.Field.getKey(), false) + ".jpg",
                     result.SavedField.GetCoalesceFullFilename());

    }

    @Test
    public void GetCoalesceFullThumbnailFilenameNotFileTest()
    {
        XsdField field = GetTestMissionNameField();

        assertTrue(StringHelper.IsNullOrEmpty(field.GetCoalesceFullThumbnailFilename()));
    }

    @Test
    public void GetCoalesceFullThumbnailFilenameTwoSubDirTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        GetCoalesceFullThumbnailFilename();
    }

    @Test
    public void GetCoalesceFullThumbnailFilenameZeroSubDirTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        CoalesceUnitTestSettings.SetSubDirectoryLength(0);

        GetCoalesceFullThumbnailFilename();
    }

    @Test
    public void GetCoalesceFullThumbnailFilenameFiveSubDirTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        CoalesceUnitTestSettings.SetSubDirectoryLength(5);

        GetCoalesceFullThumbnailFilename();

    }

    private void GetCoalesceFullThumbnailFilename() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException
    {

        FileTestResult result = GetJpgFile();

        assertEquals(XsdFieldCommon.CallGetBaseFilenameWithFullDirectoryPathForKey(result.Field.getKey(), false)
                + "_thumb.jpg", result.SavedField.GetCoalesceFullThumbnailFilename());

    }

    @Test
    public void GetCoalesceFilenameWithLastModifiedTagNotFileTest()
    {
        XsdField field = GetTestMissionNameField();

        assertTrue(StringHelper.IsNullOrEmpty(field.GetCoalesceFilenameWithLastModifiedTag()));
    }

    @Test
    public void GetCoalesceFilenameWithLastModifiedTagTwoSubDirTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        GetCoalesceFilenameWithLastModifiedTag();
    }

    @Test
    public void GetCoalesceFilenameWithLastModifiedTagZeroSubDirTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        CoalesceUnitTestSettings.SetSubDirectoryLength(0);

        GetCoalesceFilenameWithLastModifiedTag();
    }

    @Test
    public void GetCoalesceFilenameWithLastModifiedTagFiveSubDirTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        CoalesceUnitTestSettings.SetSubDirectoryLength(5);

        GetCoalesceFilenameWithLastModifiedTag();

    }

    private void GetCoalesceFilenameWithLastModifiedTag() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        FileTestResult result = GetJpgFile();

        // Create file
        File fieldFile = new File(result.Field.GetCoalesceFullFilename());
        try
        {
            fieldFile.createNewFile();
        }
        catch (IOException e)
        {
            // Catch
        }

        assertEquals(fieldFile.getName() + "?" + fieldFile.lastModified(),
                     result.SavedField.GetCoalesceFilenameWithLastModifiedTag());

        // Delete file
        fieldFile.delete();

    }

    @Test
    public void GetCoalesceThumbnailFilenameWithLastModifiedTagNotFileTest()
    {
        XsdField field = GetTestMissionNameField();

        assertTrue(StringHelper.IsNullOrEmpty(field.GetCoalesceThumbnailFilenameWithLastModifiedTag()));
    }

    @Test
    public void GetCoalesceThumbnailFilenameWithLastModifiedTagTwoSubDirTest() throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        GetCoalesceThumbnailFilenameWithLastModifiedTag();
    }

    @Test
    public void GetCoalesceThumbnailFilenameWithLastModifiedTagZeroSubDirTest() throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        CoalesceUnitTestSettings.SetSubDirectoryLength(0);

        GetCoalesceThumbnailFilenameWithLastModifiedTag();
    }

    @Test
    public void GetCoalesceThumbnailFilenameWithLastModifiedTagFiveSubDirTest() throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        CoalesceUnitTestSettings.SetSubDirectoryLength(5);

        GetCoalesceThumbnailFilenameWithLastModifiedTag();

    }

    private void GetCoalesceThumbnailFilenameWithLastModifiedTag() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        FileTestResult result = GetJpgFile();

        // Create file
        File fieldFile = new File(result.Field.GetCoalesceFullThumbnailFilename());
        try
        {
            fieldFile.createNewFile();
        }
        catch (IOException e)
        {
            // Catch
        }

        assertEquals(fieldFile.getName() + "?" + fieldFile.lastModified(),
                     result.SavedField.GetCoalesceThumbnailFilenameWithLastModifiedTag());

        // Delete file
        fieldFile.delete();

    }

    @Test
    public void GetCoalesceFilenameNotFileTest()
    {
        XsdField field = GetTestMissionNameField();

        assertTrue(StringHelper.IsNullOrEmpty(field.GetCoalesceFilename()));
    }

    @Test
    public void GetCoalesceFilenameTest()
    {

        FileTestResult result = GetJpgFile();

        assertEquals(GUIDHelper.RemoveBrackets(result.Field.getKey()) + ".jpg", result.SavedField.GetCoalesceFilename());

    }

    @Test
    public void GetCoalesceThumbnailFilenameNotFileTest()
    {
        XsdField field = GetTestMissionNameField();

        assertTrue(StringHelper.IsNullOrEmpty(field.GetCoalesceThumbnailFilename()));
    }

    @Test
    public void GetCoalesceThumbnailFilename()
    {

        FileTestResult result = GetJpgFile();

        assertEquals(GUIDHelper.RemoveBrackets(result.Field.getKey()) + "_thumb.jpg",
                     result.SavedField.GetCoalesceThumbnailFilename());

    }

    @Test
    public void StringTypeTest() throws DataFormatException
    {

        XsdField field = GetTestMissionNameField();

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
    public void SetTypedValueStringTypeTypeMismatchTest()
    {

        XsdField field = GetTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(UUID.randomUUID());

    }

    @Test
    public void UriTypeTest() throws DataFormatException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset, "Uri", ECoalesceFieldDataTypes.UriType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.Create(parentRecord, fileFieldDef);
        field.setTypedValue("uri:document/pdf");

        Object data = field.getData();

        assertTrue(data instanceof String);
        assertEquals("uri:document/pdf", data);
        assertEquals("uri:document/pdf", field.getValue());

    }

    @Test
    public void GetDataDateTimeTypeNotSetTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset,
                                                                    "DateTime",
                                                                    ECoalesceFieldDataTypes.DateTimeType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.Create(parentRecord, fileFieldDef);

        assertNull(field.getDateTimeValue());

    }

    @Test
    public void GetDataSetTypedValueDateTimeTypeTest() throws DataFormatException
    {
        XsdField field = GetTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

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
    public void SetTypedValueDateTimeTypeTypeMismatchTest()
    {

        XsdField field = GetTestMissionNameField();

        field.setTypedValue(JodaDateTimeHelper.NowInUtc());

    }

    @Test
    public void GetDataBinaryTypeNotSetTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset,
                                                                    "Binary",
                                                                    ECoalesceFieldDataTypes.BinaryType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.Create(parentRecord, fileFieldDef);

        byte[] bytes = new byte[0];

        assertArrayEquals(bytes, field.getBinaryValue());

    }

    @Test
    public void GetDataSetTypedValueBinaryTypeTest() throws UnsupportedEncodingException, DataFormatException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset,
                                                                    "Binary",
                                                                    ECoalesceFieldDataTypes.BinaryType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.Create(parentRecord, fileFieldDef);

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
    public void SetTypedValueBinaryTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        XsdField field = GetTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        String byteString = "Testing String";
        byte[] dataBytes = byteString.getBytes("US-ASCII");
        field.setTypedValue(dataBytes);

    }

    @Test
    public void GetDataSetTypedValueBooleanTypeTest() throws DataFormatException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset,
                                                                    "Boolean",
                                                                    ECoalesceFieldDataTypes.BooleanType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.Create(parentRecord, fileFieldDef);

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
    public void SetTypedValueBooleanTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        XsdField field = GetTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(true);

    }

    @Test(expected = DataFormatException.class)
    public void GetDataIntegerTypeNotSetTest() throws DataFormatException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset,
                                                                    "Integer",
                                                                    ECoalesceFieldDataTypes.IntegerType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.Create(parentRecord, fileFieldDef);

        field.getIntegerValue();

    }

    @Test
    public void GetDataSetTypedValueIntegerTypeTest() throws DataFormatException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset,
                                                                    "Integer",
                                                                    ECoalesceFieldDataTypes.IntegerType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.Create(parentRecord, fileFieldDef);

        assertEquals("", field.getValue());

        field.setTypedValue(1111);

        Object data = field.getData();

        assertTrue(data instanceof Integer);
        assertEquals(1111, data);
        assertEquals("1111", field.getValue());
        assertEquals(1111, field.getIntegerValue());

    }

    @Test(expected = ClassCastException.class)
    public void SetTypedValueIntgerTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        XsdField field = GetTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(1111);

    }

    @Test
    public void GetDataGuidTypeNotSetTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset,
                                                                    "GUID",
                                                                    ECoalesceFieldDataTypes.GuidType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.Create(parentRecord, fileFieldDef);

        assertNull(field.getGuidValue());

    }

    @Test
    public void GetDataSetTypedValueGuidTypeTest() throws DataFormatException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset,
                                                                    "GUID",
                                                                    ECoalesceFieldDataTypes.GuidType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.Create(parentRecord, fileFieldDef);

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
    public void SetTypedValueGUIDTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        XsdField field = GetTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(UUID.randomUUID());

    }

    // -----------------------------------------------------------------------//
    // Private Static Methods
    // -----------------------------------------------------------------------//

    private static void InitializeSettings()
    {

        CoalesceUnitTestSettings.SetSubDirectoryLength(2);

    }

    private static XsdField GetTestMissionNameField(String entityXml)
    {

        XsdEntity entity = XsdEntity.create(entityXml);

        return GetTestMissionNameField(entity);

    }

    private static XsdField GetTestMissionNameField(XsdEntity entity)
    {

        return GetTestMissionFieldByName(entity, CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

    }

    public static XsdField GetTestMissionNameField()
    {

        return GetTestMissionNameField(CoalesceTypeInstances.TEST_MISSION);

    }

    public static XsdField GetTestMissionFieldByName(String fieldPath)
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        return GetTestMissionFieldByName(mission, fieldPath);

    }

    private static XsdField GetTestMissionFieldByName(XsdEntity entity, String fieldPath)
    {

        XsdDataObject fdo = entity.getDataObjectForNamePath(fieldPath);

        assertTrue(fdo instanceof XsdField);

        return (XsdField) fdo;

    }

    private static XsdField GetSavedTestMissionField(XsdEntity entity)
    {

        String serializedMission = entity.toXml();

        return GetTestMissionNameField(serializedMission);

    }

    private FileTestResult GetJpgFile()
    {

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset,
                                                                    "File",
                                                                    ECoalesceFieldDataTypes.FileType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField fileField = XsdField.Create(parentRecord, fileFieldDef);
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
