package com.shootr.android.domain.repository;

import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import java.util.List;

public interface WatchRepository {

    Watch getWatchForUserAndEvent(User user, Long idEvent);

    List<Watch> getWatchesForUsersAndEvent(List<User> users, Long idEvent);

    List<Watch> getWatchesFromUsers(List<User> userIds);

    Watch putWatch(Watch watch);

    Watch getCurrentVisibleWatch();
}
