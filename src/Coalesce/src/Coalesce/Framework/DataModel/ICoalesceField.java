package Coalesce.Framework.DataModel;

import java.util.Locale;

import Coalesce.Common.Classification.Marking;

public interface ICoalesceField {

    // Methods
    /**
     * Return the value of the Field's Name attribute
     * 
     * @return
     */
    public String getName();

    /**
     * Return the value of the Field's Value attribute
     * 
     * @return
     */
    public String getValue();

    /**
     * Return the value of the Field's Label attribute
     * 
     * @return
     */
    public String getLabel();

    /**
     * Return the value of the Field's DataType attribute
     * 
     * @return
     */
    public ECoalesceFieldDataTypes getDataType();

    /**
     * Return a Marking class value of the Field's ClassificationMarking attribute
     * 
     * @return
     */
    public Marking getClassificationMarking();

    /**
     * Return identification of who modified the Field's Value attribute
     * 
     * @return
     */
    public String getModifiedBy();

    /**
     * Return IP address of who modified the Field's Value attribute
     * 
     * @return
     */
    public String getModifiedByIP();

    /**
     * Returns the Input Language used when the Field's value was set.
     * 
     * @return
     */
    public Locale getInputLang();

    /**
     * Return the Field's MimeType. MIME types form a standard way of classifying file types on the Internet.
     * 
     * @return
     */
    public String getMimeType();

    /**
     * Return the Field's File name attribute.
     * 
     * @return
     */
    public String getFilename();

    /**
     * Return the value of the Field's extension attribute which corresponds to the filename attribute
     * 
     * @return
     */
    public String getExtension();

    /**
     * Return the value of the Field's Hash attribute
     * 
     * @return
     */
    public String getHash();

    /**
     * Return the value of the Field's Size attribute
     * 
     * @return
     */
    public int getSize();

    /**
     * Return the Key value of the Field's most recent previous history XsdDataObject
     * 
     * @return
     */
    public String getPreviousHistoryKey();

    /**
     * Sets the value of the Field's Name attribute
     * 
     * @param value
     */
    public void setName(String value);

    /**
     * Sets the value of the Field's Value attribute
     * 
     * @param value
     */
    public void setValue(String value);

    /**
     * Sets the value of the Field's Label attribute
     * 
     * @param value
     */
    public void setLabel(String value);

    /**
     * Sets the value of the Field's DataType attribute
     * 
     * @param value
     */
    public void setDataType(ECoalesceFieldDataTypes value);

    /**
     * Sets the Field's ClassificationMarking attribute based on the Marking class value parameter
     * 
     * @param value
     */
    public void setClassificationMarking(Marking value);

    /**
     * Sets the identification of who modified the Field's Value attribute
     * @param value
     */
    public void setModifiedBy(String value);

    /**
     * Sets the IP address of who modified the Field's Value attribute
     * @param value
     */
    public void setModifiedByIP(String value);

    /**
     * Sets the Input Language used as the Field's value is set.
     * @param value
     */
    public void setInputLang(Locale value);

    /**
     * Sets the Field's MimeType. MIME types form a standard way of classifying file types on the Internet.
     * 
     * @param value
     */
    public void setMimeType(String value);

    /**
     * Sets the Field's File name attribute.
     * 
     * @param value
     */
    public void setFilename(String value);

    /**
     * Sets the value of the Field's extension attribute which corresponds to the filename attribute
     * 
     * @param value
     */
    public void setExtension(String value);

    /**
     * Sets the value of the Field's Hash attribute
     * 
     * @param value
     */
    public void setHash(String value);

    /**
     * Sets the value of the Field's Size attribute
     * 
     * @param value
     */
    public void setSize(int value);

    /**
     * Sets the Key value corresponding to the Field's most recent previous history XsdDataObject
     * 
     * @param value
     */
    public void setPreviousHistoryKey(String value);
}
