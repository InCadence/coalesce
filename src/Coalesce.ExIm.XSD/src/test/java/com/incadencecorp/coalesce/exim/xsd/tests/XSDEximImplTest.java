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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import javax.xml.transform.dom.DOMSource;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.helpers.EntityLinkHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;
import com.incadencecorp.coalesce.exim.xsd.XSDEximImpl;
import com.incadencecorp.coalesce.exim.xsd.XSDGeneratorUtil;
import com.incadencecorp.coalesce.framework.EnumerationProviderUtil;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceConstraint;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEnumerationField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIntegerListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.framework.enumerationprovider.impl.JavaEnumerationProviderImpl;
import com.incadencecorp.coalesce.schema.caseobject.AccessControlSection;
import com.incadencecorp.coalesce.schema.caseobject.CaseObject;
import com.incadencecorp.coalesce.schema.caseobject.FloatMap;
import com.incadencecorp.coalesce.schema.caseobject.FloatMap.FloatMapRecord;
import com.incadencecorp.coalesce.schema.caseobject.Maps;
import com.incadencecorp.coalesce.schema.testentityexample.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.schema.testentityexample.Field1Type;
import com.incadencecorp.coalesce.schema.testentityexample.Linkages;
import com.incadencecorp.coalesce.schema.testentityexample.Linkages.Linkage;
import com.incadencecorp.coalesce.schema.testentityexample.TestEntityExample;
import com.incadencecorp.coalesce.schema.testentityexample.UnitTestRecordset;
import com.incadencecorp.coalesce.schema.testentityexample.UnitTestRecordset.UnitTestRecordsetRecord;
import com.incadencecorp.coalesce.schema.testentityexample.UnitTestRecordset.UnitTestRecordsetRecord.Field2;
import com.incadencecorp.coalesce.schema.testentityexample.UnitTestSection;

/**
 * These test ensure that objects can be created towards the schema and
 * validated.
 * 
 * @author Derek C.
 *
 */
public class XSDEximImplTest {

    private static final String ENTITY_NAME = "TestEntityExample";
    private static final String RECORDSET_NAME = "UnitTest Recordset";
    private static final String SECTION_NAME = "UnitTest Section";
    private static final String XSD_PATH = Paths.get(".", "src", "test", "resources", "test.xsd").toString();
    private static final String CAS_PATH = Paths.get(".", "src", "test", "resources", "case.xsd").toString();

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

        CoalesceEntity entity = createEntity();
        CoalesceRecordset rs = entity.getCoalesceRecordsetForNamePath(ENTITY_NAME, SECTION_NAME, RECORDSET_NAME);
        CoalesceRecord record = rs.addNew();

        ((CoalesceStringField) record.getFieldByName("field1")).setValue((String) "A");
        ((CoalesceIntegerListField) record.getFieldByName("field2")).setValue(new int[] {
                7, 8
        });

        XSDEximImpl exim = new XSDEximImpl();
        Document doc = exim.exportValues(entity, false);

