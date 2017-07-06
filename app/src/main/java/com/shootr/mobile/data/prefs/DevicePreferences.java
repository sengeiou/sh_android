package com.shootr.mobile.data.prefs;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.shootr.mobile.domain.model.Device;

public class DevicePreferences {

  private final SharedPreferences preferences;
  private final String key;
  private final Device value;

  public DevicePreferences(SharedPreferences sharedPreferences, String key, Device value) {
    this.preferences = sharedPreferences;
    this.key = key;
    this.value = value;
  }

  public Device get() {
    Gson gson = new Gson();
    String defaultjson = gson.toJson(value);
    String json = preferences.getString(key, defaultjson);
    return gson.fromJson(json, Device.class);
  }

  public boolean isSet() {
    return preferences.contains(key);
  }

  public void set(Device value) {
    Gson gson = new Gson();
    String json = gson.toJson(value);
    preferences.edit().putString(key, json).apply();
  }

  public void delete() {
    preferences.edit().remove(key).apply();
  }

}
