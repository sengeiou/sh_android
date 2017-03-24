package com.shootr.mobile.data.prefs;

import android.content.SharedPreferences;
import com.google.gson.Gson;

public class ShowcasePreference {

  private final SharedPreferences preferences;
  private final String key;
  private final ShowcaseStatus value;

  public ShowcasePreference(SharedPreferences sharedPreferences, String key, ShowcaseStatus value) {
    this.preferences = sharedPreferences;
    this.key = key;
    this.value = value;
  }

  public ShowcaseStatus get() {
    Gson gson = new Gson();
    String defaultjson = gson.toJson(value);
    String json = preferences.getString(key, defaultjson);
    return gson.fromJson(json, ShowcaseStatus.class);
  }

  public boolean isSet() {
    return preferences.contains(key);
  }

  public void set(ShowcaseStatus value) {
    Gson gson = new Gson();
    String json = gson.toJson(value);
    preferences.edit().putString(key, json).apply();
  }

  public void delete() {
    preferences.edit().remove(key).apply();
  }

}
