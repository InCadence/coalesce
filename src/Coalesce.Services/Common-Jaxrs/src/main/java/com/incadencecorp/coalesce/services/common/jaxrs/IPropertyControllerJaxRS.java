package com.incadencecorp.coalesce.services.common.jaxrs;

import javax.ws.rs.*;

/**
 * JaxRS annotations used on {@link com.incadencecorp.coalesce.services.common.controllers.PropertyController}
 *
 * @author Derek Clemenzi
 */
@Path("property")
interface IPropertyControllerJaxRS {

    @GET
    @Path("/{name}")
    @Produces("application/text")
    String getProperty(@PathParam("name") String name);

    @POST
    @Path("/{name}")
    @Consumes("application/text")
    void setProperty(@PathParam("name") String name, String value);
}
