package gm.mobi.android.ui;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import gm.mobi.android.ui.activities.MainActivity;
import gm.mobi.android.ui.activities.PartidoActivity;
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
                PartidoActivity.class,
                EmailLoginActivity.class,
                EmailRegistrationActivity.class,
                FacebookRegistroActivity.class,

        },
        complete = false,
        library = true
)
public class UiModule {
    @Provides @Singleton AppContainer provideAppContainer() {
        return AppContainer.DEFAULT;
    }
}
