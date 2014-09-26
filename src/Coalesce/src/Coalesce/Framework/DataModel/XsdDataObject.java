package Coalesce.Framework.DataModel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;

import Coalesce.Common.Helpers.JodaDateTimeHelper;
import Coalesce.Common.Helpers.StringHelper;

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

public abstract class XsdDataObject implements ICoalesceDataObject {

    /*--------------------------------------------------------------------------
    Protected Member Variables
    --------------------------------------------------------------------------*/

    protected XsdDataObject _parent;
    protected HashMap<String, XsdDataObject> _childDataObjects = new HashMap<String, XsdDataObject>();

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
     * Returns the (XML) String of the XsdDataObject.
     * 
     * @return
     */
    public abstract String toXml();

    /**
     * Sets the value of the XsdDataObject's attribute corresponding to the name argument.
     * 
     * @param name String, name of attribute to be set
     * @param value String, value to be assigned to the attribute
     * @return
     */
    public abstract boolean setAttribute(String name, String value);

    /*--------------------------------------------------------------------------
    Protected Abstract Functions
    --------------------------------------------------------------------------*/

    /**
     * Returns the value of the XsdDataObject's key attribute which should be the same as the entity's ObjectKey database
     * value.
     * 
     * @return String
     */
    protected abstract String getObjectKey();

    /**
     * Sets the value of the XsdDataObject's key attribute.
     * 
     * @param value String
     */
    protected abstract void setObjectKey(String value);

    /**
     * Sets the value of the XsdDataObject's LastModified attribute.
     * 
     * @param value DateTime
     */
    protected abstract void setObjectLastModified(DateTime value);

    /**
     * Returns the value of the XsdDataObject's status attribute.
     * 
     * @return String
     */
    protected abstract String getObjectStatus();

    /**
     * Sets the value of the XsdDataObject's status attribute.
     * 
     * @param value ECoalesceDataObjectStatus
     */
    protected abstract void setObjectStatus(ECoalesceDataObjectStatus status);

    /**
     * Returns a hashmap key-value pair of the XsdDataObject's attributes.
     * 
     * @return Map<QName, String>
     */
    protected abstract Map<QName, String> getAttributes();

    /**
     * Returns a hashmap key-value pair of the XsdDataObject's other attributes - attributes that fall into the
     * XsdDataObject's @XmlAnyAttribute HashMap.
     * 
     * @return Map<QName, String>
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
        if (StringHelper.IsNullOrEmpty(statusString))
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
    public XsdDataObject getParent()
    {
        return this._parent;
    }

    @Override
    public void setParent(XsdDataObject parent)
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
        // Set Last Modified
        this.setObjectLastModified(value);

        // Bubble Up to Parent
        if (this._parent != null)
        {
            this._parent.setLastModified(value);
        }
    }

    @Override
    public boolean getNoIndex()
    {
        return Boolean.parseBoolean(this.getAttribute("noindex"));
    }

    @Override
    public void setNoIndex(boolean value)
    {
        this.setAttribute("noindex", Boolean.toString(value));
    }

    @Override
    public Map<String, XsdDataObject> getChildDataObjects()
    {
        return this._childDataObjects;
    }

    /**
     * Sets the value of the XsdDataObject's DataObject Children, childDataObjects. New values with new keys are added,
     * values with existing keys are replaced.
     * 
     * @param key key identifying the childDataObject
     * @param value childDataObject value
     */
    public void setChildDataObjects(String key, XsdDataObject value)
    {
        this._childDataObjects.put(key, value);
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
     * Returns the value of the XsdDataObject's attribute that corresponds to the name.
     * 
     * @param name Attribute's name
     * @return String, Attribute's value
     */
    public String getAttribute(String name)
    {
        String attribute = getAttributes().get(new QName(name));

        if (attribute == null)
        {
            attribute = getOtherAttributes().get(new QName(name));
        }

        return attribute;
    }

    /**
     * Returns the String value of the XsdDataObject's other attribute that corresponds to the name; other attributes are
     * those that fall into the XsdDataObject's @XmlAnyAttribute HashMap.
     * 
     * @param name Attribute's name
     * @return String, Attribute's value
     */
    public String getOtherAttribute(String name)
    {
        return this.getOtherAttributes().get(new QName(name));
    }

    /**
     * Returns the DateTime value of the XsdDataObject's other attribute that corresponds to the name; other attributes are
     * those that fall into the XsdDataObject's @XmlAnyAttribute HashMap.
     * 
     * @param name Attribute's name
     * @return DateTime, Attribute's value
     */
    public DateTime getOtherAttributeAsDate(String name)
    {
        return JodaDateTimeHelper.fromXmlDateTimeUTC(getOtherAttribute(name));
    }

    /**
     * Sets the String value of the XsdDataObject's XsdDataObject's other attribute that corresponds to the name; other
     * attributes are those that fall into the XsdDataObject's @XmlAnyAttribute HashMap.
     * 
     * @param name String, @XmlAnyAttribute attribute name
     * @param value, @XmlAnyAttribute attribute value
     * @return boolean
     */
    public boolean setOtherAttribute(String name, String value)
    {
        this.getOtherAttributes().put(new QName(name), value);
        return true;
    }

    /**
     * Sets the DateTime value of the XsdDataObject's XsdDataObject's other attribute that corresponds to the name; other
     * attributes are those that fall into the XsdDataObject's @XmlAnyAttribute HashMap.
     * 
     * @param name String, @XmlAnyAttribute attribute name
     * @param value, @XmlAnyAttribute attribute DateTime value
     */
    public void setOtherAttributeAsDate(String name, DateTime value)
    {
        setOtherAttribute(name, JodaDateTimeHelper.toXmlDateTimeUTC(value));
    }

    /**
     * Returns the XsdDataObject's childDataObject that corresponds to the provided namepath.
     * 
     * @param namePath String corresponding to the desired childDataObject.
     * @return XsdDataObject childDataObject
     */
    public XsdDataObject getDataObjectForNamePath(String namePath)
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
            XsdDataObject dataObject = null;

            for (XsdDataObject child : _childDataObjects.values())
            {
                String childName = child.getName();

                if (childName != null && childName.equals(names[1]))
                {
                    dataObject = child;
                    break;
                }
            }

            if (dataObject != null)
            {

                String newPath = namePath.substring(namePath.indexOf("/") + 1);

                return dataObject.getDataObjectForNamePath(newPath);

            }

            // No object found
            break;
        }

        return null;
    }

    /**
     * Returns the XsdDataObject's childDataObject that corresponds to the provided objectkey.
     * 
     * @param key String corresponding to the desired childDataObject objectkey.
     * @return XsdDataObject childDataObject
     */
    public XsdDataObject getCoalesceDataObjectForKey(String key)
    {
        XsdDataObject result = null;

        if (this.getKey().equalsIgnoreCase(key))
        {
            result = this;
        }
        else
        {

            for (XsdDataObject child : _childDataObjects.values())
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

    protected String getStringElement(String value)
    {
        if (value == null) return "";

        return value;
    }

}
