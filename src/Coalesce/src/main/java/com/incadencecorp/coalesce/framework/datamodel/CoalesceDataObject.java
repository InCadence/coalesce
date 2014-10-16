package com.incadencecorp.coalesce.framework.datamodel;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.apache.commons.lang.LocaleUtils;
import org.joda.time.DateTime;

import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;

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

public abstract class CoalesceDataObject implements ICoalesceDataObject {

    /*--------------------------------------------------------------------------
    Protected Member Variables
    --------------------------------------------------------------------------*/

    private CoalesceDataObject _parent;
    private HashMap<String, CoalesceDataObject> _childDataObjects = new HashMap<String, CoalesceDataObject>();

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/
    CoalesceDataObject()
    {
        // Do Nothing
    }

    CoalesceDataObject(CoalesceDataObject dataObject)
    {
        // Copy Member Variables
        setParent(dataObject.getParent());
        _childDataObjects = dataObject.getChildDataObjects();
    }

    /*--------------------------------------------------------------------------
    Public Abstract Functions
    --------------------------------------------------------------------------*/

    @Override
    public abstract String getName();

    @Override
    public abstract void setName(String value);

    @Override
    public abstract DateTime getDateCreated();

    @Override
    public abstract void setDateCreated(DateTime value);

    @Override
    public abstract DateTime getLastModified();

    @Override
    public abstract String getType();

    /**
     * Returns the (XML) String of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}.
     * 
     * @return (XML) String of the DataObject
     */
    public abstract String toXml();

    /**
     * Sets the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s attribute
     * corresponding to the name argument.
     * 
     * @param name String, name of attribute to be set
     * @param value String, value to be assigned to the attribute
     * @return boolean indicating success/failure
     */
    public abstract boolean setAttribute(String name, String value);

    /*--------------------------------------------------------------------------
    Protected Abstract Functions
    --------------------------------------------------------------------------*/

    /**
     * Returns the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s key attribute
     * which should be the same as the entity's ObjectKey database value.
     * 
     * @return String of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s object key
     */
    protected abstract String getObjectKey();

    /**
     * Sets the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s key attribute.
     * 
     * @param value String to be the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s object key
     */
    protected abstract void setObjectKey(String value);

    /**
     * Sets the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s LastModified
     * attribute.
     * 
     * @param value DateTime to be the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s last
     *            modified date
     */
    protected abstract void setObjectLastModified(DateTime value);

    /**
     * Returns the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s status attribute.
     * 
     * @return String of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s status
     */
    protected abstract String getObjectStatus();

    /**
     * Sets the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s status attribute.
     * 
     * @param value ECoalesceDataObjectStatus to be the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s status
     */
    protected abstract void setObjectStatus(ECoalesceDataObjectStatus status);

    /**
     * Returns a hashmap key-value pair of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s
     * attributes.
     * 
     * @return Map<QName, String> of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s
     *         attributes
     */
    protected abstract Map<QName, String> getAttributes();

    /**
     * Returns a hashmap key-value pair of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s
     * other attributes - attributes that fall into the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s @XmlAnyAttribute HashMap.
     * 
     * @return Map<QName, String> of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s other
     *         attributes falling into the @XmlAnyAttribute because they weren't specified in the schema
     */
    protected abstract Map<QName, String> getOtherAttributes();

    /*--------------------------------------------------------------------------
    Public Interface Functions
    --------------------------------------------------------------------------*/

    @Override
    public ECoalesceDataObjectStatus getStatus()
    {
        // Get Status
        String statusString = this.getObjectStatus();

        // Valid String?
        if (StringHelper.isNullOrEmpty(statusString))
        {
            // No; Return Default
            return ECoalesceDataObjectStatus.ACTIVE;
        }
        else
        {
            // Yes; Parse String
            return ECoalesceDataObjectStatus.getTypeForLabel(getObjectStatus());
        }
    }

    @Override
    public void setStatus(ECoalesceDataObjectStatus value)
    {
        // Set Status SUccessful?
        this.setObjectStatus(value);

        // Yes; Update Last Modified
        this.setLastModified(JodaDateTimeHelper.nowInUtc());
    }

