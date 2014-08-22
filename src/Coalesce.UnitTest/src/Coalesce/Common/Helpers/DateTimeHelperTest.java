package Coalesce.Common.Helpers;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class DateTimeHelperTest {

/*	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}*/

	@Test
	public void FromXmlDateTimeUtcNanoTest() {
		
		String dateXml = "2014-05-02T14:33:51.8595755Z";
		
		Date utcDate = DateTimeHelper.FromXmlDateTimeUTC(dateXml);
		
		int year = utcDate.getYear();
		assertEquals(2014-1900, year);
		assertEquals(Calendar.MAY, utcDate.getMonth());
		assertEquals(2, utcDate.getDate());
		// Assumes EDT local time
		assertEquals(10, utcDate.getHours());
		assertEquals(33, utcDate.getMinutes());
		assertEquals(51, utcDate.getSeconds());

	}
	
	@Test
	public void FromXmlDateTimeUtcMillisecondTest() {
		
		String dateXml = "2014-05-02T14:33:51.859Z";
		
		Date utcDate = DateTimeHelper.FromXmlDateTimeUTC(dateXml);
		
		int year = utcDate.getYear();
		assertEquals(2014-1900, year);
		assertEquals(Calendar.MAY, utcDate.getMonth());
		assertEquals(2, utcDate.getDate());
		// Assumes EDT local time
		assertEquals(10, utcDate.getHours());
		assertEquals(33, utcDate.getMinutes());
		assertEquals(51, utcDate.getSeconds());

	}
	
	@Test
	public void ToXmlDateTimeUtcNanoTest() {

		String dateXml = "2014-05-02T14:33:51.8595755Z";
		
		Date utcDate = DateTimeHelper.FromXmlDateTimeUTC(dateXml);

		String toXmlDate = DateTimeHelper.ToXmlDateTimeUTC(utcDate);
		
		assertEquals("2014-05-02T14:33:51.859Z", toXmlDate);
	}

	@Test
	public void ToXmlDateTimeUtcMillisecondTest() {

		String dateXml = "2014-05-02T14:33:51.859Z";
		
		Date utcDate = DateTimeHelper.FromXmlDateTimeUTC(dateXml);

		String toXmlDate = DateTimeHelper.ToXmlDateTimeUTC(utcDate);
		
		assertEquals("2014-05-02T14:33:51.859Z", toXmlDate);
	}

}
