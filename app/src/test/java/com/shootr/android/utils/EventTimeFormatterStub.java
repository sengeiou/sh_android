package com.shootr.android.utils;

import com.shootr.android.util.EventTimeFormatter;

public class EventTimeFormatterStub extends EventTimeFormatter {

    public EventTimeFormatterStub() {
        super(null, null);
    }

    @Override public String formatEventDate(long eventTimestamp) {
        return "test_date";
    }
}
