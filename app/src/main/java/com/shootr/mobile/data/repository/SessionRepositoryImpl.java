package com.shootr.mobile.data.repository;

import com.shootr.mobile.data.prefs.BooleanPreference;
import com.shootr.mobile.data.prefs.CurrentUserId;
import com.shootr.mobile.data.prefs.LastTimeFiltered;
import com.shootr.mobile.data.prefs.SessionToken;
import com.shootr.mobile.data.prefs.StringPreference;
import com.shootr.mobile.data.prefs.TimelineFilterActivated;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.CrashReportTool;
import javax.inject.Inject;

public class SessionRepositoryImpl implements SessionRepository {

  private final StringPreference sessionTokenPreference;

  private final StringPreference currentUserIdPreference;
  private final BooleanPreference timelineFilterPreference;
  private final StringPreference lastTimeFilteredPreference;
  private final CrashReportTool crashReportTool;
  private final AnalyticsTool analyticsTool;
  private User currentUser;
  private int synchroTime;

  @Inject public SessionRepositoryImpl(@SessionToken StringPreference sessionTokenPreference,
      @CurrentUserId StringPreference currentUserIdPreference,
      @TimelineFilterActivated BooleanPreference timelineFilterPreference,
      @LastTimeFiltered StringPreference lastTimeFiltered, CrashReportTool crashReportTool,
      AnalyticsTool analyticsTool) {
    this.sessionTokenPreference = sessionTokenPreference;
    this.currentUserIdPreference = currentUserIdPreference;
    this.timelineFilterPreference = timelineFilterPreference;
    this.lastTimeFilteredPreference = lastTimeFiltered;
    this.crashReportTool = crashReportTool;
    this.analyticsTool = analyticsTool;
    this.synchroTime = 10;
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
    analyticsTool.reset();
    currentUser = null;
  }

  @Override public boolean isTimelineFilterActivated() {
    return timelineFilterPreference.get();
  }

  @Override public void setTimelineFilterActivated(boolean isFilterActivated) {
    timelineFilterPreference.set(isFilterActivated);
  }

  @Override public String getLastTimeFiltered() {
    return lastTimeFilteredPreference.get();
  }

  @Override public void setLastTimeFiltered(String lastTimeFiltered) {
    lastTimeFilteredPreference.set(lastTimeFiltered);
  }

  @Override public int getSynchroTime() {
    try {
      return synchroTime;
    } catch (Exception e) {
      return 10;
    }
  }

  @Override public void setSynchroTime(String synchroTime) {
    try {
      this.synchroTime = Integer.parseInt(synchroTime);
    } catch (Exception e) {
      this.synchroTime = 10;
    }
  }
}
