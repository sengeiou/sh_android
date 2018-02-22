package com.shootr.mobile.domain.model;


public class ParticipantsSocketMessage extends SocketMessage {

  public ParticipantsSocketMessage() {
    setEventType(SocketMessage.PARTICIPANTS_UPDATE);
  }

  private Participants data;

  public Participants getData() {
    return data;
  }

  public void setData(Participants data) {
    this.data = data;
  }

}

