package Coalesce.Framework.DataModel;

import java.util.Date;

import org.w3c.dom.Node;

import Coalesce.Common.Helpers.DateTimeHelper;
import Coalesce.Common.Helpers.XmlHelper;
import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;

public class CoalesceFieldDefinition extends CoalesceDataObject implements ICoalesceFieldDefinition {

	XmlHelper _XmlHelper = new XmlHelper();

//	public enum CoalesceFieldDataTypes {
//		StringType (1), 
//		DateTimeType (2), 
//		UriType (3), 
//		BinaryType (4), 
//		BooleanType (5), 
//		IntegerType (6), 
//		GuidType (7), 
//		GeocoordinateType (8), 
//		FileType (9), 
//		GeocoordinateListType (10);
//	  
//		private int _value;
//		
//		private CoalesceFieldDataTypes(int value){
//			this._value = value;
//		}
//		
//		public Integer getCoalesceFieldDataTypes() {
//			return this._value;
//		}
//
//		public void setCoalesceFieldDataTypes(int value) {
//			this._value = value;
//		}
//
//	}

    //-----------------------------------------------------------------------//
    // Factory and Initialization
    //-----------------------------------------------------------------------//

    public CallResult Create(CoalesceRecordset Parent, CoalesceFieldDefinition newFieldDefinition, String Name, String DataType, String Label, String DefaultClassificationMarking, String DefaultValue) {
        try{
            
            return Create(Parent, newFieldDefinition, Name, DataType, Label, DefaultClassificationMarking, DefaultValue, false);

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceFieldDefinition");
        }
    }

    public CallResult Create(CoalesceRecordset Parent, CoalesceFieldDefinition newFieldDefinition, String Name, String DataType, String Label, String DefaultClassificationMarking, String DefaultValue, boolean NoIndex) {
        try{
            CallResult rst;
            rst = Create(Parent, newFieldDefinition, Name, DataType);

            // Set Additional Properties
            newFieldDefinition.Label = Label;
            newFieldDefinition.DefaultClassificationMarking = DefaultClassificationMarking;
            newFieldDefinition.DefaultValue = DefaultValue;
            if (NoIndex) {
                newFieldDefinition.NoIndex = true;
            }
            // return SUCCESS
            return new CallResult(CallResults.SUCCESS);

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceFieldDefinition");
        }
    }

