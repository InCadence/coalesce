package com.incadencecorp.coalesce.framework.datamodel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;

import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;

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
/**
 * Every element within Coalesce extends this base class which defines common
 * properties.
 * 
 * @author Derek C.
 */
public abstract class CoalesceObject implements ICoalesceObject {

    /*--------------------------------------------------------------------------
    Protected Member Variables
    --------------------------------------------------------------------------*/

    private CoalesceObject _parent;
    private CoalesceObjectType _object;

    private HashMap<String, CoalesceObject> _children = new HashMap<String, CoalesceObject>();

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/
    /**
     * Class constructor. Creates a CoalesceObject class.
     */
    CoalesceObject()
    {
        // Do Nothing
    }

    /**
     * Class constructor. Creates a CoalesceObject class.
     * 
     * @param coalesceObject allowed object is {@link CoalesceObject }
     */
    CoalesceObject(CoalesceObject coalesceObject)
    {
        // Copy Member Variables
        setParent(coalesceObject.getParent());
        _children = coalesceObject.getChildCoalesceObjects();
        _object = coalesceObject._object;
    }

    /*--------------------------------------------------------------------------
    Public Functions
    --------------------------------------------------------------------------*/

    @Override
    public String getName()
    {
        return getStringElement(_object.getName());
    }

    @Override
    public void setName(String value)
    {
        _object.setName(value);
    }

    @Override
    public final DateTime getDateCreated()
    {
        return _object.getDatecreated();
    }

    @Override
    public final void setDateCreated(DateTime value)
    {
        _object.setDatecreated(value);
    }

    @Override
    public final DateTime getLastModified()
    {
        return _object.getLastmodified();
    }

    @Override
    public final int getObjectVersion()
    {

        Integer version = _object.getObjectversion();

        if (version == null)
        {
            version = 1;
        }

        return version;
    }

    /**
     * Sets the object's version that this element was added.
     * 
     * @param version
     */
    protected final void setObjectVersion(Integer version)
    {
        if (version != null && version <= 1)
        {
            version = null;
        }

        _object.setObjectversion(version);
    }

    /**
     * @return whether the version was deleted.
     */
    public final boolean isObjectVersionDeleted()
    {
        return getObjectVersionStatus() == ECoalesceObjectStatus.DELETED;
    }

    /**
     * @return the status of the version of this element.
     */
    protected final ECoalesceObjectStatus getObjectVersionStatus()
    {
        ECoalesceObjectStatus value = _object.getObjectversionstatus();

        if (value == null)
        {
            value = ECoalesceObjectStatus.ACTIVE;
        }

        return value;
    }

    /**
     * Sets the status of the version of this element.
     * 
     * @param value
     */
    protected final void setObjectVersionStatus(ECoalesceObjectStatus value)
    {
        if (value != ECoalesceObjectStatus.DELETED)
        {
            value = null;
        }

        _object.setObjectversionstatus(value);

        updateLastModified();
    }

    @Override
    public final String getModifiedBy()
    {
        return getStringElement(_object.getModifiedby());
    }

    @Override
    public final void setModifiedBy(String value)
    {
        _object.setModifiedby(value);
    }

    @Override
    public final String getModifiedByIP()
    {
        return getStringElement(_object.getModifiedbyip());
    }

    @Override
    public final void setModifiedByIP(String value)
    {
        _object.setModifiedbyip(value);
    }

    @Override
    public final String getPreviousHistoryKey()
    {

        String prevHistKey = _object.getPrevioushistorykey();
        if (StringHelper.isNullOrEmpty(prevHistKey))
        {
            return "00000000-0000-0000-0000-000000000000";
        }
        else
        {
            return prevHistKey;
        }

    }

    @Override
    public final void setPreviousHistoryKey(String value)
    {
        _object.setPrevioushistorykey(value);
    }

    @Override
    public final ECoalesceObjectStatus getStatus()
    {

        ECoalesceObjectStatus status = _object.getStatus();

        if (status == null)
        {
            status = ECoalesceObjectStatus.ACTIVE;
        }

        return status;

    }

    public void markAsDeleted()
    {
        setStatus(ECoalesceObjectStatus.DELETED);
    }

    @Override
    public void setStatus(ECoalesceObjectStatus value)
    {
        // Set Status SUccessful?
        _object.setStatus(value);

        // Yes; Update Last Modified
        updateLastModified();
    }

    @Override
    public final boolean isMarkedDeleted()
    {
        return getStatus() == ECoalesceObjectStatus.DELETED;
    }

