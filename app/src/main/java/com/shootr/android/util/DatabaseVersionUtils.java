package com.shootr.android.util;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import com.shootr.android.data.prefs.IntPreference;
import com.shootr.android.data.prefs.PreferencesDatabaseVersion;
import com.shootr.android.db.ShootrDbOpenHelper;
import javax.inject.Inject;

public class DatabaseVersionUtils {

    private final IntPreference preferencesDatabaseVersion;
    private final DatabaseBuildVersionUtil databaseBuildVersionUtil;

    @Inject public DatabaseVersionUtils(@PreferencesDatabaseVersion
        IntPreference preferencesDatabaseVersion, DatabaseBuildVersionUtil databaseBuildVersionUtil){
        this.preferencesDatabaseVersion = preferencesDatabaseVersion;
        this.databaseBuildVersionUtil = databaseBuildVersionUtil;
    }

    public void manageCurrentDatabaseVersion(ShootrDbOpenHelper shootrDbOpenHelper,
      SharedPreferences sharedPreferences) {
        int databaseVersion = getDatabaseVersion();
        if (isNecessaryToUpdateDatabase(databaseVersion)) {
            clearPreferences(sharedPreferences);
            upgradeDatabase(shootrDbOpenHelper, databaseVersion);
            updateDatabaseVersionInPreferences();
        }
    }

    private int getDatabaseVersion() {
        return preferencesDatabaseVersion.get();
    }

    private void updateDatabaseVersionInPreferences() {
        int databaseVersion = databaseBuildVersionUtil.getDatabaseVersionFromBuild();
        preferencesDatabaseVersion.set(databaseVersion);
    }

    private void clearPreferences(SharedPreferences sharedPreferences) {
        sharedPreferences.edit().clear().apply();
    }

    private void upgradeDatabase(ShootrDbOpenHelper shootrDbOpenHelper, int databaseVersion) {
        SQLiteDatabase db = shootrDbOpenHelper.getWritableDatabase();
        shootrDbOpenHelper.onUpgrade(db, databaseVersion, preferencesDatabaseVersion.get());
    }

    private boolean isNecessaryToUpdateDatabase(int databaseVersion) {
        return databaseVersion < databaseBuildVersionUtil.getDatabaseVersionFromBuild();
    }

}
