package com.shootr.android.ui;

import com.path.android.jobqueue.JobManager;
import com.shootr.android.ui.activities.AddMatchActivity;
import com.shootr.android.ui.activities.EditInfoActivity;
import com.shootr.android.ui.activities.ProfileEditActivity;
import com.shootr.android.ui.activities.SearchTeamActivity;
import com.shootr.android.ui.activities.ShotDetailActivity;
import com.shootr.android.ui.presenter.EditInfoPresenter;
import dagger.Module;
import dagger.Provides;
import com.shootr.android.ui.activities.FindFriendsActivity;
import com.shootr.android.ui.activities.InfoActivity;
import com.shootr.android.ui.activities.MainActivity;
import com.shootr.android.ui.activities.PostNewShotActivity;
import com.shootr.android.ui.activities.ProfileContainerActivity;
import com.shootr.android.ui.activities.UserFollowsContainerActivity;
import com.shootr.android.ui.activities.registro.EmailLoginActivity;
import com.shootr.android.ui.activities.registro.EmailRegistrationActivity;
import com.shootr.android.ui.activities.registro.FacebookRegistroActivity;
import com.shootr.android.ui.activities.registro.WelcomeLoginActivity;
import com.shootr.android.ui.base.BaseActivity;
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
                EditInfoActivity.class,
                AddMatchActivity.class,
                ProfileEditActivity.class,
                SearchTeamActivity.class,
                ShotDetailActivity.class,

        },
        complete = false
)
public class UiModule {
    @Provides @Singleton AppContainer provideAppContainer() {
        return AppContainer.DEFAULT;
    }
}
