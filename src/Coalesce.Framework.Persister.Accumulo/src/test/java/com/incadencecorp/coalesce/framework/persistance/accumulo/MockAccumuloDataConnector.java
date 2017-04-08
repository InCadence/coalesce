package com.incadencecorp.coalesce.framework.persistance.accumulo;

import java.util.HashMap;
import java.util.Map;

import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.mock.MockInstance;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;



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

                System.err.println("MockAccumuloDataConnector:openDataConnection - Connector User" + connector.whoami());

                createTables(connector, coalesceTable, coalesceTemplateTable, coalesceEntityIndex, coalesceSearchTable);
                
                System.err.println("MockAccumuloDataConnector:Created Tables");

                Map<String, String> parameters = new HashMap<>();
                parameters.put("instanceId", serverConnection.getDatabase());
                parameters.put("zookeepers", serverConnection.getServerName());
                parameters.put("user", serverConnection.getUser());
                parameters.put("password", serverConnection.getPassword());
                parameters.put("tableName", coalesceSearchTable);
                parameters.put("queryThreads","1");
                parameters.put("recordThreads","1");
                parameters.put("writeThreads","1");
                parameters.put("generateStats","false");
                parameters.put("collectUsageStats","false");
                parameters.put("caching","false");
                parameters.put("looseBBox","false");
                parameters.put("useMock","true");

 
                dataStore = DataStoreFinder.getDataStore(parameters);
                System.err.println("MockAccumuloDataConnector:Got Data Connector");
               
                /*
                AuthorizationsProvider authProvider = new DefaultAuthorizationsProvider();

                AuditProvider auditProvider = new ParamsAuditProvider();
               AuditWriter auditWriter = new AuditWriter();
                
                Option<Tuple3<AuditWriter, AuditProvider, String>> auditOptions = 
                	{{ auditWriter, auditProvider, ""}};
                
                 String defaultVisibilities = "";

                Option<Object> queryTimeout = new Some<Object>(2000l);

                int queryThreads = 1;
                int recordThreads = 1;
                int writeThreads = 1;
                boolean generateStats = false;
                boolean collectUsageStats = false;
                boolean caching = false;
                boolean looseBBox = false;
 
                
                AccumuloDataStoreConfig config = new AccumuloDataStoreConfig(coalesceSearchTable,
                															 defaultVisibilities, 
                															 true,
                															 authProvider,
                															  auditOptions,
                															 queryTimeout,
                															 looseBBox,
                															 caching,
                															 writeThreads,
                															 queryThreads,
                                                                             recordThreads);

                dataStore = new AccumuloDataStore(connector,
                                                   config);
*/
            }
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException(e.getMessage(), e);
        }

        return connector;
    }

}
