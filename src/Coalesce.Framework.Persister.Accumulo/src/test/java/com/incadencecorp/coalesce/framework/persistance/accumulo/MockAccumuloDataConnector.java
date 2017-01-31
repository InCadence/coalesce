package com.incadencecorp.coalesce.framework.persistance.accumulo;

import java.io.IOException;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.client.mock.MockInstance;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.geotools.data.DataStoreFinder;
import org.locationtech.geomesa.accumulo.data.AccumuloDataStore;
import org.locationtech.geomesa.accumulo.data.AccumuloDataStoreConfig;
import org.locationtech.geomesa.accumulo.data.stats.usage.ParamsAuditProvider;
import org.locationtech.geomesa.security.AuditProvider;
import org.locationtech.geomesa.security.AuthorizationsProvider;
import org.locationtech.geomesa.security.DefaultAuthorizationsProvider;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;

import scala.Option;
import scala.Some;

public class MockAccumuloDataConnector extends AccumuloDataConnector {

    public MockAccumuloDataConnector(ServerConn settings) throws CoalescePersistorException
    {
        super(settings);
    }

    @Override
    public Connector getDBConnector() throws CoalescePersistorException
    {

        try
        {

            if (connector == null)
            {
                System.err.println("AccumuloDataConnector:openDataConnection - connecting to accumulo");

                instance = new MockInstance(serverConnection.getServerName());
                connector = instance.getConnector("root", new PasswordToken(new byte[0]));

                System.err.println("AccumuloDataConnector:openDataConnection - Connector User" + connector.whoami());

                createTables(connector, coalesceTable, coalesceTemplateTable, coalesceEntityIndex, coalesceSearchTable);

                AuthorizationsProvider authProvider = new DefaultAuthorizationsProvider();

                AuditProvider auditProvider = new ParamsAuditProvider();

                String defaultVisibilities = "";

                Option<Object> queryTimeout = new Some<Object>(2000l);

                int queryThreads = 1;
                int recordThreads = 1;
                int writeThreads = 1;
                boolean generateStats = false;
                boolean collectUsageStats = false;
                boolean caching = false;
                boolean looseBBox = false;

                AccumuloDataStoreConfig config = new AccumuloDataStoreConfig(queryTimeout,
                                                                             queryThreads,
                                                                             recordThreads,
                                                                             writeThreads,
                                                                             generateStats,
                                                                             collectUsageStats,
                                                                             caching,
                                                                             looseBBox);

                dataStore = new AccumuloDataStore(connector,
                                                  coalesceSearchTable,
                                                  authProvider,
                                                  auditProvider,
                                                  defaultVisibilities,
                                                  config);

            }
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("Error Opening Data Connection", e);
        }

        return connector;
    }

}
