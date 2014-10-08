package com.incadencecorp.coalesce.framework.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.lang.NullArgumentException;
import org.joda.time.DateTime;

import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;
import com.incadencecorp.coalesce.framework.generatedjaxb.Entity.Section.Recordset.Record;
import com.incadencecorp.coalesce.framework.generatedjaxb.Entity.Section.Recordset.Record.Field;

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

    public CoalesceRecord()
    {
        // Do Nothing
    }

    public CoalesceRecord(CoalesceRecord record)
    {
        // Copy Member Variables
        _entityRecord = record._entityRecord;
        _parent = record._parent;
        _childDataObjects = record._childDataObjects;
    }

    /**
     * Creates an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord} and ties it to its parent
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset} the Recordset that this new
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord} will belong to
     * @param name {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord} name attribute
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord} the new
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord}
     */
    public static CoalesceRecord create(CoalesceRecordset parent, String name)
    {
        if (parent == null) throw new NullArgumentException("parent");
        if (name == null) throw new NullArgumentException("name");
        if (StringHelper.isNullOrEmpty(name)) throw new IllegalArgumentException("name cannot be an empty string");

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
     * Initializes a this {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord} based on a Record and ties it
     * to its parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset} the Recordset that this new
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord} will belong to
     * @param record Record that this {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord} will be based on
     * @return boolean indicator of success/failure
     */
    protected boolean initialize(CoalesceRecordset parent, Record record)
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
    protected void setObjectKey(String value)
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
     * Returns a list of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}s that belong to this
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord}
     * 
     * @return List<CoalesceField> list of {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}s contained by
     *         this {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord}
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
     * Returns a String list of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord}'s Field's names
     * 
     * @return List<String> list of the field names from the
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}s contained by this
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord}
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
     * Returns a String list of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord}'s Field's keys
     * 
     * @return List<String> list of the field keys from the
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}s contained by this
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord}
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
     * Returns an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField} with the specified
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord}'s Field's key
     * 
     * @param key String of the desired Field's key
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField} that has the key parameter. Null if not
     *         present.
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
     * Returns an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField} with the specified
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord}'s Field's name
     * 
     * @param name String of the desired Field name
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField} that has the name parameter. Null if not
     *         present.
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
     * Returns boolean indicator of the existence of the specified Field's name within the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord}
     * 
     * @param name String of the desired Field name
     * @return boolean indication that the Record does/does not have the named Field in its collection
     */
    public Boolean hasField(String name)
    {
        return (getFieldByName(name) != null);
    }

    @Override
    public String toXml()
    {
        return XmlHelper.serialize(_entityRecord);
    }

    @Override
    public DateTime getDateCreated()
    {
         return _entityRecord.getDatecreated();
    }

    @Override
    public void setDateCreated(DateTime value)
    {
        _entityRecord.setDatecreated(value);
    }

    @Override
    public DateTime getLastModified()
    {
        return _entityRecord.getLastmodified();
    }

    @Override
    protected void setObjectLastModified(DateTime value)
    {
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
                getCastParent().remove(getKey());
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
