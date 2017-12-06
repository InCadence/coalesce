package com.incadencecorp.coalesce.framework.persistance;

import com.drew.imaging.ImageProcessingException;
import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.CoalesceUnitTestSettings;
import com.incadencecorp.coalesce.common.exceptions.CoalesceCryptoException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.DocumentProperties;
import com.incadencecorp.coalesce.common.helpers.EntityLinkHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.MimeHelper;
import com.incadencecorp.coalesce.framework.CoalesceSettings;
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.WKTWriter;
import com.vividsolutions.jts.util.GeometricShapeFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.xerces.impl.dv.util.Base64;
import org.jdom2.JDOMException;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

public abstract class AbstractCoalescePersistorTest<T extends ICoalescePersistor> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCoalescePersistorTest.class);

    protected abstract T createPersister() throws CoalescePersistorException;

    private static boolean isInitialized = false;

    @Before
    public void registerEntities()
    {
        if (!isInitialized)
        {
            LOGGER.warn("Registering Entities");

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
     * This test attempts to create a entity within the data store.
     *
     * @throws Exception
     */
    @Test
    public void testCreation() throws Exception
    {
        T persister = createPersister();

        Assume.assumeTrue(persister.getCapabilities().contains(EPersistorCapabilities.CREATE));

        TestEntity entity = new TestEntity();
        entity.initialize();

        Assert.assertTrue(persister.saveEntity(false, entity));

        TestEntity entity2 = new TestEntity();
        entity2.initialize();

        EntityLinkHelper.linkEntities(entity, ELinkTypes.IS_PARENT_OF, entity2);

        Assert.assertTrue(persister.saveEntity(true, entity, entity2));

        // Cleanup
        entity.markAsDeleted();

        persister.saveEntity(true, entity);

    }

    /**
     * This test attempts to create and then update a entity within the data
     * store.
     *
     * @throws Exception
     */
    @Test
    public void testUpdates() throws Exception
    {
        T persister = createPersister();

        Assume.assumeTrue(persister.getCapabilities().contains(EPersistorCapabilities.CREATE));
        Assume.assumeTrue(persister.getCapabilities().contains(EPersistorCapabilities.READ));
        Assume.assumeTrue(persister.getCapabilities().contains(EPersistorCapabilities.UPDATE));

        TestEntity entity = new TestEntity();
        entity.initialize();

        Assert.assertTrue(persister.saveEntity(false, entity));

        TestRecord record1 = entity.addRecord1();
        record1.getBooleanField().setValue(true);

        Assert.assertTrue(persister.saveEntity(false, entity));

        CoalesceEntity updated = persister.getEntity(entity.getKey())[0];

        CoalesceRecord updatedRecord = (CoalesceRecord) updated.getCoalesceObjectForKey(record1.getKey());

        Assert.assertNotNull(updatedRecord);
        Assert.assertEquals(record1.getBooleanField().getBaseValue(),
                            updatedRecord.getFieldByName(record1.getBooleanField().getName()).getBaseValue());

        // Cleanup
        entity.markAsDeleted();

        persister.saveEntity(true, entity);
    }

    /**
     * This test attempts to mark an entity as deleted as well as remove it
     * Completely.
     *
     * @throws Exception
     */
    @Test
    public void testDeletion() throws Exception
    {
        T persister = createPersister();

        Assume.assumeTrue(persister.getCapabilities().contains(EPersistorCapabilities.CREATE));
        Assume.assumeTrue(persister.getCapabilities().contains(EPersistorCapabilities.DELETE));

        TestEntity entity = new TestEntity();
        entity.initialize();
        entity.addRecord1().getStringField().setValue("Hello");
        entity.addRecord1().getStringField().setValue("World");

        Assert.assertTrue(persister.saveEntity(false, entity));
        Assert.assertNotNull(persister.getEntityXml(entity.getKey())[0]);

        entity.addRecord1().getStringField().setValue("New Record");
        entity.markAsDeleted();

        Assert.assertTrue(persister.saveEntity(true, entity));
        Assert.assertEquals(0, persister.getEntityXml(entity.getKey()).length);
    }

    /**
     * This test attempts to retrieve an invalid entity key and should fail.
     *
     * @throws Exception
     */
    @Test
    public void testRetrieveInvalidKey() throws Exception
    {
        T persister = createPersister();

        Assume.assumeTrue(persister.getCapabilities().contains(EPersistorCapabilities.CREATE));
        Assume.assumeTrue(persister.getCapabilities().contains(EPersistorCapabilities.READ));

        TestEntity entity = new TestEntity();
        entity.initialize();

        Assert.assertTrue(persister.saveEntity(false, entity));

        String results[] = persister.getEntityXml(entity.getKey(),
                                                  UUID.randomUUID().toString(),
                                                  UUID.randomUUID().toString(),
                                                  UUID.randomUUID().toString());

        Assert.assertEquals(1, results.length);
        Assert.assertEquals(entity.getKey(), CoalesceEntity.create(results[0]).getKey());
    }

    /**
     * This test attempts to save a template and retrieve it.
     *
     * @throws Exception
     */
    @Test
    public void testTemplates() throws Exception
    {
        T persister = createPersister();

        Assume.assumeTrue(persister.getCapabilities().contains(EPersistorCapabilities.READ_TEMPLATES));

        TestEntity entity = new TestEntity();
        entity.initialize();

        CoalesceEntityTemplate template1 = CoalesceEntityTemplate.create(entity);
        entity.setName("HelloWorld");
        CoalesceEntityTemplate template2 = CoalesceEntityTemplate.create(entity);

        persister.saveTemplate(template1, template2);

        Assert.assertEquals(template1.getKey(), persister.getEntityTemplate(template1.getKey()).getKey());
        Assert.assertEquals(template2.getKey(), persister.getEntityTemplate(template2.getKey()).getKey());
    }

    /**
     * This test verifies that an exception is thrown when attempting to retrieve an invalid template with a key.
     *
     * @throws Exception
     */
    @Test
    public void testTemplatesInvalid() throws Exception
    {
        T persister = createPersister();

        Assume.assumeTrue(persister.getCapabilities().contains(EPersistorCapabilities.READ_TEMPLATES));

        String key = UUID.randomUUID().toString();

        try
        {
            persister.getEntityTemplate(key);
            Assert.fail("Expected an Exception");
        }
        catch (CoalescePersistorException e)
        {
            Assert.assertEquals(String.format(CoalesceErrors.NOT_FOUND, "Template", key), e.getMessage());
        }

        try
        {
            persister.getEntityTemplate(key, key, key);
            Assert.fail("Expected an Exception");
        }
        catch (CoalescePersistorException e)
        {
            Assert.assertEquals(String.format(CoalesceErrors.NOT_FOUND,
                                              "Template",
                                              "Name: " + key + " Source: " + key + " Version: " + key), e.getMessage());
        }
    }


    @Test
    public void testAllDataTypes() throws Exception
    {
        String filename = "Desert.jpg";
        T persistor = createPersister();

        Assume.assumeTrue(persistor.getCapabilities().contains(EPersistorCapabilities.GET_FIELD_VALUE));

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
        String fileName = CoalesceUnitTestSettings.getResourceAbsolutePath(filename);

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
        String cvalue = getFieldValue(circlefield.getKey());
        String pvalue = getFieldValue(polygonfield.getKey());
        String lvalue = getFieldValue(linefield.getKey());
        float fvalue = Float.valueOf(getFieldValue(floatfield.getKey()));
        String flistvalue[] = (getFieldValue(floatlistfield.getKey())).split(",");
        double dvalue = Double.valueOf(getFieldValue(doublefield.getKey()));
        String dlistvalue[] = (getFieldValue(doublelistfield.getKey())).split(",");
        boolean bvalue = Boolean.valueOf(getFieldValue(booleanfield.getKey()));
        String mydate = getFieldValue(datetimefield.getKey());
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
        Assert.assertEquals(ctest, cvalue);
        Assert.assertEquals(CIRCLERADIUS, Double.valueOf(circlefield.getAttribute("radius")), 0);

        // Verify Polygon
        Assert.assertEquals(ptest, pvalue);

        // Verify line
        Assert.assertEquals(ltest, lvalue);

        // Verify float
        Assert.assertEquals(ftest, fvalue, 0);

        // Verify floatlist
        Assert.assertEquals(flisttest.length, flistvalue.length);
        for (int i = 0; i < flisttest.length; i++)
        {
            assertEquals(flisttest[i], Float.valueOf(flistvalue[i]), 0);
        }

        // Verify double
        assertEquals(dtest, dvalue, 0);

        // Verify doublelist
        Assert.assertEquals(dlisttest.length, dlistvalue.length);
        for (int i = 0; i < dlisttest.length; i++)
        {
            assertEquals(dlisttest[i], Double.valueOf(dlistvalue[i]), 0);
        }

        // Verify boolean
        Assert.assertEquals(btest, bvalue);

        // Verify date
        Assert.assertEquals(datetest, datevalue);

        // Verify File Field
        Assert.assertTrue((filefieldvalue.getValue() instanceof DocumentProperties));
        Assert.assertArrayEquals(filefieldbytesvalue, fileFieldBytestest);

        Assert.assertEquals(filename, filefieldvalue.getFilename());
        Assert.assertEquals(FilenameUtils.getExtension(filename), filefieldvalue.getExtension());
        Assert.assertEquals(MimeHelper.getMimeTypeForExtension(FilenameUtils.getExtension(filename)),
                            filefieldvalue.getMimeType());
        Assert.assertEquals(filefieldbytesvalue.length, filefield.getSize());

    }

    /**
     * Should be overriden by any persister that supports gettting field values.
     *
     * @param key
     * @return
     */
    public String getFieldValue(String key) throws CoalescePersistorException
    {
        return null;
    }
}
