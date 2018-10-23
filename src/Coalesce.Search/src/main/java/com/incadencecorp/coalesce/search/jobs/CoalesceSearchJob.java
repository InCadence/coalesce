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

package com.incadencecorp.coalesce.search.jobs;

import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.jobs.AbstractCoalesceTargetJob;
import com.incadencecorp.coalesce.framework.jobs.responses.CoalesceResponseType;
import com.incadencecorp.coalesce.framework.tasks.AbstractTask;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import org.geotools.data.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This implementation creates {@link CoalesceSearchTask} for each {@link Query} specified and executes them in parallel.
 *
 * @author Derek Clemenzi
 */
public class CoalesceSearchJob extends
        AbstractCoalesceTargetJob<Collection<Query>, ICoalesceResponseType<List<SearchResults>>, SearchResults, ICoalesceSearchPersistor> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoalesceSearchJob.class);

    private final Map<String, ICoalesceSearchPersistor> mapping = new HashMap<>();
    private final QueryValidator checker = new QueryValidator();

    public CoalesceSearchJob(Collection<Query> query)
    {
        super(query);
    }

    /**
     * Defines explicit mappings by template names
     */
    public void setMappings(Map<String, ICoalesceSearchPersistor> mapping)
    {
        this.mapping.clear();
        this.mapping.putAll(mapping);
    }

    @Override
    protected Collection<AbstractTask<?, SearchResults, ICoalesceSearchPersistor>> getTasks(Collection<Query> params)
            throws CoalesceException
    {
        List<AbstractTask<?, SearchResults, ICoalesceSearchPersistor>> tasks = new ArrayList<>();

        for (Query query : params)
        {
            // Determine referenced templates
            Set<String> templates = checker.getTemplateNames(query);

            if (templates.iterator().hasNext())
            {
                // Accept the first template
                query.setTypeName(templates.iterator().next());
            }

            if (templates.size() > 1)
            {
                LOGGER.warn("Multiple templates were specified by user's query: {}", templates);
            }

            CoalesceSearchTask task = new CoalesceSearchTask();
            task.setParams(query);
            task.setTarget(mapping.get(query.getTypeName()));
            tasks.add(task);

            if (LOGGER.isDebugEnabled())
            {
                if (mapping.containsKey(query.getTypeName()))
                {
                    LOGGER.debug("({}) is mapped to ({})",
                                 query.getTypeName(),
                                 mapping.get(query.getTypeName()).getClass().getSimpleName());
                }
                else
                {
                    LOGGER.debug("({}) has no persister mapping", query.getTypeName());
                }
            }
        }

        return tasks;
    }

    @Override
    protected ICoalesceResponseType<List<SearchResults>> createResponse()
    {
        ICoalesceResponseType<List<SearchResults>> response = new CoalesceResponseType<>();
        response.setResult(new ArrayList<>());

        return response;
    }

    @Override
    protected SearchResults createResults()
    {
        return new SearchResults();
    }
}
