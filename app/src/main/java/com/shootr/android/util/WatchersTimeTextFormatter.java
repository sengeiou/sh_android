package com.shootr.android.util;

import android.content.res.Resources;

import com.shootr.android.R;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;

import java.util.Date;

import javax.inject.Inject;

public class WatchersTimeTextFormatter {

    private final Resources resources;

    @Inject
    public WatchersTimeTextFormatter(Resources resources) {
        this.resources = resources;
    }

    public String getLongTimeAgoFormat() {
        return resources.getString(R.string.entered_long_time_ago);
    }

    public String getJustEnteredFormat() {
        return resources.getString(R.string.just_entered);
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
        int daysAgo = calculateHowManyDaysAgo(date);
        if(daysAgo <= 1){
            return resources.getString(R.string.day_ago);
        }else{
            return String.valueOf(daysAgo) + resources.getString(R.string.days_ago);
        }
    }

    private int calculateHowManyDaysAgo(DateTime date) {
        DateTime dateTime = new DateTime(new Date().getTime());
        int daysBetween = Math.abs(Days.daysBetween(dateTime.withHourOfDay(0),
                date.withHourOfDay(0)).getDays());
        return daysBetween;
    }
}
