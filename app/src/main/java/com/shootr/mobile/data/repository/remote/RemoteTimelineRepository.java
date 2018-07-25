package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.api.SocketApi;
import com.shootr.mobile.data.mapper.ShotEntityMapper;
import com.shootr.mobile.data.mapper.TimelineEntityMapper;
import com.shootr.mobile.data.repository.datasource.StreamTimelineDataSource;
import com.shootr.mobile.data.repository.remote.cache.TimelineCache;
import com.shootr.mobile.domain.model.StreamTimeline;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.timeline.ExternalTimelineRepository;
import javax.inject.Inject;

public class RemoteTimelineRepository implements ExternalTimelineRepository {

  private final StreamTimelineDataSource streamTimelineDataSource;
  private final TimelineEntityMapper timelineEntityMapper;
  private final ShotEntityMapper shotEntityMapper;
  private final TimelineCache timelineCache;
  private final SocketApi socketApi;

  @Inject public RemoteTimelineRepository(@Remote StreamTimelineDataSource streamTimelineDataSource,
      TimelineEntityMapper timelineEntityMapper, ShotEntityMapper shotEntityMapper,
      TimelineCache timelineCache, SocketApi socketApi) {
    this.streamTimelineDataSource = streamTimelineDataSource;
    this.timelineEntityMapper = timelineEntityMapper;
    this.shotEntityMapper = shotEntityMapper;
    this.timelineCache = timelineCache;
    this.socketApi = socketApi;
  }

  @Override public StreamTimeline getTimeline(String idStream, String timelineType, Long timestamp) {
    return timelineEntityMapper.map(streamTimelineDataSource.getTimeline(idStream, timestamp, timelineType));
  }

  @Override public StreamTimeline getPaginatedTimeline(String idStream, String timelineType, Long timestamp) {
    return timelineEntityMapper.map(streamTimelineDataSource.getPaginatedTimeline(idStream, timestamp, timelineType));
  }

  @Override public void highlightItem(String resultType, String itemId, String idStream) {
     streamTimelineDataSource.highlightItem(resultType, itemId, idStream);
  }

  @Override public void deleteHighlightedItem(String resultType, String itemId, String idStream) {
    streamTimelineDataSource.deleteHighlightedItem(resultType, itemId, idStream);
    socketApi.unHighlightShot(itemId, idStream);
  }

  @Override
  public void createTopic(String resultType, String comment, String idStream, boolean notify) {
    streamTimelineDataSource.createTopic(resultType, comment, idStream, notify);
  }

  @Override public void deleteTopic(String resultType, String idStream) {
    streamTimelineDataSource.deleteTopic(resultType, idStream);
  }

  @Override public void putTimeline(StreamTimeline timeline, String idStream, String idFilter) {
    timelineCache.putTimeline(timeline);
  }
}
