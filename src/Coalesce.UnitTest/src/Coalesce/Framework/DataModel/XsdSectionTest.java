package Coalesce.Framework.DataModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.NullArgumentException;
import org.joda.time.DateTime;
import org.junit.Test;

import Coalesce.Common.Helpers.GUIDHelper;
import Coalesce.Common.Helpers.JodaDateTimeHelper;
import Coalesce.Common.UnitTest.CoalesceTypeInstances;
import Coalesce.Framework.GeneratedJAXB.Entity.Section;

public class XsdSectionTest {

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
    public void createSectionTest()
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Create Live Status Section
        assertNull(entity.getSection("TREXOperation/Live Status Section"));

        CoalesceSection liveSection = CoalesceSection.create(entity, "Live Status Section");
        assertNotNull(GUIDHelper.isValid(liveSection.getKey()));
        assertEquals(liveSection, entity.getSection("TREXOperation/Live Status Section"));
        assertFalse(liveSection.getNoIndex());
    }

    @Test
    public void createSectionFromXmlTest()
    {

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertNull(entity.getSection("TREXMission/A New Section"));

        CoalesceSection newSection = CoalesceSection.create(entity, "A New Section");

        assertNotNull(newSection);
        assertNotNull(GUIDHelper.isValid(newSection.getKey()));
        assertEquals(newSection, entity.getSection("TREXMission/A New Section"));
        assertFalse(newSection.getNoIndex());

    }

    @Test
    public void createSectionFromXmlExistingTest()
    {

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceSection liveSection = entity.getSection(CoalesceTypeInstances.TEST_MISSION_LIVE_SECTION_PATH);
        CoalesceSection informationSection = entity.getSection(CoalesceTypeInstances.TEST_MISSION_INFO_SECTION_PATH);

        assertNotNull(liveSection);
        assertNotNull(informationSection);

        CoalesceSection createdLiveSection = CoalesceSection.create(entity, "Live Status Section");
        CoalesceSection createdInformationSection = CoalesceSection.create(entity, "Mission Information Section");

        assertEquals(liveSection, createdLiveSection);
        assertEquals(informationSection, createdInformationSection);

    }

    @Test(expected = NullArgumentException.class)
    public void createSectionNullParentTest()
    {
        @SuppressWarnings("unused")
        CoalesceSection liveSection = CoalesceSection.create(null, "Live Status Section");
    }

    @Test(expected = NullArgumentException.class)
    public void createSectionNullNameTest()
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        @SuppressWarnings("unused")
        CoalesceSection section = CoalesceSection.create(entity, null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void createSectionEmptyNameTest()
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        @SuppressWarnings("unused")
        CoalesceSection section = CoalesceSection.create(entity, "");

    }

    @Test(expected = IllegalArgumentException.class)
    public void createSectionWhiteSpaceNameTest()
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        @SuppressWarnings("unused")
        CoalesceSection section = CoalesceSection.create(entity, "  ");

    }

    @Test(expected = NullArgumentException.class)
    public void createSectionNullBothTest()
    {
        @SuppressWarnings("unused")
        CoalesceSection section = CoalesceSection.create(null, null);
    }

    @Test
    public void createSectionExistingNoIndexTrueTest()
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        CoalesceSection liveSection = CoalesceSection.create(entity, "Live Status Section", true);
        assertEquals(liveSection, entity.getSection("TREXOperation/Live Status Section"));
        assertTrue(liveSection.getNoIndex());

        CoalesceSection liveSection2 = CoalesceSection.create(entity, "Live Status Section");
        assertEquals(liveSection2, entity.getSection("TREXOperation/Live Status Section"));
        assertEquals(liveSection, liveSection2);
        assertFalse(liveSection.getNoIndex());
        assertFalse(liveSection2.getNoIndex());

    }

    @Test
    public void createSectionWithNoIndexFalseTest()
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        CoalesceSection liveSection = CoalesceSection.create(entity, "Live Status Section", false);
        assertEquals(liveSection, entity.getSection("TREXOperation/Live Status Section"));
        assertFalse(liveSection.getNoIndex());
    }

    @Test
    public void createSectionWithNoIndexTrueTest()
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        CoalesceSection liveSection = CoalesceSection.create(entity, "Live Status Section", true);
        assertEquals(liveSection, entity.getSection("TREXOperation/Live Status Section"));
        assertTrue(liveSection.getNoIndex());

    }

    @Test
    public void createSectionWithNoIndexTrueForExistingNoIndexFalseTest()
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        CoalesceSection liveSection = CoalesceSection.create(entity, "Live Status Section", false);
        assertEquals(liveSection, entity.getSection("TREXOperation/Live Status Section"));
        assertFalse(liveSection.getNoIndex());

        CoalesceSection liveSection2 = CoalesceSection.create(entity, "Live Status Section", true);
        assertEquals(liveSection2, entity.getSection("TREXOperation/Live Status Section"));
        assertEquals(liveSection, liveSection2);
        assertTrue(liveSection.getNoIndex());
        assertTrue(liveSection2.getNoIndex());

    }

    @Test
    public void createSectionWithNoIndexTrueFromXmlTest()
    {

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertNull(entity.getSection("TREXMission/A New Section"));

        CoalesceSection newSection = CoalesceSection.create(entity, "A New Section", true);

        assertNotNull(newSection);
        assertEquals(newSection, entity.getSection("TREXMission/A New Section"));
        assertTrue(newSection.getNoIndex());

        String entityXml = entity.toXml();

        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);

        CoalesceSection desSection = desEntity.getSection("TREXMission/A New Section");
        assertEquals(newSection.getKey(), desSection.getKey());
        assertEquals(newSection.getName(), desSection.getName());
        assertEquals(newSection.getNoIndex(), desSection.getNoIndex());
    }

    @Test(expected = NullArgumentException.class)
    public void createSectionNoIndexFalseNullParentTest()
    {
        @SuppressWarnings("unused")
        CoalesceSection liveSection = CoalesceSection.create(null, "Live Status Section", false);
    }

    @Test(expected = NullArgumentException.class)
    public void createSectionNoIndexTrueNullParentTest()
    {
        @SuppressWarnings("unused")
        CoalesceSection liveSection = CoalesceSection.create(null, "Live Status Section", true);
    }

    @Test(expected = NullArgumentException.class)
    public void createSectionNoIndexFalseNullNameTest()
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        @SuppressWarnings("unused")
        CoalesceSection section = CoalesceSection.create(entity, null, false);

    }

    @Test(expected = NullArgumentException.class)
    public void createSectionNoIndexTrueNullNameTest()
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        @SuppressWarnings("unused")
        CoalesceSection section = CoalesceSection.create(entity, null, true);

    }

    @Test(expected = NullArgumentException.class)
    public void createSectionIndexFalseNullTest()
    {
        @SuppressWarnings("unused")
        CoalesceSection section = CoalesceSection.create(null, null, false);
    }

    @Test(expected = NullArgumentException.class)
    public void createSectionIndexTrueNullTest()
    {
        @SuppressWarnings("unused")
        CoalesceSection section = CoalesceSection.create(null, null, true);
    }

    @Test(expected = NullArgumentException.class)
    public void initializeNullParent()
    {
        CoalesceSection section = new CoalesceSection();

        section.initialize(null, new Section());

    }

    @Test(expected = NullArgumentException.class)
    public void initializeNullSection()
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        CoalesceSection section = new CoalesceSection();

        section.initialize(entity, null);

    }

    @Test(expected = NullArgumentException.class)
    public void initializeNullBoth()
    {
        CoalesceSection section = new CoalesceSection();

        section.initialize(null, null);

    }

    @Test
    public void keyTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceSection liveSection = entity.getSection(CoalesceTypeInstances.TEST_MISSION_LIVE_SECTION_PATH);

        assertEquals("85CB4256-4CC2-4F96-A03D-5EF880989822", liveSection.getKey());

        UUID guid = UUID.randomUUID();

        liveSection.setKey(guid);

        assertEquals(guid.toString(), liveSection.getKey());

        UUID guid2 = UUID.randomUUID();

        liveSection.setKey(guid2.toString());

        assertEquals(guid2.toString(), liveSection.getKey());

    }

    @Test
    public void nameTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceSection liveSection = entity.getSection(CoalesceTypeInstances.TEST_MISSION_LIVE_SECTION_PATH);

        assertEquals("Live Status Section", liveSection.getName());

        liveSection.setName("New Section Name");

        assertEquals("New Section Name", liveSection.getName());

    }

    @Test
    public void typeTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceSection liveSection = entity.getSection(CoalesceTypeInstances.TEST_MISSION_LIVE_SECTION_PATH);

        assertEquals("section", liveSection.getType());

        CoalesceEntity newEntity = CoalesceEntity.create("Operation", "Portal", "1.2.3.4", "ID", "Type");
        CoalesceSection newSection = CoalesceSection.create(newEntity, "Operation/New Section");

        assertEquals("section", newSection.getType());

    }

    @Test
    public void createRecordSetTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceSection informationSection = entity.getSection(CoalesceTypeInstances.TEST_MISSION_INFO_SECTION_PATH);

        CoalesceRecordset recordset = informationSection.getRecordset("Mission New Recordset");

        assertNull(recordset);

        CoalesceRecordset newRecordset = informationSection.createRecordset("Mission New Recordset");

        assertNotNull(newRecordset);
        assertEquals(informationSection, newRecordset.getParent());
        assertEquals("Mission New Recordset", newRecordset.getName());

    }

    @Test
    public void createRecordSetAlreadyExists()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceSection informationSection = entity.getSection(CoalesceTypeInstances.TEST_MISSION_INFO_SECTION_PATH);

        CoalesceRecordset recordSet = informationSection.getRecordset("Mission Information Section/Mission Information Recordset");

        assertNotNull(recordSet);

        CoalesceRecordset dublicateRecordset = informationSection.createRecordset("Mission Information Recordset");

        assertEquals(recordSet, dublicateRecordset);

    }

    @Test(expected = NullArgumentException.class)
    public void createRecordsetNullNameTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceSection informationSection = entity.getSection(CoalesceTypeInstances.TEST_MISSION_INFO_SECTION_PATH);

        @SuppressWarnings("unused")
        CoalesceRecordset recordset = informationSection.createRecordset(null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void createRecordsetEmptyNameTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceSection informationSection = entity.getSection(CoalesceTypeInstances.TEST_MISSION_INFO_SECTION_PATH);

        CoalesceRecordset recordset = informationSection.createRecordset("");

        assertNull(recordset);

    }

    @Test(expected = IllegalArgumentException.class)
    public void createRecordsetWhiteSpaceNameTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceSection informationSection = entity.getSection(CoalesceTypeInstances.TEST_MISSION_INFO_SECTION_PATH);

        CoalesceRecordset recordset = informationSection.createRecordset("   ");

        assertNull(recordset);

    }

    @Test
    public void getRecordsetNullTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Test Portal", "1.2.3.5", "ID", "Type");
        CoalesceSection section = CoalesceSection.create(entity, "New Section");
        section.createRecordset("Recordset 1");
        section.createRecordset("Recordset 2");

        CoalesceRecordset recordsection = section.getRecordset(null);

        assertNull(recordsection);

    }

    @Test
    public void getRecordsetEmptyTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Test Portal", "1.2.3.5", "ID", "Type");
        CoalesceSection section = CoalesceSection.create(entity, "New Section");
        section.createRecordset("Recordset 1");
        section.createRecordset("Recordset 2");

        CoalesceRecordset recordsection = section.getRecordset("");

        assertNull(recordsection);

    }

    @Test
    public void getRecordsetTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Test Portal", "1.2.3.5", "ID", "Type");
        CoalesceSection section = CoalesceSection.create(entity, "New Section");
        CoalesceRecordset recordset1 = section.createRecordset("Recordset 1");
        CoalesceRecordset recordset2 = section.createRecordset("Recordset 2");

        assertEquals(recordset1, section.getRecordset("New Section/Recordset 1"));
        assertEquals(recordset2, section.getRecordset("New Section/Recordset 2"));

    }

    @Test
    public void getRecordsetsOneFromXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceSection informationSection = entity.getSection(CoalesceTypeInstances.TEST_MISSION_INFO_SECTION_PATH);

        Map<String, CoalesceRecordset> recordsets = informationSection.getRecordsets();

        assertEquals(1, recordsets.size());
        assertNotNull(recordsets.get("7A158E39-B6C4-4912-A712-DF296375A368"));
        assertEquals("Mission Information Recordset", recordsets.get("7A158E39-B6C4-4912-A712-DF296375A368").getName());

    }

    @Test
    public void getRecordsetsMultipleTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Test Portal", "1.2.3.5", "ID", "Type");
        CoalesceSection section = CoalesceSection.create(entity, "New Section");
        CoalesceRecordset recordset1 = section.createRecordset("Recordset 1");
        CoalesceRecordset recordset2 = section.createRecordset("Recordset 2");

        Map<String, CoalesceRecordset> recordsets = section.getRecordsets();

        assertEquals(2, recordsets.size());
        assertEquals(recordset1, recordsets.get(recordset1.getKey()));
        assertEquals(recordset2, recordsets.get(recordset2.getKey()));

    }

    @Test
    public void noIndexTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceSection liveSection = entity.getSection(CoalesceTypeInstances.TEST_MISSION_LIVE_SECTION_PATH);

        assertTrue(liveSection.getNoIndex());

        liveSection.setNoIndex(false);

        String entityXml = entity.toXml();

        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceSection desLiveSection = desEntity.getSection(CoalesceTypeInstances.TEST_MISSION_LIVE_SECTION_PATH);

        assertFalse(desLiveSection.getNoIndex());

        CoalesceSection informationSection = entity.getSection(CoalesceTypeInstances.TEST_MISSION_INFO_SECTION_PATH);

        assertFalse(informationSection.getNoIndex());

        CoalesceEntity newEntity = CoalesceEntity.create("Operation", "Portal", "1.2.3.4", "ID", "Type");
        CoalesceSection newSection = CoalesceSection.create(newEntity, "Operation/New Section");

        assertFalse(newSection.getNoIndex());

    }

    @Test
    public void DateCreatedTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceSection liveSection = entity.getSection(CoalesceTypeInstances.TEST_MISSION_LIVE_SECTION_PATH);

        assertEquals(JodaDateTimeHelper.fromXmlDateTimeUTC("2014-05-02T14:33:51.851575Z"), liveSection.getDateCreated());

        DateTime now = JodaDateTimeHelper.nowInUtc();
        liveSection.setDateCreated(now);

        assertEquals(now, liveSection.getDateCreated());

    }

    @Test
    public void LastModifiedTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceSection liveSection = entity.getSection(CoalesceTypeInstances.TEST_MISSION_LIVE_SECTION_PATH);

        assertEquals(JodaDateTimeHelper.fromXmlDateTimeUTC("2014-05-02T14:33:59.1309914Z"), liveSection.getLastModified());

        DateTime now = JodaDateTimeHelper.nowInUtc();
        liveSection.setLastModified(now);

        assertEquals(now, liveSection.getLastModified());

    }

}
