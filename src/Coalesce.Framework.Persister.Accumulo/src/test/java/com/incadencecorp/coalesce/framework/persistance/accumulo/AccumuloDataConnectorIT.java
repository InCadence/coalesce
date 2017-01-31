package com.incadencecorp.coalesce.framework.persistance.accumulo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.accumulo.core.client.Connector;
import org.geotools.data.DataStore;
import org.junit.BeforeClass;
import org.junit.Test;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;

public class AccumuloDataConnectorIT {

    private static ServerConn conn;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        InputStream in = AccumuloDataConnectorIT.class.getResourceAsStream("/accumuloConnectionInfo.properties");
        Properties props = new Properties();
        props.load(in);
        in.close();
        String dbName = props.getProperty("database");
        String zookeepers = props.getProperty("zookeepers");
        String user = props.getProperty("userid");
        String password = props.getProperty("password");
        conn = new ServerConn.Builder().db(dbName).serverName(zookeepers).user(user).password(password).build();
    }

    protected ServerConn getConnection()
    {
        return conn;
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetDBConnection() throws CoalescePersistorException, SQLException
    {
        AccumuloDataConnector accumuloDataConnector = new AccumuloDataConnector(getConnection());
        accumuloDataConnector.getDBConnection();
        accumuloDataConnector.close();
    }

    @Test
    public void testGetProcedurePrefix() throws CoalescePersistorException
    {
        AccumuloDataConnector accumuloDataConnector = new AccumuloDataConnector(getConnection());
        assertEquals("", accumuloDataConnector.getProcedurePrefix());
        accumuloDataConnector.close();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testOpenConnection() throws CoalescePersistorException
    {
        AccumuloDataConnector accumuloDataConnector = new AccumuloDataConnector(getConnection());
        accumuloDataConnector.openConnection(true);
        accumuloDataConnector.close();
    }

    @Test
    public void testGetDBConnector() throws CoalescePersistorException
    {
        AccumuloDataConnector accumuloDataConnector = new AccumuloDataConnector(getConnection());
        Connector dbConnector = accumuloDataConnector.getDBConnector();
        assertNotNull(dbConnector);
        accumuloDataConnector.close();
    }

    @Test
    public void testGetGeoDataStore() throws CoalescePersistorException
    {
        AccumuloDataConnector accumuloDataConnector = new AccumuloDataConnector(getConnection());
        DataStore dataStore = accumuloDataConnector.getGeoDataStore();
        assertNotNull(dataStore);
        accumuloDataConnector.close();
    }

}
