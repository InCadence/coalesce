package Coalesce.Objects;

import Coalesce.Framework.DataModel.ECoalesceFieldDataTypes;
import Coalesce.Framework.DataModel.CoalesceCoordinateField;
import Coalesce.Framework.DataModel.CoalesceEntity;
import Coalesce.Framework.DataModel.CoalesceFieldDefinition;
import Coalesce.Framework.DataModel.CoalesceGUIDField;
import Coalesce.Framework.DataModel.CoalesceRecord;
import Coalesce.Framework.DataModel.CoalesceRecordset;
import Coalesce.Framework.DataModel.CoalesceSection;
import Coalesce.Framework.DataModel.CoalesceStringField;

public class ActionBaseEntity extends CoalesceEntity {

    // ----------------------------------------------------------------------//
    // Static Properties
    // ----------------------------------------------------------------------//

    public static final String Name = "Action";
    public static final String Source = "Coalesce";

    // ----------------------------------------------------------------------//
    // Protected Member Variables
    // ----------------------------------------------------------------------//

    private CoalesceRecord _liveStatusRecord;

    // ----------------------------------------------------------------------//
    // Initialization
    // ----------------------------------------------------------------------//

    public boolean initialize(String source, String version, String title)
    {
        CoalesceSection section;
        CoalesceRecordset recordSet;

        // Already Initialized?
        if (_liveStatusRecord != null) return false;

        // Initialize Entity
        if (!super.initialize(ActionBaseEntity.Name, source, version, "", "", title)) return false;

        // Create Live Section
        section = CoalesceSection.create(this, "Live Status Section");
        recordSet = CoalesceRecordset.create(section, "Live Status Recordset");
        CoalesceFieldDefinition.create(recordSet,
                                  "CurrentStatus",
                                  ECoalesceFieldDataTypes.StringType,
                                  "Status",
                                  "",
                                  EActionStatuses.CollectionComplete.getLabel());
        CoalesceFieldDefinition.create(recordSet, "ResponseStatus", ECoalesceFieldDataTypes.StringType);
        CoalesceFieldDefinition.create(recordSet, "ResponseKey", ECoalesceFieldDataTypes.GuidType);
        CoalesceFieldDefinition.create(recordSet, "Location", ECoalesceFieldDataTypes.GeocoordinateType);

        // Create New Record
        _liveStatusRecord = recordSet.addNew();

        // Initialize References
        return this.initializeReferences();
    }

    @Override
    protected boolean initializeReferences()
    {

        // Live Status Record
        if (this._liveStatusRecord == null)
        {
            CoalesceRecordset recordSet = (CoalesceRecordset) this.getDataObjectForNamePath(this.getName()
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

        return true;
    }

    // ----------------------------------------------------------------------//
    // Entity Fields
    // ----------------------------------------------------------------------//

    // Current Status
    public EActionStatuses getCurrentStatus()
    {
        return EActionStatuses.fromLabel(this._liveStatusRecord.getFieldByName("CurrentStatus").getBaseValue());
    }

    public void setCurrentStatus(EActionStatuses value)
    {
        ((CoalesceStringField) this._liveStatusRecord.getFieldByName("CurrentStatus")).setValue(value.getLabel());
    }

    // Response Status
    public EResponseStatuses getResponseStatus()
    {
        return EResponseStatuses.fromLabel(this._liveStatusRecord.getFieldByName("ResponseStatus").getBaseValue());
    }

    public void setResponseStatus(EResponseStatuses value)
    {
        ((CoalesceStringField) this._liveStatusRecord.getFieldByName("ResponseStatus")).setValue(value.getLabel());
    }

    // Response Key
    public CoalesceGUIDField getResponseKey()
    {
        return (CoalesceGUIDField) this._liveStatusRecord.getFieldByName("ResponseKey");
    }

    // Location
    public CoalesceCoordinateField getLocation()
    {
        return (CoalesceCoordinateField) this._liveStatusRecord.getFieldByName("ResponseKey");
    }

}
