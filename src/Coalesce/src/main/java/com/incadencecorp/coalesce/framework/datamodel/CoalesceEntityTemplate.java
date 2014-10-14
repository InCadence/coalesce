package com.incadencecorp.coalesce.framework.datamodel;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.incadencecorp.coalesce.common.helpers.XmlHelper;

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
     * Creates a {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate} based off of an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}.
     * 
     * @param entity {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} that will be used to create the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate}
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate} created from the
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}
     * 
     * @throws SAXException
     * @throws IOException
     */
    public static CoalesceEntityTemplate create(CoalesceEntity entity) throws SAXException, IOException
    {
        return CoalesceEntityTemplate.create(entity.toXml());
    }

    /**
     * Creates a {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate} based off of an (XML) String.
     * 
     * @param templateXml String - xml string from/of an
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate} created from the xml string
     * 
     * @throws SAXException
     * @throws IOException
     */
    public static CoalesceEntityTemplate create(String templateXml) throws SAXException, IOException
    {
        return CoalesceEntityTemplate.create(XmlHelper.loadXmlFrom(templateXml));
    }

    /**
     * Creates a {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate} based off of an entity's
     * org.w3c.dom Document.
     * 
     * @param doc org.w3c.dom Document that will be used to create the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate}
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate} created from the
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} entity's Document
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
     * Initializes a previously new {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate} based off
     * of an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}.
     * 
     * @param entity {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} that will be used to initialize
     *            the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate}
     * @return boolean indicator of success/failure
     * 
     * @throws SAXException
     * @throws IOException
     */
    public boolean initialize(CoalesceEntity entity) throws SAXException, IOException
    {
        return initialize(entity.toXml());
    }

    /**
     * Initializes a previously new {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate} based off
     * of a (XML) String.
     * 
     * @param entityTemplateXml (XML) String from/of an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}
     * @return boolean indicator of success/failure
     * 
     * @throws SAXException
     * @throws IOException
     */
    public boolean initialize(String entityTemplateXml) throws SAXException, IOException
    {
        return initialize(XmlHelper.loadXmlFrom(entityTemplateXml));
    }

    /**
     * Initializes a previously new {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate} based off
     * of an org.w3c.dom Document.
     * 
     * @param doc org.w3c.dom Document from/of an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}
     * @return boolean indicator of success/failure
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
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate}'s (org.w3c.dom Document)
     * DataObjectDocument.
     * 
     * @return Document representing the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate}'s
     *         entity
     */
    public Document getDataObjectDocument()
    {
        return _dataObjectDocument;
    }

    /**
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate}'s (org.w3c.dom Node)
     * EntityNode.
     * 
     * @return Node representing the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate}'s entity
     */
    public Node getEntityNode()
    {
        return _entityNode;
    }

    /**
     * Returns the value of the (EntityNode) Node's name attribute.
     * 
     * @return String the Node's name attribute
     */
    public String getName()
    {
        return XmlHelper.getAttribute(getEntityNode(), "name");
    }

    /**
     * Returns the value of the (EntityNode) Node's source attribute.
     * 
     * @return String the Node's source attribute
     */
    public String getSource()
    {
        return XmlHelper.getAttribute(getEntityNode(), "source");
    }

    /**
     * Returns the value of the (EntityNode) Node's version attribute.
     * 
     * @return String the Node's version attribute
     */
    public String getVersion()
    {
        return XmlHelper.getAttribute(getEntityNode(), "version");
    }

    // -----------------------------------------------------------------------//
    // public Functions
    // -----------------------------------------------------------------------//

    /**
     * Creates and initializes a new {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} based off of this
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate}'s XML String.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} of the new entity created from this
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate}
     */
    public CoalesceEntity createNewEntity()
    {
        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize(toXml());

        return entity;
    }

    /**
     * Returns the UTF-8 (XML) String of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate}.
     * 
     * @return String (XML) of this {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate}
     */
    public String toXml()
    {
        return XmlHelper.formatXml(_dataObjectDocument);
    }

    /**
     * Returns the UTF-16 (XML) String of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate}
     * if the setSQLServer string parameter = "true". If setSQLServer parameter = "false", returns the UTF-8 (XML) String of
     * the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate}.
     * 
     * @param setSQLServer String, if "true" indicates desired return is UTF-16 (XML) String; otherwise, UTF-8 (XML) String
     *            will result
     * @return (XML) String UTF-16 (XML) String or UTF-8 (XML) String depending on parameter
     */
    public String toXml(boolean setSQLServer)
    {
        if (setSQLServer)
            return toXml(true).replace("UTF-8", "UTF-16");
        else
            return toXml();
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
