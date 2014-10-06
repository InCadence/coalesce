package com.incadencecorp.coalesce.framework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.xml.sax.SAXException;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntitySyncShell;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor.ElementMetaData;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor.EntityMetaData;

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

public class CoalesceFramework {

    /*--------------------------------------------------------------------------
    	Private Member Variables
    --------------------------------------------------------------------------*/

    private ICoalescePersistor _Persister;
    private boolean _IsInitialized = false;

    /*--------------------------------------------------------------------------
    	Public Functions
    --------------------------------------------------------------------------*/

    public boolean initialize(ICoalescePersistor persister)
    {

        this._Persister = persister;
        this._IsInitialized = true;

        return true;
    }

    public boolean isInitialized()
    {
        return this._IsInitialized;
    }

    /*--------------------------------------------------------------------------
    	Get Entity
    --------------------------------------------------------------------------*/

    public CoalesceEntity getCoalesceEntity(String Key) throws CoalescePersistorException
    {
        return this._Persister.getEntity(Key);
    }

    public CoalesceEntity getEntity(String EntityId, String EntityIdType) throws CoalescePersistorException
    {
        return this._Persister.getEntity(EntityId, EntityIdType);
    }

    public CoalesceEntity getEntity(String Name, String EntityId, String EntityIdType) throws CoalescePersistorException
    {
        return this._Persister.getEntity(Name, EntityId, EntityIdType);
    }

    public String getEntityXml(String Key) throws CoalescePersistorException
    {
        return this._Persister.getEntityXml(Key);
    }

    public String getEntityXml(String EntityId, String EntityIdType) throws CoalescePersistorException
    {
        return this._Persister.getEntityXml(EntityId, EntityIdType);
    }

    public String getEntityXml(String Name, String EntityId, String EntityIdType) throws CoalescePersistorException
    {
        return this._Persister.getEntityXml(Name, EntityId, EntityIdType);
    }

    /*--------------------------------------------------------------------------
    	EntityID Functions
    --------------------------------------------------------------------------*/

    public String getCoalesceEntityKeyForEntityId(String EntityId, String EntityIdType, String EntityName)
            throws CoalescePersistorException
    {
        return this.getCoalesceEntityKeyForEntityId(EntityId, EntityIdType, EntityName, null);
    }

    public String getCoalesceEntityKeyForEntityId(String EntityId,
                                                  String EntityIdType,
                                                  String EntityName,
                                                  String EntitySource) throws CoalescePersistorException
    {

        String EntityKey = null;

        List<String> list = this.getCoalesceEntityKeysForEntityId(EntityId, EntityIdType, EntityName, EntitySource);

        if (!list.isEmpty())
        {

            EntityKey = list.get(0);

        }

        return EntityKey;
    }

    public List<String> getCoalesceEntityKeysForEntityId(String EntityId, String EntityIdType, String EntityName)
            throws CoalescePersistorException
    {
        return this.getCoalesceEntityKeysForEntityId(EntityId, EntityIdType, EntityName, null);
    }

    public List<String> getCoalesceEntityKeysForEntityId(String EntityId,
                                                         String EntityIdType,
                                                         String EntityName,
                                                         String EntitySource) throws CoalescePersistorException
    {

        List<String> list = new ArrayList<String>();

        String[] EntityIdList = EntityId.split(",");
        String[] EntityIdTypeList = EntityIdType.split(",");

        if (EntityIdList.length == EntityIdTypeList.length)
        {

            for (int i = 0; i < EntityIdTypeList.length; i++)
            {

                list.addAll(this._Persister.getCoalesceEntityKeysForEntityId(EntityIdList[i],
                                                                             EntityIdTypeList[i],
                                                                             EntityName,
                                                                             EntitySource));

            }

        }

        return list;

    }

    public EntityMetaData getCoalesceEntityIdAndTypeForKey(String Key) throws CoalescePersistorException
    {
        return this._Persister.getCoalesceEntityIdAndTypeForKey(Key);
    }

