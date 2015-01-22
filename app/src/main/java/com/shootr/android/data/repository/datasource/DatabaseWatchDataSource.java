package com.shootr.android.data.repository.datasource;

import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.db.manager.WatchManager;
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
}
