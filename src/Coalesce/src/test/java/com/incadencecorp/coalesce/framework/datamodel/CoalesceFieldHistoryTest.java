package com.incadencecorp.coalesce.framework.datamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.junit.Test;

import com.incadencecorp.coalesce.common.CoalesceTypeInstances;
import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.classification.MarkingValueTest;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.helpers.GUIDHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;
import com.incadencecorp.coalesce.framework.generatedjaxb.Field;
import com.incadencecorp.coalesce.framework.generatedjaxb.Fieldhistory;
import com.incadencecorp.coalesce.framework.generatedjaxb.Record;
import com.incadencecorp.coalesce.framework.generatedjaxb.Recordset;
import com.incadencecorp.coalesce.framework.generatedjaxb.Section;

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

public class CoalesceFieldHistoryTest {

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

    @Test
    public void ConstructorXsdFieldBaseXsdFieldTest()
    {

        CoalesceStringField field = new CoalesceStringField();
        field.initialize(null, new Field());

        @SuppressWarnings("unused")
        CoalesceFieldHistory fh = CoalesceFieldHistory.create(field);

    }

    @Test
    public void ConstructorXsdFieldHistoryTest()
    {
        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();

        CoalesceSection section = new CoalesceSection();
        section.initialize(entity, new Section());

        CoalesceRecordset rs = new CoalesceRecordset();
        rs.initialize(section, new Recordset());

        CoalesceRecord record = new CoalesceRecord();
        record.initialize(rs, new Record());

        CoalesceField<?> field = new CoalesceStringField();
        field.initialize(record, new Field());

        @SuppressWarnings("unused")
        CoalesceFieldHistory fh = CoalesceFieldHistory.create(field);

    }

    // TODO: replace these unit test with functions that don't require Coalesce.Objects
    
    /*
    @Test
    public void addFieldHistoryWithDefaultValue()
    {
        // Create New Action
        PhotoGalleryEntity entity = new PhotoGalleryEntity();
        entity.initialize();

        // Change Value (Current Status has a Default Value)
        entity.setCurrentStatus(EActionStatuses.CollectionPending);

        // Verify
        assertTrue(entity.getCurrentStatusHistory().size() == 1);

        // Change Value
        entity.setCurrentStatus(EActionStatuses.ExploitationPending);

        // Verify
        assertEquals(entity.getCurrentStatusHistory().get(0).getValue(), EActionStatuses.CollectionPending.getLabel());
        assertTrue(entity.getCurrentStatusHistory().size() == 2);

        // Create New Action from entity
        PhotoGalleryEntity entity2 = new PhotoGalleryEntity();
        entity2.initialize(entity.toXml());

        // Change Value (Same Value no History Should be Created)
        entity2.setCurrentStatus(EActionStatuses.ExploitationPending);

        // Verify
        assertTrue(entity2.getCurrentStatusHistory().size() == 2);

        // Change Value (History Should be Created)
        entity2.setCurrentStatus(EActionStatuses.CollectionComplete);

        // Verify
        assertTrue(entity2.getCurrentStatusHistory().size() == 3);
        assertEquals(entity2.getCurrentStatusHistory().get(0).getValue(), EActionStatuses.ExploitationPending.getLabel());
    }

    @Test
    public void addFieldHistory()
    {
        // Create New Mission
        MissionEntity entity = new MissionEntity();
        entity.initialize();

        // Change Value (History Should not be Created)
        entity.getMissionName().setValue("No history should be added");

        // Verify
        assertTrue(entity.getMissionName().getHistory().size() == 0);

        // Change Value (History Should be Created)
        entity.getMissionName().setValue("History should be added");

        // Verify
        assertTrue(entity.getMissionName().getHistory().size() == 1);
        assertTrue(entity.getMissionName().getHistory().get(0).getValue() == "No history should be added");
        assertTrue(entity.getMissionName().getPreviousHistoryKey() == entity.getMissionName().getHistory().get(0).getKey());
        assertTrue(entity.getMissionName().getHistory().get(0).getPreviousHistoryKey() == "00000000-0000-0000-0000-000000000000");
    }
*/
    @Test
    public void ConstructorNoPreviousHistory()
    {
        CoalesceField<?> field = CoalesceFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        String fieldKey = field.getKey();

        CoalesceFieldHistory fh = CoalesceFieldHistory.create(field);

        assertEquals(fieldKey, field.getKey());
        assertFieldHistory(field, fh);

    }

