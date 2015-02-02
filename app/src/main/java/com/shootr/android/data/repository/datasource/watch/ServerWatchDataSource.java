package com.shootr.android.data.repository.datasource.watch;

import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServerWatchDataSource implements WatchDataSource {

    private final ShootrService service;
    private final SessionRepository sessionRepository;

    @Inject public ServerWatchDataSource(ShootrService service, SessionRepository sessionRepository) {
        this.service = service;
        this.sessionRepository = sessionRepository;
    }

    @Override public WatchEntity getWatch(Long idEvent, Long idUser) {
        try {
            return service.getWatchStatus(idUser, idEvent);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public WatchEntity putWatch(WatchEntity watchEntity) {
        try {
            return service.setWatchStatus(watchEntity);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<WatchEntity> putWatches(List<WatchEntity> watchEntities) {
        //TODO agrupar en 1 operación
        for (WatchEntity watchEntity : watchEntities) {
            putWatch(watchEntity);
        }
        return watchEntities;
    }

    @Override public WatchEntity getWatching(Long userId) {
        throw new RuntimeException("Method not implemented in service: getWatching by idUser with status=1");
    }

    @Override public WatchEntity getVisible(Long userId) {
        try {
            return service.getVisibleWatch(userId);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<WatchEntity> getWatchesForUsersAndEvent(List<Long> users, Long idEvent) {
        try {
            return service.getWatchesFromUsersByEvent(idEvent, users);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<WatchEntity> getWatchesFromUsers(List<Long> userIds) {
        try {
            return service.getWatchesFromUsersAndMe(userIds, sessionRepository.getCurrentUserId());
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public void deleteAllWatchesNotPending() {
        throw new RuntimeException("Method not implemented in service");
    }

    @Override public List<WatchEntity> getEntitiesNotSynchronized() {
        throw new RuntimeException("Server DataSource can't access synchronization fields");
    }

}
