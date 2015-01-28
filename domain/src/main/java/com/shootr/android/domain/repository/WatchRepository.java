package com.shootr.android.domain.repository;

import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import java.util.List;

public interface WatchRepository {

    @Deprecated
    interface WatchCallback extends ErrorCallback{

        void onLoaded(Watch watch);

    }

    @Deprecated
    Watch getWatchForUserAndEvent(User user, Long idEvent, ErrorCallback callback);

    Watch getWatchForUserAndEvent(User user, Long idEvent);

    List<Watch> getWatchesFromUsersAndEvent(List<User> users, Long idEvent);

    List<Watch> getWatchesFromUsers(List<Long> userIds);

    @Deprecated
    void putWatch(Watch watch, WatchCallback callback);

    Watch putWatch(Watch watch);

    @Deprecated
    Watch getCurrentWatching(ErrorCallback callback);

    Watch getCurrentWatching();

    Watch getCurrentVisibleWatch();
}
