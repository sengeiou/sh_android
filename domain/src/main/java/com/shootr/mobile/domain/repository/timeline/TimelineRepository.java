package com.shootr.mobile.domain.repository.timeline;

import com.shootr.mobile.domain.model.StreamTimeline;

public interface TimelineRepository {

  StreamTimeline getTimeline(String idStream, String timelineType, Long timestamp);
}
