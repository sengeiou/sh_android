package com.shootr.mobile.data.repository;

import com.shootr.mobile.data.prefs.CurrentUserId;
import com.shootr.mobile.data.prefs.SessionToken;
import com.shootr.mobile.data.prefs.StringPreference;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.CrashReportTool;
import javax.inject.Inject;

public class SessionRepositoryImpl implements SessionRepository {

    private final StringPreference sessionTokenPreference;

    private final StringPreference currentUserIdPreference;
    private final CrashReportTool crashReportTool;

    private final AnalyticsTool analyticsTool;

    private User currentUser;

    @Inject public SessionRepositoryImpl(@SessionToken StringPreference sessionTokenPreference,
      @CurrentUserId StringPreference currentUserIdPreference, CrashReportTool crashReportTool,
      AnalyticsTool analyticsTool) {
        this.sessionTokenPreference = sessionTokenPreference;
        this.currentUserIdPreference = currentUserIdPreference;
        this.crashReportTool = crashReportTool;
        this.analyticsTool = analyticsTool;
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
        currentUser = null;
    }
}
