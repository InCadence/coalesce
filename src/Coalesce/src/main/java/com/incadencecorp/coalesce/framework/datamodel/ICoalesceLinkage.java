package com.incadencecorp.coalesce.framework.datamodel;

import java.util.Locale;

import com.incadencecorp.coalesce.common.classification.Marking;

/**
 * Interface for accessing Linkages of Coalesce Entities.
 */
public interface ICoalesceLinkage extends ICoalesceObject {

    // Methods
    /**
     * Return the value of the Linkage's Name attribute.
     * 
     * @return String of the Linkage's name attribute.
     */
    String getName();

    /**
     * Return the key value corresponding to the attribute of the Linkage's
     * first entity.
     * 
     * @return String of the attribute representing the Linkage's key of the
     *         first entity.
     */
    String getEntity1Key();

    /**
     * Return the name value corresponding to the attribution of the Linkage's
     * first entity.
     * 
     * @return String of the attribute representing the Linkage's name of the
     *         first entity.
     */
    String getEntity1Name();

    /**
     * Return the source value corresponding to the attribution of the Linkage's
     * first entity.
     * 
     * @return String of the attribute representing the Linkage's source of the
     *         first entity.
     */
    String getEntity1Source();

    /**
     * Return the version value corresponding to the attribution of the
     * Linkage's first entity.
     * 
     * @return String of the attribute representing the Linkage's version of the
     *         first entity.
     */
    String getEntity1Version();

    /**
     * Return the key value corresponding to the attribute of the Linkage's
     * second entity.
     * 
     * @return String of the attribute representing the Linkage's key of the
     *         second entity.
     */
    String getEntity2Key();

    /**
     * Return the name value corresponding to the attribution of the Linkage's
     * second entity.
     * 
     * @return String of the attribute representing the Linkage's name of the
     *         second entity.
     */
    String getEntity2Name();

    /**
     * Return the source value corresponding to the attribution of the Linkage's
     * second entity.
     * 
     * @return String of the attribute representing the Linkage's source of the
     *         second entity.
     */
    String getEntity2Source();

    /**
     * Return the version value corresponding to the attribution of the
     * Linkage's second entity.
     * 
     * @return String of the attribute representing the Linkage's version of the
     *         second entity.
     */
    String getEntity2Version();

    /**
     * @return the version of the object this link is for.
     */
    int getEntity2ObjectVersion();

    /**
     * Return the link type identifying the relationship between the two linked
     * entities.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.ELinkTypes}
     *         of the Linkage's link type attribute.
     */
    ELinkTypes getLinkType();

    /**
     * Return a Marking class value of the Linkage's ClassificationMarking
     * attribute.
     * 
     * @return Marking class of the Linkage's classification marking attribute.
     */
    String getClassificationMarkingAsString();

    /**
     * Return identification of who modified the Linkage's Value attribute.
     * 
     * @return String of the Linkage's ModifiedBy attribute.
     */
    String getModifiedBy();

    /**
     * 
     * @return the IP address of the computer used to make this change.
     */
    String getModifiedByIP();

    /**
     * @return the Linkage's PreviousHistoryKey attribute.
     */
    String getPreviousHistoryKey();

    /**
     * Returns the Input Language used when the Linkage's value was set.
     * 
     * @return Locale of the Linkage's Input Language attribute.
     */
    Locale getInputLang();

    /**
     * @return the linkage label.
     */
    String getLabel();

    /**
     * Set the value of the Linkage's Name attribute.
     * 
     * @param value String to be the Linkage's name attribute.
     */
    void setName(String value);

    /**
     * Set the key value corresponding to the attribute of the Linkage's first
     * entity.
     * 
     * @param value String to be the attribute representing the Linkage's key of
     *            the first entity.
     */
    void setEntity1Key(String value);

    /**
     * Set the name value corresponding to the attribute of the Linkage's first
     * entity.
     * 
     * @param value String to be the attribute representing the Linkage's name
     *            of the first entity.
     */
    void setEntity1Name(String value);

    /**
     * Set the source value corresponding to the attribute of the Linkage's
     * first entity.
     * 
     * @param value String to be the attribute representing the Linkage's source
     *            of the first entity.
     */
    void setEntity1Source(String value);

    /**
     * Set the version value corresponding to the attribute of the Linkage's
     * first entity.
     * 
     * @param value String to be the attribute representing the Linkage's
     *            version of the first entity.
     */
    void setEntity1Version(String value);

    /**
     * Set the key value corresponding to the attribute of the Linkage's second
     * entity.
     * 
     * @param value String to be the attribute representing the Linkage's key of
     *            the second entity.
     */
    void setEntity2Key(String value);

    /**
     * Set the name value corresponding to the attribute of the Linkage's second
     * entity.
     * 
     * @param value String to be the attribute representing the Linkage's name
     *            of the second entity.
     */
    void setEntity2Name(String value);

    /**
     * Set the source value corresponding to the attribute of the Linkage's
     * second entity.
     * 
     * @param value String to be the attribute representing the Linkage's source
     *            of the second entity.
     */
    void setEntity2Source(String value);

    /**
     * Set the version value corresponding to the attribute of the Linkage's
     * second entity.
     * 
     * @param value String to be the attribute representing the Linkage's
     *            version of the second entity.
     */
    void setEntity2Version(String value);

    /**
     * Sets the version of the object this link is for.
     * 
     * @param value
     */
    void setEntity2ObjectVersion(int value);

    /**
     * Sets the link type identifying the relationship between the two linked
     * entities.
     * 
     * @param value ELinkTypes to be the Linkage's Link Type attribute.
     */
    void setLinkType(ELinkTypes value);

    /**
     * Sets a Marking class value of the Linkage's ClassificationMarking
     * attribute.
     * 
     * @param value Marking class to be the Linkage's classification marking
     *            attribute.
     */
    void setClassificationMarkingAsString(String value);

    /**
     * Sets the Key value corresponding to the Linkage's most recent previous
     * history
     * 
     * @param value
     */
    void setPreviousHistoryKey(String value);

    /**
     * Sets the identification of who modified the Linkage's Value attribute.
     * 
     * @param value String to be the Linkage's ModifiedBy attribute.
     */
    void setModifiedBy(String value);

    /**
     * Sets the IP address of the computer used to make this change.
     * 
     * @param value
     */
    void setModifiedByIP(String value);

    /**
     * Sets the Input Language used when the Linkage's value was set.
     * 
     * @param value Locale to be the Linkage's Input language attribute.
     */
    void setInputLang(Locale value);

    /**
     * Sets the linkage label.
     * 
     * @param value
     */
    void setLabel(String value);

}
