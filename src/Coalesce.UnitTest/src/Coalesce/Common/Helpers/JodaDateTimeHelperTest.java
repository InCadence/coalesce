package Coalesce.Common.Helpers;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

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

public class JodaDateTimeHelperTest {

    /*
     * @BeforeClass public static void setUpBeforeClass() throws Exception { }
     * 
     * @AfterClass public static void tearDownAfterClass() throws Exception { }
     * 
     * @Before public void setUp() throws Exception { }
     * 
     * @After public void tearDown() throws Exception { }
     */

    @Test
    public void ConvertYYYYMMDDDateStringToDateTimeTest()
    {

        DateTime converted = JodaDateTimeHelper.ConvertyyyyMMddDateStringToDateTime("20141122");

        assertEquals(2014, converted.getYear());
        assertEquals(11, converted.getMonthOfYear());
        assertEquals(22, converted.getDayOfMonth());
        assertEquals(0, converted.getHourOfDay());
        assertEquals(0, converted.getMinuteOfHour());
        assertEquals(0, converted.getSecondOfMinute());

    }

    @Test
    public void ConvertYYYYMMDDDateSTringToDateTimeBadStringFormatTest()
    {

        DateTime converted = JodaDateTimeHelper.ConvertyyyyMMddDateStringToDateTime("2014-11-22");

        assertNull(converted);

    }

    @Test
    public void ConvertYYYYMMDDDateSTringToDateTimeNullTest()
    {

        DateTime converted = JodaDateTimeHelper.ConvertyyyyMMddDateStringToDateTime(null);

        assertNull(converted);

    }

