package Coalesce.Framework.DataModel;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import Coalesce.Common.Helpers.XmlHelper;
import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;

public class CoalesceEntityTemplate {
    //-----------------------------------------------------------------------//
    // Protected Member Variables
    //-----------------------------------------------------------------------//

    protected Document _DataObjectDocument;
    protected Node _EntityNode;
	XmlHelper _XmlHelper = new XmlHelper();

    //-----------------------------------------------------------------------//
    // Factory and Initialization
    //-----------------------------------------------------------------------//

    public CallResult Create(CoalesceEntityTemplate EntityTemplate, CoalesceEntity Entity){
        try{
        	CallResult rst;

            // Create a new CoalesceEntityTemplate
        	CoalesceEntityTemplate EntTemp = new CoalesceEntityTemplate();

            // Initialize
            rst = EntTemp.InitializeFromEntity(Entity);

            // Evaluate
            if (rst.getIsSuccess()) {
                EntityTemplate = EntTemp;
            }else{
                EntityTemplate = null;
            }

            // return
            return rst;

        }catch( Exception ex ){ 
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceEntityTemplate");
        }
    }

    public CallResult Initialize(String EntityTemplateXml){
        try{
        	//TODO: need loadXml function
//            // Create DataObjectDocument
            Document XmlDoc = null;
//            XmlDoc.LoadXml(EntityTemplateXml);

            // Call Peer.
            return Initialize(XmlDoc);

        }catch( Exception ex ){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult Initialize(Document EntityTemplateDataObjectDocument){
        try{
            // Set DataObjectDocument
            this.SetDataObjectDocument(EntityTemplateDataObjectDocument);

            // return Success
            return CallResult.successCallResult;

        }catch( Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult InitializeFromEntity(CoalesceEntity Entity){
        try{
        	//TODO: need make sure getElementsByTagName is a good replacement for vb's SelectNodes function
            // Create a Clone of the Entity's DataObjectDocument
        	Document TemplateDoc = Entity.GetDataObjectDocument();

            // Clear all Key attributes
	        //for (Node Child : TemplateDoc.SelectNodes("//@key")){
		    for(int i=0; i<TemplateDoc.getElementsByTagName("//@key").getLength(); i++){
	        	Node Child = TemplateDoc.getElementsByTagName("//@key").item(i);
                Child.setNodeValue("");
            }

            // Clear EntityId attribute
	        //for (Node Child : TemplateDoc.SelectNodes("//@entityid")){
		    for(int i=0; i<TemplateDoc.getElementsByTagName("//@entityid").getLength(); i++){
		    	Node Child = TemplateDoc.getElementsByTagName("//@entityid").item(i);
                Child.setNodeValue("");
            }

            // Clear EntityIdType attribute
	        //for (Node Child : TemplateDoc.SelectNodes("//@entityidtype")){
		    for(int i=0; i<TemplateDoc.getElementsByTagName("//@entityidtype").getLength(); i++){
		    	Node Child = TemplateDoc.getElementsByTagName("//@entityidtype").item(i);
                Child.setNodeValue("");
            }

            // Clear all Timestamps attributes
	        //for (Node Child : TemplateDoc.SelectNodes("//@datecreated")){
		    for(int i=0; i<TemplateDoc.getElementsByTagName("//@datecreated").getLength(); i++){
		    	Node Child = TemplateDoc.getElementsByTagName("//@datecreated").item(i);
                Child.setNodeValue("");
            }

            // Clear all Timestamps attributes
	        //for (Node Child : TemplateDoc.SelectNodes("//@lastmodified")){
		    for(int i=0; i<TemplateDoc.getElementsByTagName("//@lastmodified").getLength(); i++){
		    	Node Child = TemplateDoc.getElementsByTagName("//@lastmodified").item(i);
                Child.setNodeValue("");
            }

            // Remove all Records
	        //for (Node Child : TemplateDoc.SelectNodes("//record")){
		    for(int i=0; i<TemplateDoc.getElementsByTagName("//record").getLength(); i++){
		    	Node Child = TemplateDoc.getElementsByTagName("//record").item(i);
                Child.getParentNode().removeChild(Child);
            }

            // Remove all Linkages
	        //for (Node Child : TemplateDoc.SelectNodes("//linkage")){
		    for(int i=0; i<TemplateDoc.getElementsByTagName("//linkage").getLength(); i++){
		    	Node Child = TemplateDoc.getElementsByTagName("//linkage").item(i);
                Child.getParentNode().removeChild(Child);
            }

            // Set Template Doc
            this.SetDataObjectDocument(TemplateDoc);

            // return Success
            return CallResult.successCallResult;

        }catch( Exception ex ){ 
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    //-----------------------------------------------------------------------//
    // public Properties
    //-----------------------------------------------------------------------//

    public Document DataObjectDocument;
    public Document GetDataObjectDocument(){
    	return this._DataObjectDocument;
    }
    public void SetDataObjectDocument(Document value){
    	this._DataObjectDocument = value;
    	//value.getDocumentElement();
    	//value.getFirstChild();
    	//TODO: need make sure getElementsByTagName is a good replacement for vb's SelectSingleNode function
    	this._EntityNode = value.getElementsByTagName("entity").item(0);
    	//this._EntityNode = value.SelectSingleNode("entity");
    }

    public Node EntityNode;
    public Node GetEntityNode(){
    	return this._EntityNode;
    }
    public void SetEntityNode(Node value){
    	this._EntityNode = value;
    }

    public String Name;
    public String GetName(){
    	return _XmlHelper.GetAttribute(this.EntityNode, "name");
    }
    public void Set(String value){
    	_XmlHelper.SetAttribute(this.DataObjectDocument, this.EntityNode, "name", value);
    }

    //readonly
    public String Source; 
    public String GetSource(){
            return _XmlHelper.GetAttribute(this.EntityNode, "source");
    }
//    public void SetSource(String value){
//            _XmlHelper.SetAttribute(this.DataObjectDocument, this.EntityNode, "source", value);
//    }

    public String Version;
    public String GetVersion(){
            return _XmlHelper.GetAttribute(this.EntityNode, "version");
    }
    public void SetVersion(String value){
            _XmlHelper.SetAttribute(this.DataObjectDocument, this.EntityNode, "version", value);
    }

    //-----------------------------------------------------------------------//
    // public Methods
    //-----------------------------------------------------------------------//

    public CallResult CreateNewEntity(CoalesceEntity Entity){
        try{
            // Create a new DataObjectDocument from the EntityTemplate's DataObjectDocument using Clone
            Document DataObjectDoc = this.GetDataObjectDocument(); //.DataObjectDocument.Clone;

            // Create a new CoalesceEntity
            Entity = new CoalesceEntity();

            // Initialize it off of the clone DataObjectDocument
            return Entity.Initialize(DataObjectDoc);

            //// return Success
            //return new CallResult(CallResults.SUCCESS);

        }catch( Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

}
