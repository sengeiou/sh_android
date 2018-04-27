package com.shootr.mobile.domain.model;

import com.shootr.mobile.domain.messages.Message;

public interface PrintableItem extends Message {

  String getResultType();

  Long getOrder();
}
