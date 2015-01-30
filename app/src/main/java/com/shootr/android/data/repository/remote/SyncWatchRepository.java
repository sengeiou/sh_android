package com.shootr.android.data.repository.remote;

import android.support.v4.util.LongSparseArray;
import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.data.mapper.WatchEntityMapper;
import com.shootr.android.data.repository.datasource.Cached;
import com.shootr.android.data.repository.datasource.watch.WatchDataSource;
import com.shootr.android.data.repository.sync.SyncTrigger;
import com.shootr.android.data.repository.sync.SyncableRepository;
import com.shootr.android.data.repository.sync.SyncableWatchEntityFactory;
import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.repository.ErrorCallback;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.repository.WatchRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import timber.log.Timber;

public class SyncWatchRepository implements WatchRepository, SyncableRepository {

    //region Dependencies
    private final UserRepository localUserRepository;
    private final EventRepository remoteEventRepository;
    private final SessionRepository sessionRepository;
    private final WatchDataSource localWatchDataSource;
    private final WatchDataSource remoteWatchDataSource;
    private final WatchDataSource cachedWatchDataSource;
    private final WatchEntityMapper watchEntityMapper;
    private final SyncableWatchEntityFactory syncableWatchEntityFactory;
    private final SyncTrigger syncTrigger;

    @Inject public SyncWatchRepository(@Local UserRepository localUserRepository,
      @Remote EventRepository remoteEventRepository, SessionRepository sessionRepository,
      @Local WatchDataSource localWatchDataSource, @Remote WatchDataSource remoteWatchDataSource,
      @Cached WatchDataSource cachedWatchDataSource, WatchEntityMapper watchEntityMapper,
      SyncTrigger syncTrigger, SyncableWatchEntityFactory syncableWatchEntityFactory) {
        this.localUserRepository = localUserRepository;
        this.remoteEventRepository = remoteEventRepository;
        this.sessionRepository = sessionRepository;
        this.localWatchDataSource = localWatchDataSource;
        this.remoteWatchDataSource = remoteWatchDataSource;
        this.cachedWatchDataSource = cachedWatchDataSource;
        this.watchEntityMapper = watchEntityMapper;
        this.syncTrigger = syncTrigger;
        this.syncableWatchEntityFactory = syncableWatchEntityFactory;
    }
    //endregion

    //region Data access methods
    @Deprecated //?
    @Override public Watch getWatchForUserAndEvent(User user, Long idEvent) {
        WatchEntity watchEntity = remoteWatchDataSource.getWatch(idEvent, user.getIdUser());
        //TODO update local
        return watchEntityMapper.transform(watchEntity, user);
    }

    @Override public synchronized List<Watch> getWatchesForUsersAndEvent(List<User> users, Long idEvent) {
        List<Long> userIds = ids(users);
        List<WatchEntity> cachedWatches = cachedWatchDataSource.getWatchesForUsersAndEvent(userIds, idEvent);
        if (cachedWatches != null) {
            return entitiesToDomain(cachedWatches, users);
        } else {
            updateAllWatchesFromRemote();
            return entitiesToDomain(localWatchDataSource.getWatchesForUsersAndEvent(userIds, idEvent), users);
        }
    }

    @Override public synchronized List<Watch> getWatchesFromUsers(List<User> users) {
        List<Long> userIds = ids(users);
        List<WatchEntity> cachedWatches = cachedWatchDataSource.getWatchesFromUsers(userIds);
        if (cachedWatches != null) {
            return entitiesToDomain(cachedWatches, users);
        } else {
            updateAllWatchesFromRemote();
            return entitiesToDomain(localWatchDataSource.getWatchesFromUsers(userIds), users);
        }
    }

    @Deprecated @Override public void putWatch(Watch watch, WatchCallback callback) {
        throw new RuntimeException(
          "Method not implemented. It is in the interface for compatibility with old repository implementations");
    }

    @Override public Watch putWatch(Watch watch) {
        WatchEntity currentOrNewWatchEntity = syncableWatchEntityFactory.currentOrNewEntity(watch);
        try {
            WatchEntity remoteWatchEntity = remoteWatchDataSource.putWatch(currentOrNewWatchEntity);
            markEntitySynchronized(remoteWatchEntity);
            localWatchDataSource.putWatch(remoteWatchEntity);
            return watchEntityMapper.transform(remoteWatchEntity, watch.getUser());
        } catch (ServerCommunicationException e) {
            queueUpload(currentOrNewWatchEntity, e);
        }
        return watchEntityMapper.transform(currentOrNewWatchEntity, watch.getUser());
    }

