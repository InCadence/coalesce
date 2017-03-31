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

import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import org.geotools.data.Query;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;

/**
 * This abstract test suite provides generic testing against persisters that
 * have implemented {@link ICoalesceSearchPersistor} to ensure basic
 * functionality.
 * 
 * @author n78554
 *
 * @param <T>
 */
public abstract class AbstractSearchTests<T extends ICoalescePersistor & ICoalesceSearchPersistor> {

    private static final FilterFactory FF = CoalescePropertyFactory.getFilterFactory();

    protected abstract T createPersister();

    /**
     * Execute a search with one parameter and specifying the search order.
     * Verifies the default columns are returned along with the specified
     * property and that the persister respecting the sort ordering.
     * 
     * @throws Exception
     */
    @Test
    public void searchMetadataWithSortingTest() throws Exception
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
        query = FF.and(FF.equals(CoalescePropertyFactory.getName(), FF.literal(TestEntity.NAME)), query);

        List<PropertyName> properties = new ArrayList<PropertyName>();
        properties.add(CoalescePropertyFactory.getFieldProperty(entity2.addRecord1().getStringField()));

        SortBy[] sortBy = new SortBy[1];
        sortBy[0] = FF.sort(properties.get(0).getPropertyName(), SortOrder.ASCENDING);

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
        Assert.assertEquals(6, rowset.getMetaData().getColumnCount());
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getEntityKey()),
                            rowset.getMetaData().getColumnName(1));
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getSource()),
                            rowset.getMetaData().getColumnName(2));
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getName()),
                            rowset.getMetaData().getColumnName(3));
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getEntityType()),
                            rowset.getMetaData().getColumnName(4));
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getEntityTitle()),
                            rowset.getMetaData().getColumnName(5));
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(properties.get(0)), rowset.getMetaData().getColumnName(6));

        rowset.next();
        Assert.assertEquals(entity1.getKey(), rowset.getString(1));
        Assert.assertEquals(field1.getValue(), rowset.getString(6));
        rowset.next();
        Assert.assertEquals(entity2.getKey(), rowset.getString(1));
        Assert.assertEquals(field2.getValue(), rowset.getString(6));

        // // Switch Sort Order
        sortBy[0] = FF.sort(properties.get(0).getPropertyName(), SortOrder.DESCENDING);

        results = persister.search(searchQuery);

        rowset = results.getResults();

        rowset.next();
        Assert.assertEquals(entity2.getKey(), rowset.getString(1));
        Assert.assertEquals(field2.getValue(), rowset.getString(6));
        rowset.next();
        Assert.assertEquals(entity1.getKey(), rowset.getString(1));
        Assert.assertEquals(field1.getValue(), rowset.getString(6));

    }

    /**
     * Executes a search with an invalid entity key to ensure that the persister
     * can handle no results.
     * 
     * @throws Exception
     */
    @Test
    public void searchNoResults() throws Exception
    {
        T persister = createPersister();

        // Create Query
        Filter query = FF.and(CoalescePropertyFactory.getEntityKey("AA"),
                              FF.equals(CoalescePropertyFactory.getName(), FF.literal(TestEntity.NAME)));

        Query searchQuery = new Query();
        searchQuery.setStartIndex(1);
        searchQuery.setMaxFeatures(200);
        searchQuery.setFilter(query);

        SearchResults results = persister.search(searchQuery);

        Assert.assertEquals(0, results.getTotal());

        CachedRowSet rowset = results.getResults();

        // 5 Default columns +1 parameter
        Assert.assertEquals(5, rowset.getMetaData().getColumnCount());
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getEntityKey()),
                            rowset.getMetaData().getColumnName(1));
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getSource()),
                            rowset.getMetaData().getColumnName(2));
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getName()),
                            rowset.getMetaData().getColumnName(3));
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getEntityType()),
                            rowset.getMetaData().getColumnName(4));
        Assert.assertEquals(CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getEntityTitle()),
                            rowset.getMetaData().getColumnName(5));
    }
}
