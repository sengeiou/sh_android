package com.shootr.android.data.repository.local;

import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.data.mapper.WatchEntityMapper;
import com.shootr.android.data.repository.datasource.LocalDataSource;
import com.shootr.android.data.repository.datasource.WatchDataSource;
import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.repository.ErrorCallback;
import com.shootr.android.domain.repository.WatchRepository;
import java.util.Date;
import javax.inject.Inject;

public class LocalWatchRepository implements WatchRepository{

    private final WatchDataSource localWatchDataSource;
    private final WatchEntityMapper watchEntityMapper;

    @Inject public LocalWatchRepository(@LocalDataSource WatchDataSource localWatchDataSource,
      WatchEntityMapper watchEntityMapper) {
        this.localWatchDataSource = localWatchDataSource;
        this.watchEntityMapper = watchEntityMapper;
    }

    @Deprecated
    @Override public Watch getWatchForUserAndEvent(User user, Long idEvent, ErrorCallback callback) {
        return getWatchForUserAndEvent(user, idEvent);
    }

    @Override public Watch getWatchForUserAndEvent(User user, Long idEvent) {
        WatchEntity watchEntity = localWatchDataSource.getWatch(idEvent, user.getIdUser());
        if (watchEntity == null) {
            return null;
        }
        return watchEntityMapper.transform(watchEntity, user);
    }

    @Deprecated
    @Override public void putWatch(Watch watch, WatchCallback callback) {
        throw new RuntimeException("Method not implemented. It's in the interface for compatibility with old repositories.");
    }

    @Override public Watch putWatch(Watch watch) {
        WatchEntity currentWatchEntity = localWatchDataSource.getWatch(watch.getUser().getIdUser(), watch.getIdEvent());
        WatchEntity finalWatchEntity = updateCurrentEntityValuesOrCreate(currentWatchEntity, watch);

        finalWatchEntity = localWatchDataSource.putWatch(finalWatchEntity);
        return watchEntityMapper.transform(finalWatchEntity, watch.getUser());
    }

    @Override public Watch getCurrentWatching(ErrorCallback callback) {
        throw new RuntimeException("Method not implemented yet!");
    }

    @Override public Integer getAllWatchesCount() {
        throw new RuntimeException("Method not implemented yet!");
    }

    private WatchEntity updateCurrentEntityValuesOrCreate(WatchEntity currentWatchEntity, Watch watch) {
        boolean alreadyExistEntity = currentWatchEntity != null;
        if (alreadyExistEntity) {
            currentWatchEntity = createWatchEntity(watch);
        } else {
            currentWatchEntity = watchEntityMapper.transform(watch);
        }
        return currentWatchEntity;
    }

    private WatchEntity createWatchEntity(Watch watch) {
        WatchEntity newWatchEntity = new WatchEntity();
        newWatchEntity.setNotification(watch.isNotificaticationsEnabled() ? 1 : 0);
        newWatchEntity.setStatus(watch.isWatching() ? 1L : 2L);
        newWatchEntity.setPlace(watch.getUserStatus());
        newWatchEntity.setVisible(watch.isVisible());
        newWatchEntity.setCsysSynchronized(Synchronized.SYNC_UPDATED);
        newWatchEntity.setCsysModified(new Date());
        newWatchEntity.setCsysRevision(newWatchEntity.getCsysRevision() + 1);
        return newWatchEntity;
    }
}
