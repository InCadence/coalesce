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

/**
 * Application using Coalesce should access the persistor (database) through CoalesceFramework.
 */
public class CoalesceFramework {

    /*--------------------------------------------------------------------------
    	Private Member Variables
    --------------------------------------------------------------------------*/

    private ICoalescePersistor _persistor;
    private boolean _isInitialized = false;

    /*--------------------------------------------------------------------------
    	Public Functions
    --------------------------------------------------------------------------*/

    public boolean initialize(ICoalescePersistor persistor)
    {

        this._persistor = persistor;
        this._isInitialized = true;

        return true;
    }

    public boolean isInitialized()
    {
        return this._isInitialized;
    }

    /*--------------------------------------------------------------------------
    	Get Entity
    --------------------------------------------------------------------------*/

    public CoalesceEntity getCoalesceEntity(String key) throws CoalescePersistorException
    {
        return this._persistor.getEntity(key);
    }

    public CoalesceEntity getEntity(String entityId, String entityIdType) throws CoalescePersistorException
    {
        return this._persistor.getEntity(entityId, entityIdType);
    }

    public CoalesceEntity getEntity(String name, String entityId, String entityIdType) throws CoalescePersistorException
    {
        return this._persistor.getEntity(name, entityId, entityIdType);
    }

    public String getEntityXml(String key) throws CoalescePersistorException
    {
        return this._persistor.getEntityXml(key);
    }

    public String getEntityXml(String entityId, String entityIdType) throws CoalescePersistorException
    {
        return this._persistor.getEntityXml(entityId, entityIdType);
    }

    public String getEntityXml(String name, String entityId, String entityIdType) throws CoalescePersistorException
    {
        return this._persistor.getEntityXml(name, entityId, entityIdType);
    }

    /*--------------------------------------------------------------------------
    	EntityID Functions
    --------------------------------------------------------------------------*/

    public String getCoalesceEntityKeyForEntityId(String entityId, String entityIdType, String entityName)
            throws CoalescePersistorException
    {
        return this.getCoalesceEntityKeyForEntityId(entityId, entityIdType, entityName, null);
    }

    public String getCoalesceEntityKeyForEntityId(String entityId,
                                                  String entityIdType,
                                                  String entityName,
                                                  String entitySource) throws CoalescePersistorException
    {

        String entityKey = null;

        List<String> list = this.getCoalesceEntityKeysForEntityId(entityId, entityIdType, entityName, entitySource);

        if (!list.isEmpty())
        {

            entityKey = list.get(0);

        }

        return entityKey;
    }

    public List<String> getCoalesceEntityKeysForEntityId(String entityId, String entityIdType, String entityName)
            throws CoalescePersistorException
    {
        return this.getCoalesceEntityKeysForEntityId(entityId, entityIdType, entityName, null);
    }

    public List<String> getCoalesceEntityKeysForEntityId(String entityId,
                                                         String entityIdType,
                                                         String entityName,
                                                         String entitySource) throws CoalescePersistorException
    {

        List<String> list = new ArrayList<String>();

        String[] entityIdList = entityId.split(",");
        String[] entityIdTypeList = entityIdType.split(",");

        if (entityIdList.length == entityIdTypeList.length)
        {

            for (int i = 0; i < entityIdTypeList.length; i++)
            {

                list.addAll(this._persistor.getCoalesceEntityKeysForEntityId(entityIdList[i],
                                                                             entityIdTypeList[i],
                                                                             entityName,
                                                                             entitySource));

            }

        }

        return list;

    }

    public EntityMetaData getCoalesceEntityIdAndTypeForKey(String key) throws CoalescePersistorException
    {
        return this._persistor.getCoalesceEntityIdAndTypeForKey(key);
    }

    /*--------------------------------------------------------------------------
    	Other Entity Functions
    --------------------------------------------------------------------------*/

    public DateTime getCoalesceEntityLastModified(String key, String objectType) throws CoalescePersistorException
    {
        return this._persistor.getCoalesceDataObjectLastModified(key, objectType);
    }

    public boolean saveCoalesceEntity(CoalesceEntity entity) throws CoalescePersistorException
    {
        return this.saveCoalesceEntity(entity, false);
    }

    public boolean saveCoalesceEntity(CoalesceEntity entity, boolean allowRemoval) throws CoalescePersistorException
    {
        return this._persistor.saveEntity(entity, allowRemoval);
    }

    public String getCoalesceFieldValue(String fieldKey) throws CoalescePersistorException
    {
        return (String) this._persistor.getFieldValue(fieldKey);
    }

    public CoalesceRecord getCoalesceRecord(String key) throws CoalescePersistorException
    {
        CoalesceRecord record = null;

        ElementMetaData metaData = this._persistor.getXPath(key, "record");
        if (metaData != null)
        {
            CoalesceEntity entity = this._persistor.getEntity(metaData.getEntityKey());
            if (entity != null)
            {
                record = (CoalesceRecord) entity.getDataObjectForNamePath(metaData.getElementXPath());
            }
        }

        return record;
    }

    public CoalesceStringField getCoalesceFieldByFieldKey(String key) throws CoalescePersistorException
    {
        CoalesceStringField field = null;

        ElementMetaData metaData = this._persistor.getXPath(key, "field");

        if (metaData != null)
        {
            CoalesceEntity entity = this._persistor.getEntity(metaData.getEntityKey());

            if (entity != null)
            {
                field = (CoalesceStringField) entity.getCoalesceDataObjectForKey(key);
            }
        }

        return field;
    }

    /*--------------------------------------------------------------------------
    	Template Functions
    --------------------------------------------------------------------------*/

    public boolean saveCoalesceEntityTemplate(CoalesceEntityTemplate template) throws CoalescePersistorException
    {
        return this._persistor.persistEntityTemplate(template);
    }

    public CoalesceEntityTemplate getCoalesceEntityTemplate(String name, String source, String version) throws SAXException,
            IOException, CoalescePersistorException
    {

        CoalesceEntityTemplate template = new CoalesceEntityTemplate();

        // Initialize Template
        template.initialize(this.getCoalesceEntityTemplateXml(name, source, version));

        return template;

    }

    public String getCoalesceEntityTemplateXml(String key) throws CoalescePersistorException
    {
        return this._persistor.getEntityTemplateXml(key);
    }

    public String getCoalesceEntityTemplateXml(String name, String source, String version) throws CoalescePersistorException
    {
        return this._persistor.getEntityTemplateXml(name, source, version);
    }

    public String getCoalesceEntityTemplateKey(String name, String source, String version) throws CoalescePersistorException
    {
        return this._persistor.getEntityTemplateKey(name, source, version);
    }

    public String getCoalesceEntityTemplateMetadata() throws CoalescePersistorException
    {
        return this._persistor.getEntityTemplateMetadata();
    }

    public CoalesceEntity createEntityFromTemplate(String name, String source, String version)
            throws CoalescePersistorException
    {

        String Xml = this.getCoalesceEntityTemplateXml(name, source, version);

        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize(Xml);

        return entity;

    }

    /*--------------------------------------------------------------------------
    	Sync Shell Functions
    --------------------------------------------------------------------------*/

    public CoalesceEntitySyncShell getCoalesceEntitySyncShell(String key) throws CoalescePersistorException, SAXException,
            IOException
    {
        return CoalesceEntitySyncShell.create(this.getCoalesceEntity(key));
    }

}
