package com.shootr.android.util;

import android.content.Context;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.shootr.android.R;
import timber.log.Timber;

public class GoogleAnalyticsTool implements AnalyticsTool {

    @Override
    public void init(Context context) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        Tracker tracker = analytics.newTracker(context.getString(R.string.google_analytics_tracking_id));
        tracker.enableAutoActivityTracking(true);
        tracker.enableExceptionReporting(true);
    }
}
