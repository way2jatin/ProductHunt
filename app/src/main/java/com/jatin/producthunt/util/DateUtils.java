
package com.jatin.producthunt.util;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {

	public static String getMonth (int month) {
		return new DateFormatSymbols ().getMonths ()[month];
	}

	public static String getDateFormattedString (Calendar calendar) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd");
		return simpleDateFormat.format (calendar.getTime ());
	}
}
