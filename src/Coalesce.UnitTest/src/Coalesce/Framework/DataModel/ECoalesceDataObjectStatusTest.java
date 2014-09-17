package Coalesce.Framework.DataModel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/*
 * @BeforeClass public static void setUpBeforeClass() throws Exception { }
 * 
 * @AfterClass public static void tearDownAfterClass() throws Exception { }
 * 
 * @Before public void setUp() throws Exception { }
 * 
 * @After public void tearDown() throws Exception { }
 */

public class ECoalesceDataObjectStatusTest {

    @Test
    public void FromLabelActiveStatusTest()
    {
        assertEquals(ECoalesceDataObjectStatus.ACTIVE, ECoalesceDataObjectStatus.fromLabel("ACTIVE"));
        
    }

    @Test
    public void FromLabelDeletedStatusTest()
    {
        assertEquals(ECoalesceDataObjectStatus.DELETED, ECoalesceDataObjectStatus.fromLabel("DELETED"));
        
    }
    
    @Test
    public void FromLabelUnknownStatusTest()
    {
        assertEquals(ECoalesceDataObjectStatus.UNKNOWN, ECoalesceDataObjectStatus.fromLabel("UNKNOWN"));
        
    }
    
    @test
    public void GetStatusActiveStatusTest()
    {        
       //int a = 1;
       //String value = "Active";
       //assertEquals(a, ECoalesceDataObjectStatus.ACTIVE);
       
       assertEquals(ECoalesceDataObjectStatus.ACTIVE, ECoalesceDataObjectStatus.getStatus(1));
    }
    
    @test
    //getStatus
    
    @test
    //toValue
    
    @test
    //toLabel
    
    @test
    //toString
    
    
}
