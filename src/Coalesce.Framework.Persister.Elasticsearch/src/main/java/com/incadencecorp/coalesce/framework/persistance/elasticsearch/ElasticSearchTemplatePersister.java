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
    public static final String COALESCE_ENTITY = "entity";
    public static final String COALESCE_LINKAGE_INDEX = "linkages";
    public static final String COALESCE_TEMPLATE = "template";
    public static final String FIELD_XML = CoalescePropertyFactory.getEntityXml().getPropertyName();

    private ICoalesceNormalizer normalizer = new DefaultNormalizer();

    /**
     * Override the default normalizer.
     *
     * @param value normalizer used to ensure field names don't conflict with Elastic Search syntax.
     */
    public void setNormalizer(ICoalesceNormalizer value)
    {
        normalizer = value;
    }

    @Override
    public void saveTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            AbstractClient client = conn.getDBConnector(ElasticSearchSettings.getParameters());

            for (CoalesceEntityTemplate template : templates)
            {
                Map<String, Object> properties = new HashMap<>();
                properties.put(CoalescePropertyFactory.getEntityKey().getPropertyName(), template.getKey());
                properties.put(CoalescePropertyFactory.getName().getPropertyName(), template.getName());
                properties.put(CoalescePropertyFactory.getSource().getPropertyName(), template.getSource());
                properties.put(CoalescePropertyFactory.getVersion().getPropertyName(), template.getVersion());
                properties.put(CoalescePropertyFactory.getDateCreated().getPropertyName(), template.getDateCreated());
                properties.put(CoalescePropertyFactory.getLastModified().getPropertyName(), template.getLastModified());

                if (ElasticSearchSettings.getStoreXML())
                {
                    properties.put(FIELD_XML, template.toXml());
                }

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
            AbstractClient client = conn.getDBConnector(ElasticSearchSettings.getParameters());
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
            AbstractClient client = conn.getDBConnector(ElasticSearchSettings.getParameters());
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
                AbstractClient client = conn.getDBConnector(ElasticSearchSettings.getParameters());

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
            AbstractClient client = conn.getDBConnector(ElasticSearchSettings.getParameters());

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
            AbstractClient client = conn.getDBConnector(ElasticSearchSettings.getParameters());

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
        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            AbstractClient client = conn.getDBConnector(ElasticSearchSettings.getParameters());

            for (CoalesceEntityTemplate template : templates)
            {
                try
                {
                    CreateIndexRequest request = new CreateIndexRequest();
                    request.index("coalesce-" + normalize(template.getName()));
                    request.mapping(COALESCE_ENTITY, Collections.singletonMap("properties", createMapping(template)));

                    CreateIndexResponse response = client.admin().indices().create(request).actionGet();

                    LOGGER.debug("Registered Template ({}) : {}", template.getName(), response);
                }
                catch (ResourceAlreadyExistsException e)
                {
                    LOGGER.warn("Template Already Registered");
                }

                try
                {
                    client.admin().indices().create(createCoalesceEntityIndexRequest()).actionGet();
                    client.admin().indices().create(createCoalesceLinkageIndexRequest()).actionGet();
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
        CreateIndexRequest request = new CreateIndexRequest();
        request.index(COALESCE_ENTITY_INDEX);
        request.mapping(COALESCE_ENTITY, Collections.singletonMap("properties", createEntityMapping()));
        request.mapping(COALESCE_TEMPLATE, Collections.singletonMap("properties", createEntityMapping()));

        return request;
    }

    private Map<String, Object> createEntityMapping()
    {

        Map<String, Object> properties = new HashMap<>();
        properties.put(CoalescePropertyFactory.getEntityKey().getPropertyName(),
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.STRING_TYPE)));
        properties.put(CoalescePropertyFactory.getName().getPropertyName(),
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.STRING_TYPE)));
        properties.put(CoalescePropertyFactory.getSource().getPropertyName(),
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.STRING_TYPE)));
        properties.put(CoalescePropertyFactory.getVersion().getPropertyName(),
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.STRING_TYPE)));
        properties.put(CoalescePropertyFactory.getDateCreated().getPropertyName(),
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.DATE_TIME_TYPE)));
        properties.put(CoalescePropertyFactory.getLastModified().getPropertyName(),
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.DATE_TIME_TYPE)));

        if (ElasticSearchSettings.getStoreXML())
        {
            properties.put(FIELD_XML, Collections.singletonMap("enabled", "false"));
        }

        return properties;
    }

    private CreateIndexRequest createCoalesceLinkageIndexRequest()
    {
        CreateIndexRequest request = new CreateIndexRequest();
        request.index(COALESCE_LINKAGE_INDEX);
        request.mapping(COALESCE_LINKAGE_INDEX, Collections.singletonMap("properties", createLinkageMapping()));

        return request;
    }

    private Map<String, Object> createLinkageMapping()
    {

        Map<String, Object> properties = new HashMap<>();
        properties.put(CoalescePropertyFactory.getEntityKey().getPropertyName(),
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.STRING_TYPE)));
        properties.put(CoalescePropertyFactory.getName().getPropertyName(),
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.STRING_TYPE)));
        properties.put(CoalescePropertyFactory.getSource().getPropertyName(),
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.STRING_TYPE)));
        properties.put(CoalescePropertyFactory.getVersion().getPropertyName(),
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.STRING_TYPE)));

        properties.put(CoalescePropertyFactory.getLinkageEntityKey().getPropertyName(),
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.STRING_TYPE)));
        properties.put(CoalescePropertyFactory.getLinkageName().getPropertyName(),
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.STRING_TYPE)));
        properties.put(CoalescePropertyFactory.getLinkageSource().getPropertyName(),
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.STRING_TYPE)));
        properties.put(CoalescePropertyFactory.getLinkageVersion().getPropertyName(),
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.STRING_TYPE)));

        properties.put(CoalescePropertyFactory.getDateCreated().getPropertyName(),
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.DATE_TIME_TYPE)));
        properties.put(CoalescePropertyFactory.getLastModified().getPropertyName(),
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.DATE_TIME_TYPE)));
        properties.put(CoalescePropertyFactory.getLinkageLabel().getPropertyName(),
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.STRING_TYPE)));
        properties.put(CoalescePropertyFactory.getLinkageStatus().getPropertyName(),
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.STRING_TYPE)));
        properties.put(CoalescePropertyFactory.getLinkageType().getPropertyName(),
                       Collections.singletonMap("type", MAPPER.map(ECoalesceFieldDataTypes.STRING_TYPE)));

        return properties;
    }

    private Map<String, Object> createMapping(CoalesceEntityTemplate template)
    {
        Map<String, Object> propertiesMap = new HashMap<>();

        CoalesceTemplateUtil.addTemplates(template);

        for (Map.Entry<String, ECoalesceFieldDataTypes> entry : CoalesceTemplateUtil.getTemplateDataTypes(template.getKey()).entrySet())
        {
            String type = MAPPER.map(entry.getValue());

            if (!StringHelper.isNullOrEmpty(type))
            {
                propertiesMap.put(entry.getKey(), Collections.singletonMap("type", type));
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
