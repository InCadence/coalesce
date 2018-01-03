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
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.framework.persistance.*;
import com.incadencecorp.coalesce.framework.persistance.postgres.CoalesceIndexInfo;
import com.incadencecorp.coalesce.framework.persistance.postgres.mappers.StoredProcedureArgumentMapper;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalesceFeatureTypeFactory;
import com.incadencecorp.coalesce.search.resultset.CoalesceCommonColumns;
import org.geotools.data.Query;
import org.geotools.data.jdbc.FilterToSQLException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

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
     * @param schema
     */
    public void setSchema(String schema)
    {
        _schema = schema;
    }

    protected String getSchemaPrefix()
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
        CoalesceDataConnectorBase conn = derbyDataConnector;

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
     * @throws SQLException
     */
    protected boolean deleteObject(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn) throws SQLException
    {
        String objectType = coalesceObject.getType();
        String objectKey = coalesceObject.getKey();
        String tableName = CoalesceTableHelper.getTableNameForObjectType(objectType);

        conn.executeUpdate("DELETE FROM " + getSchemaPrefix() + tableName + " WHERE " + COLUMNS.getKey() + "=?",
                           new CoalesceParameter(objectKey, Types.CHAR));

        return true;
    }

    private boolean updateCoalesceObject(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn, boolean allowRemoval)
            throws SQLException

    {
        boolean isSuccessful = false;
        boolean isDeleted = false;

        if (coalesceObject.isFlatten())
        {
            switch (coalesceObject.getStatus())
            {
            case READONLY:
            case ACTIVE:
                // Persist Object
                isSuccessful = persistObject(coalesceObject, conn);
                break;

            case DELETED:
                if (allowRemoval)
                {
                    // Delete Object
                    isSuccessful = deleteObject(coalesceObject, conn);
                    isDeleted = coalesceObject instanceof CoalesceEntity;
                }
                else
                {
                    // Mark Object as Deleted
                    isSuccessful = persistObject(coalesceObject, conn);
                }

                break;

            default:
                isSuccessful = false;
            }

            // Successful?
            if (isSuccessful && !isDeleted)
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
     * @throws SQLException
     */
    protected boolean persistObject(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn) throws SQLException
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

        case "recordset":
            if (CoalesceSettings.getUseIndexing())
            {
                isSuccessful = persistRecordsetObject((CoalesceRecordset) coalesceObject, conn);
            }
            break;
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

    /**
     * Adds or updates map table entry for a given element.
     *
     * @param coalesceObject the Coalesce object to be added or updated
     * @param conn           is the SQLServerDataConnector database connection
     * @return True if successfully added/updated.
     * @throws SQLException
     */
    protected boolean persistMapTableEntry(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn) throws SQLException
    {
        return true;
    }

    /**
     * Adds or Updates a Coalesce entity that matches the given parameters.
     *
     * @param entity the XsdEntity to be added or updated
     * @param conn   is the PostGresDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistEntityObject(CoalesceEntity entity, CoalesceDataConnectorBase conn) throws SQLException
    {
        // Return true if no update is required.
        if (!checkLastModified(entity, conn))
        {
            return true;
        }

        return ((DerbyDataConnector) conn).coalesceEntity_InsertOrUpdate(entity.getKey(),
                                                                         entity.getName(),
                                                                         entity.getSource(),
                                                                         entity.getVersion(),
                                                                         entity.getEntityId(),
                                                                         entity.getEntityIdType(),
                                                                         entity.toXml(),
                                                                         entity.getDateCreated(),
                                                                         entity.getLastModified());
    }

    /**
     * Adds or Updates a Coalesce section that matches the given parameters.
     *
     * @param section the XsdSection to be added or updated
     * @param conn    is the PostGresDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistSectionObject(CoalesceSection section, CoalesceDataConnectorBase conn) throws SQLException
    {
        // Return true if no update is required.
        return true;
    }

    /**
     * Adds or Updates a Coalesce recordset that matches the given parameters.
     *
     * @param recordset the XsdRecordset to be added or updated
     * @param conn      is the PostGresDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistRecordsetObject(CoalesceRecordset recordset, CoalesceDataConnectorBase conn) throws SQLException
    {

        // Get Parent's information
        CoalesceIndexInfo info = new CoalesceIndexInfo(recordset);
        DerbyDataConnector derbyConn = null;
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
            if (recordset.isActive())
            {
                // Iterate through each record within the record set.
                for (CoalesceRecord record : recordset.getAllRecords())
                {

                    if (record.isActive())
                    {

                        List<CoalesceParameter> parameters = new ArrayList<CoalesceParameter>();

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
                            if (field != null)
                            {
                                // Can flatten Field?
                                if (field.isFlatten() && !StringHelper.isNullOrEmpty(field.getBaseValue()))
                                {
                                    value = field.getBaseValue();

                                    if (field.getDataType() == ECoalesceFieldDataTypes.DATE_TIME_TYPE)
                                    {
                                        value = JodaDateTimeHelper.toMySQLDateTime(JodaDateTimeHelper.fromXmlDateTimeUTC(
                                                value));
                                    }
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
                                LOGGER.debug("{} : {}", param.getValue(), param.getType());
                            }
                        }

                        try
                        {
                            if (!derbyConn.insertRecord(getSchema(), info.getTableName(), parameters))
                            {
                                LOGGER.error("FAILED to insert Record for " + getSchema() + "." + info.getTableName());
                            }
                        }
                        catch (CoalesceException ce)
                        {
                            throw new SQLException(
                                    "FAILED to insert Record for " + getSchema() + "." + info.getTableName() + ", REASON: "
                                            + ce.getCause().getLocalizedMessage(), ce);
                        }

                    }
                    else
                    {
                        // deleteRecord(record, conn); // TODO
                        LOGGER.warn("TODO: delete the inactive record");
                    }

                }
            }
            else
            {
                // deleteRecordset(recordset, conn); // TODO
                LOGGER.warn("TODO: delete the inactive recordset");
            }
        }
        else
        {
            LOGGER.warn(String.format("Object Not Registered (%s : %s)", info.getEntity().getName(), recordset.getName()));
        }

        return false;
    }

    /**
     * Adds or Updates a Coalesce field definition that matches the given
     * parameters.
     *
     * @param fieldDefinition the XsdFieldDefinition to be added or updated
     * @param conn            is the PostGresDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
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
     * @param conn   is the PostGresDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistRecordObject(CoalesceRecord record, CoalesceDataConnectorBase conn) throws SQLException
    {
        // Return true if no update is required.
        return true;
    }

    /**
     * Adds or Updates a Coalesce field that matches the given parameters.
     *
     * @param field the XsdField to be added or updated
     * @param conn  is the PostGresDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistFieldObject(CoalesceField<?> field, CoalesceDataConnectorBase conn) throws SQLException
    {
        // Return true if no update is required.
        return true;
    }

    /**
     * Adds or Updates a Coalesce field history that matches the given
     * parameters.
     *
     * @param fieldHistory the XsdFieldHistory to be added or updated
     * @param conn         is the PostGresDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistFieldHistoryObject(CoalesceFieldHistory fieldHistory, CoalesceDataConnectorBase conn)
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
     * @param conn           is the PostGresDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistLinkageSectionObject(CoalesceLinkageSection linkageSection, CoalesceDataConnectorBase conn)
            throws SQLException
    {
        // Return true if no update is required.
        return true;
    }

    /**
     * Adds or Updates a Coalesce linkage that matches the given parameters.
     *
     * @param linkage the XsdLinkage to be added or updated
     * @param conn    is the PostGresDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistLinkageObject(CoalesceLinkage linkage, CoalesceDataConnectorBase conn) throws SQLException
    {
        // Return true if no update is required.
        if (!checkLastModified(linkage, conn))
        {
            return true;
        }

        // Yes; Call Procedure
        return ((DerbyDataConnector) conn).coalesceLinkage_InsertOrUpdate(linkage);
    }

    /**
     * Returns the EntityMetaData for the Coalesce entity that matches the given
     * parameters.
     *
     * @param key  primary key of the Coalesce entity
     * @param conn is the PostGresDataConnector database connection
     * @return metaData the EntityMetaData for the Coalesce entity.
     * @throws SQLException
     */
    protected EntityMetaData getCoalesceEntityIdAndTypeForKey(String key, CoalesceDataConnectorBase conn) throws SQLException
    {
        EntityMetaData metaData = null;

        // Execute Query
        ResultSet results = conn.executeQuery(
                "SELECT " + COLUMNS.getEntityId() + "," + COLUMNS.getEntityIdType() + "," + COLUMNS.getKey() + " FROM "
                        + getSchemaPrefix() + "CoalesceEntity WHERE ObjectKey=?", new CoalesceParameter(key, Types.CHAR));
        // Get Results
        while (results.next())
        {
            metaData = new EntityMetaData(results.getString(COLUMNS.getEntityId()),
                                          results.getString(COLUMNS.getEntityIdType()),
                                          results.getString(COLUMNS.getKey()));
        }

        return metaData;
    }

    public static boolean tableExists(final CoalesceIndexInfo info, final String schema, final DerbyDataConnector conn)
            throws SQLException
    {
        String tableName = info.getTableName();
        return conn.tableExists(schema, tableName);
    }

    /**
     * Returns the comparison for the Coalesce object last modified date versus
     * the same objects value in the database.
     *
     * @param coalesceObject the Coalesce object to have it's last modified date
     *                       checked.
     * @param conn           is the PostGresDataConnector database connection
     * @return False = Out of Date
     * @throws SQLException
     */
    protected boolean checkLastModified(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn) throws SQLException
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

    @Override
    protected boolean flattenCore(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        boolean isSuccessful = false;

        CoalesceDataConnectorBase conn = null;

        // Create a Database Connection
        try
        {
            conn = new DerbyDataConnector(getConnectionSettings(), this.getSchema(), "memory");
            conn.openConnection(false);

            for (CoalesceEntity entity : entities)
            {
                if (persistEntityObject(entity, conn))
                {

                    isSuccessful = updateFileContent(entity, conn);

                }
            }

            conn.getConnection().commit();
        }
        catch (Exception e)
        {
            conn.rollback();

            throw new CoalescePersistorException("FlattenObject: " + e.getMessage(), e);
        }
        finally
        {
            conn.close();
        }

        return isSuccessful;
    }

    public DateTime getCoalesceObjectLastModified(String key, String objectType) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = this.getDataConnector())
        {
            return this.getCoalesceObjectLastModified(key, objectType, conn);
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("GetCoalesceObjectLastModified", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetCoalesceObjectLastModified", e);
        }
    }

    public byte[] getBinaryArray(String key) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = this.getDataConnector())
        {

            byte[] binaryArray = null;

            ResultSet results = conn.executeQuery(
                    "SELECT BinaryObject FROM " + getSchemaPrefix() + "CoalesceFieldBinaryData WHERE ObjectKey=?",
                    new CoalesceParameter(key, Types.CHAR));

            // Get Results
            if (results != null && results.first())
            {
                Blob dataVal = results.getBlob("BinaryObject");
                binaryArray = dataVal.getBytes(1, (int) dataVal.length());
            }

            return binaryArray;
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("GetBinaryArray", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetBinaryArray", e);
        }
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
            throw new CoalescePersistorException("PersistEntityTemplate", e);
        }

    }

    public ElementMetaData getXPath(String key, String objectType) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = this.getDataConnector())
        {
            return getXPathRecursive(key, objectType, "", conn);
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("GetXPath", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetXPath", e);
        }
    }

    public String getFieldValue(String fieldKey) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = this.getDataConnector())
        {
            String value = null;

            ResultSet results = conn.executeQuery(
                    "SELECT value FROM " + getSchemaPrefix() + "CoalesceField WHERE ObjectKey =?",
                    new CoalesceParameter(fieldKey, Types.CHAR));

            while (results.next())
            {
                value = results.getString("value");
            }

            return value;
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("GetFieldValue", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetFieldValue", e);
        }
    }

    @Override
    public String[] getEntityXml(String... keys) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = this.getDataConnector())
        {
            List<String> xmlList = new ArrayList<String>();
            List<CoalesceParameter> parameters = new ArrayList<CoalesceParameter>();

            StringBuilder sb = new StringBuilder("");

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

            ResultSet results = conn.executeQuery(SQL, parameters.toArray(new CoalesceParameter[parameters.size()]));

            while (results.next())
            {
                xmlList.add(results.getString(COLUMNS.getXml()));
            }

            return xmlList.toArray(new String[xmlList.size()]);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetEntityXml", e);
        }
    }

    public String getEntityXml(String entityId, String entityIdType) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = this.getDataConnector())
        {
            String value = null;

            ResultSet results = conn.executeQuery(
                    "SELECT " + COLUMNS.getXml() + " FROM " + getSchemaPrefix() + "CoalesceEntity WHERE "
                            + COLUMNS.getEntityId() + "=? AND " + COLUMNS.getEntityIdType() + "=?",
                    new CoalesceParameter(entityId),
                    new CoalesceParameter(entityIdType));

            while (results.next())
            {
                value = results.getString("EntityXml");
            }

            return value;
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("GetEntityXml", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetEntityXml", e);
        }
    }

    public String getEntityXml(String name, String entityId, String entityIdType) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = this.getDataConnector())
        {
            String value = null;

            ResultSet results = conn.executeQuery(
                    "SELECT " + COLUMNS.getXml() + " FROM " + getSchemaPrefix() + "CoalesceEntity WHERE Name=? AND "
                            + COLUMNS.getEntityId() + "=? AND " + COLUMNS.getEntityIdType() + "=?",
                    new CoalesceParameter(name),
                    new CoalesceParameter(entityId),
                    new CoalesceParameter(entityIdType));

            while (results.next())
            {
                value = results.getString(COLUMNS.getXml());
            }

            return value;
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("GetEntityXml", e);
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
        String dateValue = null;

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
     * Sets the active Coalesce field objects matching the parameters given.
     *
     * @param coalesceObject the Coalesce field object.
     * @param conn           is the PostGresDataConnector database connection
     * @throws SQLException ,Exception,CoalescePersistorException
     */
    protected boolean updateFileContent(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn) throws SQLException
    {
        boolean isSuccessful = false;

        if (!coalesceObject.isMarkedDeleted())
        {
            if (coalesceObject.getType().toLowerCase() == "field")
            {
                if (((CoalesceField<?>) coalesceObject).getDataType() == ECoalesceFieldDataTypes.FILE_TYPE)
                {
                    isSuccessful = persistFieldObject((CoalesceField<?>) coalesceObject, conn);
                }
            }

            for (Map.Entry<String, CoalesceObject> s : coalesceObject.getChildCoalesceObjects().entrySet())
            {
                isSuccessful = updateFileContent(s.getValue(), conn);
            }
        }
        return isSuccessful;
    }

    private ElementMetaData getXPathRecursive(String key, String objectType, String xPath, CoalesceDataConnectorBase conn)
            throws SQLException
    {

        boolean isEntityTable = false;
        ElementMetaData meteData = null;

        String sql = "";

        // Get Table Name
        String tableName = CoalesceTableHelper.getTableNameForObjectType(objectType);

        // Check to see if its the Entity Table
        if (tableName.equals("CoalesceEntity"))
        {
            isEntityTable = true;
        }

        if (isEntityTable)
        {
            sql = "SELECT " + COLUMNS.getName() + " FROM ".concat(getSchemaPrefix()).concat(tableName).concat(
                    " WHERE " + COLUMNS.getKey() + "=?");
        }
        else
        {
            sql = "SELECT " + COLUMNS.getName()
                    + ", ParentKey, ParentType FROM ".concat(getSchemaPrefix()).concat(tableName).concat(
                    " WHERE " + COLUMNS.getKey() + "=?");
        }

        ResultSet results = conn.executeQuery(sql, new CoalesceParameter(key.trim(), Types.CHAR));

        // Valid Results?
        while (results.next())
        {

            String name = results.getString("name");

            if (isEntityTable)
            {
                xPath = name + "/" + xPath;

                // Set Meta Data
                meteData = new ElementMetaData(key, xPath);
            }
            else
            {
                String parentKey = results.getString("ParentKey");
                String parentType = results.getString("ParentType");

                if (xPath == null || xPath == "")
                {
                    meteData = getXPathRecursive(parentKey, parentType, name, conn);
                }
                else
                {
                    meteData = getXPathRecursive(parentKey, parentType, name + "/" + xPath, conn);
                }
            }

        }

        return meteData;

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
        catch (SQLException e)
        {
            throw new CoalescePersistorException("GetEntityTemplateKey", e);
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
        throw new CoalescePersistorException("Not Implemented");
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
            query.setStartIndex(1);
        }

        SearchResults results = new SearchResults();
        results.setPage(query.getStartIndex());
        results.setPageSize(query.getMaxFeatures());

        try
        {
            // Create SQL Query
            DerbyCoalescePreparedFilter preparedFilter = new DerbyCoalescePreparedFilter(DerbySettings.getDatabaseSchema());
            preparedFilter.setPageNumber(query.getStartIndex());
            preparedFilter.setPageSize(query.getMaxFeatures());
            preparedFilter.setSortBy(query.getSortBy());
            preparedFilter.setPropertNames(query.getPropertyNames());
            preparedFilter.setIgnoreSecurity(true);
            preparedFilter.setFeatureType(CoalesceFeatureTypeFactory.createSimpleFeatureType());

            // Create SQL
            String where = preparedFilter.encodeToString(query.getFilter());

            // Add Parameters
            List<CoalesceParameter> paramList = new ArrayList<CoalesceParameter>();
            paramList.addAll(getParameters(preparedFilter));

            CoalesceParameter[] params = paramList.toArray(new CoalesceParameter[paramList.size()]);

            try (CoalesceDataConnectorBase conn = new DerbyDataConnector(getConnectionSettings().getDatabase(),
                                                                         this.getSchema(),
                                                                         "memory"))
            {
                String sql = String.format("SELECT DISTINCT %s FROM %s %s %s",
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
                                                                     EPersistorCapabilities.CASE_INSENSITIVE_SEARCH);
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

    private List<CoalesceParameter> getParameters(DerbyCoalescePreparedFilter filter) throws ParseException
    {
        List<CoalesceParameter> parameters = new ArrayList<CoalesceParameter>();

        // Add Parameters
        for (Object value : filter.getLiteralValues())
        {
            parameters.add(new CoalesceParameter(value.toString(), Types.VARCHAR));
        }

        return parameters;
    }

}
