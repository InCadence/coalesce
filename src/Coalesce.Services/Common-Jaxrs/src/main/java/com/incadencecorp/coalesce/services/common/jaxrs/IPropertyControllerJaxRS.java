package com.incadencecorp.coalesce.services.common.jaxrs;

import javax.ws.rs.*;
import java.rmi.RemoteException;
import java.util.Map;

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
    String getProperty(@PathParam("name") String name) throws RemoteException;

    @POST
    @Path("/{name}")
    @Consumes("application/text")
    void setProperty(@PathParam("name") String name, String value) throws RemoteException;

    @GET
    @Path("/")
    @Produces("application/json")
    Map<String, String> getProperties() throws RemoteException;

    @POST
    @Path("/")
    @Consumes("application/json")
    void setProperties(Map<String, String> values) throws RemoteException;

}
