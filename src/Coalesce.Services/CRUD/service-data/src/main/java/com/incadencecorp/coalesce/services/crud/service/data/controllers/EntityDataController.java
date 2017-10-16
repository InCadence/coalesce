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
package com.incadencecorp.coalesce.services.crud.service.data.controllers;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCircle;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCircleField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceObject;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.services.crud.api.ICrudClient;
import com.incadencecorp.coalesce.services.crud.service.data.model.impl.coalesce.entity.EnumerationCoalesceEntity;
import com.incadencecorp.coalesce.services.crud.service.data.model.impl.coalesce.record.ValuesCoalesceRecord;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * Responsible for storing and retrieving user search options.
 * 
 * @author Derek Clemenzi
 */
public class EntityDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityDataController.class);
    private ICrudClient crud;

    // TODO Remove this
    private EnumerationCoalesceEntity entity;

    /**
     * Default Constructor
     */
    public EntityDataController(ICrudClient crud)
    {
        this.crud = crud;

        // TODO Remove this test code
        
        entity = new EnumerationCoalesceEntity();
        entity.initialize();

        try
        {
            entity.getMetadataRecord().setEnumname("Hello World");
            entity.getMetadataRecord().setDescription("Example Enumeration");
            
            ValuesCoalesceRecord record;
            
            for (int ii=0; ii<20; ii++) {
                
                Map<String, String> associated = new HashMap<String, String>(); 
                associated.put("AA", "AA");
                associated.put("BB", "BB");
                
                record = entity.addValuesRecord();
                record.setValue(Integer.toString(ii));
                record.setOrdinal(ii);
                record.setDescription("Testing " + ii);
                record.setAssociatedValues(associated);
            }
            
            CoalesceRecordset recordset = CoalesceRecordset.create(CoalesceSection.create(entity, "test"), "test rs");
            CoalesceFieldDefinition.create(recordset, "circle", ECoalesceFieldDataTypes.CIRCLE_TYPE);

            CoalesceCircleField field = (CoalesceCircleField) recordset.addNew().getFieldByName("circle");
            CoalesceCircle value = new CoalesceCircle();
            value.setCenter(new Coordinate(1, 1));
            value.setRadius(5);
            field.setValue(value);
            
            CoalesceRecordset recordset2 = CoalesceRecordset.create(CoalesceSection.create(entity.getCoalesceSectionForNamePath(entity.getName(), "enumeration"), "nested"), "nested rs");
            CoalesceFieldDefinition.create(recordset2, "circle", ECoalesceFieldDataTypes.CIRCLE_TYPE);

        }
        catch (CoalesceDataFormatException e)
        {
            LOGGER.warn("WARN", e);
        }
    }

    public CoalesceEntity getEntity(String entityKey) throws RemoteException
    {
        return retrieveEntity(entityKey);
    }

    public void setEntity(CoalesceEntity entity) throws RemoteException
    {
        crud.updateDataObject(entity);
    }

    public CoalesceSection getSection(String entityKey, String key) throws RemoteException
    {
        return retrieveObject(entityKey, key, CoalesceSection.class);
    }

    public CoalesceRecordset getRecordset(String entityKey, String key) throws RemoteException
    {
        return retrieveObject(entityKey, key, CoalesceRecordset.class);
    }

    public CoalesceRecord getRecord(String entityKey, String key) throws RemoteException
    {
        return retrieveObject(entityKey, key, CoalesceRecord.class);
    }

    public CoalesceField<?> getField(String entityKey, String key) throws RemoteException
    {
        return retrieveObject(entityKey, key, CoalesceField.class);
    }

    // TODO Not Complete
    public void setFields(String entityKey, Map<String, String> values) throws RemoteException
    {
        CoalesceEntity entity = retrieveEntity(entityKey);

        for (Map.Entry<String, String> kvp : values.entrySet())
        {

        }
    }

    private <T extends CoalesceObject> T retrieveObject(String entityKey, String key, Class<T> clazz) throws RemoteException
    {
        CoalesceObject result = retrieveEntity(entityKey).getCoalesceObjectForKey(key);

        if (result == null)
        {
            error(String.format(CoalesceErrors.NOT_FOUND, clazz.getSimpleName(), key));
        }
        else if (!clazz.isInstance(result))
        {
            error(String.format(String.format(CoalesceErrors.INVALID_INPUT_REASON,
                                              key,
                                              "Expected: " + clazz.getSimpleName() + " Actual: "
                                                      + result.getClass().getSimpleName())));
        }

        return (T) result;
    }

    /**
     * Retrieves the entity from the data store and throws an exception if not
     * found or multiple entries are found.
     * 
     * @param key
     * @return
     * @throws RemoteException
     */
    private CoalesceEntity retrieveEntity(String key) throws RemoteException
    {
        // TODO Remove this test code

        return entity;

        // TODO Uncomment the following code

//        Results<CoalesceEntity>[] results = crud.retrieveDataObjects(key);
//
//        if (results.length == 0)
//        {
//            error(String.format(CoalesceErrors.NOT_FOUND, "Entity", key));
//        }
//        if (results.length > 1)
//        {
//            error(String.format(CoalesceErrors.INVALID_OBJECT, "Entity", key));
//        }
//
//        return results[0].getResult();
    }

    private void error(String msg) throws RemoteException
    {
        error(msg, null);
    }

    private void error(String msg, Exception e) throws RemoteException
    {
        if (e == null)
        {
            LOGGER.warn(msg);
        }
        else
        {
            LOGGER.error(msg, e);
        }

        throw new RemoteException(msg);
    }
}
