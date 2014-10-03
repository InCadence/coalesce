package Coalesce.Framework.DataModel;

import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;

public interface ICoalesceDataObject {

    /*--------------------------------------------------------------------------
    Public Properties
    --------------------------------------------------------------------------*/

    // Status
    /**
     * Returns the XsdDataObject status identifying whether the object is active, deleted or of another status.
     * 
     * @return ECoalesceDataObjectStatus the DataObject's status
     */
    public ECoalesceDataObjectStatus getStatus();

    /**
     * Sets the status of the XsdDataObject, which identifies whether the object is active, deleted or of another status.
     * 
     * @param value ECoalesceDataObjectStatus the DataObject's status
     */
    public void setStatus(ECoalesceDataObjectStatus value);

    // Parent
    /**
     * Returns the parent XsdDataObject of the current XsdDataObject
     * 
     * @return XsdDataObject the DataObject's parent
     */
    public CoalesceDataObject getParent();

    /**
     * Sets the parent XsdDataObject of the current XsdDataObject
     * 
     * @param parent XsdDataObject of the DataObject
     */
    public void setParent(CoalesceDataObject parent);

    // Key
    /**
     * Returns the string value of the XsdDataObject's key
     * 
     * @return String of the DataObject's key
     */
    public String getKey();

    /**
     * Sets the value of the XsdDataObject's key by a String parameter
     * 
     * @param value String to be the DataObject's key
     */
    public void setKey(String value);

    /**
     * Sets the value of the XsdDataObject's key by a UUID parameter
     * 
     * @param guid UUID to be the DataObject's key
     */
    public void setKey(UUID guid);

    // Name
    /**
     * Returns the value of the XsdDataObject's name attribute.
     * 
     * @return String of the DataObject's name
     */
    public String getName();

    /**
     * Sets the value of the XsdDataObject's name attribute.
     * 
     * @param value to be the DataObject's name
     */
    public void setName(String value);

    // Tag
    /**
     * Returns the value of the XsdDataObject's tag attribute.
     * 
     * @return String the DataObject's tag
     */
    public String getTag();

    /**
     * Sets the value of the XsdDataObject's tag attribute.
     * 
     * @param value String to be the DataObject's tag
     */
    public void setTag(String value);

    // Flatten
    /**
     * Returns the value of the XsdDataObject's flatten attribute.
     * 
     * @return boolean of the XsdDataObject's flatten attribute.
     */
    public boolean getFlatten();

    /**
     * Sets the value of the XsdDataObject's flatten attribute.
     * 
     * @param value boolean to be the XsdDataObject's flatten attribute.
     */
    public void setFlatten(boolean value);

    // Date Created
    /**
     * Returns the value of the XsdDataObject's DateCreated attribute.
     * 
     * @return DateTime of the XsdDataObject's DateCreated attribute.
     */
    public DateTime getDateCreated();

    /**
     * Sets the value of the XsdDataObject's DateCreated attribute.
     * 
     * @param value DateTime to be the XsdDataObject's DateCreated attribute.
     */
    public void setDateCreated(DateTime value);

    // Last Modified
    /**
     * Returns the value of the XsdDataObject's LastModified attribute.
     * 
     * @return DateTime of the XsdDataObject's LastModified attribute.
     */
    public DateTime getLastModified();

    /**
     * Sets the value of the XsdDataObject's LastModified attribute.
     * 
     * @param value DateTime to be the XsdDataObject's LastModified attribute.
     */
    public void setLastModified(DateTime value);

    // No Index
    /**
     * Returns the value of the XsdDataObject's NoIndex attribute.
     * 
     * @return boolean of the XsdDataObject's NoIndex attribute.
     */
    public boolean getNoIndex();

    /**
     * Sets the value of the XsdDataObject's NoIndex attribute.
     * 
     * @param value boolean to be the XsdDataObject's NoIndex attribute.
     */
    public void setNoIndex(boolean value);

    /*--------------------------------------------------------------------------
    Public Read Only
    --------------------------------------------------------------------------*/

    /**
     * Returns the XsdDataObject's child XsdDataObjects. E.g. an XsdEntity will have XsdLinkageSection and XsdSection
     * children.
     * 
     * @return hashmap of this XsdDataObject's child XsdDataObjects
     */
    public Map<String, CoalesceDataObject> getChildDataObjects();

    /**
     * Returns the String XsdDataObject type. E.g. field, linkage, section, etc.
     * 
     * @return String of the XsdDataObject's type attribute.
     */
    public String getType();

    /**
     * Returns the name path of the XsdDataObjects which is a "/" separated String of XsdDataObject names identifying where
     * the XsdDataObject within the larger XsdDataObject.
     * 
     * @return String of the XsdDataObject's namepath attribute.
     */
    public String getNamePath();

}
