package com.shootr.mobile.domain.messages;

public interface Message {

  String SUBSCRIBE = "SUBSCRIBE";
  String UNSUBSCRIBE = "UNSUBSCRIBE";
  String TIMELINE = "TIMELINE";
  String SHOT = "SHOT";
  String POLL = "POLL";
  String UNKNOWN = "UNKNOWN";

  String getMessageType();

}
