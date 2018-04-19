package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.geotools.data.Query;
import org.geotools.filter.Capabilities;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceObject;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.exim.impl.JsonFullEximImpl;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
import com.incadencecorp.coalesce.framework.persistance.CoalescePersistorBase;
import com.incadencecorp.coalesce.framework.persistance.ElementMetaData;
import com.incadencecorp.coalesce.framework.persistance.EntityMetaData;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;

import ironhide.client.IronhideClient;

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
public class ElasticSearchPersistor extends CoalescePersistorBase implements ICoalesceSearchPersistor {
	
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
    }

    public void searchAll() {
        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
	    	IronhideClient client = conn.getDBConnector(getProps());
	    	SearchResponse response = client.prepareSearch().get();
	    	System.out.println(response.toString());
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    public void searchSpecific(String searchValue, String searchType) {
    	
        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
	    	IronhideClient client = conn.getDBConnector(getProps());
	    	SearchResponse response = client.prepareSearch(searchValue)
	    	        .setTypes(searchType)
	    	        //.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
	    	        //.setQuery(QueryBuilders.termQuery("multi", "test"))                 // Query
	    	        //.setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
	    	        //.setFrom(0).setSize(60).setExplain(true)
	    	        .get();

	    	System.out.println(response.toString());
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    public void searchElasticGeo() {
    	
    	//FilterToElastic filterElastic = new FilterToElastic();
    	
    	//Map<String, Object> queryBuilder = filterElastic.getNativeQueryBuilder();
    	
        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
	    	IronhideClient client = conn.getDBConnector(getProps());
	    } catch (Exception e) { 
	        e.printStackTrace(); 
	    } 
    } 

    public List<String> getCoalesceEntityKeysForEntityId(String entityId,
                                                         String entityIdType,
                                                         String entityName,
                                                         String entitySource) throws CoalescePersistorException
    {
        try
        {
            if (entitySource != null && entitySource != "")
            {
            	//TODO: Add source to this if needed
                return getCoalesceEntityKeysForEntityId(entityId, entityIdType, entityName);
            }
            else
            {
                return this.getCoalesceEntityKeysForEntityId(entityId, entityIdType, entityName);
            }
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("GetCoalesceEntityKeysForEntityId", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetCoalesceEntityKeysForEntityId", e);
        }
    }

    public EntityMetaData getCoalesceEntityIdAndTypeForKey(String key) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = new ElasticSearchDataConnector())
        {
            //return this.getCoalesceEntityIdAndTypeForKey(key, conn);
        	return null; //TODO: implement this search in Elasticsearch
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetCoalesceEntityIdAndTypeForKey", e);
        }
    }


    public DateTime getCoalesceObjectLastModified(String key, String objectType) throws CoalescePersistorException
    {
        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            return this.getCoalesceObjectLastModified(key, objectType, conn.getDBConnector(getProps()));
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("GetCoalesceObjectLastModified", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetCoalesceObjectLastModified", e);
        }
    }


    public byte[] getBinaryArray(String key) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = new ElasticSearchDataConnector())
        {

            byte[] binaryArray = null;

            ResultSet results = conn.executeQuery("SELECT BinaryObject FROM " //schema prefix?
                    + "CoalesceFieldBinaryData WHERE ObjectKey=?", new CoalesceParameter(key, Types.OTHER));

            // Get Results
            if (results != null && results.first())
            {
                Blob dataVal = results.getBlob("BinaryObject");
                binaryArray = dataVal.getBytes(1, (int) dataVal.length());
            }

            return binaryArray;
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("GetBinaryArray", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetBinaryArray", e);
        }
    }

    @Override
    public void saveTemplate(CoalesceDataConnectorBase conn, CoalesceEntityTemplate... templates)
            throws CoalescePersistorException
    {
        // Do Nothing
    }
    
    public Properties getProps() throws IOException {
    	makeSureConnectorIsInitialized();
    	
    	Properties props = new Properties();
    	props.putAll(ElasticSearchSettings.getParameters());
        
        return props;
    }
    
    public void makeSureConnectorIsInitialized() {
    	if(!ElasticSearchSettings.getConnectorInitialized())
    	{	    		
	        FilePropertyConnector connector = new FilePropertyConnector(CoalesceParameters.COALESCE_CONFIG_LOCATION);
	        LOGGER.debug("Connector initialized using config file: " + CoalesceParameters.COALESCE_CONFIG_LOCATION);
	        connector.setReadOnly(true);
	
	        ElasticSearchSettings.setConnector(connector);
    	}
    }

    @Override
    public void registerTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
    	
        JsonFullEximImpl converter = new JsonFullEximImpl();
        
        for (CoalesceEntityTemplate template : templates)
        {
            try(ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
            {
    	    	IronhideClient client = conn.getDBConnector(getProps());
    	    	XmlMapper mapper = new XmlMapper();
    	    	JsonNode node = mapper.readTree(template.toXml());

    	    	ObjectMapper jsonMapper = new ObjectMapper();
    	    	String json = jsonMapper.writeValueAsString(node);

    			// persist entity template to ElasticSearch
    	    	//String fakeJson = "{\"template\": \"te*\",\"classname\":\"com.incadencecorp.coalesce.framework.datamodel.TestEntity\",\"datecreated\":\"\",\"entityid\":\"\",\"entityidtype\":\"\",\"key\":\"\",\"lastmodified\":\"\",\"name\":\"UNIT_TEST\",\"source\":\"DSS\",\"title\":\"\",\"version\":1,\"linkagesection\":{\"datecreated\":\"\",\"key\":\"\",\"lastmodified\":\"\",\"name\":\"Linkages\"},\"section\":{\"datecreated\":\"\",\"key\":\"\",\"lastmodified\":\"\",\"name\":\"test section\",\"noindex\":\"false\",\"recordset\":{\"datecreated\":\"\",\"key\":\"\",\"lastmodified\":\"\",\"maxrecords\":\"0\",\"minrecords\":\"0\",\"name\":\"test1\",\"fielddefinition\":{\"datatype\":\"booleanlist\",\"datecreated\":\"\",\"defaultclassificationmarking\":\"\",\"key\":\"\",\"lastmodified\":\"\",\"name\":\"booleanlist\"}}}}";
    	    	
    	    	coalesceTemplateToESTemplate(template, client);
    	    	//client.admin().indices().preparePutTemplate(template.getName().toLowerCase()).setSource(coalesceTemplateToESTemplate(template, client)).get();
            }
            catch (CoalesceException e)
            {
                throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_SAVED,
                                                                   "Template",
                                                                   template.getKey(),
                                                                   e.getMessage()), e);
            } catch (Exception e) {
                LOGGER.warn("Failed to register templates: " + e.getMessage());
            }
        } 
    }
    
    private Map<String, Object> coalesceTemplateToESTemplate(CoalesceEntityTemplate template, IronhideClient client) { 
    	Map<String, Object> esTemplate = new HashMap<>();
    	Map<String, Object> mappingMap = new HashMap<>();
    	Map<String, Object> propertiesMap = new HashMap<>();
    	Map<String, Object> innerMap = new HashMap<>();
    	ElasticSearchMapperImpl mapper = new ElasticSearchMapperImpl();
        
        CoalesceTemplateUtil.addTemplates(template);
        
        CoalesceTemplateUtil.getRecordsets(template.getKey());
        
        Map<String, ECoalesceFieldDataTypes> typeMap = CoalesceTemplateUtil.getTemplateDataTypes(template.getKey());
    	
    	esTemplate.put("template", "*");

    	mappingMap.put("properties", propertiesMap);
        
        for (String typeName : typeMap.keySet()) {
        	if(mapper.mapToString(typeMap.get(typeName)) != null)
        	{
				innerMap = new HashMap<String, Object>();
				
				innerMap.put("type", mapper.mapToString(typeMap.get(typeName)));
				propertiesMap.put(typeName.replace(template.getName(), ""), innerMap);
        	}
        }
    	
    	client.admin().indices().preparePutTemplate(template.getName().toLowerCase()).setSource(esTemplate)
    	.addMapping("mapping", mappingMap).get();
    	
    	LOGGER.debug("Saved template named: " + template.getName());
    	
    	return esTemplate;
    }

    public ElementMetaData getXPath(String key, String objectType) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = new ElasticSearchDataConnector())
        {
            //return getXPathRecursive(key, objectType, "", conn);
        	
        	return null; //TODO implement getXPathRecursive 
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetXPath", e);
        }
    }


    public String getFieldValue(String fieldKey) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = new ElasticSearchDataConnector())
        {
            String value = null;

            ResultSet results = conn.executeQuery("SELECT value FROM " //schema prefix?
                    + "CoalesceField WHERE ObjectKey =?", new CoalesceParameter(fieldKey, Types.OTHER));

            while (results.next())
            {
                value = results.getString("value");
            }

            return value;
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("GetFieldValue", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetFieldValue", e);
        }
    }

    @Override
    public String[] getEntityXml(String... keys) throws CoalescePersistorException
    {
        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
	    	IronhideClient client = conn.getDBConnector(getProps());
            List<String> xmlList = new ArrayList<String>();
            List<CoalesceParameter> parameters = new ArrayList<CoalesceParameter>();

            StringBuilder sb = new StringBuilder("");
            GetResponse response;
            
            for (String key : keys)
            {
	        	if(checkIfIndexExists(client, key)) {
		        	response = client.prepareGet(key, key, key).get();
		        	
		        	xmlList.add(response.getSourceAsString());
	        	}
            }

            return xmlList.toArray(new String[xmlList.size()]);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetEntityXml", e);
        }
    }


    public String getEntityXml(String entityId, String entityIdType) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = new ElasticSearchDataConnector())
        {
            String value = null;

            ResultSet results = conn.executeQuery("SELECT EntityXml FROM " //schema prefix?
                                                          + "CoalesceEntity WHERE EntityId=? AND EntityIdType=?",
                                                  new CoalesceParameter(entityId),
                                                  new CoalesceParameter(entityIdType));

            while (results.next())
            {
                value = results.getString("EntityXml");
            }

            return value;
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("GetEntityXml", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetEntityXml", e);
        }
    }


    public String getEntityXml(String name, String entityId, String entityIdType) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = new ElasticSearchDataConnector())
        {
            String value = null;

            ResultSet results = conn.executeQuery("SELECT EntityXml FROM " //schema prefix?
                                                          + "CoalesceEntity WHERE Name=? AND EntityId=? AND EntityIdType=?",
                                                  new CoalesceParameter(name),
                                                  new CoalesceParameter(entityId),
                                                  new CoalesceParameter(entityIdType));

            while (results.next())
            {
                value = results.getString("EntityXml");
            }

            return value;
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("GetEntityXml", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetEntityXml", e);
        }
    }

    @Override
    protected CoalesceDataConnectorBase getDataConnector() throws CoalescePersistorException
    {
        return new ElasticSearchDataConnector();
    }

    @Override
    protected boolean flattenObject(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        boolean isSuccessful = true;

        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {

            // Create a Database Connection
            try
            {
               IronhideClient client = conn.getDBConnector(getProps());

                for (CoalesceEntity entity : entities)
                {
                    // Persist (Recursively)
                    isSuccessful &= updateCoalesceObject(entity, client, allowRemoval);
                }

                conn.commit();
            }
            catch (SQLException e)
            {
                conn.rollback();

                throw new CoalescePersistorException("FlattenObject: " + e.getMessage(), e);
            }
            catch (Exception e) 
            {
            	conn.rollback();
            	e.printStackTrace();
            }

        }

        return isSuccessful;
    }

    @Override
    protected boolean flattenCore(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        boolean isSuccessful = false;

        ElasticSearchDataConnector conn = null;

        // Create a Database Connection
        try
        {
            conn = new ElasticSearchDataConnector();
            conn.openConnection(false);

            for (CoalesceEntity entity : entities)
            {
                if (persistEntityObject(entity, conn.getDBConnector(getProps())))
                {
                    isSuccessful = true;
                }
            }

            conn.getConnection().commit();
        }
        catch (Exception e)
        {
            conn.rollback();

            throw new CoalescePersistorException("FlattenObject: " + e.getMessage(), e);
        }
        finally
        {
            conn.close();
        }

        return isSuccessful;
    }

    @Override
    public String getEntityTemplateKey(String name, String source, String version) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = new ElasticSearchDataConnector())
        {
            String value = null;

            ResultSet results = conn.executeQuery("SELECT TemplateKey FROM " //schema prefix?
                                                          + "CoalesceEntityTemplate WHERE Name=? and Source=? and Version=?",
                                                  new CoalesceParameter(name),
                                                  new CoalesceParameter(source),
                                                  new CoalesceParameter(version));

            while (results.next())
            {
                value = results.getString("TemplateKey");
            }

            return value;
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("GetEntityTemplateKey", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetEntityTemplateKey", e);
        }
    }

    @Override
    public List<ObjectMetaData> getEntityTemplateMetadata() throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = new ElasticSearchDataConnector())
        {
        	//Don't do this because it's still sql in the parent
            return new ArrayList<>();
        }
        catch (Exception ex)
        {
            throw new CoalescePersistorException("getEntityTemplateMetadata", ex);
        }
    }

    public CoalesceEntityTemplate getEntityTemplate(String key) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = new ElasticSearchDataConnector())
        {
            String value = null;

            ResultSet results = conn.executeQuery("SELECT TemplateXml FROM " //schema prefix?
                    + "CoalesceEntityTemplate WHERE TemplateKey=?", new CoalesceParameter(key, Types.OTHER));

            while (results.next())
            {
                value = results.getString("TemplateXml");
            }

            return CoalesceEntityTemplate.create(value);
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("GetEntityTemplateXml", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetEntityTemplateXml", e);
        }
    }

    public CoalesceEntityTemplate getEntityTemplate(String name, String source, String version) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = new ElasticSearchDataConnector())
        {
            String value = null;

            ResultSet results = conn.executeQuery("SELECT TemplateXml FROM " //Schema prefix?
            										+ "CoalesceEntityTemplate WHERE Name=? and Source=? and Version=?",
                                                  new CoalesceParameter(name),
                                                  new CoalesceParameter(source),
                                                  new CoalesceParameter(version));

            while (results.next())
            {
                value = results.getString("TemplateXml");
            }

            return CoalesceEntityTemplate.create(value);
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("GetEntityTemplateXml", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetEntityTemplateXml", e);
        }
    }

    /*--------------------------------------------------------------------------
    Protected Functions
    --------------------------------------------------------------------------*/
    /**
     * Adds or Updates a Coalesce object that matches the given parameters.
     *
     * @param coalesceObject the Coalesce object to be added or updated
     * @param conn is the PostGresDataConnector database connection
     * @return isSuccessful = True = Successful add/update operation.
     * @throws SQLException
     */
    protected boolean persistObject(CoalesceObject coalesceObject, IronhideClient conn) throws SQLException
    {
        boolean isSuccessful = true;

        switch (coalesceObject.getType()) {
        case "entity":

            // isSuccessful = checkLastModified(coalesceObject, conn);
            isSuccessful = persistEntityObject((CoalesceEntity) coalesceObject, conn);
            break;

        default:
            isSuccessful = false;
        }

        return isSuccessful;
    }

    /**
     * Adds or Updates a Coalesce entity that matches the given parameters.
     *
     * @param entity the XsdEntity to be added or updated
     * @param conn is the PostGresDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    private boolean persistEntityObject(CoalesceEntity entity, IronhideClient conn) throws SQLException
    {
        // Return true if no update is required.
    	//Worry about this later. 
//        if (!checkLastModified(entity, conn))
//        {
//            return true;
//        }
 
        IndexResponse response;
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			
			for(CoalesceSection section : entity.getSectionsAsList()) {
				for(CoalesceRecordset recordset : section.getRecordsetsAsList()) {
					for(CoalesceRecord record : recordset.getAllRecords()) {
						for(CoalesceField field : record.getFields()) {
							map.put(field.getName(), field.getValue());
							LOGGER.debug("Adding field " + field.getName() + " with value: " + field.getValue());
						}
					}
				}
			}
			
			Map<String, Object> linkageMap = createLinkageMap(entity);
	        
	        conn.prepareIndex("oelinkage", "oelinkage").setSource(linkageMap).get();
			LOGGER.debug("Indexed linkage for entity " + "coalesce-" + entity.getName());


			// convert JSON string to Map
			response = conn.prepareIndex("coalesce-" + entity.getName().toLowerCase(), entity.getType().toLowerCase()).setSource(map).get();
			System.out.println("Saved Index called: " + "coalesce-" + entity.getName());
 
			System.out.println(response.toString());
		} catch (CoalesceException e) {
			e.printStackTrace();
			return  false;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		//If the index response is returned and no exception was thrown, the index operation was successful
        return true;
    }
    
    /**
     * persist the Linkage for an Entity. This will create a new document of the Linkage index
     * 
     * @param entity The entity to persist the linkages for
     * @return returns true if no exceptions were thrown
     */
    private Map<String, Object> createLinkageMap(CoalesceEntity entity) {
    	//HashMap representation of the linkage for indexing in ElasticSearch
		HashMap<String, Object> linkageMap = new HashMap<String, Object>();
		
		Map<String, CoalesceLinkage> linkages = entity.getLinkages();
        for (Map.Entry<String, CoalesceLinkage> mlink : linkages.entrySet()) {
        	CoalesceLinkage link = mlink.getValue();
        	
        	linkageMap.put(LINKAGE_KEY_COLUMN_NAME, link.getKey());
        	linkageMap.put(LINKAGE_LABEL_COLUMN_NAME, link.getName());
        	linkageMap.put(LINKAGE_ENTITY1_KEY_COLUMN_NAME, link.getEntity1Key());
        	linkageMap.put(LINKAGE_ENTITY1_NAME_COLUMN_NAME, link.getEntity1Name());
        	linkageMap.put(LINKAGE_ENTITY1_SOURCE_COLUMN_NAME, link.getEntity1Source());
        	linkageMap.put(LINKAGE_ENTITY1_VERSION_COLUMN_NAME, link.getEntity1Version());
        	linkageMap.put(LINKAGE_ENTITY2_KEY_COLUMN_NAME, link.getEntity2Key());
        	linkageMap.put(LINKAGE_ENTITY2_NAME_COLUMN_NAME, link.getEntity2Name());
        	linkageMap.put(LINKAGE_ENTITY2_SOURCE_COLUMN_NAME, link.getEntity2Source());
        	linkageMap.put(LINKAGE_LAST_MODIFIED_COLUMN_NAME, link.getLastModifiedAsString());
        	linkageMap.put(LINKAGE_LINK_TYPE_COLUMN_NAME, link.getLinkType().getLabel());
        }

		//If the index response is returned and no exception was thrown, the index operation was successful
        return linkageMap;
    }

    /*--------------------------------------------------------------------------
    Protected Methods
    --------------------------------------------------------------------------*/

    protected String getCreator(CoalesceEntity entity)
    {
        return null;
    }

    protected String getType(CoalesceEntity entity)
    {
        return null;
    }

    protected String getScope(CoalesceEntity entity)
    {
        return null;
    }

    /**
     * Returns the comparison for the Coalesce object last modified date versus
     * the same objects value in the database.
     *
     * @param coalesceObject the Coalesce object to have it's last modified date
     *            checked.
     * @param conn is the PostGresDataConnector database connection
     * @return False = Out of Date
     * @throws SQLException
     */
    protected boolean checkLastModified(CoalesceObject coalesceObject, IronhideClient conn) throws SQLException
    {
        boolean isOutOfDate = true;

        // Get LastModified from the Database
        DateTime lastModified = this.getCoalesceObjectLastModified(coalesceObject.getKey(), coalesceObject.getType(), conn);

        // DB Has Valid Time?
        if (lastModified != null)
        {
            // Remove NanoSeconds (100 ns / Tick and 1,000,000 ns / ms = 10,000
            // Ticks / ms)
            long objectTicks = coalesceObject.getLastModified().getMillis();
            long SQLRecordTicks = lastModified.getMillis();

            // TODO: Round Ticks for SQL (Not sure if this is required for .NET)
            // ObjectTicks = this.RoundTicksForSQL(ObjectTicks);

            if (objectTicks == SQLRecordTicks)
            {
                // They're equal; No Update Required
                isOutOfDate = false;
            }
        }

        return isOutOfDate;
    }

    /**
     * Deletes the Coalesce object and CoalesceObjectMap that matches the given
     * parameters.
     *
     * @param coalesceObject the Coalesce object to be deleted
     * @return DeleteResponse
     * @throws SQLException
     */
    public DeleteResponse deleteObject(CoalesceObject coalesceObject) throws SQLException
    {
        String objectType = coalesceObject.getType().toLowerCase();
        String objectKey = coalesceObject.getName().toLowerCase();

        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
	    	IronhideClient client = conn.getDBConnector(getProps());
	        DeleteResponse response = client.prepareDelete(objectKey, objectType, "1")
	                .get();

	        return response;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Returns the Coalesce entity keys that matches the given parameters.
     *
     * @param entityId of the entity.
     * @param entityIdType of the entity.
     * @param entityName of the entity.
     * @return List<String> of primary keys for the matching Coalesce entity, or null if not found
     * @throws SQLException ,Exception,CoalescePersistorException
     */
    private List<String> getCoalesceEntityKeysForEntityId(String entityId, String entityIdType, String entityName)
            throws Exception
    {
        List<String> keyList = new ArrayList<String>();

        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
        	IronhideClient client = conn.getDBConnector(getProps());
        	if(checkIfIndexExists(client, entityId)) {
	        	GetResponse response = client.prepareGet(entityId, entityIdType, entityName).get();
	        	
	        	keyList.add(response.getId());
	
	            return keyList;
        	} else {
        		return null;
        	}
        }

    }
    
    public boolean checkIfIndexExists(IronhideClient client, String index) {

    	try {
    		boolean hasIndex = client.admin().indices().exists(new IndicesExistsRequest(index.toLowerCase())).actionGet().isExists(); 
			
	        return hasIndex;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

    	return false;
    }

    private boolean updateCoalesceObject(CoalesceObject coalesceObject, IronhideClient conn, boolean allowRemoval)
            throws SQLException

    {
        boolean isSuccessful = false;

        if (coalesceObject.isFlatten())
        {
            isSuccessful = persistObject(coalesceObject, conn);

            // Successful?
            if (isSuccessful)
            {
                // Yes; Iterate Through Children
                for (CoalesceObject childObject : coalesceObject.getChildCoalesceObjects().values())
                {
                    updateCoalesceObject(childObject, conn, allowRemoval);
                }
            }
        }
        return isSuccessful;
    }

    private DateTime getCoalesceObjectLastModified(String key, String objectType, IronhideClient conn)
            throws SQLException
    {
        DateTime lastModified = null;

        //TODO: Figure out how to do this query in ElasticSearch
        /*
        // Determine the Table Name
        String tableName = CoalesceTableHelper.getTableNameForObjectType(objectType);
        String dateValue = null;

        ResultSet results = conn.executeQuery("SELECT LastModified FROM " +  + tableName
                + " WHERE ObjectKey=?", new CoalesceParameter(key.trim(), Types.OTHER));
        ResultSetMetaData resultsmd = results.getMetaData();

        // JODA Function DateTimeFormat will adjust for the Server timezone when
        // converting the time.
        if (resultsmd.getColumnCount() <= 1)
        {
            while (results.next())
            {
                dateValue = results.getString("LastModified");
                if (dateValue != null)
                {
                    lastModified = JodaDateTimeHelper.getPostGresDateTim(dateValue);
                }
            }
        }
        */
        return JodaDateTimeHelper.nowInUtc();

    }
    
    /**
     * @return EnumSet of EPersistorCapabilities
     */
    @Override
    public EnumSet<EPersistorCapabilities> getCapabilities()
    {
        EnumSet<EPersistorCapabilities> enumSet = super.getCapabilities();
        EnumSet<EPersistorCapabilities> newCapabilities = EnumSet.of(EPersistorCapabilities.GET_FIELD_VALUE,
                                                                     EPersistorCapabilities.READ_TEMPLATES,
                                                                     EPersistorCapabilities.UPDATE,
                                                                     EPersistorCapabilities.DELETE,
                                                                     EPersistorCapabilities.SEARCH,
                                                                     EPersistorCapabilities.SUPPORTS_BLOB,
                                                                     EPersistorCapabilities.GEOSPATIAL_SEARCH,
                                                                     EPersistorCapabilities.INDEX_FIELDS);
        if (enumSet != null)
        {
            enumSet.addAll(newCapabilities);
        }
        else
        {
            enumSet = newCapabilities;
        }
        return enumSet;
    }

    @Override
    public SearchResults search(Query query) throws CoalescePersistorException
    {
    	CachedRowSet rowset = null;
    	
        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
        	rowset = RowSetProvider.newFactory().createCachedRowSet();
	    	IronhideClient client = conn.getDBConnector(getProps());
	    	SearchResponse response = client.prepareSearch("gdelt_data")
    	        .setQuery(QueryBuilders.termQuery("GlobalEventID", "410479387"))                 // Query
    	        //.setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
    	        //.setFrom(0).setSize(60).setExplain(true)
    	        .get();

	    	LOGGER.debug(response.toString());
        } catch (Exception e) {
        	LOGGER.error(e.getMessage());
            //throw new CoalescePersistorException(e.getMessage(), e);
        }
        // TODO Not Implemented
    	//query.getFilter().toString();
    	//query.getAlias();
    	
    	//QueryBuilder qb = QueryBuilders.matchQuery(
    	//		"GlobalEventID",
    	//		"410479387");
        
        SearchResults queryResults = new SearchResults();
        queryResults.setResults(rowset);
        return queryResults;
    }

    @Override
    public Capabilities getSearchCapabilities()
    {
        Capabilities capability = new Capabilities();
        capability.addAll(Capabilities.SIMPLE_COMPARISONS);
        capability.addAll(Capabilities.LOGICAL);

        return capability;
    }
}
