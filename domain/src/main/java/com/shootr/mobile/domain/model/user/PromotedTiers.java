package com.shootr.mobile.domain.model.user;

import com.shootr.mobile.domain.model.PromotedReceipt;
import com.shootr.mobile.domain.model.PromotedTier;
import java.util.List;

public class PromotedTiers {

  private List<PromotedTier> data;
  private List<PromotedReceipt> pendingReceipts;

  public List<PromotedTier> getData() {
    return data;
  }

  public void setData(List<PromotedTier> data) {
    this.data = data;
  }

  public List<PromotedReceipt> getPendingReceipts() {
    return pendingReceipts;
  }

  public void setPendingReceipts(List<PromotedReceipt> pendingReceipts) {
    this.pendingReceipts = pendingReceipts;
  }

  @Override public String toString() {
    return "PromotedTiers{" + "data=" + data + ", pendingReceipts=" + pendingReceipts + '}';
  }
}
