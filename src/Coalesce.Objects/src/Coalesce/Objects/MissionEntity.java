package Coalesce.Objects;

import Coalesce.Framework.DataModel.ECoalesceFieldDataTypes;
import Coalesce.Framework.DataModel.XsdEntity;
import Coalesce.Framework.DataModel.XsdField;
import Coalesce.Framework.DataModel.XsdFieldDefinition;
import Coalesce.Framework.DataModel.XsdRecord;
import Coalesce.Framework.DataModel.XsdRecordset;
import Coalesce.Framework.DataModel.XsdSection;

public class MissionEntity extends XsdEntity {

    // ----------------------------------------------------------------------//
    // Static Properties
    // ----------------------------------------------------------------------//

    public static String Name = "Mission";
    public static String Source = "Coalesce";

    // ----------------------------------------------------------------------//
    // Protected Member Variables
    // ----------------------------------------------------------------------//

    private XsdRecord _informationRecord;
    private XsdRecord _liveStatusRecord;

    // ----------------------------------------------------------------------//
    // Constructors
    // ----------------------------------------------------------------------//

    public MissionEntity()
    {
        
    }

    public MissionEntity(String entityXml)
    {
        initialize(entityXml);
    }

    // ----------------------------------------------------------------------//
    // Initialization
    // ----------------------------------------------------------------------//

    @Override
    public boolean initialize()
    {
        XsdSection section;
        XsdRecordset recordSet;

        // Initialize Entity
        if (!super.initialize()) return false;

        this.setName(MissionEntity.Name);
        this.setSource(MissionEntity.Source);
        this.setVersion("1.0");
        this.setEntityId("");
        this.setEntityIdType("");
        this.setTitle("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionName,TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/IncidentTitle");

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

            if (recordSet != null)
            {
                if (recordSet.getCount() == 0)
                {
                    this._liveStatusRecord = recordSet.addNew();
                }
                else
                {
                    this._liveStatusRecord = recordSet.GetItem(0);
                }
            }

        }

        // Mission Information Record
        if (this._informationRecord == null)
        {
            XsdRecordset recordSet = (XsdRecordset) this.getDataObjectForNamePath(MissionEntity.Name + "/"
                    + MissionEntity.Name + " Information Section/" + MissionEntity.Name + " Information Recordset");

            if (recordSet != null)
            {
                if (recordSet.getCount() == 0)
                {
                    this._informationRecord = recordSet.addNew();
                }
                else
                {
                    this._informationRecord = recordSet.GetItem(0);
                }
            }

        }

        return true;
    }

    // ----------------------------------------------------------------------//
    // Entity Fields
    // ----------------------------------------------------------------------//

    public XsdField getActionNumber()
    {
        return _informationRecord.getFieldByName("ActionNumber");
    }

    public XsdField getIncidentNumber()
    {
        return _informationRecord.getFieldByName("IncidentNumber");
    }

    public XsdField getIncidentTitle()
    {
        return _informationRecord.getFieldByName("IncidentTitle");
    }

    public XsdField getIncidentDescription()
    {
        return _informationRecord.getFieldByName("IncidentDescription");
    }

    public XsdField getIncidentDateTime()
    {
        return _informationRecord.getFieldByName("IncidentDateTime");
    }

    public XsdField getMissionName()
    {
        return _informationRecord.getFieldByName("MissionName");
    }

    public XsdField getMissionType()
    {
        return _informationRecord.getFieldByName("MissionType");
    }

    public XsdField getMissionDescription()
    {
        return _informationRecord.getFieldByName("MissionDescription");
    }

    public XsdField getMissionIndicatorColor()
    {
        return _informationRecord.getFieldByName("MissionIndicatorColor");
    }

    public XsdField getMissionIndicatorShape()
    {
        return _informationRecord.getFieldByName("MissionIndicatorShape");
    }

    public XsdField getMissionIndicatorNumber()
    {
        return _informationRecord.getFieldByName("MissionIndicatorNumber");
    }

    public XsdField getMissionIndicatorNumberBASE10()
    {
        return _informationRecord.getFieldByName("MissionIndicatorNumberBASE10");
    }

    public XsdField getMissionStartDateTime()
    {
        return _informationRecord.getFieldByName("MissionStartDateTime");
    }

    public XsdField getMissionEndDateTime()
    {
        return _informationRecord.getFieldByName("MissionEndDateTime");
    }

    public XsdField getMissionGeoLocation()
    {
        return _informationRecord.getFieldByName("MissionGeoLocation");
    }

    public XsdField getMissionAddress()
    {
        return _informationRecord.getFieldByName("MissionAddress");
    }
}
