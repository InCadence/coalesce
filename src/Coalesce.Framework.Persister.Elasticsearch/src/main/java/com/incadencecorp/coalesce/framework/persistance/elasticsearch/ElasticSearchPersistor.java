package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.support.AbstractClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.geotools.data.Query;
import org.geotools.filter.Capabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceObject;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.exim.impl.JsonFullEximImpl;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;

/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

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
 * This persister is for a ElasticSearch database.
 *
 * @author n78554
 */
public class ElasticSearchPersistor extends ElasticSearchTemplatePersister implements ICoalescePersistor {

    // Some constants for the linkage records
    // TODO: Move this to a common area in Coalesce
    public static final String LINKAGE_ENTITY1_KEY_COLUMN_NAME = "entity1Key";
    public static final String LINKAGE_ENTITY1_NAME_COLUMN_NAME = "entity1Name";
    public static final String LINKAGE_ENTITY1_SOURCE_COLUMN_NAME = "entity1Source";
    public static final String LINKAGE_ENTITY1_VERSION_COLUMN_NAME = "entity1Version";
    public static final String LINKAGE_ENTITY2_KEY_COLUMN_NAME = "entity2Key";
    public static final String LINKAGE_ENTITY2_NAME_COLUMN_NAME = "entity2Name";
    public static final String LINKAGE_ENTITY2_SOURCE_COLUMN_NAME = "entity2Source";
    public static final String LINKAGE_ENTITY2_VERSION_COLUMN_NAME = "entity2Version";
    public static final String LINKAGE_KEY_COLUMN_NAME = "objectKey";
    public static final String LINKAGE_LAST_MODIFIED_COLUMN_NAME = "lastModified";
    public static final String LINKAGE_LINK_TYPE_COLUMN_NAME = "linkType";
    public static final String LINKAGE_LABEL_COLUMN_NAME = "label";

