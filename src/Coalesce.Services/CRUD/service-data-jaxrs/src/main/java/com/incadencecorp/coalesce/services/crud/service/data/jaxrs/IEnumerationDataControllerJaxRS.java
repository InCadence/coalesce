package com.incadencecorp.coalesce.services.crud.service.data.jaxrs;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONObject;

/**
 * JaxRS configuration used on {@link EnumerationDataControllerJaxRS}
 * 
 * @author Derek Clemenzi
 */
@Path("enumerations")
public interface IEnumerationDataControllerJaxRS {

    @GET
    @Path("/")
    @Produces("application/json")
    Map<String, String> getEnumerationList();

    @GET
    @Path("/{key}")
    @Consumes("application/json")
    JSONObject getEnumeration(@PathParam("key") String key);

    @POST
    @Path("/{key}")
    @Produces("application/json")
    void updateEnumeration(@PathParam("key")String key, JSONObject value);

}
