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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.PropertyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.services.api.Results;
import com.incadencecorp.coalesce.services.api.search.HitType;
import com.incadencecorp.coalesce.services.api.search.QueryResultsType;
import com.incadencecorp.coalesce.services.api.search.SearchDataObjectResponse;
import com.incadencecorp.coalesce.services.crud.api.ICrudClient;
import com.incadencecorp.coalesce.services.crud.service.data.model.api.record.IValuesRecord;
import com.incadencecorp.coalesce.services.crud.service.data.model.impl.coalesce.entity.EnumerationCoalesceEntity;
import com.incadencecorp.coalesce.services.crud.service.data.model.impl.coalesce.record.MetadataCoalesceRecord;
import com.incadencecorp.coalesce.services.crud.service.data.model.impl.pojo.entity.EnumerationPojoEntity;
import com.incadencecorp.coalesce.services.crud.service.data.model.impl.pojo.record.MetadataPojoRecord;
import com.incadencecorp.coalesce.services.crud.service.data.model.impl.pojo.record.ValuesPojoRecord;
import com.incadencecorp.coalesce.services.search.api.ISearchClient;

/**
 * Responsible for storing and retrieving enumerations.
 * 
 * @author Derek Clemenzi
 */
public class EnumerationDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnumerationDataController.class);
    private ICrudClient crud;
    private ISearchClient search;

    /**
     * Default Constructor
     */
    public EnumerationDataController(ICrudClient crud, ISearchClient search)
    {
        this.crud = crud;
        this.search = search;
    }

    /**
     * @return a list of available enumerations.
     */
    public List<MetadataPojoRecord> getEnumerations() throws RemoteException
    {
        List<MetadataPojoRecord> enumerations = new ArrayList<MetadataPojoRecord>();

        FilterFactory ff = CoalescePropertyFactory.getFilterFactory();

        List<Filter> filters = new ArrayList<Filter>();
        filters.add(ff.equals(CoalescePropertyFactory.getName(), ff.literal(EnumerationCoalesceEntity.NAME)));
        filters.add(ff.equals(CoalescePropertyFactory.getSource(), ff.literal(EnumerationCoalesceEntity.SOURCE)));

        List<PropertyName> properties = new ArrayList<PropertyName>();
        properties.add(CoalescePropertyFactory.getFieldProperty(EnumerationCoalesceEntity.RECORDSET_METADATA,
                                                                MetadataCoalesceRecord.EMetadataFields.ENUMNAME));
        properties.add(CoalescePropertyFactory.getFieldProperty(EnumerationCoalesceEntity.RECORDSET_METADATA,
                                                                MetadataCoalesceRecord.EMetadataFields.DESCRIPTION));

        try
        {
            SearchDataObjectResponse results = search.search(ff.and(filters),
                                                             1,
                                                             properties.toArray(new PropertyName[properties.size()]),
                                                             null,
                                                             true);

            for (QueryResultsType result : results.getResult())
            {
                if (result.getResult() != null && result.getResult().getHits() != null)
                {
                    for (HitType hit : result.getResult().getHits())
                    {
                        MetadataPojoRecord record = new MetadataPojoRecord();
                        record.setKey(hit.getEntityKey());
                        record.setEnumname(hit.getValues().get(0));
                        record.setDescription(hit.getValues().get(1));

                        enumerations.add(record);
                    }
                }
                else
                {
                    error(String.format(CoalesceErrors.NOT_FOUND, "Enumerations", "*"));
                }
            }
        }
        catch (CoalesceException e)
        {
            error(String.format(CoalesceErrors.FAILED_TASK,
                                this.getClass().getSimpleName(),
                                search.getClass().getSimpleName(),
                                e.getMessage()),
                  e);
        }

        return enumerations;
    }

    /**
     * @param key
     * @return an enumeration in JSON format
     */
    public EnumerationPojoEntity getEnumeration(String key) throws RemoteException
    {
        EnumerationPojoEntity pojo = new EnumerationPojoEntity();

        try
        {
            Results<CoalesceEntity>[] results = crud.retrieveDataObjects(key);

            if (results != null && results.length == 1 && results[0].getStatus() == EResultStatus.SUCCESS)
            {
                EnumerationCoalesceEntity enumeration = new EnumerationCoalesceEntity();
                if (enumeration.initialize(results[0].getResult()))
                {
                    pojo = new EnumerationPojoEntity(enumeration);
                }
                else
                {
                    error(String.format(CoalesceErrors.INVALID_ENUMERATION, key));
                }
            }
            else
            {
                error(String.format(CoalesceErrors.INVALID_ENUMERATION, key));
            }
        }
        catch (RemoteException | CoalesceDataFormatException e)
        {
            error(String.format(CoalesceErrors.INVALID_ENUMERATION, key), e);
        }

        return pojo;
    }

    /**
     * Saves an enumeration
     * 
     * @param value
     */
    public void setEnumeration(EnumerationPojoEntity value) throws RemoteException
    {
        try
        {
            Results<CoalesceEntity>[] results = crud.retrieveDataObjects(value.getKey());

            if (results != null && results.length == 1 && results[0].getStatus() == EResultStatus.SUCCESS)
            {
                EnumerationCoalesceEntity enumeration = new EnumerationCoalesceEntity();
                enumeration.initialize(results[0].getResult());
                enumeration.getMetadataRecord().populate(value.getMetadataRecord());

                // TODO Update Values
            }
            else
            {
                error(String.format(CoalesceErrors.NOT_SAVED,
                                    value.getKey(),
                                    "enumeration",
                                    String.format(CoalesceErrors.INVALID_ENUMERATION, value.getKey())));
            }
        }
        catch (RemoteException | CoalesceDataFormatException e)
        {
            error(String.format(CoalesceErrors.NOT_SAVED, value.getKey(), "enumeration", e.getMessage()), e);
        }
    }

