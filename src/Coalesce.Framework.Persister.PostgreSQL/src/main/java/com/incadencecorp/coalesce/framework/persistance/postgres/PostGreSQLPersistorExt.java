/*-----------------------------------------------------------------------------'
 Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.framework.persistance.postgres;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.CoalesceTableHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
import com.incadencecorp.coalesce.framework.persistance.postgres.mappers.StoredProcedureArgumentMapper;
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
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This extension flattens record sets into their own tables.
 *
 * @author n78554
 */
public class PostGreSQLPersistorExt extends PostGreSQLPersistor implements ICoalesceSearchPersistor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostGreSQLPersistorExt.class);

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

    private final StoredProcedureArgumentMapper procedureMapper = new StoredProcedureArgumentMapper();

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/

    /**
     * Uses {@link PostGreSQLSettings} to configure the database settings.
     */
    public PostGreSQLPersistorExt()
    {
        setConnectionSettings(PostGreSQLSettings.getServerConn());
        setSchema(PostGreSQLSettings.getDatabaseSchema());

    }

    /**
     * Uses {@link PostGreSQLSettings} to configure the database settings.
     *
     * @param userId   User ID used for connection to the DB.
     * @param password User's password used for connecting to the DB.
     */
    public PostGreSQLPersistorExt(String userId, String password)
    {

        PostGreSQLSettings.setUserName(userId);
        PostGreSQLSettings.setUserPassword(password);

        setConnectionSettings(PostGreSQLSettings.getServerConn());
        setSchema(PostGreSQLSettings.getDatabaseSchema());

    }

    /*--------------------------------------------------------------------------
    Override Methods
    --------------------------------------------------------------------------*/

    @Override
    protected boolean persistRecordsetObject(CoalesceRecordset recordset, CoalesceDataConnectorBase conn) throws SQLException
    {

        // Get Parent's information
        CoalesceIndexInfo info = new CoalesceIndexInfo(recordset);

        if (storedProcedureExists(info, getSchema(), conn))
        {
            if (recordset.isActive())
            {
                // Iterate through each record within the record set.
                for (CoalesceRecord record : recordset.getAllRecords())
                {

                    if (record.isActive())
                    {

                        List<CoalesceParameter> parameters = new ArrayList<>();

                        // Add required columns
                        parameters.add(new CoalesceParameter(record.getKey(), Types.OTHER));
                        parameters.add(new CoalesceParameter(info.getEntity().getKey(), Types.OTHER));
                        parameters.add(new CoalesceParameter(info.getEntity().getName(), Types.CHAR));
                        parameters.add(new CoalesceParameter(info.getEntity().getSource(), Types.CHAR));
                        parameters.add(new CoalesceParameter(getType(info.getEntity()), Types.CHAR));

                        // Get Table's Columns (Field order cannot be guaranteed
                        // because the
                        // entities could potentially be modified outside of
                        // Coalesce)

                        for (String column : getColumnNames(info.getTableName(), conn))
                        {

                            String value;

                            CoalesceField<?> field = record.getFieldByName(column);

                            // Field Found?
                            if (field != null)
                            {
                                // Can flatten Field?
                                if (field.isFlatten() && !StringHelper.isNullOrEmpty(field.getBaseValue()))
                                {
                                    value = field.getBaseValue();
                                }
                                else
                                {
                                    // No; Insert null
                                    value = null;
                                }

                                parameters.add(new CoalesceParameter(value, procedureMapper.map(field.getDataType())));

                                switch (field.getDataType())
                                {
                                case CIRCLE_TYPE:
                                    // Add Additional Parameter for Radius
                                    if (field.isFlatten() && !StringHelper.isNullOrEmpty(field.getBaseValue()))
                                    {
                                        value = field.getAttribute("radius");
                                    }
                                    else
                                    {
                                        // No; Insert null
                                        value = null;
                                    }

                                    parameters.add(new CoalesceParameter(value,
                                                                         procedureMapper.map(ECoalesceFieldDataTypes.DOUBLE_TYPE)));

                                    break;
                                default:
                                    // Do Nothing
                                    break;
                                }

                            }
                            else
                            {
                                // No; Pass null
                                parameters.add(new CoalesceParameter(null, Types.OTHER));
                            }
                        }

                        if (LOGGER.isDebugEnabled())
                        {
                            LOGGER.debug("{} Parameters", info.getProcedureName());

                            for (CoalesceParameter param : parameters)
                            {
                                LOGGER.debug(param.getValue());
                            }
                        }

                        if (!conn.executeProcedure(info.getProcedureName(),
                                                   parameters.toArray(new CoalesceParameter[parameters.size()])))
                        {
                            LOGGER.error("FAILED: executeProcedure");
                        }

                    }
                    else
                    {
                        deleteRecord(record, conn);
                    }

                }
            }
            else
            {
                deleteRecordset(recordset, conn);
            }
        }
        else
        {
            LOGGER.warn(String.format("Object Not Registered (%s : %s)", info.getEntity().getName(), recordset.getName()));
        }

        return false;
    }

    @Override
    protected boolean persistMapTableEntry(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn) throws SQLException
    {
        // Don't Persist
        return true;
    }

    @Override
    protected boolean persistSectionObject(CoalesceSection section, CoalesceDataConnectorBase conn) throws SQLException
    {
        // Don't Persist
        return true;
    }

    @Override
    protected boolean persistLinkageSectionObject(CoalesceLinkageSection linkageSection, CoalesceDataConnectorBase conn)
            throws SQLException
    {
        // Don't Persist
        return true;
    }

    @Override
    protected boolean deleteObject(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn) throws SQLException
    {

        if (coalesceObject instanceof CoalesceRecordset)
        {
            deleteRecordset((CoalesceRecordset) coalesceObject, conn);
        }
        else if (coalesceObject instanceof CoalesceRecord)
        {
            deleteRecord((CoalesceRecord) coalesceObject, conn);
        }
        else if (coalesceObject instanceof CoalesceEntity || coalesceObject instanceof CoalesceLinkage)
        {
            String objectType = coalesceObject.getType();
            String objectKey = coalesceObject.getKey();
            String tableName = CoalesceTableHelper.getTableNameForObjectType(objectType);

            String sql = String.format(SQL_DELETE_OBJECTKEY, getSchemaPrefix() + tableName);

            // Remove from Coalesce Tables
            conn.executeUpdate(sql, new CoalesceParameter(objectKey, Types.OTHER));
        }

        return true;
    }

    @Override
    public final void registerTemplate(final CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {

        try (CoalesceDataConnectorBase conn = new PostGreSQLDataConnector(getConnectionSettings(), getSchemaPrefix()))
        {

            // Create a Database Connection
            try
            {
                conn.openConnection(false);

                saveTemplate(conn, templates);

                for (CoalesceEntityTemplate template : templates)
                {
                    // Create Tables
                    PostGreSQLRegisterIterator iterator = new PostGreSQLRegisterIterator();
                    iterator.setSchema(getSchema());
                    iterator.register(template, conn);

                    // Clear Stored Procedure Cache
                    clearCache(template);
                }

                conn.commit();

            }
            catch (CoalescePersistorException e)
            {
                conn.rollback();
                throw e;
            }
            catch (SQLException | CoalesceException e)
            {
                conn.rollback();
                throw new CoalescePersistorException("Creating Indexes", e);
            }
        }
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
            PostGresCoalescePreparedFilter preparedFilter = new PostGresCoalescePreparedFilter(PostGreSQLSettings.getDatabaseSchema());
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

            try (CoalesceDataConnectorBase conn = new PostGreSQLDataConnector(getConnectionSettings(), getSchemaPrefix()))
            {
                String sql = String.format("SELECT DISTINCT %s FROM %s %s %s %s",
                                           preparedFilter.getColumns(),
                                           preparedFilter.getFrom(),
                                           where,
                                           preparedFilter.getSorting(),
                                           preparedFilter.getLimit());

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
    public Capabilities getSearchCapabilities()
    {
        return PostGresCoalescePreparedFilter.createCapabilities();
    }

    private List<CoalesceParameter> getParameters(PostGresCoalescePreparedFilter filter) throws ParseException
    {

        List<CoalesceParameter> parameters = new ArrayList<>();

        // Add Parameters
        for (Object value : filter.getLiteralValues())
        {
            parameters.add(new CoalesceParameter(value.toString(), Types.OTHER));
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

    /*--------------------------------------------------------------------------
    Public Methods
    --------------------------------------------------------------------------*/

    /**
     * Executes the given query
     *
     * @param query
     * @param parameters
     * @return the results
     */
    public int executeUpdate(String query, CoalesceParameter... parameters)
    {
        int results;

        try (CoalesceDataConnectorBase conn = new PostGreSQLDataConnector(getConnectionSettings(), getSchema()))
        {
            results = conn.executeUpdate(query, parameters);
        }
        catch (CoalescePersistorException | SQLException e)
        {
            LOGGER.error("Failed to execute query.", e);
            results = -1;
        }

        return results;
    }

    /**
     * Executes the given query
     *
     * @param query
     * @param parameters
     * @return the results
     */
    public CachedRowSet executeQuery(String query, CoalesceParameter... parameters)
    {
        CachedRowSet rowset = null;

        try (CoalesceDataConnectorBase conn = new PostGreSQLDataConnector(getConnectionSettings(), getSchema()))
        {
            rowset = RowSetProvider.newFactory().createCachedRowSet();
            rowset.populate(conn.executeQuery(query, parameters));
        }
        catch (CoalescePersistorException | SQLException e)
        {
            LOGGER.error("Failed to execute query.", e);
        }

        return rowset;
    }

    /**
     * Cleared stored procedure cache that indicates whether a procedure exists
     * or not.
     *
     * @param entity
     */
    public void clearCache(CoalesceEntity entity)
    {

        // Iterate Through Sections
        for (CoalesceSection section : entity.getSections().values())
        {

            // Iterate Through Record Sets
            for (CoalesceRecordset recordset : section.getRecordsetsAsList())
            {

                // Determine Record Set's Cache Key
                String key = normalizeCacheKey(new CoalesceIndexInfo(recordset).getProcedureName(), getSchema());

                Boolean storedProcedureExists = STORED_PROCEDURE_EXTSTS_CACHE.get(key);

                // Record Set's Stored Procedure has been cached as
                // non-existent?
                if (storedProcedureExists != null && !storedProcedureExists)
                {
                    // Yes; Remove
                    STORED_PROCEDURE_EXTSTS_CACHE.remove(key);
                }

            }

        }

    }

    /**
     * Cleared stored procedure cache that indicates whether a procedure exists
     * or not.
     *
     * @param template
     */
    public void clearCache(CoalesceEntityTemplate template)
    {
        clearCache(template.createNewEntity());
    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    /**
     * @param info   of the record set.
     * @param schema of the procedure
     * @param conn   to use for the query
     * @return <code>true</code> if the stored procedure already exists.
     * @throws SQLException
     */
    public static boolean storedProcedureExists(final CoalesceIndexInfo info,
                                                final String schema,
                                                final CoalesceDataConnectorBase conn) throws SQLException
    {
        return storedProcedureExists(info.getProcedureName(), schema, conn);
    }

    /**
     * @param name   of the procedure
     * @param schema of the procedure
     * @param conn   to use for the query
     * @return <code>true</code> if the stored procedure already exists.
     * @throws SQLException
     */
    public static boolean storedProcedureExists(final String name, final String schema, final CoalesceDataConnectorBase conn)
            throws SQLException
    {

        String normalizedKey = normalizeCacheKey(name, schema);

        // Check Cache
        Boolean exists = STORED_PROCEDURE_EXTSTS_CACHE.get(normalizedKey);

        // Cached?
        if (exists == null)
        {

            // No; Search for Procedure
            ResultSet results = conn.executeQuery(SQL_FIND_PROCEDURE_FORMAT,
                                                  new CoalesceParameter(name),
                                                  new CoalesceParameter(schema));
            // Found?
            exists = results.next();

            // Update Cache
            STORED_PROCEDURE_EXTSTS_CACHE.put(normalizedKey, exists);

        }

        return exists;

    }

    private static String normalizeCacheKey(final String name, final String schema)
    {
        return schema + "." + name;
    }

    /*--------------------------------------------------------------------------
    Private Utility Methods
    --------------------------------------------------------------------------*/

    private String[] getColumnNames(String tablename, CoalesceDataConnectorBase conn) throws SQLException
    {

        String[] columns;

        if (COLUMNS_CACHE.contains(tablename))
        {

            columns = COLUMNS_CACHE.get(tablename);

        }
        else
        {

            List<String> columnList = new ArrayList<>();

            ResultSet results = conn.executeQuery(SQL_GET_COLUMN_NAMES,
                                                  new CoalesceParameter(tablename),
                                                  new CoalesceParameter(getSchema()));

            if (results != null)
            {

                results.next(); // Skip Object Key
                results.next(); // Skip Entity Key
                results.next(); // Skip Entity Name
                results.next(); // Skip Entity Source
                results.next(); // Skip Entity Type

                while (results.next())
                {
                    columnList.add(results.getString(1));
                }
            }

            columns = columnList.toArray(new String[columnList.size()]);

            COLUMNS_CACHE.put(tablename, columns);

        }

        return columns;

    }

    /**
     * Removes the row from the flattened table.
     *
     * @param record
     * @param conn
     * @throws SQLException
     */
    private void deleteRecord(CoalesceRecord record, CoalesceDataConnectorBase conn) throws SQLException
    {

        // Get Index Table Information
        CoalesceIndexInfo info = new CoalesceIndexInfo(record.getParent());

        String sql = String.format(SQL_DELETE_OBJECTKEY, getSchemaPrefix() + info.getTableName());

        // Remove Row
        conn.executeUpdate(sql, new CoalesceParameter(record.getKey(), Types.OTHER));

        deleteFromListTables(record, conn);

    }

    /**
     * Removes all rows belonging to the recordset from the flattened table.
     *
     * @param recordset
     * @param conn
     * @throws SQLException
     */
    private void deleteRecordset(CoalesceRecordset recordset, CoalesceDataConnectorBase conn) throws SQLException
    {

        // Get Index Table Information
        CoalesceIndexInfo info = new CoalesceIndexInfo(recordset);

        String sql = String.format(SQL_DELETE_ENTITYKEY, getSchemaPrefix() + info.getTableName());

        // Remove All Rows
        conn.executeUpdate(sql, new CoalesceParameter(info.getEntity().getKey(), Types.OTHER));

        // Remove All Rows in List Tables
        for (CoalesceRecord record : recordset.getAllRecords())
        {
            deleteFromListTables(record, conn);
        }
    }

    private void deleteFromListTables(CoalesceRecord record, CoalesceDataConnectorBase conn) throws SQLException
    {

        List<ECoalesceFieldDataTypes> visited = new ArrayList<>();

        for (CoalesceField<?> field : record.getFields())
        {

            if (field.isListType() && !visited.contains(field.getDataType()))
            {
                deleteFromListTable(record.getKey(), field.getDataType(), conn);
                visited.add(field.getDataType());
            }

        }
    }

    /**
     * Deletes all rows of the record from the list table of the specified type.
     *
     * @param recordKey
     * @param type
     * @param conn
     * @throws SQLException
     */
    private void deleteFromListTable(String recordKey, ECoalesceFieldDataTypes type, CoalesceDataConnectorBase conn)
            throws SQLException
    {

        String sql = String.format(SQL_CLEAN_LIST_TABLES, getSchema(), type.getLabel());

        conn.executeUpdate(sql, new CoalesceParameter(recordKey, Types.OTHER));

    }

}
