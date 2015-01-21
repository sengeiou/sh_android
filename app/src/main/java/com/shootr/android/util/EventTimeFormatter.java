package com.shootr.android.util;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@Singleton
public class EventTimeFormatter {

    DateTimeFormatter todayFormater = DateTimeFormat.forPattern(", HH:mm");
    DateTimeFormatter tomorrowFormater = DateTimeFormat.forPattern(", HH:mm");
    DateTimeFormatter currentWeekFormater = DateTimeFormat.forPattern("EEE, HH:mm");
    DateTimeFormatter currentYearFormater = DateTimeFormat.forPattern("EEE dd MMM, HH:mm");
    DateTimeFormatter anotherYearFormater = DateTimeFormat.forPattern("EEE dd MMM yyyy, HH:mm");

    @Inject public EventTimeFormatter() {
    }

    public String eventResultDateText(long timestamp) {
        DateTime date = new DateTime(timestamp);
        if (isPast(date)) {
            return getAnotherYearFormat(date);
        }else if (isToday(date)) {
            return getTodayFormat(date);
        } else if (isTomorrow(date)) {
            return getTomorrowFormat(date);
        } else if (isLessThanAWeek(date)) {
            return getCurrentWeekFormat(date);
        } else if (isCurrentYear(date)) {
            return getCurrentYearFormat(date);
        } else {
            return getAnotherYearFormat(date);
        }
    }

    private String getTodayFormat(DateTime date) {
        return "Today" + todayFormater.print(date);
    }

    private String getTomorrowFormat(DateTime date) {
        return "Tomorrow" + tomorrowFormater.print(date);
    }

    private String getCurrentWeekFormat(DateTime date) {
        return currentWeekFormater.print(date);
    }

    private String getCurrentYearFormat(DateTime date) {
        return currentYearFormater.print(date);
    }

    private String getAnotherYearFormat(DateTime date) {
        return anotherYearFormater.print(date);
    }

    private boolean isPast(DateTime date) {
        return date.isBeforeNow();
    }

    private boolean isToday(DateTime targetDate) {
        return getDaysUntil(targetDate) == 0;
    }

    private boolean isTomorrow(DateTime date) {
        return getDaysUntil(date) == 1;
    }

    private boolean isLessThanAWeek(DateTime date) {
        return getDaysUntil(date) < 7;
    }

    private boolean isCurrentYear(DateTime date) {
        return Years.yearsBetween(DateTime.now(), date).getYears() > 1;
    }

    private int getDaysUntil(DateTime targetDate) {
        return Days.daysBetween(DateTime.now(), targetDate).getDays();
    }
}
