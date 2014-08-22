package Coalesce.Framework.DataModel;

import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import Coalesce.Common.Helpers.DateTimeHelper;
import Coalesce.Common.Helpers.XmlHelper;
import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;

public class CoalesceFieldHistory extends CoalesceField {

	XmlHelper _XmlHelper = new XmlHelper();

	//-----------------------------------------------------------------------//
    // Factory and Initialization
    //-----------------------------------------------------------------------//

    public CallResult Create(CoalesceField ParentField, CoalesceFieldHistory NewFieldHistory) { 
        try{
            CallResult rst;
            Node element = null; //xmlelement

            // Create CoalesceFieldHistory Node
            NewFieldHistory = new CoalesceFieldHistory();

            // Set References
            NewFieldHistory._DataObjectDocument = ParentField._DataObjectDocument;

            // Create new fieldhistory element (based on same document)
            element = NewFieldHistory._DataObjectDocument.createElement("fieldhistory");

            //TODO: confirm NamedNodeMap is valid replacement from vb XmlAttributeCollection
            // Copy attributes from parent node
            //XmlAttributeCollection attrs = ParentField._DataObjectNode.getAttributes();
            NamedNodeMap attrs = ParentField._DataObjectNode.getAttributes();
	        //for (XmlAttribute attr : attrs){
            for(int i = 0 ; i<attrs.getLength() ; i++) {
            	// new attribute (imported into same document)
	        	Attr attr = (Attr)attrs.item(i); 
	        	Node newAttr = NewFieldHistory._DataObjectDocument.importNode(attr, true);
                // Assign new attributes to element
	        	element.getAttributes().setNamedItem(newAttr);
                //element.Attributes.SetNamedItem(newAttr);
            }

            // Assign element as data object node
            NewFieldHistory._DataObjectNode = element;

            NewFieldHistory.SetParent(ParentField);
            //TODO: GUIDHelper
            //rst = GUIDHelper.GetGuidString(Guid.NewGuid, NewFieldHistory.Key);
            if (NewFieldHistory.GetKey() == "") NewFieldHistory.SetKey(java.util.UUID.randomUUID().toString());
            rst = _XmlHelper.SetAttribute(NewFieldHistory._DataObjectDocument, NewFieldHistory._DataObjectNode, "previoushistorykey", ParentField.PreviousHistoryKey);


            // Append to parent's child node collection
            ParentField._DataObjectNode.appendChild(NewFieldHistory._DataObjectNode);

            // Add to Parent's Child Collection
            if (!(ParentField._ChildDataObjects.containsKey(NewFieldHistory.GetKey()))) {
                ParentField._ChildDataObjects.put(NewFieldHistory.GetKey(), NewFieldHistory);
            }

            // return SUCCESS
            return new CallResult(CallResults.SUCCESS);

        }catch (Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceFieldHistory");
        }
    }

    public CallResult Initialize(CoalesceField Parent, Node DataObjectNode) { 
        try{
            CallResult rst;

            // Set References
            this.SetDataObjectDocument(Parent._DataObjectDocument);
            this.SetDataObjectNode(DataObjectNode);
            this.SetParent(Parent);

            // Check Keys and Timestamps
            //TODO: GUIDHelper
            //if (Key == "") rst = GUIDHelper.GetGuidString(Guid.NewGuid, Key);
            String Key = this.GetKey();
            if (Key == "") Key = java.util.UUID.randomUUID().toString();
            this.SetKey(Key);
            
            Date UTCDate = new Date();
            DateTimeHelper.ConvertDateToGMT(UTCDate);

	        if (DateTimeHelper.getDateTicks(this.GetDateCreated()) == 0) this.DateCreated = UTCDate;
	        if (DateTimeHelper.getDateTicks(this.GetLastModified()) == 0) this.LastModified = UTCDate;

            // return SUCCESS
            return CallResult.successCallResult;

        }catch (Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    //-----------------------------------------------------------------------//
    // public Properties
    //-----------------------------------------------------------------------//

    //-----------------------------------------------------------------------//
    // public Methods
    //-----------------------------------------------------------------------//

}
