package com.shootr.android.util;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.data.prefs.IntPreference;
import com.shootr.android.data.prefs.LastDatabaseVersion;
import javax.inject.Inject;

public class DatabaseVersionUtils {

    private final IntPreference lastDatabaseVersion;
    private final Version currentVersion;
    private final SQLiteOpenHelper sqliteOpenHelper;
    private final SharedPreferences sharedPreferences;

    @Inject public DatabaseVersionUtils(@LastDatabaseVersion IntPreference lastDatabaseVersion, Version currentVersion,
      SQLiteOpenHelper sqliteOpenHelper, SharedPreferences sharedPreferences) {
        this.lastDatabaseVersion = lastDatabaseVersion;
        this.currentVersion = currentVersion;
        this.sqliteOpenHelper = sqliteOpenHelper;
        this.sharedPreferences = sharedPreferences;
    }

    public void clearDataOnDatabaseVersionUpdated() {
        if (needsToClearData()) {
            clearSharedPreferences();
            clearDatabase();
            updateStoredDatabaseVersion();
        }
    }

    private void clearSharedPreferences() {
        sharedPreferences.edit().clear().apply();
    }

    private void clearDatabase() {
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
        sqliteOpenHelper.onUpgrade(db, currentVersion.getDatabaseVersion(), lastDatabaseVersion.get());
    }

    private void updateStoredDatabaseVersion() {
        int databaseVersion = currentVersion.getDatabaseVersion();
        lastDatabaseVersion.set(databaseVersion);
    }

    private boolean needsToClearData() {
        int lastDatabaseVersion = this.lastDatabaseVersion.get();
        int currentDatabaseVersion = currentVersion.getDatabaseVersion();
        return lastDatabaseVersion < currentDatabaseVersion;
    }
}
