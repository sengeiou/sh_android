package com.shootr.mobile.data.repository.remote.cache;

import com.shootr.mobile.data.repository.datasource.CachedDataSource;
import com.shootr.mobile.domain.model.PrintableItem;
import com.shootr.mobile.domain.model.PrintableType;
import com.shootr.mobile.domain.model.StreamTimeline;
import com.shootr.mobile.domain.model.shot.Shot;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;
import javax.inject.Inject;

public class TimelineCache implements CachedDataSource {

  private final DualCache<StreamTimeline> timelineLruCache;

  @Inject public TimelineCache(DualCache<StreamTimeline> timelineLruCache) {
    this.timelineLruCache = timelineLruCache;
  }

  public StreamTimeline getTimeline(String idStream, String idFilter) {
    return timelineLruCache.get(idStream + "-" + idFilter.toLowerCase());
  }

  public void putTimeline(StreamTimeline timeline) {

    StreamTimeline cachedTimeline = getTimeline(timeline.getStream().getId(), timeline.getFilter());

    if (cachedTimeline != null) {
      if (timeline.getItems().getSinceTimstamp() > cachedTimeline.getItems().getSinceTimstamp()) {
        storeTimeline(timeline);
      }
    } else {
      storeTimeline(timeline);
    }
  }

  private void storeTimeline(StreamTimeline timeline) {
    timelineLruCache.delete(timeline.getStream().getId() + "-" + timeline.getFilter().toLowerCase());
    timelineLruCache.put(timeline.getStream().getId() + "-" + timeline.getFilter().toLowerCase(), timeline);
  }

  public void putItemInTimeline(PrintableItem printableItem, String filter) {
    if (printableItem.getResultType().equals(PrintableType.SHOT)) {
      String idStream = ((Shot) printableItem).getStreamInfo().getIdStream();
      StreamTimeline timeline = this.getTimeline(idStream, filter.toLowerCase());
      if (timeline != null) {
        timeline.getItems().getData().add(0, printableItem);
        storeTimeline(timeline);
      }
    }
  }

  @Override public boolean isValid() {
    return false;
  }

  @Override public void invalidate() {
    timelineLruCache.invalidate();
  }
}
