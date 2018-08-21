package com.shootr.mobile.data.entity.socket;

import com.shootr.mobile.data.entity.PromotedTermsApiEntity;

public class PromotedTermsSocketMessageApiEntity extends SocketMessageApiEntity {

  public PromotedTermsSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.PROMOTED_TERMS);
  }

  private PromotedTermsApiEntity data;

  public PromotedTermsApiEntity getData() {
    return data;
  }

  public void setData(PromotedTermsApiEntity data) {
    this.data = data;
  }
}
