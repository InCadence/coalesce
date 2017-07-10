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
package com.incadencecorp.coalesce.services.crud.service.data.model.impl.coalesce.factory;
      

import com.incadencecorp.coalesce.api.ICoalesceFieldEnumDefinitionFactory ;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.services.crud.service.data.model.api.record.IMetadataRecord;
import com.incadencecorp.coalesce.services.crud.service.data.model.impl.coalesce.record.MetadataCoalesceRecord.EMetadataFields;

/**
 * Coalesce factory to be used by the Coalesce implementation of {@link IMetadataRecord} generated on 2017-07-07T00:06:59.076Z.
 * 
 * @author coalesce-plugins-template2java
 */
public final class MetadataCoalesceFactory implements ICoalesceFieldEnumDefinitionFactory<EMetadataFields> {

	@Override
    public CoalesceFieldDefinition create(CoalesceRecordset recordset, String name) 
    {
        return create(recordset, EMetadataFields.fromLabel(name)); 
    }

	@Override
    public CoalesceFieldDefinition create(CoalesceRecordset recordset, EMetadataFields field) 
    {
        CoalesceFieldDefinition fd = null;

		if (field != null)
		{
			switch (field) 
       		{
            case ENUMNAME:
                fd = CoalesceFieldDefinition.create(recordset, "enumname", ECoalesceFieldDataTypes.STRING_TYPE, "", "UNCLASSIFIED", "", false, false);
                break;
            case DESCRIPTION:
                fd = CoalesceFieldDefinition.create(recordset, "description", ECoalesceFieldDataTypes.STRING_TYPE, "", "UNCLASSIFIED", "", false, false);
                break;
 			}	
		}
        return fd; 
    }
}
