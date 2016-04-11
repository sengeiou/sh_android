package com.shootr.mobile.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.mobile.data.prefs.BooleanPreference;
import com.shootr.mobile.data.prefs.IntPreference;
import com.shootr.mobile.data.prefs.StringPreference;
import com.shootr.mobile.db.ShootrDbOpenHelper;
import com.shootr.mobile.db.TrackingCursorFactory;
import com.shootr.mobile.service.ApiModule;
import com.shootr.mobile.service.DebugApiModule;
import com.shootr.mobile.ui.debug.CaptureIntents;
import com.shootr.mobile.ui.debug.NetworkProxy;
import com.shootr.mobile.util.Version;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  injects = {
    SyncBroadcastReceiver.class
  },
  includes = DebugApiModule.class,
  complete = false,
  library = true,
  overrides = true) public class DebugDataModule {

    private static final int DEFAULT_ANIMATION_SPEED = 1; // 1x (normal) speed.
    private static final boolean DEFAULT_PICASSO_DEBUGGING = false; // Debug indicators displayed
    private static final boolean DEFAULT_PIXEL_GRID_ENABLED = false; // No pixel grid overlay.
    private static final boolean DEFAULT_PIXEL_RATIO_ENABLED = false; // No pixel ratio overlay.
    private static final boolean DEFAULT_SCALPEL_ENABLED = false; // No crazy 3D view tree.
    private static final boolean DEFAULT_SCALPEL_WIREFRAME_ENABLED = false; // Draw views by default.
    private static final boolean DEFAULT_SEEN_DEBUG_DRAWER = false; // Show debug drawer first time.
    private static final boolean DEFAULT_NETWORK_ENABLED = true;
    private static final boolean DEFAULT_NOTIFICATIONS_ENABLED = true;
    public static final boolean DEFAULT_DEBUG_MODE = false;
    private static final boolean DEFAULT_POLLER_ACTIVE = true;
    private static final boolean DEFAULT_CAPTURE_INTENTS = true;

    @Provides @Singleton @ApiEndpoint StringPreference provideEndpointPreference(SharedPreferences preferences) {
        return new StringPreference(preferences, "debug_endpoint", ApiEndpoints.TEST.url);
    }

    @Provides @Singleton @DebugMode BooleanPreference provideDebugMode(SharedPreferences preferences) {
        return new BooleanPreference(preferences, "debug_network_debug_mode", DEFAULT_DEBUG_MODE);
    }

    @Provides @Singleton @NetworkProxy StringPreference provideNetworkProxy(SharedPreferences preferences) {
        return new StringPreference(preferences, "debug_network_proxy");
    }

    @Provides @Singleton @NetworkEnabled BooleanPreference provideNetworkEnabled(SharedPreferences preferences) {
        return new BooleanPreference(preferences, "debug_network_enabled", DEFAULT_NETWORK_ENABLED);
    }

    @Provides @Singleton @AnimationSpeed IntPreference provideAnimationSpeed(SharedPreferences preferences) {
        return new IntPreference(preferences, "debug_animation_speed", DEFAULT_ANIMATION_SPEED);
    }

    @Provides @Singleton @ScalpelEnabled BooleanPreference provideScalpelEnabled(SharedPreferences preferences) {
        return new BooleanPreference(preferences, "debug_scalpel_enabled", DEFAULT_SCALPEL_ENABLED);
    }

    @Provides @Singleton @ScalpelWireframeEnabled BooleanPreference provideScalpelWireframeEnabled(
      SharedPreferences preferences) {
        return new BooleanPreference(preferences, "debug_scalpel_wireframe_drawer", DEFAULT_SCALPEL_WIREFRAME_ENABLED);
    }

    @Provides @Singleton @CustomEndpoint StringPreference provideCustomEndpoint(SharedPreferences preferences) {
        return new StringPreference(preferences, "debug_custom_endpoint", ApiModule.PRODUCTION_ENDPOINT_URL);
    }

    @Provides @Singleton @PollerEnabled BooleanPreference providePollerActive(SharedPreferences preferences) {
        return new BooleanPreference(preferences, "debug_poller_active", DEFAULT_POLLER_ACTIVE);
    }

    @Provides @Singleton @CaptureIntents BooleanPreference provideCaptureIntentsPreference(
      SharedPreferences preferences) {
        return new BooleanPreference(preferences, "debug_capture_intents", DEFAULT_CAPTURE_INTENTS);
    }

    @Provides @Singleton SQLiteOpenHelper provideSqLiteOpenHelper(Application application, Version version) {
        return new ShootrDbOpenHelper(application.getApplicationContext(), new TrackingCursorFactory(), version);
    }
}
