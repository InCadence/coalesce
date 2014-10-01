package Coalesce.Framework.DataModel;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NullArgumentException;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xml.sax.SAXException;

import Coalesce.Common.Exceptions.CoalesceException;
import Coalesce.Common.Helpers.EntityLinkHelper;
import Coalesce.Common.Helpers.GUIDHelper;
import Coalesce.Common.Helpers.JodaDateTimeHelper;
import Coalesce.Common.UnitTest.CoalesceAssert;
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

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
    public void createFromXmlTest()
    {

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        String title = entity.getTitle();
        assertEquals("NORTHCOM Volunteer Background Checks Changed, NORTHCOM Volunteer Background Checks", title);
        assertEquals(4, entity.getLinkages().values().size());
        assertEquals(2, entity.getSections().size());
        assertEquals(1,
                     ((XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH)).getCount());
        assertEquals(16,
                     ((XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH)).getFieldDefinitions().size());
        assertEquals(16,
                     entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORD_PATH).getChildDataObjects().size());
    }

    @Test
    public void createFromXmlEmptyTest()
    {
        XsdEntity entity = XsdEntity.create("");

        assertEmptyEntity(entity);

    }

    @Test
    public void createFromXmlWhitespaceTest()
    {
        XsdEntity entity = XsdEntity.create("  ");

        assertEmptyEntity(entity);

    }

    @Test
    public void createFromXmlNullTest()
    {
        XsdEntity entity = XsdEntity.create(null);

        assertEmptyEntity(entity);
    }

    @Test
    public void createFromXmlInvalidXmlTest()
    {
        XsdEntity entity = XsdEntity.create("invalid format");

        assertNull(entity);

    }

    @Test
    public void createFromXmlWithTitleNullTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION, null);

        assertEquals("TREX Portal", entity.getTitle());
    }

    @Test
    public void createFromXmlWithTitleEmptyStringTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION, "");

        assertEquals("TREX Portal", entity.getTitle());
    }

    @Test
    public void createFromXmlWithTitleWhiteSpaceTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION, "   ");

        assertEquals("TREX Portal", entity.getTitle());
    }

    @Test
    public void createFromXmlWithTitleNewXpathTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION,
                                            "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionName");

        assertEquals(((XsdField) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH)).getValue(),
                     entity.getTitle());
    }

    @Test
    public void createFromXmlWithTitleNewTitleTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION, "New Mission Title");

        assertEquals("New Mission Title", entity.getTitle());
    }

    @Test
    public void createDetailedWithoutTitleTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", "Portal", "1.1.1.1", "Entity Id", "Entity Type");

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.getName());
        assertEquals("Portal", entity.getSource());
        assertEquals("1.1.1.1", entity.getVersion());
        assertEquals("Entity Id", entity.getEntityId());
        assertEquals("Entity Type", entity.getEntityIdType());

        assertEquals("Portal", entity.getTitle());

    }

    @Test
    public void createDetailedWithoutTitleNullNameTest()
    {
        XsdEntity entity = XsdEntity.create(null, "Portal", "1.1.1.1", "Entity Id", "Entity Type");

        assertEmptyEntity(entity);

        assertEquals("", entity.getName());
        assertEquals("Portal", entity.getSource());
        assertEquals("1.1.1.1", entity.getVersion());
        assertEquals("Entity Id", entity.getEntityId());
        assertEquals("Entity Type", entity.getEntityIdType());

        assertEquals("Portal", entity.getTitle());

    }

    @Test
    public void createDetailedWithoutTitleNullSourceTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", null, "1.1.1.1", "Entity Id", "Entity Type");

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.getName());
        assertEquals("", entity.getSource());
        assertEquals("1.1.1.1", entity.getVersion());
        assertEquals("Entity Id", entity.getEntityId());
        assertEquals("Entity Type", entity.getEntityIdType());

        assertEquals("", entity.getTitle());

    }

    @Test
    public void createDetailedWithoutTitleNullVersionTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", "Portal", null, "Entity Id", "Entity Type");

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.getName());
        assertEquals("Portal", entity.getSource());
        assertEquals("", entity.getVersion());
        assertEquals("Entity Id", entity.getEntityId());
        assertEquals("Entity Type", entity.getEntityIdType());

        assertEquals("Portal", entity.getTitle());

    }

    @Test
    public void createDetailedWithoutTitleNullEntityIdTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", "Portal", "1.1.1.1", null, "Entity Type");

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.getName());
        assertEquals("Portal", entity.getSource());
        assertEquals("1.1.1.1", entity.getVersion());
        assertEquals("", entity.getEntityId());
        assertEquals("Entity Type", entity.getEntityIdType());

        assertEquals("Portal", entity.getTitle());

    }

    @Test
    public void createDetailedWithoutTitleNullEntityTypeTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", "Portal", "1.1.1.1", "Entity Id", null);

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.getName());
        assertEquals("Portal", entity.getSource());
        assertEquals("1.1.1.1", entity.getVersion());
        assertEquals("Entity Id", entity.getEntityId());
        assertEquals("", entity.getEntityIdType());

        assertEquals("Portal", entity.getTitle());

    }

    @Test
    public void createDetailedWithTitleTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", "Portal", "1.1.1.1", "Entity Id", "Entity Type", "A New Title");

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.getName());
        assertEquals("Portal", entity.getSource());
        assertEquals("1.1.1.1", entity.getVersion());
        assertEquals("Entity Id", entity.getEntityId());
        assertEquals("Entity Type", entity.getEntityIdType());

        assertEquals("A New Title", entity.getTitle());

    }

    @Test
    public void createDetailedWithTitleNullNameTest()
    {
        XsdEntity entity = XsdEntity.create(null, "Portal", "1.1.1.1", "Entity Id", "Entity Type", "A New Title");

        assertEmptyEntity(entity);

        assertEquals("", entity.getName());
        assertEquals("Portal", entity.getSource());
        assertEquals("1.1.1.1", entity.getVersion());
        assertEquals("Entity Id", entity.getEntityId());
        assertEquals("Entity Type", entity.getEntityIdType());

        assertEquals("A New Title", entity.getTitle());

    }

    @Test
    public void createDetailedWithTitleNullSourceTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", null, "1.1.1.1", "Entity Id", "Entity Type", "A New Title");

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.getName());
        assertEquals("", entity.getSource());
        assertEquals("1.1.1.1", entity.getVersion());
        assertEquals("Entity Id", entity.getEntityId());
        assertEquals("Entity Type", entity.getEntityIdType());

        assertEquals("A New Title", entity.getTitle());

    }

    @Test
    public void createDetailedWithTitleNullVersionTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", "Portal", null, "Entity Id", "Entity Type", "A New Title");

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.getName());
        assertEquals("Portal", entity.getSource());
        assertEquals("", entity.getVersion());
        assertEquals("Entity Id", entity.getEntityId());
        assertEquals("Entity Type", entity.getEntityIdType());

        assertEquals("A New Title", entity.getTitle());

    }

    @Test
    public void createDetailedWithTitleNullEntityIdTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", "Portal", "1.1.1.1", null, "Entity Type", "A New Title");

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.getName());
        assertEquals("Portal", entity.getSource());
        assertEquals("1.1.1.1", entity.getVersion());
        assertEquals("", entity.getEntityId());
        assertEquals("Entity Type", entity.getEntityIdType());

        assertEquals("A New Title", entity.getTitle());

    }

    @Test
    public void createDetailedWithTitleNullEntityTypeTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", "Portal", "1.1.1.1", "Entity Id", null, "A New Title");

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.getName());
        assertEquals("Portal", entity.getSource());
        assertEquals("1.1.1.1", entity.getVersion());
        assertEquals("Entity Id", entity.getEntityId());
        assertEquals("", entity.getEntityIdType());

        assertEquals("A New Title", entity.getTitle());

    }

    @Test
    public void createDetailedWithTitleNullTitleTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", "Portal", "1.1.1.1", "Entity Id", "Entity Id Type", null);

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.getName());
        assertEquals("Portal", entity.getSource());
        assertEquals("1.1.1.1", entity.getVersion());
        assertEquals("Entity Id", entity.getEntityId());
        assertEquals("Entity Id Type", entity.getEntityIdType());

        assertEquals("Portal", entity.getTitle());

    }

    @Test
    public void createDetailedWithTitleEmptyTitleTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", "Portal", "1.1.1.1", "Entity Id", "Entity Id Type", "");

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.getName());
        assertEquals("Portal", entity.getSource());
        assertEquals("1.1.1.1", entity.getVersion());
        assertEquals("Entity Id", entity.getEntityId());
        assertEquals("Entity Id Type", entity.getEntityIdType());

        assertEquals("Portal", entity.getTitle());

    }

    @Test
    public void createDetailedWithTitleWhiteSpaceTitleTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", "Portal", "1.1.1.1", "Entity Id", "Entity Id Type", "   ");

        assertEmptyEntity(entity);

        assertEquals("Operation", entity.getName());
        assertEquals("Portal", entity.getSource());
        assertEquals("1.1.1.1", entity.getVersion());
        assertEquals("Entity Id", entity.getEntityId());
        assertEquals("Entity Id Type", entity.getEntityIdType());

        assertEquals("Portal", entity.getTitle());

    }

    @Test
    public void createTREXOperationWithStaticTest()
    {

        // Create Entity
        XsdEntity entity = XsdEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Verify Entity Creation
        assertEquals("TREXOperation", entity.getName());
        assertEquals("TREX Portal", entity.getSource());
        assertNotNull(entity.getLinkageSection());
        assertTrue(entity.getSections().isEmpty());

        entity.setOtherAttribute("testnewattribute", "test");

        // Verify Link Section Creation
        assertTrue(entity.getLinkageSection().getName().equalsIgnoreCase("linkages"));

        // Create Live Status Section
        XsdSection liveSection = XsdSection.create(entity, "Live Status Section", true);
        XsdRecordset liveRecordSet = XsdRecordset.create(liveSection, "Live Status Recordset");

        // Verify Live Status Section Creation
        assertTrue(entity.getSection("TREXOperation/Live Status Section").getName().equalsIgnoreCase("Live Status Section"));

        XsdFieldDefinition.create(liveRecordSet, "CurrentStatus", ECoalesceFieldDataTypes.StringType);

        // Create Information Section
        XsdSection informationSection = XsdSection.create(entity, "Operation Information Section", true);
        XsdRecordset informationRecordSet = XsdRecordset.create(informationSection, "Operation Information Recordset");

        XsdFieldDefinition.create(informationRecordSet, "OperationName", ECoalesceFieldDataTypes.StringType);

        // Verify Information Section Creation
        assertNotNull(entity.getSection("TREXOperation/Operation Information Section"));

        // Serialize
        String entityXml = entity.toXml();

        // Deserialize
        XsdEntity entity2 = new XsdEntity();
        assertTrue(entity2.initialize(entityXml));

        // Verify Custom Attribute
        assertTrue(entity2.getOtherAttribute("testnewattribute").equals("test"));

        // Verify Entity
        assertTrue(entity2.getSource().equals("TREX Portal"));

        // Verify Link Section
        assertNotNull(entity2.getLinkageSection());

        // Verify Live Status Section
        assertNotNull(entity2.getSection("TREXOperation/Live Status Section"));

        // Verify Information Section
        assertNotNull(entity2.getSection("TREXOperation/Operation Information Section"));

    }

    @Test
    public void createTREXOperationWithInstanceCreateTest()
    {

        // Create Entity
        XsdEntity entity = XsdEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Verify Entity Creation
        assertTrue(entity.getSource().equals("TREX Portal"));

        entity.setOtherAttribute("testnewattribute", "test");

        // Verify Link Section Creation
        assertTrue(entity.getLinkageSection().getName().equalsIgnoreCase("linkages"));

        // Create Live Status Section
        XsdSection liveSection = entity.createSection("Live Status Section", true);
        XsdRecordset liveRecordSet = liveSection.createRecordset("Live Status Recordset");

        // Verify Live Status Section Creation
        assertTrue(entity.getSection("TREXOperation/Live Status Section").getName().equalsIgnoreCase("Live Status Section"));

        liveRecordSet.createFieldDefinition("CurrentStatus", ECoalesceFieldDataTypes.StringType);

        // Create Information Section
        XsdSection informationSection = entity.createSection("Operation Information Section", true);
        XsdRecordset informationRecordSet = informationSection.createRecordset("Operation Information Recordset");

        informationRecordSet.createFieldDefinition("OperationName", ECoalesceFieldDataTypes.StringType);

        // Verify Information Section Creation
        assertNotNull(entity.getSection("TREXOperation/Operation Information Section"));

        // Serialize
        String entityXml = entity.toXml();

        // Deserialize
        XsdEntity entity2 = new XsdEntity();
        assertTrue(entity2.initialize(entityXml));

        // Verify Custom Attribute
        assertTrue(entity2.getOtherAttribute("testnewattribute").equals("test"));

        // Verify Entity
        assertTrue(entity2.getSource().equals("TREX Portal"));

        // Verify Link Section
        assertNotNull(entity2.getLinkageSection());

        // Verify Live Status Section
        assertNotNull(entity2.getSection("TREXOperation/Live Status Section"));

        // Verify Information Section
        assertNotNull(entity2.getSection("TREXOperation/Operation Information Section"));

    }

    @Test
    public void initializeFromXmlTest()
    {

        XsdEntity entity = new XsdEntity();

        assertTrue(entity.initialize(CoalesceTypeInstances.TEST_MISSION));

        String title = entity.getTitle();
        assertEquals("NORTHCOM Volunteer Background Checks Changed, NORTHCOM Volunteer Background Checks", title);
        assertEquals(4, entity.getLinkages().values().size());
        assertEquals(2, entity.getSections().size());
        assertEquals(1,
                     ((XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH)).getCount());
        assertEquals(16,
                     ((XsdRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH)).getFieldDefinitions().size());
        assertEquals(16,
                     entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORD_PATH).getChildDataObjects().size());
    }

    @Test
    public void initializeFromXmlEmptyTest()
    {
        XsdEntity entity = new XsdEntity();

        assertTrue(entity.initialize(""));

        assertEmptyEntity(entity);

    }

    @Test
    public void initializeFromXmlWhitespaceTest()
    {
        XsdEntity entity = new XsdEntity();

        assertTrue(entity.initialize("  "));

        assertEmptyEntity(entity);

    }

    @Test
    public void initializeFromXmlNullTest()
    {
        XsdEntity entity = new XsdEntity();

        assertTrue(entity.initialize(null));

        assertEmptyEntity(entity);
    }

    @Test
    public void initializeFromXmlInvalidXmlTest()
    {
        XsdEntity entity = new XsdEntity();

        assertFalse(entity.initialize("invalid format"));

    }

    @Test
    public void initializeTest()
    {

        XsdEntity entity = new XsdEntity();

        assertTrue(entity.initialize());

        assertEmptyEntity(entity);

    }

    @Test
    public void getKeyFromXmlTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("62857EF8-3930-4F0E-BAE3-093344EBF389", entity.getKey());

    }

    @Test
    public void getKeyNewTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertNotNull(GUIDHelper.isValid(entity.getKey()));

    }

    @Test
    public void getNameFromXmlTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("TREXMission", entity.getName());

    }

    @Test
    public void getNameNewTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertEquals("Operation", entity.getName());

    }

    @Test
    public void setNameFromXmlTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        entity.setName("Other Name");

        assertEquals("Other Name", entity.getName());

    }

    @Test
    public void setNameNewTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        entity.setName("Other Name");

        assertEquals("Other Name", entity.getName());

    }

    @Test
    public void getTypeFromXmlTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("entity", entity.getType());

    }

    @Test
    public void getTypeNewTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertEquals("entity", entity.getType());

    }

    @Test
    public void getSourceFromXmlTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("TREX Portal", entity.getSource());

    }

    @Test
    public void getSourceNewTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertEquals("Portal", entity.getSource());

    }

    @Test
    public void setSourceFromXmlTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        entity.setSource("New Source");

        assertEquals("New Source", entity.getSource());

    }

    @Test
    public void setSourceNewTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        entity.setSource("New Source");

        assertEquals("New Source", entity.getSource());

    }

    @Test
    public void getVersionFromXmlTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("1.0.0.0", entity.getVersion());

    }

    @Test
    public void getVersionNewTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertEquals("1.1.1.1", entity.getVersion());

    }

    @Test
    public void setVersionFromXmlTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        entity.setVersion("2.3.4.5");

        assertEquals("2.3.4.5", entity.getVersion());

    }

    @Test
    public void setVersionNewTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        entity.setVersion("2.3.4.5");

        assertEquals("2.3.4.5", entity.getVersion());

    }

    @Test
    public void getEntityIdFromXmlTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("", entity.getEntityId());

    }

    @Test
    public void getEntityIdNewTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertEquals("Entity ID", entity.getEntityId());

    }

    @Test
    public void setEntityIdFromXmlTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        entity.setEntityId("New Entity ID");

        assertEquals("New Entity ID", entity.getEntityId());

    }

    @Test
    public void setEntityIdNewTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        entity.setEntityId("New Entity ID");

        assertEquals("New Entity ID", entity.getEntityId());

    }

    @Test
    public void getEntityIdTypeFromXmlTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("", entity.getEntityIdType());

    }

    @Test
    public void getEntityIdTypeNewTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertEquals("Entity Id Type", entity.getEntityIdType());

    }

    @Test
    public void setEntityIdTypeFromXmlTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        entity.setEntityIdType("New Entity Id Type");

        assertEquals("New Entity Id Type", entity.getEntityIdType());

    }

    @Test
    public void setEntityIdTypeNewTest()
    {
        XsdEntity entity = XsdEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertEquals("Entity Id Type", entity.getEntityIdType());

    }

    @Test
    public void getTitleXPathInvalidTest()
    {
        XsdEntity entity = XsdEntity.create("Operation",
                                            "Portal",
                                            "1.1.1.1",
                                            "Entity Id",
                                            "Entity Id Type",
                                            "Operation/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionName,TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/IncidentTitle");

        String title = entity.getTitle();

        assertEquals("Portal", title);

    }

    @Test
    public void getTitleWithoutXpathTest()
    {

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_XPATH_TITLE);

        String title = entity.getTitle();

        assertEquals("TREX Portal", title);

    }

    @Test
    public void getTitleUpdateThatUsesXpathTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdField missionName = (XsdField) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
        XsdField incidentTitle = (XsdField) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_INCIDENT_TITLE_PATH);

        missionName.setValue("Mission Name");
        incidentTitle.setValue("Incident Title");

        String title = entity.getTitle();
        assertEquals("Mission Name, Incident Title", title);

    }

    @Test
    public void setTitleThatDoesNotUseXpathTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_XPATH_TITLE);

        entity.setTitle("Mission Entity Title");

        assertEquals("Mission Entity Title", entity.getTitle());
    }

    @Test
    public void setTitleThatUsesXpathTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        entity.setTitle("Mission Entity Title");

        assertEquals("Mission Entity Title", entity.getTitle());
    }

    @Test
    public void getDateCreatedFromXmlTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertEquals(JodaDateTimeHelper.fromXmlDateTimeUTC("2014-05-02T14:33:51.851575Z"), entity.getDateCreated());

    }

    @Test
    public void getDateCreatedNewTest()
    {
        XsdEntity entity = new XsdEntity();
        entity.initialize();

        DateTime now = JodaDateTimeHelper.nowInUtc();

        assertTrue(Math.abs(now.getMillis() - entity.getDateCreated().getMillis()) < 5);
    }

    @Test
    public void setDateCreatedFromXmlTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        DateTime now = JodaDateTimeHelper.nowInUtc();

        entity.setDateCreated(now);

        assertEquals(now, entity.getDateCreated());

    }

    @Test
    public void setDateCreatedNewTest()
    {
        XsdEntity entity = new XsdEntity();
        entity.initialize();

        DateTime sixDaysAgo = JodaDateTimeHelper.nowInUtc().minusDays(6);

        entity.setDateCreated(sixDaysAgo);

        assertEquals(sixDaysAgo, entity.getDateCreated());
    }

    @Test
    public void getLastModifiedFromXmlTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertEquals(JodaDateTimeHelper.fromXmlDateTimeUTC("2014-05-20T16:17:13.2293139Z"), entity.getLastModified());

    }

    @Test
    public void getLastModifiedNewTest()
    {
        XsdEntity entity = new XsdEntity();
        entity.initialize();

        DateTime now = JodaDateTimeHelper.nowInUtc();

        assertTrue(Math.abs(now.getMillis() - entity.getLastModified().getMillis()) < 5);
    }

    @Test
    public void setLastModifiedFromXmlTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        DateTime now = JodaDateTimeHelper.nowInUtc();

        entity.setLastModified(now);

        assertEquals(now, entity.getLastModified());

    }

    @Test
    public void setLastModifiedNewTest()
    {
        XsdEntity entity = new XsdEntity();
        entity.initialize();

        DateTime sixDaysAgo = JodaDateTimeHelper.nowInUtc().minusDays(6);

        entity.setLastModified(sixDaysAgo);

        assertEquals(sixDaysAgo, entity.getLastModified());
    }

    @Test
    public void arbitraryAttributesTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        String entityXml = entity.toXml();

        assertTrue(entityXml.contains("anthony=\"Test\""));
        assertTrue(entity.getOtherAttribute("anthony").equals("Test"));

    }

    @Test
    public void getLinkagesFromXmlTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        Map<String, XsdLinkage> linkages = entity.getLinkages();

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
    public void getLinkagesNewTest()
    {
        XsdEntity entity = new XsdEntity();
        entity.initialize();

        XsdEntity entity2 = new XsdEntity();
        entity2.initialize();

        XsdEntity entity3 = new XsdEntity();
        entity3.initialize();

        assertTrue(EntityLinkHelper.linkEntities(entity, ELinkTypes.HasUseOf, entity2, false));
        assertTrue(EntityLinkHelper.linkEntities(entity, ELinkTypes.IsParentOf, entity3, false));
        assertTrue(EntityLinkHelper.linkEntities(entity2, ELinkTypes.WasCreatedBy, entity, false));

        Map<String, XsdLinkage> linkages = entity.getLinkages();

        assertEquals(3, linkages.size());

        for (XsdLinkage linkage : linkages.values())
        {
            switch (linkage.getLinkType()) {
            case HasUseOf:

                assertLinkage(entity.getKey(), linkage.getLinkType(), entity2.getKey(), linkage);

                break;

            case IsParentOf:

                assertLinkage(entity.getKey(), linkage.getLinkType(), entity3.getKey(), linkage);

                break;

            case Created:

                assertLinkage(entity.getKey(), linkage.getLinkType(), entity2.getKey(), linkage);

                break;

            default:

                fail("Unexpected Type: " + linkage.getLinkType().getLabel());

            }
        }

    }

    @Test
    public void createNewEntityTemplateTest() throws SAXException, IOException
    {
        // Test Entity
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        // Run Test
        CoalesceEntityTemplateTest.testTemplate(entity.createNewEntityTemplate());

    }

    @Test
    public void createSectionTest()
    {
        // Create Entity
        XsdEntity entity = XsdEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Create Live Status Section
        assertNull(entity.getSection("TREXOperation/Live Status Section"));

        XsdSection liveSection = entity.createSection("Live Status Section");
        assertEquals(liveSection, entity.getSection("TREXOperation/Live Status Section"));
        assertFalse(liveSection.getNoIndex());
    }

    @Test
    public void createSectionExistingNoIndexTrueTest()
    {
        // Create Entity
        XsdEntity entity = XsdEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Create Live Status Section
        assertNull(entity.getSection("TREXOperation/Live Status Section"));

        XsdSection liveSection = entity.createSection("Live Status Section", true);
        assertEquals(liveSection, entity.getSection("TREXOperation/Live Status Section"));
        assertTrue(liveSection.getNoIndex());

        XsdSection liveSection2 = entity.createSection("Live Status Section");
        assertEquals(liveSection2, entity.getSection("TREXOperation/Live Status Section"));
        assertEquals(liveSection, liveSection2);
        assertFalse(liveSection.getNoIndex());
        assertFalse(liveSection2.getNoIndex());

    }

    @Test
    public void createSectionFromXmlTest()
    {

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertNull(entity.getSection("TREXMission/A New Section"));

        XsdSection newSection = entity.createSection("A New Section");

        assertNotNull(newSection);
        assertEquals(newSection, entity.getSection("TREXMission/A New Section"));
        assertFalse(newSection.getNoIndex());

    }

    @Test
    public void createSectionWithNoIndexFalseTest()
    {
        // Create Entity
        XsdEntity entity = XsdEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Create Live Status Section
        assertNull(entity.getSection("TREXOperation/Live Status Section"));

        XsdSection liveSection = entity.createSection("Live Status Section", false);
        assertEquals(liveSection, entity.getSection("TREXOperation/Live Status Section"));
        assertFalse(liveSection.getNoIndex());
    }

    @Test
    public void createSectionWithNoIndexTrueTest()
    {
        // Create Entity
        XsdEntity entity = XsdEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Create Live Status Section
        assertNull(entity.getSection("TREXOperation/Live Status Section"));

        XsdSection liveSection = entity.createSection("Live Status Section", true);
        assertEquals(liveSection, entity.getSection("TREXOperation/Live Status Section"));
        assertTrue(liveSection.getNoIndex());

    }

    @Test
    public void createSectionWithNoIndexTrueForExistingNoIndexFalseTest()
    {
        // Create Entity
        XsdEntity entity = XsdEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Create Live Status Section
        assertNull(entity.getSection("TREXOperation/Live Status Section"));

        XsdSection liveSection = entity.createSection("Live Status Section", false);
        assertEquals(liveSection, entity.getSection("TREXOperation/Live Status Section"));
        assertFalse(liveSection.getNoIndex());

        XsdSection liveSection2 = entity.createSection("Live Status Section", true);
        assertEquals(liveSection2, entity.getSection("TREXOperation/Live Status Section"));
        assertEquals(liveSection, liveSection2);
        assertTrue(liveSection.getNoIndex());
        assertTrue(liveSection2.getNoIndex());

    }

    @Test
    public void createSectionWithNoIndexTrueFromXmlTest()
    {

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertNull(entity.getSection("TREXMission/A New Section"));

        XsdSection newSection = entity.createSection("A New Section", true);

        assertNotNull(newSection);
        assertEquals(newSection, entity.getSection("TREXMission/A New Section"));
        assertTrue(newSection.getNoIndex());

        String entityXml = entity.toXml();

        XsdEntity desEntity = XsdEntity.create(entityXml);

        XsdSection desSection = desEntity.getSection("TREXMission/A New Section");
        assertEquals(newSection.getKey(), desSection.getKey());
        assertEquals(newSection.getName(), desSection.getName());
        assertEquals(newSection.getNoIndex(), desSection.getNoIndex());
    }

    @Test
    public void createSectionWithNoIndexFalseFromXmlExistingTest()
    {

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdSection liveSection = entity.getSection("TREXMission/Live Status Section");
        XsdSection informationSection = entity.getSection("TREXMission/Mission Information Section");

        XsdSection createdLiveSection = entity.createSection("Live Status Section");
        XsdSection createdInformationSection = entity.createSection("Mission Information Section");

        assertEquals(liveSection, createdLiveSection);
        assertEquals(informationSection, createdInformationSection);

    }

    @Test
    public void getSectionsFromXmlTest()
    {

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        Map<String, XsdSection> sections = entity.getSections();

        XsdSection liveSection = sections.get("85CB4256-4CC2-4F96-A03D-5EF880989822");

        assertNotNull(liveSection);
        assertEquals("85CB4256-4CC2-4F96-A03D-5EF880989822", liveSection.getKey());

        XsdSection informationSection = sections.get("383EA645-E695-4E75-ADA6-0C79BEC09A18");
        assertNotNull(informationSection);
        assertEquals("383EA645-E695-4E75-ADA6-0C79BEC09A18", informationSection.getKey());

    }

    @Test
    public void getSectionsEmptyTest()
    {

        XsdEntity entity = XsdEntity.create("");

        Map<String, XsdSection> sections = entity.getSections();

        assertTrue(sections.isEmpty());

    }

    @Test
    public void getSectionsManuallyCreatedTest()
    {

        // Create Entity
        XsdEntity entity = XsdEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Create Live Status Section
        XsdSection liveSection = entity.createSection("Live Status Section", true);

        // Create Information Section
        XsdSection informationSection = entity.createSection("Operation Information Section", true);

        Map<String, XsdSection> sections = entity.getSections();

        assertEquals(liveSection, sections.get(liveSection.getKey()));
        assertEquals(informationSection, sections.get(informationSection.getKey()));

    }

    @Test
    public void getLinkageSectionFromXmlTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdLinkageSection linkageSection = entity.getLinkageSection();

        assertEquals("F4F126AF-4658-4D7F-A67F-4833F7EADDC3", linkageSection.getKey());

    }

    @Test
    public void getLinkageSectionEmptyEntityTest()
    {

        // Create Entity
        XsdEntity entity = XsdEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        XsdLinkageSection entityLinkageSection = entity.getLinkageSection();
        assertNotNull(entityLinkageSection);

    }

    @Test
    public void getLinkagesForEntityNameFoundTest()
    {
        Entities entities = createEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.getLinkages("Operation");

        assertEquals(3, linkages.size());

        for (XsdLinkage linkage : linkages.values())
        {
            switch (linkage.getLinkType()) {
            case HasUseOf:

                assertLinkage(entities.Entity, entities.Entity2, linkage.getLinkType(), linkage);

                break;

            case Created:

                assertLinkage(entities.Entity, entities.Entity2, linkage.getLinkType(), linkage);

                break;

            case HasMember:

                assertLinkage(entities.Entity, entities.Entity4, linkage.getLinkType(), linkage);

                break;

            default:

                fail("Unexpected Type: " + linkage.getLinkType().getLabel());

            }
        }

        linkages = entities.Entity.getLinkages("Mission");

        assertEquals(1, linkages.size());

        for (XsdLinkage linkage : linkages.values())
        {
            switch (linkage.getLinkType()) {
            case IsParentOf:

                assertLinkage(entities.Entity, entities.Entity3, linkage.getLinkType(), linkage);

                break;

            default:

                fail("Unexpected Type: " + linkage.getLinkType().getLabel());
            }
        }
    }

    @Test
    public void getLinkagesForEntityNameNotFoundTest()
    {

        Entities entities = createEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.getLinkages("User");

        assertTrue(linkages.isEmpty());

    }

    @Test
    public void getLinkagesForLinkTypeEntityNameFoundTest()
    {
        Entities entities = createEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.getLinkages(ELinkTypes.HasUseOf, "Operation");

        assertEquals(1, linkages.size());

        for (XsdLinkage linkage : linkages.values())
        {
            switch (linkage.getLinkType()) {
            case HasUseOf:

                assertLinkage(entities.Entity, entities.Entity2, linkage.getLinkType(), linkage);

                break;

            default:

                fail("Unexpected Type: " + linkage.getLinkType().getLabel());

            }
        }

    }

    @Test
    public void getLinkagesForLinkTypeEntityNameNotFoundTest()
    {

        Entities entities = createEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.getLinkages("User");

        assertTrue(linkages.isEmpty());

    }

    @Test
    public void getLinkagesForLinkTypeFoundTest()
    {
        Entities entities = createEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.getLinkages(ELinkTypes.HasUseOf);

        assertEquals(1, linkages.size());

        for (XsdLinkage linkage : linkages.values())
        {
            switch (linkage.getLinkType()) {
            case HasUseOf:

                assertLinkage(entities.Entity, entities.Entity2, linkage.getLinkType(), linkage);

                break;

            default:

                fail("Unexpected Type: " + linkage.getLinkType().getLabel());

            }
        }

    }

    @Test
    public void getLinkagesForLinkTypeNotFoundTest()
    {

        Entities entities = createEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.getLinkages(ELinkTypes.IsAMemberOf);

        assertTrue(linkages.isEmpty());

    }

    @Test
    public void getLinkagesForLinkTypeNullTest()
    {
        Entities entities = createEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.getLinkages((ELinkTypes) null);

        assertTrue(linkages.isEmpty());

    }

    @Test
    public void getLinkagesForLinkTypeEntityNameEntitySourceFoundTest()
    {
        Entities entities = createEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.getLinkages(ELinkTypes.HasUseOf, "Operation", "Portal");

        assertEquals(1, linkages.size());

        for (XsdLinkage linkage : linkages.values())
        {
            switch (linkage.getLinkType()) {
            case HasUseOf:

                assertLinkage(entities.Entity, entities.Entity2, linkage.getLinkType(), linkage);

                break;

            default:

                fail("Unexpected Type: " + linkage.getLinkType().getLabel());

            }
        }

    }

    @Test
    public void getLinkagesForLinkTypeEntityNameEntitySourceNotFoundTest()
    {

        Entities entities = createEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.getLinkages(ELinkTypes.HasOwnershipOf, "Operation", "Portal0");

        assertTrue(linkages.isEmpty());

    }

    @Test
    public void getLinkagesForLinkTypesEntityNameFoundTest()
    {
        Entities entities = createEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.getLinkages(Arrays.asList(ELinkTypes.HasUseOf, ELinkTypes.Created),
                                                                       "Operation");

        assertEquals(2, linkages.size());

        for (XsdLinkage linkage : linkages.values())
        {
            switch (linkage.getLinkType()) {
            case HasUseOf:

                assertLinkage(entities.Entity, entities.Entity2, linkage.getLinkType(), linkage);

                break;

            case Created:

                assertLinkage(entities.Entity, entities.Entity2, linkage.getLinkType(), linkage);

                break;

            default:

                fail("Unexpected Type: " + linkage.getLinkType().getLabel());

            }
        }

    }

    @Test
    public void getLinkagesForLinkTypesEntityNamePartialFoundTest()
    {
        Entities entities = createEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.getLinkages(Arrays.asList(ELinkTypes.HasUseOf,
                                                                                     ELinkTypes.IsAPeerOf), "Operation");

        assertEquals(1, linkages.size());

        for (XsdLinkage linkage : linkages.values())
        {
            switch (linkage.getLinkType()) {
            case HasUseOf:

                assertLinkage(entities.Entity, entities.Entity2, linkage.getLinkType(), linkage);

                break;

            default:

                fail("Unexpected Type: " + linkage.getLinkType().getLabel());

            }
        }

    }

    @Test
    public void getLinkagesForLinkTypesEntityNameNotFoundTest()
    {

        Entities entities = createEntityLinkages();

        Map<String, XsdLinkage> linkages = entities.Entity.getLinkages(Arrays.asList(ELinkTypes.HasOwnershipOf,
                                                                                     ELinkTypes.IsAPeerOf), "Operation");

        assertTrue(linkages.isEmpty());

    }

    @Test
    public void getSectionFromXmlTest()
    {
        // Create Entity
        XsdEntity entity = XsdEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Create Live Status Section
        XsdSection.create(entity, "Live Status Section", true);

        // Verify Live Status Section Creation
        assertNotNull(entity.getSection("TREXOperation/Live Status Section"));

        // Create Information Section
        XsdSection.create(entity, "Operation Information Section", true);

        // Verify Information Section Creation
        assertNotNull(entity.getSection("TREXOperation/Operation Information Section"));

    }

    @Test
    public void getEntityIdParamExistsTest()
    {

        XsdEntity entity = XsdEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First,Second",
                                            "Type1,Type2",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        List<String> type1List = entity.getEntityId("Type1");
        assertEquals(1, type1List.size());
        assertEquals("First", type1List.get(0));

        List<String> type2List = entity.getEntityId("Type2");
        assertEquals(1, type2List.size());
        assertEquals("Second", type2List.get(0));

    }

    @Test
    public void getEntityIdParamDoesNotExistTest()
    {

        XsdEntity entity = XsdEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First,Second",
                                            "Type1,Type2",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        assertTrue(entity.getEntityId("Type3").isEmpty());

    }

    @Test
    public void getEntityIdParamNullTest()
    {

        XsdEntity entity = XsdEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First,Second",
                                            "Type1,Type2",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        assertTrue(entity.getEntityId(null).isEmpty());

    }

    @Test
    public void getEntityIdParamMultipleTypesTest()
    {

        XsdEntity entity = XsdEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First,Second,Third",
                                            "Type1,Type2,Type1",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        List<String> ids = entity.getEntityId("Type1");

        assertEquals("First", ids.get(0));
        assertEquals("Third", ids.get(1));

    }

    @Test
    public void setEntityIdDoesNotExistTest()
    {
        XsdEntity entity = XsdEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "",
                                            "",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        entity.setEntityId("Type1", "First");

        assertEquals("First", entity.getEntityId());
        assertEquals("Type1", entity.getEntityIdType());

    }

    @Test
    public void setEntityIdDuplicateTypeTest()
    {

        XsdEntity entity = XsdEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First",
                                            "Type1",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        entity.setEntityId("Type1", "Second");

        assertEquals("First,Second", entity.getEntityId());
        assertEquals("Type1,Type1", entity.getEntityIdType());

    }

    @Test
    public void setEntityIdSingleAddTest()
    {
        XsdEntity entity = XsdEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First",
                                            "Type1",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        entity.setEntityId("Type2", "Second");

        assertEquals("First,Second", entity.getEntityId());
        assertEquals("Type1,Type2", entity.getEntityIdType());

    }

    @Test
    public void setEntityIdMultipleAddTest()
    {
        XsdEntity entity = XsdEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First,Second",
                                            "Type1,Type2",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        entity.setEntityId("Type3", "Third");

        assertEquals("First,Second,Third", entity.getEntityId());
        assertEquals("Type1,Type2,Type3", entity.getEntityIdType());

    }

    @Test
    public void setEntityIDNullTypeTest()
    {
        thrown.expect(NullArgumentException.class);
        thrown.expectMessage("typeParam");

        XsdEntity entity = XsdEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First,Second",
                                            "Type1,Type2",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        assertFalse(entity.setEntityId(null, "Third"));

    }

    @Test
    public void setEntityIDNullNameTest()
    {
        thrown.expect(NullArgumentException.class);
        thrown.expectMessage("value");

        XsdEntity entity = XsdEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First,Second",
                                            "Type1,Type2",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        assertFalse(entity.setEntityId("Type3", null));

    }

    @Test
    public void setEntityIDNullBothTest()
    {
        thrown.expect(NullArgumentException.class);
        thrown.expectMessage("typeParam");

        XsdEntity entity = XsdEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First,Second",
                                            "Type1,Type2",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        assertFalse(entity.setEntityId(null, null));

    }

    @Test
    public void markAsDeletedNotDeletedYetTest()
    {
        XsdEntity entity = XsdEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First,Second",
                                            "Type1,Type2",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        assertEquals(ECoalesceDataObjectStatus.ACTIVE, entity.getStatus());

        entity.markAsDeleted();

        assertEquals(ECoalesceDataObjectStatus.DELETED, entity.getStatus());

    }

    @Test
    public void markAsDeletedAlreadyDeletedTest()
    {
        XsdEntity entity = XsdEntity.create("TREXOperation",
                                            "TREX Portal",
                                            "1.0.0.0",
                                            "First,Second",
                                            "Type1,Type2",
                                            "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        entity.setStatus(ECoalesceDataObjectStatus.DELETED);

        entity.markAsDeleted();

        assertEquals(ECoalesceDataObjectStatus.DELETED, entity.getStatus());

    }

    @Test
    public void toXmlTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        String xml = entity.toXml();
        
        CoalesceAssert.assertXmlEquals(CoalesceTypeInstances.TEST_MISSION, xml, "UTF-8");

    }

    @Test
    public void toXmlRemoveBinaryFalseTest() throws UnsupportedEncodingException, CoalesceException
    {
        XsdEntity entity = XsdEntity.create("");
        entity.setName("Testing Entity");
        XsdSection section = entity.createSection("Testing Section");
        XsdRecordset recordset = section.createRecordset("Testing Recordset");

        recordset.createFieldDefinition("Binary1", ECoalesceFieldDataTypes.BinaryType, "", "(U)", "");
        recordset.createFieldDefinition("Binary2", ECoalesceFieldDataTypes.BinaryType, "", "(U)", "");
        recordset.createFieldDefinition("Binary3", ECoalesceFieldDataTypes.BinaryType, "", "(U)", "");

        recordset.createFieldDefinition("File1", ECoalesceFieldDataTypes.FileType, "", "(U)", "");
        recordset.createFieldDefinition("File2", ECoalesceFieldDataTypes.FileType, "", "(U)", "");
        recordset.createFieldDefinition("File3", ECoalesceFieldDataTypes.FileType, "", "(U)", "");

        XsdRecord record = recordset.addNew();
        record.setFieldValue("Binary1", "Binary1".getBytes("US-ASCII"));
        record.setFieldValue("Binary2", "Binary2".getBytes("US-ASCII"));
        record.setFieldValue("Binary3", "Binary3".getBytes("US-ASCII"));

        record.setFieldValue("File1", "File1".getBytes("US-ASCII"), "file1");
        record.setFieldValue("File2", "File2".getBytes("US-ASCII"), "file2");
        record.setFieldValue("File3", "File3".getBytes("US-ASCII"), "file3");

        String xml = entity.toXml(false);

        XsdEntity desEntity = XsdEntity.create(xml);

        XsdRecord desRecord = (XsdRecord) desEntity.getDataObjectForNamePath("Testing Entity/Testing Section/Testing Recordset/Testing Recordset Record");

        assertArrayEquals("Binary1".getBytes("US-ASCII"), desRecord.getFieldValueAsByteArray("Binary1", null));
        assertArrayEquals("Binary2".getBytes("US-ASCII"), desRecord.getFieldValueAsByteArray("Binary2", null));
        assertArrayEquals("Binary3".getBytes("US-ASCII"), desRecord.getFieldValueAsByteArray("Binary3", null));

        assertArrayEquals("File1".getBytes("US-ASCII"), desRecord.getFieldValueAsByteArray("File1", null));
        assertArrayEquals("File2".getBytes("US-ASCII"), desRecord.getFieldValueAsByteArray("File2", null));
        assertArrayEquals("File3".getBytes("US-ASCII"), desRecord.getFieldValueAsByteArray("File3", null));

    }

    @Test
    public void toXmlRemoveBinaryTrueOnlyBinaryTest() throws UnsupportedEncodingException, CoalesceException
    {
        XsdEntity entity = XsdEntity.create("");
        entity.setName("Testing Entity");
        XsdSection section = entity.createSection("Testing Section");
        XsdRecordset recordset = section.createRecordset("Testing Recordset");

        recordset.createFieldDefinition("Binary1", ECoalesceFieldDataTypes.BinaryType, "", "(U)", "");
        recordset.createFieldDefinition("Binary2", ECoalesceFieldDataTypes.BinaryType, "", "(U)", "");
        recordset.createFieldDefinition("Binary3", ECoalesceFieldDataTypes.BinaryType, "", "(U)", "");

        recordset.createFieldDefinition("File1", ECoalesceFieldDataTypes.FileType, "", "(U)", "");
        recordset.createFieldDefinition("File2", ECoalesceFieldDataTypes.FileType, "", "(U)", "");
        recordset.createFieldDefinition("File3", ECoalesceFieldDataTypes.FileType, "", "(U)", "");

        XsdRecord record = recordset.addNew();
        record.setFieldValue("Binary1", "Binary1".getBytes("US-ASCII"));
        record.setFieldValue("Binary2", "Binary2".getBytes("US-ASCII"));
        record.setFieldValue("Binary3", "Binary3".getBytes("US-ASCII"));

        record.setFieldValue("File1", "File1".getBytes("US-ASCII"), "file1");
        record.setFieldValue("File2", "File2".getBytes("US-ASCII"), "file2");
        record.setFieldValue("File3", "File3".getBytes("US-ASCII"), "file3");

        String xml = entity.toXml(true);

        XsdEntity desEntity = XsdEntity.create(xml);

        XsdRecord desRecord = (XsdRecord) desEntity.getDataObjectForNamePath("Testing Entity/Testing Section/Testing Recordset/Testing Recordset Record");

        assertArrayEquals(new byte[0], desRecord.getFieldValueAsByteArray("Binary1", null));
        assertArrayEquals(new byte[0], desRecord.getFieldValueAsByteArray("Binary2", null));
        assertArrayEquals(new byte[0], desRecord.getFieldValueAsByteArray("Binary3", null));

        assertArrayEquals(new byte[0], desRecord.getFieldValueAsByteArray("File1", null));
        assertArrayEquals(new byte[0], desRecord.getFieldValueAsByteArray("File2", null));
        assertArrayEquals(new byte[0], desRecord.getFieldValueAsByteArray("File3", null));

    }

    @Test
    public void fieldHistoryTest()
    {

        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdDataObject xdo = entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

        assertTrue(xdo instanceof XsdField);

        XsdField nameField = (XsdField) xdo;

        assertEquals(1, nameField.getHistory().size());
        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_HISTORY_VALUE, nameField.getHistory().get(0).getValue());

        xdo = entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_ACTION_NUMBER_PATH);

        assertTrue(xdo instanceof XsdField);

        XsdField actionNumberField = (XsdField) xdo;

        assertEquals(2, actionNumberField.getHistory().size());
        assertEquals(CoalesceTypeInstances.TEST_MISSION_ACTION_NUMBER_LABEL_HISTORY,
                     actionNumberField.getHistory().get(0).getLabel());

        xdo = entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        assertTrue(xdo instanceof XsdField);

        XsdField base64Field = (XsdField) xdo;

        assertTrue(base64Field.getHistory().isEmpty());

    }

    @Test
    public void mergeEntityTest()
    {

        try
        {
            // Get Entities
            XsdEntity entity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
            XsdEntity entity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

            // Get Mission Name Fields
            XsdField entity1MissionName = (XsdField) entity1.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
            XsdField entity2MissionName = (XsdField) entity2.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

            // Modify Entity 1
            entity1MissionName.setTypedValue("Should be added as history");

            // Sleep for 2 Seconds
            Thread.sleep(2000);

            // Modify Entity 2
            entity2MissionName.setTypedValue("Should be the new value");
            
            // Merge Entities
            XsdEntity mergedEntity = XsdEntity.mergeSyncEntity(entity1, entity2);

            // Get Mission Name Field of Merged Entity
            XsdField mergedEntityMissionName = (XsdField) mergedEntity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

            // Validate Merge
            assertEquals(entity2MissionName.getValue(), mergedEntityMissionName.getValue());
            assertEquals(entity1MissionName.getValue(),
                         (mergedEntityMissionName.getHistory().get(mergedEntityMissionName.getHistory().size() - 1)).getValue());

        }
        catch (CoalesceException | InterruptedException e)
        {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void mergeAttributeTest() {
        
        try
        {
            // Get Entities
            XsdEntity entity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);
            XsdEntity entity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

            // Get Mission Name Fields
            XsdField entity1MissionName = (XsdField) entity1.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
            XsdField entity2MissionName = (XsdField) entity2.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

            // Modify Entity 1
            entity1MissionName.setOtherAttribute("newattr1", "1");

            // Sleep for 2 Seconds
            Thread.sleep(2000);

            // Modify Entity 2
            entity2MissionName.setOtherAttribute("newattr2", "2");
            
            // Merge Entities
            XsdEntity mergedEntity = XsdEntity.mergeSyncEntity(entity1, entity2);

            // Get Mission Name Field of Merged Entity
            XsdField mergedEntityMissionName = (XsdField) mergedEntity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

            // Validate Merge
            assertEquals(mergedEntityMissionName.getAttribute("newattr1"), "1");
            assertEquals(mergedEntityMissionName.getAttribute("newattr2"), "2");

        }
        catch (CoalesceException | InterruptedException e)
        {
            fail(e.getMessage());
        }
        
    }

    @Test
    public void mergeSyncEntityTest()
    {

        try
        {
            XsdEntity myEntity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_HISTORY);
            XsdEntity updatedEntity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

            XsdFieldDefinition fieldDefinition = (XsdFieldDefinition) updatedEntity.getCoalesceDataObjectForKey("1A7DA2CD-8A83-4E86-ADE8-15FDECE0564E");
            fieldDefinition.setDefaultValue("UpdatedIncidentDescription");
            fieldDefinition.setLastModified(new DateTime());
            updatedEntity.setLastModified(new DateTime());

            myEntity = XsdEntity.mergeSyncEntity(myEntity, updatedEntity);
            XsdFieldDefinition myfieldDefinition = (XsdFieldDefinition) myEntity.getCoalesceDataObjectForKey("1A7DA2CD-8A83-4E86-ADE8-15FDECE0564E");
            assertEquals("UpdatedIncidentDescription", myfieldDefinition.getAttribute("defaultvalue"));

        }
        catch (CoalesceException e)
        {
            fail(e.getMessage());
        }
    }

    // -----------------------------------------------------------------------//
    // Private Methods
    // -----------------------------------------------------------------------//

    private void assertEmptyEntity(XsdEntity entity)
    {

        assertTrue(entity.getLinkages().values().isEmpty());
        assertEquals(0, entity.getSections().size());
    }

    private void assertLinkage(String entity1Key, ELinkTypes type, String entity2Key, XsdLinkage linkage)
    {
        assertEquals(entity1Key, linkage.getEntity1Key());
        assertEquals(type, linkage.getLinkType());
        assertEquals(entity2Key, linkage.getEntity2Key());

    }

    private void assertLinkage(XsdEntity entity1, XsdEntity entity2, ELinkTypes type, XsdLinkage linkage)
    {
        assertEquals(entity1.getKey(), linkage.getEntity1Key());
        assertEquals(entity2.getKey(), linkage.getEntity2Key());

        assertEquals(type, linkage.getLinkType());

    }

    private class Entities {

        public XsdEntity Entity;
        public XsdEntity Entity2;
        public XsdEntity Entity3;
        public XsdEntity Entity4;

    }

    private Entities createEntityLinkages()
    {
        Entities entities = new Entities();

        entities.Entity = XsdEntity.create("Operation", "Portal", "1.1.1.1", "ID", "Type");

        entities.Entity2 = XsdEntity.create("Operation", "Portal", "1.2.3.4", "Id2", "Type2");

        entities.Entity3 = XsdEntity.create("Mission", "Portal2", "2.3.4.5", "Id3", "Type3");

        entities.Entity4 = XsdEntity.create("Operation", "Portal2", "3.4.5.6", "Id4", "Type4");

        assertTrue(EntityLinkHelper.linkEntities(entities.Entity, ELinkTypes.HasUseOf, entities.Entity2, false));
        assertTrue(EntityLinkHelper.linkEntities(entities.Entity, ELinkTypes.IsParentOf, entities.Entity3, false));
        assertTrue(EntityLinkHelper.linkEntities(entities.Entity2, ELinkTypes.WasCreatedBy, entities.Entity, false));
        assertTrue(EntityLinkHelper.linkEntities(entities.Entity4, ELinkTypes.IsAMemberOf, entities.Entity, false));

        return entities;

    }

}
