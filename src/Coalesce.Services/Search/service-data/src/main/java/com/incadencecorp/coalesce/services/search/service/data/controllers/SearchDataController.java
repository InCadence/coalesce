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

package com.incadencecorp.coalesce.services.search.service.data.controllers;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchCriteria;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchGroup;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchQuery;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.search.filter.FilterUtil;
import com.incadencecorp.coalesce.services.api.search.QueryType;
import com.incadencecorp.coalesce.services.api.search.SearchDataObjectResponse;
import com.incadencecorp.coalesce.services.api.search.SortByType;
import com.incadencecorp.coalesce.services.search.api.ISearchClient;

/**
 * Converts a list of options into an OGC filter and passes it along to a search
 * persister returning the results to the caller.
 *
 * @author Derek Clemenzi
 */
public class SearchDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchDataController.class);

    private ISearchClient client;

    public SearchDataController(ISearchClient value)
    {
        client = value;
    }

    /**
     * @param options
     * @return search results for the provided list of criteria.
     */
    public SearchDataObjectResponse search(List<SearchCriteria> options)
    {
        FilterFactory ff = CoalescePropertyFactory.getFilterFactory();

        SearchGroup group = new SearchGroup();
        group.setBooleanComparer("AND");
        group.setCriteria(options);

        try
        {
            Filter filter = getFilter(ff, group);

            LOGGER.debug("Filter: {}", filter.toString());

            return client.search(filter, 1);

        }
        catch (CoalesceException e)
        {
            throw new RuntimeException(e);
        }

    }

    public SearchDataObjectResponse searchComplex(SearchQuery query)
    {
        FilterFactory ff = CoalescePropertyFactory.getFilterFactory();

        try
        {
            Filter filter = getFilter(ff, query.getGroup());

            // Convert Properties
            PropertyName[] properties = new PropertyName[query.getPropertyNames().size()];
            for (int ii = 0; ii < query.getPropertyNames().size(); ii++)
            {
                properties[ii] = CoalescePropertyFactory.getFilterFactory().property(query.getPropertyNames().get(ii));
            }

            // Convert Sort
            SortBy[] sortBy = new SortBy[query.getSortBy().size()];
            for (int ii = 0; ii < query.getSortBy().size(); ii++)
            {
                SortByType sort = query.getSortBy().get(ii);

                sortBy[ii] = CoalescePropertyFactory.getFilterFactory().sort(sort.getPropertyName(),
                                                                             SortOrder.valueOf(sort.getSortOrder().toString()));
            }

            return client.search(filter, query.getPageNumber(), properties, sortBy, true);

        }
        catch (CoalesceException e)
        {
            throw new RuntimeException(e);
        }
    }

    public SearchDataObjectResponse searchOGC(QueryType query) throws RemoteException
    {
        // Convert Properties
        PropertyName[] properties = new PropertyName[query.getPropertyNames().size()];
        for (int ii = 0; ii < query.getPropertyNames().size(); ii++)
        {
            properties[ii] = CoalescePropertyFactory.getFilterFactory().property(query.getPropertyNames().get(ii));
        }

        // Convert Sort
        SortBy[] sortBy = new SortBy[query.getSortBy().size()];
        for (int ii = 0; ii < query.getSortBy().size(); ii++)
        {
            SortByType sort = query.getSortBy().get(ii);

            sortBy[ii] = CoalescePropertyFactory.getFilterFactory().sort(sort.getPropertyName(),
                                                                         SortOrder.valueOf(sort.getSortOrder().toString()));
        }

        // Convert Filter
        Filter filter;
        try
        {
            filter = FilterUtil.fromXml(query.getFilter());
        }
        catch (SAXException | IOException | ParserConfigurationException e)
        {
            throw new RemoteException("(FAILED) Parsing Filter", e);
        }

        // Execute Query
        try
        {
            return client.search(filter, query.getPageNumber(), properties, sortBy, true);
        }
        catch (CoalesceException e)
        {
            throw new RemoteException("(FAILED) Executing query", e);
        }
    }

    private Filter getFilter(FilterFactory ff, SearchGroup group)
    {
        List<Filter> filters = new ArrayList<>();

        for (SearchGroup subgroup : group.getGroups())
        {
            filters.add(getFilter(ff, subgroup));
        }

        for (SearchCriteria criteria : group.getCriteria())
        {
            PropertyName property = CoalescePropertyFactory.getFieldProperty(criteria.getRecordset(), criteria.getField());

            switch (criteria.getComparer())
            {
            case "=":
                filters.add(ff.equal(property, ff.literal(criteria.getValue()), criteria.isMatchCase()));
                break;
            case "!=":
                filters.add(ff.notEqual(property, ff.literal(criteria.getValue()), criteria.isMatchCase()));
                break;
            }
        }

        Filter filter;

        switch (group.getBooleanComparer().toLowerCase())
        {
        case "or":
            filter = ff.or(filters);
            break;
        default:
            filter = ff.and(filters);
            break;
        }

        return filter;
    }

}
