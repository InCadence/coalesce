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

import java.util.ArrayList;
import java.util.List;

import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.services.crud.service.data.api.ICoalesceEnumeration;
import com.incadencecorp.coalesce.services.crud.service.data.api.ICoalesceEnumerationValue;

public class CoalesceEnumeration extends CoalesceEntity implements ICoalesceEnumeration<CoalesceEnumerationValue> {

    public static final String NAME = "Enumeration";
    public static final String SOURCE = "Enumeration";
    public static final String VERSION = "1.0";

    public static final String ENUMERTION_SECTION = "enumeration";
    public static final String METADATA_RECORDSET = "metadata";
    public static final String VALUE_RECORDSET = "values";

    private CoalesceRecord metaRecord;
    private CoalesceRecordset valueRecordset;

    /**
     * Default Constructor
     */
    public CoalesceEnumeration()
    {
        // Do Nothing
    }

    @Override
    public boolean initialize()
    {
        boolean isInitialized = false;
        if (initializeEntity(NAME, SOURCE, VERSION, "", "", ""))
        {
            isInitialized = initializeReferences();
        }
        return isInitialized;
    }

    @Override
    protected boolean initializeEntity(String name,
                                       String source,
                                       String version,
                                       String entityId,
                                       String entityIdType,
                                       String title)
    {
        boolean isInitialized = false;

        if (super.initializeEntity(name, source, version, entityId, entityIdType, title))
        {
            // Create Section
            CoalesceSection section = CoalesceSection.create(this, ENUMERTION_SECTION);

            // Create Record Sets
            metaRecord = createMetaRecordset(section).addNew();
            valueRecordset = createValsRecordset(section);

            isInitialized = true;
        }

        return isInitialized;
    }

    @Override
    protected boolean initializeReferences()
    {
        if (super.initializeReferences())
        {
            if (metaRecord == null)
            {
                CoalesceRecordset recordset = getCoalesceRecordsetForNamePath(NAME, ENUMERTION_SECTION, METADATA_RECORDSET);

                if (recordset.getCount() == 0)
                {
                    metaRecord = recordset.addNew();
                }
                else
                {
                    metaRecord = recordset.getItem(0);
                }
            }

            if (valueRecordset == null)
            {
                valueRecordset = getCoalesceRecordsetForNamePath(NAME, ENUMERTION_SECTION, VALUE_RECORDSET);
            }
        }

        return metaRecord != null && valueRecordset != null;
    }

    @Override
    public String getEnumName()
    {
        return getEnumNameField().getValue();
    }

    @Override
    public void setEnumName(String value)
    {
        getEnumNameField().setValue(value);
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
    public Marking getClassification()
    {
        return getEnumNameField().getClassificationMarking();
    }

    @Override
    public void setClassification(Marking value)
    {
        getEnumNameField().setClassificationMarking(value);
    }

    @Override
    public List<CoalesceEnumerationValue> getValues()
    {
        List<CoalesceEnumerationValue> results = new ArrayList<CoalesceEnumerationValue>();

        for (CoalesceRecord record : valueRecordset.getAllRecords())
        {
            results.add(new CoalesceEnumerationValue(record));
        }

        return results;
    }

    @Override
    public void updateValue(CoalesceEnumerationValue value) throws CoalesceDataFormatException
    {
        for (CoalesceRecord record : valueRecordset.getAllRecords())
        {
            CoalesceEnumerationValue ev = new CoalesceEnumerationValue(record);

            if (ev.getValue().equalsIgnoreCase(value.getValue()))
            {
                ev.setDescription(value.getDescription());
                ev.setStatus(value.getStatus());
                ev.setAssociatedValues(value.getAssociatedValues());
            }

        }
    }

    @Override
    public void addValues(List<CoalesceEnumerationValue> values) throws CoalesceDataFormatException
    {
        for (ICoalesceEnumerationValue value : values)
        {
            CoalesceEnumerationValue newVal = new CoalesceEnumerationValue(valueRecordset.addNew());
            
            System.out.println(valueRecordset.toXml());
           System.out.println(newVal.toXml());
            
            newVal.setOrdinal(valueRecordset.getCount());
            newVal.setValue(value.getValue());
            newVal.setDescription(value.getDescription());
            newVal.setStatus(value.getStatus());
            newVal.setAssociatedValues(value.getAssociatedValues());
        }
    }

    private CoalesceStringField getEnumNameField()
    {
        return (CoalesceStringField) metaRecord.getFieldByName("enumname");
    }

    private CoalesceStringField getDescriptionField()
    {
        return (CoalesceStringField) metaRecord.getFieldByName("description");
    }

    private static CoalesceRecordset createMetaRecordset(CoalesceSection section)
    {
        CoalesceRecordset recordset = CoalesceRecordset.create(section, METADATA_RECORDSET);

        // Add a field of each type
        CoalesceFieldDefinition.create(recordset, "enumname", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(recordset, "description", ECoalesceFieldDataTypes.STRING_TYPE);
        
        recordset.setMaxRecords(1);
        recordset.setMinRecords(1);

        return recordset;
    }

    private static CoalesceRecordset createValsRecordset(CoalesceSection section)
    {
        CoalesceRecordset recordset = CoalesceRecordset.create(section, VALUE_RECORDSET);

        // Add a field of each type
        CoalesceFieldDefinition.create(recordset, "value", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(recordset, "ordinal", ECoalesceFieldDataTypes.INTEGER_TYPE);
        CoalesceFieldDefinition.create(recordset, "description", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(recordset, "associatedkeys", ECoalesceFieldDataTypes.STRING_LIST_TYPE);
        CoalesceFieldDefinition.create(recordset, "associatedvalues", ECoalesceFieldDataTypes.STRING_LIST_TYPE);

        recordset.setMaxRecords(500);
        recordset.setMinRecords(0);

        return recordset;
    }
    


}
