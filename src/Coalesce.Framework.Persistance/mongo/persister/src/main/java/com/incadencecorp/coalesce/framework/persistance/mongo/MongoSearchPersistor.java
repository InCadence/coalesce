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
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalesceFeatureTypeFactory;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.search.resultset.CoalesceResultSet;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.geotools.data.Query;
import org.geotools.filter.Capabilities;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Mongo Persistor implementation for Search operations.
 *
 * @author Derek Clemenzi
 */
public class MongoSearchPersistor extends MongoPersistor implements ICoalesceSearchPersistor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoSearchPersistor.class);

    /**
     * Default Constructor
     */
    public MongoSearchPersistor()
    {
        this(Collections.emptyMap());
    }

    /**
     * @param params map of parameters which overrides {@link MongoSettings}
     */
    public MongoSearchPersistor(Map<String, String> params)
    {
        super(params);
    }

    @Override
    public SearchResults search(Query query) throws CoalescePersistorException
    {
        List<PropertyName> properties = new ArrayList<>();

        if (query.getProperties() != null)
        {
            properties.addAll(query.getProperties());
        }

        if (properties.isEmpty()
                || !properties.get(0).getPropertyName().equalsIgnoreCase(CoalescePropertyFactory.getEntityKey().getPropertyName()))
        {
            properties.add(0, CoalescePropertyFactory.getEntityKey());
        }

        query.setProperties(properties);

        String[] columnList = new String[properties.size()];
        for (int i = 0; i < properties.size(); i++)
        {
            String property = properties.get(i).getPropertyName();

            if (!CoalescePropertyFactory.isRecordPropertyName(property))
            {
                ECoalesceFieldDataTypes type = CoalesceTemplateUtil.getDataType(property);

                if (type == null)
                {
                    throw new IllegalArgumentException("Unknown Property: " + property);
                }

                LOGGER.debug("Property: {} Type: {}", property, type);
            }

            columnList[i] = CoalescePropertyFactory.getColumnName(property);
        }

        CachedRowSet rowset;
        int total;

        MongoQueryRewriter rewriter = new MongoQueryRewriter();
        Query localQuery = rewriter.rewrite(query);

        List<Bson> sorts = new ArrayList<>();
        if (localQuery.getSortBy() != null)
        {
            for (SortBy sort : localQuery.getSortBy())
            {
                if (sort.getSortOrder() == SortOrder.DESCENDING)
                {
                    sorts.add(Sorts.descending(sort.getPropertyName().getPropertyName()));
                }
                else
                {
                    sorts.add(Sorts.ascending(sort.getPropertyName().getPropertyName()));
                }
            }
        }

        try (MongoClient client = MongoClients.create(connectionString))
        {
            CoalesceFilterToMongo filterToMongo = new CoalesceFilterToMongo();
            filterToMongo.setFeatureType(CoalesceFeatureTypeFactory.createSimpleFeatureType());

            BasicDBObject result = (BasicDBObject) localQuery.getFilter().accept(filterToMongo, null);

            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Query - {}: {}", localQuery.getTypeName(), result);
            }

            MongoCollection<Document> collection = client.getDatabase(MongoConstants.DATABASE_ID).getCollection(localQuery.getTypeName());

            MongoDocumentIterator it = new MongoDocumentIterator(collection.find(result).skip(localQuery.getStartIndex()).limit(
                    localQuery.getMaxFeatures()).sort(Sorts.orderBy(sorts)).iterator(), query.getProperties());
            CoalesceResultSet resultSet = new CoalesceResultSet(it, columnList);

            rowset = RowSetProvider.newFactory().createCachedRowSet();
            rowset.populate(resultSet);

            total = rowset.size();

            LOGGER.debug("Search Total: {}", total);
        }
        catch (CoalesceException | SQLException e)
        {
            throw new CoalescePersistorException(e);
        }

        SearchResults results = new SearchResults();
        results.setTotal(total);
        results.setResults(rowset);

        return results;
    }

    @Override
    public EnumSet<EPersistorCapabilities> getCapabilities()
    {
        EnumSet<EPersistorCapabilities> capabilities = super.getCapabilities();

        capabilities.add(EPersistorCapabilities.SEARCH);
        capabilities.add(EPersistorCapabilities.GEOSPATIAL_SEARCH);
        capabilities.add(EPersistorCapabilities.TEMPORAL_SEARCH);
        ;

        return capabilities;
    }

    @Override
    public Capabilities getSearchCapabilities()
    {
        Capabilities capability = new Capabilities();
        capability.addAll(Capabilities.SIMPLE_COMPARISONS);
        capability.addAll(Capabilities.LOGICAL);

        return capability;
    }
}
