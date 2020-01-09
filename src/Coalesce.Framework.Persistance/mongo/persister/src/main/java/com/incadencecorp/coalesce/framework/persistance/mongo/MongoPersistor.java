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

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.WriteModel;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Mongo Persistor implementation for CRUD operations.
 *
 * @author Derek Clemenzi
 */
public class MongoPersistor extends MongoTemplatePersistor implements ICoalescePersistor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoPersistor.class);

    /**
     * Default Constructor
     */
    public MongoPersistor()
    {
        this(Collections.emptyMap());
    }

    /**
     * @param params map of parameters which overrides {@link MongoSettings}
     */
    public MongoPersistor(Map<String, String> params)
    {
        super(params);
    }

    @Override
    public boolean saveEntity(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        try (MongoClient client = MongoClients.create(connectionString))
        {
            MongoEntityIterator it = new MongoEntityIterator(client, new MongoNormalizer(), isAuthoritative());
            MongoDatabase database = client.getDatabase(MongoConstants.DATABASE_ID);

            for (Map.Entry<String, List<WriteModel<Document>>> entry : it.iterate(allowRemoval, entities).entrySet())
            {
                MongoCollection<Document> collection = database.getCollection(entry.getKey());
                BulkWriteResult results = collection.bulkWrite(entry.getValue());

                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("Collection: {}", entry.getKey());

                    for (WriteModel<Document> write : entry.getValue())
                    {
                        LOGGER.debug("\t{}", (LOGGER.isTraceEnabled()) ? write.toString() : write.getClass());
                    }

                    LOGGER.debug("Inserted: {}, Modified: {}, Deleted: {}",
                                 results.getInsertedCount(),
                                 results.getModifiedCount(),
                                 results.getDeletedCount());

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
        return Arrays.stream(getEntityXml(keys)).map(CoalesceEntity::create).toArray(CoalesceEntity[]::new);
    }

    @Override
    public String[] getEntityXml(String... keys) throws CoalescePersistorException
    {
        List<String> xml = new ArrayList<>();

        try (MongoClient client = MongoClients.create(connectionString))
        {
            MongoCollection<Document> collection = client.getDatabase(MongoConstants.DATABASE_ID).getCollection(
                    MongoConstants.COLLECTION_ENTITIES);

            Bson[] filters = new Bson[keys.length];

            for (int ii = 0; ii < keys.length; ii++)
            {
                filters[ii] = Filters.eq(MongoConstants.COLUMN_ID, keys[ii]);
            }

            for (Document document : collection.find(Filters.or(filters)))
            {
                xml.add(document.getString(MongoConstants.FIELD_XML));
            }

            if (LOGGER.isDebugEnabled() && xml.size() != keys.length)
            {
                LOGGER.debug("(WARN) {} Entities out of {} were found", xml.size(), keys.length);
            }
        }

        return xml.toArray(new String[0]);
    }

    @Override
    public EnumSet<EPersistorCapabilities> getCapabilities()
    {
        EnumSet<EPersistorCapabilities> capabilities = EnumSet.of(EPersistorCapabilities.CREATE,
                                                                  EPersistorCapabilities.UPDATE,
                                                                  EPersistorCapabilities.DELETE,
                                                                  EPersistorCapabilities.READ,
                                                                  EPersistorCapabilities.READ_TEMPLATES);

        if (isAuthoritative())
        {
            capabilities.add(EPersistorCapabilities.READ);
        }

        return capabilities;
    }
}
