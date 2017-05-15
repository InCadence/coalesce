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

package com.incadencecorp.coalesce.exim.xsd.tests;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.xml.transform.dom.DOMSource;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.helpers.EntityLinkHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;
import com.incadencecorp.coalesce.exim.xsd.XSDEximImpl;
import com.incadencecorp.coalesce.exim.xsd.XSDGeneratorUtil;
import com.incadencecorp.coalesce.framework.EnumerationProviderUtil;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCircle;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceConstraint;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import com.incadencecorp.coalesce.framework.datamodel.TestRecord;
import com.incadencecorp.coalesce.framework.datamodel.testentity.Circle;
import com.incadencecorp.coalesce.framework.datamodel.testentity.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.framework.datamodel.testentity.Linkages;
import com.incadencecorp.coalesce.framework.datamodel.testentity.Linkages.Linkage;
import com.incadencecorp.coalesce.framework.datamodel.testentity.StringType;
import com.incadencecorp.coalesce.framework.datamodel.testentity.Test1;
import com.incadencecorp.coalesce.framework.datamodel.testentity.Test1.Test1Record;
import com.incadencecorp.coalesce.framework.datamodel.testentity.Test1.Test1Record.Intlist;
import com.incadencecorp.coalesce.framework.datamodel.testentity.Testsection;
import com.incadencecorp.coalesce.framework.datamodel.testentity.UNITTEST;
import com.incadencecorp.coalesce.framework.enumerationprovider.impl.JavaEnumerationProviderImpl;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

/**
 * These test ensure that objects can be created towards the schema and
 * validated.
 * 
 * @author Derek C.
 *
 */
public class XSDEximImplTest {

    private static final String ATTRIBUTE_OTHER = "type";
    private static final Path TEST_RESOURCE = Paths.get(".", "src", "test", "resources");
    private static final String XSD_PATH = TEST_RESOURCE.resolve("test.xsd").toString();

    private static final Logger LOGGER = LoggerFactory.getLogger(XSDEximImplTest.class);

