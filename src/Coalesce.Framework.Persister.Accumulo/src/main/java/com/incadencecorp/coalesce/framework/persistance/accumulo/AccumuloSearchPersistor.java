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

package com.incadencecorp.coalesce.framework.persistance.accumulo;

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.jobs.metrics.StopWatch;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.search.resultset.CoalesceColumnMetadata;
import com.incadencecorp.coalesce.search.resultset.CoalesceResultSet;
import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.expression.PropertyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * @author Derek Clemenzi
 */
public class AccumuloSearchPersistor extends AccumuloPersistor2 implements ICoalesceSearchPersistor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloSearchPersistor.class);

    /**
     * Default constructor using {@link AccumuloSettings} for configuration
     *
     * @throws CoalescePersistorException
     */
    public AccumuloSearchPersistor() throws CoalescePersistorException
    {
        super(AccumuloSettings.getParameters());
    }

    /**
     * Default Constructor using a default {@link ExecutorService}
     *
     * @param params Configuration parameters
     */
    public AccumuloSearchPersistor(Map<String, String> params)
    {
        super(params);
    }

    /**
     * Specify an external {@link ExecutorService} to use for internal threads.
     *
     * @param service Service pool used for executing internal task in parallel.
     * @param params  Configuration parameters
     */
    public AccumuloSearchPersistor(ExecutorService service, Map<String, String> params)
    {
        super(service, params);
    }

    @Override
    public SearchResults search(Query query) throws CoalescePersistorException
    {
        // Ensure Entity Key is the first parameter
        List<PropertyName> properties = new ArrayList<>();

        if (query.getProperties() != null)
        {
            properties.addAll(query.getProperties());
        }

        if (properties.size() == 0
                || !properties.get(0).getPropertyName().equalsIgnoreCase(CoalescePropertyFactory.getEntityKey().getPropertyName()))
        {
            properties.add(0, CoalescePropertyFactory.getEntityKey());
        }

        query.setProperties(properties);

        CachedRowSet rowset = null;
        int total = 0;

        // Re-write query parameters
        AccumuloQueryRewriter2 nameChanger = new AccumuloQueryRewriter2(getNormalizer());
        Query localquery = nameChanger.rewrite(query);

        LOGGER.debug("Executing search against schema: {}", localquery.getTypeName());

        try
        {
            // Get Feature Store
            DataStore geoDataStore = getDataConnector().getGeoDataStore();
            SimpleFeatureStore featureSource = (SimpleFeatureStore) geoDataStore.getFeatureSource(localquery.getTypeName());

            // Normalize Column Headers
            List<CoalesceColumnMetadata> columnList = new ArrayList<>();
            for (PropertyName entry : properties)
            {
                // TODO - Why always String and VARCHAR.  Should these not be the real types
                columnList.add(new CoalesceColumnMetadata(CoalescePropertyFactory.getColumnName(entry.getPropertyName()),
                                                          "String",
                                                          Types.VARCHAR));
            }

            // Execute Query
            try (FeatureIterator<SimpleFeature> featureItr = featureSource.getFeatures(localquery).features())
            {
                Iterator<Object[]> columnIterator = new FeatureColumnIterator(featureItr, properties);
                CoalesceResultSet resultSet = new CoalesceResultSet(columnIterator, columnList);
                rowset = RowSetProvider.newFactory().createCachedRowSet();
                rowset.populate(resultSet);

                total = rowset.size();
            }
            catch (SQLException e)
            {
                throw new CoalescePersistorException(e.getMessage(), e);
            }

            // Results > Page Size; Determine total size
            if (total >= query.getMaxFeatures())
            {
                localquery.setMaxFeatures(Query.DEFAULT_MAX);
                localquery.setProperties(Query.NO_PROPERTIES);

                try (FeatureIterator<SimpleFeature> featureItr = featureSource.getFeatures(localquery).features())
                {
                    total = 0;

                    while (featureItr.hasNext())
                    {
                        total++;
                        featureItr.next();
                    }
                }
            }
        }
        catch (IOException e)
        {
            throw new CoalescePersistorException(e.getMessage(), e);
        }

        // Create Results
        SearchResults results = new SearchResults();
        results.setResults(rowset);
        results.setTotal(total);

        return results;
    }

    @Override
    public EnumSet<EPersistorCapabilities> getCapabilities()
    {
        EnumSet<EPersistorCapabilities> capabilities = super.getCapabilities();
        capabilities.addAll(EnumSet.of(EPersistorCapabilities.GEOSPATIAL_SEARCH,
                                       EPersistorCapabilities.TEMPORAL_SEARCH,
                                       EPersistorCapabilities.SEARCH));

        return capabilities;
    }
}
