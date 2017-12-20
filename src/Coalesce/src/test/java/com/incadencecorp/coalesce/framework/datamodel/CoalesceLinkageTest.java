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
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.incadencecorp.coalesce.common.CoalesceTypeInstances;
import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.EntityLinkHelper;
import com.incadencecorp.coalesce.common.helpers.GUIDHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;

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

    @Rule
    public ExpectedException _thrown = ExpectedException.none();

    private static final Marking UNCLASSIFIED_MARKING = new Marking("(U)");
    private static final Marking TOP_SECRET_MARKING = new Marking("(TS)");

    /**
     * This test ensures that clearing linkages well remove them from the
     * children hash map.
     * 
     * @throws Exception
     */
    @Test
    public void clearLinkagesTest() throws Exception

    {
        // Create Entities
        CoalesceEntity entity1 = new CoalesceEntity();
        entity1.initialize();

        CoalesceEntity entity2 = new CoalesceEntity();
        entity2.initialize();

        // Verify No Linkage
        Assert.assertEquals(0, entity1.getLinkages().size());

        // Link Entities
        EntityLinkHelper.linkEntities(entity1, ELinkTypes.CREATED, entity2, null, null, entity1.getKey(), true, ECoalesceObjectStatus.ACTIVE, false);

        // Verify
        Assert.assertEquals(1, entity1.getLinkages().size());
        Assert.assertEquals(entity1.getKey(), entity1.getLinkage(ELinkTypes.CREATED).getLabel());

        // Clear Linkages
        entity1.getLinkageSection().clearLinkages();

        // Verify
        Assert.assertEquals(0, entity1.getLinkages().size());
        Assert.assertNull(entity1.getLinkage(ELinkTypes.CREATED));
    }

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

    /**
     * Verifies that a user that can not modify readonly links gets an error.
     * 
     * @throws Exception
     */
    @Test(expected = CoalesceException.class)
    public void readOnlyLinkFailureTest() throws Exception
    {
        CoalesceEntity entity1 = new CoalesceEntity();
        entity1.initialize();

        CoalesceEntity entity2 = new CoalesceEntity();
        entity2.initialize();

        EntityLinkHelper.linkEntities(entity1, ELinkTypes.CREATED, entity2, "Derek", "127.0.0.1", "", true, ECoalesceObjectStatus.READONLY, false);
        EntityLinkHelper.unLinkEntities(entity1, entity2, ELinkTypes.CREATED, "Derek", "127.0.0.1", false);

    }

    /**
     * Verifies that a user that can not modify readonly links gets an error.
     * 
     * @throws Exception
     */
    @Test(expected = CoalesceException.class)
    public void readOnlyUnLinkFailureTest() throws Exception
    {
        CoalesceEntity entity1 = new CoalesceEntity();
        entity1.initialize();

        CoalesceEntity entity2 = new CoalesceEntity();
        entity2.initialize();

        EntityLinkHelper.linkEntities(entity1, ELinkTypes.CREATED, entity2, "Derek", "127.0.0.1", "", true, ECoalesceObjectStatus.READONLY, false);
        EntityLinkHelper.linkEntities(entity1, ELinkTypes.CREATED, entity2, "Derek", "127.0.0.1", "", true, ECoalesceObjectStatus.READONLY, false);

    }

    /**
     * This unit test verifies that user who can modify read only links can do
     * so.
     * 
     * @throws Exception
     */
    @Test
    public void readOnlyTest() throws Exception
    {
        CoalesceEntity entity1 = new CoalesceEntity();
        entity1.initialize();

        CoalesceEntity entity2 = new CoalesceEntity();
        entity2.initialize();

        EntityLinkHelper.linkEntities(entity1, ELinkTypes.CREATED, entity2, "Derek", "127.0.0.1", "", true, ECoalesceObjectStatus.READONLY, false);
        EntityLinkHelper.linkEntities(entity1, ELinkTypes.CREATED, entity2, "Derek", "127.0.0.1", "", true, ECoalesceObjectStatus.READONLY, true);

        assertTrue(verifyLinkage(entity1, entity2, ELinkTypes.CREATED));

        EntityLinkHelper.unLinkEntities(entity1, entity2, ELinkTypes.CREATED, "Derek", "127.0.0.1", true);

        assertFalse(verifyLinkage(entity1, entity2, ELinkTypes.CREATED));

    }

    /**
     * This unit test verifies that linkage history is created correctly.
     * 
     * @throws Exception
     */
    @Test
    public void LinkageHistoryTest() throws Exception
    {

        // Create Entities
        TestEntity entity1 = new TestEntity();
        entity1.initialize();

        TestEntity entity2 = new TestEntity();
        entity2.initialize();

        // Create History
        EntityLinkHelper.linkEntities(entity1, ELinkTypes.CREATED, entity2, "Derek1", "127.0.0.1", "", true, ECoalesceObjectStatus.ACTIVE, true);
        EntityLinkHelper.linkEntities(entity1, ELinkTypes.CREATED, entity2, "Derek2", "127.0.0.1", "", true, ECoalesceObjectStatus.READONLY, true);
        EntityLinkHelper.unLinkEntities(entity1, entity2, ELinkTypes.CREATED, "Derek3", "127.0.0.1", true);
        EntityLinkHelper.linkEntities(entity1, ELinkTypes.CREATED, entity2, "Derek4", "127.0.0.1", "", true, ECoalesceObjectStatus.ACTIVE, true);

        // Get Linkages
        Map<String, CoalesceLinkage> linkages = entity1.getLinkageSection().getLinkages();

        // Verify
        assertEquals(1, linkages.size());

        // Get linkage
        CoalesceLinkage linkage = linkages.values().iterator().next();

        assertEquals(ECoalesceObjectStatus.ACTIVE, linkage.getStatus());
        assertEquals("Derek4", linkage.getModifiedBy());

        // Verify History
        assertEquals(3, linkage.getHistory().length);

        CoalesceHistory history;

        history = linkage.getHistoryRecord(linkage.getPreviousHistoryKey());
        assertEquals(ECoalesceObjectStatus.DELETED, history.getStatus());
        assertEquals("Derek3", history.getModifiedBy());

        history = linkage.getHistoryRecord(history.getPreviousHistoryKey());
        assertEquals(ECoalesceObjectStatus.READONLY, history.getStatus());
        assertEquals("Derek2", history.getModifiedBy());

        history = linkage.getHistoryRecord(history.getPreviousHistoryKey());
        assertEquals(ECoalesceObjectStatus.ACTIVE, history.getStatus());
        assertEquals("Derek1", history.getModifiedBy());

    }

    @Test
    public void modifiedByTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceLinkage linkage = getMissionLinkage(entity);

        DateTime now = JodaDateTimeHelper.nowInUtc();

        assertTrue(StringHelper.isNullOrEmpty(linkage.getModifiedBy()));

        linkage.setModifiedBy("jDoe");

        // assertTrue(Math.abs(now.getMillis() -
        // linkage.getLastModified().getMillis()) < 5);

        assertEquals("jDoe", linkage.getModifiedBy());

        String entityXml = entity.toXml();

        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceLinkage desLinkage = getMissionLinkage(desEntity);

        // assertTrue(Math.abs(now.getMillis() -
        // linkage.getLastModified().getMillis()) < 5);
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

        assertTrue(lastMod.isBefore(desLinkage.getLastModified()));
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

        assertFalse(linkage.isNoIndex());

        linkage.setNoIndex(true);

        assertTrue(linkage.isNoIndex());

        String entityXml = entity.toXml();

        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceLinkage desLinkage = getMissionLinkage(desEntity);

        assertTrue(desLinkage.isNoIndex());

        CoalesceEntity newEntity = CoalesceEntity.create("Operation", "Portal", "1.2.3.4", "ID", "Type");
        CoalesceLinkage newLinkage = newEntity.getLinkageSection().createLinkage();

        assertFalse(newLinkage.isNoIndex());

    }

    @Test
    public void dateCreatedTest()
    {
        CoalesceLinkage linkage = getMissionLinkage();

        assertEquals(JodaDateTimeHelper.fromXmlDateTimeUTC("2014-05-02T14:33:51.8615756Z"), linkage.getDateCreated());

        DateTime now = JodaDateTimeHelper.nowInUtc();
        linkage.setDateCreated(now);

        assertEquals(now, linkage.getDateCreated());

    }

    @Test
    public void lastModifiedTest()
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

        assertFalse(linkage.isMarkedDeleted());

        linkage.setStatus(ECoalesceObjectStatus.DELETED);

        assertTrue(linkage.isMarkedDeleted());

        linkage.setStatus(ECoalesceObjectStatus.UNKNOWN);

        assertFalse(linkage.isMarkedDeleted());

    }

    @Test
    public void toXmlTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceLinkage linkage = entity.getLinkageSection().getLinkages().get("DB7E0EAF-F4EF-4473-94A9-B93A7F46281E");
        String linkageXml = linkage.toXml();

        Linkage desLinkage = (Linkage) XmlHelper.deserialize(linkageXml, Linkage.class);

        assertEquals(linkage.getKey(), desLinkage.getKey());
        assertEquals(linkage.getDateCreated(), desLinkage.getDatecreated());
        assertEquals(linkage.getLastModified(), desLinkage.getLastmodified());
        assertEquals(linkage.getName(), desLinkage.getName());
        assertEquals(linkage.getEntity1Key(), desLinkage.getEntity1Key());
        assertEquals(linkage.getEntity1Name(), desLinkage.getEntity1Name());
        assertEquals(linkage.getEntity1Source(), desLinkage.getEntity1Source());
        assertEquals(linkage.getEntity1Version(), desLinkage.getEntity1Version());
        assertEquals(linkage.getLinkType(), ELinkTypes.getTypeForLabel(desLinkage.getLinktype()));
        assertEquals(linkage.getEntity2Key(), desLinkage.getEntity2Key());
        assertEquals(linkage.getEntity2Name(), desLinkage.getEntity2Name());
        assertEquals(linkage.getEntity2Source(), desLinkage.getEntity2Source());
        assertEquals(linkage.getEntity2Version(), desLinkage.getEntity2Version());
        assertEquals(linkage.getClassificationMarking(), new Marking(desLinkage.getClassificationmarking()));
        assertEquals(linkage.getModifiedBy(), desLinkage.getModifiedby());
        assertEquals(linkage.getInputLang(), desLinkage.getInputlang());
        assertEquals(linkage.getStatus(), desLinkage.getStatus());

    }

    @Test
    public void setStatusTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceLinkage linkage = entity.getLinkageSection().getLinkages().get("DB7E0EAF-F4EF-4473-94A9-B93A7F46281E");

        assertEquals(ECoalesceObjectStatus.ACTIVE, linkage.getStatus());

        linkage.setStatus(ECoalesceObjectStatus.UNKNOWN);
        String linkageXml = linkage.toXml();

        Linkage desLinkage = (Linkage) XmlHelper.deserialize(linkageXml, Linkage.class);

        assertEquals(ECoalesceObjectStatus.UNKNOWN, desLinkage.getStatus());

    }

    @Test
    public void attributeTest()
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceLinkage linkage = entity.getLinkageSection().getLinkages().get("DB7E0EAF-F4EF-4473-94A9-B93A7F46281E");

        int before = linkage.getAttributes().size();

        linkage.setAttribute("TestAttribute", "TestingValue");

        System.out.println(linkage.toXml());

        assertEquals(before + 1, linkage.getAttributes().size());

        assertEquals("TestingValue", linkage.getAttribute("TestAttribute"));

        assertEquals("Linkage", linkage.getName());
        assertEquals(false, linkage.isNoIndex());

        linkage.setAttribute("Name", "TestingName");
        assertEquals("TestingName", linkage.getName());
        assertEquals("TestingName", linkage.getAttribute("Name"));

        UUID guid = UUID.randomUUID();
        linkage.setAttribute("Key", guid.toString());
        assertEquals(guid.toString(), linkage.getKey());
        assertEquals(guid.toString(), linkage.getAttribute("Key"));

        DateTime now = JodaDateTimeHelper.nowInUtc();
        DateTime future = now.plusDays(2);

        linkage.setAttribute("DateCreated", JodaDateTimeHelper.toXmlDateTimeUTC(now));
        assertEquals(now, linkage.getDateCreated());

        UUID entity1Guid = UUID.randomUUID();
        linkage.setAttribute("EnTiTy1Key", entity1Guid.toString());
        assertEquals(entity1Guid.toString(), linkage.getEntity1Key());

        linkage.setAttribute("Entity1Name", "TestEntity1Name");
        assertEquals("TestEntity1Name", linkage.getEntity1Name());

        linkage.setAttribute("Entity1Source", "TestEntity1Source");
        assertEquals("TestEntity1Source", linkage.getEntity1Source());

        linkage.setAttribute("Entity1Version", "TestEntity1Version");
        assertEquals("TestEntity1Version", linkage.getEntity1Version());

        linkage.setAttribute("LinkType", ELinkTypes.HAS_MEMBER.getLabel());
        assertEquals(ELinkTypes.HAS_MEMBER, linkage.getLinkType());

        UUID entity2Guid = UUID.randomUUID();
        linkage.setAttribute("EnTiTy2Key", entity2Guid.toString());
        assertEquals(entity2Guid.toString(), linkage.getEntity2Key());

        linkage.setAttribute("Entity2Name", "TestEntity2Name");
        assertEquals("TestEntity2Name", linkage.getEntity2Name());

        linkage.setAttribute("Entity2Source", "TestEntity2Source");
        assertEquals("TestEntity2Source", linkage.getEntity2Source());

        linkage.setAttribute("Entity2Version", "TestEntity2Version");
        assertEquals("TestEntity2Version", linkage.getEntity2Version());

        linkage.setAttribute("ClassificationMarking", "(TS)");
        assertEquals(new Marking("(TS)"), linkage.getClassificationMarking());

        linkage.setAttribute("ModifiedBy", "TestingUser");
        assertEquals("TestingUser", linkage.getModifiedBy());

        linkage.setAttribute("InputLang", "en");
        assertEquals(Locale.ENGLISH, linkage.getInputLang());

        linkage.setAttribute("NoIndex", "True");
        assertEquals(true, linkage.isNoIndex());

        linkage.setAttribute("Status", ECoalesceObjectStatus.UNKNOWN.toString());
        assertEquals(ECoalesceObjectStatus.UNKNOWN, linkage.getStatus());

        linkage.setAttribute("LastModified", JodaDateTimeHelper.toXmlDateTimeUTC(future));
        assertEquals(future, linkage.getLastModified());

        String entityXml = entity.toXml();
        CoalesceEntity desEntity = CoalesceEntity.create(entityXml);
        CoalesceLinkage desLinkage = desEntity.getLinkageSection().getLinkages().get(guid.toString());

        assertEquals("TestingValue", desLinkage.getAttribute("TestAttribute"));
        assertEquals("TestingName", desLinkage.getName());
        assertEquals(guid.toString(), desLinkage.getKey());
        assertEquals(now, desLinkage.getDateCreated());
        assertEquals(future, desLinkage.getLastModified());
        assertEquals(entity1Guid.toString(), desLinkage.getEntity1Key());
        assertEquals("TestEntity1Name", desLinkage.getEntity1Name());
        assertEquals("TestEntity1Source", desLinkage.getEntity1Source());
        assertEquals("TestEntity1Version", desLinkage.getEntity1Version());
        assertEquals(ELinkTypes.HAS_MEMBER, desLinkage.getLinkType());
        assertEquals(entity2Guid.toString(), desLinkage.getEntity2Key());
        assertEquals("TestEntity2Name", desLinkage.getEntity2Name());
        assertEquals("TestEntity2Source", desLinkage.getEntity2Source());
        assertEquals("TestEntity2Version", desLinkage.getEntity2Version());
        assertEquals(new Marking("(TS)"), desLinkage.getClassificationMarking());
        assertEquals("TestingUser", desLinkage.getModifiedBy());
        assertEquals(Locale.ENGLISH, desLinkage.getInputLang());
        assertEquals(true, desLinkage.isNoIndex());
        assertEquals(ECoalesceObjectStatus.UNKNOWN, desLinkage.getStatus());

    }

    @Test
    public void setAttributeInputLangInvalidCaseTest() throws CoalesceDataFormatException
    {
        _thrown.expect(IllegalArgumentException.class);
        _thrown.expectMessage("");

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceLinkage linkage = entity.getLinkageSection().getLinkages().get("DB7E0EAF-F4EF-4473-94A9-B93A7F46281E");

        linkage.setAttribute("InputLang", "en-gb");

    }

    @Test
    public void setAttributeInputLangMissingDashTest() throws CoalesceDataFormatException
    {
        _thrown.expect(IllegalArgumentException.class);
        _thrown.expectMessage("");

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        CoalesceLinkage linkage = entity.getLinkageSection().getLinkages().get("DB7E0EAF-F4EF-4473-94A9-B93A7F46281E");

        linkage.setAttribute("InputLang", "engb");

    }

    // -----------------------------------------------------------------------//
    // Private Methods
    // -----------------------------------------------------------------------//

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

    private boolean verifyLinkage(CoalesceEntity entity1, CoalesceEntity entity2, ELinkTypes type)
    {

        return verifyLinkage(entity1, entity2.getKey(), type)
                && verifyLinkage(entity2, entity1.getKey(), type.getReciprocalLinkType());

    }

    private boolean verifyLinkage(CoalesceEntity entity1, String entity2Key, ELinkTypes type)
    {

        boolean found = false;

        for (CoalesceLinkage linkage : entity1.getLinkageSection().getLinkages().values())
        {
            if (linkage.getEntity2Key().equalsIgnoreCase(entity2Key) && linkage.getLinkType() == type && linkage.isActive())
            {
                found = true;
                break;
            }
        }

        return found;

    }

}
