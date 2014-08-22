package Coalesce.Framework.DataModel;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import org.joda.time.DateTime;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;
import Coalesce.Common.Helpers.DateTimeHelper;
import Coalesce.Common.Helpers.GUIDHelper;
import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Framework.GeneratedJAXB.*;

public class XsdFieldHistory extends XsdDataObject {

	// -----------------------------------------------------------------------//
	// protected Member Variables
	// -----------------------------------------------------------------------//

	private static String MODULE = "Coalesce.Framework.DataModel.XsdFieldHistory";

	private Fieldhistory _entityFieldHistory;

	// -----------------------------------------------------------------------//
	// Factory and Initialization
	// -----------------------------------------------------------------------//

	public static CallResult Create(XsdField parent, XsdFieldHistory newFieldHistory)
	{
		try {

			// Set References

			// TODO: What is this part?
			// Copy attributes from parent node
			newFieldHistory.SetAttributes(parent._entityField);

			newFieldHistory._parent = parent;
			newFieldHistory.SetKey(java.util.UUID.randomUUID().toString());
			newFieldHistory.SetPreviousHistoryKey(parent.GetPreviousHistoryKey());

			// Append to parent's child node collection
			parent.GetEntityFieldHistories().add(newFieldHistory._entityFieldHistory);

			// Add to Parent's Child Collection
			if (!parent._childDataObjects.containsKey(newFieldHistory.GetKey())) {
				parent._childDataObjects.put(newFieldHistory.GetKey(), newFieldHistory);
			}

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, XsdFieldHistory.MODULE);
		}
	}

	public CallResult Initialize(XsdField parent, Fieldhistory fieldHistory)
	{
		try {
			CallResult rst;

			// Set References
			_parent = parent;

			_entityFieldHistory = fieldHistory;

			rst = InitializeEntity();

			return rst;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	// -----------------------------------------------------------------------//
	// public Properties
	// -----------------------------------------------------------------------//

	protected String GetObjectKey()
	{
		return _entityFieldHistory.getKey();
	}

	public void SetKey(String value)
	{
		_entityFieldHistory.setKey(value);
	}

	public String GetName()
	{
		return _entityFieldHistory.getName();
	}

	public void SetName(String value)
	{
		_entityFieldHistory.setName(value);
	}

	public String GetValue()
	{
		return _entityFieldHistory.getValue();
	}

	private void SetValue(String value)
	{
		_entityFieldHistory.setValue(value);
	}

	public String GetValueWithMarking()
	{
		// TODO: Add Common.ClassificationMarking
		String val = GetValue();
		// Marking mrk = new Marking(GetClassificationMarking());
		return GetClassificationMarking() + " " + val;
	}

	// overrides
	public String ToString()
	{
		return GetValueWithMarking();
	}

	public String GetDataType()
	{
		return _entityFieldHistory.getDatatype();
	}

	private void SetDataType(String value)
	{
		_entityFieldHistory.setDatatype(value);
	}

	public String GetLabel()
	{
		return _entityFieldHistory.getLabel();
	}

	public void SetLabel(String value)
	{
		_entityFieldHistory.setLabel(value);
	}

	public String GetModifiedBy()
	{
		return _entityFieldHistory.getModifiedby();
	}

	private void SetModifiedBy(String value)
	{
		_entityFieldHistory.setModifiedby(value);
	}

	public String GetModifiedByIP()
	{
		return _entityFieldHistory.getModifiedbyip();
	}

	private void SetModifiedByIP(String value)
	{
		_entityFieldHistory.setModifiedbyip(value);
	}

	public String GetClassificationMarking()
	{
		return _entityFieldHistory.getClassificationmarking();
	}

	private void SetClassificationMarking(String value)
	{
		_entityFieldHistory.setClassificationmarking(value);
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
		String prevHistKey = _entityFieldHistory.getPrevioushistorykey();
		if (prevHistKey.equals("")) {
			return "00000000-0000-0000-0000-000000000000";
		} else {
			return prevHistKey;
		}
	}

	private void SetPreviousHistoryKey(String value)
	{
		_entityFieldHistory.setPrevioushistorykey(value);
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

	// TODO: Missing Field attributes

	public String GetCoalesceFilename()
	{
		CallResult rst;

		if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.FileType) {

			String baseFilename = GetKey();
			baseFilename = GUIDHelper.RemoveBrackets(baseFilename);

			// TODO: Missing Extension
			return baseFilename; // + "." + GetExtension();

		} else {
			return "";
		}
	}

	public String GetCoalesceThumbnailFilename()
	{
		CallResult rst;

		if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.FileType) {

			String baseFilename = this.GetKey();
			baseFilename = GUIDHelper.RemoveBrackets(baseFilename);

			return baseFilename + "_thumb.jpg";

		} else {
			return "";
		}
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

	public DateTime GetDateCreated()
	{
		try {

			// return new
			// SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityFieldHistory.getDatecreated());
			return _entityFieldHistory.getDatecreated();

		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, this);
			return null;
		}
	}

	public CallResult SetDateCreated(DateTime value)
	{
		try {
			// _entityFieldHistory.setDatecreated(new
			// SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
			_entityFieldHistory.setDatecreated(value);

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public DateTime GetLastModified()
	{
		try {

			// return new
			// SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityFieldHistory.getLastmodified());
			return _entityFieldHistory.getLastmodified();

		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, this);
			return null;
		}
	}

	// -----------------------------------------------------------------------//
	// public Methods
	// -----------------------------------------------------------------------//

	public CallResult ToXml(StringBuilder xml)
	{
		try {
			CallResult rst;

			rst = XmlHelper.Serialize(_entityFieldHistory, xml);

			return rst;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

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
			} else if (value instanceof Date) {
				return GetTypedValue((Date) value);
			} else if (value instanceof Integer) {
				return GetTypedValue((Integer) value);
			} else if (value instanceof Byte) {
				return GetTypedValue((Byte) value);
			} else if (value instanceof UUID) {
				value = (UUID) GetUuidValue();

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

	// TODO: Need to test return types
	public UUID GetUuidValue()
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

	public CallResult GetTypedValue(Date value)
	{
		try {
			if (XsdFieldDefinition.GetCoalesceFieldDataTypeForCoalesceType(GetDataType()) == ECoalesceFieldDataTypes.DateTimeType) {
				try {

					Date.parse(this.GetValue());

					return DateTimeHelper.ConvertDateToGMT(this.GetValue(), value);

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
			historyRecord = (XsdFieldHistory) _childDataObjects.get(historyKey);

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}

	}

	// -----------------------------------------------------------------------//
	// protected Methods
	// -----------------------------------------------------------------------//

	protected CallResult SetObjectLastModified(DateTime value)
	{
		try {
			// _entityFieldHistory.setLastmodified(new
			// SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
			_entityFieldHistory.setLastmodified(value);

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	private CallResult SetAttributes(Field field)
	{
		try {
			CallResult rst;

			_entityFieldHistory.setKey(field.getKey());
			_entityFieldHistory.setName(field.getName());
			_entityFieldHistory.setValue(field.getValue());
			_entityFieldHistory.setDatatype(field.getDatatype());
			_entityFieldHistory.setLabel(field.getLabel());
			// _entityFieldHistory.setSize(field.getSize());
			_entityFieldHistory.setModifiedby(field.getModifiedby());
			_entityFieldHistory.setModifiedbyip(field.getModifiedbyip());
			_entityFieldHistory.setClassificationmarking(field.getClassificationmarking());
			_entityFieldHistory.setPrevioushistorykey(field.getPrevioushistorykey());
			/*
			 * _entityFieldHistory.setFilename(field.getFilename());
			 * _entityFieldHistory.setExtension(field.getExtension());
			 * _entityFieldHistory.setMimetype(field.getMimetype());
			 * _entityFieldHistory.setHash(field.getHash());
			 */
			_entityFieldHistory.setValue(field.getValue());
			_entityFieldHistory.setDatecreated(field.getDatecreated());
			_entityFieldHistory.setLastmodified(field.getLastmodified());
			_entityFieldHistory.setStatus(field.getStatus());

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	protected CallResult GetObjectStatus(String status)
	{
		try {
			status = _entityFieldHistory.getStatus();

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	protected CallResult SetObjectStatus(String status)
	{
		try {
			_entityFieldHistory.setStatus(status);

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

}
