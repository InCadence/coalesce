package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.support.AbstractClient;
import org.elasticsearch.common.geo.builders.ShapeBuilder;
import org.elasticsearch.rest.RestStatus;
import org.json.JSONArray;
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
        FilePropertyConnector fileConnector = new FilePropertyConnector(CoalesceParameters.COALESCE_CONFIG_LOCATION);
        fileConnector.setReadOnly(true);

        ElasticSearchSettings.setConnector(fileConnector);

        LOGGER.debug("Initialized ElasticSearchPersistor using default constructor");
    }

    public void makeSureConnectorIsInitialized()
    {
        if (!ElasticSearchSettings.getConnectorInitialized())
        {
            FilePropertyConnector connector = new FilePropertyConnector(CoalesceParameters.COALESCE_CONFIG_LOCATION);
            LOGGER.debug("Connector initialized using config file: " + CoalesceParameters.COALESCE_CONFIG_LOCATION);
            connector.setReadOnly(true);

            ElasticSearchSettings.setConnector(connector);
        }
    }

    /*--------------------------------------------------------------------------
    Protected Methods
    --------------------------------------------------------------------------*/

    public boolean checkIfIndexExists(AbstractClient client, String index)
    {

        try
        {
            boolean hasIndex = client.admin().indices().exists(new IndicesExistsRequest(index.toLowerCase())).actionGet().isExists();

            return hasIndex;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean saveEntity(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        try (ElasticSearchDataConnector conn = new ElasticSearchDataConnector())
        {
            AbstractClient client = conn.getDBConnector(ElasticSearchSettings.getParameters());

            for (CoalesceEntity entity : entities)
            {
                // TODO Execute these in parallel.
                if (entity.isMarkedDeleted() && allowRemoval)
                {
                    deleteEntityIndex(entity, client);
                    deleteLinkages(entity, client);
                    deleteEntity(entity, client);
                }
                else
                {
                    persistEntityIndex(entity, client);
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
            AbstractClient client = conn.getDBConnector(ElasticSearchSettings.getParameters());

            for (String key : keys)
            {
                GetRequest request = new GetRequest();
                request.index(COALESCE_ENTITY_INDEX);
                request.type("entity");
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
                LOGGER.error(e.getDetailedMessage());
            }
        }

        return results.toArray(new String[results.size()]);
    }

    @Override
    public EnumSet<EPersistorCapabilities> getCapabilities()
    {
        return EnumSet.of(EPersistorCapabilities.CREATE,
                          EPersistorCapabilities.READ,
                          EPersistorCapabilities.UPDATE,
                          EPersistorCapabilities.DELETE,
                          EPersistorCapabilities.READ_TEMPLATES);
    }

    private void deleteEntity(CoalesceEntity entity, AbstractClient conn) throws CoalescePersistorException
    {
        deleteFromElasticSearch(conn,
                                "coalesce-" + entity.getName().toLowerCase(),
                                entity.getType().toLowerCase(),
                                entity.getKey());
    }

    private void deleteLinkages(CoalesceEntity entity, AbstractClient conn) throws CoalescePersistorException
    {
        deleteFromElasticSearch(conn, COALESCE_LINKAGE_INDEX, "linkages", entity.getKey());
    }

    private void deleteEntityIndex(CoalesceEntity entity, AbstractClient conn) throws CoalescePersistorException
    {
        deleteFromElasticSearch(conn, COALESCE_ENTITY_INDEX, "entity", entity.getKey());
    }

    private void persistLinkages(CoalesceEntity entity, AbstractClient conn) throws CoalescePersistorException
    {
        Map<String, Object> common = createEntityMapping(entity);

        // TODO Perform Task in Parallel.
        for (CoalesceLinkage linkage : entity.getLinkages().values())
        {
            Map<String, Object> source = createLinkageMap(linkage);
            source.putAll(common);

            IndexRequest request = new IndexRequest();
            request.index(COALESCE_LINKAGE_INDEX);
            request.type(COALESCE_LINKAGE);
            request.id(linkage.getKey());
            request.source(source);

            IndexResponse response = conn.index(request).actionGet();

            // TODO Verify Response
        }

        LOGGER.debug("Indexed linkage for entity {}", entity.getKey());
    }

    private void persistEntityObject(CoalesceEntity entity, AbstractClient conn) throws CoalescePersistorException
    {
        Map<String, Object> source = createValueMap(entity);

        source.putAll(createEntityMapping(entity));

        IndexRequest request = new IndexRequest();
        request.index(COALESCE_ENTITY_INDEX + "-" + normalize(entity.getName()));
        request.type(COALESCE_ENTITY);
        request.id(entity.getKey());
        request.source(source);

        IndexResponse response = conn.index(request).actionGet();

        LOGGER.debug("Saved Index called: coalesce-{} : {}", entity.getName(), response);
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
                    String name = normalize(field);

                    try
                    {
                        LOGGER.trace("Adding field {} = {}", name, field.getBaseValue());

                        switch (field.getDataType())
                        {
                        case BINARY_TYPE:
                        case FILE_TYPE:
                            // Ignore these types.
                            break;
                        case LINE_STRING_TYPE:
                            results.put(name, createLineString((CoalesceLineStringField) field));
                            break;
                        case POLYGON_TYPE:
                            results.put(name, createPolygon((CoalescePolygonField) field));
                            break;
                        case CIRCLE_TYPE:
                            results.put(name, createCircle((CoalesceCircleField) field));
                            break;
                        case GEOCOORDINATE_TYPE:
                            Point point = ((CoalesceCoordinateField) field).getValueAsPoint();
                            results.put(name, point.getX() + ", " + point.getY());
                            break;
                        case GEOCOORDINATE_LIST_TYPE:
                            results.put(name, createMultiPoint((CoalesceCoordinateListField) field));
                            break;
                        default:
                            // Add field value to results
                            results.put(name, field.getValue());
                            break;
                        }
                    }
                    catch (CoalesceDataFormatException e)
                    {
                        LOGGER.warn("(FAILED) Adding field {} = {}", name, field.getBaseValue());
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

    private Map<String, Object> createPolygon(CoalescePolygonField field) throws CoalesceDataFormatException
    {
        Map<String, Object> results = null;

        if (field.getValue().getCoordinates().length > 0)
        {
            JSONArray polygon = new JSONArray();
            polygon.put(getCoordinates(field.getValue().getCoordinates()));

            results = new HashMap<>();
            results.put("type", ShapeBuilder.GeoShapeType.POLYGON);
            results.put("coordinates", polygon);
        }

        return results;
    }

    private Map<String, Object> createLineString(CoalesceLineStringField field) throws CoalesceDataFormatException
    {
        Map<String, Object> results = new HashMap<>();
        results.put("type", ShapeBuilder.GeoShapeType.LINESTRING);
        results.put("coordinates", getCoordinates(field.getValue().getCoordinates()));

        return results;
    }

    private Map<String, Object> createCircle(CoalesceCircleField field) throws CoalesceDataFormatException
    {
        CoalesceCircle circle = field.getValue();

        JSONArray point = new JSONArray();
        point.put(circle.getCenter().x);
        point.put(circle.getCenter().y);

        Map<String, Object> results = new HashMap<>();
        results.put("type", ShapeBuilder.GeoShapeType.CIRCLE);
        results.put("coordinates", point);
        results.put("radius", circle.getRadius());

        return results;
    }

    private Map<String, Object> createMultiPoint(CoalesceCoordinateListField field) throws CoalesceDataFormatException
    {
        Map<String, Object> results = new HashMap<>();
        results.put("type", ShapeBuilder.GeoShapeType.MULTIPOINT);
        results.put("coordinates", getCoordinates(field.getValue()));

        return results;
    }

    private JSONArray getCoordinates(Coordinate[] coords)
    {
        JSONArray results = null;

        if (coords.length > 0)
        {
            results = new JSONArray();

            for (Coordinate coord : coords)
            {
                JSONArray coord2 = new JSONArray();
                coord2.put(coord.x);
                coord2.put(coord.y);

                results.put(coord2);
            }
        }

        return results;
    }

    private class GeoJSONPoint {

        private String type;
        private Coordinate[] coordinates;
    }

    private void persistEntityIndex(CoalesceEntity entity, AbstractClient conn)
    {
        Map<String, Object> properties = createEntityMapping(entity);

        if (ElasticSearchSettings.getStoreXML())
        {
            properties.put(CoalescePropertyFactory.getEntityXml().getPropertyName(), entity.toXml());
        }

        IndexRequest request = new IndexRequest();
        request.index(COALESCE_ENTITY_INDEX);
        request.type(COALESCE_ENTITY);
        request.id(entity.getKey());
        request.source(properties);

        IndexResponse response = conn.index(request).actionGet();

        LOGGER.debug("Saved XML Index called: coalesceentityindex : {}", response);
    }

    private Map<String, Object> createEntityMapping(CoalesceEntity entity)
    {
        Map<String, Object> properties = new HashMap<>();

        properties.put(ENTITY_KEY_COLUMN_NAME, entity.getKey());
        properties.put(ENTITY_NAME_COLUMN_NAME, entity.getName());
        properties.put(ENTITY_SOURCE_COLUMN_NAME, entity.getSource());
        properties.put(ENTITY_VERSION_COLUMN_NAME, entity.getVersion());
        properties.put(ENTITY_DATE_CREATED_COLUMN_NAME, entity.getDateCreated());
        properties.put(ENTITY_LAST_MODIFIED_COLUMN_NAME, entity.getLastModified());

        return properties;
    }

    /**
     * persist the Linkage for an Entity. This will create a new document of the Linkage index
     *
     * @param linkage The entity's linkage to persist
     * @return returns true if no exceptions were thrown
     */
    private Map<String, Object> createLinkageMap(CoalesceLinkage linkage)
    {
        //HashMap representation of the linkage for indexing in ElasticSearch
        Map<String, Object> properties = new HashMap<>();

        properties.put(LINKAGE_KEY_COLUMN_NAME, linkage.getKey());

        /*
        properties.put(ENTITY_KEY_COLUMN_NAME, linkage.getEntity1Key());
        properties.put(ENTITY_NAME_COLUMN_NAME, linkage.getEntity1Name());
        properties.put(ENTITY_SOURCE_COLUMN_NAME, linkage.getEntity1Source());
        properties.put(ENTITY_VERSION_COLUMN_NAME, linkage.getEntity1Version());
        */

        properties.put(LINKAGE_ENTITY2_KEY_COLUMN_NAME, linkage.getEntity2Key());
        properties.put(LINKAGE_ENTITY2_NAME_COLUMN_NAME, linkage.getEntity2Name());
        properties.put(LINKAGE_ENTITY2_SOURCE_COLUMN_NAME, linkage.getEntity2Source());
        properties.put(LINKAGE_ENTITY2_VERSION_COLUMN_NAME, linkage.getEntity2Version());

        properties.put(LINKAGE_DATE_CREATED_COLUMN_NAME, linkage.getDateCreated());
        properties.put(LINKAGE_LAST_MODIFIED_COLUMN_NAME, linkage.getLastModifiedAsString());
        properties.put(LINKAGE_LABEL_COLUMN_NAME, linkage.getName());
        properties.put(LINKAGE_STATUS_COLUMN_NAME, linkage.getStatus().value());
        properties.put(LINKAGE_LINK_TYPE_COLUMN_NAME, linkage.getLinkType().getLabel());

        //If the index response is returned and no exception was thrown, the index operation was successful
        return properties;
    }
}
