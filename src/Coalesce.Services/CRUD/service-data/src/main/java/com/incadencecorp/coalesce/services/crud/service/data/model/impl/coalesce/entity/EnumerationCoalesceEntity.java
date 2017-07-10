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
package com.incadencecorp.coalesce.services.crud.service.data.model.impl.coalesce.entity;
    	 	 			 		 		 		 		 	 	
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.services.crud.service.data.model.api.entity.IEnumerationEntity;
import com.incadencecorp.coalesce.services.crud.service.data.model.api.record.IMetadataRecord;
import com.incadencecorp.coalesce.services.crud.service.data.model.api.record.IValuesRecord;
import com.incadencecorp.coalesce.services.crud.service.data.model.impl.coalesce.record.MetadataCoalesceRecord;
import com.incadencecorp.coalesce.services.crud.service.data.model.impl.coalesce.record.ValuesCoalesceRecord;
import java.util.ArrayList;
import java.util.List;

/**
 * Coalesce implementation of {@link IEnumerationEntity} generated on 2017-07-07T00:06:59.076Z.
 * 
 * @author coalesce-plugins-template2java
 */
public class EnumerationCoalesceEntity extends CoalesceEntity implements IEnumerationEntity {

    public static final String NAME = "Enumeration";
    public static final String SOURCE = "Enumeration";
    public static final String VERSION = "1.0";

    public static final String RECORDSET_VALUES = "values";
    public static final String RECORDSET_METADATA = "metadata";

    private CoalesceRecordset recordset_Values;
    private MetadataCoalesceRecord record_Metadata;

    /**
     * Default Constructor
     */
    public EnumerationCoalesceEntity()
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
            CoalesceSection section = CoalesceSection.create(this, "enumeration");

            recordset_Values = ValuesCoalesceRecord.createCoalesceRecordset(section, RECORDSET_VALUES);
            record_Metadata = new MetadataCoalesceRecord(MetadataCoalesceRecord.createCoalesceRecordset(section, RECORDSET_METADATA).addNew());
            isInitialized = true;
        }

        return isInitialized;
    }

    @Override
    protected boolean initializeReferences()
    {
        if (super.initializeReferences())
        {
            if (recordset_Values == null)
            {
                recordset_Values = (CoalesceRecordset) getCoalesceObjectForNamePath("Enumeration/enumeration/values");
            }
            if (record_Metadata == null)
            {
                record_Metadata = new MetadataCoalesceRecord((CoalesceRecord)getCoalesceObjectForNamePath("Enumeration/enumeration/metadata/metadata Record"));
            }
        }

        return recordset_Values != null && record_Metadata != null;
    }

    public CoalesceRecordset getValuesRecordset()
    {
        return recordset_Values;
    }

    @Override
    public ValuesCoalesceRecord addValuesRecord()
    {
        return new ValuesCoalesceRecord(recordset_Values.addNew());
    }
    
    @Override
    public List<IValuesRecord> getValuesRecords()
    {
		List<CoalesceRecord> records = recordset_Values.getRecords();
		List<IValuesRecord> results = new ArrayList<IValuesRecord>();

		for (CoalesceRecord record : records) 
		{
            results.add(new ValuesCoalesceRecord(record));
		}

        return results;
    }

    @Override
    public ValuesCoalesceRecord getValuesRecord(String key)
    {
		return new ValuesCoalesceRecord((CoalesceRecord) recordset_Values.getChildCoalesceObject(key));
    }

    @Override
    public void rmvValuesRecord(IValuesRecord record)
    {
        rmvValuesRecord(record.getKey());
    }

    @Override
    public void rmvValuesRecord(String key)
    {
        recordset_Values.remove(key);
    }

    @Override
    public MetadataCoalesceRecord getMetadataRecord()
    {
        return record_Metadata;
    }
    
    @Override
    public void setMetadataRecord(IMetadataRecord record) throws CoalesceDataFormatException
    {
		record_Metadata.populate(record);
    }
    
}