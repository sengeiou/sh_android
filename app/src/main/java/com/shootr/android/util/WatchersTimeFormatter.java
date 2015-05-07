package com.shootr.android.util;


import com.shootr.android.domain.utils.TimeUtils;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Seconds;
import org.joda.time.Years;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WatchersTimeFormatter {

    private DateTime referenceDate;
    private final TimeUtils timeUtils;
    private final WatchersTimeTextFormatter watchersTimeTextFormatter;

    @Inject
    public WatchersTimeFormatter(TimeUtils timeUtils, WatchersTimeTextFormatter watchersTimeTextFormatter){
        this.timeUtils = timeUtils;
        this.watchersTimeTextFormatter = watchersTimeTextFormatter;
    }

    public String jointDateText(long timestamp) {
        DateTime date = new DateTime(timestamp);
        referenceDate = new DateTime(timeUtils.getCurrentTime());
        String dateInTextFormat = null;
        if (isPast(date)) {
            if(isMoreThanSevenDays(date)){
                dateInTextFormat = watchersTimeTextFormatter.getLongTimeAgoFormat();
            }else{
                if(isLessThanADay(date)){
                    if(isLessThanAnHour(date)){
                        if(isLessThanAMinute(date)){
                            dateInTextFormat = watchersTimeTextFormatter.getJustEnteredFormat();
                        }else{
                            dateInTextFormat = watchersTimeTextFormatter.getMinutesAgoFormat(date);
                        }
                    }else{
                        dateInTextFormat = watchersTimeTextFormatter.getHoursAgoFormat(date);
                    }
                }else{
                    dateInTextFormat = watchersTimeTextFormatter.getDaysAgoFormat(date);
                }
            }
        }else{
            dateInTextFormat = watchersTimeTextFormatter.getJustEnteredFormat();
        }
        return dateInTextFormat;
    }

    private boolean isLessThanAnHour(DateTime date) {
        return getHoursBetweenNowAndDate(date) < 1;
    }

    private boolean isLessThanADay(DateTime date) {
        return getDaysBetweenNowAndDate(date) < 1;
    }

    private boolean isMoreThanSevenDays(DateTime date) {
        return getDaysBetweenNowAndDate(date) > 7 || getMonthsBetweenNowAndDate(date) >= 1
                || getYearsBetweenNowAndDate(date) >= 1;
    }

    private int getYearsBetweenNowAndDate(DateTime date) {
        return Math.abs(Years.yearsBetween(referenceDate, date).getYears());
    }

    private int getMonthsBetweenNowAndDate(DateTime date) {
        return Math.abs(Months.monthsBetween(referenceDate, date).getMonths());
    }

    public boolean isLessThanAMinute(DateTime date) {
        return getSecondsBetweenNowAndDate(date) < 60;
    }

    private int getSecondsBetweenNowAndDate(DateTime date) {
        return Math.abs(Seconds.secondsBetween(referenceDate,date).getSeconds());
    }

    private boolean isPast(DateTime date) {
        return date.isBefore(referenceDate);
    }

    //region Date Getters
    private int getDaysBetweenNowAndDate(DateTime targetDate) {
        return watchersTimeTextFormatter.calculateDaysOfDifferenceBetweenDates(referenceDate, targetDate);
    }

    private int getHoursBetweenNowAndDate(DateTime date) {
        return Math.abs(Hours.hoursBetween(referenceDate, date).getHours());
    }

    private int getMinutesBetweenNowAndDate(DateTime date) {
        return Math.abs(Minutes.minutesBetween(referenceDate, date).getMinutes());
    }
    //endregion
}
