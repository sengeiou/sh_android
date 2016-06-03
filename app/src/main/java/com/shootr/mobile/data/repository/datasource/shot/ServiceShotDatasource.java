package com.shootr.mobile.data.repository.datasource.shot;

import com.shootr.mobile.data.api.entity.ShotApiEntity;
import com.shootr.mobile.data.api.entity.mapper.ShotApiEntityMapper;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.exception.ErrorInfo;
import com.shootr.mobile.data.api.service.ShotApiService;
import com.shootr.mobile.data.entity.ShotDetailEntity;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.domain.StreamTimelineParameters;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShotNotFoundException;
import com.shootr.mobile.domain.exception.StreamReadOnlyException;
import com.shootr.mobile.domain.exception.StreamRemovedException;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceShotDatasource implements ShotDataSource {

  public static final String METHOD_NOT_VALID_FOR_SERVICE = "Method not valid for service";
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
      } else if (e.getErrorInfo() == ErrorInfo.StreamViewOnlyRequestException) {
        throw new StreamReadOnlyException(e);
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
      if (parameters.isRealTime()) {
        List<ShotApiEntity> shots =
            shotApiService.getStreamTimeline(parameters.getStreamId(), parameters.getStreamTypes(),
                parameters.getShotTypes(), parameters.getLimit(),
                parameters.getSinceDate(), parameters.getMaxDate());
        return shotApiEntityMapper.transform(shots);
      } else {
        List<ShotApiEntity> shots =
            shotApiService.getStreamTimlinefirstCall(parameters.getStreamId(),
                parameters.getStreamTypes(), parameters.getShotTypes(),
                parameters.getSinceDate());
        return shotApiEntityMapper.transform(shots);
      }
    } catch (ApiException | IOException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public ShotEntity getShot(String shotId, String[] streamTypes, String[] shotTypes)
      throws ShotNotFoundException {
    try {
      return shotApiEntityMapper.transform(shotApiService.getShot(shotId, streamTypes, shotTypes));
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

  @Override
  public List<ShotEntity> getReplies(String shotId, String[] streamTypes, String[] shotTypes) {
    try {
      ShotApiEntity shot = shotApiService.getShotWithReplies(shotId, streamTypes, shotTypes);
      List<ShotApiEntity> replies = shot.getReplies();
      return shotApiEntityMapper.transform(replies);
    } catch (IOException | ApiException error) {
      throw new ServerCommunicationException(error);
    }
  }

  @Override public List<ShotEntity> getStreamMediaShots(String idStream, List<String> userIds,
      Long maxTimestamp, String[] streamTypes, String[] shotTypes) {
    try {
      List<ShotApiEntity> mediaApiShots =
          shotApiService.getMediaShots(idStream, streamTypes, shotTypes, maxTimestamp);
      return shotApiEntityMapper.transform(mediaApiShots);
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override
  public List<ShotEntity> getShotsFromUser(String idUser, Integer limit, String[] streamTypes,
      String[] shotTypes) {
    try {
      List<ShotApiEntity> userApiShots =
          shotApiService.getShotsFromUser(idUser, limit, streamTypes, shotTypes);
      return shotApiEntityMapper.transform(userApiShots);
    } catch (ApiException | IOException error) {
      throw new ServerCommunicationException(error);
    }
  }

  @Override
  public ShotDetailEntity getShotDetail(String idShot, String[] streamTypes, String[] shotTypes)
      throws ShotNotFoundException {
    try {
      ShotApiEntity shotApiEntity = shotApiService.getShotDetail(idShot, streamTypes, shotTypes);

      ShotEntity shotEntity = shotApiEntityMapper.transform(shotApiEntity);
      List<ShotEntity> repliesEntities = shotApiEntityMapper.transform(shotApiEntity.getReplies());

      ShotDetailEntity shotDetailEntity = new ShotDetailEntity();
      shotDetailEntity.setShot(shotEntity);
      shotDetailEntity.setReplies(repliesEntities);
      if (shotApiEntity.getParent() != null) {
        List<ShotEntity> parentEntities = shotApiEntityMapper.transform(shotApiEntity.getThread());
        shotDetailEntity.setParents(parentEntities);
      }
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

  @Override public List<ShotEntity> getAllShotsFromUser(String userId, String[] streamTypes,
      String[] shotTypes) {
    try {
      List<ShotApiEntity> allShotsFromUser =
          shotApiService.getAllShotsFromUser(userId, streamTypes, shotTypes);
      return shotApiEntityMapper.transform(allShotsFromUser);
    } catch (ApiException | IOException error) {
      throw new ServerCommunicationException(error);
    }
  }

  @Override
  public List<ShotEntity> getAllShotsFromUserAndDate(String userId, Long currentOldestDate,
      String[] streamTypes, String[] shotTypes) {
    try {
      List<ShotApiEntity> allShotsFromUserAndDate =
          shotApiService.getAllShotsFromUserWithMaxDate(userId, currentOldestDate, streamTypes,
              shotTypes);
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

  @Override public List<ShotEntity> getUserShotsForStreamTimeline(
      StreamTimelineParameters timelineParameters) {
    try {
      List<ShotApiEntity> shots =
          shotApiService.getAllShotsFromUserInStream(timelineParameters.getUserId(),
              timelineParameters.getStreamId(), timelineParameters.getSinceDate(),
              timelineParameters.getMaxDate(), timelineParameters.getStreamTypes(),
              timelineParameters.getShotTypes());
      return shotApiEntityMapper.transform(shots);
    } catch (ApiException | IOException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public void deleteShotsByIdStream(String idStream) {
    throw new IllegalArgumentException("This method should not have remote implementation");
  }

  @Override public void hideShot(String idShot, Long timestamp) {
    try {
      shotApiService.hideShot(idShot);
    } catch (ApiException | IOException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public void unhideShot(String idShot) {
    try {
      shotApiService.unhideShot(idShot);
    } catch (ApiException | IOException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public List<ShotEntity> getEntitiesNotSynchronized() {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
  }
}