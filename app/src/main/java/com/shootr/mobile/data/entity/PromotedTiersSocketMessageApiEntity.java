package com.shootr.mobile.data.entity;

import com.shootr.mobile.data.entity.socket.SocketMessageApiEntity;

public class PromotedTiersSocketMessageApiEntity extends SocketMessageApiEntity {

  public PromotedTiersSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.PROMOTED_TIERS);
  }

  private PromotedTiersApiEntity data;

  public PromotedTiersApiEntity getData() {
    return data;
  }

  public void setData(PromotedTiersApiEntity data) {
    this.data = data;
  }
}
