package Coalesce.Common.Helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;

public class DateTimeHelper {

	private static String MODULE = "Coalesce.Common.Helpers.DateTimeHelper";

	// Make static class
	private DateTimeHelper() {
	}

	// -----------------------------------------------------------------------'
	// public Shared Methods
	// -----------------------------------------------------------------------'
	// Convert Biometric strings in format "YYYYMMDD" to a DateTime
	public static CallResult ConvertYYYYMMDDDateStringToDateTime(String strDate, java.util.Date MyDate)
	{

		try {
			// Calendar cal = new Calendar();
			// cal.s
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			MyDate = dateFormat.parse(strDate);
			// cal = dateFormat.parse(strDate);

			// MyDate = DateTime.ParseExact(strDate,
			// "yyyyMMdd",
			// System.Globalization.CultureInfo.InvariantCulture)
			return CallResult.successCallResult;
		} catch (Exception ex) {
			return CallResult.failedCallResult;
		}

	}

	// Direct Version
	public static String MilitaryFormat(Date MyDate, boolean DateOnly)
	{
		try {
			DateFormat writeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (MyDate != null) {
				String formattedDate = writeFormat.format(MyDate);
				return formattedDate;
			} else
				return "";
			// If (MyDate.Ticks > 0) Then
			// // Cast to format "yyyy-MM-dd HH:mm:ssZ"
			// If DateOnly Then
			// Return MyDate.ToString("yyyy-MM-dd")
			// Else
			// Return MyDate.ToString("u")
			// End If
			// Else
			// // Empty Date
			// Return ""
			// End If

		} catch (Exception ex) {
			// Log
			CallResult.log(CallResults.FAILED_ERROR, ex, DateTimeHelper.MODULE);

			// Return Empty String
			return "";
		}
	}

	// CallResult Version
	public static CallResult MilitaryFormat(Date MyDate, boolean DateOnly, String FormattedDate)
	{
		try {
			DateFormat writeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			FormattedDate = writeFormat.format(MyDate);

			// if (MyDate.Ticks > 0){
			// // Cast to format "yyyy-mm-dd HH:mm:ssZ"
			// if DateOnly
			// FormattedDate = MyDate.ToString("yyyy-MM-dd");
			// else
			// FormattedDate = MyDate.ToString("u");
			//
			// }else{
			// // Empty Date
			// FormattedDate = "";
			// }

			// Return Success
			return CallResult.successCallResult;

		} catch (Exception ex) {
			// Retrun Failed Error
			return new CallResult(CallResults.FAILED_ERROR, ex, DateTimeHelper.MODULE);
		}
	}

	public static String GetElapsedGMTTimeString(Date ForDate,
	                                             boolean IncludeParenthesis,
	                                             boolean IncludeDateTime,
	                                             boolean DateOnly)
	{
		try {
			String Value = "";

			if (ForDate == null) {
				// do nothing
			} else {

				// Call on Overload
				GetElapsedGMTTimeString(ForDate, Value, IncludeParenthesis, IncludeDateTime, DateOnly);
			}

			// Return
			return Value;

		} catch (Exception ex) {
			// Log
			CallResult.log(CallResults.FAILED_ERROR, ex, DateTimeHelper.MODULE);

			// Return Empty String
			return "";
		}
	}

	public static String GetElapsedGMTTimeString(Date ForDate, boolean IncludeParenthesis, boolean IncludeTime)
	{
		try {
			String Value = "";

			if (ForDate == null) {
				// do nothing
			} else {

				// Call on Overload
				GetElapsedGMTTimeString(ForDate, Value, IncludeParenthesis, IncludeTime, IncludeTime);

			}

			// Return
			return Value;

		} catch (Exception ex) {
			// Log
			// CallResult rst = new CallResult();
			CallResult.log(CallResults.FAILED_ERROR, ex, DateTimeHelper.MODULE);

			// Return Empty String
			return "";
		}
	}

