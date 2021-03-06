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
package com.incadencecorp.coalesce.framework.persistance.derby;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.CoalesceTableHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.CoalesceSettings;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFileField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceObject;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
import com.incadencecorp.coalesce.framework.persistance.CoalescePersistorBase;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.framework.persistance.postgres.CoalesceIndexInfo;
import com.incadencecorp.coalesce.framework.persistance.postgres.mappers.StoredProcedureArgumentMapper;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalesceFeatureTypeFactory;
import com.incadencecorp.coalesce.search.resultset.CoalesceCommonColumns;
import org.geotools.data.Query;
import org.geotools.data.jdbc.FilterToSQLException;
import org.geotools.filter.Capabilities;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

public class DerbyPersistor extends CoalescePersistorBase implements ICoalesceSearchPersistor {

    /*--------------------------------------------------------------------------
    Private Members
    --------------------------------------------------------------------------*/
    private static final Logger LOGGER = LoggerFactory.getLogger(DerbyPersistor.class);
    private static final DerbyNormalizer NORMALIZER = new DerbyNormalizer();
    private static final CoalesceCommonColumns COLUMNS = new CoalesceCommonColumns(NORMALIZER);

    private String _schema;
    private DerbyDataConnector derbyDataConnector;

    /**
     * Default Constructor
     */
    public DerbyPersistor()
    {
        setConnectionSettings(DerbySettings.getServerConn());
        setSchema(DerbySettings.getDatabaseSchema());
    }

    /**
     * Set the schema to use when making database calls.
     *
     * @param schema to use when creating queries
     */
    public void setSchema(String schema)
    {
        _schema = schema;
    }

    private String getSchemaPrefix()
    {
        if (_schema != null)
        {
            return _schema + ".";
        }
        else
        {
            return "";
        }
    }

    protected String getSchema()
    {
        return _schema;
    }

    public void setDerbyDataConnector(DerbyDataConnector derbyDataConnector)
    {
        this.derbyDataConnector = derbyDataConnector;
    }

    public DerbyDataConnector getDerbyDataConnector()
    {
        return derbyDataConnector;
    }

    @Override
    protected CoalesceDataConnectorBase getDataConnector() throws CoalescePersistorException
    {
        // if (derbyDataConnector == null)
        // {
        // // create a default
        // ServerConn serverConnection = super.getConnectionSettings();
        // if (serverConnection == null)
        // {
        // serverConnection = DerbyDataConnector.getServerConnection();
        // }
        // derbyDataConnector = new
        // DerbyDataConnector(serverConnection.getDatabase(), null, "memory");
        // }
        // TODO Re-look this.
        return new DerbyDataConnector(getConnectionSettings().getDatabase(), this.getSchema(), "memory");
    }

    @Override
    protected boolean flattenObject(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        boolean isSuccessful = true;
        DerbyDataConnector conn = derbyDataConnector;

        if (conn == null)
        {
            conn = (DerbyDataConnector) this.getDataConnector();
        }

        // Create a Database Connection
        try
        {
            conn.openConnection(false);

            for (CoalesceEntity entity : entities)
            {
                // Persist (Recursively)
                isSuccessful &= updateCoalesceObject(entity, conn, allowRemoval);
            }

            conn.commit();
        }
        catch (SQLException e)
        {
            conn.rollback();

            throw new CoalescePersistorException("FlattenObject: " + e.getMessage(), e);
        }

        return isSuccessful;
    }

    /**
     * Deletes the Coalesce object and CoalesceObjectMap that matches the given
     * parameters.
     *
     * @param coalesceObject the Coalesce object to be deleted
     * @param conn           is the PostGresDataConnector database connection
     * @return True = Successful delete
     * @throws SQLException on error
     */
    private boolean deleteObject(CoalesceObject coalesceObject, DerbyDataConnector conn) throws SQLException
    {
        boolean isSuccessful;

        if ("entity".equalsIgnoreCase(coalesceObject.getType()))
        {
            isSuccessful = conn.deleteEntity(coalesceObject.getKey());
        }
        else
        {
            isSuccessful = persistObject(coalesceObject, conn);
        }

        return isSuccessful;
    }

