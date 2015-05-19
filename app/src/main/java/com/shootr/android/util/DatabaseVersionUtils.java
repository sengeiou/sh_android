package com.shootr.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.data.dagger.ApplicationContext;
import com.shootr.android.data.prefs.IntPreference;
import com.shootr.android.data.prefs.LastDatabaseVersion;
import com.shootr.android.db.ShootrDbOpenHelper;
import javax.inject.Inject;

public class DatabaseVersionUtils {

    private final IntPreference lastDatabaseVersion;
    private final Version currentVersion;
    private final SharedPreferences sharedPreferences;
    private Context context;

    @Inject public DatabaseVersionUtils(@ApplicationContext Context context, SharedPreferences sharedPreferences,
      @LastDatabaseVersion IntPreference lastDatabaseVersion, Version currentVersion) {
        this.lastDatabaseVersion = lastDatabaseVersion;
        this.currentVersion = currentVersion;
        this.sharedPreferences = sharedPreferences;
        this.context = context;
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
        context.deleteDatabase(ShootrDbOpenHelper.DATABASE_NAME);
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