    /*--------------------------------------------------------------------------
    	Other Entity Functions
    --------------------------------------------------------------------------*/

    public DateTime getCoalesceEntityLastModified(String key, String objectType) throws CoalescePersistorException
    {
        return this._Persister.getCoalesceDataObjectLastModified(key, objectType);
    }

    public boolean saveCoalesceEntity(CoalesceEntity entity) throws CoalescePersistorException
    {
        return this.saveCoalesceEntity(entity, false);
    }

    public boolean saveCoalesceEntity(CoalesceEntity entity, boolean AllowRemoval) throws CoalescePersistorException
    {
        return this._Persister.saveEntity(entity, AllowRemoval);
    }

    public String getCoalesceFieldValue(String FieldKey) throws CoalescePersistorException
    {
        return (String) this._Persister.getFieldValue(FieldKey);
    }

    public CoalesceRecord getCoalesceRecord(String Key) throws CoalescePersistorException
    {
        CoalesceRecord record = null;

        ElementMetaData metaData = this._Persister.getXPath(Key, "record");
        if (metaData != null)
        {
            CoalesceEntity entity = this._Persister.getEntity(metaData.entityKey);
            if (entity != null)
            {
                record = (CoalesceRecord) entity.getDataObjectForNamePath(metaData.elementXPath);
            }
        }

        return record;
    }

    public CoalesceStringField getCoalesceFieldByFieldKey(String Key) throws CoalescePersistorException
    {
        CoalesceStringField field = null;

        ElementMetaData metaData = this._Persister.getXPath(Key, "field");

        if (metaData != null)
        {
            CoalesceEntity entity = this._Persister.getEntity(metaData.entityKey);

            if (entity != null)
            {
                field = (CoalesceStringField) entity.getCoalesceDataObjectForKey(Key);
            }
        }

        return field;
    }

    /*--------------------------------------------------------------------------
    	Template Functions
    --------------------------------------------------------------------------*/

    public boolean saveCoalesceEntityTemplate(CoalesceEntityTemplate template) throws CoalescePersistorException
    {
        return this._Persister.persistEntityTemplate(template);
    }

    public CoalesceEntityTemplate getCoalesceEntityTemplate(String Name, String Source, String Version) throws SAXException,
            IOException, CoalescePersistorException
    {

        CoalesceEntityTemplate template = new CoalesceEntityTemplate();

        // Initialize Template
        template.initialize(this.getCoalesceEntityTemplateXml(Name, Source, Version));

        return template;

    }

    public String getCoalesceEntityTemplateXml(String Key) throws CoalescePersistorException
    {
        return this._Persister.getEntityTemplateXml(Key);
    }

    public String getCoalesceEntityTemplateXml(String Name, String Source, String Version) throws CoalescePersistorException
    {
        return this._Persister.getEntityTemplateXml(Name, Source, Version);
    }

    public String getCoalesceEntityTemplateKey(String Name, String Source, String Version) throws CoalescePersistorException
    {
        return this._Persister.getEntityTemplateKey(Name, Source, Version);
    }

    public String getCoalesceEntityTemplateMetadata() throws CoalescePersistorException
    {
        return this._Persister.getEntityTemplateMetadata();
    }

    public CoalesceEntity createEntityFromTemplate(String Name, String Source, String Version) throws CoalescePersistorException
    {

        String Xml = this.getCoalesceEntityTemplateXml(Name, Source, Version);

        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize(Xml);

        return entity;

    }

    /*--------------------------------------------------------------------------
    	Sync Shell Functions
    --------------------------------------------------------------------------*/

    public CoalesceEntitySyncShell getCoalesceEntitySyncShell(String Key) throws CoalescePersistorException, SAXException,
            IOException
    {
        return CoalesceEntitySyncShell.create(this.getCoalesceEntity(Key));
    }

}
