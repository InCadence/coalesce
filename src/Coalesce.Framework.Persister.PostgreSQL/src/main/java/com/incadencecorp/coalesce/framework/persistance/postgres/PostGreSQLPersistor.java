package com.incadencecorp.coalesce.framework.persistance.postgres;

import java.io.IOException;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import org.joda.time.DateTime;

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.CoalesceTableHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.framework.CoalesceSettings;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldHistory;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceObject;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
import com.incadencecorp.coalesce.framework.persistance.CoalescePersistorBase;
import com.incadencecorp.coalesce.framework.persistance.ElementMetaData;
import com.incadencecorp.coalesce.framework.persistance.EntityMetaData;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import org.xml.sax.SAXException;

/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

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

/**
 * This persister is for a PostGres database.
 *
 * @author n78554
 */
public class PostGreSQLPersistor extends CoalescePersistorBase {

    /*--------------------------------------------------------------------------
    Private Members
    --------------------------------------------------------------------------*/

    private String _schema;

    /*--------------------------------------------------------------------------
    Overrided Functions
    --------------------------------------------------------------------------*/

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

    /**
     * Returns the Coalesce entity keys that matches the given parameters.
     *
     * @param entityId of the entity.
     * @param entityIdType of the entity.
     * @param entityName of the entity.
     * @param entitySource of the entity.
     * @return List$lt;String&gt; of primary keys for the matching Coalesce entity.
     * @throws CoalescePersistorException
     */
    public List<String> getCoalesceEntityKeysForEntityId(String entityId,
                                                         String entityIdType,
                                                         String entityName,
                                                         String entitySource) throws CoalescePersistorException
    {
        try
        {
            if (entitySource != null && entitySource != "")
            {
                return getCoalesceEntityKeysForEntityIdAndSource(entityId, entityIdType, entityName, entitySource);
            }
            else
            {
                return this.getCoalesceEntityKeysForEntityId(entityId, entityIdType, entityName);
            }
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("GetCoalesceEntityKeysForEntityId", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetCoalesceEntityKeysForEntityId", e);
        }
    }

    /**
     * Returns the Coalesce entity meta data that matches the given parameters.
     *
     * @param key the primary key of the entity.
     * @return EntityMetaData for the matching Coalesce entity.
     * @throws CoalescePersistorException
     */
    public EntityMetaData getCoalesceEntityIdAndTypeForKey(String key) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = new PostGreSQLDataConnector(getConnectionSettings(), getSchemaPrefix()))
        {
            return this.getCoalesceEntityIdAndTypeForKey(key, conn);
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("GetCoalesceEntityIdAndTypeForKey", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetCoalesceEntityIdAndTypeForKey", e);
        }
    }

