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
package com.incadencecorp.coalesce.services.crud.service.data.model.api.entity;
  	 			 		 		 	
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.services.crud.service.data.model.api.record.IMetadataRecord;
import com.incadencecorp.coalesce.services.crud.service.data.model.api.record.IValuesRecord;
import java.util.List;
import org.joda.time.DateTime;

/**
 * Generated on 2017-07-07T00:06:59.076Z.
 * 
 * @author coalesce-plugins-template2java
 */
public interface IEnumerationEntity {

    /**
    * Returns this objects key.
    */
    String getKey(); 
    
    /**
    * Sets this objects key.
    */
    void setKey(String value);
    
	/**
	* Returns the date this object was created.
	*/
    DateTime getDateCreated();

	/**
	* Sets the date this object was created.
	*/
    void setDateCreated(DateTime value);

	/**
	* Returns the date this object was last modified.
	*/
    DateTime getLastModified();

	/**
	* Sets the date this object was created.
	*/
    void setLastModified(DateTime value);

	/**
	* Sets the user ID who last modified this object.
	*/
    String getModifiedBy();

	/**
	* Returns the user ID who last modified this object.
	*/
    void setModifiedBy(String value);
        
	/**
	* Returns this entity's status
	*/
    ECoalesceObjectStatus getStatus();
    
	/**
	* Sets this entity's status
	*/
	void setStatus(ECoalesceObjectStatus value);

	/**
	* @return a list of records belong to values.
	*/
    List<IValuesRecord> getValuesRecords();

	/**
	* @return the specified record.
        *
        * @param key
	*/
    IValuesRecord getValuesRecord(String key);

	/**
	* @return a newly created record that has been added to values.
	*/
    IValuesRecord addValuesRecord();
    
	/**
	* Removes the record.
        *
        * @param record
	*/
    void rmvValuesRecord(IValuesRecord record);

	/**
	* Removes the record by its key.
        *
        * @param key
	*/
    void rmvValuesRecord(String key);
    
	/**
	* @return the singleton record belong to metadata.
	*/
    IMetadataRecord getMetadataRecord();

	/**
	* Sets the singleton record belong to metadata. Only the field's values are copied. History and other attributes are not.
        *
        * @param record
	*/
    void setMetadataRecord(IMetadataRecord record) throws CoalesceDataFormatException;
    
}