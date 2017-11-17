package com.shootr.mobile.data.prefs;

import android.content.SharedPreferences;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  library = true,
  complete = false) public class PreferenceModule {

    private static final boolean DEFAULT_NOTIFICATIONS_ENABLED = true;

    @Provides @Singleton @CurrentUserId StringPreference provideCurrentUserId(SharedPreferences preferences) {
        return new StringPreference(preferences, "current_user_id", null);
    }

    @Provides @Singleton @SessionToken StringPreference provideSessionToken(SharedPreferences preferences) {
        return new StringPreference(preferences, "session_token", null);
    }

    @Provides @Singleton @DeviceId StringPreference provideDeviceId(SharedPreferences preferences) {
        return new StringPreference(preferences, "device_id", null);
    }

    @Provides @Singleton @NotificationsEnabled BooleanPreference providesNotificationsEnabled(
      SharedPreferences preferences) {
        return new BooleanPreference(preferences, "global_notifications_enabled", DEFAULT_NOTIFICATIONS_ENABLED);
    }

    @Provides @Singleton @LastDatabaseVersion IntPreference providePreferencesDatabaseVersion(
      SharedPreferences preferences) {
        return new IntPreference(preferences, "preferences_database_version", 0);
    }

    @Provides @Singleton @ActivityBadgeCount IntPreference provideActivityBadgeCount(SharedPreferences preferences) {
        return new IntPreference(preferences, "activity_badge_count", 0);
    }

    @Provides @Singleton @ShouldShowIntro BooleanPreference provideShouldShowIntro(SharedPreferences preferences) {
        return new BooleanPreference(preferences, "should_show_intro", true);
    }

    @Provides @Singleton @TimelineFilterActivated
    BooleanPreference provideIsTimelineFilterActivated(SharedPreferences preferences) {
        return new BooleanPreference(preferences, "timeline_filter", false);
    }

    @Provides @Singleton @LastTimeFiltered StringPreference provideLastTimeFiltered(SharedPreferences preferences) {
        return new StringPreference(preferences, "last_time_filtered", "0");
    }

    @Provides @Singleton @CheckInShowcaseStatus ShowcasePreference provideCheckInPreferences(
        SharedPreferences preferences) {
        return new ShowcasePreference(preferences, "check_in_showcase", new ShowcaseStatus(true));
    }

    @Provides @Singleton @PublicVoteAlertPreference
    BooleanPreference providePublicVoteAlertPreference(SharedPreferences preferences) {
        return new BooleanPreference(preferences, "public_vote_alert", true);
    }

    @Provides @Singleton @ActivityShowcase
    BooleanPreference provideActivityShowcasePreference(SharedPreferences preferences) {
        return new BooleanPreference(preferences, "activity_showcase", false);
    }

    @Provides @Singleton @DevicePref DevicePreferences provideDevicePreferences(
        SharedPreferences preferences) {
        return new DevicePreferences(preferences, "device_preference", null);
    }

    @Provides @Singleton @CacheTimeKeepAlive LongPreference provideCacheTimeKeepAlivePreferences(
        SharedPreferences preferences) {
        return new LongPreference(preferences, "cache_TimeKeep_Alive", 0);
    }
}
