package com.incadencecorp.coalesce.framework.datamodel;

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

import com.incadencecorp.coalesce.common.CoalesceTypeInstances;
import com.incadencecorp.coalesce.common.helpers.GUIDHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;

public class CoalesceSectionTest {

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
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation", "TREX Portal", "1.0.0.0", "", "", "");

        // Verify Sections Don't Exists
        assertNull(entity.getSection("TREXOperation/section 1/section 1.1"));
        assertNull(entity.getSection("TREXOperation/section 1/section 1.1/section 1.1.1"));
        assertNull(entity.getSection("TREXOperation/section 1/section 1.1/section 1.1.2"));
        assertNull(entity.getSection("TREXOperation/section 1"));

        // Create First Section
        CoalesceSection section1 = CoalesceSection.create(entity, "section 1");

        // Verify Section Creation
        assertNotNull(GUIDHelper.isValid(section1.getKey()));
        assertEquals(section1, entity.getSection("TREXOperation/section 1"));
        assertFalse(section1.getNoIndex());

        // Create Nested Sections
        CoalesceSection section1_1 = CoalesceSection.create(section1, "section 1.1");
        CoalesceSection section1_1_1 = CoalesceSection.create(section1_1, "section 1.1.1");
        CoalesceSection section1_1_2 = CoalesceSection.create(section1_1, "section 1.1.2");

        // Verify Nested Section Creations
        assertEquals(section1_1, entity.getSection("TREXOperation/section 1/section 1.1"));
        assertEquals(section1_1_1, entity.getSection("TREXOperation/section 1/section 1.1/section 1.1.1"));
        assertEquals(section1_1_2, entity.getSection("TREXOperation/section 1/section 1.1/section 1.1.2"));

        // Verify Section Lists
        assertEquals(section1.getSections().size(), 1);
        assertEquals(section1_1.getSections().size(), 2);
        assertEquals(section1_1_1.getSections().size(), 0);
        assertEquals(section1_1_2.getSections().size(), 0);

        // Create New Entity
        String entityXml = entity.toXml();
        CoalesceEntity entity2 = CoalesceEntity.create(entityXml);

        // Get Section
        section1_1 = entity2.getSection("TREXOperation/section 1/section 1.1");
        section1_1_1 = entity2.getSection("TREXOperation/section 1/section 1.1/section 1.1.1");
        section1_1_2 = entity2.getSection("TREXOperation/section 1/section 1.1/section 1.1.2");

        //System.out.println(entityXml);

        // Verify
        assertNotNull(section1_1);
        assertNotNull(section1_1_1);
        assertNotNull(section1_1_2);

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
    public void createSectionNullEntityParentTest()
    {
        CoalesceEntity parent = null;
        @SuppressWarnings("unused")
        CoalesceSection liveSection = CoalesceSection.create(parent, "Live Status Section");
    }

