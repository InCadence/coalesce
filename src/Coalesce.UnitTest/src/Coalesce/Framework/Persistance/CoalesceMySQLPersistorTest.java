package Coalesce.Framework.Persistance;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.database.persister.MySQLPersistor;

import Coalesce.Common.UnitTest.CoalesceTypeInstances;
import Coalesce.Framework.CoalesceFramework;
import Coalesce.Framework.DataModel.XsdEntity;

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

public class CoalesceMySQLPersistorTest {

    private static CoalesceFramework _coalesceFramework;
    
	@BeforeClass
	public static void Initialize() {
		
	    MySQLPersistor mySQLPersistor = new MySQLPersistor();
	    
	    CoalesceMySQLPersistorTest._coalesceFramework = new CoalesceFramework();
	    CoalesceMySQLPersistorTest._coalesceFramework.Initialize(mySQLPersistor);
	    
	}
	
	@Test
	public void TestConnection() {
		fail("Not yet implemented");
	}

	@Test
	public void TestSaveEntity() {
	    
	    XsdEntity mission = new XsdEntity(); 
	    mission.Initialize(CoalesceTypeInstances.TestMission);
	    	    
	    assertTrue(CoalesceMySQLPersistorTest._coalesceFramework.SaveCoalesceEntity(mission));
	    
	}
	
	@Test
	public void TestGetEntity() {
		fail("Not yet implemented");
	}

	@Test
	public void TestGetFieldValue() {
		fail("Not yet implemented");
	}
	
	@Test
	public void TestGetXPath() {
		fail("Not yet implemented");
	}

	@After
	public void Finalize() {
		
	}
	
}
