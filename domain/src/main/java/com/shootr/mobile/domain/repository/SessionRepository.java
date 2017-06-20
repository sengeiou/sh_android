package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.model.user.User;

public interface SessionRepository {

    User getCurrentUser();

    void setCurrentUser(User currentUser);

    String getSessionToken();

    void setSessionToken(String sessionToken);

    String getCurrentUserId();

    void setCurrentUserId(String currentUserId);

    void createSession(String userId, String sessionToken, User loggedInUser);

    void destroySession();

    boolean isTimelineFilterActivated();

    void setTimelineFilterActivated(boolean isFilterActivated);

    int getSynchroTime();

    void setSynchroTime(String synchroTime);

    String getDeviceId();

    void setDeviceId(String device);
}
