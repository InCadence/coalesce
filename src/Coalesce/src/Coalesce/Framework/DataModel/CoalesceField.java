package Coalesce.Framework.DataModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.io.*;

import org.w3c.dom.Node;

import Coalesce.Common.Helpers.DateTimeHelper;
import Coalesce.Common.Helpers.XmlHelper;
import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;

public class CoalesceField extends CoalesceDataObject implements ICoalesceField {

    //-----------------------------------------------------------------------//
    // protected Member Variables
    //-----------------------------------------------------------------------//

    protected Boolean _SuspendHistory= false;
	XmlHelper _XmlHelper = new XmlHelper();

    //-----------------------------------------------------------------------//
    // Factory and Initialization
    //-----------------------------------------------------------------------//
    public CallResult Create(CoalesceRecord Parent, CoalesceField NewField, CoalesceFieldDefinition FieldDefinition){
        try{
            CallResult rst;
            Node NewNode;

            // Create CoalesceField Node
            NewField = new CoalesceField();

            // Create the _DataObjectNode
            //TODO: is this the right way to create a new node?
            //NewNode = Parent._DataObjectDocument.CreateNode(XmlNodeType.Element, "field", "");
            NewNode = (Node) Parent.GetDataObjectDocument().createElement("field");
            Parent.GetDataObjectNode().appendChild(NewNode);

            // Initialize the CoalesceField Object
            rst = NewField.Initialize(Parent, NewNode);
            if ( !(rst.getIsSuccess()) ) return rst;

            // Set Default Values
            String key = NewField.GetKey();
            //TODO: GUIDHelper
            if (key == "" || key == null) NewField.SetKey(java.util.UUID.randomUUID().toString());
            //rst = GUIDHelper.GetGuidString(Guid.NewGuid, NewField.key);
            NewField.SuspendHistory = true;
            NewField.Name = FieldDefinition.Name;
            NewField.DataType = FieldDefinition.DataType;
            NewField.Value = FieldDefinition.DefaultValue;
            NewField.ClassificationMarking = FieldDefinition.DefaultClassificationMarking;
            NewField.Label = FieldDefinition.Label;
            
            Date UTCDate = new Date();
            DateTimeHelper.ConvertDateToGMT(UTCDate);
            
            NewField.DateCreated = UTCDate;
            NewField.LastModified = NewField.DateCreated;
            NewField.SuspendHistory = false;
            if (FieldDefinition.NoIndex == true) 
                NewField.NoIndex = true;

            // Boolean Type?  If so then default initial value to false.
            if (FieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(FieldDefinition.DataType) == ECoalesceFieldDataTypes.BooleanType) 
                NewField.Value = "false";

            // Add to Parent's Child Collection
            if (!(Parent.GetChildDataObjects().containsKey(NewField.GetKey())))
                Parent.GetChildDataObjects().put(NewField.key, NewField);

            // return Success
            return new CallResult(CallResults.SUCCESS);

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, "Coalesce.Framework.DataModel.CoalesceField");
        }
    }

    public CallResult Initialize(CoalesceRecord Parent, Node DataObjectNode){
        try{
            CallResult rst;

            // Set References
            this.SetDataObjectDocument(Parent.GetDataObjectDocument());
            this.SetDataObjectNode(DataObjectNode);
            this.SetParent((ICoalesceDataObject) Parent);

            Date UTCDate = new Date();
            DateTimeHelper.ConvertDateToGMT(UTCDate);

            //TODO: GUIDHelper
	        // Check Keys and Timestamps
            if (this.GetKey() == "" || this.GetKey() == null) this.SetKey(java.util.UUID.randomUUID().toString()); 
            //if (this.GetKey() == "") rst = GUIDHelper.GetGuidString(Guid.NewGuid, this.key);
	        if (DateTimeHelper.getDateTicks(this.GetDateCreated()) == 0) this.DateCreated = UTCDate;
	        if (DateTimeHelper.getDateTicks(this.GetLastModified()) == 0) this.LastModified = UTCDate;

            // Iterate Child Nodes
            //for(Node Node : this._DataObjectNode.ChildNodes){
		    for(int i=0; i<this._DataObjectNode.getChildNodes().getLength(); i++){
	        	Node ChildNode = this._DataObjectNode.getChildNodes().item(i);

                // case on Element
                switch (ChildNode.getNodeName()){

                    case "fieldhistory":
                        // Create a Field History Object
                    	CoalesceFieldHistory NewFieldHistory = new CoalesceFieldHistory();
                        rst = NewFieldHistory.Initialize(this, ChildNode);
                        if ( !(rst.getIsSuccess()) ) return rst;

                        // Add to Child Collection
                        this._ChildDataObjects.put(NewFieldHistory.key, NewFieldHistory);

                }

            }

            // return Success
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    // -----------------------------------------------------------------------// 
    // public Properties
    // -----------------------------------------------------------------------// 

    public String Name;
    public String GetName(){
    	//maybe try this: return this.Name;
    	return super.Name;
    }
    public void SetName(String value){
            super.Name = value;
    }
    
    public Date DateCreated;
    @Override
    public Date GetDateCreated(){
    	return (Date)_XmlHelper.GetAttributeAsDate(_DataObjectNode, "datecreated");
    }
    public String Value;
    public String GetValue(){
            return _XmlHelper.GetAttribute(this._DataObjectNode, "value");
        }
    public void SetValue(String value){
            this.Change("value", value);
    }
    
    //readonly
    public String ValueWithMarking;
    public String GetValueWithMarking(){
    	//TODO: Add Common.ClassificationMarking
//            String val = _XmlHelper.GetAttribute(this._DataObjectNode, "value");
//            Marking mrk = new Marking(this.ClassificationMarking);
//            return mrk.ToPortionString + " " + val;
            return ValueWithMarking;
    }

    //overrides
    public String ToString() {
        return ValueWithMarking;
    }

    public String DataType;
    public String GetDataType(){
    	return _XmlHelper.GetAttribute(this._DataObjectNode, "datatype");
    }
    public void SetDataType(String value){
    	_XmlHelper.SetAttribute(this._DataObjectDocument, this._DataObjectNode, "datatype", value);
    }

    public String Label;
    public String GetLabel(){
    	return _XmlHelper.GetAttribute(this._DataObjectNode, "label");
    }
    public void SetLabel(String value){
    	_XmlHelper.SetAttribute(this._DataObjectDocument, this._DataObjectNode, "label", value);
    }
    
    public Integer Size;
    @Override
    public Integer GetSize(){
    	try{
    		return new Integer(_XmlHelper.GetAttribute(this._DataObjectNode, "size"));
    	}catch(NumberFormatException e){
    		return 0;
        }
    }
    @Override
	public void SetSize(Integer value){
		_XmlHelper.SetAttribute(this._DataObjectDocument, this._DataObjectNode, "size", value.toString());
		Size = value;
	}
    
    public String ModifiedBy;
    public String GetModifiedBy(){
    	return _XmlHelper.GetAttribute(this._DataObjectNode, "modifiedby");
    }
    public void SetModifiedBy(String value){
    	_XmlHelper.SetAttribute(this._DataObjectDocument, this._DataObjectNode, "modifiedby", value);
    }

    public String ModifiedByIP;
    public String GetModifiedByIP(){
    	return _XmlHelper.GetAttribute(this._DataObjectNode, "modifiedbyip");
    }
    public void SetModifiedByIP(String value){
    	_XmlHelper.SetAttribute(this._DataObjectDocument, this._DataObjectNode, "modifiedbyip", value);
    }

    public String ClassificationMarking;
    public String GetClassificationMarking(){
    	return _XmlHelper.GetAttribute(this._DataObjectNode, "classificationmarking");
    }
    public void SetClassificationMarking(String value){
    	this.Change("classificationmarking", value);
    }

    //readonly
    public String PortionMarking;
    public String GetPortionMarking(){
    	//TODO: Common.ClassificationMarking
//    	Marking mrk = new Marking(this.ClassificationMarking);
//    	return mrk.ToPortionString;
    	return this.ClassificationMarking;
    }

    public String PreviousHistoryKey;
    public String GetPreviousHistoryKey(){
    	String PrevHistKey = _XmlHelper.GetAttribute(this._DataObjectNode, "previoushistorykey");
    	if(PrevHistKey == "")
    		return "00000000-0000-0000-0000-000000000000";
    	else
    		return PrevHistKey;
    }
    public void SetPreviousHistoryKey(String value){
    	_XmlHelper.SetAttribute(this._DataObjectDocument, this._DataObjectNode, "previoushistorykey", value);
    }

    public String Filename;
    public String GetFilename(){
    	return _XmlHelper.GetAttribute(this._DataObjectNode, "filename");
    }
    public void SetFilename(String value){
    	this.Change("filename", value);
    }
    
    public String Extension;
    public String GetExtension(){
    	return _XmlHelper.GetAttribute(this._DataObjectNode, "extension").toLowerCase().replace(".", "");
    }
    public void SetExtension(String value){
    	this.Change("extension", value.replace(".", ""));
    }
    
    public String MimeType;
    public String GetMimeType(){
    	return _XmlHelper.GetAttribute(this._DataObjectNode, "mimetype");
    }
    public void SetMimeType(String value){
    	_XmlHelper.SetAttribute(this._DataObjectDocument, this._DataObjectNode, "mimetype", value);
    }

    public String Hash;
    @Override
    public String GetHash(){
    	return _XmlHelper.GetAttribute(this._DataObjectNode, "hash");
    }
    @Override
    public void SetHash(String value){
    	this.Change("hash", value);
    }

    public String InputLang;
    public String GetInputLang(){
    	return _XmlHelper.GetAttribute(this._DataObjectNode, "inputlang");
    }
    public void SetInputLang(String value){
    	_XmlHelper.SetAttribute(this._DataObjectDocument, this._DataObjectNode, "inputlang", value);
    }

    public boolean SuspendHistory;
    public boolean GetSuspendHistory(){
    	return this._SuspendHistory;
    }
    public void SetSuspendHistory(boolean value){
    	this._SuspendHistory = value;
    }
    
    //readonly
    public String CoalesceFullFilename;
    public String GetCoalesceFullFilename(){
    	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
    	if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.FileType) {
    		CallResult rst;

    		String BaseFilename= this.key;
    		//TODO: GUIDHelper
    		//rst = GUIDHelper.RemoveBrackets(BaseFilename);

            String FullDirectory;

            //TODO: CoalesceSettings and System.IO
//            if (CoalesceSettings.SubDirectoryLength > 0 && CoalesceSettings.SubDirectoryLength < BaseFilename.length())
//                FullDirectory = Path.Combine(CoalesceSettings.BinaryFileStoreBasePath, BaseFilename.Substring(0, CoalesceSettings.SubDirectoryLength));
//            else
//                FullDirectory = CoalesceSettings.BinaryFileStoreBasePath;
//
//            if ( !(System.IO.Directory.Exists(FullDirectory)) )
//                System.IO.Directory.CreateDirectory(FullDirectory);
//
//            return Path.Combine(FullDirectory, BaseFilename) + "." + this.Extension;
            return "";
    	}else{
                // return Empty String
                return "";
    	}
    }

  //readonly
    public String CoalesceFullThumbnailFilename;
    public String GetCoalesceFullThumbnailFilename(){
    	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
        if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.FileType) {
            CallResult rst;

            String BaseFilename = this.GetKey();
    		//TODO: GUIDHelper
            //rst = GUIDHelper.RemoveBrackets(BaseFilename);

            String FullDirectory;

            //TODO: CoalesceSettings and System.IO
//            if (CoalesceSettings.SubDirectoryLength > 0 && CoalesceSettings.SubDirectoryLength < BaseFilename.length())
//                FullDirectory = Path.Combine(CoalesceSettings.BinaryFileStoreBasePath, BaseFilename.Substring(0, CoalesceSettings.SubDirectoryLength));
//            else
//                FullDirectory = CoalesceSettings.BinaryFileStoreBasePath;
//
//            if (!(System.IO.Directory.Exists(FullDirectory)) )
//                System.IO.Directory.CreateDirectory(FullDirectory);
//
//            return Path.Combine(FullDirectory, BaseFilename) + "_thumb.jpg";
            return "";

        }else{
            // return Empty String
            return "";
        }
    }
    
    //readonly
    public String CoalesceFilenameWithLastModifiedTag;
    public String GetCoalesceFilenameWithLastModifiedTag(){
        try{

        	//TODO: verify lastmodifiedticks and filename are correct.
        	String FullPath = this.CoalesceFullFilename;
            File theFile = new File(FullPath);
            //long lastModifiedTicks = IO.File.GetLastWriteTime(FullPath).Ticks;
            long lastModifiedTicks = theFile.lastModified();
//          int idx = FullPath.replaceAll("\\", "/").lastIndexOf("/");
//          return idx >= 0 ? FullPath.substring(idx + 1) : FullPath;
          String fileName = this.CoalesceFullFilename.substring(this.CoalesceFullFilename.replaceAll("\\", "/").lastIndexOf("/"));
            
          //return IO.Path.GetFileName(this.CoalesceFullFilename) + "?" + lastModifiedTicks;
          return fileName + "?" + Long.toString(lastModifiedTicks);
          //return FullPath + "?" + Long.toString(lastModifiedTicks);

        }catch(Exception ex){

            return this.CoalesceFilename;

        }
    }
    
    //readonly
    public String CoalesceThumbnailFilenameWithLastModifiedTag;
    public String GetCoalesceThumbnailFilenameWithLastModifiedTag(){
        try{

        	//TODO: verify lastmodifiedticks and filename are correct.
        	String FullPath = this.CoalesceFullThumbnailFilename;
            String fileName = this.CoalesceFullFilename.substring(this.CoalesceFullFilename.replaceAll("\\", "/").lastIndexOf("/"));
            File theFile = new File(FullPath);
            //TODO: Ticks, IO... ticks may be ok.
            //long lastModifiedTicks = IO.File.GetLastWriteTime(FullPath).Ticks;
            long lastModifiedTicks = theFile.lastModified();

            //return IO.Path.GetFileName(FullPath) + "?" + lastModifiedTicks;
            return fileName + "?" + Long.toString(lastModifiedTicks);

        }catch(Exception ex){

            return this.CoalesceThumbnailFilename;

        }
    }
    
    //readonly
    public String CoalesceFilename;
    public String GetCoalesceFilename(){
    	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
        if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.FileType) {
            CallResult rst;

            String BaseFilename = this.GetKey();
            //TODO: GUIDHelper
            //rst = GUIDHelper.RemoveBrackets(BaseFilename);

            return BaseFilename + "." + this.Extension;
        }else{
            // return Empty String
            return "";
        }
    }
    
    //readonly
    public String CoalesceThumbnailFilename;
    public String GetCoalesceThumbnailFilename(){
    	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
        if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.FileType) {
            CallResult rst;

            String  BaseFilename = this.GetKey();
            //TODO: GUIDHelper
            //rst = GUIDHelper.RemoveBrackets(BaseFilename);

            return BaseFilename + "_thumb.jpg";
        }else{
            // return Empty String
            return "";
        }
    }
    
    //readonly
    public ArrayList<CoalesceFieldHistory> History;
    public ArrayList<CoalesceFieldHistory> GetHistory(){
    	ArrayList<CoalesceFieldHistory> HistoryObjectList = new ArrayList<CoalesceFieldHistory>();
        //for(Map<String, ICoalesceDataObject> ChildObject : this.GetChildDataObjects()){
    	for (Map.Entry<String, ICoalesceDataObject> ChildObject : this.GetChildDataObjects().entrySet()) {
            //if (TypeOf ChildObject.Value == CoalesceFieldHistory) {
    		if (ChildObject.getValue().GetObjectType() == "CoalesceFieldHistory"){
                HistoryObjectList.add((CoalesceFieldHistory) ChildObject.getValue());
            }

        }

        //return HistoryObjectList;
        return History;
    }
    
    public Object Data;
    public Object GetData(){
    	//TODO: GeocoordinateType, GeocoordinateListType, DocumentProperties types
    	Object value = null;
    	CoalesceFieldDefinition cfd = new CoalesceFieldDefinition();
    	Object var;
        switch (cfd.GetCoalesceFieldDataTypeForCoalesceType(this.GetDataType())){
            case StringType:
            case UriType:
                var = "";
                value = var;
                break;
            case DateTimeType:
            	var = new Date();
                value = var;
                break;
            case BinaryType:
            	byte[] bytevar = {};
            	var = bytevar;
                value = var;
                break;
            case BooleanType:
            	Boolean boolvar = null;
            	var = boolvar;
                value = var;
                break;
            case IntegerType:
                int ivar = 0;
                var = ivar;
                value = var;
                break;
            case GuidType:
                UUID uidvar = null;
                var = uidvar;
                value = var;
                break;
//            case GeocoordinateType:
//            	Geolocation geocvar = new Geolocation();
//            	var = geocvar;
//                value = var;
//                break;
//            case GeocoordinateListType:
//            	ArrayList<Geolocation> geolvar = new ArrayList<Geolocation>();
//            	var = geolvar;
//                Data = var;
//                break;
//            case FileType:
//            	DocumentProperties dpvar = new DocumentProperties;
//            	var = dpvar;
//                value = var;
//                break;
        }

        GetTypedValue(value);

        return value;
    }
