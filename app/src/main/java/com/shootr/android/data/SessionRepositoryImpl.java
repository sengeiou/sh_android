package com.shootr.android.data;

import com.shootr.android.data.prefs.CurrentUserId;
import com.shootr.android.data.prefs.LongPreference;
import com.shootr.android.data.prefs.SessionToken;
import com.shootr.android.data.prefs.StringPreference;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.UserEntity;
import javax.inject.Inject;

public class SessionRepositoryImpl implements SessionRepository {

    private StringPreference sessionTokenPreference;

    private UserEntity currentUser;

    private LongPreference currentUserIdPreference;

    @Inject public SessionRepositoryImpl(@SessionToken StringPreference sessionTokenPreference,
      @CurrentUserId LongPreference currentUserIdPreference) {
        this.sessionTokenPreference = sessionTokenPreference;
        this.currentUserIdPreference = currentUserIdPreference;
    }

    @Override
    public UserEntity getCurrentUser() {
        return currentUser;
    }

    @Override
    public void setCurrentUser(UserEntity currentUser) {
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
    public long getCurrentUserId() {
        return currentUserIdPreference.get();
    }

    @Override
    public void setCurrentUserId(long currentUserId) {
        this.currentUserIdPreference.set(currentUserId);
    }

    @Override
    public void createSession(long userId, String sessionToken, UserEntity loggedInUser) {
        setCurrentUserId(userId);
        setSessionToken(sessionToken);
        setCurrentUser(loggedInUser);
    }

    @Override
    public void destroySession() {
        currentUserIdPreference.delete();
        sessionTokenPreference.delete();
        currentUser = null;
    }
}
