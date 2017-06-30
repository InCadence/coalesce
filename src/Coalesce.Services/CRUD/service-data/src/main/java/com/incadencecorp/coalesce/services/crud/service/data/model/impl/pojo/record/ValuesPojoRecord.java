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
package com.incadencecorp.coalesce.services.crud.service.data.model.impl.pojo.record;
   		    		    		    		    		    
import java.util.HashMap;
import java.util.Map;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.services.crud.service.data.model.api.record.IValuesRecord;

/**
 * Pojo implementation of {@link IValuesRecord} generated on 2017-07-07T00:06:59.076Z.
 * 
 * @author coalesce-plugins-template2java
 */
public class ValuesPojoRecord implements IValuesRecord {

    private String key;
	private ECoalesceObjectStatus status;
	private String ValueField;
    private int OrdinalField;
    private String DescriptionField;
    private Map<String, String> associatedValues = new HashMap<String, String>();
    
    /*--------------------------------------------------------------------------
    Constructor(s)
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    public ValuesPojoRecord()
    {
        super();
    }

    /**
     * Constructs a new instance from an existing record
     * 
     * @param record
     */
    public ValuesPojoRecord(IValuesRecord record) throws CoalesceDataFormatException
    {
        setKey(record.getKey());
        setStatus(record.getStatus());
		setValue(record.getValue());
		setOrdinal(record.getOrdinal());
		setDescription(record.getDescription());
		setAssociatedValues(record.getAssociatedValues());
    }

    /*--------------------------------------------------------------------------
    Public Getter(s) / Setter(s)
    --------------------------------------------------------------------------*/
    
    @Override
    public String getKey() 
    {
    	return key;
    }
    
    @Override
    public void setKey(String value)
    {
    	key = value; 
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
    
    /**
    * Gets value Field
    */
    @Override
    public String getValue() throws CoalesceDataFormatException 
    {
    	return ValueField;
    }

    /**
    * Sets value Field
    */
    @Override
    public void setValue(String value)
    {
    	ValueField = value;
    } 

    /**
    * Gets ordinal Field
    */
    @Override
    public int getOrdinal() throws CoalesceDataFormatException 
    {
    	return OrdinalField;
    }

    /**
    * Sets ordinal Field
    */
    @Override
    public void setOrdinal(int value)
    {
    	OrdinalField = value;
    } 

    /**
    * Gets description Field
    */
    @Override
    public String getDescription() throws CoalesceDataFormatException 
    {
    	return DescriptionField;
    }

    /**
    * Sets description Field
    */
    @Override
    public void setDescription(String value)
    {
    	DescriptionField = value;
    } 

    @Override
    public Map<String, String> getAssociatedValues() throws CoalesceDataFormatException
    {
        return associatedValues;
    }

    @Override
    public void setAssociatedValues(Map<String, String> value)
    {
        associatedValues.clear();
        associatedValues.putAll(value);
    }

}