package com.shootr.mobile.data.entity;

import com.shootr.mobile.data.api.entity.PromotedReceiptApiEntity;
import com.shootr.mobile.data.api.entity.PromotedTierApiEntity;
import java.util.List;

public class PromotedTiersApiEntity {

  private List<PromotedTierApiEntity> promotedTiers;
  private List<PromotedReceiptApiEntity> pendingReceipts;

  public List<PromotedTierApiEntity> getData() {
    return promotedTiers;
  }

  public void setData(List<PromotedTierApiEntity> data) {
    this.promotedTiers = promotedTiers;
  }

  public List<PromotedReceiptApiEntity> getPendingReceipts() {
    return pendingReceipts;
  }

  public void setPendingReceipts(List<PromotedReceiptApiEntity> pendingReceipts) {
    this.pendingReceipts = pendingReceipts;
  }
}
