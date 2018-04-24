package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import com.google.common.net.HostAndPort;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import ironhide.client.IronhideClient;
import ironhide.client.IronhideClient.Builder;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.support.AbstractClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.stream.Stream;

public class ElasticSearchDataConnector extends CoalesceDataConnectorBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchDataConnector.class);

    // Datastore Properties
    public static final String INSTANCE_ID = "instanceId";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public Connection getDBConnection() throws SQLException
    {
        LOGGER.error("ElasticSearchDataConnector:getDBConnection: Procedure not implemented");
        throw new UnsupportedOperationException("ElasticSearchDataConnector:getDBConnection: Procedure not implemented");
    }

    @Override
    protected String getProcedurePrefix()
    {
        return "";
    }

    public AbstractClient getDBConnector(Properties props)
    {
        try
        {
            return ElasticSearchSettings.isSSLEnabled() ? createSSLClient(props) : createClient(props);
        }
        catch (CoalescePersistorException e)
        {
            throw new RuntimeException(e);
        }
    }

    private AbstractClient createClient(Properties props) throws CoalescePersistorException
    {
        TransportClient client;

        try
        {
            Settings.builder().put("cluster.name", ElasticSearchSettings.getElasticClusterName());

            client = new PreBuiltTransportClient(Settings.builder().build());

            String eshosts = ElasticSearchSettings.getElastichosts();
            Stream.of(eshosts.split(",")).map(host -> {
                HostAndPort hostAndPort = HostAndPort.fromString(host).withDefaultPort(9300);

                try
                {
                    return new InetSocketTransportAddress(InetAddress.getByName(hostAndPort.getHostText()),
                                                          hostAndPort.getPort());
                }
                catch (UnknownHostException e)
                {
                    LOGGER.warn(e.getMessage(), e);
                    return null;
                }
            }).forEach(client::addTransportAddress);
        }
        catch (ElasticsearchException ex)
        {
            throw new CoalescePersistorException(ex.getMessage(), ex);
        }

        return client;
    }

    private AbstractClient createSSLClient(Properties props) throws CoalescePersistorException
    {
        IronhideClient client = null;
        // on startup
        String keypath = props.getProperty(ElasticSearchSettings.getKeystoreFileProperty());
        String trustpath = props.getProperty(ElasticSearchSettings.getTruststoreFileProperty());

        try
        {

            LOGGER.debug("Looking for keystore file: " + keypath);

            Builder clientBuild = IronhideClient.builder().setClusterName(ElasticSearchSettings.getElasticClusterName()).clientSSLSettings(
                    keypath,
                    "changeit",
                    trustpath,
                    "changeit");

            String eshosts = props.getProperty(ElasticSearchSettings.getElastichostsProperty());
            Stream.of(eshosts.split(",")).map(host -> {
                HostAndPort hostAndPort = HostAndPort.fromString(host).withDefaultPort(9300);

                try
                {
                    String chost = hostAndPort.getHostText();
                    InetAddress addr = InetAddress.getByName(chost);
                    return new InetSocketTransportAddress(addr, hostAndPort.getPort());
                }
                catch (UnknownHostException ex)
                {
                    // TODO Auto-generated catch block
                    LOGGER.error(ex.getMessage(), ex);
                    return null;
                }

            }).forEach(clientBuild::addTransportAddress);
            client = clientBuild.build();

            //			        .addTransportAddress(new InetSocketTransportAddress("bdpnode3", 9300))
            //			        .addTransportAddress(new InetSocketTransportAddress("bdpnode4", 9300));
            //			        .addTransportAddress(new InetSocketTransportAddress("bdpnode5", 9300));

        }
        catch (ElasticsearchException | FileNotFoundException ex)
        {
            throw new CoalescePersistorException(ex);
        }

        return client;
    }

}