    @Test(expected = NullArgumentException.class)
    public void createSectionNullSectionParentTest()
    {
        CoalesceSection parent = null;
        @SuppressWarnings("unused")
        CoalesceSection liveSection = CoalesceSection.create(parent, "Live Status Section");
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

    @Test(expected = NullArgumentException.class)
    public void createNestedSectionNullNameTest()
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                                      "TREX Portal",
                                                      "1.0.0.0",
                                                      "",
                                                      "",
                                                      "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Create Live Status Section
        CoalesceSection liveSection = CoalesceSection.create(entity, "Live Status Section");

        @SuppressWarnings("unused")
        CoalesceSection section = CoalesceSection.create(liveSection, null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void createNestedSectionEmptyNameTest()
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                                      "TREX Portal",
                                                      "1.0.0.0",
                                                      "",
                                                      "",
                                                      "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Create Live Status Section
        CoalesceSection liveSection = CoalesceSection.create(entity, "Live Status Section");

        @SuppressWarnings("unused")
        CoalesceSection section = CoalesceSection.create(liveSection, "");

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

    @Test(expected = IllegalArgumentException.class)
    public void createNestedSectionWhiteSpaceNameTest()
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                                      "TREX Portal",
                                                      "1.0.0.0",
                                                      "",
                                                      "",
                                                      "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Create Live Status Section
        CoalesceSection liveSection = CoalesceSection.create(entity, "Live Status Section");

        @SuppressWarnings("unused")
        CoalesceSection section = CoalesceSection.create(liveSection, "  ");

    }

    @Test(expected = NullArgumentException.class)
    public void createSectionNullBothTestEntity()
    {
        CoalesceEntity parent = null;
        @SuppressWarnings("unused")
        CoalesceSection section = CoalesceSection.create(parent, null);
    }

    @Test(expected = NullArgumentException.class)
    public void createSectionNullBothTestSection()
    {
        CoalesceSection parent = null;
        @SuppressWarnings("unused")
        CoalesceSection section = CoalesceSection.create(parent, null);
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
    public void createNestedSectionExistingNoIndexTrueTest()
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                                      "TREX Portal",
                                                      "1.0.0.0",
                                                      "",
                                                      "",
                                                      "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        CoalesceSection liveSection = CoalesceSection.create(entity, "Live Status Section", true);

        // Create Live Status Section
        // assertNull(entity.getSection("TREXOperation/Live Status Section/Live Status Sub Section"));

        CoalesceSection liveSubSection = CoalesceSection.create(liveSection, "Live Status Sub Section", true);
        assertNotNull(GUIDHelper.isValid(liveSubSection.getKey()));
        // assertEquals(liveSubSection, entity.getSection("TREXOperation/Live Status Section/Live Status Sub Section"));
        // assertFalse(liveSubSection.getNoIndex());

        CoalesceSection liveSubSection2 = CoalesceSection.create(liveSection, "Live Status Sub Section");
        assertEquals(liveSubSection2, liveSection.getSection("Live Status Section/Live Status Sub Section"));
        assertEquals(liveSubSection, liveSubSection2);
        assertFalse(liveSubSection.getNoIndex());
        assertFalse(liveSubSection2.getNoIndex());
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
    public void createNestedSectionWithNoIndexFalseTest()
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                                      "TREX Portal",
                                                      "1.0.0.0",
                                                      "",
                                                      "",
                                                      "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        CoalesceSection liveSection = CoalesceSection.create(entity, "Live Status Section", false);
        assertFalse(liveSection.getNoIndex());

        CoalesceSection liveSubSection = CoalesceSection.create(liveSection, "Live Status Sub Section", false);
        assertEquals(liveSubSection, liveSection.getSection("Live Status Section/Live Status Sub Section"));
        assertFalse(liveSubSection.getNoIndex());
    }

    @Test
    public void createNestedSectionWithNoIndexTrueTest()
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                                      "TREX Portal",
                                                      "1.0.0.0",
                                                      "",
                                                      "",
                                                      "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        CoalesceSection liveSection = CoalesceSection.create(entity, "Live Status Section", true);
        assertTrue(liveSection.getNoIndex());

        CoalesceSection liveSubSection = CoalesceSection.create(liveSection, "Live Status Sub Section", true);
        assertEquals(liveSubSection, liveSection.getSection("Live Status Section/Live Status Sub Section"));
        assertTrue(liveSubSection.getNoIndex());
    }

    @Test
    public void createNestedSectionWithNoIndexTrueForExistingNoIndexFalseTest()
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

        CoalesceSection liveSubSection = CoalesceSection.create(liveSection, "Live Status Sub Section", false);
        assertFalse(liveSubSection.getNoIndex());

        CoalesceSection liveSubSection2 = CoalesceSection.create(liveSection, "Live Status Sub Section", true);
        assertEquals(liveSubSection2, liveSection.getSection("Live Status Section/Live Status Sub Section"));
        assertEquals(liveSubSection, liveSubSection2);
        assertTrue(liveSubSection.getNoIndex());
        assertTrue(liveSubSection2.getNoIndex());

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

    @Test
    public void createNestedSectionWithNoIndexTrueFromXmlTest()
    {

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertNull(entity.getSection("TREXMission/A New Section"));

        CoalesceSection newSection = CoalesceSection.create(entity, "A New Section", true);

        assertNotNull(newSection);
        assertEquals(newSection, entity.getSection("TREXMission/A New Section"));
        assertTrue(newSection.getNoIndex());

        CoalesceSection newSubSection = CoalesceSection.create(newSection, "A New Sub Section", true);

        assertNotNull(newSubSection);
        assertEquals(newSubSection, newSection.getSection("A New Section/A New Sub Section"));
        assertTrue(newSubSection.getNoIndex());

        String entityXml = entity.toXml();

        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);