    private boolean updateCoalesceObject(CoalesceObject coalesceObject, DerbyDataConnector conn, boolean allowRemoval)
            throws SQLException

    {
        boolean isSuccessful = false;

        if (coalesceObject.isFlatten())
        {
            if (!coalesceObject.isMarkedDeleted() && !coalesceObject.getEntity().isMarkedDeleted())
            {
                isSuccessful = persistObject(coalesceObject, conn);
            }
            else
            {
                if (coalesceObject instanceof CoalesceEntity && !allowRemoval)
                {
                    // Mark Object as Deleted
                    isSuccessful = persistObject(coalesceObject, conn);
                }
                else
                {
                    // Delete Object
                    isSuccessful = deleteObject(coalesceObject, conn);
                }
            }

            // Successful?
            if (isSuccessful)
            {
                // Yes; Iterate Through Children
                for (CoalesceObject childObject : coalesceObject.getChildCoalesceObjects().values())
                {
                    updateCoalesceObject(childObject, conn, allowRemoval);
                }
            }

        }
        return isSuccessful;
    }

    /**
     * Adds or Updates a Coalesce object that matches the given parameters.
     *
     * @param coalesceObject the Coalesce object to be added or updated
     * @param conn           is the PostGresDataConnector database connection
     * @return isSuccessful = True = Successful add/update operation.
     * @throws SQLException on error
     */
    private boolean persistObject(CoalesceObject coalesceObject, DerbyDataConnector conn) throws SQLException
    {
        boolean isSuccessful = true;

        switch (coalesceObject.getType())
        {
        case "entity":

            // isSuccessful = checkLastModified(coalesceObject, conn);
            isSuccessful = persistEntityObject((CoalesceEntity) coalesceObject, conn);
            break;

        case "recordset":
            if (CoalesceSettings.getUseIndexing())
            {
                isSuccessful = persistRecordsetObject((CoalesceRecordset) coalesceObject, conn);
            }
            break;

        case "section":
        case "fielddefinition":
        case "record":
        case "field":// Not testing the type to ascertain if it is BINARY now.
        case "fieldhistory":
        case "linkagesection":
            // Do Nothing
            isSuccessful = true;
            break;

        case "linkage":
            if (CoalesceSettings.getUseIndexing())
            {
                isSuccessful = persistLinkageObject((CoalesceLinkage) coalesceObject, conn);
            }
            break;

        default:
            isSuccessful = false;
        }

        return isSuccessful;
    }

    /**
     * Adds or Updates a Coalesce entity that matches the given parameters.
     *
     * @param entity the XsdEntity to be added or updated
     * @param conn   is the PostGresDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException on error
     */
    private boolean persistEntityObject(CoalesceEntity entity, DerbyDataConnector conn) throws SQLException
    {
        return conn.coalesceEntity_InsertOrUpdate(entity.getKey(),
                                                  entity.getName(),
                                                  entity.getSource(),
                                                  entity.getVersion(),
                                                  entity.getStatus(),
                                                  entity.getEntityId(),
                                                  entity.getEntityIdType(),
                                                  entity.toXml(),
                                                  entity.getDateCreated(),
                                                  entity.getCreatedBy(),
                                                  entity.getLastModified(),
                                                  entity.getModifiedBy());

    }

