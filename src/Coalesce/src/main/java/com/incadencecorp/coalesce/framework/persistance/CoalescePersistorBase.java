package com.incadencecorp.coalesce.framework.persistance;

import java.util.List;

import org.joda.time.DateTime;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;

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

public abstract class CoalescePersistorBase implements ICoalescePersistor {

    /*--------------------------------------------------------------------------
    	Private Member Variables
    --------------------------------------------------------------------------*/

    private ICoalesceCacher _cacher = null;

    /*--------------------------------------------------------------------------
    	Interface Implementation
    --------------------------------------------------------------------------*/

    @Override
    public boolean initialize(ICoalesceCacher cacher) throws CoalescePersistorException
    {

        this._cacher = cacher;

        return true;

    }

    @Override
    public boolean saveEntity(CoalesceEntity entity, boolean allowRemoval) throws CoalescePersistorException
    {
        boolean isSuccessful = false;

        // Cacher Disabled or not an Entity
        if (this._cacher == null || entity.getType().toLowerCase().equals("entity"))
        {
            // Yes; Persist and Flatten Now
            isSuccessful = this.flattenObject(entity, allowRemoval);
        }
        else
        {
            // Delayed Persisting and Space Available?
            if (this._cacher.getSupportsDelayedSave() && this._cacher.getState() == ECoalesceCacheStates.SPACE_AVAILABLE)
            {
                // Yes; Only Flatten Core Elements
                isSuccessful = this.flattenCore(entity, allowRemoval);
            }
            else
            {
                // No; Persist and Flatten Entity Now
                isSuccessful = this.flattenObject(entity, allowRemoval);
            }

            // If Successful Add to Cache
            if (isSuccessful) isSuccessful = this._cacher.storeEntity(entity);

        }

        return isSuccessful;
    }

    @Override
    public CoalesceEntity getEntity(String key) throws CoalescePersistorException
    {

        CoalesceEntity entity = null;

        // Get Entity From Cache
        entity = this.getEntityFromCache(key);

        // Entity Cached?
        if (entity == null)
        {

            // No; Load Entity's XML
            String EntityXml = this.getEntityXml(key);

            // Found?
            if (EntityXml != null)
            {

                // Yes; Initialize Entity
                entity = new CoalesceEntity();
                entity.initialize(EntityXml);

                // Add Entity to Cache
                this.addEntityToCache(entity);

            }

        }

        return entity;

    }

    @Override
    public CoalesceEntity getEntity(String entityId, String entityIdType) throws CoalescePersistorException
    {

        CoalesceEntity entity = null;

        // Load Entity's XML
        String entityXml = this.getEntityXml(entityId, entityIdType);

        // Found?
        if (entityXml != null)
        {

            // Yes; Initialize Entity
            entity = new CoalesceEntity();
            entity.initialize(entityXml);

            // Add Entity to Cache
            this.addEntityToCache(entity);

        }

        return entity;

    }

    @Override
    public CoalesceEntity getEntity(String name, String entityId, String entityIdType) throws CoalescePersistorException
    {

        CoalesceEntity entity = null;

        // Load Entity's XML
        String entityXml = this.getEntityXml(name, entityId, entityIdType);

        // Found?
        if (entityXml != null)
        {

            // Yes; Initialize Entity
            entity = new CoalesceEntity();
            entity.initialize(entityXml);

            // Add Entity to Cache
            this.addEntityToCache(entity);

        }

        return entity;

    }

    /*--------------------------------------------------------------------------
    Abstract Public Functions
    --------------------------------------------------------------------------*/

    @Override
    public abstract String getEntityXml(String key) throws CoalescePersistorException;

    @Override
    public abstract String getEntityXml(String entityId, String entityIdType) throws CoalescePersistorException;

    @Override
    public abstract String getEntityXml(String name, String entityId, String entityIdType) throws CoalescePersistorException;

    @Override
    public abstract Object getFieldValue(String fieldKey) throws CoalescePersistorException;

    @Override
    public abstract ElementMetaData getXPath(String key, String objectType) throws CoalescePersistorException;

    @Override
    public abstract DateTime getCoalesceDataObjectLastModified(String key, String objectType)
            throws CoalescePersistorException;

    @Override
    public abstract List<String> getCoalesceEntityKeysForEntityId(String entityId,
                                                                  String entityIdType,
                                                                  String entityName,
                                                                  String entitySource) throws CoalescePersistorException;

    @Override
    public abstract EntityMetaData getCoalesceEntityIdAndTypeForKey(String key) throws CoalescePersistorException;

    @Override
    public abstract byte[] getBinaryArray(String binaryFieldKey) throws CoalescePersistorException;

    @Override
    public abstract boolean persistEntityTemplate(CoalesceEntityTemplate entityTemplate) throws CoalescePersistorException;

    @Override
    public abstract String getEntityTemplateXml(String key) throws CoalescePersistorException;

    @Override
    public abstract String getEntityTemplateXml(String name, String source, String version)
            throws CoalescePersistorException;

    @Override
    public abstract String getEntityTemplateKey(String name, String source, String version)
            throws CoalescePersistorException;

    @Override
    public abstract String getEntityTemplateMetadata() throws CoalescePersistorException;

    /*--------------------------------------------------------------------------
    Abstract Protected Functions
    --------------------------------------------------------------------------*/

    protected abstract boolean flattenObject(CoalesceEntity entity, boolean allowRemoval) throws CoalescePersistorException;

    protected abstract boolean flattenCore(CoalesceEntity entity, boolean allowRemoval) throws CoalescePersistorException;

    /*--------------------------------------------------------------------------
    	Private Functions
    --------------------------------------------------------------------------*/

    private CoalesceEntity getEntityFromCache(String key)
    {

        CoalesceEntity entity = null;

        // Cacher Initialized?
        if (this._cacher != null)
        {

            // Yes; Contains Entity?
            if (this._cacher.containsEntity(key))
            {

                // Yes; Retrieve Entity
                entity = this._cacher.retrieveEntity(key);

            }

        }

        return entity;

    }

    private boolean addEntityToCache(CoalesceEntity entity)
    {

        boolean IsModified = false;

        // Cacher Initialized?
        if (this._cacher != null)
        {

            // Yes; Retrieve Entity
            IsModified = this._cacher.storeEntity(entity);

        }

        return IsModified;

    }

}
