package com.shootr.android.ui.model;

public class RelativeEndDate implements EndDate {

    private String title;
    private int menuItemId;
    private long timeGap;

    public RelativeEndDate(String title, int menuItemId, long timeGap) {
        this.title = title;
        this.menuItemId = menuItemId;
        this.timeGap = timeGap;
    }

    @Override public String getTitle() {
        return title;
    }

    @Override public int getMenuItemId() {
        return menuItemId;
    }

    @Override public long getDateTime(long startDateTime) {
        return startDateTime + timeGap;
    }
}
