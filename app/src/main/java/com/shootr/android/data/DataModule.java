package com.shootr.android.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.log.CustomLogger;
import com.path.android.jobqueue.network.NetworkUtil;
import com.path.android.jobqueue.network.NetworkUtilImpl;
import com.shootr.android.BuildConfig;
import com.shootr.android.data.prefs.PreferenceModule;
import com.shootr.android.data.repository.SessionRepositoryImpl;
import com.shootr.android.data.repository.dagger.RepositoryModule;
import com.shootr.android.data.service.ServiceModule;
import com.shootr.android.db.ManagerModule;
import com.shootr.android.db.MapperModule;
import com.shootr.android.db.ShootrDbOpenHelper;
import com.shootr.android.db.manager.AbstractManager;
import com.shootr.android.db.manager.DeviceManager;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.utils.ImageResizer;
import com.shootr.android.domain.utils.LocaleProvider;
import com.shootr.android.domain.utils.StreamDateTimeTextProvider;
import com.shootr.android.domain.utils.TimeUtils;
import com.shootr.android.interactor.InteractorModule;
import com.shootr.android.notifications.gcm.GCMIntentService;
import com.shootr.android.service.ApiModule;
import com.shootr.android.task.NetworkConnection;
import com.shootr.android.task.NetworkConnectionImpl;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.task.jobs.follows.GetFollowUnFollowUserOfflineJob;
import com.shootr.android.task.jobs.follows.GetFollowUnfollowUserOnlineJob;
import com.shootr.android.task.jobs.follows.GetUsersFollowsJob;
import com.shootr.android.task.jobs.follows.SearchPeopleLocalJob;
import com.shootr.android.task.jobs.follows.SearchPeopleRemoteJob;
import com.shootr.android.task.jobs.loginregister.GCMRegistrationJob;
import com.shootr.android.task.jobs.profile.GetUserInfoJob;
import com.shootr.android.task.jobs.profile.RemoveProfilePhotoJob;
import com.shootr.android.task.jobs.profile.UpdateUserProfileJob;
import com.shootr.android.task.jobs.profile.UploadProfilePhotoJob;
import com.shootr.android.task.jobs.shots.GetLatestShotsJob;
import com.shootr.android.ui.activities.UserFollowsContainerActivity;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.fragments.PeopleFragment;
import com.shootr.android.ui.fragments.ProfileFragment;
import com.shootr.android.ui.fragments.UserFollowsFragment;
import com.shootr.android.ui.presenter.PeoplePresenter;
import com.shootr.android.ui.presenter.PostNewShotPresenter;
import com.shootr.android.ui.presenter.ProfileEditPresenter;
import com.shootr.android.ui.presenter.ShotDetailPresenter;
import com.shootr.android.ui.presenter.StreamDetailPresenter;
import com.shootr.android.ui.presenter.StreamsListPresenter;
import com.shootr.android.ui.presenter.WatchNumberPresenter;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.BitmapImageResizer;
import com.shootr.android.util.CrashReportTool;
import com.shootr.android.util.CrashReportToolFactoryImpl;
import com.shootr.android.util.FeedbackMessage;
import com.shootr.android.util.GlideImageLoader;
import com.shootr.android.util.ImageLoader;
import com.shootr.android.util.LogTreeFactory;
import com.shootr.android.util.LogTreeFactoryImpl;
import com.shootr.android.util.ResourcesLocaleProvider;
import com.shootr.android.util.ResourcesStreamDateTimeTextProvider;
import com.shootr.android.util.SnackbarFeedbackMessage;
import com.shootr.android.util.TimeFormatter;
import com.shootr.android.util.Version;
import com.sloydev.okresponsefaker.ResponseFaker;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import dagger.Module;
import dagger.Provides;
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

    FollowManager.class, UserFollowsContainerActivity.class, UserFollowsFragment.class, PeopleFragment.class,

    GetUserInfoJob.class, GetUsersFollowsJob.class,
    GetFollowUnfollowUserOnlineJob.class, GetFollowUnFollowUserOfflineJob.class, GetLatestShotsJob.class,

    ProfileFragment.class,
    UploadProfilePhotoJob.class,
    RemoveProfilePhotoJob.class, UpdateUserProfileJob.class,

    ShotManager.class, SearchPeopleRemoteJob.class, SearchPeopleLocalJob.class,

    GCMRegistrationJob.class,

    ProfileFragment.class,

    UserManager.class, DeviceManager.class,

    GCMIntentService.class,

    LogTreeFactory.class,

    ProfileEditPresenter.class,

    PostNewShotPresenter.class,

    ShotDetailPresenter.class,

    WatchNumberPresenter.class,

    PeoplePresenter.class,

    StreamDetailPresenter.class,

    StreamsListPresenter.class,

    TimeFormatter.class,

    BitmapImageResizer.class,

    NetworkConnection.class,

  },
  includes = {
    ApiModule.class, PreferenceModule.class, MapperModule.class, ManagerModule.class, InteractorModule.class,
    RepositoryModule.class, ServiceModule.class,
  },
  complete = false,
  library = true)
