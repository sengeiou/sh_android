package gm.mobi.android.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.log.CustomLogger;
import com.path.android.jobqueue.network.NetworkUtil;
import com.path.android.jobqueue.network.NetworkUtilImpl;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;

import gm.mobi.android.db.manager.AbstractManager;
import gm.mobi.android.db.manager.ShotManager;
import gm.mobi.android.task.jobs.CancellableJob;
import gm.mobi.android.task.jobs.follows.GetFollowingsJob;
import gm.mobi.android.task.jobs.follows.GetUsersFollowingJob;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.TeamManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.task.jobs.JobModule;
import gm.mobi.android.task.jobs.profile.GetUserInfoJob;
import gm.mobi.android.ui.activities.FollowingUsersActivity;
import gm.mobi.android.ui.base.BaseSignedInActivity;
import gm.mobi.android.ui.fragments.DummyFragment;
import gm.mobi.android.ui.fragments.FollowingUsersFragment;
import gm.mobi.android.ui.fragments.ProfileFragment;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import gm.mobi.android.BuildConfig;
import gm.mobi.android.data.prefs.PreferenceModule;
import gm.mobi.android.db.OpenHelper;
import gm.mobi.android.service.ApiModule;
import gm.mobi.android.sync.GMSyncAdapter;
import gm.mobi.android.task.jobs.loginregister.LoginUserJob;
import gm.mobi.android.task.jobs.shots.NewShotJob;
import gm.mobi.android.task.jobs.timeline.TimelineJob;
import gm.mobi.android.ui.activities.MainActivity;
import gm.mobi.android.ui.fragments.InitialSetupFragment;
import gm.mobi.android.ui.fragments.TimelineFragment;
import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;

@Module(
        injects = {
                BaseSignedInActivity.class,

                CancellableJob.class,

                DummyFragment.class,

                FollowManager.class,
                FollowingUsersActivity.class,
                FollowingUsersFragment.class,

                GetFollowingsJob.class,
                GMSyncAdapter.class,
                GetUserInfoJob.class,
                GetUsersFollowingJob.class,

                InitialSetupFragment.class,

                LoginUserJob.class,

                MainActivity.class,

                NewShotJob.class,

                ShotManager.class,

                TimelineJob.class,
                TimelineFragment.class,
                TeamManager.class,

                ProfileFragment.class,

                UserManager.class,
        },
        includes = {
                ApiModule.class,
                JobModule.class,
                PreferenceModule.class
        },
        complete = false,
        library = true
)
public class DataModule {
    static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
    private static final long TIMEOUT = 10; // 10 seconds


    @Provides @Singleton SQLiteOpenHelper provideSQLiteOpenHelper(Application application) {
        return new OpenHelper(application.getApplicationContext());
    }

    @Provides
    SQLiteDatabase provideDatabase(Application app){
        return provideSQLiteOpenHelper(app).getWritableDatabase();
    }

    @Provides  UserManager provideUserManager(){
        return new UserManager();
    }

    @Provides FollowManager provideFollowManager(){
        return new FollowManager();
    }

    @Provides
    ShotManager provideShotManager(){
        return new ShotManager();
    }

    @Provides
    TeamManager provideTeamManager(){
        return new TeamManager();
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
                })
                .build();
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

    static JobManager configureJobManager(Application app, NetworkUtil networkUtil) {
        // Custom config: https://github.com/path/android-priority-jobqueue/wiki/Job-Manager-Configuration, https://github.com/path/android-priority-jobqueue/blob/master/examples/twitter/TwitterClient/src/com/path/android/jobqueue/examples/twitter/TwitterApplication.java
        Configuration configuration = new Configuration.Builder(app)
                .networkUtil(networkUtil)
                .customLogger(new CustomLogger() {

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
