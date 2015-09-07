package com.shootr.android;

import android.app.Application;
import android.content.Context;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.DumperPluginsProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.dumpapp.DumperPlugin;
import com.shootr.android.util.DatabaseVersionUtils;
import com.shootr.android.util.LogTreeFactory;
import dagger.ObjectGraph;
import java.util.ArrayList;
import javax.inject.Inject;
import timber.log.Timber;

public class ShootrApplication extends Application {

    private ObjectGraph objectGraph;
    @Inject DatabaseVersionUtils databaseVersionUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        buildObjectGraphAndInject();
        plantLoggerTrees();
        if (!BuildConfig.DEBUG) {
            Crashlytics.start(this);
        }

        databaseVersionUtils.clearDataOnNewerVersion();
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
