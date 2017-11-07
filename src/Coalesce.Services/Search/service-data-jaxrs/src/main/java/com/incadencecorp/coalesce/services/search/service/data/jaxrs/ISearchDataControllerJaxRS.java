package com.incadencecorp.coalesce.services.search.service.data.jaxrs;

import java.rmi.RemoteException;
import java.util.List;

import javax.ws.rs.*;

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
    @Produces("application/json")
    @Consumes("application/json")
    SearchDataObjectResponse search(List<SearchCriteria> options);

    @POST
    @Path("/complex")
    @Produces("application/json")
    @Consumes("application/json")
    SearchDataObjectResponse searchComplex(SearchQuery query) throws RemoteException;

    @POST
    @Path("/ogc")
    @Produces("application/json")
    @Consumes("application/json")
    SearchDataObjectResponse searchOGC(QueryType query) throws RemoteException;

}
