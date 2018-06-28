package com.incadencecorp.coalesce.framework.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.incadencecorp.coalesce.api.Views;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import org.apache.commons.lang.NullArgumentException;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

/**
 * This is a container for records.
 *
 * @author n78554
 */
public class CoalesceRecordset extends CoalesceObjectHistory implements ICoalesceRecordset {

    public static final String ATTRIBUTE_RECORDS_MIN = "minrecords";

    public static final String ATTRIBUTE_RECORDS_MAX = "maxrecords";

    // -----------------------------------------------------------------------//
    // protected Member Variables
    // -----------------------------------------------------------------------//

    private Recordset _entityRecordset;

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    /**
     * Creates an {@link CoalesceRecordset}, by name, and ties it to its parent
     * {@link CoalesceSection} with default max and min records of 0.
     *
     * @param parent {@link CoalesceSection} the {@link CoalesceSection} that
     *               this new {@link CoalesceRecordset} will belong to.
     * @param name   String the name of this {@link CoalesceRecordset} .
     * @return {@link CoalesceRecordset} , the new {@link CoalesceRecordset} .
     */
    public static CoalesceRecordset create(CoalesceSection parent, String name)
    {
        return create(parent, name, 0, 0);
    }

    /**
     * Creates an {@link CoalesceRecordset}, by name, and ties it to its parent
     * {@link CoalesceSection} with max and min records assignments.
     *
     * @param parent     {@link CoalesceSection} the {@link CoalesceSection} that
     *                   this new {@link CoalesceRecordset} will belong to.
     * @param name       String the name of this {@link CoalesceRecordset} .
     * @param minRecords integer, the minimum number of records this
     *                   {@link CoalesceRecordset} can contain.
     * @param maxRecords integer, the maximum number of records this
     *                   {@link CoalesceRecordset} can contain.
     * @return {@link CoalesceRecordset} , the new {@link CoalesceRecordset} .
     */
    public static CoalesceRecordset create(CoalesceSection parent, String name, int minRecords, int maxRecords)
    {

        if (parent == null)
            throw new NullArgumentException("parent");
        if (name == null)
            throw new NullArgumentException("name");
        if (StringHelper.isNullOrEmpty(name.trim()))
            throw new IllegalArgumentException("name cannot be empty");

        if (minRecords < 0 || maxRecords < minRecords)
            return null;

        // Check that a recordset with the same name doesn't already exist
        for (CoalesceRecordset recordset : parent.getRecordsetsAsList())
        {
            if (recordset.getName().equalsIgnoreCase(name))
            {
                return recordset;
            }
        }

        Recordset newEntityRecordset = new Recordset();
        parent.getEntityRecordSets().add(newEntityRecordset);

        CoalesceRecordset newRecordset = new CoalesceRecordset();
        if (!newRecordset.initialize(parent, newEntityRecordset))
            return null;

        newRecordset.setName(name);
        newRecordset.setMinRecords(minRecords);
        newRecordset.setMaxRecords(maxRecords);

        // Add to parent's child collection
        if (!parent.getChildCoalesceObjects().containsKey(newRecordset.getKey()))
        {
            parent.addChildCoalesceObject(newRecordset);
        }

        return newRecordset;

    }

    /**
     * Class constructor. Creates a CoalesceRecordset class.
     */
    public CoalesceRecordset()
    {
        super();
    }

    /**
     * Class constructor. Creates a CoalesceRecordset class off of an existing
     * CoalesceRecordset.
     *
     * @param recordset
     */
    public CoalesceRecordset(CoalesceRecordset recordset)
    {
        super(recordset);

        // Copy Member Variables
        _entityRecordset = recordset._entityRecordset;
    }

    /**
     * Initializes a this {@link CoalesceRecordset} based on a Recordset and
     * ties it to its parent {@link CoalesceSection}.
     *
     * @param parent    {@link CoalesceSection} the {@link CoalesceSection} that
     *                  this new {@link CoalesceRecordset} will belong to.
     * @param recordset Recordset that this {@link CoalesceRecordset} will be
     *                  based on.
     * @return boolean indicator of success/failure.
     */
    protected boolean initialize(CoalesceSection parent, Recordset recordset)
    {

        if (parent == null)
            throw new NullArgumentException("parent");
        if (recordset == null)
            throw new NullArgumentException("recordset");

        // Set References
        setParent(parent);
        _entityRecordset = recordset;

        super.initialize(_entityRecordset);

        for (Fielddefinition entityFieldDefinition : _entityRecordset.getFielddefinition())
        {
            CoalesceFieldDefinition newFieldDefinition = new CoalesceFieldDefinition();
            newFieldDefinition.initialize(this, entityFieldDefinition);
        }

        for (Record entityRecord : _entityRecordset.getRecord())
        {
            CoalesceRecord newRecord = new CoalesceRecord();
            newRecord.initialize(this, entityRecord);
        }

        return true;

    }

