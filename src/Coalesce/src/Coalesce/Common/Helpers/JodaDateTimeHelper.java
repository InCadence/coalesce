package Coalesce.Common.Helpers;

import org.apache.commons.lang.NullArgumentException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
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

public class JodaDateTimeHelper {

    // Make static class
    private JodaDateTimeHelper()
    {
    }

    // -----------------------------------------------------------------------'
    // public Shared Methods
    // -----------------------------------------------------------------------'

    /**
     * Returns Datetime object based on the provided strDate
     * 
     * @param strDate The date string in the form of 'yyyyMMdd
     * @return A date object
     */
    public static DateTime ConvertyyyyMMddDateStringToDateTime(String strDate)
    {
        if (strDate == null) throw new NullArgumentException("strDate");

        try
        {
            DateTimeFormatter dateFormat = ISODateTimeFormat.basicDate().withZoneUTC();

            return dateFormat.parseDateTime(strDate);

        }
        catch (IllegalArgumentException iae)
        {
            return null;
        }
    }

    /**
     * Returns a string representation of the myDate parameter. If dateOnly is true it will be formated as Else 'yyyy-MM-dd
     * HH:mm:ssZZ'
     * 
     * @param myDate The date to be formatted as a string
     * @param dateOnly Should the time be included along with the date
     * @return The date converted to a string
     */
    public static String MilitaryFormat(DateTime myDate, boolean dateOnly)
    {
        if (myDate == null) throw new NullArgumentException("myDate");

        if (dateOnly)
        {
            return myDate.toString(ISODateTimeFormat.date());
        }
        else
        {
            return myDate.toString(ISODateTimeFormat.dateTimeNoMillis()).replace("T", " ");
        }
    }

    public static String toMySQLDateTime(DateTime value)
    {
        return value.toString().replace("T", " ").replace("Z", "");
    }

    public static DateTime getMySQLDateTime(String value)
    {
        if (value.indexOf(" ") > 1)
        {
            value = value.replace(" ", "T") + "Z";
        }
        else if (value.indexOf("T") > 1 && value.indexOf("Z") == 0)
        {
            value = value + "Z";
        }

        return DateTime.parse(value);
    }

