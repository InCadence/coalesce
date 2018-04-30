package com.incadencecorp.coalesce.framework.persistance;

import com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchDataConnector;
import com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchSettings;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;
import ironhide.client.IronhideClient;
import org.elasticsearch.client.support.AbstractClient;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;

public class ElasticSearchDataConnectorIT {

    private static final String NAME = "name";
    private static ServerConn conn;
    private Map<String, String> parameters = new HashMap<>();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        FilePropertyConnector connector = new FilePropertyConnector(Paths.get("src", "test", "resources"));
        connector.setReadOnly(true);

        ElasticSearchSettings.setConnector(connector);

        Properties props = new Properties();
        props.putAll(ElasticSearchSettings.getParameters());

        String dbName = props.getProperty("database");
        String zookeepers = props.getProperty("zookeepers");
        String user = props.getProperty("userid");
        String password = props.getProperty("password");
        conn = new ServerConn.Builder().db(dbName).serverName(zookeepers).user(user).password(password).build();
    }

    @Test
    public void testConnector()
    {
        FilePropertyConnector connector = new FilePropertyConnector(Paths.get("src", "test", "resources"));
        connector.setReadOnly(true);

        ElasticSearchSettings.setConnector(connector);
    }

    @Test
    public void testGetDBConnection() throws Exception
    {
        ElasticSearchDataConnector connector = new ElasticSearchDataConnector();

        Properties props = new Properties();
        props.putAll(ElasticSearchSettings.getParameters());

        AbstractClient client = connector.getDBConnector(props);
        connector.close();
        client.close();
    }

    @Test
    public void testIronhideConnection() throws Exception
    {
        AbstractClient client = null;
        ElasticSearchDataConnector connector = new ElasticSearchDataConnector();
        FilePropertyConnector fileConnector = new FilePropertyConnector(Paths.get("src", "test", "resources"));
        fileConnector.setReadOnly(true);

        ElasticSearchSettings.setConnector(fileConnector);

        Properties props = new Properties();
        props.putAll(ElasticSearchSettings.getParameters());

        client = connector.getDBConnector(props);
        assertNotNull(client);
        connector.close();
    }
}
