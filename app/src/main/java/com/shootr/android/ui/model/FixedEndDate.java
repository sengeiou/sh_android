package com.shootr.android.ui.model;

import com.shootr.android.util.DateFormatter;
import com.shootr.android.util.TimeFormatter;

public class FixedEndDate implements EndDate {

    private final TimeFormatter timeFormatter;
    private final DateFormatter dateFormatter;

    private final long fixedTimestamp;
    private final int menuItemId;

    public FixedEndDate(long fixedTimestamp, int menuItemId, TimeFormatter timeFormatter, DateFormatter dateFormatter) {
        this.fixedTimestamp = fixedTimestamp;
        this.menuItemId = menuItemId;
        this.timeFormatter = timeFormatter;
        this.dateFormatter = dateFormatter;
    }

    @Override public String getTitle() {
        return dateFormatter.getAbsoluteDate(fixedTimestamp) + ", " + timeFormatter.getAbsoluteTime(fixedTimestamp);
    }

    @Override public int getMenuItemId() {
        return menuItemId;
    }

    @Override public long getDateTime(long startDateTime) {
        return fixedTimestamp;
    }
}
