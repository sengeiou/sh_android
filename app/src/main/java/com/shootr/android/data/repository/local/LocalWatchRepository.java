package com.shootr.android.data.repository.local;

import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.data.mapper.WatchEntityMapper;
import com.shootr.android.data.repository.sync.SyncableWatchEntityFactory;
import com.shootr.android.data.repository.datasource.LocalDataSource;
import com.shootr.android.data.repository.datasource.WatchDataSource;
import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.repository.ErrorCallback;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.WatchRepository;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

public class LocalWatchRepository implements WatchRepository {

    private final WatchDataSource localWatchDataSource;
    private final WatchEntityMapper watchEntityMapper;
    private final SessionRepository sessionRepository;
    private final SyncableWatchEntityFactory syncableWatchEntityFactory;

    @Inject public LocalWatchRepository(@LocalDataSource WatchDataSource localWatchDataSource,
      WatchEntityMapper watchEntityMapper, SessionRepository sessionRepository,
      SyncableWatchEntityFactory syncableWatchEntityFactory) {
        this.localWatchDataSource = localWatchDataSource;
        this.watchEntityMapper = watchEntityMapper;
        this.sessionRepository = sessionRepository;
        this.syncableWatchEntityFactory = syncableWatchEntityFactory;
    }

    @Deprecated @Override public Watch getWatchForUserAndEvent(User user, Long idEvent, ErrorCallback callback) {
        return getWatchForUserAndEvent(user, idEvent);
    }

    @Override public Watch getWatchForUserAndEvent(User user, Long idEvent) {
        WatchEntity watchEntity = localWatchDataSource.getWatch(idEvent, user.getIdUser());
        if (watchEntity == null) {
            return null;
        }
        return watchEntityMapper.transform(watchEntity, user);
    }

    @Override public List<Watch> getWatchesFromUsersAndEvent(List<User> users, Long idEvent) {
        //TODO Mock!!!
        WatchEntity watchEntity = localWatchDataSource.getWatch(idEvent, users.get(0).getIdUser());
        Watch watch = watchEntityMapper.transform(watchEntity, users.get(0));
        return Arrays.asList(watch, watch, watch);
    }

    @Override public List<Watch> getWatchesFromUsers(List<Long> userIds) {
        throw new RuntimeException("Method not implemented yet!");
    }

    @Deprecated @Override public void putWatch(Watch watch, WatchCallback callback) {
        throw new RuntimeException(
          "Method not implemented. It's in the interface for compatibility with old repositories.");
    }

    @Override public Watch putWatch(Watch watch) {
        WatchEntity currentOrNewWatchEntity = syncableWatchEntityFactory.currentOrNewEntity(watch);
        WatchEntity storedWatchEntity = localWatchDataSource.putWatch(currentOrNewWatchEntity);
        return watchEntityMapper.transform(storedWatchEntity, watch.getUser());
    }

    @Override public Watch getCurrentWatching(ErrorCallback callback) {
        throw new RuntimeException(
          "Method not implemented. It's in the interface for compatibility with old repositories.");
    }

    @Override public Watch getCurrentWatching() {
        WatchEntity watchEntity = localWatchDataSource.getWatching(sessionRepository.getCurrentUserId());
        return watchEntityMapper.transform(watchEntity, sessionRepository.getCurrentUser());
    }

    @Override public Watch getCurrentVisibleWatch() {
        //TODO mock!!!
        Watch watch = new Watch();
        watch.setWatching(true);
        watch.setUser(sessionRepository.getCurrentUser());
        watch.setIdEvent(305596L);
        watch.setVisible(true);
        watch.setNotificaticationsEnabled(true);
        watch.setUserStatus("Mock watch");
        return watch;
    }
}