    @Override
    public CoalesceDataObject getParent()
    {
        return this._parent;
    }

    @Override
    public void setParent(CoalesceDataObject parent)
    {
        this._parent = parent;
    }

    @Override
    public String getKey()
    {
        return getObjectKey();
    }

    @Override
    public void setKey(String key)
    {
        this.setObjectKey(key);
    }

    @Override
    public void setKey(UUID guid)
    {
        setObjectKey(guid.toString());
    }

    @Override
    public String getTag()
    {
        return this.getAttribute("tag");
    }

    @Override
    public void setTag(String value)
    {
        this.setAttribute("tag", value);
    }

    @Override
    public boolean getFlatten()
    {
        return Boolean.parseBoolean(this.getAttribute("flatten"));
    }

    @Override
    public void setFlatten(boolean value)
    {
        this.setAttribute("flatten", Boolean.toString(value));
    }

    @Override
    public void setLastModified(DateTime value)
    {
        if (value != null)
        {
            // Set Last Modified
            this.setObjectLastModified(value);

            // Bubble Up to Parent
            if (this._parent != null)
            {
                this._parent.setLastModified(value);
            }
        }
    }

    @Override
    public boolean getNoIndex()
    {
        String value = this.getAttribute("noindex");

        if (StringHelper.isNullOrEmpty(value)) return false;

        return Boolean.parseBoolean(value);
    }

    @Override
    public void setNoIndex(boolean value)
    {
        if (value)
        {
            setAttribute("noindex", Boolean.toString(value));
        }
        else
        {
            getOtherAttributes().remove("noindex");
        }
    }

    @Override
    public HashMap<String, CoalesceDataObject> getChildDataObjects()
    {
        return this._childDataObjects;
    }

    /**
     * Sets the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s DataObject Children,
     * childDataObjects. New values with new keys are added, values with existing keys are replaced.
     * 
     * @param key key identifying the childDataObject
     * @param value childDataObject to add to the DataObject's children
     */
    public void setChildDataObject(String key, CoalesceDataObject value)
    {
        this._childDataObjects.put(key, value);
    }

    public CoalesceDataObject getChildDataObject(String key)
    {
        return this._childDataObjects.get(key);
    }

    @Override
    public String getNamePath()
    {
        if (this._parent == null)
        {
            return this.getName();
        }
        else
        {
            return this._parent.getNamePath() + "/" + this.getName();
        }
    }

    /*--------------------------------------------------------------------------
    Public Functions
    --------------------------------------------------------------------------*/

    /**
     * Returns the value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s attribute that
     * corresponds to the name.
     * 
     * @param name Attribute's name
     * @return String, Attribute's value
     */
    public String getAttribute(String name)
    {
        String attribute = getAttributes().get(new QName(name.toLowerCase()));

        if (attribute == null)
        {
            attribute = getOtherAttributes().get(new QName(name.toLowerCase()));
        }

        return attribute;
    }

    /**
     * Returns the String value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s other
     * attribute that corresponds to the name; other attributes are those that fall into the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s @XmlAnyAttribute HashMap.
     * 
     * @param name Attribute's name
     * @return String, Attribute's value
     */
    public String getOtherAttribute(String name)
    {
        return this.getOtherAttributes().get(new QName(name));
    }

    /**
     * Returns the DateTime value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s other
     * attribute that corresponds to the name; other attributes are those that fall into the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s @XmlAnyAttribute HashMap.
     * 
     * @param name Attribute's name
     * @return DateTime, Attribute's value
     */
    public DateTime getOtherAttributeAsDate(String name)
    {
        return JodaDateTimeHelper.fromXmlDateTimeUTC(getOtherAttribute(name));
    }

    /**
     * Sets the String value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s other attribute that corresponds to the
     * name; other attributes are those that fall into the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s @XmlAnyAttribute HashMap.
     * 
     * @param name String @XmlAnyAttribute attribute name
     * @param value @XmlAnyAttribute attribute value
     * @return boolean indicating success/failure
     */
    public boolean setOtherAttribute(String name, String value)
    {
        getOtherAttributes().put(new QName(name.toLowerCase()), value);
        return true;
    }

