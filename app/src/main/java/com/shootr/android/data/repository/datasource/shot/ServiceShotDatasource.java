package com.shootr.android.data.repository.datasource.shot;

import com.shootr.android.data.api.entity.ShotApiEntity;
import com.shootr.android.data.api.entity.mapper.ShotApiEntityMapper;
import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.service.ShotApiService;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.domain.ShotType;
import com.shootr.android.domain.StreamTimelineParameters;
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

    @Override public List<ShotEntity> getShotsForStreamTimeline(StreamTimelineParameters parameters) {
        try {
            List<ShotApiEntity> shots = shotApiService.getStreamTimeline(parameters.getStreamId(),
              parameters.getLimit(),
              parameters.getSinceDate(),
              parameters.getMaxDate(),
              parameters.getIncludeNiceShots(),
              parameters.getMaxNiceShotsIncluded());
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
            ShotApiEntity shot = shotApiService.getShotWithReplies(shotId);
            List<ShotApiEntity> replies = shot.getReplies();
            return shotApiEntityMapper.transform(replies);
        } catch (IOException | ApiException error) {
            throw new ServerCommunicationException(error);
        }
    }

    @Override public Integer getStreamMediaShotsCount(String idStream, List<String> idUsers) {
        try {
            return shootrService.getStreamMediaShotsCount(idStream, idUsers);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<ShotEntity> getStreamMediaShots(String idStream, List<String> userIds) {
        try {
            return shootrService.getStreamMediaShots(idStream, userIds);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override
    public List<ShotEntity> getShotsFromUser(String idUser, Integer limit) {
        try {
            List<ShotApiEntity> userApiShots = shotApiService.getShotsFromUser(idUser, limit, ShotType.TYPES_SHOWN);
            return shotApiEntityMapper.transform(userApiShots);
        } catch (ApiException | IOException error) {
            throw new ServerCommunicationException(error);
        }
    }

    @Override public List<ShotEntity> getAllShotsFromUser(String userId) {
        try {
            return shotApiService.getAllShotsFromUser(userId);
        } catch (ApiException | IOException error) {
            throw new ServerCommunicationException(error);
        }
    }
}