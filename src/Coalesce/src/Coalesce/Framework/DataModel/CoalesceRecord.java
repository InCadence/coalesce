package Coalesce.Framework.DataModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.w3c.dom.Node;

import Coalesce.Common.Helpers.DateTimeHelper;
import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;

public class CoalesceRecord extends CoalesceDataObject {

    //-----------------------------------------------------------------------//
    // Factory and Initialization
    //-----------------------------------------------------------------------//

    public CallResult Create(CoalesceRecordset ParentRecordset, CoalesceRecord NewRecord, String Name) {
        try{
            CallResult rst;
            Node NewNode = null;

            // Create CoalesceRecord Node
            NewRecord = new CoalesceRecord();

            // Create the DataObjectNode
            //TODO: is this the right way to create a new node?
            //NewNode = ParentRecordset.GetDataObjectDocument().CreateNode(XmlNodeType.Element, "record", "");
            NewNode = (Node) ParentRecordset.GetDataObjectDocument().createElement("record");
            ParentRecordset.GetDataObjectNode().appendChild(NewNode);

            // Initialize the CoalesceRecord Object
            rst = NewRecord.Initialize(ParentRecordset, NewNode);
            if ( !(rst.getIsSuccess()) ) return rst;

            // Create Fields
            //For Each FieldDefinition As CoalesceFieldDefinition In ParentRecordset.FieldDefinitions
	        for(int i=0; i < ParentRecordset.GetFieldDefinitions().size(); i++){
	        	CoalesceFieldDefinition FieldDefinition = ParentRecordset.GetFieldDefinitions().get(i);
                // Got a Definition; Create the Field                        
	        	CoalesceField NewField = new CoalesceField();
                rst = NewField.Create(NewRecord, NewField, FieldDefinition);
            }

            Date UTCDate = new Date();
            DateTimeHelper.ConvertDateToGMT(UTCDate);

            // Set Default Values
            //rst = GUIDHelper.GetGuidString(Guid.NewGuid, NewRecord.Key);
        	NewRecord.SetKey(java.util.UUID.randomUUID().toString());
            NewRecord.SetName(Name);
            NewRecord.SetDateCreated(UTCDate);
            NewRecord.SetLastModified(UTCDate);

            // Add to Parent's Child Collection
            if ( !(ParentRecordset.GetChildDataObjects().containsKey(NewRecord.GetKey())) ) {
                ParentRecordset.GetChildDataObjects().put(NewRecord.GetKey(), NewRecord);
            }

            // return Success
            return new CallResult(CallResults.SUCCESS);

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceRecord");
        }
    }

