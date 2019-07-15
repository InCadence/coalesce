/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.services.search.service.data.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.datamodel.api.record.ISearchQueryRecord;
import com.incadencecorp.coalesce.datamodel.impl.coalesce.entity.SearchQueryCoalesceEntity;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.services.crud.service.data.controllers.AbstractObjectController;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchGroup;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchQuery;
import com.incadencecorp.coalesce.services.search.service.data.model.SearchQueryDetails;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author Derek Clemenzi
 */
public class SearchQueryController extends AbstractObjectController<SearchQueryDetails, SearchQueryCoalesceEntity> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public SearchQueryController(CoalesceFramework framework)
    {
        super(framework);
    }

    @Override
    protected SearchQueryCoalesceEntity createEntity()
    {
        return new SearchQueryCoalesceEntity();
    }

    @Override
    protected SearchQueryCoalesceEntity toCoalesce(SearchQueryCoalesceEntity entity, SearchQueryDetails object)
            throws CoalesceException
    {
        SearchQuery query = object.getQuery();

        ISearchQueryRecord record = entity.getSearchQueryRecord();
        record.setCql(query.getCql());
        record.setIndextype(query.getType());
        record.setPageSize(query.getPageSize());
        record.setPropertyNames(query.getPropertyNames().toArray(new String[0]));
        record.setCapabilities(query.getCapabilities().stream().map(Enum::toString).toArray(String[]::new));
        record.setSaved(!StringHelper.isNullOrEmpty(object.getTitle()));

        try
        {
            record.setSortBy(MAPPER.writeValueAsString(query.getSortBy()));
            record.setCriteria(MAPPER.writeValueAsString(query.getGroup()));
        }
        catch (JsonProcessingException e)
        {
            throw new CoalesceDataFormatException("(FAILED) Parsing JSON", e);
        }

        if (!StringHelper.isNullOrEmpty(object.getTitle()))
        {
            entity.setTitle(object.getTitle());
        }

        return entity;
    }

    @Override
    protected SearchQueryDetails fromCoalesce(SearchQueryCoalesceEntity entity) throws CoalesceDataFormatException
    {
        SearchQueryDetails result = new SearchQueryDetails();

        ISearchQueryRecord record = entity.getSearchQueryRecord();

        SearchQuery query = new SearchQuery();

        query.setKey(entity.getKey());
        query.setCql(record.getCql());
        query.setType(record.getIndextype());
        query.setPageSize(record.getPageSize());
        query.setPropertyNames(Arrays.asList(record.getPropertyNames()));
        //query.setCapabilities(record.getCapabilities());

        try
        {
            //query.setSortBy(MAPPER.readValue(record.getSortBy(), List.class));
            query.setGroup(MAPPER.readValue(record.getCriteria(), SearchGroup.class));
        }
        catch (IOException e)
        {
            throw new CoalesceDataFormatException("(FAILED) Parsing JSON", e);
        }

        result.setQuery(query);
        result.setTitle(entity.getTitle());
        result.setLastModified(entity.getLastModified());

        return result;
    }
}
