package com.shootr.mobile.util;

import android.app.Activity;
import android.content.Context;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.shootr.mobile.R;
import java.util.HashMap;

public class GoogleAnalyticsTool implements AnalyticsTool {

    private static final String APP_TRACKER = "app_tracker";
    private final String ACTION = "action";

    private Tracker tracker;
    private HashMap<String, Tracker> trackers = new HashMap();

    @Override public void init(Context context) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        tracker = analytics.newTracker(context.getString(R.string.google_analytics_tracking_id));
        tracker.enableAutoActivityTracking(true);
        tracker.enableExceptionReporting(true);
    }

    @Override public void setUserId(String userId) {
        tracker.set("&uid", userId);
    }

    @Override public void analyticsStart(Context context, String name) {
        tracker = getTracker(context, APP_TRACKER);
        tracker.setScreenName(name);
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override public void analyticsStop(Context context, Activity activity) {
        GoogleAnalytics.getInstance(context).reportActivityStop(activity);
    }

    @Override public void analyticsSendAction(Context context, String actionId, String labelId) {
        tracker = getTracker(context, APP_TRACKER);
        tracker.send(new HitBuilders.EventBuilder().setCategory(ACTION)
            .setAction(actionId)
            .setLabel(labelId)
            .build());
    }

    private synchronized Tracker getTracker(Context context, String appTracker) {
        if (!trackers.containsKey(appTracker)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            Tracker t = analytics.newTracker(R.xml.navigation_tracker);
            trackers.put(appTracker, t);
        }
        return trackers.get(appTracker);
    }
}
