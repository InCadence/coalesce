package com.incadencecorp.coalesce.framework.datamodel;

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
import org.apache.xerces.impl.dv.util.Base64;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xml.sax.SAXException;

import com.incadencecorp.coalesce.common.CoalesceAssert;
import com.incadencecorp.coalesce.common.CoalesceTypeInstances;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.EntityLinkHelper;
import com.incadencecorp.coalesce.common.helpers.GUIDHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;

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

public class CoalesceEntityTest {

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

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("TREXMission", entity.getNamePath());

        String title = entity.getTitle();
        assertEquals("NORTHCOM Volunteer Background Checks Changed, NORTHCOM Volunteer Background Checks", title);
        assertEquals(4, entity.getLinkages().values().size());
        assertEquals(2, entity.getSections().size());
        assertEquals(1,
                     ((CoalesceRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH)).getCount());
        assertEquals(17,
                     ((CoalesceRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH)).getFieldDefinitions().size());
        assertEquals(17,
                     entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORD_PATH).getChildDataObjects().size());
    }

    @Test
    public void createFromXmlEmptyTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("");

        assertEmptyEntity(entity);

    }

    @Test
    public void createFromXmlWhitespaceTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("  ");

        assertEmptyEntity(entity);

    }

    @Test
    public void createFromXmlNullTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(null);

        assertEmptyEntity(entity);
    }

    @Test
    public void createFromXmlInvalidXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("invalid format");

        assertNull(entity);

    }

    @Test
    public void createFromXmlWithTitleNullTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION, null);

        assertEquals("TREX Portal", entity.getTitle());
    }

    @Test
    public void createFromXmlWithTitleEmptyStringTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION, "");

        assertEquals("TREX Portal", entity.getTitle());
    }

    @Test
    public void createFromXmlWithTitleWhiteSpaceTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION, "   ");

        assertEquals("TREX Portal", entity.getTitle());
    }

    @Test
    public void createFromXmlWithTitleNewXpathTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION,
                                                      "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionName");

        assertEquals(((CoalesceField<?>) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH)).getBaseValue(),
                     entity.getTitle());
    }

    @Test
    public void createFromXmlWithTitleNewTitleTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION, "New Mission Title");

        assertEquals("New Mission Title", entity.getTitle());
    }

    @Test
    public void createDetailedWithoutTitleTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", "1.1.1.1", "Entity Id", "Entity Type");

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
        CoalesceEntity entity = CoalesceEntity.create(null, "Portal", "1.1.1.1", "Entity Id", "Entity Type");

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
        CoalesceEntity entity = CoalesceEntity.create("Operation", null, "1.1.1.1", "Entity Id", "Entity Type");

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
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", null, "Entity Id", "Entity Type");

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
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", "1.1.1.1", null, "Entity Type");

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
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", "1.1.1.1", "Entity Id", null);

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
        CoalesceEntity entity = CoalesceEntity.create("Operation",
                                                      "Portal",
                                                      "1.1.1.1",
                                                      "Entity Id",
                                                      "Entity Type",
                                                      "A New Title");

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
        CoalesceEntity entity = CoalesceEntity.create(null, "Portal", "1.1.1.1", "Entity Id", "Entity Type", "A New Title");

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
        CoalesceEntity entity = CoalesceEntity.create("Operation",
                                                      null,
                                                      "1.1.1.1",
                                                      "Entity Id",
                                                      "Entity Type",
                                                      "A New Title");

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
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", null, "Entity Id", "Entity Type", "A New Title");

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
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", "1.1.1.1", null, "Entity Type", "A New Title");

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
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", "1.1.1.1", "Entity Id", null, "A New Title");

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
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", "1.1.1.1", "Entity Id", "Entity Id Type", null);

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
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", "1.1.1.1", "Entity Id", "Entity Id Type", "");

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
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", "1.1.1.1", "Entity Id", "Entity Id Type", "   ");

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
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
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
        CoalesceSection liveSection = CoalesceSection.create(entity, "Live Status Section", true);
        CoalesceRecordset liveRecordSet = CoalesceRecordset.create(liveSection, "Live Status Recordset");

        // Verify Live Status Section Creation
        assertTrue(entity.getSection("TREXOperation/Live Status Section").getName().equalsIgnoreCase("Live Status Section"));

        CoalesceFieldDefinition.create(liveRecordSet, "CurrentStatus", ECoalesceFieldDataTypes.STRING_TYPE);

        // Create Information Section
        CoalesceSection informationSection = CoalesceSection.create(entity, "Operation Information Section", true);
        CoalesceRecordset informationRecordSet = CoalesceRecordset.create(informationSection,
                                                                          "Operation Information Recordset");

        CoalesceFieldDefinition.create(informationRecordSet, "OperationName", ECoalesceFieldDataTypes.STRING_TYPE);

        // Verify Information Section Creation
        assertNotNull(entity.getSection("TREXOperation/Operation Information Section"));

        // Serialize
        String entityXml = entity.toXml();

        // Deserialize
        CoalesceEntity entity2 = new CoalesceEntity();
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
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
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
        CoalesceSection liveSection = entity.createSection("Live Status Section", true);
        CoalesceRecordset liveRecordSet = liveSection.createRecordset("Live Status Recordset");

        // Verify Live Status Section Creation
        assertTrue(entity.getSection("TREXOperation/Live Status Section").getName().equalsIgnoreCase("Live Status Section"));

        liveRecordSet.createFieldDefinition("CurrentStatus", ECoalesceFieldDataTypes.STRING_TYPE);

        // Create Information Section
        CoalesceSection informationSection = entity.createSection("Operation Information Section", true);
        CoalesceRecordset informationRecordSet = informationSection.createRecordset("Operation Information Recordset");

        informationRecordSet.createFieldDefinition("OperationName", ECoalesceFieldDataTypes.STRING_TYPE);

        // Verify Information Section Creation
        assertNotNull(entity.getSection("TREXOperation/Operation Information Section"));

        // Serialize
        String entityXml = entity.toXml();

        // Deserialize
        CoalesceEntity entity2 = new CoalesceEntity();
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

        CoalesceEntity entity = new CoalesceEntity();

        assertTrue(entity.initialize(CoalesceTypeInstances.TEST_MISSION));

        String title = entity.getTitle();
        assertEquals("NORTHCOM Volunteer Background Checks Changed, NORTHCOM Volunteer Background Checks", title);
        assertEquals(4, entity.getLinkages().values().size());
        assertEquals(2, entity.getSections().size());
        assertEquals(1,
                     ((CoalesceRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH)).getCount());
        assertEquals(17,
                     ((CoalesceRecordset) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORDSET_PATH)).getFieldDefinitions().size());
        assertEquals(17,
                     entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_RECORD_PATH).getChildDataObjects().size());
    }

    @Test
    public void initializeFromXmlEmptyTest()
    {
        CoalesceEntity entity = new CoalesceEntity();

        assertTrue(entity.initialize(""));

        assertEmptyEntity(entity);

    }

    @Test
    public void initializeFromXmlWhitespaceTest()
    {
        CoalesceEntity entity = new CoalesceEntity();

        assertTrue(entity.initialize("  "));

        assertEmptyEntity(entity);

    }

    @Test
    public void initializeFromXmlNullTest()
    {
        CoalesceEntity entity = new CoalesceEntity();

        assertTrue(entity.initialize(""));

        assertEmptyEntity(entity);
    }

    @Test
    public void initializeFromXmlInvalidXmlTest()
    {
        CoalesceEntity entity = new CoalesceEntity();

        assertFalse(entity.initialize("invalid format"));

    }

    @Test
    public void initializeFromNonEntityTest()
    {
        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();

        String linkageSectionXml = entity.getLinkageSection().toXml();

        CoalesceEntity desEntity = new CoalesceEntity();

        assertFalse(desEntity.initialize(linkageSectionXml));
    }

    @Test
    public void initializeTest()
    {
        CoalesceEntity entity = new CoalesceEntity();

        assertTrue(entity.initialize());

        assertEmptyEntity(entity);

    }

    @Test
    public void initializeEntityTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceEntity copiedEntity = new CoalesceEntity();
        copiedEntity.initialize(entity);

        assertEquals(entity.getParent(), copiedEntity.getParent());
        assertEquals(entity.toXml(), copiedEntity.toXml());
        assertEquals(entity.getChildDataObjects(), copiedEntity.getChildDataObjects());

    }

    @Test
    public void getKeyFromXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("62857EF8-3930-4F0E-BAE3-093344EBF389", entity.getKey());

    }

    @Test
    public void getKeyNewTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertNotNull(GUIDHelper.isValid(entity.getKey()));

    }

    @Test
    public void getNameFromXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("TREXMission", entity.getName());

    }

    @Test
    public void getNameNewTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertEquals("Operation", entity.getName());

    }

    @Test
    public void setNameFromXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        entity.setName("Other Name");

        assertEquals("Other Name", entity.getName());

    }

    @Test
    public void setNameNewTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        entity.setName("Other Name");

        assertEquals("Other Name", entity.getName());

    }

    @Test
    public void getTypeFromXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("entity", entity.getType());

    }

    @Test
    public void getTypeNewTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertEquals("entity", entity.getType());

    }

    @Test
    public void getSourceFromXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("TREX Portal", entity.getSource());

    }

    @Test
    public void getSourceNewTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertEquals("Portal", entity.getSource());

    }

    @Test
    public void setSourceFromXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        entity.setSource("New Source");

        assertEquals("New Source", entity.getSource());

    }

    @Test
    public void setSourceNewTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        entity.setSource("New Source");

        assertEquals("New Source", entity.getSource());

    }

    @Test
    public void getVersionFromXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("1.0.0.0", entity.getVersion());

    }

    @Test
    public void getVersionNewTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertEquals("1.1.1.1", entity.getVersion());

    }

    @Test
    public void setVersionFromXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        entity.setVersion("2.3.4.5");

        assertEquals("2.3.4.5", entity.getVersion());

    }

    @Test
    public void setVersionNewTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        entity.setVersion("2.3.4.5");

        assertEquals("2.3.4.5", entity.getVersion());

    }

    @Test
    public void getEntityIdFromXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("", entity.getEntityId());

    }

    @Test
    public void getEntityIdNewTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertEquals("Entity ID", entity.getEntityId());

    }

    @Test
    public void setEntityIdFromXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        entity.setEntityId("New Entity ID");

        assertEquals("New Entity ID", entity.getEntityId());

    }

    @Test
    public void setEntityIdNewTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        entity.setEntityId("New Entity ID");

        assertEquals("New Entity ID", entity.getEntityId());

    }

    @Test
    public void getEntityIdTypeFromXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertEquals("", entity.getEntityIdType());

    }

    @Test
    public void getEntityIdTypeNewTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertEquals("Entity Id Type", entity.getEntityIdType());

    }

    @Test
    public void setEntityIdTypeFromXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        entity.setEntityIdType("New Entity Id Type");

        assertEquals("New Entity Id Type", entity.getEntityIdType());

    }

    @Test
    public void setEntityIdTypeNewTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", "1.1.1.1", "Entity ID", "Entity Id Type");

        assertEquals("Entity Id Type", entity.getEntityIdType());

    }

    @Test
    public void getTitleXPathInvalidTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation",
                                                      "Portal",
                                                      "1.1.1.1",
                                                      "Entity Id",
                                                      "Entity Id Type",
                                                      "Operation/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionName,TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/IncidentTitle");

        String title = entity.getTitle();

        assertEquals("Operation/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionName,TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/IncidentTitle",
                     title);

    }

    @Test
    public void getTitleXPathNotAFieldTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation",
                                                      "Portal",
                                                      "1.1.1.1",
                                                      "Entity Id",
                                                      "Entity Id Type",
                                                      "Operation/Operation Section/Operation Recordset/Operation Recordset Record");
        CoalesceSection section = entity.createSection("Operation Section");
        CoalesceRecordset recordset = section.createRecordset("Operation Recordset");
        recordset.createFieldDefinition("Name", ECoalesceFieldDataTypes.STRING_TYPE);

        CoalesceRecord record = recordset.addNew();

        CoalesceStringField nameField = (CoalesceStringField) record.getFieldByName("Name");
        nameField.setValue("Testing Title Length");

        assertEquals("Operation/Operation Section/Operation Recordset/Operation Recordset Record/Name",
                     nameField.getNamePath());
        String title = entity.getTitle();

        assertEquals("Operation/Operation Section/Operation Recordset/Operation Recordset Record", title);

    }

    @Test
    public void getTitleXPathInvalidFieldTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation",
                                                      "Portal",
                                                      "1.1.1.1",
                                                      "Entity Id",
                                                      "Entity Id Type",
                                                      "Operation/Section/Recordset/Recordset Record/Unknown");
        CoalesceSection section = entity.createSection("Section");
        CoalesceRecordset recordset = section.createRecordset("Recordset");
        recordset.createFieldDefinition("Name", ECoalesceFieldDataTypes.STRING_TYPE);

        CoalesceRecord record = recordset.addNew();

        CoalesceStringField nameField = (CoalesceStringField) record.getFieldByName("Name");
        nameField.setValue("Testing Title Length");

        String title = entity.getTitle();

        assertEquals("Operation/Section/Recordset/Recordset Record/Unknown", title);

    }

    @Test
    public void getTitleLengthUnderFiftyTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation",
                                                      "Portal",
                                                      "1.1.1.1",
                                                      "Entity Id",
                                                      "Entity Id Type",
                                                      "Operation/Section/Recordset/Recordset Record/Name");
        CoalesceSection section = entity.createSection("Section");
        CoalesceRecordset recordset = section.createRecordset("Recordset");
        recordset.createFieldDefinition("Name", ECoalesceFieldDataTypes.STRING_TYPE);

        CoalesceRecord record = recordset.addNew();

        CoalesceStringField nameField = (CoalesceStringField) record.getFieldByName("Name");
        nameField.setValue("Testing Title Length");

        String title = entity.getTitle();

        assertEquals("Testing Title Length", title);

    }

    @Test
    public void getTitleFieldNotSetTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation",
                                                      "Portal",
                                                      "1.1.1.1",
                                                      "Entity Id",
                                                      "Entity Id Type",
                                                      "Operation/Section/Recordset/Recordset Record/Name");
        CoalesceSection section = entity.createSection("Section");
        CoalesceRecordset recordset = section.createRecordset("Recordset");
        recordset.createFieldDefinition("Name", ECoalesceFieldDataTypes.STRING_TYPE);
        recordset.addNew();

        String title = entity.getTitle();

        assertEquals("Portal", title);

    }

    @Test
    public void getTitleWithoutXpathTest()
    {

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_XPATH_TITLE);

        String title = entity.getTitle();

        assertEquals("TREX Portal", title);

    }

    @Test
    public void getTitleUpdateThatUsesXpathTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceField<?> missionName = (CoalesceField<?>) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
        CoalesceField<?> incidentTitle = (CoalesceField<?>) entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_INCIDENT_TITLE_PATH);

        missionName.setBaseValue("Mission Name");
        incidentTitle.setBaseValue("Incident Title");

        String title = entity.getTitle();
        assertEquals("Mission Name, Incident Title", title);

    }

    @Test
    public void setTitleThatDoesNotUseXpathTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_XPATH_TITLE);

        entity.setTitle("Mission Entity Title");

        assertEquals("Mission Entity Title", entity.getTitle());
    }

    @Test
    public void setTitleThatUsesXpathTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        entity.setTitle("Mission Entity Title");

        assertEquals("Mission Entity Title", entity.getTitle());
    }

    @Test
    public void setTitleNullTest() throws InterruptedException
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation",
                                                      "Portal",
                                                      "1.1.1.1",
                                                      "Entity Id",
                                                      "Entity Id Type",
                                                      "Operation/Section/Recordset/Recordset Record/Name");

        DateTime lastModified = entity.getLastModified();
        Thread.sleep(500);

        entity.setTitle(null);

        assertEquals("Portal", entity.getTitle());
        assertTrue("Last modified should be more recent",
                   DateTimeComparator.getInstance().compare(entity.getLastModified(), lastModified) > 0);

    }

    @Test
    public void setTitleNullWasNullTest() throws InterruptedException
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", "1.1.1.1", "Entity Id", "Entity Id Type");

        DateTime lastModified = entity.getLastModified();
        Thread.sleep(500);

        entity.setTitle(null);

        assertEquals("Portal", entity.getTitle());
        assertEquals(entity.getLastModified(), lastModified);

    }

    @Test
    public void setTitleWasNullTest() throws InterruptedException
    {
        CoalesceEntity entity = CoalesceEntity.create("Operation", "Portal", "1.1.1.1", "Entity Id", "Entity Id Type");
        CoalesceSection section = entity.createSection("Operation Section");
        CoalesceRecordset recordset = section.createRecordset("Operation Recordset");
        recordset.createFieldDefinition("Name", ECoalesceFieldDataTypes.STRING_TYPE);

        CoalesceRecord record = recordset.addNew();

        CoalesceStringField nameField = (CoalesceStringField) record.getFieldByName("Name");
        nameField.setValue("Testing Title");

        DateTime lastModified = entity.getLastModified();
        Thread.sleep(500);

        entity.setTitle("Operation/Operation Section/Operation Recordset/Operation Recordset Record/Name");

        assertEquals("Testing Title", entity.getTitle());

        DateTime newLastModified = entity.getLastModified();
        assertTrue("Last modified should be more recent",
                   DateTimeComparator.getInstance().compare(newLastModified, lastModified) > 0);

    }

    @Test
    public void getDateCreatedFromXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertEquals(JodaDateTimeHelper.fromXmlDateTimeUTC("2014-05-02T14:33:51.851575Z"), entity.getDateCreated());

    }

    @Test
    public void getDateCreatedNewTest()
    {
        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();

        DateTime now = JodaDateTimeHelper.nowInUtc();

        assertTrue(Math.abs(now.getMillis() - entity.getDateCreated().getMillis()) < 5);
    }

    @Test
    public void setDateCreatedFromXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        DateTime now = JodaDateTimeHelper.nowInUtc();

        entity.setDateCreated(now);

        assertEquals(now, entity.getDateCreated());

    }

    @Test
    public void setDateCreatedNewTest()
    {
        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();

        DateTime sixDaysAgo = JodaDateTimeHelper.nowInUtc().minusDays(6);

        entity.setDateCreated(sixDaysAgo);

        assertEquals(sixDaysAgo, entity.getDateCreated());
    }

    @Test
    public void getLastModifiedFromXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertEquals(JodaDateTimeHelper.fromXmlDateTimeUTC("2014-05-20T16:17:13.2293139Z"), entity.getLastModified());

    }

    @Test
    public void getLastModifiedNewTest()
    {
        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();

        DateTime now = JodaDateTimeHelper.nowInUtc();

        assertTrue(Math.abs(now.getMillis() - entity.getLastModified().getMillis()) < 5);
    }

    @Test
    public void setLastModifiedFromXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        DateTime now = JodaDateTimeHelper.nowInUtc();

        entity.setLastModified(now);

        assertEquals(now, entity.getLastModified());

    }

    @Test
    public void setLastModifiedNewTest()
    {
        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();

        DateTime sixDaysAgo = JodaDateTimeHelper.nowInUtc().minusDays(6);

        entity.setLastModified(sixDaysAgo);

        assertEquals(sixDaysAgo, entity.getLastModified());
    }

    @Test
    public void arbitraryAttributesTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        String entityXml = entity.toXml();

        assertTrue(entityXml.contains("anthony=\"Test\""));
        assertTrue(entity.getOtherAttribute("anthony").equals("Test"));

    }

    @Test
    public void getLinkagesFromXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        Map<String, CoalesceLinkage> linkages = entity.getLinkages();

        assertEquals(4, linkages.size());

        assertLinkage("62857EF8-3930-4F0E-BAE3-093344EBF389",
                      ELinkTypes.IS_CHILD_OF,
                      "AEACD69E-5365-4401-87A1-D95E657E0785",
                      linkages.get("DB7E0EAF-F4EF-4473-94A9-B93A7F46281E"));
        assertLinkage("62857EF8-3930-4F0E-BAE3-093344EBF389",
                      ELinkTypes.IS_PARENT_OF,
                      "C42DFD35-EA71-4F56-BC3B-D4287279123D",
                      linkages.get("9A04CBCD-297F-43E2-A590-F59D8438E386"));
        assertLinkage("62857EF8-3930-4F0E-BAE3-093344EBF389",
                      ELinkTypes.HAS_PARTICIPANT,
                      "BDCD779B-3C74-4391-BCCB-2DB8D06D5A6F",
                      linkages.get("309153E3-5F53-4EDB-B89C-35AE6EECEBF1"));
        assertLinkage("62857EF8-3930-4F0E-BAE3-093344EBF389",
                      ELinkTypes.IS_PARENT_OF,
                      "55DFA165-0AB8-48B7-BF35-5DA6CADB5E1E",
                      linkages.get("6AD08B0F-C492-4105-8033-A5E43056B864"));
    }

    @Test
    public void getLinkagesNewTest()
    {
        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();

        CoalesceEntity entity2 = new CoalesceEntity();
        entity2.initialize();

        CoalesceEntity entity3 = new CoalesceEntity();
        entity3.initialize();

        assertTrue(EntityLinkHelper.linkEntities(entity, ELinkTypes.HAS_USE_OF, entity2, false));
        assertTrue(EntityLinkHelper.linkEntities(entity, ELinkTypes.IS_PARENT_OF, entity3, false));
        assertTrue(EntityLinkHelper.linkEntities(entity2, ELinkTypes.WAS_CREATED_BY, entity, false));

        Map<String, CoalesceLinkage> linkages = entity.getLinkages();

        assertEquals(3, linkages.size());

        for (CoalesceLinkage linkage : linkages.values())
        {
            switch (linkage.getLinkType()) {
            case HAS_USE_OF:

                assertLinkage(entity.getKey(), linkage.getLinkType(), entity2.getKey(), linkage);

                break;

            case IS_PARENT_OF:

                assertLinkage(entity.getKey(), linkage.getLinkType(), entity3.getKey(), linkage);

                break;

            case CREATED:

                assertLinkage(entity.getKey(), linkage.getLinkType(), entity2.getKey(), linkage);

                break;

            default:

                fail("Unexpected Type: " + linkage.getLinkType().getLabel());

            }
        }

    }

    @Test
    public void getLinkagesWithoutInitializingTest()
    {
        CoalesceEntity entity = new CoalesceEntity();

        assertNull("Entity should return null for linkages", entity.getLinkages(ELinkTypes.IS_PARENT_OF));

    }

    @Test
    public void getLinkagesIncludingDeletedLinakgeTest()
    {
        CoalesceEntity entity1 = CoalesceEntity.create("Test Entity",
                                                       "Unit Test",
                                                       "1.0",
                                                       "Unit Testing",
                                                       "Unit Test",
                                                       "Entity1");
        CoalesceEntity entity2 = CoalesceEntity.create("Test Entity",
                                                       "Unit Test",
                                                       "1.0",
                                                       "Unit Testing",
                                                       "Unit Test",
                                                       "Entity2");
        CoalesceEntity entity3 = CoalesceEntity.create("Test Entity",
                                                       "Unit Test",
                                                       "1.0",
                                                       "Unit Testing",
                                                       "Unit Test",
                                                       "Entity2");

        EntityLinkHelper.linkEntities(entity1, ELinkTypes.IS_PARENT_OF, entity2, true);
        EntityLinkHelper.linkEntities(entity1, ELinkTypes.IS_PARENT_OF, entity3, true);

        Map<String, CoalesceLinkage> originalLinkages = entity1.getLinkages(ELinkTypes.IS_PARENT_OF);

        assertEquals(2, originalLinkages.size());

        EntityLinkHelper.unLinkEntities(entity1, entity2, ELinkTypes.IS_PARENT_OF);

        Map<String, CoalesceLinkage> linkagesMinusDeleted = entity1.getLinkages(ELinkTypes.IS_PARENT_OF);

        assertEquals(1, linkagesMinusDeleted.size());

    }

    @Test
    public void createNewEntityTemplateTest() throws SAXException, IOException
    {
        // Test Entity
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        // Run Test
        CoalesceEntityTemplateTest.testTemplate(entity.createNewEntityTemplate());

    }

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

        CoalesceSection liveSection = entity.createSection("Live Status Section");
        assertEquals(liveSection, entity.getSection("TREXOperation/Live Status Section"));
        assertFalse(liveSection.getNoIndex());
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

        // Create Live Status Section
        assertNull(entity.getSection("TREXOperation/Live Status Section"));

        CoalesceSection liveSection = entity.createSection("Live Status Section", true);
        assertEquals(liveSection, entity.getSection("TREXOperation/Live Status Section"));
        assertTrue(liveSection.getNoIndex());

        CoalesceSection liveSection2 = entity.createSection("Live Status Section");
        assertEquals(liveSection2, entity.getSection("TREXOperation/Live Status Section"));
        assertEquals(liveSection, liveSection2);
        assertFalse(liveSection.getNoIndex());
        assertFalse(liveSection2.getNoIndex());

    }

    @Test
    public void createSectionFromXmlTest()
    {

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        assertNull(entity.getSection("TREXMission/A New Section"));

        CoalesceSection newSection = entity.createSection("A New Section");

        assertNotNull(newSection);
        assertEquals(newSection, entity.getSection("TREXMission/A New Section"));
        assertFalse(newSection.getNoIndex());

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

        // Create Live Status Section
        assertNull(entity.getSection("TREXOperation/Live Status Section"));

        CoalesceSection liveSection = entity.createSection("Live Status Section", false);
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

        // Create Live Status Section
        assertNull(entity.getSection("TREXOperation/Live Status Section"));

        CoalesceSection liveSection = entity.createSection("Live Status Section", true);
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

        // Create Live Status Section
        assertNull(entity.getSection("TREXOperation/Live Status Section"));

        CoalesceSection liveSection = entity.createSection("Live Status Section", false);
        assertEquals(liveSection, entity.getSection("TREXOperation/Live Status Section"));
        assertFalse(liveSection.getNoIndex());

        CoalesceSection liveSection2 = entity.createSection("Live Status Section", true);
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

        CoalesceSection newSection = entity.createSection("A New Section", true);

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
    public void createSectionWithNoIndexFalseFromXmlExistingTest()
    {

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceSection liveSection = entity.getSection("TREXMission/Live Status Section");
        CoalesceSection informationSection = entity.getSection("TREXMission/Mission Information Section");

        CoalesceSection createdLiveSection = entity.createSection("Live Status Section");
        CoalesceSection createdInformationSection = entity.createSection("Mission Information Section");

        assertEquals(liveSection, createdLiveSection);
        assertEquals(informationSection, createdInformationSection);

    }

    @Test
    public void getSectionsFromXmlTest()
    {

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        Map<String, CoalesceSection> sections = entity.getSections();

        CoalesceSection liveSection = sections.get("85CB4256-4CC2-4F96-A03D-5EF880989822");

        assertNotNull(liveSection);
        assertEquals("85CB4256-4CC2-4F96-A03D-5EF880989822", liveSection.getKey());

        CoalesceSection informationSection = sections.get("383EA645-E695-4E75-ADA6-0C79BEC09A18");
        assertNotNull(informationSection);
        assertEquals("383EA645-E695-4E75-ADA6-0C79BEC09A18", informationSection.getKey());

    }

    @Test
    public void getSectionsEmptyTest()
    {

        CoalesceEntity entity = CoalesceEntity.create("");

        Map<String, CoalesceSection> sections = entity.getSections();

        assertTrue(sections.isEmpty());

    }

    @Test
    public void getSectionsManuallyCreatedTest()
    {

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                                      "TREX Portal",
                                                      "1.0.0.0",
                                                      "",
                                                      "",
                                                      "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Create Live Status Section
        CoalesceSection liveSection = entity.createSection("Live Status Section", true);

        // Create Information Section
        CoalesceSection informationSection = entity.createSection("Operation Information Section", true);

        Map<String, CoalesceSection> sections = entity.getSections();

        assertEquals(liveSection, sections.get(liveSection.getKey()));
        assertEquals(informationSection, sections.get(informationSection.getKey()));

    }

    @Test
    public void getLinkageSectionFromXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceLinkageSection linkageSection = entity.getLinkageSection();

        assertEquals("F4F126AF-4658-4D7F-A67F-4833F7EADDC3", linkageSection.getKey());

    }

    @Test
    public void getLinkageSectionEmptyEntityTest()
    {

        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                                      "TREX Portal",
                                                      "1.0.0.0",
                                                      "",
                                                      "",
                                                      "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        CoalesceLinkageSection entityLinkageSection = entity.getLinkageSection();
        assertNotNull(entityLinkageSection);

    }

    @Test
    public void getLinkageSectionEntityNotInitializedTest()
    {
        CoalesceEntity entity = new CoalesceEntity();

        assertNull(entity.getLinkageSection());

        assertNull(entity.getLinkages("Entity"));

    }

    @Test
    public void getLinkagesForEntityNameFoundTest()
    {
        Entities entities = createEntityLinkages();

        Map<String, CoalesceLinkage> linkages = entities.Entity.getLinkages("Operation");

        assertEquals(3, linkages.size());

        for (CoalesceLinkage linkage : linkages.values())
        {
            switch (linkage.getLinkType()) {
            case HAS_USE_OF:

                assertLinkage(entities.Entity, entities.Entity2, linkage.getLinkType(), linkage);

                break;

            case CREATED:

                assertLinkage(entities.Entity, entities.Entity2, linkage.getLinkType(), linkage);

                break;

            case HAS_MEMBER:

                assertLinkage(entities.Entity, entities.Entity4, linkage.getLinkType(), linkage);

                break;

            default:

                fail("Unexpected Type: " + linkage.getLinkType().getLabel());

            }
        }

        linkages = entities.Entity.getLinkages("Mission");

        assertEquals(1, linkages.size());

        for (CoalesceLinkage linkage : linkages.values())
        {
            switch (linkage.getLinkType()) {
            case IS_PARENT_OF:

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

        Map<String, CoalesceLinkage> linkages = entities.Entity.getLinkages("User");

        assertTrue(linkages.isEmpty());

    }

    @Test
    public void getLinkagesForLinkTypeEntityNameFoundTest()
    {
        Entities entities = createEntityLinkages();

        Map<String, CoalesceLinkage> linkages = entities.Entity.getLinkages(ELinkTypes.HAS_USE_OF, "Operation");

        assertEquals(1, linkages.size());

        for (CoalesceLinkage linkage : linkages.values())
        {
            switch (linkage.getLinkType()) {
            case HAS_USE_OF:

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

        Map<String, CoalesceLinkage> linkages = entities.Entity.getLinkages("User");

        assertTrue(linkages.isEmpty());

    }

    @Test
    public void getLinkagesForLinkTypeFoundTest()
    {
        Entities entities = createEntityLinkages();

        Map<String, CoalesceLinkage> linkages = entities.Entity.getLinkages(ELinkTypes.HAS_USE_OF);

        assertEquals(1, linkages.size());

        for (CoalesceLinkage linkage : linkages.values())
        {
            switch (linkage.getLinkType()) {
            case HAS_USE_OF:

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

        Map<String, CoalesceLinkage> linkages = entities.Entity.getLinkages(ELinkTypes.IS_A_MEMBER_OF);

        assertTrue(linkages.isEmpty());

    }

    @Test
    public void getLinkagesForLinkTypeNullTest()
    {
        Entities entities = createEntityLinkages();

        Map<String, CoalesceLinkage> linkages = entities.Entity.getLinkages((ELinkTypes) null);

        assertTrue(linkages.isEmpty());

    }

    @Test
    public void getLinkagesForLinkTypeEntityNameEntitySourceFoundTest()
    {
        Entities entities = createEntityLinkages();

        Map<String, CoalesceLinkage> linkages = entities.Entity.getLinkages(ELinkTypes.HAS_USE_OF, "Operation", "Portal");

        assertEquals(1, linkages.size());

        for (CoalesceLinkage linkage : linkages.values())
        {
            switch (linkage.getLinkType()) {
            case HAS_USE_OF:

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

        Map<String, CoalesceLinkage> linkages = entities.Entity.getLinkages(ELinkTypes.HAS_OWNERSHIP_OF,
                                                                            "Operation",
                                                                            "Portal0");

        assertTrue(linkages.isEmpty());

    }

    @Test
    public void getLinkagesForLinkTypesEntityNameFoundTest()
    {
        Entities entities = createEntityLinkages();

        Map<String, CoalesceLinkage> linkages = entities.Entity.getLinkages(Arrays.asList(ELinkTypes.HAS_USE_OF,
                                                                                          ELinkTypes.CREATED), "Operation");

        assertEquals(2, linkages.size());

        for (CoalesceLinkage linkage : linkages.values())
        {
            switch (linkage.getLinkType()) {
            case HAS_USE_OF:

                assertLinkage(entities.Entity, entities.Entity2, linkage.getLinkType(), linkage);

                break;

            case CREATED:

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

        Map<String, CoalesceLinkage> linkages = entities.Entity.getLinkages(Arrays.asList(ELinkTypes.HAS_USE_OF,
                                                                                          ELinkTypes.IS_A_PEER_OF),
                                                                            "Operation");

        assertEquals(1, linkages.size());

        for (CoalesceLinkage linkage : linkages.values())
        {
            switch (linkage.getLinkType()) {
            case HAS_USE_OF:

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

        Map<String, CoalesceLinkage> linkages = entities.Entity.getLinkages(Arrays.asList(ELinkTypes.HAS_OWNERSHIP_OF,
                                                                                          ELinkTypes.IS_A_PEER_OF),
                                                                            "Operation");

        assertTrue(linkages.isEmpty());

    }

    @Test
    public void getSectionFromXmlTest()
    {
        // Create Entity
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                                      "TREX Portal",
                                                      "1.0.0.0",
                                                      "",
                                                      "",
                                                      "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        // Create Live Status Section
        CoalesceSection.create(entity, "Live Status Section", true);

        // Verify Live Status Section Creation
        assertNotNull(entity.getSection("TREXOperation/Live Status Section"));

        // Create Information Section
        CoalesceSection.create(entity, "Operation Information Section", true);

        // Verify Information Section Creation
        assertNotNull(entity.getSection("TREXOperation/Operation Information Section"));

    }

    @Test
    public void getSectionPathNotSectionTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceSection section = entity.getSection("TREXMission/Linkages");

        assertNull("Section path shouldn't return a valid section", section);

    }

    @Test
    public void getEntityIdParamExistsTest()
    {

        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
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

        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
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

        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
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

        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
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
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
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

        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
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
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
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
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
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

        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
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

        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
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

        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                                      "TREX Portal",
                                                      "1.0.0.0",
                                                      "First,Second",
                                                      "Type1,Type2",
                                                      "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        assertFalse(entity.setEntityId(null, null));

    }

    @Test
    public void setEntityIDEmptyTypeParamTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("typeParam cannot be empty");

        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                                      "TREX Portal",
                                                      "1.0.0.0",
                                                      "First,Second",
                                                      "Type1,Type2",
                                                      "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        assertFalse(entity.setEntityId("", "something"));

    }

    @Test
    public void setEntityIDEmptyValueTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("value cannot be empty");

        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
                                                      "TREX Portal",
                                                      "1.0.0.0",
                                                      "First,Second",
                                                      "Type1,Type2",
                                                      "TREXOperation/Operation Information Section/Operation Information Recordset/Operation Information Recordset Record/OperationName");

        assertFalse(entity.setEntityId("something", ""));

    }

    @Test
    public void markAsDeletedNotDeletedYetTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
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
        CoalesceEntity entity = CoalesceEntity.create("TREXOperation",
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
    public void getSyncEntityTest() throws CoalescePersistorException, SAXException, IOException
    {

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceEntitySyncShell shell = entity.getSyncEntity();

        assertNotNull(shell.toXml());
        assertTrue(CoalesceEntitySyncShellTest.ValidateSyncShell(shell));

    }

    @Test
    public void toXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        String xml = entity.toXml();

        CoalesceAssert.assertXmlEquals(CoalesceTypeInstances.TEST_MISSION, xml, "UTF-8");

    }

    @Test
    public void toXmlRemoveBinaryFalseTest() throws UnsupportedEncodingException, CoalesceException
    {
        CoalesceEntity entity = CoalesceEntity.create("");
        entity.setName("Testing Entity");
        CoalesceSection section = entity.createSection("Testing Section");
        CoalesceRecordset recordset = section.createRecordset("Testing Recordset");

        recordset.createFieldDefinition("Binary1", ECoalesceFieldDataTypes.BINARY_TYPE, "", "(U)", "");
        recordset.createFieldDefinition("Binary2", ECoalesceFieldDataTypes.BINARY_TYPE, "", "(U)", "");
        recordset.createFieldDefinition("Binary3", ECoalesceFieldDataTypes.BINARY_TYPE, "", "(U)", "");

        recordset.createFieldDefinition("File1", ECoalesceFieldDataTypes.FILE_TYPE, "", "(U)", "");
        recordset.createFieldDefinition("File2", ECoalesceFieldDataTypes.FILE_TYPE, "", "(U)", "");
        recordset.createFieldDefinition("File3", ECoalesceFieldDataTypes.FILE_TYPE, "", "(U)", "");

        CoalesceRecord record = recordset.addNew();
        setBinaryFileFieldValues(record);

        String xml = entity.toXml(false);

        CoalesceEntity desEntity = CoalesceEntity.create(xml);

        CoalesceRecord desRecord = (CoalesceRecord) desEntity.getDataObjectForNamePath("Testing Entity/Testing Section/Testing Recordset/Testing Recordset Record");

        assertBinaryField(desRecord, "Binary1", "Binary1");
        assertBinaryField(desRecord, "Binary2", "Binary2");
        assertBinaryField(desRecord, "Binary3", "Binary3");

        assertFilefiled(desRecord, "File1", "File1");
        assertFilefiled(desRecord, "File2", "File2");
        assertFilefiled(desRecord, "File3", "File3");

    }

    @Test
    public void toXmlRemoveBinaryTrueOnlyBinaryTest() throws UnsupportedEncodingException, CoalesceException
    {
        CoalesceEntity entity = CoalesceEntity.create("");
        entity.setName("Testing Entity");
        CoalesceSection section = entity.createSection("Testing Section");
        CoalesceRecordset recordset = section.createRecordset("Testing Recordset");

        recordset.createFieldDefinition("Binary1", ECoalesceFieldDataTypes.BINARY_TYPE, "", "(U)", "");
        recordset.createFieldDefinition("Binary2", ECoalesceFieldDataTypes.BINARY_TYPE, "", "(U)", "");
        recordset.createFieldDefinition("Binary3", ECoalesceFieldDataTypes.BINARY_TYPE, "", "(U)", "");

        recordset.createFieldDefinition("File1", ECoalesceFieldDataTypes.FILE_TYPE, "", "(U)", "");
        recordset.createFieldDefinition("File2", ECoalesceFieldDataTypes.FILE_TYPE, "", "(U)", "");
        recordset.createFieldDefinition("File3", ECoalesceFieldDataTypes.FILE_TYPE, "", "(U)", "");

        CoalesceRecord record = recordset.addNew();
        setBinaryFileFieldValues(record);

        String xml = entity.toXml(true);

        CoalesceEntity desEntity = CoalesceEntity.create(xml);

        CoalesceRecord desRecord = (CoalesceRecord) desEntity.getDataObjectForNamePath("Testing Entity/Testing Section/Testing Recordset/Testing Recordset Record");

        assertBinaryField(desRecord, "Binary1", "");
        assertBinaryField(desRecord, "Binary2", "");
        assertBinaryField(desRecord, "Binary3", "");

        assertFilefiled(desRecord, "File1", "");
        assertFilefiled(desRecord, "File2", "");
        assertFilefiled(desRecord, "File3", "");

    }

    @Test
    public void fieldHistoryTest()
    {

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceDataObject xdo = entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

        assertTrue(xdo instanceof CoalesceField<?>);

        CoalesceField<?> nameField = (CoalesceField<?>) xdo;

        assertEquals(1, nameField.getHistory().size());
        assertEquals(CoalesceTypeInstances.TEST_MISSION_NAME_HISTORY_VALUE, nameField.getHistory().get(0).getBaseValue());

        xdo = entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_ACTION_NUMBER_PATH);

        assertTrue(xdo instanceof CoalesceField<?>);

        CoalesceField<?> actionNumberField = (CoalesceField<?>) xdo;

        assertEquals(2, actionNumberField.getHistory().size());
        assertEquals(CoalesceTypeInstances.TEST_MISSION_ACTION_NUMBER_LABEL_HISTORY,
                     actionNumberField.getHistory().get(0).getLabel());

        xdo = entity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_BASE64_PATH);

        assertTrue(xdo instanceof CoalesceField<?>);

        CoalesceField<?> base64Field = (CoalesceField<?>) xdo;

        assertTrue(base64Field.getHistory().isEmpty());

    }

    @Test
    public void mergeEntityTest()
    {

        try
        {
            // Get Entities
            CoalesceEntity entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
            CoalesceEntity entity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

            // Get Mission Name Fields
            CoalesceField<?> entity1MissionName = (CoalesceField<?>) entity1.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
            CoalesceField<?> entity2MissionName = (CoalesceField<?>) entity2.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

            // Modify Entity 1
            entity1MissionName.setTypedValue("Should be added as history");

            // Sleep for 2 Seconds
            Thread.sleep(2000);

            // Modify Entity 2
            entity2MissionName.setTypedValue("Should be the new value");

            // Merge Entities
            // XsdEntity mergedEntity = XsdEntity.mergeSyncEntity(entity1, entity2);
            CoalesceEntity mergedEntity = CoalesceEntity.mergeSyncEntity(entity1, entity2);
            System.out.println(mergedEntity.toXml());

            // Get Mission Name Field of Merged Entity
            CoalesceField<?> mergedEntityMissionName = (CoalesceField<?>) mergedEntity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

            // Validate Merge
            assertEquals(entity2MissionName.getBaseValue(), mergedEntityMissionName.getBaseValue());
            assertEquals(entity1MissionName.getBaseValue(), (mergedEntityMissionName.getHistory().get(0).getBaseValue()));

        }
        catch (CoalesceException | InterruptedException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void mergeAttributeTest()
    {

        try
        {
            // Get Entities
            CoalesceEntity entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
            CoalesceEntity entity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

            // Get Mission Name Fields
            CoalesceField<?> entity1MissionName = (CoalesceField<?>) entity1.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);
            CoalesceField<?> entity2MissionName = (CoalesceField<?>) entity2.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

            // Modify Entity 1
            entity1MissionName.setOtherAttribute("newattr1", "1");

            // Sleep for 2 Seconds
            Thread.sleep(2000);

            // Modify Entity 2
            entity2MissionName.setOtherAttribute("newattr2", "2");

            // Merge Entities
            CoalesceEntity mergedEntity = CoalesceEntity.mergeSyncEntity(entity1, entity2);

            // Get Mission Name Field of Merged Entity
            CoalesceField<?> mergedEntityMissionName = (CoalesceField<?>) mergedEntity.getDataObjectForNamePath(CoalesceTypeInstances.TEST_MISSION_NAME_PATH);

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
            CoalesceEntity myEntity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_HISTORY);
            CoalesceEntity updatedEntity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_HISTORY);

            CoalesceFieldDefinition fieldDefinition = (CoalesceFieldDefinition) updatedEntity.getCoalesceDataObjectForKey("1A7DA2CD-8A83-4E86-ADE8-15FDECE0564E");
            fieldDefinition.setDefaultValue("UpdatedIncidentDescription");
            fieldDefinition.setLastModified(new DateTime());
            updatedEntity.setLastModified(new DateTime());

            myEntity = CoalesceEntity.mergeSyncEntity(myEntity, updatedEntity);
            CoalesceFieldDefinition myfieldDefinition = (CoalesceFieldDefinition) myEntity.getCoalesceDataObjectForKey("1A7DA2CD-8A83-4E86-ADE8-15FDECE0564E");
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

    private void setBinaryFileFieldValues(CoalesceRecord record) throws UnsupportedEncodingException
    {
        setBinaryFieldValue(record, "Binary1");
        setBinaryFieldValue(record, "Binary2");
        setBinaryFieldValue(record, "Binary3");

        setFileFieldValue(record, "File1");
        setFileFieldValue(record, "File2");
        setFileFieldValue(record, "File3");

    }

    private void setBinaryFieldValue(CoalesceRecord record, String fieldName) throws UnsupportedEncodingException
    {
        CoalesceBinaryField binaryField = (CoalesceBinaryField) record.getFieldByName(fieldName);
        binaryField.setValue(fieldName.getBytes("US-ASCII"));
    }

    private void setFileFieldValue(CoalesceRecord record, String fieldName) throws UnsupportedEncodingException
    {
        CoalesceFileField fileField = (CoalesceFileField) record.getFieldByName(fieldName);
        fileField.setValue(fieldName.getBytes("US-ASCII"), fieldName + ".jpg", "jpg");

    }

    private void assertBinaryField(CoalesceRecord record, String fieldName, String expectedValue)
            throws UnsupportedEncodingException, CoalesceDataFormatException
    {
        CoalesceBinaryField binaryField = (CoalesceBinaryField) record.getFieldByName(fieldName);
        assertArrayEquals(expectedValue.getBytes("US-ASCII"), binaryField.getValue());
    }

    private void assertFilefiled(CoalesceRecord record, String fieldName, String expectedValue)
            throws UnsupportedEncodingException
    {
        CoalesceFileField fileField = (CoalesceFileField) record.getFieldByName(fieldName);
        assertArrayEquals(expectedValue.getBytes("US-ASCII"), Base64.decode(fileField.getBaseValue()));
    }

    private void assertEmptyEntity(CoalesceEntity entity)
    {

        assertTrue(entity.getLinkages().values().isEmpty());
        assertEquals(0, entity.getSections().size());
    }

    private void assertLinkage(String entity1Key, ELinkTypes type, String entity2Key, CoalesceLinkage linkage)
    {
        assertEquals(entity1Key, linkage.getEntity1Key());
        assertEquals(type, linkage.getLinkType());
        assertEquals(entity2Key, linkage.getEntity2Key());

    }

    private void assertLinkage(CoalesceEntity entity1, CoalesceEntity entity2, ELinkTypes type, CoalesceLinkage linkage)
    {
        assertEquals(entity1.getKey(), linkage.getEntity1Key());
        assertEquals(entity2.getKey(), linkage.getEntity2Key());

        assertEquals(type, linkage.getLinkType());

    }

    private class Entities {

        public CoalesceEntity Entity;
        public CoalesceEntity Entity2;
        public CoalesceEntity Entity3;
        public CoalesceEntity Entity4;

    }

    private Entities createEntityLinkages()
    {
        Entities entities = new Entities();

        entities.Entity = CoalesceEntity.create("Operation", "Portal", "1.1.1.1", "ID", "Type");

        entities.Entity2 = CoalesceEntity.create("Operation", "Portal", "1.2.3.4", "Id2", "Type2");

        entities.Entity3 = CoalesceEntity.create("Mission", "Portal2", "2.3.4.5", "Id3", "Type3");

        entities.Entity4 = CoalesceEntity.create("Operation", "Portal2", "3.4.5.6", "Id4", "Type4");

        assertTrue(EntityLinkHelper.linkEntities(entities.Entity, ELinkTypes.HAS_USE_OF, entities.Entity2, false));
        assertTrue(EntityLinkHelper.linkEntities(entities.Entity, ELinkTypes.IS_PARENT_OF, entities.Entity3, false));
        assertTrue(EntityLinkHelper.linkEntities(entities.Entity2, ELinkTypes.WAS_CREATED_BY, entities.Entity, false));
        assertTrue(EntityLinkHelper.linkEntities(entities.Entity4, ELinkTypes.IS_A_MEMBER_OF, entities.Entity, false));

        return entities;

    }

}
