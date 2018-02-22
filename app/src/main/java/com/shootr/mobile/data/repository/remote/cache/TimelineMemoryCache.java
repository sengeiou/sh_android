package com.shootr.mobile.data.repository.remote.cache;

import com.fewlaps.quitnowcache.QNCache;
import com.shootr.mobile.domain.model.PrintableItem;
import com.shootr.mobile.domain.model.PrintableType;
import com.shootr.mobile.domain.model.StreamTimeline;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton public class TimelineMemoryCache {

  public static final String GET_PEOPLE = "getPeople";
  public static final int GET_PEOPLE_KEEP_ALIVE_SECONDS = 20 * 1000;
  private final QNCache<StreamTimeline> timelineCache;

  @Inject public TimelineMemoryCache(QNCache timelineCache) {
    this.timelineCache = timelineCache;
  }

  public StreamTimeline getTimeline() {
    StreamTimeline timeline = timelineCache.get(GET_PEOPLE);
    Timber.d("getTimeline cache %s", timeline != null ? "valid" : "invalid");
    if (timeline != null) {
      return timeline;
    } else {
      return null;
    }
  }

  public void putTimeline(StreamTimeline streamTimeline) {
    timelineCache.set(streamTimeline.getStream().getId(), streamTimeline, 3, TimeUnit.HOURS);
  }

  public void putItemInTimeline(PrintableItem printableItem) {
    if (printableItem.getResultType().equals(PrintableType.SHOT)) {
      //StreamTimeline timeline = timelineCache.get(((Shot) printableItem).getStreamInfo().getIdStream());
      //TODO REMOVE
      StreamTimeline timeline = timelineCache.get("5964e5002ab79c00075dbd9c");
      if (timeline != null) {
        timeline.getItems().getData().add(printableItem);
        timelineCache.set(timeline.getStream().getId(), timeline);
      }
    }
  }

}