public class DataModule {
    static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
    private static final long TIMEOUT_SECONDS = 30;
    private static final long TIMEOUT_CONNECT_SECONDS = 15;

    @Provides @Singleton TimeUtils provideTimeUtils(AndroidTimeUtils androidTimeUtils) {
        return androidTimeUtils;
    }

    @Provides LocaleProvider provideLocaleProvider(ResourcesLocaleProvider resourcesLocaleProvider) {
        return resourcesLocaleProvider;
    }

    @Provides @Singleton SQLiteOpenHelper provideSQLiteOpenHelper(Application application, Version version) {
        return new ShootrDbOpenHelper(application.getApplicationContext(), version);
    }

    @Provides @Singleton
    StreamDateTimeTextProvider provideStreamTimeFormatter(ResourcesStreamDateTimeTextProvider timeTextFormatter) {
        return timeTextFormatter;
    }

    @Provides @Singleton SharedPreferences provideSharedPreferences(Application app) {
        return app.getSharedPreferences("shootr", MODE_PRIVATE);
    }

    @Provides @Singleton Picasso providePicasso(Application app, OkHttpClient okHttpClient) {
        return new Picasso.Builder(app)
          .downloader(new OkHttpDownloader(okHttpClient))
          .build();
    }

    @Provides ImageLoader provideImageLoader(GlideImageLoader imageLoader) {
        return imageLoader;
    }

    @Provides
    FeedbackMessage provideFeedbackLoader(SnackbarFeedbackMessage snackbarFeedbackLoader) {
        return snackbarFeedbackLoader;
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Application app,
      AuthHeaderInterceptor authHeaderInterceptor,
      VersionHeaderInterceptor versionHeaderInterceptor,
      ServerDownErrorInterceptor serverDownErrorInterceptor,
      UnauthorizedErrorInterceptor unauthorizedErrorInterceptor,
      VersionOutdatedErrorInterceptor versionOutdatedErrorInterceptor) {

        OkHttpClient client = new OkHttpClient();

        try {
            // Install an HTTP cache in the application cache directory.
            File cacheDir = new File(app.getCacheDir(), "http");
            Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
            client.setCache(cache);
        } catch (IOException e) {
            Timber.e(e, "Unable to install disk cache.");
        }

        client.setConnectTimeout(TIMEOUT_CONNECT_SECONDS, TimeUnit.SECONDS);
        client.setReadTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        client.setWriteTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        client.networkInterceptors().add(new StethoInterceptor());
        client.interceptors().add(authHeaderInterceptor);
        client.interceptors().add(versionHeaderInterceptor);
        client.interceptors().add(serverDownErrorInterceptor);
        client.interceptors().add(unauthorizedErrorInterceptor);
        client.interceptors().add(versionOutdatedErrorInterceptor);
        client.interceptors().add(ResponseFaker.interceptor());

        return client;
    }

    @Provides @Singleton NetworkUtil provideNetworkUtil(Application app) {
        return new NetworkUtilImpl(app);
    }

    @Provides @Singleton NetworkConnection provideNetworkConnection(Application application, NetworkUtil networkUtil) {
        return new NetworkConnectionImpl(application, networkUtil);
    }

    @Provides @Singleton JobManager provideJobManager(Application app, NetworkUtil networkUtil) {
        return configureJobManager(app, networkUtil);
    }

    @Provides @Singleton GoogleCloudMessaging provideGoogleCloudMessaging(Application application) {
        return GoogleCloudMessaging.getInstance(application);
    }

    @Provides CrashReportTool.Factory provideCrashReportToolFactory() {
        return new CrashReportToolFactoryImpl();
    }

    @Provides CrashReportTool provideCrashReportTool(CrashReportTool.Factory factory) {
        return factory.create();
    }

    @Provides LogTreeFactory provideLogTreeFactory() {
        return new LogTreeFactoryImpl();
    }

    @Provides @Singleton SessionRepository provideSessionManager(SessionRepositoryImpl sessionManager) {
        return sessionManager;
    }

    @Provides ImageResizer provideImageResizer(BitmapImageResizer imageResizer) {
        return imageResizer;
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
}
