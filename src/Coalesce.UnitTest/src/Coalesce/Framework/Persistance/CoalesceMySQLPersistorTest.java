package Coalesce.Framework.Persistance;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import Coalesce.Common.UnitTest.CoalesceTypeInstances;
import Coalesce.Framework.CoalesceFramework;
import Coalesce.Framework.DataModel.XsdEntity;

import com.database.persister.MySQLPersistor;
import com.database.persister.ServerConn;

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
	static ServerConn serCon;
	
    private static CoalesceFramework _coalesceFramework;
    
	@BeforeClass
	public static void Initialize() {
		serCon = new ServerConn();
		serCon.setURL("jdbc:mysql://localhost:3306/coalescedatabase");
		serCon.setUser("root");
		serCon.setPassword("web23ler");

	    MySQLPersistor mySQLPersistor = new MySQLPersistor();
	    
	    mySQLPersistor.Initialize(serCon);
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
	    mission.Initialize(CoalesceTypeInstances.TESTMISSION);
	    	    
	    assertTrue(CoalesceMySQLPersistorTest._coalesceFramework.SaveCoalesceEntity(mission));
	    
	}
	
	@Test
	public void TestGetEntity() {
	    XsdEntity ent = new XsdEntity();
	    ent=CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceEntity("62857EF8-3930-4F0E-BAE3-093344EBF389");
	}

	@Test
	public void TestGetFieldValue() {
	    assertFalse(CoalesceMySQLPersistorTest._coalesceFramework.GetCoalesceFieldValue("02e03da7-de93-4deb-8c7c-be65d25f918e"));
	}
	
    private boolean assertFalse(String object) {
		if(object!=null)
			return true;
		return false;
	}

	@Test
	public void TestGetXPath() {
		fail("Not yet implemented");
	}

	@After
	public void Finalize() {
		
	}
	
}
