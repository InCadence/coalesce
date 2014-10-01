package Coalesce.Framework.DataModel;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import Coalesce.Common.Helpers.StringHelper;
import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Common.UnitTest.CoalesceTypeInstances;

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
    public void testTemplateFromEntity() throws SAXException, IOException
    {
        // Test Entity
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        // Run Test
        CoalesceEntityTemplateTest.testTemplate(CoalesceEntityTemplate.create(entity));
    }

    @Test
    public void testTemplateFromString() throws SAXException, IOException
    {
        // Run Test
        CoalesceEntityTemplateTest.testTemplate(CoalesceEntityTemplate.create(CoalesceTypeInstances.TEST_MISSION));
    }

    @Test
    public void testTemplateFromDocument() throws SAXException, IOException
    {
        // Load Document
        Document XmlDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        // Run Test
        CoalesceEntityTemplateTest.testTemplate(CoalesceEntityTemplate.create(XmlDoc));
    }

    public static void testTemplate(CoalesceEntityTemplate template)
    {
        String templateXml = template.toXml();
        //System.out.print(templateXml);

        // Confirm Template
        assertNotNull(templateXml);
        assertTrue(template.getName().equalsIgnoreCase("trexmission"));
        assertTrue(template.getSource().equalsIgnoreCase("trex portal"));
        assertTrue(template.getVersion().equalsIgnoreCase("1.0.0.0"));

        // Confirm Values
        NodeList nodeList = template.getDataObjectDocument().getElementsByTagName("*");

        for (int jj = 0; jj < nodeList.getLength(); jj++)
        {
            Node node = nodeList.item(jj);

            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                NamedNodeMap attributeList = node.getAttributes();

                for (int ii = 0; ii < attributeList.getLength(); ii++)
                {

                    Node attribute = attributeList.item(ii);

                    if (!attribute.getNodeName().equalsIgnoreCase("name")
                            && !attribute.getNodeName().equalsIgnoreCase("source")
                            && !attribute.getNodeName().equalsIgnoreCase("version"))
                    {
                        assertTrue(StringHelper.isNullOrEmpty(attribute.getNodeValue()));
                    }
                    else
                    {
                        assertNotNull(attribute.getNodeValue());
                    }
                }
            }
        }

        // Create Entity from Template
        XsdEntity entity2 = template.createNewEntity();

        String entityXml = entity2.toXml();
        //System.out.print(entityXml);

        // Confirm Entity
        assertNotNull(entityXml);
        assertNotNull(entity2.getKey());
        assertTrue(entity2.getName().equalsIgnoreCase("trexmission"));
        assertTrue(entity2.getSource().equalsIgnoreCase("trex portal"));
        assertTrue(entity2.getVersion().equalsIgnoreCase("1.0.0.0"));
    }

}
