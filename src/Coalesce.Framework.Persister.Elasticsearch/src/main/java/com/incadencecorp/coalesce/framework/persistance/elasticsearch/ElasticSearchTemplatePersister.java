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
import com.incadencecorp.coalesce.framework.DefaultNormalizer;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.persistance.ICoalesceTemplatePersister;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.ResourceAlreadyExistsException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.support.AbstractClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ElasticSearchTemplatePersister implements ICoalesceTemplatePersister {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchTemplatePersister.class);
    private static final ElasticSearchMapperImpl MAPPER = new ElasticSearchMapperImpl();

    public static final String COALESCE_ENTITY_INDEX = "coalesce";
    public static final String COALESCE_LINKAGE_INDEX = COALESCE_ENTITY_INDEX + "-linkages";
    public static final String COALESCE_ENTITY = "entity";
    public static final String COALESCE_TEMPLATE = "template";
    public static final String COALESCE_LINKAGE = CoalesceLinkage.NAME;
    public static final String FIELD_XML = CoalescePropertyFactory.getEntityXml().getPropertyName();

    // Linkage Column Names
    public static final String LINKAGE_KEY_COLUMN_NAME = CoalescePropertyFactory.getLinkageKey().getPropertyName();
    public static final String LINKAGE_DATE_CREATED_COLUMN_NAME = CoalescePropertyFactory.getLinkageDateCreated().getPropertyName();
    public static final String LINKAGE_LAST_MODIFIED_COLUMN_NAME = CoalescePropertyFactory.getLinkageLastModified().getPropertyName();
    public static final String LINKAGE_LINK_TYPE_COLUMN_NAME = CoalescePropertyFactory.getLinkageType().getPropertyName();
    public static final String LINKAGE_LABEL_COLUMN_NAME = CoalescePropertyFactory.getLinkageLabel().getPropertyName();
    public static final String LINKAGE_STATUS_COLUMN_NAME = CoalescePropertyFactory.getLinkageStatus().getPropertyName();

    // Linkage Entity 1 Column Names
    public static final String ENTITY_KEY_COLUMN_NAME = CoalescePropertyFactory.getEntityKey().getPropertyName();
    public static final String ENTITY_NAME_COLUMN_NAME = CoalescePropertyFactory.getName().getPropertyName();
    public static final String ENTITY_SOURCE_COLUMN_NAME = CoalescePropertyFactory.getSource().getPropertyName();
    public static final String ENTITY_VERSION_COLUMN_NAME = CoalescePropertyFactory.getVersion().getPropertyName();
    public static final String ENTITY_DATE_CREATED_COLUMN_NAME = CoalescePropertyFactory.getDateCreated().getPropertyName();
    public static final String ENTITY_LAST_MODIFIED_COLUMN_NAME = CoalescePropertyFactory.getLastModified().getPropertyName();

    // Linkage Entity 2 Column Names
    public static final String LINKAGE_ENTITY2_KEY_COLUMN_NAME = CoalescePropertyFactory.getLinkageEntityKey().getPropertyName();
    public static final String LINKAGE_ENTITY2_NAME_COLUMN_NAME = CoalescePropertyFactory.getLinkageName().getPropertyName();
    public static final String LINKAGE_ENTITY2_SOURCE_COLUMN_NAME = CoalescePropertyFactory.getLinkageSource().getPropertyName();
    public static final String LINKAGE_ENTITY2_VERSION_COLUMN_NAME = CoalescePropertyFactory.getLinkageVersion().getPropertyName();

    private ICoalesceNormalizer normalizer = new DefaultNormalizer();

    protected final Map<String, String> params;
    protected ElasticSearchIterator iterator;
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
        this.params = ElasticSearchSettings.getParameters();
        this.params.putAll(params);

        isAuthoritative = this.params.containsKey(ElasticSearchSettings.PARAM_IS_AUTHORITATIVE)
                && Boolean.parseBoolean(this.params.get(ElasticSearchSettings.PARAM_IS_AUTHORITATIVE));
        iterator = new ElasticSearchIterator(normalizer, isAuthoritative);

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Parameters: ");
            for (Map.Entry<String, String> param : this.params.entrySet())
            {
                LOGGER.debug("\t{} = {}", param.getKey(), param.getValue());
            }
        }

    }

    /**
     * Override the default normalizer.
     *
     * @param value normalizer used to ensure field names don't conflict with Elastic Search syntax.
     */
    public void setNormalizer(ICoalesceNormalizer value)
    {
        normalizer = value;
        iterator = new ElasticSearchIterator(normalizer, isAuthoritative);
    }

    @Override
    public void saveTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            AbstractClient client = conn.getDBConnector(params);

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
                request.index(COALESCE_ENTITY_INDEX);
                request.type(COALESCE_TEMPLATE);
                request.id(template.getKey());
                request.source(properties);

                IndexResponse response = client.index(request).actionGet();

                LOGGER.debug("Saved XML Index called: coalesceentityindex : {}", response);
            }
        }

    }

    @Override
    public void deleteTemplate(String... keys) throws CoalescePersistorException
    {
        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            AbstractClient client = conn.getDBConnector(params);
            for (String key : keys)
            {
                deleteFromElasticSearch(client, COALESCE_ENTITY_INDEX, COALESCE_TEMPLATE, key);
            }
        }
    }

    @Override
    public void unregisterTemplate(String... keys) throws CoalescePersistorException
    {
        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            AbstractClient client = conn.getDBConnector(params);
            for (String key : keys)
            {
                deleteFromElasticSearch(client, COALESCE_ENTITY_INDEX, COALESCE_TEMPLATE, key);
                deleteFromElasticSearch(client, COALESCE_ENTITY_INDEX, COALESCE_ENTITY, key);
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
                AbstractClient client = conn.getDBConnector(params);

                GetRequest request = new GetRequest();
                request.index(COALESCE_ENTITY_INDEX);
                request.type(COALESCE_TEMPLATE);
                request.id(key);

                GetResponse response = client.get(request).actionGet();

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
            AbstractClient client = conn.getDBConnector(params);

            BoolQueryBuilder boolQuery = new BoolQueryBuilder().must(QueryBuilders.matchQuery("coalesceentity.name",
                                                                                              name)).must(QueryBuilders.matchQuery(
                    "coalesceentity.source",
                    source)).must(QueryBuilders.matchQuery("coalesceentity.version", version));

            SearchRequestBuilder searchRequest = client.prepareSearch(COALESCE_ENTITY_INDEX).setTypes(COALESCE_TEMPLATE).setQuery(
                    boolQuery);

            //LOGGER.debug("Trying this search: " + searchRequest.toString());

            SearchResponse response = searchRequest.get();

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

        return null;
    }

    @Override
    public List<ObjectMetaData> getEntityTemplateMetadata() throws CoalescePersistorException
    {
        List<ObjectMetaData> metaDatas = new ArrayList<>();

        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            AbstractClient client = conn.getDBConnector(params);

            SearchRequestBuilder searchRequest = client.prepareSearch(COALESCE_ENTITY_INDEX).setTypes(COALESCE_TEMPLATE).setQuery(
                    QueryBuilders.matchAllQuery());

            SearchResponse response = searchRequest.get();

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
        return metaDatas;
    }

    @Override
    public void registerTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        CoalesceTemplateUtil.addTemplates(templates);

        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            AbstractClient client = conn.getDBConnector(params);

            CreateIndexResponse response;

            for (CoalesceEntityTemplate template : templates)
            {
                Map<String, Object> source = createMapping(template);
                String index = COALESCE_ENTITY_INDEX + "-" + normalize(template.getName());

                CreateIndexRequest request = new CreateIndexRequest();
                request.index(index);

                // Add a type / recordset
                for (String type : CoalesceTemplateUtil.getRecordsets(template.getKey()))
                {
                    request.mapping(type, Collections.singletonMap("properties", source));
                }

                try
                {
                    response = client.admin().indices().create(request).actionGet();
                    LOGGER.debug("Registered Coalesce Template Index : {}", index, response);
                }
                catch (ResourceAlreadyExistsException e)
                {
                    LOGGER.warn("Template {} Already Registered", index);
                }

                try
                {
                    response = client.admin().indices().create(createCoalesceEntityIndexRequest()).actionGet();

                    LOGGER.debug("Registered Coalesce Index : {}", template.getName(), response);
                }
                catch (ResourceAlreadyExistsException e)
                {
                    // Do Nothing indexes already exists
                }

                try
                {
                    response = client.admin().indices().create(createCoalesceLinkageIndexRequest()).actionGet();

                    LOGGER.debug("Registered Coalesce Linkage Index : {}", template.getName(), response);
                }
                catch (ResourceAlreadyExistsException e)
                {
                    // Do Nothing indexes already exists
                }

            }
        }
    }

    protected void deleteFromElasticSearch(AbstractClient conn, String index, String type, String id)
    {
        DeleteRequest entityRequest = new DeleteRequest();
        entityRequest.index(index);
        entityRequest.type(type);
        entityRequest.id(id);

        DeleteResponse entityResponse = conn.delete(entityRequest).actionGet();

        LOGGER.debug("Delete entity for entity {} : {}", index, entityResponse);
    }

    private CreateIndexRequest createCoalesceEntityIndexRequest()
    {
        Map<String, Object> mapping = createEntityMapping();

        mapping.put(FIELD_XML, Collections.singletonMap("enabled", "false"));

        CreateIndexRequest request = new CreateIndexRequest();
        request.index(COALESCE_ENTITY_INDEX);
        request.mapping(COALESCE_ENTITY, Collections.singletonMap("properties", mapping));
        request.mapping(COALESCE_TEMPLATE, Collections.singletonMap("properties", mapping));

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
        properties.put(ENTITY_LAST_MODIFIED_COLUMN_NAME,
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.DATE_TIME_TYPE)));

        return properties;
    }

    private CreateIndexRequest createCoalesceLinkageIndexRequest()
    {
        CreateIndexRequest request = new CreateIndexRequest();
        request.index(COALESCE_LINKAGE_INDEX);
        request.mapping(COALESCE_LINKAGE, Collections.singletonMap("properties", createLinkageMapping()));

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

                propertiesMap.put(entry.getKey(), mapping);

            }
        }

        return propertiesMap;
    }

    protected String normalize(String value)
    {
        return normalizer != null ? normalizer.normalize(value) : value;
    }

    protected String normalize(CoalesceField<?> field)
    {
        return normalizer != null ? normalizer.normalize(field.getParent().getParent().getName(),
                                                         field.getName()) : field.getName();
    }

}
