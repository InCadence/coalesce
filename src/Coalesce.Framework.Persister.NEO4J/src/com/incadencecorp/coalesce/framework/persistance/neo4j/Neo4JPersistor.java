package com.incadencecorp.coalesce.framework.persistance.neo4j;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDataObject;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
import com.incadencecorp.coalesce.framework.persistance.CoalescePersisterBase;
import com.incadencecorp.coalesce.framework.persistance.CoalesceTable;
import com.incadencecorp.coalesce.framework.persistance.ICoalesceCacher;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;

public class Neo4JPersistor extends CoalescePersisterBase {

    private ServerConn serCon;

    /*--------------------------------------------------------------------------
    Constructor / Initializers
    --------------------------------------------------------------------------*/

    public Neo4JPersistor()
    {
        /***********
         * Define the PostGresSQL Database Connection in the URL, change to whatever the schema name is on your system
         ***********/
        serCon = new ServerConn();
        /* Set URL, User, Pass */
    }

    public void Initialize(ServerConn svConn)
    {
        serCon = svConn;
    }

    public boolean Initialize(ICoalesceCacher cacher, ServerConn svConn) throws CoalescePersistorException
    {
        serCon = svConn;

        return super.initialize(cacher);
    }

    @Override
    public String getEntityXml(String Key) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEntityXml(String EntityId, String EntityIdType) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEntityXml(String Name, String EntityId, String EntityIdType) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getFieldValue(String fieldKey) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ElementMetaData getXPath(String Key, String ObjectType) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getCoalesceEntityKeysForEntityId(String EntityId,
                                                         String EntityIdType,
                                                         String EntityName,
                                                         String EntitySource) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EntityMetaData getCoalesceEntityIdAndTypeForKey(String Key) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] getBinaryArray(String BinaryFieldKey) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean persistEntityTemplate(CoalesceEntityTemplate EntityTemplate) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getEntityTemplateXml(String Key) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEntityTemplateXml(String Name, String Source, String Version) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEntityTemplateKey(String Name, String Source, String Version) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEntityTemplateMetadata() throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean FlattenObject(CoalesceEntity entity, boolean AllowRemoval) throws CoalescePersistorException
    {
        boolean isSuccessful = false;

        // Create a Database Connection
        try (Neo4JDataConnector conn = new Neo4JDataConnector(this.serCon))
        {

            conn.openConnection();
            conn.executeCmd("CONSTRAINT ON (item:" + entity.getName() + ") ASSERT item.EntityKey IS UNIQUE");

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

    protected boolean persistEntityObject(CoalesceEntity entity, Neo4JDataConnector conn) throws SQLException
    {
        // Return true if no update is required.
        // if (!this.checkLastModified(entity, conn)) return true;

        String sqlStmt = "(Entity:".concat(entity.getName().concat(" {EntityKey: {ParamEntityKey}})"));

        return false;
    }

    protected String getValues(CoalesceDataObject dataObject, Map values)
    {
        switch (dataObject.getStatus()) {
        case ACTIVE:
            switch (dataObject.getType().toLowerCase()) {
            case "field":
                CoalesceField<?> fieldObject = (CoalesceField<?>) dataObject;
                switch (fieldObject.getType().toUpperCase()) {
                case "BINARY":
                case "FILE":
                default: {
                    if (values == null)
                    {

                    }
                }
                    break;
                }
            }
        }
        return null;
    }

    protected boolean checkLastModified(CoalesceDataObject dataObject, Neo4JDataConnector conn) throws SQLException
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

    private DateTime getCoalesceDataObjectLastModified(String Key, String ObjectType, Neo4JDataConnector conn)
            throws SQLException
    {
        DateTime lastModified = DateTime.now(DateTimeZone.UTC);

        // Determine the Table Name
        String tableName = CoalesceTable.getTableNameForObjectType(ObjectType);
        String dateValue = null;

        ResultSet results = conn.executeQuery("?", new CoalesceParameter(Key.trim()));
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

    @Override
    protected boolean FlattenCore(CoalesceEntity entity, boolean AllowRemoval) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public DateTime getCoalesceDataObjectLastModified(String Key, String ObjectType) throws CoalescePersistorException
    {
        try (Neo4JDataConnector conn = new Neo4JDataConnector(this.serCon))
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

}