    /**
     * Initializes utilities required by these unit tests.
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void initialize() throws Exception
    {
        if (!EnumerationProviderUtil.isInitialized())
        {
            // This is required when converting ENUMERATION_TYPE fields.
            EnumerationProviderUtil.setEnumerationProviders(new JavaEnumerationProviderImpl());
        }

        testCreateExampleXSD();
    }

    /**
     * This test attempts to validate an entity that is missing a mandatory
     * field which should result in an exception.
     * 
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testExImMandatoryFailure() throws Exception
    {

        TestEntity entity = createEntity();
        TestRecord record = entity.addRecord1();

        record.getStringField().setValue("A");
        record.getIntegerListField().setValue(new int[] {
                7, 8
        });

        XSDEximImpl exim = new XSDEximImpl();
        Document doc = exim.exportValues(entity, false);

        // Validate DOM
        XSDGeneratorUtil.validateXMLSchema(XSD_PATH, new DOMSource(doc));

    }

    private static final GeometryFactory GF = new GeometryFactory();

    /**
     * This test converts a Coalesce object to XML that conforms to the schema
     * 
     * @throws Exception
     */
    @Test
    public void testExIm() throws Exception
    {
        String otherAttributeValue = "test";

        String field1Value = "A";
        int[] field2Value = new int[] {
                7, 8
        };
        int field3Value = 2;

        TestEntity entity = createEntity();
        TestRecord record = entity.addRecord1();

        record.getStringField().setValue(field1Value);
        record.getStringListField().setValue(new String[] {
                "01234567890123456789AA", "98765432109876543210AA"
        });
        record.getIntegerListField().setValue(field2Value);
        record.getEnumerationField().setValue(field3Value);
        record.getEnumerationListField().setValue(field2Value);

        record.getGeoField().setValue(new Coordinate(5.6, 3));
        record.getGeoListField().setValue(new Coordinate[] {
                new Coordinate(1, 2, 3), new Coordinate(3, 2.5, 1)
        });

        CoalesceCircle circle = new CoalesceCircle();
        circle.setCenter(new Coordinate(1, 1, 1));
        circle.setRadius(5);

        record.getCircleField().setValue(circle);

        Polygon polygon = GF.createPolygon(new Coordinate[] {
                new Coordinate(1, 2, 3), new Coordinate(-3, -2.2, 1), new Coordinate(2, 3, 3), new Coordinate(1, 2, 3)
        });

        record.getPolygonField().setValue(polygon);

        LineString linestring = GF.createLineString(new Coordinate[] {
                new Coordinate(1, 2, 3), new Coordinate(3, 2.3, 1)
        });

        record.getLineField().setValue(linestring);

        entity.setAttribute(ATTRIBUTE_OTHER, otherAttributeValue);

        XSDEximImpl exim = new XSDEximImpl();
        Document doc = exim.exportValues(entity, false);

        LOGGER.info(formatPretty(doc));

        // Validate DOM
        XSDGeneratorUtil.validateXMLSchema(XSD_PATH, new DOMSource(doc));

        // Test Linkage Section
        CoalesceEntity entity2 = createEntity();

        EntityLinkHelper.linkEntities(entity,
                                      ELinkTypes.HAS_INPUT_OF,
                                      entity2,
                                      new Marking(null),
                                      "",
                                      "",
                                      "01234567890123456789AA",
                                      null,
                                      true,
                                      false,
                                      true);
        doc = exim.exportValues(entity, false);

        LOGGER.info(formatPretty(doc));

        // Validate DOM
        XSDGeneratorUtil.validateXMLSchema(XSD_PATH, new DOMSource(doc));

        TestEntity imported = new TestEntity();
        imported.initialize(exim.importValues(doc, CoalesceEntityTemplate.create(entity)));
        LOGGER.info(imported.toXml());

        TestRecord importedRecord = new TestRecord(imported.getRecordset1().getRecords().get(0));

        // Validate Values
        for (CoalesceField<?> field : record.getFields())
        {
            if (field.getDataType() == ECoalesceFieldDataTypes.STRING_LIST_TYPE)
            {
                for (int ii = 0; ii < field.getBaseValues().length; ii++)
                {
                    // Verify Truncation
                    Assert.assertEquals(field.getBaseValues()[ii].substring(0, 20),
                                        importedRecord.getFieldByName(field.getName()).getBaseValues()[ii]);
                }
            }
            else
            {
                Assert.assertEquals(field.getBaseValue(), importedRecord.getFieldByName(field.getName()).getBaseValue());
            }
        }

        // Validate Linkage
        Assert.assertEquals(1, imported.getLinkages().size());
        Assert.assertNull("Linkage was Undefined", imported.getLinkage(ELinkTypes.UNDEFINED));
        Assert.assertEquals(entity.getKey(), imported.getKey());
        Assert.assertEquals(otherAttributeValue, imported.getAttribute(ATTRIBUTE_OTHER));

        CoalesceLinkage originalLinkage = entity.getLinkage(ELinkTypes.HAS_INPUT_OF);
        CoalesceLinkage importedLinkage = imported.getLinkage(ELinkTypes.HAS_INPUT_OF);

        Assert.assertNotNull(importedLinkage);
        Assert.assertEquals(originalLinkage.getEntity1Key(), importedLinkage.getEntity1Key());
        Assert.assertEquals(originalLinkage.getEntity1Name(), importedLinkage.getEntity1Name());
        Assert.assertEquals(originalLinkage.getEntity1Source(), importedLinkage.getEntity1Source());
        Assert.assertEquals(originalLinkage.getEntity1Version(), importedLinkage.getEntity1Version());
        Assert.assertEquals(originalLinkage.getEntity2Key(), importedLinkage.getEntity2Key());
        Assert.assertEquals(originalLinkage.getEntity2Name(), importedLinkage.getEntity2Name());
        Assert.assertEquals(originalLinkage.getEntity2Source(), importedLinkage.getEntity2Source());
        Assert.assertEquals(originalLinkage.getEntity2Version(), importedLinkage.getEntity2Version());
        Assert.assertEquals(originalLinkage.getLinkType(), importedLinkage.getLinkType());
        Assert.assertEquals(originalLinkage.getStatus(), importedLinkage.getStatus());
        Assert.assertEquals(originalLinkage.getLabel().substring(0, 20), importedLinkage.getLabel());

    }