    @Override public synchronized Watch getCurrentWatching(ErrorCallback callback) {
        throw new RuntimeException(
          "Method not implemented. It is in the interface for compatibility with old repository implementations");
    }

    @Override public synchronized Watch getCurrentWatching() {
        throw new RuntimeException("Method not implemented yet!");
    }

    @Override public synchronized Watch getCurrentVisibleWatch() {
        long currentUserId = sessionRepository.getCurrentUserId();
        WatchEntity cachedVisible = cachedWatchDataSource.getVisible(currentUserId);
        User currentUser = sessionRepository.getCurrentUser();
        if (cachedVisible != null) {
            return watchEntityMapper.transform(cachedVisible, currentUser);
        } else {
            updateAllWatchesFromRemote();
            return watchEntityMapper.transform(localWatchDataSource.getVisible(currentUserId), currentUser);
        }
    }
    //endregion

    private synchronized void updateAllWatchesFromRemote() {
        syncTrigger.triggerSync();
        List<Long> users = ids(localUserRepository.getPeople());
        users.add(sessionRepository.getCurrentUserId());
        List<WatchEntity> remoteWatches = remoteWatchDataSource.getWatchesFromUsers(users);
        ensureEventsExist(eventIds(remoteWatches));
        markEntitiesSynchronized(remoteWatches);
        localWatchDataSource.deleteAllWatchesNotPending();
        localWatchDataSource.putWatches(remoteWatches);
        cachedWatchDataSource.putWatches(remoteWatches);
    }

    private void ensureEventsExist(List<Long> eventIds) {
        // Little decoupling violation. This function depends on the EventRepository storing the events when get is called
        remoteEventRepository.getEventsByIds(eventIds);
    }

    private List<Long> eventIds(List<WatchEntity> watchEntities) {
        Set<Long> ids = new HashSet<>();
        for (WatchEntity watchEntity : watchEntities) {
            ids.add(watchEntity.getIdEvent());
        }
        return new ArrayList<>(ids);
    }

    //region Synchronization
    private void queueUpload(WatchEntity watchEntity, ServerCommunicationException reason) {
        Timber.w(reason, "Watch upload queued: idUser %d, idEvent %d", watchEntity.getIdUser(),
          watchEntity.getIdEvent());
        prepareEntityForSynchronization(watchEntity);
        syncTrigger.notifyNeedsSync(this);
    }

    private void prepareEntityForSynchronization(WatchEntity watchEntity) {
        if (!isReadyForSync(watchEntity)) {
            watchEntity.setCsysSynchronized(Synchronized.SYNC_UPDATED);
        }
        localWatchDataSource.putWatch(watchEntity);
    }

    private boolean isReadyForSync(WatchEntity watchEntity) {
        return Synchronized.SYNC_UPDATED.equals(watchEntity.getCsysSynchronized()) || Synchronized.SYNC_NEW.equals(
          watchEntity.getCsysSynchronized()) || Synchronized.SYNC_DELETED.equals(watchEntity.getCsysSynchronized());
    }

    @Override public void dispatchSync() {
        List<WatchEntity> notSynchronized = localWatchDataSource.getEntitiesNotSynchronized();
        for (WatchEntity watchEntity : notSynchronized) {
            WatchEntity synchedEntity = remoteWatchDataSource.putWatch(watchEntity);
            synchedEntity.setCsysSynchronized(Synchronized.SYNC_SYNCHRONIZED);
            localWatchDataSource.putWatch(synchedEntity);
            Timber.d("Synchronized Watch entity: idEvent=%d; idUser=%d", watchEntity.getIdEvent(),
              watchEntity.getIdUser());
        }
    }
    //endregion

    //region Utils
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

    private void markEntitiesSynchronized(List<WatchEntity> entities) {
        for (WatchEntity entity : entities) {
            markEntitySynchronized(entity);
        }
    }

    private void markEntitySynchronized(WatchEntity entity) {
        entity.setCsysSynchronized(Synchronized.SYNC_SYNCHRONIZED);
    }
    //endregion
}
