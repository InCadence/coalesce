package com.incadencecorp.coalesce.services.common.jaxrs;

import com.incadencecorp.coalesce.services.common.controllers.datamodel.GraphObj;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.rmi.RemoteException;
import java.util.List;

/**
 * JaxRS annotations used on {@link com.incadencecorp.coalesce.services.common.controllers.BlueprintController}
 *
 * @author Derek Clemenzi
 */
@Path("blueprints")
interface IBlueprintControllerJaxRS {

    @GET
    @Path("/")
    @Produces("application/json")
    List<String> getBlueprints();

    @GET
    @Path("/{name}")
    @Produces("application/json")
    GraphObj getBlueprint(@PathParam("name") String name) throws RemoteException;
}
