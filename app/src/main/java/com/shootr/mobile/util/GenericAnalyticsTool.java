package com.shootr.mobile.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.shootr.mobile.BuildConfig;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.model.user.User;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

public class GenericAnalyticsTool implements AnalyticsTool {

  private static final String APP_TRACKER = "app_tracker";
  private static final String MIX_PANEL_PRO = "017c3e7d9670fec221d97a4eeeca00bf";
  private static final String MIX_PANEL_TST = "e86d9d782e8d6d32b0c2263d5cf2758a";
  private static final String DISTINCT_ID = "distinct_id";
  private static final String ID_STREAM = "idStream";
  private static final String STREAM_TITLE = "streamTitle";
  private static final String ACTIVATED_USER = "activatedUser";
  private static final String USER_TYPE = "userType";
  private static final String SOURCE = "source";
  private static final String ID_TARGET_USER = "idTargetUser";
  private static final String TARGET_USERNAME = "targetUsername";
  private static final String NOTIFICATION_NAME = "notificationName";
  private static final String PUSH_REDIRECTION = "pushRedirection";
  private static final String SIGNUP_METHOD = "Signup method";
  private static final String EMAIL = "$email";
  private static final String FIRST_NAME = "$first_name";
  private static final String ACTIVATED = "Activated";
  private static final String TYPE = "Type";
  private final String ACTION = "action";
  private Tracker tracker;
  private MixpanelAPI mixpanel;
  private HashMap<String, Tracker> trackers = new HashMap();
  private User user;

  @Override public void init(Context context) {
    try {
      GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
      tracker =
          analytics.newTracker(context.getString(R.string.google_analytics_tracking_id));
      tracker.enableAutoActivityTracking(true);
      tracker.enableExceptionReporting(true);
    } catch (Exception error) {
    }
    mixpanel =
        MixpanelAPI.getInstance(context, (BuildConfig.DEBUG) ? MIX_PANEL_TST : MIX_PANEL_PRO);
  }

  private void storeUserMixPanel() {
    mixpanel.identify(user.getIdUser());
    mixpanel.getPeople().identify(user.getIdUser());
    mixpanel.getPeople().set(SIGNUP_METHOD, user.isSocialLogin() ? "Facebook" : "Email");
    mixpanel.getPeople().set(EMAIL, user.getEmail());
    mixpanel.getPeople().set(FIRST_NAME, user.getUsername());
    mixpanel.getPeople().set(ACTIVATED, user.getReceivedReactions() == 1L);
    mixpanel.getPeople().set(TYPE, user.getAnalyticsUserType());
  }

  @Override public void setUser(User user) {
      this.user = user;
    try {
      tracker.set("&uid", user.getIdUser());
      storeUserMixPanel();
    } catch (NullPointerException error) {
      /* no-op */
    }
  }

  @Override public void analyticsStart(Context context, String name) {
    tracker = getTracker(context, APP_TRACKER);
    tracker.setScreenName(name);
    tracker.send(new HitBuilders.AppViewBuilder().build());
  }

  @Override public void analyticsStop(Context context, Activity activity) {
    GoogleAnalytics.getInstance(context).reportActivityStop(activity);
  }

  @Override public void analyticsSendAction(Builder builder) {
    Context context = builder.getContext();
    String action = builder.getAction();
    String actionId = builder.getActionId();
    String labelId = builder.getLabelId();
    String source = builder.getSource();
    String idTargetUser = builder.getIdTargetUser();
    String targetUsername = builder.getTargetUsername();
    String notificationName = builder.getNotificationName();
    String pushRedirection = builder.getPushRedirection();
    String idStream = builder.getIdStream();
    String stream = builder.getStreamName();
    User user = builder.getUser();

    sendGoogleAnalytics(context, action, actionId, labelId);
    sendMixPanelAnalytics(user, actionId, source, idTargetUser, targetUsername,
        notificationName, pushRedirection, idStream, stream, context);
  }

  private void sendMixPanelAnalytics(User user, String actionId, String source,
      String idTargetUser, String targetUsername,
      String notificationName, String pushRedirection, String idStream, String streamName,
      Context context) {
    try {
      if (user != null) {
        JSONObject props = new JSONObject();
        props.put(DISTINCT_ID, user.getIdUser());
        if (idStream != null) {
          props.put(ID_STREAM, idStream);
        }
        if (streamName != null) {
          props.put(STREAM_TITLE, streamName);
        }
        props.put(ACTIVATED_USER, user.getReceivedReactions() == 1L);
        props.put(USER_TYPE, user.getAnalyticsUserType());
        if (source != null) {
          props.put(SOURCE, source);
        }
        if (idTargetUser != null) {
          props.put(ID_TARGET_USER, idTargetUser);
        }
        if (targetUsername != null) {
          props.put(TARGET_USERNAME, targetUsername);
        }
        if (notificationName != null) {
          props.put(NOTIFICATION_NAME, notificationName);
        }
        if (pushRedirection != null) {
          props.put(PUSH_REDIRECTION, pushRedirection);
        }

        try {
          mixpanel.track(actionId, props);
        } catch (Exception error) {
          mixpanel = MixpanelAPI.
              getInstance(context, (BuildConfig.DEBUG) ? MIX_PANEL_TST : MIX_PANEL_PRO);
          mixpanel.track(actionId, props);
        }

      }
    } catch (JSONException e) {
      Log.e("Shootr", "Unable to add properties to JSONObject", e);
    } catch (NullPointerException error) {
      Log.e("Shootr", "Unable to build mixPanel object", error);
    }
  }

  private void sendGoogleAnalytics(Context context, String action, String actionId,
      String labelId) {
    try {
      tracker = getTracker(context, APP_TRACKER);
      tracker.send(new HitBuilders.EventBuilder().setCategory(ACTION)
          .setAction(actionId + ((action != null) ? action : ""))
          .setLabel((labelId != null) ? labelId : "")
          .build());
    } catch (Exception e) {
      e.printStackTrace();
    }
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
