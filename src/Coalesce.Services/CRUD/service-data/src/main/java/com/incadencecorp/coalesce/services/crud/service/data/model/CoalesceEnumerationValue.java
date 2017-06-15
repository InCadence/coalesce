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

import java.util.HashMap;
import java.util.Map;

import com.incadencecorp.coalesce.api.ICoalesceFieldDefinitionFactory;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIntegerField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringListField;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.services.crud.service.data.api.ICoalesceEnumerationValue;

public class CoalesceEnumerationValue extends CoalesceRecord implements ICoalesceEnumerationValue {

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    public CoalesceEnumerationValue()
    {
        super();
    }

    /**
     * Constructs a new instance from an existing record
     * 
     * @param record
     */
    public CoalesceEnumerationValue(CoalesceRecord record)
    {
        super(record);
    }

    @Override
    public int getOrdinal() throws CoalesceDataFormatException
    {
        return getOridinalField().getValue();
    }

    @Override
    public void setOrdinal(int value)
    {
        getOridinalField().setValue(value);
    }

    @Override
    public String getValue()
    {
        return getValueField().getValue();
    }

    @Override
    public void setValue(String value)
    {
        getValueField().setValue(value);
    }

    @Override
    public String getDescription()
    {
        return getDescriptionField().getValue();
    }

    @Override
    public void setDescription(String value)
    {
        getDescriptionField().setValue(value);
    }

    @Override
    public Map<String, String> getAssociatedValues() throws CoalesceDataFormatException
    {
        String[] keys = getAssociatedKeysField().getValue();
        String[] vals = getAssociatedValsField().getValue();

        Map<String, String> results = new HashMap<String, String>();

        for (int ii = 0; ii < keys.length && ii < vals.length; ii++)
        {
            results.put(keys[ii], vals[ii]);
        }

        return results;
    }

    @Override
    public void setAssociatedValues(Map<String, String> value)
    {
        String[] keys = new String[value.size()];
        String[] vals = new String[value.size()];

        int ii = 0;

        for (Map.Entry<String, String> entry : value.entrySet())
        {

            keys[ii] = entry.getKey();
            vals[ii] = entry.getValue();

            ii++;
        }

        getAssociatedKeysField().setValue(keys);
        getAssociatedValsField().setValue(vals);
    }

    private class EnumerationFieldFactory implements ICoalesceFieldDefinitionFactory {

        @Override
        public CoalesceFieldDefinition create(CoalesceRecordset recordset, String name)
        {
            CoalesceFieldDefinition fd = null;
            
            switch (name) {
            case "value":
                fd =CoalesceFieldDefinition.create(recordset, "value", ECoalesceFieldDataTypes.STRING_TYPE);
                break;
            case "ordinal":
                fd =CoalesceFieldDefinition.create(recordset, "oridinal", ECoalesceFieldDataTypes.INTEGER_TYPE);
                break;
            case "description":
                fd =CoalesceFieldDefinition.create(recordset, "description", ECoalesceFieldDataTypes.STRING_TYPE);
                break;
            case "associatedkeys":
                fd =CoalesceFieldDefinition.create(recordset, "associatedkeys", ECoalesceFieldDataTypes.STRING_LIST_TYPE);
                break;
            case "associatedvalues":
                fd =CoalesceFieldDefinition.create(recordset, "associatedvalues", ECoalesceFieldDataTypes.STRING_LIST_TYPE);
                break;
            }
            
            return fd;
        }

    }

    private CoalesceIntegerField getOridinalField()
    {
        return (CoalesceIntegerField) getFieldByName("ordinal", new EnumerationFieldFactory());
    }

    private CoalesceStringField getValueField()
    {
        return (CoalesceStringField) getFieldByName("value");
    }

    private CoalesceStringField getDescriptionField()
    {
        return (CoalesceStringField) getFieldByName("description");
    }

    private CoalesceStringListField getAssociatedKeysField()
    {
        return (CoalesceStringListField) getFieldByName("associatedkeys");
    }

    private CoalesceStringListField getAssociatedValsField()
    {
        return (CoalesceStringListField) getFieldByName("associatedvalues");
    }

}
