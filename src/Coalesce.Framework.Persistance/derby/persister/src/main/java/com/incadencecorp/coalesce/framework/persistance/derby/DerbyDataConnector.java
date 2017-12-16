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

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;
import com.incadencecorp.coalesce.framework.persistance.postgres.CoalesceIndexInfo;
import org.apache.derby.jdbc.ClientDriver;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DerbyDataConnector extends CoalesceDataConnectorBase {

    private static final DerbyNormalizer NORMALIZER = new DerbyNormalizer();
    private static final CommonColumnNames COLUMNS = new CommonColumnNames(NORMALIZER);

    private static String protocol = "jdbc";
    private static String databaseDriver = "derby";
    private static final String DIRECTORY = "directory";
    private static final String MEMORY = "memory";
    private static final String CLASSPATH = "classpath";
    private static final String JAR = "jar";
    // private static final String CLIENT_DRIVER =
    // "org.apache.derby.jdbc.ClientDriver";
    // private static final String EMBEDDED_DRIVER =
    // "org.apache.derby.jdbc.EmbeddedDriver";
    private static final Logger LOGGER = LoggerFactory.getLogger(DerbyDataConnector.class);
    private static final String UNSUPPORTED_TYPE = "UNSUPPORTED_TYPE";
    private static final String CREATE_TABLE_FORMAT = "CREATE TABLE %s.%s(\r%s\r)";

    private String _prefix = "";
    private boolean isEmbedded;
    private String subSubProtocol;
    private String schema = "";

    public final static String DERBY_OBJECT_ALREADY_EXISTS_SQL_STATE = "X0Y32";

    /**
     * Note: Derby stored procedures cannot handle long XML types. So, we will
     * NOT use stored procedures in the derby persistor for the base tables.
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
                                                 DateTime ivarlastmodified) throws SQLException
    {

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        String dateCreated = fmt.print(ivardatecreated);
        String lastModified = fmt.print(ivarlastmodified);

        // get connection, insert or update
        Connection conn = this.getConnection();

        // prepare the query
        Statement stmt = conn.createStatement();
        ResultSet result = null;
        result = stmt.executeQuery(
                "select " + COLUMNS.getName() + " from " + schema + ".coalesceentity where " + COLUMNS.getKey() + "='"
                        + ivarobjectkey + "'");
        if (!result.next())
        {
            // insert
            Statement stmt2 = conn.createStatement();
            StringBuilder sql = new StringBuilder("insert into " + schema + ".coalesceentity (");
            sql.append(COLUMNS.getKey() + ", ");
            sql.append(COLUMNS.getName() + ", ");
            sql.append(COLUMNS.getSource() + ", ");
            sql.append(COLUMNS.getVersion() + ", ");
            sql.append(COLUMNS.getEntityId() + ",");
            sql.append(COLUMNS.getEntityIdType() + ", ");
            sql.append(COLUMNS.getXml() + ", ");
            sql.append(COLUMNS.getDateCreated() + ", ");
            sql.append(COLUMNS.getLastModified());
            sql.append(") values ('");
            sql.append(ivarobjectkey).append("','");
            sql.append(ivarname).append("','");
            sql.append(ivarsource).append("','");
            sql.append(ivarversion).append("','");
            sql.append(ivarentityid).append("','");
            sql.append(ivarentityidtype).append("','");
            sql.append(ivarentityxml).append("','");
            sql.append(dateCreated).append("','");
            sql.append(lastModified);
            sql.append("')");

            stmt2.executeUpdate(sql.toString());
        }
        else
        {
            String format = "%s='%s', ";
            // update
            Statement stmt3 = conn.createStatement();
            StringBuilder sql = new StringBuilder("update " + schema + ".coalesceentity set ");
            sql.append(String.format(format, COLUMNS.getName(), ivarname));
            sql.append(String.format(format, COLUMNS.getSource(), ivarsource));
            sql.append(String.format(format, COLUMNS.getVersion(), ivarversion));
            sql.append(String.format(format, COLUMNS.getEntityId(), ivarentityid));
            sql.append(String.format(format, COLUMNS.getEntityIdType(), ivarentityidtype));
            sql.append(String.format(format, COLUMNS.getXml(), ivarentityxml));
            sql.append(String.format(format, COLUMNS.getDateCreated(), dateCreated));
            sql.append(COLUMNS.getLastModified() + "='" + lastModified + "'");
            sql.append(" where " + COLUMNS.getKey() + "='" + ivarobjectkey + "'");

            LOGGER.debug(sql.toString());

            stmt3.executeUpdate(sql.toString());
        }
        return true;
    }

    public boolean coalesceLinkage_InsertOrUpdate(CoalesceLinkage linkage) throws SQLException
    {
        // get connection, insert or update
        Connection conn = this.getConnection();

        // prepare the query
        ResultSet result = null;

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        String dateCreated = fmt.print(linkage.getDateCreated());
        String lastModified = fmt.print(linkage.getLastModified());

        Statement stmt = conn.createStatement();
        result = stmt.executeQuery("select name from coalesce.coalescelinkage where objectkey='" + linkage.getKey() + "'");
        if (!result.next())
        {
            // insert
            StringBuilder sql2 = new StringBuilder("insert into " + schema + ".coalescelinkage (");
            sql2.append(COLUMNS.getKey() + ",");
            sql2.append("entitykey,");
            sql2.append(COLUMNS.getName() + ",");
            sql2.append(COLUMNS.getSource() + ",");
            sql2.append(COLUMNS.getVersion() + ",");
            sql2.append(COLUMNS.getLinkageType() + ",");
            sql2.append(COLUMNS.getLinkageLabel() + ",");
            sql2.append(COLUMNS.getLinkageStatus() + ",");
            sql2.append(COLUMNS.getEntity2Key() + ",");
            sql2.append(COLUMNS.getEntity2Name() + ",");
            sql2.append(COLUMNS.getEntity2Source() + ",");
            sql2.append(COLUMNS.getEntity2Version() + ",");
            sql2.append("ClassificationMarking,");
            sql2.append("ModifiedBy,");
            sql2.append("InputLanguage,");
            sql2.append(COLUMNS.getDateCreated() + ",");
            sql2.append(COLUMNS.getLastModified());
            sql2.append(") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            int ii=1;

            PreparedStatement stmt2 = conn.prepareStatement(sql2.toString());
            stmt2.setString(ii++, linkage.getKey());
            stmt2.setString(ii++, linkage.getEntity1Key());
            stmt2.setString(ii++, linkage.getEntity1Name());
            stmt2.setString(ii++, linkage.getEntity1Source());
            stmt2.setString(ii++, linkage.getEntity1Version());
            stmt2.setString(ii++, linkage.getLinkType().getLabel());
            stmt2.setString(ii++, linkage.getLabel());
            stmt2.setString(ii++, linkage.getStatus().toString());
            stmt2.setString(ii++, linkage.getEntity2Key());
            stmt2.setString(ii++, linkage.getEntity2Name());
            stmt2.setString(ii++, linkage.getEntity2Source());
            stmt2.setString(ii++, linkage.getEntity2Version());
            stmt2.setString(ii++, linkage.getClassificationMarking().toString());
            stmt2.setString(ii++, linkage.getModifiedBy());
            stmt2.setString(ii++, linkage.getInputLang().toString());
            stmt2.setString(ii++, dateCreated);
            stmt2.setString(ii++, lastModified);

            LOGGER.debug(stmt2.toString());

            stmt2.executeUpdate();
        }
        else
        {
            // update
            StringBuilder sql3 = new StringBuilder("update " + schema + ".coalescelinkage set ");
            sql3.append("entitykey=?,");
            sql3.append(COLUMNS.getName() + "=?,");
            sql3.append(COLUMNS.getSource() + "=?,");
            sql3.append(COLUMNS.getVersion() + "=?,");
            sql3.append(COLUMNS.getLinkageType() + "=?,");
            sql3.append(COLUMNS.getLinkageLabel() + "=?,");
            sql3.append(COLUMNS.getLinkageStatus() + "=?,");
            sql3.append(COLUMNS.getEntity2Key() + "=?,");
            sql3.append(COLUMNS.getEntity2Name() + "=?,");
            sql3.append(COLUMNS.getEntity2Source() + "=?,");
            sql3.append(COLUMNS.getEntity2Version() + "=?,");
            sql3.append("ClassificationMarking=?,");
            sql3.append("ModifiedBy=?,");
            sql3.append("InputLanguage=?,");
            sql3.append("ParentKey=?,");
            sql3.append("ParentType=?,");
            sql3.append(COLUMNS.getDateCreated() + "=?,");
            sql3.append(COLUMNS.getLastModified() + "=? ");
            sql3.append("where " + COLUMNS.getKey() + "=?");

            int ii=1;

            PreparedStatement stmt3 = conn.prepareStatement(sql3.toString());
            stmt3.setString(ii++, linkage.getEntity1Key());
            stmt3.setString(ii++, linkage.getEntity1Name());
            stmt3.setString(ii++, linkage.getEntity1Source());
            stmt3.setString(ii++, linkage.getEntity1Version());
            stmt3.setString(ii++, linkage.getLinkType().getLabel());
            stmt3.setString(ii++, linkage.getLabel());
            stmt3.setString(ii++, linkage.getStatus().toString());
            stmt3.setString(ii++, linkage.getEntity2Key());
            stmt3.setString(ii++, linkage.getEntity2Name());
            stmt3.setString(ii++, linkage.getEntity2Source());
            stmt3.setString(ii++, linkage.getEntity2Version());
            stmt3.setString(ii++, linkage.getClassificationMarking().toString());
            stmt3.setString(ii++, linkage.getModifiedBy());
            stmt3.setString(ii++, linkage.getInputLang().toString());
            stmt3.setString(ii++, linkage.getParent().getKey());
            stmt3.setString(ii++, linkage.getParent().getType());
            stmt3.setString(ii++, dateCreated);
            stmt3.setString(ii++, lastModified);
            stmt3.setString(ii++, linkage.getKey());

            LOGGER.debug(stmt3.toString());

            stmt3.executeUpdate();
        }

        return true;
    }

    public boolean coalesceEntityTemplate_InsertOrUpdate(CoalesceEntityTemplate template) throws CoalesceException
    {
        try
        {
            // get connection, insert or update
            Connection conn = this.getConnection();

            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            String dateCreated = fmt.print(new DateTime());
            String lastModified = fmt.print(new DateTime());

            // prepare the query
            ResultSet result = null;

            Statement stmt = conn.createStatement();
            result = stmt.executeQuery(
                    "select " + COLUMNS.getName() + " from " + schema + ".coalesceentitytemplate where " + COLUMNS.getName() + "='"
                            + template.getName() + "' and " + COLUMNS.getSource() + "='" + template.getSource() + "' and "
                            + COLUMNS.getVersion() + "='" + template.getVersion() + "'");
            if (!result.next())
            {
                // insert
                StringBuilder sql2 = new StringBuilder("insert into " + schema + ".coalesceentitytemplate (");
                sql2.append(COLUMNS.getKey() + ",");
                sql2.append(COLUMNS.getName() + ",");
                sql2.append(COLUMNS.getSource() + ",");
                sql2.append(COLUMNS.getVersion() + ",");
                sql2.append(COLUMNS.getXml() + ",");
                sql2.append(COLUMNS.getDateCreated() + ",");
                sql2.append(COLUMNS.getLinkageLastModified());
                sql2.append(") values (?,?,?,?,?,?,?)");

                PreparedStatement stmt2 = conn.prepareStatement(sql2.toString());
                stmt2.setString(1, template.getKey());
                stmt2.setString(2, template.getName());
                stmt2.setString(3, template.getSource());
                stmt2.setString(4, template.getVersion());
                stmt2.setString(5, template.toXml());
                stmt2.setString(6, dateCreated);
                stmt2.setString(7, lastModified);
                stmt2.executeUpdate();
            }
            else
            {
                // update
                StringBuilder sql3 = new StringBuilder("update " + schema + ".coalesceentitytemplate set ");
                sql3.append(COLUMNS.getName() + "=?,");
                sql3.append(COLUMNS.getSource() + "=?,");
                sql3.append(COLUMNS.getVersion() + "=?,");
                sql3.append(COLUMNS.getXml() + "=?,");
                sql3.append(COLUMNS.getDateCreated() + "=?,");
                sql3.append(COLUMNS.getLastModified() + "=?");
                sql3.append(" where ");
                sql3.append(COLUMNS.getName() + "=? and ");
                sql3.append(COLUMNS.getSource() + "=? and ");
                sql3.append(COLUMNS.getVersion() + "=?");

                PreparedStatement stmt3 = conn.prepareStatement(sql3.toString());
                stmt3.setString(1, template.getName());
                stmt3.setString(2, template.getSource());
                stmt3.setString(3, template.getVersion());
                stmt3.setString(4, template.toXml());
                stmt3.setString(5, dateCreated);
                stmt3.setString(6, lastModified);
                stmt3.setString(7, template.getName());
                stmt3.setString(8, template.getSource());
                stmt3.setString(9, template.getVersion());
                stmt3.executeUpdate();
            }

            // create a blank entity to iterate through the recordsets
            CoalesceEntity entity = template.createNewEntity();

            // get the recordsets
            ArrayList<CoalesceRecordset> allRecordsets = new ArrayList<CoalesceRecordset>();
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
        }
        catch (SQLException e)
        {
            throw new CoalesceException(e);
        }

        return true;
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
            ResultSet result = null;
            result = stmt.executeQuery(
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

                sql.append(" WHERE " + COLUMNS.getKey() + "=").append(quote(parameters.get(0).getValue()));
            }

            //*/

            LOGGER.trace("Insert Record SQL: " + sql);
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
        return "'" + value + "'";
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
                            && !columnType.equalsIgnoreCase(DerbyDataConnector.UNSUPPORTED_TYPE))
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

                if (schema == null || (schema != null && schema.length() == 0))
                {
                    throw new CoalesceException("Schema is null or empty... aborting.");
                }

                String sql = String.format(CREATE_TABLE_FORMAT,
                                           schema,
                                           tablename,
                                           sb.toString(),
                                           schema,
                                           tablename,
                                           JodaDateTimeHelper.toXmlDateTimeUTC(JodaDateTimeHelper.nowInUtc()));

                LOGGER.debug("Register Template for " + info.getTableName());
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
            return DerbyDataConnector.UNSUPPORTED_TYPE;

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

    /**
     * This constructor is for the Derby Embedded Option
     *
     * @param databaseName
     * @param schema
     * @param subSubProtocol one of "directory", "memory", "classpath", "jar"
     * @throws CoalescePersistorException
     */
    public DerbyDataConnector(String databaseName, String schema, String subSubProtocol) throws CoalescePersistorException
    {
        try
        {
            ServerConn serverConnection = new ServerConn();
            serverConnection.setDatabase(databaseName);
            setSettings(serverConnection);
            this.schema = schema;
            isEmbedded = true;
            setSubSubProtocol(subSubProtocol);

            Driver driver = new EmbeddedDriver();
            DriverManager.registerDriver(driver);

            // Class.forName(EMBEDDED_DRIVER);

            // note: if the protocol is memory, the coalesce database needs to
            // be created
            if (subSubProtocol.equals(MEMORY))
            {
                this.createTables();
            }
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("CoalesceDataConnector", e);
        }
    }

    public DerbyDataConnector(ServerConn settings, String schema, String subSubProtocol) throws CoalescePersistorException
    {
        try
        {
            setSettings(settings);
            this.schema = schema;
            isEmbedded = false;
            setSubSubProtocol(subSubProtocol);

            Driver driver = new ClientDriver();
            DriverManager.registerDriver(driver);
            // Class.forName(CLIENT_DRIVER);

            // note: if the protocol is memory, the coalesce database needs to
            // be created
            if (subSubProtocol.equals(MEMORY))
            {
                // need to create the tables
                this.createTables();
            }
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("CoalesceDataConnector", e);
        }

    }

    private void setSubSubProtocol(String subSubProtocol) throws CoalescePersistorException
    {
        switch (subSubProtocol)
        {
        case DIRECTORY:
        case MEMORY:
        case CLASSPATH:
        case JAR:
        {
            this.subSubProtocol = subSubProtocol;
            break;
        }
        default:
            throw new CoalescePersistorException("ERROR: Unknown SubSubProtocol!");
        }
    }

    @Override
    public Connection getDBConnection() throws SQLException
    {
        if (_conn == null || (_conn != null && _conn.isClosed()))
        {
            StringBuilder urlBuilder = new StringBuilder(protocol).append(":").append(databaseDriver).append(":");
            urlBuilder.append(subSubProtocol).append(":");

            if (isEmbedded)
            {
                urlBuilder.append(getSettings().getDatabase());
            }
            else
            {
                urlBuilder.append("//").append(getSettings().getServerNameWithPort()).append("/").append(getSettings().getDatabase());
            }

            // if a memory connection, assume you have to create the database
            if (subSubProtocol.equals(MEMORY))
            {
                urlBuilder.append(";create=true");
            }

            _conn = DriverManager.getConnection(urlBuilder.toString(), getSettings().getProperties());
        }
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
            LOGGER.trace("Table name is: " + retTableName);
            if (retTableName.equalsIgnoreCase(tableName.replaceAll("\"", "")))
            {
                exists = true;
                break;
            }
        }
        rs.close();

        return exists;
    }

    java.util.List<String> getColumnNames(String schema, String tableName) throws SQLException
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
            sb.append(COLUMNS.getXml() + " LONG VARCHAR,");
            sb.append(COLUMNS.getDateCreated() + " timestamp,");
            sb.append(COLUMNS.getLastModified() + " timestamp,");
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
            if (!DerbyDataConnector.tableAlreadyExists(e))
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
            sb.append("CONSTRAINT CoalesceEntityTemplate_pkey PRIMARY KEY (" + COLUMNS.getKey() + ") ");
            sb.append(")");

            Statement stmt = conn.createStatement();
            stmt.execute(sb.toString());

            LOGGER.debug("Coalesce Entity Template Table created");
            stmt.close();
        }
        catch (SQLException e)
        {
            if (!DerbyDataConnector.tableAlreadyExists(e))
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
            sb.append("CONSTRAINT CoalesceLinkage_pkey PRIMARY KEY (" + COLUMNS.getKey() + ")");
            sb.append(")");

            Statement stmt = conn.createStatement();
            stmt.execute(sb.toString());
            LOGGER.debug("Coalesce Linkage Table created");
            stmt.close();

        }
        catch (SQLException e)
        {
            if (!DerbyDataConnector.tableAlreadyExists(e))
            {
                throw e;
            }
        }
    }

}
