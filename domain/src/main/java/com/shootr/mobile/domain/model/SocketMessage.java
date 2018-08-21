package com.shootr.mobile.domain.model;

public class SocketMessage {

  public static final String TIMELINE = "TIMELINE";
  public static final String NEW_ITEM_DATA = "NEW_ITEM_DATA";
  public static final String UPDATE_ITEM_DATA = "UPDATE_ITEM_DATA";
  public static final String PINNED_ITEMS = "PINNED_ITEMS";
  public static final String FIXED_ITEMS = "FIXED_ITEMS";
  public static final String SUBSCRIBE = "SUBSCRIBE";
  public static final String UNSUBSCRIBE = "UNSUBSCRIBE";
  public static final String PARTICIPANTS_UPDATE = "PARTICIPANTS_UPDATE";
  public static final String NEW_BADGE_CONTENT = "NEW_BADGE_CONTENT";
  public static final String SHOT_DETAIL = "SHOT_DETAIL";
  public static final String SHOT_UPDATE = "SHOT_UPDATE";
  public static final String CREATED_SHOT = "CREATED_SHOT";
  public static final String STREAM_UPDATE = "STREAM_UPDATE";
  public static final String ERROR = "ERROR";
  public static final String PROMOTED_TIERS = "PROMOTED_TIERS";
  public static final String PARTIAL_UPDATE_ITEM_DATA = "PARTIAL_UPDATE_ITEM_DATA";

  private String type;
  private int version;
  private String requestId;
  public boolean isActiveSubscription;
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

  @Override public String toString() {
    return "SocketMessage{"
        + "type='"
        + type
        + '\''
        + ", version="
        + version
        + ", requestId='"
        + requestId
        + '\''
        + '}';
  }
}
