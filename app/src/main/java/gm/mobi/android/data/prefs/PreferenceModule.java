package gm.mobi.android.data.prefs;

import android.content.SharedPreferences;
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

    @Provides @Singleton @InitialSetupCompleted BooleanPreference provideInitialSetupCompleted(SharedPreferences preferences) {
        return new BooleanPreference(preferences, "initial_setup", false);
    }

    @Provides @Singleton @GCMAppVersion IntPreference provideGCMAppVersion(SharedPreferences preferences) {
        return new IntPreference(preferences, "gcm_app_version", GCM_APP_VERSION_DEFAULT);
    }

    @Provides @Singleton @GCMRegistrationId StringPreference provideGCMRegistrationId(SharedPreferences preferences) {
        return new StringPreference(preferences, "gcm_registration_id", GCM_REGISTRATION_ID_DEFAULT);
    }

    @Provides @Singleton @CurrentUserId LongPreference provideCurrentUserId(SharedPreferences preferences) {
        return new LongPreference(preferences, "current_user_id", 0L);
    }

    @Provides @Singleton @SessionToken StringPreference provideSessionToken(SharedPreferences preferences) {
        return new StringPreference(preferences, "session_token", null);
    }
}