    /**
     * This test creates an object from the generated classes and ensures that
     * it validates.
     * 
     * @throws Exception
     */
    @Test
    public void testExampleObject() throws Exception
    {
        // Create Entity
        UNITTEST example = new UNITTEST();
        example.setKey(UUID.randomUUID().toString());
        example.setStatus(ECoalesceObjectStatus.ACTIVE);

        Linkages linkages = new Linkages();
        linkages.setKey(UUID.randomUUID().toString());

        Linkage linkage = new Linkage();
        linkage.setKey(UUID.randomUUID().toString());
        linkage.setEntity1Key(example.getKey());
        linkage.setEntity1Name(UNITTEST.class.getSimpleName());
        linkage.setEntity1Source(example.getSource());
        linkage.setEntity1Version(example.getVersion());
        linkage.setEntity2Key(UUID.randomUUID().toString());
        linkage.setEntity2Name("Hello World");
        linkage.setEntity2Source("Test");
        linkage.setEntity2Version("1");
        linkage.setEntity2Objectversion(1);
        linkage.setClassificationmarking("U");
        linkage.setLabel("Test");
        linkage.setLinktype(com.incadencecorp.coalesce.framework.datamodel.testentity.ELinkTypes.CREATED);

        linkages.getLinkage().add(linkage);

        example.setLinkages(linkages);

        Testsection section = new Testsection();
        section.setKey(UUID.randomUUID().toString());

        example.setTestsection(section);

        Test1 recordset = new Test1();
        recordset.setKey(UUID.randomUUID().toString());

        section.setTest1(recordset);

        // Create Record
        Test1Record record = new Test1Record();
        record.setKey(UUID.randomUUID().toString());
        record.setString(StringType.A);
        // record.setField2(new Field2());
        Intlist intlist = new Intlist();
        intlist.getValues().add(6);
        intlist.getValues().add(7);
        intlist.getValues().add(8);

        record.setIntlist(intlist);
        record.setGeo("POINT (1 2 3)");
        record.setGeolist("MULTIPOINT ((1 2 3), (1.2 3.5 5), (5 1 2))");
        record.setLine("LINESTRING ((1 2 3), (1.2 3.5 3), (5 1 2))");
        record.setPoly("POLYGON ((1 2 3), (1.2 3.5 6), 5 1 2)");

        // Circle
        Circle circle = new Circle();
        circle.setCenter("POINT (1 2 3)");
        circle.setRadius(5.2);

        record.setCircle(circle);

        recordset.setTest1Record(record);

        // Convert to DOM
        Document doc = XmlHelper.loadXmlFrom(XmlHelper.serialize(example));

        LOGGER.info(formatPretty(doc));

        // Validate DOM
        XSDGeneratorUtil.validateXMLSchema(XSD_PATH, new DOMSource(doc));

        LOGGER.info(formatPretty(doc));

        XSDEximImpl exim = new XSDEximImpl();

        TestEntity imported = new TestEntity();
        imported.initialize(exim.importValues(doc, CoalesceEntityTemplate.create(createEntity())));
        LOGGER.info(imported.toXml());

        TestRecord importedRecord = new TestRecord(imported.getRecordset1().getRecords().get(0));

        // Validate Values
        Assert.assertEquals(StringType.A.toString(), importedRecord.getStringField().getValue());

        Assert.assertArrayEquals(new int[] {
                6, 7, 8
        }, importedRecord.getIntegerListField().getValue());

        CoalesceCircle circleValue = importedRecord.getCircleField().getValue();

        Assert.assertEquals(1, circleValue.getCenter().x, 0);
        Assert.assertEquals(2, circleValue.getCenter().y, 0);
        Assert.assertEquals(3, circleValue.getCenter().z, 0);
        Assert.assertEquals(5.2, circleValue.getRadius(), 0);

        // Validate Linkage
        Assert.assertEquals(1, imported.getLinkages().size());
        Assert.assertEquals(example.getKey(), imported.getKey());

    }

    /**
     * Ensures that the schema supports nested sections.
     * 
     * @throws Exception
     */
    @Test
    public void testNestedSections() throws Exception
    {

        CoalesceEntity entity = CoalesceEntity.create("Test", "Test", "Test", null, null);
        CoalesceSection s1 = CoalesceSection.create(entity, "S1");
        CoalesceSection s2 = CoalesceSection.create(s1, "S2");
        CoalesceRecordset rs = CoalesceRecordset.create(s2, "RS");

        CoalesceFieldDefinition.create(rs, "field", ECoalesceFieldDataTypes.STRING_TYPE);

        Document doc = XSDGeneratorUtil.createXsd(null, CoalesceEntityTemplate.create(entity));

        LOGGER.info(formatPretty(doc));

        CoalesceRecord record = rs.addNew();
        ((CoalesceStringField) record.getFieldByName("field")).setValue("Hello World");

        XSDEximImpl exim = new XSDEximImpl();
        Document entityDoc = exim.exportValues(entity, true);

        LOGGER.info(formatPretty(entityDoc));

        XSDGeneratorUtil.validateXMLSchema(new DOMSource(doc), new DOMSource(entityDoc));

    }

