package com.shootr.android.data.prefs;

import android.content.SharedPreferences;
import com.shootr.android.BuildConfig;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        library = true,
        complete = false
)
public class PreferenceModule {

    public static final int GCM_APP_VERSION_DEFAULT = 0;
    public static final String GCM_REGISTRATION_ID_DEFAULT = "";
    private static final boolean DEFAULT_NOTIFICATIONS_ENABLED = true;

    @Provides @Singleton @GCMAppVersion IntPreference provideGCMAppVersion(SharedPreferences preferences) {
        return new IntPreference(preferences, "gcm_app_version", GCM_APP_VERSION_DEFAULT);
    }

    @Provides @Singleton @GCMRegistrationId StringPreference provideGCMRegistrationId(SharedPreferences preferences) {
        return new StringPreference(preferences, "gcm_registration_id", GCM_REGISTRATION_ID_DEFAULT);
    }

    @Provides @Singleton @CurrentUserId StringPreference provideCurrentUserId(SharedPreferences preferences) {
        return new StringPreference(preferences, "current_user_id", null);
    }

    @Provides @Singleton @SessionToken StringPreference provideSessionToken(SharedPreferences preferences) {
        return new StringPreference(preferences, "session_token", null);
    }

    @Provides @Singleton @LastVersionNotCompatible LongPreference provideLastVersionNotCompatible(
      SharedPreferences preferences) {
        return new LongPreference(preferences, "not_compatible_version", -1L);
    }

    @Provides @Singleton @NotificationsEnabled BooleanPreference providesNotificationsEnabled(
      SharedPreferences preferences) {
        return new BooleanPreference(preferences, "global_notifications_enabled", DEFAULT_NOTIFICATIONS_ENABLED);
    }

    @Provides @Singleton @LastDatabaseVersionCompatible IntPreference provideLastDatabaseVersionCompatible(
      SharedPreferences preferences) {
        return new IntPreference(preferences, "last_compatible_version", 0);
    }
}
