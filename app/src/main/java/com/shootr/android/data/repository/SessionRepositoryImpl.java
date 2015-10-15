package com.shootr.android.data.repository;

import com.shootr.android.data.prefs.CurrentUserId;
import com.shootr.android.data.prefs.SessionToken;
import com.shootr.android.data.prefs.StringPreference;
import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.util.AnalyticsTool;
import com.shootr.android.util.CrashReportTool;
import javax.inject.Inject;

public class SessionRepositoryImpl implements SessionRepository {

    private final StringPreference sessionTokenPreference;

    private final StringPreference currentUserIdPreference;
    private final CrashReportTool crashReportTool;

    private final AnalyticsTool analyticsTool;

    private User currentUser;

    @Inject public SessionRepositoryImpl(@SessionToken StringPreference sessionTokenPreference,
      @CurrentUserId StringPreference currentUserIdPreference,
      CrashReportTool crashReportTool,
      AnalyticsTool analyticsTool) {
        this.sessionTokenPreference = sessionTokenPreference;
        this.currentUserIdPreference = currentUserIdPreference;
        this.crashReportTool = crashReportTool;
        this.analyticsTool = analyticsTool;
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public String getSessionToken() {
        return sessionTokenPreference.get();
    }

    @Override
    public void setSessionToken(String sessionToken) {
        this.sessionTokenPreference.set(sessionToken);
    }

    @Override
    public String getCurrentUserId() {
        return currentUserIdPreference.get();
    }

    @Override
    public void setCurrentUserId(String currentUserId) {
        this.currentUserIdPreference.set(currentUserId);
    }

    @Override
    public void createSession(String userId, String sessionToken, User loggedInUser) {
        setCurrentUserId(userId);
        setSessionToken(sessionToken);
        setCurrentUser(loggedInUser);
        crashReportTool.setUserId(userId);
        crashReportTool.setUserName(loggedInUser.getUsername());
        crashReportTool.setUserEmail(loggedInUser.getEmail());
        analyticsTool.setUserId(userId);
    }

    @Override
    public void destroySession() {
        currentUserIdPreference.delete();
        sessionTokenPreference.delete();
        currentUser = null;
    }
}
