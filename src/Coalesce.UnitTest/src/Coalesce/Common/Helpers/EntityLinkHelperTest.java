package Coalesce.Common.Helpers;

import static org.junit.Assert.*;

import org.junit.Test;

import Coalesce.Common.Classification.Marking;
import Coalesce.Common.UnitTest.CoalesceTypeInstances;
import Coalesce.Framework.DataModel.ECoalesceDataObjectStatus;
import Coalesce.Framework.DataModel.ELinkTypes;
import Coalesce.Framework.DataModel.XsdDataObject;
import Coalesce.Framework.DataModel.XsdEntity;
import Coalesce.Framework.DataModel.XsdLinkage;

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

public class EntityLinkHelperTest {

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
    public void LinkEntitiesNullFirstDontUpdateAllTypesExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {
            XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            assertFalse(EntityLinkHelper.LinkEntities(null, linkType, entity, false));
        }
    }

    @Test
    public void LinkEntitiesNullSecondDontUpdateAllTypesExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {
            XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            assertFalse(EntityLinkHelper.LinkEntities(entity, linkType, null, false));
        }
    }

    @Test
    public void LinkEntitiesNullBothDontUpdateAllTypesExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {
            assertFalse(EntityLinkHelper.LinkEntities(null, linkType, null, false));
        }
    }

    @Test
    public void LinkEntitiesNullFirstUpdateAllTypesExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {
            XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            assertFalse(EntityLinkHelper.LinkEntities(null, linkType, entity, true));
        }
    }

    @Test
    public void LinkEntitiesNullSecondUpdateAllTypesExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {
            XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            assertFalse(EntityLinkHelper.LinkEntities(entity, linkType, null, true));
        }
    }

    @Test
    public void LinkEntitiesNullBothUpdateAllTypesExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {
            assertFalse(EntityLinkHelper.LinkEntities(null, linkType, null, true));
        }
    }

    @Test
    public void LinkEntitiesDontUpdateExistingAllLinkTypesNoExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {
            XsdEntity entity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            XsdEntity entity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

            assertTrue(EntityLinkHelper.LinkEntities(entity1, linkType, entity2, false));

            assertEquals(1, entity1.getLinkageSection().getChildDataObjects().size());
            assertEquals(1, entity2.getLinkageSection().getChildDataObjects().size());

            assertLinkages(linkType, entity1, entity2);

        }

    }

    @Test
    public void LinkEntitiesDontUpdateExistingAllLinkTypesExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {

            XsdEntity entity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            XsdEntity entity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

            EntityLinkHelper.LinkEntities(entity1, linkType, entity2, false);

            XsdEntity modifiedEntity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            XsdEntity modifiedEntity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

            modifiedEntity1.setName("Entity1name");
            modifiedEntity1.setSource("Entity1source");
            modifiedEntity1.setVersion("Entity1version");

            modifiedEntity2.setName("Entity2name");
            modifiedEntity2.setSource("Entity2source");
            modifiedEntity2.setVersion("Entity2version");

            assertTrue(EntityLinkHelper.LinkEntities(modifiedEntity1, linkType, modifiedEntity2, false));

            assertLinkages(linkType, entity1, entity2);

            XsdEntity mod2Entity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            XsdEntity mod2Entity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

            mod2Entity1.setName("Entity1name");
            mod2Entity1.setSource("Entity1source");
            mod2Entity1.setVersion("Entity1version");

            mod2Entity2.setName("Entity2name");
            mod2Entity2.setSource("Entity2source");
            mod2Entity2.setVersion("Entity2version");

            // Test updating link the other way
            assertTrue(EntityLinkHelper.LinkEntities(mod2Entity2, linkType.GetReciprocalLinkType(), mod2Entity1, false));

            assertLinkages(linkType, entity1, entity2);

        }
    }

    @Test
    public void LinkEntitiesUpdateExistingAllLinkTypesNoExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {
            XsdEntity entity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            XsdEntity entity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

            assertTrue(EntityLinkHelper.LinkEntities(entity1, linkType, entity2, true));

            assertEquals(1, entity1.getLinkageSection().getChildDataObjects().size());
            assertEquals(1, entity2.getLinkageSection().getChildDataObjects().size());

            assertLinkages(linkType, entity1, entity2);

        }
    }

    @Test
    public void LinkEntitiesUpdateExistingAllLinkTypesExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {
            XsdEntity entity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            XsdEntity entity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

            EntityLinkHelper.LinkEntities(entity1, linkType, entity2, true);

            XsdEntity modifiedEntity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            XsdEntity modifiedEntity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

            modifiedEntity1.setName("Entity1name");
            modifiedEntity1.setSource("Entity1source");
            modifiedEntity1.setVersion("Entity1version");

            modifiedEntity2.setName("Entity2name");
            modifiedEntity2.setSource("Entity2source");
            modifiedEntity2.setVersion("Entity2version");

            assertTrue(EntityLinkHelper.LinkEntities(modifiedEntity1, linkType, modifiedEntity2, true));

            assertLinkages(linkType, modifiedEntity1, modifiedEntity2);

            XsdEntity mod2Entity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            XsdEntity mod2Entity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

            mod2Entity1.setName("Entity1name");
            mod2Entity1.setSource("Entity1source");
            mod2Entity1.setVersion("Entity1version");

            mod2Entity2.setName("Entity2name");
            mod2Entity2.setSource("Entity2source");
            mod2Entity2.setVersion("Entity2version");

            // Test updating link the other way
            assertTrue(EntityLinkHelper.LinkEntities(mod2Entity2, linkType.GetReciprocalLinkType(), mod2Entity1, true));

            assertLinkages(linkType, mod2Entity1, mod2Entity2);
        }
    }

    @Test
    public void LinkEntitiesDetailedNullFirstDontUpdateExistingTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        assertFalse(EntityLinkHelper.LinkEntities(null, ELinkTypes.HasMember, entity, "(TS)", "jford", "en-gb", false));
    }

    @Test
    public void LinkEntitiesDetailedNullSecondDontUpdateExistingTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        assertFalse(EntityLinkHelper.LinkEntities(entity, ELinkTypes.HasOwnershipOf, null, "(TS)", "jford", "en-gb", false));
    }

    @Test
    public void LinkEntitiesDetailedNullBothDontUpdateExistingTest()
    {
        assertFalse(EntityLinkHelper.LinkEntities(null, ELinkTypes.HasParticipant, null, "(TS)", "jford", "en-gb", false));
    }

    @Test
    public void LinkEntitiesDetailedNullFirstUpdateExistingTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        assertFalse(EntityLinkHelper.LinkEntities(null, ELinkTypes.HasUseOf, entity, "(TS)", "jford", "en-gb", true));
    }

    @Test
    public void LinkEntitiesDetailedNullSecondUpdateExistingTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        assertFalse(EntityLinkHelper.LinkEntities(entity, ELinkTypes.IsAMemberOf, null, "(TS)", "jford", "en-gb", true));
    }

    @Test
    public void LinkEntitiesDetailedNullBothUpdateExistingTest()
    {
        assertFalse(EntityLinkHelper.LinkEntities(null, ELinkTypes.IsAParticipantOf, null, "(TS)", "jford", "en-gb", true));
    }

    @Test
    public void LinkEntitiesDetailedDontUpdateExistingANoExistingTest()
    {
        XsdEntity entity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        XsdEntity entity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

        assertTrue(EntityLinkHelper.LinkEntities(entity1, ELinkTypes.IsAPeerOf, entity2, "(TS)", "jford", "en-gb", false));

        assertEquals(1, entity1.getLinkageSection().getChildDataObjects().size());
        assertEquals(1, entity2.getLinkageSection().getChildDataObjects().size());

        assertLinkages(ELinkTypes.IsAPeerOf, "(TS)", "jford", "en-gb", entity1, entity2);

    }

    @Test
    public void LinkEntitiesDetailedDontUpdateExistingExistingTest()
    {
        XsdEntity entity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        XsdEntity entity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

        EntityLinkHelper.LinkEntities(entity1, ELinkTypes.IsBeingWatchedBy, entity2, "(TS)", "jford", "en-gb", false);

        XsdEntity modifiedEntity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        XsdEntity modifiedEntity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

        modifiedEntity1.setName("Entity1name");
        modifiedEntity1.setSource("Entity1source");
        modifiedEntity1.setVersion("Entity1version");

        modifiedEntity2.setName("Entity2name");
        modifiedEntity2.setSource("Entity2source");
        modifiedEntity2.setVersion("Entity2version");

        assertTrue(EntityLinkHelper.LinkEntities(modifiedEntity1,
                                                 ELinkTypes.IsBeingWatchedBy,
                                                 modifiedEntity2,
                                                 "(S USA)",
                                                 "bob",
                                                 "en-us",
                                                 false));

        assertLinkages(ELinkTypes.IsBeingWatchedBy, "(TS)", "jford", "en-gb", entity1, entity2);

    }

    @Test
    public void LinkEntitiesDetailedUpdateExistingNoExistingTest()
    {
        XsdEntity entity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        XsdEntity entity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

        assertTrue(EntityLinkHelper.LinkEntities(entity1, ELinkTypes.IsChildOf, entity2, "(TS)", "jford", "en-gb", true));

        assertEquals(1, entity1.getLinkageSection().getChildDataObjects().size());
        assertEquals(1, entity2.getLinkageSection().getChildDataObjects().size());

        assertLinkages(ELinkTypes.IsChildOf, "(TS)", "jford", "en-gb", entity1, entity2);

    }

    @Test
    public void LinkEntitiesDetailedUpdateExistingExistingTest()
    {
        XsdEntity entity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        XsdEntity entity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

        EntityLinkHelper.LinkEntities(entity1, ELinkTypes.IsOwnedBy, entity2, "(TS)", "jford", "en-gb", true);

        XsdEntity modifiedEntity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        XsdEntity modifiedEntity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

        modifiedEntity1.setName("Entity1name");
        modifiedEntity1.setSource("Entity1source");
        modifiedEntity1.setVersion("Entity1version");

        modifiedEntity2.setName("Entity2name");
        modifiedEntity2.setSource("Entity2source");
        modifiedEntity2.setVersion("Entity2version");

        assertTrue(EntityLinkHelper.LinkEntities(modifiedEntity1,
                                                 ELinkTypes.IsOwnedBy,
                                                 modifiedEntity2,
                                                 "(S USA)",
                                                 "bob",
                                                 "en-us",
                                                 true));

        assertLinkages(ELinkTypes.IsOwnedBy, "(S USA)", "bob", "en-us", modifiedEntity1, modifiedEntity2);

    }

    @Test
    public void UnLinkEntitiesNullFirstTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        assertFalse(EntityLinkHelper.UnLinkEntities(null, entity));
    }

    @Test
    public void UnLinkEntitiesNullSecondTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        assertFalse(EntityLinkHelper.UnLinkEntities(entity, null));
    }

    @Test
    public void UnLinkEntitiesNullBothTest()
    {
        assertFalse(EntityLinkHelper.UnLinkEntities(null, null));
    }

    @Test
    public void UnLinkEntitiesLinkTypeNullFirstTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        assertFalse(EntityLinkHelper.UnLinkEntities(null, entity, ELinkTypes.IsParentOf));
    }

    @Test
    public void UnLinkEntitiesLinkTypeNullSecondTest()
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        assertFalse(EntityLinkHelper.UnLinkEntities(entity, null, ELinkTypes.IsUsedBy));
    }

    @Test
    public void UnLinkEntitiesLinkTypeNullBothTest()
    {
        assertFalse(EntityLinkHelper.UnLinkEntities(null, null, ELinkTypes.IsWatching));
    }

    @Test
    public void UnLinkEntitiesLinkTypeNullLinkTypeTest()
    {
        XsdEntity entity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        XsdEntity entity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

        EntityLinkHelper.LinkEntities(entity1, ELinkTypes.Created, entity2, true);

        assertTrue(EntityLinkHelper.UnLinkEntities(entity1, entity2, null));
        assertEquals(ECoalesceDataObjectStatus.DELETED,
                     entity1.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());
        assertEquals(ECoalesceDataObjectStatus.DELETED,
                     entity2.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());

    }

    @Test
    public void UnLinkEntitiesAllLinkTypesTest()
    {

        for (ELinkTypes linkType : ELinkTypes.values())
        {

            XsdEntity entity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            XsdEntity entity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

            EntityLinkHelper.LinkEntities(entity1, linkType, entity2, true);

            assertTrue(EntityLinkHelper.UnLinkEntities(entity1, entity2));
            assertEquals(ECoalesceDataObjectStatus.DELETED,
                         entity1.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());
            assertEquals(ECoalesceDataObjectStatus.DELETED,
                         entity2.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());

        }
    }

    @Test
    public void UnLinkEntitiesNotLinkedTest()
    {

        XsdEntity entity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        XsdEntity entity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

        assertTrue(EntityLinkHelper.UnLinkEntities(entity1, entity2));
        assertTrue(entity1.getLinkageSection().getChildDataObjects().isEmpty());
        assertTrue(entity2.getLinkageSection().getChildDataObjects().isEmpty());

    }

    @Test
    public void UnLinkEntitiesNotLinkedFirstTest()
    {

        XsdEntity entity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        XsdEntity entity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);
        XsdEntity entity3 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        EntityLinkHelper.LinkEntities(entity1, ELinkTypes.Created, entity2, true);

        assertTrue(EntityLinkHelper.UnLinkEntities(entity1, entity3));
        assertEquals(ECoalesceDataObjectStatus.ACTIVE,
                     entity1.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());
        assertEquals(ECoalesceDataObjectStatus.ACTIVE,
                     entity2.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());

        for (XsdDataObject xdo : entity3.getChildDataObjects().values())
        {
            assertEquals(ECoalesceDataObjectStatus.ACTIVE, xdo.getStatus());
        }

    }

    @Test
    public void UnLinkEntitiesNotLinkedSecondTest()
    {

        XsdEntity entity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        XsdEntity entity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);
        XsdEntity entity3 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        EntityLinkHelper.LinkEntities(entity1, ELinkTypes.Created, entity2, true);

        assertTrue(EntityLinkHelper.UnLinkEntities(entity3, entity2));
        assertEquals(ECoalesceDataObjectStatus.ACTIVE,
                     entity1.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());
        assertEquals(ECoalesceDataObjectStatus.ACTIVE,
                     entity2.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());

        for (XsdDataObject xdo : entity3.getChildDataObjects().values())
        {
            assertEquals(ECoalesceDataObjectStatus.ACTIVE, xdo.getStatus());
        }

    }

    @Test
    public void UnLinkEntitiesLinkTypesTest()
    {
        XsdEntity entity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        XsdEntity entity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

        EntityLinkHelper.LinkEntities(entity1, ELinkTypes.Created, entity2, true);

        assertTrue(EntityLinkHelper.UnLinkEntities(entity1, entity2, ELinkTypes.Created));
        assertEquals(ECoalesceDataObjectStatus.DELETED,
                     entity1.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());
        assertEquals(ECoalesceDataObjectStatus.DELETED,
                     entity2.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());

    }

    @Test
    public void UnLinkEntitiesLinkTypesMismatchTest()
    {
        XsdEntity entity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        XsdEntity entity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

        EntityLinkHelper.LinkEntities(entity1, ELinkTypes.HasParticipant, entity2, true);

        assertTrue(EntityLinkHelper.UnLinkEntities(entity1, entity2, ELinkTypes.HasUseOf));
        assertEquals(ECoalesceDataObjectStatus.ACTIVE,
                     entity1.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());
        assertEquals(ECoalesceDataObjectStatus.ACTIVE,
                     entity2.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());

    }

    @Test
    public void UnLinkEntitiesLinkageTypeNotLinkedTest()
    {

        XsdEntity entity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        XsdEntity entity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

        assertTrue(EntityLinkHelper.UnLinkEntities(entity1, entity2, ELinkTypes.Created));
        assertTrue(entity1.getLinkageSection().getChildDataObjects().isEmpty());
        assertTrue(entity2.getLinkageSection().getChildDataObjects().isEmpty());

    }

    @Test
    public void UnLinkEntitiesLinkageTypeNotLinkedFirstTest()
    {

        XsdEntity entity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        XsdEntity entity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);
        XsdEntity entity3 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        EntityLinkHelper.LinkEntities(entity1, ELinkTypes.HasMember, entity2, true);

        assertTrue(EntityLinkHelper.UnLinkEntities(entity1, entity3, ELinkTypes.HasMember));
        assertEquals(ECoalesceDataObjectStatus.ACTIVE,
                     entity1.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());
        assertEquals(ECoalesceDataObjectStatus.ACTIVE,
                     entity2.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());

        for (XsdDataObject xdo : entity3.getChildDataObjects().values())
        {
            assertEquals(ECoalesceDataObjectStatus.ACTIVE, xdo.getStatus());
        }

    }

    @Test
    public void UnLinkEntitiesLinkagetypeNotLinkedSecondTest()
    {

        XsdEntity entity1 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        XsdEntity entity2 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);
        XsdEntity entity3 = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        EntityLinkHelper.LinkEntities(entity1, ELinkTypes.HasOwnershipOf, entity2, true);

        assertTrue(EntityLinkHelper.UnLinkEntities(entity3, entity2, ELinkTypes.HasOwnershipOf));
        assertEquals(ECoalesceDataObjectStatus.ACTIVE,
                     entity1.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());
        assertEquals(ECoalesceDataObjectStatus.ACTIVE,
                     entity2.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());

        for (XsdDataObject xdo : entity3.getChildDataObjects().values())
        {
            assertEquals(ECoalesceDataObjectStatus.ACTIVE, xdo.getStatus());
        }

    }

    // -----------------------------------------------------------------------//
    // Private Methods
    // -----------------------------------------------------------------------//

    private void assertLinkages(ELinkTypes linkType,
                                String classificationMarking,
                                String modifiedBy,
                                String inputLang,
                                XsdEntity entity1,
                                XsdEntity entity2)
    {

        assertLinkage(linkType,
                      classificationMarking,
                      modifiedBy,
                      inputLang,
                      entity1,
                      entity2,
                      (XsdLinkage) entity1.getLinkageSection().getChildDataObjects().values().iterator().next());

        assertLinkage(linkType.GetReciprocalLinkType(),
                      classificationMarking,
                      modifiedBy,
                      inputLang,
                      entity2,
                      entity1,
                      (XsdLinkage) entity2.getLinkageSection().getChildDataObjects().values().iterator().next());

    }

    private void assertLinkages(ELinkTypes linkType, XsdEntity entity1, XsdEntity entity2)
    {

        assertLinkage(linkType,
                      entity1,
                      entity2,
                      (XsdLinkage) entity1.getLinkageSection().getChildDataObjects().values().iterator().next());

        assertLinkage(linkType.GetReciprocalLinkType(),
                      entity2,
                      entity1,
                      (XsdLinkage) entity2.getLinkageSection().getChildDataObjects().values().iterator().next());

    }

    private void assertLinkage(ELinkTypes linkType, XsdEntity entity1, XsdEntity entity2, XsdLinkage linkage)
    {

        assertLinkage(linkType, "(U)", "", "en-US", entity1, entity2, linkage);
    }

    private void assertLinkage(ELinkTypes linkType,
                               String classificationMarking,
                               String modifiedBy,
                               String inputLang,
                               XsdEntity entity1,
                               XsdEntity entity2,
                               XsdLinkage linkage)
    {

        assertEquals(linkType, linkage.GetLinkType());
        assertEquals(new Marking(classificationMarking).toString(),
                     new Marking(linkage.GetClassificationMarking()).toString());
        assertEquals(modifiedBy, linkage.GetModifiedBy());
        assertEquals(inputLang, linkage.GetInputLang());

        assertEquals(entity1.getKey(), linkage.GetEntity1Key());
        assertEquals(entity1.getName(), linkage.GetEntity1Name());
        assertEquals(entity1.getSource(), linkage.GetEntity1Source());
        assertEquals(entity1.getVersion(), linkage.GetEntity1Version());

        assertEquals(entity2.getKey(), linkage.GetEntity2Key());
        assertEquals(entity2.getName(), linkage.GetEntity2Name());
        assertEquals(entity2.getSource(), linkage.GetEntity2Source());
        assertEquals(entity2.getVersion(), linkage.GetEntity2Version());
    }

}
