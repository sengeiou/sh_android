package com.shootr.mobile.data.api.entity;

import com.shootr.mobile.data.entity.PrintableItemEntity;
import com.shootr.mobile.domain.model.PrintableType;
import java.util.Date;

public class PromotedReceiptEntity implements PrintableItemEntity {

  private String productId;
  private String data;
  private Date deleted;

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  @Override public String getResultType() {
    return PrintableType.PROMOTED_RECEIPT;
  }

  public Date getDeleted() {
    return deleted;
  }

  public void setDeleted(Date deleted) {
    this.deleted = deleted;
  }
}
