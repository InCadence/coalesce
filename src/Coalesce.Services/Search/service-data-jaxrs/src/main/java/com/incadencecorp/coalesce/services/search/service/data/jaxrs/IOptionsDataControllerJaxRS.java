package com.incadencecorp.coalesce.services.search.service.data.jaxrs;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.incadencecorp.coalesce.services.search.service.data.model.SearchCriteria;

/**
 * JaxRS configuration used on {@link OptionsDataControllerJaxRS}
 * 
 * @author Derek Clemenzi
 */
@Path("options")
public interface IOptionsDataControllerJaxRS {

    @GET
    @Path("/{key}")
    @Produces("application/json")
    List<SearchCriteria> loadOptions(@PathParam("key") String key);

    @POST
    @Path("/{key}")
    @Consumes("application/json")
    void saveOptions(@PathParam("key") String key, List<SearchCriteria> options);

    @GET
    @Path("/")
    @Produces("application/json")
    List<String> getOptions();

}
