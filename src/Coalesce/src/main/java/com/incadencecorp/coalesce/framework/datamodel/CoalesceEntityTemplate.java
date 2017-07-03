package com.incadencecorp.coalesce.framework.datamodel;

import java.io.IOException;
import java.util.UUID;

import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.incadencecorp.coalesce.common.classification.helpers.StringHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
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

/**
 * Coalesce Templates are used for registering data objects with the system and
 * creating new data objects.
 * 
 * @author n78554
 *
 */
public class CoalesceEntityTemplate implements Comparable<CoalesceEntityTemplate> {

    // -----------------------------------------------------------------------//
    // Protected Member Variables
    // -----------------------------------------------------------------------//

    private Document _document;
    private Node _entityNode;

    // -----------------------------------------------------------------------//
    // Static Create Functions
    // -----------------------------------------------------------------------//

    /**
     * Creates a {@link CoalesceEntityTemplate} based off of an
     * {@link CoalesceEntity}.
     * 
     * @param entity {@link CoalesceEntity} that will be used to create the
     *            {@link CoalesceEntityTemplate}
     * @return {@link CoalesceEntityTemplate} created from the
     *         {@link CoalesceEntity}
     * 
     * @throws SAXException
     * @throws IOException
     */
    public static CoalesceEntityTemplate create(CoalesceEntity entity) throws SAXException, IOException
    {
        return CoalesceEntityTemplate.create(entity.toXml());
    }

    /**
     * Creates a {@link CoalesceEntityTemplate} based off of an (XML) String.
     * 
     * @param templateXml String - xml string from/of an {@link CoalesceEntity}
     * @return {@link CoalesceEntityTemplate} created from the xml string
     * 
     * @throws SAXException
     * @throws IOException
     */
    public static CoalesceEntityTemplate create(String templateXml) throws SAXException, IOException
    {
        return CoalesceEntityTemplate.create(XmlHelper.loadXmlFrom(templateXml));
    }

    /**
     * Creates a {@link CoalesceEntityTemplate} based off of an entity's
     * org.w3c.dom Document.
     * 
     * @param doc org.w3c.dom Document that will be used to create the
     *            {@link CoalesceEntityTemplate}
     * @return {@link CoalesceEntityTemplate} created from the
     *         {@link CoalesceEntity} entity's Document
     * 
     * @throws SAXException
     * @throws IOException
     */
    public static CoalesceEntityTemplate create(Document doc) throws SAXException, IOException
    {
        // Create a new CoalesceEntityTemplate
        CoalesceEntityTemplate entTemp = new CoalesceEntityTemplate();

        // Initialize
        if (!entTemp.initialize(doc))
            return null;

        // return
        return entTemp;
    }

    // -----------------------------------------------------------------------//
    // Initialization
    // -----------------------------------------------------------------------//

    /**
     * Initializes a previously new {@link CoalesceEntityTemplate} based off of
     * an {@link CoalesceEntity}.
     * 
     * @param entity {@link CoalesceEntity} that will be used to initialize the
     *            {@link CoalesceEntityTemplate}
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
     * Initializes a previously new {@link CoalesceEntityTemplate} based off of
     * a (XML) String.
     * 
     * @param entityTemplateXml (XML) String from/of an {@link CoalesceEntity}
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
     * Initializes a previously new {@link CoalesceEntityTemplate} based off of
     * an org.w3c.dom Document.
     * 
     * @param doc org.w3c.dom Document from/of an {@link CoalesceEntity}
     * @return boolean indicator of success/failure
     */
    public boolean initialize(Document doc)
    {

        // Clean up XML
        removeNodes(doc, "record");
        removeNodes(doc, "linkage");
        removeAttributes(doc);

        // Set Document
        _document = doc;
        _entityNode = doc.getElementsByTagName("entity").item(0);

        // return Success
        return true;
    }

    // -----------------------------------------------------------------------//
    // public Read-Only Properties
    // -----------------------------------------------------------------------//

    /**
     * Returns the {@link CoalesceEntityTemplate} 's (org.w3c.dom Document)
     * Document.
     * 
     * @return Document representing the {@link CoalesceEntityTemplate} 's
     *         entity
     */
    public Document getCoalesceObjectDocument()
    {
        return _document;
    }

    /**
     * Returns the {@link CoalesceEntityTemplate} 's (org.w3c.dom Node)
     * EntityNode.
     * 
     * @return Node representing the {@link CoalesceEntityTemplate} 's entity
     */
    public Node getEntityNode()
    {
        return _entityNode;
    }

    /**
     * Sets the value of the (EntityNode) Node's key attribute.
     */
    public void setKey(String key)
    {
        XmlHelper.setAttribute(getCoalesceObjectDocument(), getEntityNode(), CoalesceEntity.ATTRIBUTE_KEY, key);
    }

    /**
     * Returns the value of the (EntityNode) Node's key attribute. By default
     * this is based on a MD5 hash of the name, source, and version.
     * 
     * @return String the Node's name attribute
     */
    public String getKey()
    {
        String result = XmlHelper.getAttribute(getEntityNode(), CoalesceEntity.ATTRIBUTE_KEY);

        if (StringHelper.isNullOrEmpty(result))
        {
            result = UUID.nameUUIDFromBytes((getName() + getSource() + getVersion()).getBytes()).toString();
            setKey(result);

        }

        return result;
    }

