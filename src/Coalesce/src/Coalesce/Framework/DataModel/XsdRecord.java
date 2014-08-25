package Coalesce.Framework.DataModel;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;
import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Framework.GeneratedJAXB.*;

public class XsdRecord extends XsdDataObject {

    //-----------------------------------------------------------------------//
    // protected Member Variables
    //-----------------------------------------------------------------------//

	private static String MODULE = "Coalesce.Framework.DataModel.XsdRecord";
	
	private Record _entityRecord;
	
	// -----------------------------------------------------------------------//
	// Factory and Initialization
	// -----------------------------------------------------------------------//

	public static CallResult Create(XsdRecordset parent, XsdRecord newRecord, String name)
	{
		try {
			CallResult rst;

			Record newEntityRecord = new Record();
			parent.GetEntityRecords().add(newEntityRecord);
			
			rst = newRecord.Initialize(parent, newEntityRecord);
			if (!rst.getIsSuccess()) return rst;

			for (XsdFieldDefinition fieldDefinition : parent.GetFieldDefinitions()) {
	            XsdField newField = new XsdField();
	            rst = XsdField.Create(newRecord, newField, fieldDefinition);
            }
			
			newRecord.SetName(name);

			// Add to parent's child collection
			if (!parent._childDataObjects.containsKey(newRecord.GetKey())) {
				parent._childDataObjects.put(newRecord.GetKey(), newRecord);
			}
			
			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, XsdRecord.MODULE);
		}
	}

	public CallResult Initialize(XsdRecordset parent, Record record)
	{
		try {
			CallResult rst;

			// Set References
			_parent = parent;

			_entityRecord = record;

			for (Field entityField : record.getFieldNode()) {
				XsdField newField = new XsdField();
				rst = newField.Initialize(this, entityField);

				// Add to Child Collection
				_childDataObjects.put(newField.GetKey(), newField);
			}

			// Add to Parent Collections (if we're Active)
			if (GetStatus() == ECoalesceDataObjectStatus.ACTIVE) {
				parent._childDataObjects.put(GetKey(), this);
				parent.GetRecords().add(this);
			}
			 
			rst = InitializeEntity();

			return rst;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	// -----------------------------------------------------------------------//
	// Public Methods
	// -----------------------------------------------------------------------//

	protected String GetObjectKey()
	{
		return _entityRecord.getKey();
	}

	public void SetKey(String value)
	{
		_entityRecord.setKey(value);
	}

	public String GetName()
	{
		return _entityRecord.getName();
	}

	public void SetName(String value)
	{
		_entityRecord.setName(value);
	}

	public ArrayList<XsdField> GetFields()
	{
		try {

			ArrayList<XsdField> fields = new ArrayList<XsdField>();

			for (XsdDataObject dataObject : _childDataObjects.values()) {

				if (dataObject instanceof XsdField) {
					fields.add((XsdField) dataObject);
				}
			}

			return fields;

		} catch (Exception ex) {
			return null;
		}
	}

	public ArrayList<String> GetFieldNames()
	{
		try {
			
			ArrayList<String> fieldNames = new ArrayList<String>();

			for (XsdDataObject dataObject : _childDataObjects.values()) {
	            if (dataObject instanceof XsdField) {
	            	fieldNames.add(dataObject.GetName());
	            }
            }
			
			return fieldNames;

		} catch (Exception ex) {
			return null;
		}
	}

	public ArrayList<String> GetFieldKeys()
	{
		try {

			ArrayList<String> fieldKeys = new ArrayList<String>();

			for (XsdDataObject dataObject : _childDataObjects.values()) {
				if (dataObject instanceof XsdField) {
					fieldKeys.add(dataObject.GetKey());
				}
			}

			return fieldKeys;

		} catch (Exception ex) {
			return null;
		}
	}

	public XsdField GetFieldByKey(String key)
	{
		try {
			
			for (XsdField field : GetFields()) {
	            if (field.GetKey().equals(key)) {
	            	return field;
	            }
            }
			
			return null;

		} catch (Exception ex) {
			return null;
		}
	}

	public XsdField GetFieldByName(String name)
	{
		try {
			
			for (XsdField field : GetFields()) {
	            if (field.GetName().equals(name)) {
	            	return field;
	            }
            }

			return null;

		} catch (Exception ex) {
			return null;
		}
	}

	public XsdField GetFieldByIndex(int Index)
	{
		try {

			return GetFields().get(Index);

		} catch (Exception ex) {
			return null;
		}
	}

	public String GetFieldValue(String fieldName)
	{
		try {
			XsdField field = GetFieldByName(fieldName);

			// Do we have this Field?
			if (field != null) {
				// Yes; return Value;
				return field.GetValue();
			} else {
				// return Empty String
				return "";
			}

		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, this);

			return "";
		}
	}

	public CallResult GetFieldValue(String fieldName, String value)
	{
		try {
			XsdField field = GetFieldByName(fieldName);

			// Do we have this Field?
			if (field != null) {
				// Yes; Set Value
				value = field.GetValue();

				// return Success
				return CallResult.successCallResult;
			} else {
				// return Failed Error
				return new CallResult(CallResults.FAILED, "Field not found.", this);
			}

		} catch (Exception ex) {
			// return Failed Error
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public CallResult GetFieldValue(String fieldName, boolean value)
	{
		try {
			XsdField field = GetFieldByName(fieldName);

			// Do we have this Field?
			if (field != null) {
				// Yes; Set Value
				return field.GetTypedValue(value);
			} else {
				return new CallResult(CallResults.FAILED, "Field not found.", this);
			}

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public CallResult GetFieldValue(String fieldName, int value)
	{
		try {
			XsdField field = GetFieldByName(fieldName);

			// Do we have this Field?
			if (field != null) {
				// Yes; Set Value
				return field.GetTypedValue(value);
			} else {
				return new CallResult(CallResults.FAILED, "Field not found.", this);
			}

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public CallResult GetFieldValue(String fieldName, DateTime value)
	{
		try {
			XsdField field = GetFieldByName(fieldName);

			// Do we have this Field?
			if (field != null) {
				// Yes; Set Value
				return field.GetTypedValue(value);
			} else {
				return new CallResult(CallResults.FAILED, "Field not found.", this);
			}

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public CallResult GetFieldValue(String fieldName, ArrayList<Byte> value)
	{
		try {
			XsdField field = GetFieldByName(fieldName);

			// Do we have this Field?
			if (field != null) {
				// Yes; Set Value
				return field.GetTypedValue(value);
			} else {
				return new CallResult(CallResults.FAILED, "Field not found.", this);
			}

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public String GetFieldValueAsString(String fieldName, String defaultValue)
	{
		try {
			CallResult rst;
			
			String value = "";
			rst = GetFieldValue(fieldName, value);

			// Evaluate
			if (rst.getIsSuccess()) {
				return value;
			} else {
				return defaultValue;
			}

		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, this);

			return defaultValue;
		}
	}

	public boolean GetFieldValueAsBoolean(String fieldName, boolean defaultValue)
	{
		try {
			CallResult rst;
			
			Boolean value = false;
			rst = GetFieldValue(fieldName, value);

			// Evaluate
			if (rst.getIsSuccess()) {
				return value;
			} else {
				return defaultValue;
			}

		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, this);
			
			return defaultValue;
		}
	}

	public int GetFieldValueAsInteger(String fieldName, int defaultValue)
	{
		try {
			CallResult rst;
			
			Integer value = 0;
			rst = GetFieldValue(fieldName, value);

			// Evaluate
			if (rst.getIsSuccess()) {
				return value;
			} else {
				return defaultValue;
			}

		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, this);
			
			return defaultValue;
		}
	}

	public DateTime GetFieldValueAsDate(String fieldName, DateTime defaultValue)
	{
		try {
			CallResult rst;
			
			DateTime value = null;
			rst = GetFieldValue(fieldName, value);

			// Evaluate
			if (rst.getIsSuccess())
				return value;
			else
				return defaultValue;

		} catch (Exception ex) {
			// Log exception and return default
			CallResult.log(CallResults.FAILED_ERROR, ex, this);
			return defaultValue;
		}
	}

	public byte[] GetFieldValueAsByteArray(String fieldName, byte[] defaultValue)
	{
		try {
			CallResult rst;
			
			ArrayList<Byte> value = new ArrayList<Byte>();
			rst = GetFieldValue(fieldName, value);

			// Evaluate
			if (rst.getIsSuccess()) {
				
				byte[] valueBytes = new byte[value.size()];
				for (int i=0; i < value.size(); i++) {
					valueBytes[i] = value.get(i);
				}
				
				return valueBytes;
				
			} else {
				return defaultValue;
			}
			
		} catch (Exception ex) {
			// Log exception and return nothing
			CallResult.log(CallResults.FAILED_ERROR, ex, this);
			return defaultValue;
		}
	}

	public CallResult SetFieldValue(String fieldName, String value)
	{
		try {
			XsdField field = GetFieldByName(fieldName);

			// Do we have this Field?
			if (field != null) {
				
				field.SetValue(value);

				return CallResult.successCallResult;
				
			} else {
				return new CallResult(CallResults.FAILED, "Field not found.", this);
			}

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public CallResult SetFieldValue(String fieldName, boolean value)
	{
		try {
			XsdField field = GetFieldByName(fieldName);

			// Do we have this Field?
			if (field != null) {
				return field.SetTypedValue(value);
			} else {
				return new CallResult(CallResults.FAILED, "Field not found.", this);
			}

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public CallResult SetFieldValue(String fieldName, int value)
	{
		try {
			XsdField field = GetFieldByName(fieldName);

			// Do we have this Field?
			if (field != null) {
				return field.SetTypedValue(value);
			} else {
				return new CallResult(CallResults.FAILED, "Field not found.", this);
			}

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public CallResult SetFieldValue(String fieldName, DateTime value)
	{
		try {
			XsdField field = GetFieldByName(fieldName);

			// Do we have this Field?
			if (field != null) {
				return field.SetTypedValue(value);
			} else {
				// return Failed Error
				return new CallResult(CallResults.FAILED, "Field not found.", this);
			}

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public CallResult SetFieldValue(String fieldName, byte[] value)
	{
		try {
			return SetFieldValue(fieldName, value, "");
		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public CallResult SetFieldValue(String fieldName, byte[] value, String fileName)
	{
		try {
			XsdField field = GetFieldByName(fieldName);

			// Do we have this Field?
			if (field != null) {
				
				if (fileName.equals("")) {
					return field.SetTypedValue(value);
				} else {
					return field.SetTypedValue(value, "{" + fileName + "}", ".jpg", "");
				}
				
			} else {
				return new CallResult(CallResults.FAILED, "Field not found.", this);
			}

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public Boolean HasField(String name)
	{
		try {
			// Find Field
			// For Each f As XsdField In this.GetFields
			for (int i = 0; i < GetFields().size(); i++) {
				if (GetFields().get(i).GetName().equals(name)) return true;
			}

			return false;

		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, this);

			return false;
		}
	}

	public CallResult ToXml(StringBuilder xml)
	{
		try {
			CallResult rst;

			rst = XmlHelper.Serialize(_entityRecord, xml);

			return rst;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public DateTime GetDateCreated()
	{
		try {

			//return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityRecord.getDatecreated());
			return _entityRecord.getDatecreated();

		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, this);
			return null;
		}
	}

	public CallResult SetDateCreated(DateTime value)
	{
		try {
			//_entityRecord.setDatecreated(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
			_entityRecord.setDatecreated(value);

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public DateTime GetLastModified()
	{
		try {

			//return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityRecord.getLastmodified());
			return _entityRecord.getLastmodified();

		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, this);
			return null;
		}
	}

	protected CallResult SetObjectLastModified(DateTime value)
	{
		try {
			//_entityRecord.setLastmodified(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
			_entityRecord.setLastmodified(value);

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

    //-----------------------------------------------------------------------//
    // Protected Methods
    //-----------------------------------------------------------------------//

    protected CallResult GetObjectStatus(String status)
    {
    	try {
	    	status = _entityRecord.getStatus();
	    	
	    	return CallResult.successCallResult;
    	
    	} catch (Exception ex) {
    		return new CallResult(CallResults.FAILED_ERROR,ex,this);    		
    	}
    }
    protected CallResult SetObjectStatus(String status)
    {
    	try {
    		_entityRecord.setStatus(status);
    		
    		return CallResult.successCallResult;
    		
    	} catch (Exception ex) {
    		return new CallResult(CallResults.FAILED_ERROR, ex, this);
    	}
    }
    
    protected List<Field> GetEntityFields() {
    	return _entityRecord.getFieldNode();
    }
    
}
