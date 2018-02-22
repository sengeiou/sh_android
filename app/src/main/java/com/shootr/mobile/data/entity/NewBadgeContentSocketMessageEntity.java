package com.shootr.mobile.data.entity;

public class NewBadgeContentSocketMessageEntity extends SocketMessageEntity {

  private BadgeContentEntity data;

  public NewBadgeContentSocketMessageEntity() {
    setEventType(SocketMessageApiEntity.NEW_BADGE_CONTENT);
  }

  public BadgeContentEntity getData() {
    return data;
  }

  public void setData(BadgeContentEntity data) {
    this.data = data;
  }
}
