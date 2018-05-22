package com.shootr.mobile.data.entity;

public class SocketMessageApiEntity {

  public static final String TIMELINE = "TIMELINE";
  public static final String NEW_ITEM_DATA = "NEW_ITEM_DATA";
  public static final String UPDATE_ITEM_DATA = "UPDATE_ITEM_DATA";
  public static final String PINNED_ITEMS = "PINNED_ITEMS";
  public static final String FIXED_ITEMS = "FIXED_ITEMS";
  public static final String SUBSCRIBE = "SUBSCRIBE";
  public static final String UNSUBSCRIBE = "UNSUBSCRIBE";
  public static final String GET_TIMELINE = "GET_TIMELINE";
  public static final String NICE_SHOT = "NICE_SHOT";
  public static final String ACK = "ACK";
  public static final String PARTICIPANTS_UPDATE = "PARTICIPANTS_UPDATE";
  public static final String H1_ITEMS = "H1_ITEMS";
  public static final String H2_ITEMS = "H2_ITEMS";
  public static final String NEW_BADGE_CONTENT = "NEW_BADGE_CONTENT";
  public static final String SHOT_DETAIL = "SHOT_DETAIL";
  public static final String GET_SHOT_DETAIL = "GET_SHOT_DETAIL";
  public static final String SHOT_UPDATE = "SHOT_UPDATE";

  private String type;
  private int version;
  private String requestId;
  public transient boolean isActiveSubscription;
  private transient EventParams eventParams;

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
