package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.repository.remote.cache.TimelineCache;
import com.shootr.mobile.domain.model.PrintableItem;
import com.shootr.mobile.domain.model.StreamTimeline;
import com.shootr.mobile.domain.repository.timeline.InternalTimelineRepository;
import javax.inject.Inject;

public class LocalTimelineRepository implements InternalTimelineRepository {

  private final TimelineCache timelineCache;

  @Inject public LocalTimelineRepository(TimelineCache timelineCache) {
    this.timelineCache = timelineCache;
  }

  @Override
  public StreamTimeline getTimeline(String idStream, String timelineType, Long timestamp) {
    return null;
  }

  @Override public void putTimeline(StreamTimeline streamTimeline) {
    timelineCache.putTimeline(streamTimeline);
  }

  @Override public void putItem(String requestId, PrintableItem printableItem) {
    timelineCache.putItemInTimeline(printableItem, null, null, null);
  }
}
