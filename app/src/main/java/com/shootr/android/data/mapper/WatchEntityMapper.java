package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import javax.inject.Inject;

public class WatchEntityMapper {

    @Inject public WatchEntityMapper() {
    }

    public Watch transform(WatchEntity watchEntity, User user) {
        checkUserIdMatches(watchEntity, user);
        Watch watch = new Watch();
        watch.setUser(user);
        watch.setIdEvent(watchEntity.getIdMatch());
        watch.setWatching(WatchEntity.STATUS_WATCHING.equals(watchEntity.getStatus()));
        return watch;
    }

    private void checkUserIdMatches(WatchEntity watchEntity, User user) {
        if (!watchEntity.getIdUser().equals(user.getIdUser())) {
            throw new IllegalArgumentException(
              String.format("User id (%d) doesn't match watchEntity's id (%d)", user.getIdUser(),
                watchEntity.getIdUser()));
        }
    }
}
