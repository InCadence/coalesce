package Coalesce.Framework.DataModel;

import java.util.Date;

import org.w3c.dom.Node;

import Coalesce.Common.Helpers.DateTimeHelper;
import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;

public class CoalesceSection extends CoalesceDataObject {

	//-----------------------------------------------------------------------//
    // Factory and Initialization
    //-----------------------------------------------------------------------//

    public CallResult Create(CoalesceSection Parent, CoalesceSection NewSection, String Name) {
    try{
    	return Create(Parent, NewSection, Name, false);
    }catch(Exception ex){
        // return Failed Error
        return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceSection");
    }
}
	
    public CallResult Create(CoalesceSection Parent, CoalesceSection NewSection, String Name, boolean NoIndex) {
        try{
            CallResult rst;
            Node NewNode = null;

            // Create CoalesceSection Node
            NewSection = new CoalesceSection();

            //TODO:Node creation (remove null assignment above)
            // Create the DataObjectNode
            //.DOCUMENT_NODE  //NewNode = Parent.GetDataObjectDocument().CreateNode(Node.ELEMENT_NODE, "section", "");
            //NewNode = Parent.GetDataObjectDocument().CreateNode(XmlNodeType.Element, "section", "");
            Parent.GetDataObjectNode().appendChild(NewNode);

            // Initialize the CoalesceField Object
            rst = NewSection.Initialize(Parent, NewNode);
            if ( !(rst.getIsSuccess())) return rst;

            Date UTCDate = new Date();
            DateTimeHelper.ConvertDateToGMT(UTCDate);

            // Set Default Values
            //TODO: GUIDHelper
            //rst = GUIDHelper.GetGuidString(Guid.NewGuid, NewSection.Key)
            NewSection.SetKey(java.util.UUID.randomUUID().toString());
            NewSection.SetName(Name);
            NewSection.SetDateCreated(UTCDate);
            NewSection.SetLastModified(UTCDate);
            if (NoIndex) NewSection.NoIndex = true;

            // Add to Parent's Child Collection
            if ( !(Parent.GetChildDataObjects().containsKey(NewSection.GetKey())) ) {
                Parent.GetChildDataObjects().put(NewSection.GetKey(), NewSection);
            }

            // return Success
            return new CallResult(CallResults.SUCCESS);

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceSection");
        }
    }

	public CallResult Create(CoalesceEntity Parent, CoalesceSection NewSection, String Name) {
	try{
		return this.Create(Parent, NewSection, Name, false);
	}catch(Exception ex){
	    // return Failed Error
	    return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceSection");
	}
	}

    public CallResult Create(CoalesceEntity Parent, CoalesceSection NewSection, String Name, boolean NoIndex) {
        try{
            CallResult rst;
            Node NewNode = null;

            // Create CoalesceSection Node
            NewSection = new CoalesceSection();

            //TODO:Node creation (remove null assignment above)
            // Create the DataObjectNode
            //.DOCUMENT_NODE  //NewNode = Parent.GetDataObjectDocument().CreateNode(Node.ELEMENT_NODE, "linkagesection", "");
            //NewNode = Parent.GetDataObjectDocument().CreateNode(XmlNodeType.Element, "section", "");
            Parent.GetDataObjectNode().appendChild(NewNode);
            Date UTCDate = new Date();
            DateTimeHelper.ConvertDateToGMT(UTCDate);

            // Initialize the CoalesceField Object
            rst = NewSection.Initialize(Parent, NewNode);
            if ( !(rst.getIsSuccess())) return rst;

            // Set Default Values
            //TODO: GUIDHelper
            //rst = GUIDHelper.GetGuidString(Guid.NewGuid, NewSection.GetKey());
            if (NewSection.GetKey() == "") NewSection.SetKey(java.util.UUID.randomUUID().toString());
            NewSection.SetName(Name);
            NewSection.SetDateCreated(UTCDate);
            NewSection.SetLastModified(UTCDate);
            if (NoIndex) NewSection.NoIndex = true;

            // Add to Parent's Child Collection
            if ( !(Parent.GetChildDataObjects().containsKey(NewSection.GetKey())) ) {
                Parent.GetChildDataObjects().put(NewSection.GetKey(), NewSection);
            }

            // return Success
            return new CallResult(CallResults.SUCCESS);

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceSection");
        }
    }

