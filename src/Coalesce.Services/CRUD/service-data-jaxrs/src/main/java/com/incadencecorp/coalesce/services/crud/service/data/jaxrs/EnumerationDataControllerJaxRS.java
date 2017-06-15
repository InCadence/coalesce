package com.incadencecorp.coalesce.services.crud.service.data.jaxrs;

import com.incadencecorp.coalesce.services.crud.api.ICrudClient;
import com.incadencecorp.coalesce.services.crud.service.data.controllers.EnumerationDataController;
import com.incadencecorp.coalesce.services.search.api.ISearchClient;

/**
 * JaxRs Implementation
 * 
 * @author Derek Clemenzi
 */
public class EnumerationDataControllerJaxRS extends EnumerationDataController implements IEnumerationDataControllerJaxRS {

    /**
     * Default Constructor
     */
    public EnumerationDataControllerJaxRS(ICrudClient crud, ISearchClient search)
    {
        super(crud, search);
    }

}