    // -----------------------------------------------------------------------//
    // public Properties
    // -----------------------------------------------------------------------//

    /**
     * @return a list of field definitions that are defined for this recorset.
     */
    @JsonView(Views.Template.class)
    public List<CoalesceFieldDefinition> getFieldDefinitions()
    {
        return getObjectsAsList(_entityRecordset.getFielddefinition(),
                                ECoalesceObjectStatus.DELETED,
                                ECoalesceObjectStatus.UNKNOWN);
    }

    /**
     * @return a complete list of all the records within this recordset whether
     * they are marked as deleted or not.
     */
    @JsonView(Views.Entity.class)
    public List<CoalesceRecord> getAllRecords()
    {
        List<CoalesceRecord> results = new ArrayList<CoalesceRecord>();

        for (Record record : _entityRecordset.getRecord())
        {
            for (CoalesceObject xdo : getChildCoalesceObjects().values())
            {
                if (xdo instanceof CoalesceRecord && xdo.getKey().equalsIgnoreCase(record.getKey()))
                {
                    results.add((CoalesceRecord) xdo);
                    break;
                }
            }

        }

        return results;
    }

    /**
     * @return a list of active records within this recordset.
     */
    @JsonIgnore
    public List<CoalesceRecord> getRecords()
    {
        return getObjectsAsList(_entityRecordset.getRecord(), ECoalesceObjectStatus.DELETED, ECoalesceObjectStatus.UNKNOWN);
    }

    @Override
    public int getMaxRecords()
    {
        return _entityRecordset.getMaxrecords();
    }

    @Override
    public void setMaxRecords(int value)
    {
        _entityRecordset.setMaxrecords(value);
    }

    @Override
    public int getMinRecords()
    {
        return _entityRecordset.getMinrecords();
    }

    @Override
    public void setMinRecords(int value)
    {
        _entityRecordset.setMinrecords(value);
    }

    /**
     * Returns boolean indicator of the existence of this
     * {@link CoalesceRecordset} 's active {@link CoalesceRecord}s.
     *
     * @return boolean indication that the {@link CoalesceRecordset} has active
     * {@link CoalesceRecord} s.
     */
    @JsonView(Views.Entity.class)
    public boolean getHasActiveRecords()
    {

        for (CoalesceRecord record : getRecords())
        {
            if (record.isActive())
            {
                return true;
            }

        }

        // No Active Records
        return false;
    }

    /**
     * Returns boolean indicator of the existence of this
     * {@link CoalesceRecordset} 's {@link CoalesceRecord} s.
     *
     * @return boolean indication that the {@link CoalesceRecordset} has
     * {@link CoalesceRecord} s, active or not.
     */
    @JsonView(Views.Entity.class)
    public boolean getHasRecords()
    {
        return (!_entityRecordset.getRecord().isEmpty());
    }

    // -----------------------------------------------------------------------//
    // Public Methods
    // -----------------------------------------------------------------------//

    /**
     * Creates an {@link CoalesceFieldDefinition} for this
     * {@link CoalesceRecordset}, with the name, datatype, label, default
     * classification and default value specified.
     *
     * @param name                         String name of the new {@link CoalesceFieldDefinition} .
     * @param dataType                     {@link ECoalesceFieldDataTypes} that a Field based on the
     *                                     new {@link CoalesceFieldDefinition} is to contain.
     * @param label                        String label to present to the user for this
     *                                     field/fielddefinition.
     * @param defaultClassificationMarking the default classification for this
     *                                     field/fielddefinition.
     * @param defaultValue                 the default value for this field/fielddefinition.
     * @return {@link CoalesceFieldDefinition} the new
     * {@link CoalesceFieldDefinition} .
     */
    public CoalesceFieldDefinition createFieldDefinition(String name,
                                                         ECoalesceFieldDataTypes dataType,
                                                         String label,
                                                         String defaultClassificationMarking,
                                                         String defaultValue)
    {
        return CoalesceFieldDefinition.create(this, name, dataType, label, defaultClassificationMarking, defaultValue);
    }

