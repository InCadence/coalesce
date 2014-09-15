package Coalesce.Framework.DataModel;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import Coalesce.Common.Exceptions.CoalesceException;
import Coalesce.Common.Helpers.StringHelper;
import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Common.UnitTest.CoalesceTypeInstances;

public class CoalesceEntitySyncShellTest {

    @Test
    public void testCreateFromEntity() throws SAXException, IOException
    {
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        // Initialize
        CoalesceEntitySyncShell shell = CoalesceEntitySyncShell.Create(entity);

        String xml = shell.toXml();

        // Validate
        assertNotNull(xml);
        assertTrue(this.ValidateSyncShell(shell));

    }

    @Test
    public void testCreateFromString() throws SAXException, IOException
    {
        // Initialize
        CoalesceEntitySyncShell shell = CoalesceEntitySyncShell.Create(CoalesceTypeInstances.TEST_MISSION);

        // Validate
        assertTrue(this.ValidateSyncShell(shell));
    }

    @Test
    public void testCreateFromDocument() throws SAXException, IOException
    {
        // Load Document
        Document XmlDoc = XmlHelper.loadXMLFrom(CoalesceTypeInstances.TEST_MISSION);

        // Initialize
        CoalesceEntitySyncShell shell = CoalesceEntitySyncShell.Create(XmlDoc);

        // Validate
        assertTrue(this.ValidateSyncShell(shell));

    }

    @Test
    public void testClone() throws SAXException, IOException
    {
        // Initialize
        CoalesceEntitySyncShell shell = CoalesceEntitySyncShell.Create(CoalesceTypeInstances.TEST_MISSION);

        // Initialize Clone
        CoalesceEntitySyncShell clone = new CoalesceEntitySyncShell();
        clone = CoalesceEntitySyncShell.Clone(shell);

        String xml1 = shell.toXml();
        String xml2 = clone.toXml();

        // Validate
        assertTrue(xml1.equals(xml2));
        assertNotEquals(shell.GetDataObjectDocument(), clone.GetDataObjectDocument());

    }

    @Test
    public void GetRequiredChangesSyncShell() throws CoalesceException, SAXException, IOException
    {
        // Load Document
        XsdEntity entity = XsdEntity.create(CoalesceTypeInstances.TEST_MISSION);

        // Create Local Shell
        CoalesceEntitySyncShell localShell = CoalesceEntitySyncShell.Create(entity);

        // Validate Local
        assertTrue(this.ValidateSyncShell(localShell));

        // Modify Entity
        XsdRecord record = (XsdRecord) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/");
        record.setFieldValue("MissionName", "test");

        String fieldKeyValid = record.getFieldByName("MissionName").getKey();
        String fieldKeyInValid = record.getFieldByName("MissionDescription").getKey();

        // Create Remote Shell
        CoalesceEntitySyncShell remoteShell = CoalesceEntitySyncShell.Create(entity);

        // Validate Remote
        assertTrue(this.ValidateSyncShell(remoteShell));

        // Create Change Shell
        CoalesceEntitySyncShell changesShell = CoalesceEntitySyncShell.GetRequiredChangesSyncShell(localShell, remoteShell);
        // Print XML
        String changesXml = changesShell.toXml();
        System.out.println(changesXml);

        // Validate Change
        boolean foundChange = false;

        NodeList nodeList = changesShell.GetDataObjectDocument().getElementsByTagName("*");

        for (int ii = 0; ii < nodeList.getLength(); ii++)
        {
            String nodeKey = XmlHelper.GetAttribute(nodeList.item(ii), "key");

            if (nodeKey.equalsIgnoreCase(fieldKeyValid))
            {
                foundChange = true;
            }
            else if (nodeKey.equalsIgnoreCase(fieldKeyInValid))
            {
                fail("Invalid Field");
            }
        }

        assertTrue(foundChange);

    }

    private boolean ValidateSyncShell(CoalesceEntitySyncShell shell)
    {
        // Validate
        NodeList nodeList = shell.GetDataObjectDocument().getElementsByTagName("*");

        for (int jj = 0; jj < nodeList.getLength(); jj++)
        {
            Node node = nodeList.item(jj);

            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                NamedNodeMap attributeList = node.getAttributes();

                for (int ii = 0; ii < attributeList.getLength(); ii++)
                {

                    Node attribute = attributeList.item(ii);

                    if (!attribute.getNodeName().equalsIgnoreCase("key")
                            && !attribute.getNodeName().equalsIgnoreCase("lastmodified"))
                    {
                        return false;
                    }
                    else
                    {
                        if (StringHelper.IsNullOrEmpty(attribute.getNodeValue())) return false;
                    }
                }
            }
        }

        return true;
    }
}
