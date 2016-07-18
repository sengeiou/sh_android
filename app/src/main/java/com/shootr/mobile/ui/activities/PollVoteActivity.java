package com.shootr.mobile.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.PollOptionAdapter;
import com.shootr.mobile.ui.adapters.holders.PollOptionHolder;
import com.shootr.mobile.ui.adapters.listeners.OnPollOptionClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnPollOptionLongClickListener;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.ui.presenter.PollVotePresenter;
import com.shootr.mobile.ui.views.PollVoteView;
import com.shootr.mobile.util.BackStackHandler;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.MenuItemValueHolder;
import com.shootr.mobile.util.SwipeDialog;
import javax.inject.Inject;

public class PollVoteActivity extends BaseToolbarDecoratedActivity implements PollVoteView {

  private static final String EXTRA_STREAM = "idStream";
  public static final String EXTRA_ID_POLL = "idPoll";
  public static final String EXTRA_ID_USER_OWNER = "userIdOwner";

  @Bind(R.id.poll_option_list) GridView gridView;
  @Bind(R.id.poll_question) TextView pollQuestion;
  @Bind(R.id.pollvote_progress) ProgressBar progressBar;
  @Bind(R.id.poll_results) TextView viewResults;
  @Bind(R.id.poll_votes) TextView pollVotesCount;
  @Bind(R.id.poll_votes_container) LinearLayout pollVotesContainer;

  @Inject InitialsLoader initialsLoader;
  @Inject PollOptionHolder pollOptionHolder;
  @Inject PollVotePresenter presenter;
  @Inject FeedbackMessage feedbackMessage;
  @Inject BackStackHandler backStackHandler;

  private PollOptionAdapter pollOptionAdapter;
  private MenuItemValueHolder ignorePollMenu = new MenuItemValueHolder();

  public static Intent newIntent(Context context, String idStream) {
    Intent intent = new Intent(context, PollVoteActivity.class);
    intent.putExtra(EXTRA_STREAM, idStream);
    return intent;
  }

  public static Intent newIntentWithIdPoll(Context context, String idPoll) {
    Intent intent = new Intent(context, PollVoteActivity.class);
    intent.putExtra(EXTRA_ID_POLL, idPoll);
    return intent;
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_poll_vote;
  }

  @Override protected void initializePresenter() {
    String idStream = getIntent().getStringExtra(EXTRA_STREAM);
    String idStreamOwner = getIntent().getStringExtra(EXTRA_ID_USER_OWNER);
    if (idStream != null) {
      presenter.initialize(this, idStream, idStreamOwner);
    } else {
      presenter.initializeWithIdPoll(this, getIntent().getStringExtra(EXTRA_ID_POLL));
    }
  }

  @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
    /* no - op */
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    setupActionbar();
    pollOptionAdapter = new PollOptionAdapter(this, new OnPollOptionClickListener() {
      @Override public void onClickPressed(PollOptionModel pollOptionModel) {
        presenter.voteOption(pollOptionModel.getIdPollOption());
      }
    }, new OnPollOptionLongClickListener() {
      @Override public void onLongPress(PollOptionModel pollOptionModel) {
        setupPollOptionDialog(pollOptionModel);
      }
    }, imageLoader, initialsLoader, pollOptionHolder);
    gridView.setAdapter(pollOptionAdapter);
    ViewCompat.setNestedScrollingEnabled(gridView, true);
  }

  private void setupPollOptionDialog(PollOptionModel pollOptionModel) {
    SwipeDialog.newPollImageDialog(pollOptionModel, imageLoader, this.getLayoutInflater())
        .show(getFragmentManager(), SwipeDialog.DIALOG);
  }

  private void setupActionbar() {
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setDisplayShowHomeEnabled(false);
    actionBar.setHomeAsUpIndicator(R.drawable.ic_navigation_close_white_24);
  }

  @Override public void renderPoll(PollModel pollModel) {
    pollQuestion.setText(pollModel.getQuestion());
    pollOptionAdapter.setPollOptionModels(pollModel.getPollOptionModels());
    pollOptionAdapter.notifyDataSetChanged();
  }

  @Override public void showPollVotes(Long votes) {
    pollVotesContainer.setVisibility(View.VISIBLE);
    Integer pollVotes = votes.intValue();
    String pollVotesText =
        getResources().getQuantityString(R.plurals.poll_votes_count, pollVotes, pollVotes);
    pollVotesCount.setText(pollVotesText);
  }

  @Override public void ignorePoll() {
    finish();
  }

  @Override public void goToResults(String idPoll) {
    Intent intent = PollResultsActivity.newLiveResultsIntent(this, idPoll);
    startActivity(intent);
    finish();
  }

  @Override public void showError(String message) {
    feedbackMessage.show(getView(), message);
  }

  @Override public void showTimeoutAlert() {
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    alertDialogBuilder.setMessage(R.string.connection_lost) //
        .setPositiveButton(getString(R.string.poll_vote_timeout_retry), new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            presenter.retryVote();
          }
        }).show();
  }

  @Override public void showViewResultsButton() {
    viewResults.setVisibility(View.VISIBLE);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_poll_vote, menu);
    ignorePollMenu.bindRealMenuItem(menu.findItem(R.id.menu_ignore_poll));
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      backStackHandler.handleBackStack(this);
      finish();
      return true;
    } else if (item.getItemId() == R.id.menu_ignore_poll) {
      presenter.ignorePoll();
      return true;
    }  else {
      return super.onOptionsItemSelected(item);
    }
  }

  @Override protected void onResume() {
    super.onResume();
    presenter.resume();
  }

  @Override protected void onPause() {
    super.onPause();
    presenter.pause();
  }

  @Override public void showEmpty() {
    /* no-op */
  }

  @Override public void hideEmpty() {
    /* no-op */
  }

  @Override public void showLoading() {
    progressBar.setVisibility(View.VISIBLE);
    gridView.setVisibility(View.GONE);
  }

  @Override public void hideLoading() {
    gridView.setVisibility(View.VISIBLE);
    progressBar.setVisibility(View.GONE);
  }

  @OnClick(R.id.poll_results) public void goToResults() {
    presenter.viewResults();
  }
}
