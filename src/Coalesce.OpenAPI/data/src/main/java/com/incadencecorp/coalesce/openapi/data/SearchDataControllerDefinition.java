/*-----------------------------------------------------------------------------'
 Copyright 2020 - InCadence Strategic Solutions Inc., All Rights Reserved

 Notwithstanding any contractor copyright notice, the Government has Unlimited
 Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 of this work other than as specifically authorized by these DFARS Clauses may
 violate Government rights in this work.

 DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 Unlimited Rights. The Government has the right to use, modify, reproduce,
 perform, display, release or disclose this computer software and to have or
 authorize others to do so.

 Distribution Statement D. Distribution authorized to the Department of
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 -----------------------------------------------------------------------------*/

package com.incadencecorp.coalesce.openapi.data;

import com.incadencecorp.coalesce.services.api.search.QueryResult;
import com.incadencecorp.coalesce.services.api.search.QueryType;
import com.incadencecorp.coalesce.services.search.service.data.jaxrs.ISearchDataControllerJaxRS;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchCriteria;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchQuery;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchQueryDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opengis.filter.capability.GeometryOperand;
import org.opengis.filter.capability.Operator;

import javax.ws.rs.Path;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

/**
 * This interface is only used for generating Open API documentation and should not be referenced by code or other projects.
 */
@Path("/search")
@Tag(name = "Search", description = "This endpoint is used for searching the Coalesce Data Fabric")
public interface SearchDataControllerDefinition extends ISearchDataControllerJaxRS {

    /**
     * Query API
     */

    @Operation(summary = "Simple Search", description = "Returns results matching the query pattern.")
    QueryResult search(List<SearchCriteria> options) throws RemoteException;

    @Operation(summary = "Complex Search", description = "Returns results matching the query pattern.")
    QueryResult searchComplex(SearchQuery query) throws RemoteException;

    @Operation(summary = "OGC Search", description = "Returns results matching the query pattern.")
    QueryResult searchOGC(QueryType query) throws RemoteException;

    /**
     * History API
     */

    @Operation(summary = "List History", description = "Returns a list of queries previously ran.")
    List<SearchQueryDetails> getHistory(int page, int pagesize) throws RemoteException;

    @Operation(summary = "List Saved History", description = "Returns a list of queries previously ran that have been saved.")
    List<SearchQueryDetails> getSavedHistory(int page, int pagesize) throws RemoteException;

    @Operation(summary = "Create History", description = "Save a query adding a title and description for future reference.")
    String save(final SearchQueryDetails object) throws RemoteException;

    @Operation(summary = "Update History", description = "Updates a previously saved query.")
    void update(final String key, final SearchQueryDetails object) throws RemoteException;

    @Operation(summary = "Retrieve History", description = "Returns a previously saved query.")
    SearchQueryDetails load(final String key) throws RemoteException;

    @Operation(summary = "Delete History", description = "Deletes a previously saved query.")
    void delete(final String key) throws RemoteException;

    @Operation(summary = "Run History", description = "Runs a previously saved query.")
    QueryResult requery(final String key) throws RemoteException;

    /**
     * Capabilities API
     */

    Collection<Operator> getAllCapabilities() throws RemoteException;

    Collection<Operator> getComparisonCapabilities() throws RemoteException;

    Collection<Operator> getFunctionCapabilities() throws RemoteException;

    Collection<Operator> getTemporalCapabilities() throws RemoteException;

    Collection<Operator> getSpatialCapabilities() throws RemoteException;

    Collection<GeometryOperand> getGeometryCapabilities() throws RemoteException;
}
