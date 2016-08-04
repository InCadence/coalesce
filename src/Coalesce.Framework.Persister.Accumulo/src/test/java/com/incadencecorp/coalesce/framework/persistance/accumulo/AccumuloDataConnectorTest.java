package com.incadencecorp.coalesce.framework.persistance.accumulo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.nio.file.Files;
import java.sql.SQLException;

import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.minicluster.MiniAccumuloCluster;
import org.geotools.data.DataStore;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;

public class AccumuloDataConnectorTest {

	private static MiniAccumuloCluster accumulo = null;

	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		File tempDirectory = Files.createTempDirectory("accTemp").toFile();
		accumulo = new MiniAccumuloCluster(tempDirectory, "password");
		accumulo.start();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
        accumulo.stop();
        //TODO clean up tempDirectory and files created.
	}

	@Test (expected=UnsupportedOperationException.class)
	public void testGetDBConnection() throws CoalescePersistorException, SQLException {
		String name = accumulo.getInstanceName();
		String zookeepers = accumulo.getZooKeepers();
		ServerConn serverConnection = new ServerConn.Builder().db(name).serverName(zookeepers).user("root").password("password").build();
		AccumuloDataConnector accumuloDataConnector = new AccumuloDataConnector(serverConnection);
		accumuloDataConnector.getDBConnection();
		accumuloDataConnector.close();
	}

	@Test
	public void testGetProcedurePrefix() throws CoalescePersistorException {
		String name = accumulo.getInstanceName();
		String zookeepers = accumulo.getZooKeepers();
		ServerConn serverConnection = new ServerConn.Builder().db(name).serverName(zookeepers).user("root").password("password").build();
		AccumuloDataConnector accumuloDataConnector = new AccumuloDataConnector(serverConnection);
	    assertEquals("",accumuloDataConnector.getProcedurePrefix());
	    accumuloDataConnector.close();
	}

	@Test (expected=UnsupportedOperationException.class)
	public void testOpenConnection() throws CoalescePersistorException {
		String name = accumulo.getInstanceName();
		String zookeepers = accumulo.getZooKeepers();
		ServerConn serverConnection = new ServerConn.Builder().db(name).serverName(zookeepers).user("root").password("password").build();
		AccumuloDataConnector accumuloDataConnector = new AccumuloDataConnector(serverConnection);
	    accumuloDataConnector.openConnection(true);
	    accumuloDataConnector.close();
	}

	@Test 
	public void testGetDBConnector() throws CoalescePersistorException {
		String name = accumulo.getInstanceName();
		String zookeepers = accumulo.getZooKeepers();
		ServerConn serverConnection = new ServerConn.Builder().db(name).serverName(zookeepers).user("root").password("password").build();
		AccumuloDataConnector accumuloDataConnector = new AccumuloDataConnector(serverConnection);
		Connector dbConnector = accumuloDataConnector.getDBConnector();
		assertNotNull(dbConnector);
		accumuloDataConnector.close();
	}

	@Test
	public void testGetGeoDataStore() throws CoalescePersistorException {
		String name = accumulo.getInstanceName();
		String zookeepers = accumulo.getZooKeepers();
		ServerConn serverConnection = new ServerConn.Builder().db(name).serverName(zookeepers).user("root").password("password").build();
		AccumuloDataConnector accumuloDataConnector = new AccumuloDataConnector(serverConnection);
		DataStore dataStore = accumuloDataConnector.getGeoDataStore();
		assertNotNull(dataStore);
		accumuloDataConnector.close();
	}

}
