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

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.persistance.ICoalesceTemplatePersister;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.DeleteOneModel;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.WriteModel;
import org.apache.commons.lang3.NotImplementedException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mongo Persistor implementation for Template operations.
 *
 * @author Derek Clemenzi
 */
public class MongoTemplatePersistor implements ICoalesceTemplatePersister, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoTemplatePersistor.class);

    private boolean isAuthoritative = false;
    protected final ConnectionString connectionString;

    /**
     * Default Constructor
     */
    public MongoTemplatePersistor()
    {
        this(Collections.emptyMap());
    }

    /**
     * @param params map of parameters which overrides {@link MongoSettings}
     */
    public MongoTemplatePersistor(Map<String, String> params)
    {
        Map<String, String> parameters = MongoSettings.getParameters();
        parameters.putAll(params);

        isAuthoritative = Boolean.parseBoolean(parameters.get(MongoSettings.PARAM_IS_AUTHORITATIVE));

        connectionString = MongoSettings.createConnectionString(parameters);

        LOGGER.info("Connection String: {}", connectionString);
    }

    @Override
    public void saveTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        List<WriteModel<Document>> writes = new ArrayList<>();

        try (MongoClient client = MongoClients.create(connectionString))
        {
            MongoDatabase database = client.getDatabase(MongoConstants.DATABASE_ID);
            MongoCollection<Document> collection = database.getCollection(MongoConstants.COLLECTION_TEMPLATE);
            for (CoalesceEntityTemplate template : templates)
            {
                Map<String, Object> properties = new HashMap<>();
                properties.put(MongoConstants.COLUMN_ID, template.getKey());
                properties.put(MongoConstants.ENTITY_KEY_COLUMN_NAME, template.getKey());
                properties.put(MongoConstants.ENTITY_NAME_COLUMN_NAME, template.getName());
                properties.put(MongoConstants.ENTITY_SOURCE_COLUMN_NAME, template.getSource());
                properties.put(MongoConstants.ENTITY_VERSION_COLUMN_NAME, template.getVersion());
                properties.put(MongoConstants.ENTITY_DATE_CREATED_COLUMN_NAME,
                               template.getDateCreated() != null ? template.getDateCreated().toDate() : null);
                properties.put(MongoConstants.ENTITY_LAST_MODIFIED_COLUMN_NAME,
                               template.getLastModified() != null ? template.getLastModified().toDate() : null);
                properties.put(MongoConstants.FIELD_XML, template.toXml());

                Document doc = collection.find(Filters.eq(MongoConstants.COLUMN_ID, template.getKey())).first();

                if (doc == null)
                {
                    writes.add(new InsertOneModel<>(new Document(properties)));
                }
                else
                {
                    writes.add(new ReplaceOneModel<>(new Document(MongoConstants.COLUMN_ID, template.getKey()),
                                                     new Document(properties)));
                }
            }

            if (!writes.isEmpty())
            {
                collection.bulkWrite(writes);
            }
        }
    }

    @Override
    public void deleteTemplate(String... keys) throws CoalescePersistorException
    {
        List<WriteModel<Document>> writes = new ArrayList<>();

        for (String key : keys)
        {
            writes.add(new DeleteOneModel<>(new Document(MongoConstants.COLUMN_ID, key)));
        }

        if (!writes.isEmpty())
        {
            try (MongoClient client = MongoClients.create(connectionString))
            {
                MongoDatabase database = client.getDatabase(MongoConstants.DATABASE_ID);
                MongoCollection<Document> collection = database.getCollection(MongoConstants.COLLECTION_TEMPLATE);
                collection.bulkWrite(writes);
            }
        }
    }

    @Override
    public void registerTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        // Do Nothing
    }

    @Override
    public void unregisterTemplate(String... keys) throws CoalescePersistorException
    {
        throw new CoalescePersistorException(new NotImplementedException("Unregistering Templates"));
    }

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String key) throws CoalescePersistorException
    {
        try (MongoClient client = MongoClients.create(connectionString))
        {
            MongoCollection<Document> collection = client.getDatabase(MongoConstants.DATABASE_ID).getCollection(
                    MongoConstants.COLLECTION_TEMPLATE);

            Document doc = collection.find(Filters.eq(MongoConstants.COLUMN_ID, key)).first();

            if (doc != null)
            {
                try
                {
                    return CoalesceEntityTemplate.create(doc.getString(MongoConstants.FIELD_XML));
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
        try (MongoClient client = MongoClients.create(connectionString))
        {
            MongoCollection<Document> collection = client.getDatabase(MongoConstants.DATABASE_ID).getCollection(
                    MongoConstants.COLLECTION_TEMPLATE);

            Bson filter = Filters.and(Filters.eq(MongoConstants.ENTITY_NAME_COLUMN_NAME, name),
                                      Filters.eq(MongoConstants.ENTITY_SOURCE_COLUMN_NAME, source),
                                      Filters.eq(MongoConstants.ENTITY_VERSION_COLUMN_NAME, version));

            Document doc = collection.find(filter).first();

            return doc != null ? doc.getString(MongoConstants.COLUMN_ID) : null;
        }
    }

    @Override
    public List<ObjectMetaData> getEntityTemplateMetadata() throws CoalescePersistorException
    {
        List<ObjectMetaData> metadata = new ArrayList<>();

        try (MongoClient client = MongoClients.create(connectionString))
        {
            MongoCollection<Document> collection = client.getDatabase(MongoConstants.DATABASE_ID).getCollection(
                    MongoConstants.COLLECTION_TEMPLATE);

            for (Document document : collection.find())
            {
                metadata.add(new ObjectMetaData(document.getString(MongoConstants.COLUMN_ID),
                                                document.getString(MongoConstants.ENTITY_NAME_COLUMN_NAME),
                                                document.getString(MongoConstants.ENTITY_SOURCE_COLUMN_NAME),
                                                document.getString(MongoConstants.ENTITY_VERSION_COLUMN_NAME),
                                                new DateTime(document.getDate(MongoConstants.ENTITY_DATE_CREATED_COLUMN_NAME)),
                                                new DateTime(document.getDate(MongoConstants.ENTITY_LAST_MODIFIED_COLUMN_NAME))));

            }

        }
        return metadata;
    }

    @Override
    public void close() throws IOException
    {

    }

    /**
     * @return whether this persistor is initialized as authoritative.
     */
    protected boolean isAuthoritative()
    {
        return isAuthoritative;
    }
}
