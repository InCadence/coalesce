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

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.CoalesceTableHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.framework.CoalesceSettings;
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.framework.persistance.*;
import com.incadencecorp.coalesce.search.resultset.CoalesceCommonColumns;
import org.apache.commons.lang3.NotImplementedException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This implementation uses the SQL data controllers.
 *
 * @author GGaito
 */
public class SQLTemplatePersisterImpl extends CoalescePersistorBase implements ICoalesceTemplatePersister {

    private static Logger LOGGER = LoggerFactory.getLogger(SQLTemplatePersisterImpl.class);
    private static final SQLNormalizer NORMALIZER = new SQLNormalizer();

    private static final CoalesceCommonColumns COLUMNS = new CoalesceCommonColumns(NORMALIZER);
    private String _schema;
    private SQLDataConnector connector;
    private static final ConcurrentMap<String, Boolean> STORED_PROCEDURE_EXTSTS_CACHE = new ConcurrentHashMap<>();
    protected  Map<String, String> params;
    protected  SQLRegisterIterator iterator;


    /**
     * Default constructor pulling properties from {@link SQLPersisterImplSettings}.
     */
    public SQLTemplatePersisterImpl()
    {
        this.params = SQLPersisterImplSettings.getParameters();

        SQLPersisterImplSettings.setParameters(this.params);
        setConnectionSettings(SQLPersisterImplSettings.getServerConn());
        setSchema(SQLPersisterImplSettings.getDatabaseSchema());
    }

