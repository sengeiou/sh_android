package gm.mobi.android;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.log.CustomLogger;

import dagger.ObjectGraph;
import gm.mobi.android.util.FileLogger;
import timber.log.Timber;

public class GolesApplication extends Application {

    private static GolesApplication instance;
    private JobManager jobManager;
    private ObjectGraph objectGraph;

    public GolesApplication() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        configureJobManager();
        buildObjectGraphAndInject();
        plantTrees();
    }

    private void plantTrees() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            Timber.plant(new FileLogger.FileLogTree());
        } else {
            //TODO Crashlytics tree
        }

    }

    private void configureJobManager() {
        // Custom config: https://github.com/path/android-priority-jobqueue/wiki/Job-Manager-Configuration, https://github.com/path/android-priority-jobqueue/blob/master/examples/twitter/TwitterClient/src/com/path/android/jobqueue/examples/twitter/TwitterApplication.java
        Configuration configuration = new Configuration.Builder(this)
                .customLogger(new CustomLogger() {
                                  private static final String TAG = "JOBS";

                                  @Override
                                  public boolean isDebugEnabled() {
                                      return BuildConfig.DEBUG;
                                  }

                                  @Override
                                  public void d(String text, Object... args) {
                                      Timber.d(text, args);
                                  }

                                  @Override
                                  public void e(Throwable t, String text, Object... args) {
                                      Timber.e(t, text, args);
                                  }

                                  @Override
                                  public void e(String text, Object... args) {
                                      Timber.e(text, args);
                                  }
                              })
                .build();
        jobManager = new JobManager(this, configuration);
    }

    public void buildObjectGraphAndInject() {
        objectGraph = ObjectGraph.create(Modules.list(this));
        objectGraph.inject(this);
    }

    /**
     * Injects the members of {@code instance}, including injectable members
     * inherited from its supertypes.
     *
     * @throws IllegalArgumentException if the runtime type of {@code instance} is
     *     not one of this object graph's {@link dagger.Module#injects injectable types}.
     */
    public void inject(Object o) {
        objectGraph.inject(o);
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    @Deprecated
    public static GolesApplication getInstance() {
        return instance;
    }

    public static GolesApplication get(Context context) {
        return (GolesApplication) context.getApplicationContext();
    }
}
