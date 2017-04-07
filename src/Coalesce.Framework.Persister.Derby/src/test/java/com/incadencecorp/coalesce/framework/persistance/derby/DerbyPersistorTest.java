/**
 * 
 */
package com.incadencecorp.coalesce.framework.persistance.derby;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalescePersistorBaseTest;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;

/**
 * @author mdaconta
 *
 */
public class DerbyPersistorTest extends CoalescePersistorBaseTest {
    @BeforeClass
    public static void setupBeforeClass() throws Exception
    {
        DerbyPersistorTest tester = new DerbyPersistorTest();

        CoalescePersistorBaseTest.setupBeforeClassBase(tester);

    }

    @AfterClass
    public static void tearDownAfterClass() 
    {
        DerbyPersistorTest tester = new DerbyPersistorTest();

        CoalescePersistorBaseTest.tearDownAfterClassBase(tester);

    }
    
	@Override
	protected ServerConn getConnection() {
        ServerConn serCon = new ServerConn();
        // InCadence Settings
        serCon.setServerName("127.0.0.1");
        serCon.setDatabase("CoalesceDatabase");
        serCon.setUser("CoalesceUser");
        serCon.setPassword("Passw0rd");
        
        return serCon;
	}

	@Override
	protected ICoalescePersistor getPersistor(ServerConn conn) {
        DerbyPersistor derbyPersistor = new DerbyPersistor();
        derbyPersistor.setConnectionSettings(conn);
        derbyPersistor.setSchema("coalesce");

        return derbyPersistor;
	}

	@Override
	protected CoalesceDataConnectorBase getDataConnector(ServerConn conn)
			throws CoalescePersistorException {
        return new DerbyDataConnector(conn.getDatabase(), null, "memory");
	}
	
	@Override
	public void testGetFieldValue() throws CoalescePersistorException
    {
    }

}
