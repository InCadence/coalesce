package Coalesce.Framework.Persistance;

import java.util.List;

import org.joda.time.DateTime;

import Coalesce.Common.Exceptions.CoalescePersistorException;
import Coalesce.Framework.DataModel.CoalesceEntityTemplate;
import Coalesce.Framework.DataModel.XsdEntity;

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

public abstract class CoalescePersisterBase implements ICoalescePersistor {

    /*--------------------------------------------------------------------------
    	Private Member Variables
    --------------------------------------------------------------------------*/

    private ICoalesceCacher _cacher = null;

    /*--------------------------------------------------------------------------
    	Interface Implementation
    --------------------------------------------------------------------------*/

    @Override
    public boolean initialize(ICoalesceCacher Cacher) throws CoalescePersistorException
    {

        this._cacher = Cacher;

        return true;

    }

    @Override
    public boolean setEntity(XsdEntity entity, boolean AllowRemoval) throws CoalescePersistorException
    {
        boolean isSuccessful = false;

        // Cacher Disabled or not an Entity
        if (this._cacher == null || entity.getType().toLowerCase().equals("entity"))
        {
            // Yes; Persist and Flatten Now
            isSuccessful = this.FlattenObject(entity, AllowRemoval);
        }
        else
        {
            // Delayed Persisting and Space Available?
            if (this._cacher.getSupportsDelayedSave() && this._cacher.getState() == ECoalesceCacheStates.SPACE_AVAILABLE)
            {
                // Yes; Only Flatten Core Elements
                isSuccessful = this.FlattenCore(entity, AllowRemoval);
            }
            else
            {
                // No; Persist and Flatten Entity Now
                isSuccessful = this.FlattenObject(entity, AllowRemoval);
            }

            // If Successful Add to Cache
            if (isSuccessful) isSuccessful = this._cacher.storeEntity(entity);

        }

        return isSuccessful;
    }

    @Override
    public XsdEntity getEntity(String Key) throws CoalescePersistorException
    {

        XsdEntity entity = null;

        // Get Entity From Cache
        entity = this.getEntityFromCache(Key);

        // Entity Cached?
        if (entity == null)
        {

            // No; Load Entity's XML
            String EntityXml = this.getEntityXml(Key);

            // Found?
            if (EntityXml != null)
            {

                // Yes; Initialize Entity
                entity = new XsdEntity();
                entity.initialize(EntityXml);

                // Add Entity to Cache
                this.addEntityToCache(entity);

            }

        }

        return entity;

    }

    @Override
    public XsdEntity getEntity(String EntityId, String EntityIdType) throws CoalescePersistorException
    {

        XsdEntity entity = null;

        // Load Entity's XML
        String EntityXml = this.getEntityXml(EntityId, EntityIdType);

        // Found?
        if (EntityXml != null)
        {

            // Yes; Initialize Entity
            entity = new XsdEntity();
            entity.initialize(EntityXml);

            // Add Entity to Cache
            this.addEntityToCache(entity);

        }

        return entity;

    }

    @Override
    public XsdEntity getEntity(String Name, String EntityId, String EntityIdType) throws CoalescePersistorException
    {

        XsdEntity entity = null;

        // Load Entity's XML
        String EntityXml = this.getEntityXml(Name, EntityId, EntityIdType);

        // Found?
        if (EntityXml != null)
        {

            // Yes; Initialize Entity
            entity = new XsdEntity();
            entity.initialize(EntityXml);

            // Add Entity to Cache
            this.addEntityToCache(entity);

        }

        return entity;

    }

    /*--------------------------------------------------------------------------
    Abstract Public Functions
    --------------------------------------------------------------------------*/

    @Override
    public abstract String getEntityXml(String Key) throws CoalescePersistorException;

    @Override
    public abstract String getEntityXml(String EntityId, String EntityIdType) throws CoalescePersistorException;

    @Override
    public abstract String getEntityXml(String Name, String EntityId, String EntityIdType) throws CoalescePersistorException;

    @Override
    public abstract Object getFieldValue(String fieldKey) throws CoalescePersistorException;

    @Override
    public abstract ElementMetaData getXPath(String Key, String ObjectType) throws CoalescePersistorException;

    @Override
    public abstract DateTime getCoalesceDataObjectLastModified(String Key, String ObjectType)
            throws CoalescePersistorException;

    @Override
    public abstract List<String> getCoalesceEntityKeysForEntityId(String EntityId,
                                                                  String EntityIdType,
                                                                  String EntityName,
                                                                  String EntitySource) throws CoalescePersistorException;

    @Override
    public abstract EntityMetaData getCoalesceEntityIdAndTypeForKey(String Key) throws CoalescePersistorException;

    @Override
    public abstract byte[] getBinaryArray(String BinaryFieldKey) throws CoalescePersistorException;

    @Override
    public abstract boolean persistEntityTemplate(CoalesceEntityTemplate EntityTemplate) throws CoalescePersistorException;

    @Override
    public abstract String getEntityTemplateXml(String Key) throws CoalescePersistorException;

    @Override
    public abstract String getEntityTemplateXml(String Name, String Source, String Version)
            throws CoalescePersistorException;

    @Override
    public abstract String getEntityTemplateKey(String Name, String Source, String Version)
            throws CoalescePersistorException;

    @Override
    public abstract String getEntityTemplateMetadata() throws CoalescePersistorException;

    protected abstract boolean FlattenObject(XsdEntity entity, boolean AllowRemoval) throws CoalescePersistorException;

    protected abstract boolean FlattenCore(XsdEntity entity, boolean AllowRemoval) throws CoalescePersistorException;

    /*--------------------------------------------------------------------------
    	Private Functions
    --------------------------------------------------------------------------*/

    private XsdEntity getEntityFromCache(String Key)
    {

        XsdEntity entity = null;

        // Cacher Initialized?
        if (this._cacher != null)
        {

            // Yes; Contains Entity?
            if (this._cacher.containsEntity(Key))
            {

                // Yes; Retrieve Entity
                entity = this._cacher.retrieveEntity(Key);

            }

        }

        return entity;

    }

    private boolean addEntityToCache(XsdEntity entity)
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
