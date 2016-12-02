/*-----------------------------------------------------------------------------'
 Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.api;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;

/**
 * Interface for populating coalesce entities.
 * 
 * @author n78554
 */
public interface ICoalescePopulator {

    /**
     * @param type
     * @return a string that can be used to populate the base value of a field
     *         for the given type.
     */
    String populate(ECoalesceFieldDataTypes type);

    /**
     * Populates the field.
     * 
     * @param field
     */
    void populate(CoalesceField<?> field);

    /**
     * Populates the record's fields.
     * 
     * @param record
     */
    void populate(CoalesceRecord record);

    /**
     * Sets the recordset's fields to random values.
     * 
     * @param recordset
     */
    void populate(CoalesceRecordset recordset);

}
