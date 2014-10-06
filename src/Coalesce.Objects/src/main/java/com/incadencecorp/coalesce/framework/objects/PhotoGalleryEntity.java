package com.incadencecorp.coalesce.framework.objects;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;

public class PhotoGalleryEntity extends ActionBaseEntity {

    // ----------------------------------------------------------------------//
    // Static Properties
    // ----------------------------------------------------------------------//

    public static final String Name = ActionBaseEntity.Name;
    public static final String Source = "Photos: Supporting";

    // ----------------------------------------------------------------------//
    // Protected Member Variables
    // ----------------------------------------------------------------------//

    private CoalesceRecordset _photographRecordset;

    // ----------------------------------------------------------------------//
    // Initialization
    // ----------------------------------------------------------------------//

    @Override
    public boolean initialize()
    {
        if (!initializeEntity(PhotoGalleryEntity.Source, "1.0", "")) return false;
        
        // Initialize References
        return this.initializeReferences();
    }

    @Override
    protected boolean initializeEntity(String source, String version, String title)
    {
        CoalesceSection section;

        // Already Initialized?
        if (_photographRecordset != null) return false;

        // Initialize Entity
        if (!super.initializeEntity(source, version, title)) return false;

        // Create Live Section
        section = CoalesceSection.create(this, "Photographs Section");
        _photographRecordset = CoalesceRecordset.create(section, "Photographs Recordset");

        CoalesceFieldDefinition.create(_photographRecordset, "Photo", ECoalesceFieldDataTypes.FILE_TYPE);
        CoalesceFieldDefinition.create(_photographRecordset, "OriginalFilename", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(_photographRecordset, "Height", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(_photographRecordset, "Width", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(_photographRecordset, "Description", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(_photographRecordset, "BagTagID", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(_photographRecordset, "DegreesFromTrueNorth", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(_photographRecordset, "PhotoDateTimeUTC", ECoalesceFieldDataTypes.DATE_TIME_TYPE);
        CoalesceFieldDefinition.create(_photographRecordset, "PhotoLocation", ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE);
        
        return true;
    }

    @Override
    protected boolean initializeReferences()
    {
        if (!super.initializeReferences()) return false;

        if (_photographRecordset == null)
        {
            _photographRecordset = (CoalesceRecordset) getDataObjectForNamePath(getName()
                    + "/Photographs Section/Photographs Recordset");

        }

        return _photographRecordset != null;
    }

    // ----------------------------------------------------------------------//
    // Entity Fields
    // ----------------------------------------------------------------------//

}
