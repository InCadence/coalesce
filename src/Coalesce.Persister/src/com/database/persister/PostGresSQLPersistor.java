package com.database.persister;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Repository;

import Coalesce.Common.Exceptions.CoalescePersistorException;
import Coalesce.Common.Helpers.JodaDateTimeHelper;
import Coalesce.Common.Runtime.CoalesceSettings;
import Coalesce.Framework.DataModel.CoalesceEntityTemplate;
import Coalesce.Framework.DataModel.ECoalesceDataObjectStatus;
import Coalesce.Framework.DataModel.ECoalesceFieldDataTypes;
import Coalesce.Framework.DataModel.XsdDataObject;
import Coalesce.Framework.DataModel.XsdEntity;
import Coalesce.Framework.DataModel.XsdField;
import Coalesce.Framework.DataModel.XsdFieldDefinition;
import Coalesce.Framework.DataModel.XsdFieldHistory;
import Coalesce.Framework.DataModel.XsdLinkage;
import Coalesce.Framework.DataModel.XsdLinkageSection;
import Coalesce.Framework.DataModel.XsdRecord;
import Coalesce.Framework.DataModel.XsdRecordset;
import Coalesce.Framework.DataModel.XsdSection;
import Coalesce.Framework.Persistance.CoalescePersisterBase;
import Coalesce.Framework.Persistance.ICoalesceCacher;

import com.mysql.jdbc.Blob;

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

@Repository("PostGresSQLPersistor")
public class PostGresSQLPersistor extends CoalescePersisterBase {

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private ServerConn serCon;

    /*--------------------------------------------------------------------------
    Constructor / Initializers
    --------------------------------------------------------------------------*/

    public PostGresSQLPersistor()
    {
        /***********
         * Define the PostGresSQL Database Connection in the URL, change to whatever the schema name is on your system
         ***********/
        serCon = new ServerConn();
        serCon.setPostGres(true);
        /* Set URL, User, Pass */
    }

    public void Initialize(ServerConn svConn)
    {
        serCon = svConn;
        serCon.setPostGres(true);
    }

    public void Initialize(String url, String userName, String pwd)
    {
        serCon.setURL(url);
        serCon.setPassword(pwd);
        serCon.setUser(userName);
        serCon.setPostGres(true);
    }

