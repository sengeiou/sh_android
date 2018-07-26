package com.shootr.mobile.domain.model;

import com.shootr.mobile.domain.model.user.PromotedTiers;

public class PromotedTiersSocketMessage extends SocketMessage {

  public PromotedTiersSocketMessage() {
    setEventType(SocketMessage.PROMOTED_TIERS);
  }

  private PromotedTiers data;

  public PromotedTiers getData() {
    return data;
  }

  public void setData(PromotedTiers data) {
    this.data = data;
  }
}
