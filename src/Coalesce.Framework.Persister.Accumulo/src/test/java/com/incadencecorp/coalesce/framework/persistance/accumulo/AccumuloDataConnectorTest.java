package com.incadencecorp.coalesce.framework.persistance.accumulo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;

import org.apache.accumulo.core.client.Connector;
import org.geotools.data.DataStore;
import org.junit.Test;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;

public class AccumuloDataConnectorTest {

    private static final String ZOOKEEPERS = "zookeepers";
    private static final String NAME = "name";
    private static final String USERNAME = "unittest";
    private static final String PASSWORD = "unittest";

    @Test(expected = UnsupportedOperationException.class)
    public void testGetDBConnection() throws CoalescePersistorException, SQLException
    {
        ServerConn serverConnection = new ServerConn.Builder().db(NAME).serverName(ZOOKEEPERS).user(USERNAME).password(PASSWORD).build();
        MockAccumuloDataConnector accumuloDataConnector = new MockAccumuloDataConnector(serverConnection);
        accumuloDataConnector.getDBConnection();
        accumuloDataConnector.close();
    }

    @Test
    public void testGetProcedurePrefix() throws CoalescePersistorException
    {
        ServerConn serverConnection = new ServerConn.Builder().db(NAME).serverName(ZOOKEEPERS).user(USERNAME).password(PASSWORD).build();
        MockAccumuloDataConnector accumuloDataConnector = new MockAccumuloDataConnector(serverConnection);
        assertEquals("", accumuloDataConnector.getProcedurePrefix());
        accumuloDataConnector.close();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testOpenConnection() throws CoalescePersistorException
    {
        ServerConn serverConnection = new ServerConn.Builder().db(NAME).serverName(ZOOKEEPERS).user(USERNAME).password(PASSWORD).build();
        MockAccumuloDataConnector accumuloDataConnector = new MockAccumuloDataConnector(serverConnection);
        accumuloDataConnector.openConnection(true);
        accumuloDataConnector.close();
    }

    @Test
    public void testGetDBConnector() throws CoalescePersistorException
    {
        ServerConn serverConnection = new ServerConn.Builder().db(NAME).serverName(ZOOKEEPERS).user(USERNAME).password(PASSWORD).build();
        MockAccumuloDataConnector accumuloDataConnector = new MockAccumuloDataConnector(serverConnection);
        Connector dbConnector = accumuloDataConnector.getDBConnector();
        assertNotNull(dbConnector);
        accumuloDataConnector.close();
    }

    @Test
    public void testGetGeoDataStore() throws CoalescePersistorException
    {
        ServerConn serverConnection = new ServerConn.Builder().db(NAME).serverName(ZOOKEEPERS).user(USERNAME).password(PASSWORD).build();
        MockAccumuloDataConnector accumuloDataConnector = new MockAccumuloDataConnector(serverConnection);
        DataStore dataStore = accumuloDataConnector.getGeoDataStore();
        assertNotNull(dataStore);
        accumuloDataConnector.close();
    }

}
