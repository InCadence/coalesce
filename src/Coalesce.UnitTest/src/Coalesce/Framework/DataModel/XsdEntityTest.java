package Coalesce.Framework.DataModel;

import static org.junit.Assert.*;

import org.junit.Test;

import Coalesce.Common.UnitTest.CoalesceTypeInstances;
import unity.core.runtime.CallResult;

public class XsdEntityTest {

    
/*    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }

    @Before
    public void setUp() throws Exception
    {
    	
    }

    @After
    public void tearDown() throws Exception
    {
    }*/

    @Test
    public void CreateXsdEntityFromXml() {
    	CallResult rst;
    	
    	XsdEntity entity = new XsdEntity();
    	rst = XsdEntity.Create(entity, CoalesceTypeInstances.TestMission);
    	assertTrue(rst.getIsSuccess());

    	String title = entity.GetTitle();
		assertEquals("NORTHCOM Volunteer Background Checks, NORTHCOM Volunteer Background Checks", title);

    }
    
	@Test
	public void GetTitleWithoutXpathTest() {
		CallResult rst;
		
		XsdEntity entity = new XsdEntity();
		rst = XsdEntity.Create(entity, CoalesceTypeInstances.TestMissionNoXpathTitle);
		assertTrue(rst.getIsSuccess());
		
		String title = entity.GetTitle();
		
		assertEquals("TREX Portal", title);

	}
	
	@Test
	public void UpdateTitleThatUsesXpathTest() {
        fail("Not yet implemented");
	}
	
	@Test
	public void UpdateTitleThatDoesNotUseXpathTest() {
        fail("Not yet implemented");
	}

	@Test
    public void test()
    {
        fail("Not yet implemented");
    }

}
