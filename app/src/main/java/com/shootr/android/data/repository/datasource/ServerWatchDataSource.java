package com.shootr.android.data.repository.datasource;

import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServerWatchDataSource implements WatchDataSource {

    private final ShootrService service;

    @Inject public ServerWatchDataSource(ShootrService service) {
        this.service = service;
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

    @Override public WatchEntity getWatching(Long userId) {
        throw new RuntimeException("Method not implemented in service: getWatching by idUser with status=1");
    }

    @Override public List<WatchEntity> getEntitiesNotSynchronized() {
        throw new RuntimeException("Server DataSource can't access synchronization fields");
    }
}