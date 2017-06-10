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

package com.incadencecorp.coalesce.plugins.template2java;

import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;

/**
 * Default normalizer for creating keys.
 * 
 * @author n78554
 */
public class ClassNameNormalizer implements ICoalesceNormalizer {

    /**
     * Default Constructor.
     * 
     * @param params
     */
    public ClassNameNormalizer()
    {
    }

    @Override
    public String normalize(final CoalesceRecordset recordset, final CoalesceFieldDefinition definition)
    {
        return normalize(recordset.getName(), definition.getName());
    }

    @Override
    public String normalize(final CoalesceRecordset recordset, final CoalesceField<?> field)
    {
        return normalize(recordset.getName(), field.getName());
    }

    @Override
    public String normalize(final String recordsetname, final String fieldname)
    {
        return normalize(recordsetname) + "." + normalize(fieldname);
    }

    @Override
    public String normalize(final String value)
    {
        String camelCased = "";
        String[] tokens = value.replaceAll(" ", "_").split("_");
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            camelCased = camelCased + token.substring(0, 1).toUpperCase() + token.substring(1, token.length()).toLowerCase();
        }
        return camelCased.replaceAll("[^a-zA-Z0-9_\\s]", "");
    }
}
