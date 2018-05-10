package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.support.AbstractClient;
import org.elasticsearch.rest.RestStatus;
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
        IndicesExistsRequest request = new IndicesExistsRequest();
        request.indices(index.toLowerCase());

        IndicesExistsResponse response = client.admin().indices().exists(request).actionGet();
        return response.isExists();
    }

    @Override
    public boolean saveEntity(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            AbstractClient client = conn.getDBConnector(params);

            BulkRequest request = iterator.iterate(entities);
            BulkResponse response = client.bulk(request).actionGet();

            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("{} Entities Created {} Request", entities.length, request.requests().size());

                for (BulkItemResponse item : response.getItems())
                {
                    LOGGER.trace("({}) ID = {}, Index = {}, Type = {} : {}",
                                 item.status().toString(),
                                 item.getId(),
                                 item.getIndex(),
                                 item.getType(),
                                 LOGGER.isTraceEnabled() ? response : response.getClass().getSimpleName());
                }
            }
        }
        catch (CoalesceException e)
        {
            throw new CoalescePersistorException(e);
        }

        return true;
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
