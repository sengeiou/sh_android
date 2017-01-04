package com.shootr.mobile;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import com.crashlytics.android.Crashlytics;
import com.shootr.mobile.ui.activities.ErrorActivity;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.CrashReportTool;
import com.shootr.mobile.util.DatabaseVersionUtils;
import com.shootr.mobile.util.LogTreeFactory;
import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;
import javax.inject.Inject;
import timber.log.Timber;

public class ShootrApplication extends MultiDexApplication {

    private ObjectGraph objectGraph;
    @Inject DatabaseVersionUtils databaseVersionUtils;
    @Inject CrashReportTool crashReportTool;
    @Inject AnalyticsTool analyticsTool;

    @Override public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        buildObjectGraphAndInject();
        plantLoggerTrees();
        databaseVersionUtils.clearDataOnNewerVersion();
        analyticsTool.init(this);
        CustomActivityOnCrash.install(this);
        CustomActivityOnCrash.setErrorActivityClass(ErrorActivity.class);
        crashReportTool.init(this);
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
     * @throws IllegalArgumentException if the runtime type of {@code instance} is
     * not one of this object graph's {@link dagger.Module#injects injectable types}.
     */
    public <T> void inject(T o) {
        objectGraph.inject(o);
    }

    public static ShootrApplication get(Context context) {
        return (ShootrApplication) context.getApplicationContext();
    }
}
