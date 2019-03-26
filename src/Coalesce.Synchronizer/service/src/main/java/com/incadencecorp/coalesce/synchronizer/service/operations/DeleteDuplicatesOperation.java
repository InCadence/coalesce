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

package com.incadencecorp.coalesce.synchronizer.service.operations;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractOperation;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractOperationTask;
import org.geotools.data.Query;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This implementation takes a list of keys and based on the specified unique fields checks for duplicates. If found based on
 * configuration this operation will either mark them as deleted or permanently delete the entities.
 *
 * @author Derek Clemenzi
 * @see #PARAM_MARK_AS_DELETED
 * @see #PARAM_UNIQUE_KEY
 */
public class DeleteDuplicatesOperation extends AbstractOperation<AbstractOperationTask> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteDuplicatesOperation.class);
    private static final FilterFactory2 FF = CoalescePropertyFactory.getFilterFactory();

    /**
     * (Boolean) If true then the entities are only marked as deleted; otherwise they are physically deleted.
     */
    public static final String PARAM_MARK_AS_DELETED = DeleteOperationImpl.class.getName() + ".markOnly";

    /**
     * (CSV) Comma separated list of fields that uniquely identifies an entity.
     */
    public static final String PARAM_UNIQUE_KEY = DeleteOperationImpl.class.getName() + ".fields";

    // Default Parameter Values
    private static final String DEFAULT_MARK_AS_DELETED = Boolean.TRUE.toString();
    private static final String DEFAULT_UNIQUE_KEY = CoalescePropertyFactory.getEntityId().toString();

    private boolean allowRemoval = false;
    private Set<String> fields = new HashSet<>();

    @Override
    public void setProperties(Map<String, String> params)
    {
        super.setProperties(params);

        allowRemoval = !Boolean.parseBoolean(parameters.getOrDefault(PARAM_MARK_AS_DELETED, DEFAULT_MARK_AS_DELETED));
        fields.addAll(Arrays.asList(parameters.getOrDefault(PARAM_UNIQUE_KEY, DEFAULT_UNIQUE_KEY).split("[,]")));
    }

    @Override
    protected Set<String> getAdditionalRequiredColumns()
    {
        return fields;
    }

    @Override
    protected AbstractOperationTask createTask()
    {
        return new AbstractOperationTask() {

            @Override
            protected Boolean doWork(String[] keys, CachedRowSet rowset) throws CoalescePersistorException
            {
                if (!(source instanceof ICoalesceSearchPersistor))
                {
                    throw new IllegalArgumentException(String.format(CoalesceErrors.NOT_INITIALIZED,
                                                                     ICoalesceSearchPersistor.class));
                }

                try
                {
                    if (rowset.first())
                    {
                        List<String> keyList = Arrays.asList(keys);
                        Set<String> keysToDelete = new HashSet<>();

                        // Determine Column Mapping
                        Map<String, Integer> fieldIdx = getColumnMapping(rowset.getMetaData(), fields);

                        do
                        {
                            // Entity Key is always the first column
                            String key = rowset.getString(1);

                            if (keyList.contains(key))
                            {
                                // Extract key's unique values
                                Map<String, String> fieldValues = new HashMap<>();

                                for (String field : fields)
                                {
                                    fieldValues.put(field, rowset.getString(fieldIdx.get(field)));
                                }

                                keysToDelete.addAll(runDeduplicationCheck(fieldValues, source));
                            }
                        }
                        while (rowset.next());

                        CoalesceEntity[] entities = source.getEntity(keysToDelete.toArray(new String[keysToDelete.size()]));

                        for (CoalesceEntity entity : entities)
                        {
                            entity.markAsDeleted();
                        }

                        return saveWork(allowRemoval, entities);
                    }
                }
                catch (SQLException e)
                {
                    // Rethrow as a Coalesce Exception
                    throw new CoalescePersistorException(e);
                }

                return true;
            }
        };
    }

    /**
     * @return a list of keys that are consider duplicates and should be deleted.
     */
    private static List<String> runDeduplicationCheck(Map<String, String> fieldValues, ICoalescePersistor source)
            throws CoalescePersistorException
    {
        List<String> keysToDelete = new ArrayList<>();

        // Create de-duplication query
        List<Filter> filters = new ArrayList<>();

        for (Map.Entry<String, String> entry : fieldValues.entrySet())
        {
            filters.add(FF.equal(FF.property(entry.getKey()), FF.literal(entry.getValue()), false));
        }

        List<PropertyName> props = new ArrayList<>();
        props.add(CoalescePropertyFactory.getLastModified());

        Query query = new Query();
        query.setProperties(props);
        query.setFilter(FF.and(filters));
        query.setSortBy(new SortBy[] {
                FF.sort(CoalescePropertyFactory.getLastModified().getPropertyName(), SortOrder.DESCENDING)
        });

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Duplication Check Query: {}", query.getFilter());
        }

        // Execute Query
        SearchResults results = ((ICoalesceSearchPersistor) source).search(query);

        if (results.isSuccessful())
        {
            try (CachedRowSet rows = results.getResults())
            {
                // Has Duplicates?
                if (rows.size() > 1 && rows.first())
                {
                    // Keep the first entry and delete the rest
                    while (rows.next())
                    {
                        String key = rows.getString(1);
                        keysToDelete.add(key);
                        LOGGER.debug("Duplicate Detected ({})", key);
                    }
                }
            }
            catch (SQLException e)
            {
                // Rethrow as a Coalesce Exception
                throw new CoalescePersistorException(e);
            }
        }
        else
        {
            throw new CoalescePersistorException("(FAILED) Executing de-duplication query: " + results.getError());
        }

        return keysToDelete;
    }

    /**
     * @return a mapping of property names to the column indexes
     */
    private static Map<String, Integer> getColumnMapping(ResultSetMetaData metadata, Collection<String> propertyNames)
            throws SQLException
    {
        Map<String, Integer> fieldIdx = new HashMap<>();

        for (String field : propertyNames)
        {
            fieldIdx.put(field, getPropertyIdx(metadata, field));
        }

        return fieldIdx;
    }

    /**
     * @return the column index for the specified property name.
     */
    private static int getPropertyIdx(ResultSetMetaData metadata, String propertyName) throws SQLException
    {
        String columnName = CoalescePropertyFactory.getColumnName(propertyName);

        for (int ii = 1; ii <= metadata.getColumnCount(); ii++)
        {
            if (metadata.getColumnName(ii).equalsIgnoreCase(columnName))
            {
                return ii;
            }
        }

        throw new SQLException(String.format(CoalesceErrors.NOT_FOUND, "Column", columnName));
    }
}
