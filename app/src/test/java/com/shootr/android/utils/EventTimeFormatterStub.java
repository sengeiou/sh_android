package com.shootr.android.utils;

import com.shootr.android.util.EventTimeFormatter;

public class EventTimeFormatterStub extends EventTimeFormatter {

    public EventTimeFormatterStub() {
        super(null, null);
    }

    @Override public String eventResultDateText(long timestamp) {
        return "test_date";
    }
}
