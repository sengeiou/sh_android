package com.shootr.android.utils;

import com.shootr.android.domain.utils.EventDateFormatter;

public class EventDateFormatterStub extends EventDateFormatter {

    public EventDateFormatterStub() {
        super(null, null);
    }

    @Override public String formatEventDate(long eventTimestamp) {
        return "test_date";
    }
}
