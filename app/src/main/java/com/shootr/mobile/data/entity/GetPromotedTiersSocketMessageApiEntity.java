package com.shootr.mobile.data.entity;

import com.shootr.mobile.data.entity.socket.SocketMessageApiEntity;

public class GetPromotedTiersSocketMessageApiEntity extends SocketMessageApiEntity {

  private Params data;

  public GetPromotedTiersSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.GET_PROMOTED_TIERS);
  }

  public Params getData() {
    return data;
  }

  public void setData(Params data) {
    this.data = data;
  }

  private class Params {
    private String idStream;

    public String getIdStream() {
      return idStream;
    }

    public void setIdStream(String idStream) {
      this.idStream = idStream;
    }
  }
}