// TODO Experimental code using search instead of CRUD to retrieve the values. Remove this method or replace original method.
//    public List<ValuesPojoRecord> getEnumerationValues(String key) throws RemoteException
//    {
//        List<ValuesPojoRecord> values = new ArrayList<ValuesPojoRecord>();
//
//        List<PropertyName> properties = new ArrayList<PropertyName>();
//        properties.add(CoalescePropertyFactory.getFieldProperty(EnumerationCoalesceEntity.RECORDSET_VALUES,
//                                                                CoalesceObject.ATTRIBUTE_KEY));
//        properties.add(CoalescePropertyFactory.getFieldProperty(EnumerationCoalesceEntity.RECORDSET_VALUES,
//                                                                ValuesCoalesceRecord.EValuesFields.ORDINAL));
//        properties.add(CoalescePropertyFactory.getFieldProperty(EnumerationCoalesceEntity.RECORDSET_VALUES,
//                                                                ValuesCoalesceRecord.EValuesFields.VALUE));
//        properties.add(CoalescePropertyFactory.getFieldProperty(EnumerationCoalesceEntity.RECORDSET_VALUES,
//                                                                ValuesCoalesceRecord.EValuesFields.DESCRIPTION));
//
//        try
//        {
//            SearchDataObjectResponse results = search.search(CoalescePropertyFactory.getEntityKey(key),
//                                                             1,
//                                                             properties.toArray(new PropertyName[properties.size()]),
//                                                             null,
//                                                             true);
//
//            for (QueryResultsType result : results.getResult())
//            {
//                if (result.getResult() != null && result.getResult().getHits() != null)
//                {
//                    for (HitType hit : result.getResult().getHits())
//                    {
//                        ValuesPojoRecord value = new ValuesPojoRecord();
//                        value.setKey(hit.getValues().get(1));
//                        value.setOrdinal(Integer.parseInt(hit.getValues().get(1)));
//                        value.setValue(hit.getValues().get(2));
//                        value.setDescription(hit.getValues().get(5));
//                    }
//                }
//                else
//                {
//                    error(String.format(CoalesceErrors.INVALID_ENUMERATION, key));
//                }
//            }
//        }
//        catch (CoalesceException e)
//        {
//            error(String.format(CoalesceErrors.FAILED_TASK,
//                                this.getClass().getSimpleName(),
//                                search.getClass().getSimpleName(),
//                                e.getMessage()),
//                  e);
//        }
//        return values;
//    }
    public List<ValuesPojoRecord> getEnumerationValues(String key) throws RemoteException
    {
        List<ValuesPojoRecord> values = new ArrayList<ValuesPojoRecord>();

        try
        {
            Results<CoalesceEntity>[] results = crud.retrieveDataObjects(key);

            if (results != null && results.length == 1 && results[0].getStatus() == EResultStatus.SUCCESS)
            {
                EnumerationCoalesceEntity enumeration = new EnumerationCoalesceEntity();
                enumeration.initialize(results[0].getResult());

                for (IValuesRecord record : enumeration.getValuesRecords())
                {
                    values.add(new ValuesPojoRecord(record));
                }
            }
            else
            {
                error(String.format(CoalesceErrors.NOT_FOUND, "enumeration", key));
            }
        }
        catch (RemoteException | CoalesceDataFormatException e)
        {
            error(String.format(CoalesceErrors.NOT_FOUND, "enumeration", key), e);
        }

        return values;
    }

    public Map<String, String> getEnumerationAssociatedValues(String key, String valuekey) throws RemoteException
    {
        Map<String, String> values = new HashMap<String, String>();

        try
        {
            Results<CoalesceEntity>[] results = crud.retrieveDataObjects(key);

            if (results != null && results.length == 1 && results[0].getStatus() == EResultStatus.SUCCESS)
            {
                EnumerationCoalesceEntity enumeration = new EnumerationCoalesceEntity();
                enumeration.initialize(results[0].getResult());

                for (IValuesRecord value : enumeration.getValuesRecords())
                {
                    if (value.getValue().equalsIgnoreCase(valuekey))
                    {
                        values.putAll(value.getAssociatedValues());
                        break;
                    }
                }
            }
            else
            {
                error(String.format(CoalesceErrors.NOT_FOUND, "enumeration", key));
            }
        }
        catch (RemoteException | CoalesceDataFormatException e)
        {
            error(String.format(CoalesceErrors.NOT_FOUND, "enumeration", key), e);
        }

        return values;
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
