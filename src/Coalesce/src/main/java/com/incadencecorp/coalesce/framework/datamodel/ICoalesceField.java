package com.incadencecorp.coalesce.framework.datamodel;

import java.util.Locale;

import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;

/**
 * Interface for access fields of Coalesce Entities.
 *
 * @param <T>
 */
public interface ICoalesceField<T> {

    // Methods
    /**
     * Return the value of the Field's Name attribute.
     * 
     * @return String of the Field's Name attribute.
     */
    String getName();

    /**
     * Return the value of the Field's Value attribute.
     * 
     * @return String of the Field's value attribute.
     */
    T getValue() throws CoalesceDataFormatException;

    /**
     * Return the value of the Field's Label attribute.
     * 
     * @return String of the Field's label attribute.
     */
    String getLabel();

    /**
     * Return the value of the Field's DataType attribute.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes} of the Field's DataType
     *         attribute.
     */
    ECoalesceFieldDataTypes getDataType();

    /**
     * Return a Marking class value of the Field's ClassificationMarking attribute.
     * 
     * @return Marking class of the Field's ClassificationMarking attribute.
     */
    Marking getClassificationMarking();

    /**
     * Return identification of who modified the Field's Value attribute.
     * 
     * @return String of the Field's ModifiedBy attribute.
     */
    String getModifiedBy();

    /**
     * Return IP address of who modified the Field's Value attribute.
     * 
     * @return String of the Field's ModifiedByIP attribute.
     */
    String getModifiedByIP();

    /**
     * Returns the Input Language used when the Field's value was set.
     * 
     * @return Locale of the Field's InputLang attribute.
     */
    Locale getInputLang();

    /**
     * Return the Field's MimeType. MIME types form a standard way of classifying file types on the Internet.
     * 
     * @return String of the Field's MimeType attribute.
     */
    String getMimeType();

    /**
     * Return the Field's File name attribute.
     * 
     * @return String of the Field's Filename attribute.
     */
    String getFilename();

    /**
     * Return the value of the Field's extension attribute which corresponds to the filename attribute.
     * 
     * @return String of the Field's extension attribute.
     */
    String getExtension();

    /**
     * Return the value of the Field's Hash attribute.
     * 
     * @return String of the Field's hash value attribute.
     */
    String getHash();

    /**
     * Return the value of the Field's Size attribute.
     * 
     * @return integer of the Field's size attribute.
     */
    int getSize();

    /**
     * Return the Key value of the Field's most recent previous history
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}.
     * 
     * @return String to be the Field's PreviousHistoryKey attribute.
     */
    String getPreviousHistoryKey();

    /**
     * Sets the value of the Field's Name attribute.
     * 
     * @param value String to be the Field's Name attribute.
     */
    void setName(String value);

    /**
     * Sets the value of the Field's Value attribute.
     * 
     * @param value String to be the Field's value attribute.
     */
    void setValue(T value) throws CoalesceDataFormatException;

    /**
     * Sets the value of the Field's Label attribute.
     * 
     * @param value String to be the Field's label attribute.
     */
    void setLabel(String value);

    /**
     * Sets the Field's ClassificationMarking attribute based on the Marking class value parameter.
     * 
     * @param value Marking class to be the Field's ClassificationMarking attribute.
     */
    void setClassificationMarking(Marking value);

    /**
     * Sets the identification of who modified the Field's Value attribute.
     * 
     * @param value String to be the Field's ModifiedBy attribute.
     */
    void setModifiedBy(String value);

    /**
     * Sets the IP address of who modified the Field's Value attribute.
     * 
     * @param value String to be the Field's ModifiedByIP attribute.
     */
    void setModifiedByIP(String value);

    /**
     * Sets the Input Language used as the Field's value is set.
     * 
     * @param value Locale to be the Field's InputLang attribute.
     */
    void setInputLang(Locale value);

    /**
     * Sets the Field's MimeType. MIME types form a standard way of classifying file types on the Internet.
     * 
     * @param value String to be the Field's MimeType attribute.
     */
    void setMimeType(String value);

    /**
     * Sets the Field's File name attribute.
     * 
     * @param value String to be the Field's Filename attribute.
     */
    void setFilename(String value);

    /**
     * Sets the value of the Field's extension attribute which corresponds to the filename attribute.
     * 
     * @param value String to be the Field's extension attribute.
     */
    void setExtension(String value);

    /**
     * Sets the value of the Field's Hash attribute.
     * 
     * @param value String to be the Field's hash value attribute.
     */
    void setHash(String value);

    /**
     * Sets the value of the Field's Size attribute.
     * 
     * @param value integer to be the Field's size attribute.
     */
    void setSize(int value);

    /**
     * Sets the Key value corresponding to the Field's most recent previous history
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}.
     * 
     * @param value String to be the Field's PreviousHistoryKey attribute.
     */
    void setPreviousHistoryKey(String value);
}
