package com.shootr.android.data.repository;

import com.crashlytics.android.Crashlytics;
import com.shootr.android.data.prefs.CurrentUserId;
import com.shootr.android.data.prefs.SessionToken;
import com.shootr.android.data.prefs.StringPreference;
import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.SessionRepository;
import javax.inject.Inject;

public class SessionRepositoryImpl implements SessionRepository {

    private StringPreference sessionTokenPreference;

    private User currentUser;

    private StringPreference currentUserIdPreference;

    @Inject public SessionRepositoryImpl(@SessionToken StringPreference sessionTokenPreference,
      @CurrentUserId StringPreference currentUserIdPreference) {
        this.sessionTokenPreference = sessionTokenPreference;
        this.currentUserIdPreference = currentUserIdPreference;
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
        //TODO use some pattern or abstraction, setting these values here directly is quite ugly
        Crashlytics.setUserIdentifier(userId);
        Crashlytics.setUserName(loggedInUser.getUsername());
        Crashlytics.setUserEmail(loggedInUser.getEmail());
    }

    @Override
    public void destroySession() {
        currentUserIdPreference.delete();
        sessionTokenPreference.delete();
        currentUser = null;
    }
}
