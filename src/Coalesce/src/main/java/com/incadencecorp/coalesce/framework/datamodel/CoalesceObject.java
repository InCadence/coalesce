package com.incadencecorp.coalesce.framework.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.incadencecorp.coalesce.api.Views;
import com.incadencecorp.coalesce.common.helpers.GUIDHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;
import org.joda.time.DateTime;

import javax.xml.namespace.QName;
import java.util.*;

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

    /**
     * Name of the element.
     */
    public static final String ATTRIBUTE_NAME = "name";
    /**
     * IP address of the last person to modified this object.
     */
    public static final String ATTRIBUTE_MODIFIEDBYIP = "modifiedbyip";
    /**
     * User who last modified this object.
     */
    public static final String ATTRIBUTE_MODIFIEDBY = "modifiedby";
    /**
     * Current status of this object
     */
    public static final String ATTRIBUTE_STATUS = "status";
    /**
     * The time this object was last modified.
     */
    public static final String ATTRIBUTE_LASTMODIFIED = "lastmodified";
    /**
     * The time this object was created.
     */
    public static final String ATTRIBUTE_DATECREATED = "datecreated";
    /**
     * Unique identifier for this object.
     */
    public static final String ATTRIBUTE_KEY = "key";

    /**
     * @see ICoalesceObject#isNoIndex()
     */
    public static final String ATTRIBUTE_NOINDEX = "noindex";

    public static final boolean ATTRIBUTE_NOINDEX_DEFAULT = true;

    /**
     * @see ICoalesceObject#isFlatten()
     */
    public static final String ATTRIBUTE_FLATTEN = "flatten";

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

    @JsonView(Views.Entity.class)
    @JsonProperty("dateCreated")
    public final String getDateCreatedAsString()
    {
        return JodaDateTimeHelper.toXmlDateTimeUTC(getDateCreated());
    }

    @JsonIgnore
    @Override
    public final DateTime getDateCreated()
    {
        return _object.getDatecreated();
    }

    @JsonProperty("dateCreated")
    public final void setDateCreatedAsString(String value)
    {
        setDateCreated(JodaDateTimeHelper.fromXmlDateTimeUTC(value));
    }

    @JsonIgnore
    @Override
    public final void setDateCreated(DateTime value)
    {
        _object.setDatecreated(value);
    }

    @JsonView(Views.Entity.class)
    @JsonProperty("lastModified")
    public final String getLastModifiedAsString()
    {
        return JodaDateTimeHelper.toXmlDateTimeUTC(getLastModified());
    }

    @JsonIgnore
    @Override
    public final DateTime getLastModified()
    {
        return _object.getLastmodified();
    }

    @Override
    public final Integer getObjectVersion()
    {

        Integer version = _object.getObjectversion();

        if (version == null)
        {
            version = 1;
        }

        return version;
    }

    @Override
    public final void setObjectVersion(Integer version)
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
    @JsonIgnore
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

    @JsonIgnore
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

    /**
     * Calls {@link #setPreviousHistoryKey(String)} using the key from the provided object.
     *
     * @param object to obtain key from.
     */
    @JsonIgnore
    public void setPreviousHistoryKey(CoalesceObject object)
    {
        setPreviousHistoryKey(object.getKey());
    }

    @Override
    public ECoalesceObjectStatus getStatus()
    {

        ECoalesceObjectStatus status = _object.getStatus();

        if (status == null)
        {
            status = ECoalesceObjectStatus.ACTIVE;
        }

        return status;

    }

    /**
     * Marks the entity as {@link ECoalesceObjectStatus#DELETED}.
     */
    public void markAsDeleted()
    {
        setStatus(ECoalesceObjectStatus.DELETED);
    }

    @Override
    public void setStatus(ECoalesceObjectStatus value)
    {
        if (value == getStatus())
        {
            return;
        }

        // Set Status SUccessful?
        _object.setStatus(value);

        // Yes; Update Last Modified
        updateLastModified();
    }

    /**
     * @return whether this element is marked as deleted.
     */
    @JsonIgnore
    public final boolean isMarkedDeleted()
    {
        return getStatus() == ECoalesceObjectStatus.DELETED;
    }

    /**
     * @return whether this element is new
     */
    @JsonIgnore
    public final boolean isNew()
    {
        return getStatus() == ECoalesceObjectStatus.NEW;
    }

    /**
     * @return whether this element is active or read only.
     */
    @JsonIgnore
    public final boolean isActive()
    {
        switch (getStatus())
        {
        case NEW:
        case READONLY:
        case ACTIVE:
            return true;
        default:
            return false;
        }
    }

    /**
     * @return whether this element is readonly only.
     */
    @JsonIgnore
    public final boolean isReadOnly()
    {
        switch (getStatus())
        {
        case READONLY:
            return true;
        default:
            return false;
        }
    }

    /**
     * Returns the parent
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject} of
     * the current
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}.
     *
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     * the Coalesce object's parent.
     */
    @JsonIgnore
    public final CoalesceObject getParent()
    {
        return this._parent;
    }

    /**
     * @return the owning CoalesceEntity for this element.
     */
    @JsonIgnore
    public final CoalesceEntity getEntity()
    {
        CoalesceObject element = this;

        while (element.getParent() != null)
        {
            element = element.getParent();
        }

        if (element instanceof CoalesceEntity)
        {
            return (CoalesceEntity) element;
        }
        else
        {
            throw new IllegalArgumentException("Entity Not Specified");
        }
    }

    /**
     * Sets the parent
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject} of
     * the current
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}.
     *
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     *               of the object.
     */
    public final void setParent(CoalesceObject parent)
    {
        this._parent = parent;
    }

    @Override
    public String getKey()
    {
        return (_object == null) ? null : _object.getKey();
    }

    @Override
    public void setKey(String key)
    {
        if (GUIDHelper.isValid(key))
        {
            CoalesceObject parent = getParent();

            if (parent != null)
            {
                parent.removeChildCoalesceObject(this);
            }

            _object.setKey(key);

            if (parent != null)
            {
                parent.addChildCoalesceObject(this);
            }
        }
    }

    @JsonIgnore
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
    public final boolean isFlatten()
    {
        String value = this.getAttribute(ATTRIBUTE_FLATTEN);

        // Default Behavior.
        if (value == null)
            return true;

        return Boolean.parseBoolean(value);
    }

    @Override
    public final void setFlatten(boolean value)
    {
        this.setAttribute(ATTRIBUTE_FLATTEN, Boolean.toString(value));
    }

    @JsonProperty("lastModified")
    public final void setLastModifiedAsString(String value)
    {
        setLastModified(JodaDateTimeHelper.fromXmlDateTimeUTC(value));
    }

    @JsonIgnore
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
    public final boolean isNoIndex()
    {
        Boolean value = _object.isNoindex();

        if (value == null)
        {
            value = ATTRIBUTE_NOINDEX_DEFAULT;
        }

        return value;
    }

    @Override
    public final void setNoIndex(boolean value)
    {
        if (value != ATTRIBUTE_NOINDEX_DEFAULT)
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

    /**
     * Returns the String
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     * type. E.g. field, linkage, section, etc.
     *
     * @return String of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     * 's type attribute.
     */
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
     * @param name  String, name of attribute to be set
     * @param value String, value to be assigned to the attribute
     * @return boolean indicating success/failure
     */
    public final boolean setAttribute(String name, String value)
    {
        switch (name.toLowerCase())
        {
        case ATTRIBUTE_KEY:
            setKey(value);
            return true;
        case ATTRIBUTE_DATECREATED:
            setDateCreated(JodaDateTimeHelper.fromXmlDateTimeUTC(value));
            return true;
        case ATTRIBUTE_LASTMODIFIED:
            setLastModified(JodaDateTimeHelper.fromXmlDateTimeUTC(value));
            return true;
        case ATTRIBUTE_NAME:
            setName(value);
            return true;
        case ATTRIBUTE_STATUS:
            setStatus(ECoalesceObjectStatus.valueOf(value.toUpperCase()));
            return true;
        case ATTRIBUTE_MODIFIEDBY:
            setModifiedBy(value);
            return true;
        case ATTRIBUTE_MODIFIEDBYIP:
            setModifiedByIP(value);
            return true;
        case "objectversion":
            setObjectVersion(Integer.parseInt(value));
            return true;
        case "objectversionstatus":
            setObjectVersionStatus(ECoalesceObjectStatus.valueOf(value.toUpperCase()));
            return true;
        case "previoushistorykey":
            setPreviousHistoryKey(value);
            return true;
        case ATTRIBUTE_NOINDEX:
            setNoIndex(Boolean.parseBoolean(value));
            return true;
        default:
            return setExtendedAttributes(name, value);
        }
    }

    /**
     * Returns the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * child
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}s
     * E.g. an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity}
     * will have
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection}
     * and
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceSection}
     * children.
     *
     * @return hashmap of this
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     * 's child
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     * s.
     */
    @JsonIgnore
    public final HashMap<String, CoalesceObject> getChildCoalesceObjects()
    {
        return this._children;
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
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     * 's @XmlAnyAttribute HashMap.
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
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     * 's @XmlAnyAttribute HashMap.
     *
     * @return String, Attribute's value
     */
    public final Map<QName, String> getOtherAttributes()
    {
        return _object.getOtherAttributes();
    }

    /**
     * Returns the DateTime value of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}'s
     * other attribute that corresponds to the name; other attributes are those
     * that fall into the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     * 's @XmlAnyAttribute HashMap.
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
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject} 's
     * XmlAnyAttribute HashMap.
     *
     * @param name  String XmlAnyAttribute attribute name
     * @param value XmlAnyAttribute attribute value
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
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     * 's @XmlAnyAttribute HashMap.
     *
     * @param name  String, XmlAnyAttribute attribute name
     * @param value XmlAnyAttribute attribute DateTime value
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
     * of the child or null if one is not found
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
     * of the child or null if one is not found
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
     * of the child or null if one is not found
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
     * of the child or <code>null</code> if one is not found or its not
     * a field.
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

        switch (names.length)
        {
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
     * for the child or <code>null</code> if one is not found
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

    /**
     * @param items
     * @param exclusions status to exclude from the return set.
     * @return a list of CoalesceObjects for the given list of XSD objects
     * @see #getObjectsAsList(List, ECoalesceObjectStatus...)
     * @deprecated
     */
    protected <T extends CoalesceObject, Y extends CoalesceObjectType> Map<String, T> getObjectsAsMap(List<Y> items,
                                                                                                      ECoalesceObjectStatus... exclusions)
    {
        List<ECoalesceObjectStatus> statusList = Arrays.asList(exclusions);
        Map<String, T> results = new HashMap<String, T>();

        for (Y item : items)
        {
            CoalesceObject childCoalesceObject = getChildCoalesceObject(item.getKey());
            if (childCoalesceObject != null && !statusList.contains(childCoalesceObject.getStatus()))
            {
                // You should never get a cast error here because we control
                // what is inserted into the map.
                results.put(item.key, (T) childCoalesceObject);
            }
        }

        return results;
    }

    /**
     * @param items
     * @param exclusions status to exclude from the return set.
     * @return a list of CoalesceObjects for the given list of XSD objects
     */
    protected <T extends CoalesceObject, Y extends CoalesceObjectType> List<T> getObjectsAsList(List<Y> items,
                                                                                                ECoalesceObjectStatus... exclusions)
    {
        List<ECoalesceObjectStatus> statusList = Arrays.asList(exclusions);
        List<T> results = new ArrayList<>();

        for (Y item : items)
        {
            CoalesceObject childCoalesceObject = getChildCoalesceObject(item.getKey());
            if (childCoalesceObject != null && !statusList.contains(childCoalesceObject.getStatus()))
            {
                // You should never get a cast error here because we control
                // what is inserted into the map.
                results.add((T) childCoalesceObject);
            }
        }

        return results;
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

    protected void removeChildCoalesceObject(CoalesceObject newChild)
    {
        // Add to Parent's Child Collection
        if (!(_children.containsKey(newChild.getKey())))
        {
            _children.remove(newChild.getKey());
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
        return pruneCoalesceObject(child.getKey());
    }

    /**
     * Prune an element from the entity.
     *
     * @param key
     * @return whether is was successful or not.
     */
    public final boolean pruneCoalesceObject(String key)
    {
        CoalesceObject child = _children.remove(key);

        return prune(child._object);
    }

    protected abstract boolean prune(CoalesceObjectType child);

    /**
     * @return Map&lt;QName, String&gt; of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceObject}
     * 's attributes
     */
    protected Map<QName, String> getAttributes()
    {
        Map<QName, String> map = new HashMap<QName, String>();

        // Add Common Attributes
        map.put(new QName(ATTRIBUTE_KEY), getKey());
        map.put(new QName(ATTRIBUTE_DATECREATED), JodaDateTimeHelper.toXmlDateTimeUTC(getDateCreated()));
        map.put(new QName(ATTRIBUTE_LASTMODIFIED), JodaDateTimeHelper.toXmlDateTimeUTC(getLastModified()));
        map.put(new QName(ATTRIBUTE_NAME), getName());
        map.put(new QName(ATTRIBUTE_STATUS), getStatus().toString());
        map.put(new QName(ATTRIBUTE_NOINDEX), Boolean.toString(isNoIndex()));
        map.put(new QName("objectversion"), Integer.toString(getObjectVersion()));
        map.put(new QName("objectversionstatus"), getObjectVersionStatus().toString());
        map.put(new QName("previoushistorykey"), getPreviousHistoryKey());
        map.put(new QName(ATTRIBUTE_MODIFIEDBY), getModifiedBy());
        map.put(new QName(ATTRIBUTE_MODIFIEDBYIP), getModifiedByIP());

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
