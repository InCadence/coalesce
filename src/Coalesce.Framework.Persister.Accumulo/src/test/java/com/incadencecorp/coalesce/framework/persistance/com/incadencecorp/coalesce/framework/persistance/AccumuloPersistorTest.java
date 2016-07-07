package com.incadencecorp.coalesce.framework.persistance;
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
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.sql.Date;
import java.util.Map;
import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.minicluster.MiniAccumuloCluster;
import org.apache.commons.io.FilenameUtils;
import org.apache.xerces.impl.dv.util.Base64;
import org.jdom2.JDOMException;
import org.joda.time.DateTime;

import com.drew.imaging.ImageProcessingException;
import com.incadencecorp.coalesce.common.CoalesceUnitTestSettings;
import com.incadencecorp.coalesce.common.exceptions.CoalesceCryptoException;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.DocumentProperties;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.MimeHelper;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.CoalesceSettings;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalescePersistorBaseTest;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;
import com.incadencecorp.coalesce.framework.persistance.accumulo.AccumuloDataConnector;
import com.incadencecorp.coalesce.framework.persistance.accumulo.AccumuloPersistor;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTWriter;
import com.vividsolutions.jts.util.GeometricShapeFactory;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceCircleField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLineStringField;
import com.incadencecorp.coalesce.framework.datamodel.CoalescePolygonField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceBinaryField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceBooleanField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCoordinateField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCoordinateListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDateTimeField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDoubleField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDoubleListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFileField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFloatField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFloatListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceGUIDField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIntegerField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIntegerListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLongField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLongListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringListField;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.datamodel.TestRecord;


import com.vividsolutions.jts.geom.GeometryFactory;


public class AccumuloPersistorTest extends CoalescePersistorBaseTest {

	private static MiniAccumuloCluster accumulo = null;
	private static Instance instance;
	//private static ServerConn _serCon;
   // private static CoalesceFramework _coalesceFramework;

    //private static CoalesceEntity _entity;
    //private static String _entityXml;
    //private static String _fieldKey;

    //private static String _testTemplateKey = null;
    
    private  static Connector conn;
    
    private static  CoalesceDataConnectorBase aconn;
      
	@Rule
	public TemporaryFolder tempDirectory = new TemporaryFolder();
	
    @BeforeClass
    public static void setupBeforeClass() throws SAXException, IOException, CoalesceException, TableNotFoundException,
    						AccumuloException, AccumuloSecurityException
    {
        AccumuloPersistorTest tester = new AccumuloPersistorTest();

        //CoalescePersistorBaseTest.setupBeforeClassBase(tester);
        _serCon = tester.getConnection();
        
        ICoalescePersistor persistor = tester.getPersistor(_serCon);

        _coalesceFramework = new CoalesceFramework();
        _coalesceFramework.initialize(persistor);

        //CoalescePersistorBaseTest.cleanUpDatabase(tester);
        // Clean up the database
        aconn = new AccumuloDataConnector(_serCon);
        conn =  AccumuloDataConnector.getDBConnector();
        
        conn.tableOperations().deleteRows(AccumuloDataConnector.coalesceTable, null, null);
        conn.tableOperations().deleteRows(AccumuloDataConnector.coalesceTemplateTable, null, null);
        conn.tableOperations().deleteRows(AccumuloDataConnector.coalesceEntityIndex, null, null);
        
        _entity = CoalescePersistorBaseTest.createEntity();
        _entityXml = _entity.toXml();

        _fieldKey = CoalescePersistorBaseTest.getCurrentStatusField(_entity).getKey();

        try
        {
            assertTrue(_coalesceFramework.saveCoalesceEntity(_entity));
        }
        catch (CoalescePersistorException e)
        {

            if (e.getCause() != null)
            {
                System.out.println(e.getCause().getMessage());
            }
            else
            {
                System.out.println(e.getMessage());
            }
            
            // If entity fails to save halt the persister test.
            Assume.assumeNoException(e);
        }

        CoalesceUnitTestSettings.initialize();
    }

    @AfterClass
    public static void tearDownAfterClass() throws IOException
    {
        //AccumuloPersistorTest tester = new AccumuloPersistorTest();

        CoalesceUnitTestSettings.tearDownAfterClass();
        //CoalescePersistorBaseTest.tearDownAfterClassBase(tester);

    }
    @After
    public void tearDown() 
    {

    }
    @Override
    protected ServerConn getConnection()
    {
        ServerConn serCon = new ServerConn();

        // InCadence Settings
         // serCon.setServerName("localhost");
         //serCon.setServerName("127.0.0.1:5432");
         //serCon.setDatabase("CoalesceDatabase");
         serCon.setUser("root");
         serCon.setPassword("secret");
         /*
         //  MiniCluster Code for long term testing 
         if (accumulo == null) {
        	 System.err.println("Creating MiniCluster");
    		 
        	 try {
        		 // Hack to get the Junit to actually create the folder.
            	 tempDirectory.create();
        		 System.err.println("Tempdir"+tempDirectory.getRoot());
        		 
         		 accumulo = new MiniAccumuloCluster(tempDirectory.getRoot(), serCon.getPassword());
        		 accumulo.start();
        		 instance = new ZooKeeperInstance(accumulo.getInstanceName(), accumulo.getZooKeepers());
        	 } catch (Exception e) {
        		 System.err.println("Error Creating MiniCluster: " + e.getMessage());
        		 return null;
        	 }


         }
        if (accumulo==null) {
       	 System.err.println("MiniClusternot created");
        }
        serCon.setDatabase(accumulo.getInstanceName());
        serCon.setServerName(accumulo.getZooKeepers());
*/
         /*  Temp code to connect to my VM */
        serCon.setDatabase("accumulodev");
        serCon.setServerName("accumulodev");

        return serCon;

    }

