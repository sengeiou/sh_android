package com.shootr.mobile.domain.repository.timeline;

import com.shootr.mobile.domain.model.StreamTimeline;

public interface ExternalTimelineRepository extends TimelineRepository {

  StreamTimeline getPaginatedTimeline(String idStream, String timelineType, Long timestamp);

  void highlightItem(String resultType, String itemId, String idStream);

  void deleteHighlightedItem(String resultType, String itemId, String idStream);

  void createTopic(String resultType, String comment, String idStream, boolean notify);

  void deleteTopic(String resultType, String idStream);

  void putTimeline(StreamTimeline timeline, String idStream, String idFilter);

}
