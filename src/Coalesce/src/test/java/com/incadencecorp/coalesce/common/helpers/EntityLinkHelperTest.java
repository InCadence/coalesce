package com.incadencecorp.coalesce.common.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Test;

import com.incadencecorp.coalesce.common.CoalesceTypeInstances;
import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceDataObjectStatus;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;

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

    private static final Marking UNCLASSIFIED_MARKING = new Marking("(U)");
    private static final Marking SECRET_USA_MARKING = new Marking("(//S USA)");
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

    @Test
    public void linkEntitiesNullFirstDontUpdateAllTypesExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {
            try
            {
                CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
                assertFalse(EntityLinkHelper.linkEntities(null, linkType, entity, false));
            }
            catch (IllegalArgumentException ex)
            {
                // Passed
            }
            catch (Exception ex)
            {
                fail(ex.getMessage());
            }
        }
    }

    @Test
    public void linkEntitiesNullSecondDontUpdateAllTypesExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {
            try
            {
                CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
                assertFalse(EntityLinkHelper.linkEntities(entity, linkType, null, false));
            }
            catch (IllegalArgumentException ex)
            {
                // Passed
            }
            catch (Exception ex)
            {
                fail(ex.getMessage());
            }
        }
    }

    @Test
    public void linkEntitiesNullBothDontUpdateAllTypesExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {
            try
            {
                assertFalse(EntityLinkHelper.linkEntities(null, linkType, null, false));
            }
            catch (IllegalArgumentException ex)
            {
                // Passed
            }
            catch (Exception ex)
            {
                fail(ex.getMessage());
            }
        }
    }

    @Test
    public void linkEntitiesNullFirstUpdateAllTypesExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {
            try
            {
                CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
                assertFalse(EntityLinkHelper.linkEntities(null, linkType, entity, true));
            }
            catch (IllegalArgumentException ex)
            {
                // Passed
            }
            catch (Exception ex)
            {
                fail(ex.getMessage());
            }
        }
    }

    @Test
    public void linkEntitiesNullSecondUpdateAllTypesExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {
            try
            {
                CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
                assertFalse(EntityLinkHelper.linkEntities(entity, linkType, null, true));
            }
            catch (IllegalArgumentException ex)
            {
                // Passed
            }
            catch (Exception ex)
            {
                fail(ex.getMessage());
            }
        }
    }

    @Test
    public void linkEntitiesNullBothUpdateAllTypesExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {
            try
            {
                assertFalse(EntityLinkHelper.linkEntities(null, linkType, null, true));
            }
            catch (IllegalArgumentException ex)
            {
                // Passed
            }
            catch (Exception ex)
            {
                fail(ex.getMessage());
            }
        }
    }

    @Test
    public void linkEntitiestUpdateExistingTest()
    {
        CoalesceEntity entity1 = CoalesceEntity.create("LinkTest", "UnitTest", "1.0", "Unit", "Test", "Entity1");
        entity1.createSection("TestSection");
        CoalesceEntity entity2 = CoalesceEntity.create("LinkTest", "UnitTest", "1.0", "Unit", "Test", "Entity2");
        entity2.createSection("TestSection");

        EntityLinkHelper.linkEntities(entity1,
                                      ELinkTypes.IS_PARENT_OF,
                                      entity2,
                                      EntityLinkHelperTest.UNCLASSIFIED_MARKING,
                                      "tester1",
                                      Locale.US,
                                      false);

        assertLinkages(ELinkTypes.IS_PARENT_OF,
                       entity1,
                       entity2,
                       EntityLinkHelperTest.UNCLASSIFIED_MARKING,
                       "tester1",
                       Locale.US);

        EntityLinkHelper.linkEntities(entity1,
                                      ELinkTypes.IS_PARENT_OF,
                                      entity2,
                                      EntityLinkHelperTest.TOP_SECRET_MARKING,
                                      "tester2",
                                      Locale.CANADA,
                                      false);

        assertLinkages(ELinkTypes.IS_PARENT_OF,
                       entity1,
                       entity2,
                       EntityLinkHelperTest.UNCLASSIFIED_MARKING,
                       "tester1",
                       Locale.US);

        EntityLinkHelper.linkEntities(entity1,
                                      ELinkTypes.IS_PARENT_OF,
                                      entity2,
                                      EntityLinkHelperTest.TOP_SECRET_MARKING,
                                      "tester2",
                                      Locale.CANADA,
                                      true);

        assertLinkages(ELinkTypes.IS_PARENT_OF,
                       entity1,
                       entity2,
                       EntityLinkHelperTest.TOP_SECRET_MARKING,
                       "tester2",
                       Locale.CANADA);

    }

    @Test
    public void linkEntitiesDontUpdateExistingAllLinkTypesNoExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {
            CoalesceEntity entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            CoalesceEntity entity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

            assertTrue(EntityLinkHelper.linkEntities(entity1, linkType, entity2, false));

            assertEquals(1, entity1.getLinkageSection().getChildDataObjects().size());
            assertEquals(1, entity2.getLinkageSection().getChildDataObjects().size());

            assertLinkages(linkType, entity1, entity2);

        }

    }

    @Test
    public void linkEntitiesDontUpdateExistingAllLinkTypesExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {

            CoalesceEntity entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            CoalesceEntity entity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

            EntityLinkHelper.linkEntities(entity1, linkType, entity2, false);

            CoalesceEntity modifiedEntity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            CoalesceEntity modifiedEntity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

            modifiedEntity1.setName("Entity1name");
            modifiedEntity1.setSource("Entity1source");
            modifiedEntity1.setVersion("Entity1version");

            modifiedEntity2.setName("Entity2name");
            modifiedEntity2.setSource("Entity2source");
            modifiedEntity2.setVersion("Entity2version");

            assertTrue(EntityLinkHelper.linkEntities(modifiedEntity1, linkType, modifiedEntity2, false));

            assertLinkages(linkType, entity1, entity2);

            CoalesceEntity mod2Entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            CoalesceEntity mod2Entity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

            mod2Entity1.setName("Entity1name");
            mod2Entity1.setSource("Entity1source");
            mod2Entity1.setVersion("Entity1version");

            mod2Entity2.setName("Entity2name");
            mod2Entity2.setSource("Entity2source");
            mod2Entity2.setVersion("Entity2version");

            // Test updating link the other way
            assertTrue(EntityLinkHelper.linkEntities(mod2Entity2, linkType.getReciprocalLinkType(), mod2Entity1, false));

            assertLinkages(linkType, entity1, entity2);

        }
    }

    @Test
    public void linkEntitiesUpdateExistingAllLinkTypesNoExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {
            CoalesceEntity entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            CoalesceEntity entity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

            assertTrue(EntityLinkHelper.linkEntities(entity1, linkType, entity2, true));

            assertEquals(1, entity1.getLinkageSection().getChildDataObjects().size());
            assertEquals(1, entity2.getLinkageSection().getChildDataObjects().size());

            assertLinkages(linkType, entity1, entity2);

        }
    }

    @Test
    public void linkEntitiesUpdateExistingAllLinkTypesExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {
            CoalesceEntity entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            CoalesceEntity entity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

            EntityLinkHelper.linkEntities(entity1, linkType, entity2, true);

            CoalesceEntity modifiedEntity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            CoalesceEntity modifiedEntity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

            modifiedEntity1.setName("Entity1name");
            modifiedEntity1.setSource("Entity1source");
            modifiedEntity1.setVersion("Entity1version");

            modifiedEntity2.setName("Entity2name");
            modifiedEntity2.setSource("Entity2source");
            modifiedEntity2.setVersion("Entity2version");

            assertTrue(EntityLinkHelper.linkEntities(modifiedEntity1, linkType, modifiedEntity2, true));

            assertLinkages(linkType, modifiedEntity1, modifiedEntity2);

            CoalesceEntity mod2Entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            CoalesceEntity mod2Entity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

            mod2Entity1.setName("Entity1name");
            mod2Entity1.setSource("Entity1source");
            mod2Entity1.setVersion("Entity1version");

            mod2Entity2.setName("Entity2name");
            mod2Entity2.setSource("Entity2source");
            mod2Entity2.setVersion("Entity2version");

            // Test updating link the other way
            assertTrue(EntityLinkHelper.linkEntities(mod2Entity2, linkType.getReciprocalLinkType(), mod2Entity1, true));

            assertLinkages(linkType, mod2Entity1, mod2Entity2);
        }
    }

    @Test
    public void linkEntitiesDetailedNullFirstDontUpdateExistingTest()
    {
        try
        {
            CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            assertFalse(EntityLinkHelper.linkEntities(null,
                                                      ELinkTypes.HAS_MEMBER,
                                                      entity,
                                                      EntityLinkHelperTest.TOP_SECRET_MARKING,
                                                      "jford",
                                                      Locale.UK,
                                                      false));
        }
        catch (IllegalArgumentException ex)
        {
            // Passed
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void linkEntitiesDetailedNullSecondDontUpdateExistingTest()
    {
        try
        {
            CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            assertFalse(EntityLinkHelper.linkEntities(entity,
                                                      ELinkTypes.HAS_OWNERSHIP_OF,
                                                      null,
                                                      EntityLinkHelperTest.TOP_SECRET_MARKING,
                                                      "jford",
                                                      Locale.UK,
                                                      false));
        }
        catch (IllegalArgumentException ex)
        {
            // Passed
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void linkEntitiesDetailedNullBothDontUpdateExistingTest()
    {
        try
        {
            assertFalse(EntityLinkHelper.linkEntities(null,
                                                      ELinkTypes.HAS_PARTICIPANT,
                                                      null,
                                                      EntityLinkHelperTest.TOP_SECRET_MARKING,
                                                      "jford",
                                                      Locale.UK,
                                                      false));
        }
        catch (IllegalArgumentException ex)
        {
            // Passed
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void linkEntitiesDetailedNullFirstUpdateExistingTest()
    {
        try
        {
            CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            assertFalse(EntityLinkHelper.linkEntities(null,
                                                      ELinkTypes.HAS_USE_OF,
                                                      entity,
                                                      EntityLinkHelperTest.TOP_SECRET_MARKING,
                                                      "jford",
                                                      Locale.UK,
                                                      true));
        }
        catch (IllegalArgumentException ex)
        {
            // Passed
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void linkEntitiesDetailedNullSecondUpdateExistingTest()
    {
        try
        {
            CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            assertFalse(EntityLinkHelper.linkEntities(entity,
                                                      ELinkTypes.IS_A_MEMBER_OF,
                                                      null,
                                                      EntityLinkHelperTest.TOP_SECRET_MARKING,
                                                      "jford",
                                                      Locale.UK,
                                                      true));
        }
        catch (IllegalArgumentException ex)
        {
            // Passed
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void linkEntitiesDetailedNullBothUpdateExistingTest()
    {
        try
        {
            assertFalse(EntityLinkHelper.linkEntities(null,
                                                      ELinkTypes.IS_A_PARTICIPANT_OF,
                                                      null,
                                                      EntityLinkHelperTest.TOP_SECRET_MARKING,
                                                      "jford",
                                                      Locale.UK,
                                                      true));
        }
        catch (IllegalArgumentException ex)
        {
            // Passed
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void linkEntitiesDetailedDontUpdateExistingANoExistingTest()
    {
        CoalesceEntity entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        CoalesceEntity entity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

        assertTrue(EntityLinkHelper.linkEntities(entity1,
                                                 ELinkTypes.IS_A_PEER_OF,
                                                 entity2,
                                                 EntityLinkHelperTest.TOP_SECRET_MARKING,
                                                 "jford",
                                                 Locale.UK,
                                                 false));

        assertEquals(1, entity1.getLinkageSection().getChildDataObjects().size());
        assertEquals(1, entity2.getLinkageSection().getChildDataObjects().size());

        assertLinkages(ELinkTypes.IS_A_PEER_OF,
                       EntityLinkHelperTest.TOP_SECRET_MARKING,
                       "jford",
                       Locale.UK,
                       entity1,
                       entity2);

    }
    
    @Test
    public void linkEntitiesToSelfTest()
    {
        CoalesceEntity entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);

        assertFalse(EntityLinkHelper.linkEntities(entity1,
                                                 ELinkTypes.IS_A_PEER_OF,
                                                 entity1,
                                                 EntityLinkHelperTest.TOP_SECRET_MARKING,
                                                 "jford",
                                                 Locale.UK,
                                                 false));
    }

    @Test
    public void linkEntitiesDetailedDontUpdateExistingExistingTest()
    {
        CoalesceEntity entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        CoalesceEntity entity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

        EntityLinkHelper.linkEntities(entity1,
                                      ELinkTypes.IS_BEING_WATCHED_BY,
                                      entity2,
                                      EntityLinkHelperTest.TOP_SECRET_MARKING,
                                      "jford",
                                      Locale.UK,
                                      false);

        CoalesceEntity modifiedEntity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        CoalesceEntity modifiedEntity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

        modifiedEntity1.setName("Entity1name");
        modifiedEntity1.setSource("Entity1source");
        modifiedEntity1.setVersion("Entity1version");

        modifiedEntity2.setName("Entity2name");
        modifiedEntity2.setSource("Entity2source");
        modifiedEntity2.setVersion("Entity2version");

        assertTrue(EntityLinkHelper.linkEntities(modifiedEntity1,
                                                 ELinkTypes.IS_BEING_WATCHED_BY,
                                                 modifiedEntity2,
                                                 EntityLinkHelperTest.SECRET_USA_MARKING,
                                                 "bob",
                                                 Locale.US,
                                                 false));

        assertLinkages(ELinkTypes.IS_BEING_WATCHED_BY,
                       EntityLinkHelperTest.TOP_SECRET_MARKING,
                       "jford",
                       Locale.UK,
                       entity1,
                       entity2);

    }

    @Test
    public void linkEntitiesDetailedUpdateExistingNoExistingTest()
    {
        CoalesceEntity entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        CoalesceEntity entity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

        assertTrue(EntityLinkHelper.linkEntities(entity1,
                                                 ELinkTypes.IS_CHILD_OF,
                                                 entity2,
                                                 EntityLinkHelperTest.TOP_SECRET_MARKING,
                                                 "jford",
                                                 Locale.UK,
                                                 true));

        assertEquals(1, entity1.getLinkageSection().getChildDataObjects().size());
        assertEquals(1, entity2.getLinkageSection().getChildDataObjects().size());

        assertLinkages(ELinkTypes.IS_CHILD_OF, EntityLinkHelperTest.TOP_SECRET_MARKING, "jford", Locale.UK, entity1, entity2);

    }

    @Test
    public void linkEntitiesDetailedUpdateExistingExistingTest()
    {
        CoalesceEntity entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        CoalesceEntity entity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

        EntityLinkHelper.linkEntities(entity1,
                                      ELinkTypes.IS_OWNED_BY,
                                      entity2,
                                      EntityLinkHelperTest.TOP_SECRET_MARKING,
                                      "jford",
                                      Locale.UK,
                                      true);

        CoalesceEntity modifiedEntity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        CoalesceEntity modifiedEntity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

        modifiedEntity1.setName("Entity1name");
        modifiedEntity1.setSource("Entity1source");
        modifiedEntity1.setVersion("Entity1version");

        modifiedEntity2.setName("Entity2name");
        modifiedEntity2.setSource("Entity2source");
        modifiedEntity2.setVersion("Entity2version");

        assertTrue(EntityLinkHelper.linkEntities(modifiedEntity1,
                                                 ELinkTypes.IS_OWNED_BY,
                                                 modifiedEntity2,
                                                 EntityLinkHelperTest.SECRET_USA_MARKING,
                                                 "bob",
                                                 Locale.US,
                                                 true));

        assertLinkages(ELinkTypes.IS_OWNED_BY,
                       EntityLinkHelperTest.SECRET_USA_MARKING,
                       "bob",
                       Locale.US,
                       modifiedEntity1,
                       modifiedEntity2);

    }

    @Test
    public void unlinkEntitiesNullFirstTest()
    {
        try
        {
            CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            assertFalse(EntityLinkHelper.unLinkEntities(null, entity));
        }
        catch (IllegalArgumentException ex)
        {
            // Passed
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void unlinkEntitiesNullSecondTest()
    {
        try
        {
            CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            assertFalse(EntityLinkHelper.unLinkEntities(entity, null));
        }
        catch (IllegalArgumentException ex)
        {
            // Passed
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void unlinkEntitiesNullBothTest()
    {
        try
        {
            assertFalse(EntityLinkHelper.unLinkEntities(null, null));
        }
        catch (IllegalArgumentException ex)
        {
            // Passed
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void unlinkEntitiesLinkTypeNullFirstTest()
    {
        try
        {
            CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            assertFalse(EntityLinkHelper.unLinkEntities(null, entity, ELinkTypes.IS_PARENT_OF));
        }
        catch (IllegalArgumentException ex)
        {
            // Passed
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void unlinkEntitiesLinkTypeNullSecondTest()
    {
        try
        {
            CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            assertFalse(EntityLinkHelper.unLinkEntities(entity, null, ELinkTypes.IS_USED_BY));
        }
        catch (IllegalArgumentException ex)
        {
            // Passed
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void unlinkEntitiesLinkTypeNullBothTest()
    {
        try
        {
            assertFalse(EntityLinkHelper.unLinkEntities(null, null, ELinkTypes.IS_WATCHING));
        }
        catch (IllegalArgumentException ex)
        {
            // Passed
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void unlinkEntitiesLinkTypeNullLinkTypeTest()
    {
        CoalesceEntity entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        CoalesceEntity entity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

        EntityLinkHelper.linkEntities(entity1, ELinkTypes.CREATED, entity2, true);

        assertTrue(EntityLinkHelper.unLinkEntities(entity1, entity2, null));
        assertEquals(ECoalesceDataObjectStatus.DELETED,
                     entity1.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());
        assertEquals(ECoalesceDataObjectStatus.DELETED,
                     entity2.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());

    }

    @Test
    public void unlinkEntitiesAllLinkTypesTest()
    {

        for (ELinkTypes linkType : ELinkTypes.values())
        {

            CoalesceEntity entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
            CoalesceEntity entity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

            EntityLinkHelper.linkEntities(entity1, linkType, entity2, true);

            assertTrue(EntityLinkHelper.unLinkEntities(entity1, entity2));
            assertEquals(ECoalesceDataObjectStatus.DELETED,
                         entity1.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());
            assertEquals(ECoalesceDataObjectStatus.DELETED,
                         entity2.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());

        }
    }

    @Test
    public void unlinkEntitiesNotLinkedTest()
    {

        CoalesceEntity entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        CoalesceEntity entity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

        assertTrue(EntityLinkHelper.unLinkEntities(entity1, entity2));
        assertTrue(entity1.getLinkageSection().getChildDataObjects().isEmpty());
        assertTrue(entity2.getLinkageSection().getChildDataObjects().isEmpty());

    }

    @Test
    public void unlinkEntitiesNotLinkedFirstTest()
    {

        CoalesceEntity entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        CoalesceEntity entity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);
        CoalesceEntity entity3 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        EntityLinkHelper.linkEntities(entity1, ELinkTypes.CREATED, entity2, true);

        assertTrue(EntityLinkHelper.unLinkEntities(entity1, entity3));
        assertEquals(ECoalesceDataObjectStatus.ACTIVE,
                     entity1.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());
        assertEquals(ECoalesceDataObjectStatus.ACTIVE,
                     entity2.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());

        for (CoalesceDataObject xdo : entity3.getChildDataObjects().values())
        {
            assertEquals(ECoalesceDataObjectStatus.ACTIVE, xdo.getStatus());
        }

    }

    @Test
    public void unlinkEntitiesNotLinkedSecondTest()
    {

        CoalesceEntity entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        CoalesceEntity entity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);
        CoalesceEntity entity3 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        EntityLinkHelper.linkEntities(entity1, ELinkTypes.CREATED, entity2, true);

        assertTrue(EntityLinkHelper.unLinkEntities(entity3, entity2));
        assertEquals(ECoalesceDataObjectStatus.ACTIVE,
                     entity1.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());
        assertEquals(ECoalesceDataObjectStatus.ACTIVE,
                     entity2.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());

        for (CoalesceDataObject xdo : entity3.getChildDataObjects().values())
        {
            assertEquals(ECoalesceDataObjectStatus.ACTIVE, xdo.getStatus());
        }

    }

    @Test
    public void unlinkEntitiesLinkTypesTest()
    {
        CoalesceEntity entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        CoalesceEntity entity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

        EntityLinkHelper.linkEntities(entity1, ELinkTypes.CREATED, entity2, true);

        assertTrue(EntityLinkHelper.unLinkEntities(entity1, entity2, ELinkTypes.CREATED));
        assertEquals(ECoalesceDataObjectStatus.DELETED,
                     entity1.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());
        assertEquals(ECoalesceDataObjectStatus.DELETED,
                     entity2.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());

    }

    @Test
    public void unlinkEntitiesLinkTypesMismatchTest()
    {
        CoalesceEntity entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        CoalesceEntity entity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

        EntityLinkHelper.linkEntities(entity1, ELinkTypes.HAS_PARTICIPANT, entity2, true);

        assertTrue(EntityLinkHelper.unLinkEntities(entity1, entity2, ELinkTypes.HAS_USE_OF));
        assertEquals(ECoalesceDataObjectStatus.ACTIVE,
                     entity1.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());
        assertEquals(ECoalesceDataObjectStatus.ACTIVE,
                     entity2.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());

    }

    @Test
    public void unlinkEntitiesLinkageTypeNotLinkedTest()
    {

        CoalesceEntity entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        CoalesceEntity entity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);

        assertTrue(EntityLinkHelper.unLinkEntities(entity1, entity2, ELinkTypes.CREATED));
        assertTrue(entity1.getLinkageSection().getChildDataObjects().isEmpty());
        assertTrue(entity2.getLinkageSection().getChildDataObjects().isEmpty());

    }

    @Test
    public void unlinkEntitiesLinkageTypeNotLinkedFirstTest()
    {

        CoalesceEntity entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        CoalesceEntity entity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);
        CoalesceEntity entity3 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        EntityLinkHelper.linkEntities(entity1, ELinkTypes.HAS_MEMBER, entity2, true);

        assertTrue(EntityLinkHelper.unLinkEntities(entity1, entity3, ELinkTypes.HAS_MEMBER));
        assertEquals(ECoalesceDataObjectStatus.ACTIVE,
                     entity1.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());
        assertEquals(ECoalesceDataObjectStatus.ACTIVE,
                     entity2.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());

        for (CoalesceDataObject xdo : entity3.getChildDataObjects().values())
        {
            assertEquals(ECoalesceDataObjectStatus.ACTIVE, xdo.getStatus());
        }

    }

    @Test
    public void unlinkEntitiesLinkagetypeNotLinkedSecondTest()
    {

        CoalesceEntity entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_ONE);
        CoalesceEntity entity2 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION_NO_LINKS_TWO);
        CoalesceEntity entity3 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        EntityLinkHelper.linkEntities(entity1, ELinkTypes.HAS_OWNERSHIP_OF, entity2, true);

        assertTrue(EntityLinkHelper.unLinkEntities(entity3, entity2, ELinkTypes.HAS_OWNERSHIP_OF));
        assertEquals(ECoalesceDataObjectStatus.ACTIVE,
                     entity1.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());
        assertEquals(ECoalesceDataObjectStatus.ACTIVE,
                     entity2.getLinkageSection().getChildDataObjects().values().iterator().next().getStatus());

        for (CoalesceDataObject xdo : entity3.getChildDataObjects().values())
        {
            assertEquals(ECoalesceDataObjectStatus.ACTIVE, xdo.getStatus());
        }

    }

    // -----------------------------------------------------------------------//
    // Private Methods
    // -----------------------------------------------------------------------//

    private void assertLinkages(ELinkTypes linkType,
                                Marking classificationMarking,
                                String modifiedBy,
                                Locale inputLang,
                                CoalesceEntity entity1,
                                CoalesceEntity entity2)
    {

        assertLinkage(linkType,
                      classificationMarking,
                      modifiedBy,
                      inputLang,
                      entity1,
                      entity2,
                      (CoalesceLinkage) entity1.getLinkageSection().getChildDataObjects().values().iterator().next());

        assertLinkage(linkType.getReciprocalLinkType(),
                      classificationMarking,
                      modifiedBy,
                      inputLang,
                      entity2,
                      entity1,
                      (CoalesceLinkage) entity2.getLinkageSection().getChildDataObjects().values().iterator().next());

    }

    private void assertLinkages(ELinkTypes linkType,
                                CoalesceEntity entity1,
                                CoalesceEntity entity2,
                                Marking classificationMarking,
                                String modifiedBy,
                                Locale inputLang)
    {
        CoalesceLinkage entity1Linakge = (CoalesceLinkage) entity1.getLinkageSection().getChildDataObjects().values().iterator().next();
        assertEquals(classificationMarking, entity1Linakge.getClassificationMarking());
        assertEquals(modifiedBy, entity1Linakge.getModifiedBy());
        assertEquals(inputLang, entity1Linakge.getInputLang());

        CoalesceLinkage entity2Linakge = (CoalesceLinkage) entity2.getLinkageSection().getChildDataObjects().values().iterator().next();
        assertEquals(classificationMarking, entity2Linakge.getClassificationMarking());
        assertEquals(modifiedBy, entity2Linakge.getModifiedBy());
        assertEquals(inputLang, entity2Linakge.getInputLang());

        assertLinkage(linkType,
                      classificationMarking,
                      modifiedBy,
                      inputLang,
                      entity1,
                      entity2,
                      (CoalesceLinkage) entity1.getLinkageSection().getChildDataObjects().values().iterator().next());

        assertLinkage(linkType.getReciprocalLinkType(),
                      classificationMarking,
                      modifiedBy,
                      inputLang,
                      entity2,
                      entity1,
                      (CoalesceLinkage) entity2.getLinkageSection().getChildDataObjects().values().iterator().next());
        
    }

    private void assertLinkages(ELinkTypes linkType, CoalesceEntity entity1, CoalesceEntity entity2)
    {

        assertLinkage(linkType,
                      entity1,
                      entity2,
                      (CoalesceLinkage) entity1.getLinkageSection().getChildDataObjects().values().iterator().next());

        assertLinkage(linkType.getReciprocalLinkType(),
                      entity2,
                      entity1,
                      (CoalesceLinkage) entity2.getLinkageSection().getChildDataObjects().values().iterator().next());

    }

    private void assertLinkage(ELinkTypes linkType, CoalesceEntity entity1, CoalesceEntity entity2, CoalesceLinkage linkage)
    {

        assertLinkage(linkType, EntityLinkHelperTest.UNCLASSIFIED_MARKING, "", Locale.US, entity1, entity2, linkage);
    }

    private void assertLinkage(ELinkTypes linkType,
                               Marking classificationMarking,
                               String modifiedBy,
                               Locale inputLang,
                               CoalesceEntity entity1,
                               CoalesceEntity entity2,
                               CoalesceLinkage linkage)
    {

        assertEquals(linkType, linkage.getLinkType());
        assertEquals(classificationMarking, linkage.getClassificationMarking());
        assertEquals(modifiedBy, linkage.getModifiedBy());
        assertEquals(inputLang, linkage.getInputLang());

        assertEquals(entity1.getKey(), linkage.getEntity1Key());
        assertEquals(entity1.getName(), linkage.getEntity1Name());
        assertEquals(entity1.getSource(), linkage.getEntity1Source());
        assertEquals(entity1.getVersion(), linkage.getEntity1Version());

        assertEquals(entity2.getKey(), linkage.getEntity2Key());
        assertEquals(entity2.getName(), linkage.getEntity2Name());
        assertEquals(entity2.getSource(), linkage.getEntity2Source());
        assertEquals(entity2.getVersion(), linkage.getEntity2Version());
        assertEquals(linkage.getInputLang(), inputLang);
    }

}
