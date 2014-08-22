package Coalesce.Common.Helpers;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class DateTimeConverter {

	public static String printDate(DateTime value) {
		
		DateTimeFormatter formatter = ISODateTimeFormat.dateTime().withZoneUTC();
		
		return formatter.print(value);
		
	}
	
	public static DateTime parseDate(String value) {
		
		DateTimeFormatter formatter = ISODateTimeFormat.dateTime().withZoneUTC();
		
		return formatter.parseDateTime(value);
		
	}
}
