package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.stream.Stream;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import static org.elasticsearch.common.xcontent.XContentFactory.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.net.HostAndPort;
import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;

import ironhide.client.IronhideClient;
import ironhide.client.IronhideClient.Builder;

public class ElasticSearchDataConnector extends CoalesceDataConnectorBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchDataConnector.class);

    // Datastore Properties
    public static final String INSTANCE_ID = "instanceId";
	public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static String getInstanceId() {
		return INSTANCE_ID;
	}

	public TransportClient oldGetDBConnector() throws CoalescePersistorException {
		TransportClient client = null;
		// on startup
		//String keypath = props.getProperty(KEYSTORE_FILE_PROPERTY);
		//String trustpath = props.getProperty(TRUSTSTORE_FILE_PROPERTY);

		try {
			Settings settings = Settings.builder()
				    .put("cluster.name", "elasticsearch")
		            .build();

			client = new PreBuiltTransportClient(settings);

			/*Settings settings = ImmutableSettings.settingsBuilder()
		    .put("cluster.name", "RDK.bdpdev.incadencecorp.com")
	        .put("plugins." + PluginsService.LOAD_PLUGIN_FROM_CLASSPATH, false)
	        .put("plugin.types", IronhideClientPlugin.class)
	        .put(ironhide.transport.ConfigConstants.sslSettings(
	        		keypath,
	                "changeit",
	                trustpath,
	                "changeit"
	                ))
            .build();	
			
			client = new TransportClient(
					settings);
					*/

			String eshosts = "localhost:9300";
			Stream.of(eshosts.split(","))
	        .map(host -> {
	            HostAndPort hostAndPort = HostAndPort
	                    .fromString(host)
	                    .withDefaultPort(9300);

	            try {
					return new InetSocketTransportAddress(InetAddress.getByName(hostAndPort.getHostText()), hostAndPort.getPort());
				} catch (UnknownHostException e) {
					LOGGER.warn(e.getMessage(), e);
					return null;
				}
	        })
	        .forEach(client::addTransportAddress);

//			        .addTransportAddress(new InetSocketTransportAddress("bdpnode3", 9300))
//			        .addTransportAddress(new InetSocketTransportAddress("bdpnode4", 9300));
//			        .addTransportAddress(new InetSocketTransportAddress("bdpnode5", 9300));

		} catch (ElasticsearchException ex) {
			throw new CoalescePersistorException(ex.getMessage(), ex);
		}

		return client;
	}

	public Connection getDBConnection() throws SQLException {
		LOGGER.error("ElasticSearchDataConnector:getDBConnection: Procedure not implemented");
		throw new UnsupportedOperationException("ElasticSearchDataConnector:getDBConnection: Procedure not implemented");
	}

    @Override
    protected String getProcedurePrefix()
    {
        return "";
    }
    
    public IronhideClient getDBConnector(Properties props)
    {            	
        IronhideClient client = null;
        // on startup
        String keypath = props.getProperty(ElasticSearchSettings.getKeystoreFileProperty());
        String trustpath = props.getProperty(ElasticSearchSettings.getTruststoreFileProperty());

        try
        {

            LOGGER.debug("Looking for keystore file: " + keypath);
            
            Builder clientBuild = IronhideClient.builder().setClusterName(ElasticSearchSettings.getElasticClusterName())
            		.clientSSLSettings(keypath, "changeit",
            				trustpath, "changeit");

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
            // TODO Auto-generated catch block
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }

        return client;
    }
}
