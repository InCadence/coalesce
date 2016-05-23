/*-----------------------------------------------------------------------------'
 Copyright 2015 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.framework.datamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;

/**
 * This is a Coalesce Recordset that implements the map interface.
 * 
 * @author n78554
 * @param <T>
 */
public class CoalesceMap<T> extends CoalesceRecordset implements Map<String, T> {

    /**
     * Default name to be used when creating recordsets
     */
    private static final String DEFAULT_NAME = "%s Map";

    /*--------------------------------------------------------------------------
    Factory Method
    --------------------------------------------------------------------------*/

    /**
     * @param clazz
     * @return the recordset name that will be used for a given data type.
     */
    public static String getDefaultRecordsetName(Class<?> clazz)
    {
        return String.format(DEFAULT_NAME, clazz.getSimpleName());
    }

    /**
     * Creates a record set within the provided {@link CoalesceSection}.
     * Defaults the path name to 'clazz.getSimpleName() + " Map"'.
     *
     * @param pSection Name of section the record set will be part of
     * @param clazz Specifies the data type.
     * @return {@link CoalesceRecordset} object
     */
    public static <T> CoalesceMap<T> createRecordSet(CoalesceSection pSection, Class<T> clazz)
    {
        return createRecordSet(pSection, getDefaultRecordsetName(clazz), clazz);
    }

    /**
     * 
     * @param clazz
     * @return the Coalesce type for a given java type.
     */
    public static <T> ECoalesceFieldDataTypes getDataType(Class<T> clazz)
    {

        ECoalesceFieldDataTypes eType;

        if (clazz.equals(String.class))
        {
            eType = ECoalesceFieldDataTypes.STRING_TYPE;
        }
        else if (clazz.equals(Integer.class))
        {
            eType = ECoalesceFieldDataTypes.INTEGER_TYPE;
        }
        else if (clazz.equals(DateTime.class))
        {
            eType = ECoalesceFieldDataTypes.DATE_TIME_TYPE;
        }
        else if (clazz.equals(Boolean.class))
        {
            eType = ECoalesceFieldDataTypes.BOOLEAN_TYPE;
        }
        else if (clazz.equals(Double.class))
        {
            eType = ECoalesceFieldDataTypes.DOUBLE_TYPE;
        }
        else if (clazz.equals(Float.class))
        {
            eType = ECoalesceFieldDataTypes.FLOAT_TYPE;
        }
        else if (clazz.equals(Long.class))
        {
            eType = ECoalesceFieldDataTypes.LONG_TYPE;
        }
        else
        {
            throw new IllegalArgumentException("Invalid Type: " + clazz.getSimpleName());
        }

        return eType;

    }

