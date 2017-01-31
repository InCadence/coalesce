/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.services.search.api;

import javax.xml.transform.TransformerException;

import org.opengis.filter.Filter;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.services.api.IBaseClient;
import com.incadencecorp.coalesce.services.api.search.SearchDataObjectResponse;

public interface ISearchClient extends IBaseClient<ISearchEvents> {

    /**
     * Sets the max results returned in a single query.
     * 
     * @param value
     */
    void setPageSize(int value);

    /**
     * Searches the DSS database.
     *
     * @param filter OGC filter defining the constraints that can be checked
     *            against an instance of an object.
     * @param pageNumber sets the offset of the query. Each page is determined
     *            by {@link #setMaxSearchResults(int)}.
     * @return the information about the objects that match the constraints.
     * @throws TransformerException is thrown when there was an issue with the
     *             filter.
     * @throws CoalesceException
     */
    SearchDataObjectResponse search(Filter filter, int pageNumber) throws CoalesceException;

    /**
     * Asynchronously searches the DSS database.
     *
     * @param filter OGC filter defining the constraints that can be checked
     *            against an instance of an object.
     * @param pageNumber sets the offset of the query. Each page is determined
     *            by {@link #setMaxSearchResults(int)}.
     * @return the job ID
     * @throws CoalesceException
     */
    String searchAsync(Filter filter, int pageNumber) throws CoalesceException;

    /**
     * Searches the DSS database.
     *
     * @param filter OGC filter defining the constraints that can be checked
     *            against an instance of an object.
     * @param pageNumber sets the offset of the query. Each page is determined
     *            by {@link #setMaxSearchResults(int)}.
     * @param properties is the list of properties (fields) that you wish to
     *            return as a part of your query. Property formatting follow the
     *            same rules as the filter "recordset name"."field name"; so if
     *            you wanted to return access scope this would be
     *            'access_control_recordset.accessscope' or for DSS fields you
     *            can use
     *            {@link com.lmco.omega.dss.client.api.factory.DSSPropertyFactory}
     *            to get the properties.
     * @param sortBy is the list of properties (fields) that you wish to search
     *            on along with the direction. Property formatting follow the
     *            same rules as the filter "recordset name"."field name".
     * @param includeHidden if <code>true</code> then objects that have been
     *            hidden will be returned.
     * @return the keys of objects that match the search criteria.
     * @throws CoalesceException
     */
    SearchDataObjectResponse search(Filter filter,
                                    int pageNumber,
                                    PropertyName[] properties,
                                    SortBy[] sortBy,
                                    boolean includeHidden)
            throws CoalesceException;

    /**
     * Asynchronously performs a structure search.
     *
     * @param filter OGC filter defining the constraints that can be checked
     *            against an instance of an object.
     * @param pageNumber sets the offset of the query. Each page is determined
     *            by {@link #setMaxSearchResults(int)}.
     * @param properties is the list of properties (fields) that you wish to
     *            return as a part of your query. Property formatting follow the
     *            same rules as the filter "recordset name"."field name"; so if
     *            you wanted to return access scope this would be
     *            'access_control_recordset.accessscope' or for DSS fields you
     *            can use
     *            {@link com.lmco.omega.dss.client.api.factory.DSSPropertyFactory}
     *            to get the properties.
     * @param sortBy is the list of properties (fields) that you wish to search
     *            on along with the direction. Property formatting follow the
     *            same rules as the filter "recordset name"."field name".
     * @param includeHidden if <code>true</code> then objects that have been
     *            hidden will be returned.
     * @return the job ID
     * @throws CoalesceException
     */
    String searchAsync(Filter filter, int pageNumber, PropertyName[] properties, SortBy[] sortBy, boolean includeHidden)
            throws CoalesceException;

}
