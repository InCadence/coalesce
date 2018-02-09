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
import org.elasticsearch.client.transport.TransportClient;
import org.junit.BeforeClass;
import org.junit.Test;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchDataConnector;
import com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchPersistor;
import com.incadencecorp.coalesce.framework.persistance.testobjects.GDELT_Test_Entity;

public class ElasticSearchPersistorIT extends AbstractCoalescePersistorTest<ElasticSearchPersistor> {

    private static ElasticSearchDataConnector conn;
    private static TransportClient client;
    
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
        conn = new ElasticSearchDataConnector();
    	client = conn.getDBConnector();
    }

    @Override
	public void registerEntities() {
		// We don't need to customize this yet... I think
		super.registerEntities();
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
        // Create Entities
        TestEntity entity1 = new TestEntity();
        entity1.initialize();
        
        //ElasticSearch requires names be lowercase
        entity1.setName(entity1.getName().toLowerCase());

        ElasticSearchPersistor persistor = new ElasticSearchPersistor();

        // Save Entity1 (Should create a place holder for entity2)
        persistor.saveEntity(true, entity1);
        
        DeleteResponse response = persistor.deleteObject(entity1);
        
        assertEquals(Result.DELETED, response.getResult());
	}

	@Override
	public void testRetrieveInvalidKey() throws Exception {
    	ElasticSearchPersistor persistor = new ElasticSearchPersistor();

    	//Note in order for test to pass, there shouldn't be any entity with random ID
    	List<String> keys = persistor.getCoalesceEntityKeysForEntityId(UUID.randomUUID().toString(), 
    			UUID.randomUUID().toString(), UUID.randomUUID().toString(), null);

    	//keys list should be null because it's not found
		assertNull(keys);
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
		//Not using templates for now
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
    	List<String> keys = persistor.getCoalesceEntityKeysForEntityId("twitter4", "tweet", "1", null);
    	
    	System.out.println(keys.toString());
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testSearchAll() throws Exception {
    	ElasticSearchPersistor persistor = new ElasticSearchPersistor();
    	persistor.searchAll();
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testSearchSpecific() throws Exception {
    	ElasticSearchPersistor persistor = new ElasticSearchPersistor();
    	persistor.searchSpecific("twitter4", "tweet");
    }

    @Override
    protected ElasticSearchPersistor createPersister() throws CoalescePersistorException
    {
        return new ElasticSearchPersistor();
    }
}
