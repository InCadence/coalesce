package Coalesce.Framework.DataModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.junit.Test;

import Coalesce.Common.Classification.Marking;
import Coalesce.Common.Classification.MarkingValueTest;
import Coalesce.Common.Exceptions.DataFormatException;
import Coalesce.Common.Helpers.GUIDHelper;
import Coalesce.Common.Helpers.JodaDateTimeHelper;
import Coalesce.Common.UnitTest.CoalesceTypeInstances;
import Coalesce.Framework.GeneratedJAXB.Entity.Section;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset.Record;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset.Record.Field;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset.Record.Field.Fieldhistory;

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

public class XsdFieldHistoryTest {

    private static final Marking TOPSECRETCLASSIFICATIONMARKING = new Marking("//JOINT TOP SECRET AND USA//FOUO-LES//SBU/ACCM-BOB");

    /*
     * @BeforeClass public static void setUpBeforeClass() throws Exception { }
     * 
     * @AfterClass public static void tearDownAfterClass() throws Exception { }
     * 
     * @Before public void setUp() throws Exception { }
     * 
     * @After public void tearDown() throws Exception { }
     */

    @Test(expected = ClassCastException.class)
    public void ConstructorXsdFieldBaseXsdFieldHistoryTest()
    {

        XsdFieldHistory fh1 = new XsdFieldHistory();
        fh1.initialize(null, new Fieldhistory());

        @SuppressWarnings("unused")
        XsdFieldHistory fh2 = XsdFieldHistory.create((XsdFieldBase) fh1);

    }

    @Test
    public void ConstructorXsdFieldBaseXsdFieldTest()
    {

        XsdField field = new XsdField();
        field.initialize(null, new Field());

        @SuppressWarnings("unused")
        XsdFieldHistory fh = XsdFieldHistory.create((XsdFieldBase) field);

    }

    @Test(expected = ClassCastException.class)
    public void ConstructorXsdFieldHistoryNullTest()
    {

        XsdFieldHistory fh1 = new XsdFieldHistory();
        fh1.initialize(null, new Fieldhistory());

        @SuppressWarnings("unused")
        XsdFieldHistory fh2 = XsdFieldHistory.create(fh1);

    }

    @Test
    public void ConstructorXsdFieldHistoryTest()
    {
        XsdEntity entity = new XsdEntity();
        entity.initialize();

        XsdSection section = new XsdSection();
        section.initialize(entity, new Section());

        XsdRecordset rs = new XsdRecordset();
        rs.initialize(section, new Recordset());

        XsdRecord record = new XsdRecord();
        record.initialize(rs, new Record());

        XsdField field = new XsdField();
        field.initialize(record, new Field());

        @SuppressWarnings("unused")
        XsdFieldHistory fh = XsdFieldHistory.create(field);

    }

    @Test
    public void ConstructorNoPreviousHistory()
    {
        XsdField field = XsdFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        String fieldKey = field.getKey();

        XsdFieldHistory fh = XsdFieldHistory.create(field);

        assertEquals(fieldKey, field.getKey());
        assertFieldHistory(field, fh);

    }

    private void assertFieldHistory(XsdField field, XsdFieldHistory fieldHistory)
    {

        assertEquals(field.getName(), fieldHistory.getName());
        assertEquals("fieldhistory", fieldHistory.getType());
        assertEquals(field.getValue(), fieldHistory.getValue());
        assertEquals(field.getDataType(), fieldHistory.getDataType());
        assertEquals(field.getLabel(), fieldHistory.getLabel());
        assertEquals(field.getSize(), fieldHistory.getSize());
        assertEquals(field.getModifiedBy(), fieldHistory.getModifiedBy());
        assertEquals(field.getModifiedByIP(), fieldHistory.getModifiedByIP());
        assertEquals(field.getClassificationMarking(), fieldHistory.getClassificationMarking());
        assertEquals(field.getFilename(), fieldHistory.getFilename());
        assertEquals(field.getExtension(), fieldHistory.getExtension());
        assertEquals(field.getMimeType(), fieldHistory.getMimeType());
        assertEquals(field.getHash(), fieldHistory.getHash());
        assertEquals(field.getDateCreated(), fieldHistory.getDateCreated());
        assertEquals(field.getLastModified(), fieldHistory.getLastModified());
        assertEquals(field.getStatus(), fieldHistory.getStatus());

        Map<QName, String> otherAttributes = fieldHistory.getAttributes();
        for (Map.Entry<QName, String> otherAttr : field.getAttributes().entrySet())
        {
            assertTrue(otherAttributes.containsKey(otherAttr.getKey()));
            assertEquals(otherAttributes.get(otherAttr.getKey()), otherAttr.getValue());
        }
    }

