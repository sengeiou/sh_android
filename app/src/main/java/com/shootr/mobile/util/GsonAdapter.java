package com.shootr.mobile.util;

import com.google.gson.Gson;

public class GsonAdapter implements JsonAdapter {

    private final Gson gson;

    public GsonAdapter() {
      this(new Gson());
    }

    public GsonAdapter(Gson gson) {
      this.gson = gson;
    }

    @Override
    public String toJson(Object object) {
      return gson.toJson(object);
    }

    @Override
    public <T> T fromJson(String json, Class<T> type) {
      return gson.fromJson(json, type);
    }
}
