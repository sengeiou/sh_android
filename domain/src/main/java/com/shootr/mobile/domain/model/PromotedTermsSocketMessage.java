package com.shootr.mobile.domain.model;

public class PromotedTermsSocketMessage extends SocketMessage {

  public PromotedTermsSocketMessage() {
    setEventType(SocketMessage.PROMOTED_TERMS);
  }

  private PromotedTerms data;

  public PromotedTerms getData() {
    return data;
  }

  public void setData(PromotedTerms data) {
    this.data = data;
  }
}
