package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.data.mapper.WatchEntityMapper;
import com.shootr.android.data.repository.sync.SyncTrigger;
import com.shootr.android.data.repository.sync.SyncableWatchEntityFactory;
import com.shootr.android.data.repository.datasource.LocalDataSource;
import com.shootr.android.data.repository.datasource.RemoteDataSource;
import com.shootr.android.data.repository.datasource.WatchDataSource;
import com.shootr.android.data.repository.sync.SyncableRepository;
import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.repository.ErrorCallback;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.WatchRepository;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class SyncWatchRepository implements WatchRepository, SyncableRepository {

    //region Dependencies
    private final SessionRepository sessionRepository;
    private final WatchDataSource localWatchDataSource;
    private final WatchDataSource remoteWatchDataSource;
    private final WatchEntityMapper watchEntityMapper;
    private final SyncableWatchEntityFactory syncableWatchEntityFactory;
    private final SyncTrigger syncTrigger;

    @Inject public SyncWatchRepository(SessionRepository sessionRepository, @LocalDataSource WatchDataSource localWatchDataSource,
      @RemoteDataSource WatchDataSource remoteWatchDataSource, WatchEntityMapper watchEntityMapper, SyncTrigger syncTrigger,
      SyncableWatchEntityFactory syncableWatchEntityFactory) {
        this.sessionRepository = sessionRepository;
        this.localWatchDataSource = localWatchDataSource;
        this.remoteWatchDataSource = remoteWatchDataSource;
        this.watchEntityMapper = watchEntityMapper;
        this.syncTrigger = syncTrigger;
        this.syncableWatchEntityFactory = syncableWatchEntityFactory;
    }
    //endregion

    //region Data access methods
    @Deprecated @Override public Watch getWatchForUserAndEvent(User user, Long idEvent, ErrorCallback callback) {
        return getWatchForUserAndEvent(user, idEvent);
    }

    @Override public Watch getWatchForUserAndEvent(User user, Long idEvent) {
        WatchEntity watchEntity = remoteWatchDataSource.getWatch(idEvent, user.getIdUser());
        //TODO update local
        return watchEntityMapper.transform(watchEntity, user);
    }

    @Override public List<Watch> getWatchesFromUsersAndEvent(List<User> users, Long idEvent) {
        //TODO Mock!!!
        WatchEntity watchEntity = localWatchDataSource.getWatch(idEvent, users.get(0).getIdUser());
        Watch watch = watchEntityMapper.transform(watchEntity, users.get(0));
        return Arrays.asList(watch, watch);
    }

    @Override public List<Watch> getWatchesFromUsers(List<Long> userIds) {
        //TODO Mock!!
        return Arrays.asList(getCurrentVisibleWatch());
    }

    @Deprecated @Override public void putWatch(Watch watch, WatchCallback callback) {
        throw new RuntimeException(
          "Method not implemented. It is in the interface for compatibility with old repository implementations");
    }

    @Override public Watch putWatch(Watch watch) {
        WatchEntity currentOrNewWatchEntity = syncableWatchEntityFactory.currentOrNewEntity(watch);
        try {
            WatchEntity remoteWatchEntity = remoteWatchDataSource.putWatch(currentOrNewWatchEntity);
            localWatchDataSource.putWatch(remoteWatchEntity);
            return watchEntityMapper.transform(remoteWatchEntity, watch.getUser());
        } catch (ServerCommunicationException e) {
            queueUpload(currentOrNewWatchEntity, e);
        }
        return watchEntityMapper.transform(currentOrNewWatchEntity, watch.getUser());
    }

    @Override public Watch getCurrentWatching(ErrorCallback callback) {
        throw new RuntimeException(
          "Method not implemented. It is in the interface for compatibility with old repository implementations");
    }

    @Override public Watch getCurrentWatching() {
        throw new RuntimeException("Method not implemented yet!");
    }

    @Override public Watch getCurrentVisibleWatch() {
        //TODO mock!!!
        Watch watch = new Watch();
        watch.setWatching(true);
        watch.setUser(sessionRepository.getCurrentUser());
        watch.setIdEvent(305596L);
        watch.setVisible(true);
        watch.setNotificaticationsEnabled(true);
        watch.setUserStatus("Mock watch online");
        try {
            Thread.sleep(900);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return watch;
    }

    //endregion

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
        return Synchronized.SYNC_UPDATED.equals(watchEntity.getCsysSynchronized()) || Synchronized.SYNC_NEW.equals(watchEntity.getCsysSynchronized()) || Synchronized.SYNC_DELETED.equals(watchEntity.getCsysSynchronized());
    }

    @Override public void dispatchSync() {
        List<WatchEntity> notSynchronized = localWatchDataSource.getEntitiesNotSynchronized();
        for (WatchEntity watchEntity : notSynchronized) {
            WatchEntity synchedEntity = remoteWatchDataSource.putWatch(watchEntity);
            synchedEntity.setCsysSynchronized(Synchronized.SYNC_SYNCHRONIZED);
            localWatchDataSource.putWatch(synchedEntity);
            Timber.d("Synchronized Watch entity: idEvent=%d; idUser=%d", watchEntity.getIdEvent(), watchEntity.getIdUser());
        }
    }
    //endregion
}
