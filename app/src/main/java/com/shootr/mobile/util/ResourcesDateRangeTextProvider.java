package com.shootr.mobile.util;

import android.content.res.Resources;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.utils.DateRangeTextProvider;
import javax.inject.Inject;

public class ResourcesDateRangeTextProvider implements DateRangeTextProvider {

    private final Resources resources;

    @Inject public ResourcesDateRangeTextProvider(Resources resources) {
        this.resources = resources;
    }

    @Override
    public String formatJustNow() {
        return resources.getString(R.string.entered_just_now);
    }

    @Override
    public String formatMinutesAgo(int minutes) {
        if (minutes > 1) {
            return resources.getString(R.string.entered_minutes_ago, minutes);
        } else {
            return resources.getString(R.string.entered_a_minute_ago);
        }
    }

    @Override
    public String formatHoursAgo(int hours) {
        if (hours > 1) {
            return resources.getString(R.string.entered_hours_ago, hours);
        } else {
            return resources.getString(R.string.entered_an_hour_ago);
        }
    }

    @Override
    public String formatDaysAgo(int days) {
        if (days > 1) {
            return resources.getString(R.string.entered_days_ago, days);
        } else {
            return resources.getString(R.string.entered_a_day_ago);
        }
    }

    @Override
    public String formatLongTime() {
        return resources.getString(R.string.entered_long_time_ago);
    }
}
