package com.shootr.mobile.data.entity;

public class SubscribeSocketMessageApiEntity extends SocketMessageApiEntity {

  private EventParams data;

  public SubscribeSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.SUBSCRIBE);
  }

  public EventParams getData() {
    return data;
  }

  public void setData(EventParams data) {
    this.data = data;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SubscribeSocketMessageApiEntity that = (SubscribeSocketMessageApiEntity) o;

    return getData() != null ? getData().equals(that.getData()) : that.getData() == null;
  }

  @Override public int hashCode() {
    return getData() != null ? getData().hashCode() : 0;
  }

  @Override public String toString() {
    return "SubscribeSocketMessageApiEntity{"
        + "data="
        + data
        + ", isActiveSubscription="
        + isActiveSubscription
        + '}';
  }
}
