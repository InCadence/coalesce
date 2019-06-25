package com.incadencecorp.coalesce.framework.datamodel;

import java.util.ArrayList;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.incadencecorp.coalesce.api.ICoalesceFieldDefinitionFactory;
import com.incadencecorp.coalesce.common.helpers.StringHelper;

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

public class CoalesceRecord extends CoalesceObjectHistory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoalesceRecord.class);

    // -----------------------------------------------------------------------//
    // Private Member Variables
    // -----------------------------------------------------------------------//

    private Record _entityRecord;

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    /**
     * Class constructor. Creates a CoalesceRecord class.
     */
    public CoalesceRecord()
    {
        // Do Nothing
    }

    /**
     * Class constructor. Creates a CoalesceRecord class off of an existing
     * CoalesceRecord.
     * 
     * @param record
     */
    public CoalesceRecord(CoalesceRecord record)
    {
        super(record);

        // Copy Member Variables
        _entityRecord = record._entityRecord;
    }

    /**
     * Creates an {@link CoalesceRecord} and ties it to its parent
     * {@link CoalesceRecordset}.
     * 
     * @param parent {@link CoalesceRecordset} the Recordset that this new
     *            {@link CoalesceRecord} will belong to.
     * @param name {@link CoalesceRecord} name attribute.
     * @return {@link CoalesceRecord} the new {@link CoalesceRecord} .
     */
    public static CoalesceRecord create(CoalesceRecordset parent, String name)
    {
        if (parent == null)
            throw new IllegalArgumentException("parent");
        if (name == null)
            throw new IllegalArgumentException("name");
        if (StringHelper.isNullOrEmpty(name))
            throw new IllegalArgumentException("name cannot be an empty string");

        Record newEntityRecord = new Record();
        parent.getEntityRecords().add(newEntityRecord);

        CoalesceRecord newRecord = new CoalesceRecord();
        if (!newRecord.initialize(parent, newEntityRecord))
            return null;

        for (CoalesceFieldDefinition fieldDefinition : parent.getFieldDefinitions())
        {
            // Creates New Field
            CoalesceField.create(newRecord, fieldDefinition);
        }

        newRecord.setName(name);
        newRecord.setObjectVersion(parent.getEntity().getObjectVersion());

        //parent.addChildCoalesceObject(newRecord);

        return newRecord;
    }

    /**
     * Initializes a this {@link CoalesceRecord} based on a Record and ties it
     * to its parent {@link CoalesceRecordset}.
     * 
     * @param parent {@link CoalesceRecordset} the Recordset that this new
     *            {@link CoalesceRecord} will belong to.
     * @param record Record that this {@link CoalesceRecord} will be based on.
     * @return boolean indicator of success/failure.
     */
    protected boolean initialize(CoalesceRecordset parent, Record record)
    {
        if (parent == null)
            throw new IllegalArgumentException("parent");
        if (record == null)
            throw new IllegalArgumentException("record");

        // Set References
        setParent(parent);
        _entityRecord = record;

        super.initialize(_entityRecord);

        for (Field entityField : record.getField())
        {
            CoalesceFieldDefinition definition = this.getCastParent().getFieldDefinition(entityField.getName());

            if (definition != null)
            {
                CoalesceField<?> newField = CoalesceField.createTypeField(definition.getDataType());
                if (!newField.initialize(this, definition, entityField))
                    return false;

                // Add to Child Collection
                addChildCoalesceObject(newField);
            }
            else
            {
                LOGGER.warn("Failed to located defintion: {}", entityField.getName());
            }
        }

        // Add to Parent Collections (if we're Active)
        // if (getStatus() == ECoalesceObjectStatus.ACTIVE)
        {
            parent.addChildCoalesceObject(this);
        }

        return true;

    }

    // -----------------------------------------------------------------------//
    // Public Methods
    // -----------------------------------------------------------------------//

    /**
     * Returns a list of the {@link CoalesceField}s that belong to this
     * {@link CoalesceRecord}.
     * 
     * @return List&lt;CoalesceField&gt; list of {@link CoalesceField} s contained by
     *         this {@link CoalesceRecord} .
     */
    public List<CoalesceField> getFields()
    {
        return getObjectsAsList(CoalesceField.class);
    }

    /**
     * Returns a String list of the {@link CoalesceRecord}'s Field's names.
     * 
     * @return List&lt;String&gt; list of the field names from the
     *         {@link CoalesceField} s contained by this {@link CoalesceRecord}
     *         .
     */
    @JsonIgnore
    public List<String> getFieldNames()
    {
        List<String> fieldNames = new ArrayList<>();

        for (CoalesceObject coalesceObject : getChildCoalesceObjects().values())
        {
            if (coalesceObject instanceof CoalesceField<?>)
            {
                fieldNames.add(coalesceObject.getName());
            }
        }

        return fieldNames;
    }

    /**
     * Returns a String list of the {@link CoalesceRecord}'s Field's keys.
     * 
     * @return List&lt;String&gt; list of the field keys from the
     *         {@link CoalesceField} s contained by this {@link CoalesceRecord}
     *         .
     */
    @JsonIgnore
    public List<String> getFieldKeys()
    {
        List<String> fieldKeys = new ArrayList<>();

        for (CoalesceObject coalesceObject : getChildCoalesceObjects().values())
        {
            if (coalesceObject instanceof CoalesceField<?>)
            {
                fieldKeys.add(coalesceObject.getKey());
            }
        }

        return fieldKeys;
    }

    /**
     * Returns an {@link CoalesceField} with the specified
     * {@link CoalesceRecord}'s Field's key.
     * 
     * @param key String of the desired Field's key.
     * @return {@link CoalesceField} that has the key parameter. Null if not
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
     * Returns an {@link CoalesceField} with the specified
     * {@link CoalesceRecord}'s Field's name.
     * 
     * @param name String of the desired Field name.
     * @return {@link CoalesceField} that has the name parameter. Null if not
     *         present.
     */
    public CoalesceField<?> getFieldByName(String name)
    {
        return getFieldByName(name, null);
    }

    /**
     * Returns an {@link CoalesceField} with the specified
     * {@link CoalesceRecord}'s Field's name.
     * 
     * @param name String of the desired Field name.
     * @param factory used to create the field definition if the field is
     *            missing.
     * @return {@link CoalesceField} that has the name parameter. Creates the
     *         field if not present.
     */
    public CoalesceField<?> getFieldByName(String name, ICoalesceFieldDefinitionFactory factory)
    {
        CoalesceField<?> result = null;

        for (CoalesceField<?> field : getFields())
        {
            if (field.getName().equalsIgnoreCase(name))
            {
                result = field;
                break;
            }
        }

        if (result == null)
        {
            CoalesceFieldDefinition fd = null;

            if (factory != null)
            {
                fd = factory.create(this.getCastParent(), name);
            }
            else
            {
                fd = this.getCastParent().getFieldDefinition(name);
            }

            if (fd != null)
            {
                result = CoalesceField.create(this, fd);
            }
        }

        return result;
    }

    /**
     * Returns boolean indicator of the existence of the specified Field's name
     * within the {@link CoalesceRecord}.
     * 
     * @param name String of the desired Field name.
     * @return boolean indication that the Record does/does not have the named
     *         Field in its collection.
     */
    public Boolean hasField(String name)
    {
        boolean found = false;
        
        for (CoalesceField<?> field : getFields())
        {
            if (field.getName().equalsIgnoreCase(name))
            {
                found = true;
                break;
            }
        }
        
        return found;
    }

    // -----------------------------------------------------------------------//
    // Protected Methods
    // -----------------------------------------------------------------------//

    protected List<Field> getEntityFields()
    {
        return _entityRecord.getField();
    }

    @Override
    protected boolean prune(CoalesceObjectType child)
    {
        boolean isSuccessful = false;

        if (child instanceof History)
        {
            isSuccessful = _entityRecord.getHistory().remove(child);
        }
        else if (child instanceof Field)
        {
            isSuccessful = _entityRecord.getField().remove(child);
        }

        return isSuccessful;
    }

    @Override
    protected boolean setExtendedAttributes(String name, String value)
    {
        return setOtherAttribute(name, value);
    }

    private CoalesceRecordset getCastParent()
    {
        return (CoalesceRecordset) getParent();
    }

}
