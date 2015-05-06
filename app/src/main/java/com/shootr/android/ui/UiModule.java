package com.shootr.android.ui;

import com.shootr.android.ui.activities.ActivityTimelineActivity;
import com.shootr.android.ui.activities.DateTimePickerDialogActivity;
import com.shootr.android.ui.activities.DraftsActivity;
import com.shootr.android.ui.activities.EditStatusActivity;
import com.shootr.android.ui.activities.EventDetailActivity;
import com.shootr.android.ui.activities.EventTimelineActivity;
import com.shootr.android.ui.activities.EventsListActivity;
import com.shootr.android.ui.activities.FindFriendsActivity;
import com.shootr.android.ui.activities.NewEventActivity;
import com.shootr.android.ui.activities.PeopleActivity;
import com.shootr.android.ui.activities.PhotoViewActivity;
import com.shootr.android.ui.activities.PostNewShotActivity;
import com.shootr.android.ui.activities.ProfileContainerActivity;
import com.shootr.android.ui.activities.ProfileEditActivity;
import com.shootr.android.ui.activities.ShotDetailActivity;
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

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                // Every single activity extending BaseActivity, sadly
                BaseToolbarActivity.class,
                WelcomeLoginActivity.class,
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
                ShotDetailActivity.class,
                PhotoViewActivity.class,
                EventTimelineActivity.class,
                ActivityTimelineActivity.class,
                PeopleActivity.class,
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