	// CallResult Version
	public static CallResult GetElapsedGMTTimeString(Date ForDate,
	                                                 String ElapsedString,
	                                                 boolean IncludeParenthesis,
	                                                 boolean IncludeDateTime,
	                                                 boolean DateOnly)
	{
		try {
			boolean IsFutureDate = false;

			// Is ForDate Null?
			if (ForDate == null) {
				// Yes; Return Empty Elapsed Time String
				ElapsedString = "";
				return CallResult.successCallResult;
			} else {
				Date DtNow = new Date();
				long TotalSeconds = DtNow.getTimezoneOffset() - ForDate.getTimezoneOffset();

				// ForDate = ForDate.ToUniversalTime;
				// // No; Determine Elapsed Time String
				// long TotalSeconds =
				// Date.UtcNow.Subtract(ForDate).TotalSeconds;

				if (TotalSeconds < 0) {
					TotalSeconds = TotalSeconds * -1;
					IsFutureDate = true;
				}

				if (TotalSeconds < 60) {
					// 0 <= ForDate < 1 minute
					if (TotalSeconds == 1)
						ElapsedString = "1 second";
					else
						ElapsedString = TotalSeconds + " seconds";

					if (IsFutureDate)
						ElapsedString = ElapsedString + " till";
					else
						ElapsedString = ElapsedString + " ago";

				} else if (TotalSeconds < 3600) {
					// 1 minute <= ForDate < 1 Hour
					long TotalMinutes = (TotalSeconds / 60);
					// long RemainderSeconds = (TotalSeconds % 60);

					if (TotalMinutes == 1)
						ElapsedString = "1 minute";
					else
						ElapsedString = TotalMinutes + " minutes";

					if (IsFutureDate)
						ElapsedString = ElapsedString + " till";
					else
						ElapsedString = ElapsedString + " ago";

				} else if (TotalSeconds < 86400) {
					// 1 Hour <= For Date < 24 Hours
					long TotalHours = (TotalSeconds / 3600);
					// long RemainderMinutes = ((TotalSeconds % 3600) % 60);
					// long RemainderSeconds = (((TotalSeconds % 3600) % 60) %
					// 60);

					if (TotalHours == 1)
						ElapsedString = "1 hour";
					else
						ElapsedString = TotalHours + " hours";

					if (IsFutureDate)
						ElapsedString = ElapsedString + " till";
					else
						ElapsedString = ElapsedString + " ago";

				} else if (TotalSeconds < 172800) {
					// Yesterday

					if (IsFutureDate)
						ElapsedString = "Tomorrow";
					else
						ElapsedString = "Yesterday";

					// ElapsedString = ElapsedString & " at " &
					// ForDate.TimeOfDay.Hours.ToString("00") & ":" &
					// ForDate.TimeOfDay.Minutes.ToString("00") & ":" &
					// ForDate.TimeOfDay.Seconds.ToString("00") & " Z"
				} else if (TotalSeconds < 31536000) {

					long TotalDays = TotalSeconds / 86400;

					if (IsFutureDate)
						ElapsedString = TotalDays + " days till";
					else
						ElapsedString = TotalDays + " days ago";

				} else {
					long TotalYears = TotalSeconds / 31536000;

					if (IsFutureDate)
						ElapsedString = TotalYears + " years till";
					else
						ElapsedString = TotalYears + " years ago";

					// > 2 Days ago
					// If Not (IncludeTime) Then ElapsedString =
					// ForDate.ToString("M") & " at " &
					// ForDate.TimeOfDay.Hours.ToString("00") & ":" &
					// ForDate.TimeOfDay.Minutes.ToString("00") & ":" &
					// ForDate.TimeOfDay.Seconds.ToString("00") & " Z"
				}

				// Trim
				ElapsedString = ElapsedString.trim();

				// Parenthesis?

				if (IncludeParenthesis && (!(ElapsedString == null || ElapsedString.trim() != "")))
				    ElapsedString = "(" + ElapsedString + ")";
				if (IncludeDateTime) ElapsedString = MilitaryFormat(ForDate, DateOnly) + " " + ElapsedString;

				// Return Success
				return CallResult.successCallResult;
			}

		} catch (Exception ex) {
			// Return Failed Error
			return new CallResult(CallResults.FAILED_ERROR, ex, DateTimeHelper.MODULE);
		}
	}

	public static String ToXmlDateTimeUTC(Date forDate)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
         
		String toXmlDate = sdf.format(forDate);

