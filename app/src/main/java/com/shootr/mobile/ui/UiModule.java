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
import com.shootr.mobile.ui.activities.DraftsActivity;
import com.shootr.mobile.ui.activities.EmailConfirmationActivity;
import com.shootr.mobile.ui.activities.ErrorActivity;
import com.shootr.mobile.ui.activities.FindContributorsActivity;
import com.shootr.mobile.ui.activities.FindParticipantsActivity;
import com.shootr.mobile.ui.activities.FollowingChannelListFragment;
import com.shootr.mobile.ui.activities.HiddenPollResultsActivity;
import com.shootr.mobile.ui.activities.HistoryActivity;
import com.shootr.mobile.ui.activities.ListingActivity;
import com.shootr.mobile.ui.activities.MainTabbedActivity;
import com.shootr.mobile.ui.activities.ShotDetailActivity;
import com.shootr.mobile.ui.activities.NewStreamActivity;
import com.shootr.mobile.ui.activities.NicersActivity;
import com.shootr.mobile.ui.activities.OnBoardingStreamActivity;
import com.shootr.mobile.ui.activities.OnBoardingUserActivity;
import com.shootr.mobile.ui.activities.PhotoViewActivity;
import com.shootr.mobile.ui.activities.PollOptionVotedActivity;
import com.shootr.mobile.ui.activities.PollResultsActivity;
import com.shootr.mobile.ui.activities.PollVoteActivity;
import com.shootr.mobile.ui.activities.PostNewShotActivity;
import com.shootr.mobile.ui.activities.PostPromotedShotActivity;
import com.shootr.mobile.ui.activities.PrivateMessageTimelineActivity;
import com.shootr.mobile.ui.activities.ProfileActivity;
import com.shootr.mobile.ui.activities.ProfileEditActivity;
import com.shootr.mobile.ui.activities.PromotedShotIntroActivity;
import com.shootr.mobile.ui.activities.SearchActivity;
import com.shootr.mobile.ui.activities.SearchToShareActivity;
import com.shootr.mobile.ui.activities.SettingsActivity;
import com.shootr.mobile.ui.activities.ShareStreamActivity;
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
import com.shootr.mobile.ui.activities.registro.PrivacyLawActivity;
import com.shootr.mobile.ui.activities.registro.ResetPasswordActivity;
import com.shootr.mobile.ui.base.BaseToolbarActivity;
import com.shootr.mobile.ui.fragments.ActivityTimelineFragment;
import com.shootr.mobile.ui.fragments.ChannelsContainerFragment;
import com.shootr.mobile.ui.fragments.FindFriendsFragment;
import com.shootr.mobile.ui.fragments.FindStreamsFragment;
import com.shootr.mobile.ui.fragments.GenericSearchFragment;
import com.shootr.mobile.ui.fragments.SearchStreamToShareFragment;
import com.shootr.mobile.ui.presenter.DraftsPresenter;
import com.shootr.mobile.ui.views.EasterEggActivity;
import com.shootr.mobile.ui.widgets.MessageBox;
import com.shootr.mobile.ui.widgets.PromotedMessageBox;
import com.shootr.mobile.ui.widgets.PromotedShotActivationInfoDialog;
import com.shootr.mobile.ui.widgets.PromotedShotInfoDialog;
import com.shootr.mobile.ui.widgets.PromotedTermsDialog;
import com.shootr.mobile.ui.widgets.WatchersView;
import com.shootr.mobile.util.IntentFactory;
import com.shootr.mobile.util.ResourcesDateRangeTextProvider;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(injects = {
    // Every single activity extending BaseActivity, sadly
    BaseToolbarActivity.class, LoginSelectionActivity.class, EmailLoginActivity.class,
    EmailRegistrationActivity.class, PostNewShotActivity.class, ProfileActivity.class,
    UserFollowsContainerActivity.class, StreamDetailActivity.class, ProfileEditActivity.class,
    PhotoViewActivity.class, StreamTimelineActivity.class,
    WatchersView.class, NewStreamActivity.class, StreamMediaActivity.class,
    UpdateWarningActivity.class, DraftsActivity.class, DraftsPresenter.class,
    MainTabbedActivity.class, ResetPasswordActivity.class, ListingActivity.class,
    ActivityTimelineFragment.class, WhaleActivity.class, EmailConfirmationActivity.class,
    AllShotsActivity.class, SupportActivity.class, ChangePasswordActivity.class,
    AllParticipantsActivity.class, FindParticipantsActivity.class, WelcomePageActivity.class,
    ErrorActivity.class, NicersActivity.class, ContributorsActivity.class,
    FindContributorsActivity.class, PollVoteActivity.class, PollResultsActivity.class,
    SettingsActivity.class, SearchActivity.class, FindFriendsFragment.class,
    FindStreamsFragment.class, CropPictureActivity.class, EasterEggActivity.class,
    PrivateMessageTimelineActivity.class, ChannelListFragment.class,
    ChannelsContainerActivity.class, FollowingChannelListFragment.class, MessageBox.class,
    OnBoardingStreamActivity.class, OnBoardingUserActivity.class, GenericSearchFragment.class,
    ChannelsContainerFragment.class, ShareStreamActivity.class, PollOptionVotedActivity.class,
    HiddenPollResultsActivity.class, HistoryActivity.class, ShotDetailActivity.class,
    PrivacyLawActivity.class, PostPromotedShotActivity.class, PromotedMessageBox.class,
    PromotedShotInfoDialog.class, PromotedShotIntroActivity.class, PromotedShotActivationInfoDialog.class,
  SearchStreamToShareFragment.class, SearchToShareActivity.class, PromotedTermsDialog.class
}, complete = false, library = true) public class UiModule {

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
