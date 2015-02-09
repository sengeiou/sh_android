package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.utils.TimeUtils;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WatchEntityMapper {

    private final TimeUtils timeUtils;

    @Inject public WatchEntityMapper(TimeUtils timeUtils) {
        this.timeUtils = timeUtils;
    }

    public Watch transform(WatchEntity watchEntity, User user) {
        if (watchEntity == null) {
            return null;
        }
        checkUserIdMatches(watchEntity, user);
        Watch watch = new Watch();
        watch.setUser(user);
        watch.setIdEvent(watchEntity.getIdEvent());
        watch.setUserStatus(watchEntity.getPlace());
        watch.setVisible(watchEntity.isVisible());
        return watch;
    }

    private void checkUserIdMatches(WatchEntity watchEntity, User user) {
        if (watchEntity.getIdUser() == null) {
            throw new IllegalArgumentException("Watch can't have null user id");
        }
        if (!watchEntity.getIdUser().equals(user.getIdUser())) {
            throw new IllegalArgumentException(
              String.format("User id (%d) doesn't match watchEntity's id (%d)", user.getIdUser(),
                watchEntity.getIdUser()));
        }
    }

    public WatchEntity transform(Watch watch) {
        if (watch == null) {
            return null;
        }
        WatchEntity watchEntity = new WatchEntity();
        watchEntity.setIdUser(watch.getUser().getIdUser());
        watchEntity.setIdEvent(watch.getIdEvent());
        watchEntity.setPlace(watch.getUserStatus());
        watchEntity.setVisible(watch.isVisible());

        Date currentDate = timeUtils.getCurrentDate();
        watchEntity.setCsysBirth(currentDate);
        watchEntity.setCsysModified(currentDate);
        watchEntity.setCsysRevision(0);
        watchEntity.setCsysSynchronized(Synchronized.SYNC_NEW);
        return watchEntity;
    }
}
