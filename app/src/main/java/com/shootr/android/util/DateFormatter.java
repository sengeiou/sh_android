package com.shootr.android.util;

import javax.inject.Inject;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateFormatter {

    DateTimeFormatter absoluteDateFormater = DateTimeFormat.forPattern("EEE, MMM dd YYYY");

    @Inject public DateFormatter() {
    }

    public String getAbsoluteDate(long timestamp) {
        return absoluteDateFormater.print(timestamp);
    }
}
