package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.mapper.impl.JavaMapperImpl;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.search.resultset.CoalesceResultSet;
import mil.nga.giat.data.elasticsearch.ElasticDataStoreFactory;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.support.AbstractClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.Capabilities;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.expression.PropertyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class ElasticSearchPersistorSearch extends ElasticSearchPersistor implements ICoalesceSearchPersistor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchPersistorSearch.class);
    private static final ElasticSearchMapperImpl MAPPER_TYPE = new ElasticSearchMapperImpl();
    private static final JavaMapperImpl MAPPER_JAVA = new JavaMapperImpl();

    /**
     * Default constructor using {@link ElasticSearchSettings} for configuration
     */
    public ElasticSearchPersistorSearch()
    {
        super();
    }

    /**
     * @param params configuration.
     */
    public ElasticSearchPersistorSearch(Map<String, String> params)
    {
        super(params);
    }

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
        try
        {
            if (LOGGER.isDebugEnabled())
            {
                Iterator<DataStoreFactorySpi> availableStores = DataStoreFinder.getAvailableDataStores();

                LOGGER.debug("List Available Stores:");
                while (availableStores.hasNext())
                {
                    LOGGER.debug("\t{}", availableStores.next().toString());
                }

            }

            Map<String, String> props = new HashMap<>();
            // TODO Pull host ane port from params. Params stores them as host:port so this will need to be separated in the properties or parsed out.
            props.put(ElasticDataStoreFactory.HOSTNAME.key, "localhost");
            props.put(ElasticDataStoreFactory.HOSTPORT.key, "9200");
            // TODO Use the index name specified within the query. If multiple are specified this would require a join.
            props.put(ElasticDataStoreFactory.INDEX_NAME.key, "coalesce-unit_test");

            // TODO This should only be done once when the properties are set.
            DataStore datastore = DataStoreFinder.getDataStore(props);

            // TODO Remove this
            LOGGER.info(datastore.getClass().getSimpleName());

            // TODO If INDEX_NAME is 'coalesce' then the typeName = 'entity' otherwise 'recordset'
            query.setTypeName("recordset");
            SimpleFeatureSource featureSource = datastore.getFeatureSource("recordset");

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

            LOGGER.debug("Doing this search: " + query.toString());

            Map<String, ECoalesceFieldDataTypes> types = CoalesceTemplateUtil.getDataTypes();

            // Normalize Column Headers
            String[] columnList = new String[properties.size()];
            for (int i = 0; i < properties.size(); i++)
            {
                ECoalesceFieldDataTypes type = types.get(properties.get(i).getPropertyName());

                if (type == null)
                {
                    type = ECoalesceFieldDataTypes.STRING_TYPE;

                }

                LOGGER.debug("Property: {} Type: {}", properties.get(i).getPropertyName(), type);

                columnList[i] = properties.get(i).getPropertyName();
            }

            SearchResults results = new SearchResults();
            CachedRowSet rowset;
            int total = 0;

            // TODO Populate the rowset
            try (FeatureIterator<SimpleFeature> featureItr = featureSource.getFeatures(query.getFilter()).features())
            {
                if (featureItr.hasNext())
                {
                    SimpleFeature feature = featureItr.next();

                    while (featureItr.hasNext())
                    {
                        LOGGER.info("*** MATCH FOUND *** " + feature.getID());

                        //Put the Feature in the SearchResults
                        Iterator<Object[]> columnIterator = new FeatureColumnIterator(featureItr, properties);
                        CoalesceResultSet resultSet = new CoalesceResultSet(columnIterator, columnList);
                        rowset = RowSetProvider.newFactory().createCachedRowSet();
                        rowset.populate(resultSet);

                        results.setResult(rowset);

                        total++;
                        featureItr.next();
                    }

                }
                else
                {
                    LOGGER.info("No match found");
                }
            }
            results.setTotal(total);

            return results;
        }
        catch (IOException e)
        {
            throw new CoalescePersistorException(e.getMessage(), e);
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException(e.getMessage(), e);
        }
    }

}
