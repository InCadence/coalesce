package com.incadencecorp.coalesce.framework.persistance.sql.impl;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;
import com.incadencecorp.coalesce.framework.persistance.postgres.CoalesceIndexInfo;
import com.incadencecorp.coalesce.search.resultset.CoalesceCommonColumns;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * This DataConnector is for a SQL database.
 *
 * @author GGaito
 */
public class SQLDataConnector extends CoalesceDataConnectorBase {


    private static Connection conn;


    private static final SQLNormalizer NORMALIZER = new SQLNormalizer();
    private static final CoalesceCommonColumns COLUMNS = new CoalesceCommonColumns(NORMALIZER);
    private static final DateTimeFormatter DATE_TIME_FOMRATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    private static String protocol = "jdbc";
    private static String databaseDriver = "sqlserver";
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLDataConnector.class);
    private static final String UNSUPPORTED_TYPE = "UNSUPPORTED_TYPE";
    private static final String CREATE_TABLE_FORMAT = "CREATE TABLE %s.%s(\r%s\r)";

    private boolean isEmbedded;
    private String _prefix = "";
    private String schema = "dbo";


    public final static String DERBY_OBJECT_ALREADY_EXISTS_SQL_STATE = "X0Y32";

    /*--------------------------------------------------------------------------
   Initialization
   --------------------------------------------------------------------------*/

    /**
     *
     */
    public SQLDataConnector(String databaseName,
                            String serverName,
                            String username,
                            String password,
                            int portNumber,
                            String schema) throws CoalescePersistorException
    {
        this(new ServerConn(databaseName, username, password, serverName, portNumber), schema);
    }

    public SQLDataConnector(ServerConn settings, String schema) throws CoalescePersistorException
    {
        setSettings(settings);
        this.schema = schema;
        this.isEmbedded = false;

        try
        {
            Driver driver = new SQLServerDriver();
            DriverManager.registerDriver(driver);
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException(e.getLocalizedMessage(), e);
        }
    }

    public SQLDataConnector()
    {

    }

    public Connection getConn() throws SQLException
    {
        if (_conn == null || _conn.isClosed())
        {
            StringBuilder urlBuilder = new StringBuilder(protocol).append(":").append(databaseDriver).append(":");

            urlBuilder.append("//").append(getSettings().getServerNameWithPort()).append(";databaseName=").append(getSettings().getDatabase());

            LoggerFactory.getLogger(this.getClass()).debug(urlBuilder.toString());

            _conn = DriverManager.getConnection(urlBuilder.toString(), getSettings().getProperties());
        }
        return _conn;
    }

    /**
     * Note: SQL stored procedures cannot handle long XML types. So, we will
     * NOT use stored procedures in the sql persistor for the base tables.
     * <p>
     * I will keep this here temporarily as an example for template registration
     * creation of stored procedures.
     *
     * @param ivarobjectkey
     * @param ivarname
     * @param ivarsource
     * @param ivarversion
     * @param ivarentityid
     * @param ivarentityidtype
     * @param ivarentityxml
     * @param ivardatecreated
     * @param ivarlastmodified
     * @throws SQLException
     */
    public boolean coalesceEntity_InsertOrUpdate(String ivarobjectkey,
                                                 String ivarname,
                                                 String ivarsource,
                                                 String ivarversion,
                                                 String ivarentityid,
                                                 String ivarentityidtype,
                                                 String ivarentityxml,
                                                 DateTime ivardatecreated,
                                                 DateTime ivarlastmodified,
                                                 DateTime ivaruploadedtoserver) throws SQLException
    {

        String dateCreated = getDateString(ivardatecreated);
        String lastModified = getDateString(ivarlastmodified);
        String uploadedToServer = getDateString(ivaruploadedtoserver);

        // get connection, insert or update
        Connection conn = this.getConnection();

        String existsSQL =
                "select " + COLUMNS.getName() + " from " + schema + ".coalesceentity where " + COLUMNS.getKey() + "='"
                        + ivarobjectkey + "'";

        // prepare the query
        PreparedStatement stmt = conn.prepareStatement(existsSQL);
        ResultSet result = stmt.executeQuery();

        int ii = 1;

        if (!result.next())
        {
            // insert
            StringBuilder sql = new StringBuilder("insert into " + schema + ".coalesceentity (");
            sql.append(COLUMNS.getKey()).append(", ");
            sql.append(COLUMNS.getName()).append(", ");
            sql.append(COLUMNS.getSource()).append(", ");
            sql.append(COLUMNS.getVersion()).append(", ");
            sql.append(COLUMNS.getEntityId()).append(",");
            sql.append(COLUMNS.getEntityIdType()).append(", ");
            sql.append(COLUMNS.getXml()).append(", ");
            sql.append(COLUMNS.getDateCreated()).append(", ");
            sql.append(COLUMNS.getLastModified()).append(", ");
            sql.append(COLUMNS.getUploadedToServer());
            sql.append(") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            stmt = conn.prepareStatement(sql.toString());
            stmt.setObject(ii++, ivarobjectkey);
            stmt.setObject(ii++, ivarname);
            stmt.setObject(ii++, ivarsource);
            stmt.setObject(ii++, ivarversion);
            stmt.setObject(ii++, ivarentityid);
            stmt.setObject(ii++, ivarentityidtype);
            stmt.setObject(ii++, ivarentityxml);
            stmt.setObject(ii++, dateCreated);
            stmt.setObject(ii++, lastModified);
            stmt.setObject(ii, uploadedToServer);
        }
        else
        {
            // update
            StringBuilder sql = new StringBuilder("update " + schema + ".coalesceentity set ");
            sql.append(COLUMNS.getName()).append("=?, ");
            sql.append(COLUMNS.getSource()).append("=?, ");
            sql.append(COLUMNS.getVersion()).append("=?, ");
            sql.append(COLUMNS.getEntityId()).append("=?, ");
            sql.append(COLUMNS.getEntityIdType()).append("=?, ");
            sql.append(COLUMNS.getXml()).append("=?, ");
            sql.append(COLUMNS.getDateCreated()).append("=?, ");
            sql.append(COLUMNS.getLastModified()).append("=?, ");
            sql.append(COLUMNS.getUploadedToServer()).append("=?");
            sql.append(" where ").append(COLUMNS.getKey()).append("=?");

            stmt = conn.prepareStatement(sql.toString());
            stmt.setObject(ii++, ivarname);
            stmt.setObject(ii++, ivarsource);
            stmt.setObject(ii++, ivarversion);
            stmt.setObject(ii++, ivarentityid);
            stmt.setObject(ii++, ivarentityidtype);
            stmt.setObject(ii++, ivarentityxml);
            stmt.setObject(ii++, dateCreated);
            stmt.setObject(ii++, lastModified);
            stmt.setObject(ii++, uploadedToServer);
            stmt.setObject(ii, ivarobjectkey);
        }

        LOGGER.trace("{}", stmt);

        stmt.executeUpdate();

        return true;
    }

    public boolean deleteEntity(String key) throws SQLException
    {
        return executeUpdate("DELETE FROM " + schema + ".coalesceentity WHERE " + COLUMNS.getKey() + "=?",
                             new CoalesceParameter(key, Types.CHAR)) != 0;
    }

    public boolean deleteLinkage(String key) throws SQLException

    {
        return executeUpdate("DELETE FROM " + schema + ".coalescelinkage WHERE " + COLUMNS.getKey() + "=?",
                             new CoalesceParameter(key, Types.CHAR)) != 0;
    }

    public boolean coalesceLinkage_InsertOrUpdate(CoalesceLinkage linkage) throws SQLException
    {
        // get connection, insert or update
        Connection conn = this.getConnection();

        // prepare the query
        ResultSet result = null;

        String dateCreated = getDateString(linkage.getDateCreated());
        String lastModified = getDateString(linkage.getLastModified());

        try (Statement stmt = conn.createStatement())
        {
            result = stmt.executeQuery(
                    "select name from " + schema + ".coalescelinkage where objectkey='" + linkage.getKey() + "'");

            if (!result.next())
            {
                // insert
                StringBuilder sql2 = new StringBuilder("insert into " + schema + ".coalescelinkage (");
                sql2.append(COLUMNS.getKey()).append(",");
                sql2.append("Entity1Key,");
                sql2.append(COLUMNS.getName()).append(",");
                sql2.append("Entity1Source,");
                sql2.append("Entity1Version,");
                sql2.append(COLUMNS.getLinkageType()).append(",");
                sql2.append(COLUMNS.getLinkageStatus()).append(",");
                sql2.append(COLUMNS.getEntity2Key()).append(",");
                sql2.append(COLUMNS.getEntity2Name()).append(",");
                sql2.append(COLUMNS.getEntity2Source()).append(",");
                sql2.append(COLUMNS.getEntity2Version()).append(",");
                sql2.append("ClassificationMarking,");
                sql2.append("ModifiedBy,");
                sql2.append("InputLanguage,");
                sql2.append(COLUMNS.getDateCreated()).append(",");
                sql2.append(COLUMNS.getLastModified());
                sql2.append(") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

                int ii = 1;

                try (PreparedStatement stmt2 = conn.prepareStatement(sql2.toString()))
                {
                    stmt2.setString(ii++, linkage.getKey());
                    stmt2.setString(ii++, linkage.getEntity1Key());
                    stmt2.setString(ii++, linkage.getEntity1Name());
                    stmt2.setString(ii++, linkage.getEntity1Source());
                    stmt2.setString(ii++, linkage.getEntity1Version());
                    stmt2.setString(ii++, linkage.getLinkType().getLabel());
                    stmt2.setString(ii++, linkage.getStatus().toString());
                    stmt2.setString(ii++, linkage.getEntity2Key());
                    stmt2.setString(ii++, linkage.getEntity2Name());
                    stmt2.setString(ii++, linkage.getEntity2Source());
                    stmt2.setString(ii++, linkage.getEntity2Version());
                    stmt2.setString(ii++, linkage.getClassificationMarking().toString());
                    stmt2.setString(ii++, linkage.getModifiedBy());
                    stmt2.setString(ii++, (linkage.getInputLang() != null) ? linkage.getInputLang().toString() : "");
                    stmt2.setString(ii++, dateCreated);
                    stmt2.setString(ii++, lastModified);

                    LOGGER.debug(stmt2.toString());

                    stmt2.executeUpdate();
                }
            }
            else
            {
                // update
                StringBuilder sql3 = new StringBuilder("update " + schema + ".coalescelinkage set ");
                sql3.append("ObjectKey=?,");
                sql3.append(COLUMNS.getName()).append("=?,");
                sql3.append(COLUMNS.getSource()).append("=?,");
                sql3.append(COLUMNS.getVersion()).append("=?,");
                sql3.append(COLUMNS.getLinkageType()).append("=?,");
                sql3.append(COLUMNS.getLinkageStatus()).append("=?,");
                sql3.append(COLUMNS.getEntity2Key()).append("=?,");
                sql3.append(COLUMNS.getEntity2Name()).append("=?,");
                sql3.append(COLUMNS.getEntity2Source()).append("=?,");
                sql3.append(COLUMNS.getEntity2Version()).append("=?,");
                sql3.append("ClassificationMarking=?,");
                sql3.append("ModifiedBy=?,");
                sql3.append("InputLanguage=?,");
                sql3.append(COLUMNS.getDateCreated()).append("=?,");
                sql3.append(COLUMNS.getLastModified()).append("=? ");
                sql3.append("where ").append(COLUMNS.getKey()).append("=?");

                int ii = 1;

                try (PreparedStatement stmt3 = conn.prepareStatement(sql3.toString()))
                {
                    stmt3.setString(ii++, linkage.getEntity1Key());
                    stmt3.setString(ii++, linkage.getEntity1Name());
                    stmt3.setString(ii++, linkage.getEntity1Source());
                    stmt3.setString(ii++, linkage.getEntity1Version());
                    stmt3.setString(ii++, linkage.getLinkType().getLabel());
                    stmt3.setString(ii++, linkage.getStatus().toString());
                    stmt3.setString(ii++, linkage.getEntity2Key());
                    stmt3.setString(ii++, linkage.getEntity2Name());
                    stmt3.setString(ii++, linkage.getEntity2Source());
                    stmt3.setString(ii++, linkage.getEntity2Version());
                    stmt3.setString(ii++, linkage.getClassificationMarking().toString());
                    stmt3.setString(ii++, linkage.getModifiedBy());
                    stmt3.setString(ii++, (linkage.getInputLang() != null) ? linkage.getInputLang().toString() : "");
                    stmt3.setString(ii++, dateCreated);
                    stmt3.setString(ii++, lastModified);
                    //stmt3.setString(ii++, uploadedToServer);
                    stmt3.setString(ii++, linkage.getKey());

                    LOGGER.debug(stmt3.toString());

                    stmt3.executeUpdate();
                }
            }
        }

        return true;
    }

    public boolean coalesceEntityTemplate_Register(CoalesceEntityTemplate template) throws CoalesceException
    {
        // create a blank entity to iterate through the recordsets
        CoalesceEntity entity = template.createNewEntity();

        // get the recordsets
        ArrayList<CoalesceRecordset> allRecordsets = new ArrayList<>();
        List<CoalesceSection> sections = entity.getSectionsAsList();
        for (CoalesceSection section : sections)
        {
            List<CoalesceRecordset> recordsets = section.getRecordsetsAsList();
            allRecordsets.addAll(recordsets);
        }

        // now create the recordset tables
        for (CoalesceRecordset recordset : allRecordsets)
        {
            visitCoalesceRecordset(recordset, this);
        }

        return true;
    }

    public boolean coalesceEntityTemplate_InsertOrUpdate(CoalesceEntityTemplate template) throws CoalesceException
    {
        try
        {
            // get connection, insert or update
            Connection conn = this.getConnection();

            String dateCreated = getDateString(new DateTime());
            String lastModified = getDateString(new DateTime());

            // prepare the query
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(
                    "select " + COLUMNS.getName() + " from " + schema + ".coalesceentitytemplate where " + COLUMNS.getName()
                            + "='" + template.getName() + "' and " + COLUMNS.getSource() + "='" + template.getSource()
                            + "' and " + COLUMNS.getVersion() + "='" + template.getVersion() + "'");
            if (!result.next())
            {
                // insert
                StringBuilder sql2 = new StringBuilder("insert into ").append(schema + ".coalesceentitytemplate (");
                //sql2.append(COLUMNS.getKey()).append(",");
                sql2.append("TemplateKey").append(",");
                sql2.append(COLUMNS.getName()).append(",");
                sql2.append(COLUMNS.getSource()).append(",");
                sql2.append(COLUMNS.getVersion()).append(",");
                sql2.append("TemplateXml").append(",");
                sql2.append(COLUMNS.getDateCreated()).append(",");
                sql2.append(COLUMNS.getLinkageLastModified());
                sql2.append(") values (?,?,?,?,?,?,?)");

                PreparedStatement stmt2 = conn.prepareStatement(sql2.toString());
                stmt2.setString(1, template.getKey());
                stmt2.setString(2, template.getName());
                stmt2.setString(3, template.getSource());
                stmt2.setString(4, template.getVersion());
                stmt2.setString(5, template.toXml("UTF-16"));
                stmt2.setString(6, dateCreated);
                stmt2.setString(7, lastModified);
                stmt2.executeUpdate();
            }
            else
            {
                // update
                StringBuilder sql3 = new StringBuilder("update " + schema + ".coalesceentitytemplate set ");
                sql3.append(COLUMNS.getName()).append("=?,");
                sql3.append(COLUMNS.getSource()).append("=?,");
                sql3.append(COLUMNS.getVersion()).append("=?,");
                sql3.append("TemplateXml").append("=?,");
                sql3.append(COLUMNS.getDateCreated()).append("=?,");
                sql3.append(COLUMNS.getLastModified()).append("=?");
                sql3.append(" where ");
                sql3.append(COLUMNS.getName()).append("=? and ");
                sql3.append(COLUMNS.getSource()).append("=? and ");
                sql3.append(COLUMNS.getVersion()).append("=?");

                PreparedStatement stmt3 = conn.prepareStatement(sql3.toString());
                stmt3.setString(1, template.getName());
                stmt3.setString(2, template.getSource());
                stmt3.setString(3, template.getVersion());
                stmt3.setString(4, template.toXml("UTF-16"));
                stmt3.setString(5, dateCreated);
                stmt3.setString(6, lastModified);
                stmt3.setString(7, template.getName());
                stmt3.setString(8, template.getSource());
                stmt3.setString(9, template.getVersion());
                stmt3.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            throw new CoalesceException(e);
        }

        return true;
    }

    public void deletePhantomRecords(String schema, String tableName, CoalesceRecordset recordset) throws CoalesceException
    {
        try
        {
            List<CoalesceParameter> params = new ArrayList<>();
            params.add(new CoalesceParameter(recordset.getEntity().getKey()));

            StringBuilder sql = new StringBuilder(
                    "DELETE FROM " + schema + "." + NORMALIZER.normalize(tableName) + " WHERE entitykey=?");

            if (recordset.getHasRecords())
            {
                sql.append(" AND ").append(COLUMNS.getKey()).append(" NOT IN(");

                boolean isFirst = true;

                for (CoalesceRecord record : recordset.getAllRecords())
                {
                    if (!isFirst)
                    {
                        sql.append(",");
                    }

                    sql.append("?");

                    params.add(new CoalesceParameter(record.getKey()));

                    isFirst = false;
                }

                sql.append(")");
            }

            int deleted = executeUpdate(sql.toString(), params.toArray(new CoalesceParameter[params.size()]));

            LOGGER.debug("{} Deleted", deleted);
        }
        catch (SQLException e)
        {
            throw new CoalesceException("Delete Phantom Record Failed", e);
        }
    }

    public void deleteAllRecords(String schema, String tableName, String key) throws CoalesceException
    {
        try
        {
            String sql = "DELETE FROM " + schema + "." + NORMALIZER.normalize(tableName) + " WHERE entitykey=?";

            int deleted = executeUpdate(sql, new CoalesceParameter(key));

            LOGGER.debug("{} Deleted", deleted);
        }
        catch (SQLException e)
        {
            throw new CoalesceException("Deleting All Record Failed", e);
        }
    }

    public void deleteRecord(String schema, String tableName, String key) throws CoalesceException
    {
        try
        {
            String sql =
                    "DELETE FROM " + schema + "." + NORMALIZER.normalize(tableName) + " WHERE " + COLUMNS.getKey() + "=?";

            int deleted = executeUpdate(sql, new CoalesceParameter(key));

            LOGGER.debug("{} Deleted", deleted);
        }
        catch (SQLException e)
        {
            throw new CoalesceException("Deleting Record Failed", e);
        }
    }

    public boolean insertRecord(String schema, String tableName, java.util.List<CoalesceParameter> parameters)
            throws CoalesceException
    {
        boolean success = false;
        boolean first = true;

        try
        {

            Connection conn = this.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(
                    "select " + COLUMNS.getKey() + " from " + schema + "." + NORMALIZER.normalize(tableName) + " where "
                            + COLUMNS.getKey() + "='" + parameters.get(0).getValue() + "'");

            StringBuilder sql = new StringBuilder();

            if (!result.next())
            {
                // insert
                sql.append("insert into ").append(schema).append(".").append(NORMALIZER.normalize(tableName)).append(" (");
                first = true;
                for (CoalesceParameter parameter : parameters)
                {
                    if (!first)
                    {
                        sql.append(",");
                    }
                    else
                    {
                        first = false;
                    }
                    sql.append(NORMALIZER.normalize(parameter.getName()));
                }
                sql.append(") values (");
                first = true;
                for (CoalesceParameter parameter : parameters)
                {
                    if (!first)
                    {
                        sql.append(",");
                    }
                    else
                    {
                        first = false;
                    }
                    if (parameter.getType() == Types.OTHER || parameter.getType() == Types.VARCHAR
                            || parameter.getType() == Types.CHAR)
                    {
                        sql.append(quote(parameter.getValue()));
                    }
                    else
                    {
                        sql.append(parameter.getValue());
                    }
                }
                sql.append(")");
            }
            else
            {
                // update
                sql.append("update ").append(schema).append(".").append(NORMALIZER.normalize(tableName)).append(" set ");
                for (CoalesceParameter parameter : parameters)
                {
                    if (!first)
                    {
                        sql.append(",");
                    }
                    else
                    {
                        first = false;
                    }
                    sql.append(NORMALIZER.normalize(parameter.getName())).append(" = ");

                    if (parameter.getType() == Types.OTHER || parameter.getType() == Types.VARCHAR
                            || parameter.getType() == Types.CHAR)
                    {
                        sql.append(quote(parameter.getValue()));
                    }
                    else
                    {
                        sql.append(parameter.getValue());
                    }
                }

                sql.append(" WHERE ").append(COLUMNS.getKey()).append("=").append(quote(parameters.get(0).getValue()));
            }

            //*/

            LOGGER.trace("Insert Record SQL: {}", sql);
            stmt = conn.createStatement();
            stmt.executeUpdate(sql.toString());
            success = true;
        }
        catch (SQLException e)
        {
            throw new CoalesceException("Insert Record Failed", e);
        }

        return success;
    }

    protected String quote(String value)
    {
        return "'" + value.replaceAll("'", "''") + "'";
    }

    protected boolean visitCoalesceRecordset(CoalesceRecordset recordset, CoalesceDataConnectorBase conn)
            throws CoalesceException
    {
        try
        {
            CoalesceIndexInfo info = new CoalesceIndexInfo(recordset);
            String tablename = NORMALIZER.normalize(info.getTableName());

            if (!tableAlreadyExists(tablename))
            {
                // Get Parent's information

                // Build SQL Command from Field Definitions
                StringBuilder sb = new StringBuilder();

                // Add Required Columns
                sb.append("\t" + COLUMNS.getKey() + " varchar(128) NOT NULL,\r\n");
                sb.append("\tentitykey varchar(128) NOT NULL,\r\n");
                sb.append("\t" + COLUMNS.getName() + " varchar(256),\r\n");
                sb.append("\t" + COLUMNS.getSource() + " varchar(256),\r\n");
                sb.append("\t" + COLUMNS.getType() + " varchar(256),\r\n");

                // Add Columns for Field Definitions
                for (CoalesceFieldDefinition fieldDefinition : recordset.getFieldDefinitions())
                {

                    ECoalesceFieldDataTypes dataType = fieldDefinition.getDataType();

                    if (fieldDefinition.isFlatten() && fieldDefinition.isListType())
                    {
                        // createListFieldTable(dataType, conn);
                        LOGGER.warn("List field types are not currently supported.");
                    }

                    String columnType = getSQLType(dataType);

                    if (columnType != null && fieldDefinition.isFlatten()
                            && !columnType.equalsIgnoreCase(SQLDataConnector.UNSUPPORTED_TYPE))
                    {
                        // sb.append("\t" +
                        // this.normalizeFieldName(fieldDefinition.getName()) +
                        // " "
                        // + columnType + ",\r");
                        sb.append("\t" + NORMALIZER.normalize(fieldDefinition.getName()) + " " + columnType + ",\r\n");
                    }
                    else
                    {
                        LOGGER.info("Not Registering ({})", fieldDefinition.getName());
                    }
                }

                sb.append("\tCONSTRAINT " + info.getTableName() + "_pkey PRIMARY KEY (" + COLUMNS.getKey() + "),\r\n");
                sb.append("\tCONSTRAINT " + info.getTableName() + "_fkey FOREIGN KEY (entitykey) REFERENCES " + schema
                                  + ".coalesceentity (" + COLUMNS.getKey() + ") ON DELETE CASCADE\r\n");

                if (schema == null || schema.length() == 0)
                {
                    throw new CoalesceException("Schema is null or empty... aborting.");
                }

                String sql = String.format(CREATE_TABLE_FORMAT, schema, tablename.toUpperCase(), sb.toString());

                LOGGER.debug("Register Template for " + tablename);
                LOGGER.debug(sql);

                // Create Table
                conn.executeUpdate(sql);

                // Create Stored Procedure for inserting into the table
                // createStoredProcedure(recordset, conn);
            }
        }
        catch (SQLException e)
        {
            throw new CoalesceException("Registeration Failed", e);
        }
        return false;
    }

    private boolean tableAlreadyExists(String tablename) throws SQLException
    {
        ResultSet tables = _conn.getMetaData().getTables(null, "COALESCE", "%", null);

        while (tables.next())
        {
            if (tables.getString(3).equalsIgnoreCase(tablename.replaceAll("\"", "")))
            {
                return true;
            }
        }

        return false;
    }

    public static String getSQLType(final ECoalesceFieldDataTypes type)
    {

        switch (type)
        {

        case BOOLEAN_TYPE:
            return "boolean";

        case DOUBLE_TYPE:
        case FLOAT_TYPE:
            return "double";

        case GEOCOORDINATE_LIST_TYPE:
        case GEOCOORDINATE_TYPE:
        case LINE_STRING_TYPE:
        case POLYGON_TYPE:
        case CIRCLE_TYPE:
            return "varchar(256)";

        case ENUMERATION_TYPE:
        case INTEGER_TYPE:
            return "int";

        case STRING_TYPE:
        case URI_TYPE:
        case STRING_LIST_TYPE:
        case DOUBLE_LIST_TYPE:
        case ENUMERATION_LIST_TYPE:
        case INTEGER_LIST_TYPE:
        case LONG_LIST_TYPE:
        case FLOAT_LIST_TYPE:
        case GUID_LIST_TYPE:
        case BOOLEAN_LIST_TYPE:
            return "varchar(256)";

        case GUID_TYPE:
            return "varchar(128)";

        case DATE_TIME_TYPE:
            return "timestamp";

        case LONG_TYPE:
            return "bigint";

        case FILE_TYPE:
        case BINARY_TYPE:
        default:
            return null;
        }
    }

    private static boolean tableAlreadyExists(SQLException sqlException)
    {
        boolean exists;
        if (sqlException.getSQLState().equals(DERBY_OBJECT_ALREADY_EXISTS_SQL_STATE))
        {
            exists = true;
        }
        else
        {
            exists = false;
        }
        return exists;
    }

    @Override
    public Connection getDBConnection() throws SQLException
    {
        _conn = getConn();
        return _conn;
    }

    @Override
    protected String getProcedurePrefix()
    {
        return "call " + _prefix;
    }

    public boolean tableExists(String schema, String tableName) throws SQLException
    {
        boolean exists = false;
        // get connection, insert or update
        // get database metadata
        Connection conn = this.getConnection();
        DatabaseMetaData metaData = conn.getMetaData();
        // get tables
        // ResultSet rs = metaData.getTables(null, schema, "%", new String[]
        // {"TABLE"});
        ResultSet rs = metaData.getTables(null, null, null, new String[] { "TABLE"
        });
        while (rs.next())
        {
            String retTableName = rs.getString(3);
            if (retTableName.equalsIgnoreCase(tableName.replaceAll("\"", "")))
            {
                exists = true;
                break;
            }
        }
        rs.close();

        return exists;
    }

    public static String getDateString(DateTime date)
    {
        return DATE_TIME_FOMRATTER.print(date);
    }

    public static String getDateString(Date date)
    {
        return DATE_TIME_FOMRATTER.print(new DateTime(date));
    }

    public java.util.List<String> getColumnNames(String schema, String tableName) throws SQLException
    {
        Connection conn = this.getConnection();
        // get data base metadata
        DatabaseMetaData metaData = conn.getMetaData();
        // get columns
        // ResultSet rs = metaData.getColumns(null, schema, tableName, "%");
        ResultSet rs = metaData.getColumns(null, schema.toUpperCase(), tableName.toUpperCase(), null);
        java.util.List<String> columns = new ArrayList<String>();
        while (rs.next())
        {
            // 1: none
            // 2: schema
            // 3: table name
            // 4: column name
            // 5: length
            // 6: data type (CHAR, VARCHAR, TIMESTAMP, ...)
            columns.add(rs.getString(4));
        }
        rs.close();
        return columns;
    }

    private void createTables() throws SQLException
    {

        // need to create the database and tables
        Connection conn = this.getDBConnection();
        StringBuilder sb;

        try
        {
            sb = new StringBuilder("CREATE TABLE coalesce.CoalesceEntity" + "(");
            sb.append(COLUMNS.getKey() + " VARCHAR(128) NOT NULL,");
            sb.append(COLUMNS.getName() + " VARCHAR(256),");
            sb.append(COLUMNS.getSource() + " VARCHAR(256), ");
            sb.append(COLUMNS.getVersion() + " VARCHAR(256),");
            sb.append(COLUMNS.getEntityId() + " VARCHAR(256),");
            sb.append(COLUMNS.getEntityIdType() + " VARCHAR(256),");
            sb.append(COLUMNS.getXml() + " SQLXML,");
            sb.append(COLUMNS.getDateCreated() + " timestamp,");
            sb.append(COLUMNS.getLastModified() + " timestamp,");
            sb.append(COLUMNS.getUploadedToServer() + " timestamp,");
            sb.append(COLUMNS.getTitle() + " VARCHAR(256),");
            sb.append(COLUMNS.getStatus() + " NUMERIC,");
            sb.append(COLUMNS.getScope() + " VARCHAR(256),");
            sb.append(COLUMNS.getType() + " VARCHAR(256),");
            sb.append("CONSTRAINT CoalesceEntity_pkey PRIMARY KEY (" + COLUMNS.getKey() + ")");
            sb.append(")");

            Statement stmt = conn.createStatement();
            stmt.execute(sb.toString());

            LOGGER.debug("CoalesceEntity Table created");
            stmt.close();
        }
        catch (SQLException e)
        {
            if (!SQLDataConnector.tableAlreadyExists(e))
            {
                throw e;
            }
        }

        try
        {
            sb = new StringBuilder("CREATE TABLE coalesce.CoalesceEntityTemplate ( ");
            sb.append(COLUMNS.getKey() + " VARCHAR(128) NOT NULL, ");
            sb.append(COLUMNS.getName() + " VARCHAR(128), ");
            sb.append(COLUMNS.getSource() + " VARCHAR(128), ");
            sb.append(COLUMNS.getVersion() + " VARCHAR(128), ");
            sb.append(COLUMNS.getXml() + " LONG VARCHAR, ");
            sb.append(COLUMNS.getDateCreated() + " timestamp, ");
            sb.append(COLUMNS.getLastModified() + " timestamp, ");
            sb.append(COLUMNS.getUploadedToServer() + " timestamp, ");
            sb.append("CONSTRAINT CoalesceEntityTemplate_pkey PRIMARY KEY (" + COLUMNS.getKey() + ") ");
            sb.append(")");

            Statement stmt = conn.createStatement();
            stmt.execute(sb.toString());

            LOGGER.debug("Coalesce Entity Template Table created");
            stmt.close();
        }
        catch (SQLException e)
        {
            if (!SQLDataConnector.tableAlreadyExists(e))
            {
                throw e;
            }
        }
        try
        {
            sb = new StringBuilder("CREATE TABLE coalesce.CoalesceLinkage ( ");
            sb.append(COLUMNS.getKey() + " VARCHAR(128) NOT NULL,");
            sb.append(" entitykey VARCHAR(128), ");
            sb.append(COLUMNS.getName() + " VARCHAR(256),");
            sb.append(COLUMNS.getSource() + " VARCHAR(128),");
            sb.append(COLUMNS.getVersion() + " VARCHAR(64),");
            sb.append(COLUMNS.getLinkageType() + " VARCHAR(128),");
            sb.append(COLUMNS.getLinkageLabel() + " VARCHAR(256),");
            sb.append(COLUMNS.getLinkageStatus() + " VARCHAR(128),");
            sb.append(COLUMNS.getEntity2Key() + " VARCHAR(128),");
            sb.append(COLUMNS.getEntity2Name() + " VARCHAR(128),");
            sb.append(COLUMNS.getEntity2Source() + " VARCHAR(128),");
            sb.append(COLUMNS.getEntity2Version() + " VARCHAR(64),");
            sb.append(" ClassificationMarking VARCHAR(128),");
            sb.append(" ModifiedBy VARCHAR(128),");
            sb.append(" InputLanguage VARCHAR(128),");
            sb.append(COLUMNS.getDateCreated() + " timestamp,");
            sb.append(COLUMNS.getLastModified() + " timestamp,");
            sb.append(COLUMNS.getUploadedToServer() + " timestamp, ");
            sb.append("CONSTRAINT CoalesceLinkage_pkey PRIMARY KEY (" + COLUMNS.getKey() + ")");
            sb.append(")");

            Statement stmt = conn.createStatement();
            stmt.execute(sb.toString());
            LOGGER.debug("Coalesce Linkage Table created");
            stmt.close();

        }
        catch (SQLException e)
        {
            if (!SQLDataConnector.tableAlreadyExists(e))
            {
                throw e;
            }
        }
    }

    /**
     * Logs all tables and their associated values.
     *
     * @throws SQLException
     */
    public void printTableDetails() throws SQLException
    {

        Connection conn = this.getConnection();
        // get data base metadata
        DatabaseMetaData metaData = conn.getMetaData();

        ResultSet tables = metaData.getTables(null, schema.toUpperCase(), null, null);

        while (tables.next())
        {
            LOGGER.info(tables.getString(3));

            ResultSet test = metaData.getColumns(null, schema.toUpperCase(), tables.getString(3), null);

            while (test.next())
            {
                LOGGER.info("\t{}", test.getString(4));
            }

        }
    }

}
