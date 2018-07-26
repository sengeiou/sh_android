package com.shootr.mobile.data.api.entity;

import com.shootr.mobile.domain.model.PrintableType;
import java.util.Date;

public class PromotedReceiptApiEntity implements PrintableItemApiEntity {

  private String resultType;
  private String productId;
  private String data;
  private Date deleted;

  public PromotedReceiptApiEntity() {
    setResultType(PrintableType.PROMOTED_RECEIPT);
  }

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

  public Date getDeleted() {
    return deleted;
  }

  public void setDeleted(Date deleted) {
    this.deleted = deleted;
  }

  @Override public String getResultType() {
    return resultType;
  }

  @Override public void setResultType(String resultType) {
    this.resultType = resultType;
  }
}
