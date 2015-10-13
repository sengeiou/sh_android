package com.shootr.android.domain.utils;

import javax.inject.Inject;
import org.joda.time.DateTime;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.temporal.ChronoUnit;
import org.threeten.bp.temporal.Temporal;

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
        Instant dateInstant = Instant.ofEpochMilli(date.getMillis());
        Instant nowInstant = Instant.ofEpochMilli(now.getMillis());

        Duration between = Duration.between(truncateMinutes(dateInstant), truncateMinutes(nowInstant)).abs();
        return (int) between.toMinutes();
    }

    private int getHoursBetween(DateTime date, DateTime now) {
        Instant dateInstant = Instant.ofEpochMilli(date.getMillis());
        Instant nowInstant = Instant.ofEpochMilli(now.getMillis());

        Duration between = Duration.between(truncateHours(dateInstant), truncateHours(nowInstant)).abs();
        return (int) between.toHours();
    }

    private int getDaysBetween(DateTime date, DateTime now) {
        Instant dateInstant = Instant.ofEpochMilli(date.getMillis());
        Instant nowInstant = Instant.ofEpochMilli(now.getMillis());

        Duration between = Duration.between(truncateDays(dateInstant), truncateDays(nowInstant)).abs();
        return (int) between.toDays();
    }

    private Temporal truncateMinutes(Instant dateInstant) {
        ZonedDateTime zonedDateTime = dateInstant.atZone(ZoneId.systemDefault());
        return zonedDateTime.truncatedTo(ChronoUnit.MINUTES);
    }

    private Temporal truncateHours(Instant dateInstant) {
        ZonedDateTime zonedDateTime = dateInstant.atZone(ZoneId.systemDefault());
        return zonedDateTime.truncatedTo(ChronoUnit.HOURS);
    }

    private Temporal truncateDays(Instant dateInstant) {
        ZonedDateTime zonedDateTime = dateInstant.atZone(ZoneId.systemDefault());
        return zonedDateTime.truncatedTo(ChronoUnit.DAYS);
    }
    //endregion
}
