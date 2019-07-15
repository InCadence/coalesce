package com.incadencecorp.coalesce.services.search.service.data.jaxrs;

import com.incadencecorp.coalesce.api.CoalesceSimplePrincipal;
import com.incadencecorp.coalesce.api.ICoalescePrincipal;
import com.incadencecorp.coalesce.search.CoalesceSearchFramework;
import com.incadencecorp.coalesce.services.search.service.data.controllers.SearchDataController;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * JaxRs Implementation
 *
 * @author Derek Clemenzi
 */
public class SearchDataControllerJaxRS extends SearchDataController implements ISearchDataControllerJaxRS {

    @Context
    SecurityContext securityContext;

    public SearchDataControllerJaxRS(CoalesceSearchFramework framework)
    {
        super(framework);
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
            return new CoalesceSimplePrincipal();
        }
    }
}
