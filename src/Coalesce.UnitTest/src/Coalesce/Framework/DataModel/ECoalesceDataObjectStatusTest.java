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
        assertEquals(ECoalesceDataObjectStatus.ACTIVE, ECoalesceDataObjectStatus.getTypeForLabel("ACTIVE"));
        
    }

    @Test
    public void FromLabelDeletedStatusTest()
    {
        assertEquals(ECoalesceDataObjectStatus.DELETED, ECoalesceDataObjectStatus.getTypeForLabel("DELETED"));
        
    }
    
    @Test
    public void FromLabelUnknownStatusTest()
    {
        assertEquals(ECoalesceDataObjectStatus.UNKNOWN, ECoalesceDataObjectStatus.getTypeForLabel("UNKNOWN"));
        
    }
    
    //getStatus
    
    //toValue
    
    //toLabel
    
    //toString
    
    
}
