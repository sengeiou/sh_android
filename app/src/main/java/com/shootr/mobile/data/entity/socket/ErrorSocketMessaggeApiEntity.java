package com.shootr.mobile.data.entity.socket;

import com.shootr.mobile.data.entity.ErrorApiEntity;

public class ErrorSocketMessaggeApiEntity extends SocketMessageApiEntity {

  private ErrorApiEntity data;

  public ErrorSocketMessaggeApiEntity() {
    setEventType(SocketMessageApiEntity.ERROR);
  }

  public ErrorApiEntity getData() {
    return data;
  }

  public void setData(ErrorApiEntity data) {
    this.data = data;
  }
}