    @Test
    public void ConstructorPreviousHistory() throws ClassCastException, DataFormatException
    {

        XsdField field = XsdFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        field.setSuspendHistory(true);
        XsdFieldHistory fh = XsdFieldHistory.create(field);
        assertFieldHistory(field, fh);

        field.setTypedValue(2222);
        field.setSuspendHistory(false);

        assertEquals(2222, field.getIntegerValue());
        assertEquals(1, field.getHistory().size());
        assertEquals(CoalesceTypeInstances.TEST_MISSION_BASE64_VALUE, field.getHistory().get(0).getIntegerValue());
        assertEquals(fh, field.getHistory().get(0));

    }

    @Test
    public void GetKeyTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_KEY, field.getKey());

    }

    @Test
    public void SetKeyTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        UUID newGuid = UUID.randomUUID();
        field.setKey(newGuid.toString());

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals(savedField.getKey().toUpperCase(), newGuid.toString().toUpperCase());

    }

    @Test
    public void GetNameTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_NAME, field.getName());

    }

    @Test
    public void SetNameTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.setName("Testingname");

        String serializedMission = mission.toXml();
        XsdEntity savedMission = XsdEntity.create(serializedMission);

        XsdFieldHistory savedField = GetTestMissionNameFieldHistory(savedMission);

        assertEquals("Testingname", savedField.getName());

    }

    @Test
    public void GetValueTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_HISTORY_VALUE, field.getValue());

    }

    @Test
    public void SetValueTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.setValue("Testingvalue");

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("Testingvalue", savedField.getValue());

    }

    @Test
    public void GetDataType()
    {
        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdFieldHistory stringField = GetTestMissionFieldHistoryByName(mission, CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
        assertEquals(ECoalesceFieldDataTypes.StringType, stringField.getDataType());

        XsdFieldHistory dateField = GetTestMissionFieldHistoryByName(mission,
                                                                     CoalesceTypeInstances.TEST_MISSION_INCIDENT_DATE_TIME_PATH);
        assertEquals(ECoalesceFieldDataTypes.DateTimeType, dateField.getDataType());

    }

    @Test
    public void SetDateType()
    {
        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdFieldHistory stringField = GetTestMissionFieldHistoryByName(mission, CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
        stringField.setDataType(ECoalesceFieldDataTypes.DateTimeType);

        XsdFieldHistory dateField = GetTestMissionFieldHistoryByName(mission,
                                                                     CoalesceTypeInstances.TEST_MISSION_INCIDENT_DATE_TIME_PATH);
        dateField.setDataType(ECoalesceFieldDataTypes.IntegerType);

        String serializedMission = mission.toXml();
        XsdEntity savedMission = XsdEntity.create(serializedMission);

        XsdFieldHistory savedStringField = GetTestMissionFieldHistoryByName(savedMission,
                                                                            CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
        assertEquals(ECoalesceFieldDataTypes.DateTimeType, savedStringField.getDataType());

        XsdFieldHistory savedDateField = GetTestMissionFieldHistoryByName(savedMission,
                                                                          CoalesceTypeInstances.TEST_MISSION_INCIDENT_DATE_TIME_PATH);
        assertEquals(ECoalesceFieldDataTypes.IntegerType, savedDateField.getDataType());

    }

    @Test
    public void GetLabelTest()
    {

        XsdFieldHistory field = GetTestMissionFieldHistoryByName(CoalesceTypeInstances.TEST_MISSION_ACTION_NUMBER_PATH);

        assertEquals(CoalesceTypeInstances.TEST_MISSION_ACTION_NUMBER_LABEL_HISTORY, field.getLabel());

    }

    @Test
    public void GetLabelDoesNotExistTest()
    {

        XsdFieldHistory field = GetTestMissionFieldHistoryByName(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

        assertEquals("", field.getLabel());

    }

    @Test
    public void SetLabelTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.setLabel("Testinglabel");

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("Testinglabel", savedField.getLabel());

    }

    @Test
    public void SetLabelNullTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.setLabel(null);

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("", savedField.getLabel());

    }

    @Test
    public void GetSizeDoesNotExistTest()
    {
        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals(0, field.getSize());

    }

    @Test
    public void SetSizeTest()
    {
        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);
        field.setSize(128);

        String serializedMission = mission.toXml();
        XsdEntity savedMission = XsdEntity.create(serializedMission);

        XsdFieldHistory savedField = GetTestMissionNameFieldHistory(savedMission);
        assertEquals(128, savedField.getSize());

    }

    @Test
    public void GetModifiedByDoesNotExistTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("", field.getModifiedBy());

    }

    @Test
    public void SetModifiedByTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.setModifiedBy("TestingModifiedBy");

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("TestingModifiedBy", savedField.getModifiedBy());

    }

    @Test
    public void GetModifiedByIpDoesNotExistTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("", field.getModifiedByIP());

    }

    @Test
    public void SetModifiedByIpTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.setModifiedByIP("192.168.2.2");

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("192.168.2.2", savedField.getModifiedByIP());

    }

    @Test
    public void GetClassificationMarkingDefaultTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        MarkingValueTest.assertMarkingValue(new Marking().GetClassification(),
                                            new Marking(field.getClassificationMarking()).GetClassification());

    }

    @Test
    public void GetClassificationMarkingAfterSetAndSerializedTest()
    {
        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.setClassificationMarking(TOPSECRETCLASSIFICATIONMARKING);

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        MarkingValueTest.assertMarkingValue(TOPSECRETCLASSIFICATIONMARKING.GetClassification(),
                                            new Marking(savedField.getClassificationMarking()).GetClassification());

    }

    @Test
    public void SetClassificationMarkingTopSecretTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField field = (XsdField) mission.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

        assertTrue(field != null);

        assertFalse(field.getHistory().isEmpty());

        XsdFieldHistory fh = field.getHistory().get(0);

        fh.setClassificationMarking(TOPSECRETCLASSIFICATIONMARKING);

        MarkingValueTest.assertMarkingValue(TOPSECRETCLASSIFICATIONMARKING.GetClassification(),
                                            new Marking(fh.getClassificationMarking()).GetClassification());

    }

    @Test
    public void GetValueWithMarkingTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("UNCLASSIFIED NORTHCOM Volunteer Background Checks", field.getValueWithMarking());
    }

    @Test
    public void GetPortionMarkingTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("(U)", field.getPortionMarking());
    }

    @Test
    public void GetPreviousHistoryKeyNoPreviousTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("00000000-0000-0000-0000-000000000000", field.getPreviousHistoryKey());

    }

    @Test
    public void GetPreviousHistoryKeyPreviousTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("00000000-0000-0000-0000-000000000000", field.getPreviousHistoryKey());

    }

    @Test
    public void GetFilenameDoesNotExistTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("", field.getFilename());

    }

    @Test
    public void SetFilenameTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.setFilename("c:/Program Files/java/jre7/bin");

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("c:/Program Files/java/jre7/bin", savedField.getFilename());

    }

    @Test
    public void GetExtensionDoesNotExistTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("", field.getExtension());

    }

    @Test
    public void SetExtensionTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.setExtension(".jpeg");

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("jpeg", savedField.getExtension());

    }

    @Test
    public void GetMimeTypeDoesNotExistTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("", field.getMimeType());

    }

    @Test
    public void SetMimeTypeTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.setMimeType("application/pdf");

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("application/pdf", savedField.getMimeType());

    }

    @Test
    public void GetHashDoesNotExistTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("", field.getHash());

    }

    @Test
    public void SetHashTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.setHash("8743b52063cd84097a65d1633f5c74f5");

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("8743b52063cd84097a65d1633f5c74f5", savedField.getHash());

    }

    @Test
    public void GetDateCreatedExistTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_CREATED, field.getDateCreated());

    }

    @Test
    public void SetDateCreatedTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        DateTime now = JodaDateTimeHelper.NowInUtc();

        field.setDateCreated(now);

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals(now, savedField.getDateCreated());

    }

    @Test
    public void GetLastModifiedExistTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_HISTORY_MODIFIED, field.getLastModified());

    }

    @Test
    public void SetLastModifiedTest()
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        DateTime now = JodaDateTimeHelper.NowInUtc();

        field.setLastModified(now);

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals(now, savedField.getLastModified());

    }

    @Test
    public void ToXmlTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        String fieldXml = field.toXml();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_HISTORY_XML, fieldXml.replace("\n", "").replace("\r", ""));

    }

    @Test
    public void StringTypeTest() throws DataFormatException
    {

        XsdField field = XsdFieldTest.getTestMissionNameField();

        XsdFieldHistory fh = field.getHistory().get(0);
        Object data = fh.getData();

        assertTrue(data instanceof String);
        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_HISTORY_VALUE, data);

        fh.setTypedValue("Changed");

        data = null;
        data = fh.getData();

        assertTrue(data instanceof String);
        assertEquals("Changed", data);
        assertEquals("Changed", fh.getValue());

    }

    @Test(expected = ClassCastException.class)
    public void SetTypedValueStringTypeTypeMismatchTest()
    {

        XsdField field = XsdFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);
        field.setTypedValue(JodaDateTimeHelper.NowInUtc());

        XsdFieldHistory fh = field.getHistory().get(0);

        fh.setTypedValue(UUID.randomUUID());

    }

    @Test
    public void UriTypeTest() throws DataFormatException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.create(parentRecordset, "Uri", ECoalesceFieldDataTypes.UriType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.create(parentRecord, fileFieldDef);

        Sleep();
        field.setTypedValue("uri:document/pdf");
        field.setTypedValue("uri:document/xyz");

        XsdFieldHistory fh = field.getHistory().get(0);

        assertEquals("uri:document/pdf", fh.getValue());

        fh.setTypedValue("uri:document/zip");

        Object data = fh.getData();

        assertTrue(data instanceof String);
        assertEquals("uri:document/zip", data);
        assertEquals("uri:document/zip", fh.getValue());

    }

    @Test
    public void GetDataSetTypedValueDateTimeTypeTest() throws DataFormatException
    {
        XsdField field = XsdFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        DateTime now = JodaDateTimeHelper.NowInUtc();
        field.setTypedValue(now);

        XsdFieldHistory fh = field.getHistory().get(0);

        fh.setTypedValue(now.plusDays(1));

        Object data = fh.getData();

        assertTrue(data instanceof DateTime);
        assertEquals(now.plusDays(1), data);
        assertEquals(now.plusDays(1), fh.getDateTimeValue());

    }

    @Test(expected = ClassCastException.class)
    public void SetTypedValueDateTimeTypeTypeMismatchTest()
    {

        XsdField field = XsdFieldTest.getTestMissionNameField();

        DateTime now = JodaDateTimeHelper.NowInUtc();
        field.setTypedValue(now);

        XsdFieldHistory fh = field.getHistory().get(0);

        fh.setTypedValue(now);

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
        XsdField field = XsdField.create(parentRecord, fileFieldDef);

        Sleep();
        field.setTypedValue(true);
        field.setTypedValue(false);

        XsdFieldHistory fh = field.getHistory().get(0);

        Object data = fh.getData();

        assertEquals(true, data);
        assertEquals("true", fh.getValue().toLowerCase());
        assertEquals(true, fh.getBooleanValue());

        fh.setTypedValue(false);

        data = null;
        data = fh.getData();

        assertTrue(data instanceof Boolean);
        assertEquals(false, data);
        assertEquals("false", fh.getValue().toLowerCase());
        assertEquals(false, fh.getBooleanValue());

    }

    @Test(expected = ClassCastException.class)
    public void SetTypedValueBooleanTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        XsdField field = XsdFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(JodaDateTimeHelper.NowInUtc());

        XsdFieldHistory fh = field.getHistory().get(0);
        fh.setTypedValue(true);

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
        XsdField field = XsdField.create(parentRecord, fileFieldDef);

        Sleep();
        field.setTypedValue(1111);
        field.setTypedValue(2222);

        XsdFieldHistory fh = field.getHistory().get(0);

        Object data = fh.getData();

        assertTrue(data instanceof Integer);
        assertEquals("1111", fh.getValue());
        assertEquals(1111, fh.getIntegerValue());
        assertEquals(1111, data);

        fh.setTypedValue(3333);

        data = null;
        data = fh.getData();

        assertTrue(data instanceof Integer);
        assertEquals(3333, data);
        assertEquals("3333", fh.getValue());
        assertEquals(3333, fh.getIntegerValue());

    }

    @Test(expected = ClassCastException.class)
    public void SetTypedValueIntgerTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        XsdField field = XsdFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(JodaDateTimeHelper.NowInUtc());

        XsdFieldHistory fh = field.getHistory().get(0);
        fh.setTypedValue(1111);

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
        XsdField field = XsdField.create(parentRecord, fileFieldDef);

        Sleep();
        UUID guid = UUID.randomUUID();
        field.setTypedValue(guid);
        field.setTypedValue(UUID.randomUUID());

        XsdFieldHistory fh = field.getHistory().get(0);

        Object data = fh.getData();

        assertTrue(data instanceof UUID);
        assertEquals(guid, data);
        assertEquals(GUIDHelper.GetGuidString(guid), fh.getValue());
        assertEquals(guid, fh.getGuidValue());

        UUID newGuid = UUID.randomUUID();
        fh.setTypedValue(newGuid);

        data = null;
        data = fh.getData();

        assertTrue(data instanceof UUID);
        assertEquals(newGuid, data);
        assertEquals(GUIDHelper.GetGuidString(newGuid), fh.getValue());
        assertEquals(newGuid, fh.getGuidValue());

    }

    @Test(expected = ClassCastException.class)
    public void SetTypedValueGUIDTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        XsdField field = XsdFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(JodaDateTimeHelper.NowInUtc());

        XsdFieldHistory fh = field.getHistory().get(0);
        fh.setTypedValue(UUID.randomUUID());

    }

    // -----------------------------------------------------------------------//
    // Private Static Methods
    // -----------------------------------------------------------------------//

    private static XsdFieldHistory GetTestMissionNameFieldHistory(String entityXml)
    {

        XsdEntity entity = XsdEntity.create(entityXml);

        return GetTestMissionNameFieldHistory(entity);

    }

    private static XsdFieldHistory GetTestMissionNameFieldHistory(XsdEntity entity)
    {

        return GetTestMissionFieldHistoryByName(entity, CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

    }

    private static XsdFieldHistory GetTestMissionNameFieldHistory()
    {

        return GetTestMissionNameFieldHistory(CoalesceTypeInstances.TEST_MISSION);

    }

    private static XsdFieldHistory GetTestMissionFieldHistoryByName(String fieldPath)
    {

        XsdEntity mission = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        return GetTestMissionFieldHistoryByName(mission, fieldPath);

    }

    private static XsdFieldHistory GetTestMissionFieldHistoryByName(XsdEntity entity, String fieldPath)
    {

        XsdDataObject fdo = entity.getDataObjectForNamePath(fieldPath);

        assertTrue(fdo instanceof XsdField);

        XsdField field = (XsdField) fdo;

        if (field.getHistory().isEmpty())
        {
            return null;
        }
        else
        {
            return field.getHistory().get(0);
        }

    }

    private static XsdFieldHistory GetSavedTestMissionFieldHistory(XsdEntity entity)
    {

        String serializedMission = entity.toXml();

        return GetTestMissionNameFieldHistory(serializedMission);

    }

    private void Sleep()
    {

        try
        {
            Thread.sleep(2);
        }
        catch (InterruptedException e)
        {
        }
    }
}
