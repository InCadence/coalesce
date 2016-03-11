package com.incadencecorp.coalesce.framework.persistance;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
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

/**
 * Defines common functionality between persistors.
 * 
 * @author Derek C.
 */
public abstract class CoalescePersistorBase implements ICoalescePersistor {

    /*--------------------------------------------------------------------------
    	Private Member Variables
    --------------------------------------------------------------------------*/

    private ICoalesceCacher _cacher = null;
    private ServerConn _serCon;

    /*--------------------------------------------------------------------------
    Constructor / Initializers
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    public CoalescePersistorBase()
    {
        _serCon = new ServerConn();
    }

    /**
     * Sets the server connection.
     * 
     * @param svConn connection object.
     */
    public void initialize(ServerConn svConn)
    {
        _serCon = svConn;
    }

    /**
     * Sets the cacher and server connection.
     * 
     * @param cacher class cacher.
     * @param svConn connection object.
     * @return <code>true</code> is successful.
     */
    public boolean initialize(ICoalesceCacher cacher, ServerConn svConn)
    {
        _serCon = svConn;

        return initialize(cacher);
    }

    protected ServerConn getConnectionSettings()
    {
        return _serCon;
    }

    protected abstract CoalesceDataConnectorBase getDataConnector() throws CoalescePersistorException;

    /*--------------------------------------------------------------------------
    Interface Implementation
    --------------------------------------------------------------------------*/

    @Override
    public boolean initialize(ICoalesceCacher cacher)
    {

        this._cacher = cacher;

        return true;

    }

    @Override
    public boolean saveEntity(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        boolean isSuccessful = false;

        // Cacher Disabled or not an Entity
        if (this._cacher == null)
        {
            // Yes; Persist and Flatten Now
            isSuccessful = this.flattenObject(allowRemoval, entities);
        }
        else
        {
            // Delayed Persisting and Space Available?
            if (this._cacher.getSupportsDelayedSave() && this._cacher.getState() == ECoalesceCacheStates.SPACE_AVAILABLE)
            {
                // Yes; Only Flatten Core Elements
                isSuccessful = this.flattenCore(allowRemoval, entities);
            }
            else
            {
                // No; Persist and Flatten Entity Now
                isSuccessful = this.flattenObject(allowRemoval, entities);
            }

            // If Successful Add to Cache
            if (isSuccessful)
            {
                for (CoalesceEntity entity : entities)
                {
                    isSuccessful = this._cacher.storeEntity(entity);
                }
            }

        }

        return isSuccessful;
    }

    @Override
    public CoalesceEntity[] getEntity(String... keys) throws CoalescePersistorException
    {

        List<CoalesceEntity> results = new ArrayList<CoalesceEntity>();
        List<String> keysToQuery = new ArrayList<String>();

        for (String key : keys)
        {

            // Get From Cache
            CoalesceEntity entity = this.getEntityFromCache(key);

            // Cached?
            if (entity != null)
            {
                // Yes; Add to Results
                results.add(entity);
            }
            else
            {
                // No; Add to Query List
                keysToQuery.add(key);
            }

        }

        for (String xml : getEntityXml(keysToQuery.toArray(new String[keysToQuery.size()])))
        {

            CoalesceEntity entity = new CoalesceEntity();

            // Found?
            if (!StringHelper.isNullOrEmpty(xml) && entity.initialize(xml))
            {

                // Yes; Add to Results
                results.add(entity);

                // Add Entity to Cache
                this.addEntityToCache(entity);

            }

        }

        return results.toArray(new CoalesceEntity[results.size()]);

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

    @Override
    public boolean persistEntityTemplate(CoalesceEntityTemplate entityTemplate) throws CoalescePersistorException {
        
        try (CoalesceDataConnectorBase conn = getDataConnector())
        {
            // Always persist template
            return persistEntityTemplate(entityTemplate, conn);
        }
        
    }

    
    /*--------------------------------------------------------------------------
    Abstract Public Functions
    --------------------------------------------------------------------------*/

    @Override
    public abstract String[] getEntityXml(String... keys) throws CoalescePersistorException;

    @Override
    public abstract String getEntityXml(String entityId, String entityIdType) throws CoalescePersistorException;

    @Override
    public abstract String getEntityXml(String name, String entityId, String entityIdType) throws CoalescePersistorException;

    @Override
    public abstract Object getFieldValue(String fieldKey) throws CoalescePersistorException;

    @Override
    public abstract ElementMetaData getXPath(String key, String objectType) throws CoalescePersistorException;

    @Override
    public abstract DateTime getCoalesceObjectLastModified(String key, String objectType) throws CoalescePersistorException;

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
    public abstract String getEntityTemplateXml(String key) throws CoalescePersistorException;

    @Override
    public abstract String getEntityTemplateXml(String name, String source, String version)
            throws CoalescePersistorException;

    @Override
    public abstract String getEntityTemplateKey(String name, String source, String version)
            throws CoalescePersistorException;

    @Override
    public abstract String getEntityTemplateMetadata() throws CoalescePersistorException;

    protected abstract boolean persistEntityTemplate(CoalesceEntityTemplate entityTemplate, CoalesceDataConnectorBase conn) throws CoalescePersistorException;

    /*--------------------------------------------------------------------------
    Abstract Protected Functions
    --------------------------------------------------------------------------*/

    protected abstract boolean flattenObject(boolean allowRemoval, CoalesceEntity... entities)
            throws CoalescePersistorException;

    protected abstract boolean flattenCore(boolean allowRemoval, CoalesceEntity... entities)
            throws CoalescePersistorException;

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

        boolean isModified = false;

        // Cacher Initialized?
        if (this._cacher != null)
        {

            // Yes; Retrieve Entity
            isModified = this._cacher.storeEntity(entity);

        }

        return isModified;

    }

}
