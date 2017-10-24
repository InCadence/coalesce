/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.services.search.service.data.model;

import org.joda.time.DateTime;

import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.framework.datamodel.ICoalesceObject;

public class CoalesceObjectImpl implements ICoalesceObject {

    private String key;
    private String name;
    private DateTime dateCreated;
    private boolean flatten = false;
    private DateTime lastModified;
    private String modifiedBy;
    private String modifiedByIP;
    private boolean noIndex = false;
    private Integer objectVersion;
    private String namePath;
    private String previousHistoryKey;
    private ECoalesceObjectStatus status;
    private String tag;

    /**
     * Creates a new instance.
     * 
     * @param key
     * @param name
     */
    public CoalesceObjectImpl(String key, String name)
    {
        this.key = key;
        this.name = name;
    }

    public CoalesceObjectImpl(ICoalesceObject value)
    {
        key = value.getKey();
        name = value.getName();
        dateCreated = value.getDateCreated();
        flatten = value.isFlatten();
        lastModified = value.getLastModified();
        modifiedBy = value.getModifiedBy();
        modifiedByIP = value.getModifiedByIP();
        noIndex = value.isNoIndex();
        objectVersion = value.getObjectVersion();
        previousHistoryKey = value.getPreviousHistoryKey();
        status = value.getStatus();
        tag = value.getTag();
        namePath = value.getNamePath();
    }

    /**
     * @return the key
     */
    public String getKey()
    {
        return key;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }
    

    public String getNamePath()
    {
        return namePath;
    }


    
    public void setNamePath(String namePath)
    {
        this.namePath = namePath;
    }


    public DateTime getDateCreated()
    {
        return dateCreated;
    }

    
    public void setDateCreated(DateTime dateCreated)
    {
        this.dateCreated = dateCreated;
    }

    
    public boolean isFlatten()
    {
        return flatten;
    }

    
    public void setFlatten(boolean flatten)
    {
        this.flatten = flatten;
    }

    
    public DateTime getLastModified()
    {
        return lastModified;
    }

    
    public void setLastModified(DateTime lastModified)
    {
        this.lastModified = lastModified;
    }

    
    public String getModifiedBy()
    {
        return modifiedBy;
    }

    
    public void setModifiedBy(String modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }

    
    public String getModifiedByIP()
    {
        return modifiedByIP;
    }

    
    public void setModifiedByIP(String modifiedByIP)
    {
        this.modifiedByIP = modifiedByIP;
    }

    
    public boolean isNoIndex()
    {
        return noIndex;
    }

    
    public void setNoIndex(boolean noIndex)
    {
        this.noIndex = noIndex;
    }

    
    public Integer getObjectVersion()
    {
        return objectVersion;
    }

    
    public void setObjectVersion(Integer objectVersion)
    {
        this.objectVersion = objectVersion;
    }

    
    public String getPreviousHistoryKey()
    {
        return previousHistoryKey;
    }

    
    public void setPreviousHistoryKey(String previousHistoryKey)
    {
        this.previousHistoryKey = previousHistoryKey;
    }

    
    public ECoalesceObjectStatus getStatus()
    {
        return status;
    }

    
    public void setStatus(ECoalesceObjectStatus status)
    {
        this.status = status;
    }

    
    public String getTag()
    {
        return tag;
    }

    
    public void setTag(String tag)
    {
        this.tag = tag;
    }

    
    public void setKey(String key)
    {
        this.key = key;
    }

    
    public void setName(String name)
    {
        this.name = name;
    }

}
