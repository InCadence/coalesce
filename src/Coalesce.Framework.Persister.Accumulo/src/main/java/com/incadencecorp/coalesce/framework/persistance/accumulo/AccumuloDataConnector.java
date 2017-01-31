package com.incadencecorp.coalesce.framework.persistance.accumulo;

import java.io.IOException;
// Imports to allow compilation of unused routines
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;

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
 * This is an initial Accumulo connector using MiniAccumuloCluster. This class is referenced examples in the Book:
 * Accumulo Application Development, Table design, and best practice
 */

public class AccumuloDataConnector extends CoalesceDataConnectorBase {

	protected static Instance instance;
	protected static Connector connector;

	// TODO These need to move to a constants or other common location
	public static String coalesceTable = "Coalesce";
	public static String coalesceTemplateTable = "CoalesceTemplates";
	public static String coalesceEntityIndex = "CoalesceEntityIndex";
	public static String coalesceSearchTable = "CoalesceSearch";

	// These variables are for connecting to GeoMesa for the search
	protected static Map<String, String> dsConf = new HashMap<String, String>();
	protected static DataStore dataStore;

	static final String INSTANCE_ID = "instanceId";
	static final String ZOOKEEPERS = "zookeepers";
	static final String USER = "user";
	static final String PASSWORD = "password";
	static final String AUTHS = "auths";
	static final String TABLE_NAME = "tableName";

	protected final ServerConn serverConnection;

	public AccumuloDataConnector(ServerConn settings) throws CoalescePersistorException {
		serverConnection = new ServerConn.Builder().copyOf(settings).build();

		// Build the map for Geotools style connection from the connection information
		dsConf.put(INSTANCE_ID, settings.getDatabase());
		dsConf.put(ZOOKEEPERS, settings.getServerName());
		dsConf.put(USER, settings.getUser());
		dsConf.put(PASSWORD, settings.getPassword());
		dsConf.put(TABLE_NAME, coalesceSearchTable);
		dsConf.put(AUTHS, ""); // Auths will be empty for now
		
		// Set system properties for GeomesaBatchWriter
		Properties props = System.getProperties();
		props.setProperty("geomesa.batchwriter.latency.millis", "1000");
		props.setProperty("geomesa.batchwriter.maxthreads", "10");
		props.setProperty("geomesa.batchwriter.memory","52428800");
		
		
	}

	// @Override
	public Connector getDBConnector() throws CoalescePersistorException {
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

	public DataStore getGeoDataStore() {
		return dataStore;
	}

	@Override
	public void openConnection(boolean autocommit) {
		System.err.println("AccumuloDataConnector:OpenConnection: Procedure not implemented");
		throw new UnsupportedOperationException("AccumuloDataConnector:OpenConnection: Procedure not implemented");
	}

	@Override
	protected String getProcedurePrefix() {
		return "";
	}

	@Override
	public Connection getDBConnection() throws SQLException {
		System.err.println("AccumuloDataConnector:getDBConnection: Procedure not implemented");
		throw new UnsupportedOperationException("AccumuloDataConnector:getDBConnection: Procedure not implemented");
	}

	/*-----------------------------------------------------------------------------'
	Private Functions
	-----------------------------------------------------------------------------*/

	protected void createTables(Connector connector, String... tableNames)
			throws AccumuloException, AccumuloSecurityException {
		if (connector != null) {
			for (String table : tableNames) {
				if (!connector.tableOperations().exists(table)) {
					try {
						connector.tableOperations().create(table);
					} catch (TableExistsException e) {
						// Shouldn't happen because we just checked that it didn't exist.
					}
				}
			}
		}
	}

	protected void openDataConnection()
			throws IOException, InterruptedException, AccumuloException, AccumuloSecurityException {
		// TODO Add try catch for appropriate exceptions
		if (connector == null) {
			System.err.println("AccumuloDataConnector:openDataConnection - connecting to accumulo");

			instance = new ZooKeeperInstance(serverConnection.getDatabase(), serverConnection.getServerName());
			connector = instance.getConnector(serverConnection.getUser(),
					new PasswordToken(serverConnection.getPassword()));
			System.err.println("AccumuloDataConnector:openDataConnection - Connector User" + connector.whoami());

			createTables(connector, coalesceTable, coalesceTemplateTable, coalesceEntityIndex, coalesceSearchTable);

			// Now set up the GeoMesa connection verify that we can see this
			// Accumulo destination in a GeoTools manner

			dataStore = DataStoreFinder.getDataStore(dsConf);
		}
	}

}
