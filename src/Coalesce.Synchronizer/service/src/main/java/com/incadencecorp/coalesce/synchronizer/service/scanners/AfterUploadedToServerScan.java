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

package com.incadencecorp.coalesce.synchronizer.service.scanners;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractScan;
import com.incadencecorp.coalesce.synchronizer.api.common.SynchronizerParameters;
import org.geotools.data.Query;
import org.geotools.filter.SortByImpl;
import org.geotools.temporal.object.DefaultInstant;
import org.geotools.temporal.object.DefaultPeriod;
import org.geotools.temporal.object.DefaultPosition;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.opengis.temporal.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This implementation looks for changes after a given DateTime which is either
 * passed in as a parameter or read from the configured property loader. Setting
 * the last scanned parameter will override the property loader.
 * <p>
 * After a successful scan it records the start time of that scan and sequential
 * scans will be from the last successful. If the property loader is configured
 * the last successful scan is persisted between restarts.
 *
 * @author GGaito
 * @see SynchronizerParameters#PARAM_SCANNER_LAST_SUCCESS
 * @see SynchronizerParameters#PARAM_SCANNER_CQL
 * @see SynchronizerParameters#PARAM_SCANNER_WINDOW
 * @see SynchronizerParameters#PARAM_SCANNER_WINDOW_UNITS
 *
 */
public class AfterUploadedToServerScan extends AbstractScan {

    private static final FilterFactory FF = CoalescePropertyFactory.getFilterFactory();
    private static final Logger LOGGER = LoggerFactory.getLogger(AfterUploadedToServerScan.class);

    private String lastScanned = null;
    private String pendingLastScan = null;
    private int max = SynchronizerParameters.DEFAULT_SCANNER_MAX;
    private Integer window = null;
    private TimeUnit windowUnit = TimeUnit.DAYS;

    @Override
    public CachedRowSet doScan(Query query) throws CoalesceException
    {

        if(loader != null)
        {
            if(loader.getProperty("com.incadencecorp.scanners.lastscan") != null)
            {
                lastScanned = loader.getProperty("com.incadencecorp.scanners.lastscan");

            }
        }
        // Last Scan Specified?
        if (lastScanned == null)
        {
            // No; Go back 10 years
            lastScanned = JodaDateTimeHelper.toXmlDateTimeUTC(JodaDateTimeHelper.nowInUtc().minusYears(10));
        }

        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Last Successful Scan: {}", lastScanned);
        }
        DateTime start = JodaDateTimeHelper.fromXmlDateTimeUTC(lastScanned);
//        DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
//        DateTime start = formatter.parseDateTime(lastScanned);
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Start time: {}", start);
        }
        if (start == null)
        {
            throw new CoalesceException(String.format(CoalesceErrors.INVALID_INPUT, lastScanned));
        }

        Instant startInstant = new DefaultInstant(new DefaultPosition(start.toDate()));
        Filter temporal;

        if (window == null || window == 0)
        {
            temporal = FF.after(CoalescePropertyFactory.getUploadedToServer(), FF.literal(start));
        }
        else
        {
            DateTime end = JodaDateTimeHelper.plus(start, window, windowUnit);
            pendingLastScan = JodaDateTimeHelper.toXmlDateTimeUTC(end);

            Instant endInstant = new DefaultInstant(new DefaultPosition(end.toDate()));

            temporal = FF.during(CoalescePropertyFactory.getUploadedToServer(),
                    FF.literal(new DefaultPeriod(startInstant, endInstant)));
        }

        // Filter Specified?
        if (query.getFilter() == null || query.getFilter() == Filter.INCLUDE)
        {
            // No; Set
            query.setFilter(temporal);
        }
        else
        {
            // Yes; Append Temporal Filter
            query.setFilter(FF.and(query.getFilter(), temporal));

        }

        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Filter Specified: {}", query.getFilter().toString());
        }

        // Add Last Modified Property and entity Key
        List<PropertyName> properties = new ArrayList<>();
        properties.addAll(query.getProperties());
        properties.add(CoalescePropertyFactory.getUploadedToServer());

        query.setStartIndex(0);
        query.setMaxFeatures(max);
        query.setProperties(properties);
        query.setSortBy(new SortBy[] { new SortByImpl(CoalescePropertyFactory.getLastModified(), SortOrder.ASCENDING) });

        LOGGER.info("Max is: {}", query.getMaxFeatures());

        CachedRowSet result = getSource().search(query).getResults();
        LOGGER.info(String.valueOf(result.size()));
        try
        {
            if (result.last())
            {
                String timestamp = result.getString(properties.size() + 1);
                pendingLastScan = JodaDateTimeHelper.toXmlDateTimeUTC(JodaDateTimeHelper.getMySQLDateTime(timestamp));
                LOGGER.info("Pending last scan is: {}", pendingLastScan);
            }
            result.beforeFirst();
        }
        catch (SQLException e)
        {
            throw new CoalesceException(e);
        }
        return result;
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
        if (loader != null)
        {
            lastScanned = loader.getProperty(SynchronizerParameters.PARAM_SCANNER_LAST_SUCCESS);
        }

        // Max Configured?
        if (parameters.containsKey(SynchronizerParameters.PARAM_SCANNER_MAX))
        {
            max = Integer.parseInt(parameters.get(SynchronizerParameters.PARAM_SCANNER_MAX));
        }
        else if (loader != null)
        {
            String value = loader.getProperty(SynchronizerParameters.PARAM_SCANNER_MAX);

            if (!StringHelper.isNullOrEmpty(value))
            {
                max = Integer.parseInt(value);
            }
        }

        // Window Configured?
        if (parameters.containsKey(SynchronizerParameters.PARAM_SCANNER_WINDOW))
        {
            window = Integer.parseInt(parameters.get(SynchronizerParameters.PARAM_SCANNER_WINDOW));
        }
        else if (loader != null)
        {
            String value = loader.getProperty(SynchronizerParameters.PARAM_SCANNER_WINDOW);

            if (!StringHelper.isNullOrEmpty(value))
            {
                window = Integer.parseInt(value);
            }
        }

        if (parameters.containsKey(SynchronizerParameters.PARAM_SCANNER_WINDOW_UNITS))
        {
            windowUnit = TimeUnit.valueOf(parameters.get(SynchronizerParameters.PARAM_SCANNER_WINDOW_UNITS));
        }
        else if (loader != null)
        {
            String value = loader.getProperty(SynchronizerParameters.PARAM_SCANNER_WINDOW_UNITS);

            if (!StringHelper.isNullOrEmpty(value))
            {
                windowUnit = TimeUnit.valueOf(value);
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

            if (loader != null && pendingLastScan != null)
            {
                loader.setProperty(SynchronizerParameters.PARAM_SCANNER_LAST_SUCCESS, lastScanned);
                LOGGER.info("Loader is not null");
                LOGGER.info("Last Successful Scan: {}", lastScanned);
            }
            else if(parameters.containsKey(SynchronizerParameters.PARAM_SCANNER_LAST_SUCCESS ) && pendingLastScan != null)
            {
                parameters.put(SynchronizerParameters.PARAM_SCANNER_LAST_SUCCESS,lastScanned);
                LOGGER.info("Loader is null");
                LOGGER.info("Last Successful Scan: {}", lastScanned);
            }
        }
        else
        {
            LOGGER.warn("Operations Failed");
        }
    }

}
