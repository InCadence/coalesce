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
import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.api.Views;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceObject;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.exim.impl.JsonFullEximImpl;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.services.api.common.StringResponse;
import com.incadencecorp.coalesce.services.api.crud.DataObjectKeyRequest;
import com.incadencecorp.coalesce.services.api.crud.DataObjectKeyType;
import com.incadencecorp.coalesce.services.api.crud.DataObjectStatusActionType;
import com.incadencecorp.coalesce.services.api.crud.DataObjectStatusType;
import com.incadencecorp.coalesce.services.api.crud.DataObjectUpdateStatusRequest;
import com.incadencecorp.coalesce.services.common.CoalesceRemoteException;
import com.incadencecorp.coalesce.services.common.ServiceBase;
import com.incadencecorp.coalesce.services.crud.service.data.controllers.jobs.CoalesceRequest;
import com.incadencecorp.coalesce.services.crud.service.data.controllers.jobs.CreateDataObjectJob;
import com.incadencecorp.coalesce.services.crud.service.data.controllers.jobs.RetrieveDataObjectJob;
import com.incadencecorp.coalesce.services.crud.service.data.controllers.jobs.UpdateDataObjectJob;
import com.incadencecorp.coalesce.services.crud.service.data.controllers.jobs.UpdateDataObjectStatusJob;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Responsible for storing and retrieving Coalesce entities.
 *
 * @author Derek Clemenzi
 */
