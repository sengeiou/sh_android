package com.shootr.android.util;

import com.shootr.android.domain.utils.TimeUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@Singleton
public class EventTimeFormatter {

    private final TimeTextFormatter timeTextFormatter;
    private final TimeUtils timeUtils;
    private DateTime now;

    @Inject public EventTimeFormatter(TimeTextFormatter timeTextFormatter, TimeUtils timeUtils) {
        this.timeTextFormatter = timeTextFormatter;
        this.timeUtils = timeUtils;
    }

    public String eventResultDateText(long timestamp) {
        DateTime date = new DateTime(timestamp);
        now = new DateTime(timeUtils.getCurrentTime());
        String dateInTextFormat = null;
        if (isPast(date)) {
            dateInTextFormat = formatPastDate(date);
        }else if (isFuture(date)){
            dateInTextFormat = formatFutureDate(date);
        }else{
            dateInTextFormat = timeTextFormatter.getStartingNowFormat();
        }
        return dateInTextFormat;
    }

    private String formatPastDate(DateTime date) {
        String dateInTextFormat = null;
        if(isPastInLessThanAnHour(date)){
            if(isOneMinuteRemaining(date)){
                dateInTextFormat = timeTextFormatter.getStartingNowFormat();
            }else{
                dateInTextFormat = timeTextFormatter.getMinutesAgoFormat(date);
            }
        } else if(isPastBetween1And12Hours(date)){
            dateInTextFormat =  timeTextFormatter.getHoursAgoFormat(date);
        } else if(isPastBetween12And24Hours(date)){
            if(isToday(date)){
                dateInTextFormat =  timeTextFormatter.getHoursAgoFormat(date);
            }else{
                dateInTextFormat = timeTextFormatter.getYesterdayFormat(date);
            }
        } else if (isPastMoreThan24Hour(date)){
            if (!isPastMoreThanAWeek(date)){
                if(!wasYesterday(date)){
                    dateInTextFormat = timeTextFormatter.getDaysAgoFormat(date);
                }else{
                    dateInTextFormat = timeTextFormatter.getYesterdayFormat(date);
                }
            }else if(isPastMoreThanAYear(date)){
                dateInTextFormat = timeTextFormatter.getAnotherYearFormat(date);
            }else{
                dateInTextFormat = timeTextFormatter.getThisYearFormat(date);
            }
        }
        return dateInTextFormat;
    }

    private String formatFutureDate(DateTime date) {
        String dateInTextFormat;
        if(isFutureInLessThanAnHour(date)){
            if(isOneMinuteRemaining(date)){
                dateInTextFormat = timeTextFormatter.getStartingNowFormat();
            }else{
                dateInTextFormat = timeTextFormatter.getStartingInMinutesFormat(date);
            }
        } else if (isFutureBetween1And12Hours(date)) {
            dateInTextFormat = timeTextFormatter.getStartingInHoursFormat(date);
        }else if (isFutureBetween12And24Hours(date)) {
            if(isToday(date)){
                dateInTextFormat = timeTextFormatter.getTodayFormat(date);
            }else{
                dateInTextFormat = timeTextFormatter.getTomorrowFormat(date);
            }
        }else if (isFutureBetween24And48Hours(date)){
            if(isTomorrow(date)){
                dateInTextFormat = timeTextFormatter.getTomorrowFormat(date);
            }else{
                dateInTextFormat = timeTextFormatter.getCurrentWeekFormat(date);
            }
        }else{
            dateInTextFormat = timeTextFormatter.getAnotherYearFormat(date);
        }
        return dateInTextFormat;
    }

    //region Date Evaluators
    private boolean wasYesterday(DateTime date) {
        return getDaysBetweenNowAndDate(date) == 1;
    }

    private boolean isPastMoreThanAWeek(DateTime date) {
        return getDaysBetweenNowAndDate(date) > 7;
    }

    private boolean isFutureBetween24And48Hours(DateTime date) {
        return getHoursBetweenNowAndDate(date) >= 24 &&
                getHoursBetweenNowAndDate(date) <= 48;
    }

    private boolean isFutureBetween12And24Hours(DateTime date) {
        return getHoursBetweenNowAndDate(date) > 12 &&
                getHoursBetweenNowAndDate(date) <= 24;
    }

    private boolean isFutureBetween1And12Hours(DateTime date) {
        return getHoursBetweenNowAndDate(date) <= 12 && getHoursBetweenNowAndDate(date) >= 1;
    }

    private boolean isPastMoreThan24Hour(DateTime date) {
        return getHoursBetweenNowAndDate(date) >= 24;
    }

    private boolean isPastBetween12And24Hours(DateTime date) {
        return getHoursBetweenNowAndDate(date) > 12 &&
                getHoursBetweenNowAndDate(date) <= 24;
    }

    private boolean isPastBetween1And12Hours(DateTime date) {
        return getHoursBetweenNowAndDate(date) <= 12 && getHoursBetweenNowAndDate(date) >= 1;
    }

    private boolean isOneMinuteRemaining(DateTime date) {
        return getMinutesBetweenNowAndDate(date) <= 1;
    }

    private int getMinutesBetweenNowAndDate(DateTime date) {
        return Math.abs(Minutes.minutesBetween(now, date).getMinutes());
    }

    private boolean isPastInLessThanAnHour(DateTime date) {
        return getHoursBetweenNowAndDate(date) < 1;
    }

    private boolean isFutureInLessThanAnHour(DateTime date) {
        return getHoursBetweenNowAndDate(date) < 1;
    }

    private boolean isTomorrow(DateTime date) {
        return getDaysBetweenNowAndDate(date) == 1;
    }

    private boolean isPastMoreThanAYear(DateTime date) {
        return getYearsBetweenNowAndDate(date) >= 1;
    }

    private boolean isFuture(DateTime date) {
        return date.isAfter(now);
    }

    private boolean isPast(DateTime date) {
        return date.isBefore(now);
    }

    private boolean isToday(DateTime targetDate) {
        return getDaysBetweenNowAndDate(targetDate) == 0;
    }
    //endregion

    //region Date Getters
    private int getDaysBetweenNowAndDate(DateTime targetDate) {
        return timeTextFormatter.calculateDaysOfDifferenceBetweenDates(now, targetDate);
    }

    private int getHoursBetweenNowAndDate(DateTime date) {
        return Math.abs(Hours.hoursBetween(now, date).getHours());
    }

    private int getYearsBetweenNowAndDate(DateTime date) {
        return Math.abs(Years.yearsBetween(now, date).getYears());
    }
    //endregion
}
