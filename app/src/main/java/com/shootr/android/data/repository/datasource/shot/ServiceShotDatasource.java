package com.shootr.android.data.repository.datasource.shot;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.EventTimelineParameters;
import com.shootr.android.domain.ShotType;
import com.shootr.android.domain.bus.BusPublisher;
import com.shootr.android.domain.bus.WatchUpdateRequest;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Override public List<ShotEntity> getShotsForEventTimeline(EventTimelineParameters parameters) {
        try {
            return filterSyncShots(shootrService.getEventShotsByParameters(parameters));
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override
    public List<ShotEntity> getShotsForActivityTimeline(ActivityTimelineParameters parameters) {
        try {
            return filterSyncShots(shootrService.getActivityShotsByParameters(parameters));
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

    @Override public List<ShotEntity> getReplies(String shotId) {
        try {
            return shootrService.getRepliesToShot(shotId);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public Integer getEventMediaShotsByUserCount(String idEvent, String idUser) {
        try {
            return shootrService.getEventMediaCount(idEvent, idUser);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<ShotEntity> getEventMediaShotsByUser(String idEvent, String userId) {
        try {
            return shootrService.getEventMedia(idEvent, userId);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    private List<ShotEntity> filterSyncShots(List<ShotEntity> shotEntities) {
        notifySyncTrigger(shotEntities);
        List<ShotEntity> filtered = new ArrayList<>();
        for (ShotEntity shotEntity : shotEntities) {
            if (!isHiddenShot(shotEntity)) {
                filtered.add(shotEntity);
            }
        }
        return filtered;
    }

    private boolean isHiddenShot(ShotEntity shotEntity) {
        String type = shotEntity.getType();
        return Arrays.asList(ShotType.TYPES_HIDDEN).contains(type);
    }

    private void notifySyncTrigger(List<ShotEntity> newShots) {
        String currentUserId = sessionRepository.getCurrentUserId();
        for (ShotEntity newShot : newShots) {
            if (isSyncTriggerShot(newShot)
              && isNewerThanLastTrigger(newShot)
              && !newShot.getIdUser().equals(currentUserId)) {
                busPublisher.post(new WatchUpdateRequest.Event());
                lastTriggerDate = newShot.getCsysBirth().getTime();
                break;
            }
        }
    }

    private boolean isSyncTriggerShot(ShotEntity newShot) {
        String type = newShot.getType();
        return Arrays.asList(ShotType.TYPES_SYNC_TRIGGER).contains(type);
    }

    private boolean isNewerThanLastTrigger(ShotEntity newShot) {
        return newShot.getCsysBirth().getTime() > lastTriggerDate;
    }
}