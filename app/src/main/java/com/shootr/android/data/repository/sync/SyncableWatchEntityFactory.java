package com.shootr.android.data.repository.sync;

import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.data.mapper.WatchEntityMapper;
import com.shootr.android.data.repository.datasource.LocalDataSource;
import com.shootr.android.data.repository.datasource.watch.WatchDataSource;
import com.shootr.android.domain.Watch;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SyncableWatchEntityFactory extends SyncableEntityFactory<Watch, WatchEntity> {

    private WatchEntityMapper watchEntityMapper;
    private WatchDataSource localWatchDataSource;

    @Inject public SyncableWatchEntityFactory(WatchEntityMapper watchEntityMapper, @LocalDataSource WatchDataSource localWatchDataSource) {
        this.watchEntityMapper = watchEntityMapper;
        this.localWatchDataSource = localWatchDataSource;
    }

    @Override protected WatchEntity currentEntity(Watch watch) {
        return localWatchDataSource.getWatch(watch.getUser().getIdUser(), watch.getIdEvent());
    }

    @Override protected WatchEntity updateValues(WatchEntity currentWatchEntity, Watch watch) {
        WatchEntity watchEntityFromWatch = watchEntityMapper.transform(watch);
        watchEntityFromWatch.setCsysModified(new Date());
        watchEntityFromWatch.setCsysRevision(currentWatchEntity.getCsysRevision()+1);
        watchEntityFromWatch.setCsysBirth(currentWatchEntity.getCsysBirth());
        watchEntityFromWatch.setCsysSynchronized(Synchronized.SYNC_UPDATED);
        watchEntityFromWatch.setCsysDeleted(currentWatchEntity.getCsysDeleted());
        return watchEntityFromWatch;
    }

    @Override protected WatchEntity createNewEntity(Watch watch) {
        WatchEntity newEntityFromWatch = watchEntityMapper.transform(watch);
        newEntityFromWatch.setCsysSynchronized(Synchronized.SYNC_NEW);
        newEntityFromWatch.setCsysBirth(new Date()); //TODO dates from timeutils
        newEntityFromWatch.setCsysModified(new Date());
        newEntityFromWatch.setCsysRevision(0);
        return newEntityFromWatch;
    }
}
