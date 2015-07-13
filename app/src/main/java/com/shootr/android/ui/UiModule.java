package com.shootr.android.ui;

import android.os.Handler;
import com.shootr.android.ui.activities.ActivityTimelineContainerActivity;
import com.shootr.android.ui.activities.DraftsActivity;
import com.shootr.android.ui.activities.EventDetailActivity;
import com.shootr.android.ui.activities.EventMediaActivity;
import com.shootr.android.ui.activities.EventTimelineActivity;
import com.shootr.android.ui.activities.FindEventsActivity;
import com.shootr.android.ui.activities.FindFriendsActivity;
import com.shootr.android.ui.activities.ListingActivity;
import com.shootr.android.ui.activities.MainTabbedActivity;
import com.shootr.android.ui.activities.NewEventActivity;
import com.shootr.android.ui.activities.PhotoViewActivity;
import com.shootr.android.ui.activities.PostNewShotActivity;
import com.shootr.android.ui.activities.ProfileContainerActivity;
import com.shootr.android.ui.activities.ProfileEditActivity;
import com.shootr.android.ui.activities.ShotDetailActivity;
import com.shootr.android.ui.activities.UpdateWarningActivity;
import com.shootr.android.ui.activities.UserFollowsContainerActivity;
import com.shootr.android.ui.activities.WhaleActivity;
import com.shootr.android.ui.activities.registro.EmailLoginActivity;
import com.shootr.android.ui.activities.registro.EmailRegistrationActivity;
import com.shootr.android.ui.activities.registro.LoginSelectionActivity;
import com.shootr.android.ui.activities.registro.ResetPasswordActivity;
import com.shootr.android.ui.base.BaseToolbarActivity;
import com.shootr.android.ui.fragments.ActivityTimelineFragment;
import com.shootr.android.ui.fragments.EventTimelineFragment;
import com.shootr.android.ui.fragments.FavoritesFragment;
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
    LoginSelectionActivity.class,
    EmailLoginActivity.class,
    EmailRegistrationActivity.class,
    PostNewShotActivity.class,
    ProfileContainerActivity.class,
    UserFollowsContainerActivity.class,
    FindFriendsActivity.class,
    EventDetailActivity.class,
    ProfileEditActivity.class,
    ShotDetailActivity.class,
    PhotoViewActivity.class,
    EventTimelineActivity.class,
    PeopleFragment.class,
    WatchersView.class,
    NewEventActivity.class,
    EventMediaActivity.class,
    UpdateWarningActivity.class,
    DraftsActivity.class,
    DraftsPresenter.class,
    MainTabbedActivity.class,
    ResetPasswordActivity.class,
    ListingActivity.class,
    FavoritesFragment.class,
    ActivityTimelineContainerActivity.class,
    ActivityTimelineFragment.class,
    EventTimelineFragment.class,
    FindEventsActivity.class,
    WhaleActivity.class,
  },
  complete = false) public class UiModule {

    @Provides
    @Singleton
    AppContainer provideAppContainer() {
        return AppContainer.DEFAULT;
    }

    @Provides
    Poller providePoller() {
        return new Poller(new Handler());
    }
}
