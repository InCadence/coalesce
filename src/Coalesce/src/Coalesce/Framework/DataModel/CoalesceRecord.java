package Coalesce.Framework.DataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.lang.NullArgumentException;
import org.joda.time.DateTime;

import Coalesce.Common.Exceptions.CoalesceDataFormatException;
import Coalesce.Common.Exceptions.CoalesceException;
import Coalesce.Common.Exceptions.CoalesceInvalidFieldException;
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

public class CoalesceRecord extends CoalesceDataObject {

    // -----------------------------------------------------------------------//
    // Private Member Variables
    // -----------------------------------------------------------------------//

    private Record _entityRecord;

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    /**
     * Creates an XsdRecord and ties it to its parent XsdRecordset.
     * 
     * @param parent XsdRecordset the Recordset that this new XsdRecord will belong to
     * @param name XsdRecord name attribute
     * @return XsdRecord the new XsdRecord
     */
    public static CoalesceRecord create(CoalesceRecordset parent, String name)
    {
        if (parent == null) throw new NullArgumentException("parent");
        if (name == null) throw new NullArgumentException("name");
        if (StringHelper.isNullOrEmpty(name.trim())) throw new IllegalArgumentException("name cannot be an empty string");

        Record newEntityRecord = new Record();
        parent.GetEntityRecords().add(newEntityRecord);

        CoalesceRecord newRecord = new CoalesceRecord();
        if (!newRecord.initialize(parent, newEntityRecord)) return null;

        for (CoalesceFieldDefinition fieldDefinition : parent.getFieldDefinitions())
        {
            // Creates New Field
            CoalesceField.create(newRecord, fieldDefinition);
        }

        newRecord.setName(name);

        // Add to parent's child collection
        if (!parent._childDataObjects.containsKey(newRecord.getKey()))
        {
            parent._childDataObjects.put(newRecord.getKey(), newRecord);
        }

        return newRecord;
    }

    /**
     * Initializes a this XsdRecord based on a Record and ties it to its parent XsdRecordset.
     * 
     * @param parent XsdRecordset the Recordset that this new XsdRecord will belong to
     * @param record Record that this XsdRecord will be based on
     * @return boolean indicator of success/failure
     */
    public boolean initialize(CoalesceRecordset parent, Record record)
    {
        if (parent == null) throw new NullArgumentException("parent");
        if (record == null) throw new NullArgumentException("record");

        // Set References
        _parent = parent;
        _entityRecord = record;

        super.initialize();

        for (Field entityField : record.getField())
        {
            CoalesceField<?> newField = CoalesceField.createTypeField(ECoalesceFieldDataTypes.getTypeForCoalesceType(entityField.getDatatype()));
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

    /**
     * Returns a list of the XsdFields that belong to this XsdRecord
     * 
     * @return List<XsdField> list of XsdFields contained by this XsdRecord
     */
    public List<CoalesceField<?>> getFields()
    {
        List<CoalesceField<?>> fields = new ArrayList<CoalesceField<?>>();

        for (CoalesceDataObject dataObject : _childDataObjects.values())
        {

            if (dataObject instanceof CoalesceField<?>)
            {
                fields.add((CoalesceField<?>) dataObject);
            }
        }

        return fields;
    }

    /**
     * Returns a String list of the XsdRecord's Field's names
     * 
     * @return List<String> list of the field names from the XsdFields contained by this XsdRecord
     */
    public List<String> getFieldNames()
    {
        List<String> fieldNames = new ArrayList<String>();

        for (CoalesceDataObject dataObject : _childDataObjects.values())
        {
            if (dataObject instanceof CoalesceField<?>)
            {
                fieldNames.add(dataObject.getName());
            }
        }

        return fieldNames;
    }

    /**
     * Returns a String list of the XsdRecord's Field's keys
     * 
     * @return List<String> list of the field keys from the XsdFields contained by this XsdRecord
     */
    public List<String> getFieldKeys()
    {
        List<String> fieldKeys = new ArrayList<String>();

        for (CoalesceDataObject dataObject : _childDataObjects.values())
        {
            if (dataObject instanceof CoalesceField<?>)
            {
                fieldKeys.add(dataObject.getKey());
            }
        }

        return fieldKeys;
    }

    /**
     * Returns an XsdField with the specified XsdRecord's Field's key
     * 
     * @param key String of the desired Field's key
     * @return XsdField that has the key parameter. Null if not present.
     */
    public CoalesceField<?> getFieldByKey(String key)
    {
        for (CoalesceField<?> field : getFields())
        {
            if (field.getKey().equals(key))
            {
                return field;
            }
        }

        return null;
    }

    /**
     * Returns an XsdField with the specified XsdRecord's Field's name
     * 
     * @param name String of the desired Field name
     * @return XsdField that has the name parameter. Null if not present.
     */
    public CoalesceField<?> getFieldByName(String name)
    {
        for (CoalesceField<?> field : getFields())
        {
            if (field.getName().equals(name))
            {
                return field;
            }
        }

        return null;
    }

    /**
     * Returns a String value with the specified XsdRecord's Field's name
     * 
     * @param fieldName String of the desired Field name
     * @return String value of the desired Field
     * @throws CoalesceException
     */
    public String getFieldValue(String fieldName) throws CoalesceException
    {
        CoalesceField<?> field = getFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            // Yes; return Value;
            return field.getBaseValue();
        }
        else
        {
            throw new CoalesceInvalidFieldException(fieldName);
        }
    }

    /**
     * Returns a boolean value with the specified XsdRecord's Field's name
     * 
     * @param fieldName String of the desired Field name
     * @return boolean value of the desired Field
     * @throws CoalesceException
     */
    public boolean getBooleanFieldValue(String fieldName) throws CoalesceException
    {
        CoalesceField<?> field = getFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            // Yes; Set Value

            boolean value = field.getBooleanValue();

            return value;

        }
        else
        {
            throw new CoalesceInvalidFieldException(fieldName);
        }
    }

