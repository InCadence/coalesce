/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.services.crud.service.data.model;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.services.crud.service.data.api.ICoalesceEnumeration;
import com.incadencecorp.coalesce.services.crud.service.data.api.ICoalesceEnumerationValue;

public class EnumerationConverter {

    public static void convert(ICoalesceEnumerationValue source, ICoalesceEnumerationValue target)
            throws CoalesceDataFormatException
    {
        target.setValue(source.getValue());
        target.setStatus(source.getStatus());
        target.setOrdinal(source.getOrdinal());
        target.setDescription(source.getDescription());
        target.setAssociatedValues(source.getAssociatedValues());
    }
    
    public static void convert(ICoalesceEnumeration source, ICoalesceEnumeration target)
            throws CoalesceDataFormatException
    {
        target.setClassification(source.getClassification());
        target.setEnumName(source.getEnumName());
        target.setDescription(source.getDescription());
        target.addValues(source.getValues());
        target.setLastModified(source.getLastModified());
        target.setModifiedBy(source.getModifiedBy());
    }

}