    public CallResult Create(CoalesceRecordset Parent, CoalesceFieldDefinition newFieldDefinition, String Name, String DataType) {
    	try{
    		return Create(Parent, newFieldDefinition, Name, DataType, false);
    	}catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceFieldDefinition");
    	}
    }
    	

    public CallResult Create(CoalesceRecordset Parent, CoalesceFieldDefinition newFieldDefinition, String Name, String DataType, boolean NoIndex) {
        try{
            CallResult rst;
            Node newNode;

            // Create CoalesceFieldDefinition Node
            newFieldDefinition = new CoalesceFieldDefinition();

            // Create the _DataObjectNode
            //TODO: is this the right way to create a new node?
            newNode = null;
            //newNode = Parent._DataObjectDocument.CreateNode(XmlNodeType.Element, "fielddefinition", "");
            newNode = (Node) Parent.GetDataObjectDocument().createElement("fielddefinition");
            Parent._DataObjectNode.appendChild(newNode);

            // Initialize the CoalesceField Object
            rst = newFieldDefinition.Initialize(Parent, newNode);
            if (!(rst.getIsSuccess())) return rst;

            // Set Default Values
            //TODO: GUIDHelper
            //rst = GUIDHelper.GetGuidString(Guid.newGuid, newFieldDefinition.Key);
            String Key = newFieldDefinition.GetKey();
            if (Key == "" || Key == null) newFieldDefinition.SetKey(java.util.UUID.randomUUID().toString());
            //if (newFieldDefinition.GetKey() == "" || newFieldDefinition.GetKey() == null) newFieldDefinition.SetKey(java.util.UUID.randomUUID().toString());
            newFieldDefinition.Name = Name;

            Date UTCDate = new Date();
            DateTimeHelper.ConvertDateToGMT(UTCDate);
            newFieldDefinition.DateCreated = UTCDate;
            
            newFieldDefinition.LastModified = newFieldDefinition.DateCreated;
            newFieldDefinition.DefaultClassificationMarking = "U";
            newFieldDefinition.DefaultValue = "";
            if (NoIndex) {
                newFieldDefinition.NoIndex = true;
            }

            //TODO: make sure switch type matches case types - string label vs string code value
            switch (DataType){
                case "StringType":
                    newFieldDefinition.DataType = "string";
                    break;
                case "DateTimeType":
                    newFieldDefinition.DataType = "datetime";
                    break;
                case "UriType":
                    newFieldDefinition.DataType = "uri";
                    break;
                case "BinaryType":
                    newFieldDefinition.DataType = "binary";
                    break;
                case "BooleanType":
                    newFieldDefinition.DataType = "boolean";
                    break;
                case "IntegerType":
                    newFieldDefinition.DataType = "integer";
                    break;
                case "GuidType":
                    newFieldDefinition.DataType = "guid";
                    break;
                case "GeocoordinateType":
                    newFieldDefinition.DataType = "geocoordinate";
                    break;
                case "GeocoordinateListType":
                    newFieldDefinition.DataType = "geocoordinatelist";
                    break;
                case "FileType":
                    newFieldDefinition.DataType = "file";
                    break;
            }

            // return SUCCESS
            return new CallResult(CallResults.SUCCESS);

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceFieldDefinition");
        }
    }

    public CallResult Create(CoalesceRecordset Parent, CoalesceFieldDefinition newFieldDefinition, String Name, ECoalesceFieldDataTypes DataType) {
    	try{
    		return Create(Parent, newFieldDefinition, Name, DataType, false);
    	}catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceFieldDefinition");
    	}
    }
    
    public CallResult Create(CoalesceRecordset Parent, CoalesceFieldDefinition newFieldDefinition, String Name, ECoalesceFieldDataTypes DataType , boolean NoIndex) {
        try{
            CallResult rst;
            Node newNode;

            // Create CoalesceFieldDefinition Node
            newFieldDefinition = new CoalesceFieldDefinition();

            // Create the _DataObjectNode
            //TODO: is this the right way to create a new node?
            //newNode = Parent._DataObjectDocument.CreateNode(XmlNodeType.Element, "fielddefinition", "");
            newNode = (Node) Parent.GetDataObjectDocument().createElement("fielddefinition");
            Parent.GetDataObjectNode().appendChild(newNode);

            // Initialize the CoalesceField Object
            rst = newFieldDefinition.Initialize(Parent, newNode);
            if (!(rst.getIsSuccess())) return rst;

            // Set Default Values
            
            //TODO: GUIDHelper
            String key = newFieldDefinition.GetKey();
            if (key == "" || key == null) newFieldDefinition.SetKey(java.util.UUID.randomUUID().toString());
            //rst = GUIDHelper.GetGuidString(Guid.newGuid, newFieldDefinition.Key);
            newFieldDefinition.Name = Name;
            
            Date UTCDate = new Date();
            DateTimeHelper.ConvertDateToGMT(UTCDate);

            newFieldDefinition.DateCreated = UTCDate;
            newFieldDefinition.LastModified = newFieldDefinition.DateCreated;
            newFieldDefinition.DefaultClassificationMarking = "U";
            newFieldDefinition.DefaultValue = "";
            if (NoIndex) {
                newFieldDefinition.NoIndex = true;
            }

            switch (DataType){
                case StringType:
                    newFieldDefinition.DataType = "string";
                    break;
                case DateTimeType:
                    newFieldDefinition.DataType = "datetime";
                    break;
                case UriType:
                    newFieldDefinition.DataType = "uri";
                    break;
                case BinaryType:
                    newFieldDefinition.DataType = "binary";
                    break;
                case BooleanType:
                    newFieldDefinition.DataType = "boolean";
                    break;
                case IntegerType:
                    newFieldDefinition.DataType = "integer";
                    break;
                case GuidType:
                    newFieldDefinition.DataType = "guid";
                    break;
                case GeocoordinateType:
                    newFieldDefinition.DataType = "geocoordinate";
                    break;
                case GeocoordinateListType:
                    newFieldDefinition.DataType = "geocoordinatelist";
                    break;
                case FileType:
                    newFieldDefinition.DataType = "file";
                    break;
            }

            // return SUCCESS
            return new CallResult(CallResults.SUCCESS);

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceFieldDefinition");
        }
    }

    public CallResult Initialize(CoalesceRecordset Parent, Node DataObjectNode){
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
            //if (this.Key == "") rst = GUIDHelper.GetGuidString(Guid.newGuid, this.Key);
            String Key = this.GetKey();
            if (Key == "" || Key == null) this.SetKey(java.util.UUID.randomUUID().toString());

	        if (DateTimeHelper.getDateTicks(this.GetDateCreated()) == 0) this.DateCreated = UTCDate;
	        if (DateTimeHelper.getDateTicks(this.GetLastModified()) == 0) this.LastModified = UTCDate;

            // Add to Parent Collections
            if (this.GetDataObjectStatus() == ECoalesceDataObjectStatus.ACTIVE) {
                Parent.GetChildDataObjects().put(this.GetKey(), this);
                Parent.GetFieldDefinitions().add(this);
            }

            // return SUCCESS
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    //-----------------------------------------------------------------------//
    // public Properties
    //-----------------------------------------------------------------------//

    public String Name;
    @Override
    public String GetName(){
    	return super.Name;
    }
	@Override
	public void SetName(String value){
		super.Name = value;
	}
    
    public String Label;
	@Override
	public String GetLabel(){
    	return _XmlHelper.GetAttribute(this._DataObjectNode, "label");
    }
	@Override
	public void SetLabel(String value){
		_XmlHelper.SetAttribute(this._DataObjectDocument, this._DataObjectNode, "label", value);
	}
    

    public String DataType;
	@Override
    public String GetDataType(){
    	return _XmlHelper.GetAttribute(this._DataObjectNode, "datatype");
    }
	@Override
	public void SetDataType(String value){
		_XmlHelper.SetAttribute(this._DataObjectDocument, this._DataObjectNode, "datatype", value);
	}
    

    public String DefaultClassificationMarking;
    public String GetDefaultClassificationMarking(){
    	return _XmlHelper.GetAttribute(this._DataObjectNode, "defaultclassificationmarking");
    }
    public void SetDefaultClassificationMarking(String value){
    	_XmlHelper.SetAttribute(this._DataObjectDocument, this._DataObjectNode, "defaultclassificationmarking", value);
    }
    

    public String DefaultValue;
    public String GetDefaultValue(){
    	return _XmlHelper.GetAttribute(this._DataObjectNode, "defaultvalue");
    }
    public void SetDefaultValue(String value){
    	_XmlHelper.SetAttribute(this._DataObjectDocument, this._DataObjectNode, "defaultvalue", value);
    }
    

    //-----------------------------------------------------------------------//
    // public Shared Methods
    //-----------------------------------------------------------------------//

    public ECoalesceFieldDataTypes GetCoalesceFieldDataTypeForCoalesceType(String CoalesceType) {
        try{
            switch (CoalesceType.toUpperCase()){

                case "BINARY":
                    return ECoalesceFieldDataTypes.BinaryType;

                case "BOOLEAN":
                    return ECoalesceFieldDataTypes.BooleanType;

                case "DATETIME":
                    return ECoalesceFieldDataTypes.DateTimeType;

                case "GEOCOORDINATE":
                    return ECoalesceFieldDataTypes.GeocoordinateType;

                case "GEOCOORDINATELIST":
                    return ECoalesceFieldDataTypes.GeocoordinateListType;

                case "GUID":
                    return ECoalesceFieldDataTypes.GuidType;

                case "INTEGER":
                    return ECoalesceFieldDataTypes.IntegerType;

                case "URI":
                    return ECoalesceFieldDataTypes.UriType;

                case "FILE":
                    return ECoalesceFieldDataTypes.FileType;

                default:
                    return ECoalesceFieldDataTypes.StringType;

            }
        }catch(Exception ex){
            return ECoalesceFieldDataTypes.StringType;
        }
    }

    public ECoalesceFieldDataTypes GetCoalesceFieldDataTypeForSQLType(String SQLType ) { 
        try{
            switch (SQLType.toUpperCase()){
                case "ADVARWCHAR":
                case "ADLONGVARWCHAR":
                    return ECoalesceFieldDataTypes.StringType;
                case "ADDBTIMESTAMP":
                    return ECoalesceFieldDataTypes.DateTimeType;
                case "ADBOOLEAN":
                    return ECoalesceFieldDataTypes.BooleanType;
                case "ADGUID":
                    return ECoalesceFieldDataTypes.GuidType;
                case "ADSMALLINT":
                case "ADINTEGER":
                    return ECoalesceFieldDataTypes.IntegerType;
                case "ADLONGVARBINARY":
                    return ECoalesceFieldDataTypes.BinaryType;
                default:
                    return ECoalesceFieldDataTypes.StringType;
            }

        }catch(Exception ex){
            // Log
            CallResult.log(CallResults.FAILED_ERROR, ex, "Coalesce.Common.Helpers.CoalesceFieldDefinition");

            // return String Type
            return ECoalesceFieldDataTypes.StringType;
        }
    }

    //-----------------------------------------------------------------------//
    // public Methods
    //-----------------------------------------------------------------------//

    public CallResult ToXml(String Xml) {
        try{
        	//TODO: XML
//            // Examine XmlNode
//            if (this._DataObjectNode != null) {
//                // Get Xml
//                Xml = this._DataObjectDocument.OuterXml;
//            }else{
//                // Nothing
//                Xml = "";
//            }

            // return SUCCESS
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

}
