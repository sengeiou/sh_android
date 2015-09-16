package com.shootr.android.data.repository.datasource.shot;

import com.shootr.android.data.api.entity.ShotApiEntity;
import com.shootr.android.data.api.entity.mapper.ShotApiEntityMapper;
import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.service.ShotApiService;
import com.shootr.android.data.entity.ShotDetailEntity;
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
              parameters.getMaxDate());
            return shotApiEntityMapper.transform(shots);
        } catch (ApiException | IOException e) {
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

    @Override public List<ShotEntity> getStreamMediaShots(String idStream, List<String> userIds, Long maxTimestamp) {
        try {
            List<ShotApiEntity> mediaApiShots = shotApiService.getMediaShots(idStream, maxTimestamp);
            return shotApiEntityMapper.transform(mediaApiShots);
        } catch (IOException | ApiException e) {
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

    @Override
    public ShotDetailEntity getShotDetail(String idShot) {
        try {
            ShotApiEntity shotApiEntity = shotApiService.getShotDetail(idShot);

            ShotEntity shotEntity = shotApiEntityMapper.transform(shotApiEntity);
            List<ShotEntity> repliesEntities = shotApiEntityMapper.transform(shotApiEntity.getReplies());
            ShotEntity parentEntity = shotApiEntityMapper.transform(shotApiEntity.getParent());

            ShotDetailEntity shotDetailEntity = new ShotDetailEntity();
            shotDetailEntity.setShot(shotEntity);
            shotDetailEntity.setReplies(repliesEntities);
            shotDetailEntity.setParentShot(parentEntity);
            return shotDetailEntity;
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<ShotEntity> getAllShotsFromUser(String userId) {
        try {
            List<ShotApiEntity> allShotsFromUser = shotApiService.getAllShotsFromUser(userId);
            return shotApiEntityMapper.transform(allShotsFromUser);
        } catch (ApiException | IOException error) {
            throw new ServerCommunicationException(error);
        }
    }

    @Override public List<ShotEntity> getAllShotsFromUserAndDate(String userId, Long currentOldestDate) {
        try {
            List<ShotApiEntity> allShotsFromUserAndDate = shotApiService.getAllShotsFromUserWithMaxDate(userId,
              currentOldestDate);
            return shotApiEntityMapper.transform(allShotsFromUserAndDate);
        } catch (ApiException | IOException error) {
            throw new ServerCommunicationException(error);
        }
    }

    @Override public void shareShot(String idShot) {
        try {
            shotApiService.shareShot(idShot);
        } catch (ApiException | IOException error) {
            throw new ServerCommunicationException(error);
        }
    }
}