		return toXmlDate;
	
	}

	public static Date toDate(XMLGregorianCalendar calendar) {
		if (calendar == null) {
			return null;
		}
		
		return calendar.toGregorianCalendar().getTime();
	}
	
	public static Date FromXmlDateTimeUTC(String xmlDate)
	{
		try {
			
			String trimmedDate = xmlDate.replace("T", " ");
			if (xmlDate.length() > 23) {
				trimmedDate = trimmedDate.substring(0, 23);
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	         
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	         
			Date forDate = sdf.parse(trimmedDate);

			return forDate;

		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED, ex, DateTimeHelper.MODULE);

			return null;
		}
	}

	public static XMLGregorianCalendar toXmlGregorianCalendar(Date date) {
		
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		XMLGregorianCalendar xmlCalendar = null;
		
		try {
			xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
		} catch (DatatypeConfigurationException ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, DateTimeHelper.MODULE);
		}
		
		return xmlCalendar;
		
	}
	
	public static CallResult ConvertDateToGMT(String DateStr, Date DateVal)
	{
		try {
			SimpleDateFormat formatUTC = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ");
			formatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
			DateVal = formatUTC.parse(DateStr);

			// not sure if this is needed, technically the value hasn't been
			// formatted yet.
			DateVal = formatUTC.parse(formatUTC.format(DateVal));

			// Return Success
			return CallResult.successCallResult;

		} catch (Exception ex) {
			// Return Failed Error
			return new CallResult(CallResults.FAILED_ERROR, ex, DateTimeHelper.MODULE);
		}
	}

	public static CallResult ConvertDateToGMT(Date DateVal)
	{
		try {

			SimpleDateFormat formatUTC = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ");
			formatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
			DateVal = formatUTC.parse(formatUTC.format(DateVal));

			// Return Success
			return CallResult.successCallResult;

		} catch (Exception ex) {
			// Return Failed Error
			return new CallResult(CallResults.FAILED_ERROR, ex, DateTimeHelper.MODULE);
		}
	}

	public static String ConvertDateToString(Date DateVal, String format)
	{
		DateFormat df = new SimpleDateFormat(format);
		String reportDate = df.format(DateVal);
		return reportDate;
	}

	public static long getDateTicks(Date date)
	{
		try {
			// vb.net tick = 100 nanoseconds -
			// http://visualbasic.about.com/od/usingvbnet/a/ticktimer01.htm
			// for a datetime, the ticks count is how many ticks have passed
			// since 12:00:00 midnight on January 1, 0001
			// 1 second = 1,000,000,000 nanoseconds -
			// https://www.google.com/search?q=how+many+nanoseconds+are+in+a+second&ie=utf-8&oe=utf-8&aq=t&rls=org.mozilla:en-US:official&client=firefox-a&channel=nts

			// current year is incomplete, don't add an entire year for it
			long year = date.getYear();
			int month = date.getMonth();
			int day = date.getDate();
			int hour = date.getHours();
			int minute = date.getMinutes();
			int second = date.getSeconds();

			// how many leap years?
			long leapyears = year / 4;
			float leapfloat = year % 4;
			long nonLeapYears = year - leapyears;

			// determine if this year is a leap year or not and reduce correct
			// classification by 1
			if (leapfloat == 0.0)
				leapyears = leapyears - 1;
			else
				nonLeapYears = nonLeapYears - 1;

			// readd current year
			year += 1;

			long regYearSeconds = 31536000;
			long leapYearSeconds = 31622400;
			long daySeconds = 86400;
			long hourSeconds = 3600;
			long minSeconds = 60;

			// start ticks at the number of seconds for complete years
			long ticks = (regYearSeconds * nonLeapYears) + (leapYearSeconds * leapyears);

			// calculate the number of seconds expired for completed months
			long addMoTicks = 0;
			for (int i = 1; i < month; i++) {
				switch (month) {
				case 1:
					addMoTicks += (31 * daySeconds);
					break;
				case 2:
					if (leapfloat == 0.0)
						addMoTicks += (29 * daySeconds);
					else
						addMoTicks += (28 * daySeconds);
					break;
				case 3:
					addMoTicks += (31 * daySeconds);
					break;
				case 4:
					addMoTicks += (30 * daySeconds);
					break;
				case 5:
					addMoTicks += (31 * daySeconds);
					break;
				case 6:
					addMoTicks += (30 * daySeconds);
					break;
				case 7:
					addMoTicks += (31 * daySeconds);
					break;
				case 8:
					addMoTicks += (31 * daySeconds);
					break;
				case 9:
					addMoTicks += (30 * daySeconds);
					break;
				case 10:
					addMoTicks += (31 * daySeconds);
					break;
				case 11:
					addMoTicks += (30 * daySeconds);
					break;
				case 12:
					addMoTicks += (31 * daySeconds);
					break;
				}
			}

			// add seconds expired for completed months to ticks
			ticks += addMoTicks;

			// add seconds for days, hours, minutes and seconds.
			ticks = ticks + ((day - 1) * daySeconds) + ((hour - 1) * hourSeconds) + ((minute - 1) * minSeconds)
			+ second;

			// multiply by 1 billion to get the nanosecond count
			ticks = ticks * 1000000000;

			return ticks;

		} catch (Exception ex) {
			return 0;
		}

	}

}
