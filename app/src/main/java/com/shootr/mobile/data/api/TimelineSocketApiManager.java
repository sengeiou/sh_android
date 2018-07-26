package com.shootr.mobile.data.api;

import com.shootr.mobile.data.entity.GetPromotedTiersSocketMessageApiEntity;
import com.shootr.mobile.data.entity.PaginationEntity;
import com.shootr.mobile.data.entity.ParamsEntity;
import com.shootr.mobile.data.entity.VerifyReceipSocketMessageApiEntity;
import com.shootr.mobile.data.entity.socket.GetTimelineSocketMessageApiEntity;
import java.util.UUID;

public class TimelineSocketApiManager {

  private final int VERSION = 2;

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

  public void getPromotedTiers() {
    GetPromotedTiersSocketMessageApiEntity getPromotedTiersSocketMessageApiEntity =
        new GetPromotedTiersSocketMessageApiEntity();
    getPromotedTiersSocketMessageApiEntity.setRequestId(generateRequestId());
    getPromotedTiersSocketMessageApiEntity.setVersion(VERSION);
    sendSocketEventListener.sendEvent(getPromotedTiersSocketMessageApiEntity);
  }

  public void verifyReceipt(String receipt) {
    VerifyReceipSocketMessageApiEntity verifyReceipSocketMessageApiEntity = new VerifyReceipSocketMessageApiEntity();

    VerifyReceipSocketMessageApiEntity.ReceiptData receiptData = new VerifyReceipSocketMessageApiEntity.ReceiptData();
    receiptData.setReceipt(receipt);
    receiptData.setType("PLAY_STORE");

    verifyReceipSocketMessageApiEntity.setRequestId(generateRequestId());
    verifyReceipSocketMessageApiEntity.setVersion(VERSION);
    verifyReceipSocketMessageApiEntity.setData(receiptData);

    sendSocketEventListener.sendEvent(verifyReceipSocketMessageApiEntity);
  }

  private String generateRequestId() {
    return UUID.randomUUID().toString();
  }
}