    /**
     * Creates an {@link CoalesceFieldDefinition} for this
     * {@link CoalesceRecordset}, with the name, datatype, label, default
     * classification and default value specified.
     *
     * @param name                         String name of the new {@link CoalesceFieldDefinition}
     * @param dataType                     {@link ECoalesceFieldDataTypes} that a Field based on the
     *                                     new {@link CoalesceFieldDefinition} is to contain
     * @param label                        String label to present to the user for this field/field
     *                                     definition
     * @param defaultClassificationMarking the default classification for this
     *                                     field/field definition
     * @param defaultValue                 the default value for this field/field definition
     * @param disableHistory               the value defining if a field should track history
     * @return {@link CoalesceFieldDefinition} the new
     * {@link CoalesceFieldDefinition}
     */
    public CoalesceFieldDefinition createFieldDefinition(String name,
                                                         ECoalesceFieldDataTypes dataType,
                                                         String label,
                                                         String defaultClassificationMarking,
                                                         String defaultValue,
                                                         boolean disableHistory)
    {
        return CoalesceFieldDefinition.create(this,
                                              name,
                                              dataType,
                                              label,
                                              defaultClassificationMarking,
                                              defaultValue,
                                              false,
                                              disableHistory);
    }

    /**
     * Creates an {@link CoalesceFieldDefinition} for this
     * {@link CoalesceRecordset}, with the name and datatype specified.
     *
     * @param name     String name of the new {@link CoalesceFieldDefinition} .
     * @param dataType {@link ECoalesceFieldDataTypes} that a Field based on the
     *                 new {@link CoalesceFieldDefinition} is to contain.
     * @return {@link CoalesceFieldDefinition} the new
     * {@link CoalesceFieldDefinition} .
     */
    public CoalesceFieldDefinition createFieldDefinition(String name, ECoalesceFieldDataTypes dataType)
    {
        return CoalesceFieldDefinition.create(this, name, dataType);
    }

    /**
     * Returns the {@link CoalesceRecordset} 's {@link CoalesceFieldDefinition}
     * that matches the String fieldName parameter.
     *
     * @param fieldName String name of the new {@link CoalesceFieldDefinition} .
     * @return {@link CoalesceFieldDefinition} with the matching field name.
     */
    public CoalesceFieldDefinition getFieldDefinition(String fieldName)
    {
        CoalesceFieldDefinition result = null;

        for (CoalesceFieldDefinition fieldDefinition : getFieldDefinitions())
        {
            if (fieldDefinition.getName().equalsIgnoreCase(fieldName))
            {
                result = fieldDefinition;
                break;
            }
        }

        return result;
    }

    /**
     * Returns boolean indicator of {@link CoalesceRecordset} 's ability to be
     * edited.
     *
     * @return boolean indication of if the {@link CoalesceRecordset} is
     * editable.
     */
    public boolean getAllowEdit()
    {
        return true;
    }

    /**
     * Returns boolean indicator of {@link CoalesceRecordset} 's ability to add
     * new records.
     *
     * @return boolean indication of if the {@link CoalesceRecordset} can have
     * new records added to it.
     */
    public boolean getAllowNew()
    {
        return true;
    }

    /**
     * Returns boolean indicator of {@link CoalesceRecordset} 's ability to add
     * delete records.
     *
     * @return boolean indication of if the {@link CoalesceRecordset} records
     * can be removed.
     */
    public boolean getAllowRemove()
    {
        return true;
    }

    /**
     * Returns the number of {@link CoalesceRecord}s contained within this
     * {@link CoalesceRecordset}.
     *
     * @return integer of how many {@link CoalesceRecord} s are contained by
     * this {@link CoalesceRecordset} .
     */
    @JsonView(Views.Entity.class)
    public int getCount()
    {
        return getRecords().size();
    }

    /**
     * @param value
     * @return a boolean indicator of the existence of the
     * {@link CoalesceRecord} parameter within this
     * {@link CoalesceRecordset} .
     */
    public boolean contains(Object value)
    {
        return getRecords().contains(value);
    }

    /**
     * Returns Index of the Object parameter within this
     * {@link CoalesceRecordset}.
     *
     * @param value Object of {@link CoalesceRecord} .
     * @return integer position index of the {@link CoalesceRecord} Object
     * parameter within this {@link CoalesceRecordset} .
     */
    public int indexOf(Object value)
    {
        return getRecords().indexOf(value);
    }

