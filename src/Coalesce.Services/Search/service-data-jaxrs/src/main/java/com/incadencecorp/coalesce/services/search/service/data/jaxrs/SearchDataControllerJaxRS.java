package com.incadencecorp.coalesce.services.search.service.data.jaxrs;

import com.incadencecorp.coalesce.search.CoalesceSearchFramework;
import com.incadencecorp.coalesce.services.search.service.data.controllers.SearchDataController;

/**
 * JaxRs Implementation
 *
 * @author Derek Clemenzi
 */
public class SearchDataControllerJaxRS extends SearchDataController implements ISearchDataControllerJaxRS {

    public SearchDataControllerJaxRS(CoalesceSearchFramework value)
    {
        super(value);
    }

}
