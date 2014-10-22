package com.incadencecorp.coalesce.framework.persistance.sqlserver;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.CoalesceTableHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.runtime.CoalesceSettings;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldHistory;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceDataObjectStatus;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
import com.incadencecorp.coalesce.framework.persistance.CoalescePersistorBase;
import com.incadencecorp.coalesce.framework.persistance.ICoalesceCacher;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;

/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

 Notwithstanding any contractor copyright notice, the Government has Unlimited
 Rights in work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 of work other than as specifically authorized by these DFARS Clauses may
 violate Government rights in work.

 DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 Unlimited Rights. The Government has the right to use, modify, reproduce,
 perform, display, release or disclose computer software and to have or
 authorize others to do so.

 Distribution Statement D. Distribution authorized to the Department of
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 -----------------------------------------------------------------------------*/

@Repository("SQLServerPersistor")
public class SQLServerPersistor extends CoalescePersistorBase {

    public SQLServerPersistor()
    {
        _serCon = new ServerConn();
    }

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private ServerConn _serCon;

    /*--------------------------------------------------------------------------
    Constructor / Initializers
    --------------------------------------------------------------------------*/
    /**
     * Sets the server connection.
     * 
     * @param server connection object.
     */
    public void Initialize(ServerConn svConn)
    {
        _serCon = svConn;
    }

    /**
     * Sets the cacher and server connection.
     * 
     * @param base class cacher.
     * @param server connection object.
     */
    public boolean Initialize(ICoalesceCacher cacher, ServerConn svConn) throws CoalescePersistorException
    {
        _serCon = svConn;

        return super.initialize(cacher);
    }

    /*--------------------------------------------------------------------------
    Overrided Functions
    --------------------------------------------------------------------------*/

