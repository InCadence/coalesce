package com.incadencecorp.coalesce.framework.datamodel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ELinkTypesTest {

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
    public void getTypeForLabelUndefinedTest()
    {

        assertEquals(ELinkTypes.UNDEFINED, ELinkTypes.getTypeForLabel("Undefined"));

    }

    @Test
    public void getTypeForLabelUndefinedLowerCaseTest()
    {

        assertEquals(ELinkTypes.UNDEFINED, ELinkTypes.getTypeForLabel("undefined"));

    }

    @Test
    public void getTypeForLabelUndefinedUpperCaseTest()
    {

        assertEquals(ELinkTypes.UNDEFINED, ELinkTypes.getTypeForLabel("Undefined".toUpperCase()));

    }

    @Test
    public void getTypeForLabelIsChildOfTest()
    {

        assertEquals(ELinkTypes.IS_CHILD_OF, ELinkTypes.getTypeForLabel("IsChildOf"));

    }

    @Test
    public void getTypeForLabelNullTest()
    {

        assertEquals(ELinkTypes.UNDEFINED, ELinkTypes.getTypeForLabel(null));

    }

    @Test
    public void getTypeForLabelEmptyTest()
    {

        assertEquals(ELinkTypes.UNDEFINED, ELinkTypes.getTypeForLabel(""));

    }

    @Test
    public void getTypeForLabelIsParentOfTest()
    {

        assertEquals(ELinkTypes.IS_PARENT_OF, ELinkTypes.getTypeForLabel("IsParentOf"));

    }

    @Test
    public void getTypeForLabelCreatedTest()
    {

        assertEquals(ELinkTypes.CREATED, ELinkTypes.getTypeForLabel("Created"));

    }

    @Test
    public void getTypeForLabelWasCreatedByTest()
    {

        assertEquals(ELinkTypes.WAS_CREATED_BY, ELinkTypes.getTypeForLabel("WasCreatedBy"));

    }

    @Test
    public void getTypeForLabelHasMemberTest()
    {

        assertEquals(ELinkTypes.HAS_MEMBER, ELinkTypes.getTypeForLabel("HasMember"));

    }

    @Test
    public void getTypeForLabelIsAMemberOfTest()
    {

        assertEquals(ELinkTypes.IS_A_MEMBER_OF, ELinkTypes.getTypeForLabel("IsAMemberOf"));

    }

    @Test
    public void getTypeForLabelHasParticipantTest()
    {

        assertEquals(ELinkTypes.HAS_PARTICIPANT, ELinkTypes.getTypeForLabel("HasParticipant"));

    }

    @Test
    public void getTypeForLabelIsAParticipantTest()
    {

        assertEquals(ELinkTypes.IS_A_PARTICIPANT_OF, ELinkTypes.getTypeForLabel("IsAParticipantOf"));

    }

    @Test
    public void getTypeForLabelIsWatchingTest()
    {

        assertEquals(ELinkTypes.IS_WATCHING, ELinkTypes.getTypeForLabel("IsWatching"));

    }

    @Test
    public void getTypeForLabelIsBeingWatchedByTest()
    {

        assertEquals(ELinkTypes.IS_BEING_WATCHED_BY, ELinkTypes.getTypeForLabel("IsBeingWatchedBy"));

    }

    @Test
    public void getTypeForLabelIsAPeerOfTest()
    {

        assertEquals(ELinkTypes.IS_A_PEER_OF, ELinkTypes.getTypeForLabel("IsAPeerOf"));

    }

    @Test
    public void getTypeForLabelIsOwnedByTest()
    {

        assertEquals(ELinkTypes.IS_OWNED_BY, ELinkTypes.getTypeForLabel("IsOwnedBy"));

    }

    @Test
    public void getTypeForLabelHasOwnershipOfTest()
    {

        assertEquals(ELinkTypes.HAS_OWNERSHIP_OF, ELinkTypes.getTypeForLabel("HasOwnershipOf"));

    }

    @Test
    public void getTypeForLabelIsUsedByTest()
    {

        assertEquals(ELinkTypes.IS_USED_BY, ELinkTypes.getTypeForLabel("IsUsedBy"));

    }

    @Test
    public void getTypeForLabelHasUseOfTest()
    {

        assertEquals(ELinkTypes.HAS_USE_OF, ELinkTypes.getTypeForLabel("HasUseOf"));

    }

    @Test
    public void getReciprocalLinkTypeIsAMemberOfTest()
    {

        ELinkTypes value = ELinkTypes.IS_A_MEMBER_OF;
        assertEquals(ELinkTypes.HAS_MEMBER, value.getReciprocalLinkType());
    }

    @Test
    public void getReciprocalLinkTypeHasMemberTest()
    {

        ELinkTypes value = ELinkTypes.HAS_MEMBER;
        assertEquals(ELinkTypes.IS_A_MEMBER_OF, value.getReciprocalLinkType());
    }

    @Test
    public void getReciprocalLinkTypeIsChildOfTest()
    {

        ELinkTypes value = ELinkTypes.IS_CHILD_OF;
        assertEquals(ELinkTypes.IS_PARENT_OF, value.getReciprocalLinkType());
    }

    @Test
    public void getReciprocalLinkTypeIsParentOfTest()
    {

        ELinkTypes value = ELinkTypes.IS_PARENT_OF;
        assertEquals(ELinkTypes.IS_CHILD_OF, value.getReciprocalLinkType());
    }

    @Test
    public void getReciprocalLinkTypeWasCreatedByTest()
    {

        ELinkTypes value = ELinkTypes.WAS_CREATED_BY;
        assertEquals(ELinkTypes.CREATED, value.getReciprocalLinkType());
    }

    @Test
    public void getReciprocalLinkTypeCreatedTest()
    {

        ELinkTypes value = ELinkTypes.CREATED;
        assertEquals(ELinkTypes.WAS_CREATED_BY, value.getReciprocalLinkType());
    }

    @Test
    public void getReciprocalLinkTypeUndefinedTest()
    {

        ELinkTypes value = ELinkTypes.UNDEFINED;
        assertEquals(ELinkTypes.UNDEFINED, value.getReciprocalLinkType());
    }

    @Test
    public void getReciprocalLinkTypeHasParticipantTest()
    {

        ELinkTypes value = ELinkTypes.HAS_PARTICIPANT;
        assertEquals(ELinkTypes.IS_A_PARTICIPANT_OF, value.getReciprocalLinkType());
    }

    @Test
    public void getReciprocalLinkTypeIsAParticipantOfTest()
    {

        ELinkTypes value = ELinkTypes.IS_A_PARTICIPANT_OF;
        assertEquals(ELinkTypes.HAS_PARTICIPANT, value.getReciprocalLinkType());
    }

    @Test
    public void getReciprocalLinkTypeIsWatchingTest()
    {

        ELinkTypes value = ELinkTypes.IS_WATCHING;
        assertEquals(ELinkTypes.IS_BEING_WATCHED_BY, value.getReciprocalLinkType());
    }

    @Test
    public void getReciprocalLinkTypeIsBeingWatchedByTest()
    {

        ELinkTypes value = ELinkTypes.IS_BEING_WATCHED_BY;
        assertEquals(ELinkTypes.IS_WATCHING, value.getReciprocalLinkType());
    }

    @Test
    public void getReciprocalLinkTypeIsAPeerOfTest()
    {

        ELinkTypes value = ELinkTypes.IS_A_PEER_OF;
        assertEquals(ELinkTypes.IS_A_PEER_OF, value.getReciprocalLinkType());
    }

    @Test
    public void getReciprocalLinkTypeIsOwnedByTest()
    {

        ELinkTypes value = ELinkTypes.IS_OWNED_BY;
        assertEquals(ELinkTypes.HAS_OWNERSHIP_OF, value.getReciprocalLinkType());
    }

    @Test
    public void getReciprocalLinkTypeHasOwnershipOfTest()
    {

        ELinkTypes value = ELinkTypes.HAS_OWNERSHIP_OF;
        assertEquals(ELinkTypes.IS_OWNED_BY, value.getReciprocalLinkType());
    }

    @Test
    public void getReciprocalLinkTypeIsUsedByTest()
    {

        ELinkTypes value = ELinkTypes.IS_USED_BY;
        assertEquals(ELinkTypes.HAS_USE_OF, value.getReciprocalLinkType());
    }

    @Test
    public void getReciprocalLinkTypeHasUseOfTest()
    {

        ELinkTypes value = ELinkTypes.HAS_USE_OF;
        assertEquals(ELinkTypes.IS_USED_BY, value.getReciprocalLinkType());
    }

    @Test
    public void getLabelUndefinedTest()
    {

        String a = "Undefined";
        ELinkTypes value = ELinkTypes.UNDEFINED;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelIsChildOfTest()
    {

        String a = "IsChildOf";
        ELinkTypes value = ELinkTypes.IS_CHILD_OF;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelIsParentOfTest()
    {

        String a = "IsParentOf";
        ELinkTypes value = ELinkTypes.IS_PARENT_OF;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelCreatedTest()
    {

        String a = "Created";
        ELinkTypes value = ELinkTypes.CREATED;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelWasCreatedByTest()
    {

        String a = "WasCreatedBy";
        ELinkTypes value = ELinkTypes.WAS_CREATED_BY;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelHasMemberTest()
    {

        String a = "HasMember";
        ELinkTypes value = ELinkTypes.HAS_MEMBER;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelIsAMemberOfTest()
    {

        String a = "IsAMemberOf";
        ELinkTypes value = ELinkTypes.IS_A_MEMBER_OF;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelHasParticipantTest()
    {

        String a = "HasParticipant";
        ELinkTypes value = ELinkTypes.HAS_PARTICIPANT;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelIsAParticipantOfTest()
    {

        String a = "IsAParticipantOf";
        ELinkTypes value = ELinkTypes.IS_A_PARTICIPANT_OF;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelIsWatchingTest()
    {

        String a = "IsWatching";
        ELinkTypes value = ELinkTypes.IS_WATCHING;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelIsBeingWatchedByTest()
    {

        String a = "IsBeingWatchedBy";
        ELinkTypes value = ELinkTypes.IS_BEING_WATCHED_BY;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelIsAPeerOfTest()
    {

        String a = "IsAPeerOf";
        ELinkTypes value = ELinkTypes.IS_A_PEER_OF;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelIsOwnedByTest()
    {

        String a = "IsOwnedBy";
        ELinkTypes value = ELinkTypes.IS_OWNED_BY;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelHasOwnershipOfTest()
    {

        String a = "HasOwnershipOf";
        ELinkTypes value = ELinkTypes.HAS_OWNERSHIP_OF;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelIsUsedByTest()
    {

        String a = "IsUsedBy";
        ELinkTypes value = ELinkTypes.IS_USED_BY;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void getLabelHasUseOfTest()
    {

        String a = "HasUseOf";
        ELinkTypes value = ELinkTypes.HAS_USE_OF;

        assertEquals(a, value.getLabel());
    }
}
