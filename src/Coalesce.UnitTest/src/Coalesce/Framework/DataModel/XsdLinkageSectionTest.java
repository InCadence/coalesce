package Coalesce.Framework.DataModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.NullArgumentException;
import org.joda.time.DateTime;
import org.junit.Test;

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

public class XsdLinkageSectionTest {

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
        XsdLinkageSection linkageSection = XsdLinkageSection.create(null);
    }

    @Test
    public void createExistsTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdLinkageSection linkageSection = entity.getLinkageSection();

        XsdLinkageSection createdLinkageSection = XsdLinkageSection.create(entity);

        assertEquals(linkageSection, createdLinkageSection);
        assertTrue(createdLinkageSection.getNoIndex());

    }

    @Test
    public void createTest()
    {
        XsdEntity entity = new XsdEntity();
        entity.initialize();

        XsdLinkageSection linkageSection = entity.getLinkageSection();

        assertNotNull(linkageSection);
        assertEquals(entity, linkageSection.getParent());
        assertFalse(linkageSection.getNoIndex());

    }

    @Test(expected = NullArgumentException.class)
    public void initializeNullTest()
    {
        XsdLinkageSection linkageSection = new XsdLinkageSection();
        linkageSection.initialize(null);

    }

    @Test
    public void keyTest()
    {
        XsdLinkageSection linkageSection = getMissionLinkageSection();

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
        XsdLinkageSection linkageSection = getMissionLinkageSection();

        assertEquals("Linkages", linkageSection.getName());

        linkageSection.setName("New Linkages");

        assertEquals("New Linkages", linkageSection.getName());

    }

    @Test
    public void typeTest()
    {
        XsdLinkageSection linkageSection = getMissionLinkageSection();

        assertEquals("linkagesection", linkageSection.getType());

        XsdEntity newEntity = XsdEntity.create("Operation", "Portal", "1.2.3.4", "ID", "Type");

        assertEquals("linkagesection", newEntity.getLinkageSection().getType());

    }

    @Test
    public void createLinkageEmptyTest()
    {
        XsdEntity entity = XsdEntity.create("");

        XsdLinkageSection linkageSection = entity.getLinkageSection();

        Map<String, XsdLinkage> linkages = linkageSection.getLinkages();

        assertTrue(linkages.isEmpty());

        XsdLinkage newLinkage = linkageSection.createLinkage();

        assertNotNull(newLinkage);

        linkages = linkageSection.getLinkages();

        assertEquals(1, linkages.size());
        assertTrue(linkages.containsKey(newLinkage.getKey()));
        assertEquals(linkageSection, newLinkage.getParent());

    }

    @Test
    public void createLinkageMissionTest()
    {
        XsdLinkageSection missionLinkageSection = getMissionLinkageSection();

        Map<String, XsdLinkage> missionLinkages = missionLinkageSection.getLinkages();

        assertEquals(4, missionLinkages.size());

        XsdLinkage missionLinkage = missionLinkageSection.createLinkage();

        assertNotNull(missionLinkage);

        Map<String, XsdLinkage> linkages = missionLinkageSection.getLinkages();

        assertTrue(linkages.containsKey(missionLinkage.getKey()));
        assertTrue(linkages.containsValue(missionLinkage));

        for (XsdLinkage linkage : missionLinkages.values())
        {
            assertTrue(linkages.containsKey(linkage.getKey()));
            assertTrue(linkages.containsValue(linkage));
        }

    }

    @Test
    public void noIndexTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        XsdLinkageSection linkageSection = entity.getLinkageSection();

        assertTrue(linkageSection.getNoIndex());

        linkageSection.setNoIndex(false);

        assertFalse(linkageSection.getNoIndex());

        String entityXml = entity.toXml();

        XsdEntity desEntity = XsdEntity.create(entityXml);
        XsdLinkageSection desLinkageSection = desEntity.getLinkageSection();

        assertFalse(desLinkageSection.getNoIndex());

        XsdEntity newEntity = XsdEntity.create("Operation", "Portal", "1.2.3.4", "ID", "Type");

        assertFalse(newEntity.getLinkageSection().getNoIndex());

    }

    @Test
    public void DateCreatedTest()
    {
        XsdLinkageSection linkageSection = getMissionLinkageSection();

        assertEquals(JodaDateTimeHelper.FromXmlDateTimeUTC("2014-05-02T14:33:51.851575Z"), linkageSection.getDateCreated());

        DateTime now = JodaDateTimeHelper.NowInUtc();
        linkageSection.setDateCreated(now);

        assertEquals(now, linkageSection.getDateCreated());

    }

    @Test
    public void LastModifiedTest()
    {
        XsdLinkageSection linkageSection = getMissionLinkageSection();

        assertEquals(JodaDateTimeHelper.FromXmlDateTimeUTC("2014-05-20T16:17:13.2293139Z"), linkageSection.getLastModified());

        DateTime now = JodaDateTimeHelper.NowInUtc();
        linkageSection.setLastModified(now);

        assertEquals(now, linkageSection.getLastModified());

    }

    private XsdLinkageSection getMissionLinkageSection()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        return entity.getLinkageSection();
    }

}