    @Override
    public List<String> getCoalesceEntityKeysForEntityId(String EntityId,
                                                         String EntityIdType,
                                                         String EntityName,
                                                         String EntitySource) throws CoalescePersistorException
    {
        try
        {
            if (EntitySource != null && EntitySource != "")
            {
                return getCoalesceEntityKeysForEntityIdAndSource(EntityId, EntityIdType, EntityName, EntitySource);
            }
            else
            {
                return getCoalesceEntityKeysForEntityId(EntityId, EntityIdType, EntityName);
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

    @Override
    public EntityMetaData getCoalesceEntityIdAndTypeForKey(String Key) throws CoalescePersistorException
    {
        try (SQLServerDataConnector conn = new SQLServerDataConnector(_serCon))
        {
            return getCoalesceEntityIdAndTypeForKey(Key, conn);
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

    @Override
    public DateTime getCoalesceDataObjectLastModified(String Key, String ObjectType) throws CoalescePersistorException
    {
        try (SQLServerDataConnector conn = new SQLServerDataConnector(_serCon))
        {
            return getCoalesceDataObjectLastModified(Key, ObjectType, conn);
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("GetCoalesceDataObjectLastModified", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetCoalesceDataObjectLastModified", e);
        }
    }

    @Override
    public byte[] getBinaryArray(String key) throws CoalescePersistorException
    {
        try (SQLServerDataConnector conn = new SQLServerDataConnector(_serCon))
        {

            byte[] binaryArray = null;

            ResultSet results = conn.executeQuery("SELECT BinaryObject FROM CoalesceFieldBinaryData WHERE ObjectKey=?",
                                                  new CoalesceParameter(key));

            // Get Results
            while (results.next() && binaryArray == null)
            {
                Blob dataVal = (Blob) results.getBlob("BinaryObject");
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
    public boolean persistEntityTemplate(CoalesceEntityTemplate template) throws CoalescePersistorException
    {
        try (SQLServerDataConnector conn = new SQLServerDataConnector(_serCon))
        {
            // Always persist template
            return conn.executeProcedure("CoalesceEntityTemplate_InsertOrUpdate",
                                         new CoalesceParameter(UUID.randomUUID().toString()),
                                         new CoalesceParameter(template.getName()),
                                         new CoalesceParameter(template.getSource()),
                                         new CoalesceParameter(template.getVersion()),
                                         new CoalesceParameter(template.toXml("UTF-16")),
                                         new CoalesceParameter(JodaDateTimeHelper.nowInUtc()),
                                         new CoalesceParameter(JodaDateTimeHelper.nowInUtc()));
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("PersistEntityTemplate", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("PersistEntityTemplate", e);
        }
    }

    @Override
    public ElementMetaData getXPath(String Key, String ObjectType) throws CoalescePersistorException
    {
        try (SQLServerDataConnector conn = new SQLServerDataConnector(_serCon))
        {
            return getXPathRecursive(Key, ObjectType, "", conn);
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

    @Override
    public String getFieldValue(String FieldKey) throws CoalescePersistorException
    {
        try (SQLServerDataConnector conn = new SQLServerDataConnector(_serCon))
        {
            String value = null;

            ResultSet results = conn.executeQuery("SELECT value FROM CoalesceField WHERE ObjectKey =?",
                                                  new CoalesceParameter(FieldKey));

            while (results.next() && value == null)
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
    public String getEntityXml(String Key) throws CoalescePersistorException
    {
        try (SQLServerDataConnector conn = new SQLServerDataConnector(_serCon))
        {
            String value = null;

            ResultSet results = conn.executeQuery("SELECT EntityXml from CoalesceEntity WHERE ObjectKey=?",
                                                  new CoalesceParameter(Key));

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
    public String getEntityXml(String EntityId, String EntityIdType) throws CoalescePersistorException
    {
        try (SQLServerDataConnector conn = new SQLServerDataConnector(_serCon))
        {
            String value = null;

            ResultSet results = conn.executeQuery("SELECT EntityXml from CoalesceEntity WHERE EntityId=? AND EntityIdType=?",
                                                  new CoalesceParameter(EntityId),
                                                  new CoalesceParameter(EntityIdType));

            while (results.next() && value == null)
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
    public String getEntityXml(String Name, String EntityId, String EntityIdType) throws CoalescePersistorException
    {
        try (SQLServerDataConnector conn = new SQLServerDataConnector(_serCon))
        {
            String value = null;

            ResultSet results = conn.executeQuery("SELECT EntityXml from CoalesceEntity WHERE Name=? AND EntityId=? AND EntityIdType=?",
                                                  new CoalesceParameter(Name),
                                                  new CoalesceParameter(EntityId),
                                                  new CoalesceParameter(EntityIdType));

            while (results.next() && value == null)
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

    /*
     * @Override public String[] GetEntityXmlAsStrings(String EntityId, String EntityIdType) {
     * 
     * String[] crst = null; try { String sqlStmt = "SELECT ObjectKey from CoalesceEntity WHERE EntityId=?"; sqlStmt +=
     * " AND EntityIdType=?"; ArrayList<String> params = new ArrayList<>(); params.add(EntityId.trim());
     * params.add(EntityIdType.trim()); crst = getEntityKeysFromSQL(sqlStmt, params); return crst; } catch (Exception ex) {
     * CallResult.log(CallResults.FAILED_ERROR, ex, "getEntityKeys"); return crst; } }
     * 
     * private XsdEntity getXSDEntityXML(String sqlStmt, ArrayList<String> sqlParams) throws Exception { XsdEntity crst = new
     * XsdEntity(); Connection conn = null; Statement stx = null; try { Class.forName("com.mysql.jdbc.Driver"); conn =
     * DriverManager.getConnection(serCon.getURL(), serCon.getUser(), serCon.getPassword()); stx = conn.createStatement();
     * java.sql.PreparedStatement sql;
     * 
     * sql = conn.prepareStatement(sqlStmt); Object[] pRams = sqlParams.toArray();
     * 
     * for (int iSetter = 0; iSetter < sqlParams.size(); iSetter++) { sql.setString(iSetter + 1, pRams[iSetter].toString());
     * } ResultSet srs = sql.executeQuery(); ResultSetMetaData rsmd = srs.getMetaData(); if (rsmd.getColumnCount() <= 1) {
     * while (srs.next()) { crst.Initialize(srs.getString("EntityXml")); } } return crst; } finally { stx.close();
     * conn.close(); } }
     */

    @Override
    protected boolean flattenObject(CoalesceEntity entity, boolean AllowRemoval) throws CoalescePersistorException
    {
        boolean isSuccessful = false;

        // Create a Database Connection
        try (SQLServerDataConnector conn = new SQLServerDataConnector(_serCon))
        {

            // Persist (Recursively)
            isSuccessful = updateDataObject(entity, conn, AllowRemoval);

            // Persist Entity Last to Include Changes
            if (isSuccessful)
            {
                isSuccessful = persistEntityObject(entity, conn);
            }

        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("FlattenObject", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("FlattenObject", e);
        }
        return isSuccessful;
    }

    @Override
    protected boolean flattenCore(CoalesceEntity entity, boolean AllowRemoval) throws CoalescePersistorException
    {
        boolean isSuccessful = false;

        // Create a Database Connection
        try (SQLServerDataConnector conn = new SQLServerDataConnector(_serCon))
        {

            conn.openConnection();

            if (persistEntityObject(entity, conn))
            {

                isSuccessful = updateFileContent(entity, conn);

            }

        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("FlattenCore", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("FlattenCore", e);
        }

        return isSuccessful;
    }

    @Override
    public String getEntityTemplateKey(String Name, String Source, String Version) throws CoalescePersistorException
    {
        try (SQLServerDataConnector conn = new SQLServerDataConnector(_serCon))
        {
            String value = null;

            ResultSet results = conn.executeQuery("SELECT TemplateKey FROM CoalesceEntityTemplate WHERE Name=? and Source=? and Version=?",
                                                  new CoalesceParameter(Name),
                                                  new CoalesceParameter(Source),
                                                  new CoalesceParameter(Version));

            while (results.next() && value == null)
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
            throw new CoalescePersistorException("GetEntityTemplateXml", e);
        }
    }

    @Override
    public String getEntityTemplateMetadata() throws CoalescePersistorException
    {
        try (SQLServerDataConnector conn = new SQLServerDataConnector(_serCon))
        {
            return conn.getTemplateMetaData("SELECT * FROM CoalesceEntityTemplate");
        }
        catch (SQLException ex)
        {
            throw new CoalescePersistorException("getEntityTemplateMetadata", ex);
        }
        catch (Exception ex)
        {
            throw new CoalescePersistorException("getEntityTemplateMetadata", ex);
        }
    }

    @Override
    public String getEntityTemplateXml(String Key) throws CoalescePersistorException
    {
        try (SQLServerDataConnector conn = new SQLServerDataConnector(_serCon))
        {
            String value = null;

            ResultSet results = conn.executeQuery("SELECT TemplateXml FROM CoalesceEntityTemplate WHERE TemplateKey=?",
                                                  new CoalesceParameter(Key));

            while (results.next())
            {
                value = results.getString("TemplateXml");
            }

            return value;
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("GetEntityTemplateXml", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetEntityTemplateXml", e);
        }
    }

    @Override
    public String getEntityTemplateXml(String Name, String Source, String Version) throws CoalescePersistorException
    {
        try (SQLServerDataConnector conn = new SQLServerDataConnector(_serCon))
        {
            String value = null;

            ResultSet results = conn.executeQuery("SELECT TemplateXml FROM CoalesceEntityTemplate WHERE Name=? and Source=? and Version=?",
                                                  new CoalesceParameter(Name),
                                                  new CoalesceParameter(Source),
                                                  new CoalesceParameter(Version));

            while (results.next())
            {
                value = results.getString("TemplateXml");
            }

            return value;
        }
        catch (SQLException e)
        {
            throw new CoalescePersistorException("GetEntityTemplateXml", e);
        }
        catch (Exception e)
        {
            throw new CoalescePersistorException("GetEntityTemplateXml", e);
        }
    }

    /*--------------------------------------------------------------------------
    Protected Functions
    --------------------------------------------------------------------------*/
    /**
     * Adds or Updates a Coalesce object that matches the given parameters.
     * 
     * @param dataObject the XsdDataObject to be added or updated
     * @param conn is the SQLServerDataConnector database connection
     * @return isSuccessful = True = Successful add/update operation.
     * @throws SQLException
     */
    protected boolean persistObject(CoalesceDataObject dataObject, SQLServerDataConnector conn) throws SQLException
    {
        boolean isSuccessful = true;

        switch (dataObject.getType()) {
        case "entity":

            isSuccessful = checkLastModified(dataObject, conn);
            // isSuccessful = PersistEntityObject(dataObject);
            break;

        case "section":
            if (CoalesceSettings.getUseIndexing())
            {
                isSuccessful = persistSectionObject((CoalesceSection) dataObject, conn);
            }
            break;

        case "recordset":
            if (CoalesceSettings.getUseIndexing())
            {
                isSuccessful = persistRecordsetObject((CoalesceRecordset) dataObject, conn);
            }
            break;
        case "fielddefinition":
            //if (CoalesceSettings.getUseIndexing())
            //{
                // Removed Field Definition Persisting
                // isSuccessful = PersistFieldDefinitionObject((XsdFieldDefinition) dataObject, conn);
            //}
            break;

        case "record":
            if (CoalesceSettings.getUseIndexing())
            {
                isSuccessful = persistRecordObject((CoalesceRecord) dataObject, conn);
            }
            break;

        case "field":// Not testing the type to ascertain if it is BINARY now.
            if (CoalesceSettings.getUseIndexing())
            {
                isSuccessful = persistFieldObject((CoalesceField<?>) dataObject, conn);
            }
            break;

        case "fieldhistory":
            if (CoalesceSettings.getUseIndexing())
            {
                isSuccessful = persistFieldHistoryObject((CoalesceFieldHistory) dataObject, conn);
            }
            break;

        case "linkagesection":
            if (CoalesceSettings.getUseIndexing())
            {
                isSuccessful = persistLinkageSectionObject((CoalesceLinkageSection) dataObject, conn);
            }
            break;

        case "linkage":
            if (CoalesceSettings.getUseIndexing())
            {
                isSuccessful = persistLinkageObject((CoalesceLinkage) dataObject, conn);
            }
            break;

        default:
            isSuccessful = false;
        }

        if (isSuccessful && CoalesceSettings.getUseIndexing())
        {
            // Persist Map Table Entry
            isSuccessful = persistMapTableEntry(dataObject, conn);
        }

        return isSuccessful;
    }

    /**
     * Adds or updates map table entry for a given element.
     * 
     * @param dataObject the XsdDataObject to be added or updated
     * @param conn is the SQLServerDataConnector database connection
     * @return True if successfully added/updated.
     * @throws SQLException
     */
    protected boolean persistMapTableEntry(CoalesceDataObject dataObject, SQLServerDataConnector conn) throws SQLException
    {
        String parentKey;
        String parentType;

        if (dataObject.getParent() != null)
        {
            parentKey = dataObject.getParent().getKey();
            parentType = dataObject.getParent().getType();
        }
        else
        {
            parentKey = "00000000-0000-0000-0000-000000000000";
            parentType = "";
        }

        return conn.executeProcedure("CoalesceObjectMap_Insert",
                                     new CoalesceParameter(parentKey),
                                     new CoalesceParameter(parentType),
                                     new CoalesceParameter(dataObject.getKey()),
                                     new CoalesceParameter(dataObject.getType()));
    }

    /**
     * Adds or Updates a Coalesce entity that matches the given parameters.
     * 
     * @param entity the XsdEntity to be added or updated
     * @param conn is the SQLServerDataConnector database connection
     * @return True = No Update required.
     * @return True = Successful add/update operation.
     * @throws SQLException
     */
    protected boolean persistEntityObject(CoalesceEntity entity, SQLServerDataConnector conn) throws SQLException
    {
        // Return true if no update is required.
        if (!checkLastModified(entity, conn)) return true;

        // Yes; Call Store Procedure
        return conn.executeProcedure("CoalesceEntity_InsertOrUpdate",
                                     new CoalesceParameter(entity.getKey()),
                                     new CoalesceParameter(entity.getName()),
                                     new CoalesceParameter(entity.getSource()),
                                     new CoalesceParameter(entity.getVersion()),
                                     new CoalesceParameter(entity.getEntityId()),
                                     new CoalesceParameter(entity.getEntityIdType()),
                                     new CoalesceParameter(entity.toXml("UTF-16")),
                                     new CoalesceParameter(entity.getDateCreated()),
                                     new CoalesceParameter(entity.getLastModified()));
    }

    /**
     * Adds or Updates a Coalesce section that matches the given parameters.
     * 
     * @param section the XsdSection to be added or updated
     * @param conn is the SQLServerDataConnector database connection
     * @return True = No Update required.
     * @return True = Successful add/update operation.
     * @throws SQLException
     */
    protected boolean persistSectionObject(CoalesceSection section, SQLServerDataConnector conn) throws SQLException
    {
        // Return true if no update is required.
        if (!checkLastModified(section, conn)) return true;

        // Yes; Call Store Procedure
        return conn.executeProcedure("CoalesceSection_InsertOrUpdate",
                                     new CoalesceParameter(section.getKey()),
                                     new CoalesceParameter(section.getName()),
                                     new CoalesceParameter(section.getParent().getKey()),
                                     new CoalesceParameter(section.getParent().getType()),
                                     new CoalesceParameter(section.getLastModified()),
                                     new CoalesceParameter(section.getDateCreated()));

    }

    /**
     * Adds or Updates a Coalesce recordset that matches the given parameters.
     * 
     * @param recordset the XsdRecordset to be added or updated
     * @param conn is the SQLServerDataConnector database connection
     * @return True = No Update required.
     * @return True = Successful add/update operation.
     * @throws SQLException
     */
    protected boolean persistRecordsetObject(CoalesceRecordset recordset, SQLServerDataConnector conn) throws SQLException
    {
        // Return true if no update is required.
        if (!checkLastModified(recordset, conn)) return true;

        // Yes; Call Store Procedure
        return conn.executeProcedure("CoalesceRecordset_InsertOrUpdate",
                                     new CoalesceParameter(recordset.getKey()),
                                     new CoalesceParameter(recordset.getName()),
                                     new CoalesceParameter(recordset.getParent().getKey()),
                                     new CoalesceParameter(recordset.getParent().getType()),
                                     new CoalesceParameter(recordset.getDateCreated()),
                                     new CoalesceParameter(recordset.getLastModified()));
    }

    /**
     * Adds or Updates a Coalesce field definition that matches the given parameters.
     * 
     * @param fieldDefinition the XsdFieldDefinition to be added or updated
     * @param conn is the SQLServerDataConnector database connection
     * @return True = No Update required.
     * @return True = Successful add/update operation.
     * @throws SQLException
     */
    protected boolean persistFieldDefinitionObject(CoalesceFieldDefinition fieldDefinition, SQLServerDataConnector conn)
            throws SQLException
    {

        // Return true if no update is required.
        if (!checkLastModified(fieldDefinition, conn)) return true;

        // Yes; Call Store Procedure
        return conn.executeProcedure("CoalesceFieldDefinition_InsertOrUpdate",
                                     new CoalesceParameter(fieldDefinition.getKey()),
                                     new CoalesceParameter(fieldDefinition.getName()),
                                     new CoalesceParameter(fieldDefinition.getParent().getKey()),
                                     new CoalesceParameter(fieldDefinition.getParent().getType()),
                                     new CoalesceParameter(fieldDefinition.getDateCreated()),
                                     new CoalesceParameter(fieldDefinition.getLastModified()));
    }

    /**
     * Adds or Updates a Coalesce record that matches the given parameters.
     * 
     * @param record the XsdRecord to be added or updated
     * @param conn is the SQLServerDataConnector database connection
     * @return True = No Update required.
     * @return True = Successful add/update operation.
     * @throws SQLException
     */
    protected boolean persistRecordObject(CoalesceRecord record, SQLServerDataConnector conn) throws SQLException
    {
        // Return true if no update is required.
        if (!checkLastModified(record, conn)) return true;

        // Yes; Call Store Procedure
        return conn.executeProcedure("CoalesceRecord_InsertOrUpdate",
                                     new CoalesceParameter(record.getKey()),
                                     new CoalesceParameter(record.getName()),
                                     new CoalesceParameter(record.getParent().getKey()),
                                     new CoalesceParameter(record.getParent().getType()),
                                     new CoalesceParameter(record.getDateCreated()),
                                     new CoalesceParameter(record.getLastModified()));
    }

    /**
     * Adds or Updates a Coalesce field that matches the given parameters.
     * 
     * @param field the XsdField to be added or updated
     * @param conn is the SQLServerDataConnector database connection
     * @return True = No Update required.
     * @return True = Successful add/update operation.
     * @throws SQLException
     */
    protected boolean persistFieldObject(CoalesceField<?> field, SQLServerDataConnector conn) throws SQLException
    {
        // Return true if no update is required.
        if (!checkLastModified(field, conn)) return true;

        // Yes; Call Store Procedure
        return conn.executeProcedure("CoalesceField_InsertOrUpdate",
                                     new CoalesceParameter(field.getKey()),
                                     new CoalesceParameter(field.getName()),
                                     new CoalesceParameter(field.getBaseValue()),
                                     new CoalesceParameter(field.getDataType().getLabel()),
                                     new CoalesceParameter(""),
                                     new CoalesceParameter(field.getClassificationMarkingAsString()),
                                     new CoalesceParameter(field.getModifiedBy()),
                                     new CoalesceParameter(field.getParent().getKey()),
                                     new CoalesceParameter(field.getParent().getType()),
                                     new CoalesceParameter(field.getDateCreated()),
                                     new CoalesceParameter(field.getLastModified()),
                                     new CoalesceParameter(field.getPreviousHistoryKey()));
    }

    /**
     * Adds or Updates a Coalesce field history that matches the given parameters.
     * 
     * @param fieldHistory the XsdFieldHistory to be added or updated
     * @param conn is the SQLServerDataConnector database connection
     * @return True = No Update required.
     * @return True = Successful add/update operation.
     * @throws SQLException
     */
    protected boolean persistFieldHistoryObject(CoalesceFieldHistory fieldHistory, SQLServerDataConnector conn)
            throws SQLException
    {
        // Return true if no update is required.
        if (!checkLastModified(fieldHistory, conn)) return true;

        // Yes; Call Store Procedure
        return conn.executeProcedure("CoalesceFieldHistory_InsertOrUpdate",
                                     new CoalesceParameter(fieldHistory.getKey()),
                                     new CoalesceParameter(fieldHistory.getName()),
                                     new CoalesceParameter(fieldHistory.getValue()),
                                     new CoalesceParameter(fieldHistory.getDataType().getLabel()),
                                     new CoalesceParameter(""),
                                     new CoalesceParameter(fieldHistory.getClassificationMarkingAsString()),
                                     new CoalesceParameter(fieldHistory.getModifiedBy()),
                                     new CoalesceParameter(fieldHistory.getParent().getKey()),
                                     new CoalesceParameter(fieldHistory.getParent().getType()),
                                     new CoalesceParameter(fieldHistory.getDateCreated()),
                                     new CoalesceParameter(fieldHistory.getLastModified()),
                                     new CoalesceParameter(fieldHistory.getPreviousHistoryKey()));
    }

    /**
     * Adds or Updates a Coalesce linkage section that matches the given parameters.
     * 
     * @param linkageSection the XsdLinkageSection to be added or updated
     * @param conn is the SQLServerDataConnector database connection
     * @return True = No Update required.
     * @return True = Successful add/update operation.
     * @throws SQLException
     */
    protected boolean persistLinkageSectionObject(CoalesceLinkageSection linkageSection, SQLServerDataConnector conn)
            throws SQLException
    {
        // Return true if no update is required.
        if (!checkLastModified(linkageSection, conn)) return true;

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
     * @param conn is the SQLServerDataConnector database connection
     * @return True = No Update required.
     * @return True = Successful add/update operation.
     * @throws SQLException
     */
    protected boolean persistLinkageObject(CoalesceLinkage linkage, SQLServerDataConnector conn) throws SQLException
    {
        // Return true if no update is required.
        if (!checkLastModified(linkage, conn)) return true;

        // Yes; Call Store Procedure
        return conn.executeProcedure("CoalesceLinkage_InsertOrUpdate",
                                     new CoalesceParameter(linkage.getKey()),
                                     new CoalesceParameter(linkage.getName()),
                                     new CoalesceParameter(linkage.getEntity1Key()),
                                     new CoalesceParameter(linkage.getEntity1Name()),
                                     new CoalesceParameter(linkage.getEntity1Source()),
                                     new CoalesceParameter(linkage.getEntity1Version()),
                                     new CoalesceParameter(linkage.getLinkType().getLabel()),
                                     new CoalesceParameter(linkage.getStatus().getLabel()),
                                     new CoalesceParameter(linkage.getEntity2Key()),
                                     new CoalesceParameter(linkage.getEntity2Name()),
                                     new CoalesceParameter(linkage.getEntity2Source()),
                                     new CoalesceParameter(linkage.getEntity2Version()),
                                     new CoalesceParameter(linkage.getClassificationMarking().toPortionString()),
                                     new CoalesceParameter(linkage.getModifiedBy()),
                                     new CoalesceParameter(""),
                                     new CoalesceParameter(linkage.getParent().getKey()),
                                     new CoalesceParameter(linkage.getParent().getType()),
                                     new CoalesceParameter(linkage.getDateCreated()),
                                     new CoalesceParameter(linkage.getLastModified()));
    }

    /**
     * Returns the EntityMetaData for the Coalesce entity that matches the given parameters.
     * 
     * @param Key primary key of the Coalesce entity
     * @param conn is the SQLServerDataConnector database connection
     * @return metaData the EntityMetaData for the Coalesce entity.
     * @throws SQLException
     */
    protected EntityMetaData getCoalesceEntityIdAndTypeForKey(String Key, SQLServerDataConnector conn) throws SQLException
    {
        EntityMetaData metaData = null;

        // Execute Query
        ResultSet results = conn.executeQuery("SELECT EntityId,EntityIdType,ObjectKey FROM CoalesceEntity WHERE ObjectKey=?",
                                              new CoalesceParameter(Key));
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
     * Returns the rounded milliseconds.
     * 
     * @param Ticks time in milliseconds to be rounded up
     * @return Ticks rounded up time in milliseconds.
     */
    protected long roundTicksForSQL(long Ticks)
    {
        int iTick = (int) (Ticks % 10);

        switch (iTick) {
        case 0:
        case 3:
        case 7:
            break;
        case 1:
        case 4:
        case 8:
            Ticks = Ticks - 1;
            break;
        case 2:
        case 6:
        case 9:
            Ticks = Ticks + 1;
            break;
        case 5:
            Ticks = Ticks + 2;
            break;
        }
        return Ticks;
    }

    /**
     * Returns the comparison for the XsdDataObject last modified date versus the same objects value in the database.
     * 
     * @param dataObject the XsdDataObject to have it's last modified date checked.
     * @param conn is the SQLServerDataConnector database connection
     * @return False = Out of Date
     * @throws SQLException
     */
    protected boolean checkLastModified(CoalesceDataObject dataObject, SQLServerDataConnector conn) throws SQLException
    {
        boolean isOutOfDate = true;

        // Get LastModified from the Database
        DateTime LastModified = getCoalesceDataObjectLastModified(dataObject.getKey(), dataObject.getType(), conn);

        // DB Has Valid Time?
        if (LastModified != null)
        {
            // Remove NanoSeconds (100 ns / Tick and 1,000,000 ns / ms = 10,000 Ticks / ms)
            long ObjectTicks = dataObject.getLastModified().getMillis();
            long SQLRecordTicks = LastModified.getMillis();

            // TODO: Round Ticks for SQL (Not sure if is required for .NET)
            ObjectTicks = roundTicksForSQL(ObjectTicks);

            if (ObjectTicks == SQLRecordTicks)
            {
                // They're equal; No Update Required
                isOutOfDate = false;
            }
        }

        return isOutOfDate;
    }

    /**
     * Deletes the Coalesce object & CoalesceObjectMap that matches the given parameters.
     * 
     * @param dataObject the XsdDataObject to be deleted
     * @param conn is the SQLServerDataConnector database connection
     * @return True = Successful delete
     * @throws SQLException
     */
    protected boolean deleteObject(CoalesceDataObject dataObject, SQLServerDataConnector conn) throws SQLException
    {
        String objectType = dataObject.getType();
        String objectKey = dataObject.getKey();
        String tableName = CoalesceTableHelper.getTableNameForObjectType(objectType);

        conn.executeCmd("DELETE FROM CoalesceObjectMap WHERE ObjectKey=?", new CoalesceParameter(objectKey));
        conn.executeCmd("DELETE FROM " + tableName + " WHERE ObjectKey=?", new CoalesceParameter(objectKey));

        return true;
    }

    /**
     * Returns the Coalesce entity keys that matches the given parameters.
     * 
     * @param EntityId of the entity.
     * @param EntityIdType of the entity.
     * @param EntityName of the entity.
     * @return List<String> of primary keys for the matching Coalesce entity.
     * @throws SQLException,Exception,CoalescePersistorException
     */
    private List<String> getCoalesceEntityKeysForEntityId(String EntityId, String EntityIdType, String EntityName)
            throws Exception
    {
        List<String> keyList = new ArrayList<String>();

        try (SQLServerDataConnector conn = new SQLServerDataConnector(_serCon))
        {
            ResultSet results = conn.executeQuery("SELECT ObjectKey FROM CoalesceEntity WHERE (ISNULL(EntityId,' ') like ? ) AND (ISNULL(EntityIdType,' ') like  ? ) AND Name=?",
                                                  new CoalesceParameter(EntityId),
                                                  new CoalesceParameter(EntityIdType),
                                                  new CoalesceParameter(EntityName));

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
     * @param EntityId of the entity.
     * @param EntityIdType of the entity.
     * @param EntityName of the entity.
     * @param EntitySource of the entity.
     * @return List<String> of primary keys for the matching Coalesce entity.
     * @throws SQLException,Exception,CoalescePersistorException
     */
    private List<String> getCoalesceEntityKeysForEntityIdAndSource(String EntityId,
                                                                   String EntityIdType,
                                                                   String EntityName,
                                                                   String EntitySource) throws Exception
    {
        try (SQLServerDataConnector conn = new SQLServerDataConnector(_serCon))
        {
            List<String> keyList = new ArrayList<String>();

            ResultSet results = conn.executeQuery("SELECT ObjectKey FROM CoalesceEntity WHERE (ISNULL(EntityId,' ')  like  ? ) AND (ISNULL(EntityIdType,' ')  like  ? ) AND (Name=?) AND (Source=?)",
                                                  new CoalesceParameter(EntityId),
                                                  new CoalesceParameter(EntityIdType),
                                                  new CoalesceParameter(EntityName),
                                                  new CoalesceParameter(EntitySource));

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
     * @param dataObject the Coalesce field object.
     * @param conn is the SQLServerDataConnector database connection
     * @throws SQLException,Exception,CoalescePersistorException
     */
    protected boolean updateFileContent(CoalesceDataObject dataObject, SQLServerDataConnector conn) throws SQLException
    {
        boolean isSuccessful = false;

        if (dataObject.getStatus() == ECoalesceDataObjectStatus.ACTIVE)
        {
            if (dataObject.getType().toLowerCase() == "field")
            {
                if (((CoalesceField<?>) dataObject).getDataType() == ECoalesceFieldDataTypes.FILE_TYPE)
                {
                    isSuccessful = persistFieldObject((CoalesceField<?>) dataObject, conn);
                }
            }

            for (Map.Entry<String, CoalesceDataObject> s : dataObject.getChildDataObjects().entrySet())
            {
                isSuccessful = updateFileContent(s.getValue(), conn);
            }
        }
        return isSuccessful;
    }

    private boolean updateDataObject(CoalesceDataObject coalesceDataObject, SQLServerDataConnector conn, boolean AllowRemoval)
            throws SQLException

    {
        boolean isSuccessful = false;

        if (coalesceDataObject.getFlatten())
        {
            switch (coalesceDataObject.getStatus()) {
            case ACTIVE:
                // Persist Object
                isSuccessful = persistObject(coalesceDataObject, conn);
                break;

            case DELETED:
                if (AllowRemoval)
                {
                    // Delete Object
                    isSuccessful = deleteObject(coalesceDataObject, conn);
                }
                else
                {
                    // Mark Object as Deleted
                    isSuccessful = persistObject(coalesceDataObject, conn);
                }

                break;

            default:
                isSuccessful = false;
            }

            // Successful?
            if (isSuccessful)
            {
                // Yes; Iterate Through Children
                for (CoalesceDataObject childObject : coalesceDataObject.getChildDataObjects().values())
                {
                    updateDataObject(childObject, conn, AllowRemoval);
                }
            }
        }
        return isSuccessful;
    }

    private DateTime getCoalesceDataObjectLastModified(String Key, String ObjectType, SQLServerDataConnector conn)
            throws SQLException
    {
        DateTime lastModified = null;

        // Determine the Table Name
        String tableName = CoalesceTableHelper.getTableNameForObjectType(ObjectType);
        String dateValue = null;

        ResultSet results = conn.executeQuery("SELECT LastModified FROM " + tableName + " WHERE ObjectKey=?",
                                              new CoalesceParameter(Key.trim()));
        // JODA Function DateTimeFormat will adjust for the Server timezone when converting the time.
        while (results.next())
        {
            dateValue = results.getString("LastModified");
            if (dateValue != null)
            {
                lastModified = JodaDateTimeHelper.getMySQLDateTime(dateValue);
            }

        }
        return lastModified;

    }

    private ElementMetaData getXPathRecursive(String key, String objectType, String xPath, SQLServerDataConnector conn)
            throws SQLException
    {

        boolean isEntityTable = false;
        ElementMetaData meteData = null;

        String sql = "";

        // Get Table Name
        String tableName = CoalesceTableHelper.getTableNameForObjectType(objectType);

        // Check to see if its the Entity Table
        if (tableName.equals("CoalesceEntity")) isEntityTable = true;

        if (isEntityTable)
        {
            sql = "SELECT name FROM ".concat(tableName).concat(" WHERE ObjectKey=?");
        }
        else
        {
            sql = "SELECT name, ParentKey, ParentType FROM ".concat(tableName).concat(" WHERE ObjectKey=?");
        }

        ResultSet results = conn.executeQuery(sql, new CoalesceParameter(key.trim()));

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

}
