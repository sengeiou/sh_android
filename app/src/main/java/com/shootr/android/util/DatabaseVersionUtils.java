package com.shootr.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.shootr.android.data.dagger.ApplicationContext;
import com.shootr.android.data.prefs.IntPreference;
import com.shootr.android.data.prefs.LastDatabaseVersion;
import com.shootr.android.db.ShootrDbOpenHelper;
import com.shootr.android.domain.repository.DatabaseUtils;
import javax.inject.Inject;

public class DatabaseVersionUtils implements DatabaseUtils{

    private final IntPreference lastDatabaseVersion;
    private final Version currentVersion;
    private final SharedPreferences sharedPreferences;
    private final SQLiteOpenHelper dbOpenHelper;
    private Context context;

    @Inject public DatabaseVersionUtils(@ApplicationContext Context context, SharedPreferences sharedPreferences,
      @LastDatabaseVersion IntPreference lastDatabaseVersion, Version currentVersion, SQLiteOpenHelper dbOpenHelper) {
        this.lastDatabaseVersion = lastDatabaseVersion;
        this.currentVersion = currentVersion;
        this.sharedPreferences = sharedPreferences;
        this.context = context;
        this.dbOpenHelper = dbOpenHelper;
    }

    public void clearDataOnNewerVersion() {
        if (needsToClearData()) {
            resetAppData();
        }
    }

    @Override
    public void clearDataOnLogout() {
        resetAppData();
    }

    protected void resetAppData() {
        clearSharedPreferences();
        clearDatabase();
        updateStoredDatabaseVersion();
    }

    private void clearSharedPreferences() {
        sharedPreferences.edit().clear().apply();
        FacebookSdk.sdkInitialize(context);
        LoginManager.getInstance().logOut();
    }

    private void clearDatabase() {
        context.deleteDatabase(ShootrDbOpenHelper.DATABASE_NAME);
        dbOpenHelper.close();
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
