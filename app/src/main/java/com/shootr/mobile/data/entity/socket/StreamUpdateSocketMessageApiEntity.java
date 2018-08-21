package com.shootr.mobile.data.entity.socket;

import com.shootr.mobile.data.api.entity.PrintableItemApiEntity;

public class StreamUpdateSocketMessageApiEntity extends SocketMessageApiEntity {

  public StreamUpdateSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.STREAM_UPDATE);
  }

  private PrintableItemApiEntity data;

  public PrintableItemApiEntity getData() {
    return data;
  }

  public void setData(PrintableItemApiEntity data) {
    this.data = data;
  }
}
