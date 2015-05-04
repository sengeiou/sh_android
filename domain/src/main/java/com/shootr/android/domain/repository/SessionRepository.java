package com.shootr.android.domain.repository;

import com.shootr.android.domain.User;

public interface SessionRepository {

    public User getCurrentUser();

    public void setCurrentUser(User currentUser);

    public String getSessionToken();

    public void setSessionToken(String sessionToken);

    public String getCurrentUserId();

    public void setCurrentUserId(String currentUserId);

    public void createSession(String userId, String sessionToken, User loggedInUser);

    public void destroySession();
}
