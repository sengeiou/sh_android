package com.shootr.android.util;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.data.prefs.IntPreference;
import com.shootr.android.data.prefs.LastDatabaseVersion;
import javax.inject.Inject;

public class DatabaseVersionUtils {

    private final IntPreference databaseVersionPreference;
    private final Version version;
    private final SQLiteOpenHelper sqliteOpenHelper;
    private final SharedPreferences sharedPreferences;

    @Inject public DatabaseVersionUtils(@LastDatabaseVersion IntPreference databaseVersionPreference, Version version,
      SQLiteOpenHelper sqliteOpenHelper, SharedPreferences sharedPreferences) {
        this.databaseVersionPreference = databaseVersionPreference;
        this.version = version;
        this.sqliteOpenHelper = sqliteOpenHelper;
        this.sharedPreferences = sharedPreferences;
    }

    public void manageCurrentDatabaseVersion() {
        int databaseVersion = getDatabaseVersion();
        if (isNecessaryToUpdateDatabase(databaseVersion)) {
            clearPreferences(sharedPreferences);
            upgradeDatabase(databaseVersion);
            updateStoredDatabaseVersion();
        }
    }

    private int getDatabaseVersion() {
        return databaseVersionPreference.get();
    }

    private void updateStoredDatabaseVersion() {
        int databaseVersion = version.getDatabaseVersion();
        databaseVersionPreference.set(databaseVersion);
    }

    private void clearPreferences(SharedPreferences sharedPreferences) {
        sharedPreferences.edit().clear().apply();
    }

    private void upgradeDatabase(int databaseVersion) {
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
        sqliteOpenHelper.onUpgrade(db, databaseVersion, databaseVersionPreference.get());
    }

    private boolean isNecessaryToUpdateDatabase(int databaseVersion) {
        return databaseVersion < version.getDatabaseVersion();
    }
}
