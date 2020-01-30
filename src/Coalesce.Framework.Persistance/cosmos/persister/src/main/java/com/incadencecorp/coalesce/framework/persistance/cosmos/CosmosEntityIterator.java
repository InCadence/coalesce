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

package com.incadencecorp.coalesce.framework.persistance.cosmos;

import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCircle;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCircleField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCoordinateField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCoordinateListField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFileField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLineStringField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalescePolygonField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.iterators.CoalesceIterator;
import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.PartitionKey;
import com.microsoft.azure.documentdb.RequestOptions;
import com.microsoft.azure.documentdb.ResourceResponse;
import org.locationtech.jts.geom.Coordinate;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class CosmosEntityIterator extends CoalesceIterator<Map<String, Object>> {

    private final ICoalesceNormalizer normalizer;
    private final boolean isAuthoritative;
    private final DocumentClient client;
    private final RequestOptions options;

    public CosmosEntityIterator(DocumentClient client,
                                RequestOptions options,
                                ICoalesceNormalizer normalizer,
                                boolean isAuthoritative)
    {
        if (normalizer == null)
        {
            throw new IllegalArgumentException("Normalizer cannot be null");
        }

        this.client = client;
        this.options = options;
        this.normalizer = normalizer;
        this.isAuthoritative = isAuthoritative;
    }

    public void iterate(boolean allowRemoval, CoalesceEntity... entities) throws CoalesceException
    {
        for (CoalesceEntity entity : entities)
        {
            if (entity != null)
            {
                options.setPartitionKey(new PartitionKey(entity.getKey()));

                Map<String, Object> mapping = new HashMap<>();
                mapping.put("coalesceentity", createMapping(entity));

                processAllElements(entity, mapping);

                mapping.put("id", entity.getKey());
                mapping.put("coalescelinkage", createMapping(entity.getLinkageSection()));

                if (isAuthoritative)
                {
                    ((Map<String, Object>) mapping.get("coalesceentity")).put(CosmosConstants.FIELD_XML, entity.toXml());
                }

                saveMapping(allowRemoval && entity.isMarkedDeleted(), CosmosConstants.COLLECTION_ENTITIES, mapping);
            }
        }

    }

    private void saveMapping(boolean delete, String collectionId, Map<String, Object> mapping) throws CoalesceException
    {
        ResourceResponse<Document> response;

        if (delete)
        {
            response = CosmosHelper.deleteDocument(client, collectionId, CosmosHelper.createDocument(mapping), options);
        }
        else
        {
            response = CosmosHelper.createDocument(client, collectionId, CosmosHelper.createDocument(mapping), options);
        }

        if (response.getStatusCode() / 100 != 2)
        {
            throw new CoalesceException("(FAILED) Invalid Status Code: " + response.getStatusCode());
        }
    }

    @Override
    protected boolean visitCoalesceLinkageSection(CoalesceLinkageSection section, Map<String, Object> mapping)
            throws CoalesceException
    {
        boolean delete = section.getEntity().isMarkedDeleted();

        for (CoalesceLinkage linkage : section.getLinkagesAsList())
        {
            mapping.put("id", linkage.getKey());

            if (!delete && linkage.isFlatten() && !linkage.isMarkedDeleted())
            {
                mapping.put("coalescelinkage", createMapping(linkage));
                saveMapping(false, CosmosConstants.COLLECTION_LINKAGES, mapping);
            }
            else
            {
                saveMapping(true, CosmosConstants.COLLECTION_LINKAGES, mapping);
            }
        }

        mapping.remove("coalescelinkage");

        return false;
    }

    @Override
    protected boolean visitCoalesceRecordset(CoalesceRecordset recordset, Map<String, Object> mapping)
            throws CoalesceException
    {
        if (recordset.isFlatten())
        {
            boolean delete = recordset.getEntity().isMarkedDeleted();
            String colectionId = CosmosConstants.getCollectionName(recordset.getEntity().getName());

            String namespace = normalizer.normalize(recordset.getName());

            for (CoalesceRecord record : recordset.getAllRecords())
            {
                mapping.put("id", record.getKey());

                if (!delete && record.isFlatten() && !record.isMarkedDeleted())
                {
                    mapping.put(namespace, createMapping(record));
                    saveMapping(false, colectionId, mapping);
                }
                else
                {
                    saveMapping(true, colectionId, mapping);
                }
            }

            mapping.remove(namespace);
        }

        // Stop Recursion
        return false;
    }

    private Map<String, Object> createMapping(CoalesceEntity entity)
    {
        Map<String, Object> properties = new HashMap<>();

        properties.put(CosmosConstants.ENTITY_KEY_COLUMN_NAME, entity.getKey());
        properties.put(CosmosConstants.ENTITY_NAME_COLUMN_NAME, entity.getName());
        properties.put(CosmosConstants.ENTITY_SOURCE_COLUMN_NAME, entity.getSource());
        properties.put(CosmosConstants.ENTITY_VERSION_COLUMN_NAME, entity.getVersion());
        properties.put(CosmosConstants.ENTITY_DATE_CREATED_COLUMN_NAME, entity.getDateCreated());
        properties.put(CosmosConstants.ENTITY_CREATED_BY_COLUMN_NAME, entity.getCreatedBy());
        properties.put(CosmosConstants.ENTITY_LAST_MODIFIED_COLUMN_NAME, entity.getLastModified());
        properties.put(CosmosConstants.ENTITY_LAST_MODIFIED_BY_COLUMN_NAME, entity.getModifiedBy());
        properties.put(CosmosConstants.ENTITY_STATUS_COLUMN_NAME, entity.getStatus().value());
        properties.put(CosmosConstants.ENTITY_ID_COLUMN_NAME, entity.getEntityId());
        properties.put(CosmosConstants.ENTITY_ID_TYPE_COLUMN_NAME, entity.getEntityIdType());
        properties.put(CosmosConstants.ENTITY_TITLE_COLUMN_NAME, entity.getTitle());

        return properties;
    }

    private List<Map<String, Object>> createMapping(CoalesceLinkageSection linkages)
    {
        List<Map<String, Object>> mapping = new ArrayList<>();

        for (CoalesceLinkage linkage : linkages.getLinkagesAsList())
        {
            mapping.add(createMapping(linkage));
        }

        //If the index response is returned and no exception was thrown, the index operation was successful
        return mapping;
    }

    private Map<String, Object> createMapping(CoalesceLinkage linkage)
    {
        //HashMap representation of the linkage for indexing in ElasticSearch
        Map<String, Object> properties = new HashMap<>();

        properties.put(CosmosConstants.LINKAGE_KEY_COLUMN_NAME, linkage.getKey());

        properties.put(CosmosConstants.LINKAGE_ENTITY2_KEY_COLUMN_NAME, linkage.getEntity2Key());
        properties.put(CosmosConstants.LINKAGE_ENTITY2_NAME_COLUMN_NAME, linkage.getEntity2Name());
        properties.put(CosmosConstants.LINKAGE_ENTITY2_SOURCE_COLUMN_NAME, linkage.getEntity2Source());
        properties.put(CosmosConstants.LINKAGE_ENTITY2_VERSION_COLUMN_NAME, linkage.getEntity2Version());

        properties.put(CosmosConstants.LINKAGE_DATE_CREATED_COLUMN_NAME, linkage.getDateCreated());
        properties.put(CosmosConstants.LINKAGE_LAST_MODIFIED_COLUMN_NAME, linkage.getLastModified());
        properties.put(CosmosConstants.LINKAGE_LABEL_COLUMN_NAME, linkage.getLabel());
        properties.put(CosmosConstants.LINKAGE_STATUS_COLUMN_NAME, linkage.getStatus().toString());
        properties.put(CosmosConstants.LINKAGE_LINK_TYPE_COLUMN_NAME, linkage.getLinkType().getLabel());

        //If the index response is returned and no exception was thrown, the index operation was successful
        return properties;
    }

    private Map<String, Object> createMapping(CoalesceRecord record) throws CoalesceDataFormatException
    {
        Map<String, Object> properties = new HashMap<>();
        properties.put("objectkey", record.getKey());

        for (CoalesceField field : record.getFields())
        {
            if (field.isFlatten() && (field.getBaseValue() != null
                    || field.getDataType() == ECoalesceFieldDataTypes.FILE_TYPE))
            {
                String name = normalizer.normalize(field.getName());

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
                default:
                    // Add field value to results
                    properties.put(name, field.getValue());
                    break;
                }
            }
        }

        return properties;
    }

    private Map<String, Object> createPoint(CoalesceCoordinateField field) throws CoalesceDataFormatException
    {
        Map<String, Object> results = null;

        if (field.getValue() != null)
        {
            results = new HashMap<>();
            results.put("type", "Point");
            results.put("coordinates", toJson(field.getValue()));
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
            results.put("type", "Polygon");
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
            results.put("type", "Linestring");
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
            results = new HashMap<>();
            results.put("type", "Point");
            results.put("coordinates", toJson(circle.getCenter()));
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
            results.put("type", "Multipoint");
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
                results.put(toJson(coord));
            }
        }

        return results;
    }

    private JSONArray toJson(Coordinate coord)
    {
        JSONArray coord2 = new JSONArray();
        coord2.put(coord.x);
        coord2.put(coord.y);
        coord2.put(coord.z);

        return coord2;
    }

}
