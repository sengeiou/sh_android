package com.shootr.mobile.data.repository;

import com.shootr.mobile.data.prefs.ActivityBadgeCount;
import com.shootr.mobile.data.prefs.BooleanPreference;
import com.shootr.mobile.data.prefs.BootstrappingPref;
import com.shootr.mobile.data.prefs.BootstrappingPreferences;
import com.shootr.mobile.data.prefs.CacheTimeKeepAlive;
import com.shootr.mobile.data.prefs.CurrentUserId;
import com.shootr.mobile.data.prefs.DeviceId;
import com.shootr.mobile.data.prefs.DevicePref;
import com.shootr.mobile.data.prefs.DevicePreferences;
import com.shootr.mobile.data.prefs.FcmToken;
import com.shootr.mobile.data.prefs.IntPreference;
import com.shootr.mobile.data.prefs.LastTimeFiltered;
import com.shootr.mobile.data.prefs.LongPreference;
import com.shootr.mobile.data.prefs.PublicVoteAlertPreference;
import com.shootr.mobile.data.prefs.SessionToken;
import com.shootr.mobile.data.prefs.ShowSSIntroPreference;
import com.shootr.mobile.data.prefs.StringPreference;
import com.shootr.mobile.data.prefs.TimelineFilterActivated;
import com.shootr.mobile.data.prefs.TimelineMultipleFilter;
import com.shootr.mobile.domain.model.Bootstrapping;
import com.shootr.mobile.domain.model.Device;
import com.shootr.mobile.domain.model.TimelineType;
import com.shootr.mobile.domain.model.stream.LandingStreams;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.CrashReportTool;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;
import javax.inject.Inject;

public class SessionRepositoryImpl implements SessionRepository {

  private static final int REFRESH_INTERVAL_SECONDS = 10;
  private final StringPreference sessionTokenPreference;
  private final StringPreference fcmTokenPreference;
  private final StringPreference currentUserIdPreference;
  private final LongPreference cacheTimeKeepAlive;
  private final BooleanPreference timelineFilterPreference;
  private final BooleanPreference showIntroSSPreference;
  private final StringPreference timelineMultipleFilterPreference;
  private final StringPreference lastTimeFilteredPreference;
  private final BooleanPreference publicVoteAlertPreference;
  private final StringPreference deviceIdPreference;
  private final DevicePreferences devicePreference;
  private final BootstrappingPreferences bootstrappingPreferences;
  private final IntPreference badgeCount;
  private final CrashReportTool crashReportTool;
  private final AnalyticsTool analyticsTool;
  private final DualCache<LandingStreams> landingStreamsLruCache;
  private final DualCache<Long> lastStreamVisitCache;
  private User currentUser;
  private int synchroTime;

  @Inject public SessionRepositoryImpl(@SessionToken StringPreference sessionTokenPreference,
      @FcmToken StringPreference fcmTokenPreference, @CurrentUserId StringPreference currentUserIdPreference,
      @CacheTimeKeepAlive LongPreference cacheTimeKeepAlive,
      @TimelineFilterActivated BooleanPreference timelineFilterPreference,
      @ShowSSIntroPreference BooleanPreference showIntroSSPreference,
      @TimelineMultipleFilter StringPreference timelineMultipleFilterPreference,
      @LastTimeFiltered StringPreference lastTimeFiltered,
      @PublicVoteAlertPreference BooleanPreference publicVoteAlertPreference,
      @DeviceId StringPreference deviceIdPreference, @DevicePref DevicePreferences devicePreference,
      @BootstrappingPref BootstrappingPreferences bootstrappingPreferences,
      @ActivityBadgeCount IntPreference badgeCount, CrashReportTool crashReportTool,
      AnalyticsTool analyticsTool, DualCache<LandingStreams> landingStreamsLruCache,
      DualCache<Long> lastStreamVisitCache) {
    this.sessionTokenPreference = sessionTokenPreference;
    this.fcmTokenPreference = fcmTokenPreference;
    this.currentUserIdPreference = currentUserIdPreference;
    this.cacheTimeKeepAlive = cacheTimeKeepAlive;
    this.timelineFilterPreference = timelineFilterPreference;
    this.showIntroSSPreference = showIntroSSPreference;
    this.timelineMultipleFilterPreference = timelineMultipleFilterPreference;
    this.lastTimeFilteredPreference = lastTimeFiltered;
    this.publicVoteAlertPreference = publicVoteAlertPreference;
    this.deviceIdPreference = deviceIdPreference;
    this.devicePreference = devicePreference;
    this.bootstrappingPreferences = bootstrappingPreferences;
    this.badgeCount = badgeCount;
    this.crashReportTool = crashReportTool;
    this.analyticsTool = analyticsTool;
    this.landingStreamsLruCache = landingStreamsLruCache;
    this.lastStreamVisitCache = lastStreamVisitCache;
    this.synchroTime = REFRESH_INTERVAL_SECONDS;
  }

