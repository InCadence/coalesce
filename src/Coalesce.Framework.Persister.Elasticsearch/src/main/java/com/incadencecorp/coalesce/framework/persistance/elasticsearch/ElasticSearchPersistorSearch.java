package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.search.resultset.CoalesceResultSet;
import mil.nga.giat.data.elasticsearch.ElasticDataStoreFactory;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.Capabilities;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.expression.PropertyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ElasticSearchPersistorSearch extends ElasticSearchPersistor implements ICoalesceSearchPersistor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchPersistorSearch.class);
    private final Map<String, DataStore> datastores = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> keywordCache = new HashMap<>();

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

    /**
     * @return a set of fields that are not analyzed for the base indexes indicating that they are keywords.
     */
    private Set<String> getKeywords(String index) throws CoalescePersistorException
    {
        LOGGER.debug("Loading keywords for index: {}", index);

        if (!keywordCache.containsKey(index))
        {
            Set<String> keywords = new HashSet<>();

            switch (index)
            {
            case ElasticSearchPersistor.COALESCE_ENTITY_INDEX:
                keywords.addAll(getKeywords(ElasticSearchPersistor.COALESCE_ENTITY_INDEX,
                                            ElasticSearchPersistor.COALESCE_ENTITY,
                                            CoalescePropertyFactory.COALESCE_ENTITY_TABLE));
                break;
            case ElasticSearchPersistor.COALESCE_LINKAGE_INDEX:
                keywords.addAll(getKeywords(ElasticSearchPersistor.COALESCE_LINKAGE_INDEX,
                                            ElasticSearchPersistor.COALESCE_LINKAGE,
                                            CoalescePropertyFactory.COALESCE_LINKAGE_TABLE,
                                            CoalescePropertyFactory.COALESCE_ENTITY_TABLE));
                break;
            default:
                keywords.addAll(getKeywords(index, "recordset", CoalescePropertyFactory.COALESCE_ENTITY_TABLE));
                break;
            }

            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("{} Keyword Fields: ({})", index, String.join("\n", keywords));
            }

            keywordCache.put(index, keywords);
        }

        return keywordCache.get(index);
    }

    /**
     * @return a set of fields that are not analyzed indicating that they are keywords.
     */
    private Set<String> getKeywords(String index, String type, String... prefixes) throws CoalescePersistorException
    {
        Set<String> keywords = new HashSet<>();

        try
        {
            DataStore datastore = getDataStore(index);
            SimpleFeatureType feature = datastore.getSchema(type);

            for (AttributeDescriptor attr : feature.getAttributeDescriptors())
            {
                for (String prefix : prefixes)
                {
                    if (attr.getLocalName().startsWith(prefix))
                    {
                        Object value = attr.getUserData().getOrDefault("analyzed", false);

                        if (value instanceof Boolean && !((Boolean) value))
                        {
                            keywords.add(attr.getLocalName());
                        }
                    }
                }
            }
        }
        catch (IOException | RuntimeException e)
        {
            throw new CoalescePersistorException(e.getMessage(), e);
        }

        return keywords;
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

        try
        {
            ElasticSearchQueryRewriter rewriter = new ElasticSearchQueryRewriter();

            String featureType = rewriter.getFeatureType(query);
            rewriter.setKeywords(getKeywords(featureType));

            Query localQuery = rewriter.rewrite(query);

            String typeName;

            switch (localQuery.getTypeName())
            {
            case ElasticSearchPersistor.COALESCE_ENTITY_INDEX:
                typeName = ElasticSearchPersistor.COALESCE_ENTITY;
                break;
            case ElasticSearchPersistor.COALESCE_LINKAGE_INDEX:
                typeName = CoalesceLinkage.NAME;
                break;
            default:
                typeName = "recordset";
                break;
            }

            //The datastore factory needs an index in order to find a matching store
            SearchResults results = new SearchResults();
            DataStore datastore = getDataStore(localQuery.getTypeName());

            try
            {
                SimpleFeatureSource featureSource = datastore.getFeatureSource(typeName);

                LOGGER.debug("Doing this search: " + localQuery.toString());

                // Normalize Column Headers
                String[] columnList = new String[properties.size()];
                for (int i = 0; i < properties.size(); i++)
                {
                    String property = properties.get(i).getPropertyName();

                    if (!CoalescePropertyFactory.isRecordPropertyName(property))
                    {
                        ECoalesceFieldDataTypes type = CoalesceTemplateUtil.getDataType(property);

                        if (type == null)
                        {
                            throw new IllegalArgumentException("Unknown Property: " + property);
                        }

                        LOGGER.debug("Property: {} Type: {}", property, type);
                    }

                    columnList[i] = CoalescePropertyFactory.getColumnName(property);
                }

                CachedRowSet rowset;
                int total;

                try (FeatureIterator<SimpleFeature> featureItr = featureSource.getFeatures(localQuery).features())
                {
                    Iterator<Object[]> columnIterator = new FeatureColumnIterator(featureItr, properties);
                    CoalesceResultSet resultSet = new CoalesceResultSet(columnIterator, columnList);
                    rowset = RowSetProvider.newFactory().createCachedRowSet();
                    rowset.populate(resultSet);

                    total = rowset.size();
                }

                // TODO If page size is reach we need to determine the total.

                results.setTotal(total);
                results.setResults(rowset);
            }
            finally
            {
                if (!isDataStoreCacheEnabled())
                {
                    datastore.dispose();
                }
            }

            return results;
        }
        catch (IOException | SQLException | RuntimeException e)
        {
            throw new CoalescePersistorException(e.getMessage(), e);
        }
    }

    @Override
    public EnumSet<EPersistorCapabilities> getCapabilities()
    {
        EnumSet<EPersistorCapabilities> capabilities = super.getCapabilities();

        capabilities.add(EPersistorCapabilities.SEARCH);
        capabilities.add(EPersistorCapabilities.GEOSPATIAL_SEARCH);
        capabilities.add(EPersistorCapabilities.TEMPORAL_SEARCH);
        capabilities.add(EPersistorCapabilities.LUCENE_SYNTAX);

        return capabilities;
    }

    @Override
    public Capabilities getSearchCapabilities()
    {
        Capabilities capability = new Capabilities();
        capability.addAll(Capabilities.SIMPLE_COMPARISONS);
        capability.addAll(Capabilities.LOGICAL);

        return capability;
    }

    private boolean isDataStoreCacheEnabled()
    {
        return params.containsKey(ElasticSearchSettings.PARAM_DATASTORE_CACHE_ENABLED);
    }

    private DataStore getDataStore(String index) throws IOException, CoalescePersistorException
    {
        DataStore store;

        if (isDataStoreCacheEnabled())
        {
            //Check if the datastore for the given index is null, if not return it
            if (datastores.get(index) != null)
            {
                LOGGER.debug("Using Cached ({}) DataStore", index);
                store = datastores.get(index);
            }
            else
            {
                if (!params.isEmpty())
                {
                    store = createDataStore(index);
                    datastores.put(index, store);
                    LOGGER.debug("Cached ({}) DataStore", index);
                }
                else
                {
                    throw new CoalescePersistorException("No params are set, I can't initialize the datastore.");
                }
            }
        }
        else
        {
            store = createDataStore(index);
        }

        return store;
    }

    private DataStore createDataStore(String index) throws IOException
    {
        Map<String, String> props = new HashMap<>();
        props.put(ElasticDataStoreFactory.HOSTNAME.key, params.get(ElasticSearchSettings.PARAM_HTTP_HOST));
        props.put(ElasticDataStoreFactory.HOSTPORT.key, params.get(ElasticSearchSettings.PARAM_HTTP_PORT));
        props.put(ElasticDataStoreFactory.SSL_ENABLED.key, params.get(ElasticSearchSettings.PARAM_SSL_ENABLED));
        props.put(ElasticDataStoreFactory.SSL_REJECT_UNAUTHORIZED.key,
                  params.get(ElasticSearchSettings.PARAM_SSL_REJECT_UNAUTHORIZED));
        props.put(ElasticDataStoreFactory.SOURCE_FILTERING_ENABLED.key, Boolean.TRUE.toString());

        if (Boolean.parseBoolean(params.get(ElasticSearchSettings.PARAM_SSL_ENABLED)))
        {
            System.setProperty("javax.net.ssl.keyStore", params.get(ElasticSearchSettings.PARAM_KEYSTORE_FILE));
            System.setProperty("javax.net.ssl.keyStorePassword", params.get(ElasticSearchSettings.PARAM_KEYSTORE_PASSWORD));

            System.setProperty("javax.net.ssl.trustStore", params.get(ElasticSearchSettings.PARAM_TRUSTSTORE_FILE));
            System.setProperty("javax.net.ssl.trustStorePassword",
                               params.get(ElasticSearchSettings.PARAM_TRUSTSTORE_PASSWORD));
        }

        // TODO Add support for JOINS.

        //In order for the DataStoreFinder to select the ElasticDataStore it needs to have the index.
        props.put(ElasticDataStoreFactory.INDEX_NAME.key, index);

        LOGGER.debug("Creating ({}) DataStore", index);

        return DataStoreFinder.getDataStore(props);
    }

}
