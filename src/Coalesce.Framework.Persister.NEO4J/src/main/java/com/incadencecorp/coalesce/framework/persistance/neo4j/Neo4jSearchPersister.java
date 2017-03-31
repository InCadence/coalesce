/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

 Notwithstanding any contractor copyright notice, the Government has Unlimited
 Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 of this work other than as specifically authorized by these DFARS Clauses may
 violate Government rights in this work.

 DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 Unlimited Rights. The Government has the right to use, modify, reproduce,
 perform, display, release or disclose this computer software and to have or
 authorize others to do so.

 Distribution Statement D. Distribution authorized to the Department of
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 -----------------------------------------------------------------------------*/

package com.incadencecorp.coalesce.framework.persistance.neo4j;

import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import org.geotools.data.Query;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;

/**
 * Extension to {@link Neo4JPersistor} that implements
 * {@link ICoalesceSearchPersistor}.
 * 
 * @author n78554
 */
public class Neo4jSearchPersister extends Neo4JPersistor implements ICoalesceSearchPersistor {

    private static final String ENTITY_KEY_COL_NAME = CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getEntityKey());
    private static final String ENTITY_NAME_COL_NAME = CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getSource());
    private static final String ENTITY_SOURCE_COL_NAME = CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getName());
    private static final String ENTITY_TYPE_COL_NAME = CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getEntityType());
    private static final String ENTITY_TITLE_COL_NAME = CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getEntityTitle());

    private static final String LABEL = "n";
    private static final String QUERY = "MATCH (%s) %s RETURN %s %s %s";
    private static final String QUERY_COUNT = "MATCH (%s) %s RETURN count(n)";

    @Override
    public SearchResults search(Query query) throws CoalescePersistorException
    {
        SearchResults results = new SearchResults();
        results.setPageSize(query.getMaxFeatures());
        results.setPage(query.getStartIndex());

        Neo4jFilterToCypher converter = new Neo4jFilterToCypher();
        converter.setInline(false);
        converter.setDefaultLabelMapping(LABEL);

        try
        {
            StringBuilder sb = new StringBuilder();
            sb.append(LABEL + "." + Neo4JPersistor.KEY + " AS " + ENTITY_KEY_COL_NAME);
            sb.append(", " + LABEL + "." + Neo4JPersistor.NAME + " AS " + ENTITY_NAME_COL_NAME);
            sb.append(", " + LABEL + "." + Neo4JPersistor.SOURCE + " AS " + ENTITY_SOURCE_COL_NAME);
            sb.append(", " + LABEL + "." + Neo4JPersistor.TYPE + " AS " + ENTITY_TYPE_COL_NAME);
            sb.append(", " + LABEL + "." + Neo4JPersistor.TITLE + " AS " + ENTITY_TITLE_COL_NAME);

            if (query.getProperties() != null)
            {
                for (PropertyName property : query.getProperties())
                {
                    String[] parts = property.getPropertyName().split("\\.");

                    sb.append(", " + LABEL + "." + ((parts.length == 2) ? parts[1] : parts[0]));
                    sb.append(" AS " + CoalescePropertyFactory.getColumnName(property));
                }
            }

            String where = converter.encodeToString(query.getFilter());

            String entityname = LABEL;
            if (converter.getEntityName() != null)
            {
                entityname = LABEL + ":" + converter.getEntityName();
            }

            String cypher = String.format(QUERY,
                                          entityname,
                                          where,
                                          sb.toString(),
                                          getOrderBy(query.getSortBy()),
                                          getLimit(query));

            String countCypher = String.format(QUERY_COUNT, entityname, where);

            try (CoalesceDataConnectorBase conn = new Neo4JDataConnector(Neo4jSettings.getServerConn()))
            {
                results.setResults(executeQuery(cypher));

                CachedRowSet rowset = executeQuery(countCypher);
                if (rowset.next())
                {
                    results.setTotal(rowset.getLong(1));
                }

            }
        }
        catch (CoalesceException | SQLException e)
        {
            throw new CoalescePersistorException("(FAILED) Filter Encoding", e);
        }

        return results;
    }

    private String getLimit(Query query)
    {
        String limit = "";

        if (query.getMaxFeatures() > 0)
        {
            int offset = query.getMaxFeatures() * (query.getStartIndex() - 1);

            limit = String.format("SKIP %s LIMIT %s", offset, query.getMaxFeatures());
        }

        return limit;
    }

    public static String getOrderBy(SortBy[] values)
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

                String name = CoalescePropertyFactory.getColumnName(sortBy.getPropertyName());

                sb.append(String.format("%s %s", name, sortBy.getSortOrder().toSQL()));
            }

        }

        return sb.toString();
    }
}