    @Test
    public void MilitaryFormatNoDateTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9, DateTimeZone.UTC);

        String milDate = JodaDateTimeHelper.MilitaryFormat(date, true);

        assertEquals("2014-05-06", milDate);

    }

    @Test
    public void MilitaryFormatNullDateTest()
    {

        String milDate = JodaDateTimeHelper.MilitaryFormat(null, true);

        assertEquals("", milDate);

    }

    @Test
    public void MilitaryFormatNullDateNoDateTest()
    {

        String milDate = JodaDateTimeHelper.MilitaryFormat(null, false);

        assertEquals("", milDate);

    }

    @Test
    public void MilitaryFormatDateTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9, DateTimeZone.UTC);

        String milDate = JodaDateTimeHelper.MilitaryFormat(date, false);

        assertEquals("2014-05-06 07:08:09Z", milDate);

    }

    @Test
    public void MilitaryFormatNoUTCTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);

        String milDate = JodaDateTimeHelper.MilitaryFormat(date, false);

        String offset = date.toString("ZZ");
        assertEquals("2014-05-06 07:08:09" + offset, milDate);

    }

    @Test
    public void GetElapsedGMTTimeStringSameTimeTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(date);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(now, date, true, false, true);

        assertEquals("(0 seconds ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringOneSecondTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 7, 8, 10);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 second ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringTwoSecondsTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 7, 8, 11);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(2 seconds ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringFiftyNineTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 7, 9, 8);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(59 seconds ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringLessThanMinuteInFutureTest()
    {

        DateTime now = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime date = new DateTime(2014, 5, 6, 7, 9, 8);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(59 seconds till)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringOneMinuteTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 7, 9, 9);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 minute ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringOneMinuteOneSecondTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 7, 9, 10);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 minute ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringOneMinuteFiftyNineSecondsTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 7, 10, 8);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 minute ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringTwoMinutesTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 7, 10, 9);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(2 minutes ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringTwoMinutesOneSecondTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 7, 10, 10);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(2 minutes ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringFiftyNineMinutesFiftyNineSecondsTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 8, 8, 8);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(59 minutes ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringMinutesInFutureTest()
    {

        DateTime now = new DateTime(2014, 5, 6, 8, 8, 8);
        DateTime date = new DateTime(2014, 5, 6, 8, 38, 8);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(30 minutes till)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringOneHourTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 8, 8, 9);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 hour ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringOneHourOneSecondTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 8, 8, 10);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 hour ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringOneHourThirtyMinutesTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 8, 38, 8);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 hour ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringOneHourFiftyNineMinutesFiftyNineSecondsTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 8, 8, 8);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 hour ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringOneHourThirtyMinutesInFutureTest()
    {

        DateTime now = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime date = new DateTime(2014, 5, 6, 8, 38, 9);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 hour till)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringTwoHoursTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 8, 8, 9);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(2 hours ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringTwentyThreeHoursFiftyNineMinutesFiftyNineSecondsTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime now = new DateTime(2014, 5, 7, 6, 8, 8);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(23 hours ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringTwentyFourHoursAgoTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime now = new DateTime(2014, 5, 7, 6, 8, 9);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(Yesterday)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringFourtySevenFiftyNineMinutesFiftyNineSecondsAgoTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime now = new DateTime(2014, 5, 8, 6, 8, 8);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(Yesterday)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringTomorrowTest()
    {

        DateTime now = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime date = new DateTime(2014, 5, 8, 6, 8, 8);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(Tomorrow)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringFourtyEightHoursAgoTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime now = new DateTime(2014, 5, 8, 6, 8, 9);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(2 days ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringFourtyEightHoursTillTest()
    {

        DateTime now = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime date = new DateTime(2014, 5, 8, 6, 8, 9);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(2 days till)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringThreeHundredFiftyFourDaysFiftyNineMinutesFiftyNineSecondsAgoTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime now = new DateTime(2015, 5, 6, 6, 8, 8);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(364 days ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringOneYearAgoTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime now = new DateTime(2015, 5, 6, 6, 8, 9);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 year ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringOneYearThreeHundredFiftyFourDaysFiftyNineMinuesFiftyNineSecondsAgoTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime now = new DateTime(2016, 5, 5, 6, 8, 8);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 year ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringTwoYearsAgoTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime now = new DateTime(2016, 5, 5, 6, 8, 9);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(2 years ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringOneYearTillTest()
    {

        DateTime now = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime date = new DateTime(2015, 5, 6, 6, 8, 9);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 year till)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringWithoutParenthesisTest()
    {

        DateTime now = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime date = new DateTime(2015, 5, 6, 6, 8, 9);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, false, false, true);

        assertEquals("1 year till", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringWithDateTimeTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, DateTimeZone.UTC);
        DateTime now = new DateTime(2016, 5, 6, 6, 7, 9, DateTimeZone.UTC);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, true, false);

        assertEquals("2014-05-06 06:08:09Z (2 years ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringWithDateTimeNotUTCTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime now = new DateTime(2016, 5, 6, 6, 7, 9);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, true, false);

        String offset = date.toString("ZZ");
        assertEquals("2014-05-06 06:08:09" + offset + " (2 years ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringWithDateOnlyTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, DateTimeZone.UTC);
        DateTime now = new DateTime(2016, 5, 6, 6, 7, 9, DateTimeZone.UTC);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, true, true);

        assertEquals("2014-05-06 (2 years ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringAllOffTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, DateTimeZone.UTC);
        DateTime now = new DateTime(2016, 5, 6, 6, 7, 9, DateTimeZone.UTC);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, false, false, false);

        assertEquals("2 years ago", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringTimeZoneTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, DateTimeZone.forOffsetHours(-4));
        DateTime now = new DateTime(2016, 5, 6, 6, 7, 9, DateTimeZone.UTC);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, true, false);

        assertEquals("2014-05-06 06:08:09-04:00 (2 years ago)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringFourHoursTillTimeZoneTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, DateTimeZone.forOffsetHours(-4));
        DateTime now = new DateTime(2014, 5, 6, 6, 8, 9, DateTimeZone.UTC);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, true, false);

        assertEquals("2014-05-06 06:08:09-04:00 (4 hours till)", elapsed);
    }

    @Test
    public void GetElapsedGMTTimeStringThreeHoursFiftyNineSecondsTillTimeZoneTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, DateTimeZone.forOffsetHours(-4));
        DateTime now = new DateTime(2014, 5, 6, 6, 8, 10, DateTimeZone.UTC);

        String elapsed = JodaDateTimeHelper.GetElapsedGMTTimeString(date, now, true, true, false);

        assertEquals("2014-05-06 06:08:09-04:00 (3 hours till)", elapsed);
    }

    @Test
    public void ToXmlDateTimeUTCTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, DateTimeZone.forOffsetHours(-4));

        String utcTime = JodaDateTimeHelper.ToXmlDateTimeUTC(date);

        assertEquals("2014-05-06T10:08:09.000Z", utcTime);

    }

    @Test
    public void ToXmlDateTimeUTCNullDateTest()
    {

        String utcTime = JodaDateTimeHelper.ToXmlDateTimeUTC(null);

        assertEquals("", utcTime);

    }

    @Test
    public void FromXmlDateTimeUTCTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, DateTimeZone.forOffsetHours(-4));

        String utcTime = JodaDateTimeHelper.ToXmlDateTimeUTC(date);

        DateTime fromDate = JodaDateTimeHelper.FromXmlDateTimeUTC(utcTime);

        assertEquals(2014, fromDate.getYear());
        assertEquals(5, fromDate.getMonthOfYear());
        assertEquals(6, fromDate.getDayOfMonth());
        assertEquals(10, fromDate.getHourOfDay());
        assertEquals(8, fromDate.getMinuteOfHour());
        assertEquals(9, fromDate.getSecondOfMinute());

    }

    @Test
    public void FromXmlDateTimeUTCNullStringTest()
    {

        DateTime fromDate = JodaDateTimeHelper.FromXmlDateTimeUTC(null);

        assertNull(fromDate);
    }

    @Test
    public void FromXmlDateTimeUTCEmptyStringTest()
    {

        DateTime fromDate = JodaDateTimeHelper.FromXmlDateTimeUTC("");

        assertNull(fromDate);
    }

    @Test
    public void FromXmlDateTimeUTCWhiteSpaceStringTest()
    {

        DateTime fromDate = JodaDateTimeHelper.FromXmlDateTimeUTC(" ");

        assertNull(fromDate);
    }

    @Test
    public void FromXmlDateTimeUTCBadFormatTest()
    {

        DateTime fromDate = JodaDateTimeHelper.FromXmlDateTimeUTC("2014-05-06 10:08:09");

        assertNull(fromDate);
    }

    @Test
    public void FromXmlJodaDateTimeUtcNanoTest()
    {

        String dateXml = "2014-05-02T14:33:51.8595755Z";

        DateTime utcDate = JodaDateTimeHelper.FromXmlDateTimeUTC(dateXml);

        assertEquals(2014, utcDate.getYear());
        assertEquals(5, utcDate.getMonthOfYear());
        assertEquals(2, utcDate.getDayOfMonth());
        assertEquals(14, utcDate.getHourOfDay());
        assertEquals(33, utcDate.getMinuteOfHour());
        assertEquals(51, utcDate.getSecondOfMinute());
        assertEquals(859, utcDate.getMillisOfSecond());

    }

    @Test
    public void FromXmlJodaDateTimeUtcMillisecondTest()
    {

        String dateXml = "2014-05-02T14:33:51.859Z";

        DateTime utcDate = JodaDateTimeHelper.FromXmlDateTimeUTC(dateXml);

        assertEquals(2014, utcDate.getYear());
        assertEquals(5, utcDate.getMonthOfYear());
        assertEquals(2, utcDate.getDayOfMonth());
        assertEquals(14, utcDate.getHourOfDay());
        assertEquals(33, utcDate.getMinuteOfHour());
        assertEquals(51, utcDate.getSecondOfMinute());
        assertEquals(859, utcDate.getMillisOfSecond());

    }

    @Test
    public void ToXmlJodaDateTimeUtcNanoTest()
    {

        String dateXml = "2014-05-02T14:33:51.8595755Z";

        DateTime utcDate = JodaDateTimeHelper.FromXmlDateTimeUTC(dateXml);

        String toXmlDate = JodaDateTimeHelper.ToXmlDateTimeUTC(utcDate);

        assertEquals("2014-05-02T14:33:51.859Z", toXmlDate);
    }

    @Test
    public void ToXmlJodaDateTimeUtcMillisecondTest()
    {

        String dateXml = "2014-05-02T14:33:51.859Z";

        DateTime utcDate = JodaDateTimeHelper.FromXmlDateTimeUTC(dateXml);

        String toXmlDate = JodaDateTimeHelper.ToXmlDateTimeUTC(utcDate);

        assertEquals("2014-05-02T14:33:51.859Z", toXmlDate);
    }

}
