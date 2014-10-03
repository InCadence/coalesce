package Coalesce.Objects;

import Coalesce.Framework.DataModel.ECoalesceFieldDataTypes;
import Coalesce.Framework.DataModel.XsdCoordinateField;
import Coalesce.Framework.DataModel.XsdEntity;
import Coalesce.Framework.DataModel.XsdFieldDefinition;
import Coalesce.Framework.DataModel.XsdGUIDField;
import Coalesce.Framework.DataModel.XsdRecord;
import Coalesce.Framework.DataModel.XsdRecordset;
import Coalesce.Framework.DataModel.XsdSection;
import Coalesce.Framework.DataModel.XsdStringField;

public class ActionBaseEntity extends XsdEntity {

    // ----------------------------------------------------------------------//
    // Static Properties
    // ----------------------------------------------------------------------//

    public static final String Name = "Action";
    public static final String Source = "Coalesce";

    // ----------------------------------------------------------------------//
    // Protected Member Variables
    // ----------------------------------------------------------------------//

    private XsdRecord _liveStatusRecord;

    // ----------------------------------------------------------------------//
    // Initialization
    // ----------------------------------------------------------------------//

    public boolean initialize(String source, String version, String title)
    {
        XsdSection section;
        XsdRecordset recordSet;

        // Already Initialized?
        if (_liveStatusRecord != null) return false;

        // Initialize Entity
        if (!super.initialize(ActionBaseEntity.Name, source, version, "", "", title)) return false;

        // Create Live Section
        section = XsdSection.create(this, "Live Status Section");
        recordSet = XsdRecordset.create(section, "Live Status Recordset");
        XsdFieldDefinition.create(recordSet,
                                  "CurrentStatus",
                                  ECoalesceFieldDataTypes.StringType,
                                  "Status",
                                  "",
                                  EActionStatuses.CollectionComplete.getLabel());
        XsdFieldDefinition.create(recordSet, "ResponseStatus", ECoalesceFieldDataTypes.StringType);
        XsdFieldDefinition.create(recordSet, "ResponseKey", ECoalesceFieldDataTypes.GuidType);
        XsdFieldDefinition.create(recordSet, "Location", ECoalesceFieldDataTypes.GeocoordinateType);

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
            XsdRecordset recordSet = (XsdRecordset) this.getDataObjectForNamePath(this.getName()
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
        ((XsdStringField) this._liveStatusRecord.getFieldByName("CurrentStatus")).setValue(value.getLabel());
    }

    // Response Status
    public EResponseStatuses getResponseStatus()
    {
        return EResponseStatuses.fromLabel(this._liveStatusRecord.getFieldByName("ResponseStatus").getBaseValue());
    }

    public void setResponseStatus(EResponseStatuses value)
    {
        ((XsdStringField) this._liveStatusRecord.getFieldByName("ResponseStatus")).setValue(value.getLabel());
    }

    // Response Key
    public XsdGUIDField getResponseKey()
    {
        return (XsdGUIDField) this._liveStatusRecord.getFieldByName("ResponseKey");
    }

    // Location
    public XsdCoordinateField getLocation()
    {
        return (XsdCoordinateField) this._liveStatusRecord.getFieldByName("ResponseKey");
    }

}
