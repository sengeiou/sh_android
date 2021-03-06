package com.shootr.mobile.data;

import com.shootr.mobile.data.api.SendSocketEventListener;
import com.shootr.mobile.data.entity.AcceptPromotedTermsSocketMessageApiEntity;
import com.shootr.mobile.data.entity.EventParams;
import com.shootr.mobile.data.entity.GetPromotedTermsSocketMessageApiEntity;
import com.shootr.mobile.data.entity.HighlightSocketMessageApiEntity;
import com.shootr.mobile.data.entity.PaginationEntity;
import com.shootr.mobile.data.entity.SeenParams;
import com.shootr.mobile.data.entity.SeenSocketMessageApiEntity;
import com.shootr.mobile.data.entity.UnHighlightSocketMessageApiEntity;
import com.shootr.mobile.data.entity.socket.GetShotDetailSocketMessageApiEntity;
import com.shootr.mobile.data.entity.socket.NiceSocketMessageApiEntity;
import java.util.Date;
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

  public void sendHighlightShot(String idShot, String idStream) {
    HighlightSocketMessageApiEntity highlightSocketMessageApiEntity =
        new HighlightSocketMessageApiEntity();
    EventParams eventParams = new EventParams();

    eventParams.setIdShot(idShot);
    eventParams.setIdStream(idStream);

    highlightSocketMessageApiEntity.setRequestId(generateRequestId());
    highlightSocketMessageApiEntity.setVersion(VERSION);
    highlightSocketMessageApiEntity.setData(eventParams);

    sendSocketEventListener.sendEvent(highlightSocketMessageApiEntity);
  }

  public void sendUnHighlightShot(String idShot, String idStream) {
    UnHighlightSocketMessageApiEntity unHighlightSocketMessageApiEntity =
        new UnHighlightSocketMessageApiEntity();
    EventParams eventParams = new EventParams();

    eventParams.setIdShot(idShot);
    eventParams.setIdStream(idStream);

    unHighlightSocketMessageApiEntity.setRequestId(generateRequestId());
    unHighlightSocketMessageApiEntity.setVersion(VERSION);
    unHighlightSocketMessageApiEntity.setData(eventParams);

    sendSocketEventListener.sendEvent(unHighlightSocketMessageApiEntity);
  }

  public void sendSeen(String type, String itemId) {
    SeenSocketMessageApiEntity seenSocketMessageApiEntity = new SeenSocketMessageApiEntity();
    SeenParams seenParams = new SeenParams();

    seenParams.setItemId(itemId);
    seenParams.setType(type);
    seenParams.setTimestamp(new Date().getTime());

    seenSocketMessageApiEntity.setRequestId(generateRequestId());
    seenSocketMessageApiEntity.setVersion(VERSION);
    seenSocketMessageApiEntity.setData(seenParams);

    sendSocketEventListener.sendEvent(seenSocketMessageApiEntity);
  }

  public void getPromotedTerms(String idStream) {
    GetPromotedTermsSocketMessageApiEntity getPromotedTermsSocketMessageApiEntity =
        new GetPromotedTermsSocketMessageApiEntity();
    GetPromotedTermsSocketMessageApiEntity.Params params =
        new GetPromotedTermsSocketMessageApiEntity.Params();

    params.setIdStream(idStream);

    getPromotedTermsSocketMessageApiEntity.setRequestId(generateRequestId());
    getPromotedTermsSocketMessageApiEntity.setVersion(VERSION);
    getPromotedTermsSocketMessageApiEntity.setData(params);

    sendSocketEventListener.sendEvent(getPromotedTermsSocketMessageApiEntity);
  }

  public void acceptPromotedTerms(String idStream, int version) {
    AcceptPromotedTermsSocketMessageApiEntity acceptPromotedTermsSocketMessageApiEntity =
        new AcceptPromotedTermsSocketMessageApiEntity();
    AcceptPromotedTermsSocketMessageApiEntity.Params params =
        new AcceptPromotedTermsSocketMessageApiEntity.Params();

    params.setIdStream(idStream);
    params.setVersion(version);

    acceptPromotedTermsSocketMessageApiEntity.setRequestId(generateRequestId());
    acceptPromotedTermsSocketMessageApiEntity.setVersion(VERSION);
    acceptPromotedTermsSocketMessageApiEntity.setData(params);

    sendSocketEventListener.sendEvent(acceptPromotedTermsSocketMessageApiEntity);
  }

  private String generateRequestId() {
    return UUID.randomUUID().toString();
  }
}
