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

import java.util.ArrayList;
import java.util.List;

import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.PropertyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.services.api.search.SearchDataObjectResponse;
import com.incadencecorp.coalesce.services.search.api.ISearchClient;
import com.incadencecorp.coalesce.services.search.service.data.model.Option;

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
     * @return search results for the provided options.
     */
    public SearchDataObjectResponse search(List<Option> options)
    {
        LOGGER.trace("Submitting Options");

        FilterFactory ff = CoalescePropertyFactory.getFilterFactory();

        List<Filter> filters = new ArrayList<Filter>();

        for (Option option : options)
        {
            PropertyName property = CoalescePropertyFactory.getFieldProperty(option.getRecordset(), option.getField());

            switch (option.getComparer()) {
            case "=":
                filters.add(ff.equal(property, ff.literal(option.getValue()), option.isMatchCase()));
                break;
            case "!=":
                filters.add(ff.notEqual(property, ff.literal(option.getValue()), option.isMatchCase()));
                break;
            }

        }

        try
        {
            Filter filter = ff.and(filters);

            LOGGER.debug("Filter: {}", filter.toString());

            return client.search(filter, 1);

        }
        catch (CoalesceException e)
        {
            throw new RuntimeException(e);
        }

    }
}
