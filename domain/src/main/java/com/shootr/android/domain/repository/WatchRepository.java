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

    @Deprecated
    void putWatch(Watch watch, WatchCallback callback);

    Watch putWatch(Watch watch);

    Watch getCurrentWatching(ErrorCallback callback);

    Integer getAllWatchesCount();
}
