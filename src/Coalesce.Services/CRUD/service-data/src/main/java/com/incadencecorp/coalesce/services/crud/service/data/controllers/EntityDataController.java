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
package com.incadencecorp.coalesce.services.crud.service.data.controllers;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.api.Views;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.framework.exim.impl.JsonFullEximImpl;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.services.api.Results;
import com.incadencecorp.coalesce.services.api.crud.DataObjectStatusActionType;
import com.incadencecorp.coalesce.services.api.crud.DataObjectStatusType;
import com.incadencecorp.coalesce.services.crud.api.ICrudClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Responsible for storing and retrieving Coalesce entities.
 *
 * @author Derek Clemenzi
 */
public class EntityDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityDataController.class);
    private final JsonFullEximImpl exim = new JsonFullEximImpl();

    private ICrudClient crud;
    private ICoalescePersistor persister;
    private Class<?> clazz = Views.Entity.class;

    /**
     * @param crud      used for storing and retrieving Coalesce entities.
     * @param persister used for retrieving templates.
     */
    public EntityDataController(ICrudClient crud, ICoalescePersistor persister)
    {
        this.crud = crud;
        this.persister = persister;
    }

    /**
     * Sets the view to use when converting entities to JSON.
     *
     * @param clazz
     */
    public void setView(Class<?> clazz)
    {
        this.exim.setView(clazz);
        this.clazz = clazz;
    }

    /**
     * @return the view to use when converting entities to JSON.
     */
    public Class<?> getView()
    {
        return this.clazz;
    }

    public CoalesceEntity getEntity(String entityKey) throws RemoteException
    {
        return retrieveEntity(entityKey);
    }

    public String getEntityAsXml(String entityKey) throws RemoteException
    {
        return retrieveEntity(entityKey).toXml();
    }

    public void updateEntity(String entityKey, String json) throws RemoteException
    {
        JSONObject obj = new JSONObject(json);

        // If the supplied object has no key, set the key to the value of
        // "entityKey"--this prevents the key mismatch that would be caused
        // if the entity "initialize" method generated a random key.
        String keyName = CoalesceEntity.ATTRIBUTE_KEY;
        if (!obj.has(keyName) || obj.isNull(keyName) || obj.getString(keyName).equals(""))
        {
            obj.put(CoalesceEntity.ATTRIBUTE_KEY, entityKey);
        }

        setUpdatedEntity(entityKey, processJSON(obj));
    }

    public String createEntity(String json) throws RemoteException
    {
        JSONObject obj = new JSONObject(json);
        CoalesceEntity newEntity = processJSON(obj);
        String entityKey = newEntity.getKey();
        setNewEntity(newEntity);

        return entityKey;
    }

    private CoalesceEntity processJSON(JSONObject obj) throws RemoteException
    {
        String name = obj.getString(CoalesceEntity.ATTRIBUTE_NAME);
        String source = obj.getString(CoalesceEntity.ATTRIBUTE_SOURCE);
        String version = obj.getString(CoalesceEntity.ATTRIBUTE_VERSION);

        CoalesceEntity entity = null;
        CoalesceEntityTemplate template = null;

        // Load Template
        try
        {
            template = persister.getEntityTemplate(name, source, version);
        }
        catch (CoalescePersistorException e)
        {
            error(String.format(CoalesceErrors.NOT_FOUND,
                                "Template",
                                "name=" + name + ", source=" + source + ", version=" + version), e);
        }

        try
        {
            entity = exim.importValues(obj, template);
        }
        catch (CoalesceException e)
        {
            error(String.format(CoalesceErrors.NOT_SAVED, "Entity", name, e.getMessage()), e);
        }

        return entity;
    }

    public void updateEntityAsXml(String entityKey, String xml) throws RemoteException
    {
        setUpdatedEntity(entityKey, CoalesceEntity.createWithKey(entityKey, xml));
    }

    public String createEntityAsXml(String xml) throws RemoteException
    {
        CoalesceEntity newEntity = CoalesceEntity.create(xml);
        String entityKey = newEntity.getKey();
        setNewEntity(newEntity);

        return entityKey;
    }

    private void setUpdatedEntity(String entityKey, CoalesceEntity entity) throws RemoteException
    {
        if (entity == null)
        {
            error("(FAILED) Initializing Entity");
        }

        if (!entityKey.equalsIgnoreCase(entity.getKey()))
        {
            error(String.format(CoalesceErrors.KEY_MISMATCH, entityKey, entity.getKey()));
        }

        if (!crud.updateDataObject(entity))
        {
            error(crud.getLastResult()[0].getError());
        }
    }

    private void setNewEntity(CoalesceEntity entity) throws RemoteException
    {
        if (entity == null)
        {
            error("(FAILED) Initializing Entity");
        }

        if (!crud.createDataObject(entity))
        {
            error(crud.getLastResult()[0].getError());
        }
    }

    public void deleteEntities(String[] keys) throws RemoteException
    {
        List<DataObjectStatusType> tasks = new ArrayList<>();

        for (String key : keys)
        {
            DataObjectStatusType task = new DataObjectStatusType();
            task.setAction(DataObjectStatusActionType.MARK_AS_DELETED);
            task.setKey(key);

            tasks.add(task);
        }

        crud.updateDataObjectStatus(tasks.toArray(new DataObjectStatusType[tasks.size()]));
    }

    public CoalesceSection getSection(String entityKey, String key) throws RemoteException
    {
        return retrieveObject(entityKey, key, CoalesceSection.class);
    }

    public CoalesceRecordset getRecordset(String entityKey, String key) throws RemoteException
    {
        return retrieveObject(entityKey, key, CoalesceRecordset.class);
    }

    public CoalesceRecord getRecord(String entityKey, String key) throws RemoteException
    {
        return retrieveObject(entityKey, key, CoalesceRecord.class);
    }

    public CoalesceField<?> getField(String entityKey, String key) throws RemoteException
    {
        return retrieveObject(entityKey, key, CoalesceField.class);
    }

    // TODO Not Complete
    public void setFields(String entityKey, Map<String, String> values) throws RemoteException
    {
        CoalesceEntity entity = retrieveEntity(entityKey);

        for (Map.Entry<String, String> kvp : values.entrySet())
        {

        }
    }

    private <T extends CoalesceObject> T retrieveObject(String entityKey, String key, Class<T> clazz) throws RemoteException
    {
        CoalesceObject result = retrieveEntity(entityKey).getCoalesceObjectForKey(key);

        if (result == null)
        {
            error(String.format(CoalesceErrors.NOT_FOUND, clazz.getSimpleName(), key));
        }
        else if (!clazz.isInstance(result))
        {
            error(String.format(String.format(CoalesceErrors.INVALID_INPUT_REASON,
                                              key,
                                              "Expected: " + clazz.getSimpleName() + " Actual: "
                                                      + result.getClass().getSimpleName())));
        }

        return (T) result;
    }

    /**
     * Retrieves the entity from the data store and throws an exception if not
     * found or multiple entries are found.
     *
     * @param key
     * @return
     * @throws RemoteException
     */
    private CoalesceEntity retrieveEntity(String key) throws RemoteException
    {
        Results<CoalesceEntity>[] results = crud.retrieveDataObjects(key);

        if (results.length == 0)
        {
            error(String.format(CoalesceErrors.NOT_FOUND, "Entity", key));
        }
        else if (results.length > 1)
        {
            error(String.format(CoalesceErrors.INVALID_OBJECT, "Entity", key));
        }
        else if (results[0].getStatus() != EResultStatus.SUCCESS)
        {
            LOGGER.debug(results[0].getError());
            error(String.format(CoalesceErrors.NOT_FOUND, "Entity", key));
        }

        return results[0].getResult();
    }

    private void error(String msg) throws RemoteException
    {
        error(msg, null);
    }

    private void error(String msg, Exception e) throws RemoteException
    {
        if (e == null)
        {
            LOGGER.warn(msg);
        }
        else
        {
            LOGGER.error(msg, e);
        }

        throw new RemoteException(msg);
    }
}