    public boolean Initialize(ICoalesceCacher cacher, ServerConn svConn) throws CoalescePersistorException
    {
        serCon = svConn;
        serCon.setPostGres(true);
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
                return this.getCoalesceEntityKeysForEntityIdAndSource(EntityId, EntityIdType, EntityName, EntitySource);
            }
            else
            {
                return this.getCoalesceEntityKeysForEntityId(EntityId, EntityIdType, EntityName);
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
        try (CoalesceDataConnector conn = new CoalesceDataConnector(this.serCon, ConnectionType.PostGresSQL))
        {
            return this.getCoalesceEntityIdAndTypeForKey(Key, conn);
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
        try (CoalesceDataConnector conn = new CoalesceDataConnector(this.serCon, ConnectionType.PostGresSQL))
        {
            return this.getCoalesceDataObjectLastModified(Key, ObjectType, conn);
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
        try (CoalesceDataConnector conn = new CoalesceDataConnector(this.serCon, ConnectionType.PostGresSQL))
        {

            byte[] binaryArray = null;

            ResultSet results = conn.ExecuteQuery("SELECT BinaryObject FROM CoalesceFieldBinaryData WHERE ObjectKey=?", key);

            // Get Results
            if (results != null && results.first())
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
        try (CoalesceDataConnector conn = new CoalesceDataConnector(this.serCon, ConnectionType.PostGresSQL))
        {
            // Always persist template
            return conn.ExecuteProcedure("CoalesceEntityTemplate_InsertOrUpdate",
                                         UUID.randomUUID().toString(),
                                         template.getName(),
                                         template.getSource(),
                                         template.getVersion(),
                                         template.toXml(),
                                         JodaDateTimeHelper.toMySQLDateTime(JodaDateTimeHelper.NowInUtc()),
                                         JodaDateTimeHelper.toMySQLDateTime(JodaDateTimeHelper.NowInUtc()));
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
        try (CoalesceDataConnector conn = new CoalesceDataConnector(this.serCon, ConnectionType.PostGresSQL))
        {
            return this.getXPathRecursive(Key, ObjectType, "", conn);
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
        try (CoalesceDataConnector conn = new CoalesceDataConnector(this.serCon, ConnectionType.PostGresSQL))
        {
            String value = null;

            ResultSet results = conn.ExecuteQuery("SELECT value FROM CoalesceField WHERE ObjectKey =?", FieldKey);

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
    public String getEntityXml(String Key) throws CoalescePersistorException
    {
        try (CoalesceDataConnector conn = new CoalesceDataConnector(this.serCon, ConnectionType.PostGresSQL))
        {
            String value = null;

            ResultSet results = conn.ExecuteQuery("SELECT EntityXml from CoalesceEntity WHERE ObjectKey=?", Key);

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
        try (CoalesceDataConnector conn = new CoalesceDataConnector(this.serCon, ConnectionType.PostGresSQL))
        {
            String value = null;

            ResultSet results = conn.ExecuteQuery("SELECT EntityXml from CoalesceEntity WHERE EntityId=? AND EntityIdType=?",
                                                  EntityId,
                                                  EntityIdType);

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
    public String getEntityXml(String Name, String EntityId, String EntityIdType) throws CoalescePersistorException
    {
        try (CoalesceDataConnector conn = new CoalesceDataConnector(this.serCon, ConnectionType.PostGresSQL))
        {
            String value = null;

            ResultSet results = conn.ExecuteQuery("SELECT EntityXml from CoalesceEntity WHERE Name=? AND EntityId=? AND EntityIdType=?",
                                                  Name,
                                                  EntityId,
                                                  EntityIdType);

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
    protected boolean FlattenObject(XsdEntity entity, boolean AllowRemoval) throws CoalescePersistorException
    {
        boolean isSuccessful = false;

        // Create a Database Connection
        try (CoalesceDataConnector conn = new CoalesceDataConnector(this.serCon, ConnectionType.PostGresSQL))
        {

            conn.OpenPSConnection();

            // Persist (Recursively)
            isSuccessful = this.updateDataObject(entity, conn, AllowRemoval);

            // Persist Entity Last to Include Changes
            switch (entity.getType().toLowerCase()) {
            case "entity":
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
    protected boolean FlattenCore(XsdEntity entity, boolean AllowRemoval) throws CoalescePersistorException
    {
        boolean isSuccessful = false;

        // Create a Database Connection
        try (CoalesceDataConnector conn = new CoalesceDataConnector(this.serCon, ConnectionType.PostGresSQL))
        {

            conn.OpenPSConnection();

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
        try (CoalesceDataConnector conn = new CoalesceDataConnector(this.serCon, ConnectionType.PostGresSQL))
        {
            String value = null;

            ResultSet results = conn.ExecuteQuery("SELECT TemplateKey FROM CoalesceEntityTemplate WHERE Name=? and Source=? and Version=?",
                                                  Name,
                                                  Source,
                                                  Version);

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
    public String getEntityTemplateMetadata()
    {
        // TODO - I need to make sure it cannot be done!!!
        // *************************************************************************************************************************************
        // SQL in the .NET version does not seem to be supported in MySQL - it uses
        // "FOR XML RAW, ROOT('coalesceentitytemplates')"
        //
        // This means: By specifying the ROOT option in the FOR XML query, you can request a single, top-level element for
        // the resulting XML, as shown in this query. The argument specified for the ROOT directive provides the root element
        // name. http://msdn.microsoft.com/en-us/library/bb522623.aspx
        // *************************************************************************************************************************************
        return null;
    }

    @Override
    public String getEntityTemplateXml(String Key) throws CoalescePersistorException
    {
        try (CoalesceDataConnector conn = new CoalesceDataConnector(this.serCon, ConnectionType.PostGresSQL))
        {
            String value = null;

            ResultSet results = conn.ExecuteQuery("SELECT TemplateXml FROM CoalesceEntityTemplate WHERE TemplateKey=?", Key);

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
        try (CoalesceDataConnector conn = new CoalesceDataConnector(this.serCon, ConnectionType.PostGresSQL))
        {
            String value = null;

            ResultSet results = conn.ExecuteQuery("SELECT TemplateXml FROM CoalesceEntityTemplate WHERE Name=? and Source=? and Version=?",
                                                  Name,
                                                  Source,
                                                  Version);

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

    protected boolean persistObject(XsdDataObject dataObject, CoalesceDataConnector conn) throws SQLException
    {
        boolean isSuccessful = true;

        switch (dataObject.getType()) {
        case "entity":

            isSuccessful = this.checkLastModified(dataObject, conn);
            // isSuccessful = PersistEntityObject(dataObject);
            break;

        case "section":
            if (CoalesceSettings.GetUseIndexing())
            {
                isSuccessful = persistSectionObject((XsdSection) dataObject, conn);
            }
            break;

        case "recordset":
            if (CoalesceSettings.GetUseIndexing())
            {
                isSuccessful = persistRecordsetObject((XsdRecordset) dataObject, conn);
            }
            break;
        case "fielddefinition":
            if (CoalesceSettings.GetUseIndexing())
            {
                // Removed Field Definition Persisting
                // isSuccessful = PersistFieldDefinitionObject((XsdFieldDefinition) dataObject, conn);
            }
            break;

        case "record":
            if (CoalesceSettings.GetUseIndexing())
            {
                isSuccessful = persistRecordObject((XsdRecord) dataObject, conn);
            }
            break;

        case "field":// Not testing the type to ascertain if it is BINARY now.
            if (CoalesceSettings.GetUseIndexing())
            {
                isSuccessful = persistFieldObject((XsdField) dataObject, conn);
            }
            break;

        case "fieldhistory":
            if (CoalesceSettings.GetUseIndexing())
            {
                isSuccessful = persistFieldHistoryObject((XsdFieldHistory) dataObject, conn);
            }
            break;

        case "linkagesection":
            if (CoalesceSettings.GetUseIndexing())
            {
                isSuccessful = persistLinkageSectionObject((XsdLinkageSection) dataObject, conn);
            }
            break;

        case "linkage":
            if (CoalesceSettings.GetUseIndexing())
            {
                isSuccessful = persistLinkageObject((XsdLinkage) dataObject, conn);
            }
            break;

        default:
            isSuccessful = false;
        }
        return isSuccessful;
    }

    protected boolean persistEntityObject(XsdEntity entity, CoalesceDataConnector conn) throws SQLException
    {
        // Return true if no update is required.
        if (!this.checkLastModified(entity, conn)) return true;

        // Yes; Call Store Procedure
        return conn.ExecuteProcedure("CoalesceEntity_InsertOrUpdate",
                                     entity.getKey(),
                                     entity.getName(),
                                     entity.getSource(),
                                     entity.getVersion(),
                                     entity.getEntityId(),
                                     entity.getEntityIdType(),
                                     entity.toXml(),
                                     JodaDateTimeHelper.toPostGestSQLDateTime(entity.getDateCreated()),
                                     JodaDateTimeHelper.toPostGestSQLDateTime(entity.getLastModified()));
    }

    protected boolean persistSectionObject(XsdSection section, CoalesceDataConnector conn) throws SQLException
    {
        // Return true if no update is required.
        if (!this.checkLastModified(section, conn)) return true;

        // Yes; Call Store Procedure
        return conn.ExecuteProcedure("CoalesceSection_InsertOrUpdate",
                                     section.getKey(),
                                     section.getName(),
                                     section.getParent().getKey(),
                                     section.getParent().getType(),
                                     JodaDateTimeHelper.toPostGestSQLDateTime(section.getDateCreated()),
                                     JodaDateTimeHelper.toPostGestSQLDateTime(section.getLastModified()));
    }

    protected boolean persistRecordsetObject(XsdRecordset recordset, CoalesceDataConnector conn) throws SQLException
    {
        // Return true if no update is required.
        if (!this.checkLastModified(recordset, conn)) return true;

        // Yes; Call Store Procedure
        return conn.ExecuteProcedure("CoalesceRecordset_InsertOrUpdate",
                                     recordset.getKey(),
                                     recordset.getName(),
                                     recordset.getParent().getKey(),
                                     recordset.getParent().getType(),
                                     JodaDateTimeHelper.toPostGestSQLDateTime(recordset.getDateCreated()),
                                     JodaDateTimeHelper.toPostGestSQLDateTime(recordset.getLastModified()));
    }

    protected boolean persistFieldDefinitionObject(XsdFieldDefinition fieldDefinition, CoalesceDataConnector conn)
            throws SQLException
    {

        // Return true if no update is required.
        if (!this.checkLastModified(fieldDefinition, conn)) return true;

        // Yes; Call Store Procedure
        return conn.ExecuteProcedure("CoalesceFieldDefinition_InsertOrUpdate",
                                     fieldDefinition.getKey(),
                                     fieldDefinition.getName(),
                                     fieldDefinition.getParent().getKey(),
                                     fieldDefinition.getParent().getType(),
                                     JodaDateTimeHelper.toPostGestSQLDateTime(fieldDefinition.getDateCreated()),
                                     JodaDateTimeHelper.toPostGestSQLDateTime(fieldDefinition.getLastModified()));
    }

    protected boolean persistRecordObject(XsdRecord record, CoalesceDataConnector conn) throws SQLException
    {
        // Return true if no update is required.
        if (!this.checkLastModified(record, conn)) return true;

        // Yes; Call Store Procedure
        return conn.ExecuteProcedure("CoalesceRecord_InsertOrUpdate",
                                     record.getKey(),
                                     record.getName(),
                                     record.getParent().getKey(),
                                     record.getParent().getType(),
                                     JodaDateTimeHelper.toPostGestSQLDateTime(record.getDateCreated()),
                                     JodaDateTimeHelper.toPostGestSQLDateTime(record.getLastModified()));
    }

    protected boolean persistFieldObject(XsdField field, CoalesceDataConnector conn) throws SQLException
    {
        // Return true if no update is required.
        if (!this.checkLastModified(field, conn)) return true;

        // Yes; Call Store Procedure
        return conn.ExecuteProcedure("CoalesceField_InsertOrUpdate",
                                     field.getKey(),
                                     field.getName(),
                                     field.getValue(),
                                     field.getDataType().getLabel(),
                                     "",
                                     field.getClassificationMarkingAsString(),
                                     field.getModifiedBy(),
                                     field.getParent().getKey(),
                                     field.getParent().getType(),
                                     JodaDateTimeHelper.toPostGestSQLDateTime(field.getDateCreated()),
                                     JodaDateTimeHelper.toPostGestSQLDateTime(field.getLastModified()),
                                     field.getPreviousHistoryKey());
    }

    protected boolean persistFieldHistoryObject(XsdFieldHistory fieldHistory, CoalesceDataConnector conn)
            throws SQLException
    {
        // Return true if no update is required.
        if (!this.checkLastModified(fieldHistory, conn)) return true;

        // Yes; Call Store Procedure
        return conn.ExecuteProcedure("CoalesceFieldHistory_InsertOrUpdate",
                                     fieldHistory.getKey(),
                                     fieldHistory.getName(),
                                     fieldHistory.getValue(),
                                     fieldHistory.getDataType().getLabel(),
                                     "",
                                     fieldHistory.getClassificationMarkingAsString(),
                                     fieldHistory.getModifiedBy(),
                                     fieldHistory.getParent().getKey(),
                                     fieldHistory.getParent().getType(),
                                     JodaDateTimeHelper.toPostGestSQLDateTime(fieldHistory.getDateCreated()),
                                     JodaDateTimeHelper.toPostGestSQLDateTime(fieldHistory.getLastModified()),
                                     fieldHistory.getPreviousHistoryKey());
    }

    protected boolean persistLinkageSectionObject(XsdLinkageSection linkageSection, CoalesceDataConnector conn)
            throws SQLException
    {
        // Return true if no update is required.
        if (!this.checkLastModified(linkageSection, conn)) return true;

        // Yes; Call Store Procedure
        return conn.ExecuteProcedure("CoalesceLinkageSection_InsertOrUpdate",
                                     linkageSection.getKey(),
                                     linkageSection.getName(),
                                     linkageSection.getParent().getKey(),
                                     linkageSection.getParent().getType(),
                                     JodaDateTimeHelper.toPostGestSQLDateTime(linkageSection.getDateCreated()),
                                     JodaDateTimeHelper.toPostGestSQLDateTime(linkageSection.getLastModified()));
    }

    protected boolean persistLinkageObject(XsdLinkage linkage, CoalesceDataConnector conn) throws SQLException
    {
        // Return true if no update is required.
        if (!this.checkLastModified(linkage, conn)) return true;

        // Yes; Call Store Procedure
        return conn.ExecuteProcedure("CoalesceLinkage_InsertOrUpdate",
                                     linkage.getKey(),
                                     linkage.getName(),
                                     linkage.getEntity1Key(),
                                     linkage.getEntity1Name(),
                                     linkage.getEntity1Source(),
                                     linkage.getEntity1Version(),
                                     linkage.getLinkType().getLabel(),
                                     linkage.getStatus().getLabel(),
                                     linkage.getEntity2Key(),
                                     linkage.getEntity2Name(),
                                     linkage.getEntity2Source(),
                                     linkage.getEntity2Version(),
                                     linkage.getClassificationMarking().ToPortionString(),
                                     linkage.getModifiedBy(),
                                     "",
                                     linkage.getParent().getKey(),
                                     linkage.getParent().getType(),
                                     JodaDateTimeHelper.toPostGestSQLDateTime(linkage.getDateCreated()),
                                     JodaDateTimeHelper.toPostGestSQLDateTime(linkage.getLastModified()));
    }

    protected EntityMetaData getCoalesceEntityIdAndTypeForKey(String Key, CoalesceDataConnector conn) throws SQLException
    {
        EntityMetaData metaData = new EntityMetaData();

        // Execute Query
        ResultSet results = conn.ExecuteQuery("SELECT EntityId,EntityIdType,ObjectKey FROM CoalesceEntity WHERE ObjectKey=?",
                                              Key);
        // Get Results
        while (results.next())
        {
            metaData.entityId = results.getString("EntityId");
            metaData.entityType = results.getString("EntityIdType");
            metaData.entityKey = results.getString("ObjectKey");
        }

        return metaData;
    }

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
     * @param dataObject
     * @param conn
     * @return False = Out of Date
     * @throws SQLException
     * @throws SQLException
     */
    protected boolean checkLastModified(XsdDataObject dataObject, CoalesceDataConnector conn) throws SQLException
    {
        boolean isOutOfDate = true;

        // Get LastModified from the Database
        DateTime LastModified = this.getCoalesceDataObjectLastModified(dataObject.getKey(), dataObject.getType(), conn);

        // DB Has Valid Time?
        if (LastModified != null)
        {
            // Remove NanoSeconds (100 ns / Tick and 1,000,000 ns / ms = 10,000 Ticks / ms)
            long ObjectTicks = dataObject.getLastModified().getMillis();
            long SQLRecordTicks = LastModified.getMillis();

            // TODO: Round Ticks for SQL (Not sure if this is required for .NET)
            // ObjectTicks = this.RoundTicksForSQL(ObjectTicks);

            if (ObjectTicks == SQLRecordTicks)
            {
                // They're equal; No Update Required
                isOutOfDate = false;
            }
        }

        return isOutOfDate;
    }

    protected boolean deleteObject(XsdDataObject dataObject, CoalesceDataConnector conn) throws SQLException
    {
        String objectType = dataObject.getType();
        String objectKey = dataObject.getKey();
        String tableName = CoalesceTable.gettableNameForObjectType(objectType);

        conn.ExecuteCmd("DELETE FROM CoalesceObjectMap WHERE ObjectKey=?", objectKey);
        conn.ExecuteCmd("DELETE FROM " + tableName + " WHERE ObjectKey=?", objectKey);

        return true;
    }

    protected List<String> getCoalesceEntityKeysForEntityId(String EntityId, String EntityIdType, String EntityName)
            throws SQLException, Exception,CoalescePersistorException
    {
        List<String> keyList = new ArrayList<String>();

        try (CoalesceDataConnector conn = new CoalesceDataConnector(this.serCon, ConnectionType.PostGresSQL))
        {
            ResultSet results = conn.ExecuteLikeQuery("SELECT ObjectKey FROM CoalesceEntity WHERE (EntityId like ?) AND (EntityIdType like ?) AND (Name=?)",
                                                      2,
                                                      EntityId,
                                                      EntityIdType,
                                                      EntityName);

            while (results.next())
            {
                keyList.add(results.getString("ObjectKey"));
            }

            return keyList;
        }

    }

    protected List<String> getCoalesceEntityKeysForEntityIdAndSource(String EntityId,
                                                                     String EntityIdType,
                                                                     String EntityName,
                                                                     String EntitySource) throws SQLException, Exception,
            CoalescePersistorException
    {

        try (CoalesceDataConnector conn = new CoalesceDataConnector(this.serCon, ConnectionType.PostGresSQL))
        {
            List<String> keyList = new ArrayList<String>();

            ResultSet results = conn.ExecuteLikeQuery("SELECT ObjectKey FROM CoalesceEntity WHERE (EntityId like ? ) AND (EntityIdType like  ? ) AND (Name=?) AND (Source=?)",
                                                      2,
                                                      EntityId,
                                                      EntityIdType,
                                                      EntityName,
                                                      EntitySource);

            while (results.next())
            {
                keyList.add(results.getString("ObjectKey"));
            }

            return keyList;
        }
    }

    protected boolean updateFileContent(XsdDataObject dataObject, CoalesceDataConnector conn) throws SQLException
    {
        boolean isSuccessful = false;

        if (dataObject.getStatus() == ECoalesceDataObjectStatus.ACTIVE)
        {
            if (dataObject.getType().toLowerCase() == "field")
            {
                if (((XsdField) dataObject).getDataType() == ECoalesceFieldDataTypes.FileType)
                {
                    isSuccessful = persistFieldObject((XsdField) dataObject, conn);
                }
            }

            for (Map.Entry<String, XsdDataObject> s : dataObject.getChildDataObjects().entrySet())
            {
                System.out.println("ACTIVE OBJECT-Processing Child Key(FILE): " + s.getKey());

                isSuccessful = updateFileContent(s.getValue(), conn);
            }
        }
        return isSuccessful;
    }

    private boolean updateDataObject(XsdDataObject xsdDataObject, CoalesceDataConnector conn, boolean AllowRemoval)
            throws SQLException

    {
        boolean isSuccessful = false;

        System.out.println(xsdDataObject.getStatus().getLabel() + " OBJECT [" + xsdDataObject.getName() + " : "
                + xsdDataObject.getType() + "] Processing Key:  " + xsdDataObject.getKey());

        switch (xsdDataObject.getStatus()) {
        case ACTIVE:
            // Persist Object
            isSuccessful = persistObject(xsdDataObject, conn);
            break;

        case DELETED:
            if (AllowRemoval)
            {
                // Delete Object
                isSuccessful = deleteObject(xsdDataObject, conn);
            }
            else
            {
                // Mark Object as Deleted
                isSuccessful = persistObject(xsdDataObject, conn);
            }

            break;

        default:
            isSuccessful = false;

        }

        // Successful?
        if (isSuccessful)
        {

            // Yes; Iterate Through Children
            for (Map.Entry<String, XsdDataObject> s : xsdDataObject.getChildDataObjects().entrySet())
            {
                updateDataObject(s.getValue(), conn, AllowRemoval);
            }
        }

        return isSuccessful;
    }

    private DateTime getCoalesceDataObjectLastModified(String Key, String ObjectType, CoalesceDataConnector conn)
            throws SQLException
    {
        DateTime lastModified = DateTime.now(DateTimeZone.UTC);

        // Determine the Table Name
        String tableName = CoalesceTable.gettableNameForObjectType(ObjectType);
        String dateValue = null;

        ResultSet results = conn.ExecuteQuery("SELECT LastModified FROM " + tableName + " WHERE ObjectKey=?", Key.trim());
        ResultSetMetaData resultsmd = results.getMetaData();

        // JODA Function DateTimeFormat will adjust for the Server timezone when converting the time.
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

    private ElementMetaData getXPathRecursive(String Key, String ObjectType, String XPath, CoalesceDataConnector conn)
            throws SQLException
    {

        boolean isEntityTable = false;
        ElementMetaData meteData = null;

        String sql = "";

        // Get Table Name
        String tableName = CoalesceTable.gettableNameForObjectType(ObjectType);

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

        ResultSet results = conn.ExecuteQuery(sql, Key.trim());

        // Valid Results?
        while (results.next())
        {

            String name = results.getString("name");

            if (isEntityTable)
            {
                XPath = name + "/" + XPath;

                // Set Meta Data
                meteData = new ElementMetaData();
                meteData.entityKey = Key;
                meteData.elementXPath = XPath;

            }
            else
            {
                String parentKey = results.getString("ParentKey");
                String parentType = results.getString("ParentType");

                if (XPath == null || XPath == "")
                {
                    meteData = getXPathRecursive(parentKey, parentType, name, conn);
                }
                else
                {
                    meteData = getXPathRecursive(parentKey, parentType, name + "/" + XPath, conn);
                }
            }

        }

        return meteData;

    }
}