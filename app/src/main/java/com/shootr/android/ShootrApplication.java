package com.shootr.android;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.shootr.android.data.prefs.IntPreference;
import com.shootr.android.data.prefs.LastDatabaseVersionCompatible;
import com.shootr.android.db.ShootrDbOpenHelper;
import com.shootr.android.util.LogTreeFactory;
import dagger.ObjectGraph;
import javax.inject.Inject;
import timber.log.Timber;

public class ShootrApplication extends Application {

    private ObjectGraph objectGraph;
    @Inject @LastDatabaseVersionCompatible IntPreference lastCompatibleDatabaseVersion;

    @Override
    public void onCreate() {
        super.onCreate();
        buildObjectGraphAndInject();
        plantLoggerTrees();
        if(!BuildConfig.DEBUG) {
            Crashlytics.start(this);
        }
        Stetho.initialize(Stetho.newInitializerBuilder(this)
            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
            .build());

        ShootrDbOpenHelper shootrDbOpenHelper = new ShootrDbOpenHelper(getApplicationContext(),null);
        SharedPreferences sharedPreferences = getSharedPreferences("DatabaseVersionPreferences", MODE_PRIVATE);
        int databaseVersion = sharedPreferences.getInt("databaseVersion", 0);
        if(databaseVersion == 0){
            databaseVersion = BuildConfig.DATABASE_VERSION;
        }
        if (databaseVersion <= lastCompatibleDatabaseVersion.get()) {
            sharedPreferences.edit().clear().apply();
            SQLiteDatabase db = shootrDbOpenHelper.getWritableDatabase();
            shootrDbOpenHelper.onUpgrade(db, databaseVersion, lastCompatibleDatabaseVersion.get());
            databaseVersion = BuildConfig.DATABASE_VERSION;
            sharedPreferences.edit().putInt("databaseVersion",databaseVersion).apply();
        }
    }

    public void plantLoggerTrees() {
        LogTreeFactory logTreeFactory = objectGraph.get(LogTreeFactory.class);
        for (Timber.Tree tree : logTreeFactory.getTrees()) {
            Timber.plant(tree);
        }
    }

    public void buildObjectGraphAndInject() {
        objectGraph = ObjectGraph.create(Modules.list(this));
        objectGraph.inject(this);
    }

    public ObjectGraph getObjectGraph() {
        return objectGraph;
    }

    /**
     * Injects the members of {@code instance}, including injectable members
     * inherited from its supertypes.
     *
     * @throws IllegalArgumentException  if the runtime type of {@code instance} is
     *     not one of this object graph's {@link dagger.Module#injects injectable types}.
     */
    public <T> void inject(T o) {
        objectGraph.inject(o);
    }

    public static ShootrApplication get(Context context) {
        return (ShootrApplication) context.getApplicationContext();
    }
}
