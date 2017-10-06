package com.shootr.mobile.data.repository;

import com.shootr.mobile.data.prefs.ActivityBadgeCount;
import com.shootr.mobile.data.prefs.BooleanPreference;
import com.shootr.mobile.data.prefs.CacheTimeKeepAlive;
import com.shootr.mobile.data.prefs.CurrentUserId;
import com.shootr.mobile.data.prefs.DeviceId;
import com.shootr.mobile.data.prefs.DevicePref;
import com.shootr.mobile.data.prefs.DevicePreferences;
import com.shootr.mobile.data.prefs.IntPreference;
import com.shootr.mobile.data.prefs.LastTimeFiltered;
import com.shootr.mobile.data.prefs.LongPreference;
import com.shootr.mobile.data.prefs.PublicVoteAlertPreference;
import com.shootr.mobile.data.prefs.SessionToken;
import com.shootr.mobile.data.prefs.StringPreference;
import com.shootr.mobile.data.prefs.TimelineFilterActivated;
import com.shootr.mobile.domain.model.Device;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.CrashReportTool;
import javax.inject.Inject;

public class SessionRepositoryImpl implements SessionRepository {

  private static final int REFRESH_INTERVAL_SECONDS = 10;
  private final StringPreference sessionTokenPreference;
  private final StringPreference currentUserIdPreference;
  private final LongPreference cacheTimeKeepAlive;
  private final BooleanPreference timelineFilterPreference;
  private final StringPreference lastTimeFilteredPreference;
  private final BooleanPreference publicVoteAlertPreference;
  private final StringPreference deviceIdPreference;
  private final DevicePreferences devicePreference;
  private final IntPreference badgeCount;
  private final CrashReportTool crashReportTool;
  private final AnalyticsTool analyticsTool;
  private User currentUser;
  private int synchroTime;

  @Inject public SessionRepositoryImpl(@SessionToken StringPreference sessionTokenPreference,
      @CurrentUserId StringPreference currentUserIdPreference,
      @CacheTimeKeepAlive LongPreference cacheTimeKeepAlive,
      @TimelineFilterActivated BooleanPreference timelineFilterPreference,
      @LastTimeFiltered StringPreference lastTimeFiltered,
      @PublicVoteAlertPreference BooleanPreference publicVoteAlertPreference,
      @DeviceId StringPreference deviceIdPreference, @DevicePref DevicePreferences devicePreference,
      @ActivityBadgeCount IntPreference badgeCount, CrashReportTool crashReportTool, AnalyticsTool analyticsTool) {
    this.sessionTokenPreference = sessionTokenPreference;
    this.currentUserIdPreference = currentUserIdPreference;
    this.cacheTimeKeepAlive = cacheTimeKeepAlive;
    this.timelineFilterPreference = timelineFilterPreference;
    this.lastTimeFilteredPreference = lastTimeFiltered;
    this.publicVoteAlertPreference = publicVoteAlertPreference;
    this.deviceIdPreference = deviceIdPreference;
    this.devicePreference = devicePreference;
    this.badgeCount = badgeCount;
    this.crashReportTool = crashReportTool;
    this.analyticsTool = analyticsTool;
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
    publicVoteAlertPreference.delete();
    deviceIdPreference.delete();
    devicePreference.delete();
    cacheTimeKeepAlive.delete();
    badgeCount.delete();
    analyticsTool.reset();
    currentUser = null;
  }

  @Override public boolean isTimelineFilterActivated() {
    return timelineFilterPreference.get();
  }

  @Override public void setTimelineFilterActivated(boolean isFilterActivated) {
    timelineFilterPreference.set(isFilterActivated);
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
}