    /**
     * Adds or Updates a Coalesce recordset that matches the given parameters.
     *
     * @param recordset the XsdRecordset to be added or updated
     * @param conn      is the PostGresDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException on error
     */
    private boolean persistRecordsetObject(CoalesceRecordset recordset, CoalesceDataConnectorBase conn) throws SQLException
    {

        // Get Parent's information
        CoalesceIndexInfo info = new CoalesceIndexInfo(recordset);
        String tablename = NORMALIZER.normalize(info.getTableName());

        DerbyDataConnector derbyConn;
        if (conn instanceof DerbyDataConnector)
        {
            derbyConn = (DerbyDataConnector) conn;
        }
        else
        {
            throw new SQLException("Derby Persistor requires the DerbyDataConnector.");
        }

        if (tableExists(info, getSchema(), derbyConn))
        {
            if (recordset.isActive() && !recordset.getEntity().isMarkedDeleted())
            {
                try
                {
                    derbyConn.deletePhantomRecords(getSchema(), tablename, recordset);
                }
                catch (CoalesceException e)
                {
                    throw new SQLException(e);
                }

                // Iterate through each record within the record set.
                for (CoalesceRecord record : recordset.getAllRecords())
                {

                    if (record.isActive())
                    {

                        List<CoalesceParameter> parameters = new ArrayList<>();

                        // Add required columns
                        parameters.add(new CoalesceParameter(COLUMNS.getKey(), record.getKey(), Types.OTHER));
                        // TODO Replace "entitykey"
                        parameters.add(new CoalesceParameter("entitykey", info.getEntity().getKey(), Types.OTHER));
                        parameters.add(new CoalesceParameter(COLUMNS.getName(), info.getEntity().getName(), Types.CHAR));
                        parameters.add(new CoalesceParameter(COLUMNS.getSource(), info.getEntity().getSource(), Types.CHAR));
                        parameters.add(new CoalesceParameter(COLUMNS.getType(), info.getEntity().getVersion(), Types.CHAR));

                        // Get Table's Columns (Field order cannot be guaranteed
                        // because the
                        // entities could potentially be modified outside of
                        // Coalesce)

                        StoredProcedureArgumentMapper mapper = new StoredProcedureArgumentMapper();

                        for (String column : derbyConn.getColumnNames(getSchema(), info.getTableName()))
                        {

                            String value;

                            CoalesceField<?> field = record.getFieldByName(column);

                            // Field Found?
                            if (field != null && field.isFlatten())
                            {
                                // Can flatten Field?
                                if (!StringHelper.isNullOrEmpty(field.getBaseValue()))
                                {
                                    value = field.getBaseValue();

                                    if (field.getDataType() == ECoalesceFieldDataTypes.DATE_TIME_TYPE)
                                    {
                                        value = JodaDateTimeHelper.toMySQLDateTime(JodaDateTimeHelper.fromXmlDateTimeUTC(
                                                value));
                                    }
                                    else if (field.getDataType() == ECoalesceFieldDataTypes.STRING_TYPE
                                            && value.length() > DerbyDataConnector.MAX_STRING_LENGTH)
                                    {
                                        value = value.substring(0, DerbyDataConnector.MAX_STRING_LENGTH);
                                        LOGGER.warn("Truncating Field: {}", column);
                                    }
                                }
                                else if (field.getDataType() == ECoalesceFieldDataTypes.FILE_TYPE)
                                {
                                    value = ((CoalesceFileField) field).getFilename();
                                }
                                else
                                {
                                    // No; Insert null
                                    value = null;
                                }

                                if (value != null)
                                {
                                    parameters.add(new CoalesceParameter(column, value, mapper.map(field.getDataType())));
                                }

                            }
                            /*
                             * else { // No; Pass null parameters.add(new
                             * CoalesceParameter(null, Types.OTHER)); }
                             */
                        }

                        if (LOGGER.isDebugEnabled())
                        {
                            LOGGER.debug("{} Parameters", info.getProcedureName());

                            for (CoalesceParameter param : parameters)
                            {
                                LOGGER.debug("\t{} : {}", param.getValue(), param.getType());
                            }
                        }

                        try
                        {
                            if (!derbyConn.insertRecord(getSchema(), tablename, parameters))
                            {
                                LOGGER.error("FAILED to insert Record for " + getSchema() + "." + tablename);
                            }
                        }
                        catch (CoalesceException ce)
                        {
                            throw new SQLException(
                                    "FAILED to insert Record for " + getSchema() + "." + tablename + ", REASON: "
                                            + ce.getCause().getLocalizedMessage(), ce);
                        }

                    }
                    else
                    {
                        try
                        {
                            derbyConn.deleteRecord(getSchema(), tablename, record.getKey());
                        }
                        catch (CoalesceException ce)
                        {
                            throw new SQLException("(FAILED) Deleting Record {}", record.getKey(), ce);
                        }
                    }

                }
            }
            else
            {
                try
                {
                    derbyConn.deleteAllRecords(getSchema(), tablename, recordset.getEntity().getKey());
                }
                catch (CoalesceException ce)
                {
                    throw new SQLException("(FAILED) Deleting Record {}", recordset.getEntity().getKey(), ce);
                }
            }
        }
        else
        {
            LOGGER.warn(String.format("Object Not Registered (%s : %s)", info.getEntity().getName(), recordset.getName()));
        }

        return false;
    }

