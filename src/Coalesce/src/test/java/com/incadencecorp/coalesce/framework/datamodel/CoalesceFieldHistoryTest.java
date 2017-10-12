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
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.incadencecorp.coalesce.common.CoalesceTypeInstances;
import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.helpers.GUIDHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;
import com.incadencecorp.coalesce.framework.testobjects.EActionStatuses;
import com.incadencecorp.coalesce.framework.testobjects.MissionEntity;
import com.incadencecorp.coalesce.framework.testobjects.Photos.PhotoGalleryEntity;

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

    @Rule
    public ExpectedException _thrown = ExpectedException.none();

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
    public void constructorCoalesceFieldBaseCoalesceFieldTest()
    {

        CoalesceStringField field = new CoalesceStringField();
        field.initialize(null, null, new Field());

        @SuppressWarnings("unused")
        CoalesceFieldHistory fh = CoalesceFieldHistory.create(field);

    }

    @Test
    public void constructorCoalesceFieldHistoryTest()
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
        field.initialize(record, null, new Field());

        @SuppressWarnings("unused")
        CoalesceFieldHistory fh = CoalesceFieldHistory.create(field);

    }

    // TODO: replace these unit test with functions that don't require
    // Coalesce.Objects

    @Test
    public void addFieldHistoryWithDefaultValue()
    {
        // Create New Action
        PhotoGalleryEntity entity = new PhotoGalleryEntity();
        entity.initialize();

        // Change Value (Current Status has a Default Value)
        entity.setCurrentStatus(EActionStatuses.CollectionPending);

        // Verify
        assertTrue(entity.getCurrentStatusHistory().length == 1);

        // Change Value
        entity.setCurrentStatus(EActionStatuses.ExploitationPending);

        // Verify
        assertEquals(entity.getCurrentStatusHistory()[0].getValue(), EActionStatuses.CollectionPending.getLabel());
        assertTrue(entity.getCurrentStatusHistory().length == 2);

        // Create New Action from entity
        PhotoGalleryEntity entity2 = new PhotoGalleryEntity();
        entity2.initialize(entity.toXml());

        // Change Value (Same Value no History Should be Created)
        entity2.setCurrentStatus(EActionStatuses.ExploitationPending);

        // Verify
        assertTrue(entity2.getCurrentStatusHistory().length == 2);

        // Change Value (History Should be Created)
        entity2.setCurrentStatus(EActionStatuses.CollectionComplete);

        // Verify
        assertTrue(entity2.getCurrentStatusHistory().length == 3);
        assertEquals(entity2.getCurrentStatusHistory()[0].getValue(), EActionStatuses.ExploitationPending.getLabel());
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
        assertTrue(entity.getMissionName().getHistory().length == 0);

        // Change Value (History Should be Created)
        entity.getMissionName().setValue("History should be added");

        // Verify
        assertTrue(entity.getMissionName().getHistory().length == 1);
        assertTrue(entity.getMissionName().getHistory()[0].getValue() == "No history should be added");
        assertTrue(entity.getMissionName().getPreviousHistoryKey() == entity.getMissionName().getHistory()[0].getKey());
        assertTrue(entity.getMissionName().getHistory()[0].getPreviousHistoryKey() == "00000000-0000-0000-0000-000000000000");
    }

    @Test
    public void constructorNoPreviousHistory()
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
        assertEquals(field.getModifiedBy(), fieldHistory.getModifiedBy());
        assertEquals(field.getModifiedByIP(), fieldHistory.getModifiedByIP());
        assertEquals(field.getClassificationMarking(), fieldHistory.getClassificationMarking());
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
    public void constructorPreviousHistory() throws CoalesceDataFormatException
    {

        CoalesceField<?> field = CoalesceFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        field.setSuspendHistory(true);
        CoalesceFieldHistory fh = CoalesceFieldHistory.create(field);
        assertFieldHistory(field, fh);

        field.setTypedValue(2222);
        field.setSuspendHistory(false);

        assertEquals(2222, field.getIntegerValue().intValue());
        assertEquals(1, field.getHistory().length);
        assertEquals(CoalesceTypeInstances.TEST_MISSION_BASE64_VALUE, field.getHistory()[0].getIntegerValue().intValue());
        assertEquals(fh, field.getHistory()[0]);

    }

    @Test
    public void getKeyTest()
    {

        CoalesceFieldHistory field = getTestMissionNameFieldHistory();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_HISTORY_KEY, field.getKey());

    }

    @Test
    public void setKeyTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = getTestMissionNameFieldHistory(mission);

        UUID newGuid = UUID.randomUUID();
        field.setKey(newGuid.toString());

        CoalesceFieldHistory savedField = getSavedTestMissionFieldHistory(mission);

        assertEquals(savedField.getKey().toUpperCase(), newGuid.toString().toUpperCase());

    }

    @Test
    public void getNameTest()
    {

        CoalesceFieldHistory field = getTestMissionNameFieldHistory();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_NAME, field.getName());

    }

    @Test
    public void setNameTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = getTestMissionNameFieldHistory(mission);

        field.setName("Testingname");

        String serializedMission = mission.toXml();
        CoalesceEntity savedMission = CoalesceEntity.create(serializedMission);

        CoalesceFieldHistory savedField = getTestMissionNameFieldHistory(savedMission);

        assertEquals("Testingname", savedField.getName());

    }

    @Test
    public void getValueTest()
    {

        CoalesceFieldHistory field = getTestMissionNameFieldHistory();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_HISTORY_VALUE, field.getBaseValue());

    }

    @Test
    public void setValueTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = getTestMissionNameFieldHistory(mission);

        field.setBaseValue("Testingvalue");

        CoalesceFieldHistory savedField = getSavedTestMissionFieldHistory(mission);

        assertEquals("Testingvalue", savedField.getBaseValue());

    }

    @Test
    public void getDataType()
    {
        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory stringField = getTestMissionFieldHistoryByName(mission,
                                                                            CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
        assertEquals(ECoalesceFieldDataTypes.STRING_TYPE, stringField.getDataType());

        CoalesceFieldHistory dateField = getTestMissionFieldHistoryByName(mission,
                                                                          CoalesceTypeInstances.TEST_MISSION_INCIDENT_DATE_TIME_PATH);
        assertEquals(ECoalesceFieldDataTypes.DATE_TIME_TYPE, dateField.getDataType());

    }

    @Test
    public void setDateType()
    {
        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory stringField = getTestMissionFieldHistoryByName(mission,
                                                                            CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
        stringField.setDataType(ECoalesceFieldDataTypes.DATE_TIME_TYPE);

        CoalesceFieldHistory dateField = getTestMissionFieldHistoryByName(mission,
                                                                          CoalesceTypeInstances.TEST_MISSION_INCIDENT_DATE_TIME_PATH);
        dateField.setDataType(ECoalesceFieldDataTypes.INTEGER_TYPE);

        String serializedMission = mission.toXml();
        CoalesceEntity savedMission = CoalesceEntity.create(serializedMission);

        CoalesceFieldHistory savedStringField = getTestMissionFieldHistoryByName(savedMission,
                                                                                 CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
        assertEquals(ECoalesceFieldDataTypes.DATE_TIME_TYPE, savedStringField.getDataType());

        CoalesceFieldHistory savedDateField = getTestMissionFieldHistoryByName(savedMission,
                                                                               CoalesceTypeInstances.TEST_MISSION_INCIDENT_DATE_TIME_PATH);
        assertEquals(ECoalesceFieldDataTypes.INTEGER_TYPE, savedDateField.getDataType());

    }

    @Test
    public void getLabelTest()
    {

        CoalesceFieldHistory field = getTestMissionFieldHistoryByName(CoalesceTypeInstances.TEST_MISSION_ACTION_NUMBER_PATH);

        assertEquals(CoalesceTypeInstances.TEST_MISSION_ACTION_NUMBER_LABEL_HISTORY, field.getLabel());

    }

    @Test
    public void getLabelDoesNotExistTest()
    {

        CoalesceFieldHistory field = getTestMissionFieldHistoryByName(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

        assertEquals("", field.getLabel());

    }

    @Test
    public void setLabelTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = getTestMissionNameFieldHistory(mission);

        field.setLabel("Testinglabel");

        CoalesceFieldHistory savedField = getSavedTestMissionFieldHistory(mission);

        assertEquals("Testinglabel", savedField.getLabel());

    }

    @Test
    public void setLabelNullTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = getTestMissionNameFieldHistory(mission);

        field.setLabel(null);

        CoalesceFieldHistory savedField = getSavedTestMissionFieldHistory(mission);

        assertEquals("", savedField.getLabel());

    }

    @Test
    public void getModifiedByDoesNotExistTest()
    {

        CoalesceFieldHistory field = getTestMissionNameFieldHistory();

        assertEquals("", field.getModifiedBy());

    }

    @Test
    public void setModifiedByTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = getTestMissionNameFieldHistory(mission);

        field.setModifiedBy("TestingModifiedBy");

        CoalesceFieldHistory savedField = getSavedTestMissionFieldHistory(mission);

        assertEquals("TestingModifiedBy", savedField.getModifiedBy());

    }

    @Test
    public void getModifiedByIpDoesNotExistTest()
    {

        CoalesceFieldHistory field = getTestMissionNameFieldHistory();

        assertEquals("", field.getModifiedByIP());

    }

    @Test
    public void setModifiedByIpTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = getTestMissionNameFieldHistory(mission);

        field.setModifiedByIP("192.168.2.2");

        CoalesceFieldHistory savedField = getSavedTestMissionFieldHistory(mission);

        assertEquals("192.168.2.2", savedField.getModifiedByIP());

    }

    @Test
    public void getClassificationMarkingDefaultTest()
    {

        CoalesceFieldHistory field = getTestMissionNameFieldHistory();

        Assert.assertEquals(new Marking().getClassification().getPortion(),
                            field.getClassificationMarking().getClassification().getPortion());

    }

    @Test
    public void getClassificationMarkingAfterSetAndSerializedTest()
    {
        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = getTestMissionNameFieldHistory(mission);

        field.setClassificationMarking(TOPSECRETCLASSIFICATIONMARKING);

        CoalesceFieldHistory savedField = getSavedTestMissionFieldHistory(mission);

        Assert.assertEquals(TOPSECRETCLASSIFICATIONMARKING.getClassification().getPortion(),
                            savedField.getClassificationMarking().getClassification().getPortion());

    }

    @Test
    public void setClassificationMarkingTopSecretTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> field = (CoalesceField<?>) mission.getCoalesceObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

        assertTrue(field != null);

        assertFalse(field.getHistory().length == 0);

        CoalesceFieldHistory fh = field.getHistory()[0];

        fh.setClassificationMarking(TOPSECRETCLASSIFICATIONMARKING);

        Assert.assertEquals(TOPSECRETCLASSIFICATIONMARKING.getClassification().getPortion(),
                            fh.getClassificationMarking().getClassification().getPortion());

    }

    @Test
    public void getValueWithMarkingTest()
    {

        CoalesceFieldHistory field = getTestMissionNameFieldHistory();

        assertEquals("UNCLASSIFIED NORTHCOM Volunteer Background Checks", field.getValueWithMarking());
    }

    @Test
    public void getPortionMarkingTest()
    {

        CoalesceFieldHistory field = getTestMissionNameFieldHistory();

        assertEquals("(U)", field.getPortionMarking());
    }

    @Test
    public void getPreviousHistoryKeyNoPreviousTest()
    {

        CoalesceFieldHistory field = getTestMissionNameFieldHistory();

        assertEquals("00000000-0000-0000-0000-000000000000", field.getPreviousHistoryKey());

        field.setPreviousHistoryKey("");

        assertEquals("00000000-0000-0000-0000-000000000000", field.getPreviousHistoryKey());

    }

    @Test
    public void getPreviousHistoryKeyPreviousTest()
    {

        CoalesceFieldHistory field = getTestMissionNameFieldHistory();

        assertEquals("00000000-0000-0000-0000-000000000000", field.getPreviousHistoryKey());

    }

    @Test
    public void inputLangTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceFieldHistory fh = ((CoalesceStringField) entity.getCoalesceObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/ActionNumber")).getHistoryRecord("00BB7A9F-4F37-46E9-85EB-9280ED3619CC");

        assertEquals(null, fh.getInputLang());

        fh.setInputLang(Locale.UK);

        assertEquals(Locale.UK, fh.getInputLang());

        String entityXml = entity.toXml();

        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceFieldHistory desFh = ((CoalesceStringField) desEntity.getCoalesceObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/ActionNumber")).getHistoryRecord("00BB7A9F-4F37-46E9-85EB-9280ED3619CC");

        assertEquals(Locale.UK, desFh.getInputLang());

    }

    @Test
    public void getDateCreatedExistTest()
    {

        CoalesceFieldHistory field = getTestMissionNameFieldHistory();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_CREATED, field.getDateCreated());

    }

    @Test
    public void setDateCreatedTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = getTestMissionNameFieldHistory(mission);

        DateTime now = JodaDateTimeHelper.nowInUtc();

        field.setDateCreated(now);

        CoalesceFieldHistory savedField = getSavedTestMissionFieldHistory(mission);

        assertEquals(now, savedField.getDateCreated());

    }

    @Test
    public void getLastModifiedExistTest()
    {

        CoalesceFieldHistory field = getTestMissionNameFieldHistory();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_HISTORY_MODIFIED, field.getLastModified());

    }

    @Test
    public void setLastModifiedTest()
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceFieldHistory field = getTestMissionNameFieldHistory(mission);

        DateTime now = JodaDateTimeHelper.nowInUtc();

        field.setLastModified(now);

        CoalesceFieldHistory savedField = getSavedTestMissionFieldHistory(mission);

        assertEquals(now, savedField.getLastModified());

    }

    // @Test
    public void toXmlTest()
    {

        CoalesceFieldHistory field = getTestMissionNameFieldHistory();

        String fieldXml = field.toXml();

        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_HISTORY_XML, fieldXml.replace("\n", "").replace("\r", ""));

    }

    @Test
    public void stringTypeTest() throws CoalesceDataFormatException
    {

        CoalesceField<?> field = CoalesceFieldTest.getTestMissionNameField();

        CoalesceFieldHistory fh = field.getHistory()[0];
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
    public void setTypedValueStringTypeTypeMismatchTest()
    {

        CoalesceField<?> field = CoalesceFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);
        field.setTypedValue(JodaDateTimeHelper.nowInUtc());

        CoalesceFieldHistory fh = field.getHistory()[0];

        fh.setTypedValue(UUID.randomUUID());

    }

    @Test
    public void uriTypeTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getCoalesceObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "Uri",
                                                                              ECoalesceFieldDataTypes.URI_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        sleep();
        field.setTypedValue("uri:document/pdf");
        field.setTypedValue("uri:document/xyz");

        CoalesceFieldHistory fh = field.getHistory()[0];

        assertEquals("uri:document/pdf", fh.getBaseValue());

        fh.setTypedValue("uri:document/zip");

        String data = fh.getValue();

        assertEquals("uri:document/zip", data);
        assertEquals("uri:document/zip", fh.getBaseValue());

    }

    @Test
    public void getDataSetTypedValueDateTimeTypeTest() throws CoalesceDataFormatException
    {
        CoalesceField<?> field = CoalesceFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        DateTime now = JodaDateTimeHelper.nowInUtc();
        field.setTypedValue(now);

        CoalesceFieldHistory fh = field.getHistory()[0];

        DateTime newDate = now.plusDays(1);

        fh.setTypedValue(newDate);

        assertEquals(newDate, fh.getDateTimeValue());

    }

    @Test(expected = ClassCastException.class)
    public void setTypedValueDateTimeTypeTypeMismatchTest()
    {

        CoalesceField<?> field = CoalesceFieldTest.getTestMissionNameField();

        DateTime now = JodaDateTimeHelper.nowInUtc();
        field.setTypedValue(now);

        CoalesceFieldHistory fh = field.getHistory()[0];

        fh.setTypedValue(now);

    }

    @Test
    public void getDataSetTypedValueBooleanTypeTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getCoalesceObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "Boolean",
                                                                              ECoalesceFieldDataTypes.BOOLEAN_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        sleep();
        field.setTypedValue(true);
        field.setTypedValue(false);

        CoalesceFieldHistory fh = field.getHistory()[0];

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
    public void setTypedValueBooleanTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        CoalesceField<?> field = CoalesceFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(JodaDateTimeHelper.nowInUtc());

        CoalesceFieldHistory fh = field.getHistory()[0];
        fh.setTypedValue(true);

    }

    @Test
    public void getDataSetTypedValueIntegerTypeTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getCoalesceObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "Integer",
                                                                              ECoalesceFieldDataTypes.INTEGER_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        sleep();
        field.setTypedValue(1111);
        field.setTypedValue(2222);

        CoalesceFieldHistory fh = field.getHistory()[0];

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
    public void setTypedValueIntgerTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        CoalesceField<?> field = CoalesceFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(JodaDateTimeHelper.nowInUtc());

        CoalesceFieldHistory fh = field.getHistory()[0];
        fh.setTypedValue(1111);

    }

    @Test
    public void getDataSetTypedValueGuidTypeTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset parentRecordset = (CoalesceRecordset) entity.getCoalesceObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset");
        CoalesceFieldDefinition fileFieldDef = CoalesceFieldDefinition.create(parentRecordset,
                                                                              "GUID",
                                                                              ECoalesceFieldDataTypes.GUID_TYPE);

        CoalesceRecord parentRecord = parentRecordset.getItem(0);
        CoalesceField<?> field = CoalesceField.create(parentRecord, fileFieldDef);

        sleep();
        UUID guid = UUID.randomUUID();
        field.setTypedValue(guid);
        field.setTypedValue(UUID.randomUUID());

        CoalesceFieldHistory fh = field.getHistory()[0];

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
    public void setTypedValueGUIDTypeTypeMismatchTest() throws UnsupportedEncodingException
    {

        CoalesceField<?> field = CoalesceFieldTest.getTestMissionFieldByName(CoalesceTypeInstances.TEST_MISSION_START_TIME_PATH);

        field.setTypedValue(JodaDateTimeHelper.nowInUtc());

        CoalesceFieldHistory fh = field.getHistory()[0];
        fh.setTypedValue(UUID.randomUUID());

    }

    @Test
    public void toXmlTest2()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceFieldHistory fh = ((CoalesceStringField) entity.getCoalesceObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/ActionNumber")).getHistoryRecord("00BB7A9F-4F37-46E9-85EB-9280ED3619CC");

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
        assertEquals(fh.getStatus(), desFh.getStatus());

    }

    @Test
    public void setStatusTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceFieldHistory fh = ((CoalesceStringField) entity.getCoalesceObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/ActionNumber")).getHistoryRecord("00BB7A9F-4F37-46E9-85EB-9280ED3619CC");

        assertEquals(ECoalesceObjectStatus.ACTIVE, fh.getStatus());

        fh.setStatus(ECoalesceObjectStatus.UNKNOWN);
        String fhXml = fh.toXml();

        Fieldhistory desFh = (Fieldhistory) XmlHelper.deserialize(fhXml, Fieldhistory.class);

        assertEquals(ECoalesceObjectStatus.UNKNOWN, desFh.getStatus());

    }

    @Test
    public void attributeTest() throws CoalesceDataFormatException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceFieldHistory fh = ((CoalesceStringField) entity.getCoalesceObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/ActionNumber")).getHistoryRecord("00BB7A9F-4F37-46E9-85EB-9280ED3619CC");

        int before = fh.getAttributes().size();

        fh.setAttribute("TestAttribute", "TestingValue");

        assertEquals(before + 1, fh.getAttributes().size());

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

        fh.setAttribute("InputLang", "en-GB");
        assertEquals(Locale.UK, fh.getInputLang());

        fh.setAttribute("Status", ECoalesceObjectStatus.UNKNOWN.toString());
        assertEquals(ECoalesceObjectStatus.UNKNOWN, fh.getStatus());

        fh.setAttribute("LastModified", JodaDateTimeHelper.toXmlDateTimeUTC(future));
        assertEquals(future, fh.getLastModified());

        String entityXml = entity.toXml();
        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceFieldHistory desFh = ((CoalesceStringField) desEntity.getCoalesceObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/ActionNumber")).getHistoryRecord(guid.toString());

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
        assertEquals(ECoalesceObjectStatus.UNKNOWN, desFh.getStatus());

    }

    @Test
    public void setAttributeInputLangInvalidCaseTest() throws CoalesceDataFormatException
    {
        _thrown.expect(IllegalArgumentException.class);
        _thrown.expectMessage("");

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceFieldHistory fh = ((CoalesceStringField) entity.getCoalesceObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/ActionNumber")).getHistoryRecord("00BB7A9F-4F37-46E9-85EB-9280ED3619CC");

        fh.setAttribute("InputLang", "en-gb");

    }

    @Test
    public void setAttributeInputLangMissingDashTest() throws CoalesceDataFormatException
    {
        _thrown.expect(IllegalArgumentException.class);
        _thrown.expectMessage("");

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceFieldHistory fh = ((CoalesceStringField) entity.getCoalesceObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/ActionNumber")).getHistoryRecord("00BB7A9F-4F37-46E9-85EB-9280ED3619CC");

        fh.setAttribute("InputLang", "engb");

    }

    // -----------------------------------------------------------------------//
    // Private Static Methods
    // -----------------------------------------------------------------------//

    private static CoalesceFieldHistory getTestMissionNameFieldHistory(String entityXml)
    {

        CoalesceEntity entity = CoalesceEntity.create(entityXml);

        return getTestMissionNameFieldHistory(entity);

    }

    private static CoalesceFieldHistory getTestMissionNameFieldHistory(CoalesceEntity entity)
    {

        return getTestMissionFieldHistoryByName(entity, CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

    }

    private static CoalesceFieldHistory getTestMissionNameFieldHistory()
    {

        return getTestMissionNameFieldHistory(CoalesceTypeInstances.TEST_MISSION);

    }

    private static CoalesceFieldHistory getTestMissionFieldHistoryByName(String fieldPath)
    {

        CoalesceEntity mission = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        return getTestMissionFieldHistoryByName(mission, fieldPath);

    }

    private static CoalesceFieldHistory getTestMissionFieldHistoryByName(CoalesceEntity entity, String fieldPath)
    {

        CoalesceObject fdo = entity.getCoalesceObjectForNamePath(fieldPath);

        assertTrue(fdo instanceof CoalesceField<?>);

        CoalesceField<?> field = (CoalesceField<?>) fdo;

        if (field.getHistory().length == 0)
        {
            return null;
        }
        else
        {
            return field.getHistory()[0];
        }

    }

    private static CoalesceFieldHistory getSavedTestMissionFieldHistory(CoalesceEntity entity)
    {

        String serializedMission = entity.toXml();

        return getTestMissionNameFieldHistory(serializedMission);

    }

    private void sleep()
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