    @Override
    protected ICoalescePersistor getPersistor(ServerConn conn)
    {
    	System.err.println("getPersistor test:"+conn.getDatabase());
        AccumuloPersistor accumuloPersistor = new AccumuloPersistor();
        accumuloPersistor.initialize(conn);
        //accumuloPersistor.setSchema("coalesce");

        return accumuloPersistor;

    }

    @Override
    protected CoalesceDataConnectorBase getDataConnector(ServerConn conn) throws CoalescePersistorException
    {
//    	System.err.println("getDataConnector test:"+conn.getDatabase());
        return new AccumuloDataConnector(conn);
    }
    
    @Test
    public void testConnection() throws CoalescePersistorException, Exception
    {

    	Map<String,String> sysconf = conn.instanceOperations().getSystemConfiguration();
    	assertNotNull(sysconf);
    	for (Map.Entry<String,String> entry : sysconf.entrySet()) {
    		System.out.println(entry.getKey() + ":" + entry.getValue());
    		
    	}

    }
	static private String TESTFILENAME = "Desert.jpg";
    
    @Test
    public void AccumuloDataTypesTest() throws CoalesceDataFormatException, CoalescePersistorException, SAXException, IOException
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
        Coordinate coords[] = {new Coordinate(0, 0), new Coordinate(1, 1), new Coordinate(2, 2) };
        GeometryFactory gf = new GeometryFactory();
        LineString line = gf.createLineString(coords);  
        linefield.setValue(line);

        // Create a geo list
        CoalesceCoordinateListField geolistfield = record.getGeoListField();
        MultiPoint geolist = new GeometryFactory().createMultiPoint(coords);
        geolistfield.setValue(geolist);
        
        // Create a point value
        CoalesceCoordinateField coordfield = record.getGeoField();
        coordfield.setValue(new Coordinate(10,10));
        
        // Int
        CoalesceIntegerField intfield = record.getIntegerField();
        intfield.setValue(42);
        
        // Check a int list field
        int intlist[] = { 3,4,5,6 };
        CoalesceIntegerListField intlistfield = record.getIntegerListField();
        intlistfield.setValue(intlist);

        // Long
        CoalesceLongField longfield = record.getLongField();
        longfield.setValue((long) 42);
        
        // Check a long list field
        long longlist[] = { 3,4,5,6 };
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
        floatfield.setValue((float)3.145964);
        
        // Check a float list field
        float floatlist[] = { (float)3.145964, (float)7.87856, (float)10000.000045566 };
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
            fail("Error processing image file: "+ e.getMessage());
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
        CoalesceEntity filefieldentity  = _coalesceFramework.getCoalesceEntity(entity.getKey());
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
        assertEquals(ftest,fvalue,0);
        
        // Verify floatlist
        assertEquals(flisttest.length,flistvalue.length);    
        for (int i=0;i<flisttest.length;i++) { assertEquals(flisttest[i],Float.valueOf(flistvalue[i]),0); }
        
        
        // Verify double
        assertEquals(dtest,dvalue,0);
        
        // Verify doublelist
        assertEquals(dlisttest.length,dlistvalue.length);      
        for (int i=0;i<dlisttest.length;i++) { assertEquals(dlisttest[i],Double.valueOf(dlistvalue[i]),0); }
        
        // Verify boolean
        assertEquals(btest,bvalue);
        
        // Verify date
        assertEquals(datetest,datevalue);
 
                		
        // Verify File Field
        assertTrue((filefieldvalue.getValue() instanceof DocumentProperties));
        assertArrayEquals(filefieldbytesvalue, fileFieldBytestest);
        
        assertEquals(TESTFILENAME, filefieldvalue.getFilename());
        assertEquals(FilenameUtils.getExtension(TESTFILENAME), filefieldvalue.getExtension());
        assertEquals(MimeHelper.getMimeTypeForExtension(FilenameUtils.getExtension(TESTFILENAME)), filefieldvalue.getMimeType());
        assertEquals(filefieldbytesvalue.length, filefield.getSize());


    }

}

