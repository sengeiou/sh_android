package com.shootr.android.data.repository;

import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.data.mapper.WatchEntityMapper;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.exception.RepositoryException;
import com.shootr.android.domain.repository.ErrorCallback;
import com.shootr.android.domain.repository.WatchRepository;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.NetworkConnection;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import java.io.IOException;
import javax.inject.Inject;

public class WatchRepositoryImpl implements WatchRepository {

    private final ShootrService shootrService;
    private final NetworkConnection networkConnection;
    private final WatchManager watchManager;
    private final WatchEntityMapper watchEntityMapper;

    @Inject public WatchRepositoryImpl(ShootrService shootrService, NetworkConnection networkConnection,
      WatchManager watchManager, WatchEntityMapper watchEntityMapper) {
        this.shootrService = shootrService;
        this.networkConnection = networkConnection;
        this.watchManager = watchManager;
        this.watchEntityMapper = watchEntityMapper;
    }

    @Override public Watch getWatchForUserAndEvent(User user, Long idEvent, ErrorCallback callback) {
        WatchEntity watchEntity = watchManager.getWatchByKeys(user.getIdUser(), idEvent);
        if (watchEntity == null) {
            watchEntity = getWatchEntityByKeysFromServer(user, idEvent, callback);
        }

        if (watchEntity != null) {
            return watchEntityMapper.transform(watchEntity, user);
        } else {
            return null;
        }
    }

    private WatchEntity getWatchEntityByKeysFromServer(User user, Long idEvent, ErrorCallback callback) {
        if (networkConnection.isConnected()) {
            try {
                return shootrService.getWatchStatus(user.getIdUser(), idEvent);
            } catch (IOException e) {
                callback.onError(new RepositoryException(e));
            }
        } else {
            callback.onError(new ConnectionNotAvailableEvent());
        }
        return null;
    }

    @Override public void putWatch(Watch watch, ErrorCallback callback) {
        WatchEntity currentWatchEntity = watchManager.getWatchByKeys(watch.getUser().getIdUser(), watch.getIdEvent());
        WatchEntity finalWatchEntity = updateEntityValues(currentWatchEntity, watch);

        watchManager.saveWatch(finalWatchEntity);
        sendWatchToServer(finalWatchEntity, callback);
    }

    private void sendWatchToServer(WatchEntity finalWatchEntity, ErrorCallback callback) {
        if (networkConnection.isConnected()) {
            try {
                shootrService.setWatchStatus(finalWatchEntity);
            } catch (IOException e) {
                callback.onError(new RepositoryException(e));
            }
        } else {
            callback.onError(new ConnectionNotAvailableEvent());
        }
    }

    private WatchEntity updateEntityValues(WatchEntity currentWatchEntity, Watch watch) {
        if (currentWatchEntity != null) {
            currentWatchEntity.setNotification(watch.isNotificaticationsEnabled() ? 1 : 0);
            currentWatchEntity.setStatus(watch.isWatching() ? 1L : 2L);
            if (watch.getUserStatus() != null) {
                currentWatchEntity.setPlace(watch.getUserStatus());
            }
            currentWatchEntity.setCsysSynchronized(Synchronized.SYNC_UPDATED);
        } else {
            currentWatchEntity = watchEntityMapper.transform(watch);
        }
        return currentWatchEntity;
    }
}
