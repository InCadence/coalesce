package Coalesce.Framework.DataModel;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import Coalesce.Common.UnitTest.CoalesceTypeInstances;

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

public class XsdEntityTest {

    /*
     * @BeforeClass public static void setUpBeforeClass() throws Exception {
     * 
     * }
     * 
     * @AfterClass public static void tearDownAfterClass() throws Exception { }
     * 
     * @Before public void setUp() throws Exception {
     * 
     * }
     * 
     * @After public void tearDown() throws Exception { }
     */

    @Test
    public void CreateFromXmlTest()
    {

        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        String title = entity.GetTitle();
        assertEquals("NORTHCOM Volunteer Background Checks Changed, NORTHCOM Volunteer Background Checks", title);
        assertEquals(4, entity.GetLinkages().values().size());
        assertEquals(2, entity.GetSections().size());
        assertEquals(1,
                     ((XsdRecordset) entity.GetDataObjectForNamePath(CoalesceTypeInstances.TESTMISSIONRECORDSETPATH)).GetCount());
        assertEquals(16,
                     ((XsdRecordset) entity.GetDataObjectForNamePath(CoalesceTypeInstances.TESTMISSIONRECORDSETPATH)).GetFieldDefinitions().size());
        assertEquals(16,
                     entity.GetDataObjectForNamePath(CoalesceTypeInstances.TESTMISSIONRECORDPATH).GetChildDataObjects().size());
    }

    @Test
    public void CreateFromXmlEmptyTest()
    {
        XsdEntity entity = XsdEntity.Create("");

        assertEmptyEntity(entity);

    }

    @Test
    public void CreateFromXmlWhitespaceTest()
    {
        XsdEntity entity = XsdEntity.Create("  ");

        assertEmptyEntity(entity);

    }

    @Test
    public void CreateFromXmlNullTest()
    {
        XsdEntity entity = XsdEntity.Create(null);

        assertEmptyEntity(entity);
    }

    @Test
    public void CreateFromXmlInvalidXmlTest()
    {
        XsdEntity entity = XsdEntity.Create("invalid format");

        assertNull(entity);

    }

    @Test
    public void GetTitleWithoutXpathTest()
    {

        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOXPATHTITLE);

        String title = entity.GetTitle();

