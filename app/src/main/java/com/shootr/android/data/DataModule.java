package com.shootr.android.data;

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
import com.shootr.android.task.jobs.info.InfoListBuilderFactory;
import com.shootr.android.task.jobs.info.SetWatchingInfoOfflineJob;
import com.shootr.android.task.jobs.info.SetWatchingInfoOnlineJob;
import com.shootr.android.task.jobs.timeline.GetWatchingRequestsPendingJob;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;
import dagger.Module;
import dagger.Provides;
import com.shootr.android.BuildConfig;
import com.shootr.android.data.prefs.PreferenceModule;
import com.shootr.android.db.ManagerModule;
import com.shootr.android.db.MapperModule;
import com.shootr.android.db.ShootrDbOpenHelper;
import com.shootr.android.db.manager.AbstractManager;
import com.shootr.android.db.manager.DeviceManager;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.MatchManager;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.gcm.GCMIntentService;
import com.shootr.android.gcm.NotificationIntentReceiver;
import com.shootr.android.gcm.notifications.ShootrNotificationManager;
import com.shootr.android.gcm.notifications.NotificationBuilderFactory;
import com.shootr.android.service.ApiModule;
import com.shootr.android.sync.ShootrSyncAdapter;
import com.shootr.android.sync.InfoCleaner;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.task.jobs.follows.GetFollowUnFollowUserOfflineJob;
import com.shootr.android.task.jobs.follows.GetFollowUnfollowUserJob;
import com.shootr.android.task.jobs.follows.GetFollowingsJob;
import com.shootr.android.task.jobs.follows.GetPeopleJob;
import com.shootr.android.task.jobs.follows.GetUsersFollowsJob;
import com.shootr.android.task.jobs.follows.SearchPeopleLocalJob;
import com.shootr.android.task.jobs.follows.SearchPeopleRemoteJob;
import com.shootr.android.task.jobs.info.GetWatchingInfoJob;
import com.shootr.android.task.jobs.loginregister.GCMRegistrationJob;
import com.shootr.android.task.jobs.loginregister.LoginUserJob;
import com.shootr.android.task.jobs.profile.GetUserInfoJob;
import com.shootr.android.task.jobs.shots.PostNewShotJob;
import com.shootr.android.task.jobs.timeline.RetrieveFromDataBaseTimeLineJob;
import com.shootr.android.task.jobs.timeline.RetrieveInitialTimeLineJob;
import com.shootr.android.task.jobs.timeline.RetrieveNewShotsTimeLineJob;
import com.shootr.android.task.jobs.timeline.RetrieveOldShotsTimeLineJob;
import com.shootr.android.task.jobs.timeline.TimelineJob;
import com.shootr.android.ui.activities.InfoActivity;
import com.shootr.android.ui.activities.MainActivity;
import com.shootr.android.ui.activities.UserFollowsContainerActivity;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.fragments.DummyFragment;
import com.shootr.android.ui.fragments.InitialSetupFragment;
import com.shootr.android.ui.fragments.PeopleFragment;
import com.shootr.android.ui.fragments.ProfileFragment;
import com.shootr.android.ui.fragments.TimelineFragment;
import com.shootr.android.ui.fragments.UserFollowsFragment;
import com.shootr.android.util.LogTreeFactory;
import com.shootr.android.util.LogTreeFactoryImpl;
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

    ShootrBaseJob.class,

    DummyFragment.class,

    FollowManager.class, UserFollowsContainerActivity.class, UserFollowsFragment.class, PeopleFragment.class,
    InfoActivity.class,

    GetFollowingsJob.class, ShootrSyncAdapter.class, GetUserInfoJob.class, GetUsersFollowsJob.class, GetPeopleJob.class,
    GetFollowUnfollowUserJob.class, GetFollowUnFollowUserOfflineJob.class,

    InitialSetupFragment.class,

    LoginUserJob.class,

    MainActivity.class,

    PostNewShotJob.class,

    ProfileFragment.class,
    RetrieveFromDataBaseTimeLineJob.class, RetrieveInitialTimeLineJob.class, RetrieveNewShotsTimeLineJob.class,
    RetrieveOldShotsTimeLineJob.class, GetWatchingInfoJob.class, SetWatchingInfoOfflineJob.class,
    SetWatchingInfoOnlineJob.class, GetWatchingRequestsPendingJob.class,

    ShotManager.class, SearchPeopleRemoteJob.class, SearchPeopleLocalJob.class,

    TimelineJob.class, TimelineFragment.class,

    GCMRegistrationJob.class,

    ProfileFragment.class,

    UserManager.class, DeviceManager.class,

    NotificationIntentReceiver.class,

    GCMIntentService.class,

    LogTreeFactory.class,

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
        return new ShootrDbOpenHelper(application.getApplicationContext(), null);
    }

    @Provides @Singleton SharedPreferences provideSharedPreferences(Application app) {
        return app.getSharedPreferences("shootr", MODE_PRIVATE);
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

    @Provides @Singleton NotificationManagerCompat provideNotificationManagerCompat(Application application) {
        return NotificationManagerCompat.from(application);

    }

    @Provides @Singleton ShootrNotificationManager provideShootrNotificationManager(Application app, NotificationManagerCompat nm, NotificationBuilderFactory notificationBuilderFactory, Picasso picasso) {
        return new ShootrNotificationManager(app, nm, notificationBuilderFactory, picasso);
    }

    @Provides @Singleton NotificationBuilderFactory provideNotificationBuilderFactory() {
        return new NotificationBuilderFactory();
    }

    @Provides @Singleton InfoListBuilderFactory provideInfoListBuilderFactory(){
        return new InfoListBuilderFactory();
    }

    @Provides LogTreeFactory provideLogTreeFactory() {
        return new LogTreeFactoryImpl();
    }

    @Provides @Singleton SessionManager provideSessionManager(SessionManagerImpl sessionManager) {
        return sessionManager;
    }

    @Provides @Singleton InfoCleaner provideInfoCleaner(MatchManager matchManager, WatchManager watchManager) {
        return new InfoCleaner(matchManager, watchManager);
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