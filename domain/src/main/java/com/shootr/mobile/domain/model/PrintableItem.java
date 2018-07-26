package com.shootr.mobile.domain.model;

import com.shootr.mobile.domain.messages.Message;
import java.util.Date;

public interface PrintableItem extends Message {

  String getResultType();

  Long getOrder();

  Date getDeletedData();

  void setDeletedData(Date deleted);
}
