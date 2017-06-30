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
package com.incadencecorp.coalesce.services.crud.service.data.model.impl.pojo.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.services.crud.service.data.model.api.entity.IEnumerationEntity;
import com.incadencecorp.coalesce.services.crud.service.data.model.api.record.IMetadataRecord;
import com.incadencecorp.coalesce.services.crud.service.data.model.api.record.IValuesRecord;
import com.incadencecorp.coalesce.services.crud.service.data.model.impl.pojo.record.MetadataPojoRecord;
import com.incadencecorp.coalesce.services.crud.service.data.model.impl.pojo.record.ValuesPojoRecord;

/**
 * Pojo implementation of {@link IEnumerationEntity} generated on
 * 2017-07-07T00:06:59.076Z.
 * 
 * @author coalesce-plugins-template2java
 */
public class EnumerationPojoEntity implements IEnumerationEntity {

    public static final String NAME = "Enumeration";
    public static final String SOURCE = "Enumeration";
    public static final String VERSION = "1.0";

    private Map<String, ValuesPojoRecord> recordset_Values = new HashMap<String, ValuesPojoRecord>();
    private MetadataPojoRecord record_Metadata;

    private String key;
    private DateTime dateCreated;
    private DateTime lastModified;
    private String lastModifiedBy;
    private ECoalesceObjectStatus status;

    /**
     * Default Constructor
     */
    public EnumerationPojoEntity()
    {
        // Do Nothing
    }

    public EnumerationPojoEntity(IEnumerationEntity entity) throws CoalesceDataFormatException
    {
        setKey(entity.getKey());
        setDateCreated(entity.getDateCreated());
        setLastModified(entity.getLastModified());
        setModifiedBy(entity.getModifiedBy());
        setStatus(entity.getStatus());    
        
        setMetadataRecord(entity.getMetadataRecord());
        addValuesRecord(entity.getValuesRecords());
    }
    
    public String getKey() 
    {
        return key;
    }
    
    public void setKey(String value)
    {
        key = value; 
    }  
    
    @Override
    public DateTime getDateCreated()
    {
        return dateCreated;
    }

    @Override
    public void setDateCreated(DateTime value)
    {
        dateCreated = value;
    }

    @Override
    public DateTime getLastModified()
    {
        return lastModified;
    }

    @Override
    public void setLastModified(DateTime value)
    {
        lastModified = value;
    }

    @Override
    public String getModifiedBy()
    {
        return lastModifiedBy;
    }

    @Override
    public void setModifiedBy(String value)
    {
        lastModifiedBy = value;
    }

    @Override
    public ECoalesceObjectStatus getStatus()
    {
        return status;
    }

    @Override
    public void setStatus(ECoalesceObjectStatus value)
    {
        status = value;
    }

    public void addValuesRecord(List<IValuesRecord> values) throws CoalesceDataFormatException
    {
        for (IValuesRecord value : values)
        {
            recordset_Values.put(value.getKey(), new ValuesPojoRecord(value));
        }
    }

    @Override
    public ValuesPojoRecord addValuesRecord()
    {
        ValuesPojoRecord result = new ValuesPojoRecord();
        recordset_Values.put(result.getKey(), result);
        return result;
    }

    @Override
    public List<IValuesRecord> getValuesRecords()
    {
        List<IValuesRecord> result = new ArrayList<IValuesRecord>();
        result.addAll(recordset_Values.values());
        return result;
    }

    @Override
    public ValuesPojoRecord getValuesRecord(String key)
    {
        return recordset_Values.get(key);
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
    public MetadataPojoRecord getMetadataRecord()
    {
        return record_Metadata;
    }

    @Override
    public void setMetadataRecord(IMetadataRecord record) throws CoalesceDataFormatException
    {
        record_Metadata = new MetadataPojoRecord(record);
    }

}
