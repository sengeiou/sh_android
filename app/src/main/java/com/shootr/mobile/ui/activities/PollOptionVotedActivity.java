package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.base.BaseActivity;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.ui.presenter.PollOptionVotedPresenter;
import com.shootr.mobile.ui.views.PollOptionVotedView;
import com.shootr.mobile.ui.widgets.AvatarView;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.ShareManager;
import javax.inject.Inject;

public class PollOptionVotedActivity extends BaseActivity implements PollOptionVotedView {

  public static final String EXTRA_POLL = "extraPoll";

  @Inject AndroidTimeUtils timeUtils;

  @BindView(R.id.poll_countdown) TextView pollCountdown;
  @BindView(R.id.poll_option_image)AvatarView pollOptionImage;
  @BindView(R.id.voted_option) TextView votedOption;
  @BindView(R.id.legal_text) TextView legalText;
  @BindString(R.string.poll_option_voted) String optionVotedResource;
  @BindString(R.string.daily_hidden_text) String dailyHiddenText;
  @BindString(R.string.secure_daily_hidden_text) String secureDailyHiddenText;


  @Inject ShareManager shareManager;
  @Inject PollOptionVotedPresenter presenter;
  @Inject ImageLoader imageLoader;

  public static Intent getIntentForActivity(Context context, PollModel pollModel) {
    Intent intent = new Intent(context, PollOptionVotedActivity.class);
    intent.putExtra(EXTRA_POLL, pollModel);
    return intent;
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_poll_option_voted;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
  }

  @Override protected void initializePresenter() {
    presenter.initialize(this, extractPollFromIntent());
  }

  private PollModel extractPollFromIntent() {
    return (PollModel) getIntent().getSerializableExtra(EXTRA_POLL);
  }

  @Override public void renderPollOptionVoted(PollOptionModel pollOptionVoted) {
    imageLoader.loadProfilePhoto(
        pollOptionVoted.getOptionImage() != null ? pollOptionVoted.getOptionImage()
            .getSizes()
            .getMedium()
            .getUrl() : null, pollOptionImage, pollOptionVoted.getText());
    votedOption.setText(String.format(optionVotedResource, pollOptionVoted.getText()));
  }

  @Override public void showPollVotesTimeToExpire(Long timeToExpire, boolean isExpired) {
    String timeToExpireText = timeUtils.getPollElapsedTime(getBaseContext(), timeToExpire);
    if (!isExpired) {
      pollCountdown.setText(timeToExpireText);
      pollCountdown.setVisibility(View.VISIBLE);
    } else {
      pollCountdown.setVisibility(View.GONE);
    }
  }

  @Override public void shareVoted(PollModel pollModel, PollOptionModel pollOptionModel) {
    Intent shareIntent = shareManager.sharePollVotedIntent(this, pollModel, pollOptionModel);
    Intents.maybeStartActivity(this, shareIntent);
  }

  @Override public void showLegalText() {
    legalText.setVisibility(View.VISIBLE);
  }

  @Override public void showHiddenDailyText() {
    legalText.setVisibility(View.VISIBLE);
    legalText.setText(dailyHiddenText);
  }

  @Override public void showSecureHiddenDailyText() {
    legalText.setVisibility(View.VISIBLE);
    legalText.setText(secureDailyHiddenText);
  }

  @OnClick(R.id.share_button) void onShareClick() {
    presenter.shareVoted();
  }
}
