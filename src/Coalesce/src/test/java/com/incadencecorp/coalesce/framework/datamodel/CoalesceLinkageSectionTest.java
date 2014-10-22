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
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;
import com.incadencecorp.coalesce.framework.generatedjaxb.Linkagesection;

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

public class CoalesceLinkageSectionTest {

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
        CoalesceLinkageSection linkageSection = CoalesceLinkageSection.create(null);
    }

    @Test
    public void createExistsTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceLinkageSection linkageSection = entity.getLinkageSection();

        CoalesceLinkageSection createdLinkageSection = CoalesceLinkageSection.create(entity);

        assertEquals(linkageSection, createdLinkageSection);
        assertTrue(createdLinkageSection.getNoIndex());

    }

    @Test
    public void createTest()
    {
        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();

        CoalesceLinkageSection linkageSection = entity.getLinkageSection();

        assertNotNull(linkageSection);
        assertEquals(entity, linkageSection.getParent());
        assertFalse(linkageSection.getNoIndex());

    }

    @Test
    public void createLinkageSectionParentMissingLinkageSection()
    {
        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();
        entity.getChildDataObjects().clear();

        assertNull("Parent entity shouldn't have a linkage section yet", entity.getLinkageSection());

        CoalesceLinkageSection linkageSection = CoalesceLinkageSection.create(entity, false);

        assertNotNull(linkageSection);
        assertEquals(entity.getLinkageSection(), linkageSection);
        assertEquals(entity, linkageSection.getParent());
        assertFalse(linkageSection.getNoIndex());

        assertTrue(entity.getChildDataObjects().containsKey(linkageSection.getKey()));

    }

    @Test(expected = NullArgumentException.class)
    public void initializeNullTest()
    {
        CoalesceLinkageSection linkageSection = new CoalesceLinkageSection();
        linkageSection.initialize(null);

    }

    @Test
    public void keyTest()
    {
        CoalesceLinkageSection linkageSection = getMissionLinkageSection();

        assertEquals("F4F126AF-4658-4D7F-A67F-4833F7EADDC3", linkageSection.getKey());

        UUID guid = UUID.randomUUID();

        linkageSection.setKey(guid);

        assertEquals(guid.toString(), linkageSection.getKey());

        UUID guid2 = UUID.randomUUID();

        linkageSection.setKey(guid2.toString());

        assertEquals(guid2.toString(), linkageSection.getKey());

    }

    @Test
    public void nameTest()
    {
        CoalesceLinkageSection linkageSection = getMissionLinkageSection();

        assertEquals("Linkages", linkageSection.getName());

        linkageSection.setName("New Linkages");

        assertEquals("New Linkages", linkageSection.getName());

    }

    @Test
    public void typeTest()
    {
        CoalesceLinkageSection linkageSection = getMissionLinkageSection();

        assertEquals("linkagesection", linkageSection.getType());

        CoalesceEntity newEntity = CoalesceEntity.create("Operation", "Portal", "1.2.3.4", "ID", "Type");

        assertEquals("linkagesection", newEntity.getLinkageSection().getType());

    }

    @Test
    public void createLinkageEmptyTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("");

        CoalesceLinkageSection linkageSection = entity.getLinkageSection();

        Map<String, CoalesceLinkage> linkages = linkageSection.getLinkages();

        assertTrue(linkages.isEmpty());

        CoalesceLinkage newLinkage = linkageSection.createLinkage();

        assertNotNull(newLinkage);

        linkages = linkageSection.getLinkages();

        assertEquals(1, linkages.size());
        assertTrue(linkages.containsKey(newLinkage.getKey()));
        assertEquals(linkageSection, newLinkage.getParent());

    }

    @Test
    public void createLinkageMissionTest()
    {
        CoalesceLinkageSection missionLinkageSection = getMissionLinkageSection();

        Map<String, CoalesceLinkage> missionLinkages = missionLinkageSection.getLinkages();

        assertEquals(4, missionLinkages.size());

        CoalesceLinkage missionLinkage = missionLinkageSection.createLinkage();

        assertNotNull(missionLinkage);

        Map<String, CoalesceLinkage> linkages = missionLinkageSection.getLinkages();

        assertTrue(linkages.containsKey(missionLinkage.getKey()));
        assertTrue(linkages.containsValue(missionLinkage));

        for (CoalesceLinkage linkage : missionLinkages.values())
        {
            assertTrue(linkages.containsKey(linkage.getKey()));
            assertTrue(linkages.containsValue(linkage));
        }

    }

    @Test
    public void noIndexTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceLinkageSection linkageSection = entity.getLinkageSection();

        assertTrue(linkageSection.getNoIndex());

        linkageSection.setNoIndex(false);

        assertFalse(linkageSection.getNoIndex());

        String entityXml = entity.toXml();

        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceLinkageSection desLinkageSection = desEntity.getLinkageSection();

        assertFalse(desLinkageSection.getNoIndex());

        CoalesceEntity newEntity = CoalesceEntity.create("Operation", "Portal", "1.2.3.4", "ID", "Type");

        assertFalse(newEntity.getLinkageSection().getNoIndex());

    }

    @Test
    public void dateCreatedTest()
    {
        CoalesceLinkageSection linkageSection = getMissionLinkageSection();

        assertEquals(JodaDateTimeHelper.fromXmlDateTimeUTC("2014-05-02T14:33:51.851575Z"), linkageSection.getDateCreated());

        DateTime now = JodaDateTimeHelper.nowInUtc();
        linkageSection.setDateCreated(now);

        assertEquals(now, linkageSection.getDateCreated());

    }

    @Test
    public void lastModifiedTest()
    {
        CoalesceLinkageSection linkageSection = getMissionLinkageSection();

        assertEquals(JodaDateTimeHelper.fromXmlDateTimeUTC("2014-05-20T16:17:13.2293139Z"), linkageSection.getLastModified());

        DateTime now = JodaDateTimeHelper.nowInUtc();
        linkageSection.setLastModified(now);

        assertEquals(now, linkageSection.getLastModified());

    }

    @Test
    public void toXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceLinkageSection linkageSection = entity.getLinkageSection();
        String linkageXml = linkageSection.toXml();

        Linkagesection desLinkageSection = (Linkagesection) XmlHelper.deserialize(linkageXml, Linkagesection.class);

        assertEquals(linkageSection.getLinkages().size(), desLinkageSection.getLinkage().size());
        assertEquals(linkageSection.getKey(), desLinkageSection.getKey());
        assertEquals(linkageSection.getName(), desLinkageSection.getName());
        assertEquals(linkageSection.getNoIndex(), Boolean.parseBoolean(desLinkageSection.getNoindex()));
        assertEquals(linkageSection.getDateCreated(), desLinkageSection.getDatecreated());
        assertEquals(linkageSection.getLastModified(), desLinkageSection.getLastmodified());
        assertEquals(linkageSection.getStatus(), ECoalesceDataObjectStatus.getTypeForLabel(desLinkageSection.getStatus()));

    }

    @Test
    public void setStatusTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceLinkageSection linkageSection = entity.getLinkageSection();

        assertEquals(ECoalesceDataObjectStatus.ACTIVE, linkageSection.getStatus());
        
        linkageSection.setStatus(ECoalesceDataObjectStatus.UNKNOWN);
        String linkageXml = linkageSection.toXml();

        Linkagesection desLinkageSection = (Linkagesection) XmlHelper.deserialize(linkageXml, Linkagesection.class);

        assertEquals(ECoalesceDataObjectStatus.UNKNOWN, ECoalesceDataObjectStatus.getTypeForLabel(desLinkageSection.getStatus()));

    }
    
    @Test
    public void attributeTest()
    {
        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();
        
        CoalesceLinkageSection linkageSection = entity.getLinkageSection();
        linkageSection.setAttribute("TestAttribute", "TestingValue");
        
        assertEquals(6, linkageSection.getAttributes().size());
        
        assertEquals("TestingValue", linkageSection.getAttribute("TestAttribute"));
        
        assertEquals("Linkages", linkageSection.getName());
        assertEquals(false, linkageSection.getNoIndex());
        
        linkageSection.setAttribute("Name", "TestingName");
        assertEquals("TestingName", linkageSection.getName());
        assertEquals("TestingName", linkageSection.getAttribute("Name"));

        UUID guid = UUID.randomUUID();
        linkageSection.setAttribute("Key", guid.toString());
        assertEquals(guid.toString(), linkageSection.getKey());
        assertEquals(guid.toString(), linkageSection.getAttribute("Key"));
        
        DateTime now = JodaDateTimeHelper.nowInUtc();
        DateTime future = now.plusDays(2);
        
        linkageSection.setAttribute("DateCreated", JodaDateTimeHelper.toXmlDateTimeUTC(now));
        assertEquals(now, linkageSection.getDateCreated());
        
        linkageSection.setAttribute("NoIndex", "True");
        assertEquals(true, linkageSection.getNoIndex());
        
        linkageSection.setAttribute("Status", ECoalesceDataObjectStatus.UNKNOWN.getLabel());
        assertEquals(ECoalesceDataObjectStatus.UNKNOWN, linkageSection.getStatus());
        
        linkageSection.setAttribute("LastModified", JodaDateTimeHelper.toXmlDateTimeUTC(future));
        assertEquals(future, linkageSection.getLastModified());

        String entityXml = entity.toXml();
        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceLinkageSection desLinkageSection = desEntity.getLinkageSection();

        assertEquals("TestingValue", desLinkageSection.getAttribute("TestAttribute"));
        assertEquals("TestingName", desLinkageSection.getName());
        assertEquals(guid.toString(), desLinkageSection.getKey());
        assertEquals(now, desLinkageSection.getDateCreated());
        assertEquals(future, desLinkageSection.getLastModified());
        assertEquals(true, desLinkageSection.getNoIndex());
        assertEquals(ECoalesceDataObjectStatus.UNKNOWN, desLinkageSection.getStatus());
        
    }
    
    // -----------------------------------------------------------------------//
    // Private Methods
    // -----------------------------------------------------------------------//

    private CoalesceLinkageSection getMissionLinkageSection()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        return entity.getLinkageSection();
    }

}
