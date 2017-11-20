package com.incadencecorp.coalesce.framework.persistance.accumulo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Properties;

import com.incadencecorp.unity.common.connectors.FilePropertyConnector;
import org.apache.accumulo.core.client.Connector;
import org.geotools.data.DataStore;
import org.junit.BeforeClass;
import org.junit.Test;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;

public class AccumuloDataConnectorIT {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        FilePropertyConnector connector = new FilePropertyConnector(Paths.get("src", "test", "resources"));
        connector.setReadOnly(true);

        AccumuloSettings.setConnector(connector);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetDBConnection() throws CoalescePersistorException, SQLException
    {
        AccumuloDataConnector accumuloDataConnector = new AccumuloDataConnector(AccumuloSettings.getServerConn());
        accumuloDataConnector.getDBConnection();
        accumuloDataConnector.close();
    }

    @Test
    public void testGetProcedurePrefix() throws CoalescePersistorException
    {
        AccumuloDataConnector accumuloDataConnector = new AccumuloDataConnector(AccumuloSettings.getServerConn());
        assertEquals("", accumuloDataConnector.getProcedurePrefix());
        accumuloDataConnector.close();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testOpenConnection() throws CoalescePersistorException
    {
        AccumuloDataConnector accumuloDataConnector = new AccumuloDataConnector(AccumuloSettings.getServerConn());
        accumuloDataConnector.openConnection(true);
        accumuloDataConnector.close();
    }

    @Test
    public void testGetDBConnector() throws CoalescePersistorException
    {
        AccumuloDataConnector accumuloDataConnector = new AccumuloDataConnector(AccumuloSettings.getServerConn());
        Connector dbConnector = accumuloDataConnector.getDBConnector();
        assertNotNull(dbConnector);
        accumuloDataConnector.close();
    }

    @Test
    public void testGetGeoDataStore() throws CoalescePersistorException
    {
        AccumuloDataConnector accumuloDataConnector = new AccumuloDataConnector(AccumuloSettings.getServerConn());
        DataStore dataStore = accumuloDataConnector.getGeoDataStore();
        assertNotNull(dataStore);
        accumuloDataConnector.close();
    }

}
