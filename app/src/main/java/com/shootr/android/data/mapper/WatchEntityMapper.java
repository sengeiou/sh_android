package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.util.TimeUtils;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WatchEntityMapper {

    private final TimeUtils timeUtils;

    @Inject public WatchEntityMapper(TimeUtils timeUtils) {
        this.timeUtils = timeUtils;
    }

    public Watch transform(WatchEntity watchEntity, User user) {
        checkUserIdMatches(watchEntity, user);
        Watch watch = new Watch();
        watch.setUser(user);
        watch.setIdEvent(watchEntity.getIdEvent());
        watch.setUserStatus(watchEntity.getPlace());
        watch.setWatching(WatchEntity.STATUS_WATCHING.equals(watchEntity.getStatus()));
        watch.setNotificaticationsEnabled(Integer.valueOf(1).equals(watchEntity.getNotification()));
        return watch;
    }

    private void checkUserIdMatches(WatchEntity watchEntity, User user) {
        if (!watchEntity.getIdUser().equals(user.getIdUser())) {
            throw new IllegalArgumentException(
              String.format("User id (%d) doesn't match watchEntity's id (%d)", user.getIdUser(),
                watchEntity.getIdUser()));
        }
    }

    public WatchEntity transform(Watch watch) {
        WatchEntity watchEntity = new WatchEntity();
        watchEntity.setIdUser(watch.getUser().getIdUser());
        watchEntity.setIdEvent(watch.getIdEvent());
        watchEntity.setPlace(watch.getUserStatus());
        watchEntity.setStatus(watch.isWatching() ? WatchEntity.STATUS_WATCHING : WatchEntity.STATUS_REJECT);
        watchEntity.setNotification(watch.isNotificaticationsEnabled() ? WatchEntity.NOTIFICATION_ON : WatchEntity.NOTIFICATION_OFF);
        watchEntity.setVisible(false);

        watchEntity.setCsysBirth(timeUtils.getCurrentDate());
        watchEntity.setCsysModified(timeUtils.getCurrentDate());
        watchEntity.setCsysRevision(0);
        watchEntity.setCsysSynchronized(Synchronized.SYNC_NEW);
        return watchEntity;
    }
}
