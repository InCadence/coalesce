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
public class DOCEXEntity extends DOMEXEntity {

    // ----------------------------------------------------------------------//
    // Static Properties
    // ----------------------------------------------------------------------//

    public static final String SOURCE = "DOCEX";
    public static final String VERSION = "1";

    // ----------------------------------------------------------------------//
    // Private Member Variables
    // ----------------------------------------------------------------------//

    private CoalesceRecordset _DOCEXRecordset;

    // ----------------------------------------------------------------------//
    // Initialization
    // ----------------------------------------------------------------------//

    @Override
    public boolean initialize()
    {
        if (!initializeEntity(DOCEXEntity.SOURCE, DOCEXEntity.VERSION, "")) return false;

        // Initialize References
        return this.initializeReferences();
    }

    @Override
    protected boolean initializeEntity(String source, String version, String title)
    {
        CoalesceSection section;

        // Already Initialized?
        if (_DOCEXRecordset != null) return false; // Yes; Return

        // Initialize Entity
        if (!super.initializeEntity(source, version, title)) return false;

        // Create DOMEX Section
        section = CoalesceSection.create(super.getParentSection(), DOCEXEntity.SOURCE + " Section");
        section.setAttribute("version", DOCEXEntity.VERSION);

        // Create DOMEX Recordset
        _DOCEXRecordset = CoalesceRecordset.create(section, DOCEXEntity.SOURCE + " Recordset");

        // TODO: Replace with DOMEX fields
        CoalesceFieldDefinition.create(_DOCEXRecordset, "Photo", ECoalesceFieldDataTypes.FILE_TYPE);
        CoalesceFieldDefinition.create(_DOCEXRecordset, "OriginalFilename", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(_DOCEXRecordset, "Height", ECoalesceFieldDataTypes.INTEGER_TYPE);
        CoalesceFieldDefinition.create(_DOCEXRecordset, "Width", ECoalesceFieldDataTypes.INTEGER_TYPE);
        CoalesceFieldDefinition.create(_DOCEXRecordset, "Description", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(_DOCEXRecordset, "BagTagID", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(_DOCEXRecordset, "DegreesFromTrueNorth", ECoalesceFieldDataTypes.INTEGER_TYPE);
        CoalesceFieldDefinition.create(_DOCEXRecordset, "PhotoDateTimeUTC", ECoalesceFieldDataTypes.DATE_TIME_TYPE);
        CoalesceFieldDefinition.create(_DOCEXRecordset, "PhotoLocation", ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE);

        return true;
    }

    @Override
    protected boolean initializeReferences()
    {
        if (!super.initializeReferences()) return false;

        if (_DOCEXRecordset == null)
        {
            _DOCEXRecordset = (CoalesceRecordset) getCoalesceObjectForNamePath(getName()
                    + "/Photographs Section/Photographs Recordset");

        }

        return _DOCEXRecordset != null;
    }

    @Override
    protected CoalesceSection getParentSection()
    {
        return (CoalesceSection) this._DOCEXRecordset.getParent();
    }

    // ----------------------------------------------------------------------//
    // Entity Fields
    // ----------------------------------------------------------------------//

    // TODO: Expose Fields

}
