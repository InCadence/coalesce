package Coalesce.Common.RunTime;

import static org.junit.Assert.*;

import org.junit.Test;

import Coalesce.Common.Helpers.StringHelper;
import Coalesce.Common.Runtime.CoalesceSettings;

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

public class CoalesceSettingTest {

/*	@BeforeClass
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
    public void GetDefaultApplicationRootNotSetTest()
    {
        String root = CoalesceSettings.GetDefaultApplicationRoot();
     
        // TODO: Uses Unit test runner as the main path which is based on the individual IDE installation location
        //       Should somehow be changed to a testable assert. Verified manually that it does return bin path
        //       of a standalone application.
        assertFalse(StringHelper.IsNullOrEmpty(root));
    }
    
    @Test
    public void GetDefaultApplicationRootSetTest()
    {
        CoalesceSettings.SetDefaultApplicationRoot("C:\\Program Files\\Java\\jre7\\bin");
        
        assertEquals("C:\\Program Files\\Java\\jre7\\bin", CoalesceSettings.GetDefaultApplicationRoot());
        
    }
    
	@Test
	public void test()
	{
	    // TODO: Complete testing
	}

}
