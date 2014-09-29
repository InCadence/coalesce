package Coalesce.Framework.DataModel;

import java.util.Locale;

import Coalesce.Common.Classification.Marking;

public interface ICoalesceLinkage {

    // Methods
    /**
     * Return the value of the Linkage's Name attribute
     * 
     * @return
     */
    public String getName();

    /**
     * Return the key value corresponding to the attribute of the Linkage's first entity
     * 
     * @return
     */
    public String getEntity1Key();

    /**
     * Return the name value corresponding to the attribution of the Linkage's first entity
     * 
     * @return
     */
    public String getEntity1Name();

    /**
     * Return the source value corresponding to the attribution of the Linkage's first entity
     * 
     * @return
     */
    public String getEntity1Source();

    /**
     * Return the version value corresponding to the attribution of the Linkage's first entity
     * 
     * @return
     */
    public String getEntity1Version();

    /**
     * Return the key value corresponding to the attribute of the Linkage's second entity
     * 
     * @return
     */
    public String getEntity2Key();

    /**
     * Return the name value corresponding to the attribution of the Linkage's second entity
     * 
     * @return
     */
    public String getEntity2Name();

    /**
     * Return the source value corresponding to the attribution of the Linkage's second entity
     * 
     * @return
     */
    public String getEntity2Source();

    /**
     * Return the version value corresponding to the attribution of the Linkage's second entity
     * 
     * @return
     */
    public String getEntity2Version();

    /**
     * Return the link type identifying the relationship between the two linked entities
     * 
     * @return
     */
    public ELinkTypes getLinkType();

    /**
     * Return a Marking class value of the Linkage's ClassificationMarking attribute
     * 
     * @return
     */
    public Marking getClassificationMarking();

    /**
     * Return identification of who modified the Linkage's Value attribute
     * 
     * @return
     */
    public String getModifiedBy();

    /**
     * Returns the Input Language used when the Linkage's value was set.
     * 
     * @return
     */
    public Locale getInputLang();

    /**
     * Set the value of the Linkage's Name attribute
     * 
     * @param value
     */
    public void setName(String value);

    /**
     * Set the key value corresponding to the attribute of the Linkage's first entity
     * 
     * @param value
     */
    public void setEntity1Key(String value);

    /**
     * Set the name value corresponding to the attribute of the Linkage's first entity
     * 
     * @param value
     */
    public void setEntity1Name(String value);

    /**
     * Set the source value corresponding to the attribute of the Linkage's first entity
     * 
     * @param value
     */
    public void setEntity1Source(String value);

    /**
     * Set the version value corresponding to the attribute of the Linkage's first entity
     * 
     * @param value
     */
    public void setEntity1Version(String value);

    /**
     * Set the key value corresponding to the attribute of the Linkage's second entity
     * 
     * @param value
     */
    public void setEntity2Key(String value);

    /**
     * Set the name value corresponding to the attribute of the Linkage's second entity
     * 
     * @param value
     */
    public void setEntity2Name(String value);

    /**
     * Set the source value corresponding to the attribute of the Linkage's second entity
     * 
     * @param value
     */
    public void setEntity2Source(String value);

    /**
     * Set the version value corresponding to the attribute of the Linkage's second entity
     * 
     * @param value
     */
    public void setEntity2Version(String value);

    /**
     * Sets the link type identifying the relationship between the two linked entities
     * 
     * @param value
     */
    public void setLinkType(ELinkTypes value);

    /**
     * Sets a Marking class value of the Linkage's ClassificationMarking attribute
     * 
     * @param value
     */
    public void setClassificationMarking(Marking value);

    /**
     * Sets the identification of who modified the Linkage's Value attribute
     * 
     * @param value
     */
    public void setModifiedBy(String value);

    /**
     * Sets the Input Language used when the Linkage's value was set.
     * 
     * @param value
     */
    public void setInputLang(Locale value);
}
