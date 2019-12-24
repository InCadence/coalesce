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

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.search.resultset.CoalesceResultSet;
import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.documentdb.FeedOptions;
import com.microsoft.azure.documentdb.FeedResponse;
import org.geotools.data.Query;
import org.geotools.filter.Capabilities;
import org.opengis.filter.expression.PropertyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class CosmosSearchPersistor extends CosmosPersistor implements ICoalesceSearchPersistor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CosmosSearchPersistor.class);

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    public CosmosSearchPersistor()
    {
        this(Collections.emptyMap());
    }

    /**
     * @param params map of parameters which overrides {@link CosmosSettings}
     */
    public CosmosSearchPersistor(Map<String, String> params)
    {
        super(params);
    }

    /*--------------------------------------------------------------------------
    Implementations
    --------------------------------------------------------------------------*/

    @Override
    public SearchResults search(Query query) throws CoalescePersistorException
    {
        List<PropertyName> properties = new ArrayList<>();

        if (query.getProperties() != null)
        {
            properties.addAll(query.getProperties());
        }

        if (properties.size() == 0
                || !properties.get(0).getPropertyName().equalsIgnoreCase(CoalescePropertyFactory.getEntityKey().getPropertyName()))
        {
            properties.add(0, CoalescePropertyFactory.getEntityKey());
        }

        query.setProperties(properties);

        CosmosQueryRewriter rewriter = new CosmosQueryRewriter();
        //String featureType = rewriter.getFeatureType(query);

        Query localQuery = rewriter.rewrite(query);

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

        FeedOptions options = new FeedOptions();
        options.setEnableCrossPartitionQuery(true);
        options.setPageSize(query.getMaxFeatures());

        FeedResponse<Document> queryResults = CosmosHelper.queryDocument(getClient(), localQuery, options);

        CachedRowSet rowset;
        int total;

        try
        {
            Iterator<Object[]> columnIterator = new CosmosDocumentIterator(queryResults.getQueryIterator(),
                                                                           query.getProperties());
            CoalesceResultSet resultSet = new CoalesceResultSet(columnIterator, columnList);
            rowset = RowSetProvider.newFactory().createCachedRowSet();
            rowset.populate(resultSet);

            total = rowset.size();
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException(e);
        }

        // TODO If page size is reach we need to determine the total.

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
        capabilities.add(EPersistorCapabilities.CASE_INSENSITIVE_SEARCH);

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
