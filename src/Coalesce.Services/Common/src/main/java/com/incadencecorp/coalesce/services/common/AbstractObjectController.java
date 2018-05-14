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

package com.incadencecorp.coalesce.services.common;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.CoalesceComponentImpl;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.services.api.IObjectController;
import com.incadencecorp.coalesce.services.api.Results;
import com.incadencecorp.coalesce.services.api.crud.DataObjectStatusActionType;
import com.incadencecorp.coalesce.services.api.crud.DataObjectStatusType;
import com.incadencecorp.coalesce.services.crud.api.ICrudClient;

import java.rmi.RemoteException;

/**
 * @author Derek Clemenzi
 */
abstract public class AbstractObjectController<T, E extends CoalesceEntity> extends CoalesceComponentImpl
        implements IObjectController<T> {

    private ICrudClient crud;

    /**
     * Sets the CRUD implementation to be used for storing and retrieving Spider queries.
     *
     * @param client implementation
     */
    public void setCrud(ICrudClient client)
    {
        this.crud = client;
    }

    @Override
    public String save(T object) throws RemoteException
    {
        E entity = createEntity();
        entity.initialize();

        try
        {
            if (!crud.createDataObject(toCoalesce(entity, object)))
            {
                throw new RemoteException("Server Error Saving " + entity.getKey());
            }
        }
        catch (CoalesceException e)
        {
            throw new RemoteException("Failed Saving", e);
        }

        return entity.getKey();
    }

    @Override
    public void update(final String key, final T object) throws RemoteException
    {
        try
        {
            Results<CoalesceEntity> results[] = crud.retrieveDataObjects(key);

            if (results[0].isSuccessful())
            {
                E entity = createEntity();
                entity.initialize(results[0].getResult());

                if (!crud.updateDataObject(toCoalesce(entity, object)))
                {
                    throw new RemoteException("Server Error Updating " + entity.getKey());
                }
            }
            else
            {
                throw new RemoteException(results[0].getError());
            }
        }
        catch (CoalesceException | RemoteException e)
        {
            throw new RemoteException("Failed Updating", e);
        }
    }

    @Override
    public T load(String key) throws RemoteException
    {
        Results<CoalesceEntity> results[] = crud.retrieveDataObjects(key);

        try
        {
            if (results[0].isSuccessful())
            {
                E entity = createEntity();
                entity.initialize(results[0].getResult());

                return fromCoalesce(entity);
            }
            else
            {
                throw new RemoteException(results[0].getError());
            }
        }
        catch (CoalesceException e)
        {
            throw new RemoteException("Failed Loading", e);
        }
    }

    @Override
    public void delete(String key) throws RemoteException
    {
        DataObjectStatusType task = new DataObjectStatusType();
        task.setKey(key);
        task.setAction(DataObjectStatusActionType.MARK_AS_DELETED);

        if (!crud.updateDataObjectStatus(task))
        {
            throw new RemoteException("Server Error Deleting " + key);
        }
    }

    abstract protected E createEntity();

    abstract protected E toCoalesce(final E entity, final T object) throws CoalesceException;

    abstract protected T fromCoalesce(final E entity) throws CoalesceDataFormatException;

}
