package com.shootr.mobile.ui;

import android.os.Handler;

import com.shootr.mobile.domain.utils.DateRangeTextProvider;
import com.shootr.mobile.ui.activities.ActivityTimelinesContainerActivity;
import com.shootr.mobile.ui.activities.AllParticipantsActivity;
import com.shootr.mobile.ui.activities.AllShotsActivity;
import com.shootr.mobile.ui.activities.ChangePasswordActivity;
import com.shootr.mobile.ui.activities.DraftsActivity;
import com.shootr.mobile.ui.activities.EmailConfirmationActivity;
import com.shootr.mobile.ui.activities.ErrorActivity;
import com.shootr.mobile.ui.activities.FindFriendsActivity;
import com.shootr.mobile.ui.activities.FindParticipantsActivity;
import com.shootr.mobile.ui.activities.FindStreamsActivity;
import com.shootr.mobile.ui.activities.ListingActivity;
import com.shootr.mobile.ui.activities.MainTabbedActivity;
import com.shootr.mobile.ui.activities.NewStreamActivity;
import com.shootr.mobile.ui.activities.PhotoViewActivity;
import com.shootr.mobile.ui.activities.PostNewShotActivity;
import com.shootr.mobile.ui.activities.ProfileContainerActivity;
import com.shootr.mobile.ui.activities.ProfileEditActivity;
import com.shootr.mobile.ui.activities.ShotDetailActivity;
import com.shootr.mobile.ui.activities.StreamDataInfoActivity;
import com.shootr.mobile.ui.activities.StreamDetailActivity;
import com.shootr.mobile.ui.activities.StreamMediaActivity;
import com.shootr.mobile.ui.activities.StreamTimelineActivity;
import com.shootr.mobile.ui.activities.SupportActivity;
import com.shootr.mobile.ui.activities.UpdateWarningActivity;
import com.shootr.mobile.ui.activities.UserFollowsContainerActivity;
import com.shootr.mobile.ui.activities.WelcomePageActivity;
import com.shootr.mobile.ui.activities.WhaleActivity;
import com.shootr.mobile.ui.activities.registro.EmailLoginActivity;
import com.shootr.mobile.ui.activities.registro.EmailRegistrationActivity;
import com.shootr.mobile.ui.activities.registro.LoginSelectionActivity;
import com.shootr.mobile.ui.activities.registro.ResetPasswordActivity;
import com.shootr.mobile.ui.base.BaseToolbarActivity;
import com.shootr.mobile.ui.fragments.ActivityTimelineFragment;
import com.shootr.mobile.ui.fragments.FavoritesFragment;
import com.shootr.mobile.ui.fragments.MeActivityTimelineFragment;
import com.shootr.mobile.ui.fragments.PeopleFragment;
import com.shootr.mobile.ui.presenter.DraftsPresenter;
import com.shootr.mobile.ui.widgets.WatchersView;
import com.shootr.mobile.util.IntentFactory;
import com.shootr.mobile.util.ResourcesDateRangeTextProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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
    StreamDetailActivity.class,
    ProfileEditActivity.class,
    ShotDetailActivity.class,
    PhotoViewActivity.class,
    StreamTimelineActivity.class,
    PeopleFragment.class,
    WatchersView.class,
    NewStreamActivity.class,
    StreamMediaActivity.class,
    UpdateWarningActivity.class,
    DraftsActivity.class,
    DraftsPresenter.class,
    MainTabbedActivity.class,
    ResetPasswordActivity.class,
    ListingActivity.class,
    FavoritesFragment.class,
    ActivityTimelinesContainerActivity.class,
    ActivityTimelineFragment.class,
    FindStreamsActivity.class,
    WhaleActivity.class,
    EmailConfirmationActivity.class,
    AllShotsActivity.class,
    SupportActivity.class,
    ChangePasswordActivity.class,
    AllParticipantsActivity.class,
    FindParticipantsActivity.class,
    WelcomePageActivity.class,
    ErrorActivity.class,
    StreamDataInfoActivity.class,
    MeActivityTimelineFragment.class
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

  @Provides
  DateRangeTextProvider provideDateRangeTextProvider(ResourcesDateRangeTextProvider resourcesDateRangeTextProvider) {
    return resourcesDateRangeTextProvider;
  }

  @Provides
  @Singleton
  IntentFactory provideIntentFactory() {
    return IntentFactory.REAL;
  }
}
