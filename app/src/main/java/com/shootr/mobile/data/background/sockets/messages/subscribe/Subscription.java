package com.shootr.mobile.data.background.sockets.messages.subscribe;

public class Subscription {

  public static final String TYPE_TIMELINE = "TIMELINE";
  public static final String TYPE_USER = "USER";

  private String subscriptionType;
  private String idStream;
  private String filter;
  private String idUser;

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

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public String getIdUser() {
    return idUser;
  }

  public void setIdUser(String idUser) {
    this.idUser = idUser;
  }
}
