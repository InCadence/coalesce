package com.incadencecorp.coalesce.services.crud.service.data.jaxrs;

import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.services.crud.api.ICrudClient;
import com.incadencecorp.coalesce.services.crud.service.data.controllers.EntityDataController;

import java.rmi.RemoteException;

/**
 * JaxRs Implementation
 *
 * @author Derek Clemenzi
 */
public class EntityDataControllerJaxRS extends EntityDataController implements IEntityDataControllerJaxRS {

    /**
     * Default Constructor
     *
     * @see EntityDataController
     */
    public EntityDataControllerJaxRS(ICrudClient crud, ICoalescePersistor persister)
    {
        super(crud, persister);
    }

    @Override
    public void deleteEntity(String entityKey) throws RemoteException
    {
        this.deleteEntities(new String[] { entityKey });
    }
}
