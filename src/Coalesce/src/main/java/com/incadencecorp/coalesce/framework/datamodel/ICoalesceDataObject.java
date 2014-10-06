package com.incadencecorp.coalesce.framework.datamodel;

import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;

public interface ICoalesceDataObject {

    /*--------------------------------------------------------------------------
    Public Properties
    --------------------------------------------------------------------------*/

    // Status
    /**
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject} status identifying whether the
     * object is active, deleted or of another status.
     * 
     * @return ECoalesceDataObjectStatus the DataObject's status
     */
    public ECoalesceDataObjectStatus getStatus();

    /**
     * Sets the status of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}, which identifies
     * whether the object is active, deleted or of another status.
     * 
     * @param value ECoalesceDataObjectStatus the DataObject's status
     */
    public void setStatus(ECoalesceDataObjectStatus value);

    // Parent
    /**
     * Returns the parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject} of the current
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject} the DataObject's parent
     */
    public CoalesceDataObject getParent();

    /**
     * Sets the parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject} of the current
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject} of the DataObject
     */
    public void setParent(CoalesceDataObject parent);

    // Key
    /**
     * Returns the string value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s key
     * 
     * @return String of the DataObject's key
     */
    public String getKey();

    /**
     * Sets the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s key by a String
     * parameter
     * 
     * @param value String to be the DataObject's key
     */
    public void setKey(String value);

    /**
     * Sets the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s key by a UUID
     * parameter
     * 
     * @param guid UUID to be the DataObject's key
     */
    public void setKey(UUID guid);

    // Name
    /**
     * Returns the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s name attribute.
     * 
     * @return String of the DataObject's name
     */
    public String getName();

    /**
     * Sets the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s name attribute.
     * 
     * @param value to be the DataObject's name
     */
    public void setName(String value);

    // Tag
    /**
     * Returns the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s tag attribute.
     * 
     * @return String the DataObject's tag
     */
    public String getTag();

    /**
     * Sets the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s tag attribute.
     * 
     * @param value String to be the DataObject's tag
     */
    public void setTag(String value);

    // Flatten
    /**
     * Returns the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s flatten
     * attribute.
     * 
     * @return boolean of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s flatten attribute.
     */
    public boolean getFlatten();

    /**
     * Sets the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s flatten attribute.
     * 
     * @param value boolean to be the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s flatten
     *            attribute.
     */
    public void setFlatten(boolean value);

    // Date Created
    /**
     * Returns the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s DateCreated
     * attribute.
     * 
     * @return DateTime of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s DateCreated
     *         attribute.
     */
    public DateTime getDateCreated();

    /**
     * Sets the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s DateCreated
     * attribute.
     * 
     * @param value DateTime to be the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s
     *            DateCreated attribute.
     */
    public void setDateCreated(DateTime value);

    // Last Modified
    /**
     * Returns the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s LastModified
     * attribute.
     * 
     * @return DateTime of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s LastModified
     *         attribute.
     */
    public DateTime getLastModified();

    /**
     * Sets the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s LastModified
     * attribute.
     * 
     * @param value DateTime to be the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s
     *            LastModified attribute.
     */
    public void setLastModified(DateTime value);

    // No Index
    /**
     * Returns the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s NoIndex
     * attribute.
     * 
     * @return boolean of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s NoIndex attribute.
     */
    public boolean getNoIndex();

    /**
     * Sets the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s NoIndex attribute.
     * 
     * @param value boolean to be the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s NoIndex
     *            attribute.
     */
    public void setNoIndex(boolean value);

    /*--------------------------------------------------------------------------
    Public Read Only
    --------------------------------------------------------------------------*/

    /**
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s child
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}s. E.g. an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} will have
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection} and
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection} children.
     * 
     * @return hashmap of this {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s child
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}s
     */
    public Map<String, CoalesceDataObject> getChildDataObjects();

    /**
     * Returns the String {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject} type. E.g. field,
     * linkage, section, etc.
     * 
     * @return String of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s type attribute.
     */
    public String getType();

    /**
     * Returns the name path of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}s which is a "/"
     * separated String of {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject} names identifying where
     * the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject} within the larger
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}.
     * 
     * @return String of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s namepath attribute.
     */
    public String getNamePath();

}
