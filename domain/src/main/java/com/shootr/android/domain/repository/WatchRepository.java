package com.shootr.android.domain.repository;

import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;

public interface WatchRepository {

    Watch getWatchForUserAndEvent(User user, Long idEvent, ErrorCallback callback);

    void putWatch(Watch watch, ErrorCallback callback);

    Watch getCurrentWatching(ErrorCallback callback);
}