    @Override
    public final boolean isActive()
    {
        switch (getStatus()) {
        case READONLY:
        case ACTIVE:
            return true;
        default:
            return false;
        }
    }

    @Override
    public final boolean isReadOnly()
    {
        switch (getStatus()) {
        case READONLY:
            return true;
        default:
            return false;
        }
    }

    @Override
    public final CoalesceObject getParent()
    {
        return this._parent;
    }

    @Override
    public final CoalesceEntity getEntity()
    {
        CoalesceObject element = this.getParent(); 
        
        while(element.getParent() != null) {
            element = element.getParent();
        }
        
        if (element instanceof CoalesceEntity) {
            return (CoalesceEntity) element;
        }
        else {
            return null;
        }
    }

    @Override
    public final void setParent(CoalesceObject parent)
    {
        this._parent = parent;
    }

    @Override
    public String getKey()
    {
        return _object.getKey();
    }

    @Override
    public void setKey(String key)
    {
        _object.setKey(key);
    }

    @Override
    public final void setKey(UUID guid)
    {
        setKey(guid.toString());
    }

    @Override
    public final String getTag()
    {
        return this.getAttribute("tag");
    }

    @Override
    public final void setTag(String value)
    {
        this.setAttribute("tag", value);
    }

    @Override
    public final boolean getFlatten()
    {
        String value = this.getAttribute("flatten");

        // Default Behavior.
        if (value == null)
            return true;

        return Boolean.parseBoolean(value);
    }

    @Override
    public final void setFlatten(boolean value)
    {
        this.setAttribute("flatten", Boolean.toString(value));
    }

    @Override
    public final void setLastModified(DateTime value)
    {
        if (value != null)
        {
            // Set Last Modified
            _object.setLastmodified(value);

            // Bubble Up to Parent
            if (this._parent != null)
            {
                this._parent.setLastModified(value);
            }
        }
    }

    @Override
    public final boolean getNoIndex()
    {
        Boolean value = _object.isNoindex();

        if (value == null)
        {
            value = false;
        }

        return value;
    }

    @Override
    public final void setNoIndex(boolean value)
    {
        if (value)
        {
            _object.setNoindex(value);
        }
        else
        {
            _object.setNoindex(null);
        }
    }

    /**
     * Updated the last modified value to be the current time.
     */
    public void updateLastModified()
    {
        setLastModified(JodaDateTimeHelper.nowInUtc());
    }

    /*--------------------------------------------------------------------------
    Public Abstract Functions
    --------------------------------------------------------------------------*/

    @Override
    public final String getType()
    {
        return _object.getClass().getSimpleName().toLowerCase();
    }

    /**
     * Returns the (XML) String of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}.
     * 
     * @return (XML) String of the Coalesce object
     */
    public final String toXml()
    {
        return XmlHelper.serialize(_object);
    }

    /*--------------------------------------------------------------------------
    Protected Abstract Functions
    --------------------------------------------------------------------------*/

    /**
     * Sets attributes that are unique to each Coalesce element.
     * 
     * @param name
     * @param value
     * @return <code>true</code> if successful.
     */
    protected abstract boolean setExtendedAttributes(String name, String value);

    /*--------------------------------------------------------------------------
    Public Interface Functions
    --------------------------------------------------------------------------*/

    /**
     * Sets the value of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * attribute corresponding to the name argument.
     * 
     * @param name String, name of attribute to be set
     * @param value String, value to be assigned to the attribute
     * @return boolean indicating success/failure
     */
    public final boolean setAttribute(String name, String value)
    {
        switch (name.toLowerCase()) {
        case "key":
            setKey(value);
            return true;
        case "datecreated":
            setDateCreated(JodaDateTimeHelper.fromXmlDateTimeUTC(value));
            return true;
        case "lastmodified":
            setLastModified(JodaDateTimeHelper.fromXmlDateTimeUTC(value));
            return true;
        case "name":
            setName(value);
            return true;
        case "status":
            setStatus(ECoalesceObjectStatus.valueOf(value));
            return true;
        case "modifiedby":
            setModifiedBy(value);
            return true;
        case "modifiedbyip":
            setModifiedByIP(value);
            return true;
        case "objectversion":
            setObjectVersion(Integer.parseInt(value));
            return true;
        case "objectversionstatus":
            setObjectVersionStatus(ECoalesceObjectStatus.valueOf(value));
            return true;
        case "previoushistorykey":
            setPreviousHistoryKey(value);
            return true;
        case "noindex":
            setNoIndex(Boolean.parseBoolean(value));
            return true;
        default:
            return setExtendedAttributes(name, value);
        }
    }

