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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.search.factory.CoalesceFeatureTypeFactory;
import com.microsoft.azure.documentdb.*;
import com.microsoft.azure.documentdb.internal.DocumentServiceResponse;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.geotools.data.Query;
import org.geotools.data.jdbc.FilterToSQLException;
import org.geotools.jdbc.PreparedFilterToSQL;
import org.opengis.filter.sort.SortBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Derek Clemenzi
 */
public class CosmosHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CosmosHelper.class);
    private static final ObjectMapper MAPPER;

    static
    {
        MAPPER = new ObjectMapper();
        MAPPER.registerModule(new JodaModule());
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public static FeedResponse<Document> queryDocument(DocumentClient client, Query query, FeedOptions options)
            throws CoalescePersistorException
    {
        String clause = "";

        Collection<String> parameters = new ArrayList<>();

        if (query.getStartIndex() == null)
        {
            query.setStartIndex(0);
        }

        if (query.getProperties() != null && !query.getProperties().isEmpty())
        {
            parameters.addAll(query.getProperties().stream().map(s -> "c."
                    + s.getPropertyName()).collect(Collectors.toList()));
        }

        if (parameters.isEmpty())
        {
            parameters.add("c.id");
        }

        if (query.getFilter() != null)
        {
            try
            {
                PreparedFilterToSQL filterToSql = new CosmosFilterToSql(new CosmosNormalizer(), null);
                filterToSql.setPrepareEnabled(false);
                filterToSql.setFeatureType(CoalesceFeatureTypeFactory.createSimpleFeatureType());

                clause = filterToSql.encodeToString(query.getFilter());
            }
            catch (CoalesceException | FilterToSQLException e)

            {
                throw new CoalescePersistorException(e);
            }
        }

        // Offset does work as expected and throws an exception from within Karaf; therefore its been disabled.
        String sql = String.format("SELECT %s FROM c %s %s", // OFFSET %d LIMIT %d",
                                   String.join(",", parameters),
                                   clause,
                                   getOrderBy(query.getSortBy()));//,
                                   //query.getStartIndex(),
                                   //query.getMaxFeatures());

        LOGGER.debug("Link: {}, SQL: {}", CosmosConstants.COLLECTION_LINK + query.getTypeName(), sql);

        return client.queryDocuments(CosmosConstants.COLLECTION_LINK + query.getTypeName(), sql, options);

    }

    private static String getOrderBy(SortBy[] values)
    {

        StringBuilder sb = new StringBuilder();

        if (values != null && values.length > 0)
        {

            for (SortBy sortBy : values)
            {

                if (sb.length() == 0)
                {
                    sb.append("ORDER BY ");
                }
                else
                {
                    sb.append(", ");
                }

                String name = sortBy.getPropertyName().getPropertyName();

                sb.append(String.format("c.%s %s", name, sortBy.getSortOrder().toSQL()));
            }

        }

        return sb.toString();
    }

    /**
     * @param client       used for this transaction
     * @param collectionId of the collection the document belongs to.
     * @param document     to search for
     * @return whether or not the document exists.
     */
    public static boolean existsDocument(DocumentClient client, String collectionId, Document document)
    {
        FeedOptions options = new FeedOptions();
        String entityKey = document.getString(CosmosConstants.ENTITY_KEY_COLUMN_NAME);

        if (entityKey != null && !entityKey.isEmpty())
        {
            options.setPartitionKey(new PartitionKey(entityKey));
        }
        else
        {
            options.setEnableCrossPartitionQuery(true);
        }

        FeedResponse<Document> queryResults = client.queryDocuments(CosmosConstants.COLLECTION_LINK + collectionId,
                                                                    String.format("SELECT c.id FROM c where c.id = '%s'",
                                                                                  document.getId()),
                                                                    options);

        return queryResults.getQueryIterator().hasNext();
    }

    /**
     * @param client       used for this transaction
     * @param collectionId of the collection the document belongs to.
     * @param id           of the document
     * @return the document
     * @throws CoalescePersistorException on error
     */
    public static ResourceResponse<Document> readDocument(DocumentClient client, String collectionId, String id)
            throws CoalescePersistorException
    {
        RequestOptions options = new RequestOptions();
        options.setPartitionKey(new PartitionKey(id));

        try
        {
            return client.readDocument(CosmosConstants.COLLECTION_LINK + collectionId + "/docs/" + id, options);
        }
        catch (DocumentClientException e)
        {
            if (e.getError().getCode().equalsIgnoreCase("NotFound"))
            {
                return createResponse(HttpStatus.SC_NOT_FOUND, Document.class);
            }
            else
            {
                throw new CoalescePersistorException(e);
            }
        }
    }

    /**
     * @param client       used for this transaction
     * @param collectionId of the document's collection
     * @param document     to delete
     * @param options      of the request.
     * @return the response
     * @throws CoalescePersistorException on error
     */
    public static ResourceResponse<Document> deleteDocument(DocumentClient client,
                                                            String collectionId,
                                                            Document document,
                                                            RequestOptions options) throws CoalescePersistorException
    {
        try
        {
            if (existsDocument(client, collectionId, document))
            {
                return client.deleteDocument(CosmosConstants.COLLECTION_LINK + collectionId + "/docs/" + document.getId(),
                                             options);
            }
            else
            {
                return createResponse(HttpStatus.SC_NO_CONTENT, Document.class);
            }
        }
        catch (DocumentClientException e)
        {
            throw new CoalescePersistorException(e);
        }

    }

    /**
     * @param params to convert into a document
     * @return a document representation of the map of parameters
     * @throws CoalescePersistorException on error
     */
    public static Document createDocument(Map<String, Object> params) throws CoalescePersistorException
    {
        try
        {
            return new Document(MAPPER.writeValueAsString(params));
        }
        catch (JsonProcessingException e)
        {
            throw new CoalescePersistorException(e);
        }
    }

    /**
     * @param client       used for this transaction
     * @param collectionId of the collection the document belongs to.
     * @param document     to store
     * @return the response
     * @throws CoalescePersistorException on error
     */
    public static ResourceResponse<Document> createDocument(DocumentClient client,
                                                            String collectionId,
                                                            Document document,
                                                            RequestOptions options) throws CoalescePersistorException
    {
        try
        {
            String link = CosmosConstants.COLLECTION_LINK + collectionId;

            if (existsDocument(client, collectionId, document))
            {
                return client.replaceDocument(link + "/docs/" + document.getId(), document, options);
            }
            else
            {
                return client.createDocument(link, document, options, true);
            }
        }
        catch (DocumentClientException e)
        {
            throw new CoalescePersistorException(e);
        }
    }

    /**
     * @param client     used for this transaction
     * @param databaseId to create
     * @param options    of the request.
     * @throws CoalescePersistorException on error
     */
    public static void createDatabaseIfNotExists(DocumentClient client, String databaseId, RequestOptions options)
            throws CoalescePersistorException
    {
        Database definition = new Database();
        definition.setId(databaseId);

        createDatabaseIfNotExists(client, definition, options);
    }

    /**
     * @param client   used for this transaction
     * @param database to create
     * @param options  of the request.
     * @throws CoalescePersistorException on error
     */
    public static void createDatabaseIfNotExists(DocumentClient client, Database database, RequestOptions options)
            throws CoalescePersistorException
    {
        try
        {
            FeedResponse<Database> queryResults = client.queryDatabases(String.format("SELECT * FROM r where r.id = '%s'",
                                                                                      database.getId()), null);
            if (!queryResults.getQueryIterator().hasNext())
            {
                LOGGER.info("Creating Collection: {}", database.getId());
                client.createDatabase(database, options);
            }
        }
        catch (DocumentClientException e)
        {
            throw new CoalescePersistorException(e);
        }
    }

    /**
     * @param client       used for this transaction
     * @param collectionId to create
     * @param options      of the request.
     * @throws CoalescePersistorException on error
     */
    public static void createCollectionIfNotExists(DocumentClient client, String collectionId, RequestOptions options)
            throws CoalescePersistorException
    {
        // Create Partition Key
        PartitionKeyDefinition partitionKeyDefinition = new PartitionKeyDefinition();
        Collection<String> paths = new ArrayList<>();
        paths.add("/" + CosmosConstants.ENTITY_KEY_COLUMN_NAME);
        partitionKeyDefinition.setPaths(paths);

        // Create Indexes
        Collection<Index> indexes = new ArrayList<>();

        indexes.add(Index.Spatial(DataType.Point));

        Index numberIndex = Index.Range(DataType.Number);
        numberIndex.set("precision", -1);
        indexes.add(numberIndex);

        IncludedPath includedPath = new IncludedPath();
        includedPath.setPath("/*");
        includedPath.setIndexes(indexes);

        Collection<IncludedPath> includedPaths = new ArrayList<>();
        includedPaths.add(includedPath);

        // Create Index Policy
        IndexingPolicy indexingPolicy = new IndexingPolicy();
        indexingPolicy.setIncludedPaths(includedPaths);

        // Create Definition
        DocumentCollection definition = new DocumentCollection();
        definition.setId(collectionId.toLowerCase());
        definition.setIndexingPolicy(indexingPolicy);
        definition.setPartitionKey(partitionKeyDefinition);

        createCollectionIfNotExists(client, definition, options);
    }

    /**
     * @param client     used for this transaction
     * @param collection to create
     * @param options    of the request.
     * @throws CoalescePersistorException on error
     */
    public static void createCollectionIfNotExists(DocumentClient client,
                                                   DocumentCollection collection,
                                                   RequestOptions options) throws CoalescePersistorException
    {
        try
        {
            FeedResponse<DocumentCollection> queryResults = client.queryCollections(CosmosConstants.DATABASE_LINK,
                                                                                    String.format(
                                                                                            "SELECT * FROM r where r.id = '%s'",
                                                                                            collection.getId()),
                                                                                    null);

            if (!queryResults.getQueryIterator().hasNext())
            {
                LOGGER.info("Creating Collection: {}", collection.getId());
                client.createCollection(CosmosConstants.DATABASE_LINK, collection, options);
            }
        }
        catch (DocumentClientException e)
        {
            throw new CoalescePersistorException(e);
        }
    }

    private static <T extends Resource> ResourceResponse<T> createResponse(int status, Class<T> clazz)
            throws CoalescePersistorException
    {
        try
        {
            HttpResponse http = new DefaultHttpResponseFactory().newHttpResponse(HttpVersion.HTTP_1_1, status, null);
            DocumentServiceResponse response = new DocumentServiceResponse(http, false, null);

            // Get access to private constructor
            Constructor<ResourceResponse> constructor = ResourceResponse.class.getDeclaredConstructor(DocumentServiceResponse.class,
                                                                                                      Class.class);
            constructor.setAccessible(true);
            return constructor.newInstance(response, clazz);
        }
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex)
        {
            throw new CoalescePersistorException(ex);
        }
    }

}
