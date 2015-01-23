package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.data.mapper.WatchEntityMapper;
import com.shootr.android.data.repository.datasource.LocalDataSource;
import com.shootr.android.data.repository.datasource.RemoteDataSource;
import com.shootr.android.data.repository.datasource.WatchDataSource;
import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.repository.ErrorCallback;
import com.shootr.android.domain.repository.WatchRepository;
import javax.inject.Inject;

public class SyncWatchRepository implements WatchRepository {

    private final WatchDataSource localWatchDataSource;
    private final WatchDataSource remoteWatchDataSource;
    private final WatchEntityMapper watchEntityMapper;

    @Inject public SyncWatchRepository(@LocalDataSource WatchDataSource localWatchDataSource,
      @RemoteDataSource WatchDataSource remoteWatchDataSource, WatchEntityMapper watchEntityMapper) {
        this.localWatchDataSource = localWatchDataSource;
        this.remoteWatchDataSource = remoteWatchDataSource;
        this.watchEntityMapper = watchEntityMapper;
    }

    @Deprecated @Override public Watch getWatchForUserAndEvent(User user, Long idEvent, ErrorCallback callback) {
        return getWatchForUserAndEvent(user, idEvent);
    }

    @Override public Watch getWatchForUserAndEvent(User user, Long idEvent) {
        WatchEntity watchEntity = remoteWatchDataSource.getWatch(idEvent, user.getIdUser());
        //TODO update local
        return watchEntityMapper.transform(watchEntity, user);
    }

    @Deprecated @Override public void putWatch(Watch watch, WatchCallback callback) {
        throw new RuntimeException(
          "Method not implemented. It is in the interface for compatibility with old repository implementations");
    }

    @Override public Watch putWatch(Watch watch) {
        WatchEntity localWatchEntity = localWatchDataSource.getWatch(watch.getIdEvent(), watch.getUser().getIdUser());
        //TODO verify something maybe?
        WatchEntity remoteWatchEntity = remoteWatchDataSource.putWatch(localWatchEntity);
        localWatchDataSource.putWatch(remoteWatchEntity);
        return watchEntityMapper.transform(remoteWatchEntity, watch.getUser());
    }

    @Override public Watch getCurrentWatching(ErrorCallback callback) {
        throw new RuntimeException(
          "Method not implemented. It is in the interface for compatibility with old repository implementations");
    }

    @Override public Watch getCurrentWatching() {
        throw new RuntimeException("Method not implemented yet!");
    }

    @Override public Integer getAllWatchesCount() {
        throw new RuntimeException("Method not implemented yet!");
    }
}
