package com.incadencecorp.coalesce.common.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.NullArgumentException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9, DateTimeZone.UTC);

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

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9, DateTimeZone.UTC);

        String milDate = JodaDateTimeHelper.militaryFormat(date, false);

        assertEquals("2014-05-06 07:08:09Z", milDate);

    }

    @Test
    public void militaryFormatNoUTCTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);

        String milDate = JodaDateTimeHelper.militaryFormat(date, false);

        String offset = date.toString("ZZ");
        assertEquals("2014-05-06 07:08:09" + offset, milDate);

    }

    @Test
    public void getElapsedGMTTimeStringForDateTest()
    {
        DateTime now = JodaDateTimeHelper.nowInUtc();
        
        assertEquals("(2 days till)", JodaDateTimeHelper.getElapsedGMTTimeString(now.plusDays(2).plusSeconds(1), true, false));
        
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

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(date);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(now, date, true, false, true);

        assertEquals("(0 seconds ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneSecondTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 7, 8, 10);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 second ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringTwoSecondsTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 7, 8, 11);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(2 seconds ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringFiftyNineTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 7, 9, 8);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(59 seconds ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringLessThanMinuteInFutureTest()
    {

        DateTime now = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime date = new DateTime(2014, 5, 6, 7, 9, 8);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(59 seconds till)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneMinuteTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 7, 9, 9);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 minute ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneMinuteOneSecondTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 7, 9, 10);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 minute ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneMinuteFiftyNineSecondsTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 7, 10, 8);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 minute ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringTwoMinutesTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 7, 10, 9);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(2 minutes ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringTwoMinutesOneSecondTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 7, 10, 10);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(2 minutes ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringFiftyNineMinutesFiftyNineSecondsTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 8, 8, 8);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(59 minutes ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringMinutesInFutureTest()
    {

        DateTime now = new DateTime(2014, 5, 6, 8, 8, 8);
        DateTime date = new DateTime(2014, 5, 6, 8, 38, 8);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(30 minutes till)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneHourTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 8, 8, 9);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 hour ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneHourOneSecondTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 8, 8, 10);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 hour ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneHourThirtyMinutesTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 8, 38, 8);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 hour ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneHourFiftyNineMinutesFiftyNineSecondsTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 8, 8, 8);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 hour ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneHourThirtyMinutesInFutureTest()
    {

        DateTime now = new DateTime(2014, 5, 6, 7, 8, 9);
        DateTime date = new DateTime(2014, 5, 6, 8, 38, 9);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 hour till)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringTwoHoursTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime now = new DateTime(2014, 5, 6, 8, 8, 9);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(2 hours ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringTwentyThreeHoursFiftyNineMinutesFiftyNineSecondsTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime now = new DateTime(2014, 5, 7, 6, 8, 8);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(23 hours ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringTwentyFourHoursAgoTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime now = new DateTime(2014, 5, 7, 6, 8, 9);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(Yesterday)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringFourtySevenFiftyNineMinutesFiftyNineSecondsAgoTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime now = new DateTime(2014, 5, 8, 6, 8, 8);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(Yesterday)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringTomorrowTest()
    {

        DateTime now = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime date = new DateTime(2014, 5, 8, 6, 8, 8);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(Tomorrow)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringFourtyEightHoursAgoTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime now = new DateTime(2014, 5, 8, 6, 8, 9);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(2 days ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringFourtyEightHoursTillTest()
    {

        DateTime now = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime date = new DateTime(2014, 5, 8, 6, 8, 9);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(2 days till)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringThreeHundredFiftyFourDaysFiftyNineMinutesFiftyNineSecondsAgoTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime now = new DateTime(2015, 5, 6, 6, 8, 8);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(364 days ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneYearAgoTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime now = new DateTime(2015, 5, 6, 6, 8, 9);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 year ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneYearThreeHundredFiftyFourDaysFiftyNineMinuesFiftyNineSecondsAgoTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime now = new DateTime(2016, 5, 5, 6, 8, 8);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 year ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringTwoYearsAgoTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime now = new DateTime(2016, 5, 5, 6, 8, 9);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(2 years ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringOneYearTillTest()
    {

        DateTime now = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime date = new DateTime(2015, 5, 6, 6, 8, 9);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, false, true);

        assertEquals("(1 year till)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringWithoutParenthesisTest()
    {

        DateTime now = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime date = new DateTime(2015, 5, 6, 6, 8, 9);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, false, false, true);

        assertEquals("1 year till", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringWithDateTimeTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, DateTimeZone.UTC);
        DateTime now = new DateTime(2016, 5, 6, 6, 7, 9, DateTimeZone.UTC);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, true, false);

        assertEquals("2014-05-06 06:08:09Z (2 years ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringWithDateTimeNotUTCTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9);
        DateTime now = new DateTime(2016, 5, 6, 6, 7, 9);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, true, false);

        String offset = date.toString("ZZ");
        assertEquals("2014-05-06 06:08:09" + offset + " (2 years ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringWithDateOnlyTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, DateTimeZone.UTC);
        DateTime now = new DateTime(2016, 5, 6, 6, 7, 9, DateTimeZone.UTC);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, true, true);

        assertEquals("2014-05-06 (2 years ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringAllOffTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, DateTimeZone.UTC);
        DateTime now = new DateTime(2016, 5, 6, 6, 7, 9, DateTimeZone.UTC);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, false, false, false);

        assertEquals("2 years ago", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringTimeZoneTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, DateTimeZone.forOffsetHours(-4));
        DateTime now = new DateTime(2016, 5, 6, 6, 7, 9, DateTimeZone.UTC);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, true, false);

        assertEquals("2014-05-06 06:08:09-04:00 (2 years ago)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringFourHoursTillTimeZoneTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, DateTimeZone.forOffsetHours(-4));
        DateTime now = new DateTime(2014, 5, 6, 6, 8, 9, DateTimeZone.UTC);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, true, false);

        assertEquals("2014-05-06 06:08:09-04:00 (4 hours till)", elapsed);
    }

    @Test
    public void getElapsedGMTTimeStringThreeHoursFiftyNineSecondsTillTimeZoneTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, DateTimeZone.forOffsetHours(-4));
        DateTime now = new DateTime(2014, 5, 6, 6, 8, 10, DateTimeZone.UTC);

        String elapsed = JodaDateTimeHelper.getElapsedGMTTimeString(date, now, true, true, false);

        assertEquals("2014-05-06 06:08:09-04:00 (3 hours till)", elapsed);
    }

    @Test
    public void toXmlDateTimeUTCTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, DateTimeZone.forOffsetHours(-4));

        String utcTime = JodaDateTimeHelper.toXmlDateTimeUTC(date);

        assertEquals("2014-05-06T10:08:09.000Z", utcTime);

    }

    @Test
    public void toXmlDateTimeUTCNullDateTest()
    {
        _thrown.expect(NullArgumentException.class);
        _thrown.expectMessage("forDate");
        
        @SuppressWarnings("unused")
        String utcTime = JodaDateTimeHelper.toXmlDateTimeUTC(null);
    }

    @Test
    public void fromXmlDateTimeUTCTest()
    {

        DateTime date = new DateTime(2014, 5, 6, 6, 8, 9, DateTimeZone.forOffsetHours(-4));

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
}
