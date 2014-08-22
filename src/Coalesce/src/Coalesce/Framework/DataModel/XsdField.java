package Coalesce.Framework.DataModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;
import Coalesce.Common.Helpers.GUIDHelper;
import Coalesce.Common.Helpers.JodaDateTimeHelper;
import Coalesce.Common.Helpers.StringHelper;
import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Framework.DataModel.Entity.Section.Recordset.Record.Field;
import Coalesce.Framework.DataModel.Entity.Section.Recordset.Record.Field.Fieldhistory;

public class XsdField extends XsdDataObject {

    //-----------------------------------------------------------------------//
    // protected Member Variables
    //-----------------------------------------------------------------------//

    private static String MODULE = "Coalesce.Framework.DataModel.XsdField";

    protected Field _entityField;
    private Boolean _suspendHistory = false;

    //-----------------------------------------------------------------------//
    // Factory and Initialization
    //-----------------------------------------------------------------------//
    
    public static CallResult Create(XsdRecord parent, XsdField newField, XsdFieldDefinition fieldDefinition)
    {
        try {
            CallResult rst;

            Field newEntityField = new Field();
            parent.GetEntityFields().add(newEntityField);

            rst = newField.Initialize(parent, newEntityField);
            if (!rst.getIsSuccess()) return rst;

            newField.SetSuspendHistory(true);

            newField.SetName(fieldDefinition.GetName());
            newField.SetDataType(fieldDefinition.GetDataType());
            newField.SetValue(fieldDefinition.GetDefaultValue());
            newField.SetClassificationMarking(fieldDefinition.GetDefaultClassificationMarking());
            newField.SetLabel(fieldDefinition.GetLabel());

            newField.SetSuspendHistory(false);
            
            newField.SetNoIndex(true);

            // Boolean Type? If so then default initial value to false.
            if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(fieldDefinition.GetDataType()) == ECoalesceFieldDataTypes.BooleanType) {
                newField.SetValue("false");
            }

            // Add to Parent's Child Collection            
            if (!(parent._childDataObjects.containsKey(newField.GetKey()))) {
                parent._childDataObjects.put(newField.GetKey(), newField);
            }

            return CallResult.successCallResult;

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, XsdField.MODULE);
        }
    }

    public CallResult Initialize(XsdRecord parent, Field field)
    {
        try {

            // Set References
            _parent = parent;

            _entityField = field;

            if (_entityField.fieldhistory == null) _entityField.fieldhistory = new ArrayList<Fieldhistory>();

            for (Fieldhistory entityFieldHistory : _entityField.fieldhistory) {

                XsdFieldHistory fieldHistory = new XsdFieldHistory();
                fieldHistory.Initialize(this, entityFieldHistory);

                // Add to Child Collection
                _childDataObjects.put(fieldHistory.GetKey(), fieldHistory);
            }

            return CallResult.successCallResult;

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    // -----------------------------------------------------------------------// 
    // public Properties
    // -----------------------------------------------------------------------// 

    protected String GetObjectKey()
    {
        return _entityField.getKey();
    }

    public void SetKey(String value)
    {
        _entityField.setKey(value);
    }

    public String GetName()
    {
        return _entityField.getName();
    }

    public void SetName(String value)
    {
        _entityField.setName(value);
    }

    public String GetValue()
    {
        return _entityField.getValue();
    }

    public void SetValue(String value)
    {
        String oldValue = _entityField.getValue();
        
        _entityField.setValue(value);
        SetChanged(oldValue, value);
    }

    public String GetValueWithMarking()
    {
        // TODO: Add Common.ClassificationMarking
        String val = GetValue();
        //Marking mrk = new Marking(GetClassificationMarking());
        return GetClassificationMarking() + " " + val;
    }

    // overrides
    public String ToString()
    {
        return GetValueWithMarking();
    }

    public String GetDataType()
    {
        return _entityField.getDatatype();
    }

    public void SetDataType(String value)
    {
        _entityField.setDatatype(value);
    }

    public String GetLabel()
    {
        return _entityField.getLabel();
    }

    public void SetLabel(String value)
    {
        _entityField.setLabel(value);
    }

    public Integer GetSize()
    {
        try {
            return new Integer(_entityField.getSize());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void SetSize(Integer value)
    {
        _entityField.setSize(value.toString());
    }

    public String GetModifiedBy()
    {
        return _entityField.getModifiedby();
    }

    public void SetModifiedBy(String value)
    {
        _entityField.setModifiedby(value);
    }

    public String GetModifiedByIP()
    {
        return _entityField.getModifiedbyip();
    }

    public void SetModifiedByIP(String value)
    {
        _entityField.setModifiedbyip(value);
    }

    public String GetClassificationMarking()
    {
        return _entityField.getClassificationmarking();
    }

    public void SetClassificationMarking(String value)
    {
        String oldValue = _entityField.getClassificationmarking();
        _entityField.setClassificationmarking(value);
        SetChanged(oldValue, value);
    }

    public String GetPortionMarking()
    {
        // TODO: Common.ClassificationMarking
        // Marking mrk = new Marking(this.ClassificationMarking);
        // return mrk.ToPortionString;
        return GetClassificationMarking();
    }

    public String GetPreviousHistoryKey()
    {
        String prevHistKey = _entityField.getPrevioushistorykey();
        if (prevHistKey.equals("")) {
            return "00000000-0000-0000-0000-000000000000";
        } else {
            return prevHistKey;
        }
    }

    public void SetPreviousHistoryKey(String value)
    {
        _entityField.setPrevioushistorykey(value);
    }

    public String GetFilename()
    {
        return _entityField.getFilename();
    }

    public void SetFilename(String value)
    {
        String oldFilename = _entityField.getFilename();
        
        _entityField.setFilename(value);
        SetChanged(oldFilename, value);
    }

    public String GetExtension()
    {
        return _entityField.getExtension();
    }

    public void SetExtension(String value)
    {
        String oldExtension = _entityField.getExtension();
        
        _entityField.setExtension(value.replace(".", ""));
        SetChanged(oldExtension, value);
    }

    public String GetMimeType()
    {
        return _entityField.getMimetype();
    }

    public void SetMimeType(String value)
    {
        _entityField.setMimetype(value);
    }

    public String GetHash()
    {
        return _entityField.getHash();
    }

    public void SetHash(String value)
    {
        String oldHash = _entityField.getHash();
        
        _entityField.setHash(value);
        SetChanged(oldHash, value);
    }

    /*
     * public String GetInputLang(){ return _entityField.getInputlang(); }
     * public void SetInputLang(String value){ _entityField.setInputlang(value);
     * }
     */

    public boolean GetSuspendHistory()
    {
        return this._suspendHistory;
    }

    public void SetSuspendHistory(boolean value)
    {
        this._suspendHistory = value;
    }

    public String GetCoalesceFullFilename()
    {
    	CallResult rst;

        if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.FileType) {

            String baseFilename = GetKey();
            baseFilename = GUIDHelper.RemoveBrackets(baseFilename);

            String fullDirectory;

            // TODO: CoalesceSettings and System.IO
            /*
             * if (CoalesceSettings.SubDirectoryLength > 0 &&
             * CoalesceSettings.SubDirectoryLength < baseFilename.length()) {
             * fullDirectory =
             * Path.Combine(CoalesceSettings.BinaryFileStoreBasePath,
             * baseFilename.Substring(0, CoalesceSettings.SubDirectoryLength));
             * } else { fullDirectory =
             * CoalesceSettings.BinaryFileStoreBasePath; }
             * 
             * if ( !(System.IO.Directory.Exists(fullDirectory)) ) {
             * System.IO.Directory.CreateDirectory(fullDirectory);
             * 
             * return Path.Combine(fullDirectory, baseFilename) + "." +
             * GetExtension();
             */

            return "";
        } else {
            return "";
        }
    }

    public String GetCoalesceFullThumbnailFilename()
    {
        CallResult rst;

        if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.FileType) {

            String baseFilename = GetKey();
            baseFilename = GUIDHelper.RemoveBrackets(baseFilename);

            String fullDirectory;

            // TODO: CoalesceSettings and System.IO
            /*
             * if (CoalesceSettings.SubDirectoryLength > 0 &&
             * CoalesceSettings.SubDirectoryLength < baseFilename.length())
             * fullDirectory =
             * Path.Combine(CoalesceSettings.BinaryFileStoreBasePath,
             * baseFilename.Substring(0, CoalesceSettings.SubDirectoryLength));
             * else fullDirectory = CoalesceSettings.BinaryFileStoreBasePath;
             * 
             * if (!(System.IO.Directory.Exists(fullDirectory)) )
             * System.IO.Directory.CreateDirectory(fullDirectory);
             * 
             * return Path.Combine(bullDirectory, baseFilename) + "_thumb.jpg";
             */

            return "";

        } else {
            return "";
        }
    }

    public String GetCoalesceFilenameWithLastModifiedTag()
    {
        try {

            // TODO: verify lastmodifiedticks and filename are correct.
            String fullPath = GetCoalesceFullFilename();
            File theFile = new File(fullPath);
            // long lastModifiedTicks =
            // IO.File.GetLastWriteTime(FullPath).Ticks;
            long lastModifiedTicks = theFile.lastModified();
            // int idx = FullPath.replaceAll("\\", "/").lastIndexOf("/");
            // return idx >= 0 ? FullPath.substring(idx + 1) : FullPath;
            String fileName = fullPath.substring(fullPath.replaceAll("\\", "/").lastIndexOf("/"));

            // return IO.Path.GetFileName(this.CoalesceFullFilename) + "?" +
            // lastModifiedTicks;
            return fileName + "?" + Long.toString(lastModifiedTicks);
            // return FullPath + "?" + Long.toString(lastModifiedTicks);

        } catch (Exception ex) {
            return this.GetCoalesceFilename();
        }
    }

    public String GetCoalesceThumbnailFilenameWithLastModifiedTag()
    {
        try {

            // TODO: verify lastmodifiedticks and filename are correct.
            String fullThumbPath = GetCoalesceFullThumbnailFilename();
            String fullPath = GetCoalesceFullFilename();
            String fileName = fullPath.substring(fullPath.replaceAll("\\", "/").lastIndexOf("/"));
            File theFile = new File(fullThumbPath);
            // TODO: Ticks, IO... ticks may be ok.
            // long lastModifiedTicks =
            // IO.File.GetLastWriteTime(FullPath).Ticks;
            long lastModifiedTicks = theFile.lastModified();

            // return IO.Path.GetFileName(FullPath) + "?" + lastModifiedTicks;
            return fileName + "?" + Long.toString(lastModifiedTicks);

        } catch (Exception ex) {
            return this.GetCoalesceThumbnailFilename();
        }
    }
    
    public String GetCoalesceFilename()
    {
        CallResult rst;

        if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.FileType) {

            String baseFilename = GetKey();
            baseFilename = GUIDHelper.RemoveBrackets(baseFilename);

            return baseFilename + "." + GetExtension();
        } else {
            return "";
        }
    }

    public String GetCoalesceThumbnailFilename()
    {
        CallResult rst;

        if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.FileType) {

            String baseFilename = this.GetKey();
            baseFilename= GUIDHelper.RemoveBrackets(baseFilename);

            return baseFilename + "_thumb.jpg";

        } else {
            return "";
        }
    }

    public ArrayList<XsdFieldHistory> GetHistory()
    {

        ArrayList<XsdFieldHistory> historyList = new ArrayList<XsdFieldHistory>();

        for (Map.Entry<String, XsdDataObject> childObject : _childDataObjects.entrySet()) {
            if (childObject.getValue() instanceof XsdFieldHistory) {
                historyList.add((XsdFieldHistory) childObject.getValue());
            }
        }

        return historyList;
    }

    public Object GetData()
    {

        // TODO: GeocoordinateType, GeocoordinateListType, DocumentProperties
        // types
        Object value = null;
        Object var;
        switch (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType())) {
        case StringType:
        case UriType:
            var = "";
            value = var;
            break;
        case DateTimeType:
            var = new DateTime();
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
        // case GeocoordinateType:
        // Geolocation geocvar = new Geolocation();
        // var = geocvar;
        // value = var;
        // break;
        // case GeocoordinateListType:
        // ArrayList<Geolocation> geolvar = new ArrayList<Geolocation>();
        // var = geolvar;
        // Data = var;
        // break;
        // case FileType:
        // DocumentProperties dpvar = new DocumentProperties;
        // var = dpvar;
        // value = var;
        // break;
        }

        GetTypedValue(value);

        return value;
    }

    public void SetData(Object value)
    {
        SetTypedValue(value.toString());
    }

    public DateTime GetDateCreated()
    {
        try {

            //return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityField.getDatecreated());
            return _entityField.getDatecreated();

        } catch (Exception ex) {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
            return null;
        }
    }

    public CallResult SetDateCreated(DateTime value)
    {
        try {
            //_entityField.setDatecreated(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
            _entityField.setDatecreated(value);
            
            return CallResult.successCallResult;

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public DateTime GetLastModified()
    {
        try {

            //return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityField.getLastmodified());
            return _entityField.getLastmodified();

        } catch (Exception ex) {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
            return null;
        }
    }

    protected CallResult SetObjectLastModified(DateTime value)
    {
        try {
            //_entityField.setLastmodified(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
            _entityField.setLastmodified(value);

            return CallResult.successCallResult;

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    // -----------------------------------------------------------------------//
    // public Methods
    // -----------------------------------------------------------------------//

    public CallResult ToXml(StringBuilder xml)
    {
        try {
            CallResult rst;

            rst = XmlHelper.Serialize(_entityField, xml);

            return rst;

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetTypedValue(String value)
    {
        try {
            if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.StringType) {

                SetValue(value);

                return CallResult.successCallResult;

            } else {
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetTypedValue(UUID value)
    {
        try {
            if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.GuidType) {

                SetValue(GUIDHelper.GetGuidString(value));

                return CallResult.successCallResult;

            } else {
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetTypedValue(DateTime value)
    {
        try {
            if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.DateTimeType) {

                SetValue(JodaDateTimeHelper.ToXmlDateTimeUTC(value));

                return CallResult.successCallResult;

            } else {
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetTypedValue(boolean value)
    {
        try {
            if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.BooleanType) {

                SetValue(String.valueOf(value));

                return CallResult.successCallResult;

            } else {
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetTypedValue(int value)
    {
        try {
            if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.IntegerType) {

                SetValue(String.valueOf(value));

                return CallResult.successCallResult;

            } else {
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    //TODO: Microsoft.SqlServer.Types.SqlGeography
//    public CallResult SetTypedValue(Microsoft.SqlServer.Types.SqlGeography Value){
//        try{
//            CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
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
//            CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
//            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.GeocoordinateType) {
//                // Set
//                Microsoft.SqlServer.Types.SqlGeographyBuilder Builder = new Microsoft.SqlServer.Types.SqlGeographyBuilder;
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
//            CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
//            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.GeocoordinateListType) {
//                // Set
//                Microsoft.SqlServer.Types.SqlGeographyBuilder Builder = new Microsoft.SqlServer.Types.SqlGeographyBuilder;
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

    public CallResult SetTypedValue(byte[] dataBytes)
    {
        try {
            if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.BinaryType) {

                // TODO: make sure the string conversion is correct
                // this.Value = Convert.ToBase64String(DataBytes);
                SetValue(dataBytes.toString());
                SetSize(dataBytes.length);

                return CallResult.successCallResult;

            } else {
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetTypedValue(byte[] dataBytes,
                                    String filename, 
                                    String extension,
                                    String mimeType)
    {
        try {
            // TODO: make sure the string conversion is correct
            // this.Value = Convert.ToBase64String(DataBytes);
            SetValue(dataBytes.toString());
            SetFilename(filename);
            SetExtension(extension);
            SetMimeType(mimeType);
            SetSize(dataBytes.length);

            return CallResult.successCallResult;

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetTypedValue(String filename,
                                    String extension,
                                    String mimeType,
                                    String hash)
    {
        try {
            // Set Bytes

            SetFilename(filename);
            SetExtension(extension);
            SetMimeType(mimeType);
            SetHash(hash);

            return CallResult.successCallResult;

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    //TODO: DocumentProperties
//    public CallResult SetTypedValue(Byte[] DataBytes, DocumentProperties DocProps){
//        try{
//            CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
//            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.FileType) {
//
//                // Set Bytes
//                //TODO: make sure the string conversion is correct
//              //this.Value = Convert.ToBase64String(DataBytes);
//                  this.Value = DataBytes.toString();
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
//            CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
//            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.FileType) {
//                // Does File Exist?
//                if (File.Exists(DocProps.FullFilename)) {
//                    // Read Bytes
//                    Byte[]  FileBytes = File.ReadAllBytes(DocProps.FullFilename);
//
//                    // Set Bytes
//                    //TODO: make sure the string conversion is correct
//                  //this.Value = Convert.ToBase64String(FileBytes);
//                      this.Value = FileBytes.toString();
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

    // TODO: Need to test return type by value
    public CallResult GetTypedValue(Object value)
    {

        // TODO: Geolocation and GeocoordinateList classes
        try {
            // switch(Value.getClass()){
            // }
            if (value instanceof String) {
                return GetTypedValue((String) value);
            } else if (value instanceof Boolean) {
                return GetTypedValue((Boolean) value);
            } else if (value instanceof DateTime) {
                return GetTypedValue((DateTime) value);
            } else if (value instanceof Integer) {
                return GetTypedValue((Integer) value);
            } else if (value instanceof Byte) {
                return GetTypedValue((Byte) value);
            } else if (value instanceof UUID) {
                value = (UUID)GetUuidValue();
                if (value == null) {
                	return new CallResult(CallResults.FAILED, "Invalid object", this);
                } else {
                	return CallResult.successCallResult;
                }
                
                // }else if (Value.getClass().equals(Geolocation.class)){
                // return GetTypedValue((Geolocation) Value);
                // }else if (Value.getClass().equals(GeocoordinateList.class)){
                // return GetTypedValue((GeocoordinateList) Value);

            } else {
                // return Failed; Type Mismatch
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    // TODO: Need to test return type
    private UUID GetUuidValue()
    {
        try {
            if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) != ECoalesceFieldDataTypes.GuidType) {
                CallResult.log(CallResults.FAILED, "Type mismatch", this);
                return null;
            }
                        
            String validUuid = GUIDHelper.IsValid(GetValue());
            
            if (validUuid == null) return null;
            
            UUID value = GUIDHelper.GetGuid(GetValue());

            return value;

        } catch (Exception ex) {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
            return null;
        }
    }

    public CallResult GetTypedValue(DateTime value)
    {
        try {
            if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.DateTimeType) {

                value = JodaDateTimeHelper.FromXmlDateTimeUTC(this.GetValue());
                    
                if (value == null) {
                    return new CallResult(CallResults.FAILED, "Date format error", this);
                } else {
                    return CallResult.successCallResult;
                }
                
            } else {
                CallResult.log(CallResults.FAILED, "Type mismatch", this);
            }
            
            return null;
            
        } catch (Exception ex) {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
            return null;
        }
    }

    public CallResult GetTypedValue(boolean value)
    {
        try {
            if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.BooleanType) {
                try {

                    value = Boolean.parseBoolean(this.GetValue());

                    return CallResult.successCallResult;

                } catch (Exception ex) {
                    return new CallResult(CallResults.FAILED, "Type mismatch", this);
                }
            } else {

                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }
        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult GetTypedValue(int value)
    {
        try {
            if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.IntegerType) {
                try {

                    value = Integer.parseInt(this.GetValue());

                    return CallResult.successCallResult;

                } catch (Exception ex) {
                    return new CallResult(CallResults.FAILED, "Type mismatch", this);
                }
            } else {
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    //TODO: Geolocation type
//    public CallResult GetTypedValue(Geolocation GeoLocation) {
//        try{
//            CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
//            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.GeocoordinateType) {
//                // Basic Check
//                if (!(this.Value.StartsWith("POINT"))) {
//                    // return Failed; Type Mismatch
//                    return new CallResult(CallResults.FAILED, "Type mismatch", this);
//                }else{
//                    // Get
//                    Microsoft.SqlServer.Types.SqlGeography Geography = null;
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
//            CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
//            if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) == ECoalesceFieldDataTypes.GeocoordinateListType) {
//                // Basic Check
//                if (!(this.Value.StartsWith("MULTIPOINT"))) {
//                    // return Failed; Type Mismatch
//                    return new CallResult(CallResults.FAILED, "Type mismatch", this);
//                }else{
//                    // Get
//                    ArrayList<Geolocation> TempGeoLocations = new ArrayList<Geolocation>();
//                    Microsoft.SqlServer.Types.SqlGeography Geography = null;
//
//                    Geography = Microsoft.SqlServer.Types.SqlGeography.STMPointFromText(new System.Data.SqlTypes.SqlString(this.Value), 4326);
//                    Dim geoPointCount = Geography.STNumGeometries();
//                    for(int geoPointIndex = 1; geoPointIndex <= geoPointCount; geoPointCount++){
//                        Microsoft.SqlServer.Types.SqlGeography geoPoint = Geography.STGeometryN(geoPointIndex);
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

    public CallResult GetTypedValue(byte[] bytes)
    {
        try {
            if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.BinaryType) {
                // Basic Check
                if (GetValue().length() > 0) {
                    // return Byte Array
                    // TODO: Verify that this is good for "FromBase64String"
                    // Bytes = Convert.FromBase64String(this.Value);
                    bytes = this.GetValue().getBytes();

                    return CallResult.successCallResult;

                } else {
                    bytes = null;

                    return new CallResult(CallResults.FAILED, "No data", this);
                }
            } else {
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }
        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult GetTypedValue(String value)
    {
        try {
            if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.StringType) {
                if (GetValue() == null) {
                    value = "";
                } else {
                    value = GetValue();
                }

                return CallResult.successCallResult;

            } else {
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult GetHistoryRecord(String historyKey, XsdFieldHistory historyRecord)
    {
        try {
            historyRecord = (XsdFieldHistory)_childDataObjects.get(historyKey);

            return CallResult.successCallResult;

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }

    }

    // -----------------------------------------------------------------------// 
    // protected Methods
    // -----------------------------------------------------------------------// 

    protected CallResult SetChanged(Object oldValue, Object newValue)
    {
        try {
            CallResult rst;

            // Does the new value differ from the existing?
            if (!oldValue.equals(newValue)) {

                // Yes; should we create a FieldHistory entry to reflect the
                // change?
                // We create FieldHistory entry if History is not Suspended; OR
                // if DataType is binary; OR if DateCreated=LastModified and
                // Value is unset
                if (!this.GetSuspendHistory()) {

                    switch (GetDataType().toUpperCase()) {

                    case "BINARY":
                    case "FILE":
                        // Don't Create History Entry for these types
                        break;
                    default:
                        
                        // Does LastModified = DateCreated?
                        if (GetLastModified().compareTo(GetDateCreated()) != 0) {
                            
                            // No; Create History Entry
                            XsdFieldHistory fieldHistory = new XsdFieldHistory();
                            rst = XsdFieldHistory.Create(this, fieldHistory);
                            if (!rst.getIsSuccess()) return rst;

                            SetPreviousHistoryKey(fieldHistory.GetKey());
                        }

                    }

                }

                // Set LastModified
                DateTime utcNow = JodaDateTimeHelper.NowInUtc();
                if (utcNow != null) SetLastModified(utcNow);

            }

            return CallResult.successCallResult;

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult Change(String value, String marking, String user, String ip)
    {
        try {
            CallResult rst;

            // Does the new value differ from the existing?
            if (!(GetValue().equals(value) && GetClassificationMarking().equals(marking))) {

                // Yes; should we create a FieldHistory entry to reflect the
                // change?
                // We create FieldHistory entry if History is not Suspended; OR
                // if DataType is binary; OR if DateCreated=LastModified and
                // Value is unset
                if (!GetSuspendHistory()) {

                    // Does LastModified = DateCreated?
                    if (GetLastModified().compareTo(GetDateCreated()) != 0) {
                        // No; Create History Entry
                        // TODO: Something just feels wrong about declaring one
                        // CoalesceFieldHistory to create another.
                        XsdFieldHistory fieldHistory = new XsdFieldHistory();
                        rst = XsdFieldHistory.Create(this, fieldHistory);
                        if (!rst.getIsSuccess()) return rst;

                        SetPreviousHistoryKey(fieldHistory.GetKey());
                    }
                }

                
                // Change Values
                if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.DateTimeType &&
                    !StringHelper.IsNullOrEmpty(value)) {
                    
                    DateTime valueDate = JodaDateTimeHelper.FromXmlDateTimeUTC(value);

                    SetTypedValue(valueDate);
                    
                } else {
                    SetValue(value);
                }

                SetClassificationMarking(marking);
                SetModifiedBy(user);
                SetModifiedByIP(ip);
                
                // Set LastModified
                DateTime utcNow = JodaDateTimeHelper.NowInUtc();
                if (utcNow != null) SetLastModified(utcNow);

            }

            return CallResult.successCallResult;

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    //TODO: Unused.  Attribute set should be handled by each property.
   /* protected CallResult ChangeDate(String AttributeName, Date Value) {
        try{
            CallResult rst;

            //TODO: XMLHelper
            // Does the new value differ from the existing?
//            if (_XmlHelper.GetAttribute(this._DataObjectNode, AttributeName) != Value) {

                // Yes; should we create a FieldHistory entry to reflect the change?
                // We create FieldHistory entry if History is not Suspended; OR if DataType is binary; OR if DateCreated=LastModified and Value is unset
                if (!GetSuspendHistory()) {

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
    }*/
    
    protected CallResult GetObjectStatus(String status)
    {
        try {
            status = _entityField.getStatus();

            return CallResult.successCallResult;

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    protected CallResult SetObjectStatus(String status)
    {
        try {
            _entityField.setStatus(status);

            return CallResult.successCallResult;

        } catch (Exception ex) {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }
        
    protected List<Fieldhistory> GetEntityFieldHistories() {
        return _entityField.fieldhistory;
    }

}
