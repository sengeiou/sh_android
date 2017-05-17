package com.shootr.mobile.util;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.appsflyer.AppsFlyerLib;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mixpanel.android.mpmetrics.OnMixpanelTweaksUpdatedListener;
import com.mixpanel.android.mpmetrics.OnMixpanelUpdatesReceivedListener;
import com.shootr.mobile.BuildConfig;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.model.user.User;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class GenericAnalyticsTool implements AnalyticsTool {

  private static final String APP_TRACKER = "app_tracker";
  private static final String MIX_PANEL_PRO = "017c3e7d9670fec221d97a4eeeca00bf";
  private static final String MIX_PANEL_TST = "e86d9d782e8d6d32b0c2263d5cf2758a";
  private static final String APPSFLYER = "FEg6kuUpkGTE3FrD2BdHPg";
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
  private static final String ID_POLL = "idPoll";
  private static final String ID_SHOT = "idShot";
  private static final String LOGIN_TYPE = "loginType";
  private static final String FAVORITES = "Favorites";
  private static final String FOLLOWING = "Following";
  private static final String FOLLOWERS = "Followers";
  private static final String SIGNUP_METHOD = "Signup method";
  private static final String EMAIL = "$email";
  private static final String FIRST_NAME = "$first_name";
  private static final String ACTIVATED = "Activated";
  private static final String TYPE = "Type";
  private static final String LASTDATE = "LastDate";
  private static final String PLATFORM_TYPE = "shootrPlatform";
  private static final String ANDROID_PLATFORM = "shootrAndroid";
  private final String ACTION = "action";
  private Tracker tracker;
  private MixpanelAPI mixpanel;
  private HashMap<String, Tracker> trackers = new HashMap();
  private User user;
  private AppsFlyerLib appsFlyerLib;

  @Override public void init(Application application) {
    try {
      GoogleAnalytics analytics = GoogleAnalytics.getInstance(application);
      tracker = analytics.newTracker(application.getString(R.string.google_analytics_tracking_id));
      tracker.enableAutoActivityTracking(false);
      tracker.enableExceptionReporting(true);
    } catch (Exception error) {
    }
    mixpanel =
        MixpanelAPI.getInstance(application, (BuildConfig.DEBUG) ? MIX_PANEL_TST : MIX_PANEL_PRO);

    appsFlyerLib = AppsFlyerLib.getInstance();
    appsFlyerLib.setCollectIMEI(false);
    appsFlyerLib.setCollectAndroidID(false);
    appsFlyerLib.startTracking(application, APPSFLYER);
  }

  private void storeUserMixPanel() {
    mixpanel.getPeople().set(SIGNUP_METHOD, user.isSocialLogin() ? "Facebook" : "Email");
    mixpanel.getPeople().set(EMAIL, user.getEmail());
    mixpanel.getPeople().set(FIRST_NAME, user.getUsername());
    mixpanel.getPeople().set(ACTIVATED, user.getReceivedReactions() == 1L);
    mixpanel.getPeople().set(TYPE, user.getAnalyticsUserType());
    mixpanel.getPeople().set(DISTINCT_ID, user.getIdUser());
    mixpanel.getPeople().set(FOLLOWERS, user.getNumFollowers());
    mixpanel.getPeople().set(FOLLOWING, user.getNumFollowings());
    mixpanel.getPeople().set(FAVORITES, user.getFavoritedStreamsCount());
    mixpanel.getPeople().set(PLATFORM_TYPE, ANDROID_PLATFORM);
    mixpanel.getPeople().set(LASTDATE, new Date());
  }

  @Override public void sendOpenAppMixPanelAnalytics(String actionId, String loginType, Context context) {
    try {
      JSONObject props = new JSONObject();
      mixpanel = MixpanelAPI.
          getInstance(context, (BuildConfig.DEBUG) ? MIX_PANEL_TST : MIX_PANEL_PRO);
      props.put(LOGIN_TYPE, loginType);
      mixpanel.track(actionId, props);
    } catch (NullPointerException error) {
      Log.e("Shootr", "Unable to build mixPanel object", error);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override public void sendSignUpEvent(User newUser, String actionId, String loginType, Context context) {
    try {
      JSONObject props = new JSONObject();
      mixpanel = MixpanelAPI.
          getInstance(context, (BuildConfig.DEBUG) ? MIX_PANEL_TST : MIX_PANEL_PRO);
      mixpanel.alias(newUser.getIdUser(), mixpanel.getDistinctId());
      mixpanel.getPeople().identify(newUser.getIdUser());
      mixpanel.getPeople().set(DISTINCT_ID, newUser.getIdUser());
      mixpanel.getPeople().set(LOGIN_TYPE, loginType);
      props.put(LOGIN_TYPE, loginType);
      mixpanel.track(actionId, props);
      mixpanel.flush();
    } catch (NullPointerException error) {
      Log.e("Shootr", "Unable to build mixPanel object", error);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    sendSignupToApsFlyer(actionId, loginType, context);
  }

  private void sendSignupToApsFlyer(String actionId, String loginType, Context context) {
    if (appsFlyerLib != null) {
      try {
        if (user != null) {
          Map<String, Object> eventData = new HashMap<>();
          eventData.put(DISTINCT_ID, user.getIdUser());
          eventData.put(LOGIN_TYPE, loginType);
          appsFlyerLib.trackEvent(context, actionId, eventData);
        }
      }  catch (NullPointerException error) {
        Log.e("Shootr", "Unable to build appsflyer object", error);
      }
    }
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
    String idPoll = builder.getIdPoll();
    String idShot = builder.getIdShot();
    String loginType = builder.getLoginType();
    User user = builder.getUser();

    sendGoogleAnalytics(context, action, actionId, labelId);
    sendMixPanelAnalytics(user, actionId, source, idTargetUser, targetUsername, notificationName,
        pushRedirection, idStream, stream, idPoll, idShot, loginType, context);
  }

  @Override public void appsFlyerSendAction(Builder builder) {
    if (appsFlyerLib != null) {
      Context context = builder.getContext();
      String actionId = builder.getActionId();
      String source = builder.getSource();
      String idTargetUser = builder.getIdTargetUser();
      String targetUsername = builder.getTargetUsername();
      String idStream = builder.getIdStream();
      String stream = builder.getStreamName();
      User user = builder.getUser();

      try {
        if (user != null) {
          Map<String, Object> eventData = new HashMap<>();
          eventData.put(DISTINCT_ID, user.getIdUser());
          if (idStream != null) {
            eventData.put(ID_STREAM, idStream);
          }
          if (stream != null) {
            eventData.put(STREAM_TITLE, stream);
          }
          eventData.put(ACTIVATED_USER, user.getReceivedReactions() == 1L);
          eventData.put(USER_TYPE, user.getAnalyticsUserType());
          if (source != null) {
            eventData.put(SOURCE, source);
          }
          if (idTargetUser != null) {
            eventData.put(ID_TARGET_USER, idTargetUser);
          }
          if (targetUsername != null) {
            eventData.put(TARGET_USERNAME, targetUsername);
          }
          eventData.put(FAVORITES, user.getFavoritedStreamsCount());
          eventData.put(FOLLOWING, user.getNumFollowings());
          eventData.put(FOLLOWERS, user.getNumFollowers());

          appsFlyerLib.trackEvent(context, actionId, eventData);
        }
      }  catch (NullPointerException error) {
        Log.e("Shootr", "Unable to build appsflyer object", error);
      }
    }
  }

  @Override public void reset() {
    mixpanel.clearSuperProperties();
    mixpanel.reset();
  }

  @Override public long getDiscoverTweak() {
    return 0;
  }

  @Override public void identify(User currentUser) {
    try {
      mixpanel.identify(currentUser.getIdUser());
      mixpanel.flush();
      mixpanel.getPeople()
          .addOnMixpanelUpdatesReceivedListener(new OnMixpanelUpdatesReceivedListener() {
            @Override public void onMixpanelUpdatesReceived() {
              if (mixpanel.getPeople().getDistinctId() != null) {
                mixpanel.getPeople().joinExperimentIfAvailable();
              }
            }
          });
      mixpanel.getPeople()
          .addOnMixpanelTweaksUpdatedListener(new OnMixpanelTweaksUpdatedListener() {
            @Override public void onMixpanelTweakUpdated() {

            }
          });
    } catch (NullPointerException error) {
      /* no-op */
    }
  }

  private void sendMixPanelAnalytics(User user, String actionId, String source, String idTargetUser,
      String targetUsername, String notificationName, String pushRedirection, String idStream,
      String streamName, String idPoll, String idShot, String loginType, Context context) {
    try {
      JSONObject props = new JSONObject();
      if (user != null) {
        mixpanel.identify(user.getIdUser());
        mixpanel.getPeople().identify(user.getIdUser());
        props.put(DISTINCT_ID, user.getIdUser());
        props.put(ACTIVATED_USER, user.getReceivedReactions() == 1L);
        props.put(USER_TYPE, user.getAnalyticsUserType());
        props.put(FAVORITES, user.getFavoritedStreamsCount());
        props.put(FOLLOWING, user.getNumFollowings());
        props.put(FOLLOWERS, user.getNumFollowers());
      }

      if (idStream != null) {
        props.put(ID_STREAM, idStream);
      }
      if (streamName != null) {
        props.put(STREAM_TITLE, streamName);
      }

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
      if (idPoll != null) {
        props.put(ID_POLL, idPoll);
      }
      if (idShot != null) {
        props.put(ID_SHOT, idShot);
      }
      if (loginType != null) {
        props.put(LOGIN_TYPE, loginType);
      }
      props.put(PLATFORM_TYPE, ANDROID_PLATFORM);
      try {
        mixpanel.track(actionId, props);
      } catch (Exception error) {
        mixpanel = MixpanelAPI.
            getInstance(context, (BuildConfig.DEBUG) ? MIX_PANEL_TST : MIX_PANEL_PRO);
        mixpanel.track(actionId, props);
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
      tracker.enableAdvertisingIdCollection(true);
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
