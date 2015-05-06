package com.shootr.android.util;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.inject.Inject;

public class TimeTextFormatter {

    DateTimeFormatter todayFormater = DateTimeFormat.forPattern("HH:mm");
    DateTimeFormatter tomorrowFormater = DateTimeFormat.forPattern("HH:mm");
    DateTimeFormatter currentWeekFormater = DateTimeFormat.forPattern("EEE, HH:mm");
    DateTimeFormatter currentYearFormater = DateTimeFormat.forPattern("EEE dd MMM, HH:mm");
    DateTimeFormatter anotherYearFormater = DateTimeFormat.forPattern("EEE dd MMM yyyy, HH:mm");

    @Inject public TimeTextFormatter(){

    }

    private boolean isInAnHourRange(DateTime date) {
        return getHoursBetweenNowAndDate(date) <= 1;
    }

    public String getStartingInMinutesFormat(DateTime date) {
        String dateInText = "Starting in ";
        dateInText += String.valueOf(Hours.hoursBetween(DateTime.now(), date).getHours());
        dateInText += " minutes";
        return dateInText;
    }

    public String getMinutesAgoFormat(DateTime date) {
        String dateInText = "Started ";
        dateInText += String.valueOf(Math.abs(Hours.hoursBetween(DateTime.now(), date).getHours()));
        dateInText += " minutes ago";
        return dateInText;
    }

    public String getStartingNowFormat(DateTime date) {
        return "Starting Now";
    }

    private int getHoursBetweenNowAndDate(DateTime date) {
        return Math.abs(Hours.hoursBetween(DateTime.now(), date).getHours());
    }

    public String getStartingInHoursFormat(DateTime date) {
        String dateInText = "Starting in ";
        if(isInAnHourRange(date)){
            dateInText += "an hour";
        }else{
            dateInText += String.valueOf(Hours.hoursBetween(DateTime.now(),date).getHours());
            dateInText += " hours";
        }
        return dateInText;
    }

    public String getYesterdayFormat(DateTime date) {
        return "Yesterday at "+String.valueOf(date.getHourOfDay());
    }

    public String getHoursAgoFormat(DateTime date) {
        String dateInText = "Started ";
        if(isInAnHourRange(date)){
            dateInText += "an hour ago";
        }else{
            dateInText += String.valueOf(Math.abs(Hours.hoursBetween(DateTime.now(), date).getHours()));
            dateInText += " hours ago";
        }
        return dateInText;
    }

    public String getTodayFormat(DateTime date) {
        return "Today at " + todayFormater.print(date);
    }

    public String getTomorrowFormat(DateTime date) {
        return "Tomorrow at " + tomorrowFormater.print(date);
    }

    public String getCurrentWeekFormat(DateTime date) {
        return currentWeekFormater.print(date);
    }

    public String getAnotherYearFormat(DateTime date) {
        return anotherYearFormater.print(date);
    }

}
