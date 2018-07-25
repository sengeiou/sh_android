package com.shootr.mobile.domain.model;

public interface PrintableType {

  String SHOT = "SHOT";

  String TOPIC = "TOPIC";

  String POLL = "POLL";

  String EXTERNAL_VIDEO = "EXTERNAL_VIDEO";

  String STREAM = "STREAM";

  String[] PRINTABLE_TYPES = {
      SHOT, TOPIC, POLL, EXTERNAL_VIDEO, STREAM
  };
}
