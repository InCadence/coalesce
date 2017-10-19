package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.inject.Injector;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.plugins.PluginsService;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.transport.Transport;
import org.geotools.coverage.grid.GeneralGridCoordinates.Immutable;

import static org.elasticsearch.common.xcontent.XContentFactory.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;

public class ElasticSearchDataConnector extends CoalesceDataConnectorBase {

    private String _prefix;
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchDataConnector.class);
    private Client client;

    public ElasticSearchDataConnector(String prefix) throws CoalescePersistorException
    {
        try
        {
            //setSettings(settings);
            //_prefix = prefix;
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("ElasticSearchDataConnector", e);
        }
    }
    
	public Client getDBConnector() throws CoalescePersistorException {
		try {

			LOGGER.info("ElasticSearchDataConnector:openDataConnection - connecting to ElasticSearch");
			System.out.println("ElasticSearchDataConnector:openDataConnection - connecting to ElasticSearch");
			
			Settings settings = ImmutableSettings.settingsBuilder()
		    .put("localhost", "elastictest")
            .build();
			
			client = new TransportClient(settings);
				
			LOGGER.debug("ElasticSearchDataConnector:openDataConnection - Connector User: " + client.toString());
			System.out.println("ElasticSearchDataConnector:openDataConnection - Connector User: " + client.toString());

			//createTables(connector, coalesceTable, coalesceTemplateTable, coalesceEntityIndex, coalesceSearchTable);
			
		} catch (Exception e) {
			throw new CoalescePersistorException("Error Opening Data Connection - Exception", e);
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
        return "call " + _prefix;
    }
    
    public static void main(String args[]) {
    	ElasticSearchDataConnector connector;
		try {
			connector = new ElasticSearchDataConnector("");
			Client client = connector.getDBConnector();
			
			System.out.println("Client connected");
			
			try {
				IndexResponse response = client.prepareIndex("twitter", "tweet", "1")
				        .setSource(jsonBuilder()
				                .startObject()
				                    .field("user", "kimchy")
				                    .field("postDate", new Date())
				                    .field("message", "trying out Elasticsearch")
				                .endObject()
				              )
				    .get();
				
				// Index name
				String _index = response.getIndex();
				// Type name
				String _type = response.getType();
				// Document ID (generated or not)
				String _id = response.getId();
				// Version (if it's the first time you index this document, you will get: 1)
				long _version = response.getVersion();
				// status has stored current instance statement.
				//RestStatus status = response.;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (CoalescePersistorException e) {
			e.printStackTrace();
		}
    }

}
