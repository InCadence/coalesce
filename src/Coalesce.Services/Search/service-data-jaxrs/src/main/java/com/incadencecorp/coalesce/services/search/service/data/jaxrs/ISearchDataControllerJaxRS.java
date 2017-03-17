package com.incadencecorp.coalesce.services.search.service.data.jaxrs;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.incadencecorp.coalesce.services.api.search.SearchDataObjectResponse;
import com.incadencecorp.coalesce.services.search.service.data.model.Option;

/**
 * JaxRS configuration for {@link SearchDataControllerJaxRS}.
 * 
 * @author Derek Clemenzi
 */
@Path("/")
public interface ISearchDataControllerJaxRS {

    @POST
    @Path("/search")
    @Produces("application/json")
    @Consumes("application/json")
    SearchDataObjectResponse search(List<Option> options);

}
