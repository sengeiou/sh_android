package gm.mobi.android.data.prefs;

import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false
)
public class PreferenceModule {

    @Provides @Singleton @InitialSetupCompleted BooleanPreference provideInitialSetupCompleted(SharedPreferences preferences) {
        return new BooleanPreference(preferences, "initial_setup", false);
    }
}
