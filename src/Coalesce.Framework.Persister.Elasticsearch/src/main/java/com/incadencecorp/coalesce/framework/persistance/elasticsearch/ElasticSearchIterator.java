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

import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.framework.iterators.CoalesceIterator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.geo.builders.ShapeBuilder;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class ElasticSearchIterator extends CoalesceIterator<ElasticSearchIterator.Parameters> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchIterator.class);

    private final ICoalesceNormalizer normalizer;
    private final boolean isAuthoritative;

    public ElasticSearchIterator(ICoalesceNormalizer normalizer, boolean isAuthoritative)
    {
        if (normalizer == null)
        {
            throw new IllegalArgumentException("Normalizer cannot be null");
        }

        this.normalizer = normalizer;
        this.isAuthoritative = isAuthoritative;
    }

    public BulkRequest iterate(boolean allowRemoval, CoalesceEntity... entities) throws CoalesceException
    {
        BulkRequest result = new BulkRequest();

        for (CoalesceEntity entity : entities)
        {
            if (entity != null)
            {
                Parameters params = new Parameters(entity);
                params.allowRemoval = allowRemoval;

                processAllElements(entity, params);

                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("Entity Key: {} Name: {} Created {} Request",
                                 entity.getKey(),
                                 entity.getName(),
                                 params.request.requests().size());
                }

                result.add(params.request.requests());
            }
        }

        return result;
    }

    @Override
    protected boolean visitCoalesceEntity(CoalesceEntity entity, Parameters param) throws CoalesceException
    {
        Map<String, Object> source = createMapping(entity);

        if (isAuthoritative)
        {
            source.put(ElasticSearchPersistor.FIELD_XML, entity.toXml());
        }
        source.put(ElasticSearchPersistor.ENTITY_TITLE_COLUMN_NAME, entity.getTitle());

        DocWriteRequest request;

        if (param.allowRemoval)
        {
            request = visitObject(entity,
                                  ElasticSearchPersistor.COALESCE_ENTITY_INDEX,
                                  ElasticSearchPersistor.COALESCE_ENTITY,
                                  source);
        }
        else
        {
            request = new IndexRequest();
            ((IndexRequest) request).index(ElasticSearchPersistor.COALESCE_ENTITY_INDEX);
            ((IndexRequest) request).type(ElasticSearchPersistor.COALESCE_ENTITY);
            ((IndexRequest) request).id(entity.getKey());
            ((IndexRequest) request).source(source);
        }

        param.request.add(request);

        return true;
    }

    @Override
    protected boolean visitCoalesceLinkageSection(CoalesceLinkageSection section, Parameters param) throws CoalesceException
    {
        for (CoalesceLinkage linkage : section.getLinkages().values())
        {
            Map<String, Object> mapping = createMapping(linkage);
            mapping.putAll(param.common);

            param.request.add(visitObject(linkage,
                                          ElasticSearchPersistor.COALESCE_LINKAGE_INDEX,
                                          ElasticSearchPersistor.COALESCE_LINKAGE,
                                          mapping));
        }

        return false;
    }

    @Override
    protected boolean visitCoalesceRecordset(CoalesceRecordset recordset, Parameters param) throws CoalesceException
    {
        //String type = normalize(recordset.getName());
        if (recordset.isFlatten())
        {
            for (CoalesceRecord record : recordset.getRecords())
            {
                if (record.isFlatten())
                {
                    Map<String, Object> mapping = createMapping(record);
                    mapping.putAll(param.common);

                    param.request.add(visitObject(record, param.recordIndex, "recordset", mapping));
                }
            }
        }

        // Stop Recursion
        return false;
    }

    private DocWriteRequest visitObject(CoalesceObject object, String index, String type, Map<String, Object> source)
    {
        if (!object.isMarkedDeleted() && !object.getEntity().isMarkedDeleted())
        {
            IndexRequest request = new IndexRequest();
            request.index(index);
            request.type(type);
            request.id(object.getKey());
            request.source(source);

            return request;
        }
        else
        {
            DeleteRequest request = new DeleteRequest();
            request.index(index);
            request.type(type);
            request.id(object.getKey());

            return request;
        }
    }

    private Map<String, Object> createMapping(CoalesceEntity entity)
    {
        Map<String, Object> properties = new HashMap<>();

        properties.put(ElasticSearchPersistor.ENTITY_KEY_COLUMN_NAME, entity.getKey());
        properties.put(ElasticSearchPersistor.ENTITY_NAME_COLUMN_NAME, entity.getName());
        properties.put(ElasticSearchPersistor.ENTITY_SOURCE_COLUMN_NAME, entity.getSource());
        properties.put(ElasticSearchPersistor.ENTITY_VERSION_COLUMN_NAME, entity.getVersion());
        properties.put(ElasticSearchPersistor.ENTITY_DATE_CREATED_COLUMN_NAME, entity.getDateCreated());
        properties.put(ElasticSearchPersistor.ENTITY_LAST_MODIFIED_COLUMN_NAME, entity.getLastModified());
        properties.put(ElasticSearchPersistor.ENTITY_STATUS_COLUMN_NAME, entity.getStatus().value());
        properties.put(ElasticSearchPersistor.ENTITY_ID_COLUMN_NAME, entity.getEntityId());
        properties.put(ElasticSearchPersistor.ENTITY_TITLE_COLUMN_NAME, entity.getTitle());

        return properties;
    }

    private Map<String, Object> createMapping(CoalesceLinkage linkage)
    {
        //HashMap representation of the linkage for indexing in ElasticSearch
        Map<String, Object> source = new HashMap<>();

        source.put(ElasticSearchPersistor.LINKAGE_KEY_COLUMN_NAME, linkage.getKey());

        source.put(ElasticSearchPersistor.LINKAGE_ENTITY2_KEY_COLUMN_NAME, linkage.getEntity2Key());
        source.put(ElasticSearchPersistor.LINKAGE_ENTITY2_NAME_COLUMN_NAME, linkage.getEntity2Name());
        source.put(ElasticSearchPersistor.LINKAGE_ENTITY2_SOURCE_COLUMN_NAME, linkage.getEntity2Source());
        source.put(ElasticSearchPersistor.LINKAGE_ENTITY2_VERSION_COLUMN_NAME, linkage.getEntity2Version());

        source.put(ElasticSearchPersistor.LINKAGE_DATE_CREATED_COLUMN_NAME, linkage.getDateCreated());
        source.put(ElasticSearchPersistor.LINKAGE_LAST_MODIFIED_COLUMN_NAME, linkage.getLastModifiedAsString());
        source.put(ElasticSearchPersistor.LINKAGE_LABEL_COLUMN_NAME, linkage.getName());
        source.put(ElasticSearchPersistor.LINKAGE_STATUS_COLUMN_NAME, linkage.getStatus().value());
        source.put(ElasticSearchPersistor.LINKAGE_LINK_TYPE_COLUMN_NAME, linkage.getLinkType().getLabel());

        //If the index response is returned and no exception was thrown, the index operation was successful
        return source;
    }

    private Map<String, Object> createMapping(CoalesceRecord record) throws CoalesceDataFormatException
    {
        Map<String, Object> source = new HashMap<>();

        for (CoalesceField field : record.getFields())
        {
            if (field.isFlatten() && field.getBaseValue() != null)
            {
                String name = normalize(field);

                switch (field.getDataType())
                {
                case BINARY_TYPE:
                case FILE_TYPE:
                    // Ignore these types.
                    break;
                case LINE_STRING_TYPE:
                    source.put(name, createLineString((CoalesceLineStringField) field));
                    break;
                case POLYGON_TYPE:
                    source.put(name, createPolygon((CoalescePolygonField) field));
                    break;
                case CIRCLE_TYPE:
                    source.put(name, createCircle((CoalesceCircleField) field));
                    break;
                case GEOCOORDINATE_TYPE:
                    Point point = ((CoalesceCoordinateField) field).getValueAsPoint();
                    if (point != null)
                    {
                        source.put(name, point.getY() + ", " + point.getX());
                    }
                    break;
                case GEOCOORDINATE_LIST_TYPE:
                    source.put(name, createMultiPoint((CoalesceCoordinateListField) field));
                    break;
                default:
                    // Add field value to results
                    source.put(name, field.getValue());
                    break;
                }
            }
        }

        return source;
    }

    private Map<String, Object> createPoint(CoalesceCoordinateField field) throws CoalesceDataFormatException
    {
        Map<String, Object> results = null;

        if (field.getValue() != null)
        {
            JSONArray coord = new JSONArray();
            coord.put(field.getValue().x);
            coord.put(field.getValue().y);

            results = new HashMap<>();
            results.put("type", ShapeBuilder.GeoShapeType.POINT);
            results.put("coordinates", coord);
        }

        return results;
    }

    private Map<String, Object> createPolygon(CoalescePolygonField field) throws CoalesceDataFormatException
    {
        Map<String, Object> results = null;

        if (field.getValue() != null)
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
        Map<String, Object> results = null;

        if (field.getValue() != null)
        {
            results = new HashMap<>();
            results.put("type", ShapeBuilder.GeoShapeType.LINESTRING);
            results.put("coordinates", getCoordinates(field.getValue().getCoordinates()));
        }

        return results;
    }

    private Map<String, Object> createCircle(CoalesceCircleField field) throws CoalesceDataFormatException
    {
        Map<String, Object> results = null;
        CoalesceCircle circle = field.getValue();

        if (circle != null)
        {
            JSONArray point = new JSONArray();
            point.put(circle.getCenter().x);
            point.put(circle.getCenter().y);

            results = new HashMap<>();
            results.put("type", ShapeBuilder.GeoShapeType.CIRCLE);
            results.put("coordinates", point);
            results.put("radius", circle.getRadius() + "m");
        }

        return results;
    }

    private Map<String, Object> createMultiPoint(CoalesceCoordinateListField field) throws CoalesceDataFormatException
    {
        Map<String, Object> results = null;

        if (field.getValue() != null)
        {
            results = new HashMap<>();
            results.put("type", ShapeBuilder.GeoShapeType.MULTIPOINT);
            results.put("coordinates", getCoordinates(field.getValue()));
        }

        return results;
    }

    private JSONArray getCoordinates(Coordinate[] coords)
    {
        JSONArray results = null;

        if (coords != null && coords.length > 0)
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

    private String normalize(CoalesceField<?> field)
    {
        return normalizer.normalize(field.getParent().getParent().getName(), field.getName());
    }

    /**
     * Internal class used for passing parameters within this iterator.
     */
    public class Parameters {

        private Parameters(CoalesceEntity entity)
        {
            common = createMapping(entity);
            recordIndex = ElasticSearchPersistor.COALESCE_ENTITY_INDEX + "-" + normalizer.normalize(entity.getName());
        }

        private Map<String, Object> common;
        private String recordIndex;
        private BulkRequest request = new BulkRequest();
        private boolean allowRemoval;
    }
}
