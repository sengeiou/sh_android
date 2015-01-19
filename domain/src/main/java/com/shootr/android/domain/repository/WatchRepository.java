package com.shootr.android.domain.repository;

import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import java.util.List;

public interface WatchRepository {

    interface WatchCallback extends ErrorCallback{

        void onLoaded(Watch watch);
    }

    Watch getWatchForUserAndEvent(User user, Long idEvent, ErrorCallback callback);

    void putWatch(Watch watch, WatchCallback callback);

    Watch getCurrentWatching(ErrorCallback callback);

    Integer getAllWatchesCount();
}
