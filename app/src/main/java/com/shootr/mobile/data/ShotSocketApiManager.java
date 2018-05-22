package com.shootr.mobile.data;

import com.shootr.mobile.data.api.SendSocketEventListener;
import com.shootr.mobile.data.entity.EventParams;
import com.shootr.mobile.data.entity.GetShotDetailSocketMessageApiEntity;
import com.shootr.mobile.data.entity.NiceSocketMessageApiEntity;
import com.shootr.mobile.data.entity.PaginationEntity;
import java.util.UUID;

public class ShotSocketApiManager {

  private final int VERSION = 1;

  private final SendSocketEventListener sendSocketEventListener;

  public ShotSocketApiManager(SendSocketEventListener sendSocketEventListener) {
    this.sendSocketEventListener = sendSocketEventListener;
  }

  public void getShotDetail(String idShot, PaginationEntity promotedPagination,
      PaginationEntity subscribersPagination, PaginationEntity basicPagination) {

      GetShotDetailSocketMessageApiEntity getShotDetailSocketMessageApiEntity =
          new GetShotDetailSocketMessageApiEntity();

      GetShotDetailSocketMessageApiEntity.ShotDetailParams shotDetailParams =
          new GetShotDetailSocketMessageApiEntity.ShotDetailParams();

      GetShotDetailSocketMessageApiEntity.Pagination pagination =
          new GetShotDetailSocketMessageApiEntity.Pagination();

      pagination.setBasic(basicPagination);
      pagination.setPromoted(promotedPagination);
      pagination.setSubscribers(subscribersPagination);

      shotDetailParams.setIdShot(idShot);
      shotDetailParams.setPagination(pagination);

      getShotDetailSocketMessageApiEntity.setData(shotDetailParams);
      getShotDetailSocketMessageApiEntity.setRequestId(generateRequestId());
      getShotDetailSocketMessageApiEntity.setVersion(VERSION);

      sendSocketEventListener.sendEvent(getShotDetailSocketMessageApiEntity);
  }

  public void sendNice(String idShot) {
      NiceSocketMessageApiEntity niceSocketMessageApiEntity = new NiceSocketMessageApiEntity();
      EventParams eventParams = new EventParams();

      eventParams.setIdShot(idShot);

      niceSocketMessageApiEntity.setRequestId(generateRequestId());
      niceSocketMessageApiEntity.setVersion(VERSION);
      niceSocketMessageApiEntity.setData(eventParams);

      sendSocketEventListener.sendEvent(niceSocketMessageApiEntity);
  }

  private String generateRequestId() {
    return UUID.randomUUID().toString();
  }

}
