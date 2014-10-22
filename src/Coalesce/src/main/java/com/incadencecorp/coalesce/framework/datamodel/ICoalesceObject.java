package com.incadencecorp.coalesce.framework.datamodel;

import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;

/**
 * Interface for access elements of Coalesce Entities.
 */
public interface ICoalesceObject {

    /*--------------------------------------------------------------------------
     Properties
    --------------------------------------------------------------------------*/

    // Status
    /**
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject} status identifying whether the
     * object is active, deleted or of another status.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus} the Coalesce object's status.
     */
    ECoalesceObjectStatus getStatus();

    /**
     * Sets the status of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}, which identifies
     * whether the object is active, deleted or of another status.
     * 
     * @param value {@link com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus} the Coalesce object's status.
     */
    void setStatus(ECoalesceObjectStatus value);

    // Parent
    /**
     * Returns the parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject} of the current
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject} the Coalesce object's parent.
     */
    CoalesceObject getParent();

    /**
     * Sets the parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject} of the current
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject} of the object.
     */
    void setParent(CoalesceObject parent);

    // Key
    /**
     * Returns the string value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s key.
     * 
     * @return String of the Coalesce object's key.
     */
    String getKey();

    /**
     * Sets the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s key by a String
     * parameter.
     * 
     * @param value String to be the Coalesce object's key.
     */
    void setKey(String value);

    /**
     * Sets the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s key by a UUID
     * parameter.
     * 
     * @param guid UUID to be the Coalesce object's key.
     */
    void setKey(UUID guid);

    // Name
    /**
     * Returns the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s name attribute.
     * 
     * @return String of the Coalesce object's name.
     */
    String getName();

    /**
     * Sets the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s name attribute.
     * 
     * @param value to be the Coalesce object's name.
     */
    void setName(String value);

    // Tag
    /**
     * Returns the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s tag attribute.
     * 
     * @return String the Coalesce object's tag.
     */
    String getTag();

    /**
     * Sets the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s tag attribute.
     * 
     * @param value String to be the Coalesce object's tag.
     */
    void setTag(String value);

    // Flatten
    /**
     * Returns the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s flatten
     * attribute.
     * 
     * @return boolean of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s flatten attribute.
     */
    boolean getFlatten();

    /**
     * Sets the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s flatten attribute.
     * 
     * @param value boolean to be the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s flatten
     *            attribute.
     */
    void setFlatten(boolean value);

    // Date Created
    /**
     * Returns the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s DateCreated
     * attribute.
     * 
     * @return DateTime of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s DateCreated
     *         attribute.
     */
    DateTime getDateCreated();

    /**
     * Sets the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s DateCreated
     * attribute.
     * 
     * @param value DateTime to be the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     *            DateCreated attribute.
     */
    void setDateCreated(DateTime value);

    // Last Modified
    /**
     * Returns the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s LastModified
     * attribute.
     * 
     * @return DateTime of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s LastModified
     *         attribute.
     */
    DateTime getLastModified();

    /**
     * Sets the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s LastModified
     * attribute.
     * 
     * @param value DateTime to be the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     *            LastModified attribute.
     */
    void setLastModified(DateTime value);

    // No Index
    /**
     * Returns the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s NoIndex
     * attribute.
     * 
     * @return boolean of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s NoIndex attribute.
     */
    boolean getNoIndex();

    /**
     * Sets the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s NoIndex attribute.
     * 
     * @param value boolean to be the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s NoIndex
     *            attribute.
     */
    void setNoIndex(boolean value);

    /*--------------------------------------------------------------------------
     Read Only
    --------------------------------------------------------------------------*/

    /**
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s child
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}s E.g. an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity} will have
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection} and
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection} children.
     * 
     * @return hashmap of this {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s child
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}s.
     */
    Map<String, CoalesceObject> getChildCoalesceObjects();

    /**
     * Returns the String {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject} type. E.g. field,
     * linkage, section, etc.
     * 
     * @return String of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s type attribute.
     */
    String getType();

    /**
     * Returns the name path of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}s which is a "/"
     * separated String of {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject} names identifying where
     * the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject} within the larger
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}.
     * 
     * @return String of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s namepath attribute.
     */
    String getNamePath();

}
