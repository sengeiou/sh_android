package com.shootr.android.domain.utils;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Years;

@Singleton
public class EventDateFormatter {

    private final EventDateTimeTextProvider timeTextFormatter;
    private final TimeUtils timeUtils;

    @Inject public EventDateFormatter(EventDateTimeTextProvider timeTextFormatter, TimeUtils timeUtils) {
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
        if(differsLessThan60Minutes(date, now)){
            if(differsOneMinuteOrLess(date, now)){
                return timeTextFormatter.getStartingNowFormat();
            }else{
                return timeTextFormatter.getMinutesAgoFormat(date, getMinutesBetween(date, now));
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
        } else if (differsLessThanOneYear(date, now)) {
            return timeTextFormatter.getDateAndTimeFormat(date);
        } else {
            return timeTextFormatter.getDateAndYearFormat(date);
        }
    }

    private String formatFutureDate(DateTime date, DateTime now) {
        if(differsLessThan60Minutes(date, now)){
            if(differsOneMinuteOrLess(date, now)){
                return timeTextFormatter.getStartingNowFormat();
            }else{
                return timeTextFormatter.getStartingInMinutesFormat(date);
            }
        } else if (differsLessThan12Hours(date, now)) {
            return timeTextFormatter.getStartingInHoursFormat(date);
        }else if (differs24HoursOrLess(date, now)) {
            if(isToday(date, now)){
                return timeTextFormatter.getTodayFormat(date);
            }else{
                return timeTextFormatter.getTomorrowFormat(date);
            }
        }else if (differsLessThan48Hours(date, now)){
            if(isTomorrow(date, now)){
                return timeTextFormatter.getTomorrowFormat(date);
            }else {
                return timeTextFormatter.getWeekDayAndTimeFormat(date);
            }
        }else if (differsLessThanOneYear(date, now)){
            return timeTextFormatter.getDateAndTimeFormat(date);
        }else{
            return timeTextFormatter.getDateAndYearFormat(date);
        }
    }

    //region Date Evaluators
    private boolean wasYesterday(DateTime date, DateTime now) {
        return getDaysBetweenNowAndDate(date, now) == 1;
    }

    private boolean differsOneWeekOrLess(DateTime date, DateTime now) {
        return getDaysBetweenNowAndDate(date, now) <= 7;
    }

    private boolean differsLessThan48Hours(DateTime date, DateTime now) {
        return getHoursBetween(date, now) < 48;
    }

    private boolean differs24HoursOrLess(DateTime date, DateTime now) {
        return getHoursBetween(date, now) <= 24;
    }

    private boolean differs12HoursOrLess(DateTime date, DateTime now) {
        return getHoursBetween(date, now) <= 12;
    }

    private boolean differsLessThan12Hours(DateTime date, DateTime now) {
        return getHoursBetween(date, now) < 12;
    }

    private boolean differsOneMinuteOrLess(DateTime date, DateTime now) {
        return getMinutesBetween(date, now) <= 1;
    }

    private int getMinutesBetween(DateTime date, DateTime now) {
        DateTime dateRounded = now.withSecondOfMinute(0).withMillisOfSecond(0);
        DateTime nowRounded = date.withSecondOfMinute(0).withMillisOfSecond(0);
        return Math.abs(Minutes.minutesBetween(dateRounded, nowRounded).getMinutes());
    }

    private boolean differsLessThan60Minutes(DateTime date, DateTime now) {
        return getMinutesBetween(date, now) < 60;
    }

    private boolean isTomorrow(DateTime date, DateTime now) {
        return getDaysBetweenNowAndDate(date, now) == 1;
    }

    private boolean differsLessThanOneYear(DateTime date, DateTime now) {
        return getYearsBetween(date, now) < 1;
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

    private int getHoursBetween(DateTime date, DateTime now) {
        return Math.abs(Hours.hoursBetween(now.withMinuteOfHour(0), date.withMinuteOfHour(0)).getHours());
    }

    //region Date Getters
    private int getDaysBetweenNowAndDate(DateTime targetDate, DateTime now) {
        return Math.abs(Days.daysBetween(targetDate.withTimeAtStartOfDay(), now.withTimeAtStartOfDay()).getDays());
    }

    private int getYearsBetween(DateTime date, DateTime now) {
        return Math.abs(Years.yearsBetween(now, date).getYears());
    }
    //endregion
}
