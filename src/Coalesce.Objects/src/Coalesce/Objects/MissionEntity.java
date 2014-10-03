package Coalesce.Objects;

import org.joda.time.DateTime;

import Coalesce.Framework.DataModel.ECoalesceFieldDataTypes;
import Coalesce.Framework.DataModel.XsdCoordinateField;
import Coalesce.Framework.DataModel.XsdDateTimeField;
import Coalesce.Framework.DataModel.XsdEntity;
import Coalesce.Framework.DataModel.XsdField;
import Coalesce.Framework.DataModel.XsdFieldDefinition;
import Coalesce.Framework.DataModel.XsdIntegerField;
import Coalesce.Framework.DataModel.XsdRecord;
import Coalesce.Framework.DataModel.XsdRecordset;
import Coalesce.Framework.DataModel.XsdSection;
import Coalesce.Framework.DataModel.XsdStringField;

public class MissionEntity extends XsdEntity {

    // ----------------------------------------------------------------------//
    // Static Properties
    // ----------------------------------------------------------------------//

    public static final String Name = "Mission";
    public static final String Source = "Coalesce";

    // ----------------------------------------------------------------------//
    // Protected Member Variables
    // ----------------------------------------------------------------------//

    private XsdRecord _informationRecord;
    private XsdRecord _liveStatusRecord;

    // ----------------------------------------------------------------------//
    // Initialization
    // ----------------------------------------------------------------------//

    @Override
    public boolean initialize()
    {
        XsdSection section;
        XsdRecordset recordSet;

        // Already Initialized?
        if (_liveStatusRecord != null || _informationRecord != null) return false;

        // Initialize Entity
        if (!super.initialize(MissionEntity.Name,
                              MissionEntity.Source,
                              "1.0",
                              "",
                              "",
                              "TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionName,TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/IncidentTitle")) return false;

        // Create Live Section
        section = XsdSection.create(this, "Live Status Section");
        recordSet = XsdRecordset.create(section, "Live Status Recordset");
        XsdFieldDefinition.create(recordSet, "CurrentStatus", ECoalesceFieldDataTypes.StringType);

        // Create New Record
        _liveStatusRecord = recordSet.addNew();

        // Create Mission Information Section
        section = XsdSection.create(this, MissionEntity.Name + " Information Section");
        recordSet = XsdRecordset.create(section, MissionEntity.Name + " Information Recordset");
        XsdFieldDefinition.create(recordSet, "ActionNumber", ECoalesceFieldDataTypes.StringType, "Action Number", "U", "0");
        XsdFieldDefinition.create(recordSet, "IncidentNumber", ECoalesceFieldDataTypes.StringType);
        XsdFieldDefinition.create(recordSet, "IncidentTitle", ECoalesceFieldDataTypes.StringType);
        XsdFieldDefinition.create(recordSet, "IncidentDescription", ECoalesceFieldDataTypes.StringType);
        XsdFieldDefinition.create(recordSet, "IncidentDateTime", ECoalesceFieldDataTypes.DateTimeType);
        XsdFieldDefinition.create(recordSet, "MissionName", ECoalesceFieldDataTypes.StringType);
        XsdFieldDefinition.create(recordSet, "MissionType", ECoalesceFieldDataTypes.StringType);
        XsdFieldDefinition.create(recordSet, "MissionDescription", ECoalesceFieldDataTypes.StringType);
        XsdFieldDefinition.create(recordSet, "MissionIndicatorColor", ECoalesceFieldDataTypes.StringType);
        XsdFieldDefinition.create(recordSet, "MissionIndicatorShape", ECoalesceFieldDataTypes.StringType);
        XsdFieldDefinition.create(recordSet, "MissionIndicatorNumber", ECoalesceFieldDataTypes.StringType);
        XsdFieldDefinition.create(recordSet, "MissionIndicatorNumberBASE10", ECoalesceFieldDataTypes.IntegerType);
        XsdFieldDefinition.create(recordSet, "MissionStartDateTime", ECoalesceFieldDataTypes.DateTimeType);
        XsdFieldDefinition.create(recordSet, "MissionEndDateTime", ECoalesceFieldDataTypes.DateTimeType);
        XsdFieldDefinition.create(recordSet, "MissionGeoLocation", ECoalesceFieldDataTypes.GeocoordinateType);
        XsdFieldDefinition.create(recordSet, "MissionAddress", ECoalesceFieldDataTypes.StringType);

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
            XsdRecordset recordSet = (XsdRecordset) this.getDataObjectForNamePath(MissionEntity.Name
                    + "/Live Status Section/Live Status Recordset");

            // Valid Xml?
            if (recordSet == null) return false;

            if (recordSet.getCount() == 0)
            {
                this._liveStatusRecord = recordSet.addNew();
            }
            else
            {
                this._liveStatusRecord = recordSet.GetItem(0);
            }

        }

