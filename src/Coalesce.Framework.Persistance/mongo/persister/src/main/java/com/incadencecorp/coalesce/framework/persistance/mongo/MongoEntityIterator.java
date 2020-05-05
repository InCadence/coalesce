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

package com.incadencecorp.coalesce.framework.persistance.mongo;

import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.framework.iterators.CoalesceIterator;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.DeleteOneModel;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.model.geojson.LineString;
import com.mongodb.client.model.geojson.MultiPoint;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Polygon;
import com.mongodb.client.model.geojson.Position;
import org.locationtech.jts.geom.Coordinate;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Iterator used to convert a Coalesce Entity into write request for CRUD operations.
 *
 * @author Derek Clemenzi
 */
public class MongoEntityIterator extends CoalesceIterator<MongoEntityIterator.Parameters> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoEntityIterator.class);

    private final ICoalesceNormalizer normalizer;
    private final MongoClient client;
    private final boolean isAuthoritative;

    public MongoEntityIterator(MongoClient client, ICoalesceNormalizer normalizer, boolean isAuthoritative)
    {
        if (normalizer == null)
        {
            throw new IllegalArgumentException("Normalizer cannot be null");
        }

        this.client = client;
        this.normalizer = normalizer;
        this.isAuthoritative = isAuthoritative;
    }

    public Map<String, List<WriteModel<Document>>> iterate(boolean allowRemoval, CoalesceEntity... entities)
            throws CoalesceException
    {
        Map<String, List<WriteModel<Document>>> writes = new HashMap<>();

        for (CoalesceEntity entity : entities)
        {
            if (entity != null)
            {
                Parameters params = new Parameters(entity);
                params.allowRemoval = allowRemoval;

                processAllElements(entity, params);

                for (Map.Entry<String, List<WriteModel<Document>>> entry : params.writes.entrySet())
                {
                    if (!writes.containsKey(entry.getKey()))
                    {
                        writes.put(entry.getKey(), new ArrayList<>());
                    }
                    writes.get(entry.getKey()).addAll(entry.getValue());
                }
            }
        }

        return writes;
    }

    @Override
    protected boolean visitCoalesceEntity(CoalesceEntity entity, Parameters param) throws CoalesceException
    {
        Map<String, Object> mapping = createMapping(entity);
        mapping.putAll(param.common);
        mapping.put(MongoConstants.COLUMN_ID, entity.getKey());

        if (isAuthoritative)
        {
            mapping.put(MongoConstants.FIELD_XML, entity.toXml());
        }

        WriteModel<Document> write = createWrite(MongoConstants.COLLECTION_ENTITIES,
                                                 new Document(mapping),
                                                 param.allowRemoval && entity.isMarkedDeleted());

        if (write != null)
        {
            param.addWrite(MongoConstants.COLLECTION_ENTITIES, write);
        }

        return true;
    }

    @Override
    protected boolean visitCoalesceLinkageSection(CoalesceLinkageSection section, Parameters param) throws CoalesceException
    {
        for (CoalesceLinkage linkage : section.getLinkages().values())
        {
            Map<String, Object> mapping = createMapping(linkage);
            mapping.putAll(param.common);

            WriteModel<Document> write = createWrite(MongoConstants.COLLECTION_LINKAGES,
                                                     new Document(mapping),
                                                     linkage.isMarkedDeleted() || linkage.getEntity().isMarkedDeleted());

            if (write != null)
            {
                param.addWrite(MongoConstants.COLLECTION_LINKAGES, write);
            }
        }

        return false;
    }

    @Override
    protected boolean visitCoalesceRecordset(CoalesceRecordset recordset, Parameters param) throws CoalesceException
    {
        if (recordset.isFlatten())
        {
            for (CoalesceRecord record : recordset.getAllRecords())
            {
                if (record.isFlatten())
                {
                    Map<String, Object> mapping = createMapping(record);
                    mapping.putAll(param.common);

                    String collectionId = MongoConstants.getCollectionName(record.getEntity().getName());
                    WriteModel<Document> write = createWrite(collectionId,
                                                             new Document(mapping),
                                                             record.isMarkedDeleted()
                                                                     || record.getEntity().isMarkedDeleted());

                    if (write != null)
                    {
                        param.addWrite(collectionId, write);
                    }
                }
            }
        }

        // Stop Recursion
        return false;
    }

    private WriteModel<Document> createWrite(String collectionId, Document document, boolean delete)
    {
        WriteModel<Document> write = null;

        String id = document.getString(MongoConstants.COLUMN_ID);

        if (delete)
        {
            if (exists(id, collectionId))
            {
                write = new DeleteOneModel<>(new Document(MongoConstants.COLUMN_ID, id));
            }
        }
        else
        {
            if (exists(id, collectionId))
            {
                write = new ReplaceOneModel<>(new Document(MongoConstants.COLUMN_ID, id), document);
            }
            else
            {
                write = new InsertOneModel<>(document);
            }
        }

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("{} - Id: {} EntityKey: {} Collection: {}",
                         (write != null) ? write.getClass().getSimpleName() : "Skipping",
                         document.get(MongoConstants.COLUMN_ID),
                         document.get(MongoConstants.ENTITY_KEY_COLUMN_NAME),
                         collectionId);
        }

        return write;
    }

    private Map<String, Object> createMapping(CoalesceEntity entity)
    {
        Map<String, Object> properties = new HashMap<>();

        properties.put(MongoConstants.ENTITY_KEY_COLUMN_NAME, entity.getKey());
        properties.put(MongoConstants.ENTITY_NAME_COLUMN_NAME, entity.getName());
        properties.put(MongoConstants.ENTITY_SOURCE_COLUMN_NAME, entity.getSource());
        properties.put(MongoConstants.ENTITY_VERSION_COLUMN_NAME, entity.getVersion());
        properties.put(MongoConstants.ENTITY_DATE_CREATED_COLUMN_NAME,
                       entity.getDateCreated() != null ? entity.getDateCreated().toDate() : null);
        properties.put(MongoConstants.ENTITY_CREATED_BY_COLUMN_NAME, entity.getCreatedBy());
        properties.put(MongoConstants.ENTITY_LAST_MODIFIED_COLUMN_NAME,
                       entity.getLastModified() != null ? entity.getLastModified().toDate() : null);
        properties.put(MongoConstants.ENTITY_LAST_MODIFIED_BY_COLUMN_NAME, entity.getModifiedBy());
        properties.put(MongoConstants.ENTITY_STATUS_COLUMN_NAME, entity.getStatus().value());
        properties.put(MongoConstants.ENTITY_ID_COLUMN_NAME, entity.getEntityId());
        properties.put(MongoConstants.ENTITY_ID_TYPE_COLUMN_NAME, entity.getEntityIdType());
        properties.put(MongoConstants.ENTITY_TITLE_COLUMN_NAME, entity.getTitle());

        return properties;
    }

    private Map<String, Object> createMapping(CoalesceLinkage linkage)
    {
        //HashMap representation of the linkage for indexing in ElasticSearch
        Map<String, Object> properties = new HashMap<>();

        properties.put(MongoConstants.COLUMN_ID, linkage.getKey());
        properties.put(MongoConstants.LINKAGE_KEY_COLUMN_NAME, linkage.getKey());

        properties.put(MongoConstants.LINKAGE_ENTITY2_KEY_COLUMN_NAME, linkage.getEntity2Key());
        properties.put(MongoConstants.LINKAGE_ENTITY2_NAME_COLUMN_NAME, linkage.getEntity2Name());
        properties.put(MongoConstants.LINKAGE_ENTITY2_SOURCE_COLUMN_NAME, linkage.getEntity2Source());
        properties.put(MongoConstants.LINKAGE_ENTITY2_VERSION_COLUMN_NAME, linkage.getEntity2Version());

        properties.put(MongoConstants.LINKAGE_DATE_CREATED_COLUMN_NAME,
                       linkage.getDateCreated() != null ? linkage.getDateCreated().toDate() : null);
        properties.put(MongoConstants.LINKAGE_LAST_MODIFIED_COLUMN_NAME,
                       linkage.getLastModified() != null ? linkage.getLastModified().toDate() : null);
        properties.put(MongoConstants.LINKAGE_LABEL_COLUMN_NAME, linkage.getLabel());
        properties.put(MongoConstants.LINKAGE_STATUS_COLUMN_NAME, linkage.getStatus().toString());
        properties.put(MongoConstants.LINKAGE_LINK_TYPE_COLUMN_NAME, linkage.getLinkType().getLabel());

        //If the index response is returned and no exception was thrown, the index operation was successful
        return properties;
    }

    private Map<String, Object> createMapping(CoalesceRecord record) throws CoalesceDataFormatException
    {
        Map<String, Object> properties = new HashMap<>();
        properties.put(MongoConstants.COLUMN_ID, record.getKey());

        for (CoalesceField field : record.getFields())
        {
            if (field.isFlatten() && (field.getBaseValue() != null
                    || field.getDataType() == ECoalesceFieldDataTypes.FILE_TYPE))
            {
                String name = normalize(field);

                switch (field.getDataType())
                {
                case BINARY_TYPE:
                    // Ignore these types.
                    break;
                case FILE_TYPE:
                    if (field instanceof CoalesceFileField)
                    {
                        properties.put(name, ((CoalesceFileField) field).getFilename());
                    }
                    break;
                case LINE_STRING_TYPE:

                    properties.put(name, createLineString((CoalesceLineStringField) field));
                    break;
                case POLYGON_TYPE:
                    properties.put(name, createPolygon((CoalescePolygonField) field));
                    break;
                case CIRCLE_TYPE:
                    properties.put(name, createCircle((CoalesceCircleField) field));
                    break;
                case GEOCOORDINATE_TYPE:
                    properties.put(name, createPoint((CoalesceCoordinateField) field));
                    break;
                case GEOCOORDINATE_LIST_TYPE:
                    properties.put(name, createMultiPoint((CoalesceCoordinateListField) field));
                    break;
                case DATE_TIME_TYPE:
                    properties.put(name,
                                   field.getValue() != null ? ((CoalesceDateTimeField) field).getValue().toDate() : null);
                    break;
                case INTEGER_LIST_TYPE:
                    properties.put(name,
                                   Arrays.stream(((CoalesceIntegerListField) field).getValue()).boxed().collect(Collectors.toList()));
                    break;
                case FLOAT_LIST_TYPE:
                case BOOLEAN_LIST_TYPE:
                    properties.put(name, Arrays.stream(field.getBaseValues()).collect(Collectors.toList()));
                    break;
                case STRING_LIST_TYPE:
                    properties.put(name,
                                   Arrays.stream(((CoalesceStringListField) field).getValue()).collect(Collectors.toList()));
                    break;
                case LONG_LIST_TYPE:
                    properties.put(name,
                                   Arrays.stream(((CoalesceLongListField) field).getValue()).boxed().collect(Collectors.toList()));
                    break;
                case DOUBLE_LIST_TYPE:
                    properties.put(name,
                                   Arrays.stream(((CoalesceDoubleListField) field).getValue()).boxed().collect(Collectors.toList()));
                    break;
                case GUID_TYPE:
                    properties.put(name, ((CoalesceGUIDField) field).getBaseValue());
                    break;
                case GUID_LIST_TYPE:
                    properties.put(name,
                                   Arrays.stream(((CoalesceGUIDListField) field).getValue()).collect(Collectors.toList()));
                    break;
                case ENUMERATION_LIST_TYPE:
                    properties.put(name,
                                   Arrays.stream(((CoalesceEnumerationListField) field).getValue()).boxed().collect(
                                           Collectors.toList()));
                    break;
                default:
                    // Add field value to results
                    properties.put(name, field.getValue());
                    break;
                }
            }
        }

        return properties;
    }

    private boolean exists(String id, String collectionId)
    {
        MongoCollection<Document> collection = client.getDatabase(MongoConstants.DATABASE_ID).getCollection(collectionId);
        Document doc = collection.find(Filters.eq(MongoConstants.COLUMN_ID, id)).first();

        return doc != null;
    }

    private Point createPoint(CoalesceCoordinateField field) throws CoalesceDataFormatException
    {
        Point value = null;

        if (field.getValue() != null)
        {
            value = new Point(toPosition(field.getValue()));
        }

        return value;
    }

    private Polygon createPolygon(CoalescePolygonField field) throws CoalesceDataFormatException
    {
        Polygon value = null;

        if (field.getValue() != null)
        {
            value = new Polygon(getCoordinates(field.getValue().getCoordinates()));
        }

        return value;
    }

    private LineString createLineString(CoalesceLineStringField field) throws CoalesceDataFormatException
    {
        LineString value = null;

        if (field.getValue() != null)
        {
            value = new LineString(getCoordinates(field.getValue().getCoordinates()));
        }

        return value;
    }

    private Point createCircle(CoalesceCircleField field) throws CoalesceDataFormatException
    {
        Point value = null;

        if (field.getValue() != null)
        {
            value = new Point(toPosition(field.getValue().getCenter()));
        }

        return value;
    }

    private MultiPoint createMultiPoint(CoalesceCoordinateListField field) throws CoalesceDataFormatException
    {
        MultiPoint value = null;

        if (field.getValue() != null)
        {
            value = new MultiPoint(getCoordinates(field.getValue()));
        }

        return value;
    }

    private List<Position> getCoordinates(Coordinate[] coords)
    {
        List<Position> positions = new ArrayList<>();

        if (coords != null && coords.length > 0)
        {
            for (Coordinate coord : coords)
            {
                positions.add(toPosition(coord));
            }
        }

        return positions;
    }

    private Position toPosition(Coordinate coord)
    {
        return new Position(coord.x, coord.y, coord.z);
    }

    private String normalize(CoalesceField<?> field)
    {
        return normalizer.normalize(field.getParent().getParent().getName(), field.getName());
    }

    /**
     * Internal class used for passing parameters within this iterator.
     */
    protected class Parameters {

        private Parameters(CoalesceEntity entity)
        {
            common = createMapping(entity);
        }

        private Map<String, List<WriteModel<Document>>> writes = new HashMap<>();
        private Map<String, Object> common;
        private boolean allowRemoval;

        private void addWrite(String collection, WriteModel<Document> write)
        {
            if (!writes.containsKey(collection))
            {
                writes.put(collection, new ArrayList<>());
            }

            writes.get(collection).add(write);
        }
    }
}
