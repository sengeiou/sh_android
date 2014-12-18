package com.shootr.android.domain.repository;

import com.shootr.android.domain.UserEntity;

public interface SessionRepository {

    public UserEntity getCurrentUser();

    public void setCurrentUser(UserEntity currentUser);

    public String getSessionToken();

    public void setSessionToken(String sessionToken);

    public long getCurrentUserId();

    public void setCurrentUserId(long currentUserId);

    public void createSession(long userId, String sessionToken, UserEntity loggedInUser);

    public void destroySession();
}
