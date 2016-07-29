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
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;

/**
 * Interface used for normalizing Coalesce objects to be used as keys.
 * 
 * @author n78554
 */
public interface ICoalesceNormalizer {

    /**
     * @param recordset
     * @param definition
     * @return a normalized string that represents the given CoalesceRecordset and CoalesceFieldDefinition
     */
    String normalize(final CoalesceRecordset recordset, final CoalesceFieldDefinition definition);

    /**
     * @param recordset
     * @param field
     * @return a normalized string that represents the given CoalesceRecordset and CoalesceField
     */
    String normalize(final CoalesceRecordset recordset, final CoalesceField<?> field);

    /**
     * @param recordsetname
     * @param fieldname
     * @return a normalized string that represents the given CoalesceRecordset and CoalesceField
     */
    String normalize(final String recordsetname, final String fieldname);

    /**
     * @param name
     * @return a normalized string.
     */
    String normalize(final String name);

}
