package com.shootr.mobile.data.prefs;

import android.content.SharedPreferences;

public class LongPreference {

    private final SharedPreferences preferences;
    private final String key;
    private final long defaultValue;

    public LongPreference(SharedPreferences preferences, String key) {
        this(preferences, key, 0L);
    }

    public LongPreference(SharedPreferences preferences, String key, long defaultValue) {
        this.preferences = preferences;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public long get() {
        return preferences.getLong(key, defaultValue);
    }

    public boolean isSet() {
        return preferences.contains(key) && preferences.getLong(key, -1L) != -1L;
    }

    public void set(long value) {
        preferences.edit().putLong(key, value).apply();
    }

    public void delete() {
        preferences.edit().remove(key).apply();
    }
}
