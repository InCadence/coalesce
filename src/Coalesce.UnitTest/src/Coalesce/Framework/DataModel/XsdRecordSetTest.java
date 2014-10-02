package Coalesce.Framework.DataModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.NullArgumentException;
import org.joda.time.DateTime;
import org.junit.Test;

import Coalesce.Common.Helpers.JodaDateTimeHelper;
import Coalesce.Common.UnitTest.CoalesceTypeInstances;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset;

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

public class XsdRecordSetTest {

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
    public void createRecordsetEmptyTest()
    {
        XsdEntity entity = new XsdEntity();
        entity.initialize();
        XsdSection section = entity.createSection("New Section");

        XsdRecordset recordset = XsdRecordset.create(section, "New Recordset");

        assertNotNull(recordset);

        Map<String, XsdRecordset> recordsets = section.getRecordsets();

        assertEquals(1, recordsets.size());
        assertNotNull(recordsets.get(recordset.getKey()));

    }

    @Test
    public void createRecordsetExistsTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdSection section = entity.getSection(CoalesceTypeInstances.TEST_MISSION_INFO_SECTION_PATH);

        XsdRecordset recordset = XsdRecordset.create(section, "New Section");

        assertNotNull(recordset);

        Map<String, XsdRecordset> recordsets = section.getRecordsets();

        assertEquals(2, recordsets.size());

        XsdRecordset existingRecordset = recordsets.get("7A158E39-B6C4-4912-A712-DF296375A368");
        assertNotNull(existingRecordset);

        assertNotNull(recordsets.get(recordset.getKey()));

        List<XsdFieldDefinition> fieldDefinitions = existingRecordset.getFieldDefinitions();

        assertEquals("93C6A209-AD86-4474-9FFB-D6801B2548AA", fieldDefinitions.get(0).getKey());
        assertEquals("DBBB6CEC-DD98-4B31-9995-8AF0A5E184EC", fieldDefinitions.get(1).getKey());
        assertEquals("7D45F5BD-14A0-4890-B8C5-D502806A4607", fieldDefinitions.get(2).getKey());
        assertEquals("1A7DA2CD-8A83-4E86-ADE8-15FDECE0564E", fieldDefinitions.get(3).getKey());
        assertEquals("5F2D150A-CEDB-4BF9-9A66-DD3212721E2B", fieldDefinitions.get(4).getKey());
        assertEquals("00D19206-5246-45EE-BA15-07507767040A", fieldDefinitions.get(5).getKey());
        assertEquals("FADEBDFD-4477-4D2E-8C33-186717209F16", fieldDefinitions.get(6).getKey());
        assertEquals("3B28419A-6744-4B10-B4E7-01B3A04620ED", fieldDefinitions.get(7).getKey());
        assertEquals("F862B507-8523-4BB2-8A49-EB45FBEB88AD", fieldDefinitions.get(8).getKey());
        assertEquals("6C2296DD-205E-432B-B381-2236E7A43162", fieldDefinitions.get(9).getKey());
        assertEquals("5150BB68-0156-469D-9C93-C62CF3F7FA19", fieldDefinitions.get(10).getKey());
        assertEquals("12E16C1A-2D48-4228-90AE-596591E66536", fieldDefinitions.get(11).getKey());
        assertEquals("EEA36E54-601E-4254-84D3-2B387CF4192A", fieldDefinitions.get(12).getKey());
        assertEquals("CD00930C-1659-415A-BE64-67A57FD8A1E9", fieldDefinitions.get(13).getKey());
        assertEquals("1019A528-481A-4E20-BF32-868C36B19ED0", fieldDefinitions.get(14).getKey());
        assertEquals("1EF2E901-DDD8-4C38-A5BF-858CB13F9562", fieldDefinitions.get(16).getKey());

