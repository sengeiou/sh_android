package com.shootr.mobile.domain.model;

public class NewBadgeContentSocketMessage extends SocketMessage {

  public NewBadgeContentSocketMessage() {
    setEventType(SocketMessage.NEW_BADGE_CONTENT);
  }

  private BadgeContent data;

  public BadgeContent getData() {
    return data;
  }

  public void setData(BadgeContent data) {
    this.data = data;
  }
}
