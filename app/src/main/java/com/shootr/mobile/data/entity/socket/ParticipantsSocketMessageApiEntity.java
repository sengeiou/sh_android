package com.shootr.mobile.data.entity.socket;

import com.shootr.mobile.data.entity.ParticipantsEntity;

public class ParticipantsSocketMessageApiEntity extends SocketMessageApiEntity {

  public ParticipantsSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.PARTICIPANTS_UPDATE);
  }

  private ParticipantsEntity data;

  public ParticipantsEntity getData() {
    return data;
  }

  public void setData(ParticipantsEntity data) {
    this.data = data;
  }
}
