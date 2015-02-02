package com.shootr.android.util;

import javax.inject.Inject;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeFormatter {

    DateTimeFormatter hourFormater = DateTimeFormat.forPattern("HH:mm");
    DateTimeFormatter dayFormater = DateTimeFormat.forPattern("MM/dd");
    DateTimeFormatter dayTimeFormater = DateTimeFormat.forPattern("MM/dd HH:mm");
    DateTimeFormatter weekDayFormater = DateTimeFormat.forPattern("EEE HH:mm");
    DateTimeFormatter detailedFormater = DateTimeFormat.forPattern("EEEE dd MMMM HH:mm");

    DateTimeFormatter absoluteTimeFormater = DateTimeFormat.forPattern("HH:mm");

    @Inject public TimeFormatter() {

    }

    public String getDateAndTimeTextRelative(long targetTimestamp) {
        DateTime targetDate = new DateTime(targetTimestamp);
        if (isToday(targetDate)) {
            return getTodayFormat(targetDate);
        }else if (isLessThanAWeek(targetDate)) {
            return getDayOfWeek(targetDate).toUpperCase();
        } else {
            return getDayFormat(targetDate);
        }
    }

    public String getDateAndTimeTextGeneric(long targetTimestamp) {
        DateTime targetDate = new DateTime(targetTimestamp);
        return getDayTimeFormat(targetDate);
    }

    public String getDateAndTimeDetailed(long timestamp) {
        DateTime targetDate = new DateTime(timestamp);
        return getDetailedFormat(targetDate);
    }

    private String getDayOfWeek(DateTime targetDate) {
        return weekDayFormater.print(targetDate);
    }

    private String getDayFormat(DateTime targetDate) {
        return dayFormater.print(targetDate);
    }

    private String getDayTimeFormat(DateTime targetDate) {
        return dayTimeFormater.print(targetDate);
    }
    private String getTomorrowFormat(DateTime targetDate) {
        return "Tomorrow";
    }

    private boolean isToday(DateTime targetDate) {
        return getDaysUntil(targetDate) == 0;
    }

    private boolean isTomorrow(DateTime targetDate) {
        return getDaysUntil(targetDate) == 1;
    }

    private boolean isLessThanAWeek(DateTime targetDate) {
        return getDaysUntil(targetDate) < 7;
    }

    private String getTodayFormat(DateTime dateTime) {
        return hourFormater.print(dateTime);
    }

    private String getDetailedFormat(DateTime targetDate) {
        return detailedFormater.print(targetDate);
    }


    private int getDaysUntil(DateTime targetDate) {
        return Days.daysBetween(DateTime.now(), targetDate).getDays();
    }

    public String getAbsoluteTime(long targetTimestamp) {
        return absoluteTimeFormater.print(targetTimestamp);
    }
}
