package Coalesce.Framework.DataModel;

import java.util.UUID;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.NullArgumentException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.joda.time.DateTime;

import Coalesce.Common.Classification.Marking;
import Coalesce.Common.Exceptions.DataFormatException;
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

    /*--------------------------------------------------------------------------
    Public Abstract Functions
    --------------------------------------------------------------------------*/

    public abstract String getValue();

    public abstract void setValue(String value);

    public abstract ECoalesceFieldDataTypes getDataType();

    public abstract void setDataType(ECoalesceFieldDataTypes value);

    public abstract String getLabel();

    public abstract void setLabel(String value);

    public abstract int getSize();

    public abstract void setSize(Integer value);

    public abstract String getModifiedBy();

    public abstract void setModifiedBy(String value);

    public abstract String getModifiedByIP();

    public abstract void setModifiedByIP(String value);

    public abstract String getClassificationMarking();

    public abstract void setClassificationMarking(String value);

    public abstract String getPreviousHistoryKey();

    public abstract void setPreviousHistoryKey(String value);

    public abstract String getFilename();

    public abstract void setFilename(String value);

    public abstract String getExtension();

    public abstract void setExtension(String value);

    public abstract String getMimeType();

    public abstract void setMimeType(String value);

    public abstract String getHash();

    public abstract void setHash(String value);

    /*
     * TODO: InputLang needs to be added to entity public String GetInputLang(){ return _entityField.getInputlang(); } public
     * void SetInputLang(String value){ _entityField.setInputlang(value); }
     */

    /*--------------------------------------------------------------------------
    Public Functions
    --------------------------------------------------------------------------*/

    public String getValueWithMarking()
    {
        String val = getValue();
        Marking mrk = new Marking(getClassificationMarking());
        return mrk.toString() + " " + val;
    }

    @Override
    public String toString()
    {
        return getValueWithMarking();
    }

    public void setClassificationMarking(Marking value)
    {
        setClassificationMarking(value.toString());
    }

    public String getPortionMarking()
    {
        Marking mrk = new Marking(getClassificationMarking());
        return mrk.ToPortionString();
    }

    public void setPreviousHistoryKey(XsdFieldHistory fieldHistory)
    {
        if (fieldHistory == null) throw new NullArgumentException("fieldHistory");

        setPreviousHistoryKey(fieldHistory.getKey());
    }

    public Object getData() throws DataFormatException
    {

        // TODO: GeocoordinateType, GeocoordinateListType, DocumentProperties
        // types

        switch (getDataType()) {
        case StringType:
        case UriType:
            return getValue();

        case DateTimeType:
            return getDateTimeValue();

        case BinaryType:
            return getBinaryValue();

        case BooleanType:
            return getBooleanValue();

        case IntegerType:
            return getIntegerValue();

        case GuidType:
            return getGuidValue();

        default:
            throw new NotImplementedException(getDataType() + " not implemented");

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

    public void setData(Object value)
    {
        setTypedValue(value.toString());
    }

    public void setTypedValue(String value)
    {
        ECoalesceFieldDataTypes fieldType = getDataType();
        if (fieldType != ECoalesceFieldDataTypes.StringType && fieldType != ECoalesceFieldDataTypes.UriType)
        {
            throw new ClassCastException("Type mismatch");
        }

        setValue(value);
    }

    public void setTypedValue(UUID value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.GuidType)
        {
            throw new ClassCastException("Type mismatch");
        }

        setValue(GUIDHelper.GetGuidString(value));
    }

    public void setTypedValue(DateTime value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.DateTimeType)
        {
            throw new ClassCastException("Type mismatch");
        }

        setValue(JodaDateTimeHelper.ToXmlDateTimeUTC(value));
    }

    public void setTypedValue(boolean value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.BooleanType)
        {
            throw new ClassCastException("Type mismatch");
        }

        setValue(String.valueOf(value));
    }

    public void setTypedValue(int value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.IntegerType)
        {
            throw new ClassCastException("Type mismatch");
        }

        setValue(String.valueOf(value));
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

    public void setTypedValue(byte[] dataBytes)
    {
        if (getDataType() != ECoalesceFieldDataTypes.BinaryType)
        {
            throw new ClassCastException("Type mismatch");
        }

        String value = Base64.encodeBase64String(dataBytes);
        setValue(value);
        setSize(dataBytes.length);
    }

    public void setTypedValue(byte[] dataBytes, String filename, String extension, String mimeType)
    {
        String value = Base64.encodeBase64String(dataBytes);
        setValue(value);
        setFilename(filename);
        setExtension(extension);
        setMimeType(mimeType);
        setSize(dataBytes.length);
    }

    public void setTypedValue(String filename, String extension, String mimeType, String hash)
    {
        setFilename(filename);
        setExtension(extension);
        setMimeType(mimeType);
        setHash(hash);
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

    public UUID getGuidValue() throws ClassCastException
    {

        if (getDataType() != ECoalesceFieldDataTypes.GuidType)
        {
            throw new ClassCastException("Type mismatch");
        }

        String value = getValue();

        if (GUIDHelper.IsValid(value))
        {
            return GUIDHelper.GetGuid(value);
        }
        else
        {
            return null;
        }
    }

    public DateTime getDateTimeValue() throws ClassCastException
    {

        if (getDataType() != ECoalesceFieldDataTypes.DateTimeType)
        {
            throw new ClassCastException("Type mismatch");
        }

        DateTime value = JodaDateTimeHelper.FromXmlDateTimeUTC(this.getValue());

        if (value == null) return null;

        return value;

    }

    public boolean getBooleanValue() throws ClassCastException
    {
        if (getDataType() != ECoalesceFieldDataTypes.BooleanType)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (StringHelper.IsNullOrEmpty(this.getValue())) throw new ClassCastException("Type mismatch");

        boolean value = Boolean.parseBoolean(this.getValue());

        return value;

    }

    public int getIntegerValue() throws ClassCastException, DataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.IntegerType)
        {
            throw new ClassCastException("Type mismatch");
        }

        try
        {

            int value = Integer.parseInt(this.getValue());

            return value;

        }
        catch (NumberFormatException nfe)
        {
            throw new DataFormatException("Failed to parse integer value for: " + getName());
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

    public byte[] getBinaryValue() throws ClassCastException
    {
        if (getDataType() != ECoalesceFieldDataTypes.BinaryType)
        {
            throw new ClassCastException("Type mismatch");
        }

        // Basic Check
        String rawValue = getValue();
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
