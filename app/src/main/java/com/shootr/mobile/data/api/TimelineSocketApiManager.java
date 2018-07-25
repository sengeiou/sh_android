package com.shootr.mobile.data.api;

import com.shootr.mobile.data.entity.socket.GetTimelineSocketMessageApiEntity;
import com.shootr.mobile.data.entity.PaginationEntity;
import com.shootr.mobile.data.entity.ParamsEntity;
import java.util.UUID;

public class TimelineSocketApiManager {

  private final int VERSION = 1;

  private final SendSocketEventListener sendSocketEventListener;

  public TimelineSocketApiManager(SendSocketEventListener sendSocketEventListener) {
    this.sendSocketEventListener = sendSocketEventListener;
  }

  public void getTimeline(String idStream, String filter, PaginationEntity paginationEntity) {
    GetTimelineSocketMessageApiEntity getTimelineSocketMessageApiEntity =
        new GetTimelineSocketMessageApiEntity();
    GetTimelineSocketMessageApiEntity.TimelineParams timelineParams =
        new GetTimelineSocketMessageApiEntity.TimelineParams();

    timelineParams.setFilter(filter);
    timelineParams.setIdStream(idStream);
    timelineParams.setPagination(paginationEntity);

    getTimelineSocketMessageApiEntity.setRequestId(generateRequestId());
    getTimelineSocketMessageApiEntity.setVersion(VERSION);
    getTimelineSocketMessageApiEntity.setData(timelineParams);

    sendSocketEventListener.sendEvent(getTimelineSocketMessageApiEntity);
  }

  public void getNicestTimeline(String idStream, String filter, PaginationEntity paginationEntity,
      ParamsEntity paramsEntity) {
    GetTimelineSocketMessageApiEntity getTimelineSocketMessageApiEntity =
        new GetTimelineSocketMessageApiEntity();
    GetTimelineSocketMessageApiEntity.TimelineParams timelineParams =
        new GetTimelineSocketMessageApiEntity.TimelineParams();

    timelineParams.setFilter(filter);
    timelineParams.setIdStream(idStream);
    timelineParams.setPagination(paginationEntity);
    timelineParams.setParams(paramsEntity);

    getTimelineSocketMessageApiEntity.setRequestId(generateRequestId());
    getTimelineSocketMessageApiEntity.setVersion(VERSION);
    getTimelineSocketMessageApiEntity.setData(timelineParams);

    sendSocketEventListener.sendEvent(getTimelineSocketMessageApiEntity);
  }

  private String generateRequestId() {
    return UUID.randomUUID().toString();
  }
}
