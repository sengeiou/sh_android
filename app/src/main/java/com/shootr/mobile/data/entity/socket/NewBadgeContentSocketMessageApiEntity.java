package com.shootr.mobile.data.entity.socket;

import com.shootr.mobile.data.entity.BadgeContentApiEntity;

public class NewBadgeContentSocketMessageApiEntity extends SocketMessageApiEntity {

  public NewBadgeContentSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.NEW_BADGE_CONTENT);
  }

  private BadgeContentApiEntity data;

  public BadgeContentApiEntity getData() {
    return data;
  }

  public void setData(BadgeContentApiEntity data) {
    this.data = data;
  }
}
