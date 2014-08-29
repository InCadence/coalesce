package Coalesce.Framework.DataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;
import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset.Record;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset.Record.Field;

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

public class XsdRecord extends XsdDataObject {

    public class InvalidFieldException extends Exception {

        private static final long serialVersionUID = 1096699796765997918L;

        public InvalidFieldException(String message)
        {
            super(message);
        }
    }

    // -----------------------------------------------------------------------//
    // protected Member Variables
    // -----------------------------------------------------------------------//

    private Record _entityRecord;

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    public static XsdRecord Create(XsdRecordset parent, String name)
    {
        try
        {

            Record newEntityRecord = new Record();
            parent.GetEntityRecords().add(newEntityRecord);

            XsdRecord newRecord = new XsdRecord();
            if (!newRecord.Initialize(parent, newEntityRecord)) return null;

            for (XsdFieldDefinition fieldDefinition : parent.GetFieldDefinitions())
            {
                // Creates New Field
                XsdField.Create(newRecord, fieldDefinition);
            }

            newRecord.SetName(name);

            // Add to parent's child collection
            if (!parent._childDataObjects.containsKey(newRecord.GetKey()))
            {
                parent._childDataObjects.put(newRecord.GetKey(), newRecord);
            }

            return newRecord;

        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public boolean Initialize(XsdRecordset parent, Record record)
    {

        // Set References
        _parent = parent;
        _entityRecord = record;

        for (Field entityField : record.getField())
        {
            XsdField newField = new XsdField();
            if (!newField.Initialize(this, entityField)) return false;

            // Add to Child Collection
            _childDataObjects.put(newField.GetKey(), newField);
        }

        // Add to Parent Collections (if we're Active)
        if (GetStatus() == ECoalesceDataObjectStatus.ACTIVE)
        {
            parent._childDataObjects.put(GetKey(), this);
            parent.GetRecords().add(this);
        }

        return super.Initialize();

    }

    // -----------------------------------------------------------------------//
    // Public Methods
    // -----------------------------------------------------------------------//

    @Override
    protected String GetObjectKey()
    {
        return _entityRecord.getKey();
    }

    @Override
    public void SetObjectKey(String value)
    {
        _entityRecord.setKey(value);
    }

    @Override
    public String GetName()
    {
        return _entityRecord.getName();
    }

    @Override
    public void SetName(String value)
    {
        _entityRecord.setName(value);
    }

    public ArrayList<XsdField> GetFields()
    {
        try
        {

            ArrayList<XsdField> fields = new ArrayList<XsdField>();

            for (XsdDataObject dataObject : _childDataObjects.values())
            {

                if (dataObject instanceof XsdField)
                {
                    fields.add((XsdField) dataObject);
                }
            }

            return fields;

        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public ArrayList<String> GetFieldNames()
    {
        try
        {

            ArrayList<String> fieldNames = new ArrayList<String>();

            for (XsdDataObject dataObject : _childDataObjects.values())
            {
                if (dataObject instanceof XsdField)
                {
                    fieldNames.add(dataObject.GetName());
                }
            }

            return fieldNames;

        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public ArrayList<String> GetFieldKeys()
    {
        try
        {

            ArrayList<String> fieldKeys = new ArrayList<String>();

            for (XsdDataObject dataObject : _childDataObjects.values())
            {
                if (dataObject instanceof XsdField)
                {
                    fieldKeys.add(dataObject.GetKey());
                }
            }

            return fieldKeys;

        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public XsdField GetFieldByKey(String key)
    {
        try
        {

            for (XsdField field : GetFields())
            {
                if (field.GetKey().equals(key))
                {
                    return field;
                }
            }

            return null;

        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public XsdField GetFieldByName(String name)
    {
        try
        {

            for (XsdField field : GetFields())
            {
                if (field.GetName().equals(name))
                {
                    return field;
                }
            }

            return null;

        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public XsdField GetFieldByIndex(int Index)
    {
        try
        {

            return GetFields().get(Index);

        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public String GetFieldValue(String fieldName) throws InvalidFieldException
    {
        try
        {
            XsdField field = GetFieldByName(fieldName);

            // Do we have this Field?
            if (field != null)
            {
                // Yes; return Value;
                return field.GetValue();
            }
            else
            {
                throw new InvalidFieldException(fieldName);
            }

        }
        catch (Exception ex)
        {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);

            return "";
        }
    }

    public boolean GetBooleanFieldValue(String fieldName) throws InvalidFieldException
    {
        XsdField field = GetFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            // Yes; Set Value

            boolean value = field.GetBooleanValue();

            return value;

        }
        else
        {
            throw new InvalidFieldException(fieldName);
        }
    }

    public int GetIntegerFieldValue(String fieldName) throws InvalidFieldException
    {
        XsdField field = GetFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            // Yes; Set Value
            int value = field.GetIntegerValue();

            return value;

        }
        else
        {
            throw new InvalidFieldException(fieldName);
        }
    }

    public DateTime GetDateTimeFieldValue(String fieldName) throws InvalidFieldException
    {
        XsdField field = GetFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            // Yes; Set Value
            DateTime value = field.GetDateCreated();

            return value;

        }
        else
        {
            throw new InvalidFieldException(fieldName);
        }
    }

    public byte[] GetByteArrayFieldValue(String fieldName) throws InvalidFieldException
    {
        XsdField field = GetFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            // Yes; Set Value
            byte[] value = field.GetByteArrayValue();

            return value;

        }
        else
        {
            throw new InvalidFieldException(fieldName);
        }
    }

    public String GetFieldValueAsString(String fieldName, String defaultValue)
    {
        try
        {
            String value = GetFieldValue(fieldName);

            return value;

        }
        catch (InvalidFieldException ife)
        {
            return defaultValue;
        }
    }

    public boolean GetFieldValueAsBoolean(String fieldName, boolean defaultValue)
    {
        try
        {
            Boolean value = GetBooleanFieldValue(fieldName);

            return value;

        }
        catch (InvalidFieldException ife)
        {
            return defaultValue;
        }
    }

    public int GetFieldValueAsInteger(String fieldName, int defaultValue)
    {
        try
        {
            int value = GetIntegerFieldValue(fieldName);

            return value;

        }
        catch (InvalidFieldException ife)
        {
            return defaultValue;
        }
    }

    public DateTime GetFieldValueAsDate(String fieldName, DateTime defaultValue)
    {
        try
        {
            DateTime value = GetDateTimeFieldValue(fieldName);

            return value;

        }
        catch (InvalidFieldException ife)
        {
            return defaultValue;
        }
    }

    public byte[] GetFieldValueAsByteArray(String fieldName, byte[] defaultValue)
    {
        try
        {
            byte[] value = GetByteArrayFieldValue(fieldName);

            return value;

        }
        catch (InvalidFieldException ife)
        {
            return defaultValue;
        }
    }

    public CallResult SetFieldValue(String fieldName, String value)
    {
        try
        {
            XsdField field = GetFieldByName(fieldName);

            // Do we have this Field?
            if (field != null)
            {

                field.SetValue(value);

                return CallResult.successCallResult;

            }
            else
            {
                return new CallResult(CallResults.FAILED, "Field not found.", this);
            }

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetFieldValue(String fieldName, boolean value)
    {
        try
        {
            XsdField field = GetFieldByName(fieldName);

            // Do we have this Field?
            if (field != null)
            {
                return field.SetTypedValue(value);
            }
            else
            {
                return new CallResult(CallResults.FAILED, "Field not found.", this);
            }

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetFieldValue(String fieldName, int value)
    {
        try
        {
            XsdField field = GetFieldByName(fieldName);

            // Do we have this Field?
            if (field != null)
            {
                return field.SetTypedValue(value);
            }
            else
            {
                return new CallResult(CallResults.FAILED, "Field not found.", this);
            }

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetFieldValue(String fieldName, DateTime value)
    {
        try
        {
            XsdField field = GetFieldByName(fieldName);

            // Do we have this Field?
            if (field != null)
            {
                return field.SetTypedValue(value);
            }
            else
            {
                // return Failed Error
                return new CallResult(CallResults.FAILED, "Field not found.", this);
            }

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetFieldValue(String fieldName, byte[] value)
    {
        try
        {
            return SetFieldValue(fieldName, value, "");
        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult SetFieldValue(String fieldName, byte[] value, String fileName)
    {
        try
        {
            XsdField field = GetFieldByName(fieldName);

            // Do we have this Field?
            if (field != null)
            {

                if (fileName.equals(""))
                {
                    return field.SetTypedValue(value);
                }
                else
                {
                    return field.SetTypedValue(value, "{" + fileName + "}", ".jpg", "");
                }

            }
            else
            {
                return new CallResult(CallResults.FAILED, "Field not found.", this);
            }

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public Boolean HasField(String name)
    {
        try
        {
            // Find Field
            // For Each f As XsdField In this.GetFields
            for (int i = 0; i < GetFields().size(); i++)
            {
                if (GetFields().get(i).GetName().equals(name)) return true;
            }

            return false;

        }
        catch (Exception ex)
        {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);

            return false;
        }
    }

    public String ToXml()
    {
        return XmlHelper.Serialize(_entityRecord);
    }

    @Override
    public DateTime GetDateCreated()
    {
        // return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityRecord.getDatecreated());
        return _entityRecord.getDatecreated();
    }

    @Override
    public void SetDateCreated(DateTime value)
    {
        // _entityRecord.setDatecreated(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entityRecord.setDatecreated(value);
    }

    @Override
    public DateTime GetLastModified()
    {
        // return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityRecord.getLastmodified());
        return _entityRecord.getLastmodified();
    }

    @Override
    protected void SetObjectLastModified(DateTime value)
    {
        // _entityRecord.setLastmodified(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entityRecord.setLastmodified(value);
    }

    // -----------------------------------------------------------------------//
    // Protected Methods
    // -----------------------------------------------------------------------//

    @Override
    protected String GetObjectStatus()
    {
        return _entityRecord.getStatus();
    }

    @Override
    protected void SetObjectStatus(String status)
    {
        _entityRecord.setStatus(status);
    }

    protected List<Field> GetEntityFields()
    {
        return _entityRecord.getField();
    }

    @Override
    protected Map<QName, String> getAttributes()
    {
        return this._entityRecord.getOtherAttributes();
    }
}