    /**
     * Sets the DateTime value of the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s other attribute that corresponds to the
     * name; other attributes are those that fall into the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s @XmlAnyAttribute HashMap.
     * 
     * @param name String, @XmlAnyAttribute attribute name
     * @param value @XmlAnyAttribute attribute DateTime value
     */
    public void setOtherAttributeAsDate(String name, DateTime value)
    {
        setOtherAttribute(name, JodaDateTimeHelper.toXmlDateTimeUTC(value));
    }

    /**
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s childDataObject that
     * corresponds to the provided namepath.
     * 
     * @param namePath String corresponding to the desired childDataObject.
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject} of the childDataObject or null if
     *         one is not found
     */
    public CoalesceDataObject getDataObjectForNamePath(String namePath)
    {
        if (namePath == null) return null;

        String[] names = namePath.split("/");

        switch (names.length) {
        case 0:

            // No path. Object not found.
            break;

        case 1:

            // End of the path, is our Base Object named the Name Path?
            if (getName().equals(names[0]))
            {
                return this;
            }

            // No object found
            break;

        default:

            // Find next child
            CoalesceDataObject dataObject = null;

            for (CoalesceDataObject child : _childDataObjects.values())
            {
                String childName = child.getName();

                if (childName != null && childName.equals(names[names.length - 1]))
                {
                    dataObject = child;
                    break;
                }
            }

            if (dataObject != null)
            {

                String newPath = namePath.substring(namePath.lastIndexOf("/") + 1);

                return dataObject.getDataObjectForNamePath(newPath);

            }

            // No object found
            break;
        }

        return null;
    }

    /**
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject}'s childDataObject that
     * corresponds to the provided objectkey.
     * 
     * @param key String corresponding to the desired childDataObject objectkey.
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject} for childDataObject or null if one
     *         is not found
     */
    public CoalesceDataObject getCoalesceDataObjectForKey(String key)
    {
        CoalesceDataObject result = null;

        if (this.getKey().equalsIgnoreCase(key))
        {
            result = this;
        }
        else
        {

            for (CoalesceDataObject child : _childDataObjects.values())
            {
                result = child.getCoalesceDataObjectForKey(key);
                if (result != null) break;
            }
        }

        return result;
    }

    /*--------------------------------------------------------------------------
    Protected Functions
    --------------------------------------------------------------------------*/

    protected boolean initialize()
    {

        if (getKey() == null || getKey().equals(""))
        {
            setKey(java.util.UUID.randomUUID().toString());
        }

        DateTime utcDate = JodaDateTimeHelper.nowInUtc();

        if (getDateCreated() == null)
        {
            setDateCreated(utcDate);
        }
        if (getLastModified() == null)
        {
            setLastModified(utcDate);
        }

        return true;

    }

    protected boolean initialize(CoalesceDataObject dataObject)
    {
        setParent(dataObject.getParent());

        _childDataObjects = dataObject.getChildDataObjects();

        return true;
    }

    protected String getStringElement(String value)
    {
        if (value == null) return "";

        return value;
    }

    protected boolean getBooleanElement(Boolean value)
    {
        if (value == null)
        {
            return false;
        }
        else
        {
            return value;
        }
    }

    protected void addChild(CoalesceDataObject newChild)
    {
        // Add to Parent's Child Collection
        if (!(_childDataObjects.containsKey(newChild.getKey())))
        {
            _childDataObjects.put(newChild.getKey(), newChild);
        }

    }

    protected static Locale parseLocale(String value)
    {
        String[] parts = value.replace("-", "_").split("_");

        String formatted;
        switch (parts.length) {
        case 1:
            formatted = parts[0].toLowerCase();
            break;

        case 2:
            formatted = parts[0].toLowerCase() + "_" + parts[1].toUpperCase();
            break;

        case 3:
            formatted = parts[0].toLowerCase() + "_" + parts[1].toUpperCase() + "_" + parts[2];
            break;

        default:
            return null;

        }

        try
        {
            Locale inputLang = LocaleUtils.toLocale(formatted);

            return inputLang;

        }
        catch (IllegalArgumentException iae)
        {
            return null;
        }

    }
}
