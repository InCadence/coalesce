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
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.ArrayHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.DefaultNormalizer;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.search.jobs.CoalesceSearchJob;
import org.geotools.data.Query;
import org.geotools.filter.Capabilities;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.PropertyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * This extension exposes the {@link ICoalesceSearchPersistor} interface.
 *
 * @author Derek Clemenzi
 */
public class CoalesceSearchFramework extends CoalesceFramework {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoalesceSearchFramework.class);
    private static final FilterFactory2 FF = CoalescePropertyFactory.getFilterFactory();

    private final Map<String, ICoalesceSearchPersistor> mapping = new HashMap<>();

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
     * @param service used for executing jobs / tasks
     */
    public CoalesceSearchFramework(ExecutorService service)
    {
        super(service);
    }

    /**
     * Defines explicit mappings by template names
     */
    public void setMapping(Map<String, ICoalesceSearchPersistor> mapping)
    {
        DefaultNormalizer normalizer = new DefaultNormalizer();

        this.mapping.clear();

        for (Map.Entry<String, ICoalesceSearchPersistor> entry : mapping.entrySet())
        {
            this.mapping.put(normalizer.normalize(entry.getKey()), entry.getValue());
        }
    }

    /**
     * Executes a singular query
     *
     * @param query to execute
     * @return the results of executing the provided query.
     * @throws CoalescePersistorException on error
     */
    public SearchResults search(Query query) throws CoalescePersistorException, InterruptedException
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
            throws CoalescePersistorException, InterruptedException
    {
        List<SearchResults> results;

        ICoalesceSearchPersistor persistor = getSearchPersistor(requirements);

        if (persistor != null)
        {
            LOGGER.debug("Selected {} for Query Execution", persistor.getClass().getSimpleName());

            CoalesceSearchJob job = new CoalesceSearchJob(Arrays.asList(queries));
            job.setTarget(persistor);
            job.setMappings(mapping);
            job.setExecutor(this);

            ICoalesceResponseType<List<SearchResults>> response = job.call();

            if (response.getStatus() != EResultStatus.SUCCESS)
            {
                if (response.getStatus() == EResultStatus.INTERRUPTED)
                {
                    throw new InterruptedException(response.getError());
                }
                else
                {
                    throw new CoalescePersistorException(response.getError());
                }
            }
            else
            {
                results = response.getResult();
            }
        }
        else
        {
            results = new ArrayList<>();

            for (Query ignored : queries)
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
     * @param id entity ID that uniquely identifies an entity
     * @return the key if the entity is found matching the id and name; otherwise <code>null</code>
     * @throws CoalesceException on error
     */
    public String findEntityId(String id) throws CoalesceException, InterruptedException
    {
        return findEntityId(id, null);
    }

    /**
     * @param id   entity ID that uniquely identifies an entity
     * @param name of the entities template.
     * @return the key if the entity is found matching the id and name; otherwise <code>null</code>
     * @throws CoalesceException on error
     */
    public String findEntityId(String id, String name) throws CoalesceException, InterruptedException
    {
        List<org.opengis.filter.Filter> filters = new ArrayList<>();
        filters.add(createEquals(CoalescePropertyFactory.getEntityId(), id));

        if (name != null)
        {
            filters.add(createEquals(CoalescePropertyFactory.getName(), name));
        }

        return find(FF.and(filters));
    }

    /**
     * @param filter that uniquely identifies an entity.
     * @return the key of the entity matching the filter.
     * @throws CoalesceException on error
     */
    public String find(Filter filter) throws CoalesceException, InterruptedException
    {
        FindDetails results = find(filter, new String[0]);
        return results != null ? results.getKey() : null;
    }

    /**
     * @param filter that uniquely identifies an entity.
     * @return the key along with specified properties of the entity matching the filter.
     * @throws CoalesceException on error
     */
    public FindDetails find(Filter filter, String... properties) throws CoalesceException, InterruptedException
    {
        FindDetails result = null;

        LOGGER.debug("Executing: {}", filter.toString());

        Query query = new Query();
        query.setFilter(filter);

        if (!ArrayHelper.isNullOrEmpty(properties))
        {
            query.setPropertyNames(properties);
        }

        SearchResults results = search(query);

        if (results.getStatus() == EResultStatus.SUCCESS)
        {
            try (CachedRowSet rowset = results.getResults())
            {
                if (rowset.size() > 1)
                {
                    LOGGER.warn(
                            "{} Matches found for {}; may want to reconsider the datamodel or this query for performance.",
                            rowset.size(),
                            filter.toString());
                }

                if (rowset.first())
                {
                    int idx = 1;

                    result = new FindDetails();
                    result.setKey(rowset.getString(idx++));

                    if (properties != null)
                    {
                        for (String property : properties)
                        {
                            result.put(property, rowset.getString(idx++));
                        }
                    }
                }
            }
            catch (SQLException e)
            {
                throw new CoalesceException(e);
            }
        }
        else
        {
            throw new CoalesceException("Duplication Check Failed: " + results.getError());
        }

        return result;
    }

    private static Filter createEquals(PropertyName name, String value)
    {
        Filter filter;
        if (StringHelper.isNullOrEmpty(value))
        {
            filter = FF.or(FF.equals(name, FF.literal(value)), FF.isNull(name));
        }
        else
        {
            filter = FF.equals(name, FF.literal(value));
        }

        return filter;
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
            for (ICoalescePersistor secondary : getSecondaryPersistors(capabilities))
            {
                if (secondary instanceof ICoalesceSearchPersistor)
                {
                    result = (ICoalesceSearchPersistor) secondary;
                    break;
                }
            }
        }

        return result;
    }

}
