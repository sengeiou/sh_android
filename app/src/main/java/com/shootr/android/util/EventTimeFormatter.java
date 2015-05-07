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
        if(isPastInLessThanAnHour(date, now)){
            if(isOneMinuteRemaining(date, now)){
                return timeTextFormatter.getStartingNowFormat();
            }else{
                return timeTextFormatter.getMinutesAgoFormat(date);
            }
        } else if(isPastBetween1And12Hours(date, now)){
            return  timeTextFormatter.getHoursAgoFormat(date);
        } else if(isPastBetween12And24Hours(date, now)){
            if(isToday(date, now)){
                return  timeTextFormatter.getHoursAgoFormat(date);
            }else{
                return timeTextFormatter.getYesterdayFormat(date);
            }
        } else if (isPastMoreThan24Hour(date, now)) {
            if (!isPastMoreThanAWeek(date, now)) {
                if (!wasYesterday(date, now)) {
                    return timeTextFormatter.getDaysAgoFormat(date);
                } else {
                    return timeTextFormatter.getYesterdayFormat(date);
                }
            } else if (isPastMoreThanAYear(date, now)) {
                return timeTextFormatter.getAnotherYearFormat(date);
            } else {
                return timeTextFormatter.getThisYearFormat(date);
            }
        } else {
            return null; // TODO can't/shouldn't happen, refactor to remove this case
        }
    }

    private String formatFutureDate(DateTime date, DateTime now) {
        if(isFutureInLessThanAnHour(date, now)){
            if(isOneMinuteRemaining(date, now)){
                return timeTextFormatter.getStartingNowFormat();
            }else{
                return timeTextFormatter.getStartingInMinutesFormat(date);
            }
        } else if (isFutureBetween1And12Hours(date, now)) {
            return timeTextFormatter.getStartingInHoursFormat(date);
        }else if (isFutureBetween12And24Hours(date, now)) {
            if(isToday(date, now)){
                return timeTextFormatter.getTodayFormat(date);
            }else{
                return timeTextFormatter.getTomorrowFormat(date);
            }
        }else if (isFutureBetween24And48Hours(date, now)){
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

    private boolean isPastMoreThanAWeek(DateTime date, DateTime now) {
        return getDaysBetweenNowAndDate(date, now) > 7;
    }

    private boolean isFutureBetween24And48Hours(DateTime date, DateTime now) {
        return getHoursBetweenNowAndDate(date, now) >= 24 &&
                getHoursBetweenNowAndDate(date, now) <= 48;
    }

    private boolean isFutureBetween12And24Hours(DateTime date, DateTime now) {
        return getHoursBetweenNowAndDate(date, now) > 12 &&
                getHoursBetweenNowAndDate(date, now) <= 24;
    }

    private boolean isFutureBetween1And12Hours(DateTime date, DateTime now) {
        return getHoursBetweenNowAndDate(date, now) <= 12 && getHoursBetweenNowAndDate(date, now) >= 1;
    }

    private boolean isPastMoreThan24Hour(DateTime date, DateTime now) {
        return getHoursBetweenNowAndDate(date, now) >= 24;
    }

    private boolean isPastBetween12And24Hours(DateTime date, DateTime now) {
        return getHoursBetweenNowAndDate(date, now) > 12 &&
                getHoursBetweenNowAndDate(date, now) <= 24;
    }

    private boolean isPastBetween1And12Hours(DateTime date, DateTime now) {
        return getHoursBetweenNowAndDate(date, now) <= 12 && getHoursBetweenNowAndDate(date, now) >= 1;
    }

    private boolean isOneMinuteRemaining(DateTime date, DateTime now) {
        return getMinutesBetweenNowAndDate(date, now) <= 1;
    }

    private int getMinutesBetweenNowAndDate(DateTime date, DateTime now) {
        return Math.abs(Minutes.minutesBetween(now, date).getMinutes());
    }

    private boolean isPastInLessThanAnHour(DateTime date, DateTime now) {
        return getHoursBetweenNowAndDate(date, now) < 1;
    }

    private boolean isFutureInLessThanAnHour(DateTime date, DateTime now) {
        return getHoursBetweenNowAndDate(date, now) < 1;
    }

    private boolean isTomorrow(DateTime date, DateTime now) {
        return getDaysBetweenNowAndDate(date, now) == 1;
    }

    private boolean isPastMoreThanAYear(DateTime date, DateTime now) {
        return getYearsBetweenNowAndDate(date, now) >= 1;
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
