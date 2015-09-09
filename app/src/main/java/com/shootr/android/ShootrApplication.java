package com.shootr.android;

import android.app.Application;
import android.content.Context;
import com.shootr.android.util.CrashReportTool;
import com.shootr.android.util.DatabaseVersionUtils;
import com.shootr.android.util.LogTreeFactory;
import dagger.ObjectGraph;
import javax.inject.Inject;
import timber.log.Timber;

public class ShootrApplication extends Application {

    private ObjectGraph objectGraph;
    @Inject DatabaseVersionUtils databaseVersionUtils;
    @Inject CrashReportTool crashReportTool;

    @Override
    public void onCreate() {
        super.onCreate();
        buildObjectGraphAndInject();
        plantLoggerTrees();
        crashReportTool.init(this);
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
