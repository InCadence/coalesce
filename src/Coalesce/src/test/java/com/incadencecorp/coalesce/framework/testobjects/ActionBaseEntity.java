package com.incadencecorp.coalesce.framework.testobjects;

import java.util.ArrayList;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceCoordinateField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldHistory;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceGUIDField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;

public class ActionBaseEntity extends CoalesceEntity {

    // ----------------------------------------------------------------------//
    // Static Properties
    // ----------------------------------------------------------------------//

    public static final String NAME = "Action";
    public static final String SOURCE = "Coalesce";

    // ----------------------------------------------------------------------//
    // Protected Member Variables
    // ----------------------------------------------------------------------//

    private CoalesceRecord _liveStatusRecord;

    // ----------------------------------------------------------------------//
    // Initialization
    // ----------------------------------------------------------------------//

    @Override
    public boolean initialize()
    {
        if (!initializeEntity("", "", "")) return false;

        return initializeReferences();
    }

    protected boolean initializeEntity(String source, String version, String title)
    {
        CoalesceSection section;
        CoalesceRecordset recordSet;

        // Already Initialized?
        if (_liveStatusRecord != null) return false;

        // Initialize Entity
        if (!super.initializeEntity(ActionBaseEntity.NAME, source, version, "", "", title)) return false;

        // Create Live Section
        section = CoalesceSection.create(this, "Live Status Section");
        recordSet = CoalesceRecordset.create(section, "Live Status Recordset");
        CoalesceFieldDefinition.create(recordSet,
                                       "CurrentStatus",
                                       ECoalesceFieldDataTypes.STRING_TYPE,
                                       "Status",
                                       "",
                                       EActionStatuses.CollectionComplete.getLabel());
        CoalesceFieldDefinition.create(recordSet, "ResponseStatus", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(recordSet, "ResponseKey", ECoalesceFieldDataTypes.GUID_TYPE);
        CoalesceFieldDefinition.create(recordSet, "Location", ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE);

        // Create New Record
        _liveStatusRecord = recordSet.addNew();

        // Initialize References
        return true;
    }

    @Override
    protected boolean initializeReferences()
    {
        if (!super.initializeReferences()) return false;

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
                this._liveStatusRecord = recordSet.getItem(0);
            }

        }

        return _liveStatusRecord != null;
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

    public ArrayList<CoalesceFieldHistory> getCurrentStatusHistory()
    {
        return this._liveStatusRecord.getFieldByName("CurrentStatus").getHistory();
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

    public ArrayList<CoalesceFieldHistory> getResponseStatusHistory()
    {
        return this._liveStatusRecord.getFieldByName("ResponseStatus").getHistory();
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
