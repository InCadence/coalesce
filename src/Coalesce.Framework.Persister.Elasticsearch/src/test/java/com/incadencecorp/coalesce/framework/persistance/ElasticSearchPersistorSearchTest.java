package com.incadencecorp.coalesce.framework.persistance;

import java.io.InputStream;
import java.util.Properties;

import javax.sql.rowset.CachedRowSet;

import org.junit.BeforeClass;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchDataConnector;
import com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchPersistor;
import com.incadencecorp.coalesce.search.AbstractSearchTest;

public class ElasticSearchPersistorSearchTest extends AbstractSearchTest<ElasticSearchPersistor> {

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
	protected ElasticSearchPersistor createPersister() throws CoalescePersistorException {
		
		return new ElasticSearchPersistor();
	}

	@Override
	public void registerEntities() {
		// TODO Auto-generated method stub
		try {
			super.registerEntities();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void searchAllDataTypes() throws Exception {
		// TODO Auto-generated method stub
		super.searchAllDataTypes();
	}

	@Override
	public void searchLinkages() throws Exception {
		// TODO Auto-generated method stub
		super.searchLinkages();
	}

	@Override
	public void testSearchWithSortingTest() throws Exception {
		// TODO Auto-generated method stub
		super.testSearchWithSortingTest();
	}

	@Override
	public void testSearchNoResults() throws Exception {
		// TODO Auto-generated method stub
		super.testSearchNoResults();
	}

	@Override
	public void testSearchDuplicateProperties() throws Exception {
		// TODO Auto-generated method stub
		super.testSearchDuplicateProperties();
	}

	@Override
	public void testReverseOrder() throws Exception {
		// TODO Auto-generated method stub
		super.testReverseOrder();
	}

	@Override
	public void testMatchCase() throws Exception {
		// TODO Auto-generated method stub
		super.testMatchCase();
	}

	@Override
	public void testBoundingBoxSearch() throws Exception {
		// TODO Auto-generated method stub
		super.testBoundingBoxSearch();
	}

	@Override
	public void testSearchUpdatedValues() throws Exception {
		// TODO Auto-generated method stub
		super.testSearchUpdatedValues();
	}

	@Override
	public void testSearchNonGeoEntity() throws Exception {
		// TODO Auto-generated method stub
		super.testSearchNonGeoEntity();
	}
}
