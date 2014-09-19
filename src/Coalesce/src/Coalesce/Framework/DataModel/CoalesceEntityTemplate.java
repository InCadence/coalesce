package Coalesce.Framework.DataModel;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import Coalesce.Common.Helpers.XmlHelper;

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

public class CoalesceEntityTemplate {

    // -----------------------------------------------------------------------//
    // Protected Member Variables
    // -----------------------------------------------------------------------//

    private Document _dataObjectDocument;
    private Node _entityNode;

    // -----------------------------------------------------------------------//
    // Static Create Functions
    // -----------------------------------------------------------------------//

    public static CoalesceEntityTemplate create(XsdEntity entity) throws SAXException, IOException
    {
        return CoalesceEntityTemplate.create(entity.toXml());
    }

    public static CoalesceEntityTemplate create(String templateXml) throws SAXException, IOException
    {
        return CoalesceEntityTemplate.create(XmlHelper.loadXMLFrom(templateXml));
    }

    public static CoalesceEntityTemplate create(Document doc) throws SAXException, IOException
    {
        // Create a new CoalesceEntityTemplate
        CoalesceEntityTemplate entTemp = new CoalesceEntityTemplate();

        // Initialize
        if (!entTemp.initialize(doc)) return null;

        // return
        return entTemp;
    }

    // -----------------------------------------------------------------------//
    // Initialization
    // -----------------------------------------------------------------------//

    public boolean initialize(XsdEntity entity) throws SAXException, IOException
    {
        return initialize(entity.toXml());
    }

    public boolean initialize(String EntityTemplateXml) throws SAXException, IOException
    {
        return initialize(XmlHelper.loadXMLFrom(EntityTemplateXml));
    }

    public boolean initialize(Document doc)
    {

        // Clean up XML
        removeNodes(doc, "record");
        removeNodes(doc, "linkage");
        removeAttributes(doc);

        // Set DataObjectDocument
        _dataObjectDocument = doc;
        _entityNode = doc.getElementsByTagName("entity").item(0);

        // return Success
        return true;
    }

    // -----------------------------------------------------------------------//
    // public Read-Only Properties
    // -----------------------------------------------------------------------//

    public Document getDataObjectDocument()
    {
        return _dataObjectDocument;
    }

    public Node getEntityNode()
    {
        return _entityNode;
    }

    public String getName()
    {
        return XmlHelper.GetAttribute(getEntityNode(), "name");
    }

    public String getSource()
    {
        return XmlHelper.GetAttribute(getEntityNode(), "source");
    }

    public String getVersion()
    {
        return XmlHelper.GetAttribute(getEntityNode(), "version");
    }

    // -----------------------------------------------------------------------//
    // public Functions
    // -----------------------------------------------------------------------//

    public XsdEntity createNewEntity()
    {
        XsdEntity entity = new XsdEntity();
        entity.initialize(toXml());

        return entity;
    }

    public String toXml()
    {
        return XmlHelper.FormatXml(_dataObjectDocument);
    }

    public String toXml(Boolean setSQLServer)
    {
        if (setSQLServer)
        {
            return XmlHelper.FormatXml(_dataObjectDocument).replace("UTF-8", "UTF-16");
        }
        else
        {
            return XmlHelper.FormatXml(_dataObjectDocument);
        }
    }

    /*--------------------------------------------------------------------------
    Private Functions
    --------------------------------------------------------------------------*/

    private void removeNodes(Document doc, String nodeName)
    {

        NodeList nodeList = doc.getElementsByTagName(nodeName);

        // Remove all Linkages
        for (int i = nodeList.getLength() - 1; i >= 0; i--)
        {
            Node child = nodeList.item(i);

            while (child.hasChildNodes())
            {
                child.removeChild(child.getFirstChild());
            }

            child.getParentNode().removeChild(child);
        }

    }

    private void removeAttributes(Document doc)
    {

        NodeList nodeList = doc.getElementsByTagName("*");

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
                        attribute.setNodeValue("");
                    }
                }
            }
        }

    }
}
