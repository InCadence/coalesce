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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.derby.jdbc.ClientDriver;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;
import com.incadencecorp.coalesce.framework.persistance.postgres.CoalesceIndexInfo;

public class DerbyDataConnector extends CoalesceDataConnectorBase {

    private static String protocol = "jdbc";
    private static String databaseDriver = "derby";
    private static final String DIRECTORY = "directory";
    private static final String MEMORY = "memory";
    private static final String CLASSPATH = "classpath";
    private static final String JAR = "jar";
//    private static final String CLIENT_DRIVER = "org.apache.derby.jdbc.ClientDriver";
//    private static final String EMBEDDED_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
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
     * 
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
                                                 DateTime ivarlastmodified)
            throws SQLException
    {

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        String dateCreated = fmt.print(ivardatecreated);
        String lastModified = fmt.print(ivarlastmodified);

        // get connection, insert or update
        Connection conn = this.getConnection();

        // prepare the query
        Statement stmt = conn.createStatement();
        ResultSet result = null;
        result = stmt.executeQuery("select name from coalesce.coalesceentity where objectkey='" + ivarobjectkey + "'");
        if (!result.next())
        {
            // insert
            Statement stmt2 = conn.createStatement();
            StringBuilder sql = new StringBuilder("insert into coalesce.coalesceentity (objectkey, name, source, version, entityid,").append("entityidtype, entityxml, datecreated, lastmodified) values ('").append(ivarobjectkey).append("','").append(ivarname).append("','").append(ivarsource).append("','").append(ivarversion).append("','").append(ivarentityid).append("','").append(ivarentityidtype).append("','").append(ivarentityxml).append("','").append(dateCreated).append("','").append(lastModified).append("')");
            stmt2.executeUpdate(sql.toString());
        }
        else
        {
            // update
            Statement stmt3 = conn.createStatement();
            StringBuilder sql = new StringBuilder("update coalesce.coalesceentity set name = '").append(ivarname).append("', source = '").append(ivarsource).append("', version = '").append(ivarversion).append("', entityid = '").append(ivarentityid).append("',").append("entityidtype = '").append(ivarentityidtype).append("', entityxml='").append(ivarentityxml).append("', datecreated = '").append(dateCreated).append("', lastmodified='").append(lastModified).append("' where objectkey='").append(ivarobjectkey).append("'");

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

        Statement stmt = conn.createStatement();
        result = stmt.executeQuery("select name from coalesce.coalescelinkage where objectkey='" + linkage.getKey() + "'");
        if (!result.next())
        {
            // insert
            StringBuilder sql2 = new StringBuilder("insert into coalesce.coalescelinkage (ObjectKey,").append("Name,Entity1Key,Entity1Name,Entity1Source,Entity1Version,LinkType,").append("LinkLabel,LinkStatus,Entity2Key,Entity2Name,Entity2Source,Entity2Version,").append("ClassificationMarking,ModifiedBy,InputLanguage,ParentKey,ParentType,DateCreated,").append("LastModified) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement stmt2 = conn.prepareStatement(sql2.toString());
            stmt2.setString(1, linkage.getKey());
            stmt2.setString(2, linkage.getName());
            stmt2.setString(3, linkage.getEntity1Key());
            stmt2.setString(4, linkage.getEntity1Name());
            stmt2.setString(5, linkage.getEntity1Source());
            stmt2.setString(6, linkage.getEntity1Version());
            stmt2.setString(7, linkage.getLinkType().getLabel());
            stmt2.setString(8, linkage.getLabel());
            stmt2.setString(9, linkage.getStatus().toString());
            stmt2.setString(10, linkage.getEntity2Key());
            stmt2.setString(11, linkage.getEntity2Name());
            stmt2.setString(12, linkage.getEntity2Source());
            stmt2.setString(13, linkage.getEntity2Version());
            stmt2.setString(14, linkage.getClassificationMarking().toString());
            stmt2.setString(15, linkage.getModifiedBy());
            stmt2.setString(16, linkage.getParent().getKey());
            stmt2.setString(17, linkage.getParent().getType());
            stmt2.setString(18, linkage.getDateCreated().toString());
            stmt2.setString(19, linkage.getLastModified().toString());
            stmt2.executeUpdate();
        }
        else
        {
            // update
            StringBuilder sql3 = new StringBuilder("update coalesce.coalescelinkage set Name=?,").append("Entity1Key=?,Entity1Name=?,Entity1Source=?,Entity1Version=?,LinkType=?,").append("LinkLabel=?,LinkStatus=?,Entity2Key=?,Entity2Name=?,Entity2Source=?,Entity2Version=?,").append("ClassificationMarking=?,ModifiedBy=?,InputLanguage=?,ParentKey=?,ParentType=?,DateCreated=?,").append("LastModified=? where ObjectKey=?");

            PreparedStatement stmt3 = conn.prepareStatement(sql3.toString());
            stmt3.setString(1, linkage.getName());
            stmt3.setString(2, linkage.getEntity1Key());
            stmt3.setString(3, linkage.getEntity1Name());
            stmt3.setString(4, linkage.getEntity1Source());
            stmt3.setString(5, linkage.getEntity1Version());
            stmt3.setString(6, linkage.getLinkType().getLabel());
            stmt3.setString(7, linkage.getLabel());
            stmt3.setString(8, linkage.getStatus().toString());
            stmt3.setString(9, linkage.getEntity2Key());
            stmt3.setString(10, linkage.getEntity2Name());
            stmt3.setString(11, linkage.getEntity2Source());
            stmt3.setString(12, linkage.getEntity2Version());
            stmt3.setString(13, linkage.getClassificationMarking().toString());
            stmt3.setString(14, linkage.getModifiedBy());
            stmt3.setString(15, linkage.getParent().getKey());
            stmt3.setString(16, linkage.getParent().getType());
            stmt3.setString(17, linkage.getDateCreated().toString());
            stmt3.setString(18, linkage.getLastModified().toString());
            stmt3.setString(19, linkage.getKey());
            stmt3.executeUpdate();
        }

        return true;
    }

    public boolean coalesceEntityTemplate_InsertOrUpdate(CoalesceEntityTemplate template)
            throws SQLException, CoalesceException
    {
        // get connection, insert or update
        Connection conn = this.getConnection();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        String dateCreated = fmt.print(new DateTime());
        String lastModified = fmt.print(new DateTime());

        // prepare the query
        ResultSet result = null;

        Statement stmt = conn.createStatement();
        result = stmt.executeQuery("select name from coalesce.coalesceentitytemplate where name='" + template.getName()
                + "' and source='" + template.getSource() + "' and version='" + template.getVersion() + "'");
        if (!result.next())
        {
            // insert
            StringBuilder sql2 = new StringBuilder("insert into coalesce.coalesceentitytemplate (TemplateKey,").append("Name,Source, Version,TemplateXml,DateCreated,LastModified) values ").append("(?,?,?,?,?,?,?)");

            PreparedStatement stmt2 = conn.prepareStatement(sql2.toString());
            stmt2.setString(1, UUID.randomUUID().toString());
            stmt2.setString(2, template.getName());
            stmt2.setString(3, template.getSource());
            stmt2.setString(4, template.getVersion());
            stmt2.setString(5, template.toXml());
            stmt2.setString(6, dateCreated);
            stmt2.setString(7, lastModified);
            stmt2.executeUpdate();

            // create a blank entity to iterate through the recordsets
            CoalesceEntity entity = template.createNewEntity();

            // get the recordsets
            ArrayList<CoalesceRecordset> recordSetList = new ArrayList<CoalesceRecordset>();
            java.util.List<CoalesceSection> sections = entity.getSectionsAsList();
            for (CoalesceSection section : sections)
            {
                java.util.List<CoalesceRecordset> records = section.getRecordsetsAsList();
                recordSetList.addAll(records);
            }

            // now create the recordset tables
            for (CoalesceRecordset recordset : recordSetList)
            {
                visitCoalesceRecordset(recordset, this);
            }

        }
        else
        {
            // update
            StringBuilder sql3 = new StringBuilder("update coalesce.coalesceentitytemplate set Name=?,").append("Source=?,Version=?,TemplateXml=?,DateCreated=?,LastModified=?").append(" where Name=? and Source=? and Version=?");

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

            // drop and recreate the recordset tables

        }

        return true;
    }

    public boolean insertRecord(String schema, String tableName, java.util.List<CoalesceParameter> parameters)
            throws CoalesceException
    {
        boolean success = false;

        StringBuilder sql = new StringBuilder("insert into ").append(schema).append(".").append(tableName).append(" (");
        boolean first = true;
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
            sql.append(parameter.getName());
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
            if (parameter.getType() == ECoalesceFieldDataTypes.STRING_TYPE.ordinal()
                    || parameter.getType() == ECoalesceFieldDataTypes.GUID_TYPE.ordinal())
            {
                sql.append(quote(parameter.getValue()));
            }
            else
            {
                sql.append(parameter.getValue());
            }
        }
        sql.append(")");

        try
        {
            LOGGER.trace("Insert Record SQL: " + sql);
            Connection conn = this.getConnection();
            Statement stmt = conn.createStatement();
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

            // Get Parent's information
            CoalesceIndexInfo info = new CoalesceIndexInfo(recordset);

            // Build SQL Command from Field Definitions
            StringBuilder sb = new StringBuilder();

            // Add Required Columns
            sb.append("\tobjectkey varchar(128) NOT NULL,\r");
            sb.append("\tentitykey varchar(128) NOT NULL,\r");
            sb.append("\tentityname varchar(256),\r");
            sb.append("\tentitysource varchar(256),\r");
            sb.append("\tentitytype varchar(256),\r");

            // Add Columns for Field Definitions
            for (CoalesceFieldDefinition fieldDefinition : recordset.getFieldDefinitions())
            {

                ECoalesceFieldDataTypes dataType = fieldDefinition.getDataType();

                if (fieldDefinition.getFlatten() && fieldDefinition.isListType())
                {
                    // createListFieldTable(dataType, conn);
                    LOGGER.warn("List field types are not currently supported.");
                }

                String columnType = getSQLType(dataType);

                if (columnType != null && fieldDefinition.getFlatten()
                        && !columnType.equalsIgnoreCase(DerbyDataConnector.UNSUPPORTED_TYPE))
                {
                    // sb.append("\t" +
                    // this.normalizeFieldName(fieldDefinition.getName()) + " "
                    // + columnType + ",\r");
                    sb.append("\t" + fieldDefinition.getName() + " " + columnType + ",\r");
                }
                else
                {
                    LOGGER.info("Not Registering ({})", fieldDefinition.getName());
                }
            }

            sb.append("\tCONSTRAINT " + info.getTableName() + "_pkey PRIMARY KEY (objectkey),\r");
            sb.append("\tCONSTRAINT " + info.getTableName() + "_fkey FOREIGN KEY (entitykey) REFERENCES " + schema
                    + ".coalesceentity (objectkey) ON DELETE CASCADE");

            if (schema == null || (schema != null && schema.length() == 0))
            {
                throw new CoalesceException("Schema is null or empty... aborting.");
            }

            String sql = String.format(CREATE_TABLE_FORMAT,
                                       schema,
                                       info.getTableName(),
                                       sb.toString(),
                                       schema,
                                       info.getTableName(),
                                       JodaDateTimeHelper.toXmlDateTimeUTC(JodaDateTimeHelper.nowInUtc()));

            LOGGER.debug("Register Template for " + info.getTableName());
            LOGGER.debug(sql);

            // Create Table
            conn.executeUpdate(sql);

            // Create Stored Procedure for inserting into the table
            // createStoredProcedure(recordset, conn);

        }
        catch (SQLException e)
        {
            throw new CoalesceException("Registeration Failed", e);
        }
        return false;
    }

    public static String normalizeFieldName(String fieldName)
    {
        switch (fieldName) {
        case "boolean":
        case "double":
        case "float":
        case "geocoordinatelist":
        case "geocoordinate":
        case "linestring":
        case "polygon":
        case "circle":
        case "enum":
        case "integer":
        case "string":
        case "uri":
        case "stringlist":
        case "doublelist":
        case "enumlist":
        case "integerlist":
        case "longlist":
        case "floatlist":
        case "guidlist":
        case "booleanlist":
        case "guid":
        case "datetime":
        case "long":
        case "file":
        case "binary":
        case "int":
        case "date":
            return fieldName + "Field";
        }

        return fieldName;
    }

    public static String getSQLType(final ECoalesceFieldDataTypes type)
    {

        switch (type) {

        case BOOLEAN_TYPE:
            return "smallint";

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

    public static ServerConn getServerConnection()
    {
        ServerConn serCon = new ServerConn();
        // InCadence Settings
        serCon.setServerName("127.0.0.1");
        serCon.setDatabase("CoalesceDatabase");
        serCon.setUser("CoalesceUser");
        serCon.setPassword("Passw0rd");
        return serCon;
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
     * @param prefix
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
        switch (subSubProtocol) {
        case DIRECTORY:
        case MEMORY:
        case CLASSPATH:
        case JAR: {
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
        ResultSet rs = metaData.getTables(null, null, null, new String[] {
                                                                           "TABLE"
        });
        while (rs.next())
        {
            String retTableName = rs.getString(3);
            LOGGER.trace("Table name is: " + retTableName);
            if (retTableName.equalsIgnoreCase(tableName))
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
        try
        {
            Statement stmt = null;
            stmt = conn.createStatement();
            stmt.execute("CREATE TABLE coalesce.CoalesceEntity" + "( ObjectKey VARCHAR(128) NOT NULL,"
                    + " Name VARCHAR(256)," + " Source VARCHAR(256), " + " Version VARCHAR(256)," + " EntityId VARCHAR(256),"
                    + " EntityIdType VARCHAR(256)," + " EntityXml LONG VARCHAR," + " DateCreated timestamp,"
                    + " LastModified timestamp," + " title VARCHAR(256)," + " deleted boolean DEFAULT false,"
                    + " securitylow bigint DEFAULT 0," + " securityhigh bigint DEFAULT 0," + " scope VARCHAR(256),"
                    + " creator VARCHAR(256)," + " type VARCHAR(256),"
                    + " CONSTRAINT CoalesceEntity_pkey PRIMARY KEY (ObjectKey)" + ")");

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
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE coalesce.CoalesceEntityTemplate " + "( " + " TemplateKey VARCHAR(128) NOT NULL, "
                    + " Name VARCHAR(128), " + " Source VARCHAR(128), " + " Version VARCHAR(128), "
                    + " TemplateXml LONG VARCHAR, " + " DateCreated timestamp, " + " LastModified timestamp, "
                    + "  CONSTRAINT CoalesceEntityTemplate_pkey PRIMARY KEY (TemplateKey) " + ")");
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
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE coalesce.CoalesceLinkage " + "( " + " ObjectKey VARCHAR(128) NOT NULL,"
                    + " Name VARCHAR(256)," + " Entity1Key VARCHAR(128), " + " Entity1Name VARCHAR(256),"
                    + " Entity1Source VARCHAR(128)," + " Entity1Version VARCHAR(64)," + " LinkType VARCHAR(128),"
                    + " LinkLabel VARCHAR(256)," + " LinkStatus VARCHAR(128)," + " Entity2Key VARCHAR(128),"
                    + " Entity2Name VARCHAR(128)," + " Entity2Source VARCHAR(128)," + " Entity2Version VARCHAR(64),"
                    + " ClassificationMarking VARCHAR(128)," + " ModifiedBy VARCHAR(128)," + " InputLanguage VARCHAR(128),"
                    + " ParentKey VARCHAR(128)," + " ParentType VARCHAR(64)," + " DateCreated timestamp,"
                    + " LastModified timestamp," + " CONSTRAINT CoalesceLinkage_pkey PRIMARY KEY (ObjectKey)" + ")");
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
