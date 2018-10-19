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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.iterators.CoalesceIterator;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;

/**
 * This iterator implementation generates tables within PostGres for the
 * specified template's record sets making them searchable.
 * 
 * @author n78554
 *
 */
public class PostGreSQLRegisterIterator extends CoalesceIterator<CoalesceDataConnectorBase> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostGreSQLRegisterIterator.class);

    private static final String SQL_GET_COLUMN_NAMES = "SELECT column_name, data_type FROM information_schema.columns WHERE "
            + "table_name = ? AND table_schema=? ORDER BY ordinal_position";

    private static final String CREATE_PROCEDURE_FORMAT = "CREATE OR REPLACE FUNCTION %1$s_insertorupdate(%2$s) RETURNS void AS"
            + "\r$BODY$\rBEGIN\r" + "%3$s" + "\rIF NOT FOUND THEN\r" + "%4$s" + "\rEND IF;\r%5$s\rRETURN;\rEND;\r$BODY$"
            + "\rLANGUAGE plpgsql VOLATILE\rCOST 100;"
            + "COMMENT ON FUNCTION %1$s_insertorupdate(%2$s) IS 'Generated by DSS (%6$s)';"
            + "REVOKE ALL ON FUNCTION %1$s_insertorupdate(%2$s) FROM PUBLIC";

    private static final String UPDATE_FORMAT = "\tUPDATE %s\r\tSET\r%s\r\tWHERE\r\t\tobjectkey=ivarobjectkey;";

    private static final String INSERT_FORMAT = "\tINSERT INTO %s (\r%s)\r\tVALUES (\r%s);";

    private static final String CREATE_TABLE_FORMAT = "CREATE TABLE IF NOT EXISTS %s.%s(\r%s\r); COMMENT ON TABLE %s.%s IS 'Generated by DSS(%s)';";

    private static final String CREATE_BITMASK_COLUMNS = "ALTER TABLE %s.coalesceentity ADD COLUMN %s BIT(%s)";

    private static final String DROP_BITMASK_COLUMNS = "ALTER TABLE %s.coalesceentity DROP COLUMN IF EXISTS %s";

    private static final String CREATE_BITMASK_PROCEDURE = "CREATE OR REPLACE FUNCTION %s.coalesceentityext_insertorupdate(ivarobjectkey uuid, ivarname text, ivarsource text, ivarversion text, ivarentityid text, ivarentityidtype text, ivarentityxml text, ivardatecreated timestamp with time zone, ivarlastmodified timestamp with time zone, ivartitle text, ivardeleted boolean, ivarscope text, ivarcreator text, ivartype text %s) RETURNS void AS"
            + "\r$BODY$\rBEGIN\r"
            + "\tUPDATE %s.CoalesceEntity\r"
            + "\tSET\r"
            + "\t\tName = ivarname,\r"
            + "\t\tSource = ivarsource,\r"
            + "\t\tVersion = ivarversion,\r"
            + "\t\tEntityId = ivarentityid,\r"
            + "\t\tEntityIdType = ivarentityidtype,\r"
            + "\t\tEntityXml = ivarentityxml,\r"
            + "\t\tDateCreated = ivardatecreated,\r"
            + "\t\tLastModified = ivarlastmodified,\r"
            + "\t\ttitle = ivartitle,\r"
            + "\t\tdeleted = ivardeleted,\r"
            + "\t\tscope = ivarscope,\r"
            + "\t\tcreator = ivarcreator,\r"
            + "\t\ttype = ivartype%s\r"
            + "\tWHERE\r"
            + "\t\tObjectKey = ivarobjectkey;\r"
            + "\rIF NOT FOUND THEN\r"
            + "\tINSERT INTO %s.CoalesceEntity\r"
            + "\t\t(ObjectKey,\r"
            + "\t\tName,\r"
            + "\t\tSource,\r"
            + "\t\tVersion,\r"
            + "\t\tEntityId,\r"
            + "\t\tEntityIdType,\r"
            + "\t\tEntityXml,\r"
            + "\t\tDateCreated,\r"
            + "\t\tLastModified,\r"
            + "\t\ttitle,\r"
            + "\t\tdeleted,\r"
            + "\t\tscope,\r"
            + "\t\tcreator,\r"
            + "\t\ttype%s)\r"
            + "\tVALUES\r"
            + "\t\t(ivarobjectkey,\r"
            + "\t\tivarname,\r"
            + "\t\tivarsource,\r"
            + "\t\tivarversion,\r"
            + "\t\tivarentityid,\r"
            + "\t\tivarentityidtype,\r"
            + "\t\tivarentityxml,\r"
            + "\t\tivardatecreated,\r"
            + "\t\tivarlastmodified,\r"
            + "\t\tivartitle,\r"
            + "\t\tivardeleted,\r"
            + "\t\tivarscope,\r"
            + "\t\tivarcreator,\r"
            + "\t\tivartype%s);\r"
            + "\rEND IF;\rRETURN;\rEND;\r$BODY$"
            + "\rLANGUAGE plpgsql VOLATILE\rCOST 100;";

    /**
     * Defines the PostGIS type to use for coordinate types
     */
    private static final String ST_TYPE = "geography";
    private static final boolean CREATE_FK = PostGreSQLSettings.isUseForeignKeys();
    private String schema = PostGreSQLSettings.getDatabaseSchema();
    private int srid = PostGreSQLSettings.getSRID();

    /**
     * Creates tables based on the record sets.
     * 
     * @param entity
     * @param conn
     * @throws CoalesceException
     */
    public void register(final CoalesceEntity entity, final CoalesceDataConnectorBase conn) throws CoalesceException
    {
        processAllElements(entity, conn);
    }

    /**
     * Creates tables based on the record sets.
     * 
     * @param template
     * @param conn
     * @throws CoalesceException
     */
    public void register(final CoalesceEntityTemplate template, final CoalesceDataConnectorBase conn)
            throws CoalesceException
    {
        processAllElements(template.createNewEntity(), conn);
    }

    /**
     * Removes BIT fields from the CoalesceEntity table.
     * 
     * @param columns
     * @param conn
     * @throws CoalesceException
     */
    public void unRegisterBitmaskFields(final Set<String> columns, final CoalesceDataConnectorBase conn)
            throws CoalesceException
    {
        for (String column : columns)
        {
            try
            {
                conn.executeUpdate(String.format(DROP_BITMASK_COLUMNS, schema, column));
            }
            catch (SQLException e)
            {
                throw new CoalesceException("Bitmask Unregister Failed", e);
            }
        }
    }

    /**
     * Appends BIT fields onto the CoalesceEntity table for Access Control List
     * (ACL) access restrictions.
     * 
     * @param columns
     * @param conn
     * @throws CoalesceException
     */
    public void registerBitmaskFields(final Map<String, Integer> columns, final CoalesceDataConnectorBase conn)
            throws CoalesceException

    {
        StringBuilder sbParams = new StringBuilder();
        StringBuilder sbSet = new StringBuilder();
        StringBuilder sbInsert = new StringBuilder();
        StringBuilder sbValues = new StringBuilder();

        try
        {
            for (Map.Entry<String, Integer> entry : columns.entrySet())
            {
                String sqlColumn = String.format(CREATE_BITMASK_COLUMNS, schema, entry.getKey(), entry.getValue());

                LOGGER.debug("Adding Column");
                LOGGER.debug(sqlColumn);

                conn.executeUpdate(sqlColumn);

                sbParams.append(String.format(",\r\t\tivar%s text", entry.getKey()));
                sbSet.append(String.format(",\r\t\t%1$s = ivar%1$s::bit(%2$s)", entry.getKey(), entry.getValue()));
                sbInsert.append(String.format(",\r\t\t%s", entry.getKey()));
                sbValues.append(String.format(",\r\t\tivar%s::bit(%s)", entry.getKey(), entry.getValue()));
            }

            String sql = String.format(CREATE_BITMASK_PROCEDURE,
                                       schema,
                                       sbParams.toString(),
                                       schema,
                                       sbSet.toString(),
                                       schema,
                                       sbInsert.toString(),
                                       sbValues.toString());

            LOGGER.debug("Create Stored Procedure");
            LOGGER.debug(sql);

            // Create Procedure
            conn.executeUpdate(sql);
        }
        catch (SQLException e)
        {
            throw new CoalesceException("Bitmask Register Failed", e);
        }

    }

    /**
     * Sets the schema. If not specified it will default to
     * {@link PostGreSQLSettings#getDatabaseSchema()}.
     * 
     * @param schema
     */
    public void setSchema(String schema)
    {
        this.schema = schema;
    }

    /**
     * Sets the SRID for geo-spatial fields. If not specified it will default to
     * {@link PostGreSQLSettings#getSRID()}.
     * 
     * @param srid
     */
    public void setSRID(int srid)
    {
        this.srid = srid;
    }

    @Override
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
            sb.append("\tobjectkey uuid NOT NULL,\r");
            sb.append("\tentitykey uuid NOT NULL,\r");
            sb.append("\tentityname text NOT NULL,\r");
            sb.append("\tentitysource text NOT NULL,\r");
            sb.append("\tentitytype text,\r");

            // Add Columns for Field Definitions
            for (CoalesceFieldDefinition fieldDefinition : recordset.getFieldDefinitions())
            {

                ECoalesceFieldDataTypes dataType = fieldDefinition.getDataType();

                if (fieldDefinition.isFlatten() && fieldDefinition.isListType())
                {
                    createListFieldTable(dataType, conn);
                }

                String columnType = getSQLType(dataType, srid);

                if (columnType != null && fieldDefinition.isFlatten())
                {
                    sb.append("\t" + fieldDefinition.getName() + " " + columnType + ",\r");
                }
                else
                {
                    LOGGER.info("Not Registering ({})", fieldDefinition.getName());
                }
            }

            sb.append("\tCONSTRAINT " + info.getTableName() + "_pkey PRIMARY KEY (objectkey)");
            if (CREATE_FK)
            {
                sb.append(",\r\tCONSTRAINT " + info.getTableName() + "_fkey FOREIGN KEY (entitykey) REFERENCES " + schema
                        + ".coalesceentity (objectkey) ON DELETE CASCADE");
            }

            String sql = String.format(CREATE_TABLE_FORMAT,
                                       schema,
                                       info.getTableName(),
                                       sb.toString(),
                                       schema,
                                       info.getTableName(),
                                       JodaDateTimeHelper.toXmlDateTimeUTC(JodaDateTimeHelper.nowInUtc()));

            LOGGER.debug("Register Template");
            LOGGER.debug(sql);

            // Create Table
            conn.executeUpdate(sql);

            // Create Stored Procedure for inserting into the table
            createStoredProcedure(recordset, conn);

        }
        catch (SQLException e)
        {
            throw new CoalesceException("Registeration Failed", e);
        }
        return false;
    }

    private List<String> getColumnNames(final String tablename, final CoalesceDataConnectorBase conn) throws SQLException
    {
        List<String> columnList = new ArrayList<>();

        ResultSet results = conn.executeQuery(SQL_GET_COLUMN_NAMES,
                                              new CoalesceParameter(tablename),
                                              new CoalesceParameter(schema));

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

        return columnList;
    }

    private void createStoredProcedure(final CoalesceRecordset recordset, final CoalesceDataConnectorBase conn)
            throws SQLException
    {

        // Get Parent's information
        CoalesceIndexInfo info = new CoalesceIndexInfo(recordset);

        List<String> columns = getColumnNames(info.getTableName(), conn);

        // No; Create
        String update = String.format(UPDATE_FORMAT,
                                      schema + "." + info.getTableName(),
                                      formatBody("\t\t%1$s = ivar%1$s",
                                                 "\t\t%1$s = " + createSpatialFormat("ivar%1$s"),
                                                 "\t\t%1$s = " + createCircleFormat("ivar%1$s"),
                                                 recordset,
                                                 columns));
        String insert = String.format(INSERT_FORMAT,
                                      schema + "." + info.getTableName(),
                                      formatBody("\t\t%s", recordset, columns),
                                      formatBody("\t\tivar%s", "\t\t" + createSpatialFormat("ivar%s"), "\t\t"
                                              + createCircleFormat("ivar%1$s"), recordset, columns));

        StringBuilder listsBuilder = new StringBuilder();

        // Create Complete Script
        StringBuilder sb = new StringBuilder();
        sb.append("ivarobjectkey uuid, ");
        sb.append("ivarentitykey uuid, ");
        sb.append("ivarentityname text, ");
        sb.append("ivarentitysource text, ");
        sb.append("ivarentitytype text");

        for (String field : columns)
        {
            // Get Field's Definition
            CoalesceFieldDefinition fieldDefinition = recordset.getFieldDefinition(field);

            // Template Contains Definition?
            if (fieldDefinition != null)
            {
                String columnType = getSQLTypeForArgs(fieldDefinition.getDataType());

                String fieldName = "ivar" + fieldDefinition.getName();
                if (columnType != null && fieldDefinition.isFlatten())
                {
                    sb.append(", " + fieldName + " " + columnType);

                    switch (fieldDefinition.getDataType()) {
                    case CIRCLE_TYPE:
                        // Add Radius as an Additional Parameter
                        sb.append(", " + fieldName + "Radius " + getSQLTypeForArgs(ECoalesceFieldDataTypes.DOUBLE_TYPE));
                        break;
                    default:
                        // Do Nothing
                        break;
                    }

                }

                String listColumnType = getListFieldSQLType(fieldDefinition.getDataType());

                if (listColumnType != null && fieldDefinition.isFlatten())
                {
                    listsBuilder.append(String.format("PERFORM %s.%s_insertorupdate (ivarentitykey,ivarobjectkey,'%s',%s);\r",
                                                      schema,
                                                      fieldDefinition.getDataType().getLabel(),
                                                      fieldDefinition.getName(),
                                                      fieldName));
                }
            }
            else
            {
                LOGGER.warn("Missing ({})'s Field Definition", field);
            }
        }

        String lists = listsBuilder.toString();

        String sql = String.format(CREATE_PROCEDURE_FORMAT,
                                   schema + "." + info.getTableName(),
                                   sb.toString(),
                                   update,
                                   insert,
                                   lists,
                                   JodaDateTimeHelper.toXmlDateTimeUTC(JodaDateTimeHelper.nowInUtc()));

        LOGGER.debug("Create Stored Procedure");
        LOGGER.debug(sql);

        // Create Procedure
        conn.executeUpdate(sql);

    }

    private String createSpatialFormat(String format)
    {
        return String.format("ST_Force_3D(ST_GeomFromText(%s,%s))", format, srid);
    }

    private String createCircleFormat(String format)
    {
        return String.format("ST_Force_3D(ST_GeomFromText(ST_AsText(ST_Buffer(ST_GeogFromText(%1$s), %1$sRadius)), %2$s))",
                             format,
                             srid);
    }

    private void createListFieldTable(final ECoalesceFieldDataTypes dataType, final CoalesceDataConnectorBase conn)
            throws SQLException
    {

        /*
         * Table
         */

        // Build SQL Command from Field Definitions
        StringBuilder sb = new StringBuilder();

        String columnType = getListFieldSQLType(dataType);

        String tableName = schema + "." + "fieldtable_" + dataType.getLabel();

        // Add Required Columns
        sb.append("\trecordkey uuid NOT NULL,\r");
        sb.append("\tentitykey uuid NOT NULL,\r");
        sb.append("\tfieldname text NOT NULL,\r");
        sb.append("\tlistorder int NOT NULL,\r");
        sb.append("\tfieldvalue " + columnType + " NOT NULL"); // Not null?
        if (CREATE_FK)
        {
            sb.append(",\r\tCONSTRAINT entitykey_fkey FOREIGN KEY (entitykey) REFERENCES " + schema
                    + ".coalesceentity (objectkey) ON DELETE CASCADE");
        }

        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(\r" + sb.toString() + "\r);";

        LOGGER.debug(sql);

        // Create Table
        conn.executeUpdate(sql);

        /*
         * Index
         */

        String indexName = "fieldtable_" + dataType.getLabel() + "_idx";

        sql = "DO $$\rBEGIN\rIF NOT EXISTS (\rSELECT 1\rFROM pg_class c\rJOIN pg_namespace n ON n.oid = c.relnamespace\rWHERE c.relname = '"
                + indexName
                + "'\rAND n.nspname = '"
                + schema
                + "'\r) THEN\rCREATE INDEX "
                + indexName
                + " ON "
                + tableName
                + "(fieldname, fieldvalue);\rEND IF;\rEND$$";

        LOGGER.debug(sql);

        conn.executeUpdate(sql);

        createListStoredProcedure(dataType, conn);

    }

    private void createListStoredProcedure(final ECoalesceFieldDataTypes dataType, final CoalesceDataConnectorBase conn)
            throws SQLException
    {
        String format = "CREATE OR REPLACE FUNCTION %1$s.%2$s_insertorupdate(ivarentitykey uuid, ivarrecordkey uuid, ivarfieldname text, ivarvalues text)\r"
                + "RETURNS void AS\r"
                + "$BODY$\r"
                + "BEGIN\r"
                + "  DELETE FROM %1$s.fieldtable_%2$s\r"
                + "  WHERE recordkey = ivarrecordkey AND fieldname = ivarfieldname;\r"
                + "  IF NOT ivarvalues IS NULL THEN\r"
                + "    INSERT INTO %1$s.fieldtable_%2$s (recordkey, entitykey, fieldname, listorder, fieldvalue) (\r"
                + "      SELECT ivarrecordkey,\r"
                + "      ivarrecordkey,\r"
                + "      ivarfieldname,\r"
                + "      ROW_NUMBER() OVER(),\r"
                + "      fieldvalue FROM (SELECT %3$s AS fieldvalue) AS foo\r"
                + "    );\r"
                + "  END IF;\rRETURN;\rEND;\r$BODY$\rLANGUAGE plpgsql VOLATILE COST 100;";

        String parserFormat;

        if (dataType == ECoalesceFieldDataTypes.STRING_LIST_TYPE)
        {
            parserFormat = schema
                    + ".unescape(regexp_split_to_table(ivarvalues,',(?=(?:[^\"]*\"[^\"]*\")*(?![^\"]*\"))')::text)";
        }
        else
        {
            parserFormat = String.format("regexp_split_to_table(ivarvalues,',')::%s", getListFieldSQLType(dataType));
        }

        conn.executeUpdate(String.format(format, schema, dataType.getLabel(), parserFormat));
    }

    private String formatBody(final String format, final CoalesceRecordset recordset, final List<String> columns)
    {

        StringBuilder sb = new StringBuilder();

        // Create update Portion
        for (CoalesceFieldDefinition fieldDefinition : recordset.getFieldDefinitions())
        {

            String columnType = getSQLType(fieldDefinition.getDataType(), srid);

            if (columnType != null && fieldDefinition.isFlatten()
                    && columns.contains(fieldDefinition.getName().toLowerCase()))
            {
                sb.append(String.format(format + ",\r", fieldDefinition.getName()));
            }
        }

        sb.append(String.format(format + ",\r", "entityname"));
        sb.append(String.format(format + ",\r", "entitysource"));
        sb.append(String.format(format + ",\r", "entitytype"));
        sb.append(String.format(format + ",\r", "objectkey"));
        sb.append(String.format(format, "entitykey"));

        return sb.toString();
    }

    private String formatBody(final String format,
                              String geoFormat,
                              String circleFormat,
                              final CoalesceRecordset recordset,
                              final List<String> columns)
    {

        StringBuilder sb = new StringBuilder();

        // Create update Portion
        for (CoalesceFieldDefinition fieldDefinition : recordset.getFieldDefinitions())
        {

            ECoalesceFieldDataTypes type = fieldDefinition.getDataType();
            String columnType = getSQLType(type, srid);

            if (columnType != null && fieldDefinition.isFlatten()
                    && columns.contains(fieldDefinition.getName().toLowerCase()))
            {
                switch (type) {
                case GEOCOORDINATE_LIST_TYPE:
                case GEOCOORDINATE_TYPE:
                case LINE_STRING_TYPE:
                case POLYGON_TYPE:
                    sb.append(String.format(geoFormat + ",\r", fieldDefinition.getName()));
                    break;
                case CIRCLE_TYPE:
                    sb.append(String.format(circleFormat + ",\r", fieldDefinition.getName()));
                    break;
                default:
                    sb.append(String.format(format + ",\r", fieldDefinition.getName()));
                }
            }

        }

        sb.append(String.format(format + ",\r", "entityname"));
        sb.append(String.format(format + ",\r", "entitysource"));
        sb.append(String.format(format + ",\r", "entitytype"));

        sb.append(String.format(format + ",\r", "objectkey"));
        sb.append(String.format(format, "entitykey"));

        return sb.toString();
    }

    /**
     * @param type Coalesce Data Type
     * @param srid
     * @return SQL type used when tables are being generated.
     */
    public static String getSQLType(final ECoalesceFieldDataTypes type, final int srid)
    {

        switch (type) {

        case BOOLEAN_TYPE:
            return "boolean";

        case DOUBLE_TYPE:
        case FLOAT_TYPE:
            return "double precision";

        case GEOCOORDINATE_LIST_TYPE:
            return ST_TYPE + "(MULTIPOINTZ," + srid + ")";

        case GEOCOORDINATE_TYPE:
            return ST_TYPE + "(POINTZ," + srid + ")";

        case LINE_STRING_TYPE:
            return ST_TYPE + "(LineStringZ," + srid + ")";

        case POLYGON_TYPE:
        case CIRCLE_TYPE:
            return ST_TYPE + "(PolygonZ," + srid + ")";

        case ENUMERATION_TYPE:
        case INTEGER_TYPE:
            return "integer";

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
            return "text";

        case GUID_TYPE:
            return "uuid";

        case DATE_TIME_TYPE:
            return "timestamp with time zone";

        case LONG_TYPE:
            return "bigint";

        case FILE_TYPE:
        case BINARY_TYPE:
        default:
            return null;
        }

    }

    /**
     * @param type Coalesce Data Type
     * @return SQL type used as arguments within stored procedures.
     */
    public static String getSQLTypeForArgs(final ECoalesceFieldDataTypes type)
    {

        switch (type) {

        case BOOLEAN_TYPE:
            return "boolean";

        case DOUBLE_TYPE:
        case FLOAT_TYPE:
            return "double precision";

        case ENUMERATION_TYPE:
        case INTEGER_TYPE:
            return "integer";

        case GEOCOORDINATE_LIST_TYPE:
        case GEOCOORDINATE_TYPE:
        case LINE_STRING_TYPE:
        case POLYGON_TYPE:
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
        case CIRCLE_TYPE:
            return "text";

        case GUID_TYPE:
            return "uuid";

        case DATE_TIME_TYPE:
            return "timestamp with time zone";

        case LONG_TYPE:
            return "bigint";

        case FILE_TYPE:
        case BINARY_TYPE:
        default:
            return null;
        }

    }

    private static String getListFieldSQLType(final ECoalesceFieldDataTypes type)
    {

        switch (type) {

        case DOUBLE_LIST_TYPE:
        case FLOAT_LIST_TYPE:
            return "double precision";

        case ENUMERATION_LIST_TYPE:
        case INTEGER_LIST_TYPE:
            return "integer";

        case STRING_LIST_TYPE:
            return "text";

        case GUID_LIST_TYPE:
            return "uuid";

        case LONG_LIST_TYPE:
            return "bigint";

        case BOOLEAN_LIST_TYPE:
            return "boolean";

        case BOOLEAN_TYPE:
        case GEOCOORDINATE_LIST_TYPE:
        case GEOCOORDINATE_TYPE:
        case DATE_TIME_TYPE:
        case GUID_TYPE:
        case DOUBLE_TYPE:
        case FLOAT_TYPE:
        case FILE_TYPE:
        case ENUMERATION_TYPE:
        case INTEGER_TYPE:
        case LONG_TYPE:
        case BINARY_TYPE:
        case STRING_TYPE:
        case URI_TYPE:
        case LINE_STRING_TYPE:
        case POLYGON_TYPE:
        case CIRCLE_TYPE:
        default:
            return null;
        }

    }

}