    private void assertFieldHistory(CoalesceField<?> field, CoalesceFieldHistory fieldHistory)
    {

        assertEquals(field.getName(), fieldHistory.getName());
        assertEquals("fieldhistory", fieldHistory.getType());
        assertEquals(field.getBaseValue(), fieldHistory.getBaseValue());
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

        Map<QName, String> otherAttributes = fieldHistory.getOtherAttributes();
        for (Map.Entry<QName, String> otherAttr : field.getOtherAttributes().entrySet())
        {
            assertTrue(otherAttributes.containsKey(otherAttr.getKey()));
            assertEquals(otherAttributes.get(otherAttr.getKey()), otherAttr.getValue());
        }
    }

    @Test
    public void ConstructorPreviousHistory() throws ClassCastException, CoalesceDataFormatException
    {

        CoalesceField<?> field = CoalesceFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        field.setSuspendHistory(true);
        CoalesceFieldHistory fh = CoalesceFieldHistory.create(field);
        assertFieldHistory(field, fh);

        field.setTypedValue(2222);
        field.setSuspendHistory(false);

        assertEquals(2222, field.getIntegerValue().intValue());
        assertEquals(1, field.getHistory().size());
        assertEquals(CoalesceTypeInstances.TEST_MISSION_BASE64_VALUE, field.getHistory().get(0).getIntegerValue().intValue());
        assertEquals(fh, field.getHistory().get(0));

    }

