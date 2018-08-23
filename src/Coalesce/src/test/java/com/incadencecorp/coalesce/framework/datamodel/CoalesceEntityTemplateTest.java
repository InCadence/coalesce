package com.incadencecorp.coalesce.framework.datamodel;

import com.incadencecorp.coalesce.common.CoalesceTypeInstances;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.*;

import static org.junit.Assert.*;

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

public class CoalesceEntityTemplateTest {

    @Test
    public void testTemplateFromEntity() throws Exception
    {
        // Test Entity
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        // Run Test
        CoalesceEntityTemplateTest.testTemplate(CoalesceEntityTemplate.create(entity));
    }

    @Test
    public void testTemplateFromString() throws Exception
    {
        // Run Test
        CoalesceEntityTemplateTest.testTemplate(CoalesceEntityTemplate.create(CoalesceTypeInstances.TEST_MISSION));
    }

    @Test
    public void testTemplateFromDocument() throws Exception
    {
        // Load Document
        Document xmlDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        // Run Test
        CoalesceEntityTemplateTest.testTemplate(CoalesceEntityTemplate.create(xmlDoc));
    }

    public static void testTemplate(CoalesceEntityTemplate template)
    {
        String templateXml = template.toXml();
        // System.out.print(templateXml);

        // Confirm Template
        assertNotNull(templateXml);
        assertTrue(template.getName().equalsIgnoreCase("trexmission"));
        assertTrue(template.getSource().equalsIgnoreCase("trex portal"));
        assertTrue(template.getVersion().equalsIgnoreCase("1.0.0.0"));

        // Confirm Values
        NodeList nodeList = template.getCoalesceObjectDocument().getElementsByTagName("*");

        for (int jj = 0; jj < nodeList.getLength(); jj++)
        {
            Node node = nodeList.item(jj);

            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                NamedNodeMap attributeList = node.getAttributes();

                for (int ii = 0; ii < attributeList.getLength(); ii++)
                {

                    Node attribute = attributeList.item(ii);

                    if (CoalesceEntityTemplate.excludeAttribute(node.getNodeName(), attribute.getNodeName()))
                    {
                        assertTrue(attribute.getNodeName(), StringHelper.isNullOrEmpty(attribute.getNodeValue()));
                    }
                    else
                    {
                        assertNotNull(attribute.getNodeValue());
                    }
                }
            }
        }

        // Create Entity from Template
        CoalesceEntity entity2 = template.createNewEntity();

        String entityXml = entity2.toXml();
        // System.out.print(entityXml);

        // Confirm Entity
        assertNotNull(entityXml);
        assertNotNull(entity2.getKey());
        assertTrue(entity2.getName().equalsIgnoreCase("trexmission"));
        assertTrue(entity2.getSource().equalsIgnoreCase("trex portal"));
        assertTrue(entity2.getVersion().equalsIgnoreCase("1.0.0.0"));
    }

    /**
     * Test to verify the template compareTo method works correctly.
     */
    @Test
    public void compareTemplateTest() throws Exception
    {

        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entity);

        assertEquals(0, template.compareTo(template));

    }

    /**
     * Test to verify the template compareTo method correctly returns non-zero
     * when templates do not match.
     */
    @Test
    public void compareTemplateFailureTest() throws Exception
    {

        CoalesceEntity entity1 = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        CoalesceEntityTemplate template1 = CoalesceEntityTemplate.create(entity1);

        CoalesceEntity entity2 = new CoalesceEntity();
        entity2.initialize();
        CoalesceSection.create(entity2, "unit test");

        CoalesceEntityTemplate template2 = CoalesceEntityTemplate.create(entity2);

        assertEquals(1, template1.compareTo(template2));

    }

    /**
     * This test ensures that templates when created don't contain timestamps or keys on the field definitions.
     */
    @Test
    public void test() throws Exception
    {
        CoalesceEntity entity = new TestEntity();
        entity.initialize();

        // Create Initial Template
        CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entity);

        NodeList nodes = template.getEntityNode().getElementsByTagName(Fielddefinition.class.getSimpleName());

        for (int ii = 0; ii < nodes.getLength(); ii++)
        {
            Assert.assertFalse(((Element) nodes.item(ii)).hasAttribute(CoalesceObject.ATTRIBUTE_KEY));
            Assert.assertFalse(((Element) nodes.item(ii)).hasAttribute(CoalesceObject.ATTRIBUTE_DATECREATED));
            Assert.assertFalse(((Element) nodes.item(ii)).hasAttribute(CoalesceObject.ATTRIBUTE_LASTMODIFIED));
        }

        Element root = template.getEntityNode();

        Assert.assertTrue(root.hasAttribute(CoalesceObject.ATTRIBUTE_KEY));
        Assert.assertTrue(root.hasAttribute(CoalesceEntity.ATTRIBUTE_NAME));
        Assert.assertTrue(root.hasAttribute(CoalesceEntity.ATTRIBUTE_SOURCE));
        Assert.assertTrue(root.hasAttribute(CoalesceEntity.ATTRIBUTE_VERSION));
        Assert.assertTrue(root.hasAttribute(CoalesceObject.ATTRIBUTE_LASTMODIFIED));

    }

}
