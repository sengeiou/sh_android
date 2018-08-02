package com.shootr.mobile.data.entity;

import com.shootr.mobile.data.entity.socket.SocketMessageApiEntity;

public class AcceptPromotedTermsSocketMessageApiEntity extends SocketMessageApiEntity {

  private Params data;

  public AcceptPromotedTermsSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.ACCEPT_PROMOTED_TERMS);
  }

  public Params getData() {
    return data;
  }

  public void setData(Params data) {
    this.data = data;
  }

  public static class Params {
    private String idStream;
    private int version;

    public String getIdStream() {
      return idStream;
    }

    public void setIdStream(String idStream) {
      this.idStream = idStream;
    }

    public int getVersion() {
      return version;
    }

    public void setVersion(int version) {
      this.version = version;
    }
  }
}
