package com.shootr.mobile.data.entity.socket;

import com.shootr.mobile.data.entity.PromotedTermsEntity;

public class PromotedTermsSocketMessageEntity extends SocketMessageEntity {

  public PromotedTermsSocketMessageEntity() {
    setEventType(SocketMessageApiEntity.PROMOTED_TERMS);
  }

  private PromotedTermsEntity data;

  public PromotedTermsEntity getData() {
    return data;
  }

  public void setData(PromotedTermsEntity data) {
    this.data = data;
  }
}
