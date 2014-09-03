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
        fh1.Initialize(null, new Fieldhistory());

        @SuppressWarnings("unused")
        XsdFieldHistory fh2 = XsdFieldHistory.Create((XsdFieldBase) fh1);

    }

    @Test
    public void ConstructorXsdFieldBaseXsdFieldTest()
    {

        XsdField field = new XsdField();
        field.Initialize(null, new Field());

        @SuppressWarnings("unused")
        XsdFieldHistory fh = XsdFieldHistory.Create((XsdFieldBase) field);

    }

    @Test(expected = ClassCastException.class)
    public void ConstructorXsdFieldHistoryNullTest()
    {

        XsdFieldHistory fh1 = new XsdFieldHistory();
        fh1.Initialize(null, new Fieldhistory());

        @SuppressWarnings("unused")
        XsdFieldHistory fh2 = XsdFieldHistory.Create(fh1);

    }

    @Test
    public void ConstructorXsdFieldHistoryTest()
    {
        XsdEntity entity = new XsdEntity();
        entity.Initialize();

        XsdSection section = new XsdSection();
        section.Initialize(entity, new Section());

        XsdRecordset rs = new XsdRecordset();
        rs.Initialize(section, new Recordset());

        XsdRecord record = new XsdRecord();
        record.Initialize(rs, new Record());

        XsdField field = new XsdField();
        field.Initialize(record, new Field());

        @SuppressWarnings("unused")
        XsdFieldHistory fh = XsdFieldHistory.Create(field);

    }

    @Test
    public void ConstructorNoPreviousHistory()
    {
        XsdField field = XsdFieldTest.GetTestMissionFieldByName(CoalesceTypeInstances.TESTMISSIONBASE64PATH);

        String fieldKey = field.GetKey();

        XsdFieldHistory fh = XsdFieldHistory.Create(field);

        assertEquals(fieldKey, field.GetKey());
        assertFieldHistory(field, fh);

    }

    private void assertFieldHistory(XsdField field, XsdFieldHistory fieldHistory)
    {

        assertEquals(field.GetName(), fieldHistory.GetName());
        assertEquals("fieldhistory", fieldHistory.getType());
        assertEquals(field.GetValue(), fieldHistory.GetValue());
        assertEquals(field.GetDataType(), fieldHistory.GetDataType());
        assertEquals(field.GetLabel(), fieldHistory.GetLabel());
        assertEquals(field.GetSize(), fieldHistory.GetSize());
        assertEquals(field.GetModifiedBy(), fieldHistory.GetModifiedBy());
        assertEquals(field.GetModifiedByIP(), fieldHistory.GetModifiedByIP());
        assertEquals(field.GetClassificationMarking(), fieldHistory.GetClassificationMarking());
        assertEquals(field.GetFilename(), fieldHistory.GetFilename());
        assertEquals(field.GetExtension(), fieldHistory.GetExtension());
        assertEquals(field.GetMimeType(), fieldHistory.GetMimeType());
        assertEquals(field.GetHash(), fieldHistory.GetHash());
        assertEquals(field.GetDateCreated(), fieldHistory.GetDateCreated());
        assertEquals(field.GetLastModified(), fieldHistory.GetLastModified());
        assertEquals(field.GetStatus(), fieldHistory.GetStatus());

        Map<QName, String> otherAttributes = fieldHistory.getAttributes();
        for (Map.Entry<QName, String> otherAttr : field.getAttributes().entrySet())
        {
            assertTrue(otherAttributes.containsKey(otherAttr.getKey()));
            assertEquals(otherAttributes.get(otherAttr.getKey()), otherAttr.getValue());
        }
    }

    @Test
    public void ConstructorPreviousHistory()
    {

        XsdField field = XsdFieldTest.GetTestMissionFieldByName(CoalesceTypeInstances.TESTMISSIONBASE64PATH);

        field.SetSuspendHistory(true);
        XsdFieldHistory fh = XsdFieldHistory.Create(field);
        assertFieldHistory(field, fh);

        field.SetTypedValue(2222);
        field.SetSuspendHistory(false);

        assertEquals(2222, field.GetIntegerValue());
        assertEquals(1, field.GetHistory().size());
        assertEquals(CoalesceTypeInstances.TESTMISSIONBASE64VALUE, field.GetHistory().get(0).GetIntegerValue());
        assertEquals(fh, field.GetHistory().get(0));

    }

    @Test
    public void GetKeyTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals(CoalesceTypeInstances.TESTMISSIONNAMEKEY, field.GetKey());

    }

    @Test
    public void SetKeyTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        UUID newGuid = UUID.randomUUID();
        field.SetKey(newGuid.toString());

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals(savedField.GetKey().toUpperCase(), newGuid.toString().toUpperCase());

    }

    @Test
    public void GetNameTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals(CoalesceTypeInstances.TESTMISSIONNAMENAME, field.GetName());

    }

    @Test
    public void SetNameTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.SetName("Testingname");

        String serializedMission = mission.ToXml();
        XsdEntity savedMission = XsdEntity.Create(serializedMission);

        XsdFieldHistory savedField = GetTestMissionNameFieldHistory(savedMission);

        assertEquals("Testingname", savedField.GetName());

    }

    @Test
    public void GetValueTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals(CoalesceTypeInstances.TESTMISSIONNAMEHISTORYVALUE, field.GetValue());

    }

    @Test
    public void SetValueTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.SetValue("Testingvalue");

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("Testingvalue", savedField.GetValue());

    }

    @Test
    public void GetDataType()
    {
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdFieldHistory stringField = GetTestMissionFieldHistoryByName(mission, CoalesceTypeInstances.TESTMISSIONNAMEPATH);
        assertEquals("string", stringField.GetDataType());

        XsdFieldHistory dateField = GetTestMissionFieldHistoryByName(mission,
                                                                     CoalesceTypeInstances.TESTMISSIONINCIDENTDATETIMEPATH);
        assertEquals("datetime", dateField.GetDataType());

    }

    @Test
    public void SetDateType()
    {
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdFieldHistory stringField = GetTestMissionFieldHistoryByName(mission, CoalesceTypeInstances.TESTMISSIONNAMEPATH);
        stringField.SetDataType("datetime");

        XsdFieldHistory dateField = GetTestMissionFieldHistoryByName(mission,
                                                                     CoalesceTypeInstances.TESTMISSIONINCIDENTDATETIMEPATH);
        dateField.SetDataType("integer");

        String serializedMission = mission.ToXml();
        XsdEntity savedMission = XsdEntity.Create(serializedMission);

        XsdFieldHistory savedStringField = GetTestMissionFieldHistoryByName(savedMission,
                                                                            CoalesceTypeInstances.TESTMISSIONNAMEPATH);
        assertEquals("datetime", savedStringField.GetDataType());

        XsdFieldHistory savedDateField = GetTestMissionFieldHistoryByName(savedMission,
                                                                          CoalesceTypeInstances.TESTMISSIONINCIDENTDATETIMEPATH);
        assertEquals("integer", savedDateField.GetDataType());

    }

    @Test
    public void GetLabelTest()
    {

        XsdFieldHistory field = GetTestMissionFieldHistoryByName(CoalesceTypeInstances.TESTMISSIONACTIONNUMBERPATH);

        assertEquals(CoalesceTypeInstances.TESTMISSIONACTIONNUMBERLABELHISTORY, field.GetLabel());

    }

    @Test
    public void GetLabelDoesNotExistTest()
    {

        XsdFieldHistory field = GetTestMissionFieldHistoryByName(CoalesceTypeInstances.TESTMISSIONNAMEPATH);

        assertEquals("", field.GetLabel());

    }

    @Test
    public void SetLabelTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.SetLabel("Testinglabel");

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("Testinglabel", savedField.GetLabel());

    }

    @Test
    public void SetLabelNullTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.SetLabel(null);

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("", savedField.GetLabel());

    }

    @Test
    public void GetSizeDoesNotExistTest()
    {
        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals(0, field.GetSize());

    }

    @Test
    public void SetSizeTest()
    {
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);
        field.SetSize(128);

        String serializedMission = mission.ToXml();
        XsdEntity savedMission = XsdEntity.Create(serializedMission);

        XsdFieldHistory savedField = GetTestMissionNameFieldHistory(savedMission);
        assertEquals(128, savedField.GetSize());

    }

    @Test
    public void GetModifiedByDoesNotExistTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("", field.GetModifiedBy());

    }

    @Test
    public void SetModifiedByTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.SetModifiedBy("TestingModifiedBy");

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("TestingModifiedBy", savedField.GetModifiedBy());

    }

    @Test
    public void GetModifiedByIpDoesNotExistTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("", field.GetModifiedByIP());

    }

    @Test
    public void SetModifiedByIpTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.SetModifiedByIP("192.168.2.2");

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("192.168.2.2", savedField.GetModifiedByIP());

    }

    @Test
    public void GetClassificationMarkingDefaultTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        MarkingValueTest.assertMarkingValue(new Marking().GetClassification(),
                                            new Marking(field.GetClassificationMarking()).GetClassification());

    }

    @Test
    public void GetClassificationMarkingAfterSetAndSerializedTest()
    {
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.SetClassificationMarking(TOPSECRETCLASSIFICATIONMARKING);

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        MarkingValueTest.assertMarkingValue(TOPSECRETCLASSIFICATIONMARKING.GetClassification(),
                                            new Marking(savedField.GetClassificationMarking()).GetClassification());

    }

    @Test
    public void SetClassificationMarkingTopSecretTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = (XsdField) mission.GetDataObjectForNamePath(CoalesceTypeInstances.TESTMISSIONNAMEPATH);

        assertTrue(field != null);

        assertFalse(field.GetHistory().isEmpty());

        XsdFieldHistory fh = field.GetHistory().get(0);

        fh.SetClassificationMarking(TOPSECRETCLASSIFICATIONMARKING);

        MarkingValueTest.assertMarkingValue(TOPSECRETCLASSIFICATIONMARKING.GetClassification(),
                                            new Marking(fh.GetClassificationMarking()).GetClassification());

    }

    @Test
    public void GetValueWithMarkingTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("UNCLASSIFIED NORTHCOM Volunteer Background Checks", field.GetValueWithMarking());
    }

    @Test
    public void GetPortionMarkingTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("(U)", field.GetPortionMarking());
    }

    @Test
    public void GetPreviousHistoryKeyNoPreviousTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("00000000-0000-0000-0000-000000000000", field.GetPreviousHistoryKey());

    }

    @Test
    public void GetPreviousHistoryKeyPreviousTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("00000000-0000-0000-0000-000000000000", field.GetPreviousHistoryKey());

    }

    @Test
    public void GetFilenameDoesNotExistTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("", field.GetFilename());

    }

    @Test
    public void SetFilenameTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.SetFilename("c:/Program Files/java/jre7/bin");

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("c:/Program Files/java/jre7/bin", savedField.GetFilename());

    }

    @Test
    public void GetExtensionDoesNotExistTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("", field.GetExtension());

    }

    @Test
    public void SetExtensionTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.SetExtension(".jpeg");

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("jpeg", savedField.GetExtension());

    }

    @Test
    public void GetMimeTypeDoesNotExistTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("", field.GetMimeType());

    }

    @Test
    public void SetMimeTypeTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.SetMimeType("application/pdf");

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("application/pdf", savedField.GetMimeType());

    }

    @Test
    public void GetHashDoesNotExistTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("", field.GetHash());

    }

    @Test
    public void SetHashTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.SetHash("8743b52063cd84097a65d1633f5c74f5");

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("8743b52063cd84097a65d1633f5c74f5", savedField.GetHash());

    }

    @Test
    public void GetDateCreatedExistTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals(CoalesceTypeInstances.TESTMISSIONNAMECREATED, field.GetDateCreated());

    }

    @Test
    public void SetDateCreatedTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        DateTime now = JodaDateTimeHelper.NowInUtc();

        field.SetDateCreated(now);

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals(now, savedField.GetDateCreated());

    }

    @Test
    public void GetLastModifiedExistTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals(CoalesceTypeInstances.TESTMISSIONNAMEHISTORYMODIFIED, field.GetLastModified());

    }

    @Test
    public void SetLastModifiedTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdFieldHistory field = GetTestMissionNameFieldHistory(mission);

        DateTime now = JodaDateTimeHelper.NowInUtc();

        field.SetLastModified(now);

        XsdFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals(now, savedField.GetLastModified());

    }

    @Test
    public void ToXmlTest()
    {

        XsdFieldHistory field = GetTestMissionNameFieldHistory();

        String fieldXml = field.ToXml();

        assertEquals(CoalesceTypeInstances.TESTMISSIONNAMEHISTORYXML, fieldXml.replace("\n", "").replace("\r", ""));

    }

    @Test
    public void StringTypeTest()
    {

        XsdField field = XsdFieldTest.GetTestMissionNameField();

        XsdFieldHistory fh = field.GetHistory().get(0);
        Object data = fh.GetData();

        assertTrue(data instanceof String);
        assertEquals(CoalesceTypeInstances.TESTMISSIONNAMEHISTORYVALUE, data);

        fh.SetTypedValue("Changed");

        data = null;
        data = fh.GetData();

        assertTrue(data instanceof String);
        assertEquals("Changed", data);
        assertEquals("Changed", fh.GetValue());

    }

    @Test(expected = ClassCastException.class)
    public void SetTypedValueStringTypeTypeMismatchTest()
    {

        XsdField field = XsdFieldTest.GetTestMissionFieldByName(CoalesceTypeInstances.TESTMISSIONSTARTTIMEPATH);
        field.SetTypedValue(JodaDateTimeHelper.NowInUtc());

        XsdFieldHistory fh = field.GetHistory().get(0);

        fh.SetTypedValue(UUID.randomUUID());

    }

    @Test
    public void UriTypeTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdRecordset parentRecordset = (XsdRecordset) entity.GetDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        XsdFieldDefinition fileFieldDef = XsdFieldDefinition.Create(parentRecordset, "Uri", ECoalesceFieldDataTypes.UriType);

        XsdRecord parentRecord = parentRecordset.GetItem(0);
        XsdField field = XsdField.Create(parentRecord, fileFieldDef);

        Sleep();
        field.SetTypedValue("uri:document/pdf");
        field.SetTypedValue("uri:document/xyz");

        XsdFieldHistory fh = field.GetHistory().get(0);

        assertEquals("uri:document/pdf", fh.GetValue());

        fh.SetTypedValue("uri:document/zip");

        Object data = fh.GetData();

        assertTrue(data instanceof String);
        assertEquals("uri:document/zip", data);
        assertEquals("uri:document/zip", fh.GetValue());

    }

    @Test
    public void GetDataSetTypedValueDateTimeTypeTest()
    {
        XsdField field = XsdFieldTest.GetTestMissionFieldByName(CoalesceTypeInstances.TESTMISSIONSTARTTIMEPATH);

        DateTime now = JodaDateTimeHelper.NowInUtc();
        field.SetTypedValue(now);

        XsdFieldHistory fh = field.GetHistory().get(0);

        fh.SetTypedValue(now.plusDays(1));

        Object data = fh.GetData();

        assertTrue(data instanceof DateTime);
        assertEquals(now.plusDays(1), data);
        assertEquals(now.plusDays(1), fh.GetDateTimeValue());

    }

    @Test(expected = ClassCastException.class)
    public void SetTypedValueDateTimeTypeTypeMismatchTest()
    {

        XsdField field = XsdFieldTest.GetTestMissionNameField();
        
        DateTime now = JodaDateTimeHelper.NowInUtc();
        field.SetTypedValue(now);

        XsdFieldHistory fh = field.GetHistory().get(0);

        fh.SetTypedValue(now);

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
        
        Sleep();
        field.SetTypedValue(true);
        field.SetTypedValue(false);

        XsdFieldHistory fh = field.GetHistory().get(0);

        Object data = fh.GetData();
        
        assertEquals(true, data);
        assertEquals("true", fh.GetValue().toLowerCase());
        assertEquals(true, fh.GetBooleanValue());
        
        fh.SetTypedValue(false);

        data = null;
        data = fh.GetData();

        assertTrue(data instanceof Boolean);
        assertEquals(false, data);
        assertEquals("false", fh.GetValue().toLowerCase());
        assertEquals(false, fh.GetBooleanValue());

    }

    @Test(expected = ClassCastException.class)
    public void SetTypedValueBooleanTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        XsdField field = XsdFieldTest.GetTestMissionFieldByName(CoalesceTypeInstances.TESTMISSIONSTARTTIMEPATH);

        field.SetTypedValue(JodaDateTimeHelper.NowInUtc());
        
        XsdFieldHistory fh = field.GetHistory().get(0);
        fh.SetTypedValue(true);

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

        Sleep();
        field.SetTypedValue(1111);
        field.SetTypedValue(2222);
        
        XsdFieldHistory fh = field.GetHistory().get(0);

        Object data = fh.GetData();

        assertTrue(data instanceof Integer);
        assertEquals("1111", fh.GetValue());
        assertEquals(1111, fh.GetIntegerValue());
        assertEquals(1111, data);
        
        fh.SetTypedValue(3333);

        data = null;
        data = fh.GetData();

        assertTrue(data instanceof Integer);
        assertEquals(3333, data);
        assertEquals("3333", fh.GetValue());
        assertEquals(3333, fh.GetIntegerValue());

    }

    @Test(expected = ClassCastException.class)
    public void SetTypedValueIntgerTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        XsdField field = XsdFieldTest.GetTestMissionFieldByName(CoalesceTypeInstances.TESTMISSIONSTARTTIMEPATH);

        field.SetTypedValue(JodaDateTimeHelper.NowInUtc());
        
        XsdFieldHistory fh = field.GetHistory().get(0);
        fh.SetTypedValue(1111);

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

        Sleep();
        UUID guid = UUID.randomUUID();
        field.SetTypedValue(guid);
        field.SetTypedValue(UUID.randomUUID());
        
        XsdFieldHistory fh = field.GetHistory().get(0);

        Object data = fh.GetData();

        assertTrue(data instanceof UUID);
        assertEquals(guid, data);
        assertEquals(GUIDHelper.GetGuidString(guid), fh.GetValue());
        assertEquals(guid, fh.GetGuidValue());

        UUID newGuid = UUID.randomUUID();
        fh.SetTypedValue(newGuid);

        data = null;
        data = fh.GetData();

        assertTrue(data instanceof UUID);
        assertEquals(newGuid, data);
        assertEquals(GUIDHelper.GetGuidString(newGuid), fh.GetValue());
        assertEquals(newGuid, fh.GetGuidValue());

    }

    @Test(expected = ClassCastException.class)
    public void SetTypedValueGUIDTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        XsdField field = XsdFieldTest.GetTestMissionFieldByName(CoalesceTypeInstances.TESTMISSIONSTARTTIMEPATH);

        field.SetTypedValue(JodaDateTimeHelper.NowInUtc());
        
        XsdFieldHistory fh = field.GetHistory().get(0);
        fh.SetTypedValue(UUID.randomUUID());

    }

    // -----------------------------------------------------------------------//
    // Private Static Methods
    // -----------------------------------------------------------------------//

    private static XsdFieldHistory GetTestMissionNameFieldHistory(String entityXml)
    {

        XsdEntity entity = XsdEntity.Create(entityXml);

        return GetTestMissionNameFieldHistory(entity);

    }

    private static XsdFieldHistory GetTestMissionNameFieldHistory(XsdEntity entity)
    {

        return GetTestMissionFieldHistoryByName(entity, CoalesceTypeInstances.TESTMISSIONNAMEPATH);

    }

    private static XsdFieldHistory GetTestMissionNameFieldHistory()
    {

        return GetTestMissionNameFieldHistory(CoalesceTypeInstances.TESTMISSION);

    }

    private static XsdFieldHistory GetTestMissionFieldHistoryByName(String fieldPath)
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        return GetTestMissionFieldHistoryByName(mission, fieldPath);

    }

    private static XsdFieldHistory GetTestMissionFieldHistoryByName(XsdEntity entity, String fieldPath)
    {

        XsdDataObject fdo = entity.GetDataObjectForNamePath(fieldPath);

        assertTrue(fdo instanceof XsdField);

        XsdField field = (XsdField) fdo;

        if (field.GetHistory().isEmpty())
        {
            return null;
        }
        else
        {
            return field.GetHistory().get(0);
        }

    }

    private static XsdFieldHistory GetSavedTestMissionFieldHistory(XsdEntity entity)
    {

        String serializedMission = entity.ToXml();

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
