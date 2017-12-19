/*-----------------------------------------------------------------------------'
 Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.search;

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.EntityLinkHelper;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.geotools.data.Query;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * This abstract test suite provides generic testing against persisters that
 * have implemented {@link ICoalesceSearchPersistor} to ensure basic
 * functionality. The {@link TestEntity} must have already been resgistered
 * before running these test.
 *
 * @param <T>
 * @author n78554
 */
public abstract class AbstractSearchTest<T extends ICoalescePersistor & ICoalesceSearchPersistor> {

    private static final FilterFactory FF = CoalescePropertyFactory.getFilterFactory();
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSearchTest.class);

    private Boolean isInitialized = false;

    protected abstract T createPersister() throws CoalescePersistorException;

    /**
     * Registers the entities used by these tests.
     */
    @Before
    public void registerEntities()
    {
        synchronized (isInitialized)
        {
            if (!isInitialized)
            {
                TestEntity entity = new TestEntity();
                entity.initialize();

                try
                {
                    CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entity);

                    createPersister().registerTemplate(template);
                    CoalesceTemplateUtil.addTemplates(template);
                }
                catch (CoalescePersistorException | SAXException | IOException e)
                {
                    LOGGER.warn("Failed to register templates");
                }

                isInitialized = true;
            }
        }
    }

    /**
     * This test persist an entity that contains linkages and verifies that they linkages can be retrieved doing a search.
     *
     * @throws Exception
     */
    @Test
    public void searchLinkages() throws Exception
    {
        T persister = createPersister();

        // Create Entity
        TestEntity entity1 = new TestEntity();
        entity1.initialize();

        TestEntity entity2 = new TestEntity();
        entity2.initialize();

        // Link Entities
        EntityLinkHelper.linkEntitiesBiDirectional(entity1, ELinkTypes.IS_PARENT_OF, entity2);
        EntityLinkHelper.linkEntitiesBiDirectional(entity1, ELinkTypes.HAS_MEMBER, entity2);

        // Save Entity
        persister.saveEntity(false, entity1);

        List<PropertyName> properties = new ArrayList<>();
        properties.add(CoalescePropertyFactory.getLinkageLabel());
        properties.add(CoalescePropertyFactory.getLinkageStatus());
        properties.add(CoalescePropertyFactory.getLinkageType());

        Query query = new Query();
        query.setFilter(CoalescePropertyFactory.getLinkageEntityKey(entity2.getKey()));
        query.setProperties(properties);

        SearchResults results = persister.search(query);

        Assert.assertEquals(2, results.getTotal());
    }

    /**
     * Execute a search with one parameter and specifying the search order.
     * Verifies the default columns are returned along with the specified
     * property and that the persister respecting the sort ordering.
     *
     * @throws Exception
     */
    @Test
    public void testSearchWithSortingTest() throws Exception
    {
        T persister = createPersister();

        // Create Entity
        TestEntity entity1 = new TestEntity();
        entity1.initialize();

        CoalesceStringField field1 = entity1.addRecord1().getStringField();
        field1.setValue("AAA");

        // Create Entity
        TestEntity entity2 = new TestEntity();
        entity2.initialize();

        CoalesceStringField field2 = entity2.addRecord1().getStringField();
        field2.setValue("ZZZ");

        persister.saveEntity(false, entity1, entity2);

        // Create Query
        Filter query1 = CoalescePropertyFactory.getEntityKey(entity1.getKey());
        Filter query2 = CoalescePropertyFactory.getEntityKey(entity2.getKey());

        Filter query = FF.or(query1, query2);
        query = FF.and(FF.equal(CoalescePropertyFactory.getName(), FF.literal(TestEntity.NAME), true), query);

        List<PropertyName> properties = new ArrayList<>();
        properties.add(CoalescePropertyFactory.getName());
        properties.add(CoalescePropertyFactory.getSource());
        properties.add(CoalescePropertyFactory.getEntityTitle());
        properties.add(CoalescePropertyFactory.getFieldProperty(entity2.addRecord1().getStringField()));

        SortBy[] sortBy = new SortBy[1];
        sortBy[0] = FF.sort(properties.get(3).getPropertyName(), SortOrder.ASCENDING);

        Query searchQuery = new Query();
        searchQuery.setStartIndex(1);
        searchQuery.setMaxFeatures(200);
        searchQuery.setSortBy(sortBy);
        searchQuery.setProperties(properties);
        searchQuery.setFilter(query);

        SearchResults results = persister.search(searchQuery);

        Assert.assertEquals(2, results.getTotal());

        CachedRowSet rowset = results.getResults();

        // 4 Default columns +1 parameter
        Assert.assertEquals(properties.size() + 1, rowset.getMetaData().getColumnCount());
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getEntityKey()).toLowerCase(),
                            rowset.getMetaData().getColumnName(1).toLowerCase());

        for (int ii = 0; ii < properties.size(); ii++)
        {
            Assert.assertEquals(CoalescePropertyFactory.getColumnName(properties.get(ii)).toLowerCase(),
                                rowset.getMetaData().getColumnName(ii + 2).toLowerCase());
        }

        Assert.assertTrue(rowset.next());
        Assert.assertEquals(entity1.getKey(), rowset.getString(1));
        Assert.assertEquals(field1.getValue(), rowset.getString(5));
        Assert.assertTrue(rowset.next());
        Assert.assertEquals(entity2.getKey(), rowset.getString(1));
        Assert.assertEquals(field2.getValue(), rowset.getString(5));

        // // Switch Sort Order
        sortBy[0] = FF.sort(properties.get(3).getPropertyName(), SortOrder.DESCENDING);

        results = persister.search(searchQuery);

        rowset = results.getResults();

        // 4 Default columns +1 parameter
        Assert.assertEquals(properties.size() + 1, rowset.getMetaData().getColumnCount());
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getEntityKey()).toLowerCase(),
                            rowset.getMetaData().getColumnName(1).toLowerCase());

        for (int ii = 0; ii < properties.size(); ii++)
        {
            Assert.assertEquals(CoalescePropertyFactory.getColumnName(properties.get(ii)).toLowerCase(),
                                rowset.getMetaData().getColumnName(ii + 2).toLowerCase());
        }

        Assert.assertTrue(rowset.next());
        Assert.assertEquals(entity2.getKey(), rowset.getString(1));
        Assert.assertEquals(field2.getValue(), rowset.getString(5));
        Assert.assertTrue(rowset.next());
        Assert.assertEquals(entity1.getKey(), rowset.getString(1));
        Assert.assertEquals(field1.getValue(), rowset.getString(5));

        rowset.close();

        // Cleanup
        entity1.markAsDeleted();
        entity2.markAsDeleted();

        persister.saveEntity(true, entity1, entity2);
    }

    /**
     * Executes a search with an invalid entity key to ensure that the persister
     * can handle no results.
     *
     * @throws Exception
     */
    @Test
    public void testSearchNoResults() throws Exception
    {
        T persister = createPersister();

        // Create Query
        Filter query = FF.and(CoalescePropertyFactory.getEntityKey(UUID.randomUUID().toString()),
                              FF.equals(CoalescePropertyFactory.getName(), FF.literal(TestEntity.NAME)));

        Query searchQuery = new Query();
        searchQuery.setStartIndex(1);
        searchQuery.setMaxFeatures(200);
        searchQuery.setFilter(query);

        SearchResults results = persister.search(searchQuery);

        Assert.assertEquals(0, results.getTotal());

        CachedRowSet rowset = results.getResults();

        // 5 Default columns
        Assert.assertEquals(1, rowset.getMetaData().getColumnCount());
        Assert.assertFalse(rowset.next());

        rowset.close();
    }

    /**
     * Verifies that the number of requested properties submitted are returned even
     * if they are duplicate. This test was created to reveal an issue with Neo4j
     * and duplicate properties.
     *
     * @throws Exception
     */
    @Test
    public void testSearchDuplicateProperties() throws Exception
    {
        TestEntity entity = new TestEntity();
        entity.initialize();

        T persister = createPersister();
        persister.saveEntity(false, entity);

        List<PropertyName> properties = new ArrayList<>();
        properties.add(CoalescePropertyFactory.getName());
        properties.add(CoalescePropertyFactory.getName());

        Query searchQuery = new Query();
        searchQuery.setStartIndex(1);
        searchQuery.setMaxFeatures(200);
        searchQuery.setFilter(CoalescePropertyFactory.getEntityKey(entity.getKey()));
        searchQuery.setProperties(properties);

        SearchResults results = persister.search(searchQuery);

        Assert.assertEquals(1, results.getTotal());

        CachedRowSet rowset = results.getResults();

        // Verify EntityKey and two requested columns
        Assert.assertEquals(3, rowset.getMetaData().getColumnCount());
        Assert.assertTrue(rowset.next());

        rowset.close();

        entity.markAsDeleted();

        persister.saveEntity(true, entity);
    }

    /**
     * This test creates a entity with a geo location and verifies that a bounding box search will return it.
     *
     * @throws Exception
     */
    @Test
    public void testBoundingBoxSearch() throws Exception
    {
        T persistor = createPersister();

        Assume.assumeTrue(persistor.getCapabilities().contains(EPersistorCapabilities.GEOSPATIAL_SEARCH));
        Assume.assumeTrue(persistor.getCapabilities().contains(EPersistorCapabilities.TEMPORAL_SEARCH));

        TestEntity entity = new TestEntity();
        entity.initialize();
        TestRecord record = entity.addRecord1();

        // Create Record
        GeometryFactory factory = new GeometryFactory();
        record.getGeoField().setValue(factory.createPoint(new Coordinate(51.4347, -3.18)));
        record.getDateField().setValue(new DateTime(2006, 07, 25, 00, 00, 00, 00));
        record.getIntegerField().setValue(562505648);
        record.getStringField().setValue("EUROPE");

        // Persist
        persistor.saveEntity(false, entity);

        // Verify
        //CoalesceEntity[] entities = persistor.getEntity(entity.getKey());
        //Assert.assertEquals(1, entities.length);
        //Assert.assertEquals(entity.getKey(), entities[0].getKey());

        // Create Filter
        Filter cqlFilter = createFilter(CoalescePropertyFactory.getFieldProperty(record.getGeoField()).getPropertyName(),
                                        -180,
                                        -180,
                                        180,
                                        180,
                                        CoalescePropertyFactory.getFieldProperty(record.getDateField()).getPropertyName(),
                                        "2000-07-01T00:00:00.000Z",
                                        "2016-12-31T00:00:00.000Z",
                                        null);

        List<PropertyName> props = new ArrayList<>();
        props.add(CoalescePropertyFactory.getFieldProperty(record.getIntegerField()));
        props.add(CoalescePropertyFactory.getFieldProperty(record.getStringField()));

        // Create Query
        Query query = new Query(TestEntity.getTest1RecordsetName(), cqlFilter);
        query.setProperties(props);

        CachedRowSet results = persistor.search(query).getResults();
        Assert.assertTrue(results.size() > 0);
        Assert.assertTrue(results.next());
        Assert.assertEquals((int) record.getIntegerField().getValue(), results.getInt(2));
        Assert.assertEquals(record.getStringField().getValue(), results.getString(3));
        results.close();

        entity.markAsDeleted();

        persistor.saveEntity(true, entity);
    }

    /**
     * This test creates an entity and updates it. Then verifies that returning the updated property shows the updated value and not the original.
     *
     * @throws Exception
     */
    @Test
    public void testSearchUpdatedValues() throws Exception
    {
        T persistor = createPersister();

        // Create Entity
        TestEntity entity = new TestEntity();
        entity.initialize();

        // Create Record
        TestRecord record = entity.addRecord1();
        record.getIntegerField().setValue((new Random()).nextInt());
        record.getStringField().setValue("MERICA");
        record.getDateField().setValue(new DateTime());

        // Persist
        persistor.saveEntity(false, entity);

        // update
        record.getStringField().setValue("TEXAS");
        persistor.saveEntity(false, entity);

        String cql = "\"" + CoalescePropertyFactory.getFieldProperty(record.getIntegerField()).getPropertyName() + "\"="
                + record.getIntegerField().getValue();

        List<PropertyName> props = new ArrayList<>();
        props.add(CoalescePropertyFactory.getFieldProperty(record.getIntegerField()));
        props.add(CoalescePropertyFactory.getFieldProperty(record.getStringField()));

        // Create Query
        Query query = new Query(TestEntity.getTest1RecordsetName(), CQL.toFilter(cql));
        query.setProperties(props);

        // Search
        CachedRowSet results = persistor.search(query).getResults();

        // One and only 1 result
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(results.next());
        Assert.assertEquals((int) record.getIntegerField().getValue(), results.getInt(2));
        Assert.assertEquals(record.getStringField().getValue(), results.getString(3));

        results.close();

        entity.markAsDeleted();

        persistor.saveEntity(true, entity);
    }

    /**
     * This test verifies searching for entities w/o geospatial fields.
     *
     * @throws Exception
     */
    @Test
    public void testSearchNonGeoEntity() throws Exception
    {
        T persistor = createPersister();

        TestEntity entity = new TestEntity();
        entity.initialize();

        // set fields
        TestRecord record = entity.addRecord1();
        record.getIntegerField().setValue((new Random()).nextInt());
        record.getStringField().setValue("MERICA");
        record.getDateField().setValue(new DateTime());

        // Persist
        persistor.saveEntity(false, entity);

        String cql = "\"" + CoalescePropertyFactory.getFieldProperty(record.getIntegerField()).getPropertyName() + "\"="
                + record.getIntegerField().getValue();

        // Set up to return the GlobalEventID and Actor1Name fields
        List<PropertyName> props = new ArrayList<>();
        props.add(CoalescePropertyFactory.getFieldProperty(record.getIntegerField()));
        props.add(CoalescePropertyFactory.getFieldProperty(record.getStringField()));

        Query query = new Query(TestEntity.getTest1RecordsetName(), CQL.toFilter(cql));
        query.setProperties(props);

        CachedRowSet results = persistor.search(query).getResults();

        Assert.assertTrue(results.next());

        Assert.assertEquals((int) record.getIntegerField().getValue(), results.getInt(2));
        Assert.assertEquals(record.getStringField().getValue(), results.getString(3));
    }

    private static Filter createFilter(String geomField,
                                       double x0,
                                       double y0,
                                       double x1,
                                       double y1,
                                       String dateField,
                                       String t0,
                                       String t1,
                                       String attributesQuery) throws CQLException, IOException
    {

        String cqlGeometry = "BBOX(\"" + geomField + "\", " + x0 + ", " + y0 + ", " + x1 + ", " + y1 + ")";
        String cqlDates = "(\"" + dateField + "\" DURING " + t0 + "/" + t1 + ")";
        String cqlAttributes = attributesQuery == null ? "INCLUDE" : attributesQuery;
        String cql = cqlGeometry + " AND " + cqlDates + " AND " + cqlAttributes;
        return CQL.toFilter(cql);
    }

}