    /**
     * Ensures that pruning fields wont invalidate an object.
     * 
     * @throws Exception
     */
    @Test
    public void testPrundedFields() throws Exception
    {
        CoalesceEntity entity = CoalesceEntity.create("Test", "Test", "Test", null, null);
        CoalesceSection s1 = CoalesceSection.create(entity, "S1");
        CoalesceRecordset rs = CoalesceRecordset.create(s1, "RS");

        CoalesceFieldDefinition fd;

        fd = CoalesceFieldDefinition.create(rs, "field1", ECoalesceFieldDataTypes.STRING_TYPE);
        fd = CoalesceFieldDefinition.create(rs, "field2", ECoalesceFieldDataTypes.STRING_TYPE);

        CoalesceConstraint.createMandatory(fd, "mandatory");

        Document doc = XSDGeneratorUtil.createXsd(null, CoalesceEntityTemplate.create(entity));

        CoalesceRecord record = rs.addNew();
        ((CoalesceStringField) record.getFieldByName("field1")).setValue("Hello");
        ((CoalesceStringField) record.getFieldByName("field2")).setValue("World");

        // Attempting to prune a field
        record.pruneCoalesceObject(record.getFieldByName("field1"));

        XSDEximImpl exim = new XSDEximImpl();

        XSDGeneratorUtil.validateXMLSchema(new DOMSource(doc), new DOMSource(exim.exportValues(entity, true)));

        // Attempting to prune a mandatory field
        record.pruneCoalesceObject(record.getFieldByName("field2"));

        try
        {
            XSDGeneratorUtil.validateXMLSchema(new DOMSource(doc), new DOMSource(exim.exportValues(entity, true)));
            Assert.fail("Expected: " + IllegalArgumentException.class.getName());
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }
    }

    /**
     * This test ensures that there are no issues merging entities that have
     * been exported and imported.
     * 
     * @throws Exception
     */
    @Test
    public void testMergingEntities() throws Exception
    {
        XSDEximImpl exim = new XSDEximImpl();

        // Create Entity
        TestEntity entity = new TestEntity();
        entity.initialize();

        TestRecord record = entity.addRecord1();
        record.getStringField().setValue("Hello World");

        // Export Entity
        Document doc = exim.exportValues(entity, true);

        // Import Entity
        TestEntity entity2 = new TestEntity();
        entity2.initialize();

        exim.importValues(doc, entity2);

        TestRecord entity2Record = new TestRecord(entity2.getRecordset1().getRecords().get(0));

        Assert.assertEquals(record.getStringField().getValue(), entity2Record.getStringField().getValue());

        // Update Entity
        record.getStringField().setValue("Updated");

        // Export
        doc = exim.exportValues(entity, true);

        // Import Entity
        TestEntity entity3 = new TestEntity();
        entity3.initialize();

        exim.importValues(doc, entity3);

        TestRecord entity3Record = new TestRecord(entity3.getRecordset1().getRecords().get(0));

        Assert.assertNotEquals(record.getStringField().getValue(), entity2Record.getStringField().getValue());
        Assert.assertEquals(record.getStringField().getValue(), entity3Record.getStringField().getValue());

        // Merge Entities
        TestEntity merged = new TestEntity();
        merged.initialize(CoalesceEntity.mergeSyncEntity(entity2, entity3, "test", "localhost"));

        // Verify Value (Field should have been merged)
        TestRecord mergedRecord = new TestRecord(merged.getRecordset1().getRecords().get(0));

        // Verify Value & Number of Fields
        Assert.assertEquals(record.getStringField().getValue(), mergedRecord.getStringField().getValue());
        Assert.assertEquals(record.getFields().size(), mergedRecord.getFields().size());

    }

    private static TestEntity createEntity()
    {
        TestEntity entity = new TestEntity();
        entity.initialize();

        CoalesceRecordset rs = entity.getRecordset1();

        rs.setMinRecords(1);
        rs.setMaxRecords(1);

        CoalesceFieldDefinition fd;

        fd = rs.getFieldDefinition("enum");
        CoalesceConstraint.createMandatory(fd, "mandatory");

        fd = rs.getFieldDefinition("string");
        CoalesceConstraint.createMandatory(fd, "mandatory", true);
        CoalesceConstraint.createEnumeration(fd, "enum", ETest.class);

        fd = rs.getFieldDefinition("line");
        CoalesceConstraint.createMandatory(fd, "mandatory", true);

        fd = rs.getFieldDefinition("intlist");
        CoalesceConstraint.createMin(fd, "enum", 5, false);
        CoalesceConstraint.createMax(fd, "enum", 10, false);

        return entity;
    }

    /**
     * This test creates an XSD from the generated object.
     * 
     * @throws Exception
     */
    private static void testCreateExampleXSD() throws Exception
    {
        CoalesceEntity entity = createEntity();
        entity.setAttribute(ATTRIBUTE_OTHER, "");

        Document xsdDoc = XSDGeneratorUtil.createXsd(null, CoalesceEntityTemplate.create(entity));

        LOGGER.debug(formatPretty(xsdDoc));

        File file = new File(XSD_PATH);
        FileWriter writer = new FileWriter(file);
        writer.write(formatPretty(xsdDoc));
        writer.close();

    }

    private static String formatPretty(Node document)
    {
        return XmlHelper.formatXml(document);
    }

    private enum ETest
    {
        A, B, C;
    }

}
