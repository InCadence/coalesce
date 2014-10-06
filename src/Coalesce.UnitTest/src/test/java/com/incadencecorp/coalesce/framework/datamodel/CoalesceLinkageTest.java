package com.incadencecorp.coalesce.framework.datamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.NullArgumentException;
import org.joda.time.DateTime;
import org.junit.Test;

import com.incadencecorp.coalesce.common.CoalesceTypeInstances;
import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.helpers.GUIDHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceDataObjectStatus;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.framework.generatedjaxb.Entity.Linkagesection.Linkage;

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

public class CoalesceLinkageTest {

    private static final Marking UNCLASSIFIED_MARKING = new Marking("(U)");
    private static final Marking TOP_SECRET_MARKING = new Marking("(//TS)");

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
        CoalesceLinkage linkage = CoalesceLinkage.create(null);
    }

    @Test
    public void createEmptyTest()
    {
        CoalesceEntity entity = CoalesceEntity.create("");

        CoalesceLinkageSection linkageSection = entity.getLinkageSection();

        Map<String, CoalesceLinkage> linkages = linkageSection.getLinkages();

        assertTrue(linkages.isEmpty());

        CoalesceLinkage newLinkage = CoalesceLinkage.create(linkageSection);

        assertNotNull(newLinkage);

        linkages = linkageSection.getLinkages();

        assertEquals(1, linkages.size());
        assertTrue(linkages.containsKey(newLinkage.getKey()));
        assertEquals(linkageSection, newLinkage.getParent());

    }

    @Test
    public void createMissionTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceLinkageSection missionLinkageSection = entity.getLinkageSection();

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

    @Test(expected = NullArgumentException.class)
    public void initializeNullParentTest()
    {
        CoalesceLinkage linkage = new CoalesceLinkage();
        linkage.initialize(null, new Linkage());
    }

    @Test(expected = NullArgumentException.class)
    public void initializeNullLinkageTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceLinkageSection missionLinkageSection = entity.getLinkageSection();

        CoalesceLinkage linkage = new CoalesceLinkage();
        linkage.initialize(missionLinkageSection, null);

    }

    @Test(expected = NullArgumentException.class)
    public void initializeNullBothTest()
    {
        CoalesceLinkage linkage = new CoalesceLinkage();
        linkage.initialize(null, null);
    }

    @Test
    public void keyTest()
    {
        CoalesceLinkage linkage = getMissionLinkage();

        assertEquals("DB7E0EAF-F4EF-4473-94A9-B93A7F46281E", linkage.getKey());

        UUID guid = UUID.randomUUID();

        linkage.setKey(guid);

        assertEquals(guid.toString(), linkage.getKey());

        UUID guid2 = UUID.randomUUID();

        linkage.setKey(guid2.toString());

        assertEquals(guid2.toString(), linkage.getKey());

    }

    @Test
    public void nameTest()
    {
        CoalesceLinkage linkage = getMissionLinkage();

        assertEquals("Linkage", linkage.getName());

        linkage.setName("New Linkage");

        assertEquals("New Linkage", linkage.getName());

    }

    @Test
    public void typeTest()
    {
        CoalesceLinkage linkage = getMissionLinkage();

        assertEquals("linkage", linkage.getType());

        CoalesceEntity newEntity = CoalesceEntity.create("Operation", "Portal", "1.2.3.4", "ID", "Type");
        CoalesceLinkage newLinkage = newEntity.getLinkageSection().createLinkage();

        assertEquals("linkage", newLinkage.getType());

    }

    @Test
    public void modifiedByTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceLinkage linkage = getMissionLinkage(entity);

        DateTime now = JodaDateTimeHelper.nowInUtc();

        assertTrue(StringHelper.isNullOrEmpty(linkage.getModifiedBy()));

        linkage.setModifiedBy("jDoe");

        assertTrue(Math.abs(now.getMillis() - linkage.getLastModified().getMillis()) < 5);

        assertEquals("jDoe", linkage.getModifiedBy());

        String entityXml = entity.toXml();

        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceLinkage desLinkage = getMissionLinkage(desEntity);

        assertTrue(Math.abs(now.getMillis() - linkage.getLastModified().getMillis()) < 5);
        assertEquals("jDoe", desLinkage.getModifiedBy());

        CoalesceEntity newEntity = CoalesceEntity.create("Operation", "Portal", "1.2.3.4", "ID", "Type");
        CoalesceLinkage newLinkage = newEntity.getLinkageSection().createLinkage();

        assertTrue(StringHelper.isNullOrEmpty(newLinkage.getModifiedBy()));

    }

    @Test
    public void classificationMarking()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceLinkage linkage = getMissionLinkage(entity);

        DateTime now = JodaDateTimeHelper.nowInUtc();

        assertEquals(CoalesceLinkageTest.UNCLASSIFIED_MARKING, linkage.getClassificationMarking());

        linkage.setClassificationMarking(CoalesceLinkageTest.TOP_SECRET_MARKING);

        assertTrue(Math.abs(now.getMillis() - linkage.getLastModified().getMillis()) < 5);

        assertEquals(CoalesceLinkageTest.TOP_SECRET_MARKING, linkage.getClassificationMarking());

        String entityXml = entity.toXml();

        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceLinkage desLinkage = getMissionLinkage(desEntity);

        assertTrue(Math.abs(now.getMillis() - linkage.getLastModified().getMillis()) < 5);
        assertEquals(CoalesceLinkageTest.TOP_SECRET_MARKING, desLinkage.getClassificationMarking());

        CoalesceEntity newEntity = CoalesceEntity.create("Operation", "Portal", "1.2.3.4", "ID", "Type");
        CoalesceLinkage newLinkage = newEntity.getLinkageSection().createLinkage();

        assertEquals(CoalesceLinkageTest.UNCLASSIFIED_MARKING, newLinkage.getClassificationMarking());

    }

    @Test
    public void entityTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceLinkage linkage = getMissionLinkage(entity);

        assertEquals("62857EF8-3930-4F0E-BAE3-093344EBF389", linkage.getEntity1Key());
        assertEquals("TREXMission", linkage.getEntity1Name());
        assertEquals("TREX Portal", linkage.getEntity1Source());
        assertEquals("1.0.0.0", linkage.getEntity1Version());

        assertEquals("AEACD69E-5365-4401-87A1-D95E657E0785", linkage.getEntity2Key());
        assertEquals("TREXOperation", linkage.getEntity2Name());
        assertEquals("TREX Portal", linkage.getEntity2Source());
        assertEquals("1.0.0.0", linkage.getEntity2Version());

        String newGuid = GUIDHelper.getGuidString(UUID.randomUUID());
        linkage.setEntity1Key(newGuid);
        linkage.setEntity1Name("New 1 Name");
        linkage.setEntity1Source("New 1 Source");
        linkage.setEntity1Version("New 1 Version");

        String new2Guid = GUIDHelper.getGuidString(UUID.randomUUID());
        linkage.setEntity2Key(new2Guid);
        linkage.setEntity2Name("New 2 Name");
        linkage.setEntity2Source("New 2 Source");
        linkage.setEntity2Version("New 2 Version");

        assertEquals(newGuid, linkage.getEntity1Key());
        assertEquals("New 1 Name", linkage.getEntity1Name());
        assertEquals("New 1 Source", linkage.getEntity1Source());
        assertEquals("New 1 Version", linkage.getEntity1Version());

        assertEquals(new2Guid, linkage.getEntity2Key());
        assertEquals("New 2 Name", linkage.getEntity2Name());
        assertEquals("New 2 Source", linkage.getEntity2Source());
        assertEquals("New 2 Version", linkage.getEntity2Version());

        String entityXml = entity.toXml();
        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceLinkage desLinkage = getMissionLinkage(desEntity);

        assertEquals(newGuid, desLinkage.getEntity1Key());
        assertEquals("New 1 Name", desLinkage.getEntity1Name());
        assertEquals("New 1 Source", desLinkage.getEntity1Source());
        assertEquals("New 1 Version", desLinkage.getEntity1Version());

        assertEquals(new2Guid, desLinkage.getEntity2Key());
        assertEquals("New 2 Name", desLinkage.getEntity2Name());
        assertEquals("New 2 Source", desLinkage.getEntity2Source());
        assertEquals("New 2 Version", desLinkage.getEntity2Version());

    }

    @Test
    public void inputLangTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceLinkage linkage = getMissionLinkage(entity);

        DateTime lastMod = linkage.getLastModified();

        assertEquals(Locale.US, linkage.getInputLang());

        linkage.setInputLang(Locale.UK);

        assertTrue(lastMod.isBefore(linkage.getLastModified()));

        assertEquals(Locale.UK, linkage.getInputLang());

        String entityXml = entity.toXml();

        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceLinkage desLinkage = getMissionLinkage(desEntity);

        assertTrue(lastMod.isBefore(linkage.getLastModified()));
        assertEquals(Locale.UK, desLinkage.getInputLang());

        CoalesceEntity newEntity = CoalesceEntity.create("Operation", "Portal", "1.2.3.4", "ID", "Type");
        CoalesceLinkage newLinkage = newEntity.getLinkageSection().createLinkage();

        assertEquals(null, newLinkage.getInputLang());

    }

    @Test
    public void linkTypeTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceLinkage linkage = getMissionLinkage(entity);

        DateTime now = JodaDateTimeHelper.nowInUtc();

        assertEquals(ELinkTypes.IS_CHILD_OF, linkage.getLinkType());

        linkage.setLinkType(ELinkTypes.IS_PARENT_OF);

        assertTrue(Math.abs(now.getMillis() - linkage.getLastModified().getMillis()) < 5);

        assertEquals(ELinkTypes.IS_PARENT_OF, linkage.getLinkType());

        String entityXml = entity.toXml();

        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceLinkage desLinkage = getMissionLinkage(desEntity);

        assertTrue(Math.abs(now.getMillis() - linkage.getLastModified().getMillis()) < 5);
        assertEquals(ELinkTypes.IS_PARENT_OF, desLinkage.getLinkType());

        CoalesceEntity newEntity = CoalesceEntity.create("Operation", "Portal", "1.2.3.4", "ID", "Type");
        CoalesceLinkage newLinkage = newEntity.getLinkageSection().createLinkage();

        assertEquals(ELinkTypes.UNDEFINED, newLinkage.getLinkType());

    }

    @Test
    public void noIndexTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceLinkage linkage = getMissionLinkage(entity);

        assertFalse(linkage.getNoIndex());

        linkage.setNoIndex(true);

        assertTrue(linkage.getNoIndex());

        String entityXml = entity.toXml();

        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceLinkage desLinkage = getMissionLinkage(desEntity);

        assertTrue(desLinkage.getNoIndex());

        CoalesceEntity newEntity = CoalesceEntity.create("Operation", "Portal", "1.2.3.4", "ID", "Type");
        CoalesceLinkage newLinkage = newEntity.getLinkageSection().createLinkage();

        assertFalse(newLinkage.getNoIndex());

    }

    @Test
    public void DateCreatedTest()
    {
        CoalesceLinkage linkage = getMissionLinkage();

        assertEquals(JodaDateTimeHelper.fromXmlDateTimeUTC("2014-05-02T14:33:51.8615756Z"), linkage.getDateCreated());

        DateTime now = JodaDateTimeHelper.nowInUtc();
        linkage.setDateCreated(now);

        assertEquals(now, linkage.getDateCreated());

    }

    @Test
    public void LastModifiedTest()
    {
        CoalesceLinkage linkage = getMissionLinkage();

        assertEquals(JodaDateTimeHelper.fromXmlDateTimeUTC("2014-05-02T14:33:51.8615756Z"), linkage.getLastModified());

        DateTime now = JodaDateTimeHelper.nowInUtc();
        linkage.setLastModified(now);

        assertEquals(now, linkage.getLastModified());

    }

    @Test
    public void getIsMarkedDeletedTest()
    {
        CoalesceLinkage linkage = getMissionLinkage();

        assertFalse(linkage.getIsMarkedDeleted());

        linkage.setStatus(ECoalesceDataObjectStatus.DELETED);

        assertTrue(linkage.getIsMarkedDeleted());

        linkage.setStatus(ECoalesceDataObjectStatus.UNKNOWN);

        assertFalse(linkage.getIsMarkedDeleted());

    }

    private CoalesceLinkage getMissionLinkage()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        return getMissionLinkage(entity);

    }

    private CoalesceLinkage getMissionLinkage(CoalesceEntity entity)
    {
        CoalesceLinkageSection missionLinkageSection = entity.getLinkageSection();

        return missionLinkageSection.getLinkages().get("DB7E0EAF-F4EF-4473-94A9-B93A7F46281E");

    }

}
