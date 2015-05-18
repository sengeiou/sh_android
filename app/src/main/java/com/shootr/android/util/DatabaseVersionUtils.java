package com.shootr.android.util;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import com.shootr.android.BuildConfig;
import com.shootr.android.data.prefs.IntPreference;
import com.shootr.android.data.prefs.LastDatabaseVersionCompatible;
import com.shootr.android.db.ShootrDbOpenHelper;
import javax.inject.Inject;

public class DatabaseVersionUtils {

    @Inject @LastDatabaseVersionCompatible IntPreference lastCompatibleDatabaseVersion;

    @Inject public DatabaseVersionUtils(){

    }

    public void manageCurrentDatabaseVersion(ShootrDbOpenHelper shootrDbOpenHelper,
      SharedPreferences sharedPreferences) {
        int databaseVersion = getDatabaseVersion(sharedPreferences);

        if(thereIsNoDatabaseVersionInPreferences(databaseVersion)){
            databaseVersion = BuildConfig.DATABASE_VERSION;
        }

        if (isNecessaryToUpdateDatabase(databaseVersion)) {
            clearPreferences(sharedPreferences);
            upgradeDatabase(shootrDbOpenHelper, databaseVersion);
            updateDatabaseVersionInPreferences(sharedPreferences);
        }
    }

    private int getDatabaseVersion(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt("databaseVersion", 0);
    }

    private boolean thereIsNoDatabaseVersionInPreferences(int databaseVersion) {
        return databaseVersion == 0;
    }

    private void updateDatabaseVersionInPreferences(SharedPreferences sharedPreferences) {
        int databaseVersion;
        databaseVersion = BuildConfig.DATABASE_VERSION;
        sharedPreferences.edit().putInt("databaseVersion",databaseVersion).apply();
    }

    private void clearPreferences(SharedPreferences sharedPreferences) {
        sharedPreferences.edit().clear().apply();
    }

    private void upgradeDatabase(ShootrDbOpenHelper shootrDbOpenHelper, int databaseVersion) {
        SQLiteDatabase db = shootrDbOpenHelper.getWritableDatabase();
        shootrDbOpenHelper.onUpgrade(db, databaseVersion, lastCompatibleDatabaseVersion.get());
    }

    private boolean isNecessaryToUpdateDatabase(int databaseVersion) {
        return databaseVersion <= lastCompatibleDatabaseVersion.get();
    }

}
