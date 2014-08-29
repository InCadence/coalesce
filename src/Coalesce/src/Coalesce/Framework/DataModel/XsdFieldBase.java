package Coalesce.Framework.DataModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.NotImplementedException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.joda.time.DateTime;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;
import Coalesce.Common.Classification.Marking;
import Coalesce.Common.Helpers.FileHelper;
import Coalesce.Common.Helpers.GUIDHelper;
import Coalesce.Common.Helpers.JodaDateTimeHelper;
import Coalesce.Common.Helpers.StringHelper;


public abstract class XsdFieldBase extends XsdDataObject {

    private boolean _suspendHistory = false;
    public abstract String GetValue();

    public abstract void SetValue(String value);
    
    public String GetValueWithMarking()
    {
        // TODO: Test
        String val = GetValue();
        Marking mrk = new Marking(GetClassificationMarking());
        return mrk.ToPortionString() + " " + val;
    }

    public String ToString()
    {
        return GetValueWithMarking();
    }

    public abstract String GetDataType();

    public abstract void SetDataType(String value);

    public abstract String GetLabel();

    public abstract void SetLabel(String value);

    public abstract int GetSize();

    public abstract void SetSize(Integer value);

    public abstract String GetModifiedBy();

    public abstract void SetModifiedBy(String value);

    public abstract String GetModifiedByIP();

    public abstract void SetModifiedByIP(String value);

    public abstract String GetClassificationMarking();

    public void SetClassificationMarking(Marking value) {
        SetClassificationMarking(value.toString());
    }
    
    public abstract void SetClassificationMarking(String value);

    public String GetPortionMarking()
    {
        // TODO: Test
        Marking mrk = new Marking(GetClassificationMarking());
        return mrk.ToPortionString();
    }

    public abstract String GetPreviousHistoryKey();

    public abstract void SetPreviousHistoryKey(String value);

    public abstract String GetFilename();

    public abstract void SetFilename(String value);
    
    public abstract String GetExtension();

    public abstract void SetExtension(String value);
    
    public abstract String GetMimeType();

    public abstract void SetMimeType(String value);

    public abstract String GetHash();

    public abstract void SetHash(String value);
    