    /**
     * Adds or Updates a Coalesce linkage that matches the given parameters.
     *
     * @param linkage the XsdLinkage to be added or updated
     * @param conn    is the PostGresDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException on error
     */
    private boolean persistLinkageObject(CoalesceLinkage linkage, DerbyDataConnector conn) throws SQLException
    {
        // Yes; Call Procedure
        if (linkage.isActive() && !linkage.getEntity().isMarkedDeleted())
        {
            return conn.coalesceLinkage_InsertOrUpdate(linkage);
        }
        else
        {
            return conn.deleteLinkage(linkage.getKey());
        }
    }

    private static boolean tableExists(final CoalesceIndexInfo info, final String schema, final DerbyDataConnector conn)
            throws SQLException
    {
        String tableName = info.getTableName();
        return conn.tableExists(schema, tableName);
    }

    @Override
    protected boolean flattenCore(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        boolean isSuccessful = false;

        DerbyDataConnector conn = null;

        // Create a Database Connection
        try
        {
            conn = new DerbyDataConnector(getConnectionSettings(), this.getSchema(), "memory");
            conn.openConnection(false);

            for (CoalesceEntity entity : entities)
            {
                isSuccessful = persistEntityObject(entity, conn);
            }

            conn.getConnection().commit();
        }
        catch (Exception e)
        {
            if (conn != null)
            {
                conn.rollback();
            }

            throw new CoalescePersistorException("FlattenObject: " + e.getMessage(), e);
        }
        finally
        {
            if (conn != null)
            {
                conn.close();
            }
        }

        return isSuccessful;
    }

    @Override
    public void saveTemplate(CoalesceDataConnectorBase conn, CoalesceEntityTemplate... templates)
            throws CoalescePersistorException
    {
        try
        {
            for (CoalesceEntityTemplate template : templates)
            {
                ((DerbyDataConnector) conn).coalesceEntityTemplate_InsertOrUpdate(template);
            }
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
            throw new CoalescePersistorException("PersistEntityTemplate", e);
        }

    }

