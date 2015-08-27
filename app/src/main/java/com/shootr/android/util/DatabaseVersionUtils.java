package com.shootr.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.FacebookController;
import com.shootr.android.data.dagger.ApplicationContext;
import com.shootr.android.data.prefs.BooleanPreference;
import com.shootr.android.data.prefs.IntPreference;
import com.shootr.android.data.prefs.LastDatabaseVersion;
import com.shootr.android.data.prefs.ShouldShowIntro;
import com.shootr.android.db.ShootrDbOpenHelper;
import com.shootr.android.domain.repository.DatabaseUtils;
import javax.inject.Inject;

public class DatabaseVersionUtils implements DatabaseUtils{

    private final IntPreference lastDatabaseVersion;
    private final Version currentVersion;
    private final SharedPreferences sharedPreferences;
    private final SQLiteOpenHelper dbOpenHelper;
    private final FacebookController facebookController;
    private final Context context;
    private final BooleanPreference shouldShowIntro;

    @Inject public DatabaseVersionUtils(@ApplicationContext Context context, SharedPreferences sharedPreferences,
      @LastDatabaseVersion IntPreference lastDatabaseVersion, Version currentVersion, SQLiteOpenHelper dbOpenHelper,
      FacebookController facebookController, @ShouldShowIntro BooleanPreference shouldShowIntro) {
        this.lastDatabaseVersion = lastDatabaseVersion;
        this.currentVersion = currentVersion;
        this.sharedPreferences = sharedPreferences;
        this.context = context;
        this.dbOpenHelper = dbOpenHelper;
        this.facebookController = facebookController;
        this.shouldShowIntro = shouldShowIntro;
    }

    public void clearDataOnNewerVersion() {
        if (needsToClearData()) {
            resetAppData();
        }
    }

    @Override
    public void clearDataOnLogout() {
        resetAppData();
        shouldShowIntro.set(false);
    }

    protected void resetAppData() {
        clearSharedPreferences();
        clearDatabase();
        updateStoredDatabaseVersion();
    }

    private void clearSharedPreferences() {
        sharedPreferences.edit().clear().apply();
        facebookController.logout();
    }

    private void clearDatabase() {
        dbOpenHelper.close();
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
