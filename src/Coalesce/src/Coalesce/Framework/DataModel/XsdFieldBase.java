package Coalesce.Framework.DataModel;

import java.util.UUID;

import org.apache.commons.lang.NotImplementedException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.joda.time.DateTime;

import Coalesce.Common.Classification.Marking;
import Coalesce.Common.Helpers.GUIDHelper;
import Coalesce.Common.Helpers.JodaDateTimeHelper;
import Coalesce.Common.Helpers.StringHelper;

/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

 Notwithstanding any contractor copyright notice, the Government has Unlimited
 Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 of this work other than as specifically authorized by these DFARS Clauses may
 violate Government rights in this work.

 DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 Unlimited Rights. The Government has the right to use, modify, reproduce,
 perform, display, release or disclose this computer software and to have or
 authorize others to do so.

 Distribution Statement D. Distribution authorized to the Department of
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 -----------------------------------------------------------------------------*/

public abstract class XsdFieldBase extends XsdDataObject {

    public abstract String GetValue();

    public abstract void SetValue(String value);

    public String GetValueWithMarking()
    {
        String val = GetValue();
        Marking mrk = new Marking(GetClassificationMarking());
        return mrk.toString() + " " + val;
    }

    public String ToString()
    {
        return GetValueWithMarking();
    }

    public abstract ECoalesceFieldDataTypes GetDataType();

    public abstract void SetDataType(ECoalesceFieldDataTypes value);

    public abstract String GetLabel();

    public abstract void SetLabel(String value);

    public abstract int GetSize();

    public abstract void SetSize(Integer value);

    public abstract String GetModifiedBy();

    public abstract void SetModifiedBy(String value);

    public abstract String GetModifiedByIP();

    public abstract void SetModifiedByIP(String value);

    public abstract String GetClassificationMarking();

    public void SetClassificationMarking(Marking value)
    {
        SetClassificationMarking(value.toString());
    }

    public abstract void SetClassificationMarking(String value);

    public String GetPortionMarking()
    {
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

    public Object GetData()
    {

        // TODO: GeocoordinateType, GeocoordinateListType, DocumentProperties
        // types

        switch (GetDataType()) {
        case StringType:
        case UriType:
            return GetValue();

        case DateTimeType:
            return GetDateTimeValue();

        case BinaryType:
            return GetBinaryValue();

        case BooleanType:
            return GetBooleanValue();

        case IntegerType:
            return GetIntegerValue();

        case GuidType:
            return GetGuidValue();

        default:
            throw new NotImplementedException(GetDataType() + " not implemented");

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

    }

    public void SetData(Object value)
    {
        SetTypedValue(value.toString());
    }

    public abstract DateTime getDateCreated();

    public abstract void setDateCreated(DateTime value);

    public abstract DateTime getLastModified();

    protected abstract void setObjectLastModified(DateTime value);

    public void SetTypedValue(String value)
    {
        ECoalesceFieldDataTypes fieldType = GetDataType();
        if (fieldType != ECoalesceFieldDataTypes.StringType && fieldType != ECoalesceFieldDataTypes.UriType)
        {
            throw new ClassCastException("Type mismatch");
        }

        SetValue(value);
    }

    public void SetTypedValue(UUID value)
    {
        if (GetDataType() != ECoalesceFieldDataTypes.GuidType)
        {
            throw new ClassCastException("Type mismatch");
        }

        SetValue(GUIDHelper.GetGuidString(value));
    }

    public void SetTypedValue(DateTime value)
    {
        if (GetDataType() != ECoalesceFieldDataTypes.DateTimeType)
        {
            throw new ClassCastException("Type mismatch");
        }

        SetValue(JodaDateTimeHelper.ToXmlDateTimeUTC(value));
    }

    public void SetTypedValue(boolean value)
    {
        if (GetDataType() != ECoalesceFieldDataTypes.BooleanType)
        {
            throw new ClassCastException("Type mismatch");
        }

        SetValue(String.valueOf(value));
    }

    public void SetTypedValue(int value)
    {
        if (GetDataType() != ECoalesceFieldDataTypes.IntegerType)
        {
            throw new ClassCastException("Type mismatch");
        }

        SetValue(String.valueOf(value));
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

    public void SetTypedValue(byte[] dataBytes)
    {
        if (GetDataType() != ECoalesceFieldDataTypes.BinaryType)
        {
            throw new ClassCastException("Type mismatch");
        }

        String value = Base64.encodeBase64String(dataBytes);
        SetValue(value);
        SetSize(dataBytes.length);
    }

    public void SetTypedValue(byte[] dataBytes, String filename, String extension, String mimeType)
    {
        String value = Base64.encodeBase64String(dataBytes);
        SetValue(value);
        SetFilename(filename);
        SetExtension(extension);
        SetMimeType(mimeType);
        SetSize(dataBytes.length);
    }

    public void SetTypedValue(String filename, String extension, String mimeType, String hash)
    {
        SetFilename(filename);
        SetExtension(extension);
        SetMimeType(mimeType);
        SetHash(hash);
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

    public UUID GetGuidValue() throws ClassCastException
    {

        if (GetDataType() != ECoalesceFieldDataTypes.GuidType)
        {
            throw new ClassCastException("Type mismatch");
        }

        String value = GetValue();

        if (GUIDHelper.IsValid(value))
        {
            return GUIDHelper.GetGuid(value);
        }
        else
        {
            return null;
        }
    }

    public DateTime GetDateTimeValue() throws ClassCastException
    {

        if (GetDataType() != ECoalesceFieldDataTypes.DateTimeType)
        {
            throw new ClassCastException("Type mismatch");
        }

        DateTime value = JodaDateTimeHelper.FromXmlDateTimeUTC(this.GetValue());

        if (value == null) return null;

        return value;

    }

    public boolean GetBooleanValue()
    {
        if (GetDataType() != ECoalesceFieldDataTypes.BooleanType)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (StringHelper.IsNullOrEmpty(this.GetValue())) throw new ClassCastException("Type mismatch");

        boolean value = Boolean.parseBoolean(this.GetValue());

        return value;

    }

    public int GetIntegerValue()
    {
        if (GetDataType() != ECoalesceFieldDataTypes.IntegerType)
        {
            throw new ClassCastException("Type mismatch");
        }

        try
        {

            int value = Integer.parseInt(this.GetValue());

            return value;

        }
        catch (NumberFormatException nfe)
        {
            throw new ClassCastException("Type mismatch");
        }

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

    public byte[] GetBinaryValue() throws ClassCastException
    {
        if (GetDataType() != ECoalesceFieldDataTypes.BinaryType)
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

}
