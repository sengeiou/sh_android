package com.shootr.android.ui;

import com.shootr.android.ui.activities.DateTimePickerDialogActivity;
import com.shootr.android.ui.activities.DraftsActivity;
import com.shootr.android.ui.activities.EditStatusActivity;
import com.shootr.android.ui.activities.EventDetailActivity;
import com.shootr.android.ui.activities.EventsListActivity;
import com.shootr.android.ui.activities.FindFriendsActivity;
import com.shootr.android.ui.activities.MainActivity;
import com.shootr.android.ui.activities.NewEventActivity;
import com.shootr.android.ui.activities.PhotoViewActivity;
import com.shootr.android.ui.activities.PostNewShotActivity;
import com.shootr.android.ui.activities.ProfileContainerActivity;
import com.shootr.android.ui.activities.ProfileEditActivity;
import com.shootr.android.ui.activities.SearchTeamActivity;
import com.shootr.android.ui.activities.ShotDetailActivity;
import com.shootr.android.ui.activities.TimelineActivity;
import com.shootr.android.ui.activities.TimezonePickerActivity;
import com.shootr.android.ui.activities.UpdateWarningActivity;
import com.shootr.android.ui.activities.UserFollowsContainerActivity;
import com.shootr.android.ui.activities.registro.EmailLoginActivity;
import com.shootr.android.ui.activities.registro.EmailRegistrationActivity;
import com.shootr.android.ui.activities.registro.FacebookRegistroActivity;
import com.shootr.android.ui.activities.registro.WelcomeLoginActivity;
import com.shootr.android.ui.base.BaseToolbarActivity;
import com.shootr.android.ui.fragments.PeopleFragment;
import com.shootr.android.ui.presenter.DraftsPresenter;
import com.shootr.android.ui.widgets.WatchersView;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        injects = {
                // Every single activity extending BaseActivity, sadly
                BaseToolbarActivity.class,
                WelcomeLoginActivity.class,
                MainActivity.class,
                EmailLoginActivity.class,
                EmailRegistrationActivity.class,
                FacebookRegistroActivity.class,
                PostNewShotActivity.class,
                ProfileContainerActivity.class,
                UserFollowsContainerActivity.class,
                FindFriendsActivity.class,
                EventDetailActivity.class,
                EditStatusActivity.class,
                ProfileEditActivity.class,
                SearchTeamActivity.class,
                ShotDetailActivity.class,
                PhotoViewActivity.class,
                TimelineActivity.class,
                PeopleFragment.class,
                WatchersView.class,
                EventsListActivity.class,
                NewEventActivity.class,
                DateTimePickerDialogActivity.class,
                UpdateWarningActivity.class, TimezonePickerActivity.class, DraftsActivity.class, DraftsPresenter.class
        },
        complete = false
)
public class UiModule {
    @Provides @Singleton AppContainer provideAppContainer() {
        return AppContainer.DEFAULT;
    }
}
