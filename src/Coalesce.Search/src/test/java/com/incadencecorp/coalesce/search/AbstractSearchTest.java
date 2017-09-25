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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.sql.rowset.CachedRowSet;

import org.geotools.data.Query;
import org.junit.Assert;
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

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;

/**
 * This abstract test suite provides generic testing against persisters that
 * have implemented {@link ICoalesceSearchPersistor} to ensure basic
 * functionality. The {@link TestEntity} must have already been resgistered
 * before running these test.
 * 
 * @author n78554
 *
 * @param <T>
 */
public abstract class AbstractSearchTest<T extends ICoalescePersistor & ICoalesceSearchPersistor> {

    private static final FilterFactory FF = CoalescePropertyFactory.getFilterFactory();
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSearchTest.class);

    private boolean isInitialized = false;

    protected abstract T createPersister();

    /**
     * Registers the entities used by these tests.
     * 
     * @param persister
     */
    @Before
    public void registerEntities()
    {
        if (!isInitialized)
        {
            TestEntity entity = new TestEntity();
            entity.initialize();

            try
            {
                createPersister().registerTemplate(CoalesceEntityTemplate.create(entity));
            }
            catch (CoalescePersistorException | SAXException | IOException e)
            {
                LOGGER.warn("Failed to register templates");
            }

            isInitialized = true;
        }
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
        query = FF.and(FF.equal(CoalescePropertyFactory.getName(), FF.literal(TestEntity.NAME),true), query);

        List<PropertyName> properties = new ArrayList<PropertyName>();
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

        // 5 Default columns +1 parameter
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

        Assert.assertTrue(rowset.next());
        Assert.assertEquals(entity2.getKey(), rowset.getString(1));
        Assert.assertEquals(field2.getValue(), rowset.getString(5));
        Assert.assertTrue(rowset.next());
        Assert.assertEquals(entity1.getKey(), rowset.getString(1));
        Assert.assertEquals(field1.getValue(), rowset.getString(5));

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
    }

}
