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

package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.persistance.ICoalesceTemplatePersister;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.opengis.filter.expression.PropertyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElasticSearchTemplatePersister implements ICoalesceTemplatePersister {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchTemplatePersister.class);

    private static final ElasticSearchMapperImpl MAPPER = new ElasticSearchMapperImpl();
    protected static final ICoalesceNormalizer NORMALIZER = new ElasticSearchNormalizer();

    public static final String COALESCE_ENTITY_INDEX = "coalesce";
    public static final String COALESCE_TEMPLATE_INDEX = COALESCE_ENTITY_INDEX + "-templates";
    public static final String COALESCE_LINKAGE_INDEX = COALESCE_ENTITY_INDEX + "-linkages";
    public static final String COALESCE_ENTITY = "entity";
    public static final String COALESCE_LINKAGE = CoalesceLinkage.NAME;
    public static final String FIELD_XML = normalize(CoalescePropertyFactory.getEntityXml());

    // Linkage Column Names
    public static final String LINKAGE_KEY_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageKey());
    public static final String LINKAGE_DATE_CREATED_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageDateCreated());
    public static final String LINKAGE_LAST_MODIFIED_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageLastModified());
    public static final String LINKAGE_LINK_TYPE_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageType());
    public static final String LINKAGE_LABEL_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageLabel());
    public static final String LINKAGE_STATUS_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageStatus());

    // Linkage Entity 1 Column Names
    public static final String ENTITY_KEY_COLUMN_NAME = normalize(CoalescePropertyFactory.getEntityKey());
    public static final String ENTITY_NAME_COLUMN_NAME = normalize(CoalescePropertyFactory.getName());
    public static final String ENTITY_SOURCE_COLUMN_NAME = normalize(CoalescePropertyFactory.getSource());
    public static final String ENTITY_VERSION_COLUMN_NAME = normalize(CoalescePropertyFactory.getVersion());
    public static final String ENTITY_DATE_CREATED_COLUMN_NAME = normalize(CoalescePropertyFactory.getDateCreated());
    public static final String ENTITY_CREATED_BY_COLUMN_NAME = normalize(CoalescePropertyFactory.getCreatedBy());
    public static final String ENTITY_LAST_MODIFIED_COLUMN_NAME = normalize(CoalescePropertyFactory.getLastModified());
    public static final String ENTITY_LAST_MODIFIED_BY_COLUMN_NAME = normalize(CoalescePropertyFactory.getLastModifiedBy());
    public static final String ENTITY_TITLE_COLUMN_NAME = normalize(CoalescePropertyFactory.getEntityTitle());
    public static final String ENTITY_STATUS_COLUMN_NAME = normalize(CoalescePropertyFactory.getEntityStatus());
    public static final String ENTITY_ID_COLUMN_NAME = normalize(CoalescePropertyFactory.getEntityId());
    public static final String ENTITY_ID_TYPE_COLUMN_NAME = normalize(CoalescePropertyFactory.getEntityIdType());

    // Linkage Entity 2 Column Names
    public static final String LINKAGE_ENTITY2_KEY_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageEntityKey());
    public static final String LINKAGE_ENTITY2_NAME_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageName());
    public static final String LINKAGE_ENTITY2_SOURCE_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageSource());
    public static final String LINKAGE_ENTITY2_VERSION_COLUMN_NAME = normalize(CoalescePropertyFactory.getLinkageVersion());

    private final Settings defaultSettings;

    protected final Map<String, String> params;
    protected final ElasticSearchIterator iterator;
    protected final boolean isAuthoritative;

    /**
     * Default Constructor
     */
    public ElasticSearchTemplatePersister()
    {
        this(Collections.emptyMap());
    }

    public ElasticSearchTemplatePersister(Map<String, String> params)
    {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try
        {
            // Required within OSGi due to the ServiceLoader used by org.elasticsearch.common.xcontent.XContentBuilder
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            XContentFactory.contentBuilder(XContentType.JSON);
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(loader);
        }

        this.params = ElasticSearchSettings.getParameters();
        this.params.putAll(params);

        isAuthoritative = this.params.containsKey(ElasticSearchSettings.PARAM_IS_AUTHORITATIVE)
                && Boolean.parseBoolean(this.params.get(ElasticSearchSettings.PARAM_IS_AUTHORITATIVE));
        iterator = new ElasticSearchIterator(NORMALIZER, isAuthoritative);

        if (this.params.containsKey(ElasticSearchSettings.PARAM_DATASTORE_CACHE_ENABLED))
        {
            if (!Boolean.parseBoolean(this.params.get(ElasticSearchSettings.PARAM_DATASTORE_CACHE_ENABLED)))
            {
                // If False remove so we can simply do a contains key.
                this.params.remove(ElasticSearchSettings.PARAM_DATASTORE_CACHE_ENABLED);
            }
        }

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Parameters: ");
            for (Map.Entry<String, String> param : this.params.entrySet())
            {
                LOGGER.debug("\t{} = {}", param.getKey(), param.getValue());
            }
        }

        Settings.Builder buidler = Settings.builder();

        for (Map.Entry<String, String> entry : params.entrySet())
        {
            if (entry.getKey().startsWith(ElasticSearchSettings.PARAM_INDEX_SETTING_PREFIX))
            {
                buidler.put(entry.getKey().replace(ElasticSearchSettings.PARAM_INDEX_SETTING_PREFIX, ""),
                            entry.getValue());
            }
        }

        defaultSettings = buidler.build();
    }

    @Override
    public void saveTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            RestHighLevelClient client = conn.getDBConnector(params);

            for (CoalesceEntityTemplate template : templates)
            {
                Map<String, Object> properties = new HashMap<>();
                properties.put(ENTITY_KEY_COLUMN_NAME, template.getKey());
                properties.put(ENTITY_NAME_COLUMN_NAME, template.getName());
                properties.put(ENTITY_SOURCE_COLUMN_NAME, template.getSource());
                properties.put(ENTITY_VERSION_COLUMN_NAME, template.getVersion());
                properties.put(ENTITY_DATE_CREATED_COLUMN_NAME, template.getDateCreated());
                properties.put(ENTITY_LAST_MODIFIED_COLUMN_NAME, template.getLastModified());
                properties.put(FIELD_XML, template.toXml());

                IndexRequest request = new IndexRequest();
                request.index(COALESCE_TEMPLATE_INDEX);
                request.id(template.getKey());
                request.source(properties);

                BulkRequest bulkRequest = new BulkRequest();
                bulkRequest.setRefreshPolicy(ElasticSearchSettings.getIndexRefreshPolicy());
                bulkRequest.add(request);

                BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);

                LOGGER.debug("Saved XML Index called: coalesce-templates : {}", response);
            }
        }
        catch (IOException e)
        {
            throw new CoalescePersistorException(e.getMessage(), e);
        }

    }

    @Override
    public void deleteTemplate(String... keys) throws CoalescePersistorException
    {
        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            RestHighLevelClient client = conn.getDBConnector(params);
            for (String key : keys)
            {
                deleteFromElasticSearch(client, COALESCE_TEMPLATE_INDEX, key);
            }
        }
    }

    @Override
    public void unregisterTemplate(String... keys) throws CoalescePersistorException
    {
        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            RestHighLevelClient client = conn.getDBConnector(params);
            for (String key : keys)
            {
                deleteFromElasticSearch(client, COALESCE_TEMPLATE_INDEX, key);
            }
        }
    }

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String key) throws CoalescePersistorException
    {
        CoalesceEntityTemplate template;

        if (!StringHelper.isNullOrEmpty(key))
        {
            try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
            {
                RestHighLevelClient client = conn.getDBConnector(params);

                GetRequest request = new GetRequest();
                request.index(COALESCE_TEMPLATE_INDEX);
                request.id(key);

                GetResponse response = client.get(request, RequestOptions.DEFAULT);

                if (response != null && response.getSource() != null)
                {
                    try
                    {
                        template = CoalesceEntityTemplate.create((String) response.getSource().get(FIELD_XML));
                    }
                    catch (CoalesceException e)
                    {
                        throw new CoalescePersistorException(e);
                    }
                }
                else
                {
                    throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_FOUND, "Template", key));
                }

            }
            catch (IOException e)
            {
                throw new CoalescePersistorException(e.getMessage(), e);
            }
        }
        else
        {
            throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_FOUND, "Template", key));
        }
        return template;
    }

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String name, String source, String version)
            throws CoalescePersistorException
    {
        String key = getEntityTemplateKey(name, source, version);

        if (StringHelper.isNullOrEmpty(key))
        {
            throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_FOUND,
                                                               "Template",
                                                               "Name: " + name + " Source: " + source + " Version: "
                                                                       + version));
        }

        return getEntityTemplate(key);
    }

    @Override
    public String getEntityTemplateKey(String name, String source, String version) throws CoalescePersistorException
    {
        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            RestHighLevelClient client = conn.getDBConnector(params);

            BoolQueryBuilder boolQuery = new BoolQueryBuilder()
                    .must(QueryBuilders.matchQuery(ENTITY_NAME_COLUMN_NAME, name))
                    .must(QueryBuilders.matchQuery(ENTITY_SOURCE_COLUMN_NAME, source))
                    .must(QueryBuilders.matchQuery(ENTITY_VERSION_COLUMN_NAME, version));

            SearchRequest searchRequest = new SearchRequest(COALESCE_TEMPLATE_INDEX);
            searchRequest.source().query(boolQuery);

            //LOGGER.debug("Trying this search: " + searchRequest.toString());

            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

            SearchHits hits = response.getHits();

            for (SearchHit result : hits)
            {
                //Like the Highlander, there should only ever be one
                return result.getId();
            }
        }
        catch (ElasticsearchException e)
        {
            if (e.status() == RestStatus.NOT_FOUND)
            {
                LOGGER.error(e.getDetailedMessage());
                throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_FOUND,
                                                                   "Template",
                                                                   "Name: " + name + " Source: " + source + " Version: "
                                                                           + version), e);
            }
        }
        catch (IOException e)
        {
            throw new CoalescePersistorException(e.getMessage(), e);
        }

        return null;
    }

    @Override
    public List<ObjectMetaData> getEntityTemplateMetadata() throws CoalescePersistorException
    {
        List<ObjectMetaData> metaDatas = new ArrayList<>();

        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            RestHighLevelClient client = conn.getDBConnector(params);

            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.matchAllQuery());

            SearchRequest searchRequest = new SearchRequest(COALESCE_TEMPLATE_INDEX);
            searchRequest.source(sourceBuilder);

            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

            SearchHits hits = response.getHits();

            for (SearchHit result : hits)
            {
                CoalesceEntityTemplate template = getEntityTemplate(result.getId());
                metaDatas.add(new ObjectMetaData(template.getKey(),
                                                 template.getName(),
                                                 template.getSource(),
                                                 template.getVersion(),
                                                 template.getDateCreated(),
                                                 template.getLastModified()));
            }
        }
        catch (ElasticsearchException e)
        {
            LOGGER.error(e.getDetailedMessage());
        }
        catch (IOException e)
        {
            LOGGER.error(e.getMessage(), e);
        }
        return metaDatas;
    }

    @Override
    public void registerTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        CoalesceTemplateUtil.addTemplates(templates);

        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            RestHighLevelClient client = conn.getDBConnector(params);

            CreateIndexResponse response;

            for (CoalesceEntityTemplate template : templates)
            {
                Map<String, Object> source = createMapping(template);
                String index = COALESCE_ENTITY_INDEX + "-" + NORMALIZER.normalize(template.getName());

                CreateIndexRequest request = new CreateIndexRequest(index);
                
                request.settings(defaultSettings);
                request.mapping(Collections.singletonMap("properties", source));

                if (!doesExists(client, index))
                {
                    response = client.indices().create(request, RequestOptions.DEFAULT);
                    LOGGER.debug("Registered Coalesce Template Index {}: {}", index, response);

                    if (!doesExists(client, COALESCE_TEMPLATE_INDEX))
                    {
                        response = client.indices().create(createCoalesceTemplateIndexRequest(), RequestOptions.DEFAULT);

                        LOGGER.debug("Registered Coalesce Template Index {}: {}", template.getName(), response);
                    }

                    if (!doesExists(client, COALESCE_ENTITY_INDEX))
                    {
                        response = client.indices().create(createCoalesceEntityIndexRequest(), RequestOptions.DEFAULT);

                        LOGGER.debug("Registered Coalesce Index {}: {}", template.getName(), response);
                    }

                    if (!doesExists(client, COALESCE_LINKAGE_INDEX))
                    {
                        response = client.indices().create(createCoalesceLinkageIndexRequest(), RequestOptions.DEFAULT);

                        LOGGER.debug("Registered Coalesce Linkage Index {}: {}", template.getName(), response);
                    }
                }
                else
                {
                    LOGGER.warn("Template {} Already Registered", index);
                }
            }
        }
        catch (IOException e)
        {
            throw new CoalescePersistorException(e.getMessage(), e);
        }
    }

    private boolean doesExists(RestHighLevelClient client, String... names) throws IOException
    {
        return doesIndexExists(client, names) || doesAliasExists(client, names);
    }

    private boolean doesAliasExists(RestHighLevelClient client, String... aliases) throws IOException
    {
        GetAliasesRequest exists = new GetAliasesRequest(aliases);

        return client.indices().existsAlias(exists, RequestOptions.DEFAULT);
    }

    private boolean doesIndexExists(RestHighLevelClient client, String... indices) throws IOException
    {
        GetIndexRequest request = new GetIndexRequest(indices);

        return client.indices().exists(request, RequestOptions.DEFAULT);
    }

    private void deleteFromElasticSearch(RestHighLevelClient conn, String index, String id)
    {
        DeleteRequest entityRequest = new DeleteRequest();
        entityRequest.index(index);
        entityRequest.id(id);
        BulkRequest request = new BulkRequest();
        request.add(entityRequest);
        request.setRefreshPolicy(ElasticSearchSettings.getIndexRefreshPolicy());

        try
        {
            BulkResponse response = conn.bulk(request, RequestOptions.DEFAULT);
            LOGGER.debug("Delete entity for entity {} : {}", index, response);
        }
        catch (IOException e)
        {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private CreateIndexRequest createCoalesceEntityIndexRequest()
    {
        Map<String, Object> mapping = createEntityMapping();
        mapping.put(FIELD_XML, Collections.singletonMap("enabled", "false"));

        CreateIndexRequest request = new CreateIndexRequest(COALESCE_ENTITY_INDEX);
        request.settings(defaultSettings);
        request.mapping(Collections.singletonMap("properties", mapping));

        return request;
    }

    private CreateIndexRequest createCoalesceTemplateIndexRequest()
    {
        Map<String, Object> mapping = createEntityMapping();

        mapping.put(FIELD_XML, Collections.singletonMap("enabled", "false"));

        CreateIndexRequest request = new CreateIndexRequest(COALESCE_TEMPLATE_INDEX);
        request.settings(defaultSettings);
        request.mapping(Collections.singletonMap("properties", mapping));

        return request;
    }

    private Map<String, Object> createEntityMapping()
    {
        Map<String, Object> properties = new HashMap<>();

        properties.put(ENTITY_KEY_COLUMN_NAME, Collections.singletonMap("type", "keyword"));
        properties.put(ENTITY_NAME_COLUMN_NAME, Collections.singletonMap("type", "keyword"));
        properties.put(ENTITY_SOURCE_COLUMN_NAME, Collections.singletonMap("type", "keyword"));
        properties.put(ENTITY_VERSION_COLUMN_NAME, Collections.singletonMap("type", "keyword"));
        properties.put(ENTITY_DATE_CREATED_COLUMN_NAME,
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.DATE_TIME_TYPE)));
        properties.put(ENTITY_CREATED_BY_COLUMN_NAME, Collections.singletonMap("type", "keyword"));
        properties.put(ENTITY_LAST_MODIFIED_BY_COLUMN_NAME, Collections.singletonMap("type", "keyword"));
        properties.put(ENTITY_LAST_MODIFIED_COLUMN_NAME,
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.DATE_TIME_TYPE)));
        properties.put(ENTITY_TITLE_COLUMN_NAME,
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.STRING_TYPE)));
        properties.put(ENTITY_STATUS_COLUMN_NAME, Collections.singletonMap("type", "keyword"));
        properties.put(ENTITY_ID_COLUMN_NAME, Collections.singletonMap("type", "keyword"));
        properties.put(ENTITY_ID_TYPE_COLUMN_NAME, Collections.singletonMap("type", "keyword"));

        return properties;
    }

    private CreateIndexRequest createCoalesceLinkageIndexRequest()
    {
        CreateIndexRequest request = new CreateIndexRequest(COALESCE_LINKAGE_INDEX);
        request.settings(defaultSettings);
        request.mapping(Collections.singletonMap("properties", createLinkageMapping()));

        return request;
    }

    private Map<String, Object> createLinkageMapping()
    {

        Map<String, Object> properties = createEntityMapping();

        properties.put(LINKAGE_KEY_COLUMN_NAME, Collections.singletonMap("type", "keyword"));

        properties.put(LINKAGE_ENTITY2_KEY_COLUMN_NAME, Collections.singletonMap("type", "keyword"));
        properties.put(LINKAGE_ENTITY2_NAME_COLUMN_NAME, Collections.singletonMap("type", "keyword"));
        properties.put(LINKAGE_ENTITY2_SOURCE_COLUMN_NAME, Collections.singletonMap("type", "keyword"));
        properties.put(LINKAGE_ENTITY2_VERSION_COLUMN_NAME, Collections.singletonMap("type", "keyword"));

        properties.put(LINKAGE_DATE_CREATED_COLUMN_NAME,
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.DATE_TIME_TYPE)));
        properties.put(LINKAGE_LAST_MODIFIED_COLUMN_NAME,
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.DATE_TIME_TYPE)));
        properties.put(LINKAGE_LABEL_COLUMN_NAME, Collections.singletonMap("type", "keyword"));
        properties.put(LINKAGE_STATUS_COLUMN_NAME, Collections.singletonMap("type", "keyword"));
        properties.put(LINKAGE_LINK_TYPE_COLUMN_NAME, Collections.singletonMap("type", "keyword"));

        return properties;
    }

    private Map<String, Object> createMapping(CoalesceEntityTemplate template)
    {
        Map<String, Object> propertiesMap = createEntityMapping();

        for (Map.Entry<String, ECoalesceFieldDataTypes> entry : CoalesceTemplateUtil.getTemplateDataTypes(template.getKey()).entrySet())
        {
            String type = MAPPER.map(entry.getValue());

            if (!StringHelper.isNullOrEmpty(type))
            {
                Map<String, Object> mapping = new HashMap<>();
                mapping.put("type", type);

                if (entry.getValue() == ECoalesceFieldDataTypes.STRING_TYPE)
                {
                    Map<String, Object> keywordField = new HashMap<>();
                    keywordField.put("type", "keyword");
                    keywordField.put("ignore_above", 256);

                    mapping.put("fields", Collections.singletonMap("keyword", keywordField));
                }

                propertiesMap.put(normalize(entry.getKey()), mapping);

            }
        }

        return propertiesMap;
    }

    private static String normalize(PropertyName property)
    {
        return normalize(property.getPropertyName());
    }

    private static String normalize(String value)
    {
        String[] parts = value.split("[.]");
        return NORMALIZER.normalize(parts[0], parts[1]);
    }

}
