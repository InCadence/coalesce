package Coalesce.Framework.DataModel;

import static org.junit.Assert.*;

import org.junit.Test;

import Coalesce.Common.UnitTest.CoalesceTypeInstances;
import unity.core.runtime.CallResult;

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
	public void EmptyDateInitialization() {
		fail("Not yet implemented");
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
