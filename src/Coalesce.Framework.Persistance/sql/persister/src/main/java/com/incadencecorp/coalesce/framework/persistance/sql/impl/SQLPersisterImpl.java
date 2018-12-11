/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.framework.persistance.sql.impl;

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.CoalesceTableHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.CoalesceSettings;
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.framework.persistance.*;
import com.incadencecorp.coalesce.search.resultset.CoalesceCommonColumns;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

/**
 * This persister is for a SQL database.
 *
 * @author GGaito
 */
public class SQLPersisterImpl extends SQLTemplatePersisterImpl implements ICoalescePersistor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLPersisterImpl.class);
    private static final SQLNormalizer NORMALIZER = new SQLNormalizer();
    private static final CoalesceCommonColumns COLUMNS = new CoalesceCommonColumns(NORMALIZER);

    private String _schema;
    private ICoalesceCacher _cacher = null;

    /**
     * Default Constructor
     */
    public SQLPersisterImpl()
    {
        setConnectionSettings(SQLPersisterImplSettings.getServerConn());
        setSchema(SQLPersisterImplSettings.getDatabaseSchema());
    }

    /**
     * Special Constructor
     *
     * @param params A map with specified fields needed for the database
     */
    public SQLPersisterImpl(Map<String, String> params)
    {
       super(params);
    }
    @Override
    public boolean saveEntity(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        boolean isSuccessful = false;

        // Cacher Disabled or not an Entity
        if (_cacher == null)
        {
            // Yes; Persist and Flatten Now
            isSuccessful = flattenObject(allowRemoval, entities);
        }
        else
        {
            // Delayed Persisting and Space Available?
            if (_cacher.getSupportsDelayedSave() && _cacher.getState() == ECoalesceCacheStates.SPACE_AVAILABLE)
            {
                // Yes; Only Flatten Core Elements
                isSuccessful = flattenCore(allowRemoval, entities);
            }
            else
            {
                // No; Persist and Flatten Entity Now
                isSuccessful = flattenObject(allowRemoval, entities);
            }

            // If Successful Add to Cache
            if (isSuccessful)
            {
                for (CoalesceEntity entity : entities)
                {
                    isSuccessful = _cacher.storeEntity(entity);
                }
            }

        }

        return isSuccessful;
    }

    @Override
    public CoalesceEntity[] getEntity(String... keys) throws CoalescePersistorException
    {
        List<CoalesceEntity> results = new ArrayList<CoalesceEntity>();
        List<String> keysToQuery = new ArrayList<String>();

        for (String key : keys)
        {

            // Get From Cache
            CoalesceEntity entity = getEntityFromCache(key);

            // Cached?
            if (entity != null)
            {
                // Yes; Add to Results
                results.add(entity);
            }
            else
            {
                // No; Add to Query List
                keysToQuery.add(key);
            }

        }

        if (keysToQuery.size() > 0)
        {
            for (String xml : getEntityXml(keysToQuery.toArray(new String[keysToQuery.size()])))
            {

                //add here
                CoalesceEntity entity = new CoalesceEntity();

                // Found?
                if (!StringHelper.isNullOrEmpty(xml) && entity.initialize(xml))
                {

                    // Yes; Add to Results
                    results.add(entity);

                    // Add Entity to Cache
                    addEntityToCache(entity);

                }

            }
        }

        return results.toArray(new CoalesceEntity[results.size()]);
    }

    @Override
    public String[] getEntityXml(String... keys) throws CoalescePersistorException
    {
        try (CoalesceDataConnectorBase conn = this.getDataConnector())
        {
            List<String> xmlList = new ArrayList<>();
            List<CoalesceParameter> parameters = new ArrayList<>();

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

    @Override
    public EnumSet<EPersistorCapabilities> getCapabilities()
    {
        EnumSet<EPersistorCapabilities> enumSet = super.getCapabilities();
        EnumSet<EPersistorCapabilities> newCapabilities = EnumSet.of(EPersistorCapabilities.READ_TEMPLATES,
                                                                     EPersistorCapabilities.UPDATE,
                                                                     EPersistorCapabilities.DELETE,
                                                                     EPersistorCapabilities.SEARCH,
                                                                     EPersistorCapabilities.CASE_INSENSITIVE_SEARCH,
                                                                     EPersistorCapabilities.TEMPORAL_SEARCH);
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



    /*--------------------------------------------------------------------------
    	Private Functions
    --------------------------------------------------------------------------*/

    private CoalesceEntity getEntityFromCache(String key)
    {

        CoalesceEntity entity = null;

        // Cacher Initialized?
        if (_cacher != null)
        {

            // Yes; Contains Entity?
            if (_cacher.containsEntity(key))
            {

                // Yes; Retrieve Entity
                entity = _cacher.retrieveEntity(key);

            }

        }

        return entity;

    }
    private boolean addEntityToCache(CoalesceEntity entity)
    {

        boolean isModified = false;

        // Cacher Initialized?
        if (_cacher != null)
        {

            // Yes; Retrieve Entity
            isModified = _cacher.storeEntity(entity);

        }

        return isModified;

    }
    private DateTime getCoalesceObjectLastModified(String key, String objectType, CoalesceDataConnectorBase conn)
            throws SQLException
    {
        DateTime lastModified = null;

        // Determine the Table Name
        String tableName = CoalesceTableHelper.getTableNameForObjectType(objectType);
        String dateValue = null;

        ResultSet results = conn.executeQuery(
                "SELECT LastModified FROM " + getSchemaPrefix() + tableName + " WHERE ObjectKey=?",
                new CoalesceParameter(key.trim(), Types.CHAR));
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

    private boolean updateCoalesceObject(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn, boolean allowRemoval)
            throws SQLException, CoalesceDataFormatException

    {
        boolean isSuccessful = false;
        boolean isDeleted = false;

        if (coalesceObject.isFlatten())
        {
            switch (coalesceObject.getStatus())
            {
            case NEW:
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

     /*--------------------------------------------------------------------------
        Protected Methods
     --------------------------------------------------------------------------*/
     @Override
     protected boolean flattenObject(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
     {
         boolean isSuccessful = true;

         try (CoalesceDataConnectorBase conn = new SQLDataConnector(getConnectionSettings(), getSchemaPrefix()))
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
             catch (SQLException | CoalesceDataFormatException e)
             {
                 conn.rollback();

                 throw new CoalescePersistorException("FlattenObject: " + e.getMessage(), e);
             }

         }

         return isSuccessful;
     }

    /**
     * @param entity
     * @return additional rows that have been registered for your implementaion.
     */
    protected List<CoalesceParameter> getExtendedParameters(CoalesceEntity entity)
    {
        return new ArrayList<>();
    }

    /**
     * Adds or Updates a Coalesce object that matches the given parameters.
     *
     * @param coalesceObject the Coalesce object to be added or updated
     * @param conn           is the SQLDataConnector database connection
     * @return isSuccessful = True = Successful add/update operation.
     * @throws SQLException
     */
    protected boolean persistObject(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn)
            throws SQLException, CoalesceDataFormatException
    {
        boolean isSuccessful = true;

        switch (coalesceObject.getType())
        {
        case "entity":
            isSuccessful = persitCoalesceEntityToAllTables(coalesceObject,conn);
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
            if (CoalesceSettings.getUseIndexing())
            {
                isSuccessful = persistFieldDefinitionObject((CoalesceFieldDefinition) coalesceObject, conn);
            }
            break;

        case "record":
            if (CoalesceSettings.getUseIndexing())
            {
                isSuccessful = persistRecordObject((CoalesceRecord) coalesceObject, conn);
            }
            break;

        case "field":
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

    protected boolean persitCoalesceEntityToAllTables(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn)
            throws SQLException, CoalesceDataFormatException
    {
        boolean isSuccessful = false;
        CoalesceEntity entity = (CoalesceEntity) coalesceObject;
        if (isSuccessful = persistEntityObject(entity, conn))
        {
            //For IdentityHubAction
            if (isSuccessful && entity.getName().equals("IdentityHubAction"))
            {
                isSuccessful = persistIdentityHubAction(coalesceObject, conn);
            }
            //For IdentityHubComments
            if(isSuccessful && entity.getName().equals("IdentityHubComments"))
            {
                isSuccessful = persistIdentityHubComments(coalesceObject,conn);

            }
            //For IdentityHubJob
            if(isSuccessful && entity.getName().equals("IdentityHubJob"))
            {
                isSuccessful = persistIdentityHubJob(coalesceObject,conn);
            }
            //For IdentityHubMission
            if(isSuccessful && entity.getName().equalsIgnoreCase("IdentityHubMission"))
            {
                isSuccessful = persistIdentityHubMission((CoalesceEntity)coalesceObject,conn);
            }

        }
        return isSuccessful;
    }

    public int getCurrentStatus(CoalesceRecordset coalesceRecordset)
    {
        String currentStatusString = "";
        int currentStatus;
        for (CoalesceRecord record : coalesceRecordset.getRecords())
        {
            List<CoalesceField> coalesceFields = record.getFields();
            for(CoalesceField field : coalesceFields){
                if(field.getName().equalsIgnoreCase("CurrentStatus"))
                {
                    currentStatusString = record.getFieldByName("CurrentStatus").getBaseValue();
                    break;
                }

            }

        }
        switch (currentStatusString.toUpperCase())
        {
        case "PENDING":
             currentStatus = 1;
            break;
        case "TRANSFERRED":
            currentStatus = 2;
            break;
        case "TRANSFERRING":
            currentStatus = 4;
            break;
        case "ERROR":
            currentStatus = 5;
            break;
        case "FAILED":
            currentStatus = 7;
            break;
        default:
            currentStatus = 64;
            break;
        }

        return currentStatus;
    }



    /*--------------------------------------------------------------------------
    	IdentityHub Persistance
    --------------------------------------------------------------------------*/
    /**
     * Adds or Updates a IdentityHub Action that matches the given parameters.
     *
     * @param object the action to be added or updated
     * @param conn      is the SQLDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistIdentityHubJob(CoalesceObject object, CoalesceDataConnectorBase conn)
            throws SQLException
    {
        int currentStatus;
        CoalesceParameter parentKey = new CoalesceParameter(null,Types.NULL);
        CoalesceEntity entity = object.getEntity();
        CoalesceSection coalesceSection = entity.getSection(entity.getName() + "/Live Status Section");
        CoalesceRecordset coalesceRecordset = coalesceSection.getRecordset(coalesceSection.getName() + "/Live Status Recordset");
        currentStatus = getCurrentStatus(coalesceRecordset);

        if(entity.getParent() != null)
        {
            parentKey = new CoalesceParameter(entity.getParent().getKey(),Types.CHAR);
        }

        // Yes; Call Store Procedure
        return conn.executeProcedure("IdentityHubJob_InsertOrUpdate",
                                     new CoalesceParameter(entity.getKey(),Types.CHAR),
                                     parentKey,
                                     new CoalesceParameter(entity.getSource()),
                                     new CoalesceParameter(entity.getVersion()),
                                     new CoalesceParameter(Integer.toString(currentStatus),Types.INTEGER),
                                     new CoalesceParameter(entity.getLastModified().toString(),Types.CHAR),
                                     new CoalesceParameter(entity.getDateCreated().toString(),Types.CHAR));
    }
    /**
     * Adds or Updates a IdentityHub Action that matches the given parameters.
     *
     * @param entity the action to be added or updated
     * @param conn      is the SQLDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistIdentityHubMission(CoalesceEntity entity, CoalesceDataConnectorBase conn)
            throws SQLException, CoalesceDataFormatException
    {
        String missionName = null,
                missionDescription = null,
                missionIndicatorShape = null,
                missionIndicatorColor = null,
                missionIndicatorNumber = null,
                missionIndicatorNumberBase10 = null,
                missionStartDateTime = null,
                missionEndDateTime = null,
                missionLocation = null,
                missionStatus = null,
                eventName = null,
                eventDescription = null;

        CoalesceParameter parentKey = new CoalesceParameter(null,Types.NULL);
        CoalesceSection coalesceSection = entity.getSection(entity.getName() + "/Mission Information Section");
        CoalesceRecordset coalesceRecordset = coalesceSection.getRecordset(coalesceSection.getName() + "/Mission Information Recordset");
        for(CoalesceRecord record : coalesceRecordset.getRecords())
        {
            if (record.getName().equalsIgnoreCase("mission information recordset record"))
            {
                List<CoalesceField> coalesceFields = record.getFields();
                for (CoalesceField field : coalesceFields)
                {
                    String information = null;
                    if (field.getValue() != null)
                    {
                        information = field.getValue().toString();
                    }
                    switch (field.getName().toUpperCase())
                    {
                    case "MISSIONNAME":
                        missionName = information;
                        break;
                    case "MISSIONDESCRIPTION":
                        missionDescription = information;
                        break;
                    case "MISSIONINDICATORSHAPE":
                        missionIndicatorShape = information;
                        break;
                    case "MISSIONINDICATORCOLOR":
                        missionIndicatorColor = information;
                        break;
                    case "MISSIONINDICATORNUMBER":
                        missionIndicatorNumber = information;
                        break;
                    case "MISSIONINDICATORNUMBERBASE10":
                        missionIndicatorNumberBase10 = information;
                        break;
                    case "MISSIONSTARTDATETIME":
                        missionStartDateTime = information;
                        break;
                    case "MISSIONENDDATETIME":
                        missionEndDateTime = information;
                        break;
                    case "MISSIONLOCATION":
                        missionLocation = information;
                        break;
                    case "CURRENTSTATUS":
                        missionStatus = information;
                        break;
                    case "EVENTNAME":
                        eventName = information;
                        break;
                    case "EVENTDESCRIPTION":
                        eventDescription = information;
                        break;
                    default:
                        break;
                    }
                }
            }
        }

//        if(entity.getParent() != null)
//        {
//            parentKey = new CoalesceParameter(entity.getParent().getKey(),Types.CHAR);
//        }
        // Yes; Call Store Procedure
        return conn.executeProcedure("IdentityHubMission_InsertOrUpdate",
                                     new CoalesceParameter(entity.getKey(),Types.CHAR),
                                     parentKey,
                                     new CoalesceParameter(missionName,Types.CHAR),
                                     new CoalesceParameter(missionDescription,Types.CHAR),
                                     new CoalesceParameter(missionIndicatorShape,Types.CHAR),
                                     new CoalesceParameter(missionIndicatorColor,Types.CHAR),
                                     new CoalesceParameter(missionIndicatorNumber,Types.CHAR),
                                     new CoalesceParameter(missionIndicatorNumberBase10,Types.CHAR),
                                     new CoalesceParameter(missionStartDateTime,Types.CHAR),
                                     new CoalesceParameter(missionEndDateTime,Types.CHAR),
                                     new CoalesceParameter(missionLocation,Types.CHAR),
                                     new CoalesceParameter(missionStatus,Types.CHAR),
                                     new CoalesceParameter(eventName,Types.CHAR),
                                     new CoalesceParameter(eventDescription,Types.CHAR),
                                     new CoalesceParameter(entity.getDateCreated().toString(),Types.CHAR),
                                     new CoalesceParameter(entity.getLastModified().toString(),Types.CHAR));
    }

    protected boolean persistIdentityHubComments(CoalesceObject object, CoalesceDataConnectorBase conn) throws SQLException
    {
        CoalesceParameter parentKey = new CoalesceParameter(null,Types.NULL);

        CoalesceEntity entity = object.getEntity();
        CoalesceSection coalesceSection = entity.getSection(entity.getName() + "/Comments Section");
        CoalesceRecordset coalesceRecordset = coalesceSection.getRecordset(coalesceSection.getName() + "/Comments Recordset");

        if(entity.getParent() != null)
        {
            parentKey = new CoalesceParameter(entity.getParent().getKey(),Types.CHAR);
        }

        int commentCount = coalesceRecordset.getCount();
        if(commentCount < 1)
        {
            commentCount = 0;
        }
        // Yes; Call Store Procedure
        return conn.executeProcedure("IdentityHubComments_InsertOrUpdate",
                                     new CoalesceParameter(entity.getKey(),Types.CHAR),
                                     parentKey,
                                     new CoalesceParameter(Integer.toString(commentCount),Types.INTEGER),
                                     new CoalesceParameter(entity.getDateCreated().toString(),Types.CHAR),
                                     new CoalesceParameter(entity.getLastModified().toString(), Types.CHAR));
    }
    /**
     * Adds or Updates a IdentityHub Action that matches the given parameters.
     *
     * @param coalesceObject the action to be added or updated
     * @param conn      is the SQLDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistIdentityHubAction(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn)
            throws SQLException, CoalesceDataFormatException
    {
        CoalesceEntity entity = coalesceObject.getEntity();

        String currentStatus = "";
        String responseStatus = "Unknown";
        String location = null;
        CoalesceParameter parentKey = new CoalesceParameter(null,Types.NULL);
        CoalesceParameter locationParam = new CoalesceParameter(null,Types.NULL);
        CoalesceSection coalesceSection = entity.getSection(entity.getName() + "/Live Status Section");
        CoalesceRecordset coalesceRecordset = coalesceSection.getRecordset(coalesceSection.getName() + "/Live Status Recordset");
        for (CoalesceRecord record : coalesceRecordset.getRecords())
        {
            List<CoalesceField> coalesceFields = record.getFields();
            for(CoalesceField field : coalesceFields)
            {
                if(field.getName().equalsIgnoreCase("CurrentStatus"))
                {
                    if(field.getValue() != null)
                        currentStatus = field.getBaseValue();
                }
                else if(field.getName().equalsIgnoreCase("ResponseStatus"))
                {
                    if(field.getValue() != null)
                        responseStatus = field.getBaseValue();
                }
                else if(field.getName().equalsIgnoreCase("Location"))
                {
                    if(field.getValue() != null)
                        location = field.getBaseValue();
                }
            }

        }

        if(entity.getParent() != null)
        {
            parentKey = new CoalesceParameter(entity.getParent().getKey(),Types.CHAR);
        }
        if(location != null)
        {
            locationParam = new CoalesceParameter(location, Types.CHAR);
        }

        // Yes; Call Store Procedure
            return conn.executeProcedure("IdentityHubAction_InsertOrUpdate",
                                         new CoalesceParameter(entity.getKey(),Types.CHAR),
                                         parentKey,
                                         new CoalesceParameter(entity.getSource()),
                                         new CoalesceParameter(entity.getVersion()),
                                         new CoalesceParameter(entity.getTitle(),Types.CHAR),
                                         new CoalesceParameter(null, Types.NULL),
                                         locationParam,
                                         new CoalesceParameter(currentStatus,Types.CHAR),
                                         new CoalesceParameter(responseStatus, Types.CHAR),
                                         new CoalesceParameter(entity.getLastModified().toString(),Types.CHAR),
                                         new CoalesceParameter(entity.getDateCreated().toString(),Types.CHAR));

    }

    /**
     * Adds or Updates a IdentityHub Action that matches the given parameters.
     *
     * @param entity the action to be added or updated
     * @param conn      is the SQLDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistIdentityHubChanges(CoalesceEntity entity, CoalesceDataConnectorBase conn) throws SQLException
    {
        // Return true if no update is required.
        if(!checkLastModified(entity,conn))
        {
            return true;
        }

        // Yes; Call Store Procedure
        //        return conn.executeProcedure("IdentityHubChanges_InsertOrUpdate",
        //                                     new CoalesceParameter(entity.getKey(),Types.CHAR),
        //                                     new CoalesceParameter(entity.getParent().getKey(),Types.CHAR),
        //                                     new CoalesceParameter(entity.));

        return true;
    }


    /*--------------------------------------------------------------------------
    	Coalesce Object Persistance
    --------------------------------------------------------------------------*/
    /**
     * Adds or Updates a Coalesce recordset that matches the given parameters.
     *
     * @param recordset the XsdRecordset to be added or updated
     * @param conn      is the SQLDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistRecordsetObject(CoalesceRecordset recordset, CoalesceDataConnectorBase conn) throws SQLException
    {
        // Return true if no update is required.
        if (!checkLastModified(recordset, conn))
        {
            return true;
        }

        // Yes; Call Store Procedure
        return conn.executeProcedure("CoalesceRecordset_InsertOrUpdate",
                                     new CoalesceParameter(recordset.getKey(), Types.CHAR),
                                     new CoalesceParameter(recordset.getName()),
                                     new CoalesceParameter(recordset.getParent().getKey(), Types.CHAR),
                                     new CoalesceParameter(recordset.getParent().getType()),
                                     new CoalesceParameter(recordset.getDateCreated().toString(), Types.CHAR),
                                     new CoalesceParameter(recordset.getLastModified().toString(), Types.CHAR));
    }



    /**
     * Adds or Updates a Coalesce record that matches the given parameters.
     *
     * @param record the XsdRecord to be added or updated
     * @param conn   is the SQLDataConnector database connection
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
                                     new CoalesceParameter(record.getKey(), Types.CHAR),
                                     new CoalesceParameter(record.getName()),
                                     new CoalesceParameter(record.getParent().getKey(), Types.CHAR),
                                     new CoalesceParameter(record.getParent().getType()),
                                     new CoalesceParameter(record.getDateCreated().toString(), Types.CHAR),
                                     new CoalesceParameter(record.getLastModified().toString(), Types.CHAR));
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

        // Return true if no update is required.
        if (!checkLastModified(fieldDefinition, conn))
        {
            return true;
        }

        // Yes; Call Store Procedure
        return conn.executeProcedure("CoalesceFieldDefinition_InsertOrUpdate",
                                     new CoalesceParameter(fieldDefinition.getKey(), Types.CHAR),
                                     new CoalesceParameter(fieldDefinition.getName()),
                                     new CoalesceParameter(fieldDefinition.getParent().getKey(), Types.CHAR),
                                     new CoalesceParameter(fieldDefinition.getParent().getType()),
                                     new CoalesceParameter(fieldDefinition.getDateCreated().toString(), Types.CHAR),
                                     new CoalesceParameter(fieldDefinition.getLastModified().toString(), Types.CHAR));
    }

    /**
     * Adds or Updates a Coalesce field that matches the given parameters.
     *
     * @param field the XsdField to be added or updated
     * @param conn  is the SQLDataConnector database connection
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
                                     new CoalesceParameter(field.getKey(), Types.CHAR),
                                     new CoalesceParameter(field.getName()),
                                     new CoalesceParameter(field.getBaseValue()),
                                     new CoalesceParameter(field.getDataType().getLabel()),
                                     new CoalesceParameter(""),
                                     new CoalesceParameter(field.getClassificationMarkingAsString()),
                                     new CoalesceParameter(field.getModifiedBy()),
                                     new CoalesceParameter(field.getParent().getKey(), Types.CHAR),
                                     new CoalesceParameter(field.getParent().getType()),
                                     new CoalesceParameter(field.getDateCreated().toString(), Types.CHAR),
                                     new CoalesceParameter(field.getLastModified().toString(), Types.CHAR),
                                     new CoalesceParameter(field.getPreviousHistoryKey(), Types.CHAR));
    }

    /**
     * Adds or Updates a Coalesce field history that matches the given
     * parameters.
     *
     * @param fieldHistory the XsdFieldHistory to be added or updated
     * @param conn         is the SQLDataConnector database connection
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
                                     new CoalesceParameter(fieldHistory.getKey(), Types.CHAR),
                                     new CoalesceParameter(fieldHistory.getName()),
                                     new CoalesceParameter(fieldHistory.getValue()),
                                     new CoalesceParameter(fieldHistory.getDataType().getLabel()),
                                     new CoalesceParameter(""),
                                     new CoalesceParameter(fieldHistory.getClassificationMarkingAsString()),
                                     new CoalesceParameter(fieldHistory.getModifiedBy()),
                                     new CoalesceParameter(fieldHistory.getParent().getKey(), Types.CHAR),
                                     new CoalesceParameter(fieldHistory.getParent().getType()),
                                     new CoalesceParameter(fieldHistory.getDateCreated().toString(), Types.CHAR),
                                     new CoalesceParameter(fieldHistory.getLastModified().toString(), Types.CHAR),
                                     new CoalesceParameter(fieldHistory.getPreviousHistoryKey(), Types.CHAR));
    }

    /**
     * Adds or Updates a Coalesce entity that matches the given parameters.
     *
     * @param entity the XsdEntity to be added or updated
     * @param conn   is the SQLDataConnector database connection
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
        if(entity.getUploadedToServer() == null)
        {
            int x = 2;
        }
        List<CoalesceParameter> params = new ArrayList<>();
        params.add(new CoalesceParameter(entity.getKey(), Types.CHAR));
        params.add(new CoalesceParameter(entity.getName()));
        params.add(new CoalesceParameter(entity.getSource()));
        params.add(new CoalesceParameter(entity.getVersion()));
        params.add(new CoalesceParameter(entity.getEntityId()));
        params.add(new CoalesceParameter(entity.getEntityIdType()));
        params.add(new CoalesceParameter(entity.toXml("UTF-16")));
        params.add(new CoalesceParameter(entity.getDateCreated().toString(), Types.CHAR));
        params.add(new CoalesceParameter(entity.getLastModified().toString(), Types.CHAR));
        params.add(new CoalesceParameter(JodaDateTimeHelper.nowInUtc().toString(),Types.CHAR));

        return conn.executeProcedure(procedureName, params.toArray(new CoalesceParameter[params.size()]))
                && !entity.isMarkedDeleted();
    }

    /**
     * Adds or Updates a Coalesce section that matches the given parameters.
     *
     * @param section the XsdSection to be added or updated
     * @param conn    is the SQLDataConnector database connection
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
                                     new CoalesceParameter(section.getKey(), Types.CHAR),
                                     new CoalesceParameter(section.getName()),
                                     new CoalesceParameter(section.getParent().getKey(), Types.CHAR),
                                     new CoalesceParameter(section.getParent().getType()),
                                     new CoalesceParameter(section.getDateCreated().toString(), Types.CHAR),
                                     new CoalesceParameter(section.getLastModified().toString(), Types.CHAR));
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
//        return true;
         String parentKey;
         String parentType;

         if (coalesceObject.getParent() != null)
         {
         parentKey = coalesceObject.getParent().getKey();
         parentType = coalesceObject.getParent().getType();
         }
         else
         {
         parentKey = "00000000-0000-0000-0000-000000000000";
         parentType = "";
         }

         return conn.executeProcedure("CoalesceObjectMap_Insert",
         new CoalesceParameter(parentKey, Types.CHAR),
         new CoalesceParameter(parentType),
         new CoalesceParameter(coalesceObject.getKey(), Types.CHAR),
         new CoalesceParameter(coalesceObject.getType()));
    }

    /**
     * Adds or Updates a Coalesce linkage section that matches the given
     * parameters.
     *
     * @param linkageSection the XsdLinkageSection to be added or updated
     * @param conn           is the SQLDataConnector database connection
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
                                     new CoalesceParameter(linkageSection.getKey(), Types.CHAR),
                                     new CoalesceParameter(linkageSection.getName()),
                                     new CoalesceParameter(linkageSection.getParent().getKey(), Types.CHAR),
                                     new CoalesceParameter(linkageSection.getParent().getType()),
                                     new CoalesceParameter(linkageSection.getDateCreated().toString(), Types.CHAR),
                                     new CoalesceParameter(linkageSection.getLastModified().toString(), Types.CHAR));
    }

    /**
     * Adds or Updates a Coalesce linkage that matches the given parameters.
     *
     * @param linkage the XsdLinkage to be added or updated
     * @param conn    is the SQLDataConnector database connection
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
                                     new CoalesceParameter(linkage.getKey(), Types.CHAR),
                                     new CoalesceParameter(linkage.getName()),
                                     new CoalesceParameter(linkage.getEntity1Key(), Types.CHAR),
                                     new CoalesceParameter(linkage.getEntity1Name()),
                                     new CoalesceParameter(linkage.getEntity1Source()),
                                     new CoalesceParameter(linkage.getEntity1Version()),
                                     new CoalesceParameter(linkage.getLinkType().getLabel()),
                                     new CoalesceParameter(linkage.getStatus().toString()),
                                     new CoalesceParameter(linkage.getEntity2Key(), Types.CHAR),
                                     new CoalesceParameter(linkage.getEntity2Name()),
                                     new CoalesceParameter(linkage.getEntity2Source()),
                                     new CoalesceParameter(linkage.getEntity2Version()),
                                     new CoalesceParameter(linkage.getClassificationMarking().toPortionString()),
                                     new CoalesceParameter(linkage.getModifiedBy()),
                                     new CoalesceParameter(linkage.getInputLang().getDisplayName()),
                                     new CoalesceParameter(linkage.getParent().getKey(), Types.CHAR),
                                     new CoalesceParameter(linkage.getParent().getType()),
                                     new CoalesceParameter(linkage.getDateCreated().toString(), Types.CHAR),
                                     new CoalesceParameter(linkage.getLastModified().toString(), Types.CHAR));
    }






    /**
     * Returns the comparison for the Coalesce object last modified date versus
     * the same objects value in the database.
     *
     * @param coalesceObject the Coalesce object to have it's last modified date
     *                       checked.
     * @param conn           is the SQLDataConnector database connection
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
     * @param conn           is the SQLDataConnector database connection
     * @return True = Successful delete
     * @throws SQLException
     */
    protected boolean deleteObject(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn) throws SQLException
    {
        String objectType = coalesceObject.getType();
        String objectKey = coalesceObject.getKey();
        String tableName = CoalesceTableHelper.getTableNameForObjectType(objectType);

        conn.executeUpdate("DELETE FROM " + getSchemaPrefix() + "CoalesceObjectMap WHERE ObjectKey=?",
                           new CoalesceParameter(objectKey, Types.CHAR));
        conn.executeUpdate("DELETE FROM " + getSchemaPrefix() + tableName + " WHERE ObjectKey=?",
                           new CoalesceParameter(objectKey, Types.CHAR));

        return true;
    }

}



