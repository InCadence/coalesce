/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.framework.persistance.accumulo;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;
import org.apache.accumulo.core.client.Connector;
import org.geotools.data.DataStore;
import org.junit.Test;

import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Derek Clemenzi
 */
public class AccumuloDataConnectorIT {

    private Map<String, String> parameters = new HashMap<>();

    public AccumuloDataConnectorIT()
    {
        FilePropertyConnector connector = new FilePropertyConnector(Paths.get("src", "test", "resources"));
        connector.setReadOnly(true);

        AccumuloSettings.setConnector(connector);

        parameters = getParameters();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetDBConnection() throws CoalescePersistorException, SQLException
    {
        AccumuloDataConnector accumuloDataConnector = new AccumuloDataConnector(parameters);
        accumuloDataConnector.getDBConnection();
        accumuloDataConnector.close();
    }

    @Test
    public void testGetProcedurePrefix() throws CoalescePersistorException
    {
        AccumuloDataConnector accumuloDataConnector = new AccumuloDataConnector(parameters);
        assertEquals("", accumuloDataConnector.getProcedurePrefix());
        accumuloDataConnector.close();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testOpenConnection() throws CoalescePersistorException
    {
        AccumuloDataConnector accumuloDataConnector = new AccumuloDataConnector(parameters);
        accumuloDataConnector.openConnection(true);
        accumuloDataConnector.close();
    }

    @Test
    public void testGetDBConnector() throws CoalescePersistorException
    {
        AccumuloDataConnector accumuloDataConnector = new AccumuloDataConnector(parameters);
        Connector dbConnector = accumuloDataConnector.getDBConnector();
        assertNotNull(dbConnector);
        accumuloDataConnector.close();
    }

    @Test
    public void testGetGeoDataStore() throws CoalescePersistorException
    {
        AccumuloDataConnector accumuloDataConnector = new AccumuloDataConnector(parameters);
        DataStore dataStore = accumuloDataConnector.getGeoDataStore();
        assertNotNull(dataStore);
        accumuloDataConnector.close();
    }

    protected Map<String, String> getParameters()
    {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(AccumuloDataConnector.INSTANCE_ID, AccumuloSettings.getDatabaseName());
        parameters.put(AccumuloDataConnector.ZOOKEEPERS, AccumuloSettings.getZookeepers());
        parameters.put(AccumuloDataConnector.USER, AccumuloSettings.getUserName());
        parameters.put(AccumuloDataConnector.PASSWORD, AccumuloSettings.getUserPassword());
        parameters.put(AccumuloDataConnector.TABLE_NAME, AccumuloDataConnector.COALESCE_SEARCH_TABLE);
        parameters.put(AccumuloDataConnector.QUERY_THREADS, "1");
        parameters.put(AccumuloDataConnector.RECORD_THREADS, "1");
        parameters.put(AccumuloDataConnector.WRITE_THREADS, "1");
        parameters.put(AccumuloDataConnector.GENERATE_STATS, "false");
        parameters.put(AccumuloDataConnector.COLLECT_USAGE_STATS, "false");
        parameters.put(AccumuloDataConnector.CACHING, "false");
        parameters.put(AccumuloDataConnector.LOOSE_B_BOX, "false");

        return parameters;
    }
}
