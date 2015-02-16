package com.shootr.android.util;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
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

    public String getGMT(TimeZone timeZone, long date) {
        SimpleDateFormat gmtFormatter = new SimpleDateFormat("ZZZZ");
        gmtFormatter.setTimeZone(timeZone);
        return gmtFormatter.format(date);
    }
}