    public CallResult Initialize(CoalesceEntity Parent, Node DataObjectNode) {
        try{
            // Since the Section can be initialized with an Entity as a parent or
            // another Section as a parent, we have a private method that takes in
            // an ICoalesceDataObject, and these public methods expose the strongly
            // typed interface to outside callers.
            return this.Initialize((ICoalesceDataObject)Parent, DataObjectNode);

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult Initialize(CoalesceSection Parent, Node DataObjectNode) { 
        try{
            // Since the Section can be initialized with an Entity as a parent or
            // another Section as a parent, we have a private method that takes in
            // an ICoalesceDataObject, and these public methods expose the strongly
            // typed interface to outside callers.
            return this.Initialize((ICoalesceDataObject)Parent, DataObjectNode);

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    private CallResult Initialize(ICoalesceDataObject Parent, Node DataObjectNode) {
        try{
            CallResult rst;

            // Set References
            this.SetDataObjectDocument(Parent.GetDataObjectDocument());
            this.SetDataObjectNode(DataObjectNode);
            this.SetParent(Parent);
            Date UTCDate = new Date();
            DateTimeHelper.ConvertDateToGMT(UTCDate);

            // Check Keys and Timestamps
            //TODO: GUIDHelper
            //if (this.Key == "") rst = GUIDHelper.GetGuidString(Guid.NewGuid, this.Key);
            if (this.GetKey() == "") this.SetKey(java.util.UUID.randomUUID().toString());
            if (DateTimeHelper.getDateTicks(this.DateCreated) == 0) this.DateCreated = UTCDate;
            if (DateTimeHelper.getDateTicks(this.LastModified) == 0) this.LastModified = UTCDate;

            // Iterate Child Nodes
	        for(int i=0; i < this._DataObjectNode.getChildNodes().getLength(); i++){
	        	Node ChildNode = this._DataObjectNode.getChildNodes().item(i);

	        	String NodeName = ChildNode.getNodeName();
                // case on Element
                switch(NodeName){

                    case "section":
                        // Create a Section Object
                    	CoalesceSection NewSection = new CoalesceSection();
                        rst = NewSection.Initialize(this, ChildNode);
                        if ( !(rst.getIsSuccess())) return rst;

                        // Add to Child Collection
                        if ( !(this.GetChildDataObjects().containsKey(NewSection.GetKey())) ) {
                            this.GetChildDataObjects().put(NewSection.GetKey(), NewSection);
                        }

                    case "recordset":
                    	//TODO: CoalesceRecordset
                        // Create a Record Object
//                    	CoalesceRecordset NewRecordset = new CoalesceRecordset();
//                        rst = NewRecordset.Initialize(this, ChildNode);
//                        if ( !(rst.getIsSuccess())) return rst;
//
//                        // Add to Child Collection
//                        if ( !(this.GetChildDataObjects().containsKey(NewRecordset.GetKey())) ) {
//                            this.GetChildDataObjects().put(NewRecordset.GetKey(), NewRecordset);
//                        }

                    default:
                        // Unexpected; Ignore
                }

            }

            // return Success
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    //-----------------------------------------------------------------------//
    // public Methods
    //-----------------------------------------------------------------------//

    public CallResult CreateSection(CoalesceSection NewSection, String Name) {
        try{
            CallResult rst;

            // Create new Section
            rst = NewSection.Create(this, NewSection, Name);
            if ( !(rst.getIsSuccess())) return rst;

            // return Success
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult CreateRecordset(CoalesceRecordset NewRecordset, String Name) { 
        try{
            CallResult rst;

        	//TODO: CoalesceRecordset
            // Create new Record
//            rst = CoalesceRecordset.Create(this, NewRecordset, Name);
//            if ( !(rst.getIsSuccess())) return rst;

            // return Success
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult ToXml(String Xml){
        try{
            // Examine XmlNode
            if (this.GetDataObjectNode() != null) {
                // Get Xml
            	//TODO: make sure this is good substitute for OuterXml
                Xml = this._DataObjectDocument.getTextContent(); //.OuterXml
            }else{
                // Nothing
                Xml = "";
            }

            // return Success
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }
    
}
