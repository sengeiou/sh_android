package com.shootr.mobile.data.repository.datasource.shot;

import android.support.annotation.NonNull;
import com.shootr.mobile.data.api.entity.CreateAHighlightedShotEntity;
import com.shootr.mobile.data.api.entity.ShotApiEntity;
import com.shootr.mobile.data.api.entity.mapper.ShotApiEntityMapper;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.exception.ErrorInfo;
import com.shootr.mobile.data.api.service.ShotApiService;
import com.shootr.mobile.data.entity.HighlightedShotApiEntity;
import com.shootr.mobile.data.entity.HighlightedShotEntity;
import com.shootr.mobile.data.entity.ShotDetailEntity;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.data.mapper.HighlightedShotEntityMapper;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrError;
import com.shootr.mobile.domain.exception.ShotNotFoundException;
import com.shootr.mobile.domain.exception.StreamReadOnlyException;
import com.shootr.mobile.domain.exception.StreamRemovedException;
import com.shootr.mobile.domain.exception.UserAlreadyCheckInRequestException;
import com.shootr.mobile.domain.exception.UserCannotCheckInRequestException;
import com.shootr.mobile.domain.model.stream.StreamTimelineParameters;
import com.shootr.mobile.util.AndroidTimeUtils;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceShotDatasource implements ShotDataSource {

  public static final String METHOD_NOT_VALID_FOR_SERVICE = "Method not valid for service";
  private final ShotApiService shotApiService;
  private final ShotApiEntityMapper shotApiEntityMapper;
  private final HighlightedShotEntityMapper highlightedShotEntityMapper;
  private final DatabaseShotDataSource databaseShotDataSource;

  @Inject public ServiceShotDatasource(ShotApiService shotApiService,
      ShotApiEntityMapper shotApiEntityMapper,
      HighlightedShotEntityMapper highlightedShotEntityMapper,
      DatabaseShotDataSource databaseShotDataSource) {
    this.shotApiService = shotApiService;
    this.shotApiEntityMapper = shotApiEntityMapper;
    this.highlightedShotEntityMapper = highlightedShotEntityMapper;
    this.databaseShotDataSource = databaseShotDataSource;
  }

  @Override public ShotEntity putShot(ShotEntity shotEntity, String idUserMe) {
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

  @Override public void putShots(List<ShotEntity> shotEntities, String idUserMe) {
    for (ShotEntity shotEntity : shotEntities) {
      putShot(shotEntity, idUserMe);
    }
  }

  @Override public List<ShotEntity> getShotsForStreamTimeline(StreamTimelineParameters parameters) {
    try {
      if (parameters.isRealTime()) {
        List<ShotApiEntity> shots =
            shotApiService.getStreamTimeline(parameters.getStreamId(), parameters.getStreamTypes(),
                parameters.getShotTypes(), parameters.getLimit(), parameters.getSinceDate(),
                parameters.getMaxDate());
        return shotApiEntityMapper.transform(shots);
      } else {
        List<ShotApiEntity> shots =
            shotApiService.getStreamTimlinefirstCall(parameters.getStreamId(),
                parameters.getStreamTypes(), parameters.getShotTypes(), parameters.getSinceDate());
        return shotApiEntityMapper.transform(shots);
      }
    } catch (ApiException | IOException e) {
      throw new ServerCommunicationException(e);
    } catch (Exception generalError) {
      throw new ServerCommunicationException(generalError);
    }
  }

  @Override
  public List<ShotEntity> getShotsForStreamTimelineFiltered(StreamTimelineParameters parameters) {
    throw new IllegalStateException("Method not valid for service");
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

  @Override public List<ShotEntity> getStreamMediaShots(String idStream,
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
      if (idShot == null) {
        throw new ApiException(ErrorInfo.ResourceNotFoundException);
      }
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

  @Override
  public List<ShotEntity> updateImportantShots(StreamTimelineParameters timelineParameters) {
    AndroidTimeUtils androidTimeUtils = new AndroidTimeUtils();
    try {
      List<ShotApiEntity> shots = shotApiService.getImportantShots(timelineParameters.getStreamId(),
          timelineParameters.getStreamTypes(), timelineParameters.getShotTypes(),
          androidTimeUtils.getCurrentTime(), timelineParameters.getLimit());
      return shotApiEntityMapper.transform(shots);
    } catch (ApiException | IOException e) {
      throw new ServerCommunicationException(e);
    } catch (Exception generalError) {
      throw new ServerCommunicationException(generalError);
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

  @Override public HighlightedShotEntity getHighlightedShot(String idStream) {
    try {
      StreamTimelineParameters streamTimelineParameters =
          StreamTimelineParameters.builder().forStream(idStream).build();
      List<HighlightedShotApiEntity> highlightedShot =
          shotApiService.getHighlightedShot(idStream, streamTimelineParameters.getShotTypes());
      HighlightedShotEntity localHighlightedShot =
          databaseShotDataSource.getHighlightedShot(idStream);

      if (!highlightedShot.isEmpty()) {
        HighlightedShotEntity highlightedShotEntity =
            setHighlightShotVisibility(highlightedShot, localHighlightedShot);
        databaseShotDataSource.putHighlightShot(highlightedShotEntity);
        return highlightedShotEntity;
      } else {
        return null;
      }
    } catch (ApiException | IOException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @NonNull private HighlightedShotEntity setHighlightShotVisibility(
      List<HighlightedShotApiEntity> highlightedShot, HighlightedShotEntity localHighlightedShot) {
    HighlightedShotEntity highlightedShotEntity =
        highlightedShotEntityMapper.transform(highlightedShot.get(0));

    if (isInLocal(localHighlightedShot, highlightedShotEntity)) {
      highlightedShotEntity.setVisible(localHighlightedShot.isVisible());
    } else {
      highlightedShotEntity.setVisible(true);
    }
    return highlightedShotEntity;
  }

  private boolean isInLocal(HighlightedShotEntity localHighlightedShot,
      HighlightedShotEntity highlightedShotEntity) {
    return localHighlightedShot != null && localHighlightedShot.getIdHighlightedShot()
        .equals(highlightedShotEntity.getIdHighlightedShot());
  }

  @Override public HighlightedShotEntity highlightShot(String idShot) {
    CreateAHighlightedShotEntity createAHighlightedShotEntity = new CreateAHighlightedShotEntity();
    createAHighlightedShotEntity.setIdShot(idShot);
    try {
      HighlightedShotApiEntity createdhighlightedShotApiEntity =
          shotApiService.postHighlightedShotEntity(createAHighlightedShotEntity);
      HighlightedShotEntity highlightedShotEntity =
          highlightedShotEntityMapper.transform(createdhighlightedShotApiEntity);
      highlightedShotEntity.setVisible(true);
      databaseShotDataSource.putHighlightShot(highlightedShotEntity);
      return highlightedShotEntityMapper.transform(createdhighlightedShotApiEntity);
    } catch (ApiException | IOException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public void putHighlightShot(HighlightedShotEntity highlightedShotApiEntity) {
    throw new IllegalArgumentException("method not implemented");
  }

  @Override public void dismissHighlight(String idHighlightedShot) {
    try {
      shotApiService.dismissHighlightedShot(idHighlightedShot);
      databaseShotDataSource.dismissHighlight(idHighlightedShot);
    } catch (ApiException | IOException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public void hideHighlightedShot(String idHighlightedShot) {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
  }

  @Override public void callCtaCheckIn(String idStream)
      throws UserCannotCheckInRequestException, UserAlreadyCheckInRequestException {
    try {
      shotApiService.checkIn(idStream);
    } catch (IOException e) {
      throw new ServerCommunicationException(e);
    } catch (ApiException e) {
      if (String.valueOf(e.getErrorInfo().code()).equals(ShootrError.ERROR_CODE_CHECKIN)) {
        throw new UserCannotCheckInRequestException(e);
      } else if (String.valueOf(e.getErrorInfo().code())
          .equals(ShootrError.ERROR_ALREADY_CHECKIN)) {
        throw new UserAlreadyCheckInRequestException(e);
      } else {
        throw new ServerCommunicationException(e);
      }
    }
  }

  @Override public boolean hasNewFilteredShots(String idStream, String lastTimeFiltered) {
    throw new IllegalArgumentException("method not implemented");
  }

  @Override public List<ShotEntity> getEntitiesNotSynchronized() {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
  }
}