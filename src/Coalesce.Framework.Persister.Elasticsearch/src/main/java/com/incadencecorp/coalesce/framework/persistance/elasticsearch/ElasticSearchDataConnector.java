package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
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
			client = TransportClient.builder().build()
					   .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
			LOGGER.debug("ElasticSearchDataConnector:openDataConnection - Connector User: " + client.toString());
			System.out.println("ElasticSearchDataConnector:openDataConnection - Connector User: " + client.toString());

			//createTables(connector, coalesceTable, coalesceTemplateTable, coalesceEntityIndex, coalesceSearchTable);
			
		} catch (IOException e) {
			throw new CoalescePersistorException("Error Opening Data Connection - IOException", e);
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
		} catch (CoalescePersistorException e) {
			e.printStackTrace();
		}
    }

}
