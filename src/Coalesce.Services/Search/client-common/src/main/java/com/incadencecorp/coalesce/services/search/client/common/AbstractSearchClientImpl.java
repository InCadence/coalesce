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

package com.incadencecorp.coalesce.services.search.client.common;

import javax.xml.transform.TransformerException;

import org.opengis.filter.Filter;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.search.filter.FilterUtil;
import com.incadencecorp.coalesce.search.filter.FilterUtil.EConfiguration;
import com.incadencecorp.coalesce.services.api.search.ESortDirection;
import com.incadencecorp.coalesce.services.api.search.QueryType;
import com.incadencecorp.coalesce.services.api.search.SearchDataObjectRequest;
import com.incadencecorp.coalesce.services.api.search.SearchDataObjectResponse;
import com.incadencecorp.coalesce.services.api.search.SortByType;
import com.incadencecorp.coalesce.services.client.common.AbstractBaseClient;
import com.incadencecorp.coalesce.services.search.api.ISearchClient;
import com.incadencecorp.coalesce.services.search.api.ISearchEvents;

public abstract class AbstractSearchClientImpl extends AbstractBaseClient<ISearchEvents> implements ISearchClient {

    /*--------------------------------------------------------------------------
    Private Members
    --------------------------------------------------------------------------*/

    private int pageSize = 200;

    /*--------------------------------------------------------------------------
    Override Methods
    --------------------------------------------------------------------------*/

    @Override
    public void setPageSize(int value)
    {
        pageSize = value;
    }

    @Override
    public SearchDataObjectResponse search(Filter filter, int pageNumber) throws CoalesceException
    {
        return search(filter, pageNumber, null, null, false);
    }

    @Override
    public String searchAsync(Filter filter, int pageNumber) throws CoalesceException
    {
        return searchAsync(filter, pageNumber, null, null, false);
    }

    @Override
    public SearchDataObjectResponse search(Filter filter,
                                           int pageNumber,
                                           PropertyName[] properties,
                                           SortBy[] sortBy,
                                           boolean includeHidden)
            throws CoalesceException
    {
        return search(createSearchDataObjectRequest(false, filter, pageNumber, properties, sortBy, includeHidden));
    }

    @Override
    public String searchAsync(Filter filter,
                              int pageNumber,
                              PropertyName[] properties,
                              SortBy[] sortBy,
                              boolean includeHidden)
            throws CoalesceException
    {
        SearchDataObjectRequest request = createSearchDataObjectRequest(true,
                                                                        filter,
                                                                        pageNumber,
                                                                        properties,
                                                                        sortBy,
                                                                        includeHidden);

        return addAsyncResponse(search(request), request);
    }

    /*--------------------------------------------------------------------------
    Abstract Methods
    --------------------------------------------------------------------------*/

    abstract protected SearchDataObjectResponse search(SearchDataObjectRequest request);

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private SearchDataObjectRequest createSearchDataObjectRequest(boolean async,
                                                                  Filter filter,
                                                                  int pageNumber,
                                                                  PropertyName[] properties,
                                                                  SortBy[] sortBy,
                                                                  boolean includeHidden)
            throws CoalesceException
    {

        String xml;
        try
        {
            xml = FilterUtil.toXml(EConfiguration.CUSTOM, filter);
        }
        catch (TransformerException e)
        {
            throw new CoalesceException("Failed to serialize filter.", e);
        }

        // Create Request
        QueryType query = new QueryType();
        query.setFilter(xml);
        query.setPageNumber(pageNumber);
        query.setPageSize(pageSize);
        query.setIncludeHidden(includeHidden);

        if (properties != null)
        {
            for (PropertyName proeprty : properties)
            {
                query.getPropertyNames().add(proeprty.getPropertyName());
            }
        }

        if (sortBy != null)
        {
            for (SortBy sort : sortBy)
            {

                SortByType type = new SortByType();

                type.setPropertyName(sort.getPropertyName().getPropertyName());
                type.setSortOrder(ESortDirection.valueOf(sort.getSortOrder().toSQL()));

                query.getSortBy().add(type);
            }
        }

        SearchDataObjectRequest request = new SearchDataObjectRequest();
        request.getQuery().add(query);
        request.setAsyncCall(async);

        return request;
    }

}
