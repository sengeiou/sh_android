package gm.mobi.android.ui;

import dagger.Module;
import dagger.Provides;
import gm.mobi.android.ui.activities.FindFriendsActivity;
import gm.mobi.android.ui.activities.InfoActivity;
import gm.mobi.android.ui.activities.MainActivity;
import gm.mobi.android.ui.activities.PostNewShotActivity;
import gm.mobi.android.ui.activities.ProfileContainerActivity;
import gm.mobi.android.ui.activities.UserFollowsContainerActivity;
import gm.mobi.android.ui.activities.registro.EmailLoginActivity;
import gm.mobi.android.ui.activities.registro.EmailRegistrationActivity;
import gm.mobi.android.ui.activities.registro.FacebookRegistroActivity;
import gm.mobi.android.ui.activities.registro.WelcomeLoginActivity;
import gm.mobi.android.ui.base.BaseActivity;
import javax.inject.Singleton;

@Module(
        injects = {
                // Every single activity extending BaseActivity, sadly
                BaseActivity.class,
                WelcomeLoginActivity.class,
                MainActivity.class,
                EmailLoginActivity.class,
                EmailRegistrationActivity.class,
                FacebookRegistroActivity.class,
                PostNewShotActivity.class,
                ProfileContainerActivity.class,
                UserFollowsContainerActivity.class,
                FindFriendsActivity.class,
                InfoActivity.class,

        },
        complete = false
)
public class UiModule {
    @Provides @Singleton AppContainer provideAppContainer() {
        return AppContainer.DEFAULT;
    }
}
