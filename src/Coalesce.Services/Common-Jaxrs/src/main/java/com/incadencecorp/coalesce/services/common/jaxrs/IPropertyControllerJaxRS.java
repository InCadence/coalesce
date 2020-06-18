package com.incadencecorp.coalesce.services.common.jaxrs;

import com.incadencecorp.coalesce.api.ICoalescePrincipal;
import com.incadencecorp.coalesce.services.common.api.IPropertyController;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Extends {@link IPropertyController} and appends JAX-RS annotations.
 *
 * @author Derek Clemenzi
 */
@Path("property")
public interface IPropertyControllerJaxRS extends IPropertyController {

    @GET
    @Path("/{name}.json")
    @Produces(MediaType.APPLICATION_JSON)
    String getJsonConfiguration(@PathParam("name") String name) throws RemoteException;

    @PUT
    @Path("/{name}.json")
    @Consumes(MediaType.APPLICATION_JSON)
    void setJsonConfiguration(@PathParam("name") String name, String json) throws RemoteException;

    @GET
    @Path("/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    String getProperty(@PathParam("name") String name) throws RemoteException;

    @PUT
    @Path("/{name}")
    @Consumes(MediaType.TEXT_PLAIN)
    void setProperty(@PathParam("name") String name, String value) throws RemoteException;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, String> getProperties() throws RemoteException;

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Map<String, String> getProperties(String[] names) throws RemoteException;

    @PUT
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    void setProperties(Map<String, String> values) throws RemoteException;

    @GET
    @Path("/whoami")
    @Produces(MediaType.APPLICATION_JSON)
    ICoalescePrincipal whoami();

}
