package com.shootr.mobile.domain.repository.timeline;

import com.shootr.mobile.domain.model.PrintableItem;
import com.shootr.mobile.domain.model.StreamTimeline;

public interface InternalTimelineRepository extends TimelineRepository {

  void putTimeline(StreamTimeline streamTimeline);

  void putItem(String requestId, PrintableItem printableItem);
}
