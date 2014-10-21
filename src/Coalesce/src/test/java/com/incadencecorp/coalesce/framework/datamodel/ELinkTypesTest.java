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
    public void GetTypeForLabelUndefinedTest()
    {

        assertEquals(ELinkTypes.UNDEFINED, ELinkTypes.getTypeForLabel("Undefined"));

    }

    @Test
    public void GetTypeForLabelUndefinedLowerCaseTest()
    {

        assertEquals(ELinkTypes.UNDEFINED, ELinkTypes.getTypeForLabel("undefined"));

    }

    @Test
    public void GetTypeForLabelUndefinedUpperCaseTest()
    {

        assertEquals(ELinkTypes.UNDEFINED, ELinkTypes.getTypeForLabel("Undefined".toUpperCase()));

    }

    @Test
    public void GetTypeForLabelIsChildOfTest()
    {

        assertEquals(ELinkTypes.IS_CHILD_OF, ELinkTypes.getTypeForLabel("IsChildOf"));

    }

    @Test
    public void GetTypeForLabelNullTest()
    {

        assertEquals(ELinkTypes.UNDEFINED, ELinkTypes.getTypeForLabel(null));

    }

    @Test
    public void GetTypeForLabelEmptyTest()
    {

        assertEquals(ELinkTypes.UNDEFINED, ELinkTypes.getTypeForLabel(""));

    }

    @Test
    public void GetTypeForLabelIsParentOfTest()
    {

        assertEquals(ELinkTypes.IS_PARENT_OF, ELinkTypes.getTypeForLabel("IsParentOf"));

    }

    @Test
    public void GetTypeForLabelCreatedTest()
    {

        assertEquals(ELinkTypes.CREATED, ELinkTypes.getTypeForLabel("Created"));

    }

    @Test
    public void GetTypeForLabelWasCreatedByTest()
    {

        assertEquals(ELinkTypes.WAS_CREATED_BY, ELinkTypes.getTypeForLabel("WasCreatedBy"));

    }

    @Test
    public void GetTypeForLabelHasMemberTest()
    {

        assertEquals(ELinkTypes.HAS_MEMBER, ELinkTypes.getTypeForLabel("HasMember"));

    }

    @Test
    public void GetTypeForLabelIsAMemberOfTest()
    {

        assertEquals(ELinkTypes.IS_A_MEMBER_OF, ELinkTypes.getTypeForLabel("IsAMemberOf"));

    }

    @Test
    public void GetTypeForLabelHasParticipantTest()
    {

        assertEquals(ELinkTypes.HAS_PARTICIPANT, ELinkTypes.getTypeForLabel("HasParticipant"));

    }

    @Test
    public void GetTypeForLabelIsAParticipantTest()
    {

        assertEquals(ELinkTypes.IS_A_PARTICIPANT_OF, ELinkTypes.getTypeForLabel("IsAParticipantOf"));

    }

    @Test
    public void GetTypeForLabelIsWatchingTest()
    {

        assertEquals(ELinkTypes.IS_WATCHING, ELinkTypes.getTypeForLabel("IsWatching"));

    }

    @Test
    public void GetTypeForLabelIsBeingWatchedByTest()
    {

        assertEquals(ELinkTypes.IS_BEING_WATCHED_BY, ELinkTypes.getTypeForLabel("IsBeingWatchedBy"));

    }

    @Test
    public void GetTypeForLabelIsAPeerOfTest()
    {

        assertEquals(ELinkTypes.IS_A_PEER_OF, ELinkTypes.getTypeForLabel("IsAPeerOf"));

    }

    @Test
    public void GetTypeForLabelIsOwnedByTest()
    {

        assertEquals(ELinkTypes.IS_OWNED_BY, ELinkTypes.getTypeForLabel("IsOwnedBy"));

    }

    @Test
    public void GetTypeForLabelHasOwnershipOfTest()
    {

        assertEquals(ELinkTypes.HAS_OWNERSHIP_OF, ELinkTypes.getTypeForLabel("HasOwnershipOf"));

    }

    @Test
    public void GetTypeForLabelIsUsedByTest()
    {

        assertEquals(ELinkTypes.IS_USED_BY, ELinkTypes.getTypeForLabel("IsUsedBy"));

    }

    @Test
    public void GetTypeForLabelHasUseOfTest()
    {

        assertEquals(ELinkTypes.HAS_USE_OF, ELinkTypes.getTypeForLabel("HasUseOf"));

    }

    @Test
    public void GetReciprocalLinkTypeIsAMemberOfTest()
    {

        ELinkTypes value = ELinkTypes.IS_A_MEMBER_OF;
        assertEquals(ELinkTypes.HAS_MEMBER, value.getReciprocalLinkType());
    }

    @Test
    public void GetReciprocalLinkTypeHasMemberTest()
    {

        ELinkTypes value = ELinkTypes.HAS_MEMBER;
        assertEquals(ELinkTypes.IS_A_MEMBER_OF, value.getReciprocalLinkType());
    }

    @Test
    public void GetReciprocalLinkTypeIsChildOfTest()
    {

        ELinkTypes value = ELinkTypes.IS_CHILD_OF;
        assertEquals(ELinkTypes.IS_PARENT_OF, value.getReciprocalLinkType());
    }

    @Test
    public void GetReciprocalLinkTypeIsParentOfTest()
    {

        ELinkTypes value = ELinkTypes.IS_PARENT_OF;
        assertEquals(ELinkTypes.IS_CHILD_OF, value.getReciprocalLinkType());
    }

    @Test
    public void GetReciprocalLinkTypeWasCreatedByTest()
    {

        ELinkTypes value = ELinkTypes.WAS_CREATED_BY;
        assertEquals(ELinkTypes.CREATED, value.getReciprocalLinkType());
    }

    @Test
    public void GetReciprocalLinkTypeCreatedTest()
    {

        ELinkTypes value = ELinkTypes.CREATED;
        assertEquals(ELinkTypes.WAS_CREATED_BY, value.getReciprocalLinkType());
    }

    @Test
    public void GetReciprocalLinkTypeUndefinedTest()
    {

        ELinkTypes value = ELinkTypes.UNDEFINED;
        assertEquals(ELinkTypes.UNDEFINED, value.getReciprocalLinkType());
    }

    @Test
    public void GetReciprocalLinkTypeHasParticipantTest()
    {

        ELinkTypes value = ELinkTypes.HAS_PARTICIPANT;
        assertEquals(ELinkTypes.IS_A_PARTICIPANT_OF, value.getReciprocalLinkType());
    }

    @Test
    public void GetReciprocalLinkTypeIsAParticipantOfTest()
    {

        ELinkTypes value = ELinkTypes.IS_A_PARTICIPANT_OF;
        assertEquals(ELinkTypes.HAS_PARTICIPANT, value.getReciprocalLinkType());
    }

    @Test
    public void GetReciprocalLinkTypeIsWatchingTest()
    {

        ELinkTypes value = ELinkTypes.IS_WATCHING;
        assertEquals(ELinkTypes.IS_BEING_WATCHED_BY, value.getReciprocalLinkType());
    }

    @Test
    public void GetReciprocalLinkTypeIsBeingWatchedByTest()
    {

        ELinkTypes value = ELinkTypes.IS_BEING_WATCHED_BY;
        assertEquals(ELinkTypes.IS_WATCHING, value.getReciprocalLinkType());
    }

    @Test
    public void GetReciprocalLinkTypeIsAPeerOfTest()
    {

        ELinkTypes value = ELinkTypes.IS_A_PEER_OF;
        assertEquals(ELinkTypes.IS_A_PEER_OF, value.getReciprocalLinkType());
    }

    @Test
    public void GetReciprocalLinkTypeIsOwnedByTest()
    {

        ELinkTypes value = ELinkTypes.IS_OWNED_BY;
        assertEquals(ELinkTypes.HAS_OWNERSHIP_OF, value.getReciprocalLinkType());
    }

    @Test
    public void GetReciprocalLinkTypeHasOwnershipOfTest()
    {

        ELinkTypes value = ELinkTypes.HAS_OWNERSHIP_OF;
        assertEquals(ELinkTypes.IS_OWNED_BY, value.getReciprocalLinkType());
    }

    @Test
    public void GetReciprocalLinkTypeIsUsedByTest()
    {

        ELinkTypes value = ELinkTypes.IS_USED_BY;
        assertEquals(ELinkTypes.HAS_USE_OF, value.getReciprocalLinkType());
    }

    @Test
    public void GetReciprocalLinkTypeHasUseOfTest()
    {

        ELinkTypes value = ELinkTypes.HAS_USE_OF;
        assertEquals(ELinkTypes.IS_USED_BY, value.getReciprocalLinkType());
    }

    @Test
    public void GetLabelUndefinedTest()
    {

        String a = "Undefined";
        ELinkTypes value = ELinkTypes.UNDEFINED;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelIsChildOfTest()
    {

        String a = "IsChildOf";
        ELinkTypes value = ELinkTypes.IS_CHILD_OF;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelIsParentOfTest()
    {

        String a = "IsParentOf";
        ELinkTypes value = ELinkTypes.IS_PARENT_OF;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelCreatedTest()
    {

        String a = "Created";
        ELinkTypes value = ELinkTypes.CREATED;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelWasCreatedByTest()
    {

        String a = "WasCreatedBy";
        ELinkTypes value = ELinkTypes.WAS_CREATED_BY;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelHasMemberTest()
    {

        String a = "HasMember";
        ELinkTypes value = ELinkTypes.HAS_MEMBER;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelIsAMemberOfTest()
    {

        String a = "IsAMemberOf";
        ELinkTypes value = ELinkTypes.IS_A_MEMBER_OF;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelHasParticipantTest()
    {

        String a = "HasParticipant";
        ELinkTypes value = ELinkTypes.HAS_PARTICIPANT;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelIsAParticipantOfTest()
    {

        String a = "IsAParticipantOf";
        ELinkTypes value = ELinkTypes.IS_A_PARTICIPANT_OF;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelIsWatchingTest()
    {

        String a = "IsWatching";
        ELinkTypes value = ELinkTypes.IS_WATCHING;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelIsBeingWatchedByTest()
    {

        String a = "IsBeingWatchedBy";
        ELinkTypes value = ELinkTypes.IS_BEING_WATCHED_BY;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelIsAPeerOfTest()
    {

        String a = "IsAPeerOf";
        ELinkTypes value = ELinkTypes.IS_A_PEER_OF;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelIsOwnedByTest()
    {

        String a = "IsOwnedBy";
        ELinkTypes value = ELinkTypes.IS_OWNED_BY;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelHasOwnershipOfTest()
    {

        String a = "HasOwnershipOf";
        ELinkTypes value = ELinkTypes.HAS_OWNERSHIP_OF;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelIsUsedByTest()
    {

        String a = "IsUsedBy";
        ELinkTypes value = ELinkTypes.IS_USED_BY;

        assertEquals(a, value.getLabel());
    }

    @Test
    public void GetLabelHasUseOfTest()
    {

        String a = "HasUseOf";
        ELinkTypes value = ELinkTypes.HAS_USE_OF;

        assertEquals(a, value.getLabel());
    }
}
