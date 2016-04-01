package com.shootr.mobile.util;

import android.app.Activity;
import android.content.Context;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.shootr.mobile.R;
import java.util.HashMap;

public class GoogleAnalyticsTool implements AnalyticsTool {

    private Tracker tracker;
    private HashMap<TrackerName, Tracker> mTrackers = new HashMap();
    private enum TrackerName {
        APP_TRACKER, GLOBAL_TRACKER, NAVIGATION_TRACKER,
    }

    @Override
    public void init(Context context) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        tracker = analytics.newTracker(context.getString(R.string.google_analytics_tracking_id));
        tracker.enableAutoActivityTracking(true);
        tracker.enableExceptionReporting(true);
    }

    @Override
    public void setUserId(String userId) {
        tracker.set("&uid", userId);
    }

    @Override public void analyticsStart(Context context, String name) {
        tracker = getTracker(context, TrackerName.APP_TRACKER);
        tracker.setScreenName(name);
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override public void analyticsStop(Context context, Activity activity) {
        GoogleAnalytics.getInstance(context).reportActivityStop(activity);
    }

    public synchronized Tracker getTracker(Context context, TrackerName appTracker) {
        if (!mTrackers.containsKey(appTracker)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            Tracker t = analytics.newTracker(R.xml.navigation_tracker);
            mTrackers.put(appTracker, t);
        }
        return mTrackers.get(appTracker);
    }
}
