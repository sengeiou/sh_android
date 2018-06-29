package com.shootr.mobile.data.entity;


public class CreateShotSocketMessageApiService extends SocketMessageApiEntity {

  private NewShotContentApiEntity data;

  public CreateShotSocketMessageApiService() {
    setEventType(SocketMessageApiEntity.CREATE_SHOT);
  }

  public NewShotContentApiEntity getData() {
    return data;
  }

  public void setData(NewShotContentApiEntity data) {
    this.data = data;
  }
}
