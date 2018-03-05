package com.shootr.mobile.data.repository.remote.cache;

import com.shootr.mobile.domain.model.TimelineReposition;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;
import javax.inject.Inject;

public class TimelineRepositionCache {

  private final DualCache<TimelineReposition> timelineRepositionDualCache;

  @Inject public TimelineRepositionCache(DualCache<TimelineReposition> timelineRepositionDualCache) {
    this.timelineRepositionDualCache = timelineRepositionDualCache;
  }

  public void putTimelineReposition(TimelineReposition item, String idStream, String filter) {
    timelineRepositionDualCache.delete(idStream + "-" + filter.toLowerCase());
    timelineRepositionDualCache.put(idStream + "-" + filter.toLowerCase(), item);
  }

  public TimelineReposition getTimelineReposition(String idStream, String filter) {
    return timelineRepositionDualCache.get(idStream + "-" + filter.toLowerCase());
  }
}
