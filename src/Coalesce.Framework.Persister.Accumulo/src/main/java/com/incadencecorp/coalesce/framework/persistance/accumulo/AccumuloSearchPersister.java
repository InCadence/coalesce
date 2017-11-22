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

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.search.resultset.CoalesceColumnMetadata;
import com.incadencecorp.coalesce.search.resultset.CoalesceResultSet;
import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
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

/**
 * @author Derek Clemenzi
 */
public class AccumuloSearchPersister extends AccumuloPersister2 implements ICoalesceSearchPersistor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloSearchPersister.class);

    public AccumuloSearchPersister(Map<String, String> params)
    {
        super(params);
    }

    @Override
    public SearchResults search(Query query) throws CoalescePersistorException
    {
        // Make sure objectkey is returned UNLESS the user specifically
        // specified no properties. - Which is done by an empty props array
        // objectkey is always the first parameter
        AccumuloQueryRewriter2 nameChanger = new AccumuloQueryRewriter2(getNormalizer());

        CachedRowSet rowset = null;
        SimpleFeatureCollection featureCount;
        DataStore geoDataStore = getDataConnector().getGeoDataStore();
        // Make a copy of the query
        Query localquery = nameChanger.rewrite(query);

        LOGGER.debug("Executing search against schema: {}", localquery.getTypeName());
        try
        {

            SimpleFeatureStore featureSource = (SimpleFeatureStore) geoDataStore.getFeatureSource(localquery.getTypeName());

            List<CoalesceColumnMetadata> columnList = new ArrayList<>();
            // Not needed as the Coalesce behavior does not support just get all Columns
            // Check if no properties were defined
            //            if (localquery.retrieveAllProperties()) {
            //                for (PropertyDescriptor entry : featureSource.getSchema().getDescriptors())
            //                {
            //                	String columnName = entry.getName().getLocalPart();
            //                    CoalesceColumnMetadata columnMetadata = new CoalesceColumnMetadata(columnName, "String", Types.VARCHAR);
            //                    columnList.add(columnMetadata);
            //                }
            //
            //            } else {
            // TODO - Why always String and VARCHAR.  Should these not be the real types
            // Use the original property names to populate the Rowset
            // ALSO NO DOTS SEPARATING THE TABLE FROM COLUMN
            if (localquery.getProperties() != null)
            {
                for (PropertyName entry : localquery.getProperties())
                {
                    //String columnName = entry.getPropertyName().replaceAll("[.]", "");
                    CoalesceColumnMetadata columnMetadata = new CoalesceColumnMetadata(entry.getPropertyName(),
                                                                                       "String",
                                                                                       Types.VARCHAR);
                    columnList.add(columnMetadata);
                }
            }

            // Need to get a count of the query without limitations to support the paging
            // Do no properties or sort, just the rewritten filter.
            Query countQuery = new Query(localquery.getTypeName());
            countQuery.setFilter(localquery.getFilter());
            countQuery.setProperties(Query.NO_PROPERTIES);

            featureCount = featureSource.getFeatures(countQuery);
            FeatureIterator<SimpleFeature> featureItr = featureSource.getFeatures(localquery).features();
            Iterator<Object[]> columnIterator = new FeatureColumnIterator<Object[]>(featureItr);

            CoalesceResultSet resultSet = new CoalesceResultSet(columnIterator, columnList);

            rowset = RowSetProvider.newFactory().createCachedRowSet();
            rowset.populate(resultSet);
            featureItr.close();
        }
        catch (IOException | SQLException e)
        {
            throw new CoalescePersistorException(e.getMessage(), e);
        }

        SearchResults results = new SearchResults();

        results.setResults(rowset);
        results.setTotal(featureCount.size());

        return results;
    }
}
