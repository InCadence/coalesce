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

package com.incadencecorp.coalesce.framework.persistance.derby;

import java.util.List;

import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.api.IEnumerationProvider;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.enumerationprovider.impl.ResourceEnumerationProviderImpl;

public class DerbyNormalizer implements ICoalesceNormalizer {

    private List<String> keywords;

    public DerbyNormalizer()
    {
        IEnumerationProvider provider = new ResourceEnumerationProviderImpl();
        keywords = provider.getValues(null, "keywords");
    }

    @Override
    public String normalize(CoalesceRecordset recordset, CoalesceFieldDefinition definition)
    {
        return normalize(recordset.getName(), definition.getName());
    }

    @Override
    public String normalize(CoalesceRecordset recordset, CoalesceField<?> field)
    {
        return normalize(recordset.getName(), field.getName());
    }

    @Override
    public String normalize(String recordsetname, String fieldname)
    {
        return normalize(recordsetname) + "." + normalize(fieldname);
    }

    @Override
    public String normalize(String name)
    {
        return keywords.contains(name.toUpperCase())? "\"" + name + "\"": name;
    }

}