        CoalesceSection desSection = desEntity.getSection("TREXMission/A New Section");
        CoalesceSection desSubSection = desSection.getSection("A New Section/A New Sub Section");
        assertEquals(newSubSection.getKey(), desSubSection.getKey());
        assertEquals(newSubSection.getName(), desSubSection.getName());
        assertEquals(newSubSection.getNoIndex(), desSubSection.getNoIndex());
    }

    @Test(expected = NullArgumentException.class)
    public void createSectionNoIndexFalseNullParentTest()
    {
        CoalesceEntity parent = null;
        @SuppressWarnings("unused")
        CoalesceSection liveSection = CoalesceSection.create(parent, "Live Status Section", false);
    }

    @Test(expected = NullArgumentException.class)
    public void createSectionNoIndexTrueNullParentTest()
    {
        CoalesceEntity parent = null;
        @SuppressWarnings("unused")
        CoalesceSection liveSection = CoalesceSection.create(parent, "Live Status Section", true);
    }

    @Test(expected = NullArgumentException.class)
    public void createNestedSectionNoIndexFalseNullParentTest()
    {
        CoalesceSection parent = null;
        @SuppressWarnings("unused")
        CoalesceSection liveSection = CoalesceSection.create(parent, "Live Status Section", false);
    }

    @Test(expected = NullArgumentException.class)
    public void createNestedSectionNoIndexTrueNullParentTest()
    {
        CoalesceSection parent = null;
        @SuppressWarnings("unused")
        CoalesceSection liveSection = CoalesceSection.create(parent, "Live Status Section", true);
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
    public void createNestedSectionNoIndexFalseNullNameTest()
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                                      "TREX Portal",
                                                      "1.0.0.0",
                                                      "",
                                                      "",
                                                      "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        CoalesceSection section = CoalesceSection.create(entity, "A New Section", false);
        @SuppressWarnings("unused")
        CoalesceSection subsection = CoalesceSection.create(section, null, false);

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
    public void createNestedSectionNoIndexTrueNullNameTest()
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                                      "TREX Portal",
                                                      "1.0.0.0",
                                                      "",
                                                      "",
                                                      "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        CoalesceSection section = CoalesceSection.create(entity, "A New Section", true);
        @SuppressWarnings("unused")
        CoalesceSection subsection = CoalesceSection.create(section, null, true);

    }

    @Test(expected = NullArgumentException.class)
    public void createSectionIndexFalseNullTest()
    {
        CoalesceEntity parent = null;
        @SuppressWarnings("unused")
        CoalesceSection section = CoalesceSection.create(parent, null, false);
    }

    @Test(expected = NullArgumentException.class)
    public void createSectionIndexTrueNullTest()
    {
        CoalesceEntity parent = null;
        @SuppressWarnings("unused")
        CoalesceSection section = CoalesceSection.create(parent, null, true);
    }

    @Test(expected = NullArgumentException.class)
    public void initializeNullParent()
    {
        CoalesceEntity parent = null;
        CoalesceSection section = new CoalesceSection();

        section.initialize(parent, new Section());

    }

    @Test(expected = NullArgumentException.class)
    public void createNestedSectionIndexFalseNullTest()
    {
        CoalesceSection parent = null;
        @SuppressWarnings("unused")
        CoalesceSection section = CoalesceSection.create(parent, null, false);
    }

    @Test(expected = NullArgumentException.class)
    public void createNestedSectionIndexTrueNullTest()
    {
        CoalesceSection parent = null;
        @SuppressWarnings("unused")
        CoalesceSection section = CoalesceSection.create(parent, null, true);
    }

    @Test(expected = NullArgumentException.class)
    public void initializeNestedNullParent()
    {
        CoalesceSection parent = null;
        CoalesceSection section = new CoalesceSection();

        section.initialize(parent, new Section());

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
    public void initializeNullNestedSection()
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                                      "TREX Portal",
                                                      "1.0.0.0",
                                                      "",
                                                      "",
                                                      "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        CoalesceSection section = new CoalesceSection();

        section.initialize(entity, new Section());

        CoalesceSection subsection = new CoalesceSection();
        subsection.initialize(section, null);
    }

    @Test(expected = NullArgumentException.class)
    public void initializeNullBoth()
    {
        CoalesceEntity parent = null;
        CoalesceSection section = new CoalesceSection();

        section.initialize(parent, null);

    }

    @Test(expected = NullArgumentException.class)
    public void initializeNestedNullBoth()
    {
        CoalesceSection parent = null;
        CoalesceSection section = new CoalesceSection();

        section.initialize(parent, null);

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
    public void dateCreatedTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceSection liveSection = entity.getSection(CoalesceTypeInstances.TEST_MISSION_LIVE_SECTION_PATH);

        assertEquals(JodaDateTimeHelper.fromXmlDateTimeUTC("2014-05-02T14:33:51.851575Z"), liveSection.getDateCreated());

        DateTime now = JodaDateTimeHelper.nowInUtc();
        liveSection.setDateCreated(now);

        assertEquals(now, liveSection.getDateCreated());

    }

    @Test
    public void lastModifiedTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceSection liveSection = entity.getSection(CoalesceTypeInstances.TEST_MISSION_LIVE_SECTION_PATH);

        assertEquals(JodaDateTimeHelper.fromXmlDateTimeUTC("2014-05-02T14:33:59.1309914Z"), liveSection.getLastModified());

        DateTime now = JodaDateTimeHelper.nowInUtc();
        liveSection.setLastModified(now);

        assertEquals(now, liveSection.getLastModified());

    }

    @Test
    public void toXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceSection section = entity.getSection("TREXMission/Mission Information Section");
        String sectionXml = section.toXml();

        Section desSection = (Section) XmlHelper.deserialize(sectionXml, Section.class);

        assertEquals(section.getRecordsets().size(), desSection.getRecordset().size());
        assertEquals(section.getKey(), desSection.getKey());
        assertEquals(section.getName(), desSection.getName());
        assertEquals(null, desSection.isNoindex());
        assertEquals(section.getDateCreated(), desSection.getDatecreated());
        assertEquals(section.getLastModified(), desSection.getLastmodified());
        assertEquals(section.getStatus(), desSection.getStatus());

    }

    @Test
    public void setStatusTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceSection section = entity.getSection("TREXMission/Mission Information Section");

        assertEquals(ECoalesceObjectStatus.ACTIVE, section.getStatus());

        section.setStatus(ECoalesceObjectStatus.UNKNOWN);
        String sectionXml = section.toXml();

        Section desSection = (Section) XmlHelper.deserialize(sectionXml, Section.class);

        assertEquals(ECoalesceObjectStatus.UNKNOWN, desSection.getStatus());

    }

    @Test
    public void attributeTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceSection section = entity.getSection("TREXMission/Mission Information Section");

        int before = section.getAttributes().size();

        section.setAttribute("TestAttribute", "TestingValue");

        assertEquals(before + 1, section.getAttributes().size());

        assertEquals("TestingValue", section.getAttribute("TestAttribute"));

        assertEquals("Mission Information Section", section.getName());
        assertEquals(false, section.getNoIndex());

        section.setAttribute("Name", "TestingName");
        assertEquals("TestingName", section.getName());
        assertEquals("TestingName", section.getAttribute("Name"));

        UUID guid = UUID.randomUUID();
        section.setAttribute("Key", guid.toString());
        assertEquals(guid.toString(), section.getKey());
        assertEquals(guid.toString(), section.getAttribute("Key"));

        DateTime now = JodaDateTimeHelper.nowInUtc();
        DateTime future = now.plusDays(2);

        section.setAttribute("DateCreated", JodaDateTimeHelper.toXmlDateTimeUTC(now));
        assertEquals(now, section.getDateCreated());

        section.setAttribute("NoIndex", "True");
        assertEquals(true, section.getNoIndex());

        section.setAttribute("Status", ECoalesceObjectStatus.UNKNOWN.toString());
        assertEquals(ECoalesceObjectStatus.UNKNOWN, section.getStatus());

        section.setAttribute("LastModified", JodaDateTimeHelper.toXmlDateTimeUTC(future));
        assertEquals(future, section.getLastModified());

        String entityXml = entity.toXml();
        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceSection desSection = desEntity.getSection("TREXMission/TestingName");

        assertEquals("TestingValue", desSection.getAttribute("TestAttribute"));
        assertEquals("TestingName", desSection.getName());
        assertEquals(guid.toString(), desSection.getKey());
        assertEquals(now, desSection.getDateCreated());
        assertEquals(future, desSection.getLastModified());
        assertEquals(true, desSection.getNoIndex());
        assertEquals(ECoalesceObjectStatus.UNKNOWN, desSection.getStatus());

    }
}
