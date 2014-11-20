package com.incadencecorp.coalesce.framework.objects;

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

/**
 *
 */
public class IntelligenceEntity extends CoalesceEntity {

    // ----------------------------------------------------------------------//
    // Static Properties
    // ----------------------------------------------------------------------//

    public static final String NAME = "Intelligence";
    public static final String SOURCE = "Coalesce";
    public static final String VERSION = "1";

    // ----------------------------------------------------------------------//
    // Protected Member Variables
    // ----------------------------------------------------------------------//

    private CoalesceRecord _intelligenceRecord;

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
        if (_intelligenceRecord != null) return false;

        // Initialize Entity
        if (!super.initializeEntity(IntelligenceEntity.NAME, source, version, "", "", title)) return false;

        // Create Intelligence Section
        section = CoalesceSection.create(this, IntelligenceEntity.NAME + " Section");
        section.setAttribute("version", IntelligenceEntity.VERSION);

        // Create Intelligence Recordset
        recordSet = CoalesceRecordset.create(section, IntelligenceEntity.NAME + " Recordset");
        
        // TODO: Replace with Intelligence Fields
        CoalesceFieldDefinition.create(recordSet,
                                       "CurrentStatus",
                                       ECoalesceFieldDataTypes.STRING_TYPE,
                                       "Status",
                                       "",
                                       EIntelligenceStatuses.CollectionComplete.getLabel());
        CoalesceFieldDefinition.create(recordSet, "ResponseStatus", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(recordSet, "ResponseKey", ECoalesceFieldDataTypes.GUID_TYPE);
        CoalesceFieldDefinition.create(recordSet, "Location", ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE);

        // Create New Record
        _intelligenceRecord = recordSet.addNew();

        // Initialize References
        return true;
    }

    @Override
    protected boolean initializeReferences()
    {
        if (!super.initializeReferences()) return false;

        // Live Status Record
        if (this._intelligenceRecord == null)
        {
            CoalesceRecordset recordSet = (CoalesceRecordset) this.getCoalesceObjectForNamePath(this.getName()
                    + "/Intelligence Section/Intelligence Recordset");

            // Valid Xml?
            if (recordSet == null) return false;

            if (recordSet.getCount() == 0)
            {
                this._intelligenceRecord = recordSet.addNew();
            }
            else
            {
                this._intelligenceRecord = recordSet.getItem(0);
            }

        }

        return _intelligenceRecord != null;
    }

    /**
     * Returns the parent section to be used for nesting modalities. 
     * 
     * @return Returns the sections that contains the intelligence recordset. 
     */
    protected CoalesceSection getParentSection()
    {
        return (CoalesceSection) this._intelligenceRecord.getParent().getParent();
    }
    
    // ----------------------------------------------------------------------//
    // Entity Fields
    // ----------------------------------------------------------------------//

    // Current Status
    public EIntelligenceStatuses getCurrentStatus()
    {
        return EIntelligenceStatuses.fromLabel(this._intelligenceRecord.getFieldByName("CurrentStatus").getBaseValue());
    }

    public void setCurrentStatus(EIntelligenceStatuses value)
    {
        ((CoalesceStringField) this._intelligenceRecord.getFieldByName("CurrentStatus")).setValue(value.getLabel());
    }

    public ArrayList<CoalesceFieldHistory> getCurrentStatusHistory()
    {
        return this._intelligenceRecord.getFieldByName("CurrentStatus").getHistory();
    }

    // Response Status
    public EForensicStatuses getResponseStatus()
    {
        return EForensicStatuses.fromLabel(this._intelligenceRecord.getFieldByName("ResponseStatus").getBaseValue());
    }

    public void setResponseStatus(EForensicStatuses value)
    {
        ((CoalesceStringField) this._intelligenceRecord.getFieldByName("ResponseStatus")).setValue(value.getLabel());
    }

    public ArrayList<CoalesceFieldHistory> getResponseStatusHistory()
    {
        return this._intelligenceRecord.getFieldByName("ResponseStatus").getHistory();
    }

    // Response Key
    public CoalesceGUIDField getResponseKey()
    {
        return (CoalesceGUIDField) this._intelligenceRecord.getFieldByName("ResponseKey");
    }

    // Location
    public CoalesceCoordinateField getLocation()
    {
        return (CoalesceCoordinateField) this._intelligenceRecord.getFieldByName("ResponseKey");
    }

}
