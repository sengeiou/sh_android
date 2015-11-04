package com.shootr.mobile.domain.utils;

import javax.inject.Inject;
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
        Instant date = Instant.ofEpochMilli(eventTimestamp);
        Instant now = Instant.ofEpochMilli(timeUtils.getCurrentTime());
        return formatRelativeJoinStreamDate(date, now);
    }

    private String formatRelativeJoinStreamDate(Instant joinDate, Instant now) {
        if (isPast(joinDate, now)) {
            return formatPastDate(joinDate, now);
        }else{
            return formatFutureDate();
        }
    }

    private String formatFutureDate() {
        return dateRangeTextProvider.formatJustNow();
    }

    private String formatPastDate(Instant date, Instant now) {
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
    private boolean isPast(Instant date, Instant now) {
        return date.isBefore(now);
    }

    private boolean differsOneWeekOrLess(Instant date, Instant now) {
        return getDaysBetween(date, now) <= 7;
    }

    private boolean differsLessThan24Hours(Instant date, Instant now) {
        return getHoursBetween(date, now) < 24;
    }

    private boolean differsLessThanOneMinute(Instant date, Instant now) {
        return getMinutesBetween(date, now) < 1;
    }

    private boolean differsLessThan60Minutes(Instant date, Instant now) {
        return getMinutesBetween(date, now) < 60;
    }

    private int getMinutesBetween(Instant date, Instant now) {
        Duration between = Duration.between(truncateMinutes(date), truncateMinutes(now)).abs();
        return (int) between.toMinutes();
    }

    private int getHoursBetween(Instant date, Instant now) {
        Duration between = Duration.between(truncateHours(date), truncateHours(now)).abs();
        return (int) between.toHours();
    }

    private int getDaysBetween(Instant date, Instant now) {
        Duration between = Duration.between(truncateDays(date), truncateDays(now)).abs();
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
