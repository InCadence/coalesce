package com.incadencecorp.coalesce.framework.testobjects;

import org.joda.time.DateTime;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceCoordinateField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDateTimeField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIntegerField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.testobjects.EMissionStatuses;

public class MissionEntity extends CoalesceEntity {

    // ----------------------------------------------------------------------//
    // Static Properties
    // ----------------------------------------------------------------------//

    public static final String NAME = "Mission";
    public static final String SOURCE = "Coalesce";

    // ----------------------------------------------------------------------//
    // Protected Member Variables
    // ----------------------------------------------------------------------//

    private CoalesceRecord _informationRecord;
    private CoalesceRecord _liveStatusRecord;

    // ----------------------------------------------------------------------//
    // Initialization
    // ----------------------------------------------------------------------//

    @Override
    public boolean initialize()
    {
        if (!initializeEntity(MissionEntity.SOURCE,
                              "1.0",
                              "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionName,TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/IncidentTitle")) return false;

        return initializeReferences();
    }

    protected boolean initializeEntity(String source, String version, String title)
    {
        CoalesceSection section;
        CoalesceRecordset recordSet;

        // Already Initialized?
        if (_liveStatusRecord != null || _informationRecord != null) return false;

        // Initialize Entity
        if (!super.initializeEntity(MissionEntity.NAME, source, version, "", "", title)) return false;

        // Create Live Section
        section = CoalesceSection.create(this, "Live Status Section");
        recordSet = CoalesceRecordset.create(section, "Live Status Recordset");
        CoalesceFieldDefinition.create(recordSet, "CurrentStatus", ECoalesceFieldDataTypes.STRING_TYPE);

        // Create New Record
        _liveStatusRecord = recordSet.addNew();

        // Create Mission Information Section
        section = CoalesceSection.create(this, MissionEntity.NAME + " Information Section");
        recordSet = CoalesceRecordset.create(section, MissionEntity.NAME + " Information Recordset");
        CoalesceFieldDefinition.create(recordSet, "ActionNumber", ECoalesceFieldDataTypes.STRING_TYPE, "Action Number", "U", "0");
        CoalesceFieldDefinition.create(recordSet, "IncidentNumber", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(recordSet, "IncidentTitle", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(recordSet, "IncidentDescription", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(recordSet, "IncidentDateTime", ECoalesceFieldDataTypes.DATE_TIME_TYPE);
        CoalesceFieldDefinition.create(recordSet, "MissionName", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(recordSet, "MissionType", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(recordSet, "MissionDescription", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(recordSet, "MissionIndicatorColor", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(recordSet, "MissionIndicatorShape", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(recordSet, "MissionIndicatorNumber", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(recordSet, "MissionIndicatorNumberBASE10", ECoalesceFieldDataTypes.INTEGER_TYPE);
        CoalesceFieldDefinition.create(recordSet, "MissionStartDateTime", ECoalesceFieldDataTypes.DATE_TIME_TYPE);
        CoalesceFieldDefinition.create(recordSet, "MissionEndDateTime", ECoalesceFieldDataTypes.DATE_TIME_TYPE);
        CoalesceFieldDefinition.create(recordSet, "MissionGeoLocation", ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE);
        CoalesceFieldDefinition.create(recordSet, "MissionAddress", ECoalesceFieldDataTypes.STRING_TYPE);

        _informationRecord = recordSet.addNew();

        // Initialize References
        return this.initializeReferences();
    }

    @Override
    protected boolean initializeReferences()
    {

        // Live Status Record
        if (this._liveStatusRecord == null)
        {
            CoalesceRecordset recordSet = (CoalesceRecordset) this.getCoalesceObjectForNamePath(MissionEntity.NAME
                    + "/Live Status Section/Live Status Recordset");

            // Valid Xml?
            if (recordSet == null) return false;

            if (recordSet.getCount() == 0)
            {
                this._liveStatusRecord = recordSet.addNew();
            }
            else
            {
                this._liveStatusRecord = recordSet.getItem(0);
            }

        }

        // Mission Information Record
        if (this._informationRecord == null)
        {
            CoalesceRecordset recordSet = (CoalesceRecordset) this.getCoalesceObjectForNamePath(MissionEntity.NAME + "/"
                    + MissionEntity.NAME + " Information Section/" + MissionEntity.NAME + " Information Recordset");

            // Valid Xml?
            if (recordSet == null) return false;

            if (recordSet.getCount() == 0)
            {
                this._informationRecord = recordSet.addNew();
            }
            else
            {
                this._informationRecord = recordSet.getItem(0);
            }

        }

        return true;
    }

    // ----------------------------------------------------------------------//
    // Entity Fields
    // ----------------------------------------------------------------------//

    // Current Status
    public EMissionStatuses getCurrentStatus()
    {
        return EMissionStatuses.fromLabel(this._liveStatusRecord.getFieldByName("CurrentStatus").getBaseValue());
    }

    public void setCurrentStatus(EMissionStatuses value)
    {
        ((CoalesceStringField) this._liveStatusRecord.getFieldByName("CurrentStatus")).setValue(value.getLabel());
    }

    public CoalesceStringField getActionNumber()
    {
        return (CoalesceStringField) _informationRecord.getFieldByName("ActionNumber");
    }

    public CoalesceStringField getIncidentNumber()
    {
        return (CoalesceStringField) _informationRecord.getFieldByName("IncidentNumber");
    }

    public CoalesceStringField getIncidentTitle()
    {
        return (CoalesceStringField) _informationRecord.getFieldByName("IncidentTitle");
    }

    public CoalesceStringField getIncidentDescription()
    {
        return (CoalesceStringField) _informationRecord.getFieldByName("IncidentDescription");
    }

    public CoalesceField<DateTime> getIncidentDateTime()
    {
        return (CoalesceDateTimeField) _informationRecord.getFieldByName("IncidentDateTime");
    }

    public CoalesceStringField getMissionName()
    {
        return (CoalesceStringField) _informationRecord.getFieldByName("MissionName");
    }

    public CoalesceStringField getMissionType()
    {
        return (CoalesceStringField) _informationRecord.getFieldByName("MissionType");
    }

    public CoalesceStringField getMissionDescription()
    {
        return (CoalesceStringField) _informationRecord.getFieldByName("MissionDescription");
    }

    public CoalesceStringField getMissionIndicatorColor()
    {
        return (CoalesceStringField) _informationRecord.getFieldByName("MissionIndicatorColor");
    }

    public CoalesceStringField getMissionIndicatorShape()
    {
        return (CoalesceStringField) _informationRecord.getFieldByName("MissionIndicatorShape");
    }

    public CoalesceStringField getMissionIndicatorNumber()
    {
        return (CoalesceStringField) _informationRecord.getFieldByName("MissionIndicatorNumber");
    }

    public CoalesceIntegerField getMissionIndicatorNumberBASE10()
    {
        return (CoalesceIntegerField) _informationRecord.getFieldByName("MissionIndicatorNumberBASE10");
    }

    public CoalesceDateTimeField getMissionStartDateTime()
    {
        return (CoalesceDateTimeField) _informationRecord.getFieldByName("MissionStartDateTime");
    }

    public CoalesceDateTimeField getMissionEndDateTime()
    {
        return (CoalesceDateTimeField) _informationRecord.getFieldByName("MissionEndDateTime");
    }

    public CoalesceCoordinateField getMissionGeoLocation()
    {
        return (CoalesceCoordinateField) _informationRecord.getFieldByName("MissionGeoLocation");
    }

    public CoalesceStringField getMissionAddress()
    {
        return (CoalesceStringField) _informationRecord.getFieldByName("MissionAddress");
    }
}
