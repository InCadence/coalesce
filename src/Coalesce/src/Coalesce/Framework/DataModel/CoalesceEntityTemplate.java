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

    private Document _DataObjectDocument;
    private Node _EntityNode;

    // -----------------------------------------------------------------------//
    // Static Create Functions
    // -----------------------------------------------------------------------//

    public static CoalesceEntityTemplate Create(XsdEntity entity) throws SAXException, IOException
    {
        return CoalesceEntityTemplate.Create(entity.ToXml());
    }

    public static CoalesceEntityTemplate Create(String templateXml) throws SAXException, IOException
    {
        return CoalesceEntityTemplate.Create(XmlHelper.loadXMLFrom(templateXml));
    }

    public static CoalesceEntityTemplate Create(Document doc) throws SAXException, IOException
    {
        // Create a new CoalesceEntityTemplate
        CoalesceEntityTemplate EntTemp = new CoalesceEntityTemplate();

        // Initialize
        if (!EntTemp.Initialize(doc)) return null;

        // return
        return EntTemp;
    }

    // -----------------------------------------------------------------------//
    // Initialization
    // -----------------------------------------------------------------------//

    public boolean Initialize(XsdEntity entity) throws SAXException, IOException
    {
        return this.Initialize(entity.ToXml());
    }

    public boolean Initialize(String EntityTemplateXml) throws SAXException, IOException
    {
        return this.Initialize(XmlHelper.loadXMLFrom(EntityTemplateXml));
    }

    public boolean Initialize(Document doc)
    {

        // Clean up XML
        this.RemoveNodes(doc, "record");
        this.RemoveNodes(doc, "linkage");
        this.RemoveAttributes(doc);

        // Set DataObjectDocument
        this._DataObjectDocument = doc;
        this._EntityNode = doc.getElementsByTagName("entity").item(0);

        // return Success
        return true;
    }

    // -----------------------------------------------------------------------//
    // public Read-Only Properties
    // -----------------------------------------------------------------------//

    public Document GetDataObjectDocument()
    {
        return this._DataObjectDocument;
    }

    public Node GetEntityNode()
    {
        return this._EntityNode;
    }

    public String GetName()
    {
        return XmlHelper.GetAttribute(this.GetEntityNode(), "name");
    }

    public String GetSource()
    {
        return XmlHelper.GetAttribute(this.GetEntityNode(), "source");
    }

    public String GetVersion()
    {
        return XmlHelper.GetAttribute(this.GetEntityNode(), "version");
    }

    // -----------------------------------------------------------------------//
    // public Functions
    // -----------------------------------------------------------------------//

    public XsdEntity CreateNewEntity()
    {
        XsdEntity Entity = new XsdEntity();
        Entity.Initialize(this.toXml());

        return Entity;
    }

    public String toXml()
    {
        return XmlHelper.FormatXml(this._DataObjectDocument);
    }

    /*--------------------------------------------------------------------------
    Private Functions
    --------------------------------------------------------------------------*/

    private void RemoveNodes(Document doc, String nodeName)
    {

        NodeList nodeList = doc.getElementsByTagName(nodeName);

        // Remove all Linkages
        for (int i = nodeList.getLength() - 1; i >= 0; i--)
        {
            Node Child = nodeList.item(i);

            while (Child.hasChildNodes())
            {
                Child.removeChild(Child.getFirstChild());
            }

            Child.getParentNode().removeChild(Child);
        }

    }

    private void RemoveAttributes(Document doc)
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
