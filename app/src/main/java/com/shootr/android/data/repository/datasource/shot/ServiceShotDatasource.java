package com.shootr.android.data.repository.datasource.shot;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.domain.TimelineParameters;
import com.shootr.android.domain.bus.BusPublisher;
import com.shootr.android.domain.bus.WatchUpdateRequest;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ServiceShotDatasource implements ShotDataSource {

    private final ShootrService shootrService;
    private final BusPublisher busPublisher;
    private final SessionRepository sessionRepository;
    private long lastTriggerDate;

    @Inject public ServiceShotDatasource(ShootrService shootrService, BusPublisher busPublisher,
      SessionRepository sessionRepository) {
        this.shootrService = shootrService;
        this.busPublisher = busPublisher;
        this.sessionRepository = sessionRepository;
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

    @Override public ShotEntity getShot(String shotId) {
        try {
            return shootrService.getShotById(shotId);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<ShotEntity> getReplies(Long shotId) {
        try {
            return shootrService.getRepliesToShot(shotId);
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
        String currentUserId = sessionRepository.getCurrentUserId();
        for (ShotEntity newShot : newShots) {
            if ((newShot.getType() == ShotEntity.TYPE_TRIGGER_SYNC
              || newShot.getType() == ShotEntity.TYPE_TRIGGER_SYNC_NOT_SHOW)
              && isNewerThanLastTrigger(newShot)
              && newShot.getIdUser() != currentUserId) {
                busPublisher.post(new WatchUpdateRequest.Event());
                lastTriggerDate = newShot.getCsysBirth().getTime();
                break;
            }
        }
    }

    private boolean isNewerThanLastTrigger(ShotEntity newShot) {
        return newShot.getCsysBirth().getTime() > lastTriggerDate;
    }
}