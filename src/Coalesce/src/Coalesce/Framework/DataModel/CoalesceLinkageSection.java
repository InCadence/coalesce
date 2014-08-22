package Coalesce.Framework.DataModel;

import java.util.Date;

import org.w3c.dom.Node;

import Coalesce.Common.Helpers.DateTimeHelper;
import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;

public class CoalesceLinkageSection extends CoalesceDataObject {

    //-----------------------------------------------------------------------//
    // Factory and Initialization
    //-----------------------------------------------------------------------//

    public CallResult Create(CoalesceEntity Parent, CoalesceLinkageSection NewLinkageSection){
        try{
        	boolean NoIndex = false;
            // return Success
            return Create(Parent, NewLinkageSection, NoIndex);

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceLinkageSection");
        }
    }
    
	public CallResult Create(CoalesceEntity Parent, CoalesceLinkageSection NewLinkageSection, Boolean NoIndex){
        try{
            CallResult rst;
            Node NewNode = null;

            // Create CoalesceLinkageSection Node
            NewLinkageSection = new CoalesceLinkageSection();

            //TODO: is this the right way to create a new node?
            // Create the DataObjectNode
            //.DOCUMENT_NODE  //NewNode = Parent.GetDataObjectDocument().CreateNode(Node.ELEMENT_NODE, "linkagesection", "");
            //NewNode = Parent.GetDataObjectDocument().CreateNode(XmlNodeType.Element, "linkagesection", "");
            NewNode = (Node) Parent.GetDataObjectDocument().createElement("linkagesection");
            Parent.GetDataObjectNode().appendChild(NewNode);

            // Initialize the CoalesceField Object
            rst = NewLinkageSection.Initialize(Parent, NewNode);
            if (! (rst.getIsSuccess()) ) return rst;

            // Set Default Values
            //TODO: GUIDHelper
            //rst = GUIDHelper.GetGuidString(Guid.NewGuid, NewLinkageSection.Key);
            NewLinkageSection.SetKey(java.util.UUID.randomUUID().toString());
            NewLinkageSection.Name = "Linkages";
            Date UTCDate = new Date();
            DateTimeHelper.ConvertDateToGMT(UTCDate);
            
            NewLinkageSection.DateCreated = UTCDate;
            NewLinkageSection.LastModified = NewLinkageSection.DateCreated;
            if (NoIndex = false) 
                NewLinkageSection.NoIndex = false;
            else
                NewLinkageSection.NoIndex = true;
            

            // Add to Parent's Child Collection
            if (! (Parent.GetChildDataObjects().containsKey(NewLinkageSection.GetKey()))) {
                Parent.GetChildDataObjects().put(NewLinkageSection.GetKey(), NewLinkageSection);
            }

            // return Success
            return new CallResult(CallResults.SUCCESS);

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceLinkageSection");
        }
    }

    public CallResult Initialize(CoalesceEntity Parent, Node DataObjectNode) {
        try{
            // Since the Section can be initialized with an Entity as a parent or
            // another Section as a parent, we have a private method that takes in
            // an ICoalesceDataObject, and these public methods expose the strongly
            // typed interface to outside callers.
            return this.Initialize((ICoalesceDataObject) Parent, DataObjectNode);

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
            //if (this.Key == "") { rst = GUIDHelper.GetGuidString(Guid.NewGuid, this.Key)
            if (this.GetKey() == "") this.SetKey(java.util.UUID.randomUUID().toString());
            if (DateTimeHelper.getDateTicks(this.DateCreated) == 0) this.SetDateCreated(UTCDate);
            if (DateTimeHelper.getDateTicks(this.LastModified) == 0) this.SetLastModified(UTCDate);

            // Iterate Child Nodes
            //For Each Node As XmlNode In this.DataObjectNode.ChildNodes
	        for(int i=0; i<this._DataObjectNode.getChildNodes().getLength(); i++){
	        	Node ChildNode = this._DataObjectNode.getChildNodes().item(i);

	        	String NodeName = ChildNode.getNodeName();
                // Case on Element
                switch (NodeName){

                    case "linkage":
                        // Create a Linkage Object
                    	CoalesceLinkage NewLinkage = new CoalesceLinkage();
                        rst = NewLinkage.Initialize(this, ChildNode);
                        if (! (rst.getIsSuccess()) ) return rst;

                        // Add to Child Collection
                        if (! (this.GetChildDataObjects().containsKey(NewLinkage.GetKey()))) {
                            this.GetChildDataObjects().put(NewLinkage.GetKey(), NewLinkage);
                        }

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

    public CallResult CreateLinkage(CoalesceLinkage NewLinkage) { 
        try{
            CallResult rst;

            // Create new Linkage
            rst = NewLinkage.Create(this, NewLinkage);
            if (! (rst.getIsSuccess()) ) return rst;

            // return Success
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult ToXml(String Xml) {
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
