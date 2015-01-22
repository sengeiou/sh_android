package com.shootr.android.data.repository;

import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.data.mapper.WatchEntityMapper;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.exception.RepositoryException;
import com.shootr.android.domain.repository.ErrorCallback;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.WatchRepository;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.NetworkConnection;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

@Deprecated
public class WatchRepositoryImpl implements WatchRepository {

    private final ShootrService shootrService;
    private final NetworkConnection networkConnection;
    private final WatchManager watchManager;
    private final FollowManager followManager;
    private final WatchEntityMapper watchEntityMapper;
    private final SessionRepository sessionRepository;

    @Inject public WatchRepositoryImpl(ShootrService shootrService, NetworkConnection networkConnection,
      WatchManager watchManager, FollowManager followManager, WatchEntityMapper watchEntityMapper,
      SessionRepository sessionRepository) {
        this.shootrService = shootrService;
        this.networkConnection = networkConnection;
        this.watchManager = watchManager;
        this.followManager = followManager;
        this.watchEntityMapper = watchEntityMapper;
        this.sessionRepository = sessionRepository;
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

    @Override public Watch getWatchForUserAndEvent(User user, Long idEvent) {
        throw new RuntimeException("Method not implemented. It is declared for the new type of synchronous repository");
    }

    @Override public Watch getCurrentWatching(ErrorCallback callback) {
        WatchEntity watching = watchManager.getWatching(sessionRepository.getCurrentUserId());
        if (watching == null) {
            //TODO ask server
        }
        if (watching != null) {
            return watchEntityMapper.transform(watching, sessionRepository.getCurrentUser());
        } else {
            return null;
        }
    }

    @Override public Integer getAllWatchesCount() {
        try {
            List<Long> following = followManager.getUserFollowingIds(sessionRepository.getCurrentUserId());
            following.add(sessionRepository.getCurrentUserId());
            List<WatchEntity> allWatches = watchManager.getWatchesNotEndedFromUsers(following);

            return countEvents(allWatches);
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    private Integer countEvents(List<WatchEntity> allWatches) {
        Set<Long> eventIds = new HashSet<>();
        for (WatchEntity watch : allWatches) {
            if (watch.getStatus() == WatchEntity.STATUS_WATCHING) {
                eventIds.add(watch.getIdEvent());
            }
        }
        return eventIds.size();
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

    @Override public void putWatch(Watch watch, WatchCallback callback) {
        WatchEntity currentWatchEntity = watchManager.getWatchByKeys(watch.getUser().getIdUser(), watch.getIdEvent());
        WatchEntity finalWatchEntity = updateEntityValues(currentWatchEntity, watch);

        watchManager.saveWatch(finalWatchEntity);
        Watch finalWatch = watchEntityMapper.transform(finalWatchEntity, watch.getUser());
        callback.onLoaded(finalWatch);

        sendWatchToServer(finalWatchEntity, callback, watch.getUser());
    }

    @Override public Watch putWatch(Watch watch) {
        throw new RuntimeException("Method not implemented. It is declared for the new type of synchronous repository");
    }

    private void sendWatchToServer(WatchEntity finalWatchEntity, WatchCallback callback, @Deprecated User user) {
        if (networkConnection.isConnected()) {
            try {
                WatchEntity receivedWatchEntity = shootrService.setWatchStatus(finalWatchEntity);
                Watch receivedWatch = watchEntityMapper.transform(receivedWatchEntity, user);
                callback.onLoaded(receivedWatch);
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
            currentWatchEntity.setPlace(watch.getUserStatus());
            currentWatchEntity.setVisible(watch.isVisible());
            currentWatchEntity.setCsysSynchronized(Synchronized.SYNC_UPDATED);
            currentWatchEntity.setCsysModified(new Date());
            currentWatchEntity.setCsysRevision(currentWatchEntity.getCsysRevision() + 1);
        } else {
            currentWatchEntity = watchEntityMapper.transform(watch);
        }
        return currentWatchEntity;
    }
}
