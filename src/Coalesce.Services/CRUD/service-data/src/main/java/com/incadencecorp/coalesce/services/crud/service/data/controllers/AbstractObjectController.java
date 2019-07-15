/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.services.crud.service.data.controllers;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.CoalesceSimplePrincipal;
import com.incadencecorp.coalesce.api.ICoalescePrincipal;
import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.services.api.IObjectController;
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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Derek Clemenzi
 */
abstract public class AbstractObjectController<T, E extends CoalesceEntity> extends ServiceBase<CoalesceFramework>
        implements IObjectController<T> {

    /**
     * @param framework used for access the Coalesce data store
     */
    public AbstractObjectController(CoalesceFramework framework)
    {
        super(framework, framework.getExecutorService());
    }

    @Override
    public String save(T object) throws RemoteException
    {
        E entity = createEntity();
        entity.initialize();

        try
        {
            entity = toCoalesce(entity, object);
        }
        catch (CoalesceException e)
        {
            throw new CoalesceRemoteException("Failed Saving", e);
        }

        StringResponse response = performJob(new CreateDataObjectJob(new CoalesceRequest<>(new CoalesceEntity[] { entity
        })));
        verify(response.getResult());

        return entity.getKey();
    }

    @Override
    public void update(final String key, final T object) throws RemoteException
    {
        E entity = createEntity();
        entity.initialize();

        try
        {
            entity = toCoalesce(entity, object);
        }
        catch (CoalesceException e)
        {
            throw new CoalesceRemoteException("Failed Saving", e);
        }

        StringResponse response = performJob(new UpdateDataObjectJob(new CoalesceRequest<>(new CoalesceEntity[] { entity
        })));
        verify(response.getResult());
    }

    @Override
    public T load(String key) throws RemoteException
    {
        DataObjectKeyType task = new DataObjectKeyType();
        task.setKey(key);
        task.setVer(-1);

        DataObjectKeyRequest request = new DataObjectKeyRequest();
        request.getKeyList().addAll(Collections.singletonList(task));
        request.setAsyncCall(false);

        ICoalesceResponseType<List<ICoalesceResponseType<CoalesceEntity>>> response = performJob(new RetrieveDataObjectJob(
                request));
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

        E entity = createEntity();
        entity.initialize(results.get(0).getResult());

        try
        {
            return fromCoalesce(entity);
        }
        catch (CoalesceDataFormatException e)
        {
            throw new CoalesceRemoteException(e.getMessage(), e);
        }

    }

    @Override
    public void delete(String key) throws RemoteException
    {
        List<DataObjectStatusType> tasks = new ArrayList<>();

        DataObjectStatusType task = new DataObjectStatusType();
        task.setAction(DataObjectStatusActionType.MARK_AS_DELETED);
        task.setKey(key);

        tasks.add(task);

        DataObjectUpdateStatusRequest request = new DataObjectUpdateStatusRequest();
        request.getTaskList().addAll(tasks);
        request.setAsyncCall(false);

        StringResponse response = performJob(new UpdateDataObjectStatusJob(request));
        verify(response.getResult());
    }

    /**
     * @return the principal executing the current thread.
     */
    protected ICoalescePrincipal getPrincipal()
    {
        return new CoalesceSimplePrincipal();
    }

    abstract protected E createEntity();

    abstract protected E toCoalesce(final E entity, final T object) throws CoalesceException;

    abstract protected T fromCoalesce(final E entity) throws CoalesceDataFormatException;

}
