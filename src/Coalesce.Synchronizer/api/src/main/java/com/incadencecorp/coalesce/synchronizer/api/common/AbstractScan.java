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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.rowset.CachedRowSet;

import org.geotools.data.Query;
import org.opengis.filter.expression.PropertyName;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.CoalesceComponentImpl;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.synchronizer.api.IPersistorScan;

/**
 * Abstract implementation to be used as the base of all scanner
 * implementations.
 * 
 * @author n78554
 */
public abstract class AbstractScan extends CoalesceComponentImpl implements IPersistorScan {

    private boolean isInitialized = false;

    private ICoalesceSearchPersistor source;
    private Set<String> columns;

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
    public final void setSource(ICoalesceSearchPersistor source)
    {
        this.source = source;
    }

    @Override
    public void setReturnedColumns(Set<String> columns)
    {
        this.columns = new HashSet<String>();
        this.columns.addAll(columns);
    }

    @Override
    public void finished(boolean successful, CachedRowSet rows)
    {
        // Do Nothing
    }

    @Override
    public final CachedRowSet scan(Query query) throws CoalesceException
    {
        List<PropertyName> properties = new ArrayList<PropertyName>();

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
            columns = new HashSet<String>();
        }
        return columns;
    }

}
