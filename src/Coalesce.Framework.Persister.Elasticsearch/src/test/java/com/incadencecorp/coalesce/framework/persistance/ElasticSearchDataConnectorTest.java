package com.incadencecorp.coalesce.framework.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.geotools.data.DataStore;
import org.junit.BeforeClass;
import org.junit.Test;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;
import com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchDataConnector;

public class ElasticSearchDataConnectorTest {

    private static final String NAME = "name";
    private static ServerConn conn;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        InputStream in = ElasticSearchDataConnector.class.getResourceAsStream("/elasticsearch-config.properties");
        Properties props = new Properties();
        props.load(in);
        in.close();
        String dbName = props.getProperty("database");
        String zookeepers = props.getProperty("zookeepers");
        String user = props.getProperty("userid");
        String password = props.getProperty("password");
        conn = new ServerConn.Builder().db(dbName).serverName(zookeepers).user(user).password(password).build();
    }

    @Test
    public static void testGetDBConnection() throws CoalescePersistorException, SQLException
    {
    	ElasticSearchDataConnector connector = new ElasticSearchDataConnector("ESearch");
    	TransportClient client = connector.getDBConnector();
    	connector.close();
    	client.close();
    }

}
