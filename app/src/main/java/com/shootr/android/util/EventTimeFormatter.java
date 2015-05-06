package com.shootr.android.util;

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

    @Inject public EventTimeFormatter(TimeTextFormatter timeTextFormatter) {
        this.timeTextFormatter = timeTextFormatter;
    }

    public String eventResultDateText(long timestamp) {
        DateTime date = new DateTime(timestamp);
        String dateInTextFormat = null;
        if (isPast(date)) {
            if(isPastInLessThanAnHour(date)){
                if(isOneMinuteRemaining(date)){
                    dateInTextFormat = timeTextFormatter.getStartingNowFormat(date);
                }else{
                    dateInTextFormat = timeTextFormatter.getMinutesAgoFormat(date);
                }
            } else if(isPastBetween1And12Hours(date)){
                dateInTextFormat =  timeTextFormatter.getHoursAgoFormat(date);
            } else if(isPastBetween12And24Hours(date)){
                if(isToday(date)){
                    dateInTextFormat =  timeTextFormatter.getHoursAgoFormat(date);
                }else{
                    // Was yesterday
                    dateInTextFormat = timeTextFormatter.getYesterdayFormat(date);
                }
            } else if (isPastMoreThan24Hour(date)){
                if(isPastMoreThanAYear(date)){
                    dateInTextFormat = timeTextFormatter.getAnotherYearFormat(date);
                }else{
                    dateInTextFormat = timeTextFormatter.getYesterdayFormat(date);
                }
            }
        }else if (isFuture(date)){
            if(isFutureInLessThanAnHour(date)){
                if(isOneMinuteRemaining(date)){
                    dateInTextFormat = timeTextFormatter.getStartingNowFormat(date);
                }else{
                    dateInTextFormat = timeTextFormatter.getStartingInMinutesFormat(date);
                }
            } else if (isFutureBetween1And12Hours(date)) {
                dateInTextFormat = timeTextFormatter.getStartingInHoursFormat(date);
            }else if (isFutureBetween12And24Hours(date)) {
                if(isToday(date)){
                    dateInTextFormat = timeTextFormatter.getTodayFormat(date);
                }else{
                    // Tomorrow
                    dateInTextFormat = timeTextFormatter.getTomorrowFormat(date);
                }
            }else if (isFutureBetween24And48Hours(date)){
                if(isTomorrow(date)){
                    dateInTextFormat = timeTextFormatter.getTomorrowFormat(date);
                }else{
                    // This week day
                    dateInTextFormat = timeTextFormatter.getCurrentWeekFormat(date);
                }
            }else{
                //TODO: Refactor this method's name
                //Year Format
                dateInTextFormat = timeTextFormatter.getAnotherYearFormat(date);
            }
        }
        return dateInTextFormat;
    }

    //region Date Evaluators
    public boolean isFutureBetween24And48Hours(DateTime date) {
        return getHoursBetweenNowAndDate(date) >= 24 &&
                getHoursBetweenNowAndDate(date) <= 48;
    }

    public boolean isFutureBetween12And24Hours(DateTime date) {
        return getHoursBetweenNowAndDate(date) > 12 &&
                getHoursBetweenNowAndDate(date) <= 24;
    }

    public boolean isFutureBetween1And12Hours(DateTime date) {
        return getHoursBetweenNowAndDate(date) <= 12 && getHoursBetweenNowAndDate(date) >= 1;
    }

    public boolean isPastMoreThan24Hour(DateTime date) {
        return getHoursBetweenNowAndDate(date) >= 24;
    }

    public boolean isPastBetween12And24Hours(DateTime date) {
        return getHoursBetweenNowAndDate(date) > 12 &&
                getHoursBetweenNowAndDate(date) <= 24;
    }

    public boolean isPastBetween1And12Hours(DateTime date) {
        return getHoursBetweenNowAndDate(date) <= 12 && getHoursBetweenNowAndDate(date) >= 1;
    }

    public boolean isOneMinuteRemaining(DateTime date) {
        return getMinutesBetweenNowAndDate(date) <= 1;
    }

    private int getMinutesBetweenNowAndDate(DateTime date) {
        return Math.abs(Minutes.minutesBetween(DateTime.now(), date).getMinutes());
    }

    public boolean isPastInLessThanAnHour(DateTime date) {
        return getHoursBetweenNowAndDate(date) < 1;
    }

    public boolean isFutureInLessThanAnHour(DateTime date) {
        return getHoursBetweenNowAndDate(date) < 1;
    }

    public boolean isTomorrow(DateTime date) {
        return getDaysBetweenNowAndDate(date) == 1;
    }

    public boolean isInAnHourRange(DateTime date) {
        return getHoursBetweenNowAndDate(date) <= 1;
    }

    public boolean isPastMoreThanAYear(DateTime date) {
        return getYearsBetweenNowAndDate(date) >= 1;
    }

    public boolean isFuture(DateTime date) {
        return date.isAfterNow();
    }

    public boolean isPast(DateTime date) {
        return date.isBeforeNow();
    }

    public boolean isToday(DateTime targetDate) {
        return getDaysBetweenNowAndDate(targetDate) == 0;
    }
    //endregion
    private int getDaysBetweenNowAndDate(DateTime targetDate) {
        return Math.abs(Days.daysBetween(DateTime.now(), targetDate).getDays()) + 1;
    }

    private int getHoursBetweenNowAndDate(DateTime date) {
        return Math.abs(Hours.hoursBetween(DateTime.now(), date).getHours());
    }

    private int getYearsBetweenNowAndDate(DateTime date) {
        return Math.abs(Years.yearsBetween(DateTime.now(), date).getYears());
    }
}
