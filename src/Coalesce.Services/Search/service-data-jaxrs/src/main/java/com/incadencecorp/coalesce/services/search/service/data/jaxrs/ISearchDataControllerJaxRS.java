package com.incadencecorp.coalesce.services.search.service.data.jaxrs;

import java.rmi.RemoteException;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.incadencecorp.coalesce.services.api.search.QueryType;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchCriteria;

import com.incadencecorp.coalesce.services.api.search.SearchDataObjectResponse;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchQuery;

/**
 * JaxRS configuration for {@link SearchDataControllerJaxRS}.
 *
 * @author Derek Clemenzi
 */
@Path("/search")
public interface ISearchDataControllerJaxRS {

    @POST
    @Path("/simple")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    SearchDataObjectResponse search(List<SearchCriteria> options);

    @POST
    @Path("/complex")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    SearchDataObjectResponse searchComplex(SearchQuery query) throws RemoteException;

    @POST
    @Path("/ogc")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    SearchDataObjectResponse searchOGC(QueryType query) throws RemoteException;

}
