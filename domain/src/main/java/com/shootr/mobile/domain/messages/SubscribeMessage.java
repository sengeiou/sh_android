package com.shootr.mobile.domain.messages;


public class SubscribeMessage implements Message {

  private String subscriptionType;
  private String idStream;
  private String filterType;

  public String getSubscriptionType() {
    return subscriptionType;
  }

  public void setSubscriptionType(String subscriptionType) {
    this.subscriptionType = subscriptionType;
  }

  public String getIdStream() {
    return idStream;
  }

  public void setIdStream(String idStream) {
    this.idStream = idStream;
  }

  public String getFilterType() {
    return filterType;
  }

  public void setFilterType(String filterType) {
    this.filterType = filterType;
  }

  @Override public String getMessageType() {
    return Message.SUBSCRIBE;
  }
}
