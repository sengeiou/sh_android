package com.shootr.mobile.data.entity.socket;

import com.shootr.mobile.data.entity.PrintableItemEntity;

public class StreamUpdateSocketMessageEntity extends SocketMessageEntity {

  public StreamUpdateSocketMessageEntity() {
    setEventType(SocketMessageApiEntity.STREAM_UPDATE);
  }

  private PrintableItemEntity data;

  public PrintableItemEntity getData() {
    return data;
  }

  public void setData(PrintableItemEntity data) {
    this.data = data;
  }
}
