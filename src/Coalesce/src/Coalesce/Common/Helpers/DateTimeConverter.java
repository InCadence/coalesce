package Coalesce.Common.Helpers;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

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

/**
 * Performs the translation between date times as strings stored in the format yyyy-MM-dd'T'HH:mm:ss.SSSZZ and
 * {@link org.joda.time.DateTime}.
 * 
 * @author InCadence
 *
 */
public class DateTimeConverter {

    // Make static class
    private DateTimeConverter()
    {
        
    }
    
    
    // -----------------------------------------------------------------------'
    // Public Shared Methods
    // -----------------------------------------------------------------------'

    /**
     * Converts a {@link org.joda.time.DateTime} to a string format to be stored in
     * {@link Coalesce.Framework.DataModel.CoalesceEntity} xml.
     * 
     * @param value the date/time to be converted
     * @return A string version of <code>value</code> converted to the format yyyy-MM-dd'T'HH:mm:ss.SSSZZ
     */
    public static String printDate(DateTime value)
    {

        DateTimeFormatter formatter = ISODateTimeFormat.dateTime().withZoneUTC();

        return formatter.print(value);

    }

    /**
     * Converts a {@link java.lang.String} to a {@link org.joda.time.DateTime}. The string format is assumed to be formatted
     * as yyyy-MM-dd'T'HH:mm:ss.SSSZZ
     * 
     * @param value the date/time string to be converted
     * @return A {@link org.joda.time.DateTime} representation of <code>value</code>
     */
    public static DateTime parseDate(String value)
    {

        DateTimeFormatter formatter = ISODateTimeFormat.dateTime().withZoneUTC();

        return formatter.parseDateTime(value);

    }
}
