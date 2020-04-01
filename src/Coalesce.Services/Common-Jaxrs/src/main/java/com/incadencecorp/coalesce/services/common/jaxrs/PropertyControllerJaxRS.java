package com.incadencecorp.coalesce.services.common.jaxrs;

import com.incadencecorp.coalesce.api.CoalesceSimplePrincipal;
import com.incadencecorp.coalesce.api.ICoalescePrincipal;
import com.incadencecorp.coalesce.services.common.controllers.PropertyController;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * JaxRs Implementation
 *
 * @author Derek Clemenzi
 */
public class PropertyControllerJaxRS extends PropertyController implements IPropertyControllerJaxRS {

    @Context
    SecurityContext securityContext;

    public PropertyControllerJaxRS()
    {
        super();
    }

    public PropertyControllerJaxRS(String path)
    {
        super(path);
    }

    @Override
    public ICoalescePrincipal whoami()
    {
        if (securityContext != null && securityContext.getUserPrincipal() != null)
        {
            if (securityContext.getUserPrincipal() instanceof CoalesceSimplePrincipal)
            {
                return (CoalesceSimplePrincipal) securityContext.getUserPrincipal();
            }
            else
            {
                return new CoalesceSimplePrincipal(securityContext.getUserPrincipal());
            }
        }
        else
        {
            return new CoalesceSimplePrincipal();
        }
    }
}
