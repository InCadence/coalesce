package com.incadencecorp.coalesce.framework.datamodel;

import com.incadencecorp.coalesce.api.ICoalesceFieldDefinitionFactory;
import com.incadencecorp.coalesce.common.CoalesceTypeInstances;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;
import org.apache.commons.lang.NullArgumentException;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

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

public class CoalesceRecordTest {

    /*
     * @BeforeClass public static void setUpBeforeClass() throws Exception { }
     * 
     * @AfterClass public static void tearDownAfterClass() throws Exception { }
     * 
     * @Before public void setUp() throws Exception { }
     * 
     * @After public void tearDown() throws Exception { }
     */

    @Test(expected = NullArgumentException.class)
    public void createNullParentTest()
    {
        @SuppressWarnings("unused")
        CoalesceRecord record = CoalesceRecord.create(null, "New Record");

    }

    @Test(expected = NullArgumentException.class)
    public void createNullNameTest()
    {
        CoalesceRecordset recordset = getMissionRecordset();

        @SuppressWarnings("unused")
        CoalesceRecord record = CoalesceRecord.create(recordset, null);
    }

    @Test(expected = NullArgumentException.class)
    public void createNullBothTest()
    {
        @SuppressWarnings("unused")
        CoalesceRecord record = CoalesceRecord.create(null, null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void createEmptyNameTest()
    {
        CoalesceRecordset recordset = getMissionRecordset();

        @SuppressWarnings("unused")
        CoalesceRecord record = CoalesceRecord.create(recordset, "");

    }

    @Test(expected = IllegalArgumentException.class)
    public void createWhiteSpaceNameTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceRecordset recordset = (CoalesceRecordset) entity.getCoalesceObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        @SuppressWarnings("unused")
        CoalesceRecord record = CoalesceRecord.create(recordset, "   ");

    }

    @Test
    public void createFromXmlTest()
    {
        CoalesceRecordset recordset = getMissionRecordset();

        CoalesceRecord record = recordset.getRecords().get(0);

        assertEquals(17, record.getFields().size());
        assertEquals("D7067C3F-54B1-47FD-9C8A-A2D7946E0C2A", record.getFieldByName("ActionNumber").getKey());
        assertEquals("6CB12648-A061-4CC5-B593-3D0407EF4392", record.getFieldByName("MissionName").getKey());
        assertEquals("37496274-2077-454F-9CB3-5A57C1753640", record.getFieldByName("MissionAddress").getKey());

    }

    @Test
    public void createNewRecordTest()
    {
        CoalesceRecordset recordset = getMissionRecordset();

        CoalesceRecord newRecord = CoalesceRecord.create(recordset, "New Record");

        assertNotNull(newRecord);
        assertTrue(recordset.getChildCoalesceObjects().containsValue(newRecord));
        assertEquals(recordset, newRecord.getParent());
        assertEquals("New Record", newRecord.getName());
        assertEquals(17, newRecord.getFields().size());

        for (CoalesceField<?> field : newRecord.getFields())
        {
            CoalesceFieldDefinition fd = recordset.getFieldDefinition(field.getName());

            assertNotNull(fd);
            CoalesceFieldDefinitionTest.assertNewField(fd, field);
        }

    }

    @Test
    public void createExistingNameTest()
    {
        CoalesceRecordset recordset = getMissionRecordset();

        CoalesceRecord existingRecord = recordset.getItem(0);

        CoalesceRecord newRecord = CoalesceRecord.create(recordset, "New Record");

        assertFalse(existingRecord == newRecord);

    }

    @Test(expected = NullArgumentException.class)
    public void initializeNullParentTest()
    {
        CoalesceRecord record = new CoalesceRecord();
        record.initialize(null, new Record());

    }

    @Test(expected = NullArgumentException.class)
    public void initializeNullRecordTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceRecordset recordset = (CoalesceRecordset) entity.getCoalesceObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);

