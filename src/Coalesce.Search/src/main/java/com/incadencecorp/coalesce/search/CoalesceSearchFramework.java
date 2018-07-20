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

package com.incadencecorp.coalesce.search;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.api.ICoalesceResponseType;
import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.jobs.CoalesceSearchJob;
import org.geotools.data.Query;
import org.geotools.filter.Capabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * This extension exposes the {@link ICoalesceSearchPersistor} interface.
 *
 * @author Derek Clemenzi
 */
public class CoalesceSearchFramework extends CoalesceFramework {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoalesceSearchFramework.class);

    /**
     * Creates this framework with the default ThreadPoolExecutor based on
     * {@link com.incadencecorp.coalesce.framework.CoalesceSettings}.
     */
    public CoalesceSearchFramework()
    {
        this(null);
    }

    /**
     * Creates this framework with the provided executor service.
     *
     * @param service
     */
    public CoalesceSearchFramework(ExecutorService service)
    {
        super(service);
    }

    /**
     * Executes a singular query
     *
     * @param query to execute
     * @return the results of executing the provided query.
     * @throws CoalescePersistorException on error
     */
    public SearchResults search(Query query) throws CoalescePersistorException
    {
        return searchBulk(EnumSet.of(EPersistorCapabilities.SEARCH), query).get(0);
    }

    /**
     * Executes multiple queries in parallel.
     *
     * @param queries to execute
     * @return the results of executing the provided query.
     * @throws CoalescePersistorException on error
     */
    public List<SearchResults> searchBulk(EnumSet<EPersistorCapabilities> requirements, Query... queries)
            throws CoalescePersistorException
    {
        List<SearchResults> results;

        ICoalesceSearchPersistor persistor = getSearchPersistor(requirements);

        if (persistor != null)
        {
            LOGGER.debug("Selected {} for Query Execution", persistor.getClass().getSimpleName());

            CoalesceSearchJob job = new CoalesceSearchJob(Arrays.asList(queries));
            job.setTarget(persistor);
            job.setExecutor(this);

            ICoalesceResponseType<List<SearchResults>> response = job.call();

            if (response.getStatus() != EResultStatus.SUCCESS)
            {
                throw new CoalescePersistorException(response.getError());
            }
            else
            {
                results = response.getResult();
            }
        }
        else
        {
            results = new ArrayList<>();

            for (int ii = 0; ii < queries.length; ii++)
            {
                SearchResults result = new SearchResults();
                result.setStatus(EResultStatus.FAILED);
                result.setError(String.format(CoalesceErrors.NOT_FOUND,
                                              ICoalesceSearchPersistor.class.getSimpleName(),
                                              requirements));
                results.add(result);
            }
        }

        return results;
    }

    /**
     * @return the capabilities of the first persister defined that supports searching.
     */
    public Capabilities getCapabilities()
    {
        ICoalesceSearchPersistor persistor = getSearchPersistor(EnumSet.of(EPersistorCapabilities.SEARCH));

        return (persistor == null) ? new Capabilities() : persistor.getSearchCapabilities();
    }

    /**
     * @return the authoritative persistor if it implements {@link ICoalesceSearchPersistor} or the first secondary persistor that does if available.
     */
    private ICoalesceSearchPersistor getSearchPersistor(EnumSet<EPersistorCapabilities> capabilities)
    {
        ICoalesceSearchPersistor result = null;
        ICoalescePersistor persistor = getAuthoritativePersistor();

        if (persistor instanceof ICoalesceSearchPersistor && persistor.getCapabilities().containsAll(capabilities))
        {
            result = (ICoalesceSearchPersistor) persistor;
        }
        else
        {
            for (ICoalescePersistor secondary : getSecondaryPersistors())
            {
                if (secondary instanceof ICoalesceSearchPersistor && secondary.getCapabilities().containsAll(capabilities))
                {
                    result = (ICoalesceSearchPersistor) secondary;
                    break;
                }
            }
        }

        return result;
    }

}
