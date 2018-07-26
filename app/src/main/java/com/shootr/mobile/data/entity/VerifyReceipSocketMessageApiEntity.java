package com.shootr.mobile.data.entity;

import com.shootr.mobile.data.entity.socket.SocketMessageApiEntity;

public class VerifyReceipSocketMessageApiEntity extends SocketMessageApiEntity {

  private ReceiptData data;

  public VerifyReceipSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.CREATE_RECEIPT);
  }

  public ReceiptData getData() {
    return data;
  }

  public void setData(ReceiptData data) {
    this.data = data;
  }

  public static class ReceiptData {
    private String receipt;
    private String type;

    public String getReceipt() {
      return receipt;
    }

    public void setReceipt(String receipt) {
      this.receipt = receipt;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }
  }
}