        // Mission Information Record
        if (this._informationRecord == null)
        {
            XsdRecordset recordSet = (XsdRecordset) this.getDataObjectForNamePath(MissionEntity.Name + "/"
                    + MissionEntity.Name + " Information Section/" + MissionEntity.Name + " Information Recordset");

            // Valid Xml?
            if (recordSet == null) return false;

            if (recordSet.getCount() == 0)
            {
                this._informationRecord = recordSet.addNew();
            }
            else
            {
                this._informationRecord = recordSet.GetItem(0);
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
        ((XsdStringField) this._liveStatusRecord.getFieldByName("CurrentStatus")).setValue(value.getLabel());
    }
    
    public XsdStringField getActionNumber()
    {
        return (XsdStringField) _informationRecord.getFieldByName("ActionNumber");
    }

    public XsdStringField getIncidentNumber()
    {
        return (XsdStringField) _informationRecord.getFieldByName("IncidentNumber");
    }

    public XsdStringField getIncidentTitle()
    {
        return (XsdStringField) _informationRecord.getFieldByName("IncidentTitle");
    }

    public XsdStringField getIncidentDescription()
    {
        return (XsdStringField) _informationRecord.getFieldByName("IncidentDescription");
    }

    public XsdField<DateTime> getIncidentDateTime()
    {
        return (XsdDateTimeField) _informationRecord.getFieldByName("IncidentDateTime");
    }

    public XsdStringField getMissionName()
    {
        return (XsdStringField) _informationRecord.getFieldByName("MissionName");
    }

    public XsdStringField getMissionType()
    {
        return (XsdStringField) _informationRecord.getFieldByName("MissionType");
    }

    public XsdStringField getMissionDescription()
    {
        return (XsdStringField) _informationRecord.getFieldByName("MissionDescription");
    }

    public XsdStringField getMissionIndicatorColor()
    {
        return (XsdStringField) _informationRecord.getFieldByName("MissionIndicatorColor");
    }

    public XsdStringField getMissionIndicatorShape()
    {
        return (XsdStringField) _informationRecord.getFieldByName("MissionIndicatorShape");
    }

    public XsdStringField getMissionIndicatorNumber()
    {
        return (XsdStringField) _informationRecord.getFieldByName("MissionIndicatorNumber");
    }

    public XsdIntegerField getMissionIndicatorNumberBASE10()
    {
        return (XsdIntegerField) _informationRecord.getFieldByName("MissionIndicatorNumberBASE10");
    }

    public XsdDateTimeField getMissionStartDateTime()
    {
        return (XsdDateTimeField) _informationRecord.getFieldByName("MissionStartDateTime");
    }

    public XsdDateTimeField getMissionEndDateTime()
    {
        return (XsdDateTimeField) _informationRecord.getFieldByName("MissionEndDateTime");
    }

    public XsdCoordinateField getMissionGeoLocation()
    {
        return (XsdCoordinateField) _informationRecord.getFieldByName("MissionGeoLocation");
    }

    public XsdStringField getMissionAddress()
    {
        return (XsdStringField) _informationRecord.getFieldByName("MissionAddress");
    }
}
