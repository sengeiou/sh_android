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

    public int calculateDaysOfDifferenceBetweenDates(DateTime referenceDate, DateTime targetDate) {
        int daysBetween = Math.abs(Days.daysBetween(referenceDate, targetDate).getDays());
        if(daysBetween == 0){
            if(Math.abs(referenceDate.getDayOfMonth() - targetDate.getDayOfMonth()) != 0){
                return Math.abs(referenceDate.getDayOfMonth() - targetDate.getDayOfMonth());
            }
        } else if(daysBetween == 1){
            if(Math.abs(referenceDate.getDayOfMonth() - targetDate.getDayOfMonth()) != 1){
                return Math.abs(referenceDate.getDayOfMonth() - targetDate.getDayOfMonth());
            }
        }
        return Math.abs(Days.daysBetween(referenceDate, targetDate).getDays());
    }


    public String getLongTimeAgoFormat() {
        return resources.getString(R.string.entered_long_time_ago);
    }

    public String getJustEnteredFormat() {
        return resources.getString(R.string.just_entered);
    }

    public String getMinutesAgoFormat(DateTime date) {
        String dateInText = resources.getString(R.string.entered);
        dateInText += String.valueOf(Math.abs(
                Minutes.minutesBetween(DateTime.now(), date).getMinutes()) + 1);
        dateInText += resources.getString(R.string.minutes_ago);
        return dateInText;
    }

    public String getHoursAgoFormat(DateTime date) {
        String dateInText = resources.getString(R.string.entered);
        int hoursAgo = Math.abs(Hours.hoursBetween(DateTime.now(), date).getHours() + 1);
        dateInText += String.valueOf(hoursAgo);
        if(hoursAgo == 1){
            dateInText += resources.getString(R.string.hour_ago);
        }else{
            dateInText += resources.getString(R.string.hours_ago);
        }
        return dateInText;
    }

    public String getDaysAgoFormat(DateTime date) {
        int daysAgo = calculateHowManyDaysAgo(date);
        if(daysAgo == 1){
            return String.valueOf(daysAgo) + resources.getString(R.string.day_ago);
        }else{
            return String.valueOf(daysAgo) + resources.getString(R.string.days_ago);
        }
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
}
