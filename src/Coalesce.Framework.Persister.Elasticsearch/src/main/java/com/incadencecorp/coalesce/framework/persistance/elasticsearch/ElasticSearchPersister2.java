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

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceObject;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.support.AbstractClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class ElasticSearchPersister2 extends ElasticSearchTemplatePersister implements ICoalescePersistor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchPersister2.class);

    @Override
    public boolean saveEntity(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            AbstractClient client = conn.getDBConnector(ElasticSearchSettings.getParameters());

            for (CoalesceEntity entity : entities)
            {
                if (entity.isMarkedDeleted() && allowRemoval)
                {
                    deleteLinkages(entity, client);
                    deleteEntity(entity, client);
                }
                else
                {
                    persistLinkages(entity, client);
                    persistEntityObject(entity, client);
                }
            }
        }

        return true;
    }

    @Override
    public CoalesceEntity[] getEntity(String... keys) throws CoalescePersistorException
    {
        return new CoalesceEntity[0];
    }

    @Override
    public String[] getEntityXml(String... keys) throws CoalescePersistorException
    {
        return new String[0];
    }

    @Override
    public EnumSet<EPersistorCapabilities> getCapabilities()
    {
        return EnumSet.of(EPersistorCapabilities.CREATE, EPersistorCapabilities.UPDATE, EPersistorCapabilities.DELETE);
    }

    private void deleteLinkages(CoalesceEntity entity, AbstractClient conn) throws CoalescePersistorException
    {
        DeleteRequest request = new DeleteRequest();
        request.index("oelinkage");
        request.type("oelinkage");
        request.id(entity.getKey());

        DeleteResponse response = conn.delete(request).actionGet();

        LOGGER.debug("Delete linkage for entity coalesce-{} : {}", entity.getName(), response);

    }

    private void deleteEntity(CoalesceEntity entity, AbstractClient conn) throws CoalescePersistorException
    {
        DeleteRequest request = new DeleteRequest();
        request.index("coalesce-" + entity.getName().toLowerCase());
        request.type(entity.getType().toLowerCase());
        request.id(entity.getKey());

        DeleteResponse response = conn.delete(request).actionGet();

        LOGGER.debug("Delete entity for entity coalesce-{} : {}", entity.getName(), response);
    }

    private void persistLinkages(CoalesceEntity entity, AbstractClient conn) throws CoalescePersistorException
    {
        IndexRequest request = new IndexRequest();
        request.index("oelinkage");
        request.type("oelinkage");
        request.id(entity.getKey());
        request.source(createLinkageMap(entity));

        IndexResponse response = conn.index(request).actionGet();

        LOGGER.debug("Indexed linkage for entity coalesce-{} : {}", entity.getName(), response);
    }

    private void persistEntityObject(CoalesceEntity entity, AbstractClient conn) throws CoalescePersistorException
    {
        // Return true if no update is required.
        //Worry about this later.
        //        if (!checkLastModified(entity, conn))
        //        {
        //            return true;
        //        }

        IndexResponse response;
        IndexRequest request = new IndexRequest();
        request.index("coalesce-" + entity.getName().toLowerCase());
        request.type(entity.getType().toLowerCase());
        request.id(entity.getKey());
        request.source(createValueMap(entity));

        response = conn.index(request).actionGet();

        LOGGER.debug("Saved Index called: coalesce-{} : {}", entity.getName(), response);

        // TODO Remove this test code
        GetRequest getRequest = new GetRequest();
        getRequest.index("coalesce-" + entity.getName().toLowerCase());
        getRequest.type(entity.getType().toLowerCase());
        getRequest.id(entity.getKey());

        GetResponse getResponse = conn.get(getRequest).actionGet();

        LOGGER.debug(getResponse.toString());
    }

    private Map<String, Object> createValueMap(CoalesceEntity entity)
    {
        HashMap<String, Object> map = new HashMap<>();

        getFieldValues(entity, map);

        return map;
    }

    private void getFieldValues(CoalesceObject coalesceObject, Map<String, Object> results)
    {
        // Is Active?
        if (coalesceObject.isActive())
        {
            // Yes; Is a CoalesceField?
            if (coalesceObject.getType().equalsIgnoreCase("field"))
            {
                // Yes; Check Data Type
                CoalesceField<?> field = (CoalesceField<?>) coalesceObject;

                if (field.getBaseValue() != null)
                {
                    String name = normalizeName(field.getName());

                    switch (field.getDataType())
                    {
                    case BINARY_TYPE:
                    case FILE_TYPE:
                        // Ignore these types.
                        break;
                    default:
                        // Add field value to results
                        try
                        {
                            results.put(name, field.getValue());
                            LOGGER.trace("Adding field {} = {}", field.getName(), field.getBaseValue());
                        }
                        catch (CoalesceDataFormatException e)
                        {
                            LOGGER.warn("(FAILED) Adding field {} = {}", field.getName(), field.getBaseValue());
                        }

                        break;
                    }
                }
            }

            // Recurse Through Children
            for (CoalesceObject child : coalesceObject.getChildCoalesceObjects().values())
            {
                getFieldValues(child, results);
            }
        }
    }

    private String normalizeName(String name)
    {
        // TODO
        return name;
    }

    /**
     * persist the Linkage for an Entity. This will create a new document of the Linkage index
     *
     * @param entity The entity to persist the linkages for
     * @return returns true if no exceptions were thrown
     */
    private Map<String, Object> createLinkageMap(CoalesceEntity entity)
    {
        //HashMap representation of the linkage for indexing in ElasticSearch
        HashMap<String, Object> linkageMap = new HashMap<>();

        Map<String, CoalesceLinkage> linkages = entity.getLinkages();
        for (Map.Entry<String, CoalesceLinkage> mlink : linkages.entrySet())
        {
            CoalesceLinkage link = mlink.getValue();

            linkageMap.put(ElasticSearchPersistor.LINKAGE_KEY_COLUMN_NAME, link.getKey());
            linkageMap.put(ElasticSearchPersistor.LINKAGE_LABEL_COLUMN_NAME, link.getName());
            linkageMap.put(ElasticSearchPersistor.LINKAGE_ENTITY1_KEY_COLUMN_NAME, link.getEntity1Key());
            linkageMap.put(ElasticSearchPersistor.LINKAGE_ENTITY1_NAME_COLUMN_NAME, link.getEntity1Name());
            linkageMap.put(ElasticSearchPersistor.LINKAGE_ENTITY1_SOURCE_COLUMN_NAME, link.getEntity1Source());
            linkageMap.put(ElasticSearchPersistor.LINKAGE_ENTITY1_VERSION_COLUMN_NAME, link.getEntity1Version());
            linkageMap.put(ElasticSearchPersistor.LINKAGE_ENTITY2_KEY_COLUMN_NAME, link.getEntity2Key());
            linkageMap.put(ElasticSearchPersistor.LINKAGE_ENTITY2_NAME_COLUMN_NAME, link.getEntity2Name());
            linkageMap.put(ElasticSearchPersistor.LINKAGE_ENTITY2_SOURCE_COLUMN_NAME, link.getEntity2Source());
            linkageMap.put(ElasticSearchPersistor.LINKAGE_LAST_MODIFIED_COLUMN_NAME, link.getLastModifiedAsString());
            linkageMap.put(ElasticSearchPersistor.LINKAGE_LINK_TYPE_COLUMN_NAME, link.getLinkType().getLabel());
        }

        //If the index response is returned and no exception was thrown, the index operation was successful
        return linkageMap;
    }
}
