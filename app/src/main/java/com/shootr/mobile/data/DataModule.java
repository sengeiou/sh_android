package com.shootr.mobile.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.fewlaps.quitnowcache.QNCache;
import com.fewlaps.quitnowcache.QNCacheBuilder;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.shootr.mobile.data.prefs.PreferenceModule;
import com.shootr.mobile.data.repository.SessionRepositoryImpl;
import com.shootr.mobile.data.repository.dagger.RepositoryModule;
import com.shootr.mobile.data.service.ServiceModule;
import com.shootr.mobile.db.ManagerModule;
import com.shootr.mobile.db.MapperModule;
import com.shootr.mobile.db.ShootrDbOpenHelper;
import com.shootr.mobile.db.manager.AbstractManager;
import com.shootr.mobile.db.manager.ContributorManager;
import com.shootr.mobile.db.manager.DeviceManager;
import com.shootr.mobile.db.manager.FollowManager;
import com.shootr.mobile.db.manager.ShootrEventManager;
import com.shootr.mobile.db.manager.ShotManager;
import com.shootr.mobile.db.manager.UserManager;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.utils.DeviceFactory;
import com.shootr.mobile.domain.utils.ImageResizer;
import com.shootr.mobile.domain.utils.LocaleProvider;
import com.shootr.mobile.domain.utils.TimeUtils;
import com.shootr.mobile.interactor.InteractorModule;
import com.shootr.mobile.notifications.gcm.GCMIntentService;
import com.shootr.mobile.service.ApiModule;
import com.shootr.mobile.ui.activities.UserFollowsContainerActivity;
import com.shootr.mobile.ui.base.BaseSignedInActivity;
import com.shootr.mobile.ui.fragments.FollowFragment;
import com.shootr.mobile.ui.fragments.UserFollowsFragment;
import com.shootr.mobile.ui.presenter.MessageBoxPresenter;
import com.shootr.mobile.ui.presenter.NewMessageBarPresenter;
import com.shootr.mobile.ui.presenter.PostNewShotPresenter;
import com.shootr.mobile.ui.presenter.ProfileEditPresenter;
import com.shootr.mobile.ui.presenter.ShotDetailPresenter;
import com.shootr.mobile.ui.presenter.StreamDetailPresenter;
import com.shootr.mobile.ui.presenter.StreamsListPresenter;
import com.shootr.mobile.ui.presenter.WatchNumberPresenter;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.BackStackHandler;
import com.shootr.mobile.util.BackStackHandlerTool;
import com.shootr.mobile.util.BitmapImageResizer;
import com.shootr.mobile.util.CacheDataUtils;
import com.shootr.mobile.util.CacheUtils;
import com.shootr.mobile.util.CrashReportTool;
import com.shootr.mobile.util.CrashReportToolFactoryImpl;
import com.shootr.mobile.util.DeeplinkingNavigator;
import com.shootr.mobile.util.DeeplinkingTool;
import com.shootr.mobile.util.DefaultTabUtils;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.FormatNumberUtils;
import com.shootr.mobile.util.GenericAnalyticsTool;
import com.shootr.mobile.util.GlideImageLoader;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.InitialsLoaderTool;
import com.shootr.mobile.util.LogTreeFactory;
import com.shootr.mobile.util.LogTreeFactoryImpl;
import com.shootr.mobile.util.LoginTypeUtils;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.PercentageUtils;
import com.shootr.mobile.util.RandomUtils;
import com.shootr.mobile.util.ResourcesLocaleProvider;
import com.shootr.mobile.util.ResponseFaker;
import com.shootr.mobile.util.ShareManager;
import com.shootr.mobile.util.ShareManagerUtil;
import com.shootr.mobile.util.SnackbarFeedbackMessage;
import com.shootr.mobile.util.StreamPercentageUtils;
import com.shootr.mobile.util.StringHashUtils;
import com.shootr.mobile.util.TimeFormatter;
import com.shootr.mobile.util.Version;
import com.shootr.mobile.util.WritePermissionManager;
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

        FollowManager.class, UserFollowsContainerActivity.class, UserFollowsFragment.class,

        ShotManager.class,

        UserManager.class, DeviceManager.class,

        GCMIntentService.class,

        LogTreeFactory.class,

        ProfileEditPresenter.class,

        PostNewShotPresenter.class,

        ShotDetailPresenter.class,

        WatchNumberPresenter.class,

        StreamDetailPresenter.class,

        StreamsListPresenter.class,

        TimeFormatter.class,

        BitmapImageResizer.class,

        WritePermissionManager.class,

        ContributorManager.class,

        ShootrEventManager.class, NewMessageBarPresenter.class, MessageBoxPresenter.class,
        FollowFragment.class,
    },
    includes = {
        ApiModule.class, PreferenceModule.class, MapperModule.class, ManagerModule.class,
        InteractorModule.class, RepositoryModule.class, ServiceModule.class,
    },
    complete = false,
    library = true) public class DataModule {

  static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
  private static final long TIMEOUT_SECONDS = 30;
  private static final long TIMEOUT_CONNECT_SECONDS = 15;

  @Provides @Singleton DeviceFactory provideDeviceFactory(
      AndroidDeviceFactory androidDeviceFactory) {
    return androidDeviceFactory;
  }

  @Provides @Singleton TimeUtils provideTimeUtils(AndroidTimeUtils androidTimeUtils) {
    return androidTimeUtils;
  }

  @Provides LocaleProvider provideLocaleProvider(ResourcesLocaleProvider resourcesLocaleProvider) {
    return resourcesLocaleProvider;
  }

  @Provides @Singleton SQLiteOpenHelper provideSQLiteOpenHelper(Application application,
      Version version) {
    return new ShootrDbOpenHelper(application.getApplicationContext(), version);
  }

  @Provides @Singleton SharedPreferences provideSharedPreferences(Application app) {
    return app.getSharedPreferences("shootr", MODE_PRIVATE);
  }

  @Provides ImageLoader provideImageLoader(GlideImageLoader imageLoader) {
    return imageLoader;
  }

  @Provides FeedbackMessage provideFeedbackLoader(SnackbarFeedbackMessage snackbarFeedbackLoader) {
    return snackbarFeedbackLoader;
  }

  @Provides @Singleton OkHttpClient provideOkHttpClient(Application app,
      AuthHeaderInterceptor authHeaderInterceptor,
      VersionHeaderInterceptor versionHeaderInterceptor,
      ServerDownErrorInterceptor serverDownErrorInterceptor,
      UnauthorizedErrorInterceptor unauthorizedErrorInterceptor,
      VersionOutdatedErrorInterceptor versionOutdatedErrorInterceptor,
      SynchroTimeInterceptor synchroTimeInterceptor,
      DeviceHeaderInterceptor deviceHeaderInterceptor) {

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
    client.interceptors().add(synchroTimeInterceptor);
    client.interceptors().add(deviceHeaderInterceptor);

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
    return new GenericAnalyticsTool();
  }

  @Provides @Singleton CacheUtils provideCacheUtils(CrashReportTool crashReportTool) {
    return new CacheDataUtils(crashReportTool);
  }

  @Provides @Singleton BackStackHandler provideBackStackHandler() {
    return new BackStackHandlerTool();
  }

  @Provides @Singleton WritePermissionManager provideWritePermissionManager() {
    return new WritePermissionManager();
  }

  @Provides LogTreeFactory provideLogTreeFactory() {
    return new LogTreeFactoryImpl();
  }

  @Provides @Singleton SessionRepository provideSessionManager(
      SessionRepositoryImpl sessionManager) {
    return sessionManager;
  }

  @Provides ImageResizer provideImageResizer(BitmapImageResizer imageResizer) {
    return imageResizer;
  }

  @Provides @Singleton QNCache provideQNCache() {
    return new QNCacheBuilder().createQNCache();
  }

  @Provides @Singleton PercentageUtils providePercentageUtils(
      StreamPercentageUtils streamPercentageUtils) {
    return streamPercentageUtils;
  }

  @Provides @Singleton ShareManager provideShareManagerUtil(
      ShareManagerUtil shareManagerUtil) {
    return shareManagerUtil;
  }

  @Provides @Singleton FormatNumberUtils provideFormatNumbersUtils(
      NumberFormatUtil followsFormatUtil) {
    return followsFormatUtil;
  }

  @Provides InitialsLoader providesInitialLoader(InitialsLoaderTool initialsLoaderTool) {
    return initialsLoaderTool;
  }

  @Provides DeeplinkingNavigator providesDeeplinkingNavigator(DeeplinkingTool deeplinkingTool) {
    return deeplinkingTool;
  }

  @Provides @Singleton StringHashUtils provideStringHashUtils(
      DefaultTabUtils defaultTabUtils) {
    return defaultTabUtils;
  }

  @Provides @Singleton RandomUtils providesRandomUtils(LoginTypeUtils loginTypeUtils) {
    return loginTypeUtils;
  }
}
