package com.shootr.mobile.domain.model;

public interface PrintableType {

  String SHOT = "SHOT";

  String TOPIC = "TOPIC";

  String POLL = "POLL";

  String EXTERNAL_VIDEO = "EXTERNAL_VIDEO";

  String PROMOTED_RECEIPT = "PROMOTED_RECEIPT";

  String USER = "USER";

  String STREAM = "STREAM";

  String UNKNOWN = "UNKNOWN";

  String[] PRINTABLE_TYPES = {
      SHOT, TOPIC, POLL, EXTERNAL_VIDEO, USER, STREAM
  };
}
