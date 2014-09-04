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
    public void GetTypeForLabelUndefinedUpderCaseTest()
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
    public void GetReciprocalLinkTypeIsAMemberOfTest() {
        
        ELinkTypes value = ELinkTypes.IsAMemberOf;
        assertEquals(ELinkTypes.HasMember, value.GetReciprocalLinkType());
    }
    
    
}