    /**
     * Creates a record set within the provided {@link CoalesceSection}.
     *
     * @param pSection Name of section the record set will be part of
     * @param pPathName Path name of the record set
     * @param clazz Specifies the data type.
     * @return {@link CoalesceRecordset} object
     */
    public static <T> CoalesceMap<T> createRecordSet(CoalesceSection pSection, String pPathName, Class<T> clazz)
    {

        ECoalesceFieldDataTypes eType = getDataType(clazz);

        CoalesceMap<T> recordSet = new CoalesceMap<T>(CoalesceRecordset.create(pSection, pPathName));

        CoalesceFieldDefinition.create(recordSet, "key", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(recordSet, "value", eType);

        return recordSet;
    }

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/

    /**
     * Class constructor. Creates a CoalesceRecordset class off of an existing
     * CoalesceRecordset.
     * 
     * @param recordset
     * 
     */
    public CoalesceMap(CoalesceRecordset recordset)
    {
        super(recordset);
    }

    /*--------------------------------------------------------------------------
    Public Methods
    --------------------------------------------------------------------------*/

    /**
     * 
     * @return a map that has been detached from the underlining Coalesce
     *         object.
     */
    public Map<String, T> detach()
    {

        Map<String, T> results = new HashMap<String, T>();

        for (CoalesceRecord record : getRecords())
        {
            CoalesceEntry entry = new CoalesceEntry(record);
            if (!results.containsKey(entry.getKey()))
            {
                results.put(entry.getKey(), entry.getValue());
            }
        }

        return results;

    }

    /*--------------------------------------------------------------------------
    Interface Implementation
    --------------------------------------------------------------------------*/

    @Override
    public int size()
    {
        return getCount();
    }

    @Override
    public boolean isEmpty()
    {
        return getCount() == 0;
    }

    @Override
    public boolean containsKey(Object key)
    {
        return getDyanmicRecord(key, false) != null;
    }

    @Override
    public boolean containsValue(Object value)
    {

        boolean hasValue = false;

        for (CoalesceRecord cRecord : this.getRecords())
        {

            CoalesceEntry record = new CoalesceEntry(cRecord);

            if (record.getValue().equals(value))
            {
                hasValue = true;
                break;
            }
        }

        return hasValue;
    }

    @Override
    public T get(Object key)
    {

        T value = null;

        CoalesceEntry record = getDyanmicRecord(key, false);

        if (record != null)
        {
            value = record.getValue();
        }

        return value;

    }

    @Override
    public T put(String key, T value)
    {

        T oldValue = null;

        CoalesceEntry record = getDyanmicRecord(key, true);

        if (record == null)
        {
            record = new CoalesceEntry(this.addNew());
            record.setKey(key);
        }
        else
        {
            oldValue = record.getValue();
        }

        record.setValue(value);
        record.setStatus(ECoalesceObjectStatus.ACTIVE);

        return oldValue;
    }

    @Override
    public T remove(Object key)
    {
        if (!(key instanceof String))
        {
            throw new IllegalArgumentException("Key must be a String");
        }

        remove((String) key);

        return null;
    }

    @Override
    public void remove(String key)
    {

        // T value = null;

        CoalesceRecord recordToRemove = null;

        // Find
        // For Each Record As CoalesceRecord In this.Records
        for (CoalesceRecord record : getRecords())
        {
            CoalesceEntry entry = new CoalesceEntry(record);

            if (entry.getKey().equals(key))
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

            // value = new CoalesceEntry(recordToRemove).getValue();

        }

        // return value;

    }

    @Override
    public void putAll(Map<? extends String, ? extends T> m)
    {

        for (Map.Entry<? extends String, ? extends T> entry : m.entrySet())
        {
            put(entry.getKey(), entry.getValue());
        }

    }

    @Override
    public void clear()
    {
        this.removeAll();
    }

    @Override
    public Set<String> keySet()
    {

        Set<String> set = new HashSet<String>();

        for (CoalesceRecord record : this.getRecords())
        {
            set.add(new CoalesceEntry(record).getKey());
        }

        return set;
    }

    @Override
    public Collection<T> values()
    {

        Collection<T> list = new ArrayList<T>();

        for (CoalesceRecord record : this.getRecords())
        {
            list.add(new CoalesceEntry(record).getValue());
        }

        return list;
    }

    @Override
    public Set<java.util.Map.Entry<String, T>> entrySet()
    {

        Set<java.util.Map.Entry<String, T>> set = new HashSet<java.util.Map.Entry<String, T>>();

        for (CoalesceRecord record : this.getRecords())
        {
            set.add(new CoalesceEntry(record));
        }

        return set;
    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private CoalesceEntry getDyanmicRecord(Object key, boolean includeDeleted)
    {

        // Check for valid key type
        if (!(key instanceof String))
        {
            throw new IllegalArgumentException("Key must be a string");
        }

        return getDyanmicRecord((String) key, includeDeleted);

    }

    private CoalesceEntry getDyanmicRecord(String key, boolean includeDeleted)
    {

        CoalesceEntry record = null;

        for (CoalesceRecord cRecord : getAllRecords())
        {

            record = new CoalesceEntry(cRecord);

            if (record.getKey().equalsIgnoreCase(key) && (includeDeleted || !record.isMarkedDeleted()))
            {
                break;
            }

            record = null;

        }

        return record;

    }

    /*--------------------------------------------------------------------------
    Private Classes
    --------------------------------------------------------------------------*/

    /**
     * This is a Coalesce Record that implements that Entry interface.
     * 
     * @author n78554
     */
    private class CoalesceEntry extends CoalesceRecord implements java.util.Map.Entry<String, T> {

        /*--------------------------------------------------------------------------
        Constructors
        --------------------------------------------------------------------------*/

        /**
         * Constructs a new {@link CoalesceEntry} with a {@link CoalesceRecord}.
         *
         * @param record Record to create Access Controls Record with
         */
        public CoalesceEntry(CoalesceRecord record)
        {
            super(record);
        }

        /*--------------------------------------------------------------------------
        OVerride Methods
        --------------------------------------------------------------------------*/

        @Override
        public String getKey()
        {
            return getKeyField().getValue();
        }

        @Override
        public void setKey(String value)
        {
            getKeyField().setValue(value);
        }
        
        @Override
        public T getValue()
        {
            try
            {
                return getValueField().getValue();
            }
            catch (CoalesceDataFormatException e)
            {
                return null;
            }
        }

        @Override
        public T setValue(T value)
        {

            T original;

            CoalesceField<T> field = getValueField();

            try
            {
                original = field.getValue();
            }
            catch (CoalesceDataFormatException e)
            {
                original = null;
            }

            try
            {
                field.setValue(value);
            }
            catch (CoalesceDataFormatException e)
            {
                throw new IllegalArgumentException("Invalid Value", e);
            }

            return original;
        }

        /*--------------------------------------------------------------------------
        Private Methods
        --------------------------------------------------------------------------*/

        /**
         * @return the field containing the key.
         */
        private CoalesceStringField getKeyField()
        {
            return (CoalesceStringField) getFieldByName("key");
        }

        /**
         * @return the field containing the value.
         */
        @SuppressWarnings("unchecked")
        private CoalesceField<T> getValueField()
        {
            return (CoalesceField<T>) getFieldByName("value");
        }

    }

}
