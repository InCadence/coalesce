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

package com.incadencecorp.coalesce.search.filter;

import java.util.Date;

import org.geotools.temporal.object.DefaultInstant;
import org.geotools.temporal.object.DefaultPeriod;
import org.geotools.temporal.object.DefaultPosition;
import org.joda.time.DateTime;
import org.opengis.temporal.Period;

/**
 * This helper is used to simplify the filter creation process.
 * 
 * @author n78554
 */
public final class FilterHelper {

    private FilterHelper()
    {
        // Do Nothing
    }

    /**
     * Creates a time period which is used with temporal filters such as during.
     * 
     * @param start
     * @param end
     * @return a period
     */
    public static Period createTimePeriod(DateTime start, DateTime end)
    {
        return createTimePeriod(new Date(start.getMillis()), new Date(end.getMillis()));

    }

    /**
     * Creates a time period which is used with temporal filters such as during.
     * 
     * @param start
     * @param end
     * @return a period
     */
    public static Period createTimePeriod(Date start, Date end)
    {

        DefaultInstant startInstant = new DefaultInstant(new DefaultPosition(start));
        DefaultInstant endInstant = new DefaultInstant(new DefaultPosition(end));

        // Create Filters
        return new DefaultPeriod(startInstant, endInstant);

    }

}