        assertEquals("9A03833C-AC15-47C8-A037-1FFFD13A26E9", existingRecordset.getRecords().get(0).getKey());

    }

    @Test(expected = NullArgumentException.class)
    public void createNullParentTest()
    {
        @SuppressWarnings("unused")
        XsdRecordset recordset = XsdRecordset.create(null, "New Section");
    }

    @Test(expected = NullArgumentException.class)
    public void createNullNameTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdSection section = entity.getSection(CoalesceTypeInstances.TEST_MISSION_INFO_SECTION_PATH);

        @SuppressWarnings("unused")
        XsdRecordset recordset = XsdRecordset.create(section, null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void createEmptyNameTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdSection section = entity.getSection(CoalesceTypeInstances.TEST_MISSION_INFO_SECTION_PATH);

        @SuppressWarnings("unused")
        XsdRecordset recordset = XsdRecordset.create(section, "");

    }

    @Test(expected = IllegalArgumentException.class)
    public void createWhiteSpaceTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdSection section = entity.getSection(CoalesceTypeInstances.TEST_MISSION_INFO_SECTION_PATH);

        @SuppressWarnings("unused")
        XsdRecordset recordset = XsdRecordset.create(section, "   ");

    }

    @Test(expected = NullArgumentException.class)
    public void createNullBothTest()
    {

        @SuppressWarnings("unused")
        XsdRecordset recordset = XsdRecordset.create(null, null);

    }

    @Test
    public void createMinMaxRecordsTest()
    {
        XsdEntity entity = new XsdEntity();
        entity.initialize();
        XsdSection section = entity.createSection("New Section");

        XsdRecordset recordset = XsdRecordset.create(section, "New Recordset", 0, 5);

        assertNotNull(recordset);
        assertEquals(0, recordset.getMinRecords());
        assertEquals(5, recordset.getMaxRecords());

    }

    @Test
    public void createMinMaxRecordsRequiredRecordsTest()
    {
        XsdEntity entity = new XsdEntity();
        entity.initialize();
        XsdSection section = entity.createSection("New Section");

        XsdRecordset recordset = XsdRecordset.create(section, "New Recordset", 3, 5);

        assertNotNull(recordset);
        assertEquals(3, recordset.getMinRecords());
        assertEquals(5, recordset.getMaxRecords());

    }

    @Test
    public void createMinMaxRecordsZeroTest()
    {
        XsdEntity entity = new XsdEntity();
        entity.initialize();
        XsdSection section = entity.createSection("New Section");

        XsdRecordset recordset = XsdRecordset.create(section, "New Recordset", 0, 0);

        assertNotNull(recordset);
        assertEquals(0, recordset.getMinRecords());
        assertEquals(0, recordset.getMaxRecords());

    }

    @Test
    public void createMinMaxRecordsMinNegativeTest()
    {
        XsdEntity entity = new XsdEntity();
        entity.initialize();
        XsdSection section = entity.createSection("New Section");

        XsdRecordset recordset = XsdRecordset.create(section, "New Recordset", -1, 5);

        assertNull(recordset);

    }

    @Test
    public void createMinMaxRecordsMaxNegativeTest()
    {
        XsdEntity entity = new XsdEntity();
        entity.initialize();
        XsdSection section = entity.createSection("New Section");

        XsdRecordset recordset = XsdRecordset.create(section, "New Recordset", 0, -1);

        assertNull(recordset);

    }

    @Test
    public void createMinMaxRecordsMinGtMaxTest()
    {
        XsdEntity entity = new XsdEntity();
        entity.initialize();
        XsdSection section = entity.createSection("New Section");

        XsdRecordset recordset = XsdRecordset.create(section, "New Recordset", 10, 9);

        assertNull(recordset);

    }

    @Test(expected = NullArgumentException.class)
    public void initializeNullParentTest()
    {

        XsdRecordset recordset = new XsdRecordset();
        recordset.initialize(null, new Recordset());

    }

    @Test(expected = NullArgumentException.class)
    public void initialzeNullRecordsetTest()
    {
        XsdEntity entity = new XsdEntity();
        entity.initialize();
        XsdSection section = entity.createSection("New Section");

        XsdRecordset recordset = new XsdRecordset();
        recordset.initialize(section, null);

    }

    @Test(expected = NullArgumentException.class)
    public void initializeNullBothTest()
    {

        XsdRecordset recordset = new XsdRecordset();
        recordset.initialize(null, null);

    }

    @Test
    public void keyTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        assertEquals("7A158E39-B6C4-4912-A712-DF296375A368", recordset.getKey());

        UUID guid = UUID.randomUUID();

        recordset.setKey(guid);

        assertEquals(guid.toString(), recordset.getKey());

        UUID guid2 = UUID.randomUUID();

        recordset.setKey(guid2.toString());

        assertEquals(guid2.toString(), recordset.getKey());

    }

    @Test
    public void nameTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        assertEquals("Mission Information Recordset", recordset.getName());

        recordset.setName("New Information Recordset");

        assertEquals("New Information Recordset", recordset.getName());

    }

    @Test
    public void typeTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        assertEquals("recordset", recordset.getType());

        XsdEntity newEntity = XsdEntity.create("Operation", "Portal", "1.2.3.4", "ID", "Type");
        XsdSection newSection = XsdSection.create(newEntity, "Operation/New Section");
        XsdRecordset newRecordset = XsdRecordset.create(newSection, "New Recordset");

        assertEquals("recordset", newRecordset.getType());

    }

    @Test
    public void hasActiveRecordsNoRecoresTest()
    {
        XsdEntity entity = XsdEntity.create("");
        XsdSection section = XsdSection.create(entity, "Section");
        XsdRecordset recordset = XsdRecordset.create(section, "Recordset");

        assertFalse(recordset.getHasActiveRecords());

    }

    @Test
    public void hasActiveRecordsNoActiveTest()
    {
        XsdEntity entity = XsdEntity.create("");
        XsdSection section = XsdSection.create(entity, "Section");
        XsdRecordset recordset = XsdRecordset.create(section, "Recordset");

        XsdRecord record = recordset.addNew();
        record.setStatus(ECoalesceDataObjectStatus.DELETED);

        record = recordset.addNew();
        record.setStatus(ECoalesceDataObjectStatus.UNKNOWN);

        assertFalse(recordset.getHasActiveRecords());

        record.setStatus(ECoalesceDataObjectStatus.ACTIVE);

        assertTrue(recordset.getHasActiveRecords());

    }

    @Test
    public void hasActiveRecordsSomeActiveTest()
    {
        XsdEntity entity = XsdEntity.create("");
        XsdSection section = XsdSection.create(entity, "Section");
        XsdRecordset recordset = XsdRecordset.create(section, "Recordset");

        XsdRecord record = recordset.addNew();
        record.setStatus(ECoalesceDataObjectStatus.DELETED);

        record = recordset.addNew();
        record.setStatus(ECoalesceDataObjectStatus.UNKNOWN);

        record = recordset.addNew();
        record.setStatus(ECoalesceDataObjectStatus.ACTIVE);

        assertTrue(recordset.getHasActiveRecords());

        record.setStatus(ECoalesceDataObjectStatus.DELETED);

        assertFalse(recordset.getHasActiveRecords());

    }

    @Test
    public void hasRecordsTest()
    {
        XsdEntity entity = XsdEntity.create("");
        XsdSection section = XsdSection.create(entity, "Section");
        XsdRecordset recordset = XsdRecordset.create(section, "Recordset");

        assertFalse(recordset.getHasRecords());

        XsdRecord record = recordset.addNew();
        record.setStatus(ECoalesceDataObjectStatus.DELETED);
        record.setStatus(ECoalesceDataObjectStatus.UNKNOWN);
        assertTrue(recordset.getHasRecords());

    }

    @Test
    public void noIndexTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        assertFalse(recordset.getNoIndex());

        recordset.setNoIndex(true);

        String entityXml = entity.toXml();

        XsdEntity desEntity = XsdEntity.create(entityXml);
        XsdRecordset desRecordset = (XsdRecordset) desEntity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        assertTrue(desRecordset.getNoIndex());

        XsdEntity newEntity = XsdEntity.create("Operation", "Portal", "1.2.3.4", "ID", "Type");
        XsdSection newSection = XsdSection.create(newEntity, "Operation/New Section");
        XsdRecordset newRecordset = XsdRecordset.create(newSection, "New Recordset");

        assertFalse(newRecordset.getNoIndex());

    }

    @Test
    public void DateCreatedTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        assertEquals(JodaDateTimeHelper.fromXmlDateTimeUTC("2014-05-02T14:33:51.8525751Z"), recordset.getDateCreated());

        DateTime now = JodaDateTimeHelper.nowInUtc();
        recordset.setDateCreated(now);

        assertEquals(now, recordset.getDateCreated());

    }

    @Test
    public void LastModifiedTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        assertEquals(JodaDateTimeHelper.fromXmlDateTimeUTC("2014-05-02T14:33:59.193995Z"), recordset.getLastModified());

        DateTime now = JodaDateTimeHelper.nowInUtc();
        recordset.setLastModified(now);

        assertEquals(now, recordset.getLastModified());

    }

    @Test
    public void createFieldDefinitionFullNullNameTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        assertNull(recordset.createFieldDefinition(null, ECoalesceFieldDataTypes.StringType, "Label", "(U)", "Default"));

    }

    @Test
    public void createFieldDefinitionFullEmptyNameTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        assertNull(recordset.createFieldDefinition("", ECoalesceFieldDataTypes.StringType, "Label", "(U)", "Default"));

    }

    @Test
    public void createFieldDefinitionFullWhiteSpaceNameTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        assertNull(recordset.createFieldDefinition("   ", ECoalesceFieldDataTypes.StringType, "Label", "(U)", "Default"));

    }

    @Test
    public void createFieldDefinitionFullNullDataTypeTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        assertNull(recordset.createFieldDefinition("Field def", null, "Label", "(U)", "Default"));

    }

    @Test
    public void createFieldDefinitionFullNullLabelTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        assertNull(recordset.createFieldDefinition("Field def", ECoalesceFieldDataTypes.StringType, null, "(U)", "Default"));

    }

    @Test
    public void createFieldDefinitionFullEmptyLabelTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        assertNotNull(recordset.createFieldDefinition("Field def", ECoalesceFieldDataTypes.StringType, "", "(U)", "Default"));

    }

    @Test
    public void createFieldDefinitionFullWhiteSpaceLabelTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        assertNotNull(recordset.createFieldDefinition("Field def",
                                                      ECoalesceFieldDataTypes.StringType,
                                                      "   ",
                                                      "(U)",
                                                      "Default"));

    }

    @Test
    public void createFieldDefinitionFullNullDefaultClassTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        assertNull(recordset.createFieldDefinition("Field def", ECoalesceFieldDataTypes.StringType, "Label", null, "Default"));

    }

    @Test
    public void createFieldDefinitionFullEmptyDefaultClassTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        assertNotNull(recordset.createFieldDefinition("Field def",
                                                      ECoalesceFieldDataTypes.StringType,
                                                      "Label",
                                                      "",
                                                      "Default"));

    }

    @Test
    public void createFieldDefinitionFullWhiteSpaceDefaultClassTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        assertNotNull(recordset.createFieldDefinition("Field def",
                                                      ECoalesceFieldDataTypes.StringType,
                                                      "Label",
                                                      "   ",
                                                      "Default"));

    }

    @Test
    public void createFieldDefinitionFullNullDefaultValueTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        assertNull(recordset.createFieldDefinition("Field def", ECoalesceFieldDataTypes.StringType, "Label", "(U)", null));

    }

    @Test
    public void createFieldDefinitionFullEmptyDefaultValueTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        assertNotNull(recordset.createFieldDefinition("Field def", ECoalesceFieldDataTypes.StringType, "Label", "(U)", ""));

    }

    @Test
    public void createFieldDefinitionFullWhiteSpaceDefaultValueTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        assertNotNull(recordset.createFieldDefinition("Field def", ECoalesceFieldDataTypes.StringType, "Label", "(U)", "   "));

    }

    @Test
    public void getFieldDefinitionTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        assertEquals("93C6A209-AD86-4474-9FFB-D6801B2548AA", recordset.getFieldDefinition("ActionNumber").getKey());
        assertEquals("1EF2E901-DDD8-4C38-A5BF-858CB13F9562", recordset.getFieldDefinition("MissionAddress").getKey());

        assertNull(recordset.getFieldDefinition("Something"));

    }

    @Test
    public void getCountTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        assertEquals(1, recordset.getCount());

        recordset.addNew();

        assertEquals(2, recordset.getCount());

    }

    @Test
    public void containsTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        XsdRecord existingRecord = (XsdRecord) recordset.getDataObjectForNamePath("Mission Information Recordset/Mission Information Recordset Record");

        XsdRecord newRecord = recordset.addNew();

        assertTrue(recordset.contains(existingRecord));
        assertTrue(recordset.contains(newRecord));
        assertFalse(recordset.contains(new XsdRecord()));

    }

    @Test
    public void addNewTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        List<XsdRecord> records = recordset.getRecords();

        assertEquals(1, records.size());

        XsdRecord newRecord = recordset.addNew();

        assertEquals(2, records.size());
        assertEquals(recordset, newRecord.getParent());
        assertEquals(recordset.getName() + " Record", newRecord.getName());

    }

    @Test
    public void getItemTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        XsdRecord existingRecord = (XsdRecord) recordset.getDataObjectForNamePath("Mission Information Recordset/Mission Information Recordset Record");

        XsdRecord newRecord = recordset.addNew();

        assertEquals(existingRecord, recordset.GetItem(0));
        assertEquals(newRecord, recordset.GetItem(1));

    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getItemNegativeTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        recordset.GetItem(-1);

    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getItemGreaterTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        @SuppressWarnings("unused")
        XsdRecord newRecord = recordset.addNew();

        recordset.GetItem(2);

    }

    @Test
    public void removeAtTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        XsdRecord existingRecord = (XsdRecord) recordset.getDataObjectForNamePath("Mission Information Recordset/Mission Information Recordset Record");

        XsdRecord newRecord = recordset.addNew();

        assertTrue(recordset.contains(existingRecord));
        assertTrue(recordset.contains(newRecord));
        assertEquals(2, recordset.getCount());
        assertTrue(recordset.getHasActiveRecords());
        assertTrue(recordset.getHasRecords());

        recordset.RemoveAt(1);

        assertFalse(recordset.contains(newRecord));
        assertEquals(1, recordset.getCount());
        assertTrue(recordset.getHasActiveRecords());
        assertTrue(recordset.getHasRecords());

        recordset.RemoveAt(0);

        assertFalse(recordset.contains(existingRecord));
        assertEquals(0, recordset.getCount());
        assertFalse(recordset.getHasActiveRecords());
        assertTrue(recordset.getHasRecords());

    }

    @Test
    public void removeTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        XsdRecord existingRecord = (XsdRecord) recordset.getDataObjectForNamePath("Mission Information Recordset/Mission Information Recordset Record");

        XsdRecord newRecord = recordset.addNew();

        assertTrue(recordset.contains(existingRecord));
        assertTrue(recordset.contains(newRecord));
        assertEquals(2, recordset.getCount());
        assertTrue(recordset.getHasActiveRecords());
        assertTrue(recordset.getHasRecords());

        recordset.Remove(newRecord.getKey());

        assertFalse(recordset.contains(newRecord));
        assertEquals(1, recordset.getCount());
        assertTrue(recordset.getHasActiveRecords());
        assertTrue(recordset.getHasRecords());

        recordset.Remove(existingRecord.getKey());

        assertFalse(recordset.contains(existingRecord));
        assertEquals(0, recordset.getCount());
        assertFalse(recordset.getHasActiveRecords());
        assertTrue(recordset.getHasRecords());

    }

    @Test
    public void changeRecordStatusTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
        XsdRecordset recordset = (XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        XsdRecord existingRecord = (XsdRecord) recordset.getDataObjectForNamePath("Mission Information Recordset/Mission Information Recordset Record");

        XsdRecord newRecord = recordset.addNew();

        assertTrue(recordset.getHasActiveRecords());
        assertTrue(recordset.getHasRecords());
        assertEquals(2, recordset.getRecords().size());

        existingRecord.setStatus(ECoalesceDataObjectStatus.DELETED);

        assertTrue(recordset.getHasActiveRecords());
        assertTrue(recordset.getHasRecords());
        assertEquals(1, recordset.getRecords().size());

        newRecord.setStatus(ECoalesceDataObjectStatus.UNKNOWN);

        assertFalse(recordset.getHasActiveRecords());
        assertTrue(recordset.getHasRecords());
        assertEquals(0, recordset.getRecords().size());

        existingRecord.setStatus(ECoalesceDataObjectStatus.ACTIVE);

        assertTrue(recordset.getHasActiveRecords());
        assertTrue(recordset.getHasRecords());
        assertEquals(1, recordset.getRecords().size());

    }
}
