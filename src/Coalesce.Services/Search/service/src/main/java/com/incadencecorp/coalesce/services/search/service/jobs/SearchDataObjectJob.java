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

package com.incadencecorp.coalesce.services.search.service.jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.incadencecorp.coalesce.framework.tasks.AbstractFrameworkTask;
import com.incadencecorp.coalesce.services.api.search.QueryResultsType;
import com.incadencecorp.coalesce.services.api.search.QueryType;
import com.incadencecorp.coalesce.services.api.search.SearchDataObjectRequest;
import com.incadencecorp.coalesce.services.api.search.SearchDataObjectResponse;
import com.incadencecorp.coalesce.services.common.jobs.AbstractServiceJob;
import com.incadencecorp.coalesce.services.search.service.tasks.SearchDataObjectTask;

public class SearchDataObjectJob
        extends AbstractServiceJob<SearchDataObjectRequest, SearchDataObjectResponse, QueryResultsType> {

    public SearchDataObjectJob(SearchDataObjectRequest request)
    {
        super(request);
    }

    @Override
    protected Collection<AbstractFrameworkTask<?, QueryResultsType>> getTasks(SearchDataObjectRequest params)
    {
        List<AbstractFrameworkTask<?, QueryResultsType>> tasks = new ArrayList<AbstractFrameworkTask<?, QueryResultsType>>();

        for (QueryType query : params.getQuery())
        {
            SearchDataObjectTask task = new SearchDataObjectTask();
            task.setParams(query);

            tasks.add(task);
        }

        return tasks;
    }

    @Override
    protected SearchDataObjectResponse createResponse()
    {
        return new SearchDataObjectResponse();
    }

    @Override
    protected QueryResultsType createResults()
    {
        return new QueryResultsType();
    }

}
