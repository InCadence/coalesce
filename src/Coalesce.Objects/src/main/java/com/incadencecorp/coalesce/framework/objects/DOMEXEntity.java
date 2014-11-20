package com.incadencecorp.coalesce.framework.objects;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
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
public class DOMEXEntity extends IntelligenceEntity {

    // ----------------------------------------------------------------------//
    // Static Properties
    // ----------------------------------------------------------------------//

    public static final String SOURCE = "DOMEX";
    public static final String VERSION = "1";

    // ----------------------------------------------------------------------//
    // Private Member Variables
    // ----------------------------------------------------------------------//

    private CoalesceRecordset _DOMEXRecordset;

    // ----------------------------------------------------------------------//
    // Initialization
    // ----------------------------------------------------------------------//

    @Override
    public boolean initialize()
    {
        if (!initializeEntity(DOMEXEntity.SOURCE, DOMEXEntity.VERSION, "")) return false;

        // Initialize References
        return this.initializeReferences();
    }

    @Override
    protected boolean initializeEntity(String source, String version, String title)
    {
        CoalesceSection section;

        // Already Initialized?
        if (_DOMEXRecordset != null) return false; // Yes; Return

        // Initialize Entity
        if (!super.initializeEntity(source, version, title)) return false;

        // Create DOMEX Section
        section = CoalesceSection.create(super.getParentSection(), DOMEXEntity.SOURCE + " Section");
        section.setAttribute("version", DOMEXEntity.VERSION);

        // Create DOMEX Recordset
        _DOMEXRecordset = CoalesceRecordset.create(section, DOMEXEntity.SOURCE + " Recordset");

        // TODO: Replace with DOMEX fields
        CoalesceFieldDefinition.create(_DOMEXRecordset, "Photo", ECoalesceFieldDataTypes.FILE_TYPE);
        CoalesceFieldDefinition.create(_DOMEXRecordset, "OriginalFilename", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(_DOMEXRecordset, "Height", ECoalesceFieldDataTypes.INTEGER_TYPE);
        CoalesceFieldDefinition.create(_DOMEXRecordset, "Width", ECoalesceFieldDataTypes.INTEGER_TYPE);
        CoalesceFieldDefinition.create(_DOMEXRecordset, "Description", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(_DOMEXRecordset, "BagTagID", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(_DOMEXRecordset, "DegreesFromTrueNorth", ECoalesceFieldDataTypes.INTEGER_TYPE);
        CoalesceFieldDefinition.create(_DOMEXRecordset, "PhotoDateTimeUTC", ECoalesceFieldDataTypes.DATE_TIME_TYPE);
        CoalesceFieldDefinition.create(_DOMEXRecordset, "PhotoLocation", ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE);

        return true;
    }

    @Override
    protected boolean initializeReferences()
    {
        if (!super.initializeReferences()) return false;

        if (_DOMEXRecordset == null)
        {
            _DOMEXRecordset = (CoalesceRecordset) getCoalesceObjectForNamePath(getName()
                    + "/Photographs Section/Photographs Recordset");

        }

        return _DOMEXRecordset != null;
    }

    @Override
    protected CoalesceSection getParentSection()
    {
        return (CoalesceSection) this._DOMEXRecordset.getParent();
    }

    // ----------------------------------------------------------------------//
    // Entity Fields
    // ----------------------------------------------------------------------//

    // TODO: Expose Fields

}
