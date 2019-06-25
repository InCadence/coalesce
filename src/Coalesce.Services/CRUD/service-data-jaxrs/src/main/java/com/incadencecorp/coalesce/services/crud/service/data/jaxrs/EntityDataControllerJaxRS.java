package com.incadencecorp.coalesce.services.crud.service.data.jaxrs;

import com.incadencecorp.coalesce.api.CoalesceSimplePrincipal;
import com.incadencecorp.coalesce.api.ICoalescePrincipal;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.services.crud.service.data.controllers.EntityDataController;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.rmi.RemoteException;

/**
 * JaxRs Implementation
 *
 * @author Derek Clemenzi
 */
public class EntityDataControllerJaxRS extends EntityDataController implements IEntityDataControllerJaxRS {

    @Context
    SecurityContext securityContext;

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

    @Override
    protected ICoalescePrincipal getPrincipal()
    {
        if (securityContext != null && securityContext.getUserPrincipal() != null)
        {
            return new CoalesceSimplePrincipal(securityContext.getUserPrincipal());
        }
        else
        {
            return new CoalesceSimplePrincipal("");
        }
    }
}
