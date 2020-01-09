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

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.persistance.ICoalesceTemplatePersister;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.FeedOptions;
import com.microsoft.azure.documentdb.FeedResponse;
import com.microsoft.azure.documentdb.PartitionKey;
import com.microsoft.azure.documentdb.RequestOptions;
import org.apache.commons.lang3.NotImplementedException;
import org.geotools.data.Query;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class CosmosTemplatePersistor implements ICoalesceTemplatePersister, Closeable {

    private static final FilterFactory2 FF = CoalescePropertyFactory.getFilterFactory();

    private final DocumentClient client;
    private final boolean isAuthoritative;


    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    public CosmosTemplatePersistor()
    {
        this(Collections.emptyMap());
    }

    /**
     * @param params map of parameters which overrides {@link CosmosSettings}
     */
    public CosmosTemplatePersistor(Map<String, String> params)
    {
        Map<String, String> parameters = CosmosSettings.getParameters();
        parameters.putAll(params);

        String host = parameters.get(CosmosSettings.PARAM_HOST);
        String key = parameters.get(CosmosSettings.PARAM_KEY);

        client = new DocumentClient(host, key, null, null);

        isAuthoritative = Boolean.parseBoolean(parameters.get(CosmosSettings.PARAM_IS_AUTHORITATIVE));

    }

    /*--------------------------------------------------------------------------
    API Implementations
    --------------------------------------------------------------------------*/

    @Override
    public void saveTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        for (CoalesceEntityTemplate template : templates)
        {
            Map<String, Object> properties = new HashMap<>();
            properties.put("id", template.getKey());
            properties.put(CosmosConstants.ENTITY_KEY_COLUMN_NAME, template.getKey());
            properties.put(CosmosConstants.ENTITY_NAME_COLUMN_NAME, template.getName());
            properties.put(CosmosConstants.ENTITY_SOURCE_COLUMN_NAME, template.getSource());
            properties.put(CosmosConstants.ENTITY_VERSION_COLUMN_NAME, template.getVersion());
            properties.put(CosmosConstants.ENTITY_DATE_CREATED_COLUMN_NAME, template.getDateCreated());
            properties.put(CosmosConstants.ENTITY_LAST_MODIFIED_COLUMN_NAME, template.getLastModified());
            properties.put(CosmosConstants.FIELD_XML, template.toXml());

            CosmosHelper.createDocument(client,
                                        CosmosConstants.COLLECTION_TEMPLATE,
                                        CosmosHelper.createDocument(properties),
                                        null);
        }
    }

    @Override
    public void deleteTemplate(String... keys) throws CoalescePersistorException
    {
        RequestOptions options = new RequestOptions();

        for (String key : keys)
        {
            options.setPartitionKey(new PartitionKey(key));

            Document document = new Document();
            document.setId(key);
            document.set(CosmosConstants.ENTITY_KEY_COLUMN_NAME, key);

            CosmosHelper.deleteDocument(client, CosmosConstants.COLLECTION_TEMPLATE, document, options);
        }
    }

    @Override
    public void registerTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        // Create Database
        CosmosHelper.createDatabaseIfNotExists(client, CosmosConstants.DATABASE_ID, null);

        // Create Common Collections
        CosmosHelper.createCollectionIfNotExists(client, CosmosConstants.COLLECTION_TEMPLATE, null);
        CosmosHelper.createCollectionIfNotExists(client, CosmosConstants.COLLECTION_ENTITIES, null);
        CosmosHelper.createCollectionIfNotExists(client, CosmosConstants.COLLECTION_LINKAGES, null);

        for (CoalesceEntityTemplate template : templates)
        {
            CosmosHelper.createCollectionIfNotExists(client, CosmosConstants.getCollectionName(template), null);
        }
    }

    @Override
    public void unregisterTemplate(String... keys) throws CoalescePersistorException
    {
        throw new CoalescePersistorException(new NotImplementedException("Unregistering Templates"));
    }

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String key) throws CoalescePersistorException
    {
        FeedOptions options = new FeedOptions();
        options.setPartitionKey(new PartitionKey(key));

        Query query = new Query();
        query.setTypeName(CosmosConstants.COLLECTION_TEMPLATE);
        query.setPropertyNames(new String[] { CosmosConstants.FIELD_XML });
        query.setFilter(FF.equals(FF.property("id"), FF.literal(key)));

        FeedResponse<Document> queryResults = CosmosHelper.queryDocument(client, query, options);

        Iterator<Document> it = queryResults.getQueryIterator();

        if (it.hasNext())
        {
            try
            {
                return CoalesceEntityTemplate.create(it.next().getString(CosmosConstants.FIELD_XML));
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
        FeedOptions options = new FeedOptions();
        options.setEnableCrossPartitionQuery(true);

        List<Filter> filters = new ArrayList<>();
        filters.add(CoalescePropertyFactory.getName(name));
        filters.add(CoalescePropertyFactory.getSource(source));
        filters.add(CoalescePropertyFactory.getVersion(version));

        Query query = new Query();
        query.setTypeName(CosmosConstants.COLLECTION_TEMPLATE);
        query.setFilter(FF.and(filters));

        FeedResponse<Document> queryResults = CosmosHelper.queryDocument(client, query, options);
        Iterator<Document> it = queryResults.getQueryIterator();
        // TODO Query the database
        return it.hasNext() ? it.next().getId() : null;
    }

    @Override
    public List<ObjectMetaData> getEntityTemplateMetadata() throws CoalescePersistorException
    {
        FeedOptions options = new FeedOptions();
        options.setEnableCrossPartitionQuery(true);

        List<ObjectMetaData> metaDatas = new ArrayList<>();

        List<String> returnParams = new ArrayList<>();
        returnParams.add(CosmosConstants.ENTITY_KEY_COLUMN_NAME);
        returnParams.add(CosmosConstants.ENTITY_NAME_COLUMN_NAME);
        returnParams.add(CosmosConstants.ENTITY_SOURCE_COLUMN_NAME);
        returnParams.add(CosmosConstants.ENTITY_VERSION_COLUMN_NAME);
        returnParams.add(CosmosConstants.ENTITY_DATE_CREATED_COLUMN_NAME);
        returnParams.add(CosmosConstants.ENTITY_LAST_MODIFIED_COLUMN_NAME);

        Query query = new Query();
        query.setTypeName(CosmosConstants.COLLECTION_TEMPLATE);
        query.setPropertyNames(returnParams);

        FeedResponse<Document> queryResults = CosmosHelper.queryDocument(client, query, options);

        Iterator<Document> it = queryResults.getQueryIterator();

        while (it.hasNext())
        {
            Document document = it.next();
            metaDatas.add(new ObjectMetaData(document.getString(CosmosConstants.ENTITY_KEY_COLUMN_NAME),
                                             document.getString(CosmosConstants.ENTITY_NAME_COLUMN_NAME),
                                             document.getString(CosmosConstants.ENTITY_SOURCE_COLUMN_NAME),
                                             document.getString(CosmosConstants.ENTITY_VERSION_COLUMN_NAME),
                                             JodaDateTimeHelper.parseDateTime(document.getString(CosmosConstants.ENTITY_DATE_CREATED_COLUMN_NAME)),
                                             JodaDateTimeHelper.parseDateTime(document.getString(CosmosConstants.ENTITY_LAST_MODIFIED_COLUMN_NAME))));

        }

        return metaDatas;
    }

    @Override
    public void close()
    {
        client.close();
    }

    /*--------------------------------------------------------------------------
    Private / Protected Methods
    --------------------------------------------------------------------------*/

    /**
     * @return the client used to communicate with Cosmos
     */
    protected DocumentClient getClient()
    {
        return client;
    }

    /**
     * @return whether this persistor is initialized as authoritative.
     */
    protected boolean isAuthoritative()
    {
        return isAuthoritative;
    }

}
