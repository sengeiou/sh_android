package com.shootr.mobile.data.repository.datasource;

import com.shootr.mobile.data.entity.TimelineEntity;

public interface StreamTimelineDataSource {

  TimelineEntity getTimeline(String idStream, Long timestamp, String timelineType);

  TimelineEntity getPaginatedTimeline(String idStream, Long timestamp, String timelineType);

  void highlightItem(String resultType, String itemId, String idStream);

  void deleteHighlightedItem(String resultType, String itemId, String idStream);

  void createTopic(String resultType, String comment, String idStream, boolean notify);

  void deleteTopic(String resultType, String idStream);
}
