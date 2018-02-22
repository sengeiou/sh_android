package com.shootr.mobile.data.repository.datasource;

import com.shootr.mobile.data.api.entity.TimelineApiEntity;
import com.shootr.mobile.data.api.entity.mapper.ShotApiEntityMapper;
import com.shootr.mobile.data.api.entity.mapper.TimelineApiEntityMapper;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.TimelineApiService;
import com.shootr.mobile.data.entity.HeaderEntity;
import com.shootr.mobile.data.entity.TimelineEntity;
import com.shootr.mobile.data.repository.datasource.stream.StreamDataSource;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.repository.Local;
import java.io.IOException;
import javax.inject.Inject;

public class ServiceTimelineDataSource implements StreamTimelineDataSource {

  private final TimelineApiService timelineApiService;
  private final TimelineApiEntityMapper timelineApiEntityMapper;
  private final ShotApiEntityMapper shotApiEntityMapper;
  private final StreamDataSource streamDataSource;

  @Inject public ServiceTimelineDataSource(TimelineApiService timelineApiService,
      TimelineApiEntityMapper timelineApiEntityMapper, ShotApiEntityMapper shotApiEntityMapper,
      @Local StreamDataSource streamDataSource) {
    this.timelineApiService = timelineApiService;
    this.timelineApiEntityMapper = timelineApiEntityMapper;
    this.shotApiEntityMapper = shotApiEntityMapper;
    this.streamDataSource = streamDataSource;
  }

  @Override public TimelineEntity getTimeline(String idStream, Long timestamp, String timelineType) {
    try {
      TimelineApiEntity timelineApiEntity =
          timelineApiService.getTimeline(idStream, timelineType, timestamp);
      streamDataSource.putStream(timelineApiEntity.getStream());
      return timelineApiEntityMapper.map(timelineApiEntity);
    } catch (ApiException | IOException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public TimelineEntity getPaginatedTimeline(String idStream, Long timestamp, String timelineType) {
    try {
      TimelineApiEntity timelineApiEntity =
          timelineApiService.getPaginatedTimeline(idStream, timelineType, timestamp);
      streamDataSource.putStream(timelineApiEntity.getStream());
      return timelineApiEntityMapper.map(timelineApiEntity);
    } catch (ApiException | IOException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public void highlightItem(String resultType, String itemId, String idStream) {
    try {
      HeaderEntity headerEntity = new HeaderEntity();
      headerEntity.setType(HeaderEntity.SHOT_TYPE);
      headerEntity.setIdShot(itemId);
      timelineApiService.highlightItem(idStream, headerEntity);
    } catch (ApiException | IOException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public void deleteHighlightedItem(String resultType, String itemId, String idStream) {
    try {
      HeaderEntity headerEntity = new HeaderEntity();
      headerEntity.setType(HeaderEntity.SHOT_TYPE);
      headerEntity.setIdShot(itemId);

      timelineApiService.deleteHighlightedItem(idStream, headerEntity);
    } catch (ApiException | IOException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public void createTopic(String resultType, String comment, String idStream, boolean notify) {
    try {
      HeaderEntity headerEntity = new HeaderEntity();
      headerEntity.setType(HeaderEntity.TOPIC_TYPE);
      headerEntity.setComment(comment);
      timelineApiService.createTopic(idStream, headerEntity);
    } catch (ApiException | IOException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public void deleteTopic(String resultType, String idStream) {
    try {
      HeaderEntity headerEntity = new HeaderEntity();
      headerEntity.setType(HeaderEntity.TOPIC_TYPE);

      timelineApiService.deleteTopic(idStream, headerEntity);
    } catch (ApiException | IOException e) {
      throw new ServerCommunicationException(e);
    }
  }
}
