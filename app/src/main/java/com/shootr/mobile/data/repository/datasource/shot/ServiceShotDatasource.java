package com.shootr.mobile.data.repository.datasource.shot;

import com.shootr.mobile.data.api.entity.ShotApiEntity;
import com.shootr.mobile.data.api.entity.mapper.ShotApiEntityMapper;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.exception.ErrorInfo;
import com.shootr.mobile.data.api.service.ShotApiService;
import com.shootr.mobile.data.entity.ShotDetailEntity;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.domain.ShotType;
import com.shootr.mobile.domain.StreamTimelineParameters;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShotNotFoundException;
import com.shootr.mobile.domain.exception.StreamRemovedException;
import com.shootr.mobile.domain.exception.UserBannedException;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceShotDatasource implements ShotDataSource {

    private final ShotApiService shotApiService;
    private final ShotApiEntityMapper shotApiEntityMapper;

    @Inject public ServiceShotDatasource(ShotApiService shotApiService,
      ShotApiEntityMapper shotApiEntityMapper) {
        this.shotApiService = shotApiService;
        this.shotApiEntityMapper = shotApiEntityMapper;
    }

    @Override public ShotEntity putShot(ShotEntity shotEntity) {
        try {
            return shotApiService.postNewShot(shotEntity);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        } catch (ApiException e) {
            if (e.getErrorInfo() == ErrorInfo.ResourceNotFoundException) {
                throw new ShotNotFoundException(e);
            } else if (e.getErrorInfo() == ErrorInfo.StreamRemovedForbiddenRequestException) {
                throw new StreamRemovedException(e);
            } else if (e.getErrorInfo() == ErrorInfo.UserBannedForbiddenRequestException) {
                throw new UserBannedException(e);
            } else {
                throw new ServerCommunicationException(e);
            }
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

    @Override public ShotEntity getShot(String shotId) throws ShotNotFoundException {
        try {
            return shotApiEntityMapper.transform(shotApiService.getShot(shotId));
        } catch (IOException error) {
            throw new ServerCommunicationException(error);
        } catch (ApiException error) {
            if (error.getErrorInfo() == ErrorInfo.ResourceNotFoundException) {
                throw new ShotNotFoundException(error);
            } else {
                throw new ServerCommunicationException(error);
            }
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
    public ShotDetailEntity getShotDetail(String idShot) throws ShotNotFoundException {
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
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        } catch (ApiException e) {
            if (e.getErrorInfo() == ErrorInfo.ResourceNotFoundException) {
                throw new ShotNotFoundException(e);
            } else {
                throw new ServerCommunicationException(e);
            }
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

    @Override public void shareShot(String idShot) throws ShotNotFoundException {
        try {
            shotApiService.shareShot(idShot);
        } catch (IOException error) {
            throw new ServerCommunicationException(error);
        } catch (ApiException error) {
            if (error.getErrorInfo() == ErrorInfo.ResourceNotFoundException) {
                throw new ShotNotFoundException(error);
            } else {
                throw new ServerCommunicationException(error);
            }
        }
    }

    @Override public void deleteShot(String idShot) {
        try {
            shotApiService.deleteShot(idShot);
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<ShotEntity> getUserShotsForStreamTimeline(StreamTimelineParameters timelineParameters) {
        try {
            List<ShotApiEntity> shots = shotApiService.getAllShotsFromUserInStream(timelineParameters.getUserId(),
              timelineParameters.getStreamId(), timelineParameters.getSinceDate(), timelineParameters.getMaxDate());
            return shotApiEntityMapper.transform(shots);
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public void deleteShotsByIdStream(String idStream) {
        throw new IllegalArgumentException("This method should not have remote implementation");
    }

    @Override public void hideShot(String idShot) {
        try {
            shotApiService.hideShot(idShot);
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
    }
}