package com.incadencecorp.coalesce.common.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.UnsupportedCharsetException;

import org.apache.commons.lang.NullArgumentException;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.incadencecorp.coalesce.common.CoalesceAssert;
import com.incadencecorp.coalesce.common.CoalesceTypeInstances;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.generatedjaxb.Entity;
import com.incadencecorp.coalesce.framework.generatedjaxb.Linkagesection;
import com.incadencecorp.coalesce.framework.generatedjaxb.Recordset;

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

public class XmlHelperTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /*
     * @BeforeClass public static void setUpBeforeClass() throws Exception { }
     * 
     * @AfterClass public static void tearDownAfterClass() throws Exception { }
     * 
     * @Before public void setUp() throws Exception { }
     * 
     * @After public void tearDown() throws Exception { }
     */

    @Test
    public void serializeEntityTypeMission()
    {

        Entity entity = new Entity();

        entity = (Entity) XmlHelper.deserialize(CoalesceTypeInstances.TEST_MISSION, Entity.class);

        String xml = XmlHelper.serialize(entity);

        assertNotNull("Searialize failed", xml);
        assertTrue("xml empty", xml.length() > 0);

        assertEquals(4, entity.getLinkagesection().getLinkage().size());
        assertEquals(17, entity.getSection().get(1).getRecordset().get(0).getFielddefinition().size());
        assertEquals(17, entity.getSection().get(1).getRecordset().get(0).getRecord().get(0).getField().size());
    }

    @Test
    public void serializeEntityTypeEncodingFormatMission()
    {
        Entity entity = new Entity();
        
        entity = (Entity) XmlHelper.deserialize(CoalesceTypeInstances.TEST_MISSION, Entity.class);

        String entityXml = XmlHelper.serialize(entity, "UTF8");

        CoalesceAssert.assertXmlEquals(CoalesceTypeInstances.TEST_MISSION, entityXml);

    }

    @Test
    public void serializeEntityTypeInvalidFormatTest()
    {
        thrown.expect(UnsupportedCharsetException.class);

        Entity entity = new Entity();

        entity = (Entity) XmlHelper.deserialize(CoalesceTypeInstances.TEST_MISSION, Entity.class);

        XmlHelper.serialize(entity, "Xyz");
    }

    @Test
    public void serializeEntityNotSerializableObjectTest()
    {
        Integer serInt = 5;

        String serialized = XmlHelper.serialize(serInt);

        assertNull("Serialized non serializable object", serialized);

    }

    @Test
    public void serializeLinkageSectionTypeMission()
    {

        Linkagesection entityLinkageSection = new Linkagesection();

        entityLinkageSection = (Linkagesection) XmlHelper.deserialize(CoalesceTypeInstances.TEST_MISSION_LINKAGE_SECTION,
                                                                      Linkagesection.class);

        String xml = XmlHelper.serialize(entityLinkageSection);

        assertNotNull("Searialize failed", xml);
        assertFalse("xml empty", xml.isEmpty());

        String stripped = xml.toString().replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");
        assertEquals(CoalesceTypeInstances.TEST_MISSION_LINKAGE_SECTION.replaceAll("\\s+", ""),
                     stripped.replace(" ", "").replaceAll("\\s+", ""));

        assertEquals(4, entityLinkageSection.getLinkage().size());

    }

    @Test
    public void serializeRecordSetTypeMission()
    {

        Recordset entityRecordSet = new Recordset();

        entityRecordSet = (Recordset) XmlHelper.deserialize(CoalesceTypeInstances.TEST_MISSION_RECORDSET, Recordset.class);

        String xml = XmlHelper.serialize(entityRecordSet);

        assertNotNull("Searialize failed", xml);
        assertTrue("xml empty", xml.length() > 0);
        assertEquals(16, entityRecordSet.getFielddefinition().size());
        assertEquals(16, entityRecordSet.getRecord().get(0).getField().size());

    }

    @Test
    public void deserializeEntityTypeMission()
    {

        Object desObj = XmlHelper.deserialize(CoalesceTypeInstances.TEST_MISSION, Entity.class);

        assertNotNull("Failed to deserialize mission entity", desObj);
        assertTrue("Deserialized object no an Entity", desObj instanceof Entity);

    }

    @Test
    public void deserializeLinkageSectionTypeMission()
    {

        Object desObj = XmlHelper.deserialize(CoalesceTypeInstances.TEST_MISSION_LINKAGE_SECTION, Linkagesection.class);

        assertNotNull("Failed to deserialize mission entity linkage section", desObj);
        assertTrue("Deserialized object no an Entity", desObj instanceof Linkagesection);

    }

    @Test
    public void deserializeRecordSetTypeMission()
    {

        Object desObj = XmlHelper.deserialize(CoalesceTypeInstances.TEST_MISSION_RECORDSET, Recordset.class);

        assertNotNull("Failed to deserialize mission entity linkage section", desObj);
        assertTrue("Deserialized object no an Entity", desObj instanceof Recordset);

    }

    @Test
    public void deserializeEntityTypeInvalidStructureTest()
    {
        String entityXmlInvalid = "<entity key=\"62857EF8-3930-4F0E-BAE3-093344EBF389\" datecreated=\"2014-05-02T14:33:51.8515756Z\" lastmodified=\"2014-05-20T16:17:13.2293139Z\" status=\"active\" >";

        Entity entity = new Entity();

        entity = (Entity) XmlHelper.deserialize(entityXmlInvalid, Entity.class);

        assertNull("Invalid XML generated output", entity);

    }

    @Test
    public void getAttributeTest() throws SAXException, IOException
    {
        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        Node entityNode = entityDoc.getElementsByTagName("entity").item(0);

        assertEquals("TREXMission", XmlHelper.getAttribute(entityNode, "name"));
        assertEquals("TREX Portal", XmlHelper.getAttribute(entityNode, "source"));
        assertEquals("1.0.0.0", XmlHelper.getAttribute(entityNode, "version"));
        assertEquals("Test", XmlHelper.getAttribute(entityNode, "anthony"));

    }

    @Test
    public void getAttributeNoneTest() throws SAXException, IOException
    {
        Document entityDoc = XmlHelper.loadXmlFrom("<entity />");

        Node entityNode = entityDoc.getElementsByTagName("entity").item(0);

        assertEquals("", XmlHelper.getAttribute(entityNode, "xyz"));

    }

    @Test
    public void getAttributeNullNodeTest()
    {
        thrown.expect(NullArgumentException.class);
        thrown.expectMessage("xmlNode");

        XmlHelper.getAttribute(null, "Test");

    }

    @Test
    public void getAttributeNullNameTest() throws SAXException, IOException
    {
        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        Node entityNode = entityDoc.getElementsByTagName("entity").item(0);

        assertEquals("", XmlHelper.getAttribute(entityNode, null));

    }

    @Test
    public void getAttributeAsDateTest() throws SAXException, IOException
    {
        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        Node entityNode = entityDoc.getElementsByTagName("entity").item(0);

        assertEquals(JodaDateTimeHelper.fromXmlDateTimeUTC("2014-05-20T16:17:13.2293139Z"),
                     XmlHelper.getAttributeAsDate(entityNode, "lastmodified"));

    }

    @Test
    public void getAttributeAsDateNotFoundTest() throws SAXException, IOException
    {
        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        Node entityNode = entityDoc.getElementsByTagName("entity").item(0);

        assertNull(XmlHelper.getAttributeAsDate(entityNode, "modified"));

    }

    @Test
    public void getAttributeAsDateNullNodeTest()
    {
        thrown.expect(NullArgumentException.class);
        thrown.expectMessage("xmlNode");

        XmlHelper.getAttributeAsDate(null, "lastmodified");
    }

    @Test
    public void getAttributeAsDateNullNameTest() throws SAXException, IOException
    {
        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        Node entityNode = entityDoc.getElementsByTagName("entity").item(0);

        assertNull(XmlHelper.getAttributeAsDate(entityNode, null));

    }

    @Test
    public void getAttributeAsDateEmptyNameTest() throws SAXException, IOException
    {
        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        Node entityNode = entityDoc.getElementsByTagName("entity").item(0);

        assertNull(XmlHelper.getAttributeAsDate(entityNode, ""));

    }

    @Test
    public void getAttributeAsDateNotDateTest() throws SAXException, IOException
    {
        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        Node entityNode = entityDoc.getElementsByTagName("entity").item(0);

        assertNull(XmlHelper.getAttributeAsDate(entityNode, "name"));

    }

    @Test
    public void setAttributeTest() throws SAXException, IOException
    {
        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        Node entityNode = entityDoc.getElementsByTagName("entity").item(0);

        assertEquals("TREXMission", XmlHelper.getAttribute(entityNode, "name"));

        XmlHelper.setAttribute(entityDoc, entityNode, "name", "TestingChanged");

        assertEquals("TestingChanged", XmlHelper.getAttribute(entityNode, "name"));

    }

    @Test
    public void setAttributeDoesNotExistTest() throws SAXException, IOException
    {
        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        Node entityNode = entityDoc.getElementsByTagName("entity").item(0);

        assertEquals("", XmlHelper.getAttribute(entityNode, "xyz"));

        XmlHelper.setAttribute(entityDoc, entityNode, "xyz", "TestingValue");

        assertEquals("TestingValue", XmlHelper.getAttribute(entityNode, "xyz"));

    }

    @Test
    public void setAttributeNullDocTest() throws SAXException, IOException
    {
        thrown.expect(NullArgumentException.class);
        thrown.expectMessage("doc");

        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        Node entityNode = entityDoc.getElementsByTagName("entity").item(0);

        assertEquals("", XmlHelper.getAttribute(entityNode, "xyz"));

        XmlHelper.setAttribute(null, entityNode, "xyz", "TestingValue");

    }

    @Test
    public void setAttributeNullNodeTest() throws SAXException, IOException
    {
        thrown.expect(NullArgumentException.class);
        thrown.expectMessage("xmlNode");

        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        XmlHelper.setAttribute(entityDoc, null, "xyz", "TestingValue");

    }

    @Test
    public void setAttributeNullNameTest() throws SAXException, IOException
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("name cannot be null or empty");

        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        Node entityNode = entityDoc.getElementsByTagName("entity").item(0);

        XmlHelper.setAttribute(entityDoc, entityNode, null, "TestingValue");

    }

    @Test
    public void setAttributeEmptyNameTest() throws SAXException, IOException
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("name cannot be null or empty");

        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        Node entityNode = entityDoc.getElementsByTagName("entity").item(0);

        XmlHelper.setAttribute(entityDoc, entityNode, "", "TestingValue");

    }

    @Test
    public void setAttributeWhitespaceNameTest() throws SAXException, IOException
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("name cannot be null or empty");

        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        Node entityNode = entityDoc.getElementsByTagName("entity").item(0);

        XmlHelper.setAttribute(entityDoc, entityNode, "   ", "TestingValue");

    }

    @Test
    public void setAttributeNullValueTest() throws SAXException, IOException
    {
        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        Node entityNode = entityDoc.getElementsByTagName("entity").item(0);

        XmlHelper.setAttribute(entityDoc, entityNode, "name", (String) null);

        assertEquals("", XmlHelper.getAttribute(entityNode, "name"));

    }

    @Test
    public void setAttributeEmptyValueTest() throws SAXException, IOException
    {
        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        Node entityNode = entityDoc.getElementsByTagName("entity").item(0);

        XmlHelper.setAttribute(entityDoc, entityNode, "name", "");

        assertEquals("", XmlHelper.getAttribute(entityNode, "name"));

    }

    @Test
    public void setAttributeAsDateTest() throws SAXException, IOException
    {
        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        Node entityNode = entityDoc.getElementsByTagName("entity").item(0);

        assertEquals(JodaDateTimeHelper.fromXmlDateTimeUTC("2014-05-20T16:17:13.2293139Z"),
                     XmlHelper.getAttributeAsDate(entityNode, "lastmodified"));

        DateTime now = JodaDateTimeHelper.nowInUtc();

        XmlHelper.setAttribute(entityDoc, entityNode, "lastmodified", now);

        assertEquals(now, XmlHelper.getAttributeAsDate(entityNode, "lastmodified"));

    }

    @Test
    public void setAttributeAsDateDoesNotExistTest() throws SAXException, IOException
    {
        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        Node entityNode = entityDoc.getElementsByTagName("entity").item(0);

        assertNull("Undefined attribute shouldn't have a value", XmlHelper.getAttributeAsDate(entityNode, "xyz"));

        DateTime now = JodaDateTimeHelper.nowInUtc();

        XmlHelper.setAttribute(entityDoc, entityNode, "xyz", now);

        assertEquals(now, XmlHelper.getAttributeAsDate(entityNode, "xyz"));

    }

    @Test
    public void setAttributeAsDateNullDocTest() throws SAXException, IOException
    {
        thrown.expect(NullArgumentException.class);
        thrown.expectMessage("doc");

        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        Node entityNode = entityDoc.getElementsByTagName("entity").item(0);

        DateTime now = JodaDateTimeHelper.nowInUtc();

        XmlHelper.setAttribute(null, entityNode, "xyz", now);

    }

    @Test
    public void setAttributeAsDateNullNodeTest() throws SAXException, IOException
    {
        thrown.expect(NullArgumentException.class);
        thrown.expectMessage("xmlNode");

        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        DateTime now = JodaDateTimeHelper.nowInUtc();

        XmlHelper.setAttribute(entityDoc, null, "xyz", now);

    }

    @Test
    public void setAttributeAsDateNullNameTest() throws SAXException, IOException
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("name cannot be null or empty");

        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        Node entityNode = entityDoc.getElementsByTagName("entity").item(0);

        DateTime now = JodaDateTimeHelper.nowInUtc();

        XmlHelper.setAttribute(entityDoc, entityNode, null, now);

    }

    @Test
    public void setAttributeAsDateEmptyNameTest() throws SAXException, IOException
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("name cannot be null or empty");

        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        Node entityNode = entityDoc.getElementsByTagName("entity").item(0);

        DateTime now = JodaDateTimeHelper.nowInUtc();

        XmlHelper.setAttribute(entityDoc, entityNode, "", now);

    }

    @Test
    public void setAttributeAsDateWhitespaceNameTest() throws SAXException, IOException
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("name cannot be null or empty");

        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        Node entityNode = entityDoc.getElementsByTagName("entity").item(0);

        DateTime now = JodaDateTimeHelper.nowInUtc();

        XmlHelper.setAttribute(entityDoc, entityNode, "   ", now);

    }

    @Test
    public void setAttributeAsDateNullValueTest() throws SAXException, IOException
    {
        Document entityDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        Node entityNode = entityDoc.getElementsByTagName("entity").item(0);

        XmlHelper.setAttribute(entityDoc, entityNode, "lastmodified", (DateTime) null);

        assertNull("name attribute should be null", XmlHelper.getAttributeAsDate(entityNode, "lastmodified"));

    }

    @Test
    public void formatXmlDocumentTest() throws SAXException, IOException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);
        String entityXml = entity.toXml();

        Document doc = XmlHelper.loadXmlFrom(entityXml);

        String docXml = XmlHelper.formatXml(doc);

        CoalesceEntity docEntity = CoalesceEntity.create(docXml);

        String docEntityXml = docEntity.toXml();

        CoalesceAssert.assertXmlEquals(entityXml, docEntityXml);

    }

    @Test
    public void formatXmlDocumentNullDocTest()
    {
        String docXml = XmlHelper.formatXml((Document) null);

        assertNull(docXml);
    }

    @Test
    public void formatXmlNodeNullNodeTest()
    {
        String nodeXml = XmlHelper.formatXml((Node) null);

        assertNull(nodeXml);
    }

    @Test
    public void loadXmlFromNullXmlTest() throws SAXException, IOException
    {
        thrown.expect(NullArgumentException.class);
        thrown.expectMessage("xml");

        XmlHelper.loadXmlFrom((String) null);
    }

    @Test
    public void loadXmlFromEmptyXmlTest() throws SAXException, IOException
    {
        thrown.expect(SAXException.class);
        thrown.expectMessage("Premature end of file.");

        assertNull(XmlHelper.loadXmlFrom(""));

    }

    @Test
    public void loadXmlFromInvalidXmlTest() throws SAXException, IOException
    {
        thrown.expect(SAXException.class);
        thrown.expectMessage("XML document structures must start and end within the same entity.");

        XmlHelper.loadXmlFrom("<entity >");
    }

    @Test
    public void loadXmlFromISNullStreamTest() throws SAXException, IOException
    {
        thrown.expect(NullArgumentException.class);
        thrown.expectMessage("is");

        XmlHelper.loadXmlFrom((InputStream) null);
    }

}
