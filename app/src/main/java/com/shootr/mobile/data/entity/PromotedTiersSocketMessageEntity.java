package com.shootr.mobile.data.entity;

import com.shootr.mobile.data.entity.socket.SocketMessageApiEntity;
import com.shootr.mobile.data.entity.socket.SocketMessageEntity;

public class PromotedTiersSocketMessageEntity extends SocketMessageEntity {

  public PromotedTiersSocketMessageEntity() {
    setEventType(SocketMessageApiEntity.PROMOTED_TIERS);
  }

  private PromotedTiersEntity data;

  public PromotedTiersEntity getData() {
    return data;
  }

  public void setData(PromotedTiersEntity data) {
    this.data = data;
  }
}
