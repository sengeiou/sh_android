package com.shootr.android.util;

import android.content.res.Resources;
import com.shootr.android.R;
import com.shootr.android.domain.utils.EventDateTimeTextProvider;
import java.util.Date;
import javax.inject.Inject;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ResourcesEventDateTimeTextProvider implements EventDateTimeTextProvider {

    DateTimeFormatter todayFormater = DateTimeFormat.forPattern("HH:mm");
    DateTimeFormatter tomorrowFormater = DateTimeFormat.forPattern("HH:mm");
    DateTimeFormatter yesterdayFormater = DateTimeFormat.forPattern("HH:mm");
    DateTimeFormatter currentWeekFormater = DateTimeFormat.forPattern("EEE, HH:mm");
    DateTimeFormatter currentYearFormater = DateTimeFormat.forPattern("EEE dd MMM, HH:mm");
    DateTimeFormatter anotherYearFormater = DateTimeFormat.forPattern("EEE dd MMM yyyy, HH:mm");

    private final Resources resources;

    @Inject public ResourcesEventDateTimeTextProvider(Resources resources){
        this.resources = resources;
    }

    private boolean isInAnHourRange(DateTime date) {
        return getHoursBetweenNowAndDate(date) <= 1;
    }

    @Override public String getStartingInMinutesFormat(DateTime date) {
        String dateInText = resources.getString(R.string.starting_in);
        dateInText += String.valueOf(Minutes.minutesBetween(DateTime.now(), date).getMinutes());
        dateInText += resources.getString(R.string.space_and_minutes);
        return dateInText;
    }

    @Override public String getMinutesAgoFormat(DateTime date, int numberOfMinutes) {
        String dateInText = resources.getString(R.string.started);
        dateInText += String.valueOf(Math.abs(Minutes.minutesBetween(DateTime.now(), date).getMinutes()));
        dateInText += resources.getString(R.string.min_ago);
        return dateInText;
    }

    @Override public String getStartingNowFormat() {
        return resources.getString(R.string.starting_now);
    }

    private int getHoursBetweenNowAndDate(DateTime date) {
        return Math.abs(Hours.hoursBetween(DateTime.now(), date).getHours());
    }

    @Override public String getStartingInHoursFormat(DateTime date) {
        String dateInText = resources.getString(R.string.starting_in);
        if(isInAnHourRange(date)){
            dateInText += resources.getString(R.string.an_hour);
        }else{
            dateInText += String.valueOf(Hours.hoursBetween(DateTime.now(),date).getHours());
            dateInText += resources.getString(R.string.space_and_hours);
        }
        return dateInText;
    }

    @Override public String getYesterdayFormat(DateTime date) {
        return resources.getString(R.string.yesterday_at) + yesterdayFormater.print(date);
    }

    @Override public String getDaysAgoFormat(DateTime date) {
        int daysAgo = calculateHowManyDaysAgo(date);
        return String.valueOf(daysAgo) + resources.getString(R.string.days_ago);
    }

    private int calculateHowManyDaysAgo(DateTime date) {
        DateTime dateTime = new DateTime(new Date().getTime());
        int daysBetween = Math.abs(Days.daysBetween(dateTime, date).getDays());
        if(daysBetween == 0){
            if(Math.abs(dateTime.getDayOfMonth() - date.getDayOfMonth()) != 0){
                daysBetween = Math.abs(dateTime.getDayOfMonth() - date.getDayOfMonth());
            }
        } else if(daysBetween == 1){
            if(Math.abs(dateTime.getDayOfMonth() - date.getDayOfMonth()) != 1){
                daysBetween = Math.abs(dateTime.getDayOfMonth() - date.getDayOfMonth());
            }
        }
        return daysBetween;
    }

    @Override public String getHoursAgoFormat(DateTime date) {
        String dateInText = resources.getString(R.string.started);
        if(isInAnHourRange(date)){
            dateInText += resources.getString(R.string.an_hour_ago);
        }else{
            dateInText += String.valueOf(Math.abs(Hours.hoursBetween(DateTime.now(), date).getHours()));
            dateInText += resources.getString(R.string.hours_ago);
        }
        return dateInText;
    }

    @Override public String getTodayFormat(DateTime date) {
        return resources.getString(R.string.today_at) + todayFormater.print(date);
    }

    @Override public String getTomorrowFormat(DateTime date) {
        return resources.getString(R.string.tomorrow_at) + tomorrowFormater.print(date);
    }

    @Override public String getWeekDayAndTimeFormat(DateTime date) {
        return currentWeekFormater.print(date);
    }

    @Override public String getDateAndYearFormat(DateTime date) {
        return anotherYearFormater.print(date);
    }

    @Override public String getDateAndTimeFormat(DateTime date) {
        return currentYearFormater.print(date);
    }
}
