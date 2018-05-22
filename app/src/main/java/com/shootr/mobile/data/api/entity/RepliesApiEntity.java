package com.shootr.mobile.data.api.entity;

public class RepliesApiEntity {

  private ItemsApiEntity promoted;
  private ItemsApiEntity subscribers;
  private ItemsApiEntity other;

  public ItemsApiEntity getPromoted() {
    return promoted;
  }

  public void setPromoted(ItemsApiEntity promoted) {
    this.promoted = promoted;
  }

  public ItemsApiEntity getSubscribers() {
    return subscribers;
  }

  public void setSubscribers(ItemsApiEntity subscribers) {
    this.subscribers = subscribers;
  }

  public ItemsApiEntity getOther() {
    return other;
  }

  public void setOther(ItemsApiEntity basic) {
    this.other = basic;
  }
}
