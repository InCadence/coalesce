package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.support.AbstractClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.Capabilities;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.expression.PropertyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.persistance.accumulo.AccumuloQueryRewriter2;
import com.incadencecorp.coalesce.framework.persistance.accumulo.FeatureColumnIterator;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.search.resultset.CoalesceColumnMetadata;
import com.incadencecorp.coalesce.search.resultset.CoalesceResultSet;

public class ElasticSearchPersistorSearch extends ElasticSearchPersistor implements ICoalesceSearchPersistor {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchPersistorSearch.class);

	@Override
    public Capabilities getSearchCapabilities()
    {
        Capabilities capability = new Capabilities();
        capability.addAll(Capabilities.SIMPLE_COMPARISONS);
        capability.addAll(Capabilities.LOGICAL);

        return capability;
    }
	
	public SearchResponse searchAll()
    {
        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            AbstractClient client = conn.getDBConnector(params);
            QueryBuilder qb = QueryBuilders.matchAllQuery();
            SearchResponse response = client.prepareSearch().setQuery(qb).get();
            //.execute()
            //.actionGet();
            LOGGER.debug(response.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void searchSpecific(String searchValue, String searchType)
    {

        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            AbstractClient client = conn.getDBConnector(params);
            QueryBuilder qb = QueryBuilders.matchAllQuery();
            //QueryBuilder qb = QueryBuilders.matchPhraseQuery("PMESIIPTMilitary", "1");
            SearchResponse response = client.prepareSearch(searchValue)
                    //.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(qb)                 // Query
                    //.setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
                    //.setFrom(0).setSize(60).setExplain(true)
                    .get();

            LOGGER.debug(response.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void searchSpecificWithFilter(String searchValue, String searchType, String filterName, String filterValue)
    {

        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            AbstractClient client = conn.getDBConnector(params);
            QueryBuilder qb = QueryBuilders.matchPhraseQuery(filterName, filterValue);
            SearchResponse response = client.prepareSearch(searchValue)
                    //.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(qb)                 // Query
                    //.setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
                    //.setFrom(0).setSize(60).setExplain(true)
                    .get();

            LOGGER.debug(response.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void searchElasticGeo()
    {

        //FilterToElastic filterElastic = new FilterToElastic();

        //Map<String, Object> queryBuilder = filterElastic.getNativeQueryBuilder();

        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            AbstractClient client = conn.getDBConnector(params);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public SearchResults search(Query query) throws CoalescePersistorException
    {

        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            AbstractClient client = conn.getDBConnector(params);
    	
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
	
	        CachedRowSet rowset;
	        int total;
	
	        LOGGER.debug("Executing search against schema: {}", query.getTypeName());
	
            // Get Feature Store
            SimpleFeatureStore featureSource = (SimpleFeatureStore) geoDataStore.getFeatureSource(localquery.getTypeName());

            Map<String, ECoalesceFieldDataTypes> types = CoalesceTemplateUtil.getDataTypes();

            // Normalize Column Headers
            List<CoalesceColumnMetadata> columnList = new ArrayList<>();
            for (PropertyName entry : properties)
            {
                ECoalesceFieldDataTypes type = types.get(entry.getPropertyName());

                if (type == null)
                {
                    type = ECoalesceFieldDataTypes.STRING_TYPE;

                }

                LOGGER.debug("Property: {} Type: {}", entry.getPropertyName(), type);

                    columnList.add(new CoalesceColumnMetadata(CoalescePropertyFactory.getColumnName(entry.getPropertyName()),
                                                              MAPPER_JAVA.map(type).getTypeName(),
                                                              MAPPER_TYPE.map(type)));
                }

            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Max Features: {}", localquery.getMaxFeatures());
                LOGGER.debug(localquery.toString());
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

            LOGGER.debug("Search Hits: {}", rowset.size());

            // Results > Page Size; Determine total size
            if (total >= query.getMaxFeatures() && isReturnTotalsEnabled())
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
	            catch (IOException e)
	            {
	                throw new CoalescePersistorException(e.getMessage(), e);
	            }

                LOGGER.debug("Search Total: {}", total);
            }
        }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }

        // Create Results
        SearchResults results = new SearchResults();
        results.setResults(rowset);
        results.setTotal(total);

        return results;
    }

}
