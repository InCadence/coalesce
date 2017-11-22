package com.incadencecorp.coalesce.framework.persistance.accumulo;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;
import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.client.mock.MockInstance;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

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
 * This is an initial Accumulo connector using MiniAccumuloCluster. This class is referenced examples in the Book:
 * Accumulo Application Development, Table design, and best practice
 *
 * @author David Boyd
 * May 13, 2016
 */
public class AccumuloDataConnector extends CoalesceDataConnectorBase implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloDataConnector.class);

    // TODO These need to move to a constants or other common location
    public static final String COALESCE_ENTITY_TABLE = "Coalesce";
    public static final String COALESCE_TEMPLATE_TABLE = "CoalesceTemplates";
    public static final String COALESCE_ENTITY_IDX_TABLE = "CoalesceEntityIndex";
    public static final String COALESCE_SEARCH_TABLE = "CoalesceSearch";
    public static final String LINKAGE_FEATURE_NAME = "coalescelinkage";
    public static final String ENTITY_FEATURE_NAME = "coalesceentity";

    // Datastore Properties
    public static final String INSTANCE_ID = "instanceId";
    public static final String ZOOKEEPERS = "zookeepers";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String TABLE_NAME = "tableName";
    public static final String QUERY_THREADS = "queryThreads";
    public static final String RECORD_THREADS = "recordThreads";
    public static final String WRITE_THREADS = "writeThreads";
    public static final String GENERATE_STATS = "generateStats";
    public static final String COLLECT_USAGE_STATS = "collectUsageStats";
    public static final String CACHING = "caching";
    public static final String LOOSE_B_BOX = "looseBBox";
    public static final String USE_MOCK = "useMock";
    public static final String AUTHS = "auths";

    // These variables are for connecting to GeoMesa for the search
    private Map<String, String> dsConf = new HashMap<String, String>();
    private DataStore dataStore;
    private Instance instance;
    private Connector connector;

    public AccumuloDataConnector(Map<String, String> params) throws CoalescePersistorException
    {
        dsConf.putAll(params);

        // Set system properties for GeomesaBatchWriter
        Properties props = System.getProperties();
        props.setProperty("geomesa.batchwriter.latency.millis", "1000");
        props.setProperty("geomesa.batchwriter.maxthreads", "10");
        props.setProperty("geomesa.batchwriter.memory", "52428800");

        //populate datastore and Connector fields
        getDBConnector();
    }

    /**
     * @param settings spec
     * @throws CoalescePersistorException on error
     */
    public AccumuloDataConnector(ServerConn settings) throws CoalescePersistorException
    {
        this(settings, null);
    }

    /**
     * @param settings spec
     * @param params   additional parameters to creating the datastore.
     * @throws CoalescePersistorException on error
     */
    public AccumuloDataConnector(ServerConn settings, Map<String, String> params) throws CoalescePersistorException
    {
        if (params != null)
        {
            dsConf.putAll(params);
            dsConf.put(INSTANCE_ID, settings.getDatabase());
            dsConf.put(ZOOKEEPERS, settings.getServerName());
            dsConf.put(USER, settings.getUser());
            dsConf.put(PASSWORD, settings.getPassword());
        }
        else
        {
            dsConf.put(INSTANCE_ID, settings.getDatabase());
            dsConf.put(ZOOKEEPERS, settings.getServerName());
            dsConf.put(USER, settings.getUser());
            dsConf.put(PASSWORD, settings.getPassword());
            dsConf.put(TABLE_NAME, COALESCE_SEARCH_TABLE);
            dsConf.put(AUTHS, ""); // Auths will be empty for now
        }

        LOGGER.debug("Geotools DS conf:  Instance: {}  Zookeepers: {}, User: {}",
                     settings.getDatabase(),
                     settings.getServerName(),
                     settings.getUser());

        // Set system properties for GeomesaBatchWriter
        Properties props = System.getProperties();
        props.setProperty("geomesa.batchwriter.latency.millis", "1000");
        props.setProperty("geomesa.batchwriter.maxthreads", "10");
        props.setProperty("geomesa.batchwriter.memory", "52428800");

        //populate datastore and Connector fields
        getDBConnector();
    }

    protected Connector getDBConnector() throws CoalescePersistorException
    {
        openDataConnection();
        return connector;
    }

    /**
     * @return the datastore used by this connector
     */
    public DataStore getGeoDataStore()
    {
        return dataStore;
    }

    @Override
    public void openConnection(boolean autocommit)
    {
        LOGGER.error("AccumuloDataConnector:OpenConnection: Procedure not implemented");
        throw new UnsupportedOperationException("AccumuloDataConnector:OpenConnection: Procedure not implemented");
    }

    @Override
    protected String getProcedurePrefix()
    {
        return "";
    }

    @Override
    public Connection getDBConnection() throws SQLException
    {
        LOGGER.error("AccumuloDataConnector:getDBConnection: Procedure not implemented");
        throw new UnsupportedOperationException("AccumuloDataConnector:getDBConnection: Procedure not implemented");
    }

	/*-----------------------------------------------------------------------------'
    Private Functions
	-----------------------------------------------------------------------------*/

    protected void createTables(String... tableNames) throws AccumuloException, AccumuloSecurityException
    {
        if (connector != null)
        {
            for (String table : tableNames)
            {
                if (!connector.tableOperations().exists(table))
                {
                    try
                    {
                        connector.tableOperations().create(table);
                    }
                    catch (TableExistsException e)
                    {
                        throw new AccumuloException(String.format(CoalesceErrors.NOT_FOUND, "Table", "table"), e);
                    }
                }
            }
        }
    }

    private void openDataConnection() throws CoalescePersistorException
    {
        if (connector == null)
        {
            try
            {
                LOGGER.info("Connecting: Instance: {}  Zookeepers: {}", dsConf.get(INSTANCE_ID), dsConf.get(ZOOKEEPERS));

                // Use a Mock Instance?
                if (Boolean.parseBoolean(dsConf.get(USE_MOCK)))
                {
                    LOGGER.warn("Using Mock Instance");
                    instance = new MockInstance(dsConf.get(ZOOKEEPERS));
                }
                else
                {
                    instance = new ZooKeeperInstance(dsConf.get(INSTANCE_ID), dsConf.get(ZOOKEEPERS));
                }

                connector = instance.getConnector(dsConf.get(USER), new PasswordToken(dsConf.get(PASSWORD)));

                createTables(COALESCE_ENTITY_TABLE,
                             COALESCE_TEMPLATE_TABLE,
                             COALESCE_ENTITY_IDX_TABLE,
                             COALESCE_SEARCH_TABLE);

                Iterator<DataStoreFactorySpi> availableStores = DataStoreFinder.getAvailableDataStores();

                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("List Available Stores:");
                    while (availableStores.hasNext())
                    {
                        LOGGER.debug("\t{}", availableStores.next().toString());
                    }
                }

                // Now set up the GeoMesa connection verify that we can see this
                // Accumulo destination in a GeoTools manner

                dataStore = DataStoreFinder.getDataStore(dsConf);
                if (dataStore == null)
                {
                    LOGGER.error("Geomesa Accumulo Datastore not found in Factory.  Check classpath. DSConf values below");
                    Iterator<Entry<String, String>> it = dsConf.entrySet().iterator();
                    while (it.hasNext())
                    {
                        Map.Entry<String, String> pair = (Map.Entry<String, String>) it.next();
                        LOGGER.error(pair.getKey() + " = " + pair.getValue());
                        it.remove(); // avoids a ConcurrentModificationException
                    }
                    throw new CoalescePersistorException("Geomesa Accumulo Datastore not found in Factory.  Check classpath");
                }
            }
            catch (IOException | AccumuloException | AccumuloSecurityException e)
            {
                throw new CoalescePersistorException("Error Opening Data Connection", e);
            }
        }
    }

}
