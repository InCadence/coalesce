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


package com.incadencecorp.coalesce.framework.populators;

import com.incadencecorp.coalesce.api.ICoalescePopulator;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;

/**
 * Partial implementation of {@link ICoalescePopulator} leaving
 * {@link #populate(com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes)}
 * up to the extender.
 * 
 * @author n78554
 *
 */
public abstract class AbstractPopulator implements ICoalescePopulator {

    @Override
    public void populate(CoalesceField<?> field)
    {
        field.setAttribute(CoalesceField.ATTRIBUTE_VALUE, this.populate(field.getDataType()));
    }

    @Override
    public void populate(CoalesceRecord record)
    {
        for (CoalesceField<?> field : record.getFields())
        {
            populate(field);
        }
    }
    
    @Override
    public void populate(CoalesceRecordset recordset)
    {
        for (CoalesceRecord record : recordset.getRecords())
        {
            populate(record);
        }
    }
    
}
