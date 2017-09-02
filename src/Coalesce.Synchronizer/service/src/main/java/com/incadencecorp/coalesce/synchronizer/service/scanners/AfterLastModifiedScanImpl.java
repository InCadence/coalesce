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

package com.incadencecorp.coalesce.synchronizer.service.scanners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;

import org.geotools.data.Query;
import org.joda.time.DateTime;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.common.classification.helpers.StringHelper;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractScan;
import com.incadencecorp.coalesce.synchronizer.api.common.SynchronizerParameters;

/**
 * This implementation looks for changes after a given DateTime which is either
 * passed in as a parameter or read from the configured property loader. Setting
 * the last scanned parameter will override the property loader.
 * 
 * After a successful scan it records the start time of that scan and sequential
 * scans will be from the last successful. If the property loader is configured
 * the last successful scan is persisted between restarts.
 * 
 * @author n78554
 * @see SynchronizerParameters#PARAM_SCANNER_LAST_SUCCESS
 * @see SynchronizerParameters#PARAM_SCANNER_DAYS
 */
public class AfterLastModifiedScanImpl extends AbstractScan {

    private static final FilterFactory FF = CoalescePropertyFactory.getFilterFactory();
    private static final Logger LOGGER = LoggerFactory.getLogger(AfterLastModifiedScanImpl.class);

    private String lastScanned = null;
    private String pendingLastScan = null;
    private int days = SynchronizerParameters.DEFAULT_SCANNER_DAYS;

    @Override
    protected void doSetup()
    {
        // Initialize Defaults
        setProperties(new HashMap<String, String>());
    }

    @Override
    public CachedRowSet scan() throws CoalesceException
    {
        return scan(new Query());
    }

    // TODO Instead of using a date time window to constrain the number of
    // entities being processes in a single query, consider using limits and
    // determining when to start the next query based on the results. This
    // version of Coalesce Search API does not fully support sort by preventing
    // this from being implemented now.
    @Override
    public CachedRowSet doScan(Query query) throws CoalesceException
    {
        List<CoalesceParameter> params = new ArrayList<CoalesceParameter>();

        // Last Scan Specified?
        if (lastScanned == null)
        {
            // No; Go back 10 years
            lastScanned = JodaDateTimeHelper.toXmlDateTimeUTC(JodaDateTimeHelper.nowInUtc().minusYears(10));
        }

        DateTime start = JodaDateTimeHelper.fromXmlDateTimeUTC(lastScanned);
        DateTime end = start.plusDays(days);

        // Days not specified or end of window is in the future?
        if (days == 0 || end.isAfterNow())
        {
            // Set the end of window to now
            end = JodaDateTimeHelper.nowInUtc();
        }

        pendingLastScan = JodaDateTimeHelper.toXmlDateTimeUTC(end);// JodaDateTimeHelper.nowInUtc());

        Filter requiredFilter = FF.and(FF.after(CoalescePropertyFactory.getLastModified(), FF.literal(lastScanned)),
                                       FF.before(CoalescePropertyFactory.getLastModified(), FF.literal(pendingLastScan)));

        // Filter Specified?
        if (query.getFilter() == null || query.getFilter() == Filter.INCLUDE)
        {
            // No; Set
            query.setFilter(requiredFilter);
        }
        else
        {
            if (LOGGER.isTraceEnabled())
            {
                LOGGER.trace("Filter Specified: {}", query.getFilter().toString());
            }

            // Yes; Append Required Filter
            query.setFilter(FF.and(query.getFilter(), requiredFilter));
        }

        query.setMaxFeatures(0);
        query.setStartIndex(0);

        return getSource().search(query).getResults();
    }

    @Override
    public void setProperties(Map<String, String> properties)
    {
        super.setProperties(properties);

        // Last Successful Scan Configured?
        if (parameters.containsKey(SynchronizerParameters.PARAM_SCANNER_LAST_SUCCESS))
        {
            lastScanned = parameters.get(SynchronizerParameters.PARAM_SCANNER_LAST_SUCCESS);
        }
        else if (loader != null)
        {
            lastScanned = loader.getProperty(SynchronizerParameters.PARAM_SCANNER_LAST_SUCCESS);
        }

        // Days Configured?
        if (parameters.containsKey(SynchronizerParameters.PARAM_SCANNER_DAYS))
        {
            days = Integer.parseInt(parameters.get(SynchronizerParameters.PARAM_SCANNER_DAYS));
        }
        else if (loader != null)
        {
            String value = loader.getProperty(SynchronizerParameters.PARAM_SCANNER_DAYS);

            if (!StringHelper.isNullOrEmpty(value))
            {
                days = Integer.parseInt(value);
            }
        }

        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Last Successful Scan: {}", lastScanned);
        }
    }

    @Override
    public void finished(boolean successful, CachedRowSet rows)
    {
        if (successful)
        {
            lastScanned = pendingLastScan;

            if (loader != null)
            {
                loader.setProperty(SynchronizerParameters.PARAM_SCANNER_LAST_SUCCESS, lastScanned);
                LOGGER.info("Last Successful Scan: {}", lastScanned);
            }
        }
        else
        {
            LOGGER.warn("Operations Failed");
        }
    }

}
