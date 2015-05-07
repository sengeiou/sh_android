package com.shootr.android.util;

import com.shootr.android.domain.utils.TimeUtils;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Years;

@Singleton
public class EventTimeFormatter {

    private final TimeTextFormatter timeTextFormatter;
    private final TimeUtils timeUtils;

    @Inject public EventTimeFormatter(TimeTextFormatter timeTextFormatter, TimeUtils timeUtils) {
        this.timeTextFormatter = timeTextFormatter;
        this.timeUtils = timeUtils;
    }

    public String formatEventDate(long eventTimestamp) {
        DateTime date = new DateTime(eventTimestamp);
        DateTime now = new DateTime(timeUtils.getCurrentTime());
        return formatRelativeEventDate(date, now);
    }

    private String formatRelativeEventDate(DateTime eventDate, DateTime now) {
        if (isPast(eventDate, now)) {
            return formatPastDate(eventDate, now);
        }else if (isFuture(eventDate, now)){
            return formatFutureDate(eventDate, now);
        }else{
            return formatCurrentDate();
        }
    }

    private String formatCurrentDate() {
        return timeTextFormatter.getStartingNowFormat();
    }

    private String formatPastDate(DateTime date, DateTime now) {
        if(differsLessThan1Hour(date, now)){
            if(differsOneMinuteOrLess(date, now)){
                return timeTextFormatter.getStartingNowFormat();
            }else{
                return timeTextFormatter.getMinutesAgoFormat(date);
            }
        } else if(differs12HoursOrLess(date, now)){
            return timeTextFormatter.getHoursAgoFormat(date);
        } else if(differs24HoursOrLess(date, now)){
            if(isToday(date, now)){
                return  timeTextFormatter.getHoursAgoFormat(date);
            }else{
                return timeTextFormatter.getYesterdayFormat(date);
            }
        } else if (differsOneWeekOrLess(date, now)) {
            if (wasYesterday(date, now)) {
                return timeTextFormatter.getYesterdayFormat(date);
            } else {
                return timeTextFormatter.getDaysAgoFormat(date);
            }
        } else if (differsOneYearOrLess(date, now)) {
            return timeTextFormatter.getThisYearFormat(date);
        } else {
            return timeTextFormatter.getAnotherYearFormat(date);
        }
    }

    private String formatFutureDate(DateTime date, DateTime now) {
        if(differsLessThan1Hour(date, now)){
            if(differsOneMinuteOrLess(date, now)){
                return timeTextFormatter.getStartingNowFormat();
            }else{
                return timeTextFormatter.getStartingInMinutesFormat(date);
            }
        } else if (differs12HoursOrLess(date, now)) {
            return timeTextFormatter.getStartingInHoursFormat(date);
        }else if (differs24HoursOrLess(date, now)) {
            if(isToday(date, now)){
                return timeTextFormatter.getTodayFormat(date);
            }else{
                return timeTextFormatter.getTomorrowFormat(date);
            }
        }else if (differs48HoursOrLess(date, now)){
            if(isTomorrow(date, now)){
                return timeTextFormatter.getTomorrowFormat(date);
            }else{
                return timeTextFormatter.getCurrentWeekFormat(date);
            }
        }else{
            return timeTextFormatter.getAnotherYearFormat(date);
        }
    }

    //region Date Evaluators
    private boolean wasYesterday(DateTime date, DateTime now) {
        return getDaysBetweenNowAndDate(date, now) == 1;
    }

    private boolean differsOneWeekOrLess(DateTime date, DateTime now) {
        return getDaysBetweenNowAndDate(date, now) <= 7;
    }

    private boolean differs48HoursOrLess(DateTime date, DateTime now) {
        return getHoursBetweenNowAndDate(date, now) <= 48;
    }

    private boolean differs24HoursOrLess(DateTime date, DateTime now) {
        return getHoursBetweenNowAndDate(date, now) <= 24;
    }

    private boolean differs12HoursOrLess(DateTime date, DateTime now) {
        return getHoursBetweenNowAndDate(date, now) <= 12;
    }

    private boolean differsOneMinuteOrLess(DateTime date, DateTime now) {
        return getMinutesBetweenNowAndDate(date, now) <= 1;
    }

    private int getMinutesBetweenNowAndDate(DateTime date, DateTime now) {
        return Math.abs(Minutes.minutesBetween(now, date).getMinutes());
    }

    private boolean differsLessThan1Hour(DateTime date, DateTime now) {
        return getHoursBetweenNowAndDate(date, now) < 1;
    }

    private boolean isTomorrow(DateTime date, DateTime now) {
        return getDaysBetweenNowAndDate(date, now) == 1;
    }

    private boolean differsOneYearOrLess(DateTime date, DateTime now) {
        return getYearsBetweenNowAndDate(date, now) <= 1;
    }

    private boolean isFuture(DateTime date, DateTime now) {
        return date.isAfter(now);
    }

    private boolean isPast(DateTime date, DateTime now) {
        return date.isBefore(now);
    }

    private boolean isToday(DateTime targetDate, DateTime now) {
        return getDaysBetweenNowAndDate(targetDate, now) == 0;
    }
    //endregion

    //region Date Getters
    private int getDaysBetweenNowAndDate(DateTime targetDate, DateTime now) {
        return timeTextFormatter.calculateDaysOfDifferenceBetweenDates(now, targetDate);
    }

    private int getHoursBetweenNowAndDate(DateTime date, DateTime now) {
        return Math.abs(Hours.hoursBetween(now, date).getHours());
    }

    private int getYearsBetweenNowAndDate(DateTime date, DateTime now) {
        return Math.abs(Years.yearsBetween(now, date).getYears());
    }
    //endregion
}
