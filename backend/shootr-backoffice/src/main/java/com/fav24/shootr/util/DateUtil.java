package com.fav24.shootr.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
	public static Date format(String date, String pattern, String timeZone){
		if(date == null || date.isEmpty()){ return null; }
		Date ret = null; 
		DateFormat df = new SimpleDateFormat(pattern);
		if(timeZone != null){
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
		}
		try {
			ret = df.parse(date);
		} catch (ParseException e) {
		}
		return ret;
	}
}
