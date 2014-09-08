package Coalesce.Framework.Persistance;

import java.util.List;

import org.joda.time.DateTime;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;
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

    private ICoalesceCacher _Cacher = null;

    /*--------------------------------------------------------------------------
    	Interface Implementation
    --------------------------------------------------------------------------*/

    @Override
    public boolean Initialize(ICoalesceCacher Cacher)
    {

        this._Cacher = Cacher;

        return true;

    }

    @Override
    public boolean SetEntity(XsdEntity entity, boolean AllowRemoval)
    {

        try
        {
            boolean isSuccessful = false;

            // Cacher Disabled or not an Entity
            if (this._Cacher == null || entity.getType().toLowerCase().equals("entity"))
            {
                // Yes; Persist and Flatten Now
                isSuccessful = this.FlattenObject(entity, AllowRemoval);
            }
            else
            {
                // Delayed Persisting and Space Available?
                if (this._Cacher.getSupportsDelayedSave() && this._Cacher.getState() == ECoalesceCacheStates.SPACE_AVAILABLE)
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
                if (isSuccessful) isSuccessful = this._Cacher.StoreEntity(entity);

            }

            return isSuccessful;
        }
        catch (Exception ex)
        {
            CallResult.log(CallResults.FAILED_ERROR, ex, this);
            return false;
        }

    }

    @Override
    public XsdEntity GetEntity(String Key)
    {

        XsdEntity entity = null;

        // Get Entity From Cache
        entity = this.GetEntityFromCache(Key);

        // Entity Cached?
        if (entity == null)
        {

            // No; Load Entity's XML
            String EntityXml = this.GetEntityXml(Key);

            // Found?
            if (EntityXml != null)
            {

                // Yes; Initialize Entity
                entity = new XsdEntity();
                entity.Initialize(EntityXml);

                // Add Entity to Cache
                this.AddEntityToCache(entity);

            }

        }

        return entity;

    }

    @Override
    public XsdEntity GetEntity(String EntityId, String EntityIdType)
    {

        XsdEntity entity = null;

        // Load Entity's XML
        String EntityXml = this.GetEntityXml(EntityId, EntityIdType);

        // Found?
        if (EntityXml != null)
        {

            // Yes; Initialize Entity
            entity = new XsdEntity();
            entity.Initialize(EntityXml);

            // Add Entity to Cache
            this.AddEntityToCache(entity);

        }

        return entity;

    }

    @Override
    public XsdEntity GetEntity(String Name, String EntityId, String EntityIdType)
    {

        XsdEntity entity = null;

        // Load Entity's XML
        String EntityXml = this.GetEntityXml(Name, EntityId, EntityIdType);

        // Found?
        if (EntityXml != null)
        {

            // Yes; Initialize Entity
            entity = new XsdEntity();
            entity.Initialize(EntityXml);

            // Add Entity to Cache
            this.AddEntityToCache(entity);

        }

        return entity;

    }

    /*--------------------------------------------------------------------------
    Abstract Public Functions
    --------------------------------------------------------------------------*/

    @Override
    public abstract String GetEntityXml(String Key);

    @Override
    public abstract String GetEntityXml(String EntityId, String EntityIdType);

    @Override
    public abstract String GetEntityXml(String Name, String EntityId, String EntityIdType);

    @Override
    public abstract Object GetFieldValue(String fieldKey);

    @Override
    public abstract ElementMetaData GetXPath(String Key, String ObjectType);

    @Override
    public abstract DateTime GetCoalesceDataObjectLastModified(String Key, String ObjectType);

    @Override
    public abstract List<String> GetCoalesceEntityKeysForEntityId(String EntityId,
                                                                  String EntityIdType,
                                                                  String EntityName,
                                                                  String EntitySource);

    @Override
    public abstract EntityMetaData GetCoalesceEntityIdAndTypeForKey(String Key);

    @Override
    public abstract byte[] GetBinaryArray(String BinaryFieldKey);

    @Override
    public abstract boolean PersistEntityTemplate(CoalesceEntityTemplate EntityTemplate);

    @Override
    public abstract String GetEntityTemplateXml(String Key);

    @Override
    public abstract String GetEntityTemplateXml(String Name, String Source, String Version);

    @Override
    public abstract String GetEntityTemplateKey(String Name, String Source, String Version);

    @Override
    public abstract String GetEntityTemplateMetadata();

    protected abstract boolean FlattenObject(XsdEntity entity, boolean AllowRemoval) throws Exception;

    protected abstract boolean FlattenCore(XsdEntity entity, boolean AllowRemoval) throws Exception;

    /*--------------------------------------------------------------------------
    	Private Functions
    --------------------------------------------------------------------------*/

    private XsdEntity GetEntityFromCache(String Key)
    {

        XsdEntity entity = null;

        // Cacher Initialized?
        if (this._Cacher != null)
        {

            // Yes; Contains Entity?
            if (this._Cacher.ContainsEntity(Key))
            {

                // Yes; Retrieve Entity
                entity = this._Cacher.RetrieveEntity(Key);

            }

        }

        return entity;

    }

    private boolean AddEntityToCache(XsdEntity entity)
    {

        boolean IsModified = false;

        // Cacher Initialized?
        if (this._Cacher != null)
        {

            // Yes; Retrieve Entity
            IsModified = this._Cacher.StoreEntity(entity);

        }

        return IsModified;

    }

}
