/*-----------------------------------------------------------------------------'
 Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.framework.persistance;

import org.joda.time.DateTime;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;

/**
 * This class is used by {@link ICoalescePersistor} to return metadata
 * information about templates and entities.
 * 
 * @author n78554
 */
public class ObjectMetaData {

    private String key;
    private String name;
    private String source;
    private String version;
    private DateTime created;
    private DateTime lastModified;

    /**
     * Creates a new instance.
     * 
     * @param entity
     */
    public ObjectMetaData(CoalesceEntity entity)
    {
        this(entity.getKey(),
             entity.getName(),
             entity.getSource(),
             entity.getVersion(),
             entity.getDateCreated(),
             entity.getLastModified());
    }

    /**
     * Creates a new instance.
     * 
     * @param key
     * @param name
     * @param source
     * @param version
     */
    public ObjectMetaData(String key, String name, String source, String version)
    {
        this(key, name, source, version, null, null);
    }

    /**
     * Creates a new instance.
     * 
     * @param key
     * @param name
     * @param source
     * @param version
     * @param created
     * @param lastModified
     */
    public ObjectMetaData(String key, String name, String source, String version, DateTime created, DateTime lastModified)
    {
        this.key = key;
        this.name = name;
        this.source = source;
        this.version = version;
        this.created = created;
        this.lastModified = lastModified;
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

    /**
     * @return the source
     */
    public String getSource()
    {
        return source;
    }

    /**
     * @return the version
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * @return the creation date
     */
    public DateTime getCreated()
    {
        return created;
    }

    /**
     * @return the last modified date
     */
    public DateTime getLastModified()
    {
        return lastModified;
    }

}
