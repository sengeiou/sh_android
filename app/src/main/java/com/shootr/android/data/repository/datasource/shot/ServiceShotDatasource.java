package com.shootr.android.data.repository.datasource.shot;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.domain.TimelineParameters;
import com.shootr.android.domain.bus.BusPublisher;
import com.shootr.android.domain.bus.WatchUpdateRequest;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ServiceShotDatasource implements ShotDataSource {

    private final ShootrService shootrService;
    private final BusPublisher busPublisher;

    @Inject public ServiceShotDatasource(ShootrService shootrService, BusPublisher busPublisher) {
        this.shootrService = shootrService;
        this.busPublisher = busPublisher;
    }

    @Override public ShotEntity putShot(ShotEntity shotEntity) {
        try {
            return shootrService.postNewShotWithImage(shotEntity);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public void putShots(List<ShotEntity> shotEntities) {
        for (ShotEntity shotEntity : shotEntities) {
            putShot(shotEntity);
        }
    }

    @Override public List<ShotEntity> getShotsForTimeline(TimelineParameters parameters) {
        try {
            return filterSyncShots(shootrService.getShotsByParameters(parameters));
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    private List<ShotEntity> filterSyncShots(List<ShotEntity> shotEntities) {
        notifySyncTrigger(shotEntities);
        List<ShotEntity> filtered = new ArrayList<>();
        for (ShotEntity shotEntity : shotEntities) {
            if (shotEntity.getType() != ShotEntity.TYPE_TRIGGER_SYNC_NOT_SHOW) {
                filtered.add(shotEntity);
            }
        }
        return filtered;
    }

    private void notifySyncTrigger(List<ShotEntity> newShots) {
        for (ShotEntity newShot : newShots) {
            if (newShot.getType() == ShotEntity.TYPE_TRIGGER_SYNC || newShot.getType() == ShotEntity.TYPE_TRIGGER_SYNC_NOT_SHOW) {
                busPublisher.post(new WatchUpdateRequest.Event());
                break;
            }
        }
    }
}