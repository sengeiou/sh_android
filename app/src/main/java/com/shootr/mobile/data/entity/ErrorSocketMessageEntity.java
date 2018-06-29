package com.shootr.mobile.data.entity;


public class ErrorSocketMessageEntity extends SocketMessageEntity {

  private ErrorEntity data;

  public ErrorSocketMessageEntity() {
    setEventType(SocketMessageApiEntity.ERROR);
  }

  public ErrorEntity getData() {
    return data;
  }

  public void setData(ErrorEntity data) {
    this.data = data;
  }
}
