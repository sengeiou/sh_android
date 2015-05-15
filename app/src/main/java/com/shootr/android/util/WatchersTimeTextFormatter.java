package com.shootr.android.util;

import android.content.res.Resources;

import com.shootr.android.R;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.Date;

import javax.inject.Inject;

public class WatchersTimeTextFormatter {

    private final Resources resources;

    @Inject
    public WatchersTimeTextFormatter(Resources resources) {
        this.resources = resources;
    }

    public String getLongTimeAgoFormat() {
        String dateInText = resources.getString(R.string.entered);
        dateInText += resources.getString(R.string.long_time_ago);
        return dateInText;
    }

    public String getJustEnteredFormat() {
        String dateInText = resources.getString(R.string.entered);
        dateInText += resources.getString(R.string.just_now);
        return dateInText;
    }

    public String getMinutesAgoFormat(DateTime date) {
        String dateInText = resources.getString(R.string.entered);
        int minutesAgo = Math.abs(DateTime.now().getMinuteOfHour() - date.getMinuteOfHour());
        if(minutesAgo <= 1){
            dateInText += resources.getString(R.string.minute_ago);
        }else if (minutesAgo >= 2){
            dateInText += String.valueOf(minutesAgo);
            dateInText += resources.getString(R.string.minutes_ago);
        }
        return dateInText;
    }

    public String getHoursAgoFormat(DateTime date) {
        String dateInText = resources.getString(R.string.entered);
        DateTime dateTime = new DateTime(new Date().getTime());
        int hoursAgo = Math.abs(dateTime.getHourOfDay() - date.getHourOfDay());
        if(hoursAgo <= 1){
            dateInText += resources.getString(R.string.hour_ago);
        }else{
            dateInText += String.valueOf(hoursAgo);
            dateInText += resources.getString(R.string.hours_ago);
        }
        return dateInText;
    }

    public String getDaysAgoFormat(DateTime date) {
        String dateInText = resources.getString(R.string.entered);
        int daysAgo = calculateHowManyDaysAgo(date);
        if(daysAgo <= 1){
            dateInText += resources.getString(R.string.day_ago);
        }else{
            dateInText += String.valueOf(daysAgo) + resources.getString(R.string.days_ago);
        }
        return dateInText;
    }

    private int calculateHowManyDaysAgo(DateTime date) {
        DateTime dateTime = new DateTime(new Date().getTime());
        int daysBetween = Math.abs(Days.daysBetween(dateTime.withHourOfDay(0),
                date.withHourOfDay(0)).getDays());
        return daysBetween;
    }
}
