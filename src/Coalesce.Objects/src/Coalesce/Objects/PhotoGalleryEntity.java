package Coalesce.Objects;

import Coalesce.Framework.DataModel.ECoalesceFieldDataTypes;
import Coalesce.Framework.DataModel.XsdFieldDefinition;
import Coalesce.Framework.DataModel.XsdRecordset;
import Coalesce.Framework.DataModel.XsdSection;

public class PhotoGalleryEntity extends ActionBaseEntity {

    // ----------------------------------------------------------------------//
    // Static Properties
    // ----------------------------------------------------------------------//

    public static final String Name = ActionBaseEntity.Name;
    public static final String Source = "Photos: Supporting";

    // ----------------------------------------------------------------------//
    // Protected Member Variables
    // ----------------------------------------------------------------------//

    private XsdRecordset _photographRecordset;

    // ----------------------------------------------------------------------//
    // Initialization
    // ----------------------------------------------------------------------//

    @Override
    public boolean initialize()
    {
        XsdSection section;

        // Already Initialized?
        if (_photographRecordset != null) return false;

        // Initialize Entity
        if (!super.initialize(PhotoGalleryEntity.Source, "1.0", "")) return false;

        // Create Live Section
        section = XsdSection.create(this, "Photographs Section");
        _photographRecordset = XsdRecordset.create(section, "Photographs Recordset");

        XsdFieldDefinition.create(_photographRecordset, "Photo", ECoalesceFieldDataTypes.FileType);
        XsdFieldDefinition.create(_photographRecordset, "OriginalFilename", ECoalesceFieldDataTypes.StringType);
        XsdFieldDefinition.create(_photographRecordset, "Height", ECoalesceFieldDataTypes.StringType);
        XsdFieldDefinition.create(_photographRecordset, "Width", ECoalesceFieldDataTypes.StringType);
        XsdFieldDefinition.create(_photographRecordset, "Description", ECoalesceFieldDataTypes.StringType);
        XsdFieldDefinition.create(_photographRecordset, "BagTagID", ECoalesceFieldDataTypes.StringType);
        XsdFieldDefinition.create(_photographRecordset, "DegreesFromTrueNorth", ECoalesceFieldDataTypes.StringType);
        XsdFieldDefinition.create(_photographRecordset, "PhotoDateTimeUTC", ECoalesceFieldDataTypes.DateTimeType);
        XsdFieldDefinition.create(_photographRecordset, "PhotoLocation", ECoalesceFieldDataTypes.GeocoordinateType);

        // Initialize References
        return this.initializeReferences();
    }

    @Override
    protected boolean initializeReferences()
    {
        if (_photographRecordset == null)
        {
            _photographRecordset = (XsdRecordset) getDataObjectForNamePath(getName()
                    + "/Photographs Section/Photographs Recordset");

        }

        return _photographRecordset != null;
    }
    
    // ----------------------------------------------------------------------//
    // Entity Fields
    // ----------------------------------------------------------------------//


}
