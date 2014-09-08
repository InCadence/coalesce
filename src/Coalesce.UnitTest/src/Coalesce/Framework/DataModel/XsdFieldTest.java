package Coalesce.Framework.DataModel;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import Coalesce.Framework.DataModel.XsdFieldCommon;
import Coalesce.Common.Classification.Marking;
import Coalesce.Common.Classification.MarkingValueTest;
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

        assertEquals(CoalesceTypeInstances.TESTMISSIONNAMEKEY, field.GetKey());

    }

    @Test
    public void SetKeyTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        UUID newGuid = UUID.randomUUID();
        field.SetKey(newGuid.toString());

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals(savedField.GetKey().toUpperCase(), newGuid.toString().toUpperCase());

    }

    @Test
    public void GetNameTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals(CoalesceTypeInstances.TESTMISSIONNAMENAME, field.GetName());

    }

    @Test
    public void SetNameTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.SetName("Testingname");

        String serializedMission = mission.ToXml();
        XsdEntity savedMission = XsdEntity.Create(serializedMission);

        XsdField savedField = (XsdField) savedMission.GetDataObjectForNamePath(CoalesceTypeInstances.TESTMISSIONNAMEPATH.replace(CoalesceTypeInstances.TESTMISSIONNAMENAME,
                                                                                                                                 "Testingname"));

        assertEquals("Testingname", savedField.GetName());

    }

    @Test
    public void GetValueTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals(CoalesceTypeInstances.TESTMISSIONNAMEVALUE, field.GetValue());

    }

    @Test
    public void SetValueTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.SetValue("Testingvalue");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("Testingvalue", savedField.GetValue());

    }

    @Test
    public void GetDataType()
    {
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField stringField = GetTestMissionFieldByName(mission, CoalesceTypeInstances.TESTMISSIONNAMEPATH);
        assertEquals(ECoalesceFieldDataTypes.StringType, stringField.GetDataType());

        XsdField dateField = GetTestMissionFieldByName(mission, CoalesceTypeInstances.TESTMISSIONSTARTTIMEPATH);
        assertEquals(ECoalesceFieldDataTypes.DateTimeType, dateField.GetDataType());

        XsdField integerField = GetTestMissionFieldByName(mission, CoalesceTypeInstances.TESTMISSIONBASE64PATH);
        assertEquals(ECoalesceFieldDataTypes.IntegerType, integerField.GetDataType());

    }

    @Test
    public void SetDateType()
    {
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField stringField = GetTestMissionFieldByName(mission, CoalesceTypeInstances.TESTMISSIONNAMEPATH);
        stringField.SetDataType(ECoalesceFieldDataTypes.DateTimeType);

        XsdField dateField = GetTestMissionFieldByName(mission, CoalesceTypeInstances.TESTMISSIONSTARTTIMEPATH);
        dateField.SetDataType(ECoalesceFieldDataTypes.IntegerType);

        XsdField integerField = GetTestMissionFieldByName(mission, CoalesceTypeInstances.TESTMISSIONBASE64PATH);
        integerField.SetDataType(ECoalesceFieldDataTypes.StringType);

        String serializedMission = mission.ToXml();
        XsdEntity savedMission = XsdEntity.Create(serializedMission);

        XsdField savedStringField = GetTestMissionFieldByName(savedMission, CoalesceTypeInstances.TESTMISSIONNAMEPATH);
        assertEquals(ECoalesceFieldDataTypes.DateTimeType, savedStringField.GetDataType());

        XsdField savedDateField = GetTestMissionFieldByName(savedMission, CoalesceTypeInstances.TESTMISSIONSTARTTIMEPATH);
        assertEquals(ECoalesceFieldDataTypes.IntegerType, savedDateField.GetDataType());

        XsdField savedIntegerField = GetTestMissionFieldByName(savedMission, CoalesceTypeInstances.TESTMISSIONBASE64PATH);
        assertEquals(ECoalesceFieldDataTypes.StringType, savedIntegerField.GetDataType());

    }

    @Test
    public void GetLabelTest()
    {

        XsdField field = GetTestMissionFieldByName(CoalesceTypeInstances.TESTMISSIONACTIONNUMBERPATH);

        assertEquals(CoalesceTypeInstances.TESTMISSIONACTIONNUMBERLABEL, field.GetLabel());

    }

    @Test
    public void GetLabelDoesNotExistTest()
    {

        XsdField field = GetTestMissionFieldByName(CoalesceTypeInstances.TESTMISSIONNAMEPATH);

        assertEquals("", field.GetLabel());

    }

    @Test
    public void SetLabelTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.SetLabel("Testinglabel");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("Testinglabel", savedField.GetLabel());

    }

    @Test
    public void SetLabelNullTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.SetLabel(null);

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("", savedField.GetLabel());

    }

    @Test
    public void GetSizeDoesNotExistTest()
    {
        XsdField field = GetTestMissionNameField();

        assertEquals(0, field.GetSize());

    }

    @Test
    public void SetSizeTest()
    {
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);
        field.SetSize(128);

        String serializedMission = mission.ToXml();
        XsdEntity savedMission = XsdEntity.Create(serializedMission);

        XsdField savedField = GetTestMissionNameField(savedMission);
        assertEquals(128, savedField.GetSize());

    }

    @Test
    public void GetModifiedByDoesNotExistTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals("", field.GetModifiedBy());

    }

    @Test
    public void SetModifiedByTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.SetModifiedBy("TestingModifiedBy");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("TestingModifiedBy", savedField.GetModifiedBy());

    }

    @Test
    public void GetModifiedByIpDoesNotExistTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals("", field.GetModifiedByIP());

    }

    @Test
    public void SetModifiedByIpTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.SetModifiedByIP("192.168.2.2");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("192.168.2.2", savedField.GetModifiedByIP());

    }

    @Test
    public void GetClassificationMarkingDefaultTest()
    {

        XsdField field = GetTestMissionNameField();

        MarkingValueTest.assertMarkingValue(new Marking().GetClassification(),
                                            new Marking(field.GetClassificationMarking()).GetClassification());

    }

    @Test
    public void GetClassificationMarkingAfterSetAndSerializedTest()
    {
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.SetClassificationMarking(TOPSECRETCLASSIFICATIONMARKING);

        XsdField savedField = GetSavedTestMissionField(mission);

        MarkingValueTest.assertMarkingValue(TOPSECRETCLASSIFICATIONMARKING.GetClassification(),
                                            new Marking(savedField.GetClassificationMarking()).GetClassification());

    }

    @Test
    public void SetClassificationMarkingTopSecretTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = (XsdField) mission.GetDataObjectForNamePath(CoalesceTypeInstances.TESTMISSIONNAMEPATH);

        field.SetClassificationMarking(TOPSECRETCLASSIFICATIONMARKING);

        MarkingValueTest.assertMarkingValue(TOPSECRETCLASSIFICATIONMARKING.GetClassification(),
                                            new Marking(field.GetClassificationMarking()).GetClassification());

    }

    @Test
    public void GetValueWithMarkingTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals("UNCLASSIFIED NORTHCOM Volunteer Background Checks Changed", field.GetValueWithMarking());
    }

    @Test
    public void GetPortionMarkingTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals("(U)", field.GetPortionMarking());
    }

    @Test
    public void GetPreviousHistoryKeyNoneTest()
    {

        XsdField field = GetTestMissionFieldByName(CoalesceTypeInstances.TESTMISSIONBASE64PATH);

        assertEquals("00000000-0000-0000-0000-000000000000", field.GetPreviousHistoryKey());

    }

    @Test
    public void GetPreviousHistoryKeyClassificationMarkingChangeTest()
    {

        XsdField field = GetTestMissionFieldByName(CoalesceTypeInstances.TESTMISSIONBASE64PATH);

        assertEquals("00000000-0000-0000-0000-000000000000", field.GetPreviousHistoryKey());

        String fieldKey = field.GetKey();

        field.SetClassificationMarking(TOPSECRETCLASSIFICATIONMARKING);

        assertEquals(fieldKey, field.GetKey());
        assertEquals(field.GetHistory().get(0).GetKey(), field.GetPreviousHistoryKey());

    }

    @Test
    public void GetFilenameDoesNotExistTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals("", field.GetFilename());

    }

    @Test
    public void SetFilenameTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.SetFilename("c:/Program Files/java/jre7/bin");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("c:/Program Files/java/jre7/bin", savedField.GetFilename());

    }

    @Test
    public void GetExtensionDoesNotExistTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals("", field.GetExtension());

    }

    @Test
    public void SetExtensionTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.SetExtension(".jpeg");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("jpeg", savedField.GetExtension());

    }

    @Test
    public void GetMimeTypeDoesNotExistTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals("", field.GetMimeType());

    }

    @Test
    public void SetMimeTypeTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.SetMimeType("application/pdf");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("application/pdf", savedField.GetMimeType());

    }

    @Test
    public void GetHashDoesNotExistTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals("", field.GetHash());

    }

    @Test
    public void SetHashTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.SetHash("8743b52063cd84097a65d1633f5c74f5");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("8743b52063cd84097a65d1633f5c74f5", savedField.GetHash());

    }

    @Test
    public void PreviousHistoryOrderTest()
    {

        XsdField field = XsdFieldTest.GetTestMissionFieldByName(CoalesceTypeInstances.TESTMISSIONBASE64PATH);

        field.SetTypedValue(1111);
        field.SetTypedValue(2222);

        assertEquals(2222, field.GetIntegerValue());
        assertEquals(1111, field.GetHistory().get(0).GetIntegerValue());
        assertEquals(CoalesceTypeInstances.TESTMISSIONBASE64VALUE, field.GetHistory().get(1).GetIntegerValue());

    }

    @Test
    public void GetSuspendHistoryTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        assertFalse(field.GetSuspendHistory());

        field.SetSuspendHistory(true);

        assertTrue(field.GetSuspendHistory());

        field.SetSuspendHistory(false);

        assertFalse(field.GetSuspendHistory());
    }

    @Test
    public void SetSuspendHistoryTrueTest()
    {

        XsdField field = XsdFieldTest.GetTestMissionFieldByName(CoalesceTypeInstances.TESTMISSIONBASE64PATH);

        field.SetTypedValue(2222);

        assertEquals(2222, field.GetIntegerValue());
        assertEquals(1, field.GetHistory().size());
        assertEquals(CoalesceTypeInstances.TESTMISSIONBASE64VALUE, field.GetHistory().get(0).GetIntegerValue());

    }

    @Test
    public void SetSuspendHistoryFalseTest()
    {

        XsdField field = XsdFieldTest.GetTestMissionFieldByName(CoalesceTypeInstances.TESTMISSIONBASE64PATH);

        field.SetSuspendHistory(true);
        field.SetTypedValue(2222);

        assertEquals(2222, field.GetIntegerValue());
        assertTrue(field.GetHistory().isEmpty());

    }

    @Test
    public void SetSuspendHistoryFalseBinaryTest()
    {

        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.GetDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.Create(parentRecordset,
                                                                    "Binary",
                                                                    ECoalesceFieldDataTypes.BinaryType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.Create(parentRecord, fileFieldDef);

        assertTrue(field.GetHistory().isEmpty());

        field.SetValue("Something");

        assertTrue(field.GetHistory().isEmpty());

    }

    @Test
    public void SetSuspendHistoryFalseFileTest()
    {

        FileTestResult result = GetJpgFile();

        XsdField field = result.SavedField;
        assertTrue(field.GetHistory().isEmpty());

        field.SetValue("Something");

        assertTrue(field.GetHash().isEmpty());

    }

    @Test
    public void GetDateCreatedExistTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals(CoalesceTypeInstances.TESTMISSIONNAMECREATED, field.GetDateCreated());

    }

    @Test
    public void SetDateCreatedTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        DateTime now = JodaDateTimeHelper.NowInUtc();

        field.SetDateCreated(now);

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals(now, savedField.GetDateCreated());

    }

    @Test
    public void GetLastModifiedExistTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals(CoalesceTypeInstances.TESTMISSIONNAMEMODIFIED, field.GetLastModified());

    }

    @Test
    public void SetLastModifiedTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        DateTime now = JodaDateTimeHelper.NowInUtc();

        field.SetLastModified(now);

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals(now, savedField.GetLastModified());

    }

    @Test
    public void ToXmlTest()
    {

        XsdField field = GetTestMissionNameField();

        String fieldXml = field.ToXml();

        assertEquals(CoalesceTypeInstances.TESTMISSIONNAMEXML,
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

        assertEquals(XsdFieldCommon.CallGetBaseFilenameWithFullDirectoryPathForKey(result.Field.GetKey(), false) + ".jpg",
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

        assertEquals(XsdFieldCommon.CallGetBaseFilenameWithFullDirectoryPathForKey(result.Field.GetKey(), false)
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

        assertEquals(GUIDHelper.RemoveBrackets(result.Field.GetKey()) + ".jpg", result.SavedField.GetCoalesceFilename());

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

        assertEquals(GUIDHelper.RemoveBrackets(result.Field.GetKey()) + "_thumb.jpg",
                     result.SavedField.GetCoalesceThumbnailFilename());

    }

    @Test
    public void StringTypeTest()
    {

        XsdField field = GetTestMissionNameField();

        Object data = field.GetData();

        assertTrue(data instanceof String);
        assertEquals(CoalesceTypeInstances.TESTMISSIONNAMEVALUE, data);

        field.SetTypedValue("Changed");

        data = null;
        data = field.GetData();

        assertTrue(data instanceof String);
        assertEquals("Changed", data);
        assertEquals("Changed", field.GetValue());

    }

    @Test(expected = ClassCastException.class)
    public void SetTypedValueStringTypeTypeMismatchTest()
    {

        XsdField field = GetTestMissionFieldByName(CoalesceTypeInstances.TESTMISSIONSTARTTIMEPATH);

        field.SetTypedValue(UUID.randomUUID());

    }

    @Test
    public void UriTypeTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.GetDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.Create(parentRecordset, "Uri", ECoalesceFieldDataTypes.UriType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.Create(parentRecord, fileFieldDef);
        field.SetTypedValue("uri:document/pdf");

        Object data = field.GetData();

        assertTrue(data instanceof String);
        assertEquals("uri:document/pdf", data);
        assertEquals("uri:document/pdf", field.GetValue());

    }

    @Test
    public void GetDataDateTimeTypeNotSetTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.GetDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.Create(parentRecordset,
                                                                    "DateTime",
                                                                    ECoalesceFieldDataTypes.DateTimeType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.Create(parentRecord, fileFieldDef);

        assertNull(field.GetDateTimeValue());

    }

    @Test
    public void GetDataSetTypedValueDateTimeTypeTest()
    {
        XsdField field = GetTestMissionFieldByName(CoalesceTypeInstances.TESTMISSIONSTARTTIMEPATH);

        Object data = field.GetData();

        assertTrue(data instanceof DateTime);
        assertEquals(JodaDateTimeHelper.FromXmlDateTimeUTC(CoalesceTypeInstances.TESTMISSIONSTARTTIMEVALUE), data);

        DateTime now = JodaDateTimeHelper.NowInUtc();
        field.SetTypedValue(now);

        assertEquals(JodaDateTimeHelper.ToXmlDateTimeUTC(now), field.GetValue());

        data = null;
        data = field.GetData();

        assertTrue(data instanceof DateTime);
        assertEquals(now, data);
        assertEquals(now, field.GetDateTimeValue());

    }

    @Test(expected = ClassCastException.class)
    public void SetTypedValueDateTimeTypeTypeMismatchTest()
    {

        XsdField field = GetTestMissionNameField();

        field.SetTypedValue(JodaDateTimeHelper.NowInUtc());

    }

    @Test
    public void GetDataBinaryTypeNotSetTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.GetDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.Create(parentRecordset,
                                                                    "Binary",
                                                                    ECoalesceFieldDataTypes.BinaryType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.Create(parentRecord, fileFieldDef);

        byte[] bytes = new byte[0];

        assertArrayEquals(bytes, field.GetBinaryValue());

    }

    @Test
    public void GetDataSetTypedValueBinaryTypeTest() throws UnsupportedEncodingException
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.GetDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.Create(parentRecordset,
                                                                    "Binary",
                                                                    ECoalesceFieldDataTypes.BinaryType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.Create(parentRecord, fileFieldDef);

        String byteString = "Testing String";
        byte[] dataBytes = byteString.getBytes("US-ASCII");
        field.SetTypedValue(dataBytes);

        Object data = field.GetData();

        assertTrue(data instanceof byte[]);
        assertArrayEquals(dataBytes, (byte[]) data);
        assertArrayEquals(dataBytes, field.GetBinaryValue());
        assertEquals("VGVzdGluZyBTdHJpbmc=", field.GetValue());

    }

    @Test(expected = ClassCastException.class)
    public void SetTypedValueBinaryTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        XsdField field = GetTestMissionFieldByName(CoalesceTypeInstances.TESTMISSIONSTARTTIMEPATH);

        String byteString = "Testing String";
        byte[] dataBytes = byteString.getBytes("US-ASCII");
        field.SetTypedValue(dataBytes);

    }

    @Test
    public void GetDataSetTypedValueBooleanTypeTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.GetDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.Create(parentRecordset,
                                                                    "Boolean",
                                                                    ECoalesceFieldDataTypes.BooleanType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.Create(parentRecord, fileFieldDef);

        assertFalse(field.GetBooleanValue());
        assertEquals("false", field.GetValue().toLowerCase());

        field.SetTypedValue(true);

        Object data = field.GetData();

        assertTrue(data instanceof Boolean);
        assertEquals(true, data);
        assertEquals("true", field.GetValue().toLowerCase());
        assertEquals(true, field.GetBooleanValue());

    }

    @Test(expected = ClassCastException.class)
    public void SetTypedValueBooleanTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        XsdField field = GetTestMissionFieldByName(CoalesceTypeInstances.TESTMISSIONSTARTTIMEPATH);

        field.SetTypedValue(true);

    }

    @Test(expected = ClassCastException.class)
    public void GetDataIntegerTypeNotSetTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.GetDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.Create(parentRecordset,
                                                                    "Integer",
                                                                    ECoalesceFieldDataTypes.IntegerType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.Create(parentRecord, fileFieldDef);

        field.GetIntegerValue();

    }

    @Test
    public void GetDataSetTypedValueIntegerTypeTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.GetDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.Create(parentRecordset,
                                                                    "Integer",
                                                                    ECoalesceFieldDataTypes.IntegerType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.Create(parentRecord, fileFieldDef);

        assertEquals("", field.GetValue());

        field.SetTypedValue(1111);

        Object data = field.GetData();

        assertTrue(data instanceof Integer);
        assertEquals(1111, data);
        assertEquals("1111", field.GetValue());
        assertEquals(1111, field.GetIntegerValue());

    }

    @Test(expected = ClassCastException.class)
    public void SetTypedValueIntgerTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        XsdField field = GetTestMissionFieldByName(CoalesceTypeInstances.TESTMISSIONSTARTTIMEPATH);

        field.SetTypedValue(1111);

    }

    @Test
    public void GetDataGuidTypeNotSetTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.GetDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.Create(parentRecordset,
                                                                    "GUID",
                                                                    ECoalesceFieldDataTypes.GuidType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.Create(parentRecord, fileFieldDef);

        assertNull(field.GetGuidValue());

    }

    @Test
    public void GetDataSetTypedValueGuidTypeTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.GetDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.Create(parentRecordset,
                                                                    "GUID",
                                                                    ECoalesceFieldDataTypes.GuidType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.Create(parentRecord, fileFieldDef);

        assertEquals("", field.GetValue());

        UUID guid = UUID.randomUUID();
        field.SetTypedValue(guid);

        Object data = field.GetData();

        assertTrue(data instanceof UUID);
        assertEquals(guid, data);
        assertEquals(GUIDHelper.GetGuidString(guid), field.GetValue());
        assertEquals(guid, field.GetGuidValue());

    }

    @Test(expected = ClassCastException.class)
    public void SetTypedValueGUIDTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        XsdField field = GetTestMissionFieldByName(CoalesceTypeInstances.TESTMISSIONSTARTTIMEPATH);

        field.SetTypedValue(UUID.randomUUID());

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

        XsdEntity entity = XsdEntity.Create(entityXml);

        return GetTestMissionNameField(entity);

    }

    private static XsdField GetTestMissionNameField(XsdEntity entity)
    {

        return GetTestMissionFieldByName(entity, CoalesceTypeInstances.TESTMISSIONNAMEPATH);

    }

    public static XsdField GetTestMissionNameField()
    {

        return GetTestMissionNameField(CoalesceTypeInstances.TESTMISSION);

    }

    public static XsdField GetTestMissionFieldByName(String fieldPath)
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        return GetTestMissionFieldByName(mission, fieldPath);

    }

    private static XsdField GetTestMissionFieldByName(XsdEntity entity, String fieldPath)
    {

        XsdDataObject fdo = entity.GetDataObjectForNamePath(fieldPath);

        assertTrue(fdo instanceof XsdField);

        return (XsdField) fdo;

    }

    private static XsdField GetSavedTestMissionField(XsdEntity entity)
    {

        String serializedMission = entity.ToXml();

        return GetTestMissionNameField(serializedMission);

    }

    private FileTestResult GetJpgFile()
    {

        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.GetDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.Create(parentRecordset,
                                                                    "File",
                                                                    ECoalesceFieldDataTypes.FileType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField fileField = XsdField.Create(parentRecord, fileFieldDef);
        fileField.SetExtension("jpg");

        String savedEntity = entity.ToXml();

        XsdEntity desEntity = XsdEntity.Create(savedEntity);

        XsdField savedFileField = (XsdField) desEntity.GetDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/File");

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
