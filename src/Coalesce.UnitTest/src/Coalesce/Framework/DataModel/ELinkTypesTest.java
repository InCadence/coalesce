package Coalesce.Framework.DataModel;

import static org.junit.Assert.*;

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

        assertEquals(ELinkTypes.Undefined, ELinkTypes.GetTypeForLabel("Undefined"));
        
    }
    
    @Test
    public void GetTypeForLabelUndefinedLowerCaseTest()
    {

        assertEquals(ELinkTypes.Undefined, ELinkTypes.GetTypeForLabel("undefined"));
        
    }
    
    @Test
    public void GetTypeForLabelUndefinedUpperCaseTest()
    {

        assertEquals(ELinkTypes.Undefined, ELinkTypes.GetTypeForLabel("Undefined".toUpperCase()));
        
    }
    
    @Test
    public void GetTypeForLabelIsChildOfTest()
    {

        assertEquals(ELinkTypes.IsChildOf, ELinkTypes.GetTypeForLabel("IsChildOf"));
        
    }
    
    @Test
    public void GetTypeForLabelNullTest()
    {

        assertEquals(ELinkTypes.Undefined, ELinkTypes.GetTypeForLabel(null));
        
    }
    
    @Test
    public void GetTypeForLabelEmptyTest()
    {

        assertEquals(ELinkTypes.Undefined, ELinkTypes.GetTypeForLabel(""));
        
    }
    
    @Test
    public void GetTypeForLabelIsParentOfTest()
    {

        assertEquals(ELinkTypes.IsParentOf, ELinkTypes.GetTypeForLabel("IsParentOf"));
        
    }
    
    @Test
    public void GetTypeForLabelCreatedTest()
    {

        assertEquals(ELinkTypes.Created, ELinkTypes.GetTypeForLabel("Created"));
        
    }
    
    @Test
    public void GetTypeForLabelWasCreatedByTest()
    {

        assertEquals(ELinkTypes.WasCreatedBy, ELinkTypes.GetTypeForLabel("WasCreatedBy"));
        
    }
    
    @Test
    public void GetTypeForLabelHasMemberTest()
    {

        assertEquals(ELinkTypes.HasMember, ELinkTypes.GetTypeForLabel("HasMember"));
        
    }
    
    @Test
    public void GetTypeForLabelIsAMemberOfTest()
    {

        assertEquals(ELinkTypes.IsAMemberOf, ELinkTypes.GetTypeForLabel("IsAMemberOf"));
        
    }
    
    @Test
    public void GetTypeForLabelHasParticipantTest()
    {

        assertEquals(ELinkTypes.HasParticipant, ELinkTypes.GetTypeForLabel("HasParticipant"));
        
    }
    
    @Test
    public void GetTypeForLabelIsAParticipantTest()
    {

        assertEquals(ELinkTypes.IsAParticipantOf, ELinkTypes.GetTypeForLabel("IsAParticipantOf"));
        
    }
    
    @Test
    public void GetTypeForLabelIsWatchingTest()
    {

        assertEquals(ELinkTypes.IsWatching, ELinkTypes.GetTypeForLabel("IsWatching"));
        
    }
    
    @Test
    public void GetTypeForLabelIsBeingWatchedByTest()
    {

        assertEquals(ELinkTypes.IsBeingWatchedBy, ELinkTypes.GetTypeForLabel("IsBeingWatchedBy"));
        
    }
    
    @Test
    public void GetTypeForLabelIsAPeerOfTest()
    {

        assertEquals(ELinkTypes.IsAPeerOf, ELinkTypes.GetTypeForLabel("IsAPeerOf"));
        
    }
    
    @Test
    public void GetTypeForLabelIsOwnedByTest()
    {

        assertEquals(ELinkTypes.IsOwnedBy, ELinkTypes.GetTypeForLabel("IsOwnedBy"));
        
    }
    
    @Test
    public void GetTypeForLabelHasOwnershipOfTest()
    {

        assertEquals(ELinkTypes.HasOwnershipOf, ELinkTypes.GetTypeForLabel("HasOwnershipOf"));
        
    }
    
    @Test
    public void GetTypeForLabelIsUsedByTest()
    {

        assertEquals(ELinkTypes.IsUsedBy, ELinkTypes.GetTypeForLabel("IsUsedBy"));
        
    }
    
    @Test
    public void GetTypeForLabelHasUseOfTest()
    {

        assertEquals(ELinkTypes.HasUseOf, ELinkTypes.GetTypeForLabel("HasUseOf"));
        
    }
    
    @Test
    public void GetReciprocalLinkTypeIsAMemberOfTest() {
        
        ELinkTypes value = ELinkTypes.IsAMemberOf;
        assertEquals(ELinkTypes.HasMember, value.GetReciprocalLinkType());
    }
    
    @Test
    public void GetReciprocalLinkTypeHasMemberTest() {
        
        ELinkTypes value = ELinkTypes.HasMember;
        assertEquals(ELinkTypes.IsAMemberOf, value.GetReciprocalLinkType());
    }
   
    @Test
    public void GetReciprocalLinkTypeIsChildOfTest() {
        
        ELinkTypes value = ELinkTypes.IsChildOf;
        assertEquals(ELinkTypes.IsParentOf, value.GetReciprocalLinkType());
    }
    
    @Test
    public void GetReciprocalLinkTypeIsParentOfTest() {
        
        ELinkTypes value = ELinkTypes.IsParentOf;
        assertEquals(ELinkTypes.IsChildOf, value.GetReciprocalLinkType());
    }
    
    @Test
    public void GetReciprocalLinkTypeWasCreatedByTest() {
        
        ELinkTypes value = ELinkTypes.WasCreatedBy;
        assertEquals(ELinkTypes.Created, value.GetReciprocalLinkType());
    }
    
    @Test
    public void GetReciprocalLinkTypeCreatedTest() {
        
        ELinkTypes value = ELinkTypes.Created;
        assertEquals(ELinkTypes.WasCreatedBy, value.GetReciprocalLinkType());
    }
    
    @Test
    public void GetReciprocalLinkTypeUndefinedTest() {
        
        ELinkTypes value = ELinkTypes.Undefined;
        assertEquals(ELinkTypes.Undefined, value.GetReciprocalLinkType());
    }
    
    @Test
    public void GetReciprocalLinkTypeHasParticipantTest() {
        
        ELinkTypes value = ELinkTypes.HasParticipant;
        assertEquals(ELinkTypes.IsAParticipantOf, value.GetReciprocalLinkType());
    }
    
    @Test
    public void GetReciprocalLinkTypeIsAParticipantOfTest() {
        
        ELinkTypes value = ELinkTypes.IsAParticipantOf;
        assertEquals(ELinkTypes.HasParticipant, value.GetReciprocalLinkType());
    }
    
    @Test
    public void GetReciprocalLinkTypeIsWatchingTest() {
        
        ELinkTypes value = ELinkTypes.IsWatching;
        assertEquals(ELinkTypes.IsBeingWatchedBy, value.GetReciprocalLinkType());
    }
    
    @Test
    public void GetReciprocalLinkTypeIsBeingWatchedByTest() {
        
        ELinkTypes value = ELinkTypes.IsBeingWatchedBy;
        assertEquals(ELinkTypes.IsWatching, value.GetReciprocalLinkType());
    }
    
    @Test
    public void GetReciprocalLinkTypeIsAPeerOfTest() {
        
        ELinkTypes value = ELinkTypes.IsAPeerOf;
        assertEquals(ELinkTypes.IsAPeerOf, value.GetReciprocalLinkType());
    }
    
    @Test
    public void GetReciprocalLinkTypeIsOwnedByTest() {
        
        ELinkTypes value = ELinkTypes.IsOwnedBy;
        assertEquals(ELinkTypes.HasOwnershipOf, value.GetReciprocalLinkType());
    }
    
    @Test
    public void GetReciprocalLinkTypeHasOwnershipOfTest() {
        
        ELinkTypes value = ELinkTypes.HasOwnershipOf;
        assertEquals(ELinkTypes.IsOwnedBy, value.GetReciprocalLinkType());
    }
    
    @Test
    public void GetReciprocalLinkTypeIsUsedByTest() {
        
        ELinkTypes value = ELinkTypes.IsUsedBy;
        assertEquals(ELinkTypes.HasUseOf, value.GetReciprocalLinkType());
    }
    
    @Test
    public void GetReciprocalLinkTypeHasUseOfTest() {
        
        ELinkTypes value = ELinkTypes.HasUseOf;
        assertEquals(ELinkTypes.IsUsedBy, value.GetReciprocalLinkType());
    }
    
    
}
