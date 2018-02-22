package com.shootr.mobile.data.entity;

public class EventParams {

  private String subscriptionType;
  private String idStream;
  private String filter;
  private String idShot;

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

  public String getIdShot() {
    return idShot;
  }

  public void setIdShot(String idShot) {
    this.idShot = idShot;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    EventParams that = (EventParams) o;

    if (!getSubscriptionType().equals(that.getSubscriptionType())) return false;
    if (getIdStream() != null ? !getIdStream().equals(that.getIdStream())
        : that.getIdStream() != null) {
      return false;
    }
    if (getFilter() != null ? !getFilter().equals(that.getFilter()) : that.getFilter() != null) {
      return false;
    }
    return getIdShot() != null ? getIdShot().equals(that.getIdShot()) : that.getIdShot() == null;
  }

  @Override public int hashCode() {
    int result = getSubscriptionType().hashCode();
    result = 31 * result + (getIdStream() != null ? getIdStream().hashCode() : 0);
    result = 31 * result + (getFilter() != null ? getFilter().hashCode() : 0);
    result = 31 * result + (getIdShot() != null ? getIdShot().hashCode() : 0);
    return result;
  }

  @Override public String toString() {
    return "EventParams{"
        + "subscriptionType='"
        + subscriptionType
        + '\''
        + ", idStream='"
        + idStream
        + '\''
        + ", filter='"
        + filter
        + '\''
        + ", idShot='"
        + idShot
        + '\''
        + '}';
  }
}
