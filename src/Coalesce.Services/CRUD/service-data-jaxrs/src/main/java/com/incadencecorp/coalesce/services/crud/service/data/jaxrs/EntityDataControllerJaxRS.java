package com.incadencecorp.coalesce.services.crud.service.data.jaxrs;

import com.incadencecorp.coalesce.services.crud.api.ICrudClient;
import com.incadencecorp.coalesce.services.crud.service.data.controllers.EntityDataController;

/**
 * JaxRs Implementation
 * 
 * @author Derek Clemenzi
 */
public class EntityDataControllerJaxRS extends EntityDataController implements IEntityDataControllerJaxRS {

    /**
     * Default Constructor
     */
    public EntityDataControllerJaxRS(ICrudClient crud)
    {
        super(crud);
    }

}
