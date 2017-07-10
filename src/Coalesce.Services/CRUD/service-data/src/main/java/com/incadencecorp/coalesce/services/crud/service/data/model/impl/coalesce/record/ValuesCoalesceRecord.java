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
                	         		            	         		            		            	         		            		    
import java.util.HashMap;
import java.util.Map;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIntegerField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringListField;
import com.incadencecorp.coalesce.framework.datamodel.IFieldEnum;
import com.incadencecorp.coalesce.services.crud.service.data.model.api.record.IValuesRecord;
import com.incadencecorp.coalesce.services.crud.service.data.model.impl.coalesce.factory.ValuesCoalesceFactory;

/**
 * Coalesce implementation of {@link IValuesRecord} generated on 2017-07-07T00:06:59.076Z.
 * 
 * @author coalesce-plugins-template2java
 */
public class ValuesCoalesceRecord extends CoalesceRecord implements IValuesRecord {

	private static final ValuesCoalesceFactory FACTORY = new ValuesCoalesceFactory();

	/**
	* Enumeration of the fields used by this record.
	*/
    public enum EValuesFields implements IFieldEnum {
        VALUE("value"),
        ORDINAL("ordinal"),
        DESCRIPTION("description"),
        ASSOCIATEDKEYS("associatedkeys"),
        ASSOCIATEDVALUES("associatedvalues");
    
        private String label;

        private EValuesFields(final String label) 
        {
             this.label = label;
        }

        @Override
        public String getFieldName()  
        {
            return label;
        }    

        public static EValuesFields fromLabel(String label) 
        {
	    	for (EValuesFields field : EValuesFields.values()) 
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
    public ValuesCoalesceRecord()
    {
        super();
    }

    /**
     * Constructs a new instance from an existing record
     * 
     * @param record
     */
    public ValuesCoalesceRecord(CoalesceRecord record)
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

		for (EValuesFields field : EValuesFields.values()) 
		{
			FACTORY.create(recordset, field);
		}
		
		recordset.setMaxRecords(500);
        recordset.setMinRecords(0);
        
        return recordset;
    }

    public void populate(IValuesRecord record) throws CoalesceDataFormatException 
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
    public String getValue() throws CoalesceDataFormatException
    {
        return getValueField().getValue();
    }
    
    @Override
    public void setValue(String value)
    {
        getValueField().setValue(value);
    }
    
	@Override
    public int getOrdinal() throws CoalesceDataFormatException
    {
        return getOrdinalField().getValue();
    }
    
    @Override
    public void setOrdinal(int value)
    {
        getOrdinalField().setValue(value);
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
    
    @Override
    public Map<String, String> getAssociatedValues() throws CoalesceDataFormatException
    {
        String[] keys = getAssociatedkeysField().getValue();
        String[] vals = getAssociatedvaluesField().getValue();

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

        getAssociatedkeysField().setValue(keys);
        getAssociatedvaluesField().setValue(vals);
    }
    
    /*--------------------------------------------------------------------------
    Public Field Getter(s)
    --------------------------------------------------------------------------*/
    
    public CoalesceStringField getValueField()
    {
        return (CoalesceStringField) getFieldByName(EValuesFields.VALUE);
    }
    
    public CoalesceIntegerField getOrdinalField()
    {
        return (CoalesceIntegerField) getFieldByName(EValuesFields.ORDINAL);
    }
    
    public CoalesceStringField getDescriptionField()
    {
        return (CoalesceStringField) getFieldByName(EValuesFields.DESCRIPTION);
    }
    
    public CoalesceStringListField getAssociatedkeysField()
    {
        return (CoalesceStringListField) getFieldByName(EValuesFields.ASSOCIATEDKEYS);
    }
    
    public CoalesceStringListField getAssociatedvaluesField()
    {
        return (CoalesceStringListField) getFieldByName(EValuesFields.ASSOCIATEDVALUES);
    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private CoalesceField<?> getFieldByName(EValuesFields field) 
    {
        return getFieldByName(field.getFieldName(), FACTORY);
    }

}