    /**
     * Returns the value of the (EntityNode) Node's name attribute.
     * 
     * @return String the Node's name attribute
     */
    public String getName()
    {
        return XmlHelper.getAttribute(getEntityNode(), CoalesceEntity.ATTRIBUTE_NAME);
    }

    /**
     * Returns the value of the (EntityNode) Node's source attribute.
     * 
     * @return String the Node's source attribute
     */
    public String getSource()
    {
        return XmlHelper.getAttribute(getEntityNode(), CoalesceEntity.ATTRIBUTE_SOURCE);
    }

    /**
     * Returns the value of the (EntityNode) Node's version attribute.
     * 
     * @return String the Node's version attribute
     */
    public String getVersion()
    {
        return XmlHelper.getAttribute(getEntityNode(), CoalesceEntity.ATTRIBUTE_VERSION);
    }

    /**
     * Returns the value of the DateCreated attribute.
     * 
     * @return DateTime of the
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     *         's DateCreated attribute.
     */
    public DateTime getDateCreated()
    {
        return JodaDateTimeHelper.fromXmlDateTimeUTC(XmlHelper.getAttribute(getEntityNode(),
                                                                            CoalesceEntity.ATTRIBUTE_DATECREATED));
    }

    public DateTime getLastModified()
    {
        return JodaDateTimeHelper.fromXmlDateTimeUTC(XmlHelper.getAttribute(getEntityNode(),
                                                                            CoalesceEntity.ATTRIBUTE_LASTMODIFIED));
    }

    /**
     * @return the fully qualified class name that created this template.
     */
    public String getClassName()
    {
        return XmlHelper.getAttribute(getEntityNode(), CoalesceEntity.ATTRIBUTE_CLASSNAME);
    }

    // -----------------------------------------------------------------------//
    // public Functions
    // -----------------------------------------------------------------------//

    /**
     * Creates and initializes a new {@link CoalesceEntity} based off of this
     * {@link CoalesceEntityTemplate} 's XML String.
     * 
     * @return {@link CoalesceEntity} of the new entity created from this
     *         {@link CoalesceEntityTemplate}
     */
    public CoalesceEntity createNewEntity()
    {
        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize(toXml());

        return entity;
    }

    /**
     * Returns the UTF-8 (XML) String of the {@link CoalesceEntityTemplate} .
     * 
     * @return String (XML) in UTF-8 of this {@link CoalesceEntityTemplate}
     */
    public String toXml()
    {
        return XmlHelper.formatXml(_document);
    }

    /**
     * Returns the (XML) String in the desired encoding of the
     * {@link CoalesceEntityTemplate} .
     * 
     * @param encoding the desired encoding.
     * @return (XML) String in the desired encoding.
     */
    public String toXml(String encoding)
    {
        return XmlHelper.formatXml(_document, encoding);
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

            if (node.getNodeType() == Node.ELEMENT_NODE && !node.getNodeName().equalsIgnoreCase("constraint"))
            {
                NamedNodeMap attributeList = node.getAttributes();

                for (int ii = 0; ii < attributeList.getLength(); ii++)
                {

                    Node attribute = attributeList.item(ii);
                    if (!attribute.getNodeName().equalsIgnoreCase("name")
                            && !attribute.getNodeName().equalsIgnoreCase("source")
                            && !attribute.getNodeName().equalsIgnoreCase("version")
                            && !attribute.getNodeName().equalsIgnoreCase("flatten")
                            && !attribute.getNodeName().equalsIgnoreCase("noindex")
                            && !attribute.getNodeName().equalsIgnoreCase("datatype")
                            && !attribute.getNodeName().equalsIgnoreCase("maxrecords")
                            && !attribute.getNodeName().equalsIgnoreCase("minrecords")
                            && !attribute.getNodeName().equalsIgnoreCase("classname"))
                    {
                        attribute.setNodeValue("");
                    }
                }
            }
        }

    }

    @Override
    public int compareTo(CoalesceEntityTemplate template)
    {

        int compared = 0;

        NodeList nodeList1 = template.getCoalesceObjectDocument().getElementsByTagName("*");
        NodeList nodeList2 = this.getCoalesceObjectDocument().getElementsByTagName("*");

        if (nodeList1.getLength() == nodeList2.getLength())
        {

            for (int jj = 0; jj < nodeList1.getLength(); jj++)
            {
                Node node1 = nodeList1.item(jj);
                Node node2 = nodeList2.item(jj);

                if (node1.getNodeType() == Node.ELEMENT_NODE)
                {
                    NamedNodeMap attributeList1 = node1.getAttributes();
                    NamedNodeMap attributeList2 = node2.getAttributes();

                    // Node contains same number of attributes.
                    if (attributeList1.getLength() == attributeList2.getLength())
                    {
                        // Yes; Verify attribute values are the same.
                        for (int ii = 0; ii < attributeList1.getLength(); ii++)
                        {

                            Node attribute1 = attributeList1.item(ii);
                            Node attribute2 = attributeList2.item(ii);

                            compared = attribute1.getNodeValue().compareToIgnoreCase(attribute2.getNodeValue());

                            // Attribute Equivalent
                            if (compared != 0)
                            {
                                // No; Exit For
                                break;
                            }

                        }
                    }
                    else
                    {
                        // No; Templates Differ
                        compared = 1;
                    }
                }

                // So Far Equivalent?
                if (compared != 0)
                {
                    // No; Exit For
                    break;
                }

            }

        }
        else
        {
            compared = 1;
        }

        return compared;
    }
}
