package Coalesce.Framework.DataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.lang.NullArgumentException;
import org.joda.time.DateTime;

import Coalesce.Common.Exceptions.CoalesceException;
import Coalesce.Common.Exceptions.DataFormatException;
import Coalesce.Common.Exceptions.InvalidFieldException;
import Coalesce.Common.Helpers.JodaDateTimeHelper;
import Coalesce.Common.Helpers.StringHelper;
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
    // Private Member Variables
    // -----------------------------------------------------------------------//

    private Record _entityRecord;

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    public static XsdRecord create(XsdRecordset parent, String name)
    {
        if (parent == null) throw new NullArgumentException("parent");
        if (name == null) throw new NullArgumentException("name");
        if (StringHelper.IsNullOrEmpty(name.trim())) throw new IllegalArgumentException("name cannot be an empty string");

        Record newEntityRecord = new Record();
        parent.GetEntityRecords().add(newEntityRecord);

        XsdRecord newRecord = new XsdRecord();
        if (!newRecord.initialize(parent, newEntityRecord)) return null;

        for (XsdFieldDefinition fieldDefinition : parent.getFieldDefinitions())
        {
            // Creates New Field
            XsdField.create(newRecord, fieldDefinition);
        }

        newRecord.setName(name);

        // Add to parent's child collection
        if (!parent._childDataObjects.containsKey(newRecord.getKey()))
        {
            parent._childDataObjects.put(newRecord.getKey(), newRecord);
        }

        return newRecord;
    }

    public boolean initialize(XsdRecordset parent, Record record)
    {
        if (parent == null) throw new NullArgumentException("parent");
        if (record == null) throw new NullArgumentException("record");

        // Set References
        _parent = parent;
        _entityRecord = record;

        super.initialize();

        for (Field entityField : record.getField())
        {
            XsdField newField = new XsdField();
            if (!newField.initialize(this, entityField)) return false;

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
        return getStringElement(_entityRecord.getName());
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

    public List<XsdField> getFields()
    {
        List<XsdField> fields = new ArrayList<XsdField>();

        for (XsdDataObject dataObject : _childDataObjects.values())
        {

            if (dataObject instanceof XsdField)
            {
                fields.add((XsdField) dataObject);
            }
        }

        return fields;
    }

    public List<String> getFieldNames()
    {
        List<String> fieldNames = new ArrayList<String>();

        for (XsdDataObject dataObject : _childDataObjects.values())
        {
            if (dataObject instanceof XsdField)
            {
                fieldNames.add(dataObject.getName());
            }
        }

        return fieldNames;
    }

    public List<String> getFieldKeys()
    {
        List<String> fieldKeys = new ArrayList<String>();

        for (XsdDataObject dataObject : _childDataObjects.values())
        {
            if (dataObject instanceof XsdField)
            {
                fieldKeys.add(dataObject.getKey());
            }
        }

        return fieldKeys;
    }

    public XsdField getFieldByKey(String key)
    {
        for (XsdField field : getFields())
        {
            if (field.getKey().equals(key))
            {
                return field;
            }
        }

        return null;
    }

    public XsdField getFieldByName(String name)
    {
        for (XsdField field : getFields())
        {
            if (field.getName().equals(name))
            {
                return field;
            }
        }

        return null;
    }

    public String getFieldValue(String fieldName) throws CoalesceException
    {
        XsdField field = getFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            // Yes; return Value;
            return field.getValue();
        }
        else
        {
            throw new InvalidFieldException(fieldName);
        }
    }

    public boolean getBooleanFieldValue(String fieldName) throws CoalesceException
    {
        XsdField field = getFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            // Yes; Set Value

            boolean value = field.getBooleanValue();

            return value;

        }
        else
        {
            throw new InvalidFieldException(fieldName);
        }
    }

    public int getIntegerFieldValue(String fieldName) throws CoalesceException
    {
        XsdField field = getFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            // Yes; Set Value
            int value = field.getIntegerValue();

            return value;

        }
        else
        {
            throw new InvalidFieldException(fieldName);
        }
    }

    public DateTime getDateTimeFieldValue(String fieldName) throws CoalesceException, DataFormatException
    {
        XsdField field = getFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            // Yes; Set Value
            DateTime value = field.getDateTimeValue();

            if (value == null) throw new DataFormatException("Failed to parse Datetime value for: " + fieldName);

            return value;

        }
        else
        {
            throw new InvalidFieldException(fieldName);
        }
    }

    public byte[] getBinaryFieldValue(String fieldName) throws CoalesceException
    {
        XsdField field = getFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            // Yes; Set Value
            byte[] value = field.getBinaryValue();

            return value;

        }
        else
        {
            throw new InvalidFieldException(fieldName);
        }
    }

    public String getFieldValueAsString(String fieldName, String defaultValue)
    {
        try
        {
            String value = getFieldValue(fieldName);

            return value;

        }
        catch (CoalesceException ife)
        {
            return defaultValue;
        }
    }

    public boolean getFieldValueAsBoolean(String fieldName, boolean defaultValue)
    {
        try
        {
            Boolean value = getBooleanFieldValue(fieldName);

            return value;

        }
        catch (CoalesceException ife)
        {
            return defaultValue;
        }
    }

    public int getFieldValueAsInteger(String fieldName, int defaultValue)
    {
        try
        {
            int value = getIntegerFieldValue(fieldName);

            return value;

        }
        catch (CoalesceException ife)
        {
            return defaultValue;
        }
    }

    public DateTime getFieldValueAsDate(String fieldName, DateTime defaultValue)
    {
        try
        {
            DateTime value = getDateTimeFieldValue(fieldName);

            return value;

        }
        catch (CoalesceException ife)
        {
            return defaultValue;
        }
    }

    public byte[] getFieldValueAsByteArray(String fieldName, byte[] defaultValue)
    {
        try
        {
            byte[] value = getBinaryFieldValue(fieldName);

            return value;

        }
        catch (CoalesceException ife)
        {
            return defaultValue;
        }
    }

    public void setFieldValue(String fieldName, String value) throws CoalesceException
    {
        XsdField field = getFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            field.setValue(value);
        }
        else
        {
            throw new InvalidFieldException(fieldName);
        }
    }

    public void setFieldValue(String fieldName, boolean value) throws CoalesceException
    {
        XsdField field = getFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            field.setTypedValue(value);
        }
        else
        {
            throw new InvalidFieldException(fieldName);
        }
    }

    public void setFieldValue(String fieldName, int value) throws CoalesceException
    {
        XsdField field = getFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            field.setTypedValue(value);
        }
        else
        {
            throw new InvalidFieldException(fieldName);
        }
    }

    public void setFieldValue(String fieldName, DateTime value) throws CoalesceException
    {
        XsdField field = getFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            field.setTypedValue(value);
        }
        else
        {
            throw new InvalidFieldException(fieldName);
        }
    }

    public void setFieldValue(String fieldName, byte[] value) throws CoalesceException
    {
        setFieldValue(fieldName, value, "");
    }

    public void setFieldValue(String fieldName, byte[] value, String fileName) throws CoalesceException
    {
        XsdField field = getFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {

            if (fileName == null || StringHelper.IsNullOrEmpty(fileName.trim()))
            {
                field.setTypedValue(value);
            }
            else
            {
                field.setTypedValue(value, "{" + fileName + "}", ".jpg", "");
            }

        }
        else
        {
            throw new InvalidFieldException(fieldName);
        }

    }

    public Boolean hasField(String name)
    {

        XsdField field = getFieldByName(name);

        return (field != null);

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
    protected void setObjectStatus(ECoalesceDataObjectStatus status)
    {
        if (status == getStatus()) return;

        _entityRecord.setStatus(status.getLabel());

        if (status == ECoalesceDataObjectStatus.ACTIVE)
        {
            if (!getCastParent().contains(this))
            {
                getCastParent().getRecords().add(this);
            }
        }
        else
        {

            if (getCastParent().contains(this))
            {
                getCastParent().Remove(getKey());
            }
        }

    }

    protected List<Field> getEntityFields()
    {
        return _entityRecord.getField();
    }

    @Override
    protected Map<QName, String> getOtherAttributes()
    {
        return this._entityRecord.getOtherAttributes();
    }

    @Override
    public boolean setAttribute(String name, String value)
    {
        switch (name) {
        case "key":
            _entityRecord.setKey(value);
            return true;
        case "datecreated":
            _entityRecord.setDatecreated(JodaDateTimeHelper.FromXmlDateTimeUTC(value));
            return true;
        case "lastmodified":
            _entityRecord.setLastmodified(JodaDateTimeHelper.FromXmlDateTimeUTC(value));
            return true;
        case "status":
            _entityRecord.setStatus(value);
            return true;
        case "name":
            _entityRecord.setName(value);
            return true;
        default:
            this.setOtherAttribute(name, value);
            return true;
        }
    }

    @Override
    protected Map<QName, String> getAttributes()
    {
        Map<QName, String> map = new HashMap<QName, String>();
        map.put(new QName("key"), _entityRecord.getKey());
        map.put(new QName("datecreated"), JodaDateTimeHelper.ToXmlDateTimeUTC(_entityRecord.getDatecreated()));
        map.put(new QName("lastmodified"), JodaDateTimeHelper.ToXmlDateTimeUTC(_entityRecord.getLastmodified()));
        map.put(new QName("status"), _entityRecord.getStatus());
        map.put(new QName("name"), _entityRecord.getName());
        return map;
    }

    private XsdRecordset getCastParent()
    {
        return (XsdRecordset) _parent;
    }

}
