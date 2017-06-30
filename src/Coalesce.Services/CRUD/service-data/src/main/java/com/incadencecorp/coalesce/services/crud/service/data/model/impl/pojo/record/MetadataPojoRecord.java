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
   		    		    
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.services.crud.service.data.model.api.record.IMetadataRecord;

/**
 * Pojo implementation of {@link IMetadataRecord} generated on 2017-07-07T00:06:59.076Z.
 * 
 * @author coalesce-plugins-template2java
 */
public class MetadataPojoRecord implements IMetadataRecord {

    private String key;
    private ECoalesceObjectStatus status;
    private String EnumnameField;
    private String DescriptionField;
    
    /*--------------------------------------------------------------------------
    Constructor(s)
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    public MetadataPojoRecord()
    {
        super();
    }

    /**
     * Constructs a new instance from an existing record
     * 
     * @param record
     */
    public MetadataPojoRecord(IMetadataRecord record) throws CoalesceDataFormatException
    {
        setKey(record.getKey());
        setStatus(record.getStatus());
		setEnumname(record.getEnumname());
		setDescription(record.getDescription());
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
    * Gets enumname Field
    */
    @Override
    public String getEnumname() throws CoalesceDataFormatException 
    {
    	return EnumnameField;
    }

    /**
    * Sets enumname Field
    */
    @Override
    public void setEnumname(String value)
    {
    	EnumnameField = value;
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

}