//    public Object GetData(){
//    	Object value = null;
//    	CoalesceFieldDefinition cfd = new CoalesceFieldDefinition();
//        switch (cfd.GetCoalesceFieldDataTypeForCoalesceType(this.GetDataType())){
//            case StringType:
//            case UriType:
//                String var = "";
//                value = var;
//                break;
//            case DateTimeType:
//            	Date var = new Date();
//                value = var;
//                break;
//            case BinaryType:
//            	Byte[] var = {};
//                value = var;
//                break;
//            case BooleanType:
//            	Boolean var;
//                value = var;
//                break;
//            case IntegerType:
//                int var;
//                value = var;
//                break;
//            case GuidType:
//                UUID var;
//                value = var;
//                break;
//            case GeocoordinateType:
//            	Geolocation var = new Geolocation();
//                value = var;
//                break;
//            case GeocoordinateListType:
//            	List(Geolocation) var = new List(Of Geolocation);
//                Data = var;
//                break;
//            case FileType:
//            	DocumentProperties var = new DocumentProperties;
//                value = var;
//                break;
//        }
//
//        GetTypedValue(value);
//
//        return value;
//    }
    public void SetData(Object value){
        SetTypedValue(value.toString());
    }


    // -----------------------------------------------------------------------// 
    // public Methods
    // -----------------------------------------------------------------------// 

    public CallResult ToXml(String Xml){
        try{
            // Examine XmlNode
            if (this._DataObjectNode != null) {
                // Get Xml
            	//TODO: Confirm getTextContent() is same as VB's OuterXml
                Xml = this.GetDataObjectDocument().getTextContent(); //.OuterXml;
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

    public CallResult SetTypedValue(String Value) {
        try{
        	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.StringType) {
                // Set
                this.Value = Value;

                // return Success
                return CallResult.successCallResult;
            }else{
                // return Failed; Type Mismatch
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetTypedValue(UUID Value){ 
        try{
        	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.GuidType) {
                // Set
            	//TODO: GUIDHelper
                //this.Value = GUIDHelper.GetGuidString(Value);

                // return Success
                return CallResult.successCallResult;
            }else{
                // return Failed; Type Mismatch
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetTypedValue(Date Value){
        try{
        	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.DateTimeType) {
                // Set
            	this.Value = DateTimeHelper.ToXmlDateTimeUTC(Value);

                // return Success
                return CallResult.successCallResult;
            }else{
                // return Failed; Type Mismatch
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetTypedValue(boolean Value) {
        try{
        	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.BooleanType) {
                // Set
                this.Value = String.valueOf(Value);

                // return Success
                return CallResult.successCallResult;
            }else{
                // return Failed; Type Mismatch
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetTypedValue(int Value){ 
        try{
        	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.IntegerType) {
                // Set
                this.Value = String.valueOf(Value);

                // return Success
                return CallResult.successCallResult;
            }else{
                // return Failed; Type Mismatch
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    //TODO: Microsoft.SqlServer.Types.SqlGeography
//    public CallResult SetTypedValue(Microsoft.SqlServer.Types.SqlGeography Value){
//        try{
//        	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
//            if ((CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.GeocoordinateType) ||
//               (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.GeocoordinateListType)) {
//
//                // Check Spatial Reference Identifier
//                if (Value.STSrid = 4326) {
//                    // Set
//                    this.Value = String.valueOf(Value); //Value.ToString;  // ToString returns the OGC WKT representation.  http://msdn.microsoft.com/en-us/library/microsoft.sqlserver.types.sqlgeography.tostring.aspx
//
//                    // return Success
//                    return CallResult.successCallResult;
//                }else{
//                    // return Failed
//                    return new CallResult(CallResults.FAILED, "Invalid Spatial Reference Identifier (SRID). Coalesce requires SRID 4326 which is WGS 84.", this);
//                }
//            }else{
//                // return Failed; Type Mismatch
//                return new CallResult(CallResults.FAILED, "Type mismatch", this);
//            }
//
//        }catch(Exception ex){
//            // return Failed Error
//            return new CallResult(CallResults.FAILED_ERROR, ex, this);
//        }
//    }

    //TODO: Geolocation
//    public CallResult SetTypedValue(Geolocation GeoLocation){
//        try{
//        	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
//            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.GeocoordinateType) {
//                // Set
//            	Microsoft.SqlServer.Types.SqlGeographyBuilder Builder = new Microsoft.SqlServer.Types.SqlGeographyBuilder;
//                Builder.SetSrid(4326); // WGS 84
//                Builder.BeginGeography(Microsoft.SqlServer.Types.OpenGisGeographyType.Point);
//                Builder.BeginFigure(GeoLocation.Latitude, GeoLocation.Longitude);
//                Builder.EndFigure();
//                Builder.EndGeography();
//
//                // Call on Overload
//                return this.SetTypedValue(Builder.ConstructedGeography);
//
//            }else{
//                // return Failed; Type Mismatch
//                return new CallResult(CallResults.FAILED, "Type mismatch", this);
//            }
//
//        }catch(Exception ex){
//            // return Failed Error
//            return new CallResult(CallResults.FAILED_ERROR, ex, this);
//        }
//    }

    //TODO: GeocoordinateList 
//    public CallResult SetTypedValue(List(Of Geolocation) GeoLocations){
//        try{
//        	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
//            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.GeocoordinateListType) {
//                // Set
//            	Microsoft.SqlServer.Types.SqlGeographyBuilder Builder = new Microsoft.SqlServer.Types.SqlGeographyBuilder;
//                Builder.SetSrid(4326); // WGS 84
//                Builder.BeginGeography(Microsoft.SqlServer.Types.OpenGisGeographyType.MultiPoint);
//                for(Geolocation Geolocation : GeoLocations){
//                    Builder.BeginGeography(Microsoft.SqlServer.Types.OpenGisGeographyType.Point);
//                    Builder.BeginFigure(Geolocation.Latitude, Geolocation.Longitude);
//                    Builder.EndFigure();
//                    Builder.EndGeography();
//                }
//                Builder.EndGeography();
//
//                // Call on Overload
//                return this.SetTypedValue(Builder.ConstructedGeography);
//
//        }else{
//                // return Failed; Type Mismatch
//                return new CallResult(CallResults.FAILED, "Type mismatch", this);
//            }
//
//        }catch(Exception ex){
//            // return Failed Error
//            return new CallResult(CallResults.FAILED_ERROR, ex, this);
//        }
//    }

    public CallResult SetTypedValue(byte[] DataBytes) {
        try{
        	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.BinaryType) {
                // Set
            	//TODO: make sure the string conversion is correct
            	//this.Value = Convert.ToBase64String(DataBytes);
            	this.Value = DataBytes.toString();
                this.Size = DataBytes.length;

                // return Success
                return CallResult.successCallResult;
            }else{
                // return Failed; Type Mismatch
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetTypedValue(byte[] DataBytes, String Filename, String Extension, String MimeType) {
        try{
            // Set Bytes
        	//TODO: make sure the string conversion is correct
            //this.Value = Convert.ToBase64String(DataBytes);
        	this.Value = DataBytes.toString();
            this.Filename = Filename;
            this.Extension = Extension;
            this.MimeType = MimeType;
            this.Size = DataBytes.length;

            // return Success
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetTypedValue(String Filename, String Extension, String MimeType, String Hash){
        try{
            // Set Bytes

            this.Filename = Filename;
            this.Extension = Extension;
            this.MimeType = MimeType;
            this.Hash = Hash;


            // return Success
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    //TODO: DocumentProperties
//    public CallResult SetTypedValue(Byte[] DataBytes, DocumentProperties DocProps){
//        try{
//        	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
//            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.FileType) {
//
//                // Set Bytes
//            	//TODO: make sure the string conversion is correct
//  			//this.Value = Convert.ToBase64String(DataBytes);
//            	  this.Value = DataBytes.toString();
//                this.Filename = DocProps.Filename;
//                this.Extension = DocProps.Extension;
//                this.MimeType = DocProps.MimeType;
//                this.Size = DataBytes.length;
//
//                // return Success
//                return CallResult.successCallResult;
//
//            }else{
//                // return Failed; Type Mismatch
//                return new CallResult(CallResults.FAILED, "Type mismatch", this);
//            }
//
//        }catch(Exception ex){
//            // return Failed Error
//            return new CallResult(CallResults.FAILED_ERROR, ex, this);
//        }
//    }

    //TODO: DocumentProperties
//    public CallResult SetTypedValue(DocumentProperties DocProps) { 
//        try{
//        	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
//            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.FileType) {
//                // Does File Exist?
//                if (File.Exists(DocProps.FullFilename)) {
//                    // Read Bytes
//                	Byte[]  FileBytes = File.ReadAllBytes(DocProps.FullFilename);
//
//                    // Set Bytes
//                	//TODO: make sure the string conversion is correct
//  				//this.Value = Convert.ToBase64String(FileBytes);
//                	  this.Value = FileBytes.toString();
//                    this.Filename = DocProps.Filename;
//                    this.Extension = DocProps.Extension;
//                    this.MimeType = DocProps.MimeType;
//                    this.Size = FileBytes.length;
//
//                    // return Success
//                    return CallResult.successCallResult;
//                }else{
//                    // return Failed; Type Mismatch
//                    return new CallResult(CallResults.FAILED, "File not found", this);
//                }
//            }else{
//                // return Failed; Type Mismatch
//                return new CallResult(CallResults.FAILED, "Type mismatch", this);
//            }
//
//        }catch(Exception ex){
//            // return Failed Error
//            return new CallResult(CallResults.FAILED_ERROR, ex, this);
//        }
//    }

    public CallResult GetTypedValue(Object Value) {
    	
    	//TODO: Geolocation and GeocoordinateList classes
        try{
//        	switch(Value.getClass()){
//        	}
        	if (Value.getClass().equals(String.class)){
        		return GetTypedValue((String) Value);
        	}else if (Value.getClass().equals(Boolean.class)){
        		return GetTypedValue((Boolean) Value);
        	}else if (Value.getClass().equals(Date.class)){
        		return GetTypedValue((Date) Value);
        	}else if (Value.getClass().equals(Integer.class)){
        		return GetTypedValue((Integer) Value);
        	}else if (Value.getClass().equals(byte.class)){
        		return GetTypedValue((byte) Value);
        	}else if (Value.getClass().equals(UUID.class)){
        		return GetTypedValue((UUID) Value);
//        	}else if (Value.getClass().equals(Geolocation.class)){
//        		return GetTypedValue((Geolocation) Value);
//        	}else if (Value.getClass().equals(GeocoordinateList.class)){
//        		return GetTypedValue((GeocoordinateList) Value);
        	
            }else{
                // return Failed; Type Mismatch
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult GetTypedValue(UUID Value) {
        try{
        	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.GuidType) {
                // Get
            	//TODO: GUIDHelper (and remove failed callresults
//                if (GUIDHelper.IsValid(this.Value).IsSuccess) {
//                    // Set return Value
//                    return GUIDHelper.GetGuid(this.Value, Value);
//                }else{
//                    // return Failed; Type Mismatch
//                    return new CallResult(CallResults.FAILED, "Type mismatch", this);
            	return new CallResult(CallResults.FAILED, "Type mismatch", this);
//                }
            }else{
                // return Failed; Type Mismatch
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult GetTypedValue(Date Value) {
        try{
        	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.DateTimeType) {
                // Get
            	try{
            		Date.parse(this.GetValue());
            		return DateTimeHelper.ConvertDateToGMT(this.GetValue(), Value);
            	}catch(Exception ex){
                    return new CallResult(CallResults.FAILED, "Type mismatch", this);
            	}
            	
//                Date.tryParse(this.Value, Value);
//                // return Parsed Date (Note: tryParse returns the date as local, even if
//                // the string is a UTC (Z) datetime string.  This ensures the
//                // date returned is UTC.)
//                Value = Value.ToUniversalTime();
//                // return Success
//                return CallResult.successCallResult;
                
                }else{
                // return Failed; Type Mismatch
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult GetTypedValue(boolean Value) {
        try{
        	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.BooleanType) {
                // Get
            	try{
            		Value = Boolean.parseBoolean(this.GetValue());
                    return CallResult.successCallResult;
            	}catch(Exception ex){
                    // return Failed; Type Mismatch
                    return new CallResult(CallResults.FAILED, "Type mismatch", this);
            	}
            	
//                if (Boolean.tryParse(this.Value, Value) == true) {
//                    // return Success
//                    return CallResult.successCallResult;
//                }else{
//                    // return Failed; Type Mismatch
//                    return new CallResult(CallResults.FAILED, "Type mismatch", this);
//                }
                }else{
                // return Failed; Type Mismatch
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult GetTypedValue(int Value) {
        try{
        	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.IntegerType) {
                // Get
            	try{
            		Value = Integer.parseInt(this.GetValue());
            		return CallResult.successCallResult;
            	}catch(Exception ex){
            		return new CallResult(CallResults.FAILED, "Type mismatch", this);
            	}
            	
//                if (Integer.tryParse(this.Value, Value) == true) {
//                    // return Success
//                    return CallResult.successCallResult;
//                }else{
//                    // return Failed; Type Mismatch
//                    return new CallResult(CallResults.FAILED, "Type mismatch", this);
//                }
                }else{
                // return Failed; Type Mismatch
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    //TODO: Geolocation type
//    public CallResult GetTypedValue(Geolocation GeoLocation) {
//        try{
//        	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
//            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.GeocoordinateType) {
//                // Basic Check
//                if (!(this.Value.StartsWith("POINT"))) {
//                    // return Failed; Type Mismatch
//                    return new CallResult(CallResults.FAILED, "Type mismatch", this);
//                }else{
//                    // Get
//                	Microsoft.SqlServer.Types.SqlGeography Geography = null;
//                    Geography = Microsoft.SqlServer.Types.SqlGeography.STPointFromText(new System.Data.SqlTypes.SqlString(this.Value), 4326);
//                    if (GeoLocation == null) {
//                        GeoLocation = new Geolocation();
//                    }
//                    GeoLocation.Latitude = Geography.Lat;
//                    GeoLocation.Longitude = Geography.Long;
//
//                    // return Success
//                    return CallResult.successCallResult;
//                }
//            }else{
//                // return Failed; Type Mismatch
//                return new CallResult(CallResults.FAILED, "Type mismatch", this);
//            }
//
//        }catch(Exception ex){
//            // return Failed Error
//            return new CallResult(CallResults.FAILED_ERROR, ex, this);
//        }
//    }

    //TODO: GeocoordinateList type
//    public CallResult GetTypedValue(ArrayList<Geolocation> GeoLocations){
//        try{
//        	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
//            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.GeocoordinateListType) {
//                // Basic Check
//                if (!(this.Value.StartsWith("MULTIPOINT"))) {
//                    // return Failed; Type Mismatch
//                    return new CallResult(CallResults.FAILED, "Type mismatch", this);
//                }else{
//                    // Get
//                	ArrayList<Geolocation> TempGeoLocations = new ArrayList<Geolocation>();
//                	Microsoft.SqlServer.Types.SqlGeography Geography = null;
//
//                    Geography = Microsoft.SqlServer.Types.SqlGeography.STMPointFromText(new System.Data.SqlTypes.SqlString(this.Value), 4326);
//                    Dim geoPointCount = Geography.STNumGeometries();
//                    for(int geoPointIndex = 1; geoPointIndex <= geoPointCount; geoPointCount++){
//                    	Microsoft.SqlServer.Types.SqlGeography geoPoint = Geography.STGeometryN(geoPointIndex);
//                        TempGeoLocations.Add(new Geolocation(geoPoint.Lat, geoPoint.Long));
//                    }
//
//                    // All points were valid so return the locations array
//                    GeoLocations = TempGeoLocations;
//
//                    // return Success
//                    return CallResult.successCallResult;
//                }
//                // Get
//            }else{
//                // return Failed; Type Mismatch
//                return new CallResult(CallResults.FAILED, "Type mismatch", this);
//            }
//
//        }catch(Exception ex){
//            // return Failed Error
//            return new CallResult(CallResults.FAILED_ERROR, ex, this);
//        }
//    }

    public CallResult GetTypedValue(byte[] Bytes) {
        try{
        	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.BinaryType) {
                // Basic Check
                if (this.Value.length() > 0) {
                    // return Byte Array
                	//TODO: Verify that this is good for "FromBase64String" 
                    //Bytes = Convert.FromBase64String(this.Value);
                	Bytes = this.GetValue().getBytes();

                    // return Success
                    return CallResult.successCallResult;
                }else{
                    // return Nothing
                    Bytes = null;

                    return new CallResult(CallResults.FAILED, "No data", this);
                }
            }else{
                // return Failed; Type Mismatch
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult GetTypedValue(String Value) {
        try{
        	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.StringType) {
                // Set Value
                if (this.Value == null) {
                    Value = "";
                }else{
                    Value = this.Value;
                }

                // return Success
                return CallResult.successCallResult;
            }else{
                // return Failed; Type Mismatch
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult GetHistoryRecord(String HistoryKey, CoalesceFieldHistory HistoryRecord) {

        try{
        	
            //HistoryRecord = this.GetChildDataObjects().item(HistoryKey);
        		HistoryRecord = (CoalesceFieldHistory) this.GetChildDataObjects().get(HistoryKey);
	        
            // return Success
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }

    }

    // -----------------------------------------------------------------------// 
    // protected Methods
    // -----------------------------------------------------------------------// 

    protected CallResult Change(String AttributeName, String Value) {
        try{
            CallResult rst;

            // Does the new value differ from the existing?
            if (_XmlHelper.GetAttribute(this._DataObjectNode, AttributeName) != Value) {

                // Yes; should we create a FieldHistory entry to reflect the change?
                // We create FieldHistory entry if History is not Suspended; OR if DataType is binary; OR if DateCreated=LastModified and Value is unset
                if (this.SuspendHistory == false) {

                    switch (this.DataType.toUpperCase()){

                        case "BINARY":
                        case "FILE":
                            // Don't Create History Entry for these types
                        	break;
                        default:
                            // Does LastModified = DateCreated?
                            if (this.LastModified.compareTo(this.DateCreated) != 0) {
                                // No; Create History Entry
                            	CoalesceFieldHistory FieldHistory = null;
                            	CoalesceFieldHistory NewFieldHistory = new CoalesceFieldHistory();
                                rst = NewFieldHistory.Create(this, FieldHistory);
                                if (!(rst.getIsSuccess())) return rst;

                                this.PreviousHistoryKey = FieldHistory.GetKey();
                            }

                    }

                }

                // Change Attribute Value
                rst = _XmlHelper.SetAttribute(this._DataObjectDocument, this._DataObjectNode, AttributeName, Value);
                if (!(rst.getIsSuccess())) return rst;

                // Set LastModified
                Date UTCDate = new Date();
                DateTimeHelper.ConvertDateToGMT(UTCDate);
                
                this.LastModified = UTCDate;

            }

            // return Success
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult Change(String Value, String Marking, String User, String IP) {
        try{
            CallResult rst;

            // Does the new value differ from the existing?
            if ( (_XmlHelper.GetAttribute(this._DataObjectNode, "value") != Value) ||
                (_XmlHelper.GetAttribute(this._DataObjectNode, "classificationmarking") != Marking) ) {

                // Yes; should we create a FieldHistory entry to reflect the change?
                // We create FieldHistory entry if History is not Suspended; OR if DataType is binary; OR if DateCreated=LastModified and Value is unset
                if (this.SuspendHistory == false) {

                    // Does LastModified = DateCreated?
                    if (this.LastModified.compareTo(this.DateCreated) != 0) {
                        // No; Create History Entry
                    	//TODO: Something just feels wrong about declaring one CoalesceFieldHistory to create another.
                    	CoalesceFieldHistory FieldHistory = null;
                    	CoalesceFieldHistory NewFieldHistory = new CoalesceFieldHistory();
                        rst = NewFieldHistory.Create(this, FieldHistory);
                        if (!(rst.getIsSuccess())) return rst;

                        this.PreviousHistoryKey = FieldHistory.GetKey();
                    }
                }

            	Date UTCDate = new Date();
            	DateTimeHelper.ConvertDateToGMT(Value, UTCDate);
            	
                // Change Values
            	CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
                if ( CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.DateTimeType &&
                    !(Value.trim() == "" || Value == null) ) {
                    // If a date field and not clearing the value
                    rst = _XmlHelper.SetAttributeAsDate(this._DataObjectDocument, this._DataObjectNode, "value", UTCDate);
                }else{
                    rst = _XmlHelper.SetAttribute(this._DataObjectDocument, this._DataObjectNode, "value", Value);
                }

                rst = _XmlHelper.SetAttribute(this._DataObjectDocument, this._DataObjectNode, "classificationmarking", Marking);
                rst = _XmlHelper.SetAttribute(this._DataObjectDocument, this._DataObjectNode, "modifiedby", User);
                rst = _XmlHelper.SetAttribute(this._DataObjectDocument, this._DataObjectNode, "modifiedbyip", IP);
                if (!(rst.getIsSuccess())) return rst;

                // Set LastModified
            	UTCDate = new Date();
            	DateTimeHelper.ConvertDateToGMT(UTCDate);
                this.LastModified = UTCDate;

            }

            // return Success
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    protected CallResult ChangeDate(String AttributeName, Date Value) {
        try{
            CallResult rst;

            //TODO: XMLHelper
            // Does the new value differ from the existing?
//            if (_XmlHelper.GetAttribute(this._DataObjectNode, AttributeName) != Value) {

                // Yes; should we create a FieldHistory entry to reflect the change?
                // We create FieldHistory entry if History is not Suspended; OR if DataType is binary; OR if DateCreated=LastModified and Value is unset
                if (this.SuspendHistory == false) {

                    switch (this.DataType.toUpperCase()){

                        case "BINARY":
                        case "FILE":
                            // Don't Create History Entry for these types
                        	break;
                        default:
                            // Does LastModified = DateCreated?
                            if (this.LastModified.compareTo(this.DateCreated) != 0) {
                                // No; Create History Entry
                            	CoalesceFieldHistory FieldHistory = null;
                            	CoalesceFieldHistory CFH = new CoalesceFieldHistory();
                                rst = CFH.Create(this, FieldHistory);
                                if (!(rst.getIsSuccess())) return rst;
                            }
                            break;
                    }

                }

                // Change Attribute Value using the Date Method
                rst = _XmlHelper.SetAttributeAsDate(this._DataObjectDocument, this._DataObjectNode, AttributeName, Value);
                if (!(rst.getIsSuccess())) return rst;

                // Set LastModified
            	Date UTCDate = new Date();
            	DateTimeHelper.ConvertDateToGMT(UTCDate);
                this.LastModified = UTCDate;

//            }

            // return Success
            return CallResult.successCallResult;

        }catch(Exception ex){
            // return Failed Error
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

}