    @Override
    public final HashMap<String, CoalesceObject> getChildCoalesceObjects()
    {
        return this._children;
    }

    /**
     * Adds
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject} as
     * a child. New values with new keys are added, values with existing keys
     * are replaced.
     * 
     * @param key key identifying the child Coalesce object.
     * @param value child Coalesce object to add to the Coalesce object's
     *            children
     */
    public final void addChildCoalesceObject(String key, CoalesceObject value)
    {
        this._children.put(key, value);
    }

    /**
     * Returns the child CoalesceObject, for this CoalesceObject based on the
     * String key parameter.
     * 
     * @param key allowed object is {@link String }
     * @return possible object is {@link CoalesceObject }
     */
    public final CoalesceObject getChildCoalesceObject(String key)
    {
        return this._children.get(key);
    }

    @Override
    public final String getNamePath()
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
     * Returns the value of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * attribute that corresponds to the name.
     * 
     * @param name Attribute's name
     * @return String, Attribute's value
     */
    public final String getAttribute(String name)
    {
        String attribute = getAttributes().get(new QName(name.toLowerCase()));

        if (attribute == null)
        {
            attribute = _object.getOtherAttributes().get(new QName(name.toLowerCase()));
        }

        return attribute;
    }

    /**
     * Returns the String value of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * other attribute that corresponds to the name; other attributes are those
     * that fall into the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s @XmlAnyAttribute
     * HashMap.
     * 
     * @param name Attribute's name
     * @return String, Attribute's value
     */
    public final String getOtherAttribute(String name)
    {
        return _object.getOtherAttributes().get(new QName(name));
    }

    /**
     * Returns the String value of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * other attribute that corresponds to the name; other attributes are those
     * that fall into the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s @XmlAnyAttribute
     * HashMap.
     * 
     * @param name Attribute's name
     * @return String, Attribute's value
     */
    protected final Map<QName, String> getOtherAttributes()
    {
        return _object.getOtherAttributes();
    }

    /**
     * Returns the DateTime value of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * other attribute that corresponds to the name; other attributes are those
     * that fall into the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s @XmlAnyAttribute
     * HashMap.
     * 
     * @param name Attribute's name
     * @return DateTime, Attribute's value
     */
    public final DateTime getOtherAttributeAsDate(String name)
    {
        return JodaDateTimeHelper.fromXmlDateTimeUTC(getOtherAttribute(name));
    }

    /**
     * Sets the String value of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * other attribute that corresponds to the name; other attributes are those
     * that fall into the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s @XmlAnyAttribute
     * HashMap.
     * 
     * @param name String @XmlAnyAttribute attribute name
     * @param value @XmlAnyAttribute attribute value
     * @return boolean indicating success/failure
     */
    public final boolean setOtherAttribute(String name, String value)
    {
        _object.getOtherAttributes().put(new QName(name.toLowerCase()), value);
        return true;
    }

    /**
     * Sets the DateTime value of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * other attribute that corresponds to the name; other attributes are those
     * that fall into the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s @XmlAnyAttribute
     * HashMap.
     * 
     * @param name String, @XmlAnyAttribute attribute name
     * @param value @XmlAnyAttribute attribute DateTime value
     */
    public final void setOtherAttributeAsDate(String name, DateTime value)
    {
        setOtherAttribute(name, JodaDateTimeHelper.toXmlDateTimeUTC(value));
    }

    /**
     * Returns the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset}
     * 's that corresponds to the provided name path. If not found or not a
     * field <code>null</code> is returned.
     * 
     * @param names
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     *         of the child or null if one is not found
     */
    public final CoalesceRecordset getCoalesceRecordsetForNamePath(String... names)
    {

        CoalesceObject node = getCoalesceObjectForNamePath(names);

        if (node != null && node instanceof CoalesceRecordset)
        {
            return (CoalesceRecordset) node;
        }

        return null;

    }

    /**
     * Returns the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}'s
     * that corresponds to the provided name path. If not found or not a field
     * <code>null</code> is returned.
     * 
     * @param names
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     *         of the child or null if one is not found
     */
    public final CoalesceSection getCoalesceSectionForNamePath(String... names)
    {

        CoalesceObject node = getCoalesceObjectForNamePath(names);

        if (node != null && node instanceof CoalesceSection)
        {
            return (CoalesceSection) node;
        }

        return null;

    }

