package com.yowoo.newbuyhouse.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.http.impl.cookie.DateParseException;

public final class CommonUtils {

//	public static Date parseW3CDate(String dateValue) throws DateParseException {
//		return DateUtils.parseDate(dateValue, new String[] {
//				"yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd'T'HH:mm:ssZ",
//				"yyyy-MM-dd'T'HH:mmZ", "yyyy-MM-dd", "yyyy-MM", "yyyy" });
//	}
	
	public static Date parseCommonDate(String dateValue) throws DateParseException, ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.parse(dateValue);
	}
	
	public static String timestampToDateString(int timestamp){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(timestamp));
	}

	public static String formatW3CDate(Date date, boolean timezone) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"
				+ (timezone ? "Z" : ""));
		if (timezone) {
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(date);
			sdf.setTimeZone(cal.getTimeZone());
		}
		String s = sdf.format(date);
		if (timezone) {
			int l = s.length();
			s = s.substring(0, l - 2) + ':' + s.substring(l - 2);
		}
		return s;
	}

}