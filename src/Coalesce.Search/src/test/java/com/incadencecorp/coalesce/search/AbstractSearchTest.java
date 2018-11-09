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
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.EntityLinkHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCoordinateField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCoordinateListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLineStringField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalescePolygonField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.datamodel.TestRecord;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;
import org.geotools.data.Query;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.temporal.object.DefaultInstant;
import org.geotools.temporal.object.DefaultPeriod;
import org.geotools.temporal.object.DefaultPosition;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.temporal.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.sql.SQLException;
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

    private static final FilterFactory2 FF = CoalescePropertyFactory.getFilterFactory();
    private static final GeometryFactory GF = new GeometryFactory();
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSearchTest.class);
    private static final WKTReader WKT_READER = new WKTReader();
    private static final String EPSG4326 = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]]";

    private static final Random RANDOM = new Random();

    private CoordinateReferenceSystem crs;
    private Boolean isInitialized = false;
    private Object SYNC_INIT = new Object();

    protected abstract T createPersister() throws CoalescePersistorException;

    /**
     * Registers the entities used by these tests.
     */
    @Before
    public void registerEntities() throws Exception
    {
        synchronized (SYNC_INIT)
        {
            if (!isInitialized)
            {
                crs = CRS.parseWKT(EPSG4326);

                TestEntity entity = new TestEntity();
                entity.initialize();

                try
                {
                    CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entity);

                    createPersister().registerTemplate(template);
                    CoalesceTemplateUtil.addTemplates(template);
                }
                catch (CoalesceException e)
                {
                    LOGGER.warn("Failed to register templates");
                }

                isInitialized = true;
            }
        }
    }

    /**
     * This test ensures that you are able to request the record key to be returned as a property.
     */
    @Test
    public void testReturningRecordKey() throws Exception
    {

        TestEntity entity = new TestEntity();
        entity.initialize();
        TestRecord record = entity.addRecord1();

        T persister = createPersister();
        persister.saveEntity(false, entity);

        List<PropertyName> properties = new ArrayList<>();
        properties.add(CoalescePropertyFactory.getName());
        properties.add(CoalescePropertyFactory.getRecordKey(entity.getRecordset1().getName()));

        Query searchQuery = new Query();
        searchQuery.setStartIndex(1);
        searchQuery.setMaxFeatures(200);
        searchQuery.setFilter(CoalescePropertyFactory.getEntityKey(entity.getKey()));
        searchQuery.setProperties(properties);

        SearchResults results = persister.search(searchQuery);

        Assert.assertEquals(1, results.getTotal());

        CachedRowSet rowset = results.getResults();

        // Verify EntityKey and two requested columns
        Assert.assertEquals(properties.size() + 1, rowset.getMetaData().getColumnCount());
        Assert.assertTrue(rowset.next());
        Assert.assertEquals(entity.getKey(), rowset.getObject(1));
        Assert.assertEquals(entity.getName(), rowset.getObject(2));
        Assert.assertEquals(record.getKey(), rowset.getObject(3));

        rowset.close();

        entity.markAsDeleted();

        persister.saveEntity(true, entity);
    }

    /**
     * This test ensures that filters including a UUID field work
     */
    @Test
    public void testFilteringOnUUID() throws Exception
    {

        TestEntity entity = new TestEntity();
        entity.initialize();
        TestRecord record = entity.addRecord1();
        record.getGuidField().setValue(UUID.randomUUID());

        T persister = createPersister();
        persister.saveEntity(false, entity);

        Filter filter = FF.equals(CoalescePropertyFactory.getFieldProperty(record.getGuidField()),
                                  FF.literal(record.getGuidField().getValue()));

        Query searchQuery = new Query();
        searchQuery.setStartIndex(1);
        searchQuery.setMaxFeatures(200);
        searchQuery.setFilter(FF.and(filter, CoalescePropertyFactory.getEntityKey(entity.getKey())));

        SearchResults results = persister.search(searchQuery);

        Assert.assertEquals(1, results.getTotal());

        try (CachedRowSet rowset = results.getResults())
        {
            // Verify EntityKey and two requested columns
            Assert.assertTrue(rowset.next());
            Assert.assertEquals(entity.getKey(), rowset.getObject(1));
        }

        entity.markAsDeleted();

        persister.saveEntity(true, entity);
    }

    /**
     * This test ensures that filters including a Boolean fields work
     */
    @Test
    public void testFilteringOnBoolean() throws Exception
    {
        TestEntity entity = new TestEntity();
        entity.initialize();
        TestRecord record = entity.addRecord1();
        record.getBooleanField().setValue(true);

        T persister = createPersister();
        persister.saveEntity(false, entity);

        Filter filter = FF.equals(CoalescePropertyFactory.getFieldProperty(record.getBooleanField()),
                                  FF.literal(record.getBooleanField().getValue()));

        Query searchQuery = new Query();
        searchQuery.setStartIndex(1);
        searchQuery.setMaxFeatures(200);
        searchQuery.setFilter(FF.and(filter, CoalescePropertyFactory.getEntityKey(entity.getKey())));
        searchQuery.setPropertyNames(new String[] { CoalescePropertyFactory.getEntityKey().getPropertyName() });

        SearchResults results = persister.search(searchQuery);

        Assert.assertEquals(1, results.getTotal());

        try (CachedRowSet rowset = results.getResults())
        {
            // Verify EntityKey and two requested columns
            Assert.assertTrue(rowset.next());
            Assert.assertEquals(entity.getKey(), rowset.getObject(1));
        }

        entity.markAsDeleted();

        persister.saveEntity(true, entity);
    }

    @Test
    public void searchAllDataTypes() throws Exception
    {
        T persister = createPersister();

        // Create Entity
        TestEntity entity = new TestEntity();
        entity.initialize();

        Coordinate[] coords = new Coordinate[] { new Coordinate(0, 0), new Coordinate(1, 2), new Coordinate(2, 0),
                                                 new Coordinate(0, 0)
        };

        // Create Record
        TestRecord record = entity.addRecord1();
        record.getCircleField().setValue(GF.createPoint(coords[0]), 5.25);
        record.getPolygonField().setValue(GF.createPolygon(coords));
        record.getLineField().setValue(GF.createLineString(coords));
        record.getGeoField().setValue(coords[1]);
        record.getGeoListField().setValue(GF.createMultiPoint(coords));
        record.getIntegerField().setValue(Integer.MAX_VALUE);
        record.getIntegerListField().setValue(new int[] { 3, 4, Integer.MIN_VALUE, Integer.MAX_VALUE });
        record.getLongField().setValue(Long.MAX_VALUE);
        record.getLongListField().setValue(new long[] { 3, 4, Long.MIN_VALUE, Long.MAX_VALUE });
        record.getStringField().setValue("Test String");
        record.getStringListField().setValue(new String[] { "A", "B", "C" });
        record.getFloatField().setValue(Float.MAX_VALUE);
        record.getFloatListField().setValue(new float[] { 3.145964f, Float.MIN_VALUE, Float.MAX_VALUE });
        record.getDoubleField().setValue(Double.MAX_VALUE);
        record.getDoubleListField().setValue(new double[] { 3.145964, Double.MIN_VALUE, Double.MAX_VALUE });
        record.getBooleanField().setValue(true);
        record.getDateField().setValue(JodaDateTimeHelper.nowInUtc());
        record.getGuidField().setValue(UUID.randomUUID());
        record.getGUIDListField().setValue(new UUID[] { UUID.randomUUID(), UUID.randomUUID() });
        record.getEnumerationField().setValue(1);
        record.getEnumerationListField().setValue(new int[] { 1, 2 });

        // Persist Entity
        persister.saveEntity(false, entity);

        // Create Return Properties
        List<PropertyName> properties = new ArrayList<>();
        properties.add(CoalescePropertyFactory.getFieldProperty(record.getIntegerField()));
        properties.add(CoalescePropertyFactory.getFieldProperty(record.getLongField()));
        properties.add(CoalescePropertyFactory.getFieldProperty(record.getStringField()));
        properties.add(CoalescePropertyFactory.getFieldProperty(record.getFloatField()));
        properties.add(CoalescePropertyFactory.getFieldProperty(record.getDoubleField()));
        properties.add(CoalescePropertyFactory.getFieldProperty(record.getBooleanField()));
        properties.add(CoalescePropertyFactory.getFieldProperty(record.getDateField()));
        properties.add(CoalescePropertyFactory.getFieldProperty(record.getGuidField()));
        properties.add(CoalescePropertyFactory.getFieldProperty(record.getEnumerationField()));

        // List Fields
        properties.add(CoalescePropertyFactory.getFieldProperty(record.getIntegerListField()));
        properties.add(CoalescePropertyFactory.getFieldProperty(record.getLongListField()));
        properties.add(CoalescePropertyFactory.getFieldProperty(record.getStringListField()));
        properties.add(CoalescePropertyFactory.getFieldProperty(record.getFloatListField()));
        properties.add(CoalescePropertyFactory.getFieldProperty(record.getDoubleListField()));
        properties.add(CoalescePropertyFactory.getFieldProperty(record.getEnumerationListField()));

        // Geometry Fields
        properties.add(CoalescePropertyFactory.getFieldProperty(record.getCircleField()));
        properties.add(CoalescePropertyFactory.getFieldProperty(record.getPolygonField()));
        properties.add(CoalescePropertyFactory.getFieldProperty(record.getLineField()));
        properties.add(CoalescePropertyFactory.getFieldProperty(record.getGeoListField()));
        properties.add(CoalescePropertyFactory.getFieldProperty(record.getGeoField()));

        // Create Query
        Query query = new Query();
        query.setFilter(CoalescePropertyFactory.getEntityKey(entity.getKey()));
        query.setProperties(properties);

        SearchResults results = persister.search(query);

        Assert.assertEquals(1, results.getTotal());

        CachedRowSet rowset = results.getResults();

        Assert.assertTrue(rowset.next());

        if (LOGGER.isTraceEnabled())
        {
            for (int ii = 1; ii <= rowset.getMetaData().getColumnCount(); ii++)
            {
                LOGGER.trace("{} ({}:{})={}",
                             rowset.getMetaData().getColumnName(ii),
                             ii,
                             rowset.getMetaData().getColumnType(ii),
                             rowset.getString(ii));
            }
        }

        // Verify
        assertField(rowset, record.getIntegerField());
        assertField(rowset, record.getLongField());
        assertField(rowset, record.getStringField());
        assertField(rowset, record.getFloatField());
        assertField(rowset, record.getDoubleField());
        assertField(rowset, record.getBooleanField());
        assertField(rowset, record.getGuidField());
        assertField(rowset, record.getEnumerationField());
        assertField(rowset, record.getDateField());

        // Verify List
        assertField(rowset, record.getIntegerListField());
        assertField(rowset, record.getLongListField());
        assertField(rowset, record.getStringListField());
        assertField(rowset, record.getFloatListField());
        assertField(rowset, record.getDoubleListField());
        assertField(rowset, record.getEnumerationListField());

        // Verify Geometry
        assertField(rowset, record.getCircleField());
        assertField(rowset, record.getPolygonField());
        assertField(rowset, record.getLineField());
        assertField(rowset, record.getGeoListField());
        assertField(rowset, record.getGeoField());

        entity.markAsDeleted();

        // Cleanup
        persister.saveEntity(true, entity);
    }

    private void assertField(CachedRowSet rowset, CoalesceField field) throws Exception
    {
        String column = CoalescePropertyFactory.getColumnName(field);
        String value = rowset.getString(column);

        Assert.assertNotNull(value);

        switch (field.getDataType())
        {
        case GUID_TYPE:
            Assert.assertEquals(UUID.fromString(field.getBaseValue()), UUID.fromString(value));
            break;
        case URI_TYPE:
        case STRING_TYPE:
            Assert.assertEquals(field.getBaseValue(), value);
            break;
        case DATE_TIME_TYPE:
            //Assert.assertEquals(((CoalesceDateTimeField) field).getValue().toLocalDate().toString(),
            //                    rowset.getDate(column).toString());
            /* TODO Timezone and milliseconds are being stripped causing this test to fail.
            Assert.assertEquals(((CoalesceDateTimeField) field).getValue().toLocalTime().toString(),
                                rowset.getTime(column).toString());
                                */
            break;
        case BOOLEAN_TYPE:
            Assert.assertEquals(field.getValue(), rowset.getBoolean(column));
            break;
        case ENUMERATION_TYPE:
        case INTEGER_TYPE:
            Assert.assertEquals((int) field.getValue(), rowset.getInt(column));
            break;
        case DOUBLE_TYPE:
            Assert.assertEquals((double) field.getValue(), rowset.getDouble(column), 0);
            break;
        case FLOAT_TYPE:
            Assert.assertEquals((float) field.getValue(), rowset.getFloat(column), 0);
            break;
        case LONG_TYPE:
            Assert.assertEquals((long) field.getValue(), rowset.getLong(column));
            break;
        case GEOCOORDINATE_TYPE:
            Point point = (Point) WKT_READER.read(value.replaceAll("[Z]", ""));
            Assert.assertEquals(((CoalesceCoordinateField) field).getValue(), point.getCoordinate());
            break;
        case GEOCOORDINATE_LIST_TYPE:
            MultiPoint multipoint = (MultiPoint) WKT_READER.read(value.replaceAll("[Z]", ""));
            Assert.assertArrayEquals(((CoalesceCoordinateListField) field).getValue(), multipoint.getCoordinates());
            break;
        case LINE_STRING_TYPE:
            LineString line = (LineString) WKT_READER.read(value.replaceAll("[Z]", ""));
            Assert.assertEquals(((CoalesceLineStringField) field).getValue(), line);
            break;
        case POLYGON_TYPE:
            Polygon polygon = (Polygon) WKT_READER.read(value.replaceAll("[Z]", ""));
            Assert.assertEquals(((CoalescePolygonField) field).getValue(), polygon);
            break;
        case CIRCLE_TYPE:
            /* TODO Postgres converts a circle into a polygon so there wont be a direct comparison.
            Point center = (Point) WKT_READER.read(value.replaceAll("[Z]", ""));
            Assert.assertEquals(((CoalesceCircleField) field).getValue(), center.getCoordinate());
            */
            break;
        case BOOLEAN_LIST_TYPE:
        case INTEGER_LIST_TYPE:
        case GUID_LIST_TYPE:
        case DOUBLE_LIST_TYPE:
        case FLOAT_LIST_TYPE:
        case LONG_LIST_TYPE:
        case ENUMERATION_LIST_TYPE:
        case STRING_LIST_TYPE:
            Assert.assertEquals(field.getBaseValue(), value);
            break;
        case BINARY_TYPE:
        case FILE_TYPE:
            // Do Nothing
            break;
        }
    }

    /**
     * This test persist an entity that contains linkages and verifies that the linkages can be retrieved doing a search.
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

        CoalesceLinkage linkage = entity1.getLinkage(ELinkTypes.IS_PARENT_OF);
        linkage.setLabel("Hello World");

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

        CachedRowSet rowset = results.getResults();
        Assert.assertTrue(rowset.next());

        // Create Filter w/ Type and Label
        List<Filter> filters = new ArrayList<>();
        filters.add(CoalescePropertyFactory.getLinkageEntityKey(entity2.getKey()));
        // TODO OEBDP-118 Needs to be resolved before this can be uncommented. (Inconsistent handling of LinkType and Status between Elastic & Accumulo)
        //filters.add(FF.equals(CoalescePropertyFactory.getLinkageType(), FF.literal(ELinkTypes.IS_PARENT_OF.getLabel())));
        filters.add(FF.equals(CoalescePropertyFactory.getLinkageLabel(), FF.literal(linkage.getLabel())));

        query = new Query();
        query.setFilter(FF.and(filters));
        query.setProperties(properties);

        results = persister.search(query);
        rowset = results.getResults();

        // Verify
        Assert.assertEquals(1, results.getTotal());
        Assert.assertTrue(rowset.next());
        Assert.assertEquals(linkage.getLabel(), rowset.getString(2));
    }

    /**
     * This test ensures that queries can be sorted based on DateCreated and DateLastModified
     */
    @Test
    public void testTemporalSorting() throws Exception
    {
        T persister = createPersister();

        DateTime now = JodaDateTimeHelper.nowInUtc();

        // Create Entities
        TestEntity entity1 = new TestEntity();
        entity1.initialize();
        entity1.setDateCreated(now.minusMinutes(1));
        entity1.setLastModified(now);

        TestEntity entity2 = new TestEntity();
        entity2.initialize();
        entity2.setDateCreated(now.minusMinutes(10));
        entity2.setLastModified(now.minusMinutes(5));

        // Save Entities
        persister.saveEntity(false, entity1, entity2);

        // Create Query
        List<Filter> filters = new ArrayList<>();
        filters.add(CoalescePropertyFactory.getEntityKey(entity1.getKey()));
        filters.add(CoalescePropertyFactory.getEntityKey(entity2.getKey()));

        // Properties to Return
        List<PropertyName> properties = new ArrayList<>();
        properties.add(CoalescePropertyFactory.getDateCreated());
        properties.add(CoalescePropertyFactory.getLastModified());

        // Create Query
        Query searchQuery = new Query();
        searchQuery.setStartIndex(1);
        searchQuery.setMaxFeatures(200);
        searchQuery.setProperties(properties);
        searchQuery.setFilter(FF.or(filters));

        // Sort DateCreated ASC
        searchQuery.setSortBy(new SortBy[] { FF.sort(properties.get(0).getPropertyName(), SortOrder.ASCENDING) });
        verifyOrder(persister.search(searchQuery), entity2.getKey(), entity1.getKey());

        // Sort DateCreated DESC
        searchQuery.setSortBy(new SortBy[] { FF.sort(properties.get(0).getPropertyName(), SortOrder.DESCENDING) });
        verifyOrder(persister.search(searchQuery), entity1.getKey(), entity2.getKey());

        // Sort LastModified ASC
        searchQuery.setSortBy(new SortBy[] { FF.sort(properties.get(1).getPropertyName(), SortOrder.ASCENDING) });
        verifyOrder(persister.search(searchQuery), entity2.getKey(), entity1.getKey());

        // Sort LastModified DESC
        searchQuery.setSortBy(new SortBy[] { FF.sort(properties.get(1).getPropertyName(), SortOrder.DESCENDING) });
        verifyOrder(persister.search(searchQuery), entity1.getKey(), entity2.getKey());
    }

    private void verifyOrder(SearchResults results, String... keys) throws SQLException
    {
        Assert.assertEquals(keys.length, results.getTotal());

        CachedRowSet rowset = results.getResults();

        for (String key : keys)
        {
            Assert.assertTrue(rowset.next());
            Assert.assertEquals(key, rowset.getString(1));
        }
    }

    /**
     * Execute a search with one parameter and specifying the search order.
     * Verifies the default columns are returned along with the specified
     * property and that the persister respecting the sort ordering.
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
     * @throws Exception on error
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
        properties.add(CoalescePropertyFactory.getEntityKey());

        Query searchQuery = new Query();
        searchQuery.setStartIndex(1);
        searchQuery.setMaxFeatures(200);
        searchQuery.setFilter(CoalescePropertyFactory.getEntityKey(entity.getKey()));
        searchQuery.setProperties(properties);

        SearchResults results = persister.search(searchQuery);

        Assert.assertEquals(1, results.getTotal());

        CachedRowSet rowset = results.getResults();

        // Verify EntityKey and two requested columns
        Assert.assertEquals(properties.size() + 1, rowset.getMetaData().getColumnCount());
        Assert.assertTrue(rowset.next());

        rowset.close();

        entity.markAsDeleted();

        persister.saveEntity(true, entity);
    }

    /**
     * Verifies that the persister can handle the property name being specified on the right instead of the left.
     *
     * @throws Exception on error
     */
    @Test
    public void testReverseOrder() throws Exception
    {
        TestEntity entity = new TestEntity();
        entity.initialize();

        TestRecord record = entity.addRecord1();
        record.getStringField().setValue("Hello");

        T persister = createPersister();
        persister.saveEntity(false, entity);

        List<PropertyName> properties = new ArrayList<>();

        PropertyName property = CoalescePropertyFactory.getFieldProperty(record.getStringField());
        Expression value = FF.literal(record.getStringField().getValue());

        Query searchQuery = new Query();
        searchQuery.setStartIndex(1);
        searchQuery.setMaxFeatures(200);
        searchQuery.setFilter(FF.and(CoalescePropertyFactory.getEntityKey(entity.getKey()),
                                     FF.equal(value, property, true)));
        searchQuery.setProperties(properties);

        SearchResults results = persister.search(searchQuery);
        Assert.assertEquals(1, results.getTotal());

        try (CachedRowSet rowset = results.getResults())
        {
            // Verify EntityKey and two requested columns
            Assert.assertEquals(properties.size() + 1, rowset.getMetaData().getColumnCount());
            Assert.assertTrue(rowset.next());
        }

        searchQuery.setFilter(FF.and(CoalescePropertyFactory.getEntityKey(entity.getKey()),
                                     FF.equal(value, property, false)));

        results = persister.search(searchQuery);
        Assert.assertEquals(1, results.getTotal());

        entity.markAsDeleted();

        persister.saveEntity(true, entity);
    }

    /**
     * Verifies that the perisster can handle the match case flag.
     *
     * @throws Exception on error
     */
    @Test
    public void testMatchCase() throws Exception
    {
        TestEntity entity = new TestEntity();
        entity.initialize();

        TestRecord record = entity.addRecord1();
        record.getStringField().setValue("Hello");

        T persister = createPersister();

        Assume.assumeTrue(persister.getCapabilities().contains(EPersistorCapabilities.CASE_INSENSITIVE_SEARCH));

        persister.saveEntity(false, entity);

        List<PropertyName> properties = new ArrayList<>();

        PropertyName property = CoalescePropertyFactory.getFieldProperty(record.getStringField());
        Expression value = FF.literal(record.getStringField().getValue().toLowerCase());

        Query searchQuery = new Query();
        searchQuery.setStartIndex(1);
        searchQuery.setMaxFeatures(200);
        searchQuery.setFilter(FF.and(CoalescePropertyFactory.getEntityKey(entity.getKey()),
                                     FF.equal(property, value, true)));
        searchQuery.setProperties(properties);

        SearchResults results = persister.search(searchQuery);
        Assert.assertEquals(0, results.getTotal());

        searchQuery.setFilter(FF.and(CoalescePropertyFactory.getEntityKey(entity.getKey()),
                                     FF.equal(value, property, false)));

        results = persister.search(searchQuery);
        Assert.assertEquals(1, results.getTotal());

        entity.markAsDeleted();

        persister.saveEntity(true, entity);
    }

    /**
     * This test creates a entity with a geo location and verifies that a bounding box search will return it.
     */
    @Test
    public void testBoundingBoxSearch() throws Exception
    {
        T persistor = createPersister();

        Assume.assumeTrue(persistor.getCapabilities().contains(EPersistorCapabilities.GEOSPATIAL_SEARCH));

        TestEntity entity = new TestEntity();
        entity.initialize();
        TestRecord record = entity.addRecord1();

        // Create Record
        record.getGeoField().setValue(GF.createPoint(new Coordinate(51.4347, -3.18)));
        //record.getDateField().setValue(new DateTime(2006, 07, 25, 00, 00, 00, 00));
        record.getIntegerField().setValue(562505648);
        record.getStringField().setValue("EUROPE");

        // Persist
        Assert.assertTrue(persistor.saveEntity(false, entity));

        // Create Filter
        Coordinate point1 = new Coordinate(40, -5);
        Coordinate point2 = new Coordinate(60, 0);

        ReferencedEnvelope bbox = new ReferencedEnvelope(createSquare(point1, point2).getEnvelopeInternal(), crs);

        List<PropertyName> props = new ArrayList<>();
        props.add(CoalescePropertyFactory.getFieldProperty(record.getIntegerField()));
        props.add(CoalescePropertyFactory.getFieldProperty(record.getStringField()));

        // Create Query
        Query query = new Query();
        query.setFilter(FF.and(CoalescePropertyFactory.getEntityKey(entity.getKey()),
                               FF.bbox(CoalescePropertyFactory.getFieldProperty(record.getGeoField()), bbox)));
        query.setProperties(props);

        // Verify entity is returned
        try (CachedRowSet results = persistor.search(query).getResults())
        {
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.next());
            Assert.assertEquals((int) record.getIntegerField().getValue(), results.getInt(2));
            Assert.assertEquals(record.getStringField().getValue(), results.getString(3));
        }

        // Create bound box filter that excludes the field
        bbox = new ReferencedEnvelope(createSquare(new Coordinate(59, -5), point2).getEnvelopeInternal(), crs);
        query.setFilter(FF.and(CoalescePropertyFactory.getEntityKey(entity.getKey()),
                               FF.bbox(CoalescePropertyFactory.getFieldProperty(record.getGeoField()), bbox)));

        // Verify entity is not returned
        try (CachedRowSet results = persistor.search(query).getResults())
        {
            Assert.assertEquals(0, results.size());
        }

        entity.markAsDeleted();

        persistor.saveEntity(true, entity);
    }

    private Polygon createSquare(Coordinate p1, Coordinate p2)
    {
        return GF.createPolygon(new Coordinate[] { p1, new Coordinate(p1.x, p2.y), p2, new Coordinate(p2.x, p1.y), p1 });
    }

    /**
     * This test verifies that the between filter works on an integer field.
     */
    @Test
    public void testBetweenFilter() throws Exception
    {
        T persistor = createPersister();

        TestEntity entity = new TestEntity();
        entity.initialize();
        TestRecord record = entity.addRecord1();

        // Create Record
        record.getGeoField().setValue(GF.createPoint(new Coordinate(51.4347, -3.18)));
        record.getDateField().setValue(JodaDateTimeHelper.nowInUtc());
        record.getIntegerField().setValue(10);
        record.getStringField().setValue("EUROPE");

        // Persist
        Assert.assertTrue(persistor.saveEntity(false, entity));

        Filter filter = FF.between(CoalescePropertyFactory.getFieldProperty(record.getIntegerField()),
                                   FF.literal(5),
                                   FF.literal(15));

        List<PropertyName> props = new ArrayList<>();
        props.add(CoalescePropertyFactory.getFieldProperty(record.getIntegerField()));
        props.add(CoalescePropertyFactory.getFieldProperty(record.getStringField()));

        // Create Query
        Query query = new Query();
        query.setFilter(FF.and(CoalescePropertyFactory.getEntityKey(entity.getKey()), filter));
        query.setProperties(props);

        try (CachedRowSet results = persistor.search(query).getResults())
        {
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.next());
            Assert.assertEquals((int) record.getIntegerField().getValue(), results.getInt(2));
            Assert.assertEquals(record.getStringField().getValue(), results.getString(3));
        }

        filter = FF.between(CoalescePropertyFactory.getFieldProperty(record.getIntegerField()),
                            FF.literal(0),
                            FF.literal(5));
        query.setFilter(FF.and(CoalescePropertyFactory.getEntityKey(entity.getKey()), filter));
        try (CachedRowSet results = persistor.search(query).getResults())
        {
            Assert.assertEquals(0, results.size());
        }

        entity.markAsDeleted();

        persistor.saveEntity(true, entity);
    }

    /**
     * This test ensure that the during operand works as expected.
     */
    @Test
    public void testDuringFilter() throws Exception
    {
        T persistor = createPersister();

        Assume.assumeTrue(persistor.getCapabilities().contains(EPersistorCapabilities.TEMPORAL_SEARCH));

        TestEntity entity = new TestEntity();
        entity.initialize();
        TestRecord record = entity.addRecord1();

        // Create Record
        record.getDateField().setValue(JodaDateTimeHelper.nowInUtc());
        record.getIntegerField().setValue(562505648);
        record.getStringField().setValue("EUROPE");

        // Persist
        Assert.assertTrue(persistor.saveEntity(false, entity));

        Instant start = new DefaultInstant(new DefaultPosition(record.getDateField().getValue().minusDays(1).toDate()));
        Instant end = new DefaultInstant(new DefaultPosition(record.getDateField().getValue().plusDays(1).toDate()));

        Filter filter = FF.during(CoalescePropertyFactory.getFieldProperty(record.getDateField()),
                                  FF.literal(new DefaultPeriod(start, end)));

        List<PropertyName> props = new ArrayList<>();
        props.add(CoalescePropertyFactory.getFieldProperty(record.getIntegerField()));
        props.add(CoalescePropertyFactory.getFieldProperty(record.getStringField()));

        // Create Query
        Query query = new Query();
        query.setFilter(FF.and(CoalescePropertyFactory.getEntityKey(entity.getKey()), filter));
        query.setProperties(props);

        // Verify entity is returned
        try (CachedRowSet results = persistor.search(query).getResults())
        {
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.next());
            Assert.assertEquals((int) record.getIntegerField().getValue(), results.getInt(2));
            Assert.assertEquals(record.getStringField().getValue(), results.getString(3));
        }

        start = new DefaultInstant(new DefaultPosition(record.getDateField().getValue().minusDays(5).toDate()));
        end = new DefaultInstant(new DefaultPosition(record.getDateField().getValue().minusDays(2).toDate()));

        filter = FF.during(CoalescePropertyFactory.getFieldProperty(record.getDateField()),
                           FF.literal(new DefaultPeriod(start, end)));

        query.setFilter(FF.and(CoalescePropertyFactory.getEntityKey(entity.getKey()), filter));

        try (CachedRowSet results = persistor.search(query).getResults())
        {
            Assert.assertEquals(0, results.size());
        }

        entity.markAsDeleted();

        persistor.saveEntity(true, entity);
    }

    /**
     * This test ensures that the query can be made using the entity name or recordset name.
     */
    @Test
    public void testTypename() throws Exception
    {
        T persistor = createPersister();
        Query query;

        // Create Entity
        TestEntity entity = new TestEntity();
        entity.initialize();
        TestRecord record = entity.addRecord1();
        record.getStringField().setValue("Hello World");

        persistor.registerTemplate(CoalesceEntityTemplate.create(entity));
        persistor.saveEntity(false, entity);

        // Recordset Name
        query = new Query(TestEntity.RECORDSET1, CoalescePropertyFactory.getEntityKey(entity.getKey()));
        Assert.assertEquals(1, persistor.search(query).getTotal());

        // Recordset Name
        query = new Query(TestEntity.RECORDSET1.toUpperCase(), CoalescePropertyFactory.getEntityKey(entity.getKey()));
        Assert.assertEquals(1, persistor.search(query).getTotal());

        // Entity Name
        query = new Query(TestEntity.NAME, CoalescePropertyFactory.getEntityKey(entity.getKey()));
        query.setPropertyNames(new String[] {
                CoalescePropertyFactory.getFieldProperty(record.getStringField()).getPropertyName()
        });
        Assert.assertEquals(1, persistor.search(query).getTotal());

        // Entity Name
        query = new Query(TestEntity.NAME.toLowerCase(), CoalescePropertyFactory.getEntityKey(entity.getKey()));
        Assert.assertEquals(1, persistor.search(query).getTotal());
    }

    /**
     * This test creates an entity and updates it. Then verifies that returning the updated property shows the updated value and not the original.
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
        record.getIntegerField().setValue(RANDOM.nextInt());
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
        Query query = new Query(TestEntity.RECORDSET1, CQL.toFilter(cql));
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
     * This test ensures that when changing a record key it will properly delete the feature w/ the old key.
     */
    @Test
    public void testUpdateRecordKey() throws Exception
    {
        T persistor = createPersister();

        // Create Entity
        TestEntity entity1 = new TestEntity();
        entity1.initialize();

        TestEntity entity2 = new TestEntity();
        entity2.initialize();
        entity2.addRecord1();
        entity2.addRecord1();

        // Create Record
        TestRecord record = entity1.addRecord1();
        record.getStringField().setValue("Hello World");

        // Persist
        persistor.saveEntity(false, entity1, entity2);

        List<PropertyName> props = new ArrayList<>();
        props.add(CoalescePropertyFactory.getFieldProperty(record.getStringField()));
        props.add(CoalescePropertyFactory.getFieldProperty(TestEntity.RECORDSET1, "objectkey"));

        List<Filter> filters = new ArrayList<>();
        filters.add(CoalescePropertyFactory.getEntityKey(entity1.getKey()));
        filters.add(FF.equals(CoalescePropertyFactory.getFieldProperty(record.getStringField()),
                              FF.literal(record.getStringField().getValue())));

        // Create Query
        Query query = new Query(TestEntity.getTest1RecordsetName(), FF.and(filters));
        query.setProperties(props);

        // Search
        try (CachedRowSet results = persistor.search(query).getResults())
        {
            // One and only 1 result
            Assert.assertEquals(entity1.getRecordset1().getCount(), results.size());
            Assert.assertTrue(results.next());
            Assert.assertEquals(entity1.getKey(), results.getString(1));
            Assert.assertEquals(record.getKey(), results.getString(3));
        }

        // Change Record Key
        record.setKey(UUID.randomUUID().toString());
        persistor.saveEntity(false, entity1, entity2);

        // Search
        try (CachedRowSet results = persistor.search(query).getResults())
        {
            // One and only 1 result
            Assert.assertEquals(entity1.getRecordset1().getCount(), results.size());
            Assert.assertTrue(results.next());
            Assert.assertEquals(entity1.getKey(), results.getString(1));
            Assert.assertEquals(record.getKey(), results.getString(3));
        }

        // Cleanup
        entity1.markAsDeleted();
        entity2.markAsDeleted();

        persistor.saveEntity(true, entity1, entity2);
    }

    /**
     * This test ensures that when a record is deleted that it is no longer discoverable.
     */
    @Test
    public void testDeleteRecord() throws Exception
    {
        T persistor = createPersister();

        // Create Entity
        TestEntity entity = new TestEntity();
        entity.initialize();

        // Create Record
        TestRecord record = entity.addRecord1();
        record.getStringField().setValue("Hello World");

        // Persist
        persistor.saveEntity(false, entity);

        List<PropertyName> props = new ArrayList<>();
        props.add(CoalescePropertyFactory.getFieldProperty(record.getStringField()));
        props.add(CoalescePropertyFactory.getFieldProperty(TestEntity.RECORDSET1, "objectkey"));

        List<Filter> filters = new ArrayList<>();
        filters.add(CoalescePropertyFactory.getEntityKey(entity.getKey()));
        filters.add(FF.equals(CoalescePropertyFactory.getFieldProperty(record.getStringField()),
                              FF.literal(record.getStringField().getValue())));

        // Create Query
        Query query = new Query(TestEntity.getTest1RecordsetName(), FF.and(filters));
        query.setProperties(props);

        // Search
        try (CachedRowSet results = persistor.search(query).getResults())
        {
            // One and only 1 result
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.next());
            Assert.assertEquals(entity.getKey(), results.getString(1));
            Assert.assertEquals(record.getKey(), results.getString(3));
        }

        // Change Record Key
        record.markAsDeleted();
        persistor.saveEntity(false, entity);

        // Search
        try (CachedRowSet results = persistor.search(query).getResults())
        {
            // Should be no results
            Assert.assertEquals(0, results.size());
        }

        // Cleanup
        entity.markAsDeleted();

        persistor.saveEntity(true, entity);
    }

    @Test
    public void testDeleteEntity() throws Exception
    {
        T persistor = createPersister();

        // Create Entity
        TestEntity entity = new TestEntity();
        entity.initialize();

        // Create Record
        TestRecord record = entity.addRecord1();
        record.getStringField().setValue("Hello World");

        // Persist
        persistor.saveEntity(false, entity);

        List<PropertyName> props = new ArrayList<>();
        props.add(CoalescePropertyFactory.getFieldProperty(record.getStringField()));
        props.add(CoalescePropertyFactory.getFieldProperty(TestEntity.RECORDSET1, "objectkey"));

        List<Filter> filters = new ArrayList<>();
        filters.add(CoalescePropertyFactory.getEntityKey(entity.getKey()));
        filters.add(FF.equals(CoalescePropertyFactory.getFieldProperty(record.getStringField()),
                              FF.literal(record.getStringField().getValue())));

        // Create Query
        Query query = new Query(TestEntity.getTest1RecordsetName(), FF.and(filters));
        query.setProperties(props);

        // Search
        try (CachedRowSet results = persistor.search(query).getResults())
        {
            // One and only 1 result
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.next());
            Assert.assertEquals(entity.getKey(), results.getString(1));
            Assert.assertEquals(record.getKey(), results.getString(3));
        }

        // Change Record Key
        entity.markAsDeleted();
        persistor.saveEntity(false, entity);

        // Search
        try (CachedRowSet results = persistor.search(query).getResults())
        {
            // Should be no results
            Assert.assertEquals(0, results.size());
        }

        // Cleanup
        entity.markAsDeleted();

        persistor.saveEntity(true, entity);
    }

    /**
     * This test verifies searching for entities w/o geospatial fields.
     */
    @Test
    public void testSearchNonGeoEntity() throws Exception
    {
        T persistor = createPersister();

        TestEntity entity = new TestEntity();
        entity.initialize();

        // set fields
        TestRecord record = entity.addRecord1();
        record.getIntegerField().setValue(RANDOM.nextInt());
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

        for (int ii = 1; ii <= results.getMetaData().getColumnCount(); ii++)
        {
            System.out.println(results.getString(ii));
        }

        Assert.assertEquals((int) record.getIntegerField().getValue(), results.getInt(2));
        Assert.assertEquals(record.getStringField().getValue(), results.getString(3));
    }

    /**
     * This test ensures that the persister does not throw any exceptions for a large amount of records.
     */
    @Test
    public void test20KRecords() throws Exception
    {
        ICoalescePersistor persistor = createPersister();

        TestEntity entity = new TestEntity();
        entity.initialize();

        // Add X Records
        for (int ii = 0; ii < 20000; ii++)
        {
            entity.addRecord1();
        }

        persistor.saveEntity(false, entity);

        // Change Record Keys (This causes phantom records to be deleted)
        for (CoalesceRecord record : entity.getRecordset1().getAllRecords())
        {
            record.setKey(UUID.randomUUID().toString());
        }

        persistor.saveEntity(false, entity);
    }

    /**
     * This test ensures that entities can be found using the entity ID.
     */
    @Test
    public void testFindEntityId() throws Exception
    {
        TestEntity entity = new TestEntity();
        entity.initialize();
        entity.setEntityId("hello");
        entity.setEntityIdType("world");

        TestRecord record = entity.addRecord1();
        record.getStringField().setValue("world");

        CoalesceSearchFramework framework = new CoalesceSearchFramework();
        framework.setAuthoritativePersistor(createPersister());
        framework.saveCoalesceEntity(entity);

        Assert.assertEquals(entity.getKey(), framework.findEntityId("hello"));
        Assert.assertEquals(entity.getKey(), framework.findEntityId("hello", TestEntity.NAME));
        Assert.assertEquals(null, framework.findEntityId("hello", "UNKNOWN"));

        Assert.assertEquals(entity.getKey(),
                            framework.find(FF.equals(CoalescePropertyFactory.getFieldProperty(record.getStringField()),
                                                     FF.literal(record.getStringField().getValue()))));

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

        System.out.println(cql);

        return CQL.toFilter(cql);
    }

}
