package com.shootr.android;

import android.app.Application;
import android.content.Context;

import android.os.Build;
import com.crashlytics.android.Crashlytics;
import dagger.ObjectGraph;
import com.shootr.android.data.SessionManager;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.util.LogTreeFactory;
import javax.inject.Inject;
import timber.log.Timber;

public class ShootrApplication extends Application {

    private ObjectGraph objectGraph;

    @Inject SessionManager currentSession;

    @Override
    public void onCreate() {
        super.onCreate();
        if(!BuildConfig.DEBUG) Crashlytics.start(this);
        buildObjectGraphAndInject();
        plantLoggerTrees();
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
    public void inject(Object o) {
        objectGraph.inject(o);
    }

    @Deprecated
    public UserEntity getCurrentUser() {
        return currentSession.getCurrentUser();
    }

    public static ShootrApplication get(Context context) {
        return (ShootrApplication) context.getApplicationContext();
    }
}
