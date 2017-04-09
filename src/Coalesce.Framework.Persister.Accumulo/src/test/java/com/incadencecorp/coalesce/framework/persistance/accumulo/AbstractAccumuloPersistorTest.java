package com.incadencecorp.coalesce.framework.persistance.accumulo;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.accumulo.core.client.Connector;
import org.apache.commons.io.FilenameUtils;
import org.apache.xerces.impl.dv.util.Base64;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.AttributeExpressionImpl;
import org.geotools.filter.SortByImpl;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.jdom2.JDOMException;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.drew.imaging.ImageProcessingException;
import com.incadencecorp.coalesce.common.CoalesceUnitTestSettings;
import com.incadencecorp.coalesce.common.exceptions.CoalesceCryptoException;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.DocumentProperties;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.MimeHelper;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.CoalesceObjectFactory;
import com.incadencecorp.coalesce.framework.CoalesceSettings;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceBooleanField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCircleField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCoordinateField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCoordinateListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDateTimeField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDoubleField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDoubleListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFileField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFloatField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFloatListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceGUIDField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIntegerField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIntegerListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLineStringField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLongField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLongListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalescePolygonField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringListField;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.datamodel.TestRecord;
import com.incadencecorp.coalesce.framework.persistance.AbstractCoalescePersistorTest;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;
import com.incadencecorp.coalesce.framework.persistance.accumulo.testobjects.GDELT_Test_Entity;
import com.incadencecorp.coalesce.framework.persistance.accumulo.testobjects.NonGeoEntity;
import com.incadencecorp.coalesce.services.api.search.HitType;
import com.incadencecorp.coalesce.services.api.search.SearchDataObjectResponse;
import com.incadencecorp.coalesce.services.crud.api.ICrudClient;
import com.incadencecorp.coalesce.services.crud.service.client.CrudFrameworkClientImpl;
import com.incadencecorp.coalesce.services.search.api.ISearchClient;
import com.incadencecorp.coalesce.services.search.service.client.SearchFrameworkClientImpl;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTWriter;
import com.vividsolutions.jts.util.GeometricShapeFactory;

public abstract class AbstractAccumuloPersistorTest extends AbstractCoalescePersistorTest<AccumuloPersistor> {

