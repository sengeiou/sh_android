package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.model.Bootstrapping;
import com.shootr.mobile.domain.model.Device;
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

    String getTimelineFilter();

    void setTimelineFilter(String typeFilter);

    int getSynchroTime();

    void setSynchroTime(String synchroTime);

    String getDeviceId();

    void setDeviceId(String device);

    void setDevice(Device device);

    Device getDevice();

    void setBootstrapping(Bootstrapping bootstrapping);

    Bootstrapping getBootstrapping();

    String getLogAddress();

    void resetFilter(String idStream);

    void resetMultipleFilter(String idStream);

    boolean isNewTimeline();

    boolean isNewShotDetail();

    boolean isPromotedShotActivated();
}
