package com.shootr.mobile.util;

public interface FakeResponse {

  String body();

  String mediaType();

  int httpCode();
}
