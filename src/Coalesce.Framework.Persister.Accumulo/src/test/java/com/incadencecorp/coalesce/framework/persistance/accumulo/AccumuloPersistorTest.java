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
 * @author Jing Yang
 * May 13, 2016
 */

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.minicluster.MiniAccumuloCluster;
import org.apache.commons.io.FilenameUtils;
import org.apache.xerces.impl.dv.util.Base64;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.jdom2.JDOMException;
import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.filter.Filter;
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
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringListField;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.datamodel.TestRecord;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalescePersistorBaseTest;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;
import com.incadencecorp.coalesce.framework.persistance.accumulo.testobjects.CoalesceSearchTestEntity1;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTWriter;
import com.vividsolutions.jts.util.GeometricShapeFactory;

public class AccumuloPersistorTest extends CoalescePersistorBaseTest {

	private static MiniAccumuloCluster accumulo = null;

	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		File tempDirectory = Files.createTempDirectory("accTemp").toFile();
		accumulo = new MiniAccumuloCluster(tempDirectory, "password");
		accumulo.start();
		AccumuloPersistorTest tester = new AccumuloPersistorTest();
		CoalescePersistorBaseTest.setupBeforeClassBase(tester);
		CoalesceUnitTestSettings.initialize();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		CoalesceUnitTestSettings.tearDownAfterClass();
		accumulo.stop();
	}

	protected ServerConn getConnection() {
		String name = accumulo.getInstanceName();
		String zookeepers = accumulo.getZooKeepers();
		return new ServerConn.Builder().db(name).serverName(zookeepers).user("root").password("password").build();
	}

	@Override
	protected ICoalescePersistor getPersistor(ServerConn conn) {
		try {
			return new AccumuloPersistor(conn);
		} catch (CoalescePersistorException e) {
			return null;
		}
	}

	@Override
	protected CoalesceDataConnectorBase getDataConnector(ServerConn conn) throws CoalescePersistorException {
		return new AccumuloDataConnector(conn);
	}

	@Test
	public void testConnection() throws CoalescePersistorException, Exception {
		AccumuloDataConnector accumuloConnector = new AccumuloDataConnector(getConnection());
		Connector conn = accumuloConnector.getDBConnector();
		Map<String, String> sysconf = conn.instanceOperations().getSystemConfiguration();
		assertNotNull(sysconf);
		accumuloConnector.close();
	}

	static private String TESTFILENAME = "Desert.jpg";

	@Test
	public void AccumuloDataTypesTest()
			throws CoalesceDataFormatException, CoalescePersistorException, SAXException, IOException {
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

		try {
			DocumentProperties properties = new DocumentProperties();
			if (properties.initialize(fileName, CoalesceSettings.getUseEncryption())) {
				filefield.setValue(properties);
			}
		} catch (ImageProcessingException | CoalesceCryptoException | IOException | JDOMException e) {
			fail("Error processing image file: " + e.getMessage());
		}

		// Register a template for this entity so that search data is persisted
		_coalesceFramework.saveCoalesceEntityTemplate(CoalesceEntityTemplate.create(entity));

		// Persist Entity
		_coalesceFramework.saveCoalesceEntity(entity);

		// Get Persisted Values
		String cvalue = _coalesceFramework.getCoalesceFieldValue(circlefield.getKey());
		String pvalue = _coalesceFramework.getCoalesceFieldValue(polygonfield.getKey());
		String lvalue = _coalesceFramework.getCoalesceFieldValue(linefield.getKey());
		float fvalue = Float.valueOf(_coalesceFramework.getCoalesceFieldValue(floatfield.getKey()));
		String flistvalue[] = _coalesceFramework.getCoalesceFieldValue(floatlistfield.getKey()).split(",");
		double dvalue = Double.valueOf(_coalesceFramework.getCoalesceFieldValue(doublefield.getKey()));
		String dlistvalue[] = _coalesceFramework.getCoalesceFieldValue(doublelistfield.getKey()).split(",");
		boolean bvalue = Boolean.valueOf(_coalesceFramework.getCoalesceFieldValue(booleanfield.getKey()));
		String mydate = _coalesceFramework.getCoalesceFieldValue(datetimefield.getKey());
		DateTime datevalue = JodaDateTimeHelper.fromXmlDateTimeUTC(mydate);
		CoalesceEntity filefieldentity = _coalesceFramework.getCoalesceEntity(entity.getKey());
		CoalesceFileField filefieldvalue = (CoalesceFileField) filefieldentity
				.getCoalesceObjectForKey(filefield.getKey());
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
		for (int i = 0; i < flisttest.length; i++) {
			assertEquals(flisttest[i], Float.valueOf(flistvalue[i]), 0);
		}

		// Verify double
		assertEquals(dtest, dvalue, 0);

		// Verify doublelist
		assertEquals(dlisttest.length, dlistvalue.length);
		for (int i = 0; i < dlisttest.length; i++) {
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

	private static Filter createFilter(String geomField, double x0, double y0, double x1, double y1, String dateField,
			String t0, String t1, String attributesQuery) throws CQLException, IOException {

		// there are many different geometric predicates that might be used;
		// here, we just use a bounding-box (BBOX) predicate as an example.
		// this is useful for a rectangular query area
		String cqlGeometry = "BBOX(" + geomField + ", " + x0 + ", " + y0 + ", " + x1 + ", " + y1 + ")";

		// there are also quite a few temporal predicates; here, we use a
		// "DURING" predicate, because we have a fixed range of times that
		// we want to query
		String cqlDates = "(" + dateField + " DURING " + t0 + "/" + t1 + ")";

		// there are quite a few predicates that can operate on other attribute
		// types; the GeoTools Filter constant "INCLUDE" is a default that means
		// to accept everything
		String cqlAttributes = attributesQuery == null ? "INCLUDE" : attributesQuery;

		String cql = cqlGeometry + " AND " + cqlDates + " AND " + cqlAttributes;
		return CQL.toFilter(cql);
	}

	@Test
	public void testPersistRetrieveEntity()
			throws CoalescePersistorException, CoalesceDataFormatException, SAXException, IOException, CQLException {

		// Create/populate entity
		CoalesceSearchTestEntity1 searchEntity1 = new CoalesceSearchTestEntity1();
		searchEntity1.initialize();
		searchEntity1.addPointsToEntity();

		// Prerequisite setup
		_coalesceFramework.saveCoalesceEntityTemplate(CoalesceEntityTemplate.create(searchEntity1));
		CoalesceObjectFactory.register(CoalesceSearchTestEntity1.class);

		// Persist
		AccumuloPersistor persistor = (AccumuloPersistor) this.getPersistor(this.getConnection());
		persistor.saveEntity(true, searchEntity1);

		// Retrieve
		CoalesceEntity[] entities = persistor.getEntity(searchEntity1.getKey());
		assertEquals(1, entities.length);
		System.out.println(entities[0].toXml());

		DataStore geoDataStore = ((AccumuloDataConnector) persistor.getDataConnector()).getGeoDataStore();
		Filter cqlFilter = createFilter("PolygonAreaData", -180, -180, 180, 180, "DateTimeAreaData",
				"2014-07-01T00:00:00.000Z", "2016-12-31T00:00:00.000Z", "(StringAreaData = 'myareaname')");
		Query query = new Query("GeoSearch_Coalesce_1.0.GeoSearch_Area_Section.GeoSearch_Area_Recordset", cqlFilter);

		// submit the query, and get back an iterator over matching features
		FeatureSource<?, ?> featureSource = geoDataStore
				.getFeatureSource("GeoSearch_Coalesce_1.0.GeoSearch_Area_Section.GeoSearch_Area_Recordset");
		FeatureIterator<?> featureItr = featureSource.getFeatures(query).features();

		assertTrue(featureItr.hasNext());

		Feature feature = featureItr.next();
		assertEquals(searchEntity1.getKey(), feature.getProperty("entityKey").getValue());
		assertEquals(searchEntity1.getBooleanAreaData().getValue(),
				(Boolean) feature.getProperty("BooleanAreaData").getValue());
		assertEquals(searchEntity1.getStringAreaData().getValue(), feature.getProperty("StringAreaData").getValue());
		assertEquals(searchEntity1.getPolygonAreaData().getValue(), feature.getProperty("PolygonAreaData").getValue());
		featureItr.close();
		persistor.close();
	}

	private static void addPointsToEntity(CoalesceSearchTestEntity1 entity) throws CoalesceDataFormatException {
		GeometryFactory factory = new GeometryFactory();

		// entity1, the center point
		Coordinate e1pt1 = new Coordinate(-77.455811, 38.944533);
		Coordinate e1pt2 = new Coordinate(-77.037722, 38.852083);
		Coordinate e1pt3 = new Coordinate(-76.668333, 39.175361);

		Coordinate e1mp_1 = new Coordinate(-77.455811, 38.944533);
		Coordinate e1mp_2 = new Coordinate(-77.037722, 38.852083);
		Coordinate e1mp_3 = new Coordinate(-76.668333, 39.175361);

		entity.getGeocoordinatePointData().setValue(factory.createPoint(e1pt1));
		entity.getGeocoordinatePointData().setValue(factory.createPoint(e1pt2));
		entity.getGeocoordinatePointData().setValue(factory.createPoint(e1pt3));
		entity.getStringPointData().setValue("firstentitypoint");
		entity.getIntegerPointData().setValue(1);
		entity.getDateTimePointData().setValue(new DateTime());
		// searchEntity1.addPointRecord();

		entity.getGeoMultiPointData().setValue(new Coordinate[] { e1mp_1, e1mp_2, e1mp_3 });
		// searchEntity1.addMultiPointRecord();
		entity.getStringMultiPointData().setValue("somestringdatahere");
		entity.getBooleanMultipointData().setValue(true);
		entity.getDoubleMultipointData().setValue(5.0d);
		entity.getFloatMultipointData().setValue((float) 0.1);
		entity.getDateTimeMultiPointData().setValue(new DateTime());

		List<Coordinate> coordinates = new ArrayList<>();
		coordinates.add(new Coordinate(-77.455811, 38.944533));
		coordinates.add(new Coordinate(-77.037722, 38.852083));
		coordinates.add(new Coordinate(-76.668333, 39.175361));
		coordinates.add(new Coordinate(-77.455811, 38.944533));
		Polygon poly = new GeometryFactory().createPolygon(coordinates.toArray(new Coordinate[coordinates.size()]));
		entity.getPolygonAreaData().setValue(poly);
		entity.getStringAreaData().setValue("myareaname");
		entity.getBooleanAreaData().setValue(true);
		entity.getDateTimeAreaData().setValue(new DateTime());

	}

	@Test
	public void testSearch()
			throws CoalescePersistorException, CoalesceDataFormatException, SAXException, IOException, CQLException {

		// Create/populate entity
		CoalesceSearchTestEntity1 searchEntity1 = new CoalesceSearchTestEntity1();
		searchEntity1.initialize();
		searchEntity1.addPointsToEntity();

		// Prerequisite setup
		_coalesceFramework.saveCoalesceEntityTemplate(CoalesceEntityTemplate.create(searchEntity1));
		CoalesceObjectFactory.register(CoalesceSearchTestEntity1.class);

		// Persist
		AccumuloPersistor persistor = (AccumuloPersistor) this.getPersistor(this.getConnection());
		persistor.saveEntity(true, searchEntity1);

		// Retrieve
		Filter cqlFilter = createFilter("PolygonAreaData", -180, -180, 180, 180, "DateTimeAreaData",
				"2014-07-01T00:00:00.000Z", "2016-12-31T00:00:00.000Z", "(StringAreaData = 'myareaname')");
		Query query = new Query("GeoSearch_Coalesce_1.0.GeoSearch_Area_Section.GeoSearch_Area_Recordset", cqlFilter);
		List<CoalesceEntity> entities = persistor.search(query, null);
		assertFalse(entities.isEmpty());
		List<String> keys = new ArrayList<>();
		for (CoalesceEntity entity : entities) {
			keys.add(entity.getKey());
		}
		assertTrue(keys.contains(searchEntity1.getKey()));
		persistor.close();
	}

}
