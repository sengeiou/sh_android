package com.shootr.mobile.data.entity;

import com.shootr.mobile.data.entity.socket.SocketMessageApiEntity;

public class GetPromotedTermsSocketMessageApiEntity extends SocketMessageApiEntity {

  private Params data;

  public GetPromotedTermsSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.GET_PROMOTED_TERMS);
  }

  public Params getData() {
    return data;
  }

  public void setData(Params data) {
    this.data = data;
  }

  public static class Params {
    private String idStream;

    public String getIdStream() {
      return idStream;
    }

    public void setIdStream(String idStream) {
      this.idStream = idStream;
    }
  }
}
