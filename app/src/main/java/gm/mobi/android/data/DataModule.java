package gm.mobi.android.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.v4.app.NotificationManagerCompat;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.log.CustomLogger;
import com.path.android.jobqueue.network.NetworkUtil;
import com.path.android.jobqueue.network.NetworkUtilImpl;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;
import dagger.Module;
import dagger.Provides;
import gm.mobi.android.BuildConfig;
import gm.mobi.android.data.prefs.PreferenceModule;
import gm.mobi.android.db.ManagerModule;
import gm.mobi.android.db.MapperModule;
import gm.mobi.android.db.OpenHelper;
import gm.mobi.android.db.manager.AbstractManager;
import gm.mobi.android.db.manager.DeviceManager;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.ShotManager;
import gm.mobi.android.db.manager.TeamManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.gcm.notifications.BagdadNotificationManager;
import gm.mobi.android.gcm.NotificationIntentReceiver;
import gm.mobi.android.service.ApiModule;
import gm.mobi.android.sync.GMSyncAdapter;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.task.jobs.follows.GetFollowUnfollowUserJob;
import gm.mobi.android.task.jobs.follows.GetFollowingsJob;
import gm.mobi.android.task.jobs.follows.GetPeopleJob;
import gm.mobi.android.task.jobs.follows.GetUsersFollowsJob;
import gm.mobi.android.task.jobs.follows.SearchPeopleLocalJob;
import gm.mobi.android.task.jobs.follows.SearchPeopleRemoteJob;
import gm.mobi.android.task.jobs.loginregister.GCMRegistrationJob;
import gm.mobi.android.task.jobs.loginregister.LoginUserJob;
import gm.mobi.android.task.jobs.profile.GetUserInfoJob;
import gm.mobi.android.task.jobs.shots.PostNewShotJob;
import gm.mobi.android.task.jobs.timeline.RetrieveFromDataBaseTimeLineJob;
import gm.mobi.android.task.jobs.timeline.RetrieveInitialTimeLineJob;
import gm.mobi.android.task.jobs.timeline.RetrieveNewShotsTimeLineJob;
import gm.mobi.android.task.jobs.timeline.RetrieveOldShotsTimeLineJob;
import gm.mobi.android.task.jobs.timeline.TimelineJob;
import gm.mobi.android.ui.activities.MainActivity;
import gm.mobi.android.ui.activities.UserFollowsContainerActivity;
import gm.mobi.android.ui.base.BaseSignedInActivity;
import gm.mobi.android.ui.fragments.DummyFragment;
import gm.mobi.android.ui.fragments.InitialSetupFragment;
import gm.mobi.android.ui.fragments.PeopleFragment;
import gm.mobi.android.ui.fragments.ProfileFragment;
import gm.mobi.android.ui.fragments.TimelineFragment;
import gm.mobi.android.ui.fragments.UserFollowsFragment;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;

@Module(
  injects = {
    AbstractManager.class,

    BaseSignedInActivity.class,

    BagdadBaseJob.class,

    DummyFragment.class,

    FollowManager.class, UserFollowsContainerActivity.class, UserFollowsFragment.class, PeopleFragment.class,

    GetFollowingsJob.class, GMSyncAdapter.class, GetUserInfoJob.class, GetUsersFollowsJob.class, GetPeopleJob.class,
    GetFollowUnfollowUserJob.class,

    InitialSetupFragment.class,

    LoginUserJob.class,

    MainActivity.class,

    PostNewShotJob.class,

    ProfileFragment.class,
    RetrieveFromDataBaseTimeLineJob.class, RetrieveInitialTimeLineJob.class, RetrieveNewShotsTimeLineJob.class,
    RetrieveOldShotsTimeLineJob.class,

    ShotManager.class, SearchPeopleRemoteJob.class, SearchPeopleLocalJob.class,

    TimelineJob.class, TimelineFragment.class, TeamManager.class,

    GCMRegistrationJob.class,

    ProfileFragment.class,

    UserManager.class, DeviceManager.class,

    NotificationIntentReceiver.class,

  },
  includes = {
    ApiModule.class, PreferenceModule.class, MapperModule.class, ManagerModule.class,
  },
  complete = false,
  library = true)
public class DataModule {
    static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
    private static final long TIMEOUT = 10; // 10 seconds


    @Provides @Singleton SQLiteOpenHelper provideSQLiteOpenHelper(Application application) {
        return new OpenHelper(application.getApplicationContext());
    }

    @Provides @Singleton SharedPreferences provideSharedPreferences(Application app) {
        return app.getSharedPreferences("bagdad", MODE_PRIVATE);
    }

    @Provides @Singleton Picasso providePicasso(Application app) {
        return new Picasso.Builder(app)
          //                .downloader(new OkHttpDownloader(client))
          .listener(new Picasso.Listener() {
              @Override
              public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                  Timber.e(e, "Failed to load image: %s", uri);
              }
          }).build();
    }

    @Provides @Singleton OkHttpClient provideOkHttpClient(Application app) {
        return createOkHttpClient(app);
    }

    @Provides @Singleton NetworkUtil provideNetworkUtil(Application app) {
        return new NetworkUtilImpl(app);
    }

    @Provides @Singleton JobManager provideJobManager(Application app, NetworkUtil networkUtil) {
        return configureJobManager(app, networkUtil);
    }

    @Provides @Singleton GoogleCloudMessaging provideGoogleCloudMessaging(Application application) {
        return GoogleCloudMessaging.getInstance(application);
    }

    @Provides @Singleton BagdadNotificationManager provideBagdadNotificationManager(Application app, Picasso picasso) {
        NotificationManagerCompat nm = NotificationManagerCompat.from(app);
        return new BagdadNotificationManager(app, nm, picasso);
    }

    static JobManager configureJobManager(Application app, NetworkUtil networkUtil) {
        // Custom config: https://github.com/path/android-priority-jobqueue/wiki/Job-Manager-Configuration, https://github.com/path/android-priority-jobqueue/blob/master/examples/twitter/TwitterClient/src/com/path/android/jobqueue/examples/twitter/TwitterApplication.java
        Configuration configuration =
          new Configuration.Builder(app).networkUtil(networkUtil).customLogger(new CustomLogger() {

              @Override
              public boolean isDebugEnabled() {
                  return BuildConfig.DEBUG;
              }

              @Override
              public void d(String text, Object... args) {
                  Timber.v(text, args);
              }

              @Override
              public void e(Throwable t, String text, Object... args) {
                  Timber.e(t, text, args);
              }

              @Override
              public void e(String text, Object... args) {
                  Timber.e(text, args);
              }
          }).build();
        return new JobManager(app, configuration);
    }

    static OkHttpClient createOkHttpClient(Application app) {
        OkHttpClient client = new OkHttpClient();

        // Install an HTTP cache in the application cache directory.
        try {
            File cacheDir = new File(app.getCacheDir(), "http");
            Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
            client.setCache(cache);
            client.setReadTimeout(TIMEOUT, TimeUnit.SECONDS);
            client.setWriteTimeout(TIMEOUT, TimeUnit.SECONDS);
        } catch (IOException e) {
            Timber.e(e, "Unable to install disk cache.");
        }

        return client;
    }
}
