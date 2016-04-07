package com.shootr.mobile.data.prefs;

import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false
)
public class PreferenceModule {

    private static final boolean DEFAULT_NOTIFICATIONS_ENABLED = true;

    @Provides @Singleton @CurrentUserId StringPreference provideCurrentUserId(SharedPreferences preferences) {
        return new StringPreference(preferences, "current_user_id", null);
    }

    @Provides @Singleton @SessionToken StringPreference provideSessionToken(SharedPreferences preferences) {
        return new StringPreference(preferences, "session_token", null);
    }

    @Provides @Singleton @NotificationsEnabled BooleanPreference providesNotificationsEnabled(
      SharedPreferences preferences) {
        return new BooleanPreference(preferences, "global_notifications_enabled", DEFAULT_NOTIFICATIONS_ENABLED);
    }

    @Provides @Singleton @LastDatabaseVersion IntPreference providePreferencesDatabaseVersion(
      SharedPreferences preferences) {
        return new IntPreference(preferences, "preferences_database_version", 0);
    }

    @Provides @Singleton @ActivityBadgeCount IntPreference provideActivityBadgeCount(
      SharedPreferences preferences) {
        return new IntPreference(preferences, "activity_badge_count", 0);
    }

    @Provides @Singleton @ShouldShowIntro BooleanPreference provideShouldShowIntro(
      SharedPreferences preferences) {
        return new BooleanPreference(preferences, "should_show_intro", true);
    }
}
