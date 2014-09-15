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

    public abstract String toXml();

    /*--------------------------------------------------------------------------
    Protected Abstract Functions
    --------------------------------------------------------------------------*/

    protected abstract String getObjectKey();

    protected abstract void setObjectKey(String value);

    protected abstract void setObjectLastModified(DateTime value);

    protected abstract String getObjectStatus();

    protected abstract void setObjectStatus(ECoalesceDataObjectStatus status);

    protected abstract Map<QName, String> getAttributes();

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
        this.setLastModified(JodaDateTimeHelper.NowInUtc());
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
    
    public void setChildDataObjects(String key, XsdDataObject value) {
        this._childDataObjects.put(key,value);
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

    public String getAttribute(String name)
    {
        return this.getAttributes().get(new QName(name));
    }

    public DateTime getAttributeAsDate(String name)
    {
        // TODO: Not Implemented
        return null;
    }

    public boolean setAttribute(String name, String value)
    {
        this.getAttributes().put(new QName(name), value);
        return true;
    }

    public void setAttributeAsDate(String name, DateTime date)
    {
        // TODO: Not Implemented
    }

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

        DateTime utcDate = JodaDateTimeHelper.NowInUtc();

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