    /**
     * Returns the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}'s
     * that corresponds to the provided name path. If not found or not a field
     * <code>null</code> is returned.
     * 
     * @param names
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     *         of the child or null if one is not found
     */
    public final CoalesceField<?> getCoalesceFieldForNamePath(String... names)
    {

        CoalesceObject node = getCoalesceObjectForNamePath(names);

        if (node != null && node instanceof CoalesceField<?>)
        {
            return (CoalesceField<?>) node;
        }

        return null;

    }

    /**
     * You can either specify an xpath or pass in an array of names.
     * 
     * @param params String corresponding to the desired child.
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     *         of the child or <code>null</code> if one is not found or its not
     *         a field.
     */
    public final CoalesceObject getCoalesceObjectForNamePath(String... params)
    {
        if (params == null || params[0] == null)
            return null;

        String[] names;

        if (params.length == 1 && params[0].contains("/"))
        {
            names = params[0].split("/");
        }
        else
        {
            names = params;
        }

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
            CoalesceObject coalesceObject = null;

            for (CoalesceObject child : _children.values())
            {
                String childName = child.getName();

                if (childName != null && childName.equals(names[1]))
                {
                    coalesceObject = child;
                    break;
                }
            }

            if (coalesceObject != null)
            {

                // String newPath = namePath.substring(namePath.indexOf("/") +
                // 1);

                return coalesceObject.getCoalesceObjectForNamePath(popFirst(names));

            }

            // No object found
            break;
        }

        return null;
    }

    /**
     * Returns the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * child that corresponds to the provided key.
     * 
     * @param key String corresponding to the desired child key.
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     *         for the child or <code>null</code> if one is not found
     */
    public final CoalesceObject getCoalesceObjectForKey(String key)
    {
        CoalesceObject result = null;

        if (this.getKey().equalsIgnoreCase(key))
        {
            result = this;
        }
        else
        {

            for (CoalesceObject child : _children.values())
            {
                result = child.getCoalesceObjectForKey(key);
                if (result != null)
                    break;
            }
        }

        return result;
    }

    /*--------------------------------------------------------------------------
    Protected Functions
    --------------------------------------------------------------------------*/

    protected boolean initialize(CoalesceObjectType object)
    {

        _object = object;

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

    protected boolean initialize(CoalesceObject coalesceObject)
    {
        setParent(coalesceObject.getParent());

        _children = coalesceObject.getChildCoalesceObjects();
        _object = coalesceObject._object;

        return initialize(coalesceObject._object);
    }

    protected String getStringElement(String value)
    {
        if (value == null)
            return "";

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

    protected void addChildCoalesceObject(CoalesceObject newChild)
    {
        // Add to Parent's Child Collection
        if (!(_children.containsKey(newChild.getKey())))
        {
            _children.put(newChild.getKey(), newChild);
        }

    }

    /**
     * Prune an element from the entity.
     * 
     * @param child
     * @return whether is was successful or not.
     */
    public final boolean pruneCoalesceObject(CoalesceObject child)
    {
        _children.remove(child.getKey());

        return prune(child._object);
    }

    protected abstract boolean prune(CoalesceObjectType child);

    /**
     * @return Map<QName, String> of the
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     *         's attributes
     */
    protected Map<QName, String> getAttributes()
    {
        Map<QName, String> map = new HashMap<QName, String>();

        // Add Common Attributes
        map.put(new QName("key"), getKey());
        map.put(new QName("datecreated"), JodaDateTimeHelper.toXmlDateTimeUTC(getDateCreated()));
        map.put(new QName("lastmodified"), JodaDateTimeHelper.toXmlDateTimeUTC(getLastModified()));
        map.put(new QName("name"), getName());
        map.put(new QName("status"), getStatus().toString());
        map.put(new QName("noindex"), Boolean.toString(getNoIndex()));
        map.put(new QName("objectversion"), Integer.toString(getObjectVersion()));
        map.put(new QName("objectversionstatus"), getObjectVersionStatus().toString());
        map.put(new QName("previoushistorykey"), getPreviousHistoryKey());
        map.put(new QName("modifiedby"), getModifiedBy());
        map.put(new QName("modifiedbyip"), getModifiedByIP());

        map.putAll(_object.getOtherAttributes());

        return map;
    }

    private String[] popFirst(String... names)
    {

        String[] results = null;

        if (names.length > 1)
        {

            results = new String[names.length - 1];

            for (int ii = 1; ii < names.length; ii++)
            {
                results[ii - 1] = names[ii];
            }

        }

        return results;

    }
}