package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.jobs.metrics.StopWatch;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.support.AbstractClient;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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

/**
 * This persister is for a ElasticSearch database.
 *
 * @author n78554
 */
public class ElasticSearchPersistor extends ElasticSearchTemplatePersister implements ICoalescePersistor {

    /*--------------------------------------------------------------------------
    Private Members
    --------------------------------------------------------------------------*/

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchPersistor.class);

    /*--------------------------------------------------------------------------
    Overridden Functions
    --------------------------------------------------------------------------*/

    /**
     * Default constructor using {@link ElasticSearchSettings} for configuration
     */
    public ElasticSearchPersistor()
    {
        this(Collections.emptyMap());
    }

    /**
     * @param params configuration.
     */
    public ElasticSearchPersistor(Map<String, String> params)
    {
        super(params);
    }

    /*--------------------------------------------------------------------------
    Protected Methods
    --------------------------------------------------------------------------*/

    public boolean checkIfIndexExists(AbstractClient client, String index)
    {
        index = index.toLowerCase();

        if (!index.startsWith("coalesce"))
        {
            index = "coalesce-" + index;
        }

        IndicesExistsRequest request = new IndicesExistsRequest();
        request.indices(index);

        IndicesExistsResponse response = client.admin().indices().exists(request).actionGet();
        return response.isExists();
    }

    @Override
    public boolean saveEntity(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            AbstractClient client = conn.getDBConnector(params);

            BulkRequest request = iterator.iterate(allowRemoval, entities);

            executeCleanup(client, request, entities);

            LOGGER.debug("{} Entities Created {} Request", entities.length, request.requests().size());

            executeRequest(client, request, 1);
        }
        catch (CoalesceException | InterruptedException e)
        {
            throw new CoalescePersistorException(e);
        }

        return true;
    }

    private void executeCleanup(AbstractClient client, BulkRequest request, CoalesceEntity... entities)
    {
        Set<String> indices = new HashSet<>();
        Set<String> recordKeys = new HashSet<>();

        // Create List of Indices
        for (DocWriteRequest task : request.requests())
        {
            recordKeys.add(task.id());
            indices.add(task.index());
        }

        // Don't cleanup coalesce index
        indices.remove(COALESCE_ENTITY_INDEX);

        if (indices.size() > 0)
        {
            StopWatch watch = new StopWatch();
            watch.start();

            BoolQueryBuilder builder = QueryBuilders.boolQuery();

            // Only records belonging to one of the entities being updated
            for (CoalesceEntity entity : entities)
            {
                builder.should().add(QueryBuilders.matchQuery(ElasticSearchPersistor.ENTITY_KEY_COLUMN_NAME,
                                                              entity.getKey()));
            }

            int pageSize = 1000;
            int original = request.requests().size();

            SearchResponse response = client.prepareSearch(indices.toArray(new String[indices.size()])).setQuery(builder).setSize(
                    pageSize).setScroll(new TimeValue(60000)).get();

            do
            {
                for (SearchHit hit : response.getHits().getHits())
                {
                    // Records being updated by this request
                    if (!recordKeys.contains(hit.getId()))
                    {
                        // No; Create Delete Request
                        DeleteRequest deleteRequest = new DeleteRequest();
                        deleteRequest.index(hit.getIndex());
                        deleteRequest.type(hit.getType());
                        deleteRequest.id(hit.getId());

                        request.requests().add(deleteRequest);
                    }
                }

                response = client.prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(60000)).get();
            }
            while (response.getHits().getHits().length != 0);

            watch.finish();

            if (original != request.requests().size() || LOGGER.isTraceEnabled())
            {
                LOGGER.debug("Created {} Delete Request for Phantom Records in {} ms", request.requests().size() - original, watch.getWorkLife());
            }
        }
    }

    private void executeRequest(AbstractClient client, BulkRequest request, int attempt)
            throws CoalescePersistorException, InterruptedException
    {
        try
        {
            BulkResponse response = client.bulk(request).actionGet();

            for (BulkItemResponse item : response.getItems())
            {
                LOGGER.trace("({}) ID = {}, Index = {}, Type = {} : {}",
                             item.status().toString(),
                             item.getId(),
                             item.getIndex(),
                             item.getType(),
                             LOGGER.isTraceEnabled() ? response : response.getClass().getSimpleName());

                if (item.isFailed())
                {
                    // TODO Roll back other changes.
                    throw new ElasticsearchException("(FAILED) Request Failed: {}" + item.getFailureMessage());
                }

            }
        }
        catch (NoNodeAvailableException e)
        {
            if (attempt > ElasticSearchSettings.getRetryAttempts())
            {
                throw new CoalescePersistorException("(FAILED) Executing Request", e);
            }
            else
            {
                int millis = new Random().nextInt(ElasticSearchSettings.getBackoffInterval())
                        + ElasticSearchSettings.getBackoffInterval();

                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("Sleeping ({}) for {} ms", Thread.currentThread().getId(), millis);
                }

                // Back off to allow other threads to close their locks.
                Thread.sleep((long) millis * attempt);

                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("Awaken ({})", Thread.currentThread().getId());
                }

                LOGGER.warn("(RETRYING) No Nodes Available - Attempt: {} after backing off for {} ms", attempt, millis);
                executeRequest(client, request, ++attempt);
            }
        }

    }

    @Override
    public CoalesceEntity[] getEntity(String... keys) throws CoalescePersistorException
    {
        List<CoalesceEntity> results = new ArrayList<>();

        for (String entityXML : getEntityXml(keys))
        {
            results.add(CoalesceEntity.create(entityXML));
        }

        return results.toArray(new CoalesceEntity[results.size()]);
    }

    @Override
    public String[] getEntityXml(String... keys) throws CoalescePersistorException
    {
        List<String> results = new ArrayList<>();
        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            AbstractClient client = conn.getDBConnector(params);

            for (String key : keys)
            {
                GetRequest request = new GetRequest();
                request.index(COALESCE_ENTITY_INDEX);
                request.type(COALESCE_ENTITY);
                request.id(key);

                GetResponse getResponse = client.get(request).actionGet();

                if (getResponse != null && getResponse.getSource() != null)
                {
                    results.add((String) getResponse.getSource().getOrDefault(FIELD_XML, ""));
                }
            }
        }
        catch (ElasticsearchException e)
        {
            if (e.status() == RestStatus.NOT_FOUND)
            {
                LOGGER.info(e.getDetailedMessage());
            }
            else
            {
                throw new CoalescePersistorException(e);
            }
        }

        return results.toArray(new String[results.size()]);
    }

    @Override
    public EnumSet<EPersistorCapabilities> getCapabilities()
    {
        EnumSet<EPersistorCapabilities> capabilities = EnumSet.of(EPersistorCapabilities.CREATE,
                                                                  EPersistorCapabilities.UPDATE,
                                                                  EPersistorCapabilities.DELETE,
                                                                  EPersistorCapabilities.READ_TEMPLATES);

        if (isAuthoritative)
        {
            capabilities.add(EPersistorCapabilities.READ);
        }

        return capabilities;
    }

}
