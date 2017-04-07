package com.shootr.mobile.ui;

import android.os.Handler;
import com.shootr.mobile.domain.utils.DateRangeTextProvider;
import com.shootr.mobile.ui.activities.AllParticipantsActivity;
import com.shootr.mobile.ui.activities.AllShotsActivity;
import com.shootr.mobile.ui.activities.ChangePasswordActivity;
import com.shootr.mobile.ui.activities.ChannelListFragment;
import com.shootr.mobile.ui.activities.ChannelsContainerActivity;
import com.shootr.mobile.ui.activities.ContributorsActivity;
import com.shootr.mobile.ui.activities.CropPictureActivity;
import com.shootr.mobile.ui.activities.DiscoverFragment;
import com.shootr.mobile.ui.activities.DiscoverSearchActivity;
import com.shootr.mobile.ui.activities.DraftsActivity;
import com.shootr.mobile.ui.activities.EmailConfirmationActivity;
import com.shootr.mobile.ui.activities.ErrorActivity;
import com.shootr.mobile.ui.activities.FindContributorsActivity;
import com.shootr.mobile.ui.activities.FindFriendsActivity;
import com.shootr.mobile.ui.activities.FindParticipantsActivity;
import com.shootr.mobile.ui.activities.FollowingChannelListFragment;
import com.shootr.mobile.ui.activities.FriendsActivity;
import com.shootr.mobile.ui.activities.ListingActivity;
import com.shootr.mobile.ui.activities.MainTabbedActivity;
import com.shootr.mobile.ui.activities.NewStreamActivity;
import com.shootr.mobile.ui.activities.NicersActivity;
import com.shootr.mobile.ui.activities.OnBoardingStreamActivity;
import com.shootr.mobile.ui.activities.PhotoViewActivity;
import com.shootr.mobile.ui.activities.PollResultsActivity;
import com.shootr.mobile.ui.activities.PollVoteActivity;
import com.shootr.mobile.ui.activities.PostNewShotActivity;
import com.shootr.mobile.ui.activities.PrivateMessageTimelineActivity;
import com.shootr.mobile.ui.activities.ProfileActivity;
import com.shootr.mobile.ui.activities.ProfileEditActivity;
import com.shootr.mobile.ui.activities.SettingsActivity;
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
import com.shootr.mobile.ui.fragments.ActivityTimelineContainerFragment;
import com.shootr.mobile.ui.fragments.ActivityTimelineFragment;
import com.shootr.mobile.ui.fragments.DiscoverTimelineFragment;
import com.shootr.mobile.ui.fragments.FavoritesFragment;
import com.shootr.mobile.ui.fragments.FindFriendsFragment;
import com.shootr.mobile.ui.fragments.FindStreamsFragment;
import com.shootr.mobile.ui.fragments.MeActivityTimelineFragment;
import com.shootr.mobile.ui.presenter.DraftsPresenter;
import com.shootr.mobile.ui.views.EasterEggActivity;
import com.shootr.mobile.ui.widgets.MessageBox;
import com.shootr.mobile.ui.widgets.WatchersView;
import com.shootr.mobile.util.IntentFactory;
import com.shootr.mobile.util.ResourcesDateRangeTextProvider;
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
    ProfileActivity.class,
    UserFollowsContainerActivity.class,
    FindFriendsActivity.class,
    StreamDetailActivity.class,
    ProfileEditActivity.class,
    ShotDetailActivity.class,
    PhotoViewActivity.class,
    StreamTimelineActivity.class,
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
    ActivityTimelineFragment.class,
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
    MeActivityTimelineFragment.class,
    NicersActivity.class,
    ContributorsActivity.class,
    FindContributorsActivity.class,
    PollVoteActivity.class, PollResultsActivity.class,
    ActivityTimelineContainerFragment.class,
    SettingsActivity.class,
    DiscoverFragment.class,
    FriendsActivity.class,
    DiscoverSearchActivity.class,
    FindFriendsFragment.class,
    FindStreamsFragment.class,
    CropPictureActivity.class,
    EasterEggActivity.class, PrivateMessageTimelineActivity.class, ChannelListFragment.class,
    ChannelsContainerActivity.class, FollowingChannelListFragment.class, MessageBox.class,
    OnBoardingStreamActivity.class, DiscoverTimelineFragment.class
  },
  complete = false) public class UiModule {

    @Provides @Singleton AppContainer provideAppContainer() {
        return AppContainer.DEFAULT;
    }

    @Provides Poller providePoller() {
        return new Poller(new Handler());
    }

    @Provides DateRangeTextProvider provideDateRangeTextProvider(
      ResourcesDateRangeTextProvider resourcesDateRangeTextProvider) {
        return resourcesDateRangeTextProvider;
    }

    @Provides @Singleton IntentFactory provideIntentFactory() {
        return IntentFactory.REAL;
    }
}