    public static String toPostGestSQLDateTime(DateTime value){
        return value.toString();
    }
    public static DateTime getPostGresDateTim(String value)
    {
        try
        {
            //Locale bLocale = new Locale.Builder().setLanguage("en").setRegion("US").build();
            DateTimeFormatter outputFormatter 
            = DateTimeFormat.forPattern("yyyy-MM-dd H:mm:ss.SSSZ").withZone(DateTimeZone.UTC);
            DateTime dtOut=DateTime.parse(value,outputFormatter);
            return dtOut;
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public static String GetElapsedGMTTimeString(DateTime ForDate, boolean IncludeParenthesis, boolean IncludeTime)
    {
        return GetElapsedGMTTimeString(ForDate, IncludeParenthesis, IncludeTime, IncludeTime);
    }

    public static String GetElapsedGMTTimeString(DateTime forDate,
                                                 boolean includeParenthesis,
                                                 boolean includeDateTime,
                                                 boolean dateOnly)
    {
        return GetElapsedGMTTimeString(forDate,
                                       new DateTime(DateTimeZone.UTC),
                                       includeParenthesis,
                                       includeDateTime,
                                       dateOnly);

    }

    public static String GetElapsedGMTTimeString(DateTime firstDate,
                                                 DateTime secondDate,
                                                 boolean includeParenthesis,
                                                 boolean includeDateTime,
                                                 boolean dateOnly)
    {
        // Is ForDate Null?
        if (firstDate == null) throw new NullArgumentException("firstDate");
        if (secondDate == null) throw new NullArgumentException("secondDate");

        boolean IsFutureDate = false;
        String elapsedString = "";

        Duration dateDiff = new Duration(firstDate, secondDate);
        long totalSeconds = dateDiff.getStandardSeconds();

        if (totalSeconds < 0)
        {
            totalSeconds = totalSeconds * -1;
            IsFutureDate = true;
        }

        if (totalSeconds < 60)
        {

            // 0 <= ForDate < 1 minute
            if (totalSeconds == 1)
            {
                elapsedString = "1 second";
            }
            else
            {
                elapsedString = totalSeconds + " seconds";
            }

            if (IsFutureDate)
            {
                elapsedString += " till";
            }
            else
            {
                elapsedString += " ago";
            }

        }
        else if (totalSeconds < 3600)
        {

            // 1 minute <= ForDate < 1 Hour
            long TotalMinutes = totalSeconds / 60;

            if (TotalMinutes == 1)
            {
                elapsedString = "1 minute";
            }
            else
            {
                elapsedString = TotalMinutes + " minutes";
            }

            if (IsFutureDate)
            {
                elapsedString += " till";
            }
            else
            {
                elapsedString += " ago";
            }

        }
        else if (totalSeconds < 86400)
        {

            // 1 Hour <= For Date < 24 Hours
            long TotalHours = (totalSeconds / 3600);

            if (TotalHours == 1)
            {
                elapsedString = "1 hour";
            }
            else
            {
                elapsedString = TotalHours + " hours";
            }

            if (IsFutureDate)
            {
                elapsedString = elapsedString + " till";
            }
            else
            {
                elapsedString = elapsedString + " ago";
            }

        }
        else if (totalSeconds < 172800)
        {

            // Yesterday
            if (IsFutureDate)
            {
                elapsedString = "Tomorrow";
            }
            else
            {
                elapsedString = "Yesterday";
            }

        }
        else if (totalSeconds < 31536000)
        {

            long TotalDays = totalSeconds / 86400;

            if (IsFutureDate)
            {
                elapsedString = TotalDays + " days till";
            }
            else
            {
                elapsedString = TotalDays + " days ago";
            }

        }
        else
        {
            long TotalYears = totalSeconds / 31536000;

            if (TotalYears == 1)
            {
                elapsedString = "1 year";
            }
            else
            {
                elapsedString = TotalYears + " years";
            }

            if (IsFutureDate)
            {
                elapsedString = elapsedString + " till";
            }
            else
            {
                elapsedString = elapsedString + " ago";
            }

        }

        // Trim
        elapsedString = elapsedString.trim();

        // Parenthesis?
        if (includeParenthesis && !StringHelper.IsNullOrEmpty(elapsedString))
        {
            elapsedString = "(" + elapsedString + ")";
        }

        if (includeDateTime)
        {
            elapsedString = MilitaryFormat(firstDate, dateOnly) + " " + elapsedString;
        }

        return elapsedString;
    }

    public static String ToXmlDateTimeUTC(DateTime forDate)
    {
        if (forDate == null) throw new NullArgumentException("forDate");

        DateTimeFormatter formatter = ISODateTimeFormat.dateTime().withZoneUTC();

        String toXmlDate = formatter.print(forDate);

        return toXmlDate;

    }

    public static DateTime FromXmlDateTimeUTC(String xmlDate)
    {
        if (xmlDate == null) throw new NullArgumentException("xmlDate");

        try
        {
            DateTimeFormatter formatter = ISODateTimeFormat.dateTime().withZoneUTC();

            DateTime forDate = formatter.parseDateTime(xmlDate);

            return forDate;

        }
        catch (IllegalArgumentException iae)
        {
            return null;
        }
    }

    /*
     * public static XMLGregorianCalendar toXmlGregorianCalendar(DateTime date) {
     * 
     * GregorianCalendar calendar = new GregorianCalendar(); calendar.setTime(date); XMLGregorianCalendar xmlCalendar = null;
     * 
     * try { xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar); } catch
     * (DatatypeConfigurationException ex) { CallResult.log(CallResults.FAILED_ERROR, ex, JodaDateTimeHelper.MODULE); }
     * 
     * return xmlCalendar;
     * 
     * }
     */

    /*
     * public static DateTime ConvertDateToGMT(String DateStr) { try {
     * 
     * DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis().withZoneUTC();
     * 
     * DateTime date = formatter.parseDateTime(DateStr);
     * 
     * return date;
     * 
     * } catch (Exception ex) { CallResult.log(CallResults.FAILED_ERROR, ex, JodaDateTimeHelper.MODULE);
     * 
     * return null; } }
     * 
     * public static DateTime ConvertDateToGMT(DateTime originalDate) { try {
     * 
     * DateTime gmtDate = new DateTime(originalDate, DateTimeZone.UTC);
     * 
     * return gmtDate;
     * 
     * } catch (Exception ex) { CallResult.log(CallResults.FAILED_ERROR, ex, JodaDateTimeHelper.MODULE);
     * 
     * return null; } }
     */

    public static DateTime NowInUtc()
    {
        return new DateTime(DateTimeZone.UTC);
    }

    /*
     * public static String ConvertDateToString(DateTime dateVal, String format) { DateTimeFormatter formatter =
     * DateTimeFormat.forPattern(format); String reportDate = formatter.print(dateVal);
     * 
     * return reportDate; }
     */

    /*
     * public static long getDateTicks(DateTime date) { try { // vb.net tick = 100 nanoseconds - //
     * http://visualbasic.about.com/od/usingvbnet/a/ticktimer01.htm // for a datetime, the ticks count is how many ticks have
     * passed // since 12:00:00 midnight on January 1, 0001 // 1 second = 1,000,000,000 nanoseconds - //
     * https://www.google.com
     * /search?q=how+many+nanoseconds+are+in+a+second&ie=utf-8&oe=utf-8&aq=t&rls=org.mozilla:en-US:official
     * &client=firefox-a&channel=nts
     * 
     * // current year is incomplete, don't add an entire year for it long year = date.getYear(); int month =
     * date.getMonthOfYear(); int day = date.getDayOfMonth(); int hour = date.getHourOfDay(); int minute =
     * date.getMinuteOfHour(); int second = date.getSecondOfMinute();
     * 
     * // how many leap years? long leapyears = year / 4; float leapfloat = year % 4; long nonLeapYears = year - leapyears;
     * 
     * // determine if this year is a leap year or not and reduce correct // classification by 1 if (leapfloat == 0.0)
     * leapyears = leapyears - 1; else nonLeapYears = nonLeapYears - 1;
     * 
     * // read current year year += 1;
     * 
     * long regYearSeconds = 31536000; long leapYearSeconds = 31622400; long daySeconds = 86400; long hourSeconds = 3600;
     * long minSeconds = 60;
     * 
     * // start ticks at the number of seconds for complete years long ticks = (regYearSeconds * nonLeapYears) +
     * (leapYearSeconds * leapyears);
     * 
     * // calculate the number of seconds expired for completed months long addMoTicks = 0; for (int i = 1; i < month; i++) {
     * switch (month) { case 1: addMoTicks += (31 * daySeconds); break; case 2: if (leapfloat == 0.0) addMoTicks += (29 *
     * daySeconds); else addMoTicks += (28 * daySeconds); break; case 3: addMoTicks += (31 * daySeconds); break; case 4:
     * addMoTicks += (30 * daySeconds); break; case 5: addMoTicks += (31 * daySeconds); break; case 6: addMoTicks += (30 *
     * daySeconds); break; case 7: addMoTicks += (31 * daySeconds); break; case 8: addMoTicks += (31 * daySeconds); break;
     * case 9: addMoTicks += (30 * daySeconds); break; case 10: addMoTicks += (31 * daySeconds); break; case 11: addMoTicks
     * += (30 * daySeconds); break; case 12: addMoTicks += (31 * daySeconds); break; } }
     * 
     * // add seconds expired for completed months to ticks ticks += addMoTicks;
     * 
     * // add seconds for days, hours, minutes and seconds. ticks = ticks + ((day - 1) * daySeconds) + ((hour - 1) *
     * hourSeconds) + ((minute - 1) * minSeconds) + second;
     * 
     * // multiply by 1 billion to get the nanosecond count ticks = ticks * 1000000000;
     * 
     * return ticks;
     * 
     * } catch (Exception ex) { return 0; }
     * 
     * }
     */

}