    @Override
    public void registerTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        try (DerbyDataConnector conn = (DerbyDataConnector) getDataConnector())
        {
            for (CoalesceEntityTemplate template : templates)
            {
                conn.coalesceEntityTemplate_Register(template);
            }
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
            throw new CoalescePersistorException(e);
        }
    }

    @Override
    public String[] getEntityXml(String... keys) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = this.getDataConnector())
        {
            List<String> xmlList = new ArrayList<>();
            List<CoalesceParameter> parameters = new ArrayList<>();

            StringBuilder sb = new StringBuilder();

            for (String key : keys)
            {
                if (sb.length() > 0)
                {
                    sb.append(",");
                }

                sb.append("?");
                parameters.add(new CoalesceParameter(key, Types.CHAR));
            }

            String SQL = String.format(
                    "SELECT " + COLUMNS.getXml() + " FROM %sCoalesceEntity WHERE " + COLUMNS.getKey() + " IN (%s)",
                    getSchemaPrefix(),
                    sb.toString());

            ResultSet results = conn.executeQuery(SQL, parameters.toArray(new CoalesceParameter[0]));

            while (results.next())
            {
                xmlList.add(results.getString(COLUMNS.getXml()));
            }

            return xmlList.toArray(new String[0]);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetEntityXml", e);
        }
    }

    private DateTime getCoalesceObjectLastModified(String key, String objectType, CoalesceDataConnectorBase conn)
            throws SQLException
    {
        DateTime lastModified = null;

        // Determine the Table Name
        String tableName = CoalesceTableHelper.getTableNameForObjectType(objectType);
        String dateValue;

        ResultSet results = conn.executeQuery(
                "SELECT " + COLUMNS.getLastModified() + " FROM " + getSchemaPrefix() + tableName + " WHERE "
                        + COLUMNS.getKey() + "=?", new CoalesceParameter(key.trim(), Types.VARCHAR));
        ResultSetMetaData resultsmd = results.getMetaData();

        // JODA Function DateTimeFormat will adjust for the Server timezone when
        // converting the time.
        if (resultsmd.getColumnCount() <= 1)
        {
            while (results.next())
            {
                dateValue = results.getString(COLUMNS.getLastModified());
                if (dateValue != null)
                {
                    lastModified = JodaDateTimeHelper.getPostGresDateTim(dateValue);
                }
            }
        }
        return lastModified;

    }

    @Override
    public String getEntityTemplateKey(String name, String source, String version) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = this.getDataConnector())
        {
            String value = null;
            String sql = "SELECT " + COLUMNS.getKey() + " FROM " + getSchemaPrefix() + "CoalesceEntityTemplate WHERE "
                    + COLUMNS.getName() + "=? and " + COLUMNS.getSource() + "=? and " + COLUMNS.getVersion() + "=?";

            ResultSet results = conn.executeQuery(sql,
                                                  new CoalesceParameter(name),
                                                  new CoalesceParameter(source),
                                                  new CoalesceParameter(version));

            while (results.next())
            {
                value = results.getString(COLUMNS.getKey());
            }

            return value;
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetEntityTemplateKey", e);
        }
    }

    @Override
    public List<ObjectMetaData> getEntityTemplateMetadata() throws CoalescePersistorException
    {
        List<ObjectMetaData> templates = new ArrayList<>();

        try (CoalesceDataConnectorBase conn = this.getDataConnector())
        {
            ResultSet results = conn.executeQuery("SELECT * FROM " + getSchemaPrefix() + "CoalesceEntityTemplate");

            while (results.next())
            {
                templates.add(new ObjectMetaData(results.getString(COLUMNS.getKey()),
                                                 results.getString(COLUMNS.getName()),
                                                 results.getString(COLUMNS.getSource()),
                                                 results.getString(COLUMNS.getVersion()),
                                                 JodaDateTimeHelper.getPostGresDateTim(results.getString(COLUMNS.getDateCreated())),
                                                 JodaDateTimeHelper.getPostGresDateTim(results.getString(COLUMNS.getLastModified()))));
            }
        }
        catch (Exception ex)
        {
            throw new CoalescePersistorException("getEntityTemplateMetadata", ex);
        }

        return templates;
    }

    @Override
    public void deleteTemplate(String... keys) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = this.getDataConnector())
        {
            String sql = "DELETE  FROM " + getSchemaPrefix() + "CoalesceEntityTemplate WHERE " + COLUMNS.getKey() + "=?";

            for (String key : keys)
            {
                conn.executeUpdate(sql, new CoalesceParameter(key, Types.CHAR));
            }
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException(e);
        }
    }

    @Override
    public void unregisterTemplate(String... keys) throws CoalescePersistorException
    {
        throw new CoalescePersistorException("Not Implemented");
    }

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String key) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = this.getDataConnector())
        {
            String xml = null;
            String sql = "SELECT " + COLUMNS.getXml() + " FROM " + getSchemaPrefix() + "CoalesceEntityTemplate WHERE "
                    + COLUMNS.getKey() + "=?";

            ResultSet results = conn.executeQuery(sql, new CoalesceParameter(key, Types.CHAR));

            if (results.next())
            {
                xml = results.getString(COLUMNS.getXml());
            }

            if (xml == null)
            {
                throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_FOUND, "Template", key));
            }

            return CoalesceEntityTemplate.create(xml);
        }
        catch (SQLException | CoalesceException e)
        {
            throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_FOUND, "Template", key), e);
        }
    }

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String name, String source, String version)
            throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = this.getDataConnector())
        {
            String xml = null;
            String sql = "SELECT " + COLUMNS.getXml() + " FROM " + getSchemaPrefix() + "CoalesceEntityTemplate WHERE "
                    + COLUMNS.getName() + "=? and " + COLUMNS.getSource() + "=? and " + COLUMNS.getVersion() + "=?";

            ResultSet results = conn.executeQuery(sql,
                                                  new CoalesceParameter(name),
                                                  new CoalesceParameter(source),
                                                  new CoalesceParameter(version));

            if (results.next())
            {
                xml = results.getString(COLUMNS.getXml());
            }

            if (xml == null)
            {
                throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_FOUND,
                                                                   "Template",
                                                                   "Name: " + name + " Source: " + source + " Version: "
                                                                           + version));
            }

            return CoalesceEntityTemplate.create(xml);
        }
        catch (SQLException | CoalesceException e)
        {
            throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_FOUND,
                                                               "Template",
                                                               "Name: " + name + " Source: " + source + " Version: "
                                                                       + version), e);
        }
    }

    @Override
    public SearchResults search(Query query) throws CoalescePersistorException
    {
        if (query.getStartIndex() == null)
        {
            query.setStartIndex(0);
        }

        SearchResults results = new SearchResults();
        results.setPage(query.getStartIndex());
        results.setPageSize(query.getMaxFeatures());

        try
        {
            // Create SQL Query
            DerbyCoalescePreparedFilter preparedFilter = new DerbyCoalescePreparedFilter(_schema);
            preparedFilter.setOffset(query.getStartIndex());
            preparedFilter.setPageSize(query.getMaxFeatures());
            preparedFilter.setSortBy(query.getSortBy());
            preparedFilter.setPropertNames(query.getPropertyNames());
            preparedFilter.setIgnoreSecurity(true);
            preparedFilter.setFeatureType(CoalesceFeatureTypeFactory.createSimpleFeatureType());

            // Create SQL
            String where = preparedFilter.encodeToString(query.getFilter());

            // Add Parameters
            List<CoalesceParameter> paramList = new ArrayList<>(getParameters(preparedFilter));

            CoalesceParameter[] params = paramList.toArray(new CoalesceParameter[0]);

            try (CoalesceDataConnectorBase conn = new DerbyDataConnector(getConnectionSettings().getDatabase(),
                                                                         this.getSchema(),
                                                                         "memory"))
            {
                String sql = String.format("SELECT DISTINCT %s FROM %s %s %s %s",
                                           preparedFilter.getColumns(),
                                           preparedFilter.getFrom(),
                                           where,
                                           preparedFilter.getSorting(),
                                           preparedFilter.getOffsetClause());

                LOGGER.debug("Executing: {}", sql);

                // Get Hits
                CachedRowSet hits = RowSetProvider.newFactory().createCachedRowSet();
                hits.populate(conn.executeQuery(sql, query.getMaxFeatures(), params));

                hits.last();
                int numberOfHits = hits.getRow();
                hits.beforeFirst();

                LOGGER.debug("Hits: {}", numberOfHits);

                // Hits Exceeds a Page?
                if (numberOfHits >= query.getMaxFeatures() && query.getMaxFeatures() != 0)
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
        catch (FilterToSQLException | SQLException | CoalesceException e1)
        {
            throw new CoalescePersistorException("Search Failed", e1);
        }

        return results;
    }

    @Override
    public Capabilities getSearchCapabilities()
    {
        return DerbyCoalescePreparedFilter.createCapabilities();
    }

    /**
     * @return EnumSet of EPersistorCapabilities
     */
    @Override
    public EnumSet<EPersistorCapabilities> getCapabilities()
    {
        EnumSet<EPersistorCapabilities> enumSet = super.getCapabilities();
        EnumSet<EPersistorCapabilities> newCapabilities = EnumSet.of(EPersistorCapabilities.READ_TEMPLATES,
                                                                     EPersistorCapabilities.UPDATE,
                                                                     EPersistorCapabilities.DELETE,
                                                                     EPersistorCapabilities.SEARCH,
                                                                     EPersistorCapabilities.CASE_INSENSITIVE_SEARCH,
                                                                     EPersistorCapabilities.TEMPORAL_SEARCH);
        if (enumSet != null)
        {
            enumSet.addAll(newCapabilities);
        }
        else
        {
            enumSet = newCapabilities;
        }
        return enumSet;
    }

    private List<CoalesceParameter> getParameters(DerbyCoalescePreparedFilter filter)
    {
        List<CoalesceParameter> parameters = new ArrayList<>();

        // Add Parameters
        for (Object value : filter.getLiteralValues())
        {
            if (value instanceof Date)
            {
                parameters.add(new CoalesceParameter(DerbyDataConnector.getDateString((Date) value), Types.DATE));
            }
            else
            {
                parameters.add(new CoalesceParameter(value.toString(), Types.VARCHAR));
            }
        }

        return parameters;
    }

}
