package com.shootr.android.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.network.NetworkUtil;
import dagger.Module;
import dagger.Provides;
import com.shootr.android.data.prefs.BooleanPreference;
import com.shootr.android.data.prefs.IntPreference;
import com.shootr.android.data.prefs.StringPreference;
import com.shootr.android.db.ShootrDbOpenHelper;
import com.shootr.android.db.TrackingCursorFactory;
import com.shootr.android.gcm.notifications.ShootrNotificationManager;
import com.shootr.android.gcm.notifications.DebugNotificationManager;
import com.shootr.android.service.ApiModule;
import com.shootr.android.service.DebugApiModule;
import com.shootr.android.ui.debug.NetworkProxy;

import javax.inject.Singleton;

@Module(
  includes = DebugApiModule.class,
  complete = false,
  library = true,
  overrides = true
)
public class DebugDataModule {

    private static final int DEFAULT_ANIMATION_SPEED = 1; // 1x (normal) speed.
    private static final boolean DEFAULT_PICASSO_DEBUGGING = false; // Debug indicators displayed
    private static final boolean DEFAULT_PIXEL_GRID_ENABLED = false; // No pixel grid overlay.
    private static final boolean DEFAULT_PIXEL_RATIO_ENABLED = false; // No pixel ratio overlay.
    private static final boolean DEFAULT_SCALPEL_ENABLED = false; // No crazy 3D view tree.
    private static final boolean DEFAULT_SCALPEL_WIREFRAME_ENABLED = false; // Draw views by default.
    private static final boolean DEFAULT_SEEN_DEBUG_DRAWER = false; // Show debug drawer first time.
    private static final boolean DEFAULT_NETWORK_ENABLED = true;
    private static final boolean DEFAULT_NOTIFICATIONS_ENABLED = true;

    @Provides @Singleton @ApiEndpoint StringPreference provideEndpointPreference(SharedPreferences preferences) {
        return new StringPreference(preferences, "debug_endpoint", ApiEndpoints.TEST.url);
    }

    @Provides @Singleton @IsMockMode boolean provideIsMockMode(@ApiEndpoint StringPreference selectedEndpoint) {
        return ApiEndpoints.isMockMode(selectedEndpoint.get());
    }

    @Provides @Singleton @NetworkProxy StringPreference provideNetworkProxy(SharedPreferences preferences) {
        return new StringPreference(preferences, "debug_network_proxy");
    }

    @Provides @Singleton @NetworkEnabled BooleanPreference provideNetworkEnabled(SharedPreferences preferences) {
        return new BooleanPreference(preferences, "debug_network_enabled", DEFAULT_NETWORK_ENABLED);
    }

    @Provides @Singleton @NotificationsEnabled BooleanPreference providesNotificationsEnabled(
      SharedPreferences preferences) {
        return new BooleanPreference(preferences, "debug_notifications_enabled", DEFAULT_NOTIFICATIONS_ENABLED);
    }

    @Provides @Singleton @AnimationSpeed IntPreference provideAnimationSpeed(SharedPreferences preferences) {
        return new IntPreference(preferences, "debug_animation_speed", DEFAULT_ANIMATION_SPEED);
    }

    @Provides @Singleton @PicassoDebugging BooleanPreference providePicassoDebugging(SharedPreferences preferences) {
        return new BooleanPreference(preferences, "debug_picasso_debugging", DEFAULT_PICASSO_DEBUGGING);
    }

    @Provides @Singleton @SeenDebugDrawer BooleanPreference provideSeenDebugDrawer(SharedPreferences preferences) {
        return new BooleanPreference(preferences, "debug_seen_debug_drawer", DEFAULT_SEEN_DEBUG_DRAWER);
    }

    @Provides @Singleton @ScalpelEnabled BooleanPreference provideScalpelEnabled(SharedPreferences preferences) {
        return new BooleanPreference(preferences, "debug_scalpel_enabled", DEFAULT_SCALPEL_ENABLED);
    }

    @Provides @Singleton @ScalpelWireframeEnabled BooleanPreference provideScalpelWireframeEnabled(
      SharedPreferences preferences) {
        return new BooleanPreference(preferences, "debug_scalpel_wireframe_drawer", DEFAULT_SCALPEL_WIREFRAME_ENABLED);
    }

    @Provides @Singleton @CustomEndpoint StringPreference provideCustomEndpoint(SharedPreferences preferences) {
        return new StringPreference(preferences, "debug_custom_endpoint", ApiModule.PRODUCTION_API_URL);
    }

    @Provides @Singleton NetworkUtil providesNetworkUtil(DebugNetworkUtil networkUtil) {
        return networkUtil;
    }

    @Provides @Singleton SQLiteOpenHelper provideSqLiteOpenHelper(Application application) {
        return new ShootrDbOpenHelper(application.getApplicationContext(), new TrackingCursorFactory());
    }

    @Provides @Singleton ShootrNotificationManager provideShootrNotificationManager(
      DebugNotificationManager notificationManager) {
        return notificationManager;
    }

}
