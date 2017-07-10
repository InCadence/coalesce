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
package com.incadencecorp.coalesce.services.crud.service.data.model.impl.coalesce.record;
                	         		            		    
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.IFieldEnum;
import com.incadencecorp.coalesce.services.crud.service.data.model.api.record.IMetadataRecord;
import com.incadencecorp.coalesce.services.crud.service.data.model.impl.coalesce.factory.MetadataCoalesceFactory;

/**
 * Coalesce implementation of {@link IMetadataRecord} generated on 2017-07-07T00:06:59.076Z.
 * 
 * @author coalesce-plugins-template2java
 */
public class MetadataCoalesceRecord extends CoalesceRecord implements IMetadataRecord {

	private static final MetadataCoalesceFactory FACTORY = new MetadataCoalesceFactory();

	/**
	* Enumeration of the fields used by this record.
	*/
    public enum EMetadataFields implements IFieldEnum {
        ENUMNAME("enumname"),
        DESCRIPTION("description");
    
        private String label;

        private EMetadataFields(final String label) 
        {
             this.label = label;
        }

        @Override
        public String getFieldName()  
        {
            return label;
        }    

        public static EMetadataFields fromLabel(String label) 
        {
	    	for (EMetadataFields field : EMetadataFields.values()) 
            {
                if (field.getFieldName().equalsIgnoreCase(label)) 
                {
		    		return field;
                }
            }
            
            return null;
        }
    }

    /*--------------------------------------------------------------------------
    Constructor(s)
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    public MetadataCoalesceRecord()
    {
        super();
    }

    /**
     * Constructs a new instance from an existing record
     * 
     * @param record
     */
    public MetadataCoalesceRecord(CoalesceRecord record)
    {
        super(record);
    }

    /*--------------------------------------------------------------------------
    Factory Method(s)
    --------------------------------------------------------------------------*/

    /**
     * @param section
     * @param name
     * @return a record set that can be used for create new records of this
     *         type.
     */
    public static CoalesceRecordset createCoalesceRecordset(CoalesceSection section, String name)
    {
        CoalesceRecordset recordset = CoalesceRecordset.create(section, name);

		for (EMetadataFields field : EMetadataFields.values()) 
		{
			FACTORY.create(recordset, field);
		}
		
		recordset.setMaxRecords(1);
        recordset.setMinRecords(1);
        
        return recordset;
    }

    public void populate(IMetadataRecord record) throws CoalesceDataFormatException 
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
    public String getEnumname() throws CoalesceDataFormatException
    {
        return getEnumnameField().getValue();
    }
    
    @Override
    public void setEnumname(String value)
    {
        getEnumnameField().setValue(value);
    }
    
	@Override
    public String getDescription() throws CoalesceDataFormatException
    {
        return getDescriptionField().getValue();
    }
    
    @Override
    public void setDescription(String value)
    {
        getDescriptionField().setValue(value);
    }
    
    /*--------------------------------------------------------------------------
    Public Field Getter(s)
    --------------------------------------------------------------------------*/
    
    public CoalesceStringField getEnumnameField()
    {
        return (CoalesceStringField) getFieldByName(EMetadataFields.ENUMNAME);
    }
    
    public CoalesceStringField getDescriptionField()
    {
        return (CoalesceStringField) getFieldByName(EMetadataFields.DESCRIPTION);
    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private CoalesceField<?> getFieldByName(EMetadataFields field) 
    {
        return getFieldByName(field.getFieldName(), FACTORY);
    }

}