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

package com.incadencecorp.coalesce.framework.datamodel;

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
     * Returns the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     * status identifying whether the object is active, deleted or of another
     * status.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus}
     *         the Coalesce object's status.
     */
    ECoalesceObjectStatus getStatus();

    /**
     * 
     * @return the object's version that this element was added.
     */
    Integer getObjectVersion();

    /**
     * Sets the object's version that this element was added.
     * 
     * @param version
     */
    void setObjectVersion(Integer version);

    /**
     * @return the identification of who modified the Field's Value attribute.
     */
    String getModifiedBy();

    /**
     * Sets the identification of who modified the Field's Value attribute.
     * 
     * @param value String to be the Field's ModifiedBy attribute.
     */
    void setModifiedBy(String value);

    /**
     * @return the IP address of who modified the Field's Value attribute.
     */
    String getModifiedByIP();

    /**
     * Sets the IP address of who modified the Field's Value attribute.
     * 
     * @param value String to be the Field's ModifiedByIP attribute.
     */
    void setModifiedByIP(String value);

    /**
     * @return the Key value of the Field's most recent previous history
     */
    String getPreviousHistoryKey();

    /**
     * Sets the Key value of the Field's most recent previous history
     * 
     * @param value String to be the Field's PreviousHistoryKey attribute.
     */
    void setPreviousHistoryKey(String value);

    /**
     * Sets the status of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject},
     * which identifies whether the object is active, deleted or of another
     * status.
     * 
     * @param value {@link com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus}
     *            the Coalesce object's status.
     */
    void setStatus(ECoalesceObjectStatus value);

    // Key
    /**
     * Returns the string value of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * key.
     * 
     * @return String of the Coalesce object's key.
     */
    String getKey();

    /**
     * Sets the value of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * key by a String parameter.
     * 
     * @param value String to be the Coalesce object's key.
     */
    void setKey(String value);

    // Name
    /**
     * Returns the value of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * name attribute.
     * 
     * @return String of the Coalesce object's name.
     */
    String getName();

    /**
     * Sets the value of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * name attribute.
     * 
     * @param value to be the Coalesce object's name.
     */
    void setName(String value);

    // Tag
    /**
     * Returns the value of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * tag attribute.
     * 
     * @return String the Coalesce object's tag.
     */
    String getTag();

    /**
     * Sets the value of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * tag attribute.
     * 
     * @param value String to be the Coalesce object's tag.
     */
    void setTag(String value);

    // Flatten
    /**
     * Returns the value of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * flatten attribute.
     * 
     * @return boolean of the
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     *         's flatten attribute.
     */
    boolean isFlatten();

    /**
     * Sets the value of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * flatten attribute.
     * 
     * @param value boolean to be the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     *            's flatten attribute.
     */
    void setFlatten(boolean value);

    // Date Created
    /**
     * Returns the value of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * DateCreated attribute.
     * 
     * @return DateTime of the
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     *         's DateCreated attribute.
     */
    DateTime getDateCreated();

    /**
     * Sets the value of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * DateCreated attribute.
     * 
     * @param value DateTime to be the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     *            's DateCreated attribute.
     */
    void setDateCreated(DateTime value);

    // Last Modified
    /**
     * Returns the value of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * LastModified attribute.
     * 
     * @return DateTime of the
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     *         's LastModified attribute.
     */
    DateTime getLastModified();

    /**
     * Sets the value of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * LastModified attribute.
     * 
     * @param value DateTime to be the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     *            's LastModified attribute.
     */
    void setLastModified(DateTime value);

    // No Index
    /**
     * Returns the value of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * NoIndex attribute.
     * 
     * @return boolean of the
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     *         's NoIndex attribute.
     */
    boolean isNoIndex();

    /**
     * Sets the value of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * NoIndex attribute.
     * 
     * @param value boolean to be the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     *            's NoIndex attribute.
     */
    void setNoIndex(boolean value);
    
    /**
     * Returns the name path of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}s
     * which is a "/" separated String of
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     * names identifying where the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     * within the larger
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}.
     * 
     * @return String of the
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     *         's namepath attribute.
     */
    String getNamePath();

}
