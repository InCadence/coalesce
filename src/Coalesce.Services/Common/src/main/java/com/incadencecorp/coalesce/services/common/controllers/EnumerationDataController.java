/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */
package com.incadencecorp.coalesce.services.common.controllers;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.datamodel.api.record.IEnumValuesRecord;
import com.incadencecorp.coalesce.datamodel.impl.coalesce.entity.EnumerationCoalesceEntity;
import com.incadencecorp.coalesce.datamodel.impl.coalesce.record.EnumMetadataCoalesceRecord;
import com.incadencecorp.coalesce.datamodel.impl.coalesce.record.EnumValuesCoalesceRecord;
import com.incadencecorp.coalesce.datamodel.impl.pojo.entity.EnumerationPojoEntity;
import com.incadencecorp.coalesce.datamodel.impl.pojo.record.EnumMetadataPojoRecord;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.search.CoalesceSearchFramework;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.services.api.IEnumerationDataController;
import com.incadencecorp.coalesce.services.api.datamodel.EnumValuesRecord;
import org.geotools.data.Query;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.PropertyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsible for storing and retrieving enumerations.
 *
 * @author Derek Clemenzi
 */
public class EnumerationDataController implements IEnumerationDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnumerationDataController.class);
    private CoalesceSearchFramework framework;

    /**
     * Default Constructor
     */
    public EnumerationDataController(CoalesceSearchFramework framework)
    {
        this.framework = framework;
    }

    @Override
    public List<EnumMetadataPojoRecord> getEnumerations() throws RemoteException
    {
        List<EnumMetadataPojoRecord> enumerations = new ArrayList<>();

        FilterFactory ff = CoalescePropertyFactory.getFilterFactory();

        List<Filter> filters = new ArrayList<>();
        filters.add(ff.notEqual(CoalescePropertyFactory.getEntityStatus(),
                                ff.literal(ECoalesceObjectStatus.DELETED.value())));
        filters.add(ff.not(ff.isNull(CoalescePropertyFactory.getFieldProperty(EnumerationCoalesceEntity.RECORDSET_ENUMMETADATA,
                                                                              EnumMetadataCoalesceRecord.EEnumMetadataFields.ENUMNAME))));

        List<PropertyName> properties = new ArrayList<>();
        properties.add(CoalescePropertyFactory.getFieldProperty(EnumerationCoalesceEntity.RECORDSET_ENUMMETADATA,
                                                                EnumMetadataCoalesceRecord.EEnumMetadataFields.ENUMNAME));
        properties.add(CoalescePropertyFactory.getFieldProperty(EnumerationCoalesceEntity.RECORDSET_ENUMMETADATA,
                                                                EnumMetadataCoalesceRecord.EEnumMetadataFields.DESCRIPTION));
        properties.add(CoalescePropertyFactory.getEntityStatus());

        try
        {
            Query query = new Query();
            query.setFilter(ff.and(filters));
            query.setProperties(properties);

            SearchResults searchResults = framework.search(query);

            if (!searchResults.isSuccessful())
            {
                throw new RemoteException(searchResults.getError());
            }

            try (CachedRowSet rowset = searchResults.getResults())
            {
                if (rowset.first())
                {
                    do
                    {
                        int idx = 1;

                        EnumMetadataPojoRecord record = new EnumMetadataPojoRecord();
                        record.setKey(rowset.getString(idx++));
                        record.setEnumname(rowset.getString(idx++));
                        record.setDescription(rowset.getString(idx++));
                        record.setStatus(ECoalesceObjectStatus.fromValue(rowset.getString(idx)));

                        enumerations.add(record);
                    }
                    while (rowset.next());
                }
            }
            catch (SQLException e)
            {
                throw new RemoteException(e.getMessage(), e);
            }
        }
        catch (CoalesceException | InterruptedException e)
        {
            error(String.format(CoalesceErrors.FAILED_TASK,
                                this.getClass().getSimpleName(),
                                framework.getClass().getSimpleName(),
                                e.getMessage()), e);
        }

        return enumerations;
    }

    @Override
    public EnumerationPojoEntity getEnumeration(String key) throws RemoteException
    {
        EnumerationPojoEntity pojo = new EnumerationPojoEntity();

        try
        {
            EnumerationCoalesceEntity enumeration = new EnumerationCoalesceEntity();
            if (enumeration.initialize(framework.getCoalesceEntity(key)))
            {
                pojo = new EnumerationPojoEntity(enumeration);
            }
            else
            {
                error(String.format(CoalesceErrors.INVALID_ENUMERATION, key));
            }
        }
        catch (RemoteException | CoalesceException e)
        {
            error(String.format(CoalesceErrors.INVALID_ENUMERATION, key), e);
        }

        return pojo;
    }

    @Override
    public List<EnumValuesRecord> getEnumerationValues(String key) throws RemoteException
    {
        return getEnumerationValuesByEntity(key);
    }

    @Override
    public Map<String, String> getEnumerationAssociatedValues(String key, String valuekey) throws RemoteException
    {
        Map<String, String> values = new HashMap<>();

        try
        {
            EnumerationCoalesceEntity enumeration = new EnumerationCoalesceEntity();
            enumeration.initialize(framework.getCoalesceEntity(key));
            IEnumValuesRecord record = enumeration.getEnumValuesRecord(valuekey);

            String[] keys = record.getAssociatedkeys();
            String[] vals = record.getAssociatedvalues();

            for (int ii = 0; ii < keys.length && ii < vals.length; ii++)
            {
                values.put(keys[ii], vals[ii]);
            }
        }
        catch (CoalesceException e)
        {
            error(String.format(CoalesceErrors.NOT_FOUND, "enumeration", key), e);
        }

        return values;
    }

    // TODO Experimental code using search instead of CRUD to retrieve the values. Remove this method or replace original method.
    private List<EnumValuesRecord> getEnumerationValuesBySearch(String key) throws RemoteException
    {
        List<EnumValuesRecord> values = new ArrayList<>();

        List<PropertyName> properties = new ArrayList<>();
        properties.add(CoalescePropertyFactory.getRecordKey(EnumerationCoalesceEntity.RECORDSET_ENUMVALUES));
        properties.add(CoalescePropertyFactory.getFieldProperty(EnumerationCoalesceEntity.RECORDSET_ENUMVALUES,
                                                                EnumValuesCoalesceRecord.EEnumValuesFields.ORDINAL));
        properties.add(CoalescePropertyFactory.getFieldProperty(EnumerationCoalesceEntity.RECORDSET_ENUMVALUES,
                                                                EnumValuesCoalesceRecord.EEnumValuesFields.VALUE));
        properties.add(CoalescePropertyFactory.getFieldProperty(EnumerationCoalesceEntity.RECORDSET_ENUMVALUES,
                                                                EnumValuesCoalesceRecord.EEnumValuesFields.DESCRIPTION));

        try
        {
            Query query = new Query();
            query.setFilter(CoalescePropertyFactory.getRecordKey(EnumerationCoalesceEntity.RECORDSET_ENUMVALUES, key));
            query.setProperties(properties);

            SearchResults searchResults = framework.search(query);

            if (!searchResults.isSuccessful())
            {
                throw new RemoteException(searchResults.getError());
            }

            try (CachedRowSet rowset = searchResults.getResults())
            {
                if (rowset.first())
                {
                    do
                    {
                        int idx = 1;

                        EnumValuesRecord record = new EnumValuesRecord();
                        record.setKey(rowset.getString(idx++));
                        record.setOrdinal(rowset.getInt(idx++));
                        record.setValue(rowset.getString(idx++));
                        record.setDescription(rowset.getString(idx));

                        values.add(record);
                    }
                    while (rowset.next());
                }
            }
            catch (SQLException e)
            {
                throw new RemoteException(e.getMessage(), e);
            }
        }
        catch (CoalesceException | InterruptedException e)
        {
            error(String.format(CoalesceErrors.FAILED_TASK,
                                this.getClass().getSimpleName(),
                                framework.getClass().getSimpleName(),
                                e.getMessage()), e);
        }

        return values;
    }

    private List<EnumValuesRecord> getEnumerationValuesByEntity(String key) throws RemoteException
    {
        List<EnumValuesRecord> values = new ArrayList<>();

        try
        {
            EnumerationCoalesceEntity enumeration = new EnumerationCoalesceEntity();
            enumeration.initialize(framework.getCoalesceEntity(key));

            for (IEnumValuesRecord record : enumeration.getEnumValuesRecords())
            {
                values.add(new EnumValuesRecord(record));
            }
        }
        catch (CoalesceException e)
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
