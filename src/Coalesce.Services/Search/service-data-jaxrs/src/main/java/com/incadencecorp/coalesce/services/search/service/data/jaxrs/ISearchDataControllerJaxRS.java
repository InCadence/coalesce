package com.incadencecorp.coalesce.services.search.service.data.jaxrs;

import com.incadencecorp.coalesce.common.helpers.GUIDHelper;
import com.incadencecorp.coalesce.services.api.IObjectController;
import com.incadencecorp.coalesce.services.api.search.QueryResult;
import com.incadencecorp.coalesce.services.api.search.QueryType;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchCriteria;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchQuery;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchQueryDetails;
import org.opengis.filter.capability.GeometryOperand;
import org.opengis.filter.capability.Operator;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
public interface ISearchDataControllerJaxRS  {

    /**
     * Query API
     */

    @POST
    @Path("/simple")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    QueryResult search(List<SearchCriteria> options) throws RemoteException;

    @POST
    @Path("/complex")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    QueryResult searchComplex(SearchQuery query) throws RemoteException;

    @POST
    @Path("/ogc")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    QueryResult searchOGC(QueryType query) throws RemoteException;

    /**
     * History API
     */

    @GET
    @Path("/history")
    @Produces(MediaType.APPLICATION_JSON)
    List<SearchQueryDetails> getHistory(@DefaultValue("1") @QueryParam("page") int page,
                                        @DefaultValue("200") @QueryParam("pagesize") int pagesize) throws RemoteException;

    @GET
    @Path("/history/saved")
    @Produces(MediaType.APPLICATION_JSON)
    List<SearchQueryDetails> getSavedHistory(@DefaultValue("1") @QueryParam("page") int page,
                                             @DefaultValue("200") @QueryParam("pagesize") int pagesize)
            throws RemoteException;

    @POST
    @Path("/history")
    @Consumes(MediaType.APPLICATION_JSON)
    String save(final SearchQueryDetails object) throws RemoteException;

    @PUT
    @Path("/history/{key:" + GUIDHelper.REGEX_UUID + "}")
    @Consumes(MediaType.APPLICATION_JSON)
    void update(@PathParam("key") final String key, final SearchQueryDetails object) throws RemoteException;

    @GET
    @Path("/history/{key:" + GUIDHelper.REGEX_UUID + "}")
    @Produces(MediaType.APPLICATION_JSON)
    SearchQueryDetails load(@PathParam("key") final String key) throws RemoteException;

    @DELETE
    @Path("/history/{key:" + GUIDHelper.REGEX_UUID + "}")
    void delete(@PathParam("key") final String key) throws RemoteException;

    /**
     * Capabilities API
     */

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
