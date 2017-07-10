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
package com.incadencecorp.coalesce.services.crud.service.data.model.api.record;
  		    		    		    		    		    
import java.util.Map;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;

/**
 * Generated on 2017-07-07T00:06:59.076Z.
 * 
 * @author coalesce-plugins-template2java
 */
public interface IValuesRecord {

	/**
	* @return this record's key.
	*/
	String getKey();
	
	/**
	* Sets this record's key.
        *
        * @param value
	*/
	void setKey(String value);

	/**
	* Returns this record's status
	*/
    ECoalesceObjectStatus getStatus();
    
	/**
	* Sets this record's status
	*/
	void setStatus(ECoalesceObjectStatus value);
    
    /**
    * @return the value field's value.
    * @throws CoalesceDataFormatException
    */
    String getValue() throws CoalesceDataFormatException;

    /**
    * Sets the value field's value.
    *
    * @param value
    */
    void setValue(String value); 
    
    /**
    * @return the ordinal field's value.
    * @throws CoalesceDataFormatException
    */
    int getOrdinal() throws CoalesceDataFormatException;

    /**
    * Sets the ordinal field's value.
    *
    * @param value
    */
    void setOrdinal(int value); 
    
    /**
    * @return the description field's value.
    * @throws CoalesceDataFormatException
    */
    String getDescription() throws CoalesceDataFormatException;

    /**
    * Sets the description field's value.
    *
    * @param value
    */
    void setDescription(String value); 
    
    Map<String, String> getAssociatedValues() throws CoalesceDataFormatException;

    void setAssociatedValues(Map<String, String> value);

    
}