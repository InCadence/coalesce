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

package com.incadencecorp.coalesce.framework.persistance.soap.impl;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.search.resultset.CoalesceColumnMetadata;
import com.incadencecorp.coalesce.search.resultset.CoalesceResultSet;
import com.incadencecorp.coalesce.services.api.Results;
import com.incadencecorp.coalesce.services.api.search.QueryResultsType;
import com.incadencecorp.coalesce.services.api.search.SearchDataObjectResponse;
import com.incadencecorp.coalesce.services.crud.api.ICrudClient;
import com.incadencecorp.coalesce.services.crud.client.jaxws.CrudJaxwsClientImpl;
import com.incadencecorp.coalesce.services.search.api.ISearchClient;
import com.incadencecorp.coalesce.services.search.client.jaxws.SearchJaxwsClientImpl;
import org.apache.commons.lang.NotImplementedException;
import org.geotools.data.Query;
import org.geotools.filter.Capabilities;
import org.opengis.filter.expression.PropertyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * This implementation wraps the {@link ICrudClient} and {@link ISearchClient} API.
 *
 * @author Derek Clemenzi
 */
public class SOAPPersisterImpl implements ICoalescePersistor, ICoalesceSearchPersistor {

    private static Logger LOGGER = LoggerFactory.getLogger(SOAPPersisterImpl.class);

    private final ICrudClient crud;
    private final ISearchClient search;

    /**
     * Default constructor pulling properties from {@link SOAPSettings}
     */
    public SOAPPersisterImpl()
    {
        this(SOAPSettings.getProperties());
    }

    /**
     * Default constructor with user defined configuration.
     *
     * @param props configuration
     * @see SOAPSettings
     */
    public SOAPPersisterImpl(Map<String, String> props)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Properties");
            for (Map.Entry<String, String> entry : props.entrySet())
            {
                LOGGER.debug("\t{}={}", entry.getKey(), entry.getValue());
            }
        }

        if (!props.containsKey(SOAPSettings.PROPERTY_CRUD_URL))
        {
            throw new IllegalArgumentException(String.format(CoalesceErrors.NOT_SPECIFIED, SOAPSettings.PROPERTY_CRUD_URL));
        }

        if (!props.containsKey(SOAPSettings.PROPERTY_SEARCH_URL))
        {
            throw new IllegalArgumentException(String.format(CoalesceErrors.NOT_SPECIFIED,
                                                             SOAPSettings.PROPERTY_SEARCH_URL));
        }

        try
        {
            crud = new CrudJaxwsClientImpl(new URL(props.get(SOAPSettings.PROPERTY_CRUD_URL)));
            search = new SearchJaxwsClientImpl(new URL(props.get(SOAPSettings.PROPERTY_SEARCH_URL)));
        }
        catch (MalformedURLException e)
        {
            throw new IllegalArgumentException(e);
        }

    }

    /**
     * This constructor allows full controller over specifying the underlying services.
     *
     * @param crud   implementation to use.
     * @param search implementation to use.
     */
    public SOAPPersisterImpl(ICrudClient crud, ISearchClient search)
    {
        this.crud = crud;
        this.search = search;
    }

    @Override
    public boolean saveEntity(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        try
        {
            // TODO Determine when to call update verse create
            return crud.createDataObject(entities);
        }
        catch (RemoteException e)
        {
            throw new CoalescePersistorException(e);
        }
    }

    @Override
    public CoalesceEntity[] getEntity(String... keys) throws CoalescePersistorException
    {
        List<CoalesceEntity> entities = new ArrayList<>();

        try
        {
            for (Results<CoalesceEntity> result : crud.retrieveDataObjects(keys))
            {
                if (result.isSuccessful())
                {
                    entities.add(result.getResult());
                }
            }
        }
        catch (RemoteException e)
        {
            throw new CoalescePersistorException(e);
        }

        return entities.toArray(new CoalesceEntity[entities.size()]);
    }

    @Override
    public String[] getEntityXml(String... keys) throws CoalescePersistorException
    {
        List<String> xml = new ArrayList<>();

        for (CoalesceEntity entity : getEntity(keys))
        {
            xml.add(entity.toXml());
        }
        return xml.toArray(new String[xml.size()]);
    }

    @Override
    public SearchResults search(Query query) throws CoalescePersistorException
    {
        SearchResults results = new SearchResults();

        List<PropertyName> properties = new ArrayList<>();

        if (query.getProperties() != null)
        {
            properties = query.getProperties();
        }

        if (query.getStartIndex() == null)
        {
            query.setStartIndex(1);
        }

        try
        {
            SearchDataObjectResponse response = search.search(query.getFilter(),
                                                              query.getStartIndex(),
                                                              properties.toArray(new PropertyName[properties.size()]),
                                                              query.getSortBy(),
                                                              true);

            List<PropertyName> columns = new ArrayList<>();
            columns.add(CoalescePropertyFactory.getEntityKey());
            //columns.add(CoalescePropertyFactory.getName());
            //columns.add(CoalescePropertyFactory.getSource());
            //columns.add(CoalescePropertyFactory.getEntityTitle());
            columns.addAll(properties);

            List<CoalesceColumnMetadata> metadata = CoalesceResultSet.getColumns(columns, new SOAPDataTypeMapper());

            if (response.getStatus() == EResultStatus.SUCCESS)
            {
                QueryResultsType result = response.getResult().get(0);
                if (result.getStatus() == EResultStatus.SUCCESS)
                {
                    // Convert to a cached rowset
                    SOAPResultIterator iterator = new SOAPResultIterator(result.getResult(), metadata);
                    CoalesceResultSet resultSet = new CoalesceResultSet(iterator, metadata);

                    try
                    {
                        CachedRowSet rowset = RowSetProvider.newFactory().createCachedRowSet();
                        rowset.populate(resultSet);

                        results.setResults(rowset);
                        results.setTotal(rowset.size());
                    }
                    catch (SQLException e)
                    {
                        throw new CoalescePersistorException(e);
                    }

                    return results;
                }
            }
        }
        catch (CoalesceException e)
        {
            throw new CoalescePersistorException(e);
        }

        return results;
    }

    @Override
    public Capabilities getSearchCapabilities()
    {
        Capabilities capability = new Capabilities();
        capability.addAll(Capabilities.SIMPLE_COMPARISONS);
        capability.addAll(Capabilities.LOGICAL);

        return capability;
    }

    @Override
    public EnumSet<EPersistorCapabilities> getCapabilities()
    {
        return EnumSet.of(EPersistorCapabilities.READ, EPersistorCapabilities.CREATE, EPersistorCapabilities.UPDATE);
    }

    @Override
    public void saveTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        //throw new NotImplementedException();
    }

    @Override
    public void deleteTemplate(String... keys) throws CoalescePersistorException
    {
        throw new NotImplementedException();
    }

    @Override
    public void registerTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        //throw new NotImplementedException();
    }

    @Override
    public void unregisterTemplate(String... keys) throws CoalescePersistorException
    {
        throw new NotImplementedException();
    }

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String key) throws CoalescePersistorException
    {
        throw new NotImplementedException();
    }

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String name, String source, String version)
            throws CoalescePersistorException
    {
        throw new NotImplementedException();
    }

    @Override
    public String getEntityTemplateKey(String name, String source, String version) throws CoalescePersistorException
    {
        throw new NotImplementedException();
    }

    @Override
    public List<ObjectMetaData> getEntityTemplateMetadata() throws CoalescePersistorException
    {
        throw new NotImplementedException();
    }
}