        assertEquals("TREX Portal", title);

    }

    @Test
    public void ArbitraryAttributesTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        String entityXml = entity.ToXml();

        assertTrue(entityXml.contains("anthony=\"Test\""));
        assertTrue(entity.GetAttribute("anthony").equals("Test"));

    }

    @Test
    public void UpdateTitleThatUsesXpathTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField missionName = (XsdField) entity.GetDataObjectForNamePath(CoalesceTypeInstances.TESTMISSIONNAMEPATH);
        XsdField incidentTitle = (XsdField) entity.GetDataObjectForNamePath(CoalesceTypeInstances.TESTMISSIONINCIDENTTITLEPATH);

        missionName.SetValue("Mission Name");
        incidentTitle.SetValue("Incident Title");

        String title = entity.GetTitle();
        assertEquals("Mission Name, Incident Title", title);

    }

    @Test
    public void UpdateTitleThatDoesNotUseXpathTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOXPATHTITLE);

        entity.SetTitle("Mission Entity Title");

        assertEquals("Mission Entity Title", entity.GetTitle());
    }

    @Test
    public void ReplaceTitleThatUsesXpathTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        entity.SetTitle("Mission Entity Title");

        assertEquals("Mission Entity Title", entity.GetTitle());
    }

    @Test
    public void CreateTREXOperationWithStaticTest()
    {

        // Create Entity
        XsdEntity entity = XsdEntity.Create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Verify Entity Creation
        assertEquals("TREXOperation", entity.GetName());
        assertEquals("TREX Portal", entity.GetSource());
        assertNotNull(entity.GetLinkageSection());
        assertTrue(entity.GetSections().isEmpty());

        entity.SetAttribute("testnewattribute", "test");

        // Create Linkage Section
        XsdLinkageSection.Create(entity, true);

        // Verify Link Section Creation
        assertTrue(entity.GetLinkageSection() != null);

        // Create Live Status Section
        XsdSection liveSection = XsdSection.Create(entity, "Live Status Section", true);
        XsdRecordset liveRecordSet = XsdRecordset.Create(liveSection, "Live Status Recordset");

        // Verify Live Status Section Creation
        assertTrue(entity.GetSection("TREXOperation/Live Status Section") != null);

        XsdFieldDefinition.Create(liveRecordSet, "CurrentStatus", ECoalesceFieldDataTypes.StringType);

        // Create Information Section
        XsdSection informationSection = XsdSection.Create(entity, "Operation Information Section", true);
        XsdRecordset informationRecordSet = XsdRecordset.Create(informationSection, "Operation Information Recordset");

        XsdFieldDefinition.Create(informationRecordSet, "OperationName", ECoalesceFieldDataTypes.StringType);

        // Verify Information Section Creation
        assertTrue(entity.GetSection("TREXOperation/Operation Information Section") != null);

        // Serialize
        String entityXml = entity.ToXml();

        // Deserialize
        XsdEntity entity2 = new XsdEntity();
        assertTrue(entity2.Initialize(entityXml));

        // Verify Custom Attribute
        assertTrue(entity2.GetAttribute("testnewattribute").equals("test"));

        // Verify Entity
        assertTrue(entity2.GetSource().equals("TREX Portal"));

        // Verify Link Section
        assertTrue(entity2.GetLinkageSection() != null);

        // Verify Live Status Section
        assertTrue(entity2.GetSection("TREXOperation/Live Status Section") != null);

        // Verify Information Section
        assertTrue(entity2.GetSection("TREXOperation/Operation Information Section") != null);

    }

    @Test
    public void CreateTREXOperationWithInstanceCreateTest()
    {

        // Create Entity
        XsdEntity entity = XsdEntity.Create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Verify Entity Creation
        assertTrue(entity.GetSource().equals("TREX Portal"));

        entity.SetAttribute("testnewattribute", "test");

        // Verify Link Section Creation
        assertTrue(entity.GetLinkageSection() != null);

        // Create Live Status Section
        XsdSection liveSection = entity.CreateSection("Live Status Section", true);
        XsdRecordset liveRecordSet = liveSection.CreateRecordset("Live Status Recordset");

        // Verify Live Status Section Creation
        assertTrue(entity.GetSection("TREXOperation/Live Status Section") != null);

        liveRecordSet.CreateFieldDefinition("CurrentStatus", ECoalesceFieldDataTypes.StringType);

        // Create Information Section
        XsdSection informationSection = entity.CreateSection("Operation Information Section", true);
        XsdRecordset informationRecordSet = informationSection.CreateRecordset("Operation Information Recordset");

        informationRecordSet.CreateFieldDefinition("OperationName", ECoalesceFieldDataTypes.StringType);

        // Verify Information Section Creation
        assertTrue(entity.GetSection("TREXOperation/Operation Information Section") != null);

        // Serialize
        String entityXml = entity.ToXml();

        // Deserialize
        XsdEntity entity2 = new XsdEntity();
        assertTrue(entity2.Initialize(entityXml));

        // Verify Custom Attribute
        assertTrue(entity2.GetAttribute("testnewattribute").equals("test"));

        // Verify Entity
        assertTrue(entity2.GetSource().equals("TREX Portal"));

        // Verify Link Section
        assertTrue(entity2.GetLinkageSection() != null);

        // Verify Live Status Section
        assertTrue(entity2.GetSection("TREXOperation/Live Status Section") != null);

        // Verify Information Section
        assertTrue(entity2.GetSection("TREXOperation/Operation Information Section") != null);

    }

    @Test
    public void CreateSectionTest()
    {
        // Create Entity
        XsdEntity entity = XsdEntity.Create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Create Live Status Section
        assertNull(entity.GetSection("TREXOperation/Live Status Section"));

        XsdSection liveSection = entity.CreateSection("Live Status Section");
        assertEquals(liveSection, entity.GetSection("TREXOperation/Live Status Section"));
        assertFalse(liveSection.GetNoIndex());
    }

    @Test
    public void CreateSectionExistingNoIndexTrueTest()
    {
        // Create Entity
        XsdEntity entity = XsdEntity.Create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Create Live Status Section
        assertNull(entity.GetSection("TREXOperation/Live Status Section"));

        XsdSection liveSection = entity.CreateSection("Live Status Section", true);
        assertEquals(liveSection, entity.GetSection("TREXOperation/Live Status Section"));
        assertTrue(liveSection.GetNoIndex());

        XsdSection liveSection2 = entity.CreateSection("Live Status Section");
        assertEquals(liveSection2, entity.GetSection("TREXOperation/Live Status Section"));
        assertEquals(liveSection, liveSection2);
        assertFalse(liveSection.GetNoIndex());
        assertFalse(liveSection2.GetNoIndex());

    }

    @Test
    public void CreateSectionFromXmlTest()
    {

        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        assertNull(entity.GetSection("TREXMission/A New Section"));

        XsdSection newSection = entity.CreateSection("A New Section");

        assertNotNull(newSection);
        assertEquals(newSection, entity.GetSection("TREXMission/A New Section"));
        assertFalse(newSection.GetNoIndex());

    }

    @Test
    public void CreateSectionWithNoIndexFalseTest()
    {
        // Create Entity
        XsdEntity entity = XsdEntity.Create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Create Live Status Section
        assertNull(entity.GetSection("TREXOperation/Live Status Section"));

        XsdSection liveSection = entity.CreateSection("Live Status Section", false);
        assertEquals(liveSection, entity.GetSection("TREXOperation/Live Status Section"));
        assertFalse(liveSection.GetNoIndex());
    }

    @Test
    public void CreateSectionWithNoIndexTrueTest()
    {
        // Create Entity
        XsdEntity entity = XsdEntity.Create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Create Live Status Section
        assertNull(entity.GetSection("TREXOperation/Live Status Section"));

        XsdSection liveSection = entity.CreateSection("Live Status Section", true);
        assertEquals(liveSection, entity.GetSection("TREXOperation/Live Status Section"));
        assertTrue(liveSection.GetNoIndex());

    }

    @Test
    public void CreateSectionWithNoIndexTrueForExistingNoIndexFalseTest()
    {
        // Create Entity
        XsdEntity entity = XsdEntity.Create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Create Live Status Section
        assertNull(entity.GetSection("TREXOperation/Live Status Section"));

        XsdSection liveSection = entity.CreateSection("Live Status Section", false);
        assertEquals(liveSection, entity.GetSection("TREXOperation/Live Status Section"));
        assertFalse(liveSection.GetNoIndex());

        XsdSection liveSection2 = entity.CreateSection("Live Status Section", true);
        assertEquals(liveSection2, entity.GetSection("TREXOperation/Live Status Section"));
        assertEquals(liveSection, liveSection2);
        assertTrue(liveSection.GetNoIndex());
        assertTrue(liveSection2.GetNoIndex());

    }

    @Test
    public void CreateSectionWithNoIndexTrueFromXmlTest()
    {

        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        assertNull(entity.GetSection("TREXMission/A New Section"));

        XsdSection newSection = entity.CreateSection("A New Section", true);

        assertNotNull(newSection);
        assertEquals(newSection, entity.GetSection("TREXMission/A New Section"));
        assertTrue(newSection.GetNoIndex());

        String entityXml = entity.ToXml();

        XsdEntity desEntity = XsdEntity.Create(entityXml);

        XsdSection desSection = desEntity.GetSection("TREXMission/A New Section");
        assertEquals(newSection.GetKey(), desSection.GetKey());
        assertEquals(newSection.GetName(), desSection.GetName());
        assertEquals(newSection.GetNoIndex(), desSection.GetNoIndex());
    }

    @Test
    public void CreateSectionWithNoIndexFalseFromXmlExistingTest()
    {

        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdSection liveSection = entity.GetSection("TREXMission/Live Status Section");
        XsdSection informationSection = entity.GetSection("TREXMission/Mission Information Section");

        XsdSection createdLiveSection = entity.CreateSection("Live Status Section");
        XsdSection createdInformationSection = entity.CreateSection("Mission Information Section");

        assertEquals(liveSection, createdLiveSection);
        assertEquals(informationSection, createdInformationSection);

    }

    @Test
    public void GetSectionsFromXmlTest()
    {

        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        Map<String, XsdSection> sections = entity.GetSections();

        XsdSection liveSection = sections.get("85CB4256-4CC2-4F96-A03D-5EF880989822");

        assertNotNull(liveSection);
        assertEquals("85CB4256-4CC2-4F96-A03D-5EF880989822", liveSection.GetKey());

        XsdSection informationSection = sections.get("383EA645-E695-4E75-ADA6-0C79BEC09A18");
        assertNotNull(informationSection);
        assertEquals("383EA645-E695-4E75-ADA6-0C79BEC09A18", informationSection.GetKey());

    }

    @Test
    public void GetSectionsEmptyTest()
    {

        XsdEntity entity = XsdEntity.Create("");

        Map<String, XsdSection> sections = entity.GetSections();

        assertTrue(sections.isEmpty());

    }

    @Test
    public void GetSectionsManuallyCreatedTest()
    {

        // Create Entity
        XsdEntity entity = XsdEntity.Create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Create Live Status Section
        XsdSection liveSection = entity.CreateSection("Live Status Section", true);

        // Create Information Section
        XsdSection informationSection = entity.CreateSection("Operation Information Section", true);

        Map<String, XsdSection> sections = entity.GetSections();

        assertEquals(liveSection, sections.get(liveSection.GetKey()));
        assertEquals(informationSection, sections.get(informationSection.GetKey()));

    }

    @Test
    public void GetLinkageSectionFromXmlTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdLinkageSection linkageSection = entity.GetLinkageSection();

        assertEquals("F4F126AF-4658-4D7F-A67F-4833F7EADDC3", linkageSection.GetKey());

    }

    @Test
    public void GetLinkageSectionEmptyEntityTest()
    {

        // Create Entity
        XsdEntity entity = XsdEntity.Create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        XsdLinkageSection entityLinkageSection = entity.GetLinkageSection();
        assertNotNull(entityLinkageSection);

    }

    @Test
    public void FieldHistoryTest()
    {

        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdDataObject xdo = entity.GetDataObjectForNamePath(CoalesceTypeInstances.TESTMISSIONNAMEPATH);

        assertTrue(xdo instanceof XsdField);

        XsdField nameField = (XsdField) xdo;

        assertEquals(1, nameField.GetHistory().size());
        assertEquals(CoalesceTypeInstances.TESTMISSIONNAMEHISTORYVALUE, nameField.GetHistory().get(0).GetValue());

        xdo = entity.GetDataObjectForNamePath(CoalesceTypeInstances.TESTMISSIONACTIONNUMBERPATH);

        assertTrue(xdo instanceof XsdField);

        XsdField actionNumberField = (XsdField) xdo;

        assertEquals(2, actionNumberField.GetHistory().size());
        assertEquals(CoalesceTypeInstances.TESTMISSIONACTIONNUMBERLABELHISTORY,
                     actionNumberField.GetHistory().get(0).GetLabel());

        xdo = entity.GetDataObjectForNamePath(CoalesceTypeInstances.TESTMISSIONBASE64PATH);

        assertTrue(xdo instanceof XsdField);

        XsdField base64Field = (XsdField) xdo;

        assertTrue(base64Field.GetHistory().isEmpty());

    }

    // -----------------------------------------------------------------------//
    // Private Methods
    // -----------------------------------------------------------------------//

    private void assertEmptyEntity(XsdEntity entity)
    {

        assertTrue(entity.GetLinkages().values().isEmpty());
        assertEquals(0, entity.GetSections().size());
    }

}
