package com.shootr.android.data.repository.datasource.shot;

import com.shootr.android.data.api.entity.ShotApiEntity;
import com.shootr.android.data.api.entity.mapper.ShotApiEntityMapper;
import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.service.ShotApiService;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.domain.EventTimelineParameters;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceShotDatasource implements ShotDataSource {

    private final ShootrService shootrService;
    private final ShotApiService shotApiService;
    private final ShotApiEntityMapper shotApiEntityMapper;

    @Inject public ServiceShotDatasource(ShootrService shootrService,
      ShotApiService shotApiService,
      ShotApiEntityMapper shotApiEntityMapper) {
        this.shootrService = shootrService;
        this.shotApiService = shotApiService;
        this.shotApiEntityMapper = shotApiEntityMapper;
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
            List<ShotApiEntity> shots = shotApiService.getEventTimeline(parameters.getEventId(),
              parameters.getLimit(),
              parameters.getSinceDate(),
              parameters.getMaxDate(),
              parameters.getIncludeNiceShots(),
              parameters.getMaxNiceShotsIncluded(),
              parameters.getCurrentUserId());
            return shotApiEntityMapper.transform(shots);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public ShotEntity getShot(String shotId) {
        try {
            return shotApiEntityMapper.transform(shotApiService.getShot(shotId));
        } catch (IOException | ApiException error) {
            throw new ServerCommunicationException(error);
        }
    }

    @Override public List<ShotEntity> getReplies(String shotId) {
        try {
            return shootrService.getRepliesToShot(shotId);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public Integer getEventMediaShotsCount(String idEvent, List<String> idUsers) {
        try {
            return shootrService.getEventMediaShotsCount(idEvent, idUsers);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<ShotEntity> getEventMediaShots(String idEvent, List<String> userIds) {
        try {
            return shootrService.getEventMediaShots(idEvent, userIds);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override
    public List<ShotEntity> getShotsFromUser(String idUser, Integer limit) {
        try {
            List<ShotApiEntity> userApiShots = shotApiService.getShotsFromUser(idUser, limit);
            return shotApiEntityMapper.transform(userApiShots);
        } catch (ApiException | IOException error) {
            throw new ServerCommunicationException(error);
        }
    }
}