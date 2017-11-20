package com.incadencecorp.coalesce.framework.persistance.accumulo;

import com.drew.imaging.ImageProcessingException;
import com.incadencecorp.coalesce.common.CoalesceUnitTestSettings;
import com.incadencecorp.coalesce.common.exceptions.CoalesceCryptoException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.DocumentProperties;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.MimeHelper;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.CoalesceObjectFactory;
import com.incadencecorp.coalesce.framework.CoalesceSettings;
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.framework.persistance.AbstractCoalescePersistorTest;
import com.incadencecorp.coalesce.framework.persistance.accumulo.testobjects.GDELT_Test_Entity;
import com.incadencecorp.coalesce.framework.persistance.accumulo.testobjects.NonGeoEntity;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.WKTWriter;
import com.vividsolutions.jts.util.GeometricShapeFactory;
import org.apache.accumulo.core.client.Connector;
import org.apache.commons.io.FilenameUtils;
import org.apache.xerces.impl.dv.util.Base64;
import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.jdom2.JDOMException;
import org.joda.time.DateTime;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

public abstract class AbstractAccumuloPersistorTest extends AbstractCoalescePersistorTest<AccumuloPersistor> {

    private static String TESTFILENAME = "Desert.jpg";
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAccumuloPersistorTest.class);

    @Test
    public void testConnection() throws Exception
    {
        AccumuloPersistor persistor = createPersister();
        AccumuloDataConnector accumuloConnector = (AccumuloDataConnector) createPersister().getDataConnector();
        Connector conn = accumuloConnector.getDBConnector();
        Map<String, String> sysconf = conn.instanceOperations().getSystemConfiguration();
        assertNotNull(sysconf);
        accumuloConnector.close();
    }

    @Test
    public void AccumuloDataTypesTest() throws Exception
    {
        AccumuloPersistor persistor = createPersister();
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
        Coordinate coords[] = { new Coordinate(0, 0), new Coordinate(1, 1), new Coordinate(2, 2)
        };
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
        int intlist[] = { 3, 4, 5, 6
        };
        CoalesceIntegerListField intlistfield = record.getIntegerListField();
        intlistfield.setValue(intlist);

        // Long
        CoalesceLongField longfield = record.getLongField();
        longfield.setValue((long) 42);

        // Check a long list field
        long longlist[] = { 3, 4, 5, 6
        };
        CoalesceLongListField longlistfield = record.getLongListField();
        longlistfield.setValue(longlist);

        // String
        CoalesceStringField stringfield = record.getStringField();
        stringfield.setValue("Test String");

        // Check a string list field
        String stringlist[] = { "A", "B", "C"
        };
        CoalesceStringListField stringlistfield = record.getStringListField();
        stringlistfield.setValue(stringlist);

        // Float
        CoalesceFloatField floatfield = record.getFloatField();
        floatfield.setValue((float) 3.145964);

        // Check a float list field
        float floatlist[] = { (float) 3.145964, (float) 7.87856, (float) 10000.000045566
        };
        CoalesceFloatListField floatlistfield = record.getFloatListField();
        floatlistfield.setValue(floatlist);

        // Double
        CoalesceDoubleField doublefield = record.getDoubleField();
        doublefield.setValue(3.145964);

        // Check a Double list field
        double doublelist[] = { 3.145964, 7.87856, 10000.000045566
        };
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
        persistor.saveTemplate(CoalesceEntityTemplate.create(entity));

        // Persist Entity
        persistor.saveEntity(false, entity);

        // Get Persisted Values
        String cvalue = (String) persistor.getFieldValue(circlefield.getKey());
        String pvalue = (String) persistor.getFieldValue(polygonfield.getKey());
        String lvalue = (String) persistor.getFieldValue(linefield.getKey());
        float fvalue = Float.valueOf((String) persistor.getFieldValue(floatfield.getKey()));
        String flistvalue[] = ((String) persistor.getFieldValue(floatlistfield.getKey())).split(",");
        double dvalue = Double.valueOf((String) persistor.getFieldValue(doublefield.getKey()));
        String dlistvalue[] = ((String) persistor.getFieldValue(doublelistfield.getKey())).split(",");
        boolean bvalue = Boolean.valueOf((String) persistor.getFieldValue(booleanfield.getKey()));
        String mydate = (String) persistor.getFieldValue(datetimefield.getKey());
        DateTime datevalue = JodaDateTimeHelper.fromXmlDateTimeUTC(mydate);
        CoalesceEntity filefieldentity = persistor.getEntity(entity.getKey())[0];
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

        String cqlGeometry = "BBOX(" + geomField + ", " + x0 + ", " + y0 + ", " + x1 + ", " + y1 + ")";
        String cqlDates = "(" + dateField + " DURING " + t0 + "/" + t1 + ")";
        String cqlAttributes = attributesQuery == null ? "INCLUDE" : attributesQuery;
        String cql = cqlGeometry + " AND " + cqlDates + " AND " + cqlAttributes;
        return CQL.toFilter(cql);
    }

    @Test
    public void testPersistRetrieveSearchEntity() throws Exception
    {
        AccumuloPersistor persistor = createPersister();

        GDELT_Test_Entity gdeltEntity = new GDELT_Test_Entity();

        // Prerequisite setup
        persistor.saveTemplate(CoalesceEntityTemplate.create(gdeltEntity));
        CoalesceObjectFactory.register(GDELT_Test_Entity.class);

        // Persist

        persistor.saveEntity(false, gdeltEntity);

        // Retrieve
        CoalesceEntity[] entities = persistor.getEntity(gdeltEntity.getKey());
        assertEquals(1, entities.length);
        assertEquals(gdeltEntity.getKey(), entities[0].getKey());

        // Search
        //DataStore geoDataStore = ((AccumuloDataConnector) persistor.getDataConnector()).getGeoDataStore();

        // These Quoted names need escaped as they are used in filters
        String geomAttributeName = GDELT_Test_Entity.getQueryName() + ".Actor1Geo_Location";
        String dateAttributeName = GDELT_Test_Entity.getQueryName() + ".DateTime";
        // These do not need escaped
        String idAttributeName = "GlobalEventID";
        String actorAttributeName = "Actor1Name";
        Filter cqlFilter = createFilter(geomAttributeName,
                                        -180,
                                        -180,
                                        180,
                                        180,
                                        dateAttributeName,
                                        "2000-07-01T00:00:00.000Z",
                                        "2016-12-31T00:00:00.000Z",
                                        null);
        Query query = new Query(GDELT_Test_Entity.getQueryName(), cqlFilter);
        // Set up to return the GlobalEventID and Actor1Name fields
        String[] props = { idAttributeName, actorAttributeName };
        query.setPropertyNames(props);
        CachedRowSet results = persistor.search(query).getResults();
        assertTrue(results.size() > 0);
        results.next();
        assertEquals(562505648, results.getInt(idAttributeName));
        assertEquals("EUROPE", results.getString(actorAttributeName));
        results.close();
    }

    @Test
    public void SearchUpdateEntity() throws Exception
    {
        AccumuloPersistor persistor = createPersister();

        NonGeoEntity nonGeoEntity = new NonGeoEntity();

        // set fields
        Integer expectedInt = (new Random()).nextInt();

        CoalesceRecord eventRecord = nonGeoEntity.getEventRecordSet().addNew();
        nonGeoEntity.setIntegerField(eventRecord, "GlobalEventID", expectedInt);
        nonGeoEntity.setStringField(eventRecord, "Actor1Name", "MERICA");

        DateTime expectedDateTime = new DateTime();
        ((CoalesceDateTimeField) eventRecord.getFieldByName("DateTime")).setValue(expectedDateTime);

        // Prerequisite setup
        persistor.saveTemplate(CoalesceEntityTemplate.create(nonGeoEntity));
        CoalesceObjectFactory.register(NonGeoEntity.class);

        // Persist
        // AccumuloPersistor persistor = createPersister();
        persistor.saveEntity(false, nonGeoEntity);

        // update
        nonGeoEntity.setStringField(eventRecord, "Actor1Name", "TEXAS");
        persistor.saveEntity(false, nonGeoEntity);

        // Search
        DataStore geoDataStore = ((AccumuloDataConnector) persistor.getDataConnector()).getGeoDataStore();

        SimpleFeatureStore featureSource = (SimpleFeatureStore) geoDataStore.getFeatureSource(NonGeoEntity.getQueryName());

        String filterstring = "GlobalEventID =" + expectedInt.toString();
        Filter filter = CQL.toFilter(filterstring);
        Query query = new Query(NonGeoEntity.getQueryName(), filter);
        // Set up to return the GlobalEventID and Actor1Name fields
        String[] props = { "GlobalEventID", "Actor1Name" };
        query.setPropertyNames(props);
        CachedRowSet results = persistor.search(query).getResults();
        // One and only 1 result
        assertTrue(results.size() == 1);
        results.next();
        assertEquals(expectedInt.intValue(), results.getInt("GlobalEventID"));
        assertEquals("TEXAS", results.getString("Actor1Name"));

        results.close();

    }

    @Test
    public void testSearchNonGeoEntity() throws Exception
    {
        AccumuloPersistor persistor = createPersister();

        NonGeoEntity nonGeoEntity = new NonGeoEntity();

        // set fields
        Integer expectedInt = (new Random()).nextInt();

        CoalesceRecord eventRecord = nonGeoEntity.getEventRecordSet().addNew();
        nonGeoEntity.setIntegerField(eventRecord, "GlobalEventID", expectedInt);
        nonGeoEntity.setStringField(eventRecord, "Actor1Name", "MERICA");

        DateTime expectedDateTime = new DateTime();
        ((CoalesceDateTimeField) eventRecord.getFieldByName("DateTime")).setValue(expectedDateTime);

        // Prerequisite setup
        persistor.saveTemplate(CoalesceEntityTemplate.create(nonGeoEntity));
        CoalesceObjectFactory.register(GDELT_Test_Entity.class);

        // Persist
        persistor.saveEntity(false, nonGeoEntity);

        String filterstring = "GlobalEventID =" + expectedInt.toString();
        Filter trythis = CQL.toFilter(filterstring);

        LOGGER.debug(trythis.toString());

        Query query = new Query(NonGeoEntity.getQueryName(), trythis);
        // Set up to return the GlobalEventID and Actor1Name fields
        String[] props = { "GlobalEventID", "Actor1Name" };
        query.setPropertyNames(props);
        CachedRowSet results = persistor.search(query).getResults();

        assertTrue(results.next());

        Integer id = results.getInt("GlobalEventID");
        assertEquals(expectedInt, id);
        assertEquals("MERICA", results.getString("Actor1Name"));

    }
/*
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
        persistor.saveTemplate(CoalesceEntityTemplate.create(nonGeoEntity));
        CoalesceObjectFactory.register(NonGeoEntity.class);

        // Persist
        assertTrue(crud.createDataObject(nonGeoEntity));

        // Search
        Filter filter = CQL.toFilter("GlobalEventID =" + expectedInt.toString());

        // TODO: It would be nice if we can pass in a query object instead of a
        // filter for the search client so we can set
        // the typeName in the query.

        // FIXME: we always need to pass in the recordKey property or else
        // everything will be off by 1
        PropertyName recordKeyProp = new AttributeExpressionImpl("recordKey");
        PropertyName propertyName = new AttributeExpressionImpl("GlobalEventID");
        PropertyName[] properties = new PropertyName[] {
                                                         recordKeyProp, propertyName
        };

        SortOrder sortOrder = SortOrder.ASCENDING;
        SortBy sortBy = new SortByImpl(propertyName, sortOrder);
        SortBy[] sortByArray = new SortBy[] {
                                              sortBy
        };

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
    */

}
