package Coalesce.Framework.DataModel;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import Coalesce.Common.Helpers.StringHelper;
import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Common.UnitTest.CoalesceTypeInstances;

public class CoalesceEntityTemplateTest {

    @Test
    public void testTemplateFromEntity()
    {

        try
        {
            // Test Entity
            XsdEntity entity = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

            // Run Test
            this.testTemplate(CoalesceEntityTemplate.Create(entity));
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testTemplateFromString()
    {
        try
        {
            // Run Test
            this.testTemplate(CoalesceEntityTemplate.Create(CoalesceTypeInstances.TESTMISSION));
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testTemplateFromDocument()
    {
        try
        {
            // Load Document
            Document XmlDoc = XmlHelper.loadXMLFrom(CoalesceTypeInstances.TESTMISSION);
            
            // Run Test
            this.testTemplate(CoalesceEntityTemplate.Create(XmlDoc));
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
    }

    private void testTemplate(CoalesceEntityTemplate template)
    {
        String templateXml = template.toXml();
        System.out.print(templateXml);

        // Confirm Template
        assertNotNull(templateXml);
        assertTrue(template.GetName().equalsIgnoreCase("trexmission"));
        assertTrue(template.GetSource().equalsIgnoreCase("trex portal"));
        assertTrue(template.GetVersion().equalsIgnoreCase("1.0.0.0"));

        // Confirm Values
        NodeList nodeList = template.GetDataObjectDocument().getElementsByTagName("*");

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
                        assertTrue(StringHelper.IsNullOrEmpty(attribute.getNodeValue()));
                    }
                    else
                    {
                        assertNotNull(attribute.getNodeValue());
                    }
                }
            }
        }

        // Create Entity from Template
        XsdEntity entity2 = template.CreateNewEntity();

        String entityXml = entity2.ToXml();
        System.out.print(entityXml);

        // Confirm Entity
        assertNotNull(entityXml);
        assertNotNull(entity2.GetKey());
        assertTrue(entity2.GetName().equalsIgnoreCase("trexmission"));
        assertTrue(entity2.GetSource().equalsIgnoreCase("trex portal"));
        assertTrue(entity2.GetVersion().equalsIgnoreCase("1.0.0.0"));
    }

}
