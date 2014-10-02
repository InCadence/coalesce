package Coalesce.Framework.DataModel;

import java.util.Locale;

import Coalesce.Common.Classification.Marking;

public interface ICoalesceLinkage {

    // Methods
    /**
     * Return the value of the Linkage's Name attribute
     * 
     * @return String of the Linkage's name attribute
     */
    public String getName();

    /**
     * Return the key value corresponding to the attribute of the Linkage's first entity
     * 
     * @return String of the attribute representing the Linkage's key of the first entity
     */
    public String getEntity1Key();

    /**
     * Return the name value corresponding to the attribution of the Linkage's first entity
     * 
     * @return String of the attribute representing the Linkage's name of the first entity
     */
    public String getEntity1Name();

    /**
     * Return the source value corresponding to the attribution of the Linkage's first entity
     * 
     * @return String of the attribute representing the Linkage's source of the first entity
     */
    public String getEntity1Source();

    /**
     * Return the version value corresponding to the attribution of the Linkage's first entity
     * 
     * @return String of the attribute representing the Linkage's version of the first entity
     */
    public String getEntity1Version();

    /**
     * Return the key value corresponding to the attribute of the Linkage's second entity
     * 
     * @return String of the attribute representing the Linkage's key of the second entity
     */
    public String getEntity2Key();

    /**
     * Return the name value corresponding to the attribution of the Linkage's second entity
     * 
     * @return String of the attribute representing the Linkage's name of the second entity
     */
    public String getEntity2Name();

    /**
     * Return the source value corresponding to the attribution of the Linkage's second entity
     * 
     * @return String of the attribute representing the Linkage's source of the second entity
     */
    public String getEntity2Source();

    /**
     * Return the version value corresponding to the attribution of the Linkage's second entity
     * 
     * @return String of the attribute representing the Linkage's version of the second entity
     */
    public String getEntity2Version();

    /**
     * Return the link type identifying the relationship between the two linked entities
     * 
     * @return ELinkTypes of the Linkage's link type attribute
     */
    public ELinkTypes getLinkType();

    /**
     * Return a Marking class value of the Linkage's ClassificationMarking attribute
     * 
     * @return Marking class of the Linkage's classification marking attribute
     */
    public Marking getClassificationMarking();

    /**
     * Return identification of who modified the Linkage's Value attribute
     * 
     * @return String of the Linkage's ModifiedBy attribute
     */
    public String getModifiedBy();

    /**
     * Returns the Input Language used when the Linkage's value was set.
     * 
     * @return Locale of the Linkage's Input Language attribute
     */
    public Locale getInputLang();

    /**
     * Set the value of the Linkage's Name attribute
     * 
     * @param value String to be the Linkage's name attribute
     */
    public void setName(String value);

    /**
     * Set the key value corresponding to the attribute of the Linkage's first entity
     * 
     * @param value String to be the attribute representing the Linkage's key of the first entity
     */
    public void setEntity1Key(String value);

    /**
     * Set the name value corresponding to the attribute of the Linkage's first entity
     * 
     * @param value String to be the attribute representing the Linkage's name of the first entity
     */
    public void setEntity1Name(String value);

    /**
     * Set the source value corresponding to the attribute of the Linkage's first entity
     * 
     * @param value String to be the attribute representing the Linkage's source of the first entity
     */
    public void setEntity1Source(String value);

    /**
     * Set the version value corresponding to the attribute of the Linkage's first entity
     * 
     * @param value String to be the attribute representing the Linkage's version of the first entity
     */
    public void setEntity1Version(String value);

    /**
     * Set the key value corresponding to the attribute of the Linkage's second entity
     * 
     * @param value String to be the attribute representing the Linkage's key of the second entity
     */
    public void setEntity2Key(String value);

    /**
     * Set the name value corresponding to the attribute of the Linkage's second entity
     * 
     * @param value String to be the attribute representing the Linkage's name of the second entity
     */
    public void setEntity2Name(String value);

    /**
     * Set the source value corresponding to the attribute of the Linkage's second entity
     * 
     * @param value String to be the attribute representing the Linkage's source of the second entity
     */
    public void setEntity2Source(String value);

    /**
     * Set the version value corresponding to the attribute of the Linkage's second entity
     * 
     * @param value String to be the attribute representing the Linkage's version of the second entity
     */
    public void setEntity2Version(String value);

    /**
     * Sets the link type identifying the relationship between the two linked entities
     * 
     * @param value ELinkTypes to be the Linkage's Link Type attribute
     */
    public void setLinkType(ELinkTypes value);

    /**
     * Sets a Marking class value of the Linkage's ClassificationMarking attribute
     * 
     * @param value Marking class to be the Linkage's classification marking attribute
     */
    public void setClassificationMarking(Marking value);

    /**
     * Sets the identification of who modified the Linkage's Value attribute
     * 
     * @param value String to be the Linkage's ModifiedBy attribute
     */
    public void setModifiedBy(String value);

    /**
     * Sets the Input Language used when the Linkage's value was set.
     * 
     * @param value Locale to be the Linkage's Input language attribute
     */
    public void setInputLang(Locale value);
}
