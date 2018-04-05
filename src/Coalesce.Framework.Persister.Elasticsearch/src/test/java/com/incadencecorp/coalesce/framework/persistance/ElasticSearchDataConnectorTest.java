package com.incadencecorp.coalesce.framework.persistance;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchDataConnector;

import ironhide.client.IronhideClient;

import org.elasticsearch.client.transport.TransportClient;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

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
    public void testGetDBConnection() throws Exception
    {
        ElasticSearchDataConnector connector = new ElasticSearchDataConnector();
        TransportClient client = connector.getDBConnector();
        connector.close();
        client.close();
    }

    @Test
	public void testIronhideConnection() throws Exception 
    {
        IronhideClient client = null;
        ElasticSearchDataConnector connector = new ElasticSearchDataConnector();
        InputStream in = ElasticSearchDataConnector.class.getResourceAsStream("/elasticsearch-config.properties");
        Properties props = new Properties();
        props.load(in);
        in.close();
        
    	client = connector.connectElasticSearch(props);
    	assertNotNull(client);
        connector.close();
	}
}
