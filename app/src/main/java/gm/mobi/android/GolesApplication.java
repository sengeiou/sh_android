package gm.mobi.android;

import android.app.Application;
import android.util.Log;

import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.log.CustomLogger;

public class GolesApplication extends Application {

    private static GolesApplication instance;
    private JobManager jobManager;

    public GolesApplication() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        configureJobManager();
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
                                      Log.d(TAG, String.format(text, args));
                                  }

                                  @Override
                                  public void e(Throwable t, String text, Object... args) {
                                      Log.e(TAG, String.format(text, args), t);
                                  }

                                  @Override
                                  public void e(String text, Object... args) {
                                      Log.e(TAG, String.format(text, args));
                                  }
                              })
                .build();
        jobManager = new JobManager(this, configuration);
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    public static GolesApplication getInstance() {
        return instance;
    }

}
