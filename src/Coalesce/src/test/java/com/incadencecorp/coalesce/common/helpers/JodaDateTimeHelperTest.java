package com.incadencecorp.coalesce.common.helpers;

import org.apache.commons.lang.NullArgumentException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.TimeZone;

import static org.junit.Assert.*;

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

    @Rule
    public ExpectedException _thrown = ExpectedException.none();

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
    public void convertYYYYMMDDDateStringToDateTimeTest()
    {

        DateTime converted = JodaDateTimeHelper.convertyyyyMMddDateStringToDateTime("20141122");

        assertEquals(2014, converted.getYear());
        assertEquals(11, converted.getMonthOfYear());
        assertEquals(22, converted.getDayOfMonth());
        assertEquals(0, converted.getHourOfDay());
        assertEquals(0, converted.getMinuteOfHour());
        assertEquals(0, converted.getSecondOfMinute());

    }

    @Test
    public void convertYYYYMMDDDateStringToDateTimeBadStringFormatTest()
    {

        DateTime converted = JodaDateTimeHelper.convertyyyyMMddDateStringToDateTime("2014-11-22");

        assertNull(converted);

    }

    @Test
    public void convertYYYYMMDDDateStringToDateTimeNullTest()
    {
        _thrown.expect(NullArgumentException.class);
        _thrown.expectMessage("value");

        @SuppressWarnings("unused")
        DateTime converted = JodaDateTimeHelper.convertyyyyMMddDateStringToDateTime(null);
    }

    @Test
    public void convertYYYYMMDDDateStringToDateTimeEmtpyTest()
    {

        DateTime converted = JodaDateTimeHelper.convertyyyyMMddDateStringToDateTime("");

        assertNull(converted);

    }

    @Test
    public void convertYYYYMMDDDateStringToDateTimeWhiteSpaceTest()
    {

        DateTime converted = JodaDateTimeHelper.convertyyyyMMddDateStringToDateTime("  ");

        assertNull(converted);

    }

    @Test
    public void militaryFormatNoDateTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9, 0, DateTimeZone.UTC);

        String milDate = JodaDateTimeHelper.militaryFormat(date, true);

        assertEquals("2014-05-06", milDate);

    }

    @Test
    public void militaryFormatNullDateTest()
    {
        _thrown.expect(NullArgumentException.class);
        _thrown.expectMessage("value");

        @SuppressWarnings("unused")
        String milDate = JodaDateTimeHelper.militaryFormat(null, true);
    }

    @Test
    public void militaryFormatNullDateNoDateTest()
    {
        _thrown.expect(NullArgumentException.class);
        _thrown.expectMessage("value");

        @SuppressWarnings("unused")
        String milDate = JodaDateTimeHelper.militaryFormat(null, false);
    }

    @Test
    public void militaryFormatDateTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9, 0, DateTimeZone.UTC);

        String milDate = JodaDateTimeHelper.militaryFormat(date, false);

        assertEquals("2014-05-06 07:08:09Z", milDate);

    }

    @Test
    public void militaryFormatNoUTCTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9, 0);

        String milDate = JodaDateTimeHelper.militaryFormat(date, false);

        String offset = date.toString("ZZ");

        if (offset.equals("+00:00"))
        {
            offset = "Z";
        }

        assertEquals("2014-05-06 07:08:09" + offset, milDate);

    }

    @Test
    public void getElapsedGMTTimeStringForDateTest()
    {
        DateTime now = JodaDateTimeHelper.nowInUtc();

        assertEquals("(2 days till)",
                     JodaDateTimeHelper.getElapsedGMTTimeString(now.plusDays(2).plusSeconds(1), true, false));

    }

    @Test
    public void getElapsedGMTTimeStringNullFirstDateTest()
    {
        _thrown.expect(NullArgumentException.class);
        _thrown.expectMessage("firstDate");

        JodaDateTimeHelper.getElapsedGMTTimeString(null, JodaDateTimeHelper.nowInUtc(), true, true, true);

    }

    @Test
    public void getElapsedGMTTimeStringNullSecondDateTest()
    {
        _thrown.expect(NullArgumentException.class);
        _thrown.expectMessage("secondDate");

        JodaDateTimeHelper.getElapsedGMTTimeString(JodaDateTimeHelper.nowInUtc(), null, true, true, true);

    }

    @Test
    public void getElapsedGMTTimeStringNullBothDatesTest()
    {
        _thrown.expect(NullArgumentException.class);
        _thrown.expectMessage("firstDate");

        JodaDateTimeHelper.getElapsedGMTTimeString(null, null, true, true, true);

    }

    @Test
    public void getElapsedGMTTimeStringSameTimeTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9, 0);
        DateTime now = new DateTime(date);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(now, date, true, false, true);

        assertEquals("(0 seconds ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneSecondTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9, 0);
        DateTime now = new DateTime(2014, 5, 6, 7, 8, 10, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 second ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringTwoSecondsTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9, 0);
        DateTime now = new DateTime(2014, 5, 6, 7, 8, 11, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(2 seconds ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringFiftyNineTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9, 0);
        DateTime now = new DateTime(2014, 5, 6, 7, 9, 8, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(59 seconds ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringLessThanMinuteInFutureTest()
    {

        DateTime now = new DateTime(2014, 5, 6, 7, 8, 9, 0);
        DateTime date = new DateTime(2014, 5, 6, 7, 9, 8, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(59 seconds till)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneMinuteTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9, 0);
        DateTime now = new DateTime(2014, 5, 6, 7, 9, 9, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 minute ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneMinuteOneSecondTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9, 0);
        DateTime now = new DateTime(2014, 5, 6, 7, 9, 10, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 minute ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneMinuteFiftyNineSecondsTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9, 0);
        DateTime now = new DateTime(2014, 5, 6, 7, 10, 8, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 minute ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringTwoMinutesTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9, 0);
        DateTime now = new DateTime(2014, 5, 6, 7, 10, 9, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(2 minutes ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringTwoMinutesOneSecondTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9, 0);
        DateTime now = new DateTime(2014, 5, 6, 7, 10, 10, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(2 minutes ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringFiftyNineMinutesFiftyNineSecondsTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9, 0);
        DateTime now = new DateTime(2014, 5, 6, 8, 8, 8, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(59 minutes ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringMinutesInFutureTest()
    {

        DateTime now = new DateTime(2014, 5, 6, 8, 8, 8, 0);
        DateTime date = new DateTime(2014, 5, 6, 8, 38, 8, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(30 minutes till)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneHourTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9, 0);
        DateTime now = new DateTime(2014, 5, 6, 8, 8, 9, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 hour ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneHourOneSecondTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9, 0);
        DateTime now = new DateTime(2014, 5, 6, 8, 8, 10, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 hour ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneHourThirtyMinutesTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9, 0);
        DateTime now = new DateTime(2014, 5, 6, 8, 38, 8, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 hour ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneHourFiftyNineMinutesFiftyNineSecondsTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, 0);
        DateTime now = new DateTime(2014, 5, 6, 8, 8, 8, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 hour ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneHourThirtyMinutesInFutureTest()
    {

        DateTime now = new DateTime(2014, 5, 6, 7, 8, 9, 0);
        DateTime date = new DateTime(2014, 5, 6, 8, 38, 9, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 hour till)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringTwoHoursTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, 0);
        DateTime now = new DateTime(2014, 5, 6, 8, 8, 9, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(2 hours ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringTwentyThreeHoursFiftyNineMinutesFiftyNineSecondsTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, 0);
        DateTime now = new DateTime(2014, 5, 7, 6, 8, 8, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(23 hours ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringTwentyFourHoursAgoTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, 0);
        DateTime now = new DateTime(2014, 5, 7, 6, 8, 9, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(Yesterday)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringFourtySevenFiftyNineMinutesFiftyNineSecondsAgoTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, 0);
        DateTime now = new DateTime(2014, 5, 8, 6, 8, 8, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(Yesterday)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringTomorrowTest()
    {

        DateTime now = new DateTime(2014, 5, 6, 6, 8, 9, 0);
        DateTime date = new DateTime(2014, 5, 8, 6, 8, 8, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(Tomorrow)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringFourtyEightHoursAgoTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, 0);
        DateTime now = new DateTime(2014, 5, 8, 6, 8, 9, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(2 days ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringFourtyEightHoursTillTest()
    {

        DateTime now = new DateTime(2014, 5, 6, 6, 8, 9, 0);
        DateTime date = new DateTime(2014, 5, 8, 6, 8, 9, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(2 days till)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringThreeHundredFiftyFourDaysFiftyNineMinutesFiftyNineSecondsAgoTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, 0);
        DateTime now = new DateTime(2015, 5, 6, 6, 8, 8, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(364 days ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneYearAgoTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, 0);
        DateTime now = new DateTime(2015, 5, 6, 6, 8, 9, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 year ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneYearThreeHundredFiftyFourDaysFiftyNineMinuesFiftyNineSecondsAgoTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, 0);
        DateTime now = new DateTime(2016, 5, 5, 6, 8, 8, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 year ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringTwoYearsAgoTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, 0);
        DateTime now = new DateTime(2016, 5, 5, 6, 8, 9, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(2 years ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneYearTillTest()
    {

        DateTime now = new DateTime(2014, 5, 6, 6, 8, 9, 0);
        DateTime date = new DateTime(2015, 5, 6, 6, 8, 9, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 year till)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringWithoutParenthesisTest()
    {

        DateTime now = new DateTime(2014, 5, 6, 6, 8, 9, 0);
        DateTime date = new DateTime(2015, 5, 6, 6, 8, 9, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, false, false, true);

        assertEquals("1 year till", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringWithDateTimeTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, 0, DateTimeZone.UTC);
        DateTime now = new DateTime(2016, 5, 6, 6, 7, 9, 0, DateTimeZone.UTC);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, true, false);

        assertEquals("2014-05-06 06:08:09Z (2 years ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringWithDateTimeNotUTCTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, 0);
        DateTime now = new DateTime(2016, 5, 6, 6, 7, 9, 0);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, true, false);

        String offset = date.toString("ZZ");

        if (offset.equals("+00:00"))
        {
            offset = "Z";
        }

        assertEquals("2014-05-06 06:08:09" + offset + " (2 years ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringWithDateOnlyTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, 0, DateTimeZone.UTC);
        DateTime now = new DateTime(2016, 5, 6, 6, 7, 9, 0, DateTimeZone.UTC);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, true, true);

        assertEquals("2014-05-06 (2 years ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringAllOffTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, 0, DateTimeZone.UTC);
        DateTime now = new DateTime(2016, 5, 6, 6, 7, 9, 0, DateTimeZone.UTC);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, false, false, false);

        assertEquals("2 years ago", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringTimeZoneTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, 0, DateTimeZone.forOffsetHours(-4));
        DateTime now = new DateTime(2016, 5, 6, 6, 7, 9, 0, DateTimeZone.UTC);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, true, false);

        assertEquals("2014-05-06 06:08:09-04:00 (2 years ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringFourHoursTillTimeZoneTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, 0, DateTimeZone.forOffsetHours(-4));
        DateTime now = new DateTime(2014, 5, 6, 6, 8, 9, 0, DateTimeZone.UTC);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, true, false);

        assertEquals("2014-05-06 06:08:09-04:00 (4 hours till)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringThreeHoursFiftyNineSecondsTillTimeZoneTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, 0, DateTimeZone.forOffsetHours(-4));
        DateTime now = new DateTime(2014, 5, 6, 6, 8, 10, 0, DateTimeZone.UTC);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, true, false);

        assertEquals("2014-05-06 06:08:09-04:00 (3 hours till)", elapsed);
    }

    /**
     * This test ensure proper parsing of times using the PostGres format.
     */
    @Test
    public void getPostGresDateTimeTest()
    {
        DateTime date;

        date = JodaDateTimeHelper.getPostGresDateTim("2017-02-08 11:34:01.000-05");

        Assert.assertNotNull(date);
        Assert.assertEquals(2017, date.getYear());
        Assert.assertEquals(2, date.getMonthOfYear());
        Assert.assertEquals(8, date.getDayOfMonth());
        Assert.assertEquals(16, date.getHourOfDay());
        Assert.assertEquals(34, date.getMinuteOfHour());
        Assert.assertEquals(1, date.getSecondOfMinute());
        Assert.assertEquals(0, date.getMillisOfSecond());

        date = JodaDateTimeHelper.getPostGresDateTim("2017-02-08 11:34:01-04");

        Assert.assertNotNull(date);
        Assert.assertEquals(2017, date.getYear());
        Assert.assertEquals(2, date.getMonthOfYear());
        Assert.assertEquals(8, date.getDayOfMonth());
        Assert.assertEquals(15, date.getHourOfDay());
        Assert.assertEquals(34, date.getMinuteOfHour());
        Assert.assertEquals(1, date.getSecondOfMinute());
        Assert.assertEquals(0, date.getMillisOfSecond());

    }

    @Test
    public void toXmlDateTimeUTCTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, 0, DateTimeZone.forOffsetHours(-4));

        String utcTime = JodaDateTimeHelper.toXmlDateTimeUTC(date);

        assertEquals("2014-05-06T10:08:09.000Z", utcTime);

    }

    @Test
    public void toXmlDateTimeUTCNullDateTest()
    {
        String result = JodaDateTimeHelper.toXmlDateTimeUTC(null);
        Assert.assertNull(result);
    }

    @Test
    public void fromXmlDateTimeUTCTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, 0, DateTimeZone.forOffsetHours(-4));

        String utcTime = JodaDateTimeHelper.toXmlDateTimeUTC(date);

        DateTime fromDate = JodaDateTimeHelper.fromXmlDateTimeUTC(utcTime);

        assertEquals(2014, fromDate.getYear());
        assertEquals(5, fromDate.getMonthOfYear());
        assertEquals(6, fromDate.getDayOfMonth());
        assertEquals(10, fromDate.getHourOfDay());
        assertEquals(8, fromDate.getMinuteOfHour());
        assertEquals(9, fromDate.getSecondOfMinute());

    }

    @Test
    public void fromXmlDateTimeUTCNullStringTest()
    {
        assertNull(JodaDateTimeHelper.fromXmlDateTimeUTC(null));
    }

    @Test
    public void fromXmlDateTimeUTCEmptyStringTest()
    {

        DateTime fromDate = JodaDateTimeHelper.fromXmlDateTimeUTC("");

        assertNull(fromDate);
    }

    @Test
    public void fromXmlDateTimeUTCWhiteSpaceStringTest()
    {

        DateTime fromDate = JodaDateTimeHelper.fromXmlDateTimeUTC(" ");

        assertNull(fromDate);
    }

    @Test
    public void fromXmlDateTimeUTCBadFormatTest()
    {

        DateTime fromDate = JodaDateTimeHelper.fromXmlDateTimeUTC("2014-05-06 10:08:09");

        assertNull(fromDate);
    }

    @Test
    public void fromXmlJodaDateTimeUtcNanoTest()
    {

        String dateXml = "2014-05-02T14:33:51.8595755Z";

        DateTime utcDate = JodaDateTimeHelper.fromXmlDateTimeUTC(dateXml);

        assertEquals(2014, utcDate.getYear());
        assertEquals(5, utcDate.getMonthOfYear());
        assertEquals(2, utcDate.getDayOfMonth());
        assertEquals(14, utcDate.getHourOfDay());
        assertEquals(33, utcDate.getMinuteOfHour());
        assertEquals(51, utcDate.getSecondOfMinute());
        assertEquals(859, utcDate.getMillisOfSecond());

    }

    @Test
    public void fromXmlJodaDateTimeUtcMillisecondTest()
    {

        String dateXml = "2014-05-02T14:33:51.859Z";

        DateTime utcDate = JodaDateTimeHelper.fromXmlDateTimeUTC(dateXml);

        assertEquals(2014, utcDate.getYear());
        assertEquals(5, utcDate.getMonthOfYear());
        assertEquals(2, utcDate.getDayOfMonth());
        assertEquals(14, utcDate.getHourOfDay());
        assertEquals(33, utcDate.getMinuteOfHour());
        assertEquals(51, utcDate.getSecondOfMinute());
        assertEquals(859, utcDate.getMillisOfSecond());

    }

    @Test
    public void toXmlJodaDateTimeUtcNanoTest()
    {

        String dateXml = "2014-05-02T14:33:51.8595755Z";

        DateTime utcDate = JodaDateTimeHelper.fromXmlDateTimeUTC(dateXml);

        String toXmlDate = JodaDateTimeHelper.toXmlDateTimeUTC(utcDate);

        assertEquals("2014-05-02T14:33:51.859Z", toXmlDate);
    }

    @Test
    public void toXmlJodaDateTimeUtcMillisecondTest()
    {

        String dateXml = "2014-05-02T14:33:51.859Z";

        DateTime utcDate = JodaDateTimeHelper.fromXmlDateTimeUTC(dateXml);

        String toXmlDate = JodaDateTimeHelper.toXmlDateTimeUTC(utcDate);

        assertEquals("2014-05-02T14:33:51.859Z", toXmlDate);
    }

    @Test
    public void nowInUtcTest()
    {
        DateTime now = new DateTime();

        DateTime nowInUtc = JodaDateTimeHelper.nowInUtc();

        Duration dateDiff = new Duration(now, nowInUtc);
        assertTrue(Math.abs(dateDiff.getStandardSeconds()) < 2);

    }

    @Test
    public void TimeFormatTest() throws Exception
    {
        String datetie = "2018-06-12T22:59:48+00:00";
    }

    @Test
    public void testFormats() throws Exception
    {
        DateTime results;

        results = JodaDateTimeHelper.parseDateTime("20180617");

        Assert.assertNotNull(results);
        Assert.assertEquals(2018, results.getYear());
        Assert.assertEquals(6, results.getMonthOfYear());
        Assert.assertEquals(17, results.getDayOfMonth());

        results = JodaDateTimeHelper.parseDateTime("2018-06-17 05:00:00");

        Assert.assertNotNull(results);
        Assert.assertEquals(2018, results.getYear());
        Assert.assertEquals(6, results.getMonthOfYear());
        Assert.assertEquals(17, results.getDayOfMonth());

        Assert.assertEquals(5, results.getHourOfDay());
        Assert.assertEquals(0, results.getMinuteOfHour());
        Assert.assertEquals(0, results.getSecondOfMinute());

        Assert.assertEquals(TimeZone.getDefault().getID(), results.getZone().getID());

        results = JodaDateTimeHelper.parseDateTime("2018-06-17T17:47:28+0000");

        Assert.assertNotNull(results);
        Assert.assertEquals(2018, results.getYear());
        Assert.assertEquals(6, results.getMonthOfYear());
        Assert.assertEquals(17, results.getDayOfMonth());

        results = JodaDateTimeHelper.parseDateTime("2018-06-17T17:47:28+00:00");

        Assert.assertNotNull(results);
        Assert.assertEquals(2018, results.getYear());
        Assert.assertEquals(6, results.getMonthOfYear());
        Assert.assertEquals(17, results.getDayOfMonth());

    }

}
