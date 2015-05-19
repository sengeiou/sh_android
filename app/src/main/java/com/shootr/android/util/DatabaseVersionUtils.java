package com.shootr.android.util;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import com.shootr.android.data.prefs.IntPreference;
import com.shootr.android.data.prefs.LastDatabaseVersion;
import com.shootr.android.db.ShootrDbOpenHelper;
import javax.inject.Inject;

public class DatabaseVersionUtils {

    private final IntPreference databaseVersionPreference;
    private final Version version;

    @Inject public DatabaseVersionUtils(@LastDatabaseVersion
        IntPreference databaseVersionPreference, Version version){
        this.databaseVersionPreference = databaseVersionPreference;
        this.version = version;
    }

    public void manageCurrentDatabaseVersion(ShootrDbOpenHelper shootrDbOpenHelper,
      SharedPreferences sharedPreferences) {
        int databaseVersion = getDatabaseVersion();
        if (isNecessaryToUpdateDatabase(databaseVersion)) {
            clearPreferences(sharedPreferences);
            upgradeDatabase(shootrDbOpenHelper, databaseVersion);
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

    private void upgradeDatabase(ShootrDbOpenHelper shootrDbOpenHelper, int databaseVersion) {
        SQLiteDatabase db = shootrDbOpenHelper.getWritableDatabase();
        shootrDbOpenHelper.onUpgrade(db, databaseVersion, databaseVersionPreference.get());
    }

    private boolean isNecessaryToUpdateDatabase(int databaseVersion) {
        return databaseVersion < version.getDatabaseVersion();
    }

}
