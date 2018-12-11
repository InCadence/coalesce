package com.incadencecorp.coalesce.framework.persistance.sql.impl;

import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.api.IEnumerationProvider;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.enumerationprovider.impl.ResourceEnumerationProviderImpl;

import java.util.List;

public class SQLNormalizer implements ICoalesceNormalizer {

    private List<String> keywords;

    public SQLNormalizer(){
        IEnumerationProvider provider = new ResourceEnumerationProviderImpl(this.getClass());
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
        name = name.toUpperCase();
        return keywords.contains(name) ? "\"" + name.toLowerCase() + "\"" : name.toLowerCase();
    }
}
