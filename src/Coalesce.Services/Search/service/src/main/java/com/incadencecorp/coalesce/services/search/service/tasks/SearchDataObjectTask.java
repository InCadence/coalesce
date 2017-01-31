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

package com.incadencecorp.coalesce.services.search.service.tasks;

import java.util.Map;

import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.tasks.AbstractFrameworkTask;
import com.incadencecorp.coalesce.framework.tasks.TaskParameters;
import com.incadencecorp.coalesce.services.api.search.QueryResultsType;
import com.incadencecorp.coalesce.services.api.search.QueryType;

public class SearchDataObjectTask extends AbstractFrameworkTask<QueryType, QueryResultsType> {

    @Override
    protected QueryResultsType doWork(TaskParameters<CoalesceFramework, QueryType> parameters)
    {
        // TODO Not Implemented
        return null;
    }

    @Override
    protected Map<String, String> getParameters(QueryType params, boolean isTrace)
    {
        // TODO Not Implemented
        return null;
    }

    @Override
    protected QueryResultsType createResult()
    {
        return new QueryResultsType();
    }
}
