package com.shootr.android.data.repository.local;

import android.support.v4.util.LongSparseArray;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.data.mapper.WatchEntityMapper;
import com.shootr.android.data.repository.sync.SyncableWatchEntityFactory;
import com.shootr.android.data.repository.datasource.LocalDataSource;
import com.shootr.android.data.repository.datasource.watch.WatchDataSource;
import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.repository.ErrorCallback;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.WatchRepository;
import java.util.ArrayList;
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

    @Override public List<Watch> getWatchesForUsersAndEvent(List<User> users, Long idEvent) {
        List<WatchEntity> watchEntities = localWatchDataSource.getWatchesForUsersAndEvent(ids(users), idEvent);
        return entitiesToDomain(watchEntities, users);
    }

    @Override public List<Watch> getWatchesFromUsers(List<User> userIds) {
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
        WatchEntity watchEntity = localWatchDataSource.getVisible(sessionRepository.getCurrentUserId());
        return watchEntityMapper.transform(watchEntity, sessionRepository.getCurrentUser());
    }

    private List<Long> ids(List<User> users) {
        List<Long> ids = new ArrayList<>();
        for (User user : users) {
            ids.add(user.getIdUser());
        }
        return ids;
    }

    private List<Watch> entitiesToDomain(List<WatchEntity> watchEntities, List<User> users) {
        LongSparseArray<User> usersSparseArray = userListToSparseArray(users);
        List<Watch> watches = new ArrayList<>(watchEntities.size());
        for (WatchEntity watchEntity : watchEntities) {
            User user = usersSparseArray.get(watchEntity.getIdUser());
            if (user != null) {
                watches.add(watchEntityMapper.transform(watchEntity, user));
            }
        }
        return watches;
    }

    private LongSparseArray<User> userListToSparseArray(List<User> users) {
        LongSparseArray<User> sparseArray = new LongSparseArray<>(users.size());
        for (User user : users) {
            sparseArray.put(user.getIdUser(), user);
        }
        return sparseArray;
    }
}
