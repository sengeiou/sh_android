package com.shootr.mobile.data.entity;

public class SocketMessageEntity {

  private String type;
  private int version;
  private String requestId;
  private transient boolean isActiveSubscription;
  private EventParams eventParams;

  public String getEventType() {
    return type;
  }

  public void setEventType(String eventType) {
    this.type = eventType;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public boolean isActiveSubscription() {
    return isActiveSubscription;
  }

  public void setActiveSubscription(boolean activeSubscription) {
    isActiveSubscription = activeSubscription;
  }

  public EventParams getEventParams() {
    return eventParams;
  }

  public void setEventParams(EventParams eventParams) {
    this.eventParams = eventParams;
  }
}
