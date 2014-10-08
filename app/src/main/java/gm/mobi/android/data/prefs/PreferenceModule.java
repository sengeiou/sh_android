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

    @Provides @Singleton @InitialSetupCompleted BooleanPreference provideInitialSetupCompleted(SharedPreferences preferences) {
        return new BooleanPreference(preferences, "initial_setup", false);
    }
}
