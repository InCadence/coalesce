package Coalesce.Framework.DataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;

import Coalesce.Common.Exceptions.CoalesceException;
import Coalesce.Common.Exceptions.InvalidFieldException;
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

    // -----------------------------------------------------------------------//
    // protected Member Variables
    // -----------------------------------------------------------------------//

    private Record _entityRecord;

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    public static XsdRecord Create(XsdRecordset parent, String name)
    {
        Record newEntityRecord = new Record();
        parent.GetEntityRecords().add(newEntityRecord);

        XsdRecord newRecord = new XsdRecord();
        if (!newRecord.Initialize(parent, newEntityRecord)) return null;

        for (XsdFieldDefinition fieldDefinition : parent.getFieldDefinitions())
        {
            // Creates New Field
            XsdField.Create(newRecord, fieldDefinition);
        }

        newRecord.setName(name);

        // Add to parent's child collection
        if (!parent._childDataObjects.containsKey(newRecord.getKey()))
        {
            parent._childDataObjects.put(newRecord.getKey(), newRecord);
        }

        return newRecord;
    }

    public boolean Initialize(XsdRecordset parent, Record record)
    {

        // Set References
        _parent = parent;
        _entityRecord = record;

        super.initialize();

        for (Field entityField : record.getField())
        {
            XsdField newField = new XsdField();
            if (!newField.Initialize(this, entityField)) return false;

            // Add to Child Collection
            _childDataObjects.put(newField.getKey(), newField);
        }

        // Add to Parent Collections (if we're Active)
        if (getStatus() == ECoalesceDataObjectStatus.ACTIVE)
        {
            parent._childDataObjects.put(getKey(), this);
            parent.getRecords().add(this);
        }

        return true;

    }

    // -----------------------------------------------------------------------//
    // Public Methods
    // -----------------------------------------------------------------------//

    @Override
    protected String getObjectKey()
    {
        return _entityRecord.getKey();
    }

    @Override
    public void setObjectKey(String value)
    {
        _entityRecord.setKey(value);
    }

    @Override
    public String getName()
    {
        return _entityRecord.getName();
    }

    @Override
    public void setName(String value)
    {
        _entityRecord.setName(value);
    }

    @Override
    public String getType()
    {
        return "record";
    }

    public ArrayList<XsdField> GetFields()
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

    public ArrayList<String> GetFieldNames()
    {
        ArrayList<String> fieldNames = new ArrayList<String>();

        for (XsdDataObject dataObject : _childDataObjects.values())
        {
            if (dataObject instanceof XsdField)
            {
                fieldNames.add(dataObject.getName());
            }
        }

        return fieldNames;
    }

    public ArrayList<String> GetFieldKeys()
    {
        ArrayList<String> fieldKeys = new ArrayList<String>();

        for (XsdDataObject dataObject : _childDataObjects.values())
        {
            if (dataObject instanceof XsdField)
            {
                fieldKeys.add(dataObject.getKey());
            }
        }

        return fieldKeys;
    }

    public XsdField GetFieldByKey(String key)
    {
        for (XsdField field : GetFields())
        {
            if (field.getKey().equals(key))
            {
                return field;
            }
        }

        return null;
    }

    public XsdField GetFieldByName(String name)
    {
        for (XsdField field : GetFields())
        {
            if (field.getName().equals(name))
            {
                return field;
            }
        }

        return null;
    }

    public XsdField GetFieldByIndex(int Index)
    {
        return GetFields().get(Index);
    }

    public String GetFieldValue(String fieldName) throws CoalesceException 
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

    public boolean GetBooleanFieldValue(String fieldName) throws CoalesceException
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

    public int GetIntegerFieldValue(String fieldName) throws CoalesceException
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

    public DateTime GetDateTimeFieldValue(String fieldName) throws CoalesceException
    {
        XsdField field = GetFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            // Yes; Set Value
            DateTime value = field.getDateCreated();

            return value;

        }
        else
        {
            throw new InvalidFieldException(fieldName);
        }
    }

    public byte[] GetBinaryFieldValue(String fieldName) throws CoalesceException
    {
        XsdField field = GetFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            // Yes; Set Value
            byte[] value = field.GetBinaryValue();

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
        catch (CoalesceException ife)
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
        catch (CoalesceException ife)
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
        catch (CoalesceException ife)
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
        catch (CoalesceException ife)
        {
            return defaultValue;
        }
    }

    public byte[] GetFieldValueAsByteArray(String fieldName, byte[] defaultValue)
    {
        try
        {
            byte[] value = GetBinaryFieldValue(fieldName);

            return value;

        }
        catch (CoalesceException ife)
        {
            return defaultValue;
        }
    }

    public void SetFieldValue(String fieldName, String value) throws CoalesceException
    {
        XsdField field = GetFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            field.SetValue(value);
        }
        else
        {
            throw new InvalidFieldException(fieldName);
        }
    }

    public void SetFieldValue(String fieldName, boolean value) throws CoalesceException
    {
        XsdField field = GetFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            field.SetTypedValue(value);
        }
        else
        {
            throw new InvalidFieldException(fieldName);
        }
    }

    public void SetFieldValue(String fieldName, int value) throws CoalesceException
    {
        XsdField field = GetFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            field.SetTypedValue(value);
        }
        else
        {
            throw new InvalidFieldException(fieldName);
        }
    }

    public void SetFieldValue(String fieldName, DateTime value) throws CoalesceException
    {
        XsdField field = GetFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            field.SetTypedValue(value);
        }
        else
        {
            throw new InvalidFieldException(fieldName);
        }
    }

    public void SetFieldValue(String fieldName, byte[] value) throws CoalesceException
    {
        SetFieldValue(fieldName, value, "");
    }

    public void SetFieldValue(String fieldName, byte[] value, String fileName) throws CoalesceException
    {
        XsdField field = GetFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {

            if (fileName.equals(""))
            {
                field.SetTypedValue(value);
            }
            else
            {
                field.SetTypedValue(value, "{" + fileName + "}", ".jpg", "");
            }

        }
        else
        {
            throw new InvalidFieldException(fieldName);
        }

    }

    public Boolean HasField(String name)
    {
        // Find Field
        // For Each f As XsdField In this.GetFields
        for (int i = 0; i < GetFields().size(); i++)
        {
            if (GetFields().get(i).getName().equals(name)) return true;
        }

        return false;
    }

    public String toXml()
    {
        return XmlHelper.Serialize(_entityRecord);
    }

    @Override
    public DateTime getDateCreated()
    {
        // return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityRecord.getDatecreated());
        return _entityRecord.getDatecreated();
    }

    @Override
    public void setDateCreated(DateTime value)
    {
        // _entityRecord.setDatecreated(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entityRecord.setDatecreated(value);
    }

    @Override
    public DateTime getLastModified()
    {
        // return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityRecord.getLastmodified());
        return _entityRecord.getLastmodified();
    }

    @Override
    protected void setObjectLastModified(DateTime value)
    {
        // _entityRecord.setLastmodified(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entityRecord.setLastmodified(value);
    }

    // -----------------------------------------------------------------------//
    // Protected Methods
    // -----------------------------------------------------------------------//

    @Override
    protected String getObjectStatus()
    {
        return _entityRecord.getStatus();
    }

    @Override
    protected void setObjectStatus(String status)
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
