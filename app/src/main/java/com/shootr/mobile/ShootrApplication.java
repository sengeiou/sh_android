package com.shootr.mobile;

import android.content.Context;
import android.graphics.Point;
import android.support.multidex.MultiDexApplication;
import android.view.Display;
import android.view.WindowManager;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import com.crashlytics.android.answers.Answers;
import com.shootr.mobile.data.background.sockets.WebSocketService;
import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.domain.bus.CloseSocketEvent;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.activities.ErrorActivity;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.CrashReportTool;
import com.shootr.mobile.util.DatabaseVersionUtils;
import com.shootr.mobile.util.LogTreeFactory;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;
import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;
import javax.inject.Inject;
import timber.log.Timber;

public class ShootrApplication extends MultiDexApplication implements InternetConnectivityListener {

    public static final Point SCREEN_SIZE = new Point();
    private ObjectGraph objectGraph;
    @Inject DatabaseVersionUtils databaseVersionUtils;
    @Inject CrashReportTool crashReportTool;
    @Inject AnalyticsTool analyticsTool;
    @Inject SessionRepository sessionRepository;
    @Inject BusPublisher busPublisher;

    private boolean socketClosedForInternetProblems = false;
    private boolean inBackground = true;

    @Override public void onCreate() {
        super.onCreate();
        buildObjectGraphAndInject();
        plantLoggerTrees();
        databaseVersionUtils.clearDataOnNewerVersion();
        analyticsTool.init(this);
        CustomActivityOnCrash.install(this);
        CustomActivityOnCrash.setErrorActivityClass(ErrorActivity.class);
        crashReportTool.init(this);
        Fabric.with(this, new Answers());
        WindowManager manager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        if (manager != null) {
            Display display = manager.getDefaultDisplay();
            display.getSize(SCREEN_SIZE);
        }

        BackgroundManager.get(this).registerListener(appActivityListener);
        InternetAvailabilityChecker.init(this);
        InternetAvailabilityChecker internetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        internetAvailabilityChecker.addInternetConnectivityListener(this);
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

    private BackgroundManager.Listener appActivityListener = new BackgroundManager.Listener() {
        public void onBecameForeground() {
            inBackground = false;
            if (sessionRepository.getCurrentUserId() != null
                && !sessionRepository.getCurrentUserId().isEmpty()) {
                WebSocketService.startService(getApplicationContext());
            }
        }

        public void onBecameBackground() {
            inBackground = true;
            busPublisher.post(new CloseSocketEvent.Event());
        }

        @Override public void onStartActivity() {
          /* no-op */
        }
    };

    @Override public void onInternetConnectivityChanged(boolean isConnected) {
        if (!inBackground) {
            if (!isConnected) {
                if (sessionRepository.getCurrentUserId() != null
                    && !sessionRepository.getCurrentUserId().isEmpty()) {
                    socketClosedForInternetProblems = true;
                    busPublisher.post(new CloseSocketEvent.Event());
                }
            } else if (socketClosedForInternetProblems && isConnected) {
                if (sessionRepository.getCurrentUserId() != null
                    && !sessionRepository.getCurrentUserId().isEmpty()) {
                    WebSocketService.startService(getApplicationContext());
                    socketClosedForInternetProblems = false;
                }
            }
        }
    }
}