    /**
     * Returns the last modified date for the Coalesce object (entity, field,
     * record, linkage, etc.) that matches the given parameters.
     *
     * @param key the primary key of the Coalesce object.
     * @param objectType is the Coalesce object to retrieve the information for.
     * @return DateTime containing the last modified date for the Coalesce
     *         object matching the values.
     * @throws CoalescePersistorException
     */
    public DateTime getCoalesceObjectLastModified(String key, String objectType) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = new PostGreSQLDataConnector(getConnectionSettings(), getSchemaPrefix()))
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

    /**
     * Returns the Coalesce field binary data that matches the given parameters.
     *
     * @param key the primary key of the Coalesce field.
     * @return byte[] the binary data of the Coalesce field matching the value.
     * @throws CoalescePersistorException
     */
    public byte[] getBinaryArray(String key) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = new PostGreSQLDataConnector(getConnectionSettings(), getSchemaPrefix()))
        {

            byte[] binaryArray = null;

            ResultSet results = conn.executeQuery("SELECT BinaryObject FROM " + getSchemaPrefix()
                    + "CoalesceFieldBinaryData WHERE ObjectKey=?", new CoalesceParameter(key, Types.OTHER));

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
                // Always persist template
                conn.executeProcedure("CoalesceEntityTemplate_InsertOrUpdate",
                                      new CoalesceParameter(template.getKey(), Types.OTHER),
                                      new CoalesceParameter(template.getName()),
                                      new CoalesceParameter(template.getSource()),
                                      new CoalesceParameter(template.getVersion()),
                                      new CoalesceParameter(template.toXml()),
                                      new CoalesceParameter(JodaDateTimeHelper.nowInUtc().toString(), Types.OTHER),
                                      new CoalesceParameter(JodaDateTimeHelper.nowInUtc().toString(), Types.OTHER));
            }
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("PersistEntityTemplate", e);
        }

    }

    /**
     * Returns the ElementMetaData for the Coalesce object that matches the
     * given parameters.
     *
     * @param key the Coalesce object primary key
     * @param objectType the Coalesce object type specification.
     * @return ElementMetaData
     * @throws CoalescePersistorException
     */
    public ElementMetaData getXPath(String key, String objectType) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = new PostGreSQLDataConnector(getConnectionSettings(), getSchemaPrefix()))
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

    /**
     * Returns the value of the specified Coalesce field.
     *
     * @param fieldKey the primary key of the field.
     * @return returns the value of the matching field.
     * @throws CoalescePersistorException
     */
    public String getFieldValue(String fieldKey) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = new PostGreSQLDataConnector(getConnectionSettings(), getSchemaPrefix()))
        {
            String value = null;

            ResultSet results = conn.executeQuery("SELECT value FROM " + getSchemaPrefix()
                    + "CoalesceField WHERE ObjectKey =?", new CoalesceParameter(fieldKey, Types.OTHER));

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
        try (CoalesceDataConnectorBase conn = new PostGreSQLDataConnector(getConnectionSettings(), getSchemaPrefix()))
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
                parameters.add(new CoalesceParameter(key, Types.OTHER));
            }

            String SQL = String.format("SELECT EntityXml FROM %sCoalesceEntity WHERE ObjectKey IN (%s)",
                                       getSchemaPrefix(),
                                       sb.toString());

            ResultSet results = conn.executeQuery(SQL, parameters.toArray(new CoalesceParameter[parameters.size()]));

            while (results.next())
            {
                xmlList.add(results.getString("EntityXml"));
            }

            return xmlList.toArray(new String[xmlList.size()]);
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

    /**
     * Returns the Coalesce entity that matches the given parameters.
     *
     * @param entityId the unique identifier, such as a TCN number for an EFT.
     * @param entityIdType the type of entityId, such as TCN.
     * @return the matching Coalesce entity.
     * @throws CoalescePersistorException
     */
    public String getEntityXml(String entityId, String entityIdType) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = new PostGreSQLDataConnector(getConnectionSettings(), getSchemaPrefix()))
        {
            String value = null;

            ResultSet results = conn.executeQuery("SELECT EntityXml FROM " + getSchemaPrefix()
                                                          + "CoalesceEntity WHERE EntityId=? AND EntityIdType=?",
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

    /**
     * Returns the Coalesce entity that matches the given parameters.
     *
     * @param name the name of the entity.
     * @param entityId the unique identifier, such as a TCN number for an EFT.
     * @param entityIdType the type of entityId, such as TCN.
     * @return the matching Coalesce entity.
     * @throws CoalescePersistorException
     */
    public String getEntityXml(String name, String entityId, String entityIdType) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = new PostGreSQLDataConnector(getConnectionSettings(), getSchemaPrefix()))
        {
            String value = null;

            ResultSet results = conn.executeQuery("SELECT EntityXml FROM " + getSchemaPrefix()
                                                          + "CoalesceEntity WHERE Name=? AND EntityId=? AND EntityIdType=?",
                                                  new CoalesceParameter(name),
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

    @Override
    protected CoalesceDataConnectorBase getDataConnector() throws CoalescePersistorException
    {
        return new PostGreSQLDataConnector(getConnectionSettings(), getSchemaPrefix());
    }

    /*
     * @Override public String[] GetEntityXmlAsStrings(String EntityId, String
     * EntityIdType) { String[] crst = null; try { String sqlStmt =
     * "SELECT ObjectKey from CoalesceEntity WHERE EntityId=?"; sqlStmt +=
     * " AND EntityIdType=?"; ArrayList<String> params = new ArrayList<>();
     * params.add(EntityId.trim()); params.add(EntityIdType.trim()); crst =
     * getEntityKeysFromSQL(sqlStmt, params); return crst; } catch (Exception
     * ex) { CallResult.log(CallResults.FAILED_ERROR, ex, "getEntityKeys");
     * return crst; } } private XsdEntity getXSDEntityXML(String sqlStmt,
     * ArrayList<String> sqlParams) throws Exception { XsdEntity crst = new
     * XsdEntity(); Connection conn = null; Statement stx = null; try {
     * Class.forName("com.mysql.jdbc.Driver"); conn =
     * DriverManager.getConnection(serCon.getURL(), serCon.getUser(),
     * serCon.getPassword()); stx = conn.createStatement();
     * java.sql.PreparedStatement sql; sql = conn.prepareStatement(sqlStmt);
     * Object[] pRams = sqlParams.toArray(); for (int iSetter = 0; iSetter <
     * sqlParams.size(); iSetter++) { sql.setString(iSetter + 1,
     * pRams[iSetter].toString()); } ResultSet srs = sql.executeQuery();
     * ResultSetMetaData rsmd = srs.getMetaData(); if (rsmd.getColumnCount() <=
     * 1) { while (srs.next()) { crst.Initialize(srs.getString("EntityXml")); }
     * } return crst; } finally { stx.close(); conn.close(); } }
     */

    @Override
    protected boolean flattenObject(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        boolean isSuccessful = true;

        try (CoalesceDataConnectorBase conn = new PostGreSQLDataConnector(getConnectionSettings(), getSchemaPrefix()))
        {

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

        }

        return isSuccessful;
    }

    @Override
    protected boolean flattenCore(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        boolean isSuccessful = false;

        CoalesceDataConnectorBase conn = null;

        // Create a Database Connection
        try
        {
            conn = new PostGreSQLDataConnector(getConnectionSettings(), getSchemaPrefix());
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

    @Override
    public String getEntityTemplateKey(String name, String source, String version) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = new PostGreSQLDataConnector(getConnectionSettings(), getSchemaPrefix()))
        {
            String value = null;

            ResultSet results = conn.executeQuery("SELECT TemplateKey FROM " + getSchemaPrefix()
                                                          + "CoalesceEntityTemplate WHERE Name=? and Source=? and Version=?",
                                                  new CoalesceParameter(name),
                                                  new CoalesceParameter(source),
                                                  new CoalesceParameter(version));

            while (results.next())
            {
                value = results.getString("TemplateKey");
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
        try (CoalesceDataConnectorBase conn = new PostGreSQLDataConnector(getConnectionSettings(), getSchemaPrefix()))
        {
            return conn.getTemplateMetaData("SELECT * FROM " + getSchemaPrefix() + "CoalesceEntityTemplate");
        }
        catch (Exception ex)
        {
            throw new CoalescePersistorException("getEntityTemplateMetadata", ex);
        }
    }

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String key) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = new PostGreSQLDataConnector(getConnectionSettings(), getSchemaPrefix()))
        {
            String xml = null;

            ResultSet results = conn.executeQuery("SELECT TemplateXml FROM " + getSchemaPrefix()
                    + "CoalesceEntityTemplate WHERE TemplateKey=?", new CoalesceParameter(key, Types.OTHER));

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
        catch (SQLException | SAXException | IOException e)
        {
            throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_FOUND, "Template", key), e);
        }

    }

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String name, String source, String version) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = new PostGreSQLDataConnector(getConnectionSettings(), getSchemaPrefix()))
        {
            String xml = null;

            ResultSet results = conn.executeQuery("SELECT TemplateXml FROM " + getSchemaPrefix()
                                                          + "CoalesceEntityTemplate WHERE Name=? and Source=? and Version=?",
                                                  new CoalesceParameter(name),
                                                  new CoalesceParameter(source),
                                                  new CoalesceParameter(version));

            if (results.next())
            {
                xml = results.getString("TemplateXml");
            }

            if (xml == null)
            {
                throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_FOUND, "Template", "Name: " + name + " Source: " + source + " Version: " + version));
            }

            return CoalesceEntityTemplate.create(xml);
        }
        catch (SQLException | SAXException | IOException e)
        {
            throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_FOUND, "Template", "Name: " + name + " Source: " + source + " Version: " + version), e);
        }
    }

    /*--------------------------------------------------------------------------
    Protected Functions
    --------------------------------------------------------------------------*/
    /**
     * Adds or Updates a Coalesce object that matches the given parameters.
     *
     * @param coalesceObject the Coalesce object to be added or updated
     * @param conn is the PostGresDataConnector database connection
     * @return isSuccessful = True = Successful add/update operation.
     * @throws SQLException
     */
    protected boolean persistObject(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn) throws SQLException
    {
        boolean isSuccessful = true;

        switch (coalesceObject.getType()) {
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

        if (isSuccessful && CoalesceSettings.getUseIndexing())
        {
            // Persist Map Table Entry
            isSuccessful = persistMapTableEntry(coalesceObject, conn);
        }

        return isSuccessful;
    }

    /**
     * Adds or updates map table entry for a given element.
     *
     * @param coalesceObject the Coalesce object to be added or updated
     * @param conn is the SQLServerDataConnector database connection
     * @return True if successfully added/updated.
     * @throws SQLException
     */
    protected boolean persistMapTableEntry(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn)
            throws SQLException
    {
        return true;
        // String parentKey;
        // String parentType;
        //
        // if (coalesceObject.getParent() != null)
        // {
        // parentKey = coalesceObject.getParent().getKey();
        // parentType = coalesceObject.getParent().getType();
        // }
        // else
        // {
        // parentKey = "00000000-0000-0000-0000-000000000000";
        // parentType = "";
        // }
        //
        // return conn.executeProcedure("CoalesceObjectMap_Insert",
        // new CoalesceParameter(parentKey, Types.OTHER),
        // new CoalesceParameter(parentType),
        // new CoalesceParameter(coalesceObject.getKey(), Types.OTHER),
        // new CoalesceParameter(coalesceObject.getType()));
    }

    /**
     * Adds or Updates a Coalesce entity that matches the given parameters.
     *
     * @param entity the XsdEntity to be added or updated
     * @param conn is the PostGresDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistEntityObject(CoalesceEntity entity, CoalesceDataConnectorBase conn) throws SQLException
    {
        String procedureName = "CoalesceEntity_InsertOrUpdate";

        // Return true if no update is required.
        if (!checkLastModified(entity, conn))
        {
            return true;
        }

        List<CoalesceParameter> params = new ArrayList<CoalesceParameter>();
        params.add(new CoalesceParameter(entity.getKey(), Types.OTHER));
        params.add(new CoalesceParameter(entity.getName()));
        params.add(new CoalesceParameter(entity.getSource()));
        params.add(new CoalesceParameter(entity.getVersion()));
        params.add(new CoalesceParameter(entity.getEntityId()));
        params.add(new CoalesceParameter(entity.getEntityIdType()));
        params.add(new CoalesceParameter(entity.toXml()));
        params.add(new CoalesceParameter(entity.getDateCreated().toString(), Types.OTHER));
        params.add(new CoalesceParameter(entity.getLastModified().toString(), Types.OTHER));
        params.add(new CoalesceParameter(entity.getTitle()));
        params.add(new CoalesceParameter(Boolean.toString(entity.isMarkedDeleted()), Types.BOOLEAN));
        params.add(new CoalesceParameter(getScope(entity)));
        params.add(new CoalesceParameter(getCreator(entity)));
        params.add(new CoalesceParameter(getType(entity)));

        List<CoalesceParameter> securityColumns = getExtendedParameters(entity);

        if (securityColumns.size() > 0)
        {
            params.addAll(securityColumns);
            procedureName = "CoalesceEntityExt_InsertOrUpdate";
        }

        return conn.executeProcedure(procedureName, params.toArray(new CoalesceParameter[params.size()]))
                && !entity.isMarkedDeleted();
    }

    /*--------------------------------------------------------------------------
    Protected Methods
    --------------------------------------------------------------------------*/

    protected String getCreator(CoalesceEntity entity)
    {
        return null;
    }

    protected String getType(CoalesceEntity entity)
    {
        return null;
    }

    protected String getScope(CoalesceEntity entity)
    {
        return null;
    }

    /**
     * 
     * @param entity
     * @return additional rows that have been registered for your implementaion.
     */
    protected List<CoalesceParameter> getExtendedParameters(CoalesceEntity entity)
    {
        return new ArrayList<CoalesceParameter>();
    }

    /**
     * Adds or Updates a Coalesce section that matches the given parameters.
     *
     * @param section the XsdSection to be added or updated
     * @param conn is the PostGresDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistSectionObject(CoalesceSection section, CoalesceDataConnectorBase conn) throws SQLException
    {
        // Return true if no update is required.
        if (!checkLastModified(section, conn))
        {
            return true;
        }

        // Yes; Call Store Procedure
        return conn.executeProcedure("CoalesceSection_InsertOrUpdate",
                                     new CoalesceParameter(section.getKey(), Types.OTHER),
                                     new CoalesceParameter(section.getName()),
                                     new CoalesceParameter(section.getParent().getKey(), Types.OTHER),
                                     new CoalesceParameter(section.getParent().getType()),
                                     new CoalesceParameter(section.getDateCreated().toString(), Types.OTHER),
                                     new CoalesceParameter(section.getLastModified().toString(), Types.OTHER));
    }

    /**
     * Adds or Updates a Coalesce recordset that matches the given parameters.
     *
     * @param recordset the XsdRecordset to be added or updated
     * @param conn is the PostGresDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistRecordsetObject(CoalesceRecordset recordset, CoalesceDataConnectorBase conn)
            throws SQLException
    {
        // Return true if no update is required.
        if (!checkLastModified(recordset, conn))
        {
            return true;
        }

        // Yes; Call Store Procedure
        return conn.executeProcedure("CoalesceRecordset_InsertOrUpdate",
                                     new CoalesceParameter(recordset.getKey(), Types.OTHER),
                                     new CoalesceParameter(recordset.getName()),
                                     new CoalesceParameter(recordset.getParent().getKey(), Types.OTHER),
                                     new CoalesceParameter(recordset.getParent().getType()),
                                     new CoalesceParameter(recordset.getDateCreated().toString(), Types.OTHER),
                                     new CoalesceParameter(recordset.getLastModified().toString(), Types.OTHER));
    }

    /**
     * Adds or Updates a Coalesce field definition that matches the given
     * parameters.
     *
     * @param fieldDefinition the XsdFieldDefinition to be added or updated
     * @param conn is the PostGresDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistFieldDefinitionObject(CoalesceFieldDefinition fieldDefinition, CoalesceDataConnectorBase conn)
            throws SQLException
    {

        // Return true if no update is required.
        if (!checkLastModified(fieldDefinition, conn))
        {
            return true;
        }

        // Yes; Call Store Procedure
        return conn.executeProcedure("CoalesceFieldDefinition_InsertOrUpdate",
                                     new CoalesceParameter(fieldDefinition.getKey(), Types.OTHER),
                                     new CoalesceParameter(fieldDefinition.getName()),
                                     new CoalesceParameter(fieldDefinition.getParent().getKey(), Types.OTHER),
                                     new CoalesceParameter(fieldDefinition.getParent().getType()),
                                     new CoalesceParameter(fieldDefinition.getDateCreated().toString(), Types.OTHER),
                                     new CoalesceParameter(fieldDefinition.getLastModified().toString(), Types.OTHER));
    }

    /**
     * Adds or Updates a Coalesce record that matches the given parameters.
     *
     * @param record the XsdRecord to be added or updated
     * @param conn is the PostGresDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistRecordObject(CoalesceRecord record, CoalesceDataConnectorBase conn) throws SQLException
    {
        // Return true if no update is required.
        if (!checkLastModified(record, conn))
        {
            return true;
        }

        // Yes; Call Store Procedure
        return conn.executeProcedure("CoalesceRecord_InsertOrUpdate",
                                     new CoalesceParameter(record.getKey(), Types.OTHER),
                                     new CoalesceParameter(record.getName()),
                                     new CoalesceParameter(record.getParent().getKey(), Types.OTHER),
                                     new CoalesceParameter(record.getParent().getType()),
                                     new CoalesceParameter(record.getDateCreated().toString(), Types.OTHER),
                                     new CoalesceParameter(record.getLastModified().toString(), Types.OTHER));
    }

    /**
     * Adds or Updates a Coalesce field that matches the given parameters.
     *
     * @param field the XsdField to be added or updated
     * @param conn is the PostGresDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistFieldObject(CoalesceField<?> field, CoalesceDataConnectorBase conn) throws SQLException
    {
        // Return true if no update is required.
        if (!checkLastModified(field, conn))
        {
            return true;
        }

        // Yes; Call Store Procedure
        return conn.executeProcedure("CoalesceField_InsertOrUpdate",
                                     new CoalesceParameter(field.getKey(), Types.OTHER),
                                     new CoalesceParameter(field.getName()),
                                     new CoalesceParameter(field.getBaseValue()),
                                     new CoalesceParameter(field.getDataType().getLabel()),
                                     new CoalesceParameter(""),
                                     new CoalesceParameter(field.getClassificationMarkingAsString()),
                                     new CoalesceParameter(field.getModifiedBy()),
                                     new CoalesceParameter(field.getParent().getKey(), Types.OTHER),
                                     new CoalesceParameter(field.getParent().getType()),
                                     new CoalesceParameter(field.getDateCreated().toString(), Types.OTHER),
                                     new CoalesceParameter(field.getLastModified().toString(), Types.OTHER),
                                     new CoalesceParameter(field.getPreviousHistoryKey(), Types.OTHER));
    }

    /**
     * Adds or Updates a Coalesce field history that matches the given
     * parameters.
     *
     * @param fieldHistory the XsdFieldHistory to be added or updated
     * @param conn is the PostGresDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistFieldHistoryObject(CoalesceFieldHistory fieldHistory, CoalesceDataConnectorBase conn)
            throws SQLException
    {
        // Return true if no update is required.
        if (!checkLastModified(fieldHistory, conn))
        {
            return true;
        }

        // Yes; Call Store Procedure
        return conn.executeProcedure("CoalesceFieldHistory_InsertOrUpdate",
                                     new CoalesceParameter(fieldHistory.getKey(), Types.OTHER),
                                     new CoalesceParameter(fieldHistory.getName()),
                                     new CoalesceParameter(fieldHistory.getValue()),
                                     new CoalesceParameter(fieldHistory.getDataType().getLabel()),
                                     new CoalesceParameter(""),
                                     new CoalesceParameter(fieldHistory.getClassificationMarkingAsString()),
                                     new CoalesceParameter(fieldHistory.getModifiedBy()),
                                     new CoalesceParameter(fieldHistory.getParent().getKey(), Types.OTHER),
                                     new CoalesceParameter(fieldHistory.getParent().getType()),
                                     new CoalesceParameter(fieldHistory.getDateCreated().toString(), Types.OTHER),
                                     new CoalesceParameter(fieldHistory.getLastModified().toString(), Types.OTHER),
                                     new CoalesceParameter(fieldHistory.getPreviousHistoryKey(), Types.OTHER));
    }

    /**
     * Adds or Updates a Coalesce linkage section that matches the given
     * parameters.
     *
     * @param linkageSection the XsdLinkageSection to be added or updated
     * @param conn is the PostGresDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistLinkageSectionObject(CoalesceLinkageSection linkageSection, CoalesceDataConnectorBase conn)
            throws SQLException
    {
        // Return true if no update is required.
        if (!checkLastModified(linkageSection, conn))
        {
            return true;
        }

        // Yes; Call Store Procedure
        return conn.executeProcedure("CoalesceLinkageSection_InsertOrUpdate",
                                     new CoalesceParameter(linkageSection.getKey(), Types.OTHER),
                                     new CoalesceParameter(linkageSection.getName()),
                                     new CoalesceParameter(linkageSection.getParent().getKey(), Types.OTHER),
                                     new CoalesceParameter(linkageSection.getParent().getType()),
                                     new CoalesceParameter(linkageSection.getDateCreated().toString(), Types.OTHER),
                                     new CoalesceParameter(linkageSection.getLastModified().toString(), Types.OTHER));
    }

    /**
     * Adds or Updates a Coalesce linkage that matches the given parameters.
     *
     * @param linkage the XsdLinkage to be added or updated
     * @param conn is the PostGresDataConnector database connection
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

        // Yes; Call Store Procedure
        return conn.executeProcedure("CoalesceLinkage_InsertOrUpdate",
                                     new CoalesceParameter(linkage.getKey(), Types.OTHER),
                                     new CoalesceParameter(linkage.getName()),
                                     new CoalesceParameter(linkage.getEntity1Key(), Types.OTHER),
                                     new CoalesceParameter(linkage.getEntity1Name()),
                                     new CoalesceParameter(linkage.getEntity1Source()),
                                     new CoalesceParameter(linkage.getEntity1Version()),
                                     new CoalesceParameter(linkage.getLinkType().getLabel()),
                                     new CoalesceParameter(linkage.getLabel()),
                                     new CoalesceParameter(linkage.getStatus().toString()),
                                     new CoalesceParameter(linkage.getEntity2Key(), Types.OTHER),
                                     new CoalesceParameter(linkage.getEntity2Name()),
                                     new CoalesceParameter(linkage.getEntity2Source()),
                                     new CoalesceParameter(linkage.getEntity2Version()),
                                     new CoalesceParameter(linkage.getClassificationMarking().toPortionString()),
                                     new CoalesceParameter(linkage.getModifiedBy()),
                                     new CoalesceParameter(linkage.getInputLang().getDisplayName()),
                                     new CoalesceParameter(linkage.getParent().getKey(), Types.OTHER),
                                     new CoalesceParameter(linkage.getParent().getType()),
                                     new CoalesceParameter(linkage.getDateCreated().toString(), Types.OTHER),
                                     new CoalesceParameter(linkage.getLastModified().toString(), Types.OTHER));
    }

    /**
     * Returns the EntityMetaData for the Coalesce entity that matches the given
     * parameters.
     *
     * @param key primary key of the Coalesce entity
     * @param conn is the PostGresDataConnector database connection
     * @return metaData the EntityMetaData for the Coalesce entity.
     * @throws SQLException
     */
    protected EntityMetaData getCoalesceEntityIdAndTypeForKey(String key, CoalesceDataConnectorBase conn)
            throws SQLException
    {
        EntityMetaData metaData = null;

        // Execute Query
        ResultSet results = conn.executeQuery("SELECT EntityId,EntityIdType,ObjectKey FROM " + getSchemaPrefix()
                + "CoalesceEntity WHERE ObjectKey=?", new CoalesceParameter(key, Types.OTHER));
        // Get Results
        while (results.next())
        {
            metaData = new EntityMetaData(results.getString("EntityId"),
                                          results.getString("EntityIdType"),
                                          results.getString("ObjectKey"));
        }

        return metaData;
    }

    /**
     * Returns the comparison for the Coalesce object last modified date versus
     * the same objects value in the database.
     *
     * @param coalesceObject the Coalesce object to have it's last modified date
     *            checked.
     * @param conn is the PostGresDataConnector database connection
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

    /**
     * Deletes the Coalesce object and CoalesceObjectMap that matches the given
     * parameters.
     *
     * @param coalesceObject the Coalesce object to be deleted
     * @param conn is the PostGresDataConnector database connection
     * @return True = Successful delete
     * @throws SQLException
     */
    protected boolean deleteObject(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn) throws SQLException
    {
        String objectType = coalesceObject.getType();
        String objectKey = coalesceObject.getKey();
        String tableName = CoalesceTableHelper.getTableNameForObjectType(objectType);

        conn.executeUpdate("DELETE FROM " + getSchemaPrefix() + "CoalesceObjectMap WHERE ObjectKey=?",
                           new CoalesceParameter(objectKey, Types.OTHER));
        conn.executeUpdate("DELETE FROM " + getSchemaPrefix() + tableName + " WHERE ObjectKey=?",
                           new CoalesceParameter(objectKey, Types.OTHER));

        return true;
    }

    /**
     * Returns the Coalesce entity keys that matches the given parameters.
     *
     * @param entityId of the entity.
     * @param entityIdType of the entity.
     * @param entityName of the entity.
     * @return List<String> of primary keys for the matching Coalesce entity.
     * @throws SQLException ,Exception,CoalescePersistorException
     */
    private List<String> getCoalesceEntityKeysForEntityId(String entityId, String entityIdType, String entityName)
            throws Exception
    {
        List<String> keyList = new ArrayList<String>();

        try (CoalesceDataConnectorBase conn = new PostGreSQLDataConnector(getConnectionSettings(), getSchemaPrefix()))
        {
            ResultSet results = conn.executeLikeQuery("SELECT ObjectKey FROM "
                                                              + getSchemaPrefix()
                                                              + "CoalesceEntity WHERE (EntityId like ?) AND (EntityIdType like ?) AND (Name=?)",
                                                      2,
                                                      new CoalesceParameter(entityId),
                                                      new CoalesceParameter(entityIdType),
                                                      new CoalesceParameter(entityName));

            while (results.next())
            {
                keyList.add(results.getString("ObjectKey"));
            }

            return keyList;
        }

    }

    /**
     * Returns the Coalesce entity keys that matches the given parameters.
     *
     * @param entityId of the entity.
     * @param entityIdType of the entity.
     * @param entityName of the entity.
     * @param entitySource of the entity.
     * @return List<String> of primary keys for the matching Coalesce entity.
     * @throws SQLException ,Exception,CoalescePersistorException
     */
    private List<String> getCoalesceEntityKeysForEntityIdAndSource(String entityId,
                                                                   String entityIdType,
                                                                   String entityName,
                                                                   String entitySource) throws Exception
    {

        try (CoalesceDataConnectorBase conn = new PostGreSQLDataConnector(getConnectionSettings(), getSchemaPrefix()))
        {
            List<String> keyList = new ArrayList<String>();

            ResultSet results = conn.executeLikeQuery("SELECT ObjectKey FROM "
                                                              + getSchemaPrefix()
                                                              + "CoalesceEntity WHERE (EntityId like ? ) AND (EntityIdType like  ? ) AND (Name=?) AND (Source=?)",
                                                      2,
                                                      new CoalesceParameter(entityId),
                                                      new CoalesceParameter(entityIdType),
                                                      new CoalesceParameter(entityName),
                                                      new CoalesceParameter(entitySource));

            while (results.next())
            {
                keyList.add(results.getString("ObjectKey"));
            }

            return keyList;
        }
    }

    /**
     * Sets the active Coalesce field objects matching the parameters given.
     *
     * @param coalesceObject the Coalesce field object.
     * @param conn is the PostGresDataConnector database connection
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

    private boolean updateCoalesceObject(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn, boolean allowRemoval)
            throws SQLException

    {
        boolean isSuccessful = false;
        boolean isDeleted = false; 
        
        if (coalesceObject.isFlatten())
        {
            switch (coalesceObject.getStatus()) {
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

    private DateTime getCoalesceObjectLastModified(String key, String objectType, CoalesceDataConnectorBase conn)
            throws SQLException
    {
        DateTime lastModified = null;

        // Determine the Table Name
        String tableName = CoalesceTableHelper.getTableNameForObjectType(objectType);
        String dateValue = null;

        ResultSet results = conn.executeQuery("SELECT LastModified FROM " + getSchemaPrefix() + tableName
                + " WHERE ObjectKey=?", new CoalesceParameter(key.trim(), Types.OTHER));
        ResultSetMetaData resultsmd = results.getMetaData();

        // JODA Function DateTimeFormat will adjust for the Server timezone when
        // converting the time.
        if (resultsmd.getColumnCount() <= 1)
        {
            while (results.next())
            {
                dateValue = results.getString("LastModified");
                if (dateValue != null)
                {
                    lastModified = JodaDateTimeHelper.getPostGresDateTim(dateValue);
                }
            }
        }
        return lastModified;

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
            sql = "SELECT name FROM ".concat(getSchemaPrefix()).concat(tableName).concat(" WHERE ObjectKey=?");
        }
        else
        {
            sql = "SELECT name, ParentKey, ParentType FROM ".concat(getSchemaPrefix()).concat(tableName).concat(" WHERE ObjectKey=?");
        }

        ResultSet results = conn.executeQuery(sql, new CoalesceParameter(key.trim(), Types.OTHER));

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
                                                                     EPersistorCapabilities.SUPPORTS_BLOB,
                                                                     EPersistorCapabilities.GEOSPATIAL_SEARCH,
                                                                     EPersistorCapabilities.TEMPORAL_SEARCH,
                                                                     EPersistorCapabilities.INDEX_FIELDS);
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
}
