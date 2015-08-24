package com.shootr.android.domain.utils;

import javax.inject.Inject;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;

public class StreamJoinDateFormatter {


    private final DateRangeTextProvider dateRangeTextProvider;
    private final TimeUtils timeUtils;

    @Inject public StreamJoinDateFormatter(DateRangeTextProvider dateRangeTextProvider, TimeUtils timeUtils) {
        this.dateRangeTextProvider = dateRangeTextProvider;
        this.timeUtils = timeUtils;
    }

    public String format(long eventTimestamp) {
        DateTime date = new DateTime(eventTimestamp);
        DateTime now = new DateTime(timeUtils.getCurrentTime());
        return formatRelativeJoinStreamDate(date, now);
    }

    private String formatRelativeJoinStreamDate(DateTime joinDate, DateTime now) {
        if (isPast(joinDate, now)) {
            return formatPastDate(joinDate, now);
        }else{
            return formatFutureDate();
        }
    }

    private String formatFutureDate() {
        return dateRangeTextProvider.formatJustNow();
    }

    private String formatPastDate(DateTime date, DateTime now) {
        if (differsLessThanOneMinute(date, now)) {
            return dateRangeTextProvider.formatJustNow();
        }else if (differsLessThan60Minutes(date, now)) {
            return dateRangeTextProvider.formatMinutesAgo(getMinutesBetween(date, now));
        }else if (differsLessThan24Hours(date, now)) {
            return dateRangeTextProvider.formatHoursAgo(getHoursBetween(date, now));
        }else if (differsOneWeekOrLess(date, now)) {
            return dateRangeTextProvider.formatDaysAgo(getDaysBetween(date, now));
        } else {
            return dateRangeTextProvider.formatLongTime();
        }
    }

    //region Helper functions
    private boolean isPast(DateTime date, DateTime now) {
        return date.isBefore(now);
    }

    private boolean differsOneWeekOrLess(DateTime date, DateTime now) {
        return getDaysBetween(date, now) <= 7;
    }

    private boolean differsLessThan24Hours(DateTime date, DateTime now) {
        return getHoursBetween(date, now) < 24;
    }

    private boolean differsLessThanOneMinute(DateTime date, DateTime now) {
        return getMinutesBetween(date, now) < 1;
    }

    private boolean differsLessThan60Minutes(DateTime date, DateTime now) {
        return getMinutesBetween(date, now) < 60;
    }

    private int getMinutesBetween(DateTime date, DateTime now) {
        DateTime dateRounded = now.withSecondOfMinute(0).withMillisOfSecond(0);
        DateTime nowRounded = date.withSecondOfMinute(0).withMillisOfSecond(0);
        return Math.abs(Minutes.minutesBetween(dateRounded, nowRounded).getMinutes());
    }


    private int getHoursBetween(DateTime date, DateTime now) {
        return Math.abs(Hours.hoursBetween(now.withMinuteOfHour(0), date.withMinuteOfHour(0)).getHours());
    }

    private int getDaysBetween(DateTime targetDate, DateTime now) {
        return Math.abs(Days.daysBetween(targetDate.withTimeAtStartOfDay(), now.withTimeAtStartOfDay()).getDays());
    }
    //endregion
}
