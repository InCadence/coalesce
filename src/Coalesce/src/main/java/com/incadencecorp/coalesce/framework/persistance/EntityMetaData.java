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

/**
 * Stores meta data about an Coalesce Entity.
 * 
 * @see ICoalescePersistor#getCoalesceEntityIdAndTypeForKey(String)
 */
public class EntityMetaData {

    private String _entityId;
    private String _entityType;
    private String _entityKey;

    /**
     * 
     * @param id
     * @param type
     * @param key
     */
    public EntityMetaData(final String id, final String type, final String key)
    {
        _entityId = id;
        _entityType = type;
        _entityKey = key;
    }

    /**
     * @return a comma separated value (CSV) list of unique identifiers that
     *         represents a Coalesce entity.
     */
    public final String getEntityId()
    {
        return _entityId;
    }

    /**
     * @return comma separated value (CSV) list of type identifiers that map 1
     *         to 1 with _entityId.
     */
    public final String getEntityType()
    {
        return _entityType;
    }

    /**
     * @return a GUID that uniquely identifies a Coalesce entity.
     */
    public final String getEntityKey()
    {
        return _entityKey;
    }

}
