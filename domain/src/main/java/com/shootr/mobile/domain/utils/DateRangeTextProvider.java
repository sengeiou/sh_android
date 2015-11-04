package com.shootr.mobile.domain.utils;

public interface DateRangeTextProvider {

    String formatJustNow();

    String formatMinutesAgo(int minutes);

    String formatHoursAgo(int hours);

    String formatDaysAgo(int days);

    String formatLongTime();
}
