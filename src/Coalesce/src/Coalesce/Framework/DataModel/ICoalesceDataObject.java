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
     * @return
     */
    public ECoalesceDataObjectStatus getStatus();

    /**
     * Sets the status of the XsdDataObject, which identifies whether the object is active, deleted or of another status.
     * 
     * @param value
     */
    public void setStatus(ECoalesceDataObjectStatus value);

    // Parent
    /**
     * Returns the parent XsdDataObject of the current XsdDataObject
     * 
     * @return
     */
    public XsdDataObject getParent();

    /**
     * Sets the parent XsdDataObject of the current XsdDataObject
     * 
     * @param parent
     */
    public void setParent(XsdDataObject parent);

    // Key
    /**
     * Returns the string value of the XsdDataObject's key
     * 
     * @return
     */
    public String getKey();

    /**
     * Sets the value of the XsdDataObject's key by a String parameter
     * 
     * @param value
     */
    public void setKey(String value);

    /**
     * Sets the value of the XsdDataObject's key by a UUID parameter
     * 
     * @param guid
     */
    public void setKey(UUID guid);

    // Name
    /**
     * Returns the value of the XsdDataObject's name attribute.
     * 
     * @return String
     */
    public String getName();

    /**
     * Sets the value of the XsdDataObject's name attribute.
     * 
     * @param value
     */
    public void setName(String value);

    // Tag
    /**
     * Returns the value of the XsdDataObject's tag attribute.
     * 
     * @return
     */
    public String getTag();

    /**
     * Sets the value of the XsdDataObject's tag attribute.
     * 
     * @param value
     */
    public void setTag(String value);

    // Flatten
    /**
     * Returns the value of the XsdDataObject's flatten attribute.
     * 
     * @return
     */
    public boolean getFlatten();

    /**
     * Sets the value of the XsdDataObject's flatten attribute.
     * 
     * @param value
     */
    public void setFlatten(boolean value);

    // Date Created
    /**
     * Returns the value of the XsdDataObject's DateCreated attribute.
     * 
     * @return
     */
    public DateTime getDateCreated();

    /**
     * Sets the value of the XsdDataObject's DateCreated attribute.
     * 
     * @param value
     */
    public void setDateCreated(DateTime value);

    // Last Modified
    /**
     * Returns the value of the XsdDataObject's LastModified attribute.
     * 
     * @return
     */
    public DateTime getLastModified();

    /**
     * Sets the value of the XsdDataObject's LastModified attribute.
     * 
     * @param value
     */
    public void setLastModified(DateTime value);

    // No Index
    /**
     * Returns the value of the XsdDataObject's NoIndex attribute.
     * 
     * @return
     */
    public boolean getNoIndex();

    /**
     * Sets the value of the XsdDataObject's NoIndex attribute.
     * 
     * @param value
     */
    public void setNoIndex(boolean value);

    /*--------------------------------------------------------------------------
    Public Read Only
    --------------------------------------------------------------------------*/

    /**
     * Returns the XsdDataObject's child XsdDataObjects. E.g. an XsdEntity will have XsdLinkageSection and XsdSection
     * children.
     * 
     * @return
     */
    public Map<String, XsdDataObject> getChildDataObjects();

    /**
     * Returns the String XsdDataObject type. E.g. field, linkage, section, etc.
     * 
     * @return
     */
    public String getType();

    /**
     * Returns the name path of the XsdDataObjects which is a "/" separated String of XsdDataObject names identifying where
     * the XsdDataObject within the larger XsdDataObject.
     * 
     * @return
     */
    public String getNamePath();

}
