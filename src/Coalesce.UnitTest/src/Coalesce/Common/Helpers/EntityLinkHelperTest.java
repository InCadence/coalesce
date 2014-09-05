package Coalesce.Common.Helpers;

import static org.junit.Assert.*;

import org.junit.Test;

import Coalesce.Common.Classification.Marking;
import Coalesce.Common.UnitTest.CoalesceTypeInstances;
import Coalesce.Framework.DataModel.ELinkTypes;
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
            XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSONE);
            assertFalse(EntityLinkHelper.LinkEntities(null, linkType, entity, false));
        }
    }

    @Test
    public void LinkEntitiesNullSecondDontUpdateAllTypesExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {
            XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSONE);
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
            XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSONE);
            assertFalse(EntityLinkHelper.LinkEntities(null, linkType, entity, true));
        }
    }

    @Test
    public void LinkEntitiesNullSecondUpdateAllTypesExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {
            XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSONE);
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
            XsdEntity entity1 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSONE);
            XsdEntity entity2 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSTWO);

            assertTrue(EntityLinkHelper.LinkEntities(entity1, linkType, entity2, false));

            assertEquals(1, entity1.GetLinkageSection().GetChildDataObjects().size());
            assertEquals(1, entity2.GetLinkageSection().GetChildDataObjects().size());

            assertLinkages(linkType, entity1, entity2);

        }

    }

    @Test
    public void LinkEntitiesDontUpdateExistingAllLinkTypesExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {

            XsdEntity entity1 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSONE);
            XsdEntity entity2 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSTWO);

            EntityLinkHelper.LinkEntities(entity1, linkType, entity2, false);

            XsdEntity modifiedEntity1 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSONE);
            XsdEntity modifiedEntity2 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSTWO);

            modifiedEntity1.SetName("Entity1name");
            modifiedEntity1.SetSource("Entity1source");
            modifiedEntity1.SetVersion("Entity1version");

            modifiedEntity2.SetName("Entity2name");
            modifiedEntity2.SetSource("Entity2source");
            modifiedEntity2.SetVersion("Entity2version");

            assertTrue(EntityLinkHelper.LinkEntities(modifiedEntity1, linkType, modifiedEntity2, false));

            assertLinkages(linkType, entity1, entity2);

            XsdEntity mod2Entity1 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSONE);
            XsdEntity mod2Entity2 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSTWO);

            mod2Entity1.SetName("Entity1name");
            mod2Entity1.SetSource("Entity1source");
            mod2Entity1.SetVersion("Entity1version");

            mod2Entity2.SetName("Entity2name");
            mod2Entity2.SetSource("Entity2source");
            mod2Entity2.SetVersion("Entity2version");

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
            XsdEntity entity1 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSONE);
            XsdEntity entity2 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSTWO);

            assertTrue(EntityLinkHelper.LinkEntities(entity1, linkType, entity2, true));

            assertEquals(1, entity1.GetLinkageSection().GetChildDataObjects().size());
            assertEquals(1, entity2.GetLinkageSection().GetChildDataObjects().size());

            assertLinkages(linkType, entity1, entity2);

        }
    }

    @Test
    public void LinkEntitiesUpdateExistingAllLinkTypesExistingTest()
    {
        for (ELinkTypes linkType : ELinkTypes.values())
        {
            XsdEntity entity1 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSONE);
            XsdEntity entity2 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSTWO);

            EntityLinkHelper.LinkEntities(entity1, linkType, entity2, true);

            XsdEntity modifiedEntity1 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSONE);
            XsdEntity modifiedEntity2 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSTWO);

            modifiedEntity1.SetName("Entity1name");
            modifiedEntity1.SetSource("Entity1source");
            modifiedEntity1.SetVersion("Entity1version");

            modifiedEntity2.SetName("Entity2name");
            modifiedEntity2.SetSource("Entity2source");
            modifiedEntity2.SetVersion("Entity2version");

            assertTrue(EntityLinkHelper.LinkEntities(modifiedEntity1, linkType, modifiedEntity2, true));

            assertLinkages(linkType, modifiedEntity1, modifiedEntity2);

            XsdEntity mod2Entity1 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSONE);
            XsdEntity mod2Entity2 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSTWO);

            mod2Entity1.SetName("Entity1name");
            mod2Entity1.SetSource("Entity1source");
            mod2Entity1.SetVersion("Entity1version");

            mod2Entity2.SetName("Entity2name");
            mod2Entity2.SetSource("Entity2source");
            mod2Entity2.SetVersion("Entity2version");

            // Test updating link the other way
            assertTrue(EntityLinkHelper.LinkEntities(mod2Entity2, linkType.GetReciprocalLinkType(), mod2Entity1, true));

            assertLinkages(linkType, mod2Entity1, mod2Entity2);
        }
    }

    @Test
    public void LinkEntitiesDetailedNullFirstDontUpdateExistingTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSONE);
        assertFalse(EntityLinkHelper.LinkEntities(null, ELinkTypes.HasMember, entity, "(TS)", "jford", "en-gb", false));
    }

    @Test
    public void LinkEntitiesDetailedNullSecondDontUpdateExistingTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSONE);
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
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSONE);
        assertFalse(EntityLinkHelper.LinkEntities(null, ELinkTypes.HasUseOf, entity, "(TS)", "jford", "en-gb", true));
    }

    @Test
    public void LinkEntitiesDetailedNullSecondUpdateExistingTest()
    {
        XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSONE);
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
        XsdEntity entity1 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSONE);
        XsdEntity entity2 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSTWO);

        assertTrue(EntityLinkHelper.LinkEntities(entity1, ELinkTypes.IsAPeerOf, entity2, "(TS)", "jford", "en-gb", false));

        assertEquals(1, entity1.GetLinkageSection().GetChildDataObjects().size());
        assertEquals(1, entity2.GetLinkageSection().GetChildDataObjects().size());

        assertLinkages(ELinkTypes.IsAPeerOf, "(TS)", "jford", "en-gb", entity1, entity2);

    }

    @Test
    public void LinkEntitiesDetailedDontUpdateExistingExistingTest()
    {
        XsdEntity entity1 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSONE);
        XsdEntity entity2 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSTWO);

        EntityLinkHelper.LinkEntities(entity1, ELinkTypes.IsBeingWatchedBy, entity2, "(TS)", "jford", "en-gb", false);

        XsdEntity modifiedEntity1 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSONE);
        XsdEntity modifiedEntity2 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSTWO);

        modifiedEntity1.SetName("Entity1name");
        modifiedEntity1.SetSource("Entity1source");
        modifiedEntity1.SetVersion("Entity1version");

        modifiedEntity2.SetName("Entity2name");
        modifiedEntity2.SetSource("Entity2source");
        modifiedEntity2.SetVersion("Entity2version");

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
        XsdEntity entity1 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSONE);
        XsdEntity entity2 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSTWO);

        assertTrue(EntityLinkHelper.LinkEntities(entity1, ELinkTypes.IsChildOf, entity2, "(TS)", "jford", "en-gb", true));

        assertEquals(1, entity1.GetLinkageSection().GetChildDataObjects().size());
        assertEquals(1, entity2.GetLinkageSection().GetChildDataObjects().size());

        assertLinkages(ELinkTypes.IsChildOf, "(TS)", "jford", "en-gb", entity1, entity2);

    }

    @Test
    public void LinkEntitiesDetailedUpdateExistingExistingTest()
    {
        XsdEntity entity1 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSONE);
        XsdEntity entity2 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSTWO);

        EntityLinkHelper.LinkEntities(entity1, ELinkTypes.IsOwnedBy, entity2, "(TS)", "jford", "en-gb", true);

        XsdEntity modifiedEntity1 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSONE);
        XsdEntity modifiedEntity2 = XsdEntity.Create(CoalesceTypeInstances.TESTMISSIONNOLINKSTWO);

        modifiedEntity1.SetName("Entity1name");
        modifiedEntity1.SetSource("Entity1source");
        modifiedEntity1.SetVersion("Entity1version");

        modifiedEntity2.SetName("Entity2name");
        modifiedEntity2.SetSource("Entity2source");
        modifiedEntity2.SetVersion("Entity2version");

        assertTrue(EntityLinkHelper.LinkEntities(modifiedEntity1,
                                                 ELinkTypes.IsOwnedBy,
                                                 modifiedEntity2,
                                                 "(S USA)",
                                                 "bob",
                                                 "en-us",
                                                 true));

        assertLinkages(ELinkTypes.IsOwnedBy, "(S USA)", "bob", "en-us", modifiedEntity1, modifiedEntity2);

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
                      (XsdLinkage) entity1.GetLinkageSection().GetChildDataObjects().values().iterator().next());

        assertLinkage(linkType.GetReciprocalLinkType(),
                      classificationMarking,
                      modifiedBy,
                      inputLang,
                      entity2,
                      entity1,
                      (XsdLinkage) entity2.GetLinkageSection().GetChildDataObjects().values().iterator().next());

    }

    private void assertLinkages(ELinkTypes linkType, XsdEntity entity1, XsdEntity entity2)
    {

        assertLinkage(linkType,
                      entity1,
                      entity2,
                      (XsdLinkage) entity1.GetLinkageSection().GetChildDataObjects().values().iterator().next());

        assertLinkage(linkType.GetReciprocalLinkType(),
                      entity2,
                      entity1,
                      (XsdLinkage) entity2.GetLinkageSection().GetChildDataObjects().values().iterator().next());

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

        assertEquals(entity1.GetKey(), linkage.GetEntity1Key());
        assertEquals(entity1.GetName(), linkage.GetEntity1Name());
        assertEquals(entity1.GetSource(), linkage.GetEntity1Source());
        assertEquals(entity1.GetVersion(), linkage.GetEntity1Version());

        assertEquals(entity2.GetKey(), linkage.GetEntity2Key());
        assertEquals(entity2.GetName(), linkage.GetEntity2Name());
        assertEquals(entity2.GetSource(), linkage.GetEntity2Source());
        assertEquals(entity2.GetVersion(), linkage.GetEntity2Version());
    }

}
