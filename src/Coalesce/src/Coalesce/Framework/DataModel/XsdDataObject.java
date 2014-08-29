package Coalesce.Framework.DataModel;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;

import Coalesce.Common.Helpers.JodaDateTimeHelper;
import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;

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
    public abstract String GetName();

    @Override
    public abstract void SetName(String value);

    @Override
    public abstract DateTime GetDateCreated();

    @Override
    public abstract void SetDateCreated(DateTime value);

    @Override
    public abstract DateTime GetLastModified();
    
    @Override 
    public abstract String getType(); 

    public abstract String ToXml();

    /*--------------------------------------------------------------------------
    Protected Abstract Functions
    --------------------------------------------------------------------------*/

    protected abstract String GetObjectKey();

    protected abstract void SetObjectKey(String value);

    protected abstract void SetObjectLastModified(DateTime value);

    protected abstract String GetObjectStatus();

    protected abstract void SetObjectStatus(String status);

    protected abstract Map<QName, String> getAttributes();

    /*--------------------------------------------------------------------------
    Public Interface Functions
    --------------------------------------------------------------------------*/

    @Override
    public ECoalesceDataObjectStatus GetStatus()
    {

        // Get Status
        String statusString = this.GetObjectStatus();

        // Valid String?
        if (statusString == null || statusString.equals(""))
        {
            // No; Return Default
            return ECoalesceDataObjectStatus.ACTIVE;
        }
        else
        {
            // Yes; Parse String
            return (ECoalesceDataObjectStatus.fromLabel(statusString));
        }

    }

    @Override
    public void SetStatus(ECoalesceDataObjectStatus value)
    {
        // Set Status SUccessful?
        this.SetObjectStatus(value.toLabel());

        // Yes; Update Last Modified
        this.SetLastModified(new DateTime());
    }

    @Override
    public XsdDataObject GetParent()
    {
        return this._parent;
    }

    @Override
    public void SetParent(XsdDataObject parent)
    {
        this._parent = parent;
    }

    @Override
    public String GetKey()
    {
        return GetObjectKey();
    }

    @Override
    public void SetKey(String key)
    {
        this.SetObjectKey(key);
    }

    @Override
    public String GetTag()
    {
        return this.GetAttribute("tag");
    }

    @Override
    public void SetTag(String value)
    {
        this.SetAttribute("tag", value);
    }

    @Override
    public boolean GetFlatten()
    {
        return Boolean.parseBoolean(this.GetAttribute("flatten"));
    }

    @Override
    public void SetFlatten(boolean value)
    {
        this.SetAttribute("flatten", Boolean.toString(value));
    }

    @Override
    public void SetLastModified(DateTime value)
    {
        // Set Last Modified
        this.SetObjectLastModified(value);

        // Bubble Up to Parent
        if (this._parent != null)
        {
            this._parent.SetLastModified(value);
        }
    }

    @Override
    public boolean GetNoIndex()
    {
        return Boolean.parseBoolean(this.GetAttribute("noindex"));
    }

    @Override
    public void SetNoIndex(boolean value)
    {
        this.SetAttribute("noindex", Boolean.toString(value));
    }

    @Override
    public Map<String, XsdDataObject> GetChildDataObjects()
    {
        return this._childDataObjects;
    }

    @Override
    public String GetNamePath()
    {
        if (this._parent == null)
        {
            return this.GetName();
        }
        else
        {
            return this._parent.GetNamePath() + "/" + this.GetName();
        }
    }

    /*--------------------------------------------------------------------------
    Public Functions
    --------------------------------------------------------------------------*/

    public String GetAttribute(String name)
    {
        return this.getAttributes().get(new QName(name));
    }

    public DateTime GetAttributeAsDate(String name)
    {
        // TODO: Not Implemented
        return null;
    }

    public boolean SetAttribute(String name, String value)
    {
        this.getAttributes().put(new QName(name), value);
        return true;
    }

    public void SetAttributeAsDate(String name, DateTime date)
    {
        // TODO: Not Implemented
    }

    /*--------------------------------------------------------------------------
    Protected Functions
    --------------------------------------------------------------------------*/

    protected boolean Initialize()
    {

        if (GetKey() == null || GetKey().equals(""))
        {
            SetKey(java.util.UUID.randomUUID().toString());
        }

        DateTime utcDate = JodaDateTimeHelper.NowInUtc();

        if (GetDateCreated() == null)
        {
            SetDateCreated(utcDate);
        }
        if (GetLastModified() == null)
        {
            SetLastModified(utcDate);
        }

        return true;

    }

    protected XsdDataObject GetDataObjectForNamePath(String namePath)
    {
        try
        {

            String[] names = namePath.split("/");

            switch (names.length) {
            case 0:

                // No path. Object not found.
                break;

            case 1:

                // End of the path, is our Base Object named the Name Path?
                if (GetName().equals(names[0]))
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
                    if (child.GetName().equals(names[1]))
                    {
                        dataObject = child;
                        break;
                    }
                }

                if (dataObject != null)
                {

                    String newPath = namePath.substring(namePath.indexOf("/") + 1);

                    return dataObject.GetDataObjectForNamePath(newPath);

                }

                // No object found
                break;
            }

            return null;

        }
        catch (Exception ex)
        {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
            return null;
        }
    }

    protected String GetStringElement(String value)
    {
        if (value == null) return "";

        return value;
    }

}