    @Test
    public void GetKeyTest()
    {

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_KEY, field.getKey());

    }

    @Test
    public void SetKeyTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory(mission);

        UUID newGuid = UUID.randomUUID();
        field.setKey(newGuid.toString());

        CoalesceFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals(savedField.getKey().toUpperCase(), newGuid.toString().toUpperCase());

    }

    @Test
    public void GetNameTest()
    {

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_NAME, field.getName());

    }

    @Test
    public void SetNameTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.setName("Testingname");

        String serializedMission = mission.toXml();
        CoalesceEntity savedMission = CoalesceEntity.create(serializedMission);

        CoalesceFieldHistory savedField = GetTestMissionNameFieldHistory(savedMission);

        assertEquals("Testingname", savedField.getName());

    }

    @Test
    public void GetValueTest()
    {

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_HISTORY_VALUE, field.getBaseValue());

    }

    @Test
    public void SetValueTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.setBaseValue("Testingvalue");

        CoalesceFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("Testingvalue", savedField.getBaseValue());

    }

    @Test
    public void GetDataType()
    {
        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory stringField = GetTestMissionFieldHistoryByName(mission,
                                                                            CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
        assertEquals(ECoalesceFieldDataTypes.STRING_TYPE, stringField.getDataType());

        CoalesceFieldHistory dateField = GetTestMissionFieldHistoryByName(mission,
                                                                          CoalesceTypeInstances.TEST_MISSION_INCIDENT_DATE_TIME_PATH);
        assertEquals(ECoalesceFieldDataTypes.DATE_TIME_TYPE, dateField.getDataType());

    }

    @Test
    public void SetDateType()
    {
        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory stringField = GetTestMissionFieldHistoryByName(mission,
                                                                            CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
        stringField.setDataType(ECoalesceFieldDataTypes.DATE_TIME_TYPE);

        CoalesceFieldHistory dateField = GetTestMissionFieldHistoryByName(mission,
                                                                          CoalesceTypeInstances.TEST_MISSION_INCIDENT_DATE_TIME_PATH);
        dateField.setDataType(ECoalesceFieldDataTypes.INTEGER_TYPE);

        String serializedMission = mission.toXml();
        CoalesceEntity savedMission = CoalesceEntity.create(serializedMission);

        CoalesceFieldHistory savedStringField = GetTestMissionFieldHistoryByName(savedMission,
                                                                                 CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
        assertEquals(ECoalesceFieldDataTypes.DATE_TIME_TYPE, savedStringField.getDataType());

        CoalesceFieldHistory savedDateField = GetTestMissionFieldHistoryByName(savedMission,
                                                                               CoalesceTypeInstances.TEST_MISSION_INCIDENT_DATE_TIME_PATH);
        assertEquals(ECoalesceFieldDataTypes.INTEGER_TYPE, savedDateField.getDataType());

    }

    @Test
    public void GetLabelTest()
    {

        CoalesceFieldHistory field = GetTestMissionFieldHistoryByName(CoalesceTypeInstances.TEST_MISSION_ACTION_NUMBER_PATH);

        assertEquals(CoalesceTypeInstances.TEST_MISSION_ACTION_NUMBER_LABEL_HISTORY, field.getLabel());

    }

    @Test
    public void GetLabelDoesNotExistTest()
    {

        CoalesceFieldHistory field = GetTestMissionFieldHistoryByName(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

        assertEquals("", field.getLabel());

    }

    @Test
    public void SetLabelTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.setLabel("Testinglabel");

        CoalesceFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("Testinglabel", savedField.getLabel());

    }

    @Test
    public void SetLabelNullTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.setLabel(null);

        CoalesceFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("", savedField.getLabel());

    }

    @Test
    public void GetSizeDoesNotExistTest()
    {
        CoalesceFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals(0, field.getSize());

    }

    @Test
    public void SetSizeTest()
    {
        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory(mission);
        field.setSize(128);

        String serializedMission = mission.toXml();
        CoalesceEntity savedMission = CoalesceEntity.create(serializedMission);

        CoalesceFieldHistory savedField = GetTestMissionNameFieldHistory(savedMission);
        assertEquals(128, savedField.getSize());

    }

    @Test
    public void GetModifiedByDoesNotExistTest()
    {

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("", field.getModifiedBy());

    }

    @Test
    public void SetModifiedByTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.setModifiedBy("TestingModifiedBy");

        CoalesceFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("TestingModifiedBy", savedField.getModifiedBy());

    }

    @Test
    public void GetModifiedByIpDoesNotExistTest()
    {

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("", field.getModifiedByIP());

    }

    @Test
    public void SetModifiedByIpTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.setModifiedByIP("192.168.2.2");

        CoalesceFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("192.168.2.2", savedField.getModifiedByIP());

    }

    @Test
    public void GetClassificationMarkingDefaultTest()
    {

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory();

        MarkingValueTest.assertMarkingValue(new Marking().getClassification(),
                                            field.getClassificationMarking().getClassification());

    }

    @Test
    public void GetClassificationMarkingAfterSetAndSerializedTest()
    {
        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.setClassificationMarking(TOPSECRETCLASSIFICATIONMARKING);

        CoalesceFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        MarkingValueTest.assertMarkingValue(TOPSECRETCLASSIFICATIONMARKING.getClassification(),
                                            savedField.getClassificationMarking().getClassification());

    }

    @Test
    public void SetClassificationMarkingTopSecretTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) mission.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

        assertTrue(field != null);

        assertFalse(field.getHistory().isEmpty());

        CoalesceFieldHistory fh = field.getHistory().get(0);

        fh.setClassificationMarking(TOPSECRETCLASSIFICATIONMARKING);

        MarkingValueTest.assertMarkingValue(TOPSECRETCLASSIFICATIONMARKING.getClassification(),
                                            fh.getClassificationMarking().getClassification());

    }

    @Test
    public void GetValueWithMarkingTest()
    {

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("UNCLASSIFIED NORTHCOM Volunteer Background Checks", field.getValueWithMarking());
    }

    @Test
    public void GetPortionMarkingTest()
    {

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("(U)", field.getPortionMarking());
    }

    @Test
    public void GetPreviousHistoryKeyNoPreviousTest()
    {

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("00000000-0000-0000-0000-000000000000", field.getPreviousHistoryKey());

        field.setPreviousHistoryKey("");
        
        assertEquals("00000000-0000-0000-0000-000000000000", field.getPreviousHistoryKey());

    }

    @Test
    public void GetPreviousHistoryKeyPreviousTest()
    {

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("00000000-0000-0000-0000-000000000000", field.getPreviousHistoryKey());

    }

    @Test
    public void GetFilenameDoesNotExistTest()
    {

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("", field.getFilename());

    }

    @Test
    public void SetFilenameTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.setFilename("c:/Program Files/java/jre7/bin");

        CoalesceFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("c:/Program Files/java/jre7/bin", savedField.getFilename());

    }

    @Test
    public void GetExtensionDoesNotExistTest()
    {

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("", field.getExtension());

    }

    @Test
    public void SetExtensionTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.setExtension(".jpeg");

        CoalesceFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("jpeg", savedField.getExtension());

    }

    @Test
    public void GetMimeTypeDoesNotExistTest()
    {

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("", field.getMimeType());

    }

    @Test
    public void SetMimeTypeTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.setMimeType("application/pdf");

        CoalesceFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("application/pdf", savedField.getMimeType());

    }

    @Test
    public void GetHashDoesNotExistTest()
    {

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals("", field.getHash());

    }

    @Test
    public void SetHashTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory(mission);

        field.setHash("8743b52063cd84097a65d1633f5c74f5");

        CoalesceFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals("8743b52063cd84097a65d1633f5c74f5", savedField.getHash());

    }

    @Test
    public void inputLangTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceFieldHistory fh = ((CoalesceStringField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/ActionNumber")).getHistoryRecord("00BB7A9F-4F37-46E9-85EB-9280ED3619CC");

        assertEquals(null, fh.getInputLang());

        fh.setInputLang(Locale.UK);

        assertEquals(Locale.UK, fh.getInputLang());

        String entityXml = entity.toXml();

        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceFieldHistory desFh = ((CoalesceStringField) desEntity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/ActionNumber")).getHistoryRecord("00BB7A9F-4F37-46E9-85EB-9280ED3619CC");

        assertEquals(Locale.UK, desFh.getInputLang());
  
    }
    
    @Test
    public void GetDateCreatedExistTest()
    {

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_CREATED, field.getDateCreated());

    }

    @Test
    public void SetDateCreatedTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory(mission);

        DateTime now = JodaDateTimeHelper.nowInUtc();

        field.setDateCreated(now);

        CoalesceFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals(now, savedField.getDateCreated());

    }

    @Test
    public void GetLastModifiedExistTest()
    {

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_HISTORY_MODIFIED, field.getLastModified());

    }

    @Test
    public void SetLastModifiedTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory(mission);

        DateTime now = JodaDateTimeHelper.nowInUtc();

        field.setLastModified(now);

        CoalesceFieldHistory savedField = GetSavedTestMissionFieldHistory(mission);

        assertEquals(now, savedField.getLastModified());

    }

    @Test
    public void ToXmlTest()
    {

        CoalesceFieldHistory field = GetTestMissionNameFieldHistory();

        String fieldXml = field.toXml();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_HISTORY_XML, fieldXml.replace("\n", "").replace("\r", ""));

    }

    @Test
    public void StringTypeTest() throws CoalesceDataFormatException
    {

        CoalesceField<?> field = CoalesceFieldTest.getTestMissionNameField();

        CoalesceFieldHistory fh = field.getHistory().get(0);
        String data = fh.getValue();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_HISTORY_VALUE, data);

        fh.setTypedValue("Changed");

        data = null;
        data = fh.getValue();

        assertTrue(data instanceof String);
        assertEquals("Changed", data);
        assertEquals("Changed", fh.getBaseValue());

    }

    @Test(expected = ClassCastException.class)
    public void SetTypedValueStringTypeTypeMismatchTest()
    {

        CoalesceField<?> field = CoalesceFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);
        field.setTypedValue(JodaDateTimeHelper.nowInUtc());

        CoalesceFieldHistory fh = field.getHistory().get(0);

        fh.setTypedValue(UUID.randomUUID());

    }

    @Test
    public void UriTypeTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "Uri",
                                                                              ECoalesceFieldDataTypes.URI_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        Sleep();
        field.setTypedValue("uri:document/pdf");
        field.setTypedValue("uri:document/xyz");

        CoalesceFieldHistory fh = field.getHistory().get(0);

        assertEquals("uri:document/pdf", fh.getBaseValue());

        fh.setTypedValue("uri:document/zip");

        String data = fh.getValue();

        assertEquals("uri:document/zip", data);
        assertEquals("uri:document/zip", fh.getBaseValue());

    }

    @Test
    public void GetDataSetTypedValueDateTimeTypeTest() throws CoalesceDataFormatException
    {
        CoalesceField<?> field = CoalesceFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        DateTime now = JodaDateTimeHelper.nowInUtc();
        field.setTypedValue(now);

        CoalesceFieldHistory fh = field.getHistory().get(0);

        DateTime newDate = now.plusDays(1);

        fh.setTypedValue(newDate);

        assertEquals(newDate, fh.getDateTimeValue());

    }

    @Test(expected = ClassCastException.class)
    public void SetTypedValueDateTimeTypeTypeMismatchTest()
    {

        CoalesceField<?> field = CoalesceFieldTest.getTestMissionNameField();

        DateTime now = JodaDateTimeHelper.nowInUtc();
        field.setTypedValue(now);

        CoalesceFieldHistory fh = field.getHistory().get(0);

        fh.setTypedValue(now);

    }

    @Test
    public void GetDataSetTypedValueBooleanTypeTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "Boolean",
                                                                              ECoalesceFieldDataTypes.BOOLEAN_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        Sleep();
        field.setTypedValue(true);
        field.setTypedValue(false);

        CoalesceFieldHistory fh = field.getHistory().get(0);

        boolean data = Boolean.parseBoolean(fh.getValue());

        assertEquals(true, data);
        assertEquals("true", fh.getBaseValue().toLowerCase());
        assertEquals(true, fh.getBooleanValue());

        fh.setTypedValue(false);

        // data = null;
        data = Boolean.parseBoolean(fh.getValue());

        // assertTrue(data instanceof Boolean);
        assertEquals(false, data);
        assertEquals("false", fh.getBaseValue().toLowerCase());
        assertEquals(false, fh.getBooleanValue());

    }

    @Test(expected = ClassCastException.class)
    public void SetTypedValueBooleanTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        CoalesceField<?> field = CoalesceFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(JodaDateTimeHelper.nowInUtc());

        CoalesceFieldHistory fh = field.getHistory().get(0);
        fh.setTypedValue(true);

    }

    @Test
    public void GetDataSetTypedValueIntegerTypeTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "Integer",
                                                                              ECoalesceFieldDataTypes.INTEGER_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        Sleep();
        field.setTypedValue(1111);
        field.setTypedValue(2222);

        CoalesceFieldHistory fh = field.getHistory().get(0);

        int data = Integer.parseInt(fh.getValue());

        // assertTrue(data instanceof Integer);
        assertEquals("1111", fh.getBaseValue());
        assertEquals(1111, fh.getIntegerValue().intValue());
        assertEquals(1111, data);

        fh.setTypedValue(3333);

        data = Integer.parseInt(fh.getValue());

        // assertTrue(data instanceof Integer);
        assertEquals(3333, data);
        assertEquals("3333", fh.getBaseValue());
        assertEquals(3333, fh.getIntegerValue().intValue());

    }

    @Test(expected = ClassCastException.class)
    public void SetTypedValueIntgerTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        CoalesceField<?> field = CoalesceFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(JodaDateTimeHelper.nowInUtc());

        CoalesceFieldHistory fh = field.getHistory().get(0);
        fh.setTypedValue(1111);

    }

    @Test
    public void GetDataSetTypedValueGuidTypeTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "GUID",
                                                                              ECoalesceFieldDataTypes.GUID_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        Sleep();
        UUID guid = UUID.randomUUID();
        field.setTypedValue(guid);
        field.setTypedValue(UUID.randomUUID());

        CoalesceFieldHistory fh = field.getHistory().get(0);

        UUID data = fh.getGuidValue();

        assertTrue(data instanceof UUID);
        assertEquals(guid, data);
        assertEquals(GUIDHelper.getGuidString(guid), fh.getBaseValue());
        assertEquals(guid, fh.getGuidValue());

        UUID newGuid = UUID.randomUUID();
        fh.setTypedValue(newGuid);

        data = null;
        data = fh.getGuidValue();

        assertTrue(data instanceof UUID);
        assertEquals(newGuid, data);
        assertEquals(GUIDHelper.getGuidString(newGuid), fh.getBaseValue());
        assertEquals(newGuid, fh.getGuidValue());

    }

    @Test(expected = ClassCastException.class)
    public void SetTypedValueGUIDTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        CoalesceField<?> field = CoalesceFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(JodaDateTimeHelper.nowInUtc());

        CoalesceFieldHistory fh = field.getHistory().get(0);
        fh.setTypedValue(UUID.randomUUID());

    }

    @Test
    public void toXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceFieldHistory fh = ((CoalesceStringField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/ActionNumber")).getHistoryRecord("00BB7A9F-4F37-46E9-85EB-9280ED3619CC");

        String fhXml = fh.toXml();

        Fieldhistory desFh = (Fieldhistory) XmlHelper.deserialize(fhXml, Fieldhistory.class);

        assertEquals(fh.getKey(), desFh.getKey());
        assertEquals(fh.getName(), desFh.getName());
        assertEquals(fh.getDateCreated(), desFh.getDatecreated());
        assertEquals(fh.getLastModified(), desFh.getLastmodified());
        assertEquals(fh.getDataType(), ECoalesceFieldDataTypes.getTypeForCoalesceType(desFh.getDatatype()));
        assertEquals(fh.getClassificationMarking(), new Marking(desFh.getClassificationmarking()));
        assertEquals(fh.getLabel(), desFh.getLabel());
        assertEquals(fh.getValue(), desFh.getValue());
        assertEquals(fh.getPreviousHistoryKey(), desFh.getPrevioushistorykey());
        assertEquals(fh.getStatus(), ECoalesceDataObjectStatus.getTypeForLabel(desFh.getStatus()));

    }

    @Test
    public void setStatusTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceFieldHistory fh = ((CoalesceStringField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/ActionNumber")).getHistoryRecord("00BB7A9F-4F37-46E9-85EB-9280ED3619CC");

        assertEquals(ECoalesceDataObjectStatus.ACTIVE, fh.getStatus());

        fh.setStatus(ECoalesceDataObjectStatus.UNKNOWN);
        String fhXml = fh.toXml();

        Fieldhistory desFh = (Fieldhistory) XmlHelper.deserialize(fhXml, Fieldhistory.class);

        assertEquals(ECoalesceDataObjectStatus.UNKNOWN, ECoalesceDataObjectStatus.getTypeForLabel(desFh.getStatus()));

    }

    @Test
    public void attributeTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceFieldHistory fh = ((CoalesceStringField) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/ActionNumber")).getHistoryRecord("00BB7A9F-4F37-46E9-85EB-9280ED3619CC");

        fh.setAttribute("TestAttribute", "TestingValue");

        assertEquals(11, fh.getAttributes().size());

        assertEquals("TestingValue", fh.getAttribute("TestAttribute"));

        assertEquals("ActionNumber", fh.getName());
        assertEquals(false, fh.getNoIndex());

        fh.setAttribute("Name", "TestingName");
        assertEquals("TestingName", fh.getName());
        assertEquals("TestingName", fh.getAttribute("Name"));

        UUID guid = UUID.randomUUID();
        fh.setAttribute("Key", guid.toString());
        assertEquals(guid.toString(), fh.getKey());
        assertEquals(guid.toString(), fh.getAttribute("Key"));

        DateTime now = JodaDateTimeHelper.nowInUtc();
        DateTime future = now.plusDays(2);

        fh.setAttribute("DateCreated", JodaDateTimeHelper.toXmlDateTimeUTC(now));
        assertEquals(now, fh.getDateCreated());

        fh.setAttribute("NoIndex", "True");
        assertEquals(true, fh.getNoIndex());

        fh.setAttribute("DataType", "Integer");
        assertEquals(ECoalesceFieldDataTypes.INTEGER_TYPE, fh.getDataType());

        fh.setAttribute("Classificationmarking", "(TS)");
        assertEquals(new Marking("(TS)"), fh.getClassificationMarking());

        fh.setAttribute("Label", "labelTest");
        assertEquals("labelTest", fh.getLabel());

        fh.setAttribute("Value", "123");
        assertEquals("123", fh.getValue());

        UUID previousGuid = UUID.randomUUID();
        fh.setAttribute("PreviousHistoryKey", previousGuid.toString());
        assertEquals(previousGuid.toString(), fh.getPreviousHistoryKey());

        fh.setAttribute("InputLang", "");
        assertEquals(null, fh.getInputLang());
        
        fh.setAttribute("InputLang", "en-gb");
        assertEquals(Locale.UK, fh.getInputLang());
        
        fh.setAttribute("InputLang", "engb");
        assertEquals(Locale.UK, fh.getInputLang());

        fh.setAttribute("Status", ECoalesceDataObjectStatus.UNKNOWN.getLabel());
        assertEquals(ECoalesceDataObjectStatus.UNKNOWN, fh.getStatus());

        fh.setAttribute("LastModified", JodaDateTimeHelper.toXmlDateTimeUTC(future));
        assertEquals(future, fh.getLastModified());

        String entityXml = entity.toXml();
        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceFieldHistory desFh = ((CoalesceStringField) desEntity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/ActionNumber")).getHistoryRecord(guid.toString());

        assertEquals("TestingValue", desFh.getAttribute("TestAttribute"));
        assertEquals("TestingName", desFh.getName());
        assertEquals(guid.toString(), desFh.getKey());
        assertEquals(now, desFh.getDateCreated());
        assertEquals(future, desFh.getLastModified());
        assertEquals(true, desFh.getNoIndex());
        assertEquals(ECoalesceFieldDataTypes.INTEGER_TYPE, desFh.getDataType());
        assertEquals(new Marking("(TS)"), desFh.getClassificationMarking());
        assertEquals("labelTest", desFh.getLabel());
        assertEquals("123", desFh.getValue());
        assertEquals(previousGuid.toString(), desFh.getPreviousHistoryKey());
        assertEquals(Locale.UK, fh.getInputLang());
        assertEquals(ECoalesceDataObjectStatus.UNKNOWN, desFh.getStatus());

    }

    // -----------------------------------------------------------------------//
    // Private Static Methods
    // -----------------------------------------------------------------------//

    private static CoalesceFieldHistory GetTestMissionNameFieldHistory(String entityXml)
    {

        CoalesceEntity entity = CoalesceEntity.create(entityXml);

        return GetTestMissionNameFieldHistory(entity);

    }

    private static CoalesceFieldHistory GetTestMissionNameFieldHistory(CoalesceEntity entity)
    {

        return GetTestMissionFieldHistoryByName(entity, CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

    }

    private static CoalesceFieldHistory GetTestMissionNameFieldHistory()
    {

        return GetTestMissionNameFieldHistory(CoalesceTypeInstances.TEST_MISSION);

    }

    private static CoalesceFieldHistory GetTestMissionFieldHistoryByName(String fieldPath)
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        return GetTestMissionFieldHistoryByName(mission, fieldPath);

    }

    private static CoalesceFieldHistory GetTestMissionFieldHistoryByName(CoalesceEntity entity, String fieldPath)
    {

        CoalesceDataObject fdo = entity.getDataObjectForNamePath(fieldPath);

        assertTrue(fdo instanceof CoalesceField<?>);

        CoalesceField<?> field = (CoalesceField<?>) fdo;

        if (field.getHistory().isEmpty())
        {
            return null;
        }
        else
        {
            return field.getHistory().get(0);
        }

    }

    private static CoalesceFieldHistory GetSavedTestMissionFieldHistory(CoalesceEntity entity)
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
