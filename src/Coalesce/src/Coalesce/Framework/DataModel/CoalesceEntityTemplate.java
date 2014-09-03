package Coalesce.Framework.DataModel;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import Coalesce.Common.Helpers.XmlHelper;

public class CoalesceEntityTemplate {

    // -----------------------------------------------------------------------//
    // Protected Member Variables
    // -----------------------------------------------------------------------//

    protected Document _DataObjectDocument;
    protected Node _EntityNode;
    XmlHelper _XmlHelper = new XmlHelper();

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    public CoalesceEntityTemplate Create(XsdEntity Entity)
    {
        try
        {
            // Create a new CoalesceEntityTemplate
            CoalesceEntityTemplate EntTemp = new CoalesceEntityTemplate();

            // Initialize
            if (! EntTemp.InitializeFromEntity(Entity)) return null;

            // return
            return EntTemp;

        }
        catch (Exception ex)
        {
            // return Failed Error
            return null;
        }
    }

    public boolean Initialize(String EntityTemplateXml)
    {
        try
        {
            //TODO: verify this (loadXMLFrom) is a replacement for LoadXml function
            // // Create DataObjectDocument
            Document XmlDoc = null;
            // XmlDoc.LoadXml(EntityTemplateXml);
            XmlDoc = XmlHelper.loadXMLFrom(EntityTemplateXml);

            // Call Peer.
            return Initialize(XmlDoc);

        }
        catch (Exception ex)
        {
            // return Failed Error
            return false;
        }
    }

    public boolean Initialize(Document EntityTemplateDataObjectDocument)
    {
        try
        {
            // Set DataObjectDocument
            this.SetDataObjectDocument(EntityTemplateDataObjectDocument);

            // return Success
            return true;

        }
        catch (Exception ex)
        {
            // return Failed Error
            return false;
        }
    }

    public boolean InitializeFromEntity(XsdEntity Entity)
    {
        try
        {
            // TODO: need make sure getElementsByTagName is a good replacement for vb's SelectNodes function
            // Create a Clone of the Entity's DataObjectDocument
            /*
            Document TemplateDoc = Entity.GetDataObjectDocument();

            // Clear all Key attributes
            // for (Node Child : TemplateDoc.SelectNodes("//@key")){
            for (int i = 0; i < TemplateDoc.getElementsByTagName("//@key").getLength(); i++)
            {
                Node Child = TemplateDoc.getElementsByTagName("//@key").item(i);
                Child.setNodeValue("");
            }

            // Clear EntityId attribute
            // for (Node Child : TemplateDoc.SelectNodes("//@entityid")){
            for (int i = 0; i < TemplateDoc.getElementsByTagName("//@entityid").getLength(); i++)
            {
                Node Child = TemplateDoc.getElementsByTagName("//@entityid").item(i);
                Child.setNodeValue("");
            }

            // Clear EntityIdType attribute
            // for (Node Child : TemplateDoc.SelectNodes("//@entityidtype")){
            for (int i = 0; i < TemplateDoc.getElementsByTagName("//@entityidtype").getLength(); i++)
            {
                Node Child = TemplateDoc.getElementsByTagName("//@entityidtype").item(i);
                Child.setNodeValue("");
            }

            // Clear all Timestamps attributes
            // for (Node Child : TemplateDoc.SelectNodes("//@datecreated")){
            for (int i = 0; i < TemplateDoc.getElementsByTagName("//@datecreated").getLength(); i++)
            {
                Node Child = TemplateDoc.getElementsByTagName("//@datecreated").item(i);
                Child.setNodeValue("");
            }

            // Clear all Timestamps attributes
            // for (Node Child : TemplateDoc.SelectNodes("//@lastmodified")){
            for (int i = 0; i < TemplateDoc.getElementsByTagName("//@lastmodified").getLength(); i++)
            {
                Node Child = TemplateDoc.getElementsByTagName("//@lastmodified").item(i);
                Child.setNodeValue("");
            }

            // Remove all Records
            // for (Node Child : TemplateDoc.SelectNodes("//record")){
            for (int i = 0; i < TemplateDoc.getElementsByTagName("//record").getLength(); i++)
            {
                Node Child = TemplateDoc.getElementsByTagName("//record").item(i);
                Child.getParentNode().removeChild(Child);
            }

            // Remove all Linkages
            // for (Node Child : TemplateDoc.SelectNodes("//linkage")){
            for (int i = 0; i < TemplateDoc.getElementsByTagName("//linkage").getLength(); i++)
            {
                Node Child = TemplateDoc.getElementsByTagName("//linkage").item(i);
                Child.getParentNode().removeChild(Child);
            }

            // Set Template Doc
            this.SetDataObjectDocument(TemplateDoc);

            // return Success
            return CallResult.successCallResult;
*/
            return true;
        }
        catch (Exception ex)
        {
            // return Failed Error
            return false;
        }
    }

    // -----------------------------------------------------------------------//
    // public Properties
    // -----------------------------------------------------------------------//

    public Document GetDataObjectDocument()
    {
        return this._DataObjectDocument;
    }

    public void SetDataObjectDocument(Document value)
    {
        this._DataObjectDocument = value;
        // value.getDocumentElement();
        // value.getFirstChild();
        // TODO: need make sure getElementsByTagName is a good replacement for vb's SelectSingleNode function
        this.SetEntityNode(value.getElementsByTagName("entity").item(0));
        // this._EntityNode = value.SelectSingleNode("entity");
    }

    public Node GetEntityNode()
    {
        return this._EntityNode;
    }

    public void SetEntityNode(Node value)
    {
        this._EntityNode = value;
    }

    public String Name;

    public String GetName()
    {
        return _XmlHelper.GetAttribute(this.GetEntityNode(), "name");
    }

    public void Set(String value)
    {
        _XmlHelper.SetAttribute(this.GetDataObjectDocument(), this.GetEntityNode(), "name", value);
    }

    // readonly
    public String Source;

    public String GetSource()
    {
        return _XmlHelper.GetAttribute(this.GetEntityNode(), "source");
    }

    // public void SetSource(String value){
    // _XmlHelper.SetAttribute(this.DataObjectDocument, this.EntityNode, "source", value);
    // }

    public String Version;

    public String GetVersion()
    {
        return _XmlHelper.GetAttribute(this.GetEntityNode(), "version");
    }

    public void SetVersion(String value)
    {
        _XmlHelper.SetAttribute(this.GetDataObjectDocument(), this.GetEntityNode(), "version", value);
    }

    // -----------------------------------------------------------------------//
    // public Methods
    // -----------------------------------------------------------------------//

    public XsdEntity CreateNewEntity()
    {

        // TODO: Not Implemented

        // Create a new DataObjectDocument from the EntityTemplate's DataObjectDocument using Clone
        Document DataObjectDoc = this.GetDataObjectDocument(); // .DataObjectDocument.Clone;

        // Create a new CoalesceEntity
        XsdEntity Entity = new XsdEntity();

        // Initialize it off of the clone DataObjectDocument
        // return Entity.Initialize(DataObjectDoc);

        // // return Success
        // return new CallResult(CallResults.SUCCESS);
        return null;
    }

}