public class EntityDataController extends ServiceBase<CoalesceFramework> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityDataController.class);
    private final JsonFullEximImpl exim = new JsonFullEximImpl();

    private Class<?> clazz = Views.Entity.class;

    /**
     * @param framework used for access the Coalesce data store
     */
    public EntityDataController(CoalesceFramework framework)
    {
        super(framework, framework.getExecutorService());
    }

    /**
     * Sets the view to use when converting entities to JSON.
     *
     * @param clazz view's class
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

    public void updateEntityAsJson(String entityKey, String json) throws RemoteException
    {
        updatedEntity(entityKey, processJSON(json));
    }

    public String createEntityAsJson(String json) throws RemoteException
    {
        return createEntity(processJSON(json));
    }

    private CoalesceEntity processJSON(String json) throws RemoteException
    {
        JSONObject obj = new JSONObject(json);

        String name = obj.getString(CoalesceEntity.ATTRIBUTE_NAME);
        String source = obj.getString(CoalesceEntity.ATTRIBUTE_SOURCE);
        String version = obj.getString(CoalesceEntity.ATTRIBUTE_VERSION);

        CoalesceEntity entity = null;
        CoalesceEntityTemplate template = CoalesceTemplateUtil.getTemplate(name, source, version);

        if (template != null)
        {
            try
            {
                entity = exim.importValues(obj, template);
            }
            catch (CoalesceException e)
            {
                throw new CoalesceRemoteException(String.format(CoalesceErrors.NOT_SAVED, "Entity", name, e.getMessage()),
                                                  e);
            }
        }
        else
        {
            throw new CoalesceRemoteException(String.format(CoalesceErrors.NOT_FOUND,
                                                            "Template",
                                                            "name=" + name + ", source=" + source + ", version=" + version));
        }

        return entity;
    }

    public void updateEntityAsXml(String entityKey, String xml) throws RemoteException
    {
        updatedEntity(entityKey, CoalesceEntity.create(xml));
    }

    public String createEntityAsXml(String xml) throws RemoteException
    {
        return createEntity(CoalesceEntity.create(xml));
    }

    private void updatedEntity(String entityKey, CoalesceEntity entity) throws RemoteException
    {
        if (entity != null)
        {
            if (!entityKey.equalsIgnoreCase(entity.getKey()))
            {
                if (entity.wasKeyGenerated())
                {
                    // Replace with key specified by the endpoint
                    entity.setKey(entityKey);
                }
                else
                {
                    throw new CoalesceRemoteException(String.format(CoalesceErrors.KEY_MISMATCH, entityKey, entity.getKey()));
                }
            }

            StringResponse response = performJob(new UpdateDataObjectJob(new CoalesceRequest<>(new CoalesceEntity[] { entity
            })));
            verify(response.getResult());
        }
        else
        {
            throw new CoalesceRemoteException("(FAILED) Initializing Entity");
        }
    }

    private String createEntity(CoalesceEntity entity) throws RemoteException
    {
        String key = null;

        if (entity != null)
        {
            StringResponse response = performJob(new CreateDataObjectJob(new CoalesceRequest<>(new CoalesceEntity[] { entity
            })));
            verify(response.getResult());

            key = entity.getKey();
        }
        else
        {
            throw new CoalesceRemoteException("(FAILED) Initializing Entity");
        }

        return key;
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

        StringResponse response = performJob(new UpdateDataObjectStatusJob(createUpdateDataObjectStatusRequest(false,
                                                                                                               tasks)));
        verify(response.getResult());
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

    public void setFields(String key, Map<String, String> values) throws RemoteException
    {
        try
        {
            CoalesceEntity entity = getTarget().getCoalesceEntity(key);

            for (Map.Entry<String, String> kvp : values.entrySet())
            {
                CoalesceObject field = entity.getCoalesceObjectForKey(kvp.getKey());

                if (field instanceof CoalesceField)
                {
                    field.setAttribute(CoalesceField.ATTRIBUTE_VALUE, kvp.getValue());
                }
                else
                {
                    throw new CoalesceRemoteException(String.format(CoalesceErrors.NOT_FOUND, "Field", kvp.getKey()));
                }
            }

            try
            {
                getTarget().saveCoalesceEntity(entity);
            }
            catch (CoalescePersistorException e)
            {
                throw new CoalesceRemoteException(String.format(CoalesceErrors.NOT_SAVED, key, "Entity", e.getMessage()), e);
            }
        }
        catch (CoalescePersistorException e)
        {
            throw new CoalesceRemoteException(String.format(CoalesceErrors.NOT_FOUND, "Entity", key), e);
        }
    }

    private <T extends CoalesceObject> T retrieveObject(String entityKey, String key, Class<T> clazz) throws RemoteException
    {
        CoalesceObject result = retrieveEntity(entityKey).getCoalesceObjectForKey(key);

        if (result == null)
        {
            throw new CoalesceRemoteException(String.format(CoalesceErrors.NOT_FOUND, clazz.getSimpleName(), key));
        }
        else if (!clazz.isInstance(result))
        {
            throw new CoalesceRemoteException(String.format(CoalesceErrors.INVALID_INPUT_REASON,
                                key,
                                "Expected: " + clazz.getSimpleName() + " Actual: " + result.getClass().getSimpleName()));
        }

        return (T) result;
    }

    /**
     * Retrieves the entity from the data store and throws an exception if not
     * found or multiple entries are found.
     *
     * @param key of the entity to retrieve
     * @return the Coalesce Entity
     * @throws RemoteException on error
     */
    private CoalesceEntity retrieveEntity(String key) throws RemoteException
    {
        DataObjectKeyType task = new DataObjectKeyType();
        task.setKey(key);
        task.setVer(-1);

        ICoalesceResponseType<List<ICoalesceResponseType<CoalesceEntity>>> response = performJob(new RetrieveDataObjectJob(
                createDataObjectKeyRequest(false, task)));
        List<ICoalesceResponseType<CoalesceEntity>> results = response.getResult();

        verify(results);

        if (results.isEmpty())
        {
            throw new CoalesceRemoteException(String.format(CoalesceErrors.NOT_FOUND, "Entity", key));
        }
        else if (results.size() > 1)
        {
            throw new CoalesceRemoteException(String.format(CoalesceErrors.INVALID_OBJECT, "Entity", key));
        }

        return results.get(0).getResult();
    }

    private DataObjectUpdateStatusRequest createUpdateDataObjectStatusRequest(final boolean async,
                                                                              final List<DataObjectStatusType> tasks)
    {
        DataObjectUpdateStatusRequest request = new DataObjectUpdateStatusRequest();
        request.getTaskList().addAll(tasks);
        request.setAsyncCall(async);

        return request;
    }

    private DataObjectKeyRequest createDataObjectKeyRequest(final boolean async, final DataObjectKeyType... tasks)
    {
        DataObjectKeyRequest request = new DataObjectKeyRequest();
        request.getKeyList().addAll(Arrays.asList(tasks));
        request.setAsyncCall(async);

        return request;
    }

}
