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

import org.codehaus.jettison.json.JSONObject;
import org.geotools.data.Query;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.PropertyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.exim.impl.JsonEximImpl;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.services.api.Results;
import com.incadencecorp.coalesce.services.api.search.HitType;
import com.incadencecorp.coalesce.services.api.search.QueryResultsType;
import com.incadencecorp.coalesce.services.api.search.SearchDataObjectResponse;
import com.incadencecorp.coalesce.services.crud.api.ICrudClient;
import com.incadencecorp.coalesce.services.crud.service.data.model.CoalesceEnumeration;
import com.incadencecorp.coalesce.services.search.api.ISearchClient;

/**
 * Responsible for storing and retrieving user search options.
 * 
 * @author Derek Clemenzi
 */
public class EnumerationDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnumerationDataController.class);
    private ICrudClient crud;
    private ISearchClient search;

    private final JsonEximImpl exim = new JsonEximImpl();

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
    public Map<String, String> getEnumerationList()
    {
        Map<String, String> enumerations = new HashMap<String, String>();

        FilterFactory ff = CoalescePropertyFactory.getFilterFactory();

        List<Filter> filters = new ArrayList<Filter>();
        filters.add(ff.equals(CoalescePropertyFactory.getName(), ff.literal(CoalesceEnumeration.NAME)));
        filters.add(ff.equals(CoalescePropertyFactory.getSource(), ff.literal(CoalesceEnumeration.SOURCE)));

        List<PropertyName> properties = new ArrayList<PropertyName>();
        properties.add(CoalescePropertyFactory.getFieldProperty(CoalesceEnumeration.METADATA_RECORDSET, "enumname"));

        Query query = new Query();
        query.setFilter(ff.and(filters));
        query.setProperties(properties);

        try
        {
            SearchDataObjectResponse results = search.search(ff.and(filters),
                                                             1,
                                                             properties.toArray(new PropertyName[properties.size()]),
                                                             null,
                                                             true);

            for (QueryResultsType result : results.getResult())
            {
                for (HitType hit : result.getResult().getHits())
                {
                    enumerations.put(hit.getEntityKey(), hit.getValues().get(0));
                }
            }
        }
        catch (CoalesceException e)
        {
            LOGGER.error(CoalesceErrors.FAILED_TASK,
                         this.getClass().getSimpleName(),
                         search.getClass().getSimpleName(),
                         e.getMessage(),
                         e);
        }

        return enumerations;
    }

    /**
     * @param key
     * @return an enumeration in JSON format
     */
    public JSONObject getEnumeration(String key)
    {
        JSONObject json = null;
        
        try
        {
            Results<CoalesceEntity>[] results = crud.retrieveDataObjects(key);

            if (results != null && results.length == 1)
            {
                CoalesceEnumeration enumeration = new CoalesceEnumeration();
                enumeration.initialize(results[0].getResult());
                
                json = exim.exportValues(enumeration, true);
            }
            else
            {
                // TODO Log Error
            }
        }
        catch (RemoteException | CoalesceException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return json;
    }

    /**
     * Saves an enumeration
     * 
     * @param key
     * @param value
     */
    public void updateEnumeration(String key, JSONObject value)
    {
        try
        {
            Results<CoalesceEntity>[] results = crud.retrieveDataObjects(key);

            if (results != null && results.length == 1)
            {
                CoalesceEnumeration enumeration = new CoalesceEnumeration();
                enumeration.initialize(results[0].getResult());
                
                exim.importValues(value, enumeration);
            }
            else
            {
                // TODO Log Error
            }
        }
        catch (RemoteException | CoalesceException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
