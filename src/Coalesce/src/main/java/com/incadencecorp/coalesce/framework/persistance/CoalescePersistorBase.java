package com.incadencecorp.coalesce.framework.persistance;

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

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
    public void setConnectionSettings(ServerConn svConn)
    {
        _serCon = svConn;
    }

    protected ServerConn getConnectionSettings()
    {
        return _serCon;
    }

    protected abstract CoalesceDataConnectorBase getDataConnector() throws CoalescePersistorException;

    /*--------------------------------------------------------------------------
    Interface Implementation
    --------------------------------------------------------------------------*/

    /**
     * Sets the cacher.
     *
     * @param cacher Pass null if caching is not wanted
     */
    public void setCacher(ICoalesceCacher cacher)
    {
        _cacher = cacher;
    }

    protected ICoalesceCacher getCacher()
    {
        return _cacher;
    }

    @Override
    public boolean saveEntity(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        boolean isSuccessful = false;

        // Cacher Disabled or not an Entity
        if (_cacher == null)
        {
            // Yes; Persist and Flatten Now
            isSuccessful = flattenObject(allowRemoval, entities);
        }
        else
        {
            // Delayed Persisting and Space Available?
            if (_cacher.getSupportsDelayedSave() && _cacher.getState() == ECoalesceCacheStates.SPACE_AVAILABLE)
            {
                // Yes; Only Flatten Core Elements
                isSuccessful = flattenCore(allowRemoval, entities);
            }
            else
            {
                // No; Persist and Flatten Entity Now
                isSuccessful = flattenObject(allowRemoval, entities);
            }

            // If Successful Add to Cache
            if (isSuccessful)
            {
                for (CoalesceEntity entity : entities)
                {
                    isSuccessful = _cacher.storeEntity(entity);
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
            CoalesceEntity entity = getEntityFromCache(key);

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

        if (keysToQuery.size() > 0)
        {
            for (String xml : getEntityXml(keysToQuery.toArray(new String[keysToQuery.size()])))
            {

                CoalesceEntity entity = new CoalesceEntity();

                // Found?
                if (!StringHelper.isNullOrEmpty(xml) && entity.initialize(xml))
                {

                    // Yes; Add to Results
                    results.add(entity);

                    // Add Entity to Cache
                    addEntityToCache(entity);

                }

            }
        }

        return results.toArray(new CoalesceEntity[results.size()]);

    }

    @Override
    public void saveTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = getDataConnector())
        {
            saveTemplate(conn, templates);
        }
    }

    /*
     * TODO This method really should be in the search interface.
     */
    @Override
    public void registerTemplate(final CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        saveTemplate(templates);
    }

    /**
     * @return EnumSet of EPersistorCapabilities
     */
    @Override
    public EnumSet<EPersistorCapabilities> getCapabilities()
    {
        EnumSet<EPersistorCapabilities> enumSet = EnumSet.of(EPersistorCapabilities.CREATE, EPersistorCapabilities.READ);
        return enumSet;
    }

    /*--------------------------------------------------------------------------
    Abstract Public Functions
    --------------------------------------------------------------------------*/

    @Override
    public abstract String[] getEntityXml(String... keys) throws CoalescePersistorException;

    @Override
    public abstract CoalesceEntityTemplate getEntityTemplate(String key) throws CoalescePersistorException;

    @Override
    public abstract CoalesceEntityTemplate getEntityTemplate(String name, String source, String version)
            throws CoalescePersistorException;

    @Override
    public abstract String getEntityTemplateKey(String name, String source, String version)
            throws CoalescePersistorException;

    @Override
    public abstract List<ObjectMetaData> getEntityTemplateMetadata() throws CoalescePersistorException;

    protected abstract void saveTemplate(CoalesceDataConnectorBase conn, CoalesceEntityTemplate... templates)
            throws CoalescePersistorException;

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
        if (_cacher != null)
        {

            // Yes; Contains Entity?
            if (_cacher.containsEntity(key))
            {

                // Yes; Retrieve Entity
                entity = _cacher.retrieveEntity(key);

            }

        }

        return entity;

    }

    private boolean addEntityToCache(CoalesceEntity entity)
    {

        boolean isModified = false;

        // Cacher Initialized?
        if (_cacher != null)
        {

            // Yes; Retrieve Entity
            isModified = _cacher.storeEntity(entity);

        }

        return isModified;

    }

}
