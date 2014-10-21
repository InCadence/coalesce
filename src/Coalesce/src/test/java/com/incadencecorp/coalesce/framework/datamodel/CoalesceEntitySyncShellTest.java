package com.incadencecorp.coalesce.framework.datamodel;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.incadencecorp.coalesce.common.CoalesceTypeInstances;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;

public class CoalesceEntitySyncShellTest {

    @Test
    public void testCreateFromEntity() throws SAXException, IOException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        // Initialize
        CoalesceEntitySyncShell shell = CoalesceEntitySyncShell.create(entity);

        // Validate
        assertNotNull(shell.toXml());
        assertTrue(CoalesceEntitySyncShellTest.ValidateSyncShell(shell));

    }

    @Test
    public void testInitializeFromEntity() throws SAXException, IOException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        // Initialize
        CoalesceEntitySyncShell shell = new CoalesceEntitySyncShell();
        shell.initialize(entity);

        // Validate
        assertNotNull(shell.toXml());
        assertNotNull(shell.getEntityNode());
        assertTrue(CoalesceEntitySyncShellTest.ValidateSyncShell(shell));

    }

    @Test
    public void testEntityNode() throws SAXException, IOException, ParserConfigurationException
    {
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        // Initialize
        CoalesceEntitySyncShell shell = CoalesceEntitySyncShell.create(entity);

        // Copy Shell Using Entity
        CoalesceEntitySyncShell shell2 = new CoalesceEntitySyncShell();
        shell2.setEntityNode(shell.getEntityNode());

        String shellXml = shell2.toXml();

        // Validate
        assertTrue(CoalesceEntitySyncShellTest.ValidateSyncShell(shell2));
        assertNotNull(shellXml);
    }

    @Test
    public void testCreateFromString() throws SAXException, IOException
    {
        // Initialize
        CoalesceEntitySyncShell shell = CoalesceEntitySyncShell.create(CoalesceTypeInstances.TEST_MISSION);

        // Validate
        assertTrue(CoalesceEntitySyncShellTest.ValidateSyncShell(shell));
    }

    @Test
    public void testCreateFromDocument() throws SAXException, IOException
    {
        // Load Document
        Document XmlDoc = XmlHelper.loadXmlFrom(CoalesceTypeInstances.TEST_MISSION);

        // Initialize
        CoalesceEntitySyncShell shell = CoalesceEntitySyncShell.create(XmlDoc);

        // Validate
        assertTrue(CoalesceEntitySyncShellTest.ValidateSyncShell(shell));

    }

    @Test
    public void testClone() throws SAXException, IOException
    {
        // Initialize
        CoalesceEntitySyncShell shell = CoalesceEntitySyncShell.create(CoalesceTypeInstances.TEST_MISSION);

        // Initialize Clone
        CoalesceEntitySyncShell clone = new CoalesceEntitySyncShell();
        clone = CoalesceEntitySyncShell.clone(shell);

        String xml1 = shell.toXml();
        String xml2 = clone.toXml();

        // Validate
        assertTrue(xml1.equals(xml2));
        assertNotEquals(shell.getDataObjectDocument(), clone.getDataObjectDocument());

    }

    @Test
    public void GetRequiredChangesSyncShell() throws CoalesceException, SAXException, IOException
    {
        // Load Document
        CoalesceEntity entity = CoalesceEntity.create(CoalesceTypeInstances.TEST_MISSION);

        // Create Local Shell
        CoalesceEntitySyncShell localShell = CoalesceEntitySyncShell.create(entity);

        // Validate Local
        assertTrue(CoalesceEntitySyncShellTest.ValidateSyncShell(localShell));

        // Modify Entity
        CoalesceRecord record = (CoalesceRecord) entity.getDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/");
        
        CoalesceStringField missionName = (CoalesceStringField) record.getFieldByName("MissionName");
        missionName.setValue("test");

        String fieldKeyValid = record.getFieldByName("MissionName").getKey();
        String fieldKeyInValid = record.getFieldByName("MissionDescription").getKey();

        // Create Remote Shell
        CoalesceEntitySyncShell remoteShell = CoalesceEntitySyncShell.create(entity);

        // Validate Remote
        assertTrue(CoalesceEntitySyncShellTest.ValidateSyncShell(remoteShell));

        // Create Change Shell
        CoalesceEntitySyncShell changesShell = CoalesceEntitySyncShell.getRequiredChangesSyncShell(localShell, remoteShell);

        // Validate Change
        boolean foundChange = false;

        NodeList nodeList = changesShell.getDataObjectDocument().getElementsByTagName("*");

        for (int ii = 0; ii < nodeList.getLength(); ii++)
        {
            String nodeKey = XmlHelper.getAttribute(nodeList.item(ii), "key");

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

    public static boolean ValidateSyncShell(CoalesceEntitySyncShell shell)
    {
        // Validate
        NodeList nodeList = shell.getDataObjectDocument().getElementsByTagName("*");

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
                        if (StringHelper.isNullOrEmpty(attribute.getNodeValue())) return false;
                    }
                }
            }
        }

        return true;
    }
}
