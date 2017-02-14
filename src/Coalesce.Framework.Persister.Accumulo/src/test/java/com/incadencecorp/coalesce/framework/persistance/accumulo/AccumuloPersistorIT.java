package com.incadencecorp.coalesce.framework.persistance.accumulo;

/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

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
/**
 * @author Jing Yang May 13, 2016
 */

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import org.apache.accumulo.core.client.Connector;
import org.apache.commons.io.FilenameUtils;
import org.apache.xerces.impl.dv.util.Base64;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.jdom2.JDOMException;
import org.joda.time.DateTime;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.geomesa.utils.geohash.BoundingBox;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.BoundingBox3D;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
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
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
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
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalescePersistorBaseTest;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;
import com.incadencecorp.coalesce.framework.persistance.accumulo.testobjects.GDELT_Test_Entity;
import com.incadencecorp.coalesce.framework.persistance.accumulo.testobjects.NonGeoEntity;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTWriter;
import com.vividsolutions.jts.util.GeometricShapeFactory;

public class AccumuloPersistorIT extends CoalescePersistorBaseTest {

    private static ServerConn conn;
    private static String TESTFILENAME = "Desert.jpg";
    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloPersistorIT.class);

    @BeforeClass
    public static void setupBeforeClass() throws Exception
    {
        InputStream in = AccumuloDataConnectorIT.class.getClassLoader().getResourceAsStream("accumuloConnectionInfo.properties");
        Properties props = new Properties();
        props.load(in);
        in.close();
        String dbName = props.getProperty("database");
        String zookeepers = props.getProperty("zookeepers");
        String user = props.getProperty("userid");
        String password = props.getProperty("password");
        conn = new ServerConn.Builder().db(dbName).serverName(zookeepers).user(user).password(password).build();

        AccumuloPersistorIT tester = new AccumuloPersistorIT();
        CoalescePersistorBaseTest.setupBeforeClassBase(tester);

        AccumuloSettings.setPersistFieldDefAttr(false);
        AccumuloSettings.setPersistSectionAttr(true);
        AccumuloSettings.setPersistRecordsetAttr(false);
        AccumuloSettings.setPersistRecordAttr(false);

        String version = System.getProperty("java.version");

        if (!version.contains("1.8"))
        {
            LOGGER.warn("JRE {} Detected. These unit tests require JRE 1.8", version);
            LOGGER.warn("Skipping unit tests");
            // skip these tests
            Assume.assumeTrue(false);
        }
    }

    @Override
    protected ServerConn getConnection()
    {
        return conn;
    }

    @Override
    protected ICoalescePersistor getPersistor(ServerConn conn)
    {
        try
        {
            return new AccumuloPersistor(conn);
        }
        catch (CoalescePersistorException e)
        {
            return null;
        }
    }

    @Override
    protected CoalesceDataConnectorBase getDataConnector(ServerConn conn) throws CoalescePersistorException
    {
        return new AccumuloDataConnector(conn);
    }

    @Test
    public void testConnection() throws CoalescePersistorException, Exception
    {
        AccumuloDataConnector accumuloConnector = new AccumuloDataConnector(getConnection());
        Connector conn = accumuloConnector.getDBConnector();
        Map<String, String> sysconf = conn.instanceOperations().getSystemConfiguration();
        assertNotNull(sysconf);
        accumuloConnector.close();
    }

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
        AccumuloPersistor persistor = (AccumuloPersistor) this.getPersistor(this.getConnection());
        persistor.saveEntity(true, gdeltEntity);

        // Retrieve
        CoalesceEntity[] entities = persistor.getEntity(gdeltEntity.getKey());
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

        FeatureSource<?, ?> featureSource = geoDataStore.getFeatureSource(GDELT_Test_Entity.getQueryName());
        FeatureIterator<?> featureItr = featureSource.getFeatures(query).features();
        assertTrue(featureItr.hasNext());

        Feature feature = featureItr.next();
        assertEquals(562505648, feature.getProperty("GlobalEventID").getValue());
        assertEquals("EUROPE", feature.getProperty("Actor1Name").getValue());
        featureItr.close();
        persistor.close();
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
        AccumuloPersistor persistor = (AccumuloPersistor) this.getPersistor(this.getConnection());
        persistor.saveEntity(true, nonGeoEntity);

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
        persistor.close();

    }

    @Test
    public void testSearchUpdateEntity()
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
        AccumuloPersistor persistor = (AccumuloPersistor) this.getPersistor(this.getConnection());
        persistor.saveEntity(true, nonGeoEntity);

        // update
        nonGeoEntity.setStringField(eventRecord, "Actor1Name", "TEXAS");
        persistor.saveEntity(true, nonGeoEntity);

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
        persistor.close();

    }

}
