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

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.tasks.AbstractTask;
import com.incadencecorp.coalesce.framework.tasks.TaskParameters;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import org.geotools.data.Query;

import java.util.HashMap;
import java.util.Map;

/**
 * This task calls {@link ICoalesceSearchPersistor#search(Query)} and returns the results.
 *
 * @author Derek Clemenzi
 */
public class CoalesceSearchTask extends AbstractTask<Query, SearchResults, ICoalesceSearchPersistor> {

    @Override
    protected SearchResults doWork(TaskParameters<ICoalesceSearchPersistor, Query> parameters) throws CoalesceException
    {
        return parameters.getTarget().search(parameters.getParams());
    }

    @Override
    protected Map<String, String> getParameters(Query params, boolean isTrace)
    {
        return new HashMap<>();
    }

    @Override
    protected SearchResults createResult()
    {
        return new SearchResults();
    }
}
