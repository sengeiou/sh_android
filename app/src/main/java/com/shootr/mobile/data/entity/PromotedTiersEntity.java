package com.shootr.mobile.data.entity;

import com.shootr.mobile.data.api.entity.PromotedReceiptEntity;
import java.util.List;

public class PromotedTiersEntity {

  private List<PromotedTierEntity> data;
  private List<PromotedReceiptEntity> pendingReceipts;

  public List<PromotedTierEntity> getData() {
    return data;
  }

  public void setData(List<PromotedTierEntity> data) {
    this.data = data;
  }

  public List<PromotedReceiptEntity> getPendingReceipts() {
    return pendingReceipts;
  }

  public void setPendingReceipts(List<PromotedReceiptEntity> pendingReceipts) {
    this.pendingReceipts = pendingReceipts;
  }
}
