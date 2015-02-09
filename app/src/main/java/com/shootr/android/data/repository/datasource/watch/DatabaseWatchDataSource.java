package com.shootr.android.data.repository.datasource.watch;

import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.db.manager.WatchManager;
import java.util.List;
import javax.inject.Inject;

public class DatabaseWatchDataSource implements WatchDataSource {

    private final WatchManager watchManager;

    @Inject public DatabaseWatchDataSource(WatchManager watchManager) {
        this.watchManager = watchManager;
    }

    @Override public WatchEntity getWatch(Long idEvent, Long idUser) {
        return watchManager.getWatchByKeys(idUser, idEvent);
    }

    @Override public WatchEntity putWatch(WatchEntity watchEntity) {
        watchManager.saveWatch(watchEntity);
        return watchEntity;
    }

    @Override public List<WatchEntity> putWatches(List<WatchEntity> watchEntities) {
        for (WatchEntity remoteWatch : watchEntities) {
            this.putWatch(remoteWatch);
        }
        return watchEntities;
    }

    @Override public WatchEntity getVisible(Long userId) {
        return watchManager.getWatchVisibleByUser(userId);
    }

    @Override public List<WatchEntity> getWatchesForUsersAndEvent(List<Long> users, Long idEvent) {
        return watchManager.getWatchesByEventForUsers(users, idEvent);
    }

    @Override public List<WatchEntity> getWatchesFromUsers(List<Long> users) {
        return watchManager.getWatchesNotEndedFromUsers(users);
    }

    @Override public void deleteAllWatchesNotPending() {
        List<WatchEntity> watchesSynchronized = watchManager.getWatchesSynchronized();
        watchManager.deleteWatches(watchesSynchronized);
    }

    @Override public List<WatchEntity> getEntitiesNotSynchronized() {
        return watchManager.getWatchesNotSynchronized();
    }
}
