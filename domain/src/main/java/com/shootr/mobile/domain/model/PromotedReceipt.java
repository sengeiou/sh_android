package com.shootr.mobile.domain.model;

import java.util.Date;

public class PromotedReceipt implements PrintableItem {

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

  @Override public Long getOrder() {
    return 0L;
  }

  @Override public Date getDeletedData() {
    return deleted;
  }

  @Override public void setDeletedData(Date deleted) {
    //TODO
  }

  @Override public String getMessageType() {
    return null;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PromotedReceipt that = (PromotedReceipt) o;

    if (getProductId() != null ? !getProductId().equals(that.getProductId())
        : that.getProductId() != null) {
      return false;
    }
    return getData() != null ? getData().equals(that.getData()) : that.getData() == null;
  }

  @Override public int hashCode() {
    int result = getProductId() != null ? getProductId().hashCode() : 0;
    result = 31 * result + (getData() != null ? getData().hashCode() : 0);
    return result;
  }

  public Date getDeleted() {
    return deleted;
  }

  public void setDeleted(Date deleted) {
    this.deleted = deleted;
  }

  @Override public String toString() {
    return "PromotedReceipt{"
        + "productId='"
        + productId
        + '\''
        + ", data='"
        + data
        + '\''
        + ", deleted="
        + deleted
        + '}';
  }
}
