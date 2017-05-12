package com.shootr.mobile.util;

public interface JsonAdapter {

  String toJson(Object object);

  <T> T fromJson(String json, Class<T> type);
}
