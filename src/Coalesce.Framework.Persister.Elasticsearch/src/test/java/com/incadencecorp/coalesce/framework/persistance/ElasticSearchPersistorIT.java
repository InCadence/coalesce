package com.incadencecorp.coalesce.framework.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.text.cql2.CQL;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.filter.Filter;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.CoalesceObjectFactory;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDateTimeField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchDataConnector;
import com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchPersistor;
import com.incadencecorp.coalesce.framework.persistance.testobjects.GDELT_Test_Entity;

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
		ElasticSearchDataConnector testConnector = new ElasticSearchDataConnector("test"); 
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

}
