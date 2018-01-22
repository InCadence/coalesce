package com.incadencecorp.coalesce.services.search.service.data.jaxrs;

import com.incadencecorp.coalesce.services.api.search.QueryResultType;
import com.incadencecorp.coalesce.services.api.search.QueryType;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchCriteria;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchQuery;
import org.opengis.filter.capability.GeometryOperand;
import org.opengis.filter.capability.Operator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

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
    QueryResultType search(List<SearchCriteria> options);

    @POST
    @Path("/complex")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    QueryResultType searchComplex(SearchQuery query) throws RemoteException;

    @POST
    @Path("/ogc")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    QueryResultType searchOGC(QueryType query) throws RemoteException;

    @GET
    @Path("/capabilities")
    @Produces(MediaType.APPLICATION_JSON)
    Collection<Operator> getAllCapabilities() throws RemoteException;

    @GET
    @Path("/capabilities/comparison")
    @Produces(MediaType.APPLICATION_JSON)
    Collection<Operator> getComparisonCapabilities() throws RemoteException;

    @GET
    @Path("/capabilities/functions")
    @Produces(MediaType.APPLICATION_JSON)
    Collection<Operator> getFunctionCapabilities() throws RemoteException;

    @GET
    @Path("/capabilities/temporal")
    @Produces(MediaType.APPLICATION_JSON)
    Collection<Operator> getTemporalCapabilities() throws RemoteException;

    @GET
    @Path("/capabilities/spatial")
    @Produces(MediaType.APPLICATION_JSON)
    Collection<Operator> getSpatialCapabilities() throws RemoteException;

    @GET
    @Path("/capabilities/geometry")
    @Produces(MediaType.APPLICATION_JSON)
    Collection<GeometryOperand> getGeometryCapabilities() throws RemoteException;

}