    private static String TESTFILENAME = "Desert.jpg";
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAccumuloPersistorTest.class);
    protected static CoalesceFramework coalesceFramework;
    private static AccumuloPersistor persistor;

    @Test
    public void testConnection() throws CoalescePersistorException, Exception
    {
        // AccumuloDataConnector accumuloConnector = new AccumuloDataConnector(getConnection());
        AccumuloDataConnector accumuloConnector = getAccumuloDataConnector();
        Connector conn = accumuloConnector.getDBConnector();
        Map<String, String> sysconf = conn.instanceOperations().getSystemConfiguration();
        assertNotNull(sysconf);
        accumuloConnector.close();
    }

    protected abstract AccumuloDataConnector getAccumuloDataConnector() throws CoalescePersistorException;

    protected abstract ServerConn getConnection();

    @Test
    public void AccumuloDataTypesTest()
            throws CoalesceDataFormatException, CoalescePersistorException, SAXException, IOException
    {
        double CIRCLERADIUS = 5.25;

        // Create Entity
        TestEntity entity = new TestEntity();
        entity.initialize();

        // Create Record
        TestRecord record = entity.addRecord1();

        // Create Circle
        CoalesceCircleField circlefield = record.getCircleField();
        Point center = new GeometryFactory().createPoint(new Coordinate(0, 0));
        circlefield.setValue(center, CIRCLERADIUS);

        // Create Polygon
        CoalescePolygonField polygonfield = record.getPolygonField();
        GeometricShapeFactory gsf = new GeometricShapeFactory();
        gsf.setSize(10);
        gsf.setNumPoints(20);
        gsf.setCentre(new Coordinate(0, 0));
        Polygon shape = gsf.createCircle();
        polygonfield.setValue(shape);

        // Create Line
        CoalesceLineStringField linefield = record.getLineField();
        Coordinate coords[] = { new Coordinate(0, 0), new Coordinate(1, 1), new Coordinate(2, 2) };
        GeometryFactory gf = new GeometryFactory();
        LineString line = gf.createLineString(coords);
        linefield.setValue(line);

        // Create a geo list
        CoalesceCoordinateListField geolistfield = record.getGeoListField();
        MultiPoint geolist = new GeometryFactory().createMultiPoint(coords);
        geolistfield.setValue(geolist);

        // Create a point value
        CoalesceCoordinateField coordfield = record.getGeoField();
        coordfield.setValue(new Coordinate(10, 10));

        // Int
        CoalesceIntegerField intfield = record.getIntegerField();
        intfield.setValue(42);

        // Check a int list field
        int intlist[] = { 3, 4, 5, 6 };
        CoalesceIntegerListField intlistfield = record.getIntegerListField();
        intlistfield.setValue(intlist);

        // Long
        CoalesceLongField longfield = record.getLongField();
        longfield.setValue((long) 42);

        // Check a long list field
        long longlist[] = { 3, 4, 5, 6 };
        CoalesceLongListField longlistfield = record.getLongListField();
        longlistfield.setValue(longlist);

        // String
        CoalesceStringField stringfield = record.getStringField();
        stringfield.setValue("Test String");

        // Check a string list field
        String stringlist[] = { "A", "B", "C" };
        CoalesceStringListField stringlistfield = record.getStringListField();
        stringlistfield.setValue(stringlist);

        // Float
        CoalesceFloatField floatfield = record.getFloatField();
        floatfield.setValue((float) 3.145964);

        // Check a float list field
        float floatlist[] = { (float) 3.145964, (float) 7.87856, (float) 10000.000045566 };
        CoalesceFloatListField floatlistfield = record.getFloatListField();
        floatlistfield.setValue(floatlist);

        // Double
        CoalesceDoubleField doublefield = record.getDoubleField();
        doublefield.setValue(3.145964);

        // Check a Double list field
        double doublelist[] = { 3.145964, 7.87856, 10000.000045566 };
        CoalesceDoubleListField doublelistfield = record.getDoubleListField();
        doublelistfield.setValue(doublelist);

        // Boolean
        CoalesceBooleanField booleanfield = record.getBooleanField();
        booleanfield.setValue(true);

        // Date
        CoalesceDateTimeField datetimefield = record.getDateField();
        datetimefield.setValue(JodaDateTimeHelper.nowInUtc());

        // UUID
        CoalesceGUIDField guidfield = record.getGuidField();
        guidfield.setValue(UUID.randomUUID());

        // File
        CoalesceFileField filefield = record.getFileField();
        String fileName = CoalesceUnitTestSettings.getResourceAbsolutePath(TESTFILENAME);

        try
        {
            DocumentProperties properties = new DocumentProperties();
            if (properties.initialize(fileName, CoalesceSettings.getUseEncryption()))
            {
                filefield.setValue(properties);
            }
        }
        catch (ImageProcessingException | CoalesceCryptoException | IOException | JDOMException e)
        {
            fail("Error processing image file: " + e.getMessage());
        }

        // Register a template for this entity so that search data is persisted
        getFramework().saveCoalesceEntityTemplate(CoalesceEntityTemplate.create(entity));

        // Persist Entity
        getFramework().saveCoalesceEntity(entity);

        // Get Persisted Values
        String cvalue = getFramework().getCoalesceFieldValue(circlefield.getKey());
        String pvalue = getFramework().getCoalesceFieldValue(polygonfield.getKey());
        String lvalue = getFramework().getCoalesceFieldValue(linefield.getKey());
        float fvalue = Float.valueOf(getFramework().getCoalesceFieldValue(floatfield.getKey()));
        String flistvalue[] = getFramework().getCoalesceFieldValue(floatlistfield.getKey()).split(",");
        double dvalue = Double.valueOf(getFramework().getCoalesceFieldValue(doublefield.getKey()));
        String dlistvalue[] = getFramework().getCoalesceFieldValue(doublelistfield.getKey()).split(",");
        boolean bvalue = Boolean.valueOf(getFramework().getCoalesceFieldValue(booleanfield.getKey()));
        String mydate = getFramework().getCoalesceFieldValue(datetimefield.getKey());
        DateTime datevalue = JodaDateTimeHelper.fromXmlDateTimeUTC(mydate);
        CoalesceEntity filefieldentity = getFramework().getCoalesceEntity(entity.getKey());
        CoalesceFileField filefieldvalue = (CoalesceFileField) filefieldentity.getCoalesceObjectForKey(filefield.getKey());
        byte[] filefieldbytesvalue = Base64.decode(filefieldvalue.getBaseValue());

        // Create test values
        String ctest = new WKTWriter(3).write(center);
        String ptest = new WKTWriter(3).write(shape);
        String ltest = new WKTWriter(3).write(line);
        float ftest = floatfield.getValue();
        float flisttest[] = floatlistfield.getValue();
        double dtest = doublefield.getValue();
        double dlisttest[] = doublelistfield.getValue();
        boolean btest = booleanfield.getValue();
        DateTime datetest = datetimefield.getValue();
        byte[] fileFieldBytestest = Base64.decode(filefield.getBaseValue());

        // Verify Circle
        assertEquals(ctest, cvalue);
        assertEquals(CIRCLERADIUS, Double.valueOf(circlefield.getAttribute("radius")), 0);

        // Verify Polygon
        assertEquals(ptest, pvalue);

        // Verify line
        assertEquals(ltest, lvalue);

        // Verify float
        assertEquals(ftest, fvalue, 0);

        // Verify floatlist
        assertEquals(flisttest.length, flistvalue.length);
        for (int i = 0; i < flisttest.length; i++)
        {
            assertEquals(flisttest[i], Float.valueOf(flistvalue[i]), 0);
        }

        // Verify double
        assertEquals(dtest, dvalue, 0);

        // Verify doublelist
        assertEquals(dlisttest.length, dlistvalue.length);
        for (int i = 0; i < dlisttest.length; i++)
        {
            assertEquals(dlisttest[i], Double.valueOf(dlistvalue[i]), 0);
        }

        // Verify boolean
        assertEquals(btest, bvalue);

        // Verify date
        assertEquals(datetest, datevalue);

        // Verify File Field
        assertTrue((filefieldvalue.getValue() instanceof DocumentProperties));
        assertArrayEquals(filefieldbytesvalue, fileFieldBytestest);

        assertEquals(TESTFILENAME, filefieldvalue.getFilename());
        assertEquals(FilenameUtils.getExtension(TESTFILENAME), filefieldvalue.getExtension());
        assertEquals(MimeHelper.getMimeTypeForExtension(FilenameUtils.getExtension(TESTFILENAME)),
                     filefieldvalue.getMimeType());
        assertEquals(filefieldbytesvalue.length, filefield.getSize());

    }

    private CoalesceFramework getFramework()
    {

        if (coalesceFramework == null)
        {

            coalesceFramework = new CoalesceFramework();
            persistor = createPersister();
            
            coalesceFramework.setAuthoritativePersistor(persistor);

        }

        return coalesceFramework;
    }

    private static Filter createFilter(String geomField,
                                       double x0,
                                       double y0,
                                       double x1,
                                       double y1,
                                       String dateField,
                                       String t0,
                                       String t1,
                                       String attributesQuery)
            throws CQLException, IOException
    {

        String cqlGeometry = "BBOX(" + geomField + ", " + x0 + ", " + y0 + ", " + x1 + ", " + y1 + ")";
        String cqlDates = "(" + dateField + " DURING " + t0 + "/" + t1 + ")";
        String cqlAttributes = attributesQuery == null ? "INCLUDE" : attributesQuery;
        String cql = cqlGeometry + " AND " + cqlDates + " AND " + cqlAttributes;
        return CQL.toFilter(cql);
    }

    @Test
    public void testPersistRetrieveSearchEntity()
            throws CoalescePersistorException, CoalesceDataFormatException, SAXException, IOException, CQLException
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

        // Search
        DataStore geoDataStore = ((AccumuloDataConnector) persistor.getDataConnector()).getGeoDataStore();
        Filter cqlFilter = createFilter("Actor1Geo_Location",
                                        -180,
                                        -180,
                                        180,
                                        180,
                                        "DateTime",
                                        "2000-07-01T00:00:00.000Z",
                                        "2016-12-31T00:00:00.000Z",
                                        null);
        Query query = new Query(GDELT_Test_Entity.getQueryName(), cqlFilter);

        System.out.println("Feature: " + GDELT_Test_Entity.getQueryName());
        FeatureSource<?, ?> featureSource = geoDataStore.getFeatureSource(GDELT_Test_Entity.getQueryName());

        FeatureIterator<?> featureItr = featureSource.getFeatures(query).features();
        assertTrue(featureItr.hasNext());

        Feature feature = featureItr.next();
        assertEquals(562505648, feature.getProperty("GlobalEventID").getValue());
        assertEquals("EUROPE", feature.getProperty("Actor1Name").getValue());
        featureItr.close();
        //persistor.close();
    }

    @Test
    public void SearchUpdateEntity()
            throws CoalescePersistorException, CoalesceDataFormatException, SAXException, IOException, CQLException
    {

        NonGeoEntity nonGeoEntity = new NonGeoEntity();

        // set fields
        Integer expectedInt = (new Random()).nextInt();

        CoalesceRecord eventRecord = nonGeoEntity.getEventRecordSet().addNew();
        nonGeoEntity.setIntegerField(eventRecord, "GlobalEventID", expectedInt);
        nonGeoEntity.setStringField(eventRecord, "Actor1Name", "MERICA");

        DateTime expectedDateTime = new DateTime();
        ((CoalesceDateTimeField) eventRecord.getFieldByName("DateTime")).setValue(expectedDateTime);

        // Prerequisite setup
        getFramework().saveCoalesceEntityTemplate(CoalesceEntityTemplate.create(nonGeoEntity));
        CoalesceObjectFactory.register(NonGeoEntity.class);

        // Persist
        //AccumuloPersistor persistor = createPersister();
        getFramework().saveCoalesceEntity(false, nonGeoEntity);

        // update
        nonGeoEntity.setStringField(eventRecord, "Actor1Name", "TEXAS");
        getFramework().saveCoalesceEntity(false, nonGeoEntity);

        // Search
        DataStore geoDataStore = ((AccumuloDataConnector) persistor.getDataConnector()).getGeoDataStore();

        SimpleFeatureStore featureSource = (SimpleFeatureStore) geoDataStore.getFeatureSource(NonGeoEntity.getQueryName());

        Filter filter = CQL.toFilter("GlobalEventID =" + expectedInt.toString());

        Query query = new Query(NonGeoEntity.getQueryName(), filter);

        FeatureIterator<?> featureItr = featureSource.getFeatures(query).features();
        assertTrue(featureItr.hasNext());

        Feature feature = featureItr.next();
        assertEquals(expectedInt, feature.getProperty("GlobalEventID").getValue());
        assertEquals("TEXAS", feature.getProperty("Actor1Name").getValue());

        // should have only one result
        if (featureItr.hasNext())
        {
            Feature feature2 = featureItr.next();
            LOGGER.debug("{}", feature2.getProperty("Actor1Name").getValue());
            fail("More than one search result returned");
        }

        featureItr.close(); 
        
    }

    @Test
    public void testSearchNonGeoEntity()
            throws CoalescePersistorException, CoalesceDataFormatException, SAXException, IOException, CQLException
    {

        NonGeoEntity nonGeoEntity = new NonGeoEntity();

        // set fields
        Integer expectedInt = (new Random()).nextInt();

        CoalesceRecord eventRecord = nonGeoEntity.getEventRecordSet().addNew();
        nonGeoEntity.setIntegerField(eventRecord, "GlobalEventID", expectedInt);
        nonGeoEntity.setStringField(eventRecord, "Actor1Name", "MERICA");

        DateTime expectedDateTime = new DateTime();
        ((CoalesceDateTimeField) eventRecord.getFieldByName("DateTime")).setValue(expectedDateTime);

        // Prerequisite setup
        getFramework().saveCoalesceEntityTemplate(CoalesceEntityTemplate.create(nonGeoEntity));
        CoalesceObjectFactory.register(GDELT_Test_Entity.class);

        // Persist
        getFramework().saveCoalesceEntity(false, nonGeoEntity);

        // Search
        DataStore geoDataStore = ((AccumuloDataConnector) persistor.getDataConnector()).getGeoDataStore();

        FeatureSource<?, ?> featureSource = geoDataStore.getFeatureSource(NonGeoEntity.getQueryName());

        Filter trythis = CQL.toFilter("GlobalEventID =" + expectedInt.toString());

        LOGGER.debug(trythis.toString());

        Query query = new Query(NonGeoEntity.getQueryName(), trythis);

        FeatureIterator<?> featureItr = featureSource.getFeatures(query).features();
        assertTrue(featureItr.hasNext());

        Feature feature = featureItr.next();
        assertEquals(expectedInt, feature.getProperty("GlobalEventID").getValue());
        assertEquals("MERICA", feature.getProperty("Actor1Name").getValue());

        featureItr.close();
        //persistor.close();

    }

    @Test
    @Ignore("Not working yet because ISearchClient is setting the typeName in the query to the search job ID")
    public void searchClientTest() throws Exception
    {


        ICrudClient crud = new CrudFrameworkClientImpl(getFramework());
        ISearchClient client = new SearchFrameworkClientImpl(persistor);

        NonGeoEntity nonGeoEntity = new NonGeoEntity();

        // set fields
        Integer expectedInt = (new Random()).nextInt();

        CoalesceRecord eventRecord = nonGeoEntity.getEventRecordSet().addNew();
        nonGeoEntity.setIntegerField(eventRecord, "GlobalEventID", expectedInt);
        nonGeoEntity.setStringField(eventRecord, "Actor1Name", "MERICA");

        DateTime expectedDateTime = new DateTime();
        ((CoalesceDateTimeField) eventRecord.getFieldByName("DateTime")).setValue(expectedDateTime);

        // Prerequisite setup
        getFramework().saveCoalesceEntityTemplate(CoalesceEntityTemplate.create(nonGeoEntity));
        CoalesceObjectFactory.register(NonGeoEntity.class);

        // Persist
        assertTrue(crud.createDataObject(nonGeoEntity));

        // Search
        Filter filter = CQL.toFilter("GlobalEventID =" + expectedInt.toString());

        // TODO: It would be nice if we can pass in a query object instead of a filter for the search client so we can set
        // the typeName in the query.

        // FIXME: we always need to pass in the recordKey property or else everything will be off by 1
        PropertyName recordKeyProp = new AttributeExpressionImpl("recordKey");
        PropertyName propertyName = new AttributeExpressionImpl("GlobalEventID");
        PropertyName[] properties = new PropertyName[] { recordKeyProp, propertyName };

        SortOrder sortOrder = SortOrder.ASCENDING;
        SortBy sortBy = new SortByImpl(propertyName, sortOrder);
        SortBy[] sortByArray = new SortBy[] { sortBy };

        SearchDataObjectResponse results = client.search(filter, 1, properties, sortByArray, false);

        // Verify Hit
        Assert.assertEquals(1, results.getResult().size());
        Assert.assertEquals(1, results.getResult().get(0).getResult().getHits().size());

        HitType hit = results.getResult().get(0).getResult().getHits().get(0);

        // Verify Hit's Properties
        Assert.assertEquals(nonGeoEntity.getKey(), hit.getEntityKey());
        Assert.assertEquals(nonGeoEntity.getName(), hit.getName());
        Assert.assertEquals(nonGeoEntity.getSource(), hit.getSource());
        Assert.assertEquals(nonGeoEntity.getTitle(), hit.getTitle());

        for (String value : hit.getValues())
        {
            LOGGER.info(value);
        }

        client.close();
        crud.close();
 
    }

    protected abstract CoalesceDataConnectorBase getDataConnector(ServerConn conn) throws CoalescePersistorException;

}
