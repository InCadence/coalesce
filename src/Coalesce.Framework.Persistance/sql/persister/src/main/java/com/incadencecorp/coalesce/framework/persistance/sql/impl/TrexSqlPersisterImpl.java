package com.incadencecorp.coalesce.framework.persistance.sql.impl;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.framework.CoalesceSettings;
import com.incadencecorp.coalesce.framework.datamodel.*;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
import com.incadencecorp.coalesce.framework.persistance.ECoalesceCacheStates;
import com.incadencecorp.coalesce.framework.persistance.ICoalesceCacher;
import com.microsoft.sqlserver.jdbc.Geography;
import com.vividsolutions.jts.geom.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrexSqlPersisterImpl extends SQLPersisterImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrexSqlPersisterImpl.class);
    private ICoalesceCacher _cacher = null;
    private Map<String,String> params;

    public TrexSqlPersisterImpl(Map<String,String> params)
    {
        super(params);
        this.params = params;
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

    //---------------------------------------------------------------------------
    // Protected functions
    //---------------------------------------------------------------------------
    @Override
    protected boolean persistObject(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn)
            throws SQLException, CoalesceDataFormatException
    {
        boolean isSuccessful = true;

        switch (coalesceObject.getType())
        {
        case "entity":
            isSuccessful = persistCoalesceEntityToAllTables(coalesceObject,conn);
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
    //----------------------------------------------------------------------------
    //Helper Functions
    //----------------------------------------------------------------------------
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

    public String getCurrentStatusString(CoalesceRecordset coalesceRecordset)
    {
        String currentStatusString = "";
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
        return currentStatusString;
    }

    //----------------------------------------------------------------------------
    // Identity Hub specific table filling function
    //----------------------------------------------------------------------------
    protected boolean persistCoalesceEntityToAllTables(CoalesceObject coalesceObject, CoalesceDataConnectorBase conn)
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
            //For IdentityHubUnit
            if(isSuccessful && entity.getName().equalsIgnoreCase("IdentityHubUnit"))
            {
                isSuccessful = persistIdentityHubUnit((CoalesceEntity)coalesceObject,conn);
            }
            //For IdentityHubOperation
            if(isSuccessful && entity.getName().equalsIgnoreCase("IdentityHubOperation"))
            {
                isSuccessful = persistIdentityHubOperation((CoalesceEntity)coalesceObject,conn);
            }
            //For IdentityHubSystem
            if(isSuccessful && entity.getName().equalsIgnoreCase("IdentityHubSystem"))
            {
                isSuccessful = persistIdentityHubSystem((CoalesceEntity)coalesceObject,conn);
            }
        }
        return isSuccessful;
    }

    /**
     * Adds or Updates a Coalesce entity that matches the given parameters.
     * Updates the Uploaded to Server Column needed for IdentityHub
     *
     * @param entity the XsdEntity to be added or updated
     * @param conn   is the SQLDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    @Override
    protected boolean persistEntityObject(CoalesceEntity entity, CoalesceDataConnectorBase conn) throws SQLException
    {
        String procedureName = "CoalesceEntity_InsertOrUpdate";

        // Return true if no update is required.
        if (!checkLastModified(entity, conn))
        {
            return true;
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
        params.add(new CoalesceParameter(JodaDateTimeHelper.nowInUtc().toString(), Types.CHAR));

        return conn.executeProcedure(procedureName, params.toArray(new CoalesceParameter[params.size()]))
                && !entity.isMarkedDeleted();
    }
    /**
     * Adds or Updates a IdentityHub System that matches the given parameters.
     *
     * @param entity the action to be added or updated
     * @param conn      is the SQLDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistIdentityHubSystem(CoalesceEntity entity, CoalesceDataConnectorBase conn)
            throws SQLException, CoalesceDataFormatException
    {
        String systemName = null,
                systemType = null,
                systemHardwareName = null,
                systemHardwareType = null,
                systemHardwareSN = null,
                systemSoftwareName = null,
                systemSoftwareSN = null,
                systemStatus = null;

        CoalesceSection coalesceSection = entity.getSection(entity.getName() + "/System Information Section");
        CoalesceRecordset coalesceRecordset = coalesceSection.getRecordset(coalesceSection.getName() + "/System Information Recordset");
        for(CoalesceRecord record : coalesceRecordset.getRecords())
        {
            if (record.getName().equalsIgnoreCase("System Information Recordset Record"))
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
                    case "SYSTEMNAME":
                        systemName = information;
                        break;
                    case "SYSTEMTYPE":
                        systemType = information;
                        break;
                    case "SYSTEMHARDWARENAME":
                        systemHardwareName = information;
                        break;
                    case "SYSTEMHARDWARETYPE":
                        systemHardwareType = information;
                        break;
                    case "SYSTEMHARDWARESN":
                        systemHardwareSN = information;
                        break;
                    case "SYSTEMSOFTWARENAME":
                        systemSoftwareName = information;
                        break;
                    case "SYSTEMSOFTWARESN":
                        systemSoftwareSN = information;
                        break;
                    case "SYSTEMSTATUS":
                        systemStatus = information;
                        break;
                    default:
                        break;
                    }
                }
            }
        }
        // Yes; Call Store Procedure
        return conn.executeProcedure("IdentityHubSystem_InsertOrUpdate",
                                     new CoalesceParameter(entity.getKey(), Types.CHAR),
                                     new CoalesceParameter(systemName),
                                     new CoalesceParameter(systemType),
                                     new CoalesceParameter(systemHardwareName),
                                     new CoalesceParameter(systemHardwareType),
                                     new CoalesceParameter(systemHardwareSN),
                                     new CoalesceParameter(systemSoftwareName),
                                     new CoalesceParameter(systemSoftwareSN),
                                     new CoalesceParameter(systemStatus),
                                     new CoalesceParameter(entity.getDateCreated().toString(),Types.CHAR),
                                     new CoalesceParameter(entity.getLastModified().toString(),Types.CHAR));
    }

    /**
     * Adds or Updates a IdentityHub Operation that matches the given parameters.
     *
     * @param entity the action to be added or updated
     * @param conn      is the SQLDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistIdentityHubOperation(CoalesceEntity entity, CoalesceDataConnectorBase conn)
            throws SQLException, CoalesceDataFormatException
    {
        String operationName = null,
                operationDescription = null,
                operationStartDate = null,
                operationEndDate = null,
                operationAOR = null,
                operationStatus = null;

        CoalesceParameter parentKey = new CoalesceParameter(null,Types.NULL);
        CoalesceSection coalesceSection = entity.getSection(entity.getName() + "/Operation Information Section");
        CoalesceRecordset coalesceRecordset = coalesceSection.getRecordset(coalesceSection.getName() + "/Operation Information Recordset");
        for(CoalesceRecord record : coalesceRecordset.getRecords())
        {
            if (record.getName().equalsIgnoreCase("Operation Information Recordset Record"))
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
                    case "OPERATIONNAME":
                        operationName = information;
                        break;
                    case "OPERATIONDESCRIPTION":
                        operationDescription = information;
                        break;
                    case "OPERATIONSTARTDATE":
                        if(field.getValue() != null)
                            operationStartDate = field.getBaseValue();
                        break;
                    case "OPERATIONENDDATE":
                        operationEndDate = field.getBaseValue();
                        break;
                    case "OPERATIONAOR":
                        operationAOR = information;
                        break;
                    default:
                        break;
                    }
                }
            }
        }
        coalesceSection = entity.getSection(entity.getName() + "/Live Status Section");
        coalesceRecordset = coalesceSection.getRecordset(coalesceSection.getName() + "/Live Status Recordset");
        operationStatus = getCurrentStatusString(coalesceRecordset);
        if(entity.getParent() != null)
        {
            parentKey = new CoalesceParameter(entity.getParent().getKey(),Types.CHAR);
        }
        // Yes; Call Store Procedure
        return conn.executeProcedure("IdentityHubOperation_InsertOrUpdate",
                                     new CoalesceParameter(entity.getKey(),Types.CHAR),
                                     parentKey,
                                     new CoalesceParameter(operationName),
                                     new CoalesceParameter(operationDescription),
                                     new CoalesceParameter(operationStartDate),
                                     new CoalesceParameter(operationEndDate),
                                     new CoalesceParameter(operationAOR),
                                     new CoalesceParameter(operationStatus),
                                     new CoalesceParameter(entity.getDateCreated().toString(),Types.CHAR),
                                     new CoalesceParameter(entity.getLastModified().toString(),Types.CHAR));
    }

    /**
     * Adds or Updates a IdentityHub Unit that matches the given parameters.
     *
     * @param entity the action to be added or updated
     * @param conn      is the SQLDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistIdentityHubUnit(CoalesceEntity entity, CoalesceDataConnectorBase conn)
            throws SQLException, CoalesceDataFormatException
    {
        String unitName = null,
                unitEchelon = null,
                unitType = null,
                unitDescription = null,
                unitStatus = null,
                isApproved = null,
                comments = null;

        CoalesceParameter parentKey = new CoalesceParameter(null,Types.NULL);
        CoalesceSection coalesceSection = entity.getSection(entity.getName() + "/Unit Information Section");
        CoalesceRecordset coalesceRecordset = coalesceSection.getRecordset(coalesceSection.getName() + "/Unit Information Recordset");
        for(CoalesceRecord record : coalesceRecordset.getRecords())
        {
            if (record.getName().equalsIgnoreCase("unit information recordset record"))
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
                    case "UNITNAME":
                        unitName = information;
                        break;
                    case "UNITSTATUS":
                        unitStatus = information;
                        break;
                    case "UNITECHELON":
                        unitEchelon = information;
                        break;
                    case "UNITTYPE":
                        unitType = information;
                        break;
                    case "UNITDESCRIPTION":
                        unitDescription = information;
                        break;
                    case "ISAPPROVED":
                        isApproved = information;
                    case "COMMENTS":
                        comments = information;
                        break;
                    default:
                        break;
                    }
                }
            }
        }

        if(entity.getParent() != null)
        {
            parentKey = new CoalesceParameter(entity.getParent().getKey(),Types.CHAR);
        }
        // Yes; Call Store Procedure
        return conn.executeProcedure("IdentityHubUnit_InsertOrUpdate",
                                     new CoalesceParameter(entity.getKey(),Types.CHAR),
                                     parentKey,
                                     new CoalesceParameter(unitName),
                                     new CoalesceParameter(unitEchelon),
                                     new CoalesceParameter(unitType),
                                     new CoalesceParameter(unitDescription),
                                     new CoalesceParameter(unitStatus),
                                     new CoalesceParameter(isApproved),
                                     new CoalesceParameter(comments),
                                     new CoalesceParameter(entity.getDateCreated().toString(),Types.CHAR),
                                     new CoalesceParameter(entity.getLastModified().toString(),Types.CHAR));
    }

    /**
     * Adds or Updates a IdentityHub Job that matches the given parameters.
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
     * Adds or Updates a IdentityHub Mission that matches the given parameters.
     *
     * @param entity the action to be added or updated
     * @param conn      is the SQLDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
    protected boolean persistIdentityHubMission(CoalesceEntity entity, CoalesceDataConnectorBase conn)
            throws SQLException, CoalesceDataFormatException
    {
        Geography missionLocation = null;
        String missionName = null,
                missionDescription = null,
                missionIndicatorShape = null,
                missionIndicatorColor = null,
                missionIndicatorNumber = null,
                missionIndicatorNumberBase10 = null,
                missionStartDateTime = null,
                missionEndDateTime = null,
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
                    case "MISSIONGEOLOCATION":
                            Coordinate coordinate = (Coordinate) field.getValue();
                        Geography geography = null;
                            if(coordinate == null)
                            {
                                geography = Geography.point(0,0,4326);
                                missionLocation = geography;
                            }else
                            {
                                geography = Geography.point(coordinate.x, coordinate.y, 4326);
                                missionLocation = geography;
                            }
                        break;
                    case "EVENTTITLE":
                        if(information != null)
                        {
                            eventName = information;
                        }else
                        {
                            eventName = "";
                        }
                        break;
                    case "EVENTDESCRIPTION":
                        if(information != null)
                        {
                            eventDescription = information;
                        }else
                        {
                            eventDescription = "";
                        }
                        break;
                    default:
                        break;
                    }
                }
            }
        }
        coalesceSection = entity.getSection(entity.getName() + "/Live Status Section");
        coalesceRecordset = coalesceSection.getRecordset(coalesceSection.getName() + "/Live Status Recordset");
        missionStatus = getCurrentStatusString(coalesceRecordset);

        // Yes; Call Store Procedure
        return conn.executeProcedure("IdentityHubMission_InsertOrUpdate",
                                     new CoalesceParameter(entity.getKey(),Types.CHAR),
                                     parentKey,
                                     new CoalesceParameter(missionName),
                                     new CoalesceParameter(missionDescription),
                                     new CoalesceParameter(missionIndicatorShape),
                                     new CoalesceParameter(missionIndicatorColor),
                                     new CoalesceParameter(missionIndicatorNumber),
                                     new CoalesceParameter(missionIndicatorNumberBase10),
                                     new CoalesceParameter(missionStartDateTime),
                                     new CoalesceParameter(missionEndDateTime),
                                     new CoalesceParameter(missionLocation.toString()),
                                     new CoalesceParameter(missionStatus),
                                     new CoalesceParameter(eventName),
                                     new CoalesceParameter(eventDescription),
                                     new CoalesceParameter(entity.getDateCreated().toString(),Types.CHAR),
                                     new CoalesceParameter(entity.getLastModified().toString(),Types.CHAR));
    }

    /**
     * Adds or Updates a IdentityHub Comments that matches the given parameters.
     *
     * @param object the action to be added or updated
     * @param conn      is the SQLDataConnector database connection
     * @return True = No Update required.
     * @throws SQLException
     */
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
                                     new CoalesceParameter(Integer.toString(commentCount)),
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
     * Fixes the linkage table for Trex specifics
     *
     * LinkStatus
     */
    @Override
    protected boolean persistLinkageObject(CoalesceLinkage linkage, CoalesceDataConnectorBase conn) throws SQLException
    {
        // Return true if no update is required.
        if (!checkLastModified(linkage, conn))
        {
            return true;
        }

        String linkStatus = linkage.getStatus().toString();
        if(linkStatus == "ACTIVE")
        {
            linkStatus = "1";
        }else {
            linkStatus = "2";
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
                                     new CoalesceParameter(linkStatus),
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
}



