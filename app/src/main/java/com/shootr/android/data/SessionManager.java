package com.shootr.android.data;

import com.shootr.android.db.objects.UserEntity;

public interface SessionManager {

    public UserEntity getCurrentUser();

    public void setCurrentUser(UserEntity currentUser);

    public String getSessionToken();

    public void setSessionToken(String sessionToken);

    public long getCurrentUserId();

    public void setCurrentUserId(long currentUserId);

    public void createSession(long userId, String sessionToken, UserEntity loggedInUser);

    public void destroySession();
}
