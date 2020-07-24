package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
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
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.support.AbstractClient;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.io.IOException;

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

    public boolean checkIfIndexExists(RestHighLevelClient client, String index) throws CoalescePersistorException
    {
        try
        {
            index = index.toLowerCase();

            if (!index.startsWith("coalesce"))
            {
                index = "coalesce-" + index;
            }

            GetIndexRequest request = new GetIndexRequest(COALESCE_ENTITY_INDEX + "-" + NORMALIZER.normalize(index));
            return client.indices().exists(request,RequestOptions.DEFAULT);
        }
        catch (IOException e)
        {
            throw new CoalescePersistorException(e);
        }
    }

    @Override
    public boolean saveEntity(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        if (entities != null && entities.length > 0)
        {
            for(int i = 0; i<entities.length; i++)
            {
                if(entities[i].getName().equalsIgnoreCase("IDENTITYHUBMISSION") )
                {
                    try
                    {
                        try
                        {
                            CoalesceEntityTemplate template = CoalesceEntityTemplate.create(entities[i]);
                            FileWriter fileWriter = new FileWriter("C:\\Users\\GiovanniGaito\\Desktop\\replication\\coalesce\\src\\Coalesce.Objects\\templates\\" + template.getKey());
                            PrintWriter printWriter = new PrintWriter(fileWriter);
                            printWriter.print(template.toXml());
                            printWriter.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    catch (CoalesceException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
            {
                RestHighLevelClient client = conn.getDBConnector(params);

                BulkRequest request = iterator.iterate(allowRemoval, entities);

                executeCleanup(client, request, entities);

                LOGGER.debug("{} Entities Created {} Request", entities.length, request.requests().size());

                executeRequest(client, request, 1);
            }
            catch (CoalesceException | InterruptedException e)
            {
                throw new CoalescePersistorException(e);
            }
        }

        return true;
    }

    private void executeCleanup(RestHighLevelClient client, BulkRequest request, CoalesceEntity... entities) throws CoalescePersistorException
    {
        Map<String, Set<String>> recordKeys = new HashMap<>();
        List<String> keys = new ArrayList<>();

        try
        {
            // Create List of Indices
            for (DocWriteRequest task : request.requests())
            {
                // Don't cleanup coalesce index
                if (!task.index().equalsIgnoreCase(COALESCE_ENTITY_INDEX))
                {
                    if (!recordKeys.containsKey(task.index()))
                    {
                        recordKeys.put(task.index(), new HashSet<>());
                    }

                    recordKeys.get(task.index()).add(task.id());
                }
            }

            if (!recordKeys.isEmpty())
            {
                int original = request.requests().size();

                StopWatch watch = new StopWatch();
                watch.start();

                for (CoalesceEntity entity : entities)
                {
                    keys.add(entity.getKey().toLowerCase());
                }

                // Build Query; only records belonging to an entity being updated
                BoolQueryBuilder query = QueryBuilders.boolQuery();
                query.should().add(QueryBuilders.termsQuery(ENTITY_KEY_COLUMN_NAME, keys.toArray(new String[keys.size()])));

                int pageSize = 1000;

                SearchRequest searchRequest = new SearchRequest(recordKeys.keySet().toArray(new String[recordKeys.keySet().size()]));
                searchRequest.source().query(query);
                searchRequest.source().size(pageSize);
                searchRequest.scroll(new TimeValue(60000));

                if (LOGGER.isTraceEnabled())
                {
                    LOGGER.trace("Query: {} Indices: {}", query, recordKeys.keySet());
                }

                SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

                while (response.getHits().getHits().length != 0)
                {
                    for (SearchHit hit : response.getHits().getHits())
                    {
                        // Records being updated by this request
                        if (!recordKeys.get(hit.getIndex()).contains(hit.getId()))
                        {
                            // This check should not be required
                            String key = (String) hit.getSourceAsMap().get(ENTITY_KEY_COLUMN_NAME);

                            // Verify the record belongs to an entity being updated
                            if (key != null && keys.contains(key))
                            {
                                // Create Delete Request
                                DeleteRequest deleteRequest = new DeleteRequest();
                                deleteRequest.index(hit.getIndex());
                                deleteRequest.type(hit.getType());
                                deleteRequest.id(hit.getId());

                                request.requests().add(deleteRequest);

                                if (LOGGER.isTraceEnabled())
                                {
                                    LOGGER.trace("Deleting Phantom Record: ({}) From {}", hit.getId(), hit.getIndex());
                                }
                            }
                            else if (LOGGER.isTraceEnabled())
                            {
                                // Should never trigger therefore log it as a warning.
                                LOGGER.warn("Ignoring Phantom Record: ({}) in ({}) From {}", hit.getId(), key, hit.getIndex());
                            }
                        }
                    }

                    SearchScrollRequest scrollRequest = new SearchScrollRequest(response.getScrollId());
                    scrollRequest.scroll(new TimeValue(60000));
                    response = client.scroll(scrollRequest, RequestOptions.DEFAULT);
                }

                watch.finish();

                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("Created {} Delete Request for Phantom Records in {} ms",
                            request.requests().size() - original,
                            watch.getWorkLife());
                }
            }
        }
        catch (IOException e)
        {
            throw new CoalescePersistorException(e.getMessage(),e);
        }
    }

    private void executeRequest(RestHighLevelClient client, BulkRequest request, int attempt)
            throws CoalescePersistorException, InterruptedException
    {

        if (!request.requests().isEmpty())
        {
            try
            {
                BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);

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
                    Thread.sleep(millis * attempt);

                    if (LOGGER.isDebugEnabled())
                    {
                        LOGGER.debug("Awaken ({})", Thread.currentThread().getId());
                    }

                    LOGGER.warn("(RETRYING) No Nodes Available - Attempt: {} after backing off for {} ms", attempt, millis);
                    executeRequest(client, request, ++attempt);
                }
            }
            catch (IOException e)
            {
                throw new CoalescePersistorException("(FAILED) Executing Request",e);
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
            RestHighLevelClient client = conn.getDBConnector(params);

            for (String key : keys)
            {
                GetRequest request = new GetRequest();
                request.index(COALESCE_ENTITY_INDEX);
                request.id(key);

                GetResponse getResponse = client.get(request, RequestOptions.DEFAULT);

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
        catch (IOException e)
        {
            throw new CoalescePersistorException(e.getMessage(),e);
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