    /**
     * Adds a new {@link CoalesceRecord} to this {@link CoalesceRecordset} and
     * returns the {@link CoalesceRecord}.
     *
     * @return {@link CoalesceRecord} , the new {@link CoalesceRecord} .
     */
    public CoalesceRecord addNew()
    {
        CoalesceRecord newRecord = CoalesceRecord.create(this, getName() + " Record");

        return newRecord;
    }

    /**
     * Returns the {@link CoalesceRecord} at the specified index within this
     * {@link CoalesceRecordset}.
     *
     * @param index integer position of the {@link CoalesceRecordset} 's desired
     *              {@link CoalesceRecord} .
     * @return {@link CoalesceRecord} at the {@link CoalesceRecordset} 's index
     * position.
     * @throws IndexOutOfBoundsException if the <code>index</code> is not within
     *                                   the bounds of available records
     */
    public CoalesceRecord getItem(int index)
    {

        List<CoalesceRecord> records = getRecords();

        // Iterate List
        if (index >= 0 && index < records.size())
        {
            return records.get(index);
        }
        else
        {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Removes the {@link CoalesceRecord} at the specified index within this
     * {@link CoalesceRecordset}.
     *
     * @param index index position of the {@link CoalesceRecordset} 's
     *              {@link CoalesceRecord} to remove.
     */
    public void removeAt(int index)
    {
        if (index < 0 || index >= getRecords().size())
            return;

        CoalesceRecord record = (CoalesceRecord) getItem(index);

        // getRecords().remove(record);

        record.setStatus(ECoalesceObjectStatus.DELETED);

    }

    /**
     * Removes the {@link CoalesceRecord}, from within this
     * {@link CoalesceRecordset}, that has the matching key passed by parameter.
     *
     * @param key String of the {@link CoalesceRecord} key to remove from the
     *            {@link CoalesceRecordset} .
     */
    public void remove(String key)
    {
        CoalesceRecord recordToRemove = null;

        // Find
        // For Each Record As CoalesceRecord In this.Records
        for (CoalesceRecord record : getRecords())
        {
            if (record.getKey().equals(key))
            {
                recordToRemove = record;
                break;
            }
        }

        // Evaluate
        if (recordToRemove != null)
        {
            // Remove from the Records Collection
            // getRecords().remove(recordToRemove);

            // Set as Status as Deleted
            recordToRemove.setStatus(ECoalesceObjectStatus.DELETED);

        }
    }

    /**
     * Removes all of the records.
     */
    public void removeAll()
    {
        while (getRecords().size() > 0)
        {
            removeAt(0);
        }

    }

    // -----------------------------------------------------------------------//
    // Protected Methods
    // -----------------------------------------------------------------------//

    /**
     * Returns the Record list contained by the Recordset.
     *
     * @return List&lt;Record&gt; the Recordset's record list.
     */
    protected List<Record> getEntityRecords()
    {
        return _entityRecordset.getRecord();
    }

    /**
     * Returns the Fielddefinition list contained by the Recordset.
     *
     * @return List&lt;Fielddefinition&gt; the Recordset's Fielddefinition list.
     */
    protected List<Fielddefinition> getEntityFieldDefinitions()
    {
        return _entityRecordset.getFielddefinition();
    }

    @Override
    protected boolean prune(CoalesceObjectType child)
    {
        boolean isSuccessful = false;

        if (child instanceof History)
        {
            isSuccessful = _entityRecordset.getHistory().remove(child);
        }
        else if (child instanceof Fielddefinition)
        {
            isSuccessful = _entityRecordset.getFielddefinition().remove(child);
        }
        else if (child instanceof Record)
        {
            isSuccessful = _entityRecordset.getRecord().remove(child);
        }

        return isSuccessful;
    }

    @Override
    protected boolean setExtendedAttributes(String name, String value)
    {
        switch (name.toLowerCase())
        {
        case "minrecords":
            try
            {
                setMinRecords(Integer.parseInt(value));
                return true;
            }
            catch (NumberFormatException nfe)
            {
                return false;
            }

        case "maxrecords":
            try
            {
                setMaxRecords(Integer.parseInt(value));
                return true;
            }
            catch (NumberFormatException nfe)
            {
                return false;
            }

        default:
            return setOtherAttribute(name, value);
        }
    }

    @Override
    protected Map<QName, String> getAttributes()
    {
        Map<QName, String> map = super.getAttributes();

        map.put(new QName("minrecords"), Integer.toString(_entityRecordset.getMinrecords()));
        map.put(new QName("maxrecords"), Integer.toString(_entityRecordset.getMaxrecords()));

        return map;
    }

}