    /**
     * Returns a int value with the specified XsdRecord's Field's name
     * 
     * @param fieldName String of the desired Field name
     * @return integer value of the desired Field
     * @throws CoalesceException
     */
    public int getIntegerFieldValue(String fieldName) throws CoalesceException
    {
        CoalesceField<?> field = getFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            // Yes; Set Value
            int value = field.getIntegerValue();

            return value;

        }
        else
        {
            throw new CoalesceInvalidFieldException(fieldName);
        }
    }

    /**
     * Returns a DateTime value with the specified XsdRecord's Field's name
     * 
     * @param fieldName String of the desired Field name
     * @return DateTime value of the desired Field
     * @throws CoalesceException
     */
    public DateTime getDateTimeFieldValue(String fieldName) throws CoalesceException
    {
        CoalesceField<?> field = (CoalesceField<?>) getFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            // Yes; Set Value
            DateTime value = field.getDateTimeValue();

            if (value == null) throw new CoalesceDataFormatException("Failed to parse Datetime value for: " + fieldName);

            return value;

        }
        else
        {
            throw new CoalesceInvalidFieldException(fieldName);
        }
    }

    /**
     * Returns a (binary) byte[] value with the specified XsdRecord's Field's name
     * 
     * @param fieldName String of the desired Field name
     * @return byte[] value of the desired Field
     * @throws CoalesceException
     */
    public byte[] getBinaryFieldValue(String fieldName) throws CoalesceException
    {
        CoalesceField<?> field = (CoalesceField<?>) getFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            // Yes; Set Value
            byte[] value = field.getBinaryValue();

            return value;

        }
        else
        {
            throw new CoalesceInvalidFieldException(fieldName);
        }
    }

    /**
     * Returns a String default value for the specified XsdRecord's Field's name
     * 
     * @param fieldName String of the desired Field name
     * @param defaultValue, value returned if fieldName parameter does not have a field
     * @return String value of the desired Field or the defaultValue parameter if fieldName parameter does not have a field
     */
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

    /**
     * Returns a boolean default value for the specified XsdRecord's Field's name
     * 
     * @param fieldName String of the desired Field name
     * @param defaultValue, value returned if fieldName parameter does not have a field
     * @return boolean value of the desired Field or the defaultValue parameter if fieldName parameter does not have a field
     */
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

    /**
     * Returns a int default value for the specified XsdRecord's Field's name
     * 
     * @param fieldName String of the desired Field name
     * @param defaultValue, value returned if fieldName parameter does not have a field
     * @return integer value of the desired Field or the defaultValue parameter if fieldName parameter does not have a field
     */
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

    /**
     * Returns a DateTime default value for the specified XsdRecord's Field's name
     * 
     * @param fieldName String of the desired Field name
     * @param defaultValue, value returned if fieldName parameter does not have a field
     * @return DateTime value of the desired Field or the defaultValue parameter if fieldName parameter does not have a field
     */
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

    /**
     * Returns a (binary) byte[] default value for the specified XsdRecord's Field's name
     * 
     * @param fieldName String of the desired Field name
     * @param defaultValue, value returned if fieldName parameter does not have a field
     * @return byte[] value of the desired Field or the defaultValue parameter if fieldName parameter does not have a field
     */
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

    /**
     * Set a String Field's value with the specified XsdRecord's Field's name
     * 
     * @param fieldName String of the desired Field name
     * @param value String, new value for the field
     * @throws CoalesceException
     */
    public void setFieldValue(String fieldName, String value) throws CoalesceException
    {
        CoalesceField<?> field = (CoalesceField<?>) getFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            field.setBaseValue(value);
        }
        else
        {
            throw new CoalesceInvalidFieldException(fieldName);
        }
    }

    /**
     * Set a boolean Field's value with the specified XsdRecord's Field's name
     * 
     * @param fieldName String of the desired Field name
     * @param value boolean, new value for the field
     * @throws CoalesceException
     */
    public void setFieldValue(String fieldName, boolean value) throws CoalesceException
    {
        CoalesceField<?> field = (CoalesceField<?>) getFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            field.setTypedValue(value);
        }
        else
        {
            throw new CoalesceInvalidFieldException(fieldName);
        }
    }

    /**
     * Set a int Field's value with the specified XsdRecord's Field's name
     * 
     * @param fieldName String of the desired Field name
     * @param value integer, new value for the field
     * @throws CoalesceException
     */
    public void setFieldValue(String fieldName, int value) throws CoalesceException
    {
        CoalesceField<?> field = (CoalesceField<?>) getFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            field.setTypedValue(value);
        }
        else
        {
            throw new CoalesceInvalidFieldException(fieldName);
        }
    }

    /**
     * Set a DateTime Field's value with the specified XsdRecord's Field's name
     * 
     * @param fieldName String of the desired Field name
     * @param value DateTime, new value for the field
     * @throws CoalesceException
     */
    public void setFieldValue(String fieldName, DateTime value) throws CoalesceException
    {
        CoalesceField<?> field = (CoalesceField<?>) getFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {
            field.setTypedValue(value);
        }
        else
        {
            throw new CoalesceInvalidFieldException(fieldName);
        }
    }

    /**
     * Set a (binary) byte[] Field's value with the specified XsdRecord's Field's name
     * 
     * @param fieldName String of the desired Field name
     * @param value byte[], new value for the field
     * @throws CoalesceException
     */
    public void setFieldValue(String fieldName, byte[] value) throws CoalesceException
    {
        setFieldValue(fieldName, value, "");
    }

    /**
     * Set a (binary) byte[] Field's value with the specified XsdRecord's Field's name and file name
     * 
     * @param fieldName String of the desired Field name
     * @param value byte[], new value for the field
     * @param fileName String of the file name
     * @throws CoalesceException
     */
    public void setFieldValue(String fieldName, byte[] value, String fileName) throws CoalesceException
    {
        CoalesceField<?> field = (CoalesceField<?>) getFieldByName(fieldName);

        // Do we have this Field?
        if (field != null)
        {

            if (fileName == null || StringHelper.isNullOrEmpty(fileName.trim()))
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
            throw new CoalesceInvalidFieldException(fieldName);
        }

    }

    /**
     * Returns boolean indicator of the existence of the specified Field's name within the XsdRecord
     * 
     * @param name String of the desired Field name
     * @return boolean indication that the Record does/does not have the named Field in its collection
     */
    public Boolean hasField(String name)
    {

        CoalesceField<?> field = (CoalesceField<?>) getFieldByName(name);

        return (field != null);

    }

    @Override
    public String toXml()
    {
        return XmlHelper.serialize(_entityRecord);
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
            _entityRecord.setDatecreated(JodaDateTimeHelper.fromXmlDateTimeUTC(value));
            return true;
        case "lastmodified":
            _entityRecord.setLastmodified(JodaDateTimeHelper.fromXmlDateTimeUTC(value));
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
        map.put(new QName("datecreated"), JodaDateTimeHelper.toXmlDateTimeUTC(_entityRecord.getDatecreated()));
        map.put(new QName("lastmodified"), JodaDateTimeHelper.toXmlDateTimeUTC(_entityRecord.getLastmodified()));
        map.put(new QName("status"), _entityRecord.getStatus());
        map.put(new QName("name"), _entityRecord.getName());
        return map;
    }

    private CoalesceRecordset getCastParent()
    {
        return (CoalesceRecordset) _parent;
    }

}
