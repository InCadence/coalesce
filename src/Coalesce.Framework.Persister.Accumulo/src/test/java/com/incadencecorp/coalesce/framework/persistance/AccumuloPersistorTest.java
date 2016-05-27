package com.incadencecorp.coalesce.framework.persistance;
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
/**
 * @author Jing Yang
 * May 13, 2016
 */

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.minicluster.MiniAccumuloCluster;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;
import com.incadencecorp.coalesce.framework.persistance.accumulo.AccumuloDataConnector;
import com.incadencecorp.coalesce.framework.persistance.accumulo.AccumuloPersistor;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;


public class AccumuloPersistorTest extends CoalescePersistorBaseTest {

	private static MiniAccumuloCluster accumulo = null;
	private static Instance instance;
	//private static ServerConn _serCon;
   // private static CoalesceFramework _coalesceFramework;

    //private static CoalesceEntity _entity;
    //private static String _entityXml;
    //private static String _fieldKey;

    //private static String _testTemplateKey = null;
    
    private  static Connector conn;
    
    private static  CoalesceDataConnectorBase aconn;
      
	@Rule
	public TemporaryFolder tempDirectory = new TemporaryFolder();
	
    @BeforeClass
    public static void setupBeforeClass() throws SAXException, IOException, CoalesceException, TableNotFoundException,
    						AccumuloException, AccumuloSecurityException
    {
        AccumuloPersistorTest tester = new AccumuloPersistorTest();

        //CoalescePersistorBaseTest.setupBeforeClassBase(tester);
        _serCon = tester.getConnection();
        
        ICoalescePersistor persistor = tester.getPersistor(_serCon);

        _coalesceFramework = new CoalesceFramework();
        _coalesceFramework.initialize(persistor);

        //CoalescePersistorBaseTest.cleanUpDatabase(tester);
        // Clean up the database
        aconn = new AccumuloDataConnector(_serCon);
        conn =  AccumuloDataConnector.getDBConnector();
        
        conn.tableOperations().deleteRows(AccumuloDataConnector.coalesceTable, null, null);
        conn.tableOperations().deleteRows(AccumuloDataConnector.coalesceTemplateTable, null, null);
        conn.tableOperations().deleteRows(AccumuloDataConnector.coalesceEntityIndex, null, null);
        
        _entity = CoalescePersistorBaseTest.createEntity();
        _entityXml = _entity.toXml();

        _fieldKey = CoalescePersistorBaseTest.getCurrentStatusField(_entity).getKey();

        try
        {
            assertTrue(_coalesceFramework.saveCoalesceEntity(_entity));
        }
        catch (CoalescePersistorException e)
        {

            if (e.getCause() != null)
            {
                System.out.println(e.getCause().getMessage());
            }
            else
            {
                System.out.println(e.getMessage());
            }
            
            // If entity fails to save halt the persister test.
            Assume.assumeNoException(e);
        }


    }

    @AfterClass
    public static void tearDownAfterClass()
    {
        AccumuloPersistorTest tester = new AccumuloPersistorTest();

        //CoalescePersistorBaseTest.tearDownAfterClassBase(tester);

    }
    @After
    public void tearDown() 
    {

    }
    @Override
    protected ServerConn getConnection()
    {
        ServerConn serCon = new ServerConn();

        // InCadence Settings
         // serCon.setServerName("localhost");
         //serCon.setServerName("127.0.0.1:5432");
         //serCon.setDatabase("CoalesceDatabase");
         serCon.setUser("root");
         serCon.setPassword("secret");
         //  MiniCluster Code for long term testing 
         if (accumulo == null) {
        	 System.err.println("Creating MiniCluster");
    		 
        	 try {
        		 // Hack to get the Junit to actually create the folder.
            	 tempDirectory.create();
        		 System.err.println("Tempdir"+tempDirectory.getRoot());
        		 
         		 accumulo = new MiniAccumuloCluster(tempDirectory.getRoot(), serCon.getPassword());
        		 accumulo.start();
        		 instance = new ZooKeeperInstance(accumulo.getInstanceName(), accumulo.getZooKeepers());
        	 } catch (Exception e) {
        		 System.err.println("Error Creating MiniCluster: " + e.getMessage());
        		 return null;
        	 }


         }
        if (accumulo==null) {
       	 System.err.println("MiniClusternot created");
        }
        serCon.setDatabase(accumulo.getInstanceName());
        serCon.setServerName(accumulo.getZooKeepers());
//
         /*  Temp code to connect to my VM */
 //        serCon.setDatabase("accumulodev");
//         serCon.setServerName("accumulodev");

        return serCon;

    }

    @Override
    protected ICoalescePersistor getPersistor(ServerConn conn)
    {
    	System.err.println("getPersistor test:"+conn.getDatabase());
        AccumuloPersistor accumuloPersistor = new AccumuloPersistor();
        accumuloPersistor.initialize(conn);
        //accumuloPersistor.setSchema("coalesce");

        return accumuloPersistor;

    }

    @Override
    protected CoalesceDataConnectorBase getDataConnector(ServerConn conn) throws CoalescePersistorException
    {
//    	System.err.println("getDataConnector test:"+conn.getDatabase());
        return new AccumuloDataConnector(conn);
    }
    
    @Test
    public void testConnection() throws CoalescePersistorException, Exception
    {

    	Map<String,String> sysconf = conn.instanceOperations().getSystemConfiguration();
    	assertNotNull(sysconf);
    	for (Map.Entry<String,String> entry : sysconf.entrySet()) {
    		System.out.println(entry.getKey() + ":" + entry.getValue());
    		
    	}

    }

}

