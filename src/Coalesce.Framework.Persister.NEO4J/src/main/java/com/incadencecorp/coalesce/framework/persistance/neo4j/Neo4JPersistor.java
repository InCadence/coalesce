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
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceObject;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
import com.incadencecorp.coalesce.framework.persistance.CoalescePersistorBase;
import com.incadencecorp.coalesce.framework.persistance.ICoalesceCacher;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;

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

public class Neo4JPersistor extends CoalescePersistorBase {

    private ServerConn _serCon;

    /*--------------------------------------------------------------------------
    Constructor / Initializers
    --------------------------------------------------------------------------*/

    public Neo4JPersistor()
    {
        /***********
         * Define the PostGresSQL Database Connection in the URL, change to whatever the schema name is on your system
         ***********/
        _serCon = new ServerConn();
        /* Set URL, User, Pass */
    }

    public void initialize(ServerConn svConn)
    {
        _serCon = svConn;
    }

    public boolean initialize(ICoalesceCacher cacher, ServerConn svConn) throws CoalescePersistorException
    {
        _serCon = svConn;

        return super.initialize(cacher);
    }

    @Override
    public String getEntityXml(String key) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEntityXml(String entityId, String entityIdType) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEntityXml(String name, String entityId, String entityIdType) throws CoalescePersistorException
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
    public ElementMetaData getXPath(String key, String objectType) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getCoalesceEntityKeysForEntityId(String entityId,
                                                         String entityIdType,
                                                         String entityName,
                                                         String entitySource) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EntityMetaData getCoalesceEntityIdAndTypeForKey(String key) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] getBinaryArray(String binaryFieldKey) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean persistEntityTemplate(CoalesceEntityTemplate entityTemplate) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getEntityTemplateXml(String key) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEntityTemplateXml(String name, String source, String version) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEntityTemplateKey(String name, String source, String version) throws CoalescePersistorException
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
    protected boolean flattenObject(CoalesceEntity entity, boolean allowRemoval) throws CoalescePersistorException
    {
        boolean isSuccessful = false;

        // Create a Database Connection
        try (CoalesceDataConnectorBase conn = new Neo4JDataConnector(this._serCon))
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

    @Override
    protected CoalesceDataConnectorBase getDataConnector() throws CoalescePersistorException
    {
        return new Neo4JDataConnector(getConnectionSettings());
    }

    protected boolean persistEntityObject(CoalesceEntity entity, CoalesceDataConnectorBase conn) throws SQLException
    {
        // Return true if no update is required.
        // if (!this.checkLastModified(entity, conn)) return true;

        //String sqlStmt = "(Entity:".concat(entity.getName().concat(" {EntityKey: {ParamEntityKey}})"));

        return false;
    }

    protected String getValues(CoalesceObject coalesceObject, Map<?, ?> values)
    {
        switch (coalesceObject.getStatus()) {
        case ACTIVE:
            switch (coalesceObject.getType().toLowerCase()) {
            case "field":
                CoalesceField<?> fieldObject = (CoalesceField<?>) coalesceObject;
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
		default:
			break;
        }
        return null;
    }

    protected boolean checkLastModified(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn) throws SQLException
    {
        boolean isOutOfDate = true;

        // Get LastModified from the Database
        DateTime lastModified = this.getCoalesceObjectLastModified(coalesceObject.getKey(), coalesceObject.getType(), conn);

        // DB Has Valid Time?
        if (lastModified != null)
        {
            // Remove NanoSeconds (100 ns / Tick and 1,000,000 ns / ms = 10,000 Ticks / ms)
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
        DateTime lastModified = DateTime.now(DateTimeZone.UTC);

        // Determine the Table Name
        //String tableName = CoalesceTableHelper.getTableNameForObjectType(objectType);
        String dateValue = null;

        ResultSet results = conn.executeQuery("?", new CoalesceParameter(key.trim()));
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
    protected boolean flattenCore(CoalesceEntity entity, boolean allowRemoval) throws CoalescePersistorException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public DateTime getCoalesceObjectLastModified(String key, String objectType) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = new Neo4JDataConnector(this._serCon))
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

}