    /**
     * Constructor for allowing custom database not in {@link SQLPersisterImplSettings}.
     */
    public SQLTemplatePersisterImpl(Map<String, String> params)
    {
        this.params = params;
        //Get server connection
        setConnectionSettings(SQLPersisterImplSettings.getServerConn(this.params));
        setSchema(this.params.get("asid.schema"));

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Parameters: ");
            for (Map.Entry<String, String> param : this.params.entrySet())
            {
                LOGGER.debug("\t{} = {}", param.getKey(), param.getValue());
            }
        }
    }

    protected SQLDataConnector getDataConnector() throws CoalescePersistorException
    {
        connector = new SQLDataConnector(params);

        return connector;
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
    public void registerTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = new SQLDataConnector(getConnectionSettings(), this._schema))
        {
            // Create a Database Connection
            try
            {
                conn.openConnection(false);

                saveTemplate(conn, templates);

                for (CoalesceEntityTemplate template : templates)
                {
                    // Create Tables
                    SQLRegisterIterator iterator = new SQLRegisterIterator(NORMALIZER);
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
    public String[] getEntityXml(String... keys) throws CoalescePersistorException {
        return new String[0];
    }

    @Override
    public void unregisterTemplate(String... keys)
    {
        throw new NotImplementedException("unregisterTemplate");
    }

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String key) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = this.getDataConnector())
        {
            String xml = null;
            String sql = "SELECT TemplateXml FROM " + getSchemaPrefix() + "CoalesceEntityTemplate WHERE TemplateKey=?";

            ResultSet results = conn.executeQuery(sql, new CoalesceParameter(key, Types.CHAR));

            if (results.next())
            {
                xml = results.getString("TemplateXml");
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
                templates.add(new ObjectMetaData(results.getString(COLUMNS.getTemplateKey()),
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
    protected void saveTemplate(CoalesceDataConnectorBase conn, CoalesceEntityTemplate... templates) throws CoalescePersistorException {
        try
        {
            for (CoalesceEntityTemplate template : templates)
            {
                ((SQLDataConnector) conn).coalesceEntityTemplate_InsertOrUpdate(template);
            }
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
            throw new CoalescePersistorException("PersistEntityTemplate", e);
        }
    }

    @Override
    protected boolean flattenObject(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException {
        boolean isSuccessful = true;
        SQLDataConnector conn = connector;

        if (conn == null)
        {
            conn = this.getDataConnector();
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

    @Override
    protected boolean flattenCore(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException {
        return false;
    }

    public String getSchemaPrefix()
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

    private boolean updateCoalesceObject(CoalesceObject coalesceObject, SQLDataConnector conn, boolean allowRemoval)
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
     * @param conn           is the SQLDataConnector database connection
     * @return isSuccessful = True = Successful add/update operation.
     * @throws SQLException on error
     */
    private boolean persistObject(CoalesceObject coalesceObject, SQLDataConnector conn) throws SQLException
    {
        boolean isSuccessful = true;

        switch (coalesceObject.getType())
        {
            case "entity":

                // isSuccessful = checkLastModified(coalesceObject, conn);
                isSuccessful = persistEntityObject((CoalesceEntity) coalesceObject, conn);
                break;

            case "section":
                if (CoalesceSettings.getUseIndexing())
                {
                    isSuccessful = persistSectionObject((CoalesceSection) coalesceObject, conn);
                }
                break;

//            case "recordset":
//                if (CoalesceSettings.getUseIndexing())
//                {
//                    isSuccessful = persistRecordsetObject((CoalesceRecordset) coalesceObject, conn);
//                }
//                break;
            case "fielddefinition":
                // if (CoalesceSettings.getUseIndexing())
                // {
                // Removed Field Definition Persisting
                // isSuccessful =
                // PersistFieldDefinitionObject((CoalesceFieldDefinition)
                // coalesceObject, conn);
                // }
                break;

            case "record":
                if (CoalesceSettings.getUseIndexing())
                {
                    isSuccessful = persistRecordObject((CoalesceRecord) coalesceObject, conn);
                }
                break;

            case "field":// Not testing the type to ascertain if it is BINARY now.
                if (CoalesceSettings.getUseIndexing())
                {
                    isSuccessful = persistFieldObject((CoalesceField<?>) coalesceObject, conn);
                }
                break;

            case "fieldhistory":
                if (CoalesceSettings.getUseIndexing())
                {
                    isSuccessful = persistFieldHistoryObject((CoalesceFieldHistory) coalesceObject, conn);
                }
                break;

            case "linkagesection":
                if (CoalesceSettings.getUseIndexing())
                {
                    isSuccessful = persistLinkageSectionObject((CoalesceLinkageSection) coalesceObject, conn);
                }
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
    private boolean persistEntityObject(CoalesceEntity entity, SQLDataConnector conn) throws SQLException
    {
        // Return true if no update is required.
        return !checkLastModified(entity, conn) || conn.coalesceEntity_InsertOrUpdate(entity.getKey(),
                entity.getName(),
                entity.getSource(),
                entity.getVersion(),
                entity.getEntityId(),
                entity.getEntityIdType(),
                entity.toXml("UTF-16"),
                entity.getDateCreated(),
                entity.getLastModified(),
                entity.getUploadedToServer());

    }
    /**
     * Returns the comparison for the Coalesce object last modified date versus
     * the same objects value in the database.
     *
     * @param coalesceObject the Coalesce object to have it's last modified date
     *                       checked.
     * @param conn           is the SQLDataConnector database connection
     * @return False = Out of Date
     * @throws SQLException on error
     */
    private boolean checkLastModified(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn) throws SQLException
    {
        boolean isOutOfDate = true;

        // Get LastModified from the Database
        DateTime lastModified = this.getCoalesceObjectLastModified(coalesceObject.getKey(), coalesceObject.getType(), conn);

        // DB Has Valid Time?
        if (lastModified != null)
        {
            // Remove NanoSeconds (100 ns / Tick and 1,000,000 ns / ms = 10,000
            // Ticks / ms)
            long objectTicks = coalesceObject.getLastModified().getMillis();
            long SQLRecordTicks = lastModified.getMillis();

            // TODO: Round Ticks for SQL (Not sure if this is required for .NET)
            // ObjectTicks = this.RoundTicksForSQL(ObjectTicks);

            if (objectTicks == SQLRecordTicks)
            {
                // They're equal; No Update Required
                isOutOfDate = false;
            }
        }

        return isOutOfDate;
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

    /**
     * Adds or Updates a Coalesce section that matches the given parameters.
     *
     * @param section the XsdSection to be added or updated
     * @param conn    is the SQLDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException on error
     */
    private boolean persistSectionObject(CoalesceSection section, CoalesceDataConnectorBase conn) throws SQLException
    {
        // Return true if no update is required.
        return true;
    }

    /**
     * Adds or Updates a Coalesce recordset that matches the given parameters.
     *
     * @param recordset the XsdRecordset to be added or updated
     * @param conn      is the SQLDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException on error
     */
//    private boolean persistRecordsetObject(CoalesceRecordset recordset, CoalesceDataConnectorBase conn) throws SQLException
//    {
//
//        // Get Parent's information
//        CoalesceIndexInfo info = new CoalesceIndexInfo(recordset);
//        String tablename = NORMALIZER.normalize(info.getTableName());
//
//        SQLDataConnector sqlConn;
//        if (conn instanceof SQLDataConnector)
//        {
//            sqlConn = (SQLDataConnector) conn;
//        }
//        else
//        {
//            throw new SQLException("SQL Persistor requires the SQLDataConnector.");
//        }
//
//        if (tableExists(info, getSchema(), sqlConn))
//        {
//            if (recordset.isActive() && !recordset.getEntity().isMarkedDeleted())
//            {
//                try
//                {
//                    sqlConn.deletePhantomRecords(getSchema(), tablename, recordset);
//                }
//                catch (CoalesceException e)
//                {
//                    throw new SQLException(e);
//                }
//
//                // Iterate through each record within the record set.
//                for (CoalesceRecord record : recordset.getAllRecords())
//                {
//
//                    if (record.isActive())
//                    {
//
//                        List<CoalesceParameter> parameters = new ArrayList<>();
//
//                        // Add required columns
//                        parameters.add(new CoalesceParameter(COLUMNS.getKey(), record.getKey(), Types.OTHER));
//                        // TODO Replace "entitykey"
//                        parameters.add(new CoalesceParameter("entitykey", info.getEntity().getKey(), Types.OTHER));
//                        parameters.add(new CoalesceParameter(COLUMNS.getName(), info.getEntity().getName(), Types.CHAR));
//                        parameters.add(new CoalesceParameter(COLUMNS.getSource(), info.getEntity().getSource(), Types.CHAR));
//                        parameters.add(new CoalesceParameter(COLUMNS.getType(), info.getEntity().getVersion(), Types.CHAR));
//
//                        // Get Table's Columns (Field order cannot be guaranteed
//                        // because the
//                        // entities could potentially be modified outside of
//                        // Coalesce)
//
//                        StoredProcedureArgumentMapper mapper = new StoredProcedureArgumentMapper();
//
//                        for (String column : sqlConn.getColumnNames(getSchema(), info.getTableName()))
//                        {
//
//                            String value;
//
//                            CoalesceField<?> field = record.getFieldByName(column);
//
//                            // Field Found?
//                            if (field != null)
//                            {
//                                // Can flatten Field?
//                                if (field.isFlatten() && !StringHelper.isNullOrEmpty(field.getBaseValue()))
//                                {
//                                    value = field.getBaseValue();
//
//                                    if (field.getDataType() == ECoalesceFieldDataTypes.DATE_TIME_TYPE)
//                                    {
//                                        value = JodaDateTimeHelper.toMySQLDateTime(JodaDateTimeHelper.fromXmlDateTimeUTC(
//                                                value));
//                                    }
//                                }
//                                else
//                                {
//                                    // No; Insert null
//                                    value = null;
//                                }
//
//                                if (value != null)
//                                {
//                                    parameters.add(new CoalesceParameter(column, value, mapper.map(field.getDataType())));
//                                }
//
//                            }
//                            /*
//                             * else { // No; Pass null parameters.add(new
//                             * CoalesceParameter(null, Types.OTHER)); }
//                             */
//                        }
//
//                        if (LOGGER.isDebugEnabled())
//                        {
//                            LOGGER.debug("{} Parameters", info.getProcedureName());
//
//                            for (CoalesceParameter param : parameters)
//                            {
//                                LOGGER.debug("\t{} : {}", param.getValue(), param.getType());
//                            }
//                        }
//
//                        try
//                        {
//                            if (!sqlConn.insertRecord(getSchema(), tablename, parameters))
//                            {
//                                LOGGER.error("FAILED to insert Record for " + getSchema() + "." + tablename);
//                            }
//                        }
//                        catch (CoalesceException ce)
//                        {
//                            throw new SQLException(
//                                    "FAILED to insert Record for " + getSchema() + "." + tablename + ", REASON: "
//                                            + ce.getCause().getLocalizedMessage(), ce);
//                        }
//
//                    }
//                    else
//                    {
//                        try
//                        {
//                            sqlConn.deleteRecord(getSchema(), tablename, record.getKey());
//                        }
//                        catch (CoalesceException ce)
//                        {
//                            throw new SQLException("(FAILED) Deleting Record {}", record.getKey(), ce);
//                        }
//                    }
//
//                }
//            }
//            else
//            {
//                try
//                {
//                    sqlConn.deleteAllRecords(getSchema(), tablename, recordset.getEntity().getKey());
//                }
//                catch (CoalesceException ce)
//                {
//                    throw new SQLException("(FAILED) Deleting Record {}", recordset.getEntity().getKey(), ce);
//                }
//            }
//        }
//        else
//        {
//            LOGGER.warn(String.format("Object Not Registered (%s : %s)", info.getEntity().getName(), recordset.getName()));
//        }
//
//        return false;
//    }

    /**
     * Adds or Updates a Coalesce field definition that matches the given
     * parameters.
     *
     * @param fieldDefinition the XsdFieldDefinition to be added or updated
     * @param conn            is the SQLDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException on error
     */
    protected boolean persistFieldDefinitionObject(CoalesceFieldDefinition fieldDefinition, CoalesceDataConnectorBase conn)
            throws SQLException
    {
        return true;
    }

    /**
     * Adds or Updates a Coalesce record that matches the given parameters.
     *
     * @param record the XsdRecord to be added or updated
     * @param conn   is the SQLDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException on error
     */
    private boolean persistRecordObject(CoalesceRecord record, CoalesceDataConnectorBase conn) throws SQLException
    {
        // Return true if no update is required.
        return true;
    }

    /**
     * Adds or Updates a Coalesce field that matches the given parameters.
     *
     * @param field the XsdField to be added or updated
     * @param conn  is the SQLDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException on error
     */
    private boolean persistFieldObject(CoalesceField<?> field, CoalesceDataConnectorBase conn) throws SQLException
    {
        // Return true if no update is required.
        return true;
    }

    /**
     * Adds or Updates a Coalesce field history that matches the given
     * parameters.
     *
     * @param fieldHistory the XsdFieldHistory to be added or updated
     * @param conn         is the SQLDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException on error
     */
    private boolean persistFieldHistoryObject(CoalesceFieldHistory fieldHistory, CoalesceDataConnectorBase conn)
            throws SQLException
    {
        // Return true if no update is required.
        return true;
    }

    /**
     * Adds or Updates a Coalesce linkage section that matches the given
     * parameters.
     *
     * @param linkageSection the XsdLinkageSection to be added or updated
     * @param conn           is the SQLDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException on error
     */
    private boolean persistLinkageSectionObject(CoalesceLinkageSection linkageSection, CoalesceDataConnectorBase conn)
            throws SQLException
    {
        // TODO:Return true if no update is required.
        if (!checkLastModified(linkageSection, conn))
        {
            return true;
        }
        // Yes; Call Store Procedure
        return conn.executeProcedure("CoalesceLinkageSection_InsertOrUpdate",
                                     new CoalesceParameter(linkageSection.getKey()),
                                     new CoalesceParameter(linkageSection.getName()),
                                     new CoalesceParameter(linkageSection.getParent().getKey()),
                                     new CoalesceParameter(linkageSection.getParent().getType()),
                                     new CoalesceParameter(linkageSection.getDateCreated()),
                                     new CoalesceParameter(linkageSection.getLastModified()));
    }

    /**
     * Adds or Updates a Coalesce linkage that matches the given parameters.
     *
     * @param linkage the XsdLinkage to be added or updated
     * @param conn    is the SQLDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException on error
     */
    private boolean persistLinkageObject(CoalesceLinkage linkage, SQLDataConnector conn) throws SQLException
    {
        // Return true if no update is required.
        if (!checkLastModified(linkage, conn))
        {
            return true;
        }

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


//    private static boolean tableExists(final CoalesceIndexInfo info, final String schema, final SQLDataConnector conn)
//            throws SQLException
//    {
//        String tableName = info.getTableName();
//        return conn.tableExists(schema, tableName);
//    }

    protected String getSchema()
    {
        return _schema;
    }

    /**
     * Deletes the Coalesce object and CoalesceObjectMap that matches the given
     * parameters.
     *
     * @param coalesceObject the Coalesce object to be deleted
     * @param conn           is the SQLDataConnector database connection
     * @return True = Successful delete
     * @throws SQLException on error
     */
    private boolean deleteObject(CoalesceObject coalesceObject, SQLDataConnector conn) throws SQLException
    {
        boolean isSuccessful;

        switch (coalesceObject.getType())
        {
            case "entity":
                isSuccessful = conn.deleteEntity(coalesceObject.getKey());
                break;
            default:
                isSuccessful = persistObject(coalesceObject, conn);
                break;
        }

        return isSuccessful;
    }

    //below are functions created for future implementations

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

    private static String normalizeCacheKey(final String name, final String schema)
    {
        return schema + "." + name;
    }
}
