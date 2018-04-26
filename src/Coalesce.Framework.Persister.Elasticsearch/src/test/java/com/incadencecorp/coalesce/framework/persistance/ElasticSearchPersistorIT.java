package com.incadencecorp.coalesce.framework.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.support.AbstractClient;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchDataConnector;
import com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchPersistor;
import com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchPersistorSearch;
import com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchSettings;
import com.incadencecorp.coalesce.framework.persistance.testobjects.GDELT_Test_Entity;
import com.incadencecorp.coalesce.api.CoalesceParameters;

public class ElasticSearchPersistorIT extends AbstractCoalescePersistorTest<ElasticSearchPersistor> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchPersistorIT.class);

    private static ElasticSearchDataConnector conn;
    private static AbstractClient client;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        System.setProperty(CoalesceParameters.COALESCE_CONFIG_LOCATION_PROPERTY, "src/test/resources");
        InputStream in = ElasticSearchDataConnector.class.getResourceAsStream("/elasticsearch-config.properties");
        Properties props = new Properties();
        props.load(in);
        in.close();
        String dbName = props.getProperty("database");
        String zookeepers = props.getProperty("zookeepers");
        String user = props.getProperty("userid");
        String password = props.getProperty("password");
        conn = new ElasticSearchDataConnector();
        client = conn.getDBConnector(props);
    	//client = conn.getDBConnector();
    }

    @Override
	public void registerEntities() {
        try
        {
            ICoalescePersistor persistor = createPersister();

            if (persistor.getCapabilities().contains(EPersistorCapabilities.READ_TEMPLATES))
            {
                LOGGER.warn("Registering Entities");

                TestEntity entity = new TestEntity();
                entity.initialize();

                persistor.registerTemplate(CoalesceEntityTemplate.create(entity));

                GDELT_Test_Entity gdelt_entity = new GDELT_Test_Entity();
                gdelt_entity.initialize();

                persistor.registerTemplate(CoalesceEntityTemplate.create(gdelt_entity));
            }
        }
        catch (CoalesceException e)
        {
            LOGGER.warn("Failed to register templates");
        }
	}

	@Override
	public void testCreation() throws Exception {
        // Create Entities
        TestEntity entity1 = new TestEntity();
        entity1.initialize();
        
        //ElasticSearch requires names be lowercase
        entity1.setName(entity1.getName().toLowerCase());

        ElasticSearchPersistor persistor = new ElasticSearchPersistor();

        //As long as there are no problems with saving the new entity, should return true
        assertTrue(persistor.saveEntity(true, entity1));
	}

	@Test
	public void testGDELTCreation() throws Exception {
		try {
        // Create Entities
        GDELT_Test_Entity entity1 = new GDELT_Test_Entity();
        entity1.initialize();
        
        //ElasticSearch requires names be lowercase
        entity1.setName(entity1.getName().toLowerCase());

        ElasticSearchPersistor persistor = new ElasticSearchPersistor();

        //As long as there are no problems with saving the new entity, should return true
        assertTrue(persistor.saveEntity(true, entity1));
		} catch (Exception e) {
        	e.printStackTrace();
        }
	}

	@Override
	public void testUpdates() throws Exception {
        // Create Entities
        TestEntity entity1 = new TestEntity();
        entity1.initialize();
        
        //ElasticSearch requires names be lowercase
        entity1.setName(entity1.getName().toLowerCase());

        ElasticSearchPersistor persistor = new ElasticSearchPersistor();

        persistor.saveEntity(true, entity1);
        
        //TODO
        //Now pull the created entity back and updated and confirm the update worked
	}

	@Override
	public void testDeletion() throws Exception {
        AbstractClient client = conn.getDBConnector(ElasticSearchSettings.getParameters());
        // Create Entities
        TestEntity entity1 = new TestEntity();
        entity1.initialize();
        
        //ElasticSearch requires names be lowercase
        entity1.setName(entity1.getName().toLowerCase());

        ElasticSearchPersistor persistor = new ElasticSearchPersistor();

        // Save Entity1 (Should create a place holder for entity2)
        persistor.saveEntity(true, entity1);
        
        //DeleteResponse response = persistor.deleteEntity(entity1, client);
        
        //assertEquals(Result.DELETED, response.getResult());
	}
	
	@Test
	public void testCheckExists() throws Exception {
		ElasticSearchPersistor persistor = new ElasticSearchPersistor();
        TestEntity entity1 = new TestEntity();
        entity1.initialize();
		
        //Exists should be true
		assertTrue(persistor.checkIfIndexExists(client, entity1.getName()));
		
		//Should be false on a fake one
		assertFalse(persistor.checkIfIndexExists(client, UUID.randomUUID().toString()));
	}

	@Override
	public void testTemplates() throws Exception {
		ElasticSearchPersistor persistor = new ElasticSearchPersistor();
        TestEntity entity1 = new TestEntity();
        entity1.initialize();
        
        CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entity1);
        
        persistor.registerTemplate(template);
	}

	@Override
	public void testTemplatesInvalid() throws Exception {
		//Not using templates for now
	}

	@Override
	public void testAllDataTypes() throws Exception {
		//Not going to make this pass for now because we don't support binary into json at the moment
	}

	@Override
	public String getFieldValue(String key) throws CoalescePersistorException {
		//Field value of what? No entity is specified
		return null;
	}

    @Test
    public void testPersistRetrieveSearchEntity() throws Exception
    {
        ElasticSearchPersistor persister  = createPersister();
        GDELT_Test_Entity gdeltEntity = new GDELT_Test_Entity();

        // Prerequisite setup
        //persister.saveTemplate(CoalesceEntityTemplate.create(gdeltEntity));
        //CoalesceObjectFactory.register(GDELT_Test_Entity.class);

        // Persist

        persister.saveEntity(true, gdeltEntity);

        // Retrieve
        CoalesceEntity[] entities = persister.getEntity(gdeltEntity.getKey());
        assertEquals(1, entities.length);
        assertEquals(gdeltEntity.getKey(), entities[0].getKey());

        // These do not need escaped
        String idAttributeName = "GlobalEventID";
        String actorAttributeName = "Actor1Name";
    }

    @Test
    public void searchUpdateEntity() throws Exception
    {

        GDELT_Test_Entity nonGeoEntity = new GDELT_Test_Entity();

        // set fields
        Integer expectedInt = (new Random()).nextInt();

    }
    
    //Functional tests: these tests are mainly here for now just to test functionality and make sure
    //nothing is crashing. Not really "proper" tests yet
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testSaveEntity() throws Exception
    {
        // Create Entities
        TestEntity entity1 = new TestEntity();
        entity1.initialize();
        
        //ElasticSearch requires names be lowercase
        entity1.setName(entity1.getName().toLowerCase());

        ElasticSearchPersistor persistor = new ElasticSearchPersistor();

        // Save Entity1 (Should create a place holder for entity2)
        persistor.saveEntity(true, entity1);

    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testGet() throws Exception {
    	ElasticSearchPersistor persistor = new ElasticSearchPersistor();
        TestEntity entity1 = new TestEntity();
        entity1.initialize();
        
        //ElasticSearch requires names be lowercase
        entity1.setName(entity1.getName().toLowerCase());
        
        persistor.saveEntity(true, entity1);
    	
    	System.out.println(persistor.getEntity(entity1.getKey()).toString());
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testSearchAll() throws Exception {
    	ElasticSearchPersistorSearch persistor = new ElasticSearchPersistorSearch();
    	SearchResponse response = persistor.searchAll();
    	//assertNoFailures(response);
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testSearchSpecific() throws Exception {
    	ElasticSearchPersistorSearch persistor = new ElasticSearchPersistorSearch();
    	//Just entity name
    	persistor.searchSpecific("coalesce-oeevent","coalesce-oeevent");
    	//Entity name and one filter value
    	persistor.searchSpecificWithFilter("coalesce-oeevent","coalesce-oeevent","PMESIIPTMilitary", "1");
    }

    @Override
    protected ElasticSearchPersistor createPersister() throws CoalescePersistorException
    {
        return new ElasticSearchPersistor();
    }
    
    @Test
    public void testGetEntityTemplateKey() throws Exception {
    	ElasticSearchPersistor persistor = new ElasticSearchPersistor();
    	String key = persistor.getEntityTemplateKey("UNIT_TEST", "DSS", "1.0");
    	LOGGER.debug("Template key: " + key);
    }
    
    @Test
    public void testGetEntityTemplateMetadata() throws Exception {
    	ElasticSearchPersistor persistor = new ElasticSearchPersistor();
    	persistor.getEntityTemplateMetadata();
    }
}
