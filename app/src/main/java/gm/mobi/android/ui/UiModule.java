package gm.mobi.android.ui;

import gm.mobi.android.ui.activities.ProfileContainerActivity;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import gm.mobi.android.ui.activities.MainActivity;
import gm.mobi.android.ui.activities.NewShotActivity;
import gm.mobi.android.ui.activities.registro.EmailLoginActivity;
import gm.mobi.android.ui.activities.registro.EmailRegistrationActivity;
import gm.mobi.android.ui.activities.registro.FacebookRegistroActivity;
import gm.mobi.android.ui.activities.registro.WelcomeLoginActivity;
import gm.mobi.android.ui.base.BaseActivity;

@Module(
        injects = {
                // Every single activity extending BaseActivity, sadly
                BaseActivity.class,
                WelcomeLoginActivity.class,
                MainActivity.class,
                EmailLoginActivity.class,
                EmailRegistrationActivity.class,
                FacebookRegistroActivity.class,
                NewShotActivity.class,
                ProfileContainerActivity.class

        },
        complete = false
)
public class UiModule {
    @Provides @Singleton AppContainer provideAppContainer() {
        return AppContainer.DEFAULT;
    }
}
