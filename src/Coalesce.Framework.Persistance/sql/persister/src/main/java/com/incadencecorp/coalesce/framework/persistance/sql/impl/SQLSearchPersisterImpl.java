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

package com.incadencecorp.coalesce.framework.persistance.sql.impl;

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
//import com.incadencecorp.coalesce.framework.persistance.sql.impl.SQLDataConnector;
//import com.incadencecorp.coalesce.framework.persistance.sql.impl.SQLPersisterImplSettings;
//import com.incadencecorp.coalesce.framework.persistance.sql.mappers.StoredProcedureArgumentMapper;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalesceFeatureTypeFactory;
import org.geotools.data.Query;
import org.geotools.data.jdbc.FilterToSQLException;
import org.geotools.filter.Capabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author GGaito
 */
public class SQLSearchPersisterImpl extends SQLPersisterImpl implements ICoalesceSearchPersistor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLPersisterImpl.class);

     /*--------------------------------------------------------------------------
    Private Static Final (Used for SQL Queries)
    --------------------------------------------------------------------------*/

    private static final String SQL_FIND_PROCEDURE_FORMAT = "SELECT routine_name FROM information_schema.routines WHERE routine_name=? AND specific_schema=?";

    private static final String SQL_GET_COLUMN_NAMES = "SELECT column_name FROM information_schema.columns WHERE "
            + "table_name = ? AND table_schema=? ORDER BY ordinal_position";

    private static final String SQL_CLEAN_LIST_TABLES = "DELETE FROM %s.fieldtable_%s WHERE entitykey=?";

    private static final String SQL_DELETE_ENTITYKEY = "DELETE FROM %s WHERE entitykey=?";

    private static final String SQL_DELETE_OBJECTKEY = "DELETE FROM %s WHERE objectkey=?";

    private static final ConcurrentMap<String, Boolean> STORED_PROCEDURE_EXTSTS_CACHE = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, String[]> COLUMNS_CACHE = new ConcurrentHashMap<>();

//    private final StoredProcedureArgumentMapper procedureMapper = new StoredProcedureArgumentMapper();

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/
    /**
     * Uses {@link SQLPersisterImplSettings} to configure the database settings.
     */
    public SQLSearchPersisterImpl()
    {
        setConnectionSettings(SQLPersisterImplSettings.getServerConn());
        setSchema(SQLPersisterImplSettings.getDatabaseSchema());
    }

    /**
     * Uses {@link SQLPersisterImplSettings} to configure the database settings.
     *
     * @param userId   User ID used for connection to the DB.
     * @param password User's password used for connecting to the DB.
     */
    public SQLSearchPersisterImpl(String userId, String password)
    {

        SQLPersisterImplSettings.setUserName(userId);
        SQLPersisterImplSettings.setUserPassword(password);

        setConnectionSettings(SQLPersisterImplSettings.getServerConn());
        setSchema(SQLPersisterImplSettings.getDatabaseSchema());

    }

    @Override
    public SearchResults search(Query query) throws CoalescePersistorException
    {
        if (query.getStartIndex() == null)
        {
            query.setStartIndex(1);
        }

        SearchResults results = new SearchResults();
        results.setPage(query.getStartIndex());
        results.setPageSize(query.getMaxFeatures());

        try
        {
            // Create SQL Query
            SQLCoalescePreparedFilter preparedFilter = new SQLCoalescePreparedFilter(SQLPersisterImplSettings.getDatabaseSchema());
            preparedFilter.setPageNumber(query.getStartIndex());
            preparedFilter.setPageSize(query.getMaxFeatures());
            preparedFilter.setSortBy(query.getSortBy());
            preparedFilter.setPropertNames(query.getPropertyNames());
            preparedFilter.setIgnoreSecurity(true);
            preparedFilter.setFeatureType(CoalesceFeatureTypeFactory.createSimpleFeatureType());

            // Create SQL
            String where = preparedFilter.encodeToString(query.getFilter());

            // Add Parameters
            List<CoalesceParameter> paramList = new ArrayList<>();
            paramList.addAll(getParameters(preparedFilter));

            CoalesceParameter[] params = paramList.toArray(new CoalesceParameter[paramList.size()]);

            try (CoalesceDataConnectorBase conn = new SQLDataConnector(getConnectionSettings(), getSchemaPrefix()))
            {
                String sql = String.format("SELECT DISTINCT %s FROM %s %s %s ",
                                           preparedFilter.getColumns(),
                                           preparedFilter.getFrom(),
                                           where,
                                           preparedFilter.getSorting());


                // Get Hits
                CachedRowSet hits = RowSetProvider.newFactory().createCachedRowSet();
                hits.populate(conn.executeQuery(sql, params));

                hits.last();
                int numberOfHits = hits.getRow();
                hits.beforeFirst();

                // Hits Exceeds a Page?
                if (numberOfHits >= query.getMaxFeatures())
                {
                    // Yes; Get Total Hits
                    sql = String.format("SELECT DISTINCT COUNT(*) FROM %s %s", preparedFilter.getFrom(), where);

                    // Get Total Results
                    ResultSet rowset = conn.executeQuery(sql, params);

                    if (rowset.next())
                    {
                        results.setTotal(rowset.getLong(1));
                    }
                }
                else
                {
                    results.setTotal(numberOfHits);
                }

                results.setResults(hits);
            }
        }
        catch (FilterToSQLException | SQLException | ParseException | CoalesceException e1)
        {
            throw new CoalescePersistorException("Search Failed", e1);
        }

        return results;
    }

    @Override
    public EnumSet<EPersistorCapabilities> getCapabilities()
    {
        // TODO Pull capabilities from API
        EnumSet<EPersistorCapabilities> capabilities = super.getCapabilities();
        //capabilities.addAll(EnumSet.of(EPersistorCapabilities.SEARCH));

        return capabilities;
    }

    @Override
    public Capabilities getSearchCapabilities()
    {
        return SQLCoalescePreparedFilter.createCapabilities();
    }

    private List<CoalesceParameter> getParameters(SQLCoalescePreparedFilter filter) throws ParseException
    {

        List<CoalesceParameter> parameters = new ArrayList<>();

        // Add Parameters
        for (Object value : filter.getLiteralValues())
        {
            parameters.add(new CoalesceParameter(value.toString(), Types.CHAR));
        }

        // if (!filter.isIgnoreSecurity())
        // {
        //
        // for (EMasks mask : EMasks.values())
        // {
        // parameters.add(new
        // CoalesceParameter(SecurityBitmaskHelper.toString(code.getMask(mask))));
        // }
        //
        // parameters.add(new CoalesceParameter(userId, Types.CHAR));
        // }

        return parameters;

    }
}
