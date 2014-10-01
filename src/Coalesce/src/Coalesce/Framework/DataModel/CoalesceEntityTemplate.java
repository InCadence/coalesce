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

    /**
     * Creates a CoalesceEntityTemplate based off of an XsdEntity.
     * 
     * @param entity        XsdEntity
     * @return              CoalesceEntityTemplate
     * 
     * @throws SAXException
     * @throws IOException
     */
    public static CoalesceEntityTemplate create(XsdEntity entity) throws SAXException, IOException
    {
        return CoalesceEntityTemplate.create(entity.toXml());
    }

    /**
     * Creates a CoalesceEntityTemplate based off of an (XML) String.
     * 
     * @param templateXml       String
     * @return                  CoalesceEntityTemplate
     * 
     * @throws SAXException
     * @throws IOException
     */
    public static CoalesceEntityTemplate create(String templateXml) throws SAXException, IOException
    {
        return CoalesceEntityTemplate.create(XmlHelper.loadXMLFrom(templateXml));
    }

    /**
     * Creates a CoalesceEntityTemplate based off of an org.w3c.dom Document.
     * 
     * @param doc               org.w3c.dom Document
     * @return                  CoalesceEntityTemplate
     * 
     * @throws SAXException
     * @throws IOException
     */
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

    /**
     * Initializes a previously new CoalesceEntityTemplate based off of an XsdEntity.
     * 
     * @param entity            XsdEntity
     * @return                  boolean
     * 
     * @throws SAXException
     * @throws IOException
     */
    public boolean initialize(XsdEntity entity) throws SAXException, IOException
    {
        return initialize(entity.toXml());
    }

    /**
     * Initializes a previously new CoalesceEntityTemplate based off of a (XML) String.
     * 
     * @param EntityTemplateXml     (XML) String
     * @return                      boolean
     * 
     * @throws SAXException
     * @throws IOException
     */
    public boolean initialize(String EntityTemplateXml) throws SAXException, IOException
    {
        return initialize(XmlHelper.loadXMLFrom(EntityTemplateXml));
    }

    /**
     * Initializes a previously new CoalesceEntityTemplate based off of an org.w3c.dom Document.
     * 
     * @param doc   org.w3c.dom Document
     * @return      boolean
     */
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

    /**
     * Returns the CoalesceEntityTemplate's (org.w3c.dom Document) DataObjectDocument.
     * 
     * @return      Document
     */
    public Document getDataObjectDocument()
    {
        return _dataObjectDocument;
    }

    /**
     * Returns the CoalesceEntityTemplate's (org.w3c.dom Node) EntityNode.
     * 
     * @return      Node
     */
    public Node getEntityNode()
    {
        return _entityNode;
    }

    /**
     * Returns the value of the (EntityNode) Node's name attribute.
     * 
     * @return  String
     */
    public String getName()
    {
        return XmlHelper.getAttribute(getEntityNode(), "name");
    }

    /**
     * Returns the value of the (EntityNode) Node's source attribute.
     * 
     * @return String
     */
    public String getSource()
    {
        return XmlHelper.getAttribute(getEntityNode(), "source");
    }

    /**
     * Returns the value of the (EntityNode) Node's version attribute.
     * 
     * @return  String
     */
    public String getVersion()
    {
        return XmlHelper.getAttribute(getEntityNode(), "version");
    }

    // -----------------------------------------------------------------------//
    // public Functions
    // -----------------------------------------------------------------------//

    /**
     * Creates and initializes a new XsdEntity based off of this CoalesceEntityTemplate's XML String.
     * 
     * @return  XsdEntity
     */
    public XsdEntity createNewEntity()
    {
        XsdEntity entity = new XsdEntity();
        entity.initialize(toXml());

        return entity;
    }

    /**
     * Returns the UTF-8 (XML) String of the CoalesceEntityTemplate.
     * 
     * @return  String
     */
    public String toXml()
    {
        return XmlHelper.formatXml(_dataObjectDocument);
    }

    /**
     * Returns the UTF-16 (XML) String of the CoalesceEntityTemplate if the setSQLServer boolean parameter is true. 
     * If setSQLServer parameter is false, returns the UTF-8 (XML) String of the CoalesceEntityTemplate.
     * 
     * @param setSQLServer
     * @return  String
     */
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
