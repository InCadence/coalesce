package com.incadencecorp.coalesce.services.crud.service.data.jaxrs;

import com.incadencecorp.coalesce.framework.CoalesceFramework;
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
    public EntityDataControllerJaxRS(CoalesceFramework framework)
    {
        super(framework);
    }

    @Override
    public void deleteEntity(String entityKey) throws RemoteException
    {
        this.deleteEntities(new String[] { entityKey });
    }
}