  @Override public User getCurrentUser() {
    return currentUser;
  }

  @Override public void setCurrentUser(User currentUser) {
    this.currentUser = currentUser;
  }

  @Override public String getSessionToken() {
    return sessionTokenPreference.get();
  }

  @Override public void setSessionToken(String sessionToken) {
    this.sessionTokenPreference.set(sessionToken);
  }

  @Override public String getCurrentUserId() {
    return currentUserIdPreference.get();
  }

  @Override public void setCurrentUserId(String currentUserId) {
    this.currentUserIdPreference.set(currentUserId);
  }

  @Override public void createSession(String userId, String sessionToken, User loggedInUser) {
    setCurrentUserId(userId);
    setSessionToken(sessionToken);
    setCurrentUser(loggedInUser);
    crashReportTool.setUserId(userId);
    crashReportTool.setUserName(loggedInUser.getUsername());
    crashReportTool.setUserEmail(loggedInUser.getEmail());
    analyticsTool.setUser(loggedInUser);
  }

  @Override public void destroySession() {
    currentUserIdPreference.delete();
    sessionTokenPreference.delete();
    lastTimeFilteredPreference.delete();
    timelineFilterPreference.delete();
    timelineMultipleFilterPreference.delete();
    publicVoteAlertPreference.delete();
    deviceIdPreference.delete();
    devicePreference.delete();
    cacheTimeKeepAlive.delete();
    badgeCount.delete();
    analyticsTool.reset();
    landingStreamsLruCache.invalidate();
    bootstrappingPreferences.delete();
    lastStreamVisitCache.invalidate();
    showIntroSSPreference.delete();
    currentUser = null;
  }

  @Override public boolean isTimelineFilterActivated() {
    return timelineFilterPreference.get();
  }

  @Override public void setTimelineFilterActivated(boolean isFilterActivated) {
    timelineFilterPreference.set(isFilterActivated);
  }

  @Override public String getTimelineFilter() {
    return timelineMultipleFilterPreference.get();
  }

  @Override public void setTimelineFilter(String typeFilter) {
    timelineMultipleFilterPreference.set(typeFilter);
  }

  @Override public int getSynchroTime() {
    try {
      return synchroTime;
    } catch (Exception e) {
      return REFRESH_INTERVAL_SECONDS;
    }
  }

  @Override public void setSynchroTime(String synchroTime) {
    try {
      this.synchroTime = Integer.parseInt(synchroTime);
    } catch (Exception e) {
      this.synchroTime = REFRESH_INTERVAL_SECONDS;
    }
  }

  @Override public String getDeviceId() {
    return deviceIdPreference.get();
  }

  @Override public void setDeviceId(String device) {
    deviceIdPreference.set(device);
  }

  @Override public void setDevice(Device device) {
    devicePreference.set(device);
  }

  @Override public Device getDevice() {
    return devicePreference.get();
  }

  @Override public void setBootstrapping(Bootstrapping bootstrapping) {
    bootstrappingPreferences.set(bootstrapping);
  }

  @Override public Bootstrapping getBootstrapping() {
    return bootstrappingPreferences.get();
  }

  @Override public String getLogAddress() {
    return bootstrappingPreferences.get().getLogsUrl();
  }

  @Override public boolean isPromotedShotActivated() {
    return bootstrappingPreferences.get() != null && bootstrappingPreferences.get().isSuperShot();
  }

  @Override public boolean hasShownIntroPromotedShot() {
    return showIntroSSPreference.get();
  }

  @Override public void setShowIntroPromotedShot(boolean hasShownSSIntro) {
    showIntroSSPreference.set(hasShownSSIntro);
  }

  @Override public String getFCMToken() {
    return fcmTokenPreference.get();
  }

  @Override public void setFCMToken(String fcmToken) {
    fcmTokenPreference.set(fcmToken);
  }

  @Override public void resetFilter(String idStream) {
    if (!idStream.equals(currentUser.getIdWatchingStream())) {
      setTimelineFilterActivated(false);
    }
  }

  @Override public void resetMultipleFilter(String idStream) {
    if (!idStream.equals(currentUser.getIdWatchingStream())) {
      setTimelineFilter(TimelineType.MAIN);
    }
  }
}
