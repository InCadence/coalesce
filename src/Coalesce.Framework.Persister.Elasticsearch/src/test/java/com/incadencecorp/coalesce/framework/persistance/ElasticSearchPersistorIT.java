package com.incadencecorp.coalesce.framework.persistance;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.CoalesceObjectFactory;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchDataConnector;
import com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchPersistor;
import com.incadencecorp.coalesce.framework.persistance.testobjects.GDELT_Test_Entity;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;

public class ElasticSearchPersistorIT extends CoalescePersistorBaseTest {

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

	@Override
	protected ServerConn getConnection() {
		return conn;
	}

	@Override
	protected ICoalescePersistor getPersistor(ServerConn conn) {
		ElasticSearchPersistor testPersistor = new ElasticSearchPersistor();
		return testPersistor;
	}

	@Override
	protected CoalesceDataConnectorBase getDataConnector(ServerConn conn) throws CoalescePersistorException {
		ElasticSearchDataConnector testConnector = new ElasticSearchDataConnector(); 
		return testConnector;
	}

    @Test
    public void testPersistRetrieveSearchEntity() throws Exception
    {
        GDELT_Test_Entity gdeltEntity = new GDELT_Test_Entity();

        // Prerequisite setup
        getFramework().saveCoalesceEntityTemplate(CoalesceEntityTemplate.create(gdeltEntity));
        CoalesceObjectFactory.register(GDELT_Test_Entity.class);

        // Persist

        getFramework().saveCoalesceEntity(false, gdeltEntity);

        // Retrieve
        CoalesceEntity[] entities = getFramework().getCoalesceEntities(gdeltEntity.getKey());
        assertEquals(1, entities.length);
        assertEquals(gdeltEntity.getKey(), entities[0].getKey());

        // These do not need escaped
        String idAttributeName = "GlobalEventID";
        String actorAttributeName = "Actor1Name";
    }

    @Test
    public void SearchUpdateEntity() throws Exception
    {

        GDELT_Test_Entity nonGeoEntity = new GDELT_Test_Entity();

        // set fields
        Integer expectedInt = (new Random()).nextInt();

        /*
        CoalesceRecord eventRecord = nonGeoEntity.getEventRecordSet().addNew();
        nonGeoEntity.setIntegerField(eventRecord, "GlobalEventID", expectedInt);
        nonGeoEntity.setStringField(eventRecord, "Actor1Name", "MERICA");

        DateTime expectedDateTime = new DateTime();
        ((CoalesceDateTimeField) eventRecord.getFieldByName("DateTime")).setValue(expectedDateTime);

        // Prerequisite setup
        getFramework().saveCoalesceEntityTemplate(CoalesceEntityTemplate.create(nonGeoEntity));
        CoalesceObjectFactory.register(GDELT_Test_Entity.class);

        // Persist
        // AccumuloPersistor persistor = createPersister();
        getFramework().saveCoalesceEntity(false, nonGeoEntity);

        // update
        nonGeoEntity.setStringField(eventRecord, "Actor1Name", "TEXAS");
        getFramework().saveCoalesceEntity(false, nonGeoEntity);
        */

    }
    
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

        // Verify Entity1 was saved
        /*rowset = persistor.search(query).getResults();

        Assert.assertEquals(properties.size() + 1, rowset.getMetaData().getColumnCount());
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getEntityKey()),
                            rowset.getMetaData().getColumnName(1));
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getName()),
                            rowset.getMetaData().getColumnName(2));
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getSource()),
                            rowset.getMetaData().getColumnName(3));
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getEntityType()),
                            rowset.getMetaData().getColumnName(4));
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getEntityTitle()),
                            rowset.getMetaData().getColumnName(5));
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(field1), rowset.getMetaData().getColumnName(6));

        // Create Query for Entity2
        query.setFilter(CoalescePropertyFactory.getEntityKey(entity2.getKey()));

        // Verify Entity2 place holder was created
        rowset = persistor.search(query).getResults();

        Assert.assertTrue(rowset.next());
        Assert.assertEquals(entity2.getKey(), rowset.getString(1));
        Assert.assertEquals(entity2.getName(), rowset.getString(2));
        Assert.assertEquals(entity2.getSource(), rowset.getString(3));
        Assert.assertNull(rowset.getString(4));
        Assert.assertNull(rowset.getString(5));
        Assert.assertNull(rowset.getString(6));

        // Save Entity2
        persistor.saveEntity(false, entity2);

        // Verify Entity2 was saved
        rowset = persistor.search(query).getResults();

        Assert.assertTrue(rowset.next());
        Assert.assertEquals(entity2.getKey(), rowset.getString(1));
        Assert.assertEquals(entity2.getTitle(), rowset.getString(5));
        Assert.assertEquals(Boolean.toString(field2.getValue()), rowset.getString(6));
        */

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
    	persistor.searchSpecific();
    }

}
