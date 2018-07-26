package com.shootr.mobile.domain.model;

import java.util.Date;

public class UnknownPrintableItem implements PrintableItem {

  @Override public String getResultType() {
    return PrintableItem.UNKNOWN;
  }

  @Override public Long getOrder() {
    return null;
  }

  @Override public Date getDeletedData() {
    return null;
  }

  @Override public void setDeletedData(Date deleted) {

  }

  @Override public String getMessageType() {
    return null;
  }
}