        CoalesceRecord record = new CoalesceRecord();
        record.initialize(recordset, null);
    }

    @Test(expected = NullArgumentException.class)
    public void initializeNullBothTest()
    {
        @SuppressWarnings("unused")
        CoalesceRecord record = CoalesceRecord.create(null, null);

    }

    @Test
    public void initializeNewRecordTest()
    {
        CoalesceRecordset recordset = getMissionRecordset();

        CoalesceRecord newRecord = new CoalesceRecord();
        Record newEntityRecord = new Record();
        newRecord.initialize(recordset, newEntityRecord);

        assertTrue(recordset.getChildCoalesceObjects().containsValue(newRecord));
        assertEquals(recordset, newRecord.getParent());
        assertEquals(newEntityRecord.getKey(), newRecord.getKey());
        assertEquals("", newRecord.getName());
        assertEquals(0, newRecord.getFields().size());

    }

    @Test
    public void keyTest()
    {
        CoalesceRecord record = getMissionRecord();

        assertEquals("9A03833C-AC15-47C8-A037-1FFFD13A26E9", record.getKey());

        UUID guid2 = UUID.randomUUID();

        record.setKey(guid2.toString());

        assertEquals(guid2.toString(), record.getKey());

    }

    @Test
    public void nameTest()
    {
        CoalesceRecord record = getMissionRecord();

        assertEquals("Mission Information Recordset Record", record.getName());

        record.setName("New Information Recordset Record");

        assertEquals("New Information Recordset Record", record.getName());

    }

    @Test
    public void typeTest()
    {
        CoalesceRecord record = getMissionRecord();

        assertEquals("record", record.getType());

        CoalesceEntity newEntity = CoalesceEntity.create("Operation", "Portal", "1.2.3.4", "ID", "Type");
        CoalesceSection newSection = CoalesceSection.create(newEntity, "Operation/New Section");
        CoalesceRecordset newRecordset = CoalesceRecordset.create(newSection, "New Recordset");
        CoalesceRecord newRecord = newRecordset.addNew();

        assertEquals("record", newRecord.getType());

    }

    @Test
    public void getFieldsTest()
    {
        CoalesceRecord record = getMissionRecord();

        List<CoalesceField> fields = record.getFields();

        assertEquals(17, fields.size());

        boolean firstFound = false;
        boolean middleFound = false;
        boolean lastFound = false;

        for (CoalesceField field : fields)
        {
            switch (field.getKey())
            {
            case "D7067C3F-54B1-47FD-9C8A-A2D7946E0C2A":

                firstFound = true;

                break;

            case "C6695025-F302-4C28-A428-EF5AE54C3CFC":

                middleFound = true;

                break;

            case "37496274-2077-454F-9CB3-5A57C1753640":

                lastFound = true;

                break;

            }
        }

        assertTrue(firstFound);
        assertTrue(middleFound);
        assertTrue(lastFound);

    }

    @Test
    public void getFieldNamesTest()
    {
        CoalesceRecord record = getMissionRecord();

        List<String> fieldNames = record.getFieldNames();

        assertEquals(17, fieldNames.size());

        boolean firstFound = false;
        boolean middleFound = false;
        boolean lastFound = false;

        for (String fieldName : fieldNames)
        {
            switch (fieldName)
            {
            case "ActionNumber":

                firstFound = true;

                break;

            case "MissionType":

                middleFound = true;

                break;

            case "MissionAddress":

                lastFound = true;

                break;

            }
        }

        assertTrue(firstFound);
        assertTrue(middleFound);
        assertTrue(lastFound);

    }

    @Test
    public void getFieldKeysTest()
    {
        CoalesceRecord record = getMissionRecord();

        List<String> fieldKeys = record.getFieldKeys();

        assertEquals(17, fieldKeys.size());

        boolean firstFound = false;
        boolean middleFound = false;
        boolean lastFound = false;

        for (String fieldKey : fieldKeys)
        {
            switch (fieldKey)
            {
            case "D7067C3F-54B1-47FD-9C8A-A2D7946E0C2A":

                firstFound = true;

                break;

            case "C6695025-F302-4C28-A428-EF5AE54C3CFC":

                middleFound = true;

                break;

            case "37496274-2077-454F-9CB3-5A57C1753640":

                lastFound = true;

                break;

            }
        }

        assertTrue(firstFound);
        assertTrue(middleFound);
        assertTrue(lastFound);

    }

    @Test
    public void getFieldByKeyTest()
    {
        CoalesceRecord record = getMissionRecord();

        CoalesceField<?> field = record.getFieldByKey("D7067C3F-54B1-47FD-9C8A-A2D7946E0C2A");
        assertEquals("D7067C3F-54B1-47FD-9C8A-A2D7946E0C2A", field.getKey());

        field = null;
        field = record.getFieldByKey("C6695025-F302-4C28-A428-EF5AE54C3CFC");
        assertEquals("C6695025-F302-4C28-A428-EF5AE54C3CFC", field.getKey());

        field = null;
        field = record.getFieldByKey("37496274-2077-454F-9CB3-5A57C1753640");
        assertEquals("37496274-2077-454F-9CB3-5A57C1753640", field.getKey());

        field = null;
        field = record.getFieldByKey("B0101194-B600-4FE6-BBB8-019300C812DC");
        assertNull(field);

    }

    @Test
    public void hasFieldTest()
    {
        CoalesceRecord record = getMissionRecord();

        assertTrue(record.hasField("MissionName"));
        assertTrue(record.hasField("Missionname"));
        assertFalse(record.hasField("Invalid"));
    }

    @Test
    public void noIndexTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecordset recordset = getMissionRecordset(entity);

        CoalesceRecord record = recordset.addNew();
        record.setName("New Record");

        Assert.assertEquals(CoalesceObject.ATTRIBUTE_NOINDEX_DEFAULT, record.isNoIndex());

        record.setNoIndex(true);

        String entityXml = entity.toXml();

        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceRecord desRecord = (CoalesceRecord) desEntity.getCoalesceObjectForNamePath(
                CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH + "/New Record");

        assertTrue(desRecord.isNoIndex());

        CoalesceEntity newEntity = CoalesceEntity.create("Operation", "Portal", "1.2.3.4", "ID", "Type");
        CoalesceSection newSection = CoalesceSection.create(newEntity, "Operation/New Section");
        CoalesceRecordset newRecordset = CoalesceRecordset.create(newSection, "New Recordset");
        CoalesceRecord newRecord = newRecordset.addNew();

        Assert.assertEquals(CoalesceObject.ATTRIBUTE_NOINDEX_DEFAULT, newRecord.isNoIndex());

    }

    @Test
    public void dateCreatedTest()
    {
        CoalesceRecord record = getMissionRecord();

        assertEquals(JodaDateTimeHelper.fromXmlDateTimeUTC("2014-05-02T14:33:51.8595755Z"), record.getDateCreated());

        DateTime now = JodaDateTimeHelper.nowInUtc();
        record.setDateCreated(now);

        assertEquals(now, record.getDateCreated());

    }

    @Test
    public void lastModifiedTest()
    {
        CoalesceRecord record = getMissionRecord();

        assertEquals(JodaDateTimeHelper.fromXmlDateTimeUTC("2014-05-02T14:33:59.193995Z"), record.getLastModified());

        DateTime now = JodaDateTimeHelper.nowInUtc();
        record.setLastModified(now);

        assertEquals(now, record.getLastModified());

    }

    @Test
    public void toXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecord record = (CoalesceRecord) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record");
        String recordXml = record.toXml();

        Record desRecord = (Record) XmlHelper.deserialize(recordXml, Record.class);

        assertEquals(record.getFields().size(), desRecord.getField().size());
        assertEquals(record.getKey(), desRecord.getKey());
        assertEquals(record.getName(), desRecord.getName());
        assertEquals(record.getDateCreated(), desRecord.getDatecreated());
        assertEquals(record.getLastModified(), desRecord.getLastmodified());
        assertEquals(record.getStatus(), desRecord.getStatus());

    }

    @Test
    public void setStatusTest()
    {
        TestEntity entity = new TestEntity();
        entity.initialize();
        TestRecord record = entity.addRecord1();

        assertEquals(ECoalesceObjectStatus.ACTIVE, record.getStatus());

        record.setStatus(ECoalesceObjectStatus.UNKNOWN);

        assertEquals(ECoalesceObjectStatus.UNKNOWN, record.getStatus());

        String recordXml = record.toXml();

        Record desRecord = (Record) XmlHelper.deserialize(recordXml, Record.class);

        assertEquals(ECoalesceObjectStatus.UNKNOWN, desRecord.getStatus());

    }

    @Test
    public void setStatusNullTest()
    {
        TestEntity entity = new TestEntity();
        entity.initialize();
        TestRecord record = entity.addRecord1();

        assertEquals(ECoalesceObjectStatus.ACTIVE, record.getStatus());

        record.setStatus(null);

        assertEquals(ECoalesceObjectStatus.ACTIVE, record.getStatus());
    }

    @Test
    public void attributeTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceRecord record = (CoalesceRecord) entity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record");

        int before = record.getAttributes().size();

        record.setAttribute("TestAttribute", "TestingValue");

        assertEquals(before + 1, record.getAttributes().size());

        assertEquals("TestingValue", record.getAttribute("TestAttribute"));

        assertEquals("Mission Information Recordset Record", record.getName());
        assertEquals(CoalesceObject.ATTRIBUTE_NOINDEX_DEFAULT, record.isNoIndex());

        record.setAttribute("Name", "TestingName");
        assertEquals("TestingName", record.getName());
        assertEquals("TestingName", record.getAttribute("Name"));

        UUID guid = UUID.randomUUID();
        record.setAttribute("Key", guid.toString());
        assertEquals(guid.toString(), record.getKey());
        assertEquals(guid.toString(), record.getAttribute("Key"));

        DateTime now = JodaDateTimeHelper.nowInUtc();
        DateTime future = now.plusDays(2);

        record.setAttribute("DateCreated", JodaDateTimeHelper.toXmlDateTimeUTC(now));
        assertEquals(now, record.getDateCreated());

        record.setAttribute("NoIndex", "True");
        assertEquals(true, record.isNoIndex());

        record.setAttribute("Status", ECoalesceObjectStatus.UNKNOWN.toString());
        assertEquals(ECoalesceObjectStatus.UNKNOWN, record.getStatus());

        record.setStatus(ECoalesceObjectStatus.ACTIVE);

        record.setAttribute("LastModified", JodaDateTimeHelper.toXmlDateTimeUTC(future));
        assertEquals(future, record.getLastModified());

        String entityXml = entity.toXml();
        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceRecord desRecord = (CoalesceRecord) desEntity.getCoalesceObjectForNamePath(
                "TREXMission/Mission Information Section/Mission Information Recordset/TestingName");

        assertEquals("TestingValue", desRecord.getAttribute("TestAttribute"));
        assertEquals("TestingName", desRecord.getName());
        assertEquals(guid.toString(), desRecord.getKey());
        assertEquals(now, desRecord.getDateCreated());
        assertEquals(future, desRecord.getLastModified());
        assertEquals(true, desRecord.isNoIndex());
        assertEquals(ECoalesceObjectStatus.ACTIVE, desRecord.getStatus());

    }

    /**
     * This test ensures that
     * {@link CoalesceRecord#getFieldByName(String, ICoalesceFieldDefinitionFactory)}
     * will create the field along with its definition using the provided
     * factory if not found.
     *
     * @throws Exception
     */
    @Test
    public void testMissingField() throws Exception
    {
        final String[] fields = new String[] { "field1", "field2", "field3"
        };

        final ICoalesceFieldDefinitionFactory factory = new ICoalesceFieldDefinitionFactory() {

            @Override
            public CoalesceFieldDefinition create(CoalesceRecordset recordset, String name)
            {
                CoalesceFieldDefinition fd = null;

                switch (name)
                {
                case "field1":
                    fd = CoalesceFieldDefinition.create(recordset, name, ECoalesceFieldDataTypes.STRING_TYPE);
                    break;
                case "field2":
                    fd = CoalesceFieldDefinition.create(recordset, name, ECoalesceFieldDataTypes.STRING_TYPE);
                    break;

                }

                return fd;
            }

        };

        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();

        CoalesceRecordset rs = CoalesceRecordset.create(CoalesceSection.create(entity, "test"), "recordset");

        CoalesceFieldDefinition.create(rs, fields[0], ECoalesceFieldDataTypes.STRING_TYPE);

        CoalesceRecord record = rs.addNew();

        Assert.assertNotNull(record.getFieldByName(fields[0]));
        Assert.assertNull(record.getFieldByName(fields[1]));
        Assert.assertNull(record.getFieldByName(fields[2]));

        Assert.assertNotNull(rs.getFieldDefinition(fields[0]));
        Assert.assertNull(rs.getFieldDefinition(fields[1]));
        Assert.assertNull(rs.getFieldDefinition(fields[2]));

        Assert.assertNotNull(record.getFieldByName(fields[0]));
        Assert.assertNotNull(record.getFieldByName(fields[1], factory));
        Assert.assertNull(record.getFieldByName(fields[2], factory));

        Assert.assertNotNull(rs.getFieldDefinition(fields[0]));
        Assert.assertNotNull(rs.getFieldDefinition(fields[1]));
        Assert.assertNull(rs.getFieldDefinition(fields[2]));
    }

    // -----------------------------------------------------------------------//
    // Private Methods
    // -----------------------------------------------------------------------//

    private CoalesceRecordset getMissionRecordset()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        return getMissionRecordset(entity);

    }

    private CoalesceRecordset getMissionRecordset(CoalesceEntity entity)
    {
        return (CoalesceRecordset) entity.getCoalesceObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH);
    }

    private CoalesceRecord getMissionRecord()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        return (CoalesceRecord) entity.getCoalesceObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORD_PATH);

    }
}