    /*
     * public String GetInputLang(){ return _entityField.getInputlang(); } public void SetInputLang(String value){
     * _entityField.setInputlang(value); }
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

        if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) != ECoalesceFieldDataTypes.FileType)
        {
            return "";
        }

        String baseFilename = FileHelper.GetBaseFilenameWithFullDirectoryPathForKey(GetKey());

        return baseFilename + "." + GetExtension();

    }

    public String GetCoalesceFullThumbnailFilename()
    {

        if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) != ECoalesceFieldDataTypes.FileType)
        {
            return "";
        }

        String baseFilename = FileHelper.GetBaseFilenameWithFullDirectoryPathForKey(GetKey());

        return baseFilename + "_thumb.jpg";

    }

    public String GetCoalesceFilenameWithLastModifiedTag()
    {
        try
        {

            // TODO: Test
            String fullPath = GetCoalesceFullFilename();
            File theFile = new File(fullPath);
            long lastModifiedTicks = theFile.lastModified();

            return theFile.getName() + "?" + lastModifiedTicks;

        }
        catch (Exception ex)
        {
            return this.GetCoalesceFilename();
        }
    }

    public String GetCoalesceThumbnailFilenameWithLastModifiedTag()
    {
        try
        {

            // TODO: Test
            String fullThumbPath = GetCoalesceFullThumbnailFilename();
            File theFile = new File(fullThumbPath);
            long lastModifiedTicks = theFile.lastModified();

            return theFile.getName() + "?" + lastModifiedTicks;

        }
        catch (Exception ex)
        {
            return this.GetCoalesceThumbnailFilename();
        }
    }

    public String GetCoalesceFilename()
    {

        if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.FileType)
        {

            String baseFilename = GetKey();
            baseFilename = GUIDHelper.RemoveBrackets(baseFilename);

            return baseFilename + "." + GetExtension();

        }
        else
        {
            return "";
        }
    }

    public String GetCoalesceThumbnailFilename()
    {

        if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.FileType)
        {

            String baseFilename = this.GetKey();
            baseFilename = GUIDHelper.RemoveBrackets(baseFilename);

            return baseFilename + "_thumb.jpg";

        }
        else
        {
            return "";
        }
    }

    public ArrayList<XsdFieldHistory> GetHistory()
    {

        ArrayList<XsdFieldHistory> historyList = new ArrayList<XsdFieldHistory>();

        for (Map.Entry<String, XsdDataObject> childObject : _childDataObjects.entrySet())
        {
            if (childObject.getValue() instanceof XsdFieldHistory)
            {
                historyList.add((XsdFieldHistory) childObject.getValue());
            }
        }

        return historyList;
    }

    public Object GetData()
    {

        // TODO: GeocoordinateType, GeocoordinateListType, DocumentProperties
        // types

        switch (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType())) {
        case StringType:
        case UriType:
            
            return GetValue();
            
        case DateTimeType:
            
            return GetDateTimeValue();
            
        case BinaryType:
            
            return GetByteArrayValue();
            
        case BooleanType:
            
            return GetBooleanValue();
            
        case IntegerType:
            
            return GetIntegerValue();
            
        case GuidType:
            
            return GetGuidValue();
            
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

        throw new NotImplementedException(GetDataType() + " not implemented");
        
    }

    public void SetData(Object value)
    {
        SetTypedValue(value.toString());
    }

    public abstract DateTime GetDateCreated();

    public abstract CallResult SetDateCreated(DateTime value);

    public abstract DateTime GetLastModified();

    protected abstract CallResult SetObjectLastModified(DateTime value);
 
    public CallResult SetTypedValue(String value)
    {
        try
        {
            if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.StringType)
            {

                SetValue(value);

                return CallResult.successCallResult;

            }
            else
            {
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetTypedValue(UUID value)
    {
        try
        {
            if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.GuidType)
            {

                SetValue(GUIDHelper.GetGuidString(value));

                return CallResult.successCallResult;

            }
            else
            {
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetTypedValue(DateTime value)
    {
        try
        {
            if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.DateTimeType)
            {

                SetValue(JodaDateTimeHelper.ToXmlDateTimeUTC(value));

                return CallResult.successCallResult;

            }
            else
            {
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetTypedValue(boolean value)
    {
        try
        {
            if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.BooleanType)
            {

                SetValue(String.valueOf(value));

                return CallResult.successCallResult;

            }
            else
            {
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetTypedValue(int value)
    {
        try
        {
            if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.IntegerType)
            {

                SetValue(String.valueOf(value));

                return CallResult.successCallResult;

            }
            else
            {
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    // TODO: Microsoft.SqlServer.Types.SqlGeography
    // public CallResult SetTypedValue(Microsoft.SqlServer.Types.SqlGeography
    // Value){
    // try{
    // CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
    // if ((CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) ==
    // ECoalesceFieldDataTypes.GeocoordinateType) ||
    // (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) ==
    // ECoalesceFieldDataTypes.GeocoordinateListType)) {
    //
    // // Check Spatial Reference Identifier
    // if (Value.STSrid = 4326) {
    // // Set
    // this.Value = String.valueOf(Value); //Value.ToString; // ToString returns
    // the OGC WKT representation.
    // http://msdn.microsoft.com/en-us/library/microsoft.sqlserver.types.sqlgeography.tostring.aspx
    //
    // // return Success
    // return CallResult.successCallResult;
    // }else{
    // // return Failed
    // return new CallResult(CallResults.FAILED,
    // "Invalid Spatial Reference Identifier (SRID). Coalesce requires SRID 4326 which is WGS 84.",
    // this);
    // }
    // }else{
    // // return Failed; Type Mismatch
    // return new CallResult(CallResults.FAILED, "Type mismatch", this);
    // }
    //
    // }catch(Exception ex){
    // // return Failed Error
    // return new CallResult(CallResults.FAILED_ERROR, ex, this);
    // }
    // }

    // TODO: Geolocation
    // public CallResult SetTypedValue(Geolocation GeoLocation){
    // try{
    // CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
    // if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) ==
    // ECoalesceFieldDataTypes.GeocoordinateType) {
    // // Set
    // Microsoft.SqlServer.Types.SqlGeographyBuilder Builder = new
    // Microsoft.SqlServer.Types.SqlGeographyBuilder;
    // Builder.SetSrid(4326); // WGS 84
    // Builder.BeginGeography(Microsoft.SqlServer.Types.OpenGisGeographyType.Point);
    // Builder.BeginFigure(GeoLocation.Latitude, GeoLocation.Longitude);
    // Builder.EndFigure();
    // Builder.EndGeography();
    //
    // // Call on Overload
    // return this.SetTypedValue(Builder.ConstructedGeography);
    //
    // }else{
    // // return Failed; Type Mismatch
    // return new CallResult(CallResults.FAILED, "Type mismatch", this);
    // }
    //
    // }catch(Exception ex){
    // // return Failed Error
    // return new CallResult(CallResults.FAILED_ERROR, ex, this);
    // }
    // }

    // TODO: GeocoordinateList
    // public CallResult SetTypedValue(List(Of Geolocation) GeoLocations){
    // try{
    // CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
    // if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) ==
    // ECoalesceFieldDataTypes.GeocoordinateListType) {
    // // Set
    // Microsoft.SqlServer.Types.SqlGeographyBuilder Builder = new
    // Microsoft.SqlServer.Types.SqlGeographyBuilder;
    // Builder.SetSrid(4326); // WGS 84
    // Builder.BeginGeography(Microsoft.SqlServer.Types.OpenGisGeographyType.MultiPoint);
    // for(Geolocation Geolocation : GeoLocations){
    // Builder.BeginGeography(Microsoft.SqlServer.Types.OpenGisGeographyType.Point);
    // Builder.BeginFigure(Geolocation.Latitude, Geolocation.Longitude);
    // Builder.EndFigure();
    // Builder.EndGeography();
    // }
    // Builder.EndGeography();
    //
    // // Call on Overload
    // return this.SetTypedValue(Builder.ConstructedGeography);
    //
    // }else{
    // // return Failed; Type Mismatch
    // return new CallResult(CallResults.FAILED, "Type mismatch", this);
    // }
    //
    // }catch(Exception ex){
    // // return Failed Error
    // return new CallResult(CallResults.FAILED_ERROR, ex, this);
    // }
    // }

    public CallResult SetTypedValue(byte[] dataBytes)
    {
        try
        {
            if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.BinaryType)
            {

                // TODO: make sure the string conversion is correct.
                String value = Base64.encodeBase64String(dataBytes);
                SetValue(value);
                SetSize(dataBytes.length);

                return CallResult.successCallResult;

            }
            else
            {
                return new CallResult(CallResults.FAILED, "Type mismatch", this);
            }

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetTypedValue(byte[] dataBytes, String filename, String extension, String mimeType)
    {
        try
        {
            // TODO: make sure the string conversion is correct
            // this.Value = Convert.ToBase64String(DataBytes);
            String value = Base64.encodeBase64String(dataBytes);
            SetValue(value);
            SetFilename(filename);
            SetExtension(extension);
            SetMimeType(mimeType);
            SetSize(dataBytes.length);

            return CallResult.successCallResult;

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetTypedValue(String filename, String extension, String mimeType, String hash)
    {
        try
        {
            // Set Bytes

            SetFilename(filename);
            SetExtension(extension);
            SetMimeType(mimeType);
            SetHash(hash);

            return CallResult.successCallResult;

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    // TODO: DocumentProperties
    // public CallResult SetTypedValue(Byte[] DataBytes, DocumentProperties
    // DocProps){
    // try{
    // CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
    // if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) ==
    // ECoalesceFieldDataTypes.FileType) {
    //
    // // Set Bytes
    // //TODO: make sure the string conversion is correct
    // //this.Value = Convert.ToBase64String(DataBytes);
    // this.Value = DataBytes.toString();
    // this.Filename = DocProps.Filename;
    // this.Extension = DocProps.Extension;
    // this.MimeType = DocProps.MimeType;
    // this.Size = DataBytes.length;
    //
    // // return Success
    // return CallResult.successCallResult;
    //
    // }else{
    // // return Failed; Type Mismatch
    // return new CallResult(CallResults.FAILED, "Type mismatch", this);
    // }
    //
    // }catch(Exception ex){
    // // return Failed Error
    // return new CallResult(CallResults.FAILED_ERROR, ex, this);
    // }
    // }

    // TODO: DocumentProperties
    // public CallResult SetTypedValue(DocumentProperties DocProps) {
    // try{
    // CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
    // if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) ==
    // ECoalesceFieldDataTypes.FileType) {
    // // Does File Exist?
    // if (File.Exists(DocProps.FullFilename)) {
    // // Read Bytes
    // Byte[] FileBytes = File.ReadAllBytes(DocProps.FullFilename);
    //
    // // Set Bytes
    // //TODO: make sure the string conversion is correct
    // //this.Value = Convert.ToBase64String(FileBytes);
    // this.Value = FileBytes.toString();
    // this.Filename = DocProps.Filename;
    // this.Extension = DocProps.Extension;
    // this.MimeType = DocProps.MimeType;
    // this.Size = FileBytes.length;
    //
    // // return Success
    // return CallResult.successCallResult;
    // }else{
    // // return Failed; Type Mismatch
    // return new CallResult(CallResults.FAILED, "File not found", this);
    // }
    // }else{
    // // return Failed; Type Mismatch
    // return new CallResult(CallResults.FAILED, "Type mismatch", this);
    // }
    //
    // }catch(Exception ex){
    // // return Failed Error
    // return new CallResult(CallResults.FAILED_ERROR, ex, this);
    // }
    // }

    private UUID GetGuidValue() throws ClassCastException
    {

        if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) != ECoalesceFieldDataTypes.GuidType)
        {
            throw new ClassCastException("Type mismatch");
        }

        String validUuid = GUIDHelper.IsValid(GetValue());

        if (validUuid == null) return null;

        UUID value = GUIDHelper.GetGuid(GetValue());

        return value;

    }

    public DateTime GetDateTimeValue() throws ClassCastException
    {

        if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) != ECoalesceFieldDataTypes.DateTimeType)
        {
            throw new ClassCastException("Type mismatch");
        }

        DateTime value = JodaDateTimeHelper.FromXmlDateTimeUTC(this.GetValue());

        if (value == null) return null;

        return value;

    }

    public boolean GetBooleanValue()
    {
        if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) != ECoalesceFieldDataTypes.BooleanType)
        {
            throw new ClassCastException("Type mismatch");
        }

        boolean value = Boolean.parseBoolean(this.GetValue());

        return value;

    }

    public int GetIntegerValue()
    {
        if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) != ECoalesceFieldDataTypes.IntegerType)
        {
            throw new ClassCastException("Type mismatch");
        }

        int value = Integer.parseInt(this.GetValue());

        return value;

    }

    // TODO: Geolocation type
    // public CallResult GetTypedValue(Geolocation GeoLocation) {
    // try{
    // CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
    // if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) ==
    // ECoalesceFieldDataTypes.GeocoordinateType) {
    // // Basic Check
    // if (!(this.Value.StartsWith("POINT"))) {
    // // return Failed; Type Mismatch
    // return new CallResult(CallResults.FAILED, "Type mismatch", this);
    // }else{
    // // Get
    // Microsoft.SqlServer.Types.SqlGeography Geography = null;
    // Geography = Microsoft.SqlServer.Types.SqlGeography.STPointFromText(new
    // System.Data.SqlTypes.SqlString(this.Value), 4326);
    // if (GeoLocation == null) {
    // GeoLocation = new Geolocation();
    // }
    // GeoLocation.Latitude = Geography.Lat;
    // GeoLocation.Longitude = Geography.Long;
    //
    // // return Success
    // return CallResult.successCallResult;
    // }
    // }else{
    // // return Failed; Type Mismatch
    // return new CallResult(CallResults.FAILED, "Type mismatch", this);
    // }
    //
    // }catch(Exception ex){
    // // return Failed Error
    // return new CallResult(CallResults.FAILED_ERROR, ex, this);
    // }
    // }

    // TODO: GeocoordinateList type
    // public CallResult GetTypedValue(ArrayList<Geolocation> GeoLocations){
    // try{
    // CoalesceFieldDefinition CFD = new CoalesceFieldDefinition();
    // if (CFD.GetCoalesceFieldDataTypeForCoalesceType(this.DataType) ==
    // ECoalesceFieldDataTypes.GeocoordinateListType) {
    // // Basic Check
    // if (!(this.Value.StartsWith("MULTIPOINT"))) {
    // // return Failed; Type Mismatch
    // return new CallResult(CallResults.FAILED, "Type mismatch", this);
    // }else{
    // // Get
    // ArrayList<Geolocation> TempGeoLocations = new ArrayList<Geolocation>();
    // Microsoft.SqlServer.Types.SqlGeography Geography = null;
    //
    // Geography = Microsoft.SqlServer.Types.SqlGeography.STMPointFromText(new
    // System.Data.SqlTypes.SqlString(this.Value), 4326);
    // Dim geoPointCount = Geography.STNumGeometries();
    // for(int geoPointIndex = 1; geoPointIndex <= geoPointCount;
    // geoPointCount++){
    // Microsoft.SqlServer.Types.SqlGeography geoPoint =
    // Geography.STGeometryN(geoPointIndex);
    // TempGeoLocations.Add(new Geolocation(geoPoint.Lat, geoPoint.Long));
    // }
    //
    // // All points were valid so return the locations array
    // GeoLocations = TempGeoLocations;
    //
    // // return Success
    // return CallResult.successCallResult;
    // }
    // // Get
    // }else{
    // // return Failed; Type Mismatch
    // return new CallResult(CallResults.FAILED, "Type mismatch", this);
    // }
    //
    // }catch(Exception ex){
    // // return Failed Error
    // return new CallResult(CallResults.FAILED_ERROR, ex, this);
    // }
    // }

    public byte[] GetByteArrayValue() throws ClassCastException
    {
        if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) != ECoalesceFieldDataTypes.BinaryType)
        {
            throw new ClassCastException("Type mismatch");
        }

        // Basic Check
        String rawValue = GetValue();
        if (rawValue.length() > 0)
        {
            // Needs to be tested for compatibility with .Net. Should be.
            byte[] bytes = Base64.decodeBase64(rawValue);

            return bytes;

        }
        else
        {

            return new byte[0];

        }
    }

    public XsdFieldHistory GetHistoryRecord(String historyKey)
    {
        XsdFieldHistory historyRecord = (XsdFieldHistory)_childDataObjects.get(historyKey);

        return historyRecord;

    }

    // -----------------------------------------------------------------------//
    // protected Methods
    // -----------------------------------------------------------------------//

    protected CallResult SetChanged(Object oldValue, Object newValue)
    {
        try
        {
 
            // Does the new value differ from the existing?
            if (!oldValue.equals(newValue))
            {

                // Yes; should we create a FieldHistory entry to reflect the
                // change?
                // We create FieldHistory entry if History is not Suspended; OR
                // if DataType is binary; OR if DateCreated=LastModified and
                // Value is unset
                if (!this.GetSuspendHistory())
                {

                    switch (GetDataType().toUpperCase()) {

                    case "BINARY":
                    case "FILE":
                        // Don't Create History Entry for these types
                        break;
                    default:

                        // Does LastModified = DateCreated?
                        if (GetLastModified().compareTo(GetDateCreated()) != 0)
                        {

                            // No; Create History Entry
                            XsdFieldHistory fieldHistory = XsdFieldHistory.Create(this);
                            if (fieldHistory == null) return CallResult.failedCallResult;

                            SetPreviousHistoryKey(fieldHistory.GetKey());
                        }

                    }

                }

                // Set LastModified
                DateTime utcNow = JodaDateTimeHelper.NowInUtc();
                if (utcNow != null) SetLastModified(utcNow);

            }

            return CallResult.successCallResult;

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult Change(String value, String marking, String user, String ip)
    {
        try
        {

            // Does the new value differ from the existing?
            if (!(GetValue().equals(value) && GetClassificationMarking().equals(marking)))
            {

                // Yes; should we create a FieldHistory entry to reflect the
                // change?
                // We create FieldHistory entry if History is not Suspended; OR
                // if DataType is binary; OR if DateCreated=LastModified and
                // Value is unset
                if (!GetSuspendHistory())
                {

                    // Does LastModified = DateCreated?
                    if (GetLastModified().compareTo(GetDateCreated()) != 0)
                    {
                        // No; Create History Entry
                        // TODO: Something just feels wrong about declaring one
                        // CoalesceFieldHistory to create another.
                        XsdFieldHistory fieldHistory = XsdFieldHistory.Create(this);
                        if (fieldHistory == null) return CallResult.failedCallResult;

                        SetPreviousHistoryKey(fieldHistory.GetKey());
                    }
                }

                // Change Values
                if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.DateTimeType
                        && !StringHelper.IsNullOrEmpty(value))
                {

                    DateTime valueDate = JodaDateTimeHelper.FromXmlDateTimeUTC(value);

                    SetTypedValue(valueDate);

                }
                else
                {
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

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    // TODO: Unused. Attribute set should be handled by each property.
    /*
     * protected CallResult ChangeDate(String AttributeName, Date Value) { try{ CallResult rst;
     * 
     * //TODO: XMLHelper // Does the new value differ from the existing? // if (_XmlHelper.GetAttribute(this._DataObjectNode,
     * AttributeName) != Value) {
     * 
     * // Yes; should we create a FieldHistory entry to reflect the change? // We create FieldHistory entry if History is not
     * Suspended; OR if DataType is binary; OR if DateCreated=LastModified and Value is unset if (!GetSuspendHistory()) {
     * 
     * switch (this.DataType.toUpperCase()){
     * 
     * case "BINARY": case "FILE": // Don't Create History Entry for these types break; default: // Does LastModified =
     * DateCreated? if (this.LastModified.compareTo(this.DateCreated) != 0) { // No; Create History Entry
     * CoalesceFieldHistory FieldHistory = null; CoalesceFieldHistory CFH = new CoalesceFieldHistory(); rst =
     * CFH.Create(this, FieldHistory); if (!(rst.getIsSuccess())) return rst; } break; }
     * 
     * }
     * 
     * // Change Attribute Value using the Date Method rst = _XmlHelper.SetAttributeAsDate(this._DataObjectDocument,
     * this._DataObjectNode, AttributeName, Value); if (!(rst.getIsSuccess())) return rst;
     * 
     * // Set LastModified Date UTCDate = new Date(); DateTimeHelper.ConvertDateToGMT(UTCDate); this.LastModified = UTCDate;
     * 
     * // }
     * 
     * // return Success return CallResult.successCallResult;
     * 
     * }catch(Exception ex){ // return Failed Error return new CallResult(CallResults.FAILED_ERROR, ex, this); } }
     */
}
