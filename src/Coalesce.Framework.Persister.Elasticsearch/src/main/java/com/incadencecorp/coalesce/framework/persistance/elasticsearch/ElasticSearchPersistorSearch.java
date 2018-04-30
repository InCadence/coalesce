package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.support.AbstractClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.geotools.data.Query;
import org.geotools.filter.Capabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;

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
        CachedRowSet rowset = null;

        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            rowset = RowSetProvider.newFactory().createCachedRowSet();
            AbstractClient client = conn.getDBConnector(params);
            SearchResponse response = client.prepareSearch("gdelt_data").setQuery(QueryBuilders.termQuery("GlobalEventID",
                                                                                                          "410479387"))                 // Query
                    //.setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
                    //.setFrom(0).setSize(60).setExplain(true)
                    .get();

            LOGGER.debug(response.toString());
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage());
            //throw new CoalescePersistorException(e.getMessage(), e);
        }
        // TODO Not Implemented
        //query.getFilter().toString();
        //query.getAlias();

        //QueryBuilder qb = QueryBuilders.matchQuery(
        //		"GlobalEventID",
        //		"410479387");

        SearchResults queryResults = new SearchResults();
        queryResults.setResults(rowset);
        return queryResults;
    }

}
