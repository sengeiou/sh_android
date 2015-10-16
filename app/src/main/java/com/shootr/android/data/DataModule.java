package com.shootr.android.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.fewlaps.quitnowcache.QNCache;
import com.fewlaps.quitnowcache.QNCacheBuilder;
import com.google.android.gms.gcm.GoogleCloudMessaging;
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
import com.shootr.android.domain.utils.DeviceFactory;
import com.shootr.android.domain.utils.ImageResizer;
import com.shootr.android.domain.utils.LocaleProvider;
import com.shootr.android.domain.utils.TimeUtils;
import com.shootr.android.interactor.InteractorModule;
import com.shootr.android.notifications.gcm.GCMIntentService;
import com.shootr.android.service.ApiModule;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.task.jobs.follows.GetUsersFollowsJob;
import com.shootr.android.task.jobs.follows.SearchPeopleLocalJob;
import com.shootr.android.task.jobs.follows.SearchPeopleRemoteJob;
import com.shootr.android.task.jobs.profile.UpdateUserProfileJob;
import com.shootr.android.ui.activities.UserFollowsContainerActivity;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.fragments.PeopleFragment;
import com.shootr.android.ui.fragments.UserFollowsFragment;
import com.shootr.android.ui.presenter.PeoplePresenter;
import com.shootr.android.ui.presenter.PostNewShotPresenter;
import com.shootr.android.ui.presenter.ProfileEditPresenter;
import com.shootr.android.ui.presenter.ShotDetailPresenter;
import com.shootr.android.ui.presenter.StreamDetailPresenter;
import com.shootr.android.ui.presenter.StreamsListPresenter;
import com.shootr.android.ui.presenter.WatchNumberPresenter;
import com.shootr.android.util.AnalyticsTool;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.BitmapImageResizer;
import com.shootr.android.util.CrashReportTool;
import com.shootr.android.util.CrashReportToolFactoryImpl;
import com.shootr.android.util.FeedbackMessage;
import com.shootr.android.util.GlideImageLoader;
import com.shootr.android.util.GoogleAnalyticsTool;
import com.shootr.android.util.ImageLoader;
import com.shootr.android.util.LogTreeFactory;
import com.shootr.android.util.LogTreeFactoryImpl;
import com.shootr.android.util.ResourcesLocaleProvider;
import com.shootr.android.util.SnackbarFeedbackMessage;
import com.shootr.android.util.TimeFormatter;
import com.shootr.android.util.Version;
import com.sloydev.okresponsefaker.ResponseFaker;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
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

    GetUsersFollowsJob.class,

    UpdateUserProfileJob.class,

    ShotManager.class, SearchPeopleRemoteJob.class, SearchPeopleLocalJob.class,

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

    @Provides @Singleton DeviceFactory provideDeviceFactory(AndroidDeviceFactory androidDeviceFactory) {
        return androidDeviceFactory;
    }

    @Provides @Singleton TimeUtils provideTimeUtils(AndroidTimeUtils androidTimeUtils) {
        return androidTimeUtils;
    }

    @Provides LocaleProvider provideLocaleProvider(ResourcesLocaleProvider resourcesLocaleProvider) {
        return resourcesLocaleProvider;
    }

    @Provides @Singleton SQLiteOpenHelper provideSQLiteOpenHelper(Application application, Version version) {
        return new ShootrDbOpenHelper(application.getApplicationContext(), version);
    }

    @Provides @Singleton SharedPreferences provideSharedPreferences(Application app) {
        return app.getSharedPreferences("shootr", MODE_PRIVATE);
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

    @Provides @Singleton GoogleCloudMessaging provideGoogleCloudMessaging(Application application) {
        return GoogleCloudMessaging.getInstance(application);
    }

    @Provides CrashReportTool.Factory provideCrashReportToolFactory() {
        return new CrashReportToolFactoryImpl();
    }

    @Provides @Singleton CrashReportTool provideCrashReportTool(CrashReportTool.Factory factory) {
        return factory.create();
    }

    @Provides @Singleton AnalyticsTool provideAnalyticsTool() {
        return new GoogleAnalyticsTool();
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

    @Provides @Singleton
    QNCache provideQNCache() {
        return new QNCacheBuilder().createQNCache();
    }
}
