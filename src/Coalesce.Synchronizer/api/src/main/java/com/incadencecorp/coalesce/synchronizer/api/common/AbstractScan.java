/*-----------------------------------------------------------------------------'
 Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.synchronizer.api.common;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.CoalesceComponentImpl;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.synchronizer.api.IPersistorScan;
import org.geotools.data.Query;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.PropertyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Abstract implementation to be used as the base of all scanner
 * implementations.
 *
 * @author n78554
 * @see SynchronizerParameters#PARAM_SCANNER_CQL
 */
public abstract class AbstractScan extends CoalesceComponentImpl implements IPersistorScan {

    private static Logger LOGGER = LoggerFactory.getLogger(AbstractScan.class);

    private boolean isInitialized = false;

    private ICoalesceSearchPersistor source;
    private Set<String> columns;
    private Filter filter;

    @Override
    public final void setup()
    {
        if (!isInitialized)
        {
            doSetup();
            isInitialized = true;
        }
    }

    @Override
    public void setProperties(Map<String, String> properties)
    {
        super.setProperties(properties);

        String cql = null;

        // Last Successful Scan Configured?
        if (parameters.containsKey(SynchronizerParameters.PARAM_SCANNER_CQL))
        {
            cql = parameters.get(SynchronizerParameters.PARAM_SCANNER_CQL);
        }
        else if (loader != null)
        {
            cql = parameters.get(SynchronizerParameters.PARAM_SCANNER_CQL);
        }

        if (!StringHelper.isNullOrEmpty(cql))
        {
            try
            {
                filter = CQL.toFilter(cql);
            }
            catch (CQLException e)
            {
                throw new RuntimeException(e);
            }

            LOGGER.debug("CQL = {}", cql);
            LOGGER.debug("Filter = {}", filter.toString());
        }
    }

    @Override
    public final void setSource(ICoalesceSearchPersistor source)
    {
        this.source = source;
    }

    @Override
    public void setReturnedColumns(Set<String> columns)
    {
        this.columns = new HashSet<>();
        this.columns.addAll(columns);
    }

    @Override
    public void finished(boolean successful, CachedRowSet rows)
    {
        // Do Nothing
    }

    @Override
    public final CachedRowSet scan() throws CoalesceException
    {
        Query query = new Query();

        if (filter != null)
        {
            query.setFilter(filter);
        }

        return scan(query);
    }

    @Override
    public final CachedRowSet scan(Query query) throws CoalesceException
    {
        List<PropertyName> properties = new ArrayList<>();

        // Add Specified Columns
        if (query.getProperties() != null)
        {
            properties.addAll(query.getProperties());
        }

        // Add Required Columns
        for (String column : getColumns())
        {
            properties.add(CoalescePropertyFactory.getFilterFactory().property(column));
        }

        query.setProperties(properties);

        return doScan(query);
    }

    protected abstract CachedRowSet doScan(Query query) throws CoalesceException;

    protected void doSetup()
    {
        // Do Nothing
    }

    protected ICoalesceSearchPersistor getSource()
    {
        return source;
    }

    private Set<String> getColumns()
    {
        if (columns == null)
        {
            columns = new HashSet<>();
        }
        return columns;
    }

}
