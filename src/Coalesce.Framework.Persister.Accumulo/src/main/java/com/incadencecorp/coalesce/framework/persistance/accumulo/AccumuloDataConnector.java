package com.incadencecorp.coalesce.framework.persistance.accumulo;

import java.io.File;
import java.io.IOException;
import java.lang.RuntimeException;

import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.client.ZooKeeperInstance;

import com.google.common.io.Files;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;

// Imports to allow compilation of unused routines
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;



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
* @author David Boyd
* May 13, 2016
*/

/**
* This is an initial Accumulo connector using MiniAccumuloCluster. This class is referenced examples in the
* Book: Accumulo Application Development, Table design, and best practice 
*/

public class AccumuloDataConnector extends CoalesceDataConnectorBase {

    private static String _instancename;
    private static String _zookeepers;
    private static String _username;
    private static String _password;
    private static Instance instance;
    private static Connector connector;
    
    //TODO These need to move to a constants or other common location
    public static String coalesceTable = "Coalesce";
    public static String coalesceTemplateTable = "CoalesceTemplates";
    public static String coalesceEntityIndex = "CoalesceEntityIndex";
    public static String coalesceSearchTable = "CoalesceSearch";
    
    // These variables are for connecting to GeoMesa for the search
    private static Map<String,String> dsConf = new HashMap<String,String>();
    private static DataStore dataStore;
    
	static final String INSTANCE_ID = "instanceId";
	static final String ZOOKEEPERS = "zookeepers";
	static final String USER = "user";
	static final String PASSWORD = "password";
	static final String AUTHS = "auths";
	static final String TABLE_NAME = "tableName";

    public AccumuloDataConnector(ServerConn settings) throws CoalescePersistorException
    {
        _instancename = settings.getDatabase();
        _zookeepers = settings.getServerName();
        _username = settings.getUser();
        _password = settings.getPassword();

    	// Build the map for Geotools style connection from the connection information
        dsConf.put(INSTANCE_ID, _instancename);
        dsConf.put(ZOOKEEPERS, _zookeepers);
        dsConf.put(USER, _username);
        dsConf.put(PASSWORD, _password);
        dsConf.put(TABLE_NAME, coalesceSearchTable);
        dsConf.put(AUTHS, ""); //Auths will be empty for now
    }

     
 //   @Override
    public static Connector getDBConnector() throws CoalescePersistorException {
    	try {
    		openDataConnection();
    	} catch (IOException e) {
    		throw new CoalescePersistorException("Error Opening Data Connection - IOException", e);
    	} catch (InterruptedException e) {
    		throw new CoalescePersistorException("Error Opening Data Connection - InterruptedException", e);
    	} catch (AccumuloException e) {
    		throw new CoalescePersistorException("Error Opening Data Connection - AccumuloException", e);
    	} catch (AccumuloSecurityException e) {
    		throw new CoalescePersistorException("Error Opening Data Connection - AccumuloSecurityException", e);
    	}
    	return connector;
    }
    
    public static DataStore getGeoDataStore() {
    	return dataStore;
    }
    
    @Override
    public void openConnection(boolean autocommit) 
    {
        System.err.println("AccumuloDataConnector:OpenConnection: Procedure not implemented");
    }

    
    @Override
    protected String getProcedurePrefix()
    {
    	return "";
    }
    
    @Override
    public Connection getDBConnection() throws SQLException
    {
   
        System.err.println("AccumuloDataConnector:getDBConnection: Procedure not implemented");

        return null;
    }

    /*-----------------------------------------------------------------------------'
    Private Functions
    -----------------------------------------------------------------------------*/

    private static void openDataConnection() throws IOException, InterruptedException, AccumuloException, AccumuloSecurityException
    {
    	   //TODO Add try catch for appropriate exceptions
    	if (connector == null) {
            System.err.println("AccumuloDataConnector:openDataConnection - connecting to accumulo");

			instance = new ZooKeeperInstance(_instancename, _zookeepers);
			connector = instance.getConnector(_username, new PasswordToken(_password));
			System.err.println("AccumuloDataConnector:openDataConnection - Connector User"+connector.whoami());
			
			// Make sure the Coalesce table exists first time
	    	if(!connector.tableOperations().exists(coalesceTable)) {
				System.err.println("creating table " + coalesceTable);
				try {
					connector.tableOperations().create(coalesceTable);
				} catch (TableExistsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.err.println("created table " + coalesceTable);
			}
	    	
			// Make sure the CoalesceTemplates table exists first time
	    	if(!connector.tableOperations().exists(coalesceTemplateTable)) {
				System.err.println("creating table " + coalesceTemplateTable);
				try {
					connector.tableOperations().create(coalesceTemplateTable);
				} catch (TableExistsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.err.println("created table " + coalesceTemplateTable);
			}
			// Make sure the CoalesceEntityIndex table exists first time
	    	if(!connector.tableOperations().exists(coalesceEntityIndex)) {
				System.err.println("creating table " + coalesceEntityIndex);
				try {
					connector.tableOperations().create(coalesceEntityIndex);
				} catch (TableExistsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.err.println("created table " + coalesceEntityIndex);
				
		
			}
			// Make sure the CoalesceSearch table exists first time
	    	if(!connector.tableOperations().exists(coalesceSearchTable)) {
				System.err.println("creating table " + coalesceSearchTable);
				try {
					connector.tableOperations().create(coalesceSearchTable);
				} catch (TableExistsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.err.println("created table " + coalesceSearchTable);
				
		
			}
	    	
	    	// Now set up the GeoMesa connection
	        // verify that we can see this Accumulo destination in a GeoTools manner
	    	
	        dataStore = DataStoreFinder.getDataStore(dsConf);

        }
    }

}
 