    /*--------------------------------------------------------------------------
    Private Members
    --------------------------------------------------------------------------*/

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchPersistor.class);
    private static final String COALESCE_ENTITY_INDEX = "coalesceentityindex";
    private static final String XML = CoalescePropertyFactory.getEntityXml().getPropertyName();

    /*--------------------------------------------------------------------------
    Overridden Functions
    --------------------------------------------------------------------------*/

    /**
     * Default constructor using {@link ElasticSearchSettings} for configuration
     */
    public ElasticSearchPersistor()
    {
        FilePropertyConnector fileConnector = new FilePropertyConnector(CoalesceParameters.COALESCE_CONFIG_LOCATION);
        fileConnector.setReadOnly(true);

        ElasticSearchSettings.setConnector(fileConnector);

        LOGGER.debug("Initialized ElasticSearchPersistor using default constructor");
    }

    

    public void makeSureConnectorIsInitialized()
    {
        if (!ElasticSearchSettings.getConnectorInitialized())
        {
            FilePropertyConnector connector = new FilePropertyConnector(CoalesceParameters.COALESCE_CONFIG_LOCATION);
            LOGGER.debug("Connector initialized using config file: " + CoalesceParameters.COALESCE_CONFIG_LOCATION);
            connector.setReadOnly(true);

            ElasticSearchSettings.setConnector(connector);
        }
    }

    /*--------------------------------------------------------------------------
    Protected Methods
    --------------------------------------------------------------------------*/

    public boolean checkIfIndexExists(AbstractClient client, String index)
    {

        try
        {
            boolean hasIndex = client.admin().indices().exists(new IndicesExistsRequest(index.toLowerCase())).actionGet().isExists();

            return hasIndex;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean saveEntity(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            AbstractClient client = conn.getDBConnector(ElasticSearchSettings.getParameters());

            for (CoalesceEntity entity : entities)
            {
                if (entity.isMarkedDeleted() && allowRemoval)
                {
                    deleteLinkages(entity, client);
                    deleteEntity(entity, client);
                    deleteEntityIndex(entity, client);
                }
                else
                {
                    persistLinkages(entity, client);
                    persistEntityObject(entity, client);
                }
            }
        }

        return true;
    }

    @Override
    public CoalesceEntity[] getEntity(String... keys) throws CoalescePersistorException
    { 
    	List<CoalesceEntity> results = new ArrayList<>();

    	for(String entityXML : getEntityXml(keys)) 
    	{
    		results.add(CoalesceEntity.create(entityXML));
    	}
    	
        return results.toArray(new CoalesceEntity[results.size()]);
    }

    @Override
    public String[] getEntityXml(String... keys) throws CoalescePersistorException
    {
    	List<String> results = new ArrayList<>();
    	try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
	        AbstractClient client = conn.getDBConnector(ElasticSearchSettings.getParameters());
	        
	    	for(String key : keys) 
	    	{
	    		GetRequest request = new GetRequest();
	    		request.index(COALESCE_ENTITY_INDEX);
	    		request.type(COALESCE_ENTITY_INDEX);
	    		request.id(key);
	    		
	    		GetResponse getResponse = client.get(request).actionGet();
	    		
	    		//LOGGER.debug("Returned index: " + getResponse.getSource().get(XML));
	    		
	    		if(getResponse != null && getResponse.getSource() != null ) 
	    		{
	    			results.add((String)getResponse.getSource().getOrDefault(XML,""));
	    		}
	    	}
        }	catch (ElasticsearchException e) {
    	    if (e.status() == RestStatus.NOT_FOUND) {
    	        LOGGER.error(e.getDetailedMessage());
    	    }
    	}
    	
        return results.toArray(new String[results.size()]);
    }

    @Override
    public EnumSet<EPersistorCapabilities> getCapabilities()
    {
        return EnumSet.of(EPersistorCapabilities.CREATE, EPersistorCapabilities.READ, EPersistorCapabilities.UPDATE, EPersistorCapabilities.DELETE);
    }

    private void deleteEntity(CoalesceEntity entity, AbstractClient conn) throws CoalescePersistorException
    {
        deleteFromElasticSearch(conn, "coalesce-" + entity.getName().toLowerCase(), 
        		entity.getType().toLowerCase(), 
        		entity.getKey());
    }

    private void deleteLinkages(CoalesceEntity entity, AbstractClient conn) throws CoalescePersistorException
    {
        deleteFromElasticSearch(conn, "oelinkage", 
        		"oelinkage", 
        		entity.getKey());
    }

    private void deleteEntityIndex(CoalesceEntity entity, AbstractClient conn) throws CoalescePersistorException
    {
        deleteFromElasticSearch(conn, COALESCE_ENTITY_INDEX, 
        		COALESCE_ENTITY_INDEX, 
        		entity.getKey());
    }

    private void deleteFromElasticSearch(AbstractClient conn, String index, String type, String id) {
        DeleteRequest entityRequest = new DeleteRequest();
        entityRequest.index(index);
        entityRequest.type(type);
        entityRequest.id(id);

        DeleteResponse entityResponse = conn.delete(entityRequest).actionGet();

        LOGGER.debug("Delete entity for entity {} : {}", index, entityResponse);
    }

    private void persistLinkages(CoalesceEntity entity, AbstractClient conn) throws CoalescePersistorException
    {
        IndexRequest request = new IndexRequest();
        request.index("oelinkage");
        request.type("oelinkage");
        request.id(entity.getKey());
        request.source(createLinkageMap(entity));

        IndexResponse response = conn.index(request).actionGet();

        LOGGER.debug("Indexed linkage for entity coalesce-{} : {}", entity.getName(), response);
    }

    private void persistEntityObject(CoalesceEntity entity, AbstractClient conn) throws CoalescePersistorException
    {
        // Return true if no update is required.
        //Worry about this later.
        //        if (!checkLastModified(entity, conn))
        //        {
        //            return true;
        //        }

        IndexResponse response;
        IndexRequest request = new IndexRequest();
        request.index("coalesce-" + entity.getName().toLowerCase());
        request.type(entity.getType().toLowerCase());
        request.id(entity.getKey());
        request.source(createValueMap(entity));

        response = conn.index(request).actionGet();

        LOGGER.debug("Saved Index called: coalesce-{} : {}", entity.getName(), response);

        // TODO Remove this test code
        GetRequest getRequest = new GetRequest();
        getRequest.index("coalesce-" + entity.getName().toLowerCase());
        getRequest.type(entity.getType().toLowerCase());
        getRequest.id(entity.getKey());

        GetResponse getResponse = conn.get(getRequest).actionGet();

        LOGGER.debug(getResponse.toString());
        
        persistEntityIndex(entity, conn);
    }

    private Map<String, Object> createValueMap(CoalesceEntity entity)
    {
        HashMap<String, Object> map = new HashMap<>();

        getFieldValues(entity, map);
        
        if(ElasticSearchSettings.getStoreXML()) {
	        //Add entity XML to the end of the map.
	        map.put("entityXML", entity.toXml());
        }

        return map;
    }

    private void getFieldValues(CoalesceObject coalesceObject, Map<String, Object> results)
    {
        // Is Active?
        if (coalesceObject.isActive())
        {
            // Yes; Is a CoalesceField?
            if (coalesceObject.getType().equalsIgnoreCase("field"))
            {
                // Yes; Check Data Type
                CoalesceField<?> field = (CoalesceField<?>) coalesceObject;

                if (field.getBaseValue() != null)
                {
                    String name = normalizeName(field.getName());

                    switch (field.getDataType())
                    {
                    case BINARY_TYPE:
                    case FILE_TYPE:
                        // Ignore these types.
                        break;
                    default:
                        // Add field value to results
                        try
                        {
                            results.put(name, field.getValue());
                            LOGGER.trace("Adding field {} = {}", field.getName(), field.getBaseValue());
                        }
                        catch (CoalesceDataFormatException e)
                        {
                            LOGGER.warn("(FAILED) Adding field {} = {}", field.getName(), field.getBaseValue());
                        }

                        break;
                    }
                }
            }

            // Recurse Through Children
            for (CoalesceObject child : coalesceObject.getChildCoalesceObjects().values())
            {
                getFieldValues(child, results);
            }
        }
    }
    
    private void persistEntityIndex(CoalesceEntity entity, AbstractClient conn) {
    	IndexResponse response;
	    IndexRequest request = new IndexRequest();
	    request.index(COALESCE_ENTITY_INDEX);
	    request.type(COALESCE_ENTITY_INDEX);
	    request.id(entity.getKey());
	    
	    Map<String, Object> entityMap = new HashMap<>();
	    entityMap.put("id", entity.getAttribute(CoalesceEntity.ATTRIBUTE_ENTITYID));
	    entityMap.put("name", entity.getAttribute(CoalesceEntity.ATTRIBUTE_NAME));
	    entityMap.put("source", entity.getAttribute(CoalesceEntity.ATTRIBUTE_SOURCE));
	    entityMap.put("version", entity.getAttribute(CoalesceEntity.ATTRIBUTE_VERSION));
	    entityMap.put("dateCreated", entity.getAttribute(CoalesceEntity.ATTRIBUTE_DATECREATED));
	    entityMap.put("lastModified", entity.getAttribute(CoalesceEntity.ATTRIBUTE_LASTMODIFIED));
	    
	    if(ElasticSearchSettings.getStoreXML()) {
		    entityMap.put(XML, entity.toXml());
		    
		    //Nested tabs on purpose to show map structure
		    
						    Map<String, Object> enabledMap = new HashMap<>();
						    enabledMap.put("enabled", false);
						    
					    Map<String, Object> xmlFieldMap = new HashMap<>();
					    xmlFieldMap.put(XML, enabledMap);
					    
				    Map<String, Object> propertiesMap = new HashMap<>();
				    propertiesMap.put("properties", xmlFieldMap);
				    
			    Map<String, Object> typeMap = new HashMap<>();
			    typeMap.put(entity.getType().toLowerCase(), propertiesMap);
			    
		    entityMap.put("mappings", typeMap);
	    }
	    request.source(entityMap);
	
	    response = conn.index(request).actionGet();
	
	    LOGGER.debug("Saved XML Index called: coalesceentityindex : {}", response);
    }

    private String normalizeName(String name)
    {
        // TODO
        return name;
    }

    /**
     * persist the Linkage for an Entity. This will create a new document of the Linkage index
     *
     * @param entity The entity to persist the linkages for
     * @return returns true if no exceptions were thrown
     */
    private Map<String, Object> createLinkageMap(CoalesceEntity entity)
    {
        //HashMap representation of the linkage for indexing in ElasticSearch
        HashMap<String, Object> linkageMap = new HashMap<>();

        Map<String, CoalesceLinkage> linkages = entity.getLinkages();
        for (Map.Entry<String, CoalesceLinkage> mlink : linkages.entrySet())
        {
            CoalesceLinkage link = mlink.getValue();

            linkageMap.put(ElasticSearchPersistor.LINKAGE_KEY_COLUMN_NAME, link.getKey());
            linkageMap.put(ElasticSearchPersistor.LINKAGE_LABEL_COLUMN_NAME, link.getName());
            linkageMap.put(ElasticSearchPersistor.LINKAGE_ENTITY1_KEY_COLUMN_NAME, link.getEntity1Key());
            linkageMap.put(ElasticSearchPersistor.LINKAGE_ENTITY1_NAME_COLUMN_NAME, link.getEntity1Name());
            linkageMap.put(ElasticSearchPersistor.LINKAGE_ENTITY1_SOURCE_COLUMN_NAME, link.getEntity1Source());
            linkageMap.put(ElasticSearchPersistor.LINKAGE_ENTITY1_VERSION_COLUMN_NAME, link.getEntity1Version());
            linkageMap.put(ElasticSearchPersistor.LINKAGE_ENTITY2_KEY_COLUMN_NAME, link.getEntity2Key());
            linkageMap.put(ElasticSearchPersistor.LINKAGE_ENTITY2_NAME_COLUMN_NAME, link.getEntity2Name());
            linkageMap.put(ElasticSearchPersistor.LINKAGE_ENTITY2_SOURCE_COLUMN_NAME, link.getEntity2Source());
            linkageMap.put(ElasticSearchPersistor.LINKAGE_LAST_MODIFIED_COLUMN_NAME, link.getLastModifiedAsString());
            linkageMap.put(ElasticSearchPersistor.LINKAGE_LINK_TYPE_COLUMN_NAME, link.getLinkType().getLabel());
        }

        //If the index response is returned and no exception was thrown, the index operation was successful
        return linkageMap;
    }
}