        // Validate DOM
        XSDGeneratorUtil.validateXMLSchema(XSD_PATH, new DOMSource(doc));

    }

    /**
     * This test converts a Coalesce object to XML that conforms to the schema
     * 
     * @throws Exception
     */
    @Test
    public void testExIm() throws Exception
    {
        String field1Value = "A";
        int[] field2Value = new int[] {
                7, 8
        };
        int field3Value = 2;

        CoalesceEntity entity = createEntity();
        CoalesceRecordset rs = entity.getCoalesceRecordsetForNamePath(ENTITY_NAME, SECTION_NAME, RECORDSET_NAME);
        CoalesceRecord record = rs.addNew();

        ((CoalesceStringField) record.getFieldByName("field1")).setValue(field1Value);
        ((CoalesceIntegerListField) record.getFieldByName("field2")).setValue(field2Value);
        ((CoalesceEnumerationField) record.getFieldByName("field3")).setValue(field3Value);

        XSDEximImpl exim = new XSDEximImpl();
        Document doc = exim.exportValues(entity, false);

        LOGGER.info(formatPretty(doc));

        // Validate DOM
        XSDGeneratorUtil.validateXMLSchema(XSD_PATH, new DOMSource(doc));

        // Test Linkage Section
        CoalesceEntity entity2 = createEntity();

        EntityLinkHelper.linkEntities(entity,
                                      ELinkTypes.CREATED,
                                      entity2,
                                      new Marking(null),
                                      "",
                                      "",
                                      "Hello World",
                                      null,
                                      true,
                                      false,
                                      true);
        doc = exim.exportValues(entity, false);

        LOGGER.info(formatPretty(doc));

        // Validate DOM
        XSDGeneratorUtil.validateXMLSchema(XSD_PATH, new DOMSource(doc));

        CoalesceEntity imported = exim.importValues(doc, CoalesceEntityTemplate.create(entity));
        LOGGER.info(imported.toXml());

        rs = imported.getCoalesceRecordsetForNamePath(ENTITY_NAME, SECTION_NAME, RECORDSET_NAME);

        // Validate Values
        Assert.assertEquals(field1Value, ((CoalesceStringField) rs.getRecords().get(0).getFieldByName("field1")).getValue());
        Assert.assertArrayEquals(field2Value,
                                 ((CoalesceIntegerListField) rs.getRecords().get(0).getFieldByName("field2")).getValue());
        Assert.assertEquals(field3Value,
                            (int) ((CoalesceEnumerationField) rs.getRecords().get(0).getFieldByName("field3")).getValue());

        // Validate Linkage
        Assert.assertEquals(1, imported.getLinkages().size());
        Assert.assertEquals(entity.getKey(), imported.getKey());
    }

    @Ignore
    @Test
    public void testCaseObject() throws Exception
    {
        CaseObject entity = new CaseObject();
        entity.setSource("Omega");
        entity.setKey(UUID.randomUUID().toString());
        entity.setVersion("1.0");

        FloatMapRecord mapRecord = new FloatMapRecord();
        mapRecord.setKey(UUID.randomUUID().toString());

        Maps maps = new Maps();
        maps.setKey(UUID.randomUUID().toString());

        FloatMap floatMap = new FloatMap();
        floatMap.setKey(UUID.randomUUID().toString());
        floatMap.getFloatMapRecord().add(mapRecord);

        maps.setFloatMap(floatMap);
        entity.setMaps(maps);

        AccessControlSection section = new AccessControlSection();
        section.setKey(UUID.randomUUID().toString());

        entity.setAccessControlSection(section);

        com.incadencecorp.coalesce.schema.caseobject.Linkages linkages = new com.incadencecorp.coalesce.schema.caseobject.Linkages();
        linkages.setKey(UUID.randomUUID().toString());

        entity.setLinkages(linkages);

        XSDGeneratorUtil.validateXMLSchema(CAS_PATH.toString(),
                                           new DOMSource(XmlHelper.loadXmlFrom(XmlHelper.serialize(entity))));

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
        TestEntityExample example = new TestEntityExample();
        example.setSource("TestSource");
        example.setVersion("TestVersion");
        example.setKey(UUID.randomUUID().toString());
        example.setStatus(ECoalesceObjectStatus.ACTIVE);

        Linkages linkages = new Linkages();
        linkages.setKey(UUID.randomUUID().toString());

        Linkage linkage = new Linkage();
        linkage.setKey(UUID.randomUUID().toString());
        linkage.setEntity1Key(example.getKey());
        linkage.setEntity1Name(TestEntityExample.class.getSimpleName());
        linkage.setEntity1Source(example.getSource());
        linkage.setEntity1Version(example.getVersion());
        linkage.setEntity2Key(UUID.randomUUID().toString());
        linkage.setEntity2Name("Hello World");
        linkage.setEntity2Source("Test");
        linkage.setEntity2Version("1");
        linkage.setEntity2Objectversion("1");
        linkage.setClassificationmarking("U");
        linkage.setLabel("Test");
        linkage.setLinktype(com.incadencecorp.coalesce.schema.testentityexample.ELinkTypes.CREATED);

        linkages.getLinkage().add(linkage);

        example.setLinkages(linkages);

        UnitTestSection section = new UnitTestSection();
        section.setKey(UUID.randomUUID().toString());

        example.setUnitTestSection(section);

        UnitTestRecordset recordset = new UnitTestRecordset();
        recordset.setKey(UUID.randomUUID().toString());

        section.setUnitTestRecordset(recordset);

        // Create Record
        UnitTestRecordsetRecord record = new UnitTestRecordsetRecord();
        record.setKey(UUID.randomUUID().toString());
        record.setField1(Field1Type.A);
        record.setField2(new Field2());
        record.getField2().getValues().add(6);
        record.getField2().getValues().add(7);
        record.getField2().getValues().add(8);

        recordset.setUnitTestRecordsetRecord(record);

        // Convert to DOM
        Document doc = XmlHelper.loadXmlFrom(XmlHelper.serialize(example));

        LOGGER.info(formatPretty(doc));

        // Validate DOM
        XSDGeneratorUtil.validateXMLSchema(XSD_PATH, new DOMSource(doc));

        LOGGER.info(formatPretty(doc));

        XSDEximImpl exim = new XSDEximImpl();
        CoalesceEntity imported = exim.importValues(doc, CoalesceEntityTemplate.create(createEntity()));
        LOGGER.info(imported.toXml());

        CoalesceRecordset rs = imported.getCoalesceRecordsetForNamePath(ENTITY_NAME, SECTION_NAME, RECORDSET_NAME);

        // Validate Values
        Assert.assertEquals(Field1Type.A.toString(),
                            ((CoalesceStringField) rs.getRecords().get(0).getFieldByName("field1")).getValue());

        Assert.assertArrayEquals(new int[] {
                6, 7, 8
        }, ((CoalesceIntegerListField) rs.getRecords().get(0).getFieldByName("field2")).getValue());

        // Validate Linkage
        Assert.assertEquals(1, imported.getLinkages().size());
        Assert.assertEquals(example.getKey(), imported.getKey());

    }

    @Ignore
    @Test
    public void testCreateCaseXSD() throws Exception
    {

        String xml = new String(Files.readAllBytes(Paths.get("/home/evans_home/n78554/tmp2.txt")));

        CoalesceEntityTemplate template = CoalesceEntityTemplate.create(xml);

        Document xsdDoc = XSDGeneratorUtil.createXsd(null, template);

        LOGGER.info(formatPretty(xsdDoc));

        File file = new File(CAS_PATH.toString());
        FileWriter writer = new FileWriter(file);
        writer.write(formatPretty(xsdDoc));
        writer.close();

    }

    private static CoalesceEntity createEntity()
    {
        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();
        entity.setName(ENTITY_NAME);
        entity.setSource("TestSource");
        entity.setVersion("TestVersion");

        CoalesceSection section = CoalesceSection.create(entity, SECTION_NAME);
        CoalesceRecordset rs = CoalesceRecordset.create(section, RECORDSET_NAME);

        rs.setMaxRecords(1);
        rs.setMinRecords(1);

        CoalesceFieldDefinition fd = CoalesceFieldDefinition.create(rs, "field1", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceConstraint.createEnumeration(fd, "enum", ETest.class);

        fd = CoalesceFieldDefinition.create(rs, "field2", ECoalesceFieldDataTypes.INTEGER_LIST_TYPE);
        CoalesceConstraint.createMin(fd, "enum", 5, false);
        CoalesceConstraint.createMax(fd, "enum", 10, false);

        fd = CoalesceFieldDefinition.create(rs, "field3", ECoalesceFieldDataTypes.ENUMERATION_TYPE);
        CoalesceConstraint.createEnumeration(fd, fd.getName(), ETest.class);
        CoalesceConstraint.createMandatory(fd, "mandatory");

        fd = CoalesceFieldDefinition.create(rs, "field4", ECoalesceFieldDataTypes.ENUMERATION_LIST_TYPE);
        CoalesceConstraint.createEnumeration(fd, fd.getName(), ETest.class);

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

        Document xsdDoc = XSDGeneratorUtil.createXsd(null, CoalesceEntityTemplate.create(entity));

        LOGGER.info(formatPretty(xsdDoc));

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
