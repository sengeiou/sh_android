package com.shootr.mobile.data.entity;

public class RepliesEntity {

  private ItemsEntity promoted;
  private ItemsEntity subscribers;
  private ItemsEntity basic;

  public ItemsEntity getPromoted() {
    return promoted;
  }

  public void setPromoted(ItemsEntity promoted) {
    this.promoted = promoted;
  }

  public ItemsEntity getSubscribers() {
    return subscribers;
  }

  public void setSubscribers(ItemsEntity subscribers) {
    this.subscribers = subscribers;
  }

  public ItemsEntity getBasic() {
    return basic;
  }

  public void setBasic(ItemsEntity basic) {
    this.basic = basic;
  }
}
