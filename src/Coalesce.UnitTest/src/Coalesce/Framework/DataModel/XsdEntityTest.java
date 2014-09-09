package Coalesce.Framework.DataModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Test;

import Coalesce.Common.Helpers.EntityLinkHelper;
import Coalesce.Common.Helpers.GUIDHelper;
import Coalesce.Common.Helpers.JodaDateTimeHelper;
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
    public void CreateFromXmlWithTitleNullTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION, null);

        assertEquals("TREX Portal", entity.GetTitle());
    }

    @Test
    public void CreateFromXmlWithTitleEmptyStringTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION, "");

        assertEquals("TREX Portal", entity.GetTitle());
    }

    @Test
    public void CreateFromXmlWithTitleWhiteSpaceTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION, "   ");

        assertEquals("TREX Portal", entity.GetTitle());
    }

    @Test
    public void CreateFromXmlWithTitleNewXpathTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION,
                                            "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionName");

        assertEquals(((XsdField) entity.GetDataObjectForNamePath(CoalesceTypeInstances.TESTMISSIONNAMEPATH)).GetValue(),
                     entity.GetTitle());
    }

    @Test
    public void CreateFromXmlWithTitleNewTitleTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION, "New Mission Title");

        assertEquals("New Mission Title", entity.GetTitle());
    }

    @Test
    public void CreateDetailedWithoutTitleTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", "Portal", "1.1.1.1", "Entity Id", "Entity Type");

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.GetName());
        assertEquals("Portal", entity.GetSource());
        assertEquals("1.1.1.1", entity.GetVersion());
        assertEquals("Entity Id", entity.GetEntityId());
        assertEquals("Entity Type", entity.GetEntityIdType());

        assertEquals("Portal", entity.GetTitle());

    }

    @Test
    public void CreateDetailedWithoutTitleNullNameTest()
    {
        XsdEntity entity = XsdEntity.Create(null, "Portal", "1.1.1.1", "Entity Id", "Entity Type");

        assertEmptyEntity(entity);

        assertEquals("", entity.GetName());
        assertEquals("Portal", entity.GetSource());
        assertEquals("1.1.1.1", entity.GetVersion());
        assertEquals("Entity Id", entity.GetEntityId());
        assertEquals("Entity Type", entity.GetEntityIdType());

        assertEquals("Portal", entity.GetTitle());

    }

    @Test
    public void CreateDetailedWithoutTitleNullSourceTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", null, "1.1.1.1", "Entity Id", "Entity Type");

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.GetName());
        assertEquals("", entity.GetSource());
        assertEquals("1.1.1.1", entity.GetVersion());
        assertEquals("Entity Id", entity.GetEntityId());
        assertEquals("Entity Type", entity.GetEntityIdType());

        assertEquals("", entity.GetTitle());

    }

    @Test
    public void CreateDetailedWithoutTitleNullVersionTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", "Portal", null, "Entity Id", "Entity Type");

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.GetName());
        assertEquals("Portal", entity.GetSource());
        assertEquals("", entity.GetVersion());
        assertEquals("Entity Id", entity.GetEntityId());
        assertEquals("Entity Type", entity.GetEntityIdType());

        assertEquals("Portal", entity.GetTitle());

    }

    @Test
    public void CreateDetailedWithoutTitleNullEntityIdTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", "Portal", "1.1.1.1", null, "Entity Type");

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.GetName());
        assertEquals("Portal", entity.GetSource());
        assertEquals("1.1.1.1", entity.GetVersion());
        assertEquals("", entity.GetEntityId());
        assertEquals("Entity Type", entity.GetEntityIdType());

        assertEquals("Portal", entity.GetTitle());

    }

    @Test
    public void CreateDetailedWithoutTitleNullEntityTypeTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", "Portal", "1.1.1.1", "Entity Id", null);

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.GetName());
        assertEquals("Portal", entity.GetSource());
        assertEquals("1.1.1.1", entity.GetVersion());
        assertEquals("Entity Id", entity.GetEntityId());
        assertEquals("", entity.GetEntityIdType());

        assertEquals("Portal", entity.GetTitle());

    }

    @Test
    public void CreateDetailedWithTitleTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", "Portal", "1.1.1.1", "Entity Id", "Entity Type", "A New Title");

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.GetName());
        assertEquals("Portal", entity.GetSource());
        assertEquals("1.1.1.1", entity.GetVersion());
        assertEquals("Entity Id", entity.GetEntityId());
        assertEquals("Entity Type", entity.GetEntityIdType());

        assertEquals("A New Title", entity.GetTitle());

    }

    @Test
    public void CreateDetailedWithTitleNullNameTest()
    {
        XsdEntity entity = XsdEntity.Create(null, "Portal", "1.1.1.1", "Entity Id", "Entity Type", "A New Title");

        assertEmptyEntity(entity);

        assertEquals("", entity.GetName());
        assertEquals("Portal", entity.GetSource());
        assertEquals("1.1.1.1", entity.GetVersion());
        assertEquals("Entity Id", entity.GetEntityId());
        assertEquals("Entity Type", entity.GetEntityIdType());

        assertEquals("A New Title", entity.GetTitle());

    }

    @Test
    public void CreateDetailedWithTitleNullSourceTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", null, "1.1.1.1", "Entity Id", "Entity Type", "A New Title");

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.GetName());
        assertEquals("", entity.GetSource());
        assertEquals("1.1.1.1", entity.GetVersion());
        assertEquals("Entity Id", entity.GetEntityId());
        assertEquals("Entity Type", entity.GetEntityIdType());

        assertEquals("A New Title", entity.GetTitle());

    }

    @Test
    public void CreateDetailedWithTitleNullVersionTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", "Portal", null, "Entity Id", "Entity Type", "A New Title");

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.GetName());
        assertEquals("Portal", entity.GetSource());
        assertEquals("", entity.GetVersion());
        assertEquals("Entity Id", entity.GetEntityId());
        assertEquals("Entity Type", entity.GetEntityIdType());

        assertEquals("A New Title", entity.GetTitle());

    }

    @Test
    public void CreateDetailedWithTitleNullEntityIdTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", "Portal", "1.1.1.1", null, "Entity Type", "A New Title");

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.GetName());
        assertEquals("Portal", entity.GetSource());
        assertEquals("1.1.1.1", entity.GetVersion());
        assertEquals("", entity.GetEntityId());
        assertEquals("Entity Type", entity.GetEntityIdType());

        assertEquals("A New Title", entity.GetTitle());

    }

    @Test
    public void CreateDetailedWithTitleNullEntityTypeTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", "Portal", "1.1.1.1", "Entity Id", null, "A New Title");

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.GetName());
        assertEquals("Portal", entity.GetSource());
        assertEquals("1.1.1.1", entity.GetVersion());
        assertEquals("Entity Id", entity.GetEntityId());
        assertEquals("", entity.GetEntityIdType());

        assertEquals("A New Title", entity.GetTitle());

    }

    @Test
    public void CreateDetailedWithTitleNullTitleTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", "Portal", "1.1.1.1", "Entity Id", "Entity Id Type", null);

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.GetName());
        assertEquals("Portal", entity.GetSource());
        assertEquals("1.1.1.1", entity.GetVersion());
        assertEquals("Entity Id", entity.GetEntityId());
        assertEquals("Entity Id Type", entity.GetEntityIdType());

        assertEquals("Portal", entity.GetTitle());

    }

    @Test
    public void CreateDetailedWithTitleEmptyTitleTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", "Portal", "1.1.1.1", "Entity Id", "Entity Id Type", "");

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.GetName());
        assertEquals("Portal", entity.GetSource());
        assertEquals("1.1.1.1", entity.GetVersion());
        assertEquals("Entity Id", entity.GetEntityId());
        assertEquals("Entity Id Type", entity.GetEntityIdType());

        assertEquals("Portal", entity.GetTitle());

    }

    @Test
    public void CreateDetailedWithTitleWhiteSpaceTitleTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", "Portal", "1.1.1.1", "Entity Id", "Entity Id Type", "   ");

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.GetName());
        assertEquals("Portal", entity.GetSource());
        assertEquals("1.1.1.1", entity.GetVersion());
        assertEquals("Entity Id", entity.GetEntityId());
        assertEquals("Entity Id Type", entity.GetEntityIdType());

        assertEquals("Portal", entity.GetTitle());

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
        assertNotNull(entity.GetLinkageSection());

        // Create Live Status Section
        XsdSection liveSection = XsdSection.Create(entity, "Live Status Section", true);
        XsdRecordset liveRecordSet = XsdRecordset.Create(liveSection, "Live Status Recordset");

        // Verify Live Status Section Creation
        assertNotNull(entity.GetSection("TREXOperation/Live Status Section"));

        XsdFieldDefinition.Create(liveRecordSet, "CurrentStatus", ECoalesceFieldDataTypes.StringType);

        // Create Information Section
        XsdSection informationSection = XsdSection.Create(entity, "Operation Information Section", true);
        XsdRecordset informationRecordSet = XsdRecordset.Create(informationSection, "Operation Information Recordset");

        XsdFieldDefinition.Create(informationRecordSet, "OperationName", ECoalesceFieldDataTypes.StringType);

        // Verify Information Section Creation
        assertNotNull(entity.GetSection("TREXOperation/Operation Information Section"));

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
        assertNotNull(entity2.GetLinkageSection());

        // Verify Live Status Section
        assertNotNull(entity2.GetSection("TREXOperation/Live Status Section"));

        // Verify Information Section
        assertNotNull(entity2.GetSection("TREXOperation/Operation Information Section"));

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
        assertNotNull(entity.GetLinkageSection());

        // Create Live Status Section
        XsdSection liveSection = entity.CreateSection("Live Status Section", true);
        XsdRecordset liveRecordSet = liveSection.CreateRecordset("Live Status Recordset");

        // Verify Live Status Section Creation
        assertNotNull(entity.GetSection("TREXOperation/Live Status Section"));

        liveRecordSet.CreateFieldDefinition("CurrentStatus", ECoalesceFieldDataTypes.StringType);

        // Create Information Section
        XsdSection informationSection = entity.CreateSection("Operation Information Section", true);
        XsdRecordset informationRecordSet = informationSection.CreateRecordset("Operation Information Recordset");

        informationRecordSet.CreateFieldDefinition("OperationName", ECoalesceFieldDataTypes.StringType);

        // Verify Information Section Creation
        assertNotNull(entity.GetSection("TREXOperation/Operation Information Section"));

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
        assertNotNull(entity2.GetLinkageSection());

        // Verify Live Status Section
        assertNotNull(entity2.GetSection("TREXOperation/Live Status Section"));

        // Verify Information Section
        assertNotNull(entity2.GetSection("TREXOperation/Operation Information Section"));

    }

    @Test
    public void InitializeFromXmlTest()
    {

        XsdEntity entity = new XsdEntity();

        assertTrue(entity.Initialize(CoalesceTypeInstances.TESTMISSION));

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
    public void InitializeFromXmlEmptyTest()
    {
        XsdEntity entity = new XsdEntity();

        assertTrue(entity.Initialize(""));

        assertEmptyEntity(entity);

    }

    @Test
    public void InitializeFromXmlWhitespaceTest()
    {
        XsdEntity entity = new XsdEntity();

        assertTrue(entity.Initialize("  "));

        assertEmptyEntity(entity);

    }

    @Test
    public void InitializeFromXmlNullTest()
    {
        XsdEntity entity = new XsdEntity();

        assertTrue(entity.Initialize(null));

        assertEmptyEntity(entity);
    }

    @Test
    public void InitializeFromXmlInvalidXmlTest()
    {
        XsdEntity entity = new XsdEntity();

        assertFalse(entity.Initialize("invalid format"));

    }

    @Test
    public void InitializeTest()
    {

        XsdEntity entity = new XsdEntity();

        assertTrue(entity.Initialize());

        assertEmptyEntity(entity);

    }

    @Test
    public void GetKeyFromXmlTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        assertEquals("62857EF8-3930-4F0E-BAE3-093344EBF389", entity.GetKey());

    }

    @Test
    public void GetKeyNewTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertNotNull(GUIDHelper.IsValid(entity.GetKey()));

    }

    @Test
    public void GetNameFromXmlTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        assertEquals("TREXMission", entity.GetName());

    }

    @Test
    public void GetNameNewTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertEquals("Operation", entity.GetName());

    }

    @Test
    public void SetNameFromXmlTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        entity.SetName("Other Name");

        assertEquals("Other Name", entity.GetName());

    }

    @Test
    public void SetNameNewTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        entity.SetName("Other Name");

        assertEquals("Other Name", entity.GetName());

    }

    @Test
    public void getTypeFromXmlTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        assertEquals("entity", entity.getType());

    }

    @Test
    public void getTypeNewTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertEquals("entity", entity.getType());

    }

    @Test
    public void GetSourceFromXmlTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        assertEquals("TREX Portal", entity.GetSource());

    }

    @Test
    public void GetSourceNewTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertEquals("Portal", entity.GetSource());

    }

    @Test
    public void SetSourceFromXmlTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        entity.SetSource("New Source");

        assertEquals("New Source", entity.GetSource());

    }

    @Test
    public void SetSourceNewTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        entity.SetSource("New Source");

        assertEquals("New Source", entity.GetSource());

    }

    @Test
    public void GetVersionFromXmlTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        assertEquals("1.0.0.0", entity.GetVersion());

    }

    @Test
    public void GetVersionNewTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertEquals("1.1.1.1", entity.GetVersion());

    }

    @Test
    public void SetVersionFromXmlTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        entity.SetVersion("2.3.4.5");

        assertEquals("2.3.4.5", entity.GetVersion());

    }

    @Test
    public void SetVersionNewTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        entity.SetVersion("2.3.4.5");

        assertEquals("2.3.4.5", entity.GetVersion());

    }

    @Test
    public void GetEntityIdFromXmlTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        assertEquals("", entity.GetEntityId());

    }

    @Test
    public void GetEntityIdNewTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertEquals("Entity ID", entity.GetEntityId());

    }

    @Test
    public void SetEntityIdFromXmlTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        entity.SetEntityId("New Entity ID");

        assertEquals("New Entity ID", entity.GetEntityId());

    }

    @Test
    public void SetEntityIdNewTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        entity.SetEntityId("New Entity ID");

        assertEquals("New Entity ID", entity.GetEntityId());

    }

    @Test
    public void GetEntityIdTypeFromXmlTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        assertEquals("", entity.GetEntityIdType());

    }

    @Test
    public void GetEntityIdTypeNewTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertEquals("Entity Id Type", entity.GetEntityIdType());

    }

    @Test
    public void SetEntityIdTypeFromXmlTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        entity.SetEntityIdType("New Entity Id Type");

        assertEquals("New Entity Id Type", entity.GetEntityIdType());

    }

    @Test
    public void SetEntityIdTypeNewTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertEquals("Entity Id Type", entity.GetEntityIdType());

    }

    @Test
    public void GetTitleXPathInvalidTest()
    {
        XsdEntity entity = XsdEntity.Create("Operation",
                                            "Portal",
                                            "1.1.1.1",
                                            "Entity Id",
                                            "Entity Id Type",
                                            "Operation/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionName,TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/IncidentTitle");

        String title = entity.GetTitle();

        assertEquals("Portal", title);

    }

    @Test
    public void GetTitleWithoutXpathTest()
    {

        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOXPATHTITLE);

        String title = entity.GetTitle();

        assertEquals("TREX Portal", title);

    }

    @Test
    public void GetTitleUpdateThatUsesXpathTest()
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
    public void SetTitleThatDoesNotUseXpathTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOXPATHTITLE);

        entity.SetTitle("Mission Entity Title");

        assertEquals("Mission Entity Title", entity.GetTitle());
    }

    @Test
    public void SetTitleThatUsesXpathTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        entity.SetTitle("Mission Entity Title");

        assertEquals("Mission Entity Title", entity.GetTitle());
    }

    @Test
    public void GetDateCreatedFromXmlTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        assertEquals(JodaDateTimeHelper.FromXmlDateTimeUTC("2014-05-02T14:33:51.851575Z"), entity.GetDateCreated());

    }

    @Test
    public void GetDateCreatedNewTest()
    {
        XsdEntity entity = new XsdEntity();
        entity.Initialize();

        DateTime now = JodaDateTimeHelper.NowInUtc();

        assertTrue(Math.abs(now.getMillis() - entity.GetDateCreated().getMillis()) < 5);
    }

    @Test
    public void SetDateCreatedFromXmlTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        DateTime now = JodaDateTimeHelper.NowInUtc();

        entity.SetDateCreated(now);

        assertEquals(now, entity.GetDateCreated());

    }

    @Test
    public void SetDateCreatedNewTest()
    {
        XsdEntity entity = new XsdEntity();
        entity.Initialize();

        DateTime sixDaysAgo = JodaDateTimeHelper.NowInUtc().minusDays(6);

        entity.SetDateCreated(sixDaysAgo);

        assertEquals(sixDaysAgo, entity.GetDateCreated());
    }

    @Test
    public void GetLastModifiedFromXmlTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        assertEquals(JodaDateTimeHelper.FromXmlDateTimeUTC("2014-05-20T16:17:13.2293139Z"), entity.GetLastModified());

    }

    @Test
    public void GetLastModifiedNewTest()
    {
        XsdEntity entity = new XsdEntity();
        entity.Initialize();

        DateTime now = JodaDateTimeHelper.NowInUtc();

        assertTrue(Math.abs(now.getMillis() - entity.GetLastModified().getMillis()) < 5);
    }

    @Test
    public void SetLastModifiedFromXmlTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        DateTime now = JodaDateTimeHelper.NowInUtc();

        entity.SetLastModified(now);

        assertEquals(now, entity.GetLastModified());

    }

    @Test
    public void SetLastModifiedNewTest()
    {
        XsdEntity entity = new XsdEntity();
        entity.Initialize();

        DateTime sixDaysAgo = JodaDateTimeHelper.NowInUtc().minusDays(6);

        entity.SetLastModified(sixDaysAgo);

        assertEquals(sixDaysAgo, entity.GetLastModified());
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
    public void GetLinkagesFromXmlTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        Map<String, XsdLinkage> linkages = entity.GetLinkages();

        assertEquals(4, linkages.size());

        assertLinkage("62857EF8-3930-4F0E-BAE3-093344EBF389",
                      ELinkTypes.IsChildOf,
                      "AEACD69E-5365-4401-87A1-D95E657E0785",
                      linkages.get("DB7E0EAF-F4EF-4473-94A9-B93A7F46281E"));
        assertLinkage("62857EF8-3930-4F0E-BAE3-093344EBF389",
                      ELinkTypes.IsParentOf,
                      "C42DFD35-EA71-4F56-BC3B-D4287279123D",
                      linkages.get("9A04CBCD-297F-43E2-A590-F59D8438E386"));
        assertLinkage("62857EF8-3930-4F0E-BAE3-093344EBF389",
                      ELinkTypes.HasParticipant,
                      "BDCD779B-3C74-4391-BCCB-2DB8D06D5A6F",
                      linkages.get("309153E3-5F53-4EDB-B89C-35AE6EECEBF1"));
        assertLinkage("62857EF8-3930-4F0E-BAE3-093344EBF389",
                      ELinkTypes.IsParentOf,
                      "55DFA165-0AB8-48B7-BF35-5DA6CADB5E1E",
                      linkages.get("6AD08B0F-C492-4105-8033-A5E43056B864"));
    }

    @Test
    public void GetLinkagesNewTest()
    {
        XsdEntity entity = new XsdEntity();
        entity.Initialize();

        XsdEntity entity2 = new XsdEntity();
        entity2.Initialize();

        XsdEntity entity3 = new XsdEntity();
        entity3.Initialize();

        assertTrue(EntityLinkHelper.LinkEntities(entity, ELinkTypes.HasUseOf, entity2, false));
        assertTrue(EntityLinkHelper.LinkEntities(entity, ELinkTypes.IsParentOf, entity3, false));
        assertTrue(EntityLinkHelper.LinkEntities(entity2, ELinkTypes.WasCreatedBy, entity, false));

        Map<String, XsdLinkage> linkages = entity.GetLinkages();

        assertEquals(3, linkages.size());

        for (XsdLinkage linkage : linkages.values())
        {
            switch (linkage.GetLinkType()) {
            case HasUseOf:

                assertLinkage(entity.GetKey(), linkage.GetLinkType(), entity2.GetKey(), linkage);

                break;

            case IsParentOf:

                assertLinkage(entity.GetKey(), linkage.GetLinkType(), entity3.GetKey(), linkage);

                break;

            case Created:

                assertLinkage(entity.GetKey(), linkage.GetLinkType(), entity2.GetKey(), linkage);

                break;

            default:

                fail("Unexpected Type: " + linkage.GetLinkType().getLabel());

            }
        }

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
    public void GetLinkagesForEntityNameFoundTest()
    {
        Entities entities = CreateEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.GetLinkages("Operation");

        assertEquals(3, linkages.size());

        for (XsdLinkage linkage : linkages.values())
        {
            switch (linkage.GetLinkType()) {
            case HasUseOf:

                assertLinkage(entities.Entity, entities.Entity2, linkage.GetLinkType(), linkage);

                break;

            case Created:

                assertLinkage(entities.Entity, entities.Entity2, linkage.GetLinkType(), linkage);

                break;

            case HasMember:

                assertLinkage(entities.Entity, entities.Entity4, linkage.GetLinkType(), linkage);

                break;

            default:

                fail("Unexpected Type: " + linkage.GetLinkType().getLabel());

            }
        }

        linkages = entities.Entity.GetLinkages("Mission");

        assertEquals(1, linkages.size());

        for (XsdLinkage linkage : linkages.values())
        {
            switch (linkage.GetLinkType()) {
            case IsParentOf:

                assertLinkage(entities.Entity, entities.Entity3, linkage.GetLinkType(), linkage);

                break;

            default:

                fail("Unexpected Type: " + linkage.GetLinkType().getLabel());
            }
        }
    }

    @Test
    public void GetLinkagesForEntityNameNotFoundTest()
    {

        Entities entities = CreateEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.GetLinkages("User");

        assertTrue(linkages.isEmpty());

    }

    @Test
    public void GetLinkagesForLinkTypeEntityNameFoundTest()
    {
        Entities entities = CreateEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.GetLinkages(ELinkTypes.HasUseOf, "Operation");

        assertEquals(1, linkages.size());

        for (XsdLinkage linkage : linkages.values())
        {
            switch (linkage.GetLinkType()) {
            case HasUseOf:

                assertLinkage(entities.Entity, entities.Entity2, linkage.GetLinkType(), linkage);

                break;

            default:

                fail("Unexpected Type: " + linkage.GetLinkType().getLabel());

            }
        }

    }

    @Test
    public void GetLinkagesForLinkTypeEntityNameNotFoundTest()
    {

        Entities entities = CreateEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.GetLinkages("User");

        assertTrue(linkages.isEmpty());

    }

    @Test
    public void GetLinkagesForLinkTypeFoundTest()
    {
        Entities entities = CreateEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.GetLinkages(ELinkTypes.HasUseOf);

        assertEquals(1, linkages.size());

        for (XsdLinkage linkage : linkages.values())
        {
            switch (linkage.GetLinkType()) {
            case HasUseOf:

                assertLinkage(entities.Entity, entities.Entity2, linkage.GetLinkType(), linkage);

                break;

            default:

                fail("Unexpected Type: " + linkage.GetLinkType().getLabel());

            }
        }

    }

    @Test
    public void GetLinkagesForLinkTypeNotFoundTest()
    {

        Entities entities = CreateEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.GetLinkages(ELinkTypes.IsAMemberOf);

        assertTrue(linkages.isEmpty());

    }

    @Test
    public void GetLinkagesForLinkTypeNullTest()
    {
        Entities entities = CreateEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.GetLinkages((ELinkTypes) null);

        assertTrue(linkages.isEmpty());

    }

    @Test
    public void GetLinkagesForLinkTypeEntityNameEntitySourceFoundTest()
    {
        Entities entities = CreateEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.GetLinkages(ELinkTypes.HasUseOf, "Operation", "Portal");

        assertEquals(1, linkages.size());

        for (XsdLinkage linkage : linkages.values())
        {
            switch (linkage.GetLinkType()) {
            case HasUseOf:

                assertLinkage(entities.Entity, entities.Entity2, linkage.GetLinkType(), linkage);

                break;

            default:

                fail("Unexpected Type: " + linkage.GetLinkType().getLabel());

            }
        }

    }

    @Test
    public void GetLinkagesForLinkTypeEntityNameEntitySourceNotFoundTest()
    {

        Entities entities = CreateEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.GetLinkages(ELinkTypes.HasOwnershipOf, "Operation", "Portal0");

        assertTrue(linkages.isEmpty());

    }

    @Test
    public void GetLinkagesForLinkTypesEntityNameFoundTest()
    {
        Entities entities = CreateEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.GetLinkages(Arrays.asList(ELinkTypes.HasUseOf, ELinkTypes.Created),
                                                                       "Operation");

        assertEquals(2, linkages.size());

        for (XsdLinkage linkage : linkages.values())
        {
            switch (linkage.GetLinkType()) {
            case HasUseOf:

                assertLinkage(entities.Entity, entities.Entity2, linkage.GetLinkType(), linkage);

                break;

            case Created:

                assertLinkage(entities.Entity, entities.Entity2, linkage.GetLinkType(), linkage);

                break;

            default:

                fail("Unexpected Type: " + linkage.GetLinkType().getLabel());

            }
        }

    }

    @Test
    public void GetLinkagesForLinkTypesEntityNamePartialFoundTest()
    {
        Entities entities = CreateEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.GetLinkages(Arrays.asList(ELinkTypes.HasUseOf,
                                                                                     ELinkTypes.IsAPeerOf), "Operation");

        assertEquals(1, linkages.size());

        for (XsdLinkage linkage : linkages.values())
        {
            switch (linkage.GetLinkType()) {
            case HasUseOf:

                assertLinkage(entities.Entity, entities.Entity2, linkage.GetLinkType(), linkage);

                break;

            default:

                fail("Unexpected Type: " + linkage.GetLinkType().getLabel());

            }
        }

    }

    @Test
    public void GetLinkagesForLinkTypesEntityNameNotFoundTest()
    {

        Entities entities = CreateEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.GetLinkages(Arrays.asList(ELinkTypes.HasOwnershipOf,
                                                                                     ELinkTypes.IsAPeerOf), "Operation");

        assertTrue(linkages.isEmpty());

    }

    @Test
    public void GetSectionFromXmlTest()
    {
        // Create Entity
        XsdEntity entity = XsdEntity.Create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Create Live Status Section
        XsdSection.Create(entity, "Live Status Section", true);

        // Verify Live Status Section Creation
        assertNotNull(entity.GetSection("TREXOperation/Live Status Section"));

        // Create Information Section
        XsdSection.Create(entity, "Operation Information Section", true);

        // Verify Information Section Creation
        assertNotNull(entity.GetSection("TREXOperation/Operation Information Section"));

    }

    @Test
    public void GetEntityIdParamExistsTest()
    {

        XsdEntity entity = XsdEntity.Create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First,Second",
                                            "Type1,Type2",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        List<String> type1List = entity.GetEntityId("Type1");
        assertEquals(1, type1List.size());
        assertEquals("First", type1List.get(0));
        
        List<String> type2List = entity.GetEntityId("Type2");
        assertEquals(1, type2List.size());
        assertEquals("Second", type2List.get(0));

    }

    @Test
    public void GetEntityIdParamDoesNotExistTest()
    {

        XsdEntity entity = XsdEntity.Create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First,Second",
                                            "Type1,Type2",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        assertTrue(entity.GetEntityId("Type3").isEmpty());

    }

    @Test
    public void GetEntityIdParamNullTest()
    {

        XsdEntity entity = XsdEntity.Create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First,Second",
                                            "Type1,Type2",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        assertTrue(entity.GetEntityId(null).isEmpty());

    }

    @Test
    public void GetEntityIdParamMultipleTypesTest()
    {

        XsdEntity entity = XsdEntity.Create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First,Second,Third",
                                            "Type1,Type2,Type1",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        List<String> ids = entity.GetEntityId("Type1");
        
        assertEquals("First", ids.get(0));
        assertEquals("Third", ids.get(1));

    }

    @Test
    public void SetEntityIdDoesNotExistTest()
    {
        XsdEntity entity = XsdEntity.Create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        entity.SetEntityId("Type1", "First");
        
        assertEquals("First", entity.GetEntityId());
        assertEquals("Type1", entity.GetEntityIdType());
        
    }
    
    @Test
    public void SetEntityIdDuplicateTypeTest()
    {
        
        XsdEntity entity = XsdEntity.Create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First",
                                            "Type1",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        entity.SetEntityId("Type1", "Second");
        
        assertEquals("First,Second", entity.GetEntityId());
        assertEquals("Type1,Type1", entity.GetEntityIdType());
        
    }
    
    @Test
    public void SetEntityIdSingleAddTest()
    {
        XsdEntity entity = XsdEntity.Create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First",
                                            "Type1",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        entity.SetEntityId("Type2", "Second");
        
        assertEquals("First,Second", entity.GetEntityId());
        assertEquals("Type1,Type2", entity.GetEntityIdType());

    }
    
    @Test
    public void SetEntityIdMultipleAddTest()
    {
        XsdEntity entity = XsdEntity.Create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First,Second",
                                            "Type1,Type2",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        entity.SetEntityId("Type3", "Third");
        
        assertEquals("First,Second,Third", entity.GetEntityId());
        assertEquals("Type1,Type2,Type3", entity.GetEntityIdType());
        
    }

    @Test
    public void SetEntityIDNullTypeTest()
    {
        XsdEntity entity = XsdEntity.Create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First,Second",
                                            "Type1,Type2",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        assertFalse(entity.SetEntityId(null, "Third"));
        assertEquals("First,Second", entity.GetEntityId());
        assertEquals("Type1,Type2", entity.GetEntityIdType());
        
    }
 
    @Test
    public void SetEntityIDNullNameTest()
    {
        XsdEntity entity = XsdEntity.Create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First,Second",
                                            "Type1,Type2",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        assertFalse(entity.SetEntityId("Type3", null));
        assertEquals("First,Second", entity.GetEntityId());
        assertEquals("Type1,Type2", entity.GetEntityIdType());
        
    }

    @Test
    public void SetEntityIDNullBothTest()
    {
        XsdEntity entity = XsdEntity.Create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First,Second",
                                            "Type1,Type2",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        assertFalse(entity.SetEntityId(null, null));
        assertEquals("First,Second", entity.GetEntityId());
        assertEquals("Type1,Type2", entity.GetEntityIdType());
        
    }
    
    @Test
    public void MarkAsDeletedNotDeletedYetTest()
    {
        XsdEntity entity = XsdEntity.Create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First,Second",
                                            "Type1,Type2",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        assertEquals(ECoalesceDataObjectStatus.ACTIVE, entity.GetStatus());
        
        entity.MarkAsDeleted();
        
        assertEquals(ECoalesceDataObjectStatus.DELETED, entity.GetStatus());
        
    }
    
    @Test
    public void MarkAsDeletedAlreadyDeletedTest()
    {
        XsdEntity entity = XsdEntity.Create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First,Second",
                                            "Type1,Type2",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        entity.SetStatus(ECoalesceDataObjectStatus.DELETED);
        
        entity.MarkAsDeleted();
        
        assertEquals(ECoalesceDataObjectStatus.DELETED, entity.GetStatus());
        
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

    private void assertLinkage(String entity1Key, ELinkTypes type, String entity2Key, XsdLinkage linkage)
    {
        assertEquals(entity1Key, linkage.GetEntity1Key());
        assertEquals(type, linkage.GetLinkType());
        assertEquals(entity2Key, linkage.GetEntity2Key());

    }

    private void assertLinkage(XsdEntity entity1, XsdEntity entity2, ELinkTypes type, XsdLinkage linkage)
    {
        assertEquals(entity1.GetKey(), linkage.GetEntity1Key());
        assertEquals(entity2.GetKey(), linkage.GetEntity2Key());

        assertEquals(type, linkage.GetLinkType());

    }

    private class Entities {

        public XsdEntity Entity;
        public XsdEntity Entity2;
        public XsdEntity Entity3;
        public XsdEntity Entity4;

    }

    private Entities CreateEntityLinkages()
    {
        Entities entities = new Entities();

        entities.Entity = XsdEntity.Create("Operation", "Portal", "1.1.1.1", "ID", "Type");

        entities.Entity2 = XsdEntity.Create("Operation", "Portal", "1.2.3.4", "Id2", "Type2");

        entities.Entity3 = XsdEntity.Create("Mission", "Portal2", "2.3.4.5", "Id3", "Type3");

        entities.Entity4 = XsdEntity.Create("Operation", "Portal2", "3.4.5.6", "Id4", "Type4");

        assertTrue(EntityLinkHelper.LinkEntities(entities.Entity, ELinkTypes.HasUseOf, entities.Entity2, false));
        assertTrue(EntityLinkHelper.LinkEntities(entities.Entity, ELinkTypes.IsParentOf, entities.Entity3, false));
        assertTrue(EntityLinkHelper.LinkEntities(entities.Entity2, ELinkTypes.WasCreatedBy, entities.Entity, false));
        assertTrue(EntityLinkHelper.LinkEntities(entities.Entity4, ELinkTypes.IsAMemberOf, entities.Entity, false));

        return entities;

    }

}