    public CallResult Initialize(CoalesceRecordset Parent, Node DataObjectNode) {
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
            //if (this.GetKey() == "") rst = GUIDHelper.GetGuidString(Guid.NewGuid, this.Key);
            if (this.GetKey() == "") this.SetKey(java.util.UUID.randomUUID().toString());
            if (DateTimeHelper.getDateTicks(this.DateCreated) == 0) this.DateCreated = UTCDate;
            if (DateTimeHelper.getDateTicks(this.LastModified) == 0) this.LastModified = UTCDate;

            // Iterate Child Nodes
            //For Each Node As XmlNode In this.DataObjectNode.ChildNodes
	        for(int i=0; i < this._DataObjectNode.getChildNodes().getLength(); i++){
	        	Node ChildNode = this._DataObjectNode.getChildNodes().item(i);

                // case on Element
                switch (ChildNode.getNodeName()){

                    case "field":
                        // Create a Field Object
                    	CoalesceField NewField = new CoalesceField(); 
                        rst = NewField.Initialize(this, ChildNode);
                        if ( !(rst.getIsSuccess()) ) return rst;

                        // Add to Child Collection
                        this.GetChildDataObjects().put(NewField.GetKey(), NewField);

                }

            }

            // Add to Parent Collections (if we're Active)
            if (this.GetDataObjectStatus() == ECoalesceDataObjectStatus.ACTIVE) {
                Parent.GetChildDataObjects().put(this.GetKey(), this);
                Parent.GetRecords().add(this);
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

    public ArrayList<CoalesceField> GetFields() {
        try{
            // Assemble List
        	ArrayList<CoalesceField> Fields = new ArrayList<CoalesceField>();

            // Build List
            //For Each cdo As CoalesceDataObject In this.ChildDataObjects.Values
        	//TODO: Make sure this loop works as expected
        	for (Iterator<ICoalesceDataObject> iterator = this.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
        		CoalesceField cdo = (CoalesceField) iterator;
                if (cdo.GetName() == "CoalesceField") {
                    Fields.add(cdo);
                }
        	}
//	        for(int i=0; i < this.GetChildDataObjects().size(); i++){
//	        	CoalesceDataObject cdo = this.GetChildDataObjects().item(i);
//                if (cdo.GetType.Name == "CoalesceField") {
//                    Call Fields.add(cdo);
//                }
//            }

            // return
            return Fields;

        }catch(Exception ex){
            // return null; Do Not Log
            return null;
        }
    }

    public ArrayList<String> GetFieldNames() {
        try{
            // Assemble List
        	ArrayList<String> FieldNames = new ArrayList<String>();

            // Build List
            //For Each cdo As CoalesceDataObject In this.ChildDataObjects.Values
        	//TODO: Make sure this loop works as expected
        	for (Iterator<ICoalesceDataObject> iterator = this.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
        		CoalesceDataObject cdo = (CoalesceDataObject) iterator;
                if (cdo.GetName() == "CoalesceField") {
                    FieldNames.add(cdo.GetName());
                }
        	}
//	        for(int i=0; i < this.GetChildDataObjects().size(); i++){
//	        	CoalesceDataObject cdo = this.GetChildDataObjects().values()..item(i);
//                if (cdo.GetType.Name == "CoalesceField") {
//                    Call FieldNames.add(cdo.GetName());
//                }
//            }

            // return
            return FieldNames;

        }catch(Exception ex){
            // return null; Do Not Log
            return null;
        }
    }

    public ArrayList<String> GetFieldKeys() {
        try{
            // Assemble List
        	ArrayList<String> FieldKeys = new ArrayList<String>();

            // Build List
            //For Each cdo As CoalesceDataObject In this.ChildDataObjects.Values
	        //for(int i=0; i < this.GetChildDataObjects().size(); i++){
//        	for (CoalesceDataObject cdo = (CoalesceDataObject) this.GetChildDataObjects().values().iterator(); this.GetChildDataObjects().values().iterator().hasNext();) {
//    			CoalesceDataObject cdo = this.GetChildDataObjects().values()..item(i);
//          	if (cdo.GetType().Name == "CoalesceField") {
//        }}
        	//TODO: Make sure this loop works as expected
        	for (Iterator<ICoalesceDataObject> iterator = this.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
        		CoalesceDataObject cdo = (CoalesceDataObject) iterator;
        		if (cdo.GetName() == "CoalesceField") {
                    //Call FieldKeys.add(cdo.GetKey());
        			FieldKeys.add(cdo.GetKey());
                }
            }

            // return
            return FieldKeys;

        }catch(Exception ex){
            // return null; Do Not Log
            return null;
        }
    }

    public CoalesceField GetFieldByKey(String Key) {
        try{
            // Find Field
            //For Each f As CoalesceField In this.GetFields
//	        for(int i=0; i < this.GetFields().size(); i++){
//	        	CoalesceField f = this.GetFields().item(i);
//        	}
        	for (Iterator<CoalesceField> iterator = this.GetFields().iterator(); iterator.hasNext();) {
        		CoalesceField f = (CoalesceField) iterator;
                if (f.GetKey() == Key) return f;
            }

            // Not Found
            return null;

        }catch(Exception ex){
            // return null; Do Not Log
            return null;
        }
    }

    public CoalesceField GetFieldByName(String Name) {
        try{
            // Find Field
            //For Each f As CoalesceField In this.GetFields
	        for(int i=0; i < this.GetFields().size(); i++){
	        	CoalesceField f = this.GetFields().get(i);
                if (f.GetName() == Name) return f;
            }

            // Not Found
            return null;

        }catch(Exception ex){
            // return null; Do Not Log
            return null;
        }
    }

    public CoalesceField GetFieldByIndex(int Index) {
        try{
            // Iterate List
            return this.GetFields().get(Index);

        }catch(Exception ex){
            // return null; Do Not Log
            return null;
        }
    }

    public String GetFieldValue(String FieldName) {
        try{
            CoalesceField Field = this.GetFieldByName(FieldName);

            // Do we have this Field?
            if (Field != null) {
                // Yes; return Value;
                return Field.Value;
            }else{
                // return Empty String
                return "";
            }

        }catch(Exception ex){
            // Log
            CallResult.log(CallResults.FAILED_ERROR, ex, this);

            // return Empty String
            return "";
        }
    }

    public CallResult GetFieldValue(String FieldName, String Value) { 
        try{
            CoalesceField Field = this.GetFieldByName(FieldName);

            // Do we have this Field?
            if (Field != null) {
                // Yes; Set Value
                Value = Field.Value;

                // return Success
                return CallResult.successCallResult;
            }else{
                // return Failed Error
                return new CallResult(CallResults.FAILED, "Field not found.", this);
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult GetFieldValue(String FieldName, boolean Value) { 
        try{
            CoalesceField Field = this.GetFieldByName(FieldName);

            // Do we have this Field?
            if (Field != null) {
                // Yes; Set Value
                return Field.GetTypedValue(Value);
            }else{
                // return Failed Error
                return new CallResult(CallResults.FAILED, "Field not found.", this);
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult GetFieldValue(String FieldName, int Value) {
        try{
            CoalesceField Field = this.GetFieldByName(FieldName);

            // Do we have this Field?
            if (Field != null) {
                // Yes; Set Value
                return Field.GetTypedValue(Value);
            }else{
                // return Failed Error
                return new CallResult(CallResults.FAILED, "Field not found.", this);
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult GetFieldValue(String FieldName, Date Value) {
        try{
            CoalesceField Field = this.GetFieldByName(FieldName);

            // Do we have this Field?
            if (Field != null) {
                // Yes; Set Value
                return Field.GetTypedValue(Value);
            }else{
                // return Failed Error
                return new CallResult(CallResults.FAILED, "Field not found.", this);
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult GetFieldValue(String FieldName, byte[] Value) {
        try{
            CoalesceField Field = this.GetFieldByName(FieldName);

            // Do we have this Field?
            if (Field != null) {
                // Yes; Set Value
                return Field.GetTypedValue(Value);
            }else{
                // return Failed Error
                return new CallResult(CallResults.FAILED, "Field not found.", this);
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public String GetFieldValueAsString(String FieldName, String DefaultValue) {
        try{
            // Get value
            CallResult rst;
            String Value = null;
            rst = this.GetFieldValue(FieldName, Value);

            // Evaluate
            if (rst.getIsSuccess()) 
                return Value;
            else
                return DefaultValue;

        }catch(Exception ex){
            // Log exception and return default
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
            return DefaultValue;
        }
    }

    public boolean GetFieldValueAsBoolean(String FieldName, boolean DefaultValue) {
        try{
            // Get value
            CallResult rst;
            boolean Value = false;
            rst = this.GetFieldValue(FieldName, Value);

            // Evaluate
            if (rst.getIsSuccess()) 
                return Value;
            else
                return DefaultValue;

        }catch(Exception ex){
            // Log exception and return default
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
            return DefaultValue;
        }
    }

    public int GetFieldValueAsInteger(String FieldName, int DefaultValue) {
        try{
            // Get value
            CallResult rst;
            int Value = 0	;
            rst = this.GetFieldValue(FieldName, Value);

            // Evaluate
            if (rst.getIsSuccess()) 
                return Value;
            else
                return DefaultValue;

        }catch(Exception ex){
            // Log exception and return default
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
            return DefaultValue;
        }
    }

    public Date GetFieldValueAsDate(String FieldName, Date DefaultValue) { 
        try{
            // Get value
            CallResult rst;
            Date Value = null;
            rst = this.GetFieldValue(FieldName, Value);

            // Evaluate
            if (rst.getIsSuccess()) 
                return Value;
            else
                return DefaultValue;

        }catch(Exception ex){
            // Log exception and return default
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
            return DefaultValue;
        }
    }

    public byte[] GetFieldValueAsByteArray(String FieldName, byte[] DefaultValue) {
        try{
            // Get value
            CallResult rst;
            byte[] Value = null;
            rst = this.GetFieldValue(FieldName, Value);

            // Evaluate
            if (rst.getIsSuccess()) 
                return Value;
            else
                return DefaultValue;

        }catch(Exception ex){
            // Log exception and return nothing
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
            return DefaultValue;
        }
    }

    public CallResult SetFieldValue(String FieldName, String Value) { 
        try{
            CoalesceField Field = this.GetFieldByName(FieldName);

            // Do we have this Field?
            if (Field != null) {
                // Yes; Set Value
                Field.Value = Value;

                // return Success
                return CallResult.successCallResult;
            }else{
                // return Failed Error
                return new CallResult(CallResults.FAILED, "Field not found.", this);
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetFieldValue(String FieldName, boolean Value) { 
        try{
            CoalesceField Field = this.GetFieldByName(FieldName);

            // Do we have this Field?
            if (Field != null) {
                // Yes; Set Value
                return Field.SetTypedValue(Value);
            }else{
                // return Failed Error
                return new CallResult(CallResults.FAILED, "Field not found.", this);
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetFieldValue(String FieldName, int Value) {
        try{
            CoalesceField Field = this.GetFieldByName(FieldName);

            // Do we have this Field?
            if (Field != null) {
                // Yes; Set Value
                return Field.SetTypedValue(Value);
            }else{
                // return Failed Error
                return new CallResult(CallResults.FAILED, "Field not found.", this);
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetFieldValue(String FieldName, Date Value) {
        try{
            CoalesceField Field = this.GetFieldByName(FieldName);

            // Do we have this Field?
            if (Field != null) {
                // Yes; Set Value
                return Field.SetTypedValue(Value);
            }else{
                // return Failed Error
                return new CallResult(CallResults.FAILED, "Field not found.", this);
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetFieldValue(String FieldName, byte[] Value) {
        try{
        	return SetFieldValue(FieldName, Value, "");
        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetFieldValue(String FieldName, byte[] Value, String FileName) {
        try{
            CoalesceField Field = this.GetFieldByName(FieldName);

            // Do we have this Field?
            if (Field != null) {
                // Yes; Set Value
                if (FileName == "") {
                    return Field.SetTypedValue(Value);
                }else{
                    return Field.SetTypedValue(Value, "{" + FileName + "}", ".jpg", "");
                }
            }else{
                // return Failed Error
                return new CallResult(CallResults.FAILED, "Field not found.", this);
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public Boolean HasField(String Name) {
        try{
            // Find Field
            //For Each f As CoalesceField In this.GetFields
	        for(int i=0; i < this.GetFields().size(); i++){
	        	CoalesceField f = this.GetFields().get(i);
                if (f.Name == Name) return true;
            }

            // return false
            return false;

        }catch(Exception ex){
            // Log
            CallResult.log(CallResults.FAILED_ERROR, ex, this);

            // return false
            return false;
        }
    }

    public CallResult ToXml(String Xml) {
        try{
            // Examine XmlNode
            if (this.GetDataObjectNode() != null) {
                // Get Xml
            	//TODO: make sure getTextContent is same as OuterXml
                Xml = this.GetDataObjectDocument().getTextContent(); //.OuterXml;
            }else{
                // null
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
