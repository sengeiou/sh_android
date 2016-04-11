package com.shootr.mobile.util;

import javax.inject.Inject;
import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

public class TimeFormatter {

    DateTimeFormatter detailedFormater = DateTimeFormatter.ofPattern("EEEE dd MMMM HH:mm");

    @Inject public TimeFormatter() {

    }

    public String getDateAndTimeDetailed(long timestamp) {
        ZonedDateTime targetDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        return getDetailedFormat(targetDate);
    }

    private String getDetailedFormat(ZonedDateTime targetDate) {
        return detailedFormater.format(targetDate);
    }